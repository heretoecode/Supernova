package com.archos.mediacenter.video.leanback.filebrowsing;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class UnlibraryDetailsWordsTest
{
    @Test
    public void yearSeasonAndEpisodeInPlainWords()
    {
        assertEquals("2024", UnlibraryDetailsWords.detail("2024", -1, -1));
        assertEquals("Season 2 · Episode 5", UnlibraryDetailsWords.detail("", 2, 5));
        assertEquals("2019 · Season 1 · Episode 1", UnlibraryDetailsWords.detail("2019", 1, 1));
        assertEquals("Unknown", UnlibraryDetailsWords.detail(null, -1, -1));
    }
}
