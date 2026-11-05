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
}
