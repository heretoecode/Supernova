package com.archos.mediacenter.video.leanback.overlay;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class StalledWorkWatchTest
{
    @Test
    public void workThatKeepsMovingIsNeverStalled()
    {
        StalledWorkWatch watch = new StalledWorkWatch(1000);
        assertFalse(watch.stalled(true, "10", 0));
        assertFalse(watch.stalled(true, "9", 900));
        assertFalse(watch.stalled(true, "8", 1800));
        assertFalse(watch.stalled(true, "8", 2700));
    }

    @Test
    public void sameProgressPastTheLimitIsStalledUntilItMovesOrStops()
    {
        StalledWorkWatch watch = new StalledWorkWatch(1000);
        assertFalse(watch.stalled(true, "10", 0));
        assertTrue(watch.stalled(true, "10", 1000));
        assertTrue(watch.firstReport());
        assertFalse(watch.firstReport());
        assertFalse(watch.stalled(true, "9", 1100));
        assertTrue(watch.stalled(true, "9", 2100));
        assertTrue(watch.firstReport());
        assertFalse(watch.stalled(false, "9", 2200));
        assertFalse(watch.stalled(true, "9", 2300));
        assertFalse(watch.stalled(true, "9", 3200));
        assertTrue(watch.stalled(true, "9", 3300));
    }
}
