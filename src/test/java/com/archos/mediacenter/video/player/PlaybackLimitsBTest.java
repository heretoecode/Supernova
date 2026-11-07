package com.archos.mediacenter.video.player;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import android.app.Application;
import android.content.Context;
import java.util.ArrayList;
import java.util.List;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;

@RunWith(RobolectricTestRunner.class)
@Config(application = Application.class, sdk = 30)
public class PlaybackLimitsBTest
{
    @Test public void idleContinueDoesNotAutoAdvanceButAnEpisodeBoundaryDuringTheQuestionCan()
    {
        List<Runnable> tasks = new ArrayList<>();
        List<PlaybackLimits.Event> events = new ArrayList<>();
        PlaybackLimits limits = new PlaybackLimits(() -> 0L, (delay, task) -> {
            tasks.add(task);
            return () -> {};
        }, events::add);
        limits.configureInactivity(1000);
        tasks.get(0).run();
        assertTrue(limits.inactivityQuestion());
        limits.answerStillWatching(true);
        assertEquals(PlaybackLimits.Event.IDLE_CONTINUE, events.get(events.size() - 1));
        tasks.get(2).run();
        assertFalse(limits.allowAutoAdvance());
        limits.answerStillWatching(true);
        assertEquals(PlaybackLimits.Event.CONTINUE, events.get(events.size() - 1));
        assertTrue(limits.allowAutoAdvance());
        limits.close();
    }

    @Test public void realInputFencesIdleTimersAndClockChoicesMoveWithoutMovingTheSelectedDeadline()
    {
        long[] elapsed = {0};
        List<Runnable> tasks = new ArrayList<>();
        List<PlaybackLimits.Event> events = new ArrayList<>();
        PlaybackLimits limits = new PlaybackLimits(() -> elapsed[0], (delay, task) -> {
            tasks.add(task);
            return () -> {};
        }, events::add);
        limits.configureInactivity(1000);
        limits.interaction();
        tasks.get(0).run();
        assertFalse(limits.questionPending());
        tasks.get(1).run();
        assertTrue(limits.questionPending());
        tasks.get(2).run();
        assertEquals(PlaybackLimits.Event.STOP, events.get(events.size() - 1));
        limits.setTimer(PlaybackLimits.Timer.MINUTES_30);
        assertEquals(1800100L, limits.stopAtEpochMs(PlaybackLimits.Timer.MINUTES_30, 100));
        elapsed[0] = 1000;
        assertEquals(1800000L, limits.selectedStopAtEpochMs(1000));
        assertEquals(1801000L, limits.stopAtEpochMs(PlaybackLimits.Timer.MINUTES_30, 1000));
        limits.close();
        tasks.get(1).run();
        assertFalse(limits.questionPending());
    }
    @Test public void idleKeepWatchingDoesNotInvokeServiceCompletion()
    {
        android.content.Context app = RuntimeEnvironment.getApplication();
        androidx.preference.PreferenceManager.getDefaultSharedPreferences(app).edit()
                .putInt(PlaybackLimits.KEY_IDLE_MINUTES, 1).commit();
        PlayerService service = org.mockito.Mockito.spy(new PlayerService());
        org.robolectric.util.ReflectionHelpers.callInstanceMethod(service, "attachBaseContext",
                org.robolectric.util.ReflectionHelpers.ClassParameter.from(android.content.Context.class, app));
        PlaybackLimits limits = service.playbackLimits();
        org.robolectric.Shadows.shadowOf(android.os.Looper.getMainLooper())
                .idleFor(java.time.Duration.ofMinutes(1));
        assertTrue(limits.inactivityQuestion());
        limits.answerStillWatching(true);
        org.mockito.Mockito.verify(service, org.mockito.Mockito.never()).onCompletion();
        limits.close();
        ((java.util.concurrent.ExecutorService)org.robolectric.util.ReflectionHelpers
                .getField(service, "playerEpisodeWorker")).shutdownNow();
    }

    @Test public void wakingPastTheDeadlineStopsWithoutWaitingForAnUptimeBasedHandlerDelay()
    {
        long[] elapsed = {0};
        List<PlaybackLimits.Event> events = new ArrayList<>();
        PlaybackLimits limits = new PlaybackLimits(() -> elapsed[0], (delay, task) -> () -> {}, events::add);
        limits.setTimer(PlaybackLimits.Timer.MINUTES_30);
        elapsed[0] = 1900000;
        limits.checkDeadlines();
        assertEquals(PlaybackLimits.Event.STOP, events.get(events.size() - 1));
        assertEquals(PlaybackLimits.Timer.OFF, limits.timer());
        limits.configureInactivity(1000);
        elapsed[0] += 1000;
        limits.checkDeadlines();
        assertTrue(limits.inactivityQuestion());
        elapsed[0] += 60001;
        limits.checkDeadlines();
        assertFalse(limits.questionPending());
        assertEquals(PlaybackLimits.Event.STOP, events.get(events.size() - 1));
        limits.close();
    }

    @Test public void inactivityCanBeUsedWithoutAnEpisodeCounter()
    {
        List<Runnable> tasks = new ArrayList<>();
        PlaybackLimits limits = new PlaybackLimits(() -> 0L, (delay, task) -> {
            tasks.add(task);
            return () -> {};
        }, event -> {});
        limits.configureEpisodeCounting(false);
        for (int i = 0; i < 150; i++)
            assertTrue(limits.allowAutoAdvance());
        limits.configureInactivity(1000);
        tasks.get(0).run();
        assertTrue(limits.inactivityQuestion());
        limits.close();
        assertFalse(limits.questionPending());
    }

}
