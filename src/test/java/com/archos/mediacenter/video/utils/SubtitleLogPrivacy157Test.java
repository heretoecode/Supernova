package com.archos.mediacenter.video.utils;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import android.app.Application;
import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.read.ListAppender;
import okhttp3.OkHttpClient;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;
import org.robolectric.util.ReflectionHelpers;
import org.slf4j.LoggerFactory;

@RunWith(RobolectricTestRunner.class)
@Config(application = Application.class, sdk = 30)
public class SubtitleLogPrivacy157Test
{
    private final Logger logger = (Logger)LoggerFactory.getLogger(OpenSubtitlesApiHelper.class);
    private final ListAppender<ILoggingEvent> captured = new ListAppender<>();
    private Level previous;
    private OkHttpClient client;
    private MockWebServer server;

    @Before public void prepare() throws Exception
    {
        previous = logger.getLevel();
        logger.setLevel(Level.TRACE);
        captured.start();
        logger.addAppender(captured);
        server = new MockWebServer();
        server.start();
        new OpenSubtitlesApiHelper();
        client = ReflectionHelpers.getStaticField(OpenSubtitlesApiHelper.class, "httpClient");
        ReflectionHelpers.setStaticField(OpenSubtitlesApiHelper.class, "apiKey", "PRIVATE_API_KEY");
        ReflectionHelpers.setStaticField(OpenSubtitlesApiHelper.class, "remainingDownloads", 10);
        OpenSubtitlesApiHelper.setBaseUrl(server.url("/PRIVATE_ENDPOINT/").toString());
    }

    @After public void close() throws Exception
    {
        server.close();
        client.connectionPool().evictAll();
        client.dispatcher().executorService().shutdownNow();
        logger.detachAppender(captured);
        logger.setLevel(previous);
        captured.stop();
    }

    @Test public void privateQueryResultsServerMessagesAndParseErrorsDoNotReachAppenders() throws Exception
    {
        server.enqueue(new MockResponse().setBody("{\"message\":\"PRIVATE_MESSAGE\",\"data\":[{\"id\":\"1\","
                + "\"attributes\":{\"files\":[{\"file_id\":\"123\",\"file_name\":\"PRIVATE_FILENAME\"}]}}]}"));
        var result = OpenSubtitlesApiHelper.searchSubtitle(new OpenSubtitlesQueryParams("PRIVATE_TITLE", null), "en");
        assertEquals("PRIVATE_FILENAME", result.get(0).getFileName());
        var request = server.takeRequest();
        assertTrue(request.getPath().contains("PRIVATE_TITLE"));
        assertEquals("PRIVATE_API_KEY", request.getHeader("Api-Key"));
        server.enqueue(new MockResponse().setStatus("HTTP/1.1 401 PRIVATE_REASON")
                .setBody("{\"message\":\"PRIVATE_SERVER_MESSAGE\"}"));
        assertNull(OpenSubtitlesApiHelper.getDownloadSubtitleLink("123"));
        server.enqueue(new MockResponse().setBody("PRIVATE_INVALID_JSON"));
        assertNull(OpenSubtitlesApiHelper.searchSubtitle(new OpenSubtitlesQueryParams("PRIVATE_TITLE", null), "en"));
        assertFalse(captured.list.isEmpty());
        for (ILoggingEvent event : captured.list)
        {
            assertFalse(event.getFormattedMessage().contains("PRIVATE_"));
            if (event.getArgumentArray() != null)
                for (Object argument : event.getArgumentArray())
                    assertFalse(String.valueOf(argument).contains("PRIVATE_"));
            assertNull(event.getThrowableProxy());
        }
    }

    @Test public void traceLoggingRetainsTheProductionNetworkDeadlines()
    {
        assertEquals(5000, client.connectTimeoutMillis());
        assertEquals(10000, client.readTimeoutMillis());
        assertEquals(15000, client.callTimeoutMillis());
    }
}
