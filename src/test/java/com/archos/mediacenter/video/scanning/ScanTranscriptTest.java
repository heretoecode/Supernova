package com.archos.mediacenter.video.scanning;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.List;
import org.junit.Test;

/** Scan details › Step by step (FILES.md tv/24): one line per step, listing every quarter, failures marked. */
public class ScanTranscriptTest
{
    private static ScanProgress scan(long checked, long total, long added, long removed, ScanProgress.Failure failure)
    {
        return new ScanProgress("b1", "seedbox", "Seedbox", checked, total, added, removed, 1, 1_000L, 0, true, false,
                                failure);
    }

    @Test
    public void stepsAreKeptOncePerChange()
    {
        ScanTranscript.clear();
        ScanTranscript.record(scan(0, 400, 0, 0, ScanProgress.Failure.NONE), "started", 1_000L);
        ScanTranscript.record(scan(10, 400, 0, 0, ScanProgress.Failure.NONE), "indexing", 2_000L);
        ScanTranscript.record(scan(20, 400, 0, 0, ScanProgress.Failure.NONE), "indexing", 3_000L);
        ScanTranscript.record(scan(200, 400, 0, 0, ScanProgress.Failure.NONE), "indexing", 4_000L);
        ScanTranscript.record(scan(400, 400, 3, 1, ScanProgress.Failure.NONE), "complete", 5_000L);
        List<ScanTranscript.Line> lines = ScanTranscript.lines("b1", "seedbox");
        assertEquals(4, lines.size());
        assertEquals("Started", lines.get(0).words);
        assertEquals("Listing files · 200 of 400 checked", lines.get(2).words);
        assertEquals("Finished · 3 new · 1 updated · 1 missing", lines.get(3).words);
        ScanTranscript.record(scan(0, 0, 0, 0, ScanProgress.Failure.TIMEOUT), "failed", 6_000L);
        assertTrue(ScanTranscript.lines("b1", "seedbox").get(4).failed);
        assertEquals(0, ScanTranscript.lines("other", "x").size());
    }
}
