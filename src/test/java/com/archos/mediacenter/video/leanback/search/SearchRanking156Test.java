package com.archos.mediacenter.video.leanback.search;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.List;
import org.junit.Test;

public class SearchRanking156Test
{
    @Test public void ownerClaimsCoverOriginalTitlesNamesSurnamesAnyOrderAccentsAndOneWrongLetter()
    {
        assertTrue(PreviewSearchText.matches("amelie", "Le Fabuleux Destin d’Amélie Poulain"));
        assertTrue(PreviewSearchText.matches("man spider", "Spider-Man"));
        assertTrue(PreviewSearchText.matches("spielberg", "Steven Spielberg"));
        assertTrue(PreviewSearchText.matches("ste spiel", "Steven Spielberg"));
        assertTrue(PreviewSearchText.matches("spilberg", "Steven Spielberg"));
        assertTrue(PreviewSearchText.matches("the matrix", "Matrix"));
        assertFalse(PreviewSearchText.matches("at", "Matrix"));
        assertFalse(PreviewSearchText.matches("in", "Steven Spielberg"));
        assertFalse(PreviewSearchText.matches("xx", "Steven Spielberg"));
    }

    @Test public void titleAndOriginalTitleAndFullNamesAlwaysRankAboveFilenameOnlyHits()
    {
        List<PreviewSearchText.Ranked<String>> result = PreviewSearchText.ranked(
            "spielberg",
            List.of(new PreviewSearchText.Document<>("file", List.of("Unknown"), List.of(), "spielberg.mkv"),
                    new PreviewSearchText.Document<>("person", List.of("Jaws"), List.of("Steven Spielberg"), "x.mkv"),
                    new PreviewSearchText.Document<>("title", List.of("Spielberg"), List.of(), "x.mkv")));
        assertEquals("title", result.get(0).value);
        assertEquals("person", result.get(1).value);
        assertEquals("file", result.get(2).value);
        assertTrue(PreviewSearchText.ranked("the", List.of()).isEmpty());
    }
}
