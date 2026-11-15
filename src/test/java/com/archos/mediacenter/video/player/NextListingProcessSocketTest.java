package com.archos.mediacenter.video.player;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import java.io.File;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.concurrent.Executors;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.mockwebserver.Dispatcher;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import okhttp3.mockwebserver.RecordedRequest;
import okhttp3.mockwebserver.SocketPolicy;
import org.junit.Test;

/** Real OS termination of a blocked FileCore worker; a same-endpoint playback client stays usable. */
public class NextListingProcessSocketTest
{
    @Test public void silentAndContinuallyTricklingListingsEndWithTheirOwnedProcessAndPlaybackSurvives()
        throws Exception
    {
        try (MockWebServer server = new MockWebServer())
        {
            AtomicInteger listings = new AtomicInteger();
            server.setDispatcher(new Dispatcher() {
                @Override public MockResponse dispatch(RecordedRequest request)
                {
                    if ("/play".equals(request.getPath()))
                        return new MockResponse().setBody("playback survives");
                    if ("OPTIONS".equals(request.getMethod()))
                        return new MockResponse().setHeader("DAV", "1, 2");
                    if (request.getHeader("Depth") == null)
                        return new MockResponse().setResponseCode(404);
                    if (listings.getAndIncrement() % 2 == 0)
                        return new MockResponse().setSocketPolicy(SocketPolicy.NO_RESPONSE);
                    return new MockResponse().setResponseCode(207)
                        .setHeader("Content-Type", "application/xml")
                        .setBody("<d:multistatus xmlns:d=\"DAV:\">" + " ".repeat(10000) + "</d:multistatus>")
                        .throttleBody(1, 100, TimeUnit.MILLISECONDS);
                }
            });
            server.start();
            OkHttpClient playback = new OkHttpClient();
            ScheduledExecutorService deadline = Executors.newSingleThreadScheduledExecutor();
            try
            {
                for (int iteration = 0; iteration < 4; iteration++)
                {
                    try (var response = playback.newCall(
                        new Request.Builder().url(server.url("/play")).build()).execute())
                    {
                        assertEquals("playback survives", response.body().string());
                    }
                    assertNotNull(server.takeRequest(2, TimeUnit.SECONDS));
                    File log = File.createTempFile("owned-next-child", ".log");
                    String java = new File(System.getProperty("java.home"), "bin/java").getPath();
                    List<String> command = new ArrayList<>(List.of(java, "-Xmx512m",
                        "--add-exports=java.base/jdk.internal.access=ALL-UNNAMED"));
                    for (String key : List.of("javax.net.ssl.trustStore", "https.proxyHost", "https.proxyPort",
                                              "robolectric.dependency.repo.url", "robolectric.offline"))
                    {
                        String value = System.getProperty(key);
                        if (value != null)
                            command.add("-D" + key + "=" + value);
                    }
                    command.addAll(List.of("-cp", System.getProperty("java.class.path"),
                        NextListingChildMain.class.getName(), String.valueOf(server.getPort())));
                    Process child = new ProcessBuilder(command).redirectErrorStream(true).redirectOutput(log).start();
                    try
                    {
                        long until = System.nanoTime() + TimeUnit.SECONDS.toNanos(40);
                        RecordedRequest request;
                        do
                        {
                            request = server.takeRequest(Math.max(1, until - System.nanoTime()), TimeUnit.NANOSECONDS);
                            assertNotNull(Files.readString(log.toPath(), StandardCharsets.UTF_8), request);
                        }
                        while ("OPTIONS".equals(request.getMethod()) && System.nanoTime() < until);
                        assertEquals("PROPFIND", request.getMethod());
                        Thread.sleep(250);
                        assertTrue(child.isAlive());
                        if (iteration % 2 == 0)
                            child.destroyForcibly();
                        else
                            deadline.schedule(child::destroyForcibly, 100, TimeUnit.MILLISECONDS);
                        assertTrue(child.waitFor(3, TimeUnit.SECONDS));
                        assertFalse(child.isAlive());
                    }
                    finally
                    {
                        child.destroyForcibly();
                        log.delete();
                    }
                }
                assertEquals(4, listings.get());
                try (var response = playback.newCall(
                        new Request.Builder().url(server.url("/play")).build()).execute())
                {
                    assertEquals("playback survives", response.body().string());
                }
            }
            finally
            {
                deadline.shutdownNow();
                playback.dispatcher().executorService().shutdownNow();
                playback.connectionPool().evictAll();
            }
        }
    }
}
