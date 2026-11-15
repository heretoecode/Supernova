package com.archos.mediacenter.video.player;

import static org.junit.Assert.assertEquals;

import android.app.Application;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

@RunWith(RobolectricTestRunner.class)
@Config(application = Application.class, sdk = 30)
public class PreparingEpisodeLineTest
{
    @Test
    public void episodeLineMatchesDetailsAndThePlayer()
    {
        assertEquals("Season 2 · Episode 8 · We Don't Talk Anymore",
                     PreviewPlaybackLoading.episodeLine(2, 8, "We Don't Talk Anymore"));
        assertEquals("Season 1 · Episode 3", PreviewPlaybackLoading.episodeLine(1, 3, " "));
    }
}
