package com.archos.mediacenter.video.player;

import android.app.Application;
import android.content.SharedPreferences;
import com.archos.mediacenter.utils.videodb.IndexHelper;
import com.archos.mediacenter.utils.videodb.VideoDbInfo;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;
import org.robolectric.util.ReflectionHelpers;
import static org.mockito.Mockito.*;
import static org.junit.Assert.*;

@RunWith(RobolectricTestRunner.class) @Config(application=Application.class,sdk=28)
public class FailedPlaybackCheckpointTest {
    @Test public void realCheckpointMethodDoesNotWriteFailedStartOrAccessDestinationHistory()throws Exception{
        PlayerService service=mock(PlayerService.class,CALLS_REAL_METHODS);IndexHelper index=mock(IndexHelper.class);VideoDbInfo info=new VideoDbInfo();info.id=42;info.scraperMovieId="123";
        Class<?> type=Class.forName("com.archos.mediacenter.video.player.PlayerService$PlaybackSession");Object session=ReflectionHelpers.callConstructor(type);
        ReflectionHelpers.setField(session,"startPositionApplied",true);ReflectionHelpers.setField(session,"hasPlayed",true);ReflectionHelpers.setField(session,"failed",true);ReflectionHelpers.setField(session,"outputEstablished",false);
        ReflectionHelpers.setField(service,"mPlaybackSession",session);ReflectionHelpers.setField(service,"mIndexHelper",index);ReflectionHelpers.setField(service,"mVideoInfo",info);ReflectionHelpers.setField(service,"mPlayer",null);
        service.saveVideoStateIfReady();verify(index,never()).writeVideoInfo(any(VideoDbInfo.class),anyBoolean());verify(service,never()).getSharedPreferences(anyString(),anyInt());
    }
    @Test public void cancelledPreparedPlayerPreservesPriorResumeAndNeverInitialisesViewingCoverage()throws Exception{
        PlayerService service=mock(PlayerService.class,CALLS_REAL_METHODS);IndexHelper index=mock(IndexHelper.class);SharedPreferences preferences=mock(SharedPreferences.class);Player player=mock(Player.class);when(player.isInPlaybackState()).thenReturn(true);when(player.getDuration()).thenReturn(300000);when(player.getCurrentPosition()).thenReturn(0);
        VideoDbInfo info=new VideoDbInfo();info.id=42;info.scraperMovieId="123";info.resume=3456;info.lastTimePlayed=100;info.duration=300000;
        Class<?> type=Class.forName("com.archos.mediacenter.video.player.PlayerService$PlaybackSession");Object session=ReflectionHelpers.callConstructor(type);ReflectionHelpers.setField(session,"startPositionApplied",true);ReflectionHelpers.setField(session,"hasPlayed",true);ReflectionHelpers.setField(session,"failed",false);ReflectionHelpers.setField(session,"outputEstablished",false);
        ReflectionHelpers.setField(service,"mPlaybackSession",session);ReflectionHelpers.setField(service,"mIndexHelper",index);ReflectionHelpers.setField(service,"mVideoInfo",info);ReflectionHelpers.setField(service,"mPlayer",player);ReflectionHelpers.setField(service,"mPreferences",preferences);ReflectionHelpers.setField(service,"mPlayerState",PlayerService.PlayerState.PAUSED);
        service.saveVideoStateIfReady();verify(index,never()).writeVideoInfo(any(VideoDbInfo.class),anyBoolean());verifyNoInteractions(preferences);assertEquals(3456,info.resume);assertEquals(100,info.lastTimePlayed);verify(service,never()).getSharedPreferences(anyString(),anyInt());
    }
}
