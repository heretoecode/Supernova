package com.archos.mediacenter.video.leanback.files;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class LibraryCareCountsTest
{
    @Test
    public void pillsSayHowManyOrNone()
    {
        assertEquals("3 files", LibraryCareCounts.pill(3, "file", "files"));
        assertEquals("1 video", LibraryCareCounts.pill(1, "video", "videos"));
        assertEquals("None", LibraryCareCounts.pill(0, "file", "files"));
    }

    /** M5 (owner, 26 Nov 2026): a freshly scanned file is not Not matched until its look-up has ended. */
    @org.junit.Test
    public void titlesStillBeingLookedUpAreNotCountedAsNotMatched()
    {
        com.archos.mediacenter.video.browser.adapters.object.Video fresh =
            new com.archos.mediacenter.video.browser.adapters.object.Video(41, "/storage/TV/Blue.Planet.S01E02.mkv",
                "Blue.Planet.S01E02", null, 0, 0, 0, 0, false, false, false, false, 0, 0);
        com.archos.mediacenter.video.browser.adapters.object.Video failed =
            new com.archos.mediacenter.video.browser.adapters.object.Video(42, "/storage/TV/holiday-clip.mkv",
                "holiday-clip", null, 0, 0, 0, 0, false, false, false, false, 0, 0);
        java.util.List<com.archos.mediacenter.video.browser.adapters.object.Base> media =
            java.util.Arrays.asList(fresh, failed);
        LibraryCareCounts counts = LibraryCareCounts.count(media, false, video -> false,
                                                           java.util.Collections.singleton(41L));
        org.junit.Assert.assertEquals(1, counts.notMatched);
        org.junit.Assert.assertEquals(1, counts.pending);
    }
}
