package com.archos.mediacenter.video.utils;

import android.app.Application;
import android.content.Context;
import android.net.Uri;
import androidx.preference.PreferenceManager;
import com.archos.mediacenter.utils.videodb.VideoDbInfo;
import com.archos.mediacenter.video.leanback.*;
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
public class LegacyViewingRestoreTest {
    private Context app;private File journal;private PreferenceRestoreTransaction transaction;
    private static class ProcessDeath extends Error {}
    @Before public void setup()throws Exception {
        app=RuntimeEnvironment.getApplication();ViewingHistory.awaitWrites();app.deleteDatabase(ViewingHistory.DATABASE);app.deleteDatabase("media.db");
        PreferenceManager.getDefaultSharedPreferences(app).edit().clear().putString("oauth_token","AUTH_CANARY").commit();journal=new File(app.getFilesDir(),"supernova-restore/preferences-v1.json");journal.delete();new File(journal+".bak").delete();transaction=new PreferenceRestoreTransaction(app);
        try(VideoOpenHelper helper=new VideoOpenHelper(app)){var db=helper.getWritableDatabase();db.execSQL("INSERT INTO files(_id,remote_id,media_type,_data,ArchosMediaScraper_type,ArchosMediaScraper_id,bookmark,duration,Archos_lastTimePlayed,Archos_traktSeen) VALUES(10,10,3,'file:///legacy.mkv',11,100,4444,10000,100,1)");db.execSQL("INSERT INTO MOVIE(_id,video_id,m_online_id,name_movie) VALUES(100,10,123,'Legacy')");}
    }
    private JSONObject empty(BackupCategory category)throws Exception{return new JSONObject().put("schema",1).put("kind",category.name()).put("rows",new JSONArray());}
    private PreferenceRestoreTransaction.Plan plan(BackupCategory category)throws Exception{return transaction.prepare(Map.of(category,empty(category)),null);}
    private VideoDbInfo info(){VideoDbInfo value=new VideoDbInfo();value.uri=Uri.parse("file:///legacy.mkv");value.scraperMovieId="123";value.resume=4444;value.lastTimePlayed=0;value.bookmark=7654;return value;}
    private String nativeState()throws Exception{try(VideoOpenHelper helper=new VideoOpenHelper(app);var rows=helper.getReadableDatabase().rawQuery("SELECT bookmark,Archos_lastTimePlayed,Archos_traktSeen FROM files WHERE _id=10",null)){assertTrue(rows.moveToFirst());return rows.getInt(0)+":"+rows.getLong(1)+":"+rows.getInt(2);}}
    @Test public void selectedEmptyHistorySuppressesLegacyReintroductionAndPreservesUnselectedResume()throws Exception {
        String nativeBefore=nativeState();transaction.apply(plan(BackupCategory.HISTORY),()->false);var snapshot=LogicalBackupCatalog.capture(app,()->false).snapshot;var records=ViewingHistory.reconcile(app,snapshot.movies);var record=records.get("tmdb:movie:123");assertFalse(record.completed);assertEquals(0,record.played);assertEquals(4444,record.position);assertFalse(((com.archos.mediacenter.video.browser.adapters.object.Video)snapshot.movies.get(0).media).isWatched());assertEquals(nativeBefore,nativeState());assertEquals("AUTH_CANARY",PreferenceManager.getDefaultSharedPreferences(app).getString("oauth_token",null));
    }
    @Test public void selectedEmptyPositionsSuppressNativeOffsetsAndKeepUnselectedWatchState()throws Exception {
        String nativeBefore=nativeState();transaction.apply(plan(BackupCategory.POSITIONS),()->false);var record=ViewingHistory.read(app).get("tmdb:movie:123");assertTrue(record.completed);assertEquals(100000,record.played);assertEquals(0,record.completedAt);assertEquals(0,record.position);var value=info();assertTrue(ViewingHistory.applyRestoredPosition(app,value));assertEquals(0,value.resume);assertEquals(0,value.lastTimePlayed);assertEquals(7654,value.bookmark);assertEquals(nativeBefore,nativeState());
    }
    @Test public void restoredPositivePositionWorksWithoutInventingPlayedTimeAndResumeOptOutWins()throws Exception {
        JSONObject payload=empty(BackupCategory.POSITIONS);payload.getJSONArray("rows").put(new JSONObject().put("identity","tmdb:movie:123").put("position",7777).put("duration",12000));transaction.apply(transaction.prepare(Map.of(BackupCategory.POSITIONS,payload),null),()->false);var value=info();assertTrue(ViewingHistory.applyRestoredPosition(app,value));assertEquals(7777,value.resume);assertEquals(0,value.lastTimePlayed);assertEquals(7654,value.bookmark);PreferenceManager.getDefaultSharedPreferences(app).edit().putBoolean(ViewingHistory.RESUME,false).commit();assertTrue(ViewingHistory.applyRestoredPosition(app,value));assertEquals(0,value.resume);
    }
    @Test public void everyPreDecisionFailureRemovesCopiedNativeRowsAndNewStoreAndRestoresFlags()throws Exception {
        String nativeBefore=nativeState();for(var category:List.of(BackupCategory.HISTORY,BackupCategory.POSITIONS))for(var boundary:List.of(PreferenceRestoreTransaction.Checkpoint.JOURNAL_DURABLE,PreferenceRestoreTransaction.Checkpoint.DEFAULT_DURABLE,PreferenceRestoreTransaction.Checkpoint.VIEWING_DURABLE,PreferenceRestoreTransaction.Checkpoint.PREFERENCES_DURABLE)){
            try{transaction.apply(plan(category),()->false,at->{if(at==boundary)throw new ProcessDeath();});fail();}catch(ProcessDeath expected){}JSONObject recovery=new JSONObject(Files.readString(journal.toPath()));assertEquals(8,recovery.getInt("schema"));assertEquals(1,recovery.getJSONObject("viewing").getJSONObject("introduced").getJSONArray("rows").length());assertEquals(PreferenceRestoreTransaction.Recovery.ROLLED_BACK,new PreferenceRestoreTransaction(app).recover());assertFalse(app.getDatabasePath(ViewingHistory.DATABASE).exists());assertEquals(Map.of("oauth_token","AUTH_CANARY"),PreferenceManager.getDefaultSharedPreferences(app).getAll());assertEquals(nativeBefore,nativeState());
        }
    }
    @Test public void rollbackPreservesLaterIndependentOffsetInsteadOfDeletingIntroducedRow()throws Exception {
        try{transaction.apply(plan(BackupCategory.HISTORY),()->false,at->{if(at==PreferenceRestoreTransaction.Checkpoint.VIEWING_DURABLE)throw new ProcessDeath();});fail();}catch(ProcessDeath expected){}JSONObject changed=ViewingHistory.captureRecovery(app);changed.getJSONArray("rows").getJSONObject(0).put("position",8888);ViewingHistory.replaceRecovery(app,changed,()->false);assertEquals(PreferenceRestoreTransaction.Recovery.ROLLED_BACK,transaction.recover());var value=ViewingHistory.read(app).get("tmdb:movie:123");assertNotNull(value);assertEquals(8888,value.position);assertFalse(value.completed);
    }
    @Test public void changedNativePreviewRejectsBeforeJournalOrPreferencesWrite()throws Exception {
        var prepared=plan(BackupCategory.HISTORY);try(VideoOpenHelper helper=new VideoOpenHelper(app)){helper.getWritableDatabase().execSQL("UPDATE files SET bookmark=9999 WHERE _id=10");}try{transaction.apply(prepared,()->false);fail();}catch(IOException expected){}assertFalse(journal.exists());assertFalse(app.getDatabasePath(ViewingHistory.DATABASE).exists());assertEquals(Map.of("oauth_token","AUTH_CANARY"),PreferenceManager.getDefaultSharedPreferences(app).getAll());
    }
    @Test public void missingOrConflictingIntroducedRecoveryRejectsBeforeAnyMutation()throws Exception {
        for(boolean absent:List.of(true,false)){
            try{transaction.apply(plan(BackupCategory.HISTORY),()->false,at->{if(at==PreferenceRestoreTransaction.Checkpoint.VIEWING_DURABLE)throw new ProcessDeath();});fail();}catch(ProcessDeath expected){}JSONObject original=new JSONObject(Files.readString(journal.toPath())),corrupt=new JSONObject(original.toString());if(absent)corrupt.put("viewing",JSONObject.NULL);else corrupt.getJSONObject("viewing").put("before",corrupt.getJSONObject("viewing").getJSONObject("introduced"));Files.writeString(journal.toPath(),corrupt.toString());String store=ViewingHistory.captureRecovery(app).toString();var prefs=PreferenceManager.getDefaultSharedPreferences(app).getAll();try{transaction.recover();fail();}catch(IOException expected){}assertEquals(store,ViewingHistory.captureRecovery(app).toString());assertEquals(prefs,PreferenceManager.getDefaultSharedPreferences(app).getAll());assertTrue(journal.exists());Files.writeString(journal.toPath(),original.toString());transaction.recover();
        }
    }
}
