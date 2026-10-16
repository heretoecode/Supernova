package com.archos.mediacenter.video.utils;

import android.app.Application;
import android.content.Context;
import com.archos.mediaprovider.video.VideoOpenHelper;
import com.archos.mediacenter.video.utils.backup.*;
import java.io.*;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import org.json.*;
import org.junit.*;
import org.junit.runner.RunWith;
import org.robolectric.*;
import org.robolectric.annotation.*;
import static org.junit.Assert.*;

@RunWith(RobolectricTestRunner.class) @Config(application=Application.class,sdk=28) @SQLiteMode(SQLiteMode.Mode.NATIVE)
public class PlaylistStateRecoveryTest {
    private Context app;
    @Before public void setup(){app=RuntimeEnvironment.getApplication();app.deleteDatabase("media.db");try(VideoOpenHelper helper=new VideoOpenHelper(app)){var db=helper.getWritableDatabase();db.execSQL("INSERT INTO list_table(_id,title,trakt_id,sync_status) VALUES(1,'Connected list',987654,NULL),(7,'password=PRIVATE_ORIGINAL',NULL,NULL)");db.execSQL("INSERT INTO video_list_table(_id,list_id,m_online_id,e_online_id,sync_status) VALUES(11,1,123,NULL,NULL),(12,7,NULL,456,1)");db.execSQL("INSERT INTO files(_id,media_type,_data,bookmark,Archos_hiddenByUser,title) VALUES(50,3,'file:///PRIVATE_FILE.mkv',3333,1,'PRIVATE_TITLE')");}}
    private JSONObject payload()throws Exception{return new JSONObject().put("schema",1).put("rows",new JSONArray().put(new JSONObject().put("id","playlist:12345678-1234-1234-1234-123456789012:1").put("name","Restored").put("members",new JSONArray().put("tmdb:movie:321").put("tmdb:episode:654"))));}
    private JSONObject empty()throws Exception{return new JSONObject().put("schema",1).put("rows",new JSONArray());}
    @Test public void selectedReplacementPreservesReceivingAccountOwnedDefinitionsAndEveryUnownedMediaColumn()throws Exception{
        try(VideoOpenHelper helper=new VideoOpenHelper(app)){
            var db=helper.getWritableDatabase();JSONObject before=PlaylistStateRecovery.capture(db);var prepared=PlaylistStateRecovery.prepare(before,payload(),true);assertEquals(0,prepared.unresolved);assertEquals("playlist:12345678-1234-1234-1234-123456789012:1",prepared.references.get(8L));
            db.beginTransaction();try{PlaylistStateRecovery.write(db,prepared.after,()->false);db.setTransactionSuccessful();}finally{db.endTransaction();}
            assertEquals(prepared.after.toString(),PlaylistStateRecovery.capture(db).toString());JSONObject remote=prepared.after.getJSONArray("definitions").getJSONObject(0);assertEquals(987654,remote.getLong("trakt"));assertTrue(remote.isNull("status"));JSONObject restored=prepared.after.getJSONArray("definitions").getJSONObject(1);assertTrue(restored.isNull("trakt"));assertEquals(4,restored.getInt("status"));
            try(var row=db.rawQuery("SELECT bookmark,Archos_hiddenByUser,title,_data FROM files WHERE _id=50",null)){assertTrue(row.moveToFirst());assertEquals(3333,row.getInt(0));assertEquals(1,row.getInt(1));assertEquals("PRIVATE_TITLE",row.getString(2));assertEquals("file:///PRIVATE_FILE.mkv",row.getString(3));}
        }
    }
    @Test public void presentEmptyClearsOnlyLocalDefinitionsAndKeepsReceivingTraktMembership()throws Exception{
        try(VideoOpenHelper helper=new VideoOpenHelper(app)){var db=helper.getWritableDatabase();JSONObject before=PlaylistStateRecovery.capture(db);var prepared=PlaylistStateRecovery.prepare(before,empty(),true);db.beginTransaction();try{PlaylistStateRecovery.write(db,prepared.after,()->false);db.setTransactionSuccessful();}finally{db.endTransaction();}assertEquals(1,PlaylistStateRecovery.capture(db).getJSONArray("definitions").length());assertEquals(11,PlaylistStateRecovery.capture(db).getJSONArray("members").getJSONObject(0).getLong("id"));assertEquals(7,PlaylistStateRecovery.capture(db).getLong("definitionSequence"));}
    }
    @Test public void rollbackRestoresPrivateNullableOriginalsAndBothSequenceCountersExactly()throws Exception{
        try(VideoOpenHelper helper=new VideoOpenHelper(app)){var db=helper.getWritableDatabase();JSONObject before=PlaylistStateRecovery.capture(db),after=PlaylistStateRecovery.prepare(before,payload(),true).after;db.beginTransaction();try{PlaylistStateRecovery.write(db,after,()->false);}finally{db.endTransaction();}assertEquals(before.toString(),PlaylistStateRecovery.capture(db).toString());db.beginTransaction();try{PlaylistStateRecovery.write(db,after,()->false);PlaylistStateRecovery.write(db,before,()->false);db.setTransactionSuccessful();}finally{db.endTransaction();}assertEquals(before.toString(),PlaylistStateRecovery.capture(db).toString());}
    }
    @Test public void cancellationAfterDestructiveStagingRollsBackNativeOriginals()throws Exception{
        try(VideoOpenHelper helper=new VideoOpenHelper(app)){var db=helper.getWritableDatabase();JSONObject before=PlaylistStateRecovery.capture(db),after=PlaylistStateRecovery.prepare(before,payload(),true).after;AtomicInteger checks=new AtomicInteger();db.beginTransaction();try{try{PlaylistStateRecovery.write(db,after,()->checks.incrementAndGet()>2);fail();}catch(InterruptedIOException expected){}}finally{db.endTransaction();}assertEquals(before.toString(),PlaylistStateRecovery.capture(db).toString());}
    }
    @Test public void newDeviceKeepsWholeLogicalDefinitionsPendingUntilNativeMediaStoreExists()throws Exception{
        app.deleteDatabase("media.db");var prepared=PlaylistStateRecovery.prepare(PlaylistStateRecovery.empty(),payload(),false);assertEquals(1,prepared.unresolved);assertEquals(payload().toString(),prepared.pending);assertTrue(prepared.references.isEmpty());assertFalse(VideoOpenHelper.getDatabaseFile(app).exists());assertEquals(PlaylistStateRecovery.empty().toString(),prepared.after.toString());
    }
    @Test public void privatePairRejectsAccountRebindingAndUnknownColumnsBeforeAnyWrite()throws Exception{
        try(VideoOpenHelper helper=new VideoOpenHelper(app)){var db=helper.getWritableDatabase();JSONObject before=PlaylistStateRecovery.capture(db),after=PlaylistStateRecovery.prepare(before,payload(),true).after;after.getJSONArray("definitions").getJSONObject(0).put("trakt",999);try{PlaylistStateRecovery.validatePair(before,after);fail();}catch(JSONException expected){}assertEquals(before.toString(),PlaylistStateRecovery.capture(db).toString());db.execSQL("ALTER TABLE list_table ADD COLUMN unowned_future TEXT");try{PlaylistStateRecovery.capture(db);fail();}catch(IOException expected){}try{PlaylistStateRecovery.write(db,before,()->false);fail();}catch(IOException expected){}try(var row=db.rawQuery("SELECT title FROM list_table WHERE _id=7",null)){assertTrue(row.moveToFirst());assertEquals("password=PRIVATE_ORIGINAL",row.getString(0));}}
    }
    @Test public void exhaustedReceivingIdsAndUnprotectedWritesFailWithoutReplacingAnything()throws Exception{
        try(VideoOpenHelper helper=new VideoOpenHelper(app)){var db=helper.getWritableDatabase();JSONObject before=PlaylistStateRecovery.capture(db);try{PlaylistStateRecovery.write(db,before,()->false);fail();}catch(IOException expected){}JSONObject exhausted=new JSONObject(before.toString()).put("definitionSequence",Integer.MAX_VALUE);try{PlaylistStateRecovery.prepare(exhausted,payload(),true);fail();}catch(JSONException expected){}assertEquals(before.toString(),PlaylistStateRecovery.capture(db).toString());}
    }
}
