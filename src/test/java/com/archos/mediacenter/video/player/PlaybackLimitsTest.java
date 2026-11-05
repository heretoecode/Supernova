package com.archos.mediacenter.video.player;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;
import org.junit.Test;

public class PlaybackLimitsTest
{
    @Test public void timerReplacementFencesStaleCallbacksAndEndOfEpisodeStopsBeforeNext()
    {
        long[] now = {0};
        List<Runnable> tasks = new ArrayList<>();
        List<PlaybackLimits.Event> events = new ArrayList<>();
        PlaybackLimits limits = new PlaybackLimits(() -> now[0], (delay, task) -> {
            tasks.add(task);
            return () ->
            {
            };
        }, events::add);
        limits.setTimer(PlaybackLimits.Timer.MINUTES_30);
        now[0] = 1000;
        assertEquals(1799000, limits.remainingMs());
        limits.setTimer(PlaybackLimits.Timer.MINUTES_60);
        tasks.get(0).run();
        assertFalse(events.contains(PlaybackLimits.Event.STOP));
        limits.setTimer(PlaybackLimits.Timer.END_OF_EPISODE);
        assertEquals(-1, limits.remainingMs());
        assertFalse(limits.episodeCompleted());
        assertEquals(PlaybackLimits.Event.STOP, events.get(events.size() - 1));
    }

    @Test public void asksAfterThreeAndStopsUnansweredButConfirmedAnswerResetsCount()
    {
        List<Runnable> tasks = new ArrayList<>();
        List<PlaybackLimits.Event> events = new ArrayList<>();
        PlaybackLimits limits = new PlaybackLimits(() -> 0L, (delay, task) -> {
            assertEquals(60000, delay);
            tasks.add(task);
            return () ->
            {
            };
        }, events::add);
        assertTrue(limits.allowAutoAdvance());
        assertTrue(limits.allowAutoAdvance());
        assertFalse(limits.allowAutoAdvance());
        assertTrue(limits.questionPending());
        limits.answerStillWatching(true);
        tasks.get(0).run();
        assertFalse(events.contains(PlaybackLimits.Event.STOP));
        assertTrue(limits.allowAutoAdvance());
        assertTrue(limits.allowAutoAdvance());
        assertTrue(limits.allowAutoAdvance());
        assertFalse(limits.allowAutoAdvance());
        tasks.get(1).run();
        assertEquals(PlaybackLimits.Event.STOP, events.get(events.size() - 1));
        limits.close();
    }
}
