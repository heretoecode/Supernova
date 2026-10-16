package com.archos.mediacenter.video.utils;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import androidx.preference.PreferenceManager;
import com.archos.mediacenter.utils.videodb.VideoDbInfo;
import com.archos.mediacenter.video.browser.adapters.object.Video;
import com.archos.mediacenter.video.utils.backup.*;
import com.archos.mediaprovider.video.VideoStore;
import java.util.*;
import org.json.*;
import org.junit.*;
import org.junit.runner.RunWith;
import org.robolectric.*;
import org.robolectric.annotation.Config;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith(RobolectricTestRunner.class) @Config(application=Application.class,sdk=28)
public class TitlePlaybackOverridesTest {
    private Context app;private SharedPreferences prefs;
    private static final String PATH="file:///PRIVATE_PATH.mkv";
    @Before public void setup(){app=RuntimeEnvironment.getApplication();prefs=PreferenceManager.getDefaultSharedPreferences(app);prefs.edit().clear().putString("oauth_token","AUTH_CANARY").commit();}
    private Video video(long id,String uri){Video video=mock(Video.class);when(video.getId()).thenReturn(id);when(video.getFileUri()).thenReturn(Uri.parse(uri));return video;}
    private PortablePhysicalReferences references(Video... videos){return new PortablePhysicalReferences(app,List.of(videos));}
    private Map<Long,PortableTitlePlayback.State> nativeChoices(){return Map.of(700L,new PortableTitlePlayback.State(VideoStore.paramsFromTracks(2,1),200,1,"fr"));}
    private JSONObject incoming(PortablePhysicalReferences references)throws Exception{return PortableTitlePlayback.export(app,Map.of(700L,new PortableTitlePlayback.State(VideoStore.paramsFromTracks(0,5),321,2,null,true)),references);}
    private void commit(TitlePlaybackOverrides.Prepared prepared){SharedPreferences.Editor edit=prefs.edit();for(String key:prepared.keys)edit.remove(key);for(var value:prepared.values.entrySet())edit.putString(value.getKey(),(String)value.getValue());assertTrue(edit.commit());}
    private VideoDbInfo info(String uri){VideoDbInfo info=new VideoDbInfo(Uri.parse(uri));info.id=700;info.lastTimePlayed=1234567;info.resume=888;info.bookmark=999;info.audioTrack=2;info.subtitleTrack=1;info.subtitleDelay=200;info.subtitleRatio=1;info.subtitleLanguage="fr";return info;}
    @Test public void previewIsReadOnlyAndBoundChoicesRemainPendingForBackendEnumeration()throws Exception{
        var refs=references(video(700,PATH));JSONObject payload=incoming(refs);Map<String,?> before=prefs.getAll();var prepared=TitlePlaybackOverrides.prepare(before,payload,nativeChoices(),refs);assertEquals(before,prefs.getAll());assertEquals(1,prepared.unresolved);commit(prepared);JSONObject record=new JSONObject(prefs.getString(TitlePlaybackOverrides.key(700),null));assertTrue(record.getBoolean("pending"));for(String excluded:List.of("PRIVATE_PATH","AUTH_CANARY","played","resume","bookmark"))assertFalse(excluded,record.toString().contains(excluded));assertEquals("AUTH_CANARY",prefs.getString("oauth_token",null));
    }
    @Test public void explicitChoiceClearsOldNativeDefaultsWithoutInventingOrChangingWatchMarkers()throws Exception{
        var refs=references(video(700,PATH));commit(TitlePlaybackOverrides.prepare(prefs.getAll(),incoming(refs),nativeChoices(),refs));VideoDbInfo info=info(PATH);assertTrue(TitlePlaybackOverrides.clearNativeDefaults(app,info));assertEquals(-1,info.audioTrack);assertEquals(-1,info.subtitleTrack);assertEquals(0,info.subtitleDelay);assertEquals(0,info.subtitleRatio);assertNull(info.subtitleLanguage);assertEquals(1234567,info.lastTimePlayed);assertEquals(888,info.resume);assertEquals(999,info.bookmark);assertNull(TitlePlaybackOverrides.validatedChoice(app,info,null));
    }
    @Test public void backendNoneRemapsToReceivingCountAndUnsupportedAudioKeepsChoicePending()throws Exception{
        var refs=references(video(700,PATH));commit(TitlePlaybackOverrides.prepare(prefs.getAll(),incoming(refs),nativeChoices(),refs));VideoMetadata metadata=mock(VideoMetadata.class);when(metadata.getAudioTrackNb()).thenReturn(1);when(metadata.getSubtitleTrackNb()).thenReturn(1);
        com.archos.medialib.MediaMetadata raw=mock(com.archos.medialib.MediaMetadata.class);int key=com.archos.medialib.IMediaPlayer.METADATA_KEY_AUDIO_TRACK+com.archos.medialib.IMediaPlayer.METADATA_KEY_AUDIO_TRACK_SUPPORTED;when(raw.has(key)).thenReturn(true);when(raw.getBoolean(key)).thenReturn(true);VideoMetadata.AudioTrack track=new VideoMetadata.AudioTrack(raw,0);when(metadata.getAudioTrack(0)).thenReturn(track);var restored=TitlePlaybackOverrides.validatedChoice(app,info(PATH),metadata);assertNotNull(restored);assertEquals(1,VideoStore.paramsToSubtitleTrack(restored.params));assertEquals(Boolean.TRUE,restored.subtitleNone);assertEquals(321,restored.delay);assertEquals(2,restored.ratio);when(metadata.getAudioTrackNb()).thenReturn(0);assertNull(TitlePlaybackOverrides.validatedChoice(app,info(PATH),metadata));assertTrue(new JSONObject(prefs.getString(TitlePlaybackOverrides.key(700),null)).getBoolean("pending"));
    }
    @Test public void changedFileGuardNeverAppliesOldStateAndOrphanedIntentRemainsInPublicBackup()throws Exception{
        var refs=references(video(700,PATH));commit(TitlePlaybackOverrides.prepare(prefs.getAll(),incoming(refs),nativeChoices(),refs));VideoDbInfo changed=info("file:///different.mkv");assertFalse(TitlePlaybackOverrides.clearNativeDefaults(app,changed));assertEquals(2,changed.audioTrack);assertNull(TitlePlaybackOverrides.validatedChoice(app,changed,new VideoMetadata()));var current=references(video(700,"file:///different.mkv"));JSONObject exported=TitlePlaybackOverrides.export(app,Map.of(700L,new PortableTitlePlayback.State(null,0,0,null)),current);assertEquals(1,exported.getJSONArray("rows").length());assertEquals(refs.reference(700),exported.getJSONArray("rows").getJSONObject(0).getString("physical"));assertFalse(exported.toString().contains("PRIVATE"));assertFalse(exported.toString().contains("guard"));
    }
    @Test public void selectedEmptyClearsPublicChoicesAndOldPrivateOverridesWhilePreservingCredentials()throws Exception{
        var refs=references(video(700,PATH));commit(TitlePlaybackOverrides.prepare(prefs.getAll(),incoming(refs),nativeChoices(),refs));prefs.edit().putString(TitlePlaybackOverrides.key(800),"Private orphan original").commit();var empty=new JSONObject().put("schema",2).put("rows",new JSONArray());commit(TitlePlaybackOverrides.prepare(prefs.getAll(),empty,nativeChoices(),refs));assertFalse(prefs.contains(TitlePlaybackOverrides.key(800)));assertEquals(0,TitlePlaybackOverrides.export(app,nativeChoices(),refs).getJSONArray("rows").length());assertEquals("AUTH_CANARY",prefs.getString("oauth_token",null));assertTrue(TitlePlaybackOverrides.clearNativeDefaults(app,info(PATH)));
    }
    @Test public void duplicateReceivingFilesAndUnknownReferencesStayPendingAndCorruptPrivateFieldsFailBeforeMutation()throws Exception{
        var source=references(video(700,PATH));JSONObject payload=incoming(source);var duplicate=references(video(700,PATH),video(701,PATH));var prepared=TitlePlaybackOverrides.prepare(prefs.getAll(),payload,Map.of(700L,new PortableTitlePlayback.State(null,0,0,null),701L,new PortableTitlePlayback.State(null,0,0,null)),duplicate);assertEquals(1,prepared.unresolved);assertEquals(1,new JSONObject((String)prepared.values.get(PortableTitlePlayback.PENDING)).getJSONArray("rows").length());commit(TitlePlaybackOverrides.prepare(prefs.getAll(),payload,nativeChoices(),source));JSONObject record=new JSONObject(prefs.getString(TitlePlaybackOverrides.key(700),null));record.put("credentials","AUTH_CANARY");prefs.edit().putString(TitlePlaybackOverrides.key(700),record.toString()).commit();VideoDbInfo info=info(PATH);try{TitlePlaybackOverrides.clearNativeDefaults(app,info);fail();}catch(JSONException expected){}assertEquals(2,info.audioTrack);try{TitlePlaybackOverrides.export(app,nativeChoices(),source);fail();}catch(JSONException expected){}
    }
}
