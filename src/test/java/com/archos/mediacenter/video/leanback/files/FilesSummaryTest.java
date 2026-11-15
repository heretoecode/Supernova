package com.archos.mediacenter.video.leanback.files;

import static org.junit.Assert.assertEquals;

import java.util.List;

import org.junit.Test;

public class FilesSummaryTest
{
    private static final long MINUTE = 60_000L;

    @Test
    public void factsReadLikeTheMockUp()
    {
        List<FilesSummary.Fact> facts = FilesSummary.facts(1808, 9, 100 * MINUTE, 112 * MINUTE, false);
        assertEquals("1,808 Videos", facts.get(0).text);
        assertEquals("9 To Check", facts.get(1).text);
        assertEquals("12 min ago", facts.get(2).text);
    }

    @Test
    public void nothingToCheckLeavesThatFactOutAndAScanSaysSo()
    {
        List<FilesSummary.Fact> facts = FilesSummary.facts(1, 0, 0, 0, true);
        assertEquals(2, facts.size());
        assertEquals("1 Video", facts.get(0).text);
        assertEquals("Scanning", facts.get(1).text);
    }

    @Test
    public void agoWording()
    {
        assertEquals("Not scanned yet", FilesSummary.ago(0, 5 * MINUTE));
        assertEquals("Just now", FilesSummary.ago(MINUTE, MINUTE + 1000));
        assertEquals("1 hour ago", FilesSummary.ago(MINUTE, 62 * MINUTE));
        assertEquals("2 days ago", FilesSummary.ago(MINUTE, MINUTE + 48 * 60 * MINUTE));
    }
}
