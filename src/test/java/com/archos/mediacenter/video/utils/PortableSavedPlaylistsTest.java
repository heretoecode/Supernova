package com.archos.mediacenter.video.utils;

import android.app.Application;
import android.content.Context;
import androidx.preference.PreferenceManager;
import com.archos.mediacenter.video.utils.backup.*;
import com.archos.mediaprovider.video.*;
import java.io.IOException;
import java.util.*;
import org.json.*;
import org.junit.*;
import org.junit.runner.RunWith;
import org.robolectric.*;
import org.robolectric.annotation.*;
import static org.junit.Assert.*;

@RunWith(RobolectricTestRunner.class) @Config(application=Application.class,sdk=28) @SQLiteMode(SQLiteMode.Mode.NATIVE)
public class PortableSavedPlaylistsTest {
    private Context app;
    @Before public void setup(){app=RuntimeEnvironment.getApplication();app.deleteDatabase("media.db");PreferenceManager.getDefaultSharedPreferences(app).edit().clear().putString("trakt_access_token","AUTH_CANARY").commit();}
    private JSONObject example()throws Exception{return new JSONObject().put("schema",1).put("rows",new JSONArray().put(new JSONObject().put("id","playlist:12345678-1234-1234-1234-123456789012:1").put("name","Family").put("members",new JSONArray().put("tmdb:movie:123").put("tmdb:episode:456"))));}
    @Test public void nativeSourcePreservesIndependentDefinitionsAndProviderMembershipWithoutAnyAvailableMedia()throws Exception{
        try(VideoOpenHelper helper=new VideoOpenHelper(app)){
            var db=helper.getWritableDatabase();db.execSQL("INSERT INTO list_table(_id,title,trakt_id,sync_status) VALUES(1,'Remote definition',99887766,0),(2,'Local definition',NULL,1),(3,'Deleted definition',11223344,2),(4,'Renamed definition',12345678,3)");
            db.execSQL("INSERT INTO video_list_table(list_id,m_online_id,e_online_id,sync_status) VALUES(1,123,-1,0),(1,-1,456,1),(1,789,-1,2),(2,321,-1,0),(3,987,-1,0)");
            var inventory=PortableSavedPlaylists.read(db);assertEquals(Set.of(1L,2L,4L),inventory.keySet());assertEquals(Set.of("tmdb:movie:123","tmdb:episode:456"),inventory.get(1L).members);assertEquals(Set.of("tmdb:movie:321"),inventory.get(2L).members);assertTrue(inventory.get(4L).members.isEmpty());
            var payload=PortableSavedPlaylists.export(app,inventory);PortableSavedPlaylists.validate(payload);String text=payload.toString();for(String excluded:List.of("99887766","11223344","12345678","AUTH_CANARY","trakt_id","sync_status","Deleted definition"))assertFalse(excluded,text.contains(excluded));assertEquals(3,payload.getJSONArray("rows").length());
            try(var rows=db.rawQuery("SELECT COUNT(*) FROM files",null)){assertTrue(rows.moveToFirst());assertEquals(0,rows.getLong(0));}
            try(var rows=db.rawQuery("SELECT trakt_id,sync_status FROM list_table WHERE _id=1",null)){assertTrue(rows.moveToFirst());assertEquals(99887766,rows.getLong(0));assertEquals(0,rows.getInt(1));}
        }
    }
    @Test public void nativeNullNamesAndDuplicateHistoricalLogicalMembershipArePreservedAsOneSet()throws Exception{
        try(VideoOpenHelper helper=new VideoOpenHelper(app)){var db=helper.getWritableDatabase();db.execSQL("INSERT INTO list_table(_id,title,trakt_id,sync_status) VALUES(1,NULL,NULL,NULL)");db.execSQL("INSERT INTO video_list_table(list_id,m_online_id,e_online_id,sync_status) VALUES(1,123,NULL,NULL),(1,123,-1,0)");var source=PortableSavedPlaylists.read(db);assertNull(source.get(1L).name);assertEquals(Set.of("tmdb:movie:123"),source.get(1L).members);JSONObject payload=PortableSavedPlaylists.export(app,source);assertTrue(payload.getJSONArray("rows").getJSONObject(0).isNull("name"));}
    }
    @Test public void pendingDefinitionsRetainOpaqueIdentityAndCannotOverrideCurrentSourceIntent()throws Exception{
        JSONObject saved=example();PreferenceManager.getDefaultSharedPreferences(app).edit().putString(PortableSavedPlaylists.PENDING,saved.toString()).commit();JSONObject exported=PortableSavedPlaylists.export(app,Map.of());assertEquals(saved.toString(),exported.toString());assertEquals("AUTH_CANARY",PreferenceManager.getDefaultSharedPreferences(app).getString("trakt_access_token",null));
        JSONObject current=PortableSavedPlaylists.export(app,Map.of(1L,new PortableSavedPlaylists.Definition(1,"Current",List.of("tmdb:movie:999"))));JSONObject conflict=new JSONObject(current.toString());conflict.getJSONArray("rows").getJSONObject(0).put("name","Old pending name");PreferenceManager.getDefaultSharedPreferences(app).edit().putString(PortableSavedPlaylists.PENDING,conflict.toString()).commit();assertEquals(current.toString(),PortableSavedPlaylists.export(app,Map.of(1L,new PortableSavedPlaylists.Definition(1,"Current",List.of("tmdb:movie:999")))).toString());
    }
    @Test public void unknownDefinitionAndMembershipStatusesIncludingWideIntegerOverflowFail()throws Exception{
        for(String column:List.of("definition","member"))for(long value:List.of(4L,-1L,4294967296L)){
            app.deleteDatabase("media.db");try(VideoOpenHelper helper=new VideoOpenHelper(app)){var db=helper.getWritableDatabase();db.execSQL("INSERT INTO list_table(_id,title,sync_status) VALUES(1,'Fixture',?)",new Object[]{column.equals("definition")?value:0});db.execSQL("INSERT INTO video_list_table(list_id,m_online_id,e_online_id,sync_status) VALUES(1,123,-1,?)",new Object[]{column.equals("member")?value:0});try{PortableSavedPlaylists.read(db);fail();}catch(IOException expected){}}
        }
    }
    @Test public void ambiguousOrUnusableProviderMembershipFailsWithoutChangingSource()throws Exception{
        for(long[] values:List.of(new long[]{123,456},new long[]{-1,-1},new long[]{2147483648L,-1})){
            app.deleteDatabase("media.db");try(VideoOpenHelper helper=new VideoOpenHelper(app)){var db=helper.getWritableDatabase();db.execSQL("INSERT INTO list_table(_id,title,sync_status) VALUES(1,'Fixture',0)");db.execSQL("INSERT INTO video_list_table(list_id,m_online_id,e_online_id,sync_status) VALUES(1,?,?,0)",new Object[]{values[0],values[1]});try{PortableSavedPlaylists.read(db);fail();}catch(IOException expected){}try(var row=db.rawQuery("SELECT m_online_id,e_online_id FROM video_list_table",null)){assertTrue(row.moveToFirst());assertEquals(values[0],row.getLong(0));assertEquals(values[1],row.getLong(1));}}
        }
    }
    @Test public void publicSchemaRejectsExtraFieldsAccountMappingsDuplicateReferencesAndFilenameMembers()throws Exception{
        for(String field:List.of("trakt_id","credential","file")){JSONObject invalid=example();invalid.getJSONArray("rows").getJSONObject(0).put(field,"PRIVATE_CANARY");try{PortableSavedPlaylists.validate(invalid);fail();}catch(JSONException expected){}}
        for(String value:List.of("file:///Movies/film.mkv","tmdb:tv:123:1:2","tmdb:episode:0","tmdb:movie:2147483648")){JSONObject invalid=example();invalid.getJSONArray("rows").getJSONObject(0).put("members",new JSONArray().put(value));try{PortableSavedPlaylists.validate(invalid);fail();}catch(JSONException expected){}}
        JSONObject invalid=example();invalid.getJSONArray("rows").put(invalid.getJSONArray("rows").getJSONObject(0));try{PortableSavedPlaylists.validate(invalid);fail();}catch(JSONException expected){}
        invalid=example();invalid.getJSONArray("rows").getJSONObject(0).getJSONArray("members").put("tmdb:movie:123");try{PortableSavedPlaylists.validate(invalid);fail();}catch(JSONException expected){}
    }
    @Test public void credentialLabelsRejectWithoutEchoAndCodecStaysUnregisteredUntilSyncRecoveryIntegration()throws Exception{
        for(String name:List.of("password=PRIVATE_CANARY","smb://user:PRIVATE_CANARY@nas/share",new String(new char[]{'x','\ud800'}))){try{new PortableSavedPlaylists.Definition(1,name,List.of());fail();}catch(JSONException expected){assertFalse(expected.getMessage().contains("PRIVATE_CANARY"));}}
        assertFalse(LogicalBackupArchive.registered().contains(BackupCategory.PLAYLISTS));
    }
}
