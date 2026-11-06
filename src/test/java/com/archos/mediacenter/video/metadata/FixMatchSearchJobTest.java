package com.archos.mediacenter.video.metadata;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertNotNull;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import android.app.Application;
import android.content.Context;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import okhttp3.Call;
import okhttp3.OkHttpClient;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import okhttp3.mockwebserver.SocketPolicy;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

@RunWith(RobolectricTestRunner.class)
@Config(application = Application.class, sdk = 30)
public class FixMatchSearchJobTest
{
    @Test public void silentServerIsCancelledAtDeadlineAndCompletesOnlyOnce() throws Exception
    {
        Context context = mock(Context.class);
        when(context.getApplicationContext()).thenReturn(context);
        when(context.getString(anyInt())).thenReturn("TEST_APPLICATION_KEY");
        CountDownLatch completed = new CountDownLatch(1);
        AtomicInteger completions = new AtomicInteger();
        AtomicReference<Call> call = new AtomicReference<>();
        try (MockWebServer server = new MockWebServer())
        {
            server.enqueue(new MockResponse().setSocketPolicy(SocketPolicy.NO_RESPONSE));
            server.start();
            OkHttpClient client = new OkHttpClient.Builder().addInterceptor(chain -> {
                call.set(chain.call());
                return chain.proceed(chain.request().newBuilder().url(server.url("/search/movie")).build());
            }).build();
            try (FixMatchSearchJob job = FixMatchSearchJob.start(context, "Dune", "movie", "movie", 0,
                Runnable::run, rows -> {
                    assertTrue(rows.isEmpty());
                    completions.incrementAndGet();
                    completed.countDown();
                }, client, 2000))
            {
                assertNotNull(server.takeRequest(3, TimeUnit.SECONDS));
                assertTrue(completed.await(4, TimeUnit.SECONDS));
                assertEquals(1, completions.get());
                assertNotNull(call.get());
                assertTrue(call.get().isCanceled());
            }
        }
    }
}
