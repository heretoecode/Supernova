package com.archos.mediacenter.video.scanning;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import java.util.ArrayList;
import java.util.List;
import org.junit.Test;

public class ScanActivitySignalTest
{
    @Test public void concurrentDomainsKeepTheRingOnUntilTheLastOwnerEnds()
    {
        ScanActivitySignal signal = new ScanActivitySignal();
        List<Boolean> calls = new ArrayList<>();
        try (ScanActivitySignal.Subscription subscription = signal.observe(Runnable::run, calls::add))
        {
            signal.set("local", true);
            signal.set("putio", true);
            signal.set("local", false);
            assertTrue(signal.running());
            signal.set("putio", false);
            assertFalse(signal.running());
            assertEquals(List.of(false, true, false), calls);
        }
        signal.set("local", true);
        assertEquals(3, calls.size());
    }

    @Test public void queuedCallbacksCannotReviveTheRingOrCallADisposedScreen()
    {
        ScanActivitySignal signal = new ScanActivitySignal();
        List<Runnable> queue = new ArrayList<>();
        List<Boolean> calls = new ArrayList<>();
        ScanActivitySignal.Subscription subscription = signal.observe(queue::add, calls::add);
        signal.set("local", true);
        signal.set("local", false);
        queue.forEach(Runnable::run);
        assertEquals(List.of(false), calls);
        queue.clear();
        signal.set("local", true);
        subscription.close();
        queue.forEach(Runnable::run);
        assertEquals(1, calls.size());
    }

    @Test public void oneBrokenSubscriberCannotPreventOtherObservers()
    {
        ScanActivitySignal signal = new ScanActivitySignal();
        List<Boolean> calls = new ArrayList<>();
        signal.observe(Runnable::run, running -> {
            if (running)
                throw new IllegalStateException("Detached");
        });
        signal.observe(Runnable::run, calls::add);
        signal.set("local", true);
        signal.set("local", false);
        assertEquals(List.of(false, true, false), calls);
    }
}
