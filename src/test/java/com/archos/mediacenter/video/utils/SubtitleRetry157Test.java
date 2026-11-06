package com.archos.mediacenter.video.utils;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import android.app.Application;
import java.util.concurrent.TimeUnit;
import okhttp3.OkHttpClient;
import okhttp3.mockwebserver.Dispatcher;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import okhttp3.mockwebserver.RecordedRequest;
import org.json.JSONObject;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;
import org.robolectric.util.ReflectionHelpers;

@RunWith(RobolectricTestRunner.class)
@Config(application = Application.class, sdk = 30)
public class SubtitleRetry157Test
{
    private MockWebServer server;
    private OkHttpClient client;

    @Before public void prepare() throws Exception
    {
        server = new MockWebServer();
        server.start();
        new OpenSubtitlesApiHelper();
        client = new OkHttpClient.Builder().callTimeout(2, TimeUnit.SECONDS).build();
        ReflectionHelpers.setStaticField(OpenSubtitlesApiHelper.class, "httpClient", client);
        ReflectionHelpers.setStaticField(OpenSubtitlesApiHelper.class, "apiKey", "test-api-key");
        ReflectionHelpers.setStaticField(OpenSubtitlesApiHelper.class, "username", "test-user");
        ReflectionHelpers.setStaticField(OpenSubtitlesApiHelper.class, "password", "test-password");
        ReflectionHelpers.setStaticField(OpenSubtitlesApiHelper.class, "remainingDownloads", 10);
        OpenSubtitlesApiHelper.setAuthToken("test-token");
        OpenSubtitlesApiHelper.setBaseUrl(server.url("/").toString());
    }

    @After public void close() throws Exception
    {
        server.close();
        client.connectionPool().evictAll();
        client.dispatcher().executorService().shutdownNow();
    }

    private MockResponse expired()
    {
        return new MockResponse().setResponseCode(406).setBody("{\"message\":\"invalid token\"}");
    }

    private MockResponse login() throws Exception
    {
        return new MockResponse().setBody(new JSONObject().put("token", "refreshed-test-token")
                .put("https://base_url", server.url("/").toString()).toString());
    }

    @Test public void aRepeatedlyExpiredSearchTokenGetsOneRefreshAndOneRetry() throws Exception
    {
        MockResponse refreshed = login();
        server.setDispatcher(new Dispatcher() {
            @Override public MockResponse dispatch(RecordedRequest request)
            {
                if (request.getPath().equals("/login"))
                    return refreshed;
                // Finite escape lets the unfixed implementation fail the count assertion, without hanging the test.
                return server.getRequestCount() > 7 ? new MockResponse().setBody("{\"data\":[]}") : expired();
            }
        });
        assertNull(OpenSubtitlesApiHelper.searchSubtitle(new OpenSubtitlesQueryParams("test", null), "en"));
        assertEquals(3, server.getRequestCount());
    }

    @Test public void aFailedDownloadRefreshEndsTheOperation() throws Exception
    {
        server.enqueue(expired());
        server.enqueue(new MockResponse().setResponseCode(401));
        server.enqueue(new MockResponse().setBody("{\"link\":\"unused\"}"));
        assertNull(OpenSubtitlesApiHelper.getDownloadSubtitleLink("123"));
        assertEquals(2, server.getRequestCount());
    }

    @Test public void aSuccessfulRefreshCanCompleteBothSearchAndDownload() throws Exception
    {
        server.enqueue(expired());
        server.enqueue(login());
        server.enqueue(new MockResponse().setBody("{\"data\":[]}"));
        assertNotNull(OpenSubtitlesApiHelper.searchSubtitle(new OpenSubtitlesQueryParams("test", null), "en"));
        server.enqueue(expired());
        server.enqueue(login());
        server.enqueue(new MockResponse().setBody("{\"link\":\"https://example.invalid/subtitle\"}"));
        assertEquals("https://example.invalid/subtitle", OpenSubtitlesApiHelper.getDownloadSubtitleLink("123"));
        assertEquals(6, server.getRequestCount());
    }
}
