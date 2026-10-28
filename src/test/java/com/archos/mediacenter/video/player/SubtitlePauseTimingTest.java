package com.archos.mediacenter.video.player;

import android.os.Handler;
import android.widget.FrameLayout;
import com.archos.medialib.Subtitle;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;
import org.robolectric.annotation.LooperMode;
import org.robolectric.shadows.ShadowSystemClock;
import java.lang.reflect.Field;
import java.time.Duration;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith(RobolectricTestRunner.class)
@Config(sdk = 30, application = android.app.Application.class)
@LooperMode(LooperMode.Mode.PAUSED)
public class SubtitlePauseTimingTest {
    private SubtitleManager manager() throws Exception {
        android.app.Application app = RuntimeEnvironment.getApplication();
        SubtitleManager manager = new SubtitleManager(app, new FrameLayout(app), null, true);
        field(SubtitleManager.class, "mHandler").set(manager, mock(Handler.class));
        return manager;
    }
    private Subtitle cue(int position, int duration) {
        Subtitle cue = mock(Subtitle.class);
        when(cue.isTimed()).thenReturn(true);
        when(cue.getPosition()).thenReturn(position);
        when(cue.getDuration()).thenReturn(duration);
        return cue;
    }
    @Test public void pausedReplacementRetainsItsEntireRemainingDuration() throws Exception {
        SubtitleManager manager = manager();
        manager.onPause();
        SubtitleManager.DispSubtitleThread worker = manager.new DispSubtitleThread();
        Subtitle first = cue(0, 5000), replacement = cue(1000, 3000);
        worker.addSubtitle(first);
        ShadowSystemClock.advanceBy(Duration.ofSeconds(20));
        worker.addSubtitle(replacement);
        ShadowSystemClock.advanceBy(Duration.ofSeconds(20));
        assertSame(replacement, field(worker.getClass(), "mCurrentSubtitle").get(worker));
        assertEquals(3000L, field(worker.getClass(), "mRemainingMs").get(worker));
    }
    @Test public void resumeStartsDeadlineFromRemainingTimeAndClearDropsQueuedCue() throws Exception {
        SubtitleManager manager = manager();
        manager.onPause();
        SubtitleManager.DispSubtitleThread worker = manager.new DispSubtitleThread();
        worker.addSubtitle(cue(0, 5000));
        ShadowSystemClock.advanceBy(Duration.ofSeconds(20));
        worker.setSuspended(false);
        ShadowSystemClock.advanceBy(Duration.ofSeconds(2));
        worker.setSuspended(true);
        assertEquals(3000L, field(worker.getClass(), "mRemainingMs").get(worker));
        worker.clear();
        assertNull(field(worker.getClass(), "mCurrentSubtitle").get(worker));
        assertNull(field(worker.getClass(), "mNextSubtitle").get(worker));
    }
    @Test public void pausedZeroDurationClearDoesNotBecomeVisibleCue() throws Exception {
        SubtitleManager manager = manager();
        manager.onPause();
        SubtitleManager.DispSubtitleThread worker = manager.new DispSubtitleThread();
        worker.addSubtitle(cue(0, Integer.MAX_VALUE));
        worker.addSubtitle(cue(1000, 0));
        assertNull(field(worker.getClass(), "mCurrentSubtitle").get(worker));
        assertNull(field(worker.getClass(), "mNextSubtitle").get(worker));
    }
    private static Field field(Class<?> type, String name) throws Exception {
        Field field = type.getDeclaredField(name); field.setAccessible(true); return field;
    }
}
