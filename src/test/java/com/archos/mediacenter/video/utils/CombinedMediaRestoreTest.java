package com.archos.mediacenter.video.utils;

import android.content.*;
import com.archos.mediacenter.video.leanback.ViewingHistory;
import com.archos.mediacenter.video.utils.backup.*;
import com.archos.mediaprovider.video.VideoOpenHelper;
import java.io.*;
import java.nio.file.Files;
import java.util.*;
import org.json.*;
import org.junit.*;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.*;
import static org.junit.Assert.*;

@RunWith(RobolectricTestRunner.class) @Config(application=android.app.Application.class,sdk=28) @SQLiteMode(SQLiteMode.Mode.NATIVE)
public class CombinedMediaRestoreTest extends CombinedViewingRestoreTest {
    private static class ProcessDeath extends Error {}
    @Before public void media()throws Exception {
        app.deleteDatabase("media.db");try(VideoOpenHelper helper=new VideoOpenHelper(app)) {
            var db=helper.getWritableDatabase();db.execSQL("INSERT INTO files(_id,remote_id,media_type,Archos_hiddenByUser,_data,bookmark,title) VALUES(10,10,3,1,'file:///media/PATH_CANARY.mkv',4444,'TITLE_CANARY'),(11,11,3,NULL,'file:///media/second.mkv',5555,'SECOND_CANARY')");
            db.execSQL("INSERT INTO MOVIE(_id,video_id,m_online_id,Nova_pinned,name_movie) VALUES(100,10,123,9,'MOVIE_CANARY'),(101,11,456,NULL,'OTHER_CANARY')");
            db.execSQL("INSERT INTO SHOW(_id,s_online_id,Nova_pinned,name_show) VALUES(20,789,8,'SHOW_CANARY')");
            db.execSQL("UPDATE files SET ArchosMediaScraper_type=1,ArchosMediaScraper_id=100 WHERE _id=10");
        }
    }
    private Map<BackupCategory,JSONObject> all()throws Exception {
        Map<BackupCategory,JSONObject> selected=incoming(true);Context source=new ContextWrapper(app){
            @Override public SharedPreferences getSharedPreferences(String name,int mode){return super.getSharedPreferences("pin-source-"+name,mode);}
            @Override public Context getApplicationContext(){return this;}
        };
        selected.put(BackupCategory.PINNED,PortablePinnedTitles.export(source,List.of(
                new PortablePinnedTitles.Title(PortablePinnedTitles.Kind.MOVIE,1,123,400),new PortablePinnedTitles.Title(PortablePinnedTitles.Kind.TV,2,789,800))));
        selected.put(BackupCategory.HIDDEN,new JSONObject().put("schema",1).put("rows",new JSONArray()));return selected;
    }
    private String mediaImage()throws Exception{return MediaStateRecovery.capture(app,true,true).toString();}
    private void assertNew()throws Exception {
        assertEquals(19,prefs.getInt("preview_accent41",0));assertTrue(health.getBoolean("incorrect:10",false));assertTrue(ViewingHistory.read(app).get("tmdb:movie:456").completed);
        JSONObject media=MediaStateRecovery.capture(app,true,true);JSONArray pins=media.getJSONArray("pins");assertEquals(400,pins.getJSONObject(0).getLong("value"));
        assertEquals(0,pins.getJSONObject(1).getLong("value"));assertEquals(800,pins.getJSONObject(2).getLong("value"));
        for(int i=0;i<media.getJSONArray("hidden").length();i++)assertFalse(media.getJSONArray("hidden").getJSONObject(i).getBoolean("value"));
        try(android.database.sqlite.SQLiteDatabase db=android.database.sqlite.SQLiteDatabase.openDatabase(VideoOpenHelper.getDatabaseFile(app).getPath(),null,android.database.sqlite.SQLiteDatabase.OPEN_READONLY);
            var rows=db.rawQuery("SELECT bookmark,title,_data FROM files WHERE _id=10",null)) {
            assertTrue(rows.moveToFirst());assertEquals(4444,rows.getInt(0));assertEquals("TITLE_CANARY",rows.getString(1));assertEquals("file:///media/PATH_CANARY.mkv",rows.getString(2));
        }
    }
    @Test public void fourthStoreDeathBeforeCommitDecisionRollsBackAllStoresAndPreservesSqlNulls()throws Exception {
        String originalMedia=mediaImage(),originalViewing=ViewingHistory.captureRecovery(app).toString();
        for(var boundary:List.of(PreferenceRestoreTransaction.Checkpoint.JOURNAL_DURABLE,PreferenceRestoreTransaction.Checkpoint.DEFAULT_DURABLE,
                PreferenceRestoreTransaction.Checkpoint.HEALTH_DURABLE,PreferenceRestoreTransaction.Checkpoint.VIEWING_DURABLE,
                PreferenceRestoreTransaction.Checkpoint.MEDIA_PREPARED,PreferenceRestoreTransaction.Checkpoint.PREFERENCES_DURABLE)) {
            try{transaction.apply(transaction.prepare(all(),null,physical()),()->false,at->{if(at==boundary)throw new ProcessDeath();});fail();}catch(ProcessDeath expected){}
            String image=Files.readString(journal.toPath());assertEquals(5,new JSONObject(image).getInt("schema"));assertFalse(image.contains("CANARY"));
            assertEquals(PreferenceRestoreTransaction.Recovery.ROLLED_BACK,new PreferenceRestoreTransaction(app).recover());assertOriginal(originalViewing);assertEquals(originalMedia,mediaImage());
            assertFalse(prefs.contains(PortablePinnedTitles.PENDING));assertFalse(prefs.contains(PortableHiddenItems.PENDING));
        }
    }
    @Test public void commitDecisionBeforeSqliteCommitReplaysMediaOnStartupAndKeepsEveryNewStore()throws Exception {
        try{transaction.apply(transaction.prepare(all(),null,physical()),()->false,at->{if(at==PreferenceRestoreTransaction.Checkpoint.COMMIT_MARKER_DURABLE)throw new ProcessDeath();});fail();}catch(ProcessDeath expected){}
        // SQLite rolled its uncommitted transaction back; the durable decision still requires replay.
        assertEquals(9,MediaStateRecovery.capture(app,true,true).getJSONArray("pins").getJSONObject(0).getLong("value"));
        PreferenceRestoreTransaction.recoverAtStartup(app);assertNew();assertFalse(journal.exists());
    }
    @Test public void committedMediaWithPendingCleanupIsIdempotentAcrossRecoveryAndLateCancellation()throws Exception {
        var cancel=new java.util.concurrent.atomic.AtomicBoolean(false);try{transaction.apply(transaction.prepare(all(),null,physical()),cancel::get,
                at->{if(at==PreferenceRestoreTransaction.Checkpoint.MEDIA_DURABLE){cancel.set(true);throw new ProcessDeath();}});fail();}catch(ProcessDeath expected){}
        assertNew();assertEquals(PreferenceRestoreTransaction.Recovery.COMPLETED,new PreferenceRestoreTransaction(app).recover());assertNew();assertEquals(PreferenceRestoreTransaction.Recovery.NONE,transaction.recover());
    }
    @Test public void injectedWriteFailureBeforeDecisionRollsBackFourthStoreAndOtherOriginals()throws Exception {
        String originalMedia=mediaImage(),originalViewing=ViewingHistory.captureRecovery(app).toString();try{transaction.apply(transaction.prepare(all(),null,physical()),()->false,
                at->{if(at==PreferenceRestoreTransaction.Checkpoint.MEDIA_PREPARED)throw new IOException("Injected pre-decision failure");});fail();}catch(IOException expected){}
        assertEquals(originalMedia,mediaImage());assertOriginal(originalViewing);assertFalse(journal.exists());
    }
    @Test public void corruptedFourthStoreImageRejectsBeforeAnyRecoveryMutations()throws Exception {
        try{transaction.apply(transaction.prepare(all(),null,physical()),()->false,at->{if(at==PreferenceRestoreTransaction.Checkpoint.MEDIA_DURABLE)throw new ProcessDeath();});fail();}catch(ProcessDeath expected){}
        JSONObject record=new JSONObject(Files.readString(journal.toPath()));record.getJSONObject("media").getJSONObject("before").getJSONArray("pins").getJSONObject(0).put("credentials","AUTH_CANARY");Files.writeString(journal.toPath(),record.toString());
        String media=mediaImage(),viewing=ViewingHistory.captureRecovery(app).toString();try{transaction.recover();fail();}catch(IOException expected){}
        assertEquals(media,mediaImage());assertEquals(viewing,ViewingHistory.captureRecovery(app).toString());assertEquals(19,prefs.getInt("preview_accent41",0));assertTrue(journal.exists());
    }
    @Test public void staleNativeInventoryFailsBeforePreferencesOrJournalChange()throws Exception {
        var plan=transaction.prepare(all(),null,physical());try(android.database.sqlite.SQLiteDatabase db=android.database.sqlite.SQLiteDatabase.openDatabase(VideoOpenHelper.getDatabaseFile(app).getPath(),null,android.database.sqlite.SQLiteDatabase.OPEN_READWRITE)) {
            db.execSQL("UPDATE MOVIE SET m_online_id=999 WHERE _id=100");
        }String changed=mediaImage();try{transaction.apply(plan,()->false);fail();}catch(IOException expected){}
        assertEquals(7,prefs.getInt("preview_accent41",0));assertEquals(changed,mediaImage());assertFalse(journal.exists());
    }
    @Test public void postDecisionIOExceptionReportsCommittedOutcomeAndReplaysMediaInsteadOfClaimingRollback()throws Exception {
        try{transaction.apply(transaction.prepare(all(),null,physical()),()->false,
                at->{if(at==PreferenceRestoreTransaction.Checkpoint.COMMIT_MARKER_DURABLE)throw new IOException("Injected post-decision failure");});fail();}
        catch(PreferenceRestoreTransaction.Failure failure){assertEquals(PreferenceRestoreTransaction.Recovery.COMPLETED,failure.outcome);assertFalse(failure.recoveryRequired);}
        assertNew();assertFalse(journal.exists());
    }
    @Test public void verifiedArchiveImportIncludesPinAndHiddenPayloadsAndPreservesOmittedData()throws Exception {
        File file=new File(app.getCacheDir(),"four-store.zip");LogicalBackupArchive.writeCategories(file,all(),()->false);
        try(var imported=LogicalBackupImport.prepare(app,new FileInputStream(file),()->false)) {
            assertTrue(imported.categories().containsAll(Set.of(BackupCategory.PINNED,BackupCategory.HIDDEN)));
            transaction.apply(transaction.prepare(imported.payloads(()->false),null,physical()),()->false);
        }assertNew();assertTrue(file.delete());
    }
    @Test public void workerInterruptionAfterViewingCommitRecoversOriginalsAndPreservesInterruptStatus()throws Exception {
        String originalMedia=mediaImage(),originalViewing=ViewingHistory.captureRecovery(app).toString();
        try {
            try{transaction.apply(transaction.prepare(all(),null,physical()),()->false,
                    at->{if(at==PreferenceRestoreTransaction.Checkpoint.VIEWING_DURABLE)Thread.currentThread().interrupt();});fail();}
            catch(PreferenceRestoreTransaction.Cancelled cancelled){assertEquals(PreferenceRestoreTransaction.Recovery.ROLLED_BACK,cancelled.outcome);assertFalse(cancelled.recoveryRequired);assertTrue(Thread.currentThread().isInterrupted());}
        }finally{Thread.interrupted();}
        assertOriginal(originalViewing);assertEquals(originalMedia,mediaImage());assertFalse(journal.exists());
    }
    @Test public void acknowledgedCompletionDoesNotOverwriteLaterManualMediaChangesDuringCleanupRecovery()throws Exception {
        try{transaction.apply(transaction.prepare(all(),null,physical()),()->false,
                at->{if(at==PreferenceRestoreTransaction.Checkpoint.COMPLETION_DURABLE)throw new ProcessDeath();});fail();}catch(ProcessDeath expected){}
        try(android.database.sqlite.SQLiteDatabase db=android.database.sqlite.SQLiteDatabase.openDatabase(VideoOpenHelper.getDatabaseFile(app).getPath(),null,android.database.sqlite.SQLiteDatabase.OPEN_READWRITE)) {
            db.execSQL("UPDATE MOVIE SET Nova_pinned=9000 WHERE _id=100");db.execSQL("UPDATE files SET Archos_hiddenByUser=1 WHERE _id=10");
        }String later=mediaImage();assertEquals(PreferenceRestoreTransaction.Recovery.COMPLETED,transaction.recover());assertEquals(later,mediaImage());assertFalse(journal.exists());
    }
    @Test public void earlierVersionFourDecisionWithoutAcknowledgementStillReplaysSafely()throws Exception {
        try{transaction.apply(transaction.prepare(all(),null,physical()),()->false,
                at->{if(at==PreferenceRestoreTransaction.Checkpoint.COMMIT_MARKER_DURABLE)throw new ProcessDeath();});fail();}catch(ProcessDeath expected){}
        JSONObject older=new JSONObject(Files.readString(journal.toPath()));older.put("schema",4);older.getJSONObject("media").remove("committed");Files.writeString(journal.toPath(),older.toString());
        assertEquals(PreferenceRestoreTransaction.Recovery.COMPLETED,transaction.recover());assertNew();assertFalse(journal.exists());
    }
}
