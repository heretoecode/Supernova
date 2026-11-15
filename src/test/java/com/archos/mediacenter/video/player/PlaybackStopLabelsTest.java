package com.archos.mediacenter.video.player;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

import android.app.Application;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

@RunWith(RobolectricTestRunner.class)
@Config(application = Application.class, sdk = 30)
public class PlaybackStopLabelsTest
{
    @Test
    public void offeredTimesRollWithTheClockAndEndFollowsWhatIsPlaying()
    {
        long now = 1_700_000_000_000L;
        String[] episode = PlaybackSubtitlePanel.stopLabels(now, 0, 0, true);
        assertEquals("Off", episode[0]);
        assertEquals("End of episode", episode[4]);
        assertEquals("End of movie", PlaybackSubtitlePanel.stopLabels(now, 0, 0, false)[4]);
        String[] later = PlaybackSubtitlePanel.stopLabels(now + 10 * 60_000L, 0, 0, true);
        assertNotEquals(episode[1], later[1]);
    }

    @Test
    public void aRunningTimerShowsItsOwnEndTime()
    {
        long now = 1_700_000_000_000L;
        long endsAt = now + 30 * 60_000L;
        String fixed = PlaybackSubtitlePanel.stopLabels(now, 1, endsAt, true)[1];
        String laterStill = PlaybackSubtitlePanel.stopLabels(now + 10 * 60_000L, 1, endsAt, true)[1];
        assertEquals(fixed, laterStill);
    }
}
