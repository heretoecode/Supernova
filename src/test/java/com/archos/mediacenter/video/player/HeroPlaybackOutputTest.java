package com.archos.mediacenter.video.player;

import android.app.Application;
import com.archos.medialib.IMediaPlayer;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;
import org.robolectric.util.ReflectionHelpers;
import static org.mockito.Mockito.*;

@RunWith(RobolectricTestRunner.class) @Config(application=Application.class,sdk=28)
public class HeroPlaybackOutputTest {
    @Test public void outputSignalRequiresCurrentPlayingBackendAndFiresOnce(){
        Player player=mock(Player.class,CALLS_REAL_METHODS);
        IMediaPlayer backend=mock(IMediaPlayer.class),stale=mock(IMediaPlayer.class);
        Player.Listener listener=mock(Player.Listener.class);
        ReflectionHelpers.setField(player,"mMediaPlayer",backend);
        ReflectionHelpers.setField(player,"mPlayerListener",listener);
        ReflectionHelpers.setField(player,"mCurrentState",2); // Prepared is not playback.
        player.onInfo(backend,3,0);
        verify(listener,never()).onPlaybackEstablished();
        ReflectionHelpers.setField(player,"mCurrentState",5);
        player.onInfo(stale,3,0);
        verify(listener,never()).onPlaybackEstablished();
        player.onInfo(backend,3,0);
        player.onInfo(backend,3,0);
        player.onSurfaceTextureUpdated(null);
        verify(listener,times(1)).onPlaybackEstablished();
    }
    @Test public void failedOrIdleBackendCannotConsumeEligibility(){
        Player player=mock(Player.class,CALLS_REAL_METHODS);
        Player.Listener listener=mock(Player.Listener.class);
        ReflectionHelpers.setField(player,"mPlayerListener",listener);
        ReflectionHelpers.setField(player,"mCurrentState",5);
        player.onSurfaceTextureUpdated(null); // Surface callback after release.
        verify(listener,never()).onPlaybackEstablished();
        IMediaPlayer backend=mock(IMediaPlayer.class);
        ReflectionHelpers.setField(player,"mMediaPlayer",backend);
        ReflectionHelpers.setField(player,"mCurrentState",-1);
        player.onInfo(backend,3,0);
        verify(listener,never()).onPlaybackEstablished();
    }
}
