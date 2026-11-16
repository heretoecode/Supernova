package com.archos.mediacenter.video.leanback.files;

import static org.junit.Assert.assertEquals;

import java.util.Arrays;
import java.util.Collections;
import org.junit.Test;

public class LibraryStatsTest
{
    private static final long MONTH = 1_000_000L;
    private static final long HOUR = 3_600_000L;

    @Test
    public void wholeLibraryPagesAndHeaviestShows()
    {
        LibraryStats stats = LibraryStats.of(Arrays.asList(
            new LibraryStats.Item(null, 4_000, true, 2 * HOUR, 0, true, true, true, MONTH + 5),
            new LibraryStats.Item(null, 1_000, false, 2 * HOUR, HOUR, false, false, false, 10),
            new LibraryStats.Item("Show A", 3_000, true, HOUR, 0, true, true, false, 10),
            new LibraryStats.Item("Show A", 3_000, false, HOUR, 0, false, false, false, MONTH),
            new LibraryStats.Item("Show B", 1_000, true, HOUR, 0, true, false, false, 10)), MONTH);
        assertEquals(4, stats.all.titles);
        assertEquals(5, stats.all.videos);
        assertEquals(8_000, stats.all.localBytes);
        assertEquals(4_000, stats.all.networkBytes);
        assertEquals(2, stats.movies.titles);
        assertEquals(2, stats.shows.titles);
        assertEquals(3, stats.shows.videos);
        assertEquals("Watched: film 1 and Show B", 2, stats.watchedTitles);
        assertEquals("Unwatched film 1 h left plus one 1 h episode", 2, stats.hoursToWatch);
        assertEquals(2, stats.uhdTitles);
        assertEquals(1, stats.hdrTitles);
        assertEquals("Film 1 and Show A", 2, stats.addedThisMonth);
        assertEquals(50, stats.percent(stats.watchedTitles));
        assertEquals("Show A", stats.heaviest.get(0).show);
        assertEquals(3_000, stats.heaviest.get(0).perEpisode());
    }

    @Test
    public void emptyLibraryHasNoTitlesAndNoPercent()
    {
        LibraryStats stats = LibraryStats.of(Collections.emptyList(), MONTH);
        assertEquals(0, stats.all.titles);
        assertEquals(0, stats.percent(0));
        assertEquals(0, stats.heaviest.size());
    }
}
