package com.archos.mediacenter.video.utils;

import android.app.Application;
import com.archos.mediacenter.video.leanback.ViewingHistory;
import com.archos.mediacenter.video.utils.backup.*;
import com.archos.mediaprovider.video.VideoOpenHelper;
import java.io.*;
import java.nio.file.Files;
import java.util.*;
import org.json.*;
import org.junit.*;
import org.junit.runner.RunWith;
import org.robolectric.*;
import org.robolectric.annotation.*;
import static org.junit.Assert.*;

@RunWith(RobolectricTestRunner.class) @Config(application=Application.class,sdk=28) @SQLiteMode(SQLiteMode.Mode.NATIVE)
public class CombinedPlaylistJournalTest {
    private static class ProcessDeath extends Error {}
    private final CombinedViewingRestoreTest fixture=new CombinedViewingRestoreTest();
    private static final String ID="playlist:12345678-1234-1234-1234-123456789012:1";
    @Before public void setup()throws Exception {
        fixture.setup();var app=fixture.app;app.deleteDatabase("media.db");app.deleteDatabase(BookmarkStoreRecovery.DATABASE);
        try(VideoOpenHelper helper=new VideoOpenHelper(app)){var db=helper.getWritableDatabase();
            db.execSQL("INSERT INTO files(_id,remote_id,media_type,Archos_hiddenByUser,_data,bookmark) VALUES(10,10,3,1,'file:///media/PATH_CANARY.mkv',4444)");
            db.execSQL("INSERT INTO MOVIE(_id,video_id,m_online_id,Nova_pinned) VALUES(100,10,123,9)");
            db.execSQL("INSERT INTO list_table(_id,title,trakt_id,sync_status) VALUES(7,'Original local',NULL,1),(9,'Receiving remote',987654,0)");
            db.execSQL("INSERT INTO video_list_table(_id,list_id,m_online_id,e_online_id,sync_status) VALUES(12,7,123,-1,1),(13,9,-1,456,0),(14,NULL,NULL,NULL,NULL)");
        }
        try(var db=android.database.sqlite.SQLiteDatabase.openOrCreateDatabase(app.getDatabasePath(BookmarkStoreRecovery.DATABASE),null)){
            db.execSQL("CREATE TABLE shortcuts(_id INTEGER PRIMARY KEY AUTOINCREMENT,uri TEXT NOT NULL,shortcut_name TEXT NOT NULL,friendly_uri TEXT)");db.setVersion(2);
            db.execSQL("INSERT INTO shortcuts(uri,shortcut_name,friendly_uri) VALUES('smb://nas.example/Media','Original bookmark',NULL)");
        }
        fixture.prefs.edit().putString(PortableSavedPlaylists.referenceKey(7),"playlist:12345678-1234-1234-1234-123456789012:7").putString(PortableSavedPlaylists.referenceKey(9),"playlist:12345678-1234-1234-1234-123456789012:9").commit();
    }
    private JSONObject payload()throws Exception{return new JSONObject().put("schema",1).put("rows",new JSONArray().put(new JSONObject().put("id",ID).put("name","Restored local").put("members",new JSONArray().put("tmdb:movie:321").put("tmdb:episode:654"))));}
    private Map<BackupCategory,JSONObject> all()throws Exception{
        var result=fixture.incoming(true);result.put(BackupCategory.PLAYLISTS,payload());
        result.put(BackupCategory.PINNED,new JSONObject().put("schema",1).put("rows",new JSONArray()));
        result.put(BackupCategory.HIDDEN,new JSONObject().put("schema",1).put("rows",new JSONArray()));
        result.put(BackupCategory.BOOKMARKS,new JSONObject().put("schema",1).put("rows",new JSONArray().put(new JSONObject().put("id","bookmark:12345678-1234-1234-1234-123456789012:1").put("location",PortableLocations.reference("smb://nas.example/Media")).put("name","Restored bookmark"))));return result;
    }
    private String media()throws Exception{return MediaStateRecovery.capture(fixture.app,true,true,true).toString();}
    private JSONObject export()throws Exception{try(VideoOpenHelper helper=new VideoOpenHelper(fixture.app)){return PortableSavedPlaylists.export(fixture.app,PortableSavedPlaylists.read(helper.getReadableDatabase()));}}
    private void assertNew()throws Exception{
        assertEquals(19,fixture.prefs.getInt("preview_accent41",0));assertTrue(fixture.health.getBoolean("incorrect:10",false));assertTrue(ViewingHistory.read(fixture.app).get("tmdb:movie:456").completed);
        JSONObject state=MediaStateRecovery.capture(fixture.app,true,true,true);assertEquals(0,state.getJSONArray("pins").getJSONObject(0).getLong("value"));assertFalse(state.getJSONArray("hidden").getJSONObject(0).getBoolean("value"));
        JSONArray rows=state.getJSONObject("playlists").getJSONArray("definitions");assertEquals(2,rows.length());assertEquals(987654,rows.getJSONObject(0).getLong("trakt"));assertEquals("Restored local",rows.getJSONObject(1).getString("title"));assertEquals(4,rows.getJSONObject(1).getInt("status"));
        assertFalse(fixture.prefs.contains(PortableSavedPlaylists.referenceKey(7)));assertEquals(ID,fixture.prefs.getString(PortableSavedPlaylists.referenceKey(10),null));
        assertEquals("playlist:12345678-1234-1234-1234-123456789012:9",fixture.prefs.getString(PortableSavedPlaylists.referenceKey(9),null));
        assertEquals("Restored bookmark",BookmarkStoreRecovery.capture(fixture.app).getJSONArray("rows").getJSONObject(0).getString("name"));assertEquals("AUTH_CANARY",fixture.prefs.getString("oauth_token",null));
        JSONArray publicRows=export().getJSONArray("rows");assertEquals(2,publicRows.length());assertEquals(ID,publicRows.getJSONObject(1).getString("id"));assertEquals(2,publicRows.getJSONObject(1).getJSONArray("members").length());
        assertFalse(export().toString().contains("987654"));assertFalse(export().toString().contains("CANARY"));
    }
    @Test public void unfinishedFiveStoreBoundariesRollBackListsAliasesAndAllOtherSelectedState()throws Exception {
        String before=media(),viewing=ViewingHistory.captureRecovery(fixture.app).toString(),bookmarks=BookmarkStoreRecovery.capture(fixture.app).toString();Map<String,?> prefs=fixture.prefs.getAll();
        for(var boundary:List.of(PreferenceRestoreTransaction.Checkpoint.JOURNAL_DURABLE,PreferenceRestoreTransaction.Checkpoint.DEFAULT_DURABLE,PreferenceRestoreTransaction.Checkpoint.HEALTH_DURABLE,PreferenceRestoreTransaction.Checkpoint.VIEWING_DURABLE,PreferenceRestoreTransaction.Checkpoint.MEDIA_PREPARED,PreferenceRestoreTransaction.Checkpoint.BOOKMARKS_PREPARED,PreferenceRestoreTransaction.Checkpoint.PREFERENCES_DURABLE)){
            try{fixture.transaction.apply(fixture.transaction.prepare(all(),null,fixture.physical()),()->false,at->{if(at==boundary)throw new ProcessDeath();});fail();}catch(ProcessDeath expected){}
            assertEquals(6,new JSONObject(Files.readString(fixture.journal.toPath())).getInt("schema"));assertEquals(PreferenceRestoreTransaction.Recovery.ROLLED_BACK,new PreferenceRestoreTransaction(fixture.app).recover());
            assertEquals(before,media());assertEquals(bookmarks,BookmarkStoreRecovery.capture(fixture.app).toString());fixture.assertOriginal(viewing);assertEquals(prefs,fixture.prefs.getAll());
        }
    }
    @Test public void durableDecisionBeforeEitherSqliteCommitReplaysListsAndBookmarksOnStartup()throws Exception{
        for(var boundary:List.of(PreferenceRestoreTransaction.Checkpoint.COMMIT_MARKER_DURABLE,PreferenceRestoreTransaction.Checkpoint.MEDIA_DURABLE)){
            setup();try{fixture.transaction.apply(fixture.transaction.prepare(all(),null,fixture.physical()),()->false,at->{if(at==boundary)throw new ProcessDeath();});fail();}catch(ProcessDeath expected){}
            PreferenceRestoreTransaction.recoverAtStartup(fixture.app);assertNew();assertFalse(fixture.journal.exists());assertEquals(PreferenceRestoreTransaction.Recovery.NONE,fixture.transaction.recover());
        }
    }
    @Test public void acknowledgedMediaDoesNotOverwriteLaterManualListChanges()throws Exception{
        try{fixture.transaction.apply(fixture.transaction.prepare(all(),null,fixture.physical()),()->false,at->{if(at==PreferenceRestoreTransaction.Checkpoint.MEDIA_DURABLE)throw new ProcessDeath();});fail();}catch(ProcessDeath expected){}
        try(VideoOpenHelper helper=new VideoOpenHelper(fixture.app)){helper.getWritableDatabase().execSQL("UPDATE list_table SET title='Later manual' WHERE _id=10");}
        String current=media();assertEquals(PreferenceRestoreTransaction.Recovery.COMPLETED,fixture.transaction.recover());assertEquals(current,media());assertFalse(fixture.journal.exists());
    }
    @Test public void corruptionOfAccountPreservationIsRejectedBeforeAnyRecoveryParticipantChanges()throws Exception{
        try{fixture.transaction.apply(fixture.transaction.prepare(all(),null,fixture.physical()),()->false,at->{if(at==PreferenceRestoreTransaction.Checkpoint.COMMIT_MARKER_DURABLE)throw new ProcessDeath();});fail();}catch(ProcessDeath expected){}
        String media=media(),bookmarks=BookmarkStoreRecovery.capture(fixture.app).toString(),viewing=ViewingHistory.captureRecovery(fixture.app).toString();Map<String,?> prefs=fixture.prefs.getAll();
        JSONObject record=new JSONObject(Files.readString(fixture.journal.toPath()));record.getJSONObject("media").getJSONObject("after").getJSONObject("playlists").getJSONArray("definitions").getJSONObject(0).put("trakt",999);Files.writeString(fixture.journal.toPath(),record.toString());
        try{fixture.transaction.recover();fail();}catch(IOException expected){}assertEquals(media,media());assertEquals(bookmarks,BookmarkStoreRecovery.capture(fixture.app).toString());assertEquals(viewing,ViewingHistory.captureRecovery(fixture.app).toString());assertEquals(prefs,fixture.prefs.getAll());assertTrue(fixture.journal.exists());
    }
    @Test public void changedNativeListsOrPrivateReferencesInvalidatePreviewBeforeJournalCreation()throws Exception{
        var plan=fixture.transaction.prepare(all(),null,fixture.physical());fixture.prefs.edit().putString(PortableSavedPlaylists.referenceKey(7),ID).commit();try{fixture.transaction.apply(plan,()->false);fail();}catch(IOException expected){}assertFalse(fixture.journal.exists());
        plan=fixture.transaction.prepare(all(),null,fixture.physical());try(VideoOpenHelper helper=new VideoOpenHelper(fixture.app)){helper.getWritableDatabase().execSQL("UPDATE list_table SET title='Later' WHERE _id=7");}String current=media();try{fixture.transaction.apply(plan,()->false);fail();}catch(IOException expected){}assertEquals(current,media());assertEquals(7,fixture.prefs.getInt("preview_accent41",0));assertFalse(fixture.journal.exists());
    }
    @Test public void newDevicePendingDefinitionReexportsExactIdentityWithoutCreatingMediaDatabase()throws Exception{
        fixture.app.deleteDatabase("media.db");var selected=Map.of(BackupCategory.PLAYLISTS,payload());var plan=fixture.transaction.prepare(selected,null);assertEquals(1,plan.unresolved);fixture.transaction.apply(plan,()->false);
        assertFalse(VideoOpenHelper.getDatabaseFile(fixture.app).exists());assertEquals(payload().toString(),PortableSavedPlaylists.export(fixture.app,Collections.emptyMap()).toString());assertEquals("AUTH_CANARY",fixture.prefs.getString("oauth_token",null));
    }
    @Test public void presentEmptyClearsOnlyLocalDefinitionsWhileOmittedLeavesListsUntouched()throws Exception{
        String before=media();fixture.transaction.apply(fixture.transaction.prepare(Map.of(BackupCategory.APPEARANCE,all().get(BackupCategory.APPEARANCE)),null),()->false);assertEquals(before,media());
        fixture.transaction.apply(fixture.transaction.prepare(Map.of(BackupCategory.PLAYLISTS,new JSONObject().put("schema",1).put("rows",new JSONArray())),null),()->false);
        JSONArray rows=MediaStateRecovery.capture(fixture.app,false,false,true).getJSONObject("playlists").getJSONArray("definitions");assertEquals(1,rows.length());assertEquals(9,rows.getJSONObject(0).getLong("id"));assertFalse(fixture.prefs.contains(PortableSavedPlaylists.referenceKey(7)));assertEquals(9,MediaStateRecovery.capture(fixture.app,true,true).getJSONArray("pins").getJSONObject(0).getLong("value"));
    }
    @Test public void workerInterruptAtMediaPreparationRecoversListsAndAllOtherOriginals()throws Exception{
        String before=media(),viewing=ViewingHistory.captureRecovery(fixture.app).toString();try{try{fixture.transaction.apply(fixture.transaction.prepare(all(),null,fixture.physical()),()->false,at->{if(at==PreferenceRestoreTransaction.Checkpoint.MEDIA_PREPARED)Thread.currentThread().interrupt();});fail();}catch(PreferenceRestoreTransaction.Cancelled expected){assertEquals(PreferenceRestoreTransaction.Recovery.ROLLED_BACK,expected.outcome);assertFalse(expected.recoveryRequired);assertTrue(Thread.currentThread().isInterrupted());}}finally{Thread.interrupted();}assertEquals(before,media());fixture.assertOriginal(viewing);assertFalse(fixture.journal.exists());
    }
}
