package com.archos.mediacenter.video.player;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.CALLS_REAL_METHODS;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import android.app.Application;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import com.archos.medialib.IMediaPlayer;
import com.archos.medialib.PlaybackReadEvents;
import com.archos.medialib.PlaybackReadGuard;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.time.Duration;
import java.util.concurrent.Executor;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InOrder;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.Shadows;
import org.robolectric.annotation.Config;
import org.robolectric.annotation.LooperMode;

@RunWith(RobolectricTestRunner.class)
@Config(application = Application.class, manifest = Config.NONE, sdk = 30)
@LooperMode(LooperMode.Mode.PAUSED)
public class NetworkStreamRecovery156Test
{
    private Player player;
    private IMediaPlayer old;
    private NetworkRecoveryPolicy policy;
    private PlaybackReadGuard guard, replacementGuard;
    private IMediaPlayer replacement;

    @Before public void setup() throws Exception
    {
        player = mock(Player.class, CALLS_REAL_METHODS);
        old = mock(IMediaPlayer.class);
        policy = new NetworkRecoveryPolicy();
        set(player, "networkRecovery", policy);
        set(player, "networkRelease", (Executor)Runnable::run);
        set(player, "mHandler", new Handler(Looper.getMainLooper()));
        set(player, "mPreparedAsync", mock(Runnable.class));
        set(player, "mRefreshRateCheckerAsync", mock(Runnable.class));
        set(player, "mMediaPlayer", old);
        set(player, "mUri", Uri.parse("webdavs://host/video.mkv"));
        set(player, "mCurrentState", 5);
        set(player, "mTargetState", 5);
        set(player, "mMetadataReady", true);
        set(player, "mSessionPrepared", true);
        set(player, "mDuration", 100000);
        set(player, "lastShownPosition", -1L);
        Class<?> resume = Class.forName(Player.class.getName() + "$ResumeCtx");
        Constructor<?> constructor = resume.getDeclaredConstructor(Player.class);
        constructor.setAccessible(true);
        Object resumeContext = constructor.newInstance(player);
        java.lang.reflect.Method reset = resume.getDeclaredMethod("reset");
        reset.setAccessible(true);
        reset.invoke(resumeContext);
        set(player, "mResumeCtx", resumeContext);
        when(old.getCurrentPosition()).thenReturn(42000);
        when(old.getDuration()).thenReturn(100000);
        doNothing().when(player).openVideo();
        guard = new PlaybackReadGuard(failure -> {});
        PlaybackReadEvents.register(old, guard);
    }

    @After public void cleanup()
    {
        player.beginPlaybackExit();
        guard.close();
        PlaybackReadEvents.unregister(old, guard);
        if (replacementGuard != null)
        {
            replacementGuard.close();
            PlaybackReadEvents.unregister(replacement, replacementGuard);
        }
    }

    @Test
    public void longPauseRetiresOldDecoderBeforeReopenAndCannotPlayBeforeReplacementSeekCompletes() throws Exception
    {
        player.pause(PlayerController.STATE_NORMAL);
        Shadows.shadowOf(Looper.getMainLooper()).idleFor(Duration.ofSeconds(61));
        player.start(PlayerController.STATE_NORMAL);
        Shadows.shadowOf(Looper.getMainLooper()).idle();
        InOrder ordered = inOrder(old, player);
        ordered.verify(old).release();
        ordered.verify(player).openVideo();
        assertEquals(42000, get(player, "mStopPosition"));
        replacement = mock(IMediaPlayer.class);
        replacementGuard = new PlaybackReadGuard(failure -> {});
        PlaybackReadEvents.register(replacement, replacementGuard);
        set(player, "mMediaPlayer", replacement);
        set(player, "mCurrentState", 4);
        set(player, "mMetadataReady", true);
        Object recovery = get(player, "networkReconnect");
        assertTrue(recovery != null);
        set(recovery, "surfaceReady", true);
        set(player, "mIsBusy", true);
        player.pause(PlayerController.STATE_NORMAL);
        player.start(PlayerController.STATE_NORMAL);
        verify(replacement, never()).start();
        player.onAllSeekComplete(replacement);
        verify(replacement).start();
        verify(old, never()).start();
        assertEquals(42000, get(player, "mStopPosition"));
    }

    @Test
    public void knownReadFailureReopensAtPresentationCheckpointAndRejectsObsoleteBackendCallbacks() throws Exception
    {
        set(player, "lastShownPosition", 41000L);
        set(player, "lastShownAt", android.os.SystemClock.elapsedRealtime());
        assertTrue(player.onError(old, 1001, 1000, "Idle connection dropped"));
        Shadows.shadowOf(Looper.getMainLooper()).idle();
        assertEquals(41000, get(player, "mStopPosition"));
        verify(old).release();
        verify(player).openVideo();
        // Released backend errors cannot fail the replacement or initiate another reopen.
        assertTrue(player.onError(old, 1001, 1000, "late"));
        verify(player).openVideo();
    }

    @Test public void stopBeforeQueuedReopenInvalidatesRecoveryAndDoesNotRevivePlayback() throws Exception
    {
        player.onError(old, 1001, 1000, "read failed");
        player.beginPlaybackExit();
        Shadows.shadowOf(Looper.getMainLooper()).idle();
        verify(player, never()).openVideo();
        assertEquals(true, get(player, "mPlaybackEnding"));
    }

    @Test public void interruptedSeekRetainsTheLatestUserTargetInsteadOfAnOlderRenderedFrame() throws Exception
    {
        set(player, "mStopPosition", 806892);
        set(player, "mDuration", 1000000);
        when(old.getDuration()).thenReturn(1000000);
        set(player, "mIsBusy", true);
        set(player, "lastShownPosition", 730000L);
        set(player, "lastShownAt", android.os.SystemClock.elapsedRealtime());
        assertTrue(player.onError(old, 1001, 1000, "Seek read failed"));
        assertEquals(806892, get(player, "mStopPosition"));
        player.beginPlaybackExit();
        Shadows.shadowOf(Looper.getMainLooper()).idle();
    }

    @Test public void proxyFailureNotificationMustBelongToTheCurrentBackend() throws Exception
    {
        java.lang.reflect.Method callback =
            Player.class.getDeclaredMethod("playbackReadFailed", Object.class, java.io.IOException.class);
        callback.setAccessible(true);
        callback.invoke(player, new Object(), new java.io.IOException("Metadata failure"));
        verify(player, never()).openVideo();
        callback.invoke(player, old, new java.io.IOException("Playback socket closed"));
        Shadows.shadowOf(Looper.getMainLooper()).idle();
        verify(old).release();
        verify(player).openVideo();
        assertEquals(42000, get(player, "mStopPosition"));
        player.beginPlaybackExit();
    }

    private static Object get(Object owner, String name) throws Exception
    {
        Field field = owner.getClass() == Player.class ? Player.class.getDeclaredField(name) : field(owner, name);
        field.setAccessible(true);
        return field.get(owner);
    }
    private static void set(Object owner, String name, Object value) throws Exception
    {
        Field field = field(owner, name);
        field.setAccessible(true);
        field.set(owner, value);
    }
    private static Field field(Object owner, String name) throws Exception
    {
        Class<?> type = owner instanceof Player ? Player.class : owner.getClass();
        return type.getDeclaredField(name);
    }
}
