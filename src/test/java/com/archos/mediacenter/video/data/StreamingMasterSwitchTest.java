package com.archos.mediacenter.video.data;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import android.app.Application;
import com.archos.mediacenter.video.streaming.StreamingRepository;
import java.util.Set;
import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;

@RunWith(RobolectricTestRunner.class)
@Config(application = Application.class, sdk = {23, 30}, manifest = Config.NONE)
public class StreamingMasterSwitchTest
{
    @Test public void offHidesFreshDiskAvailabilityAndDoesNotNeedValidNetworkIdentity() throws Exception
    {
        var context = RuntimeEnvironment.getApplication();
        var preferences = StreamingRepository.prefs(context);
        preferences.edit().clear().putBoolean(StreamingRepository.ENABLED, false)
            .putString(StreamingRepository.COUNTRY, "GB")
            .putLong("streaming_known_at:movie:1:GB:-1", System.currentTimeMillis())
            .putStringSet("streaming_known:movie:1:GB:-1", Set.of("8")).commit();
        assertFalse(StreamingRepository.enabled(context));
        assertTrue(StreamingRepository.load(context, "invalid", -1, "GB").offers.isEmpty());
        assertTrue(StreamingRepository.cachedAvailability(context, "movie", 1, "GB", -1).offers.isEmpty());
        assertFalse(StreamingRepository.knownOn(context, "movie", 1, "8"));
    }

    @Test public void disablingCancelsAnActiveAvailabilityCall() throws Exception
    {
        var context = RuntimeEnvironment.getApplication();
        StreamingRepository.invalidate();
        StreamingRepository.prefs(context).edit().clear().putBoolean(StreamingRepository.ENABLED, true).commit();
        var started = new java.util.concurrent.CountDownLatch(1);
        var cancelled = new java.util.concurrent.CountDownLatch(1);
        var client = new okhttp3.OkHttpClient.Builder().addInterceptor(chain -> {
            started.countDown();
            long deadline = System.nanoTime() + java.util.concurrent.TimeUnit.SECONDS.toNanos(3);
            while (!chain.call().isCanceled() && System.nanoTime() < deadline)
            {
                try
                {
                    Thread.sleep(5);
                }
                catch (InterruptedException interrupted)
                {
                    throw new java.io.InterruptedIOException();
                }
            }
            if (chain.call().isCanceled())
                cancelled.countDown();
            throw new java.io.IOException("Stopped test transport");
        }).build();
        var executor = java.util.concurrent.Executors.newSingleThreadExecutor();
        try
        {
            var request = executor.submit(() -> {
                try
                {
                    StreamingRepository.load(context, "movie", 987654, "GB", -1, client);
                }
                catch (Exception stopped)
                {
                    // Expected cancellation of the real availability producer.
                }
            });
            assertTrue(started.await(3, java.util.concurrent.TimeUnit.SECONDS));
            StreamingRepository.prefs(context).edit().putBoolean(StreamingRepository.ENABLED, false).commit();
            org.robolectric.Shadows.shadowOf(android.os.Looper.getMainLooper()).idle();
            assertTrue(cancelled.await(3, java.util.concurrent.TimeUnit.SECONDS));
            request.get(3, java.util.concurrent.TimeUnit.SECONDS);
        }
        finally
        {
            executor.shutdownNow();
        }
    }

    @Test public void offDoesNotCallRelatedAvailabilityAndRetainsLocalRecommendations()
    {
        RelatedTitles.Sources sources = new RelatedTitles.Sources() {
            @Override public JSONArray recommendations()
            {
                return new JSONArray().put(new JSONObject().put("id", 2)).put(new JSONObject().put("id", 3));
            }
            @Override public JSONArray similar()
            {
                return new JSONArray();
            }
            @Override public JSONArray traktRelated()
            {
                return new JSONArray();
            }
            @Override public boolean onServices(String kind, long id)
            {
                throw new AssertionError("Availability must not run while disabled");
            }
            @Override public boolean streamingEnabled()
            {
                return false;
            }
        };
        var result = RelatedTitles.load("movie", 1, Set.of("movie:2"), sources, () -> false);
        assertTrue(result.onServices().isEmpty());
        assertFalse(result.inLibrary().isEmpty());
    }
}
