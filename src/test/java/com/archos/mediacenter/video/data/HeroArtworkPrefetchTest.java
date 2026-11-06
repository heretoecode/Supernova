package com.archos.mediacenter.video.data;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import org.junit.Test;

public class HeroArtworkPrefetchTest
{
    @Test public void publishesOnlyAfterBothImagesAreWarmAndCachesThePair() throws Exception
    {
        CountDownLatch loaded = new CountDownLatch(1);
        HeroArtworkPrefetch.Assets assets = new HeroArtworkPrefetch.Assets("backdrop", "logo");
        try (HeroArtworkPrefetch<String> cache = new HeroArtworkPrefetch<>((url, logo) -> url,
            value -> 1, 10, Runnable::run, loaded::countDown))
        {
            cache.warm(List.of(assets));
            assertTrue(loaded.await(2, TimeUnit.SECONDS));
            assertEquals(new HeroArtworkPrefetch.Ready<>("backdrop", "logo"), cache.ready(assets));
        }
    }

    @Test public void oversizedPairsCannotBreakTheBudget() throws Exception
    {
        CountDownLatch completed = new CountDownLatch(1);
        HeroArtworkPrefetch.Assets assets = new HeroArtworkPrefetch.Assets("large", "");
        try (HeroArtworkPrefetch<String> cache = new HeroArtworkPrefetch<>((url, logo) -> {
            completed.countDown();
            return url;
        }, value -> 100, 10, Runnable::run, () -> {}))
        {
            cache.warm(List.of(assets));
            assertTrue(completed.await(2, TimeUnit.SECONDS));
            assertNull(cache.ready(assets));
        }
    }

    @Test public void disposedScreensNeverReceiveQueuedCallbacks() throws Exception
    {
        CountDownLatch queued = new CountDownLatch(1);
        java.util.List<Runnable> callbacks = new java.util.concurrent.CopyOnWriteArrayList<>();
        java.util.concurrent.atomic.AtomicInteger changes = new java.util.concurrent.atomic.AtomicInteger();
        HeroArtworkPrefetch<String> cache = new HeroArtworkPrefetch<>((url, logo) -> url, value -> 1, 10,
            task -> {
                callbacks.add(task);
                queued.countDown();
            }, changes::incrementAndGet);
        cache.warm(List.of(new HeroArtworkPrefetch.Assets("a", "")));
        assertTrue(queued.await(2, TimeUnit.SECONDS));
        cache.close();
        callbacks.forEach(Runnable::run);
        assertEquals(0, changes.get());
    }
}
