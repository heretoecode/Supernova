package com.archos.mediacenter.video.utils;

import android.app.Application;
import com.archos.mediacenter.video.leanback.ViewingHistory;
import com.archos.mediacenter.video.utils.backup.*;
import com.archos.mediaprovider.video.VideoOpenHelper;
import com.archos.mediacenter.utils.ShortcutDbAdapter;
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
public class CombinedIndexedSourcesTest {
    private final CombinedViewingRestoreTest fixture=new CombinedViewingRestoreTest();
    private static class ProcessDeath extends Error {}
    private static final String LOCATION="smb://nas.example/Media";
    @Before public void setup()throws Exception{
        fixture.setup();var app=fixture.app;app.deleteDatabase("media.db");app.deleteDatabase(BookmarkStoreRecovery.DATABASE);app.deleteDatabase(IndexedSourceStoreRecovery.DATABASE);
        fixture.prefs.edit().putStringSet(PortableLibrarySources.ROOTS,Set.of(LOCATION)).putStringSet(PortableLibrarySources.EXCLUSIONS,Set.of("file:///receiving-excluded")).commit();
        try(VideoOpenHelper helper=new VideoOpenHelper(app)){var db=helper.getWritableDatabase();db.execSQL("INSERT INTO files(_id,remote_id,media_type,_data,Archos_hiddenByUser,bookmark) VALUES(10,10,3,'file:///media/PATH_CANARY.mkv',1,4444)");db.execSQL("INSERT INTO MOVIE(_id,video_id,m_online_id,Nova_pinned) VALUES(100,10,123,9)");db.execSQL("INSERT INTO list_table(_id,title,trakt_id,sync_status) VALUES(7,'Original local',NULL,1),(9,'Receiving remote',987654,0)");db.execSQL("INSERT INTO video_list_table(_id,list_id,m_online_id,e_online_id,sync_status) VALUES(12,7,123,-1,1),(13,9,-1,456,0)");}
        try(var db=android.database.sqlite.SQLiteDatabase.openOrCreateDatabase(app.getDatabasePath(BookmarkStoreRecovery.DATABASE),null)){db.execSQL("CREATE TABLE shortcuts(_id INTEGER PRIMARY KEY AUTOINCREMENT,uri TEXT NOT NULL,shortcut_name TEXT NOT NULL,friendly_uri TEXT)");db.setVersion(2);db.execSQL("INSERT INTO shortcuts(uri,shortcut_name,friendly_uri) VALUES('smb://nas.example/Media','Original bookmark',NULL)");}
        try(var db=android.database.sqlite.SQLiteDatabase.openOrCreateDatabase(app.getDatabasePath(IndexedSourceStoreRecovery.DATABASE),null)){db.execSQL("CREATE TABLE shortcuts_table_video(_id INTEGER PRIMARY KEY AUTOINCREMENT,path TEXT NOT NULL,name TEXT NOT NULL,ippath TEXT,friendly_url TEXT,rescan INTEGER DEFAULT(1))");db.setVersion(5);db.execSQL("INSERT INTO shortcuts_table_video(_id,path,name,ippath,friendly_url,rescan) VALUES(1,'smb://nas.example/Media','Original source','smb://10.0.0.1/Media','Receiving friendly locator',0),(2,'putio:///ACCOUNT_CANARY','Receiving integration',NULL,NULL,NULL)");}
    }
    private JSONObject source()throws Exception{return new JSONObject().put("schema",2).put("roots",new JSONArray().put(PortableLocations.reference(LOCATION))).put("exclusions",new JSONArray()).put("indexed",new JSONObject().put("schema",1).put("rows",new JSONArray().put(new JSONObject().put("location",PortableLocations.reference(LOCATION)).put("name","Restored source").put("rescan",true))));}
    private Map<BackupCategory,JSONObject> all()throws Exception{
        var result=fixture.incoming(true);result.put(BackupCategory.SOURCES,source());result.put(BackupCategory.PINNED,new JSONObject().put("schema",1).put("rows",new JSONArray()));result.put(BackupCategory.HIDDEN,new JSONObject().put("schema",1).put("rows",new JSONArray()));
        result.put(BackupCategory.PLAYLISTS,new JSONObject().put("schema",1).put("rows",new JSONArray().put(new JSONObject().put("id","playlist:12345678-1234-1234-1234-123456789012:1").put("name","Restored local").put("members",new JSONArray().put("tmdb:movie:321")))));
        result.put(BackupCategory.BOOKMARKS,new JSONObject().put("schema",1).put("rows",new JSONArray().put(new JSONObject().put("id","bookmark:12345678-1234-1234-1234-123456789012:1").put("location",PortableLocations.reference(LOCATION)).put("name","Restored bookmark"))));return result;
    }
    private String media()throws Exception{return MediaStateRecovery.capture(fixture.app,true,true,true).toString();}
    private String sources()throws Exception{return IndexedSourceStoreRecovery.capture(fixture.app).toString();}
    private void assertNew()throws Exception{
        assertEquals(19,fixture.prefs.getInt("preview_accent41",0));assertTrue(fixture.health.getBoolean("incorrect:10",false));assertTrue(ViewingHistory.read(fixture.app).get("tmdb:movie:456").completed);
        assertEquals(Set.of(LOCATION),fixture.prefs.getStringSet(PortableLibrarySources.ROOTS,Set.of()));assertTrue(fixture.prefs.getStringSet(PortableLibrarySources.EXCLUSIONS,Set.of()).isEmpty());
        JSONObject source=IndexedSourceStoreRecovery.capture(fixture.app).getJSONArray("rows").getJSONObject(0);assertEquals("Restored source",source.getString("name"));assertEquals(1,source.getInt("rescan"));assertEquals("smb://10.0.0.1/Media",source.getString("ip"));
        assertEquals("putio:///ACCOUNT_CANARY",IndexedSourceStoreRecovery.capture(fixture.app).getJSONArray("rows").getJSONObject(1).getString("path"));assertEquals("Restored bookmark",BookmarkStoreRecovery.capture(fixture.app).getJSONArray("rows").getJSONObject(0).getString("name"));
        JSONObject state=MediaStateRecovery.capture(fixture.app,true,true,true);assertEquals(0,state.getJSONArray("pins").getJSONObject(0).getLong("value"));assertFalse(state.getJSONArray("hidden").getJSONObject(0).getBoolean("value"));assertEquals("Restored local",state.getJSONObject("playlists").getJSONArray("definitions").getJSONObject(1).getString("title"));assertEquals("AUTH_CANARY",fixture.prefs.getString("oauth_token",null));
        // The existing scanner reader, rather than only the backup implementation, sees the new choice.
        ShortcutDbAdapter.VIDEO.close();assertTrue(ShortcutDbAdapter.VIDEO.getRescanUris(fixture.app).contains(android.net.Uri.parse(LOCATION)));ShortcutDbAdapter.VIDEO.close();
    }
    @Test public void everyPreDecisionBoundaryRecoversAllSixStoresIncludingIndexedRescanIntent()throws Exception{
        String media=media(),sources=sources(),bookmarks=BookmarkStoreRecovery.capture(fixture.app).toString(),viewing=ViewingHistory.captureRecovery(fixture.app).toString();Map<String,?> prefs=fixture.prefs.getAll();
        for(var boundary:List.of(PreferenceRestoreTransaction.Checkpoint.JOURNAL_DURABLE,PreferenceRestoreTransaction.Checkpoint.DEFAULT_DURABLE,PreferenceRestoreTransaction.Checkpoint.HEALTH_DURABLE,PreferenceRestoreTransaction.Checkpoint.VIEWING_DURABLE,PreferenceRestoreTransaction.Checkpoint.MEDIA_PREPARED,PreferenceRestoreTransaction.Checkpoint.BOOKMARKS_PREPARED,PreferenceRestoreTransaction.Checkpoint.SOURCES_PREPARED,PreferenceRestoreTransaction.Checkpoint.PREFERENCES_DURABLE)){
            try{fixture.transaction.apply(fixture.transaction.prepare(all(),null,fixture.physical()),()->false,at->{if(at==boundary)throw new ProcessDeath();});fail();}catch(ProcessDeath expected){}
            assertEquals(7,new JSONObject(Files.readString(fixture.journal.toPath())).getInt("schema"));assertEquals(PreferenceRestoreTransaction.Recovery.ROLLED_BACK,new PreferenceRestoreTransaction(fixture.app).recover());fixture.assertOriginal(viewing);assertEquals(media,media());assertEquals(sources,sources());assertEquals(bookmarks,BookmarkStoreRecovery.capture(fixture.app).toString());assertEquals(prefs,fixture.prefs.getAll());assertFalse(fixture.journal.exists());
        }
    }
    @Test public void commitDecisionBeforeEverySqliteAcknowledgementReplaysAllSixStores()throws Exception{
        for(var boundary:List.of(PreferenceRestoreTransaction.Checkpoint.COMMIT_MARKER_DURABLE,PreferenceRestoreTransaction.Checkpoint.MEDIA_DURABLE,PreferenceRestoreTransaction.Checkpoint.BOOKMARKS_DURABLE,PreferenceRestoreTransaction.Checkpoint.SOURCES_DURABLE)){
            setup();try{fixture.transaction.apply(fixture.transaction.prepare(all(),null,fixture.physical()),()->false,at->{if(at==boundary)throw new ProcessDeath();});fail();}catch(ProcessDeath expected){}PreferenceRestoreTransaction.recoverAtStartup(fixture.app);assertNew();assertFalse(fixture.journal.exists());assertEquals(PreferenceRestoreTransaction.Recovery.NONE,fixture.transaction.recover());
        }
    }
    @Test public void sourceAcknowledgementPreservesLaterManualNameAndRescanChoiceDuringCleanup()throws Exception{
        try{fixture.transaction.apply(fixture.transaction.prepare(all(),null,fixture.physical()),()->false,at->{if(at==PreferenceRestoreTransaction.Checkpoint.SOURCES_DURABLE)throw new ProcessDeath();});fail();}catch(ProcessDeath expected){}
        try(var db=android.database.sqlite.SQLiteDatabase.openDatabase(fixture.app.getDatabasePath(IndexedSourceStoreRecovery.DATABASE).getPath(),null,android.database.sqlite.SQLiteDatabase.OPEN_READWRITE)){db.execSQL("UPDATE shortcuts_table_video SET name='Later manual',rescan=0 WHERE _id=1");}String later=sources();assertEquals(PreferenceRestoreTransaction.Recovery.COMPLETED,fixture.transaction.recover());assertEquals(later,sources());
    }
    @Test public void corruptExcludedSourcePairRejectsBeforeAnyEarlierRecoveryMutation()throws Exception{
        try{fixture.transaction.apply(fixture.transaction.prepare(all(),null,fixture.physical()),()->false,at->{if(at==PreferenceRestoreTransaction.Checkpoint.COMMIT_MARKER_DURABLE)throw new ProcessDeath();});fail();}catch(ProcessDeath expected){}
        String media=media(),sources=sources(),bookmarks=BookmarkStoreRecovery.capture(fixture.app).toString(),viewing=ViewingHistory.captureRecovery(fixture.app).toString();Map<String,?> prefs=fixture.prefs.getAll();JSONObject record=new JSONObject(Files.readString(fixture.journal.toPath()));record.getJSONObject("sources").getJSONObject("after").getJSONArray("rows").getJSONObject(1).put("path","putio:///OTHER_ACCOUNT");Files.writeString(fixture.journal.toPath(),record.toString());
        try{fixture.transaction.recover();fail();}catch(IOException expected){}assertEquals(media,media());assertEquals(sources,sources());assertEquals(bookmarks,BookmarkStoreRecovery.capture(fixture.app).toString());assertEquals(viewing,ViewingHistory.captureRecovery(fixture.app).toString());assertEquals(prefs,fixture.prefs.getAll());assertTrue(fixture.journal.exists());
    }
    @Test public void staleIndexedPreviewFailsBeforeAnyPreferenceOrJournalWrite()throws Exception{
        var plan=fixture.transaction.prepare(all(),null,fixture.physical());try(var db=android.database.sqlite.SQLiteDatabase.openDatabase(fixture.app.getDatabasePath(IndexedSourceStoreRecovery.DATABASE).getPath(),null,android.database.sqlite.SQLiteDatabase.OPEN_READWRITE)){db.execSQL("UPDATE shortcuts_table_video SET rescan=1 WHERE _id=1");}String later=sources();try{fixture.transaction.apply(plan,()->false);fail();}catch(IOException expected){}assertEquals(later,sources());assertEquals(7,fixture.prefs.getInt("preview_accent41",0));assertFalse(fixture.journal.exists());
    }
    @Test public void historicalSchemaOneSourceRestorationLeavesIndexedFoldersAndPendingIntentUntouched()throws Exception{
        String before=sources();fixture.prefs.edit().putString(PortableIndexedSources.PENDING,"PRIVATE_UNSELECTED_CANARY").commit();JSONObject old=new JSONObject(source().toString()).put("schema",1);old.remove("indexed");fixture.transaction.apply(fixture.transaction.prepare(Map.of(BackupCategory.SOURCES,old),null),()->false);assertEquals(before,sources());assertEquals("PRIVATE_UNSELECTED_CANARY",fixture.prefs.getString(PortableIndexedSources.PENDING,null));
    }
    @Test public void realSourceExportArchiveImportDeclaresSchemaTwoAndPreservesPrivacyAndRescan()throws Exception{
        File archive=LogicalBackupExporter.preparePrivate(fixture.app,Set.of(BackupCategory.SOURCES),()->false);try(var zip=new java.util.zip.ZipFile(archive)){String text=new String(zip.getInputStream(zip.getEntry("categories/SOURCES.json")).readAllBytes(),java.nio.charset.StandardCharsets.UTF_8);assertFalse(text.contains(LOCATION));assertFalse(text.contains("10.0.0.1"));assertFalse(text.contains("ACCOUNT_CANARY"));assertFalse(text.contains("AUTH_CANARY"));JSONObject manifest=new JSONObject(new String(zip.getInputStream(zip.getEntry("manifest.json")).readAllBytes(),java.nio.charset.StandardCharsets.UTF_8));assertEquals(2,manifest.getJSONArray("categories").getJSONObject(0).getInt("schema"));}
        try(var imported=LogicalBackupImport.prepare(fixture.app,new FileInputStream(archive),()->false)){var payloads=imported.payloads(()->false);assertEquals(2,payloads.get(BackupCategory.SOURCES).getInt("schema"));fixture.transaction.apply(fixture.transaction.prepare(payloads,null),()->false);}assertFalse(IndexedSourceStoreRecovery.capture(fixture.app).getJSONArray("rows").getJSONObject(0).getInt("rescan")==1);assertTrue(archive.delete());
    }
    @Test public void sourcesOnlyJournalHasNullableOtherParticipantsAndCancelsWithoutCreatingNativeStore()throws Exception{
        fixture.app.deleteDatabase(IndexedSourceStoreRecovery.DATABASE);fixture.prefs.edit().putStringSet(PortableLibrarySources.ROOTS,Set.of()).commit();JSONObject source=source();source.put("roots",new JSONArray());var plan=fixture.transaction.prepare(Map.of(BackupCategory.SOURCES,source),null);assertEquals(1,plan.unresolved);
        try{fixture.transaction.apply(plan,()->false,at->{if(at==PreferenceRestoreTransaction.Checkpoint.SOURCES_PREPARED)throw new ProcessDeath();});fail();}catch(ProcessDeath expected){}
        JSONObject record=new JSONObject(Files.readString(fixture.journal.toPath()));assertEquals(7,record.getInt("schema"));assertTrue(record.isNull("media"));assertTrue(record.isNull("bookmarks"));assertTrue(record.isNull("viewing"));assertEquals(PreferenceRestoreTransaction.Recovery.ROLLED_BACK,fixture.transaction.recover());assertFalse(fixture.app.getDatabasePath(IndexedSourceStoreRecovery.DATABASE).exists());
    }
    @Test public void pendingSourcesResolveOnlyAfterReceivingRootSetupAndPreserveLocalUserState()throws Exception{
        fixture.app.deleteDatabase(IndexedSourceStoreRecovery.DATABASE);fixture.prefs.edit().putStringSet(PortableLibrarySources.ROOTS,Set.of()).putStringSet(PortableLibrarySources.EXCLUSIONS,Set.of()).commit();
        fixture.transaction.apply(fixture.transaction.prepare(Map.of(BackupCategory.SOURCES,source()),null),()->false);assertFalse(fixture.app.getDatabasePath(IndexedSourceStoreRecovery.DATABASE).exists());assertEquals(1,new JSONObject(fixture.prefs.getString(PortableIndexedSources.PENDING,null)).getJSONArray("rows").length());
        Map<String,?> pending=fixture.prefs.getAll();fixture.transaction.reconcileSources();assertEquals(pending,fixture.prefs.getAll());assertFalse(fixture.journal.exists());assertFalse(fixture.app.getDatabasePath(IndexedSourceStoreRecovery.DATABASE).exists());
        fixture.prefs.edit().putStringSet(PortableLibrarySources.ROOTS,Set.of(LOCATION,"file:///receiving-added")).commit();fixture.transaction.reconcileSources();assertEquals(Set.of(LOCATION,"file:///receiving-added"),fixture.prefs.getStringSet(PortableLibrarySources.ROOTS,Set.of()));assertEquals(0,new JSONObject(fixture.prefs.getString(PortableIndexedSources.PENDING,null)).getJSONArray("rows").length());assertEquals("Restored source",IndexedSourceStoreRecovery.capture(fixture.app).getJSONArray("rows").getJSONObject(0).getString("name"));String stable=sources();fixture.transaction.reconcileSources();assertEquals(stable,sources());assertFalse(fixture.journal.exists());assertEquals("AUTH_CANARY",fixture.prefs.getString("oauth_token",null));
    }
    @Test public void liveIndexedChoiceWinsOverPendingIdentityAndMalformedPendingNeverChangesReceivingStores()throws Exception{
        fixture.prefs.edit().putString(PortableIndexedSources.PENDING,source().getJSONObject("indexed").toString()).commit();fixture.transaction.reconcileSources();JSONObject row=IndexedSourceStoreRecovery.capture(fixture.app).getJSONArray("rows").getJSONObject(0);assertEquals("Original source",row.getString("name"));assertEquals(0,row.getInt("rescan"));assertEquals(0,new JSONObject(fixture.prefs.getString(PortableIndexedSources.PENDING,null)).getJSONArray("rows").length());
        String sources=sources(),media=media();fixture.prefs.edit().putString(PortableIndexedSources.PENDING,"{\"schema\":999,\"rows\":[]}").commit();Map<String,?> prefs=fixture.prefs.getAll();try{fixture.transaction.reconcileSources();fail();}catch(IOException expected){}assertEquals(sources,sources());assertEquals(media,media());assertEquals(prefs,fixture.prefs.getAll());assertFalse(fixture.journal.exists());
    }
}
