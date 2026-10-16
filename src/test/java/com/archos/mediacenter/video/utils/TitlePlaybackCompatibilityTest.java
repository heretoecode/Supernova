package com.archos.mediacenter.video.utils;

import android.app.Application;
import com.archos.medialib.IMediaPlayer;
import com.archos.medialib.MediaMetadata;
import com.archos.mediaprovider.video.VideoStore;
import com.archos.mediacenter.video.utils.backup.*;
import java.util.*;
import org.junit.*;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith(RobolectricTestRunner.class) @Config(application=Application.class,sdk=28)
public class TitlePlaybackCompatibilityTest {
    private VideoMetadata metadata(boolean[] supported,String[] languages,boolean[] external,String[] paths) {
        Map<Integer,Object> values=new HashMap<>();values.put(IMediaPlayer.METADATA_KEY_NB_AUDIO_TRACK,supported.length);values.put(IMediaPlayer.METADATA_KEY_NB_SUBTITLE_TRACK,languages.length);
        for(int i=0;i<supported.length;i++){int key=IMediaPlayer.METADATA_KEY_AUDIO_TRACK+IMediaPlayer.METADATA_KEY_AUDIO_TRACK_MAX*i;values.put(key+IMediaPlayer.METADATA_KEY_AUDIO_TRACK_SUPPORTED,supported[i]);}
        for(int i=0;i<languages.length;i++){int key=IMediaPlayer.METADATA_KEY_SUBTITLE_TRACK+IMediaPlayer.METADATA_KEY_SUBTITLE_TRACK_MAX*i;values.put(key+IMediaPlayer.METADATA_KEY_SUBTITLE_TRACK_LANGUAGE,languages[i]);values.put(key+IMediaPlayer.METADATA_KEY_SUBTITLE_TRACK_PATH,external[i]?paths[i]:"");}
        MediaMetadata raw=mock(MediaMetadata.class);when(raw.has(anyInt())).thenAnswer(call->values.containsKey(call.getArgument(0)));when(raw.getInt(anyInt())).thenAnswer(call->{Object value=values.get(call.getArgument(0));return value instanceof Number?((Number)value).intValue():0;});when(raw.getBoolean(anyInt())).thenAnswer(call->Boolean.TRUE.equals(values.get(call.getArgument(0))));when(raw.getString(anyInt())).thenAnswer(call->{Object value=values.get(call.getArgument(0));return value instanceof String?(String)value:null;});VideoMetadata result=new VideoMetadata();result.setData(raw);return result;
    }
    private PortableTitlePlayback.State choice(int audio,int subtitle,String language){return new PortableTitlePlayback.State(VideoStore.paramsFromTracks(audio,subtitle),321,2,language);}
    @Test public void actualMetadataParserAllowsSupportedTrackAndExactSubtitleLanguageAliases() {
        var metadata=metadata(new boolean[]{true,true},new String[]{"eng","fra"},new boolean[]{false,false},new String[]{null,null});assertTrue(TitlePlaybackCompatibility.accepts(metadata,choice(0,0,"en")));assertTrue(TitlePlaybackCompatibility.accepts(metadata,choice(1,1,"fr")));assertFalse(TitlePlaybackCompatibility.accepts(metadata,choice(0,1,"en")));
    }
    @Test public void missingMetadataUnsupportedOrOutOfRangeAudioNeverAppliesChoice() {
        var metadata=metadata(new boolean[]{true,false},new String[]{},new boolean[]{},new String[]{});assertFalse(TitlePlaybackCompatibility.accepts(null,choice(0,-1,null)));assertFalse(TitlePlaybackCompatibility.accepts(metadata,choice(1,-1,null)));assertFalse(TitlePlaybackCompatibility.accepts(metadata,choice(2,-1,null)));assertFalse(TitlePlaybackCompatibility.accepts(metadata(new boolean[]{},new String[]{},new boolean[]{},new String[]{}),choice(0,-1,null)));
    }
    @Test public void receivingNoneSentinelUnsetAndOverflowAreDistinct() {
        var metadata=metadata(new boolean[]{true},new String[]{"eng"},new boolean[]{false},new String[]{null});assertFalse(TitlePlaybackCompatibility.accepts(metadata,choice(0,1,null)));assertTrue(TitlePlaybackCompatibility.accepts(metadata,choice(0,-1,null)));assertFalse(TitlePlaybackCompatibility.accepts(metadata,choice(0,2,null)));assertFalse(TitlePlaybackCompatibility.accepts(metadata,choice(0,1,"en")));assertFalse(TitlePlaybackCompatibility.accepts(metadata,choice(0,-1,"en")));
        var none=new PortableTitlePlayback.State(VideoStore.paramsFromTracks(0,5),321,2,null,true);assertTrue(TitlePlaybackCompatibility.accepts(metadata,none));assertEquals(1,VideoStore.paramsToSubtitleTrack(TitlePlaybackCompatibility.receivingParams(metadata,none)));
        var track=new PortableTitlePlayback.State(VideoStore.paramsFromTracks(0,0),321,2,null,false);assertTrue(TitlePlaybackCompatibility.accepts(metadata,track));assertEquals(0,VideoStore.paramsToSubtitleTrack(TitlePlaybackCompatibility.receivingParams(metadata,track)));
    }
    @Test public void externalFormatChoiceRequiresAnEnumeratedExternalTrackOfThatFormat() {
        var metadata=metadata(new boolean[]{true},new String[]{"","",""},new boolean[]{true,true,false},new String[]{"/PRIVATE_CANARY.srt","/PRIVATE_CANARY.vtt","/PRIVATE_CANARY.srt"});assertTrue(TitlePlaybackCompatibility.accepts(metadata,choice(0,0,"srt")));assertTrue(TitlePlaybackCompatibility.accepts(metadata,choice(0,1,"vtt")));assertFalse(TitlePlaybackCompatibility.accepts(metadata,choice(0,1,"srt")));assertFalse(TitlePlaybackCompatibility.accepts(metadata,choice(0,2,"srt")));
    }
    @Test public void nullParametersCanRetainDelayIntentButCannotClaimUnvalidatedLanguageSelection() {
        var metadata=new VideoMetadata();assertTrue(TitlePlaybackCompatibility.accepts(metadata,new PortableTitlePlayback.State(null,321,1,null)));assertFalse(TitlePlaybackCompatibility.accepts(metadata,new PortableTitlePlayback.State(null,321,1,"en")));assertFalse(TitlePlaybackCompatibility.accepts(null,new PortableTitlePlayback.State(null,0,0,null)));
    }
}
