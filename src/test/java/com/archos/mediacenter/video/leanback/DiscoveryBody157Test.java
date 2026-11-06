package com.archos.mediacenter.video.leanback;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThrows;
import android.app.Application;
import java.io.IOException;
import java.util.concurrent.TimeUnit;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

@RunWith(RobolectricTestRunner.class)
@Config(application = Application.class, sdk = 30)
public class DiscoveryBody157Test
{
    @Test public void declaredAndChunkedOversizeResponsesFailBeforeBecomingAChart() throws Exception
    {
        try (MockWebServer server = new MockWebServer())
        {
            server.enqueue(new MockResponse().setBody("x".repeat(2_000_001)));
            server.enqueue(new MockResponse().setChunkedBody("x".repeat(2_000_001), 8192));
            server.enqueue(new MockResponse().setBody("[{\"title\":\"Amélie\"}]"));
            server.start();
            OkHttpClient client = new OkHttpClient.Builder().callTimeout(3, TimeUnit.SECONDS).build();
            for (int i = 0; i < 2; i++)
                try (Response response = client.newCall(new Request.Builder().url(server.url("/charts")).build())
                                             .execute())
                {
                    assertThrows(IOException.class, () -> PreviewDiscovery.readBody(response.body()));
                }
            try (Response response = client.newCall(new Request.Builder().url(server.url("/charts")).build()).execute())
            {
                assertEquals("[{\"title\":\"Amélie\"}]", PreviewDiscovery.readBody(response.body()));
            }
            client.connectionPool().evictAll();
            client.dispatcher().executorService().shutdownNow();
        }
    }
}
