package com.archos.mediacenter.video.utils;

import android.app.Application;
import android.content.*;
import android.net.Uri;
import androidx.preference.PreferenceManager;
import com.archos.mediacenter.video.browser.adapters.object.Video;
import com.archos.mediacenter.video.utils.backup.*;
import com.archos.mediacenter.video.utils.backup.PortableTitlePlayback.State;
import com.archos.mediaprovider.video.VideoOpenHelper;
import com.archos.mediaprovider.video.VideoStore;
import java.util.*;
import org.json.*;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.*;
import org.robolectric.annotation.Config;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith(RobolectricTestRunner.class) @Config(application=Application.class,sdk=28)
public class PortableTitlePlaybackTest {
    private Context scope(String prefix){return new ContextWrapper(RuntimeEnvironment.getApplication()){@Override public SharedPreferences getSharedPreferences(String name,int mode){return super.getSharedPreferences(prefix+"-"+name,mode);}@Override public Context getApplicationContext(){return this;}};}
    private Video video(long id,String path){Video video=mock(Video.class);when(video.getId()).thenReturn(id);when(video.getFileUri()).thenReturn(Uri.parse(path));return video;}
    private JSONObject payload(Context source)throws Exception {
        return PortableTitlePlayback.export(source,Map.of(10L,new State(VideoStore.paramsFromTracks(3,2),-1500,1,"en")),new PortablePhysicalReferences(source,List.of(video(10,"file:///media/FILE_CANARY.mkv"))));
    }
    @Test public void sourceChoiceRemapsOnlyAfterBothMediaMatchingAndReceivingTrackValidation()throws Exception {
        Context source=scope("source"),target=scope("target");JSONObject payload=payload(source);assertFalse(payload.toString().contains("CANARY"));assertEquals(6,payload.getJSONArray("rows").getJSONObject(0).length());assertEquals(2,payload.getInt("schema"));assertFalse(payload.getJSONArray("rows").getJSONObject(0).getBoolean("subtitleNone"));
        var refs=new PortablePhysicalReferences(target,List.of(video(700,"file:///media/FILE_CANARY.mkv")));Map<Long,State> before=Map.of(700L,new State(1,500,2,"fr"));
        assertEquals(1,PortableTitlePlayback.prepare(payload,before,refs,false,(id,state)->true).unresolved);
        assertEquals(1,PortableTitlePlayback.prepare(payload,before,refs,true,null).unresolved);
        assertEquals(1,PortableTitlePlayback.prepare(payload,before,refs,true,(id,state)->false).unresolved);
        var prepared=PortableTitlePlayback.prepare(payload,before,refs,true,(id,state)->id==700&&VideoStore.paramsToAudioTrack(state.params)==3&&VideoStore.paramsToSubtitleTrack(state.params)==2);
        assertEquals(0,prepared.unresolved);assertEquals(-1500,prepared.choices.get(700L).delay);assertEquals(1,prepared.choices.get(700L).ratio);assertEquals("en",prepared.choices.get(700L).language);assertEquals(500,before.get(700L).delay);
    }
    @Test public void changedPathAndUnavailableTracksStayPendingAndPresentEmptyClearsOnlyOwnedChoices()throws Exception {
        Context source=scope("source"),target=scope("target");var refs=new PortablePhysicalReferences(target,List.of(video(700,"file:///new-media/FILE_CANARY.mkv")));var before=Map.of(700L,new State(1,500,2,"fr"));JSONObject payload=payload(source);
        var prepared=PortableTitlePlayback.prepare(payload,before,refs,true,(id,state)->true);assertEquals(1,prepared.unresolved);
        PreferenceManager.getDefaultSharedPreferences(target).edit().putString(PortableTitlePlayback.PENDING,prepared.pending).commit();assertEquals(payload.toString(),PortableTitlePlayback.export(target,Map.of(),refs).toString());
        var empty=PortableTitlePlayback.prepare(new JSONObject().put("schema",1).put("rows",new JSONArray()),before,refs,false,null);State cleared=empty.choices.get(700L);assertNull(cleared.params);assertNull(cleared.language);assertEquals(0,cleared.delay);assertEquals(0,cleared.ratio);assertEquals(500,before.get(700L).delay);
    }
    @Test public void secretsPathsHistoryFieldsUnsupportedTrackBitsAndInvalidChoicesRejectBeforePreparation()throws Exception {
        JSONObject payload=payload(scope("source"));
        for(String field:List.of("credentials","filename","subtitleBytes","played","position","completed"))try {
            JSONObject bad=new JSONObject(payload.toString());bad.getJSONArray("rows").getJSONObject(0).put(field,"CANARY");PortableTitlePlayback.validate(bad);fail();
        }catch(JSONException expected){}
        for(var change:Map.of("params",0x100000,"delay",2147483648L,"ratio",3,"language","AUTH_CANARY").entrySet())try {
            JSONObject bad=new JSONObject(payload.toString());bad.getJSONArray("rows").getJSONObject(0).put(change.getKey(),change.getValue());PortableTitlePlayback.validate(bad);fail();
        }catch(JSONException expected){}
        JSONObject bad=new JSONObject(payload.toString());bad.getJSONArray("rows").getJSONObject(0).put("delay",1.5);try{PortableTitlePlayback.validate(bad);fail();}catch(JSONException expected){}
    }
    @org.robolectric.annotation.SQLiteMode(org.robolectric.annotation.SQLiteMode.Mode.NATIVE)
    @Test public void nativeReaderKeepsSignedDelaysAndExplicitZeroTracksWithoutExportingPlaybackTimestamps()throws Exception {
        Context context=RuntimeEnvironment.getApplication();try(VideoOpenHelper helper=new VideoOpenHelper(context)) {
            var db=helper.getWritableDatabase();db.execSQL("INSERT INTO files(_id,media_type,Archos_playerParams,Archos_playerSubtitleDelay,Archos_playerSubtitleRatio,Archos_subtitleLanguage,Archos_lastTimePlayed,_data) VALUES(10,3,3075,-1500,1,'en',1789890000000,'FILE_CANARY'),(11,3,0,0,0,NULL,0,'UNPLAYED_CANARY'),(12,3,0,0,0,NULL,1789890000000,'ZERO_TRACK_CANARY')");
            var state=PortableTitlePlayback.read(db);assertEquals(-1500,state.get(10L).delay);assertEquals(Integer.valueOf(3075),state.get(10L).params);assertNull(state.get(11L).params);assertEquals(Integer.valueOf(0),state.get(12L).params);
            JSONObject payload=PortableTitlePlayback.export(context,state,new PortablePhysicalReferences(context,List.of()));assertEquals(2,payload.getJSONArray("rows").length());assertFalse(payload.toString().contains("CANARY"));assertFalse(payload.toString().contains("1789890000000"));assertFalse(payload.toString().contains("played"));
            db.execSQL("UPDATE files SET Archos_subtitleLanguage='AUTH_CANARY' WHERE _id=10");try{PortableTitlePlayback.read(db);fail();}catch(java.io.IOException expected){}
        }
    }
    @org.robolectric.annotation.SQLiteMode(org.robolectric.annotation.SQLiteMode.Mode.NATIVE)
    @Test public void nativeNoneSentinelIsExportedAsIntentRatherThanAnAmbiguousReceivingIndex()throws Exception{
        Context context=RuntimeEnvironment.getApplication();context.deleteDatabase("media.db");try(VideoOpenHelper helper=new VideoOpenHelper(context)){var db=helper.getWritableDatabase();String count=VideoStore.Video.VideoColumns.ARCHOS_NUMBER_OF_SUBTITLE_TRACKS;db.execSQL("INSERT INTO files(_id,media_type,Archos_playerParams,Archos_lastTimePlayed,"+count+") VALUES(10,3,6144,100,5),(11,3,2048,100,5),(12,3,6144,100,1)");var values=PortableTitlePlayback.read(db);assertEquals(Boolean.TRUE,values.get(10L).subtitleNone);assertEquals(Boolean.FALSE,values.get(11L).subtitleNone);assertNull(values.get(12L).subtitleNone);JSONObject exported=PortableTitlePlayback.export(context,values,new PortablePhysicalReferences(context,List.of()));assertTrue(exported.getJSONArray("rows").getJSONObject(0).getBoolean("subtitleNone"));assertFalse(exported.toString().contains(count));assertFalse(exported.toString().contains("played"));}
    }
    @Test public void legacyPayloadConvertsUnknownSubtitleModeWithoutInventingNoneAndRejectsContradictoryIntent()throws Exception{
        Context source=scope("legacy"),target=scope("legacy-target");JSONObject old=payload(source);old.put("schema",1);old.getJSONArray("rows").getJSONObject(0).remove("subtitleNone");PortableTitlePlayback.validate(old);var refs=new PortablePhysicalReferences(target,List.of(video(700,"file:///different.mkv")));var prepared=PortableTitlePlayback.prepare(old,Map.of(700L,new State(null,0,0,null)),refs,false,null);JSONObject pending=new JSONObject(prepared.pending);assertEquals(2,pending.getInt("schema"));assertFalse(pending.getJSONArray("rows").getJSONObject(0).getBoolean("subtitleNone"));PortableTitlePlayback.validate(pending);
        JSONObject bad=payload(source);bad.getJSONArray("rows").getJSONObject(0).put("subtitleNone",true);try{PortableTitlePlayback.validate(bad);fail();}catch(JSONException expected){}bad=payload(source);bad.getJSONArray("rows").getJSONObject(0).put("subtitleNone","false");try{PortableTitlePlayback.validate(bad);fail();}catch(JSONException expected){}
    }
}
