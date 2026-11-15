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
}
