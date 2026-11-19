package com.archos.mediacenter.video.player;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import java.util.List;
import org.junit.Test;

public class PlaybackPartsTest
{
    private PlaybackParts.Part part(String name, PlaybackParts.Kind kind, long start, long end)
    {
        return new PlaybackParts.Part(name, kind, start, end);
    }

    @Test public void snapsNearEdgesReplacesIntroOnlyChapterAndKeepsBothChapterSources()
    {
        PlaybackParts timeline =
            PlaybackParts.merge(List.of(part("Opening", PlaybackParts.Kind.CHAPTER, 0, 100000),
                                        part("File chapter", PlaybackParts.Kind.CHAPTER, 32000, 100000)),
                                List.of(part("DB chapter", PlaybackParts.Kind.CHAPTER, 60000, 100000)),
                                List.of(part("Intro", PlaybackParts.Kind.INTRO, 0, 30000)), 100000, true);
        assertEquals(3, timeline.parts().size());
        assertEquals(PlaybackParts.Kind.INTRO, timeline.partAt(29999).kind);
        assertEquals("File chapter", timeline.partAt(30000).name);
        assertEquals("DB chapter", timeline.partAt(60000).name);
        assertNull(timeline.partAt(100000));
    }

    @Test public void noChaptersBreaksOnlyAtIntroMarkersAndFillsTheRemainingEpisodeOrFilm()
    {
        PlaybackParts timeline =
            PlaybackParts.merge(List.of(), List.of(),
                                List.of(part("Intro", PlaybackParts.Kind.INTRO, 10000, 20000),
                                        part("Credits", PlaybackParts.Kind.CREDITS, 90000, 120000)),
                                100000, true);
        assertEquals(4, timeline.parts().size());
        assertEquals("Episode", timeline.partAt(0).name);
        assertEquals("Episode", timeline.partAt(20000).name);
        assertEquals(100000, timeline.parts().get(3).endMs);
        assertEquals("Film", PlaybackParts.merge(List.of(), List.of(), List.of(), 1000, false).partAt(0).name);
    }

    @Test public void overlappingProviderMarkersHaveDeterministicContinuousCoverage()
    {
        PlaybackParts timeline = PlaybackParts.merge(List.of(), List.of(),
                                                     List.of(part("Credits", PlaybackParts.Kind.CREDITS, 80000, 100000),
                                                             part("Credits", PlaybackParts.Kind.CREDITS, 85000, 95000)),
                                                     100000, false);
        assertEquals(2, timeline.parts().size());
        assertEquals(80000, timeline.parts().get(0).endMs);
        assertEquals(80000, timeline.parts().get(1).startMs);
    }

    @Test public void aPartUnderTenSecondsJoinsTheNextOne()
    {
        // PL5 (agreed 18 Nov 2026): no "Episode · From 0:00" stub before "Recap · From 0:02".
        PlaybackParts timeline =
            PlaybackParts.merge(List.of(), List.of(),
                                List.of(part("Recap", PlaybackParts.Kind.RECAP, 2000, 60000)), 120000, true);
        assertEquals(2, timeline.parts().size());
        assertEquals("Recap", timeline.partAt(0).name);
        assertEquals(0, timeline.parts().get(0).startMs);
    }

    @Test
    public void genericChapterNamesAreRenumberedAndRealNamesKept()
    {
        String[] generic = {"", "Scene 3", "Chapter 07", "Ch. 2", "chap 4", "Section 9", "12", "00:05:00.000"};
        for (String name : generic)
            org.junit.Assert.assertTrue(name, PlaybackParts.genericName(name));
        String[] real = {"Recap", "Intro", "The Long Night", "Scene of the crime"};
        for (String name : real)
            org.junit.Assert.assertFalse(name, PlaybackParts.genericName(name));
    }
}
