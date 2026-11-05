package com.archos.mediacenter.video.metadata;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import android.app.Application;
import java.io.IOException;
import java.util.concurrent.atomic.AtomicInteger;
import org.json.JSONObject;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;

@RunWith(RobolectricTestRunner.class)
@Config(application = Application.class, sdk = 30)
public class OmdbRatingsCacheTest
{
    @Test public void reopensOfflineWithStoredRatingsAndReservesFailedAttemptsForAWeek() throws Exception
    {
        Application app = RuntimeEnvironment.getApplication();
        AtomicInteger calls = new AtomicInteger();
        JSONObject data = new JSONObject().put("imdbRating", "8.2");
        OmdbRatingsCache cache = new OmdbRatingsCache(app, "movie", 12345);
        assertEquals("8.2", cache
                                .load(100,
                                      () -> {
                                          calls.incrementAndGet();
                                          return data;
                                      })
                                .getString("imdbRating"));
        OmdbRatingsCache reopened = new OmdbRatingsCache(app, "movie", 12345);
        assertEquals(
            "8.2", reopened.load(101, () -> { throw new AssertionError("must not refresh"); }).getString("imdbRating"));
        assertEquals("8.2", reopened
                                .load(100 + OmdbRatingsCache.WEEK_MS,
                                      () -> {
                                          calls.incrementAndGet();
                                          throw new IOException("offline");
                                      })
                                .getString("imdbRating"));
        reopened.load(101 + OmdbRatingsCache.WEEK_MS, () -> { throw new AssertionError("weekly retry limit"); });
        assertEquals(2, calls.get());
        assertNull(new OmdbRatingsCache(app, "tv", 12345).cached());
    }
}
