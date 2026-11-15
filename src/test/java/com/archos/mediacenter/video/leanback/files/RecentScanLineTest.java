package com.archos.mediacenter.video.leanback.files;

import static org.junit.Assert.assertEquals;

import com.archos.mediacenter.video.scanning.ScanProgress;

import org.junit.Test;

public class RecentScanLineTest
{
    private static final long MINUTE = 60_000L;

    @Test
    public void finishedScansSayWhatTheyFound()
    {
        assertEquals("12 min ago · 340 checked · 2 new",
                     RecentScanLine.second(ScanProgress.Outcome.FINISHED, "", MINUTE, 13 * MINUTE, 340, 2, 0));
        assertEquals("Done", RecentScanLine.pill(ScanProgress.Outcome.FINISHED));
    }

    @Test
    public void failedScansSayWhy()
    {
        assertEquals("12 min ago · Source did not respond in time",
                     RecentScanLine.second(ScanProgress.Outcome.FAILED, "Source did not respond in time",
                                           MINUTE, 13 * MINUTE, 0, 0, 0));
        assertEquals("Failed", RecentScanLine.pill(ScanProgress.Outcome.FAILED));
    }
}
