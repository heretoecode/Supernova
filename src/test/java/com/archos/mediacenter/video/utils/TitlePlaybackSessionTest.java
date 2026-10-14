package com.archos.mediacenter.video.utils;

import android.app.Application;
import android.content.Context;
import android.net.Uri;
import androidx.preference.PreferenceManager;
import com.archos.mediacenter.utils.videodb.VideoDbInfo;
import com.archos.mediacenter.video.browser.adapters.object.Video;
import com.archos.mediacenter.video.player.Player;
import com.archos.mediacenter.video.player.PlayerService;
import com.archos.mediacenter.video.utils.backup.*;
import com.archos.medialib.*;
import com.archos.mediaprovider.video.VideoStore;
import java.util.*;
import org.json.*;
import org.junit.*;
import org.junit.runner.RunWith;
import org.robolectric.*;
import org.robolectric.annotation.Config;
import org.robolectric.util.ReflectionHelpers;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith(RobolectricTestRunner.class) @Config(application=Application.class,sdk=28)
public class TitlePlaybackSessionTest {
    private Context app;private VideoDbInfo info;private Player player;private VideoMetadata metadata;private TitlePlaybackSession session;
    @Before public void setup()throws Exception{
        app=RuntimeEnvironment.getApplication();var prefs=PreferenceManager.getDefaultSharedPreferences(app);prefs.edit().clear().putString("oauth_token","AUTH_CANARY").commit();Video video=mock(Video.class);when(video.getId()).thenReturn(700L);when(video.getFileUri()).thenReturn(Uri.parse("file:///PRIVATE_PATH.mkv"));var refs=new PortablePhysicalReferences(app,List.of(video));var payload=PortableTitlePlayback.export(app,Map.of(700L,new PortableTitlePlayback.State(VideoStore.paramsFromTracks(0,5),321,2,null,true)),refs);var prepared=TitlePlaybackOverrides.prepare(prefs.getAll(),payload,Map.of(700L,new PortableTitlePlayback.State(null,0,0,null)),refs);var edit=prefs.edit();for(var value:prepared.values.entrySet())edit.putString(value.getKey(),(String)value.getValue());edit.commit();
        info=new VideoDbInfo(Uri.parse("file:///PRIVATE_PATH.mkv"));info.id=700;info.audioTrack=3;info.subtitleTrack=2;info.lastTimePlayed=0;info.bookmark=4444;session=new TitlePlaybackSession();player=mock(Player.class);when(player.setAudioTrack(anyInt())).thenReturn(true);when(player.setSubtitleTrack(anyInt())).thenReturn(true);metadata=metadata();
    }
    private VideoMetadata metadata(){Map<Integer,Object> values=new HashMap<>();values.put(IMediaPlayer.METADATA_KEY_NB_AUDIO_TRACK,2);values.put(IMediaPlayer.METADATA_KEY_NB_SUBTITLE_TRACK,1);for(int i=0;i<2;i++){int key=IMediaPlayer.METADATA_KEY_AUDIO_TRACK+IMediaPlayer.METADATA_KEY_AUDIO_TRACK_MAX*i;values.put(key+IMediaPlayer.METADATA_KEY_AUDIO_TRACK_SUPPORTED,true);values.put(key+IMediaPlayer.METADATA_KEY_AUDIO_TRACK_LANGUAGE,"eng");values.put(key+IMediaPlayer.METADATA_KEY_AUDIO_TRACK_FORMAT,"aac");}values.put(IMediaPlayer.METADATA_KEY_SUBTITLE_TRACK+IMediaPlayer.METADATA_KEY_SUBTITLE_TRACK_LANGUAGE,"eng");MediaMetadata raw=mock(MediaMetadata.class);when(raw.has(anyInt())).thenAnswer(call->values.containsKey(call.getArgument(0)));when(raw.getInt(anyInt())).thenAnswer(call->{Object value=values.get(call.getArgument(0));return value instanceof Number?((Number)value).intValue():0;});when(raw.getBoolean(anyInt())).thenAnswer(call->Boolean.TRUE.equals(values.get(call.getArgument(0))));when(raw.getString(anyInt())).thenAnswer(call->{Object value=values.get(call.getArgument(0));return value instanceof String?(String)value:null;});VideoMetadata result=new VideoMetadata();result.setData(raw);return result;}
    private boolean pending()throws Exception{return new JSONObject(PreferenceManager.getDefaultSharedPreferences(app).getString(TitlePlaybackOverrides.key(700),null)).getBoolean("pending");}
    @Test public void actualSessionQueuesRawNoneAndAcknowledgesOnlyAfterBothBackendSelections()throws Exception{
        session.videoInfo(app,info);assertEquals(-1,info.audioTrack);assertTrue(session.metadata(app,info,metadata,player));verify(player).setAudioTrack(0);verify(player).setSubtitleTrack(-1);verify(player).setSubtitleDelay(321);verify(player).setSubtitleRatio(2);assertEquals(1,info.subtitleTrack);assertEquals(0,info.lastTimePlayed);assertEquals(4444,info.bookmark);assertTrue(pending());session.selection(app,info,true,0,true);assertTrue(pending());session.selection(app,info,false,-1,true);assertFalse(pending());assertFalse(session.metadata(app,info,metadata,player));verify(player,times(1)).setAudioTrack(0);
    }
    @Test public void missingEnumerationDoesNotApplyAndLaterMetadataCanResolveOnce()throws Exception{session.videoInfo(app,info);assertFalse(session.metadata(app,info,null,player));verifyNoInteractions(player);assertTrue(pending());assertTrue(session.metadata(app,info,metadata,player));verify(player,times(1)).setAudioTrack(0);}
    @Test public void immediateAndAsynchronousRejectionsStayPendingWithoutRepeatedMetadataRetry()throws Exception{
        when(player.setAudioTrack(0)).thenReturn(false);assertFalse(session.metadata(app,info,metadata,player));assertTrue(pending());assertEquals(-1,info.audioTrack);assertFalse(session.metadata(app,info,metadata,player));verify(player,times(1)).setAudioTrack(0);
        setup();assertTrue(session.metadata(app,info,metadata,player));session.selection(app,info,true,0,false);assertTrue(pending());assertFalse(session.metadata(app,info,metadata,player));verify(player,times(1)).setAudioTrack(0);
    }
    @Test public void synchronousBackendCallbacksDoNotRaceBeforeSetterAcceptance()throws Exception{
        when(player.setAudioTrack(0)).thenAnswer(call->{session.selection(app,info,true,0,true);return true;});when(player.setSubtitleTrack(-1)).thenAnswer(call->{session.selection(app,info,false,-1,true);return true;});assertTrue(session.metadata(app,info,metadata,player));assertFalse(pending());
    }
    @Test public void laterManualTrackAndDelayChoiceSupersedesRestorationAndStaleCallbacks()throws Exception{
        session.metadata(app,info,metadata,player);info.audioTrack=1;info.subtitleTrack=0;info.nbSubtitles=1;info.subtitleLanguage="en";info.subtitleDelay=777;info.subtitleRatio=1;assertTrue(TitlePlaybackOverrides.supersede(app,info));session.manual(app,info);String manual=TitlePlaybackOverrides.token(app,info);session.selection(app,info,true,0,true);session.selection(app,info,false,-1,true);assertEquals(manual,TitlePlaybackOverrides.token(app,info));assertFalse(session.metadata(app,info,metadata,player));assertEquals(1,info.audioTrack);var chosen=TitlePlaybackOverrides.validatedChoice(app,info,metadata);assertEquals(1,VideoStore.paramsToAudioTrack(chosen.params));assertEquals(0,VideoStore.paramsToSubtitleTrack(chosen.params));assertEquals(777,chosen.delay);assertFalse(pending());
    }
    @Test public void newReceivingRowGuardNeverQueuesAStaleRestore()throws Exception{info.uri=Uri.parse("file:///different.mkv");assertFalse(session.metadata(app,info,metadata,player));verifyNoInteractions(player);assertEquals(3,info.audioTrack);assertTrue(pending());}
    @Test public void realServiceHookAndCallbackMethodsUseTheSameSelectionCoordinator()throws Exception{
        PlayerService service=mock(PlayerService.class,CALLS_REAL_METHODS);doReturn(app.getPackageName()).when(service).getPackageName();doAnswer(call->app.getSharedPreferences(call.getArgument(0),call.getArgument(1))).when(service).getSharedPreferences(anyString(),anyInt());ReflectionHelpers.setField(service,"restoredTitle",session);ReflectionHelpers.setField(service,"mVideoInfo",info);ReflectionHelpers.setField(service,"mPlayer",player);
        ReflectionHelpers.callInstanceMethod(service,"applyRestoredTitle",ReflectionHelpers.ClassParameter.from(VideoMetadata.class,metadata));verify(player).setAudioTrack(0);verify(player).setSubtitleTrack(-1);service.onAudioTrackSelectionCompleted(0,true);assertTrue(pending());service.onSubtitleTrackSelectionCompleted(-1,true);assertFalse(pending());assertEquals(0,info.lastTimePlayed);assertEquals("AUTH_CANARY",PreferenceManager.getDefaultSharedPreferences(app).getString("oauth_token",null));
    }
}
