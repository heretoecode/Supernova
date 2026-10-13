package com.archos.mediacenter.video.utils;

import android.app.Application;
import android.content.*;
import android.net.Uri;
import androidx.preference.PreferenceManager;
import com.archos.mediacenter.video.browser.adapters.object.Video;
import com.archos.mediacenter.video.utils.backup.*;
import com.archos.mediaprovider.video.VideoOpenHelper;
import java.util.*;
import org.json.*;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.*;
import org.robolectric.annotation.Config;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith(RobolectricTestRunner.class) @Config(application=Application.class,sdk=28)
public class PortableHiddenItemsTest {
    private Context scope(String prefix){return new ContextWrapper(RuntimeEnvironment.getApplication()){@Override public SharedPreferences getSharedPreferences(String name,int mode){return super.getSharedPreferences(prefix+"-"+name,mode);}@Override public Context getApplicationContext(){return this;}};}
    private Video video(long id,String path){Video video=mock(Video.class);when(video.getId()).thenReturn(id);when(video.getFileUri()).thenReturn(Uri.parse(path));return video;}
    @Test public void exactPhysicalMappingRequiresMatchingAndEmptySelectionClearsOnlyHiddenState()throws Exception {
        Context source=scope("source"),target=scope("target");var originals=new PortablePhysicalReferences(source,List.of(video(10,"file:///media/FILE_CANARY.mkv")));
        JSONObject payload=PortableHiddenItems.export(source,Map.of(10L,true,11L,false),originals);assertFalse(payload.toString().contains("CANARY"));assertEquals(1,payload.getJSONArray("rows").length());
        Map<Long,Boolean> before=Map.of(700L,false,800L,true);var receiving=new PortablePhysicalReferences(target,List.of(video(700,"file:///media/FILE_CANARY.mkv")));
        assertEquals(1,PortableHiddenItems.prepare(payload,before,receiving,false).unresolved);
        var prepared=PortableHiddenItems.prepare(payload,before,receiving,true);assertEquals(0,prepared.unresolved);assertTrue(prepared.hidden.get(700L));assertFalse(prepared.hidden.get(800L));assertTrue(before.get(800L));
        var empty=PortableHiddenItems.prepare(new JSONObject().put("schema",1).put("rows",new JSONArray()),before,receiving,true);assertEquals(Set.of(false),new HashSet<>(empty.hidden.values()));
    }
    @Test public void newPathsMissingRecordsAndAmbiguousFilesStayPendingAcrossExport()throws Exception {
        Context source=scope("source"),target=scope("target");var refs=new PortablePhysicalReferences(source,List.of(video(10,"file:///media/fixture.mkv"),video(11,"file:///media/fixture.mkv")));
        JSONObject payload=PortableHiddenItems.export(source,Map.of(10L,true,11L,true,99L,true),refs);assertEquals(3,payload.getJSONArray("rows").length());
        var receiving=new PortablePhysicalReferences(target,List.of(video(700,"file:///new-media/fixture.mkv")));var prepared=PortableHiddenItems.prepare(payload,Map.of(700L,false),receiving,true);assertEquals(3,prepared.unresolved);assertFalse(prepared.hidden.get(700L));
        PreferenceManager.getDefaultSharedPreferences(target).edit().putString(PortableHiddenItems.PENDING,prepared.pending).commit();assertEquals(3,PortableHiddenItems.export(target,Map.of(),receiving).getJSONArray("rows").length());
    }
    @Test public void addedCredentialMediaAndLocationFieldsOrDuplicateReferencesAreRejected()throws Exception {
        JSONObject row=new JSONObject().put("physical","file:"+"a".repeat(64));
        for(String field:List.of("credentials","filename","artwork","hidden"))try{PortableHiddenItems.validate(new JSONObject().put("schema",1).put("rows",new JSONArray().put(new JSONObject(row.toString()).put(field,"CANARY"))));fail();}catch(JSONException expected){}
        try{PortableHiddenItems.validate(new JSONObject().put("schema",1).put("rows",new JSONArray().put(row).put(row)));fail();}catch(JSONException expected){}
    }
    @org.robolectric.annotation.SQLiteMode(org.robolectric.annotation.SQLiteMode.Mode.NATIVE)
    @Test public void actualDatabaseReaderIncludesHiddenVideoRowsAndExcludesAudioWithoutReadingPaths()throws Exception {
        Context context=RuntimeEnvironment.getApplication();try(VideoOpenHelper helper=new VideoOpenHelper(context)) {
            var db=helper.getWritableDatabase();db.execSQL("INSERT INTO files(_id,media_type,Archos_hiddenByUser,_data) VALUES(10,3,1,'FILE_CANARY'),(11,3,0,'UNHIDDEN_CANARY'),(12,2,1,'AUDIO_CANARY'),(13,3,NULL,'NULL_CANARY')");
            var state=PortableHiddenItems.read(db);assertEquals(Map.of(10L,true,11L,false,13L,false),state);
            db.execSQL("UPDATE files SET Archos_hiddenByUser=2 WHERE _id=10");try{PortableHiddenItems.read(db);fail();}catch(java.io.IOException expected){}
            db.execSQL("UPDATE files SET Archos_hiddenByUser=1.5 WHERE _id=10");try{PortableHiddenItems.read(db);fail();}catch(java.io.IOException expected){}
        }
    }
}
