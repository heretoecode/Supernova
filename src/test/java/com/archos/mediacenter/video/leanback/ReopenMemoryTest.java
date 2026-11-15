package com.archos.mediacenter.video.leanback;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class ReopenMemoryTest
{
    @Test
    public void sameStartOfTheDeviceKeepsThePlace()
    {
        assertTrue(ReopenMemory.sameBoot(1_000_000L, 1_000_000L));
        assertTrue(ReopenMemory.sameBoot(1_000_000L, 1_000_000L + ReopenMemory.BOOT_TOLERANCE_MS));
        assertTrue(ReopenMemory.sameBoot(1_000_000L, 1_000_000L - 5_000L));
    }

    @Test
    public void aRestartOrNothingSavedOpensHome()
    {
        assertFalse(ReopenMemory.sameBoot(1_000_000L, 1_000_000L + ReopenMemory.BOOT_TOLERANCE_MS + 1));
        assertFalse(ReopenMemory.sameBoot(0L, 1_000_000L));
    }

    @Test
    public void onlyTheFourPagesAreRemembered()
    {
        assertTrue(ReopenMemory.validTab(0));
        assertTrue(ReopenMemory.validTab(3));
        assertFalse(ReopenMemory.validTab(-1));
        assertFalse(ReopenMemory.validTab(4));
    }
}
