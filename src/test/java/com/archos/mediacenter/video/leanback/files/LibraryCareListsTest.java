package com.archos.mediacenter.video.leanback.files;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

/** Library Care › Not matched (FILES.md, tv/20b): the best guess from the file name, or "No guess yet". */
public class LibraryCareListsTest
{
    @Test
    public void bestGuessFromTheFileName()
    {
        assertEquals("Looks like Blue Planet III · S1 E2 · from the file name",
                     FilesPage.bestGuess("Blue.Planet.III.S01E02.1080p.mkv", "Seedbox"));
        assertEquals("Looks like The Glass Orchard · from the file name",
                     FilesPage.bestGuess("The.Glass.Orchard.2025.2160p.mkv", "Films"));
        assertEquals("No guess yet · Downloads", FilesPage.bestGuess("x.mkv", "Downloads"));
    }
}
