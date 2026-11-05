package com.archos.mediacenter.video.streaming;

import static org.junit.Assert.assertEquals;

import android.app.Application;
import android.content.SharedPreferences;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;

@RunWith(RobolectricTestRunner.class)
@Config(application = Application.class, sdk = 30)
public class ProviderCatalogueCacheTest
{
    @Test public void countryCacheIsWeeklyAndFailedRefreshKeepsValuesAndLastUpdated() throws Exception
    {
        SharedPreferences prefs = RuntimeEnvironment.getApplication().getSharedPreferences("catalogue-test", 0);
        AtomicInteger calls = new AtomicInteger();
        List<StreamingRepository.Provider> providers = List.of(new StreamingRepository.Provider(8, "Example", "/x"));
        ProviderCatalogueCache.load(prefs, "IE", 1000, () -> {
            calls.incrementAndGet();
            return providers;
        });
        ProviderCatalogueCache.load(prefs, "IE", 2000, () -> { throw new AssertionError("not due"); });
        assertEquals(1, calls.get());
        assertEquals(8, ProviderCatalogueCache
                            .load(prefs, "IE", ProviderCatalogueCache.WEEK_MS + 1000,
                                  () -> { throw new IOException("offline"); })
                            .get(0)
                            .id);
        assertEquals(1000, ProviderCatalogueCache.lastUpdated(prefs, "IE"));
        ProviderCatalogueCache.load(prefs, "GB", 2000, () -> {
            calls.incrementAndGet();
            return providers;
        });
        assertEquals(2, calls.get());
        assertEquals(2000, ProviderCatalogueCache.lastUpdated(prefs, "GB"));
    }
}
