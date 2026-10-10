package com.archos.mediacenter.video.leanback.search;
import org.junit.Test;
import static org.junit.Assert.*;
public class PreviewSearchTextTest {
 @Test public void accentsPunctuationAndLeadingArticlesMatch(){assertTrue(PreviewSearchText.matches("amelie","Amélie"));assertTrue(PreviewSearchText.matches("spider man","Spider-Man"));assertTrue(PreviewSearchText.matches("The Matrix","Matrix"));assertTrue(PreviewSearchText.matches("strasse","Straße"));}
 @Test public void exactAndPrefixTitlesRankAheadOfMinorTypos(){assertTrue(PreviewSearchText.rank("matrix","Matrix")>PreviewSearchText.rank("matrix","Matrix Reloaded"));assertTrue(PreviewSearchText.rank("matrix","Matrix Reloaded")>PreviewSearchText.rank("matrix","Matrux"));assertFalse(PreviewSearchText.matches("matrix","Mat"));assertFalse(PreviewSearchText.matches("at","It"));}
}
