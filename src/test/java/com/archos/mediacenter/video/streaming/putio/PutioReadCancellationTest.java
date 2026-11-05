package com.archos.mediacenter.video.streaming.putio;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThrows;
import static org.junit.Assert.assertTrue;

import android.app.Application;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.FutureTask;
import java.util.concurrent.TimeUnit;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import okhttp3.mockwebserver.SocketPolicy;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;
import org.robolectric.util.ReflectionHelpers;
import okhttp3.OkHttpClient;

@RunWith(RobolectricTestRunner.class)
@Config(application = Application.class, sdk = 30)
public class PutioReadCancellationTest
{
    @Test public void cancellationUnblocksSilentAccountRequestAndIsLatchedForLaterCalls() throws Exception
    {
        try (MockWebServer server = new MockWebServer(); PutioReadClient client = new PutioReadClient("fixture-token"))
        {
            server.enqueue(new MockResponse().setSocketPolicy(SocketPolicy.NO_RESPONSE));
            server.start();
            CountDownLatch reached = new CountDownLatch(1);
            ReflectionHelpers.setField(
                client, "http",
                new OkHttpClient.Builder()
                    .addInterceptor(chain -> {
                        reached.countDown();
                        return chain.proceed(chain.request().newBuilder().url(server.url("/account/info")).build());
                    })
                    .build());
            FutureTask<PutioReconciliation.Failure> task = new FutureTask<>(() -> {
                try
                {
                    client.account();
                    throw new AssertionError("Cancelled account request succeeded");
                }
                catch (PutioReadClient.Unavailable failure)
                {
                    return failure.reason;
                }
            });
            Thread worker = new Thread(task, "PutioCancellationFixture");
            worker.start();
            try
            {
                assertTrue(reached.await(5, TimeUnit.SECONDS));
                assertTrue(server.takeRequest(5, TimeUnit.SECONDS) != null);
                client.cancel();
                assertEquals(PutioReconciliation.Failure.CANCELLED, task.get(5, TimeUnit.SECONDS));
                assertEquals(PutioReconciliation.Failure.CANCELLED,
                             assertThrows(PutioReadClient.Unavailable.class, client::account).reason);
            }
            finally
            {
                client.cancel();
                worker.join(5000);
            }
        }
    }
}
