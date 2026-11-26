package com.archos.mediacenter.video.player;

import static org.junit.Assert.assertEquals;

import android.app.Application;
import java.util.Arrays;
import java.util.Collections;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

/** PLAYER.md title block: the facts line is year · length · up to four genres, shared with Preparing playback. */
@RunWith(RobolectricTestRunner.class)
@Config(application = Application.class, sdk = 28)
public class TitleBlockFactsTest
{
    @Test
    public void yearLengthAndFourGenres()
    {
        assertEquals("2019 · 2 h 51 min · Drama · Crime · Thriller · Mystery",
                     TitleBlockLine.facts(2019, 171 * 60_000L,
                                          Arrays.asList("Drama", "Crime", "Thriller", "Mystery", "History")));
        assertEquals("46 min", TitleBlockLine.facts(0, 46 * 60_000L, Collections.emptyList()));
        assertEquals("", TitleBlockLine.facts(0, 0, null));
        assertEquals("", TitleBlockLine.factsFor(-1, 0));
    }
}
