package com.archos.mediacenter.video.leanback.files;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class DeviceLineTest
{
    @Test
    public void sizesReadLikeDrives()
    {
        assertEquals("14.2 GB", DeviceLine.size(14_200_000_000L));
        assertEquals("32 GB", DeviceLine.size(32_000_000_000L));
        assertEquals("1.1 TB", DeviceLine.size(1_100_000_000_000L));
        assertEquals("512 MB", DeviceLine.size(512_000_000L));
    }

    @Test
    public void rowsSayKindVideosAndSpace()
    {
        assertEquals("SanDisk USB drive · 1,203 videos · 1.1 TB free of 2 TB",
                     DeviceLine.second("SanDisk USB drive", 1203, 1_100_000_000_000L, 2_000_000_000_000L));
        assertEquals("310 videos · 14.2 GB free of 32 GB",
                     DeviceLine.second("", 310, 14_200_000_000L, 32_000_000_000L));
        assertEquals(556, DeviceLine.used(14_200_000_000L, 32_000_000_000L));
    }
}
