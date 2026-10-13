package com.archos.mediacenter.video.utils;

import android.app.Application;
import android.content.*;
import android.net.Uri;
import androidx.preference.PreferenceManager;
import com.archos.mediacenter.video.browser.adapters.object.Movie;
import com.archos.mediacenter.video.leanback.*;
import com.archos.mediacenter.video.utils.backup.*;
import com.archos.mediaprovider.video.SupernovaLibraryPolicy;
import java.io.*;
import java.nio.file.Files;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import org.json.*;
import org.junit.*;
import org.junit.runner.RunWith;
import org.robolectric.*;
import org.robolectric.annotation.*;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith(RobolectricTestRunner.class) @Config(application=Application.class,sdk=28) @SQLiteMode(SQLiteMode.Mode.NATIVE)
public class CombinedViewingRestoreTest {
    private Context app;private SharedPreferences prefs,health;private File journal;private PreferenceRestoreTransaction transaction;
    private static final class ProcessDeath extends Error {}
    private JSONObject row(String id,boolean completed,int position,long played)throws Exception {
        return new JSONObject().put("identity",id).put("completed",completed).put("started",true).put("position",position)
                .put("duration",10000).put("played",played).put("completedAt",completed?played:0);
    }
    private JSONObject original()throws Exception {return new JSONObject().put("schema",1).put("rows",new JSONArray()
            .put(row("tmdb:movie:123",true,-2,100)).put(row("tmdb:tv:999:2:4",false,2345,50)));}
    private JSONObject appearance()throws Exception {return new JSONObject().put("schema",1).put("category","APPEARANCE")
            .put("values",new JSONObject().put("preview_accent41",new JSONObject().put("type","INT").put("value",19)));}
    private Map<BackupCategory,JSONObject> incoming(boolean positions)throws Exception {
        JSONObject history=new JSONObject().put("schema",1).put("kind","HISTORY").put("rows",new JSONArray()
                .put(new JSONObject().put("identity","tmdb:movie:456").put("completed",true).put("started",true).put("played",900).put("completedAt",900)));
        Map<BackupCategory,JSONObject> result=new EnumMap<>(BackupCategory.class);result.put(BackupCategory.APPEARANCE,appearance());result.put(BackupCategory.HISTORY,history);
        result.put(BackupCategory.REVIEW,new JSONObject().put("schema",1).put("rows",new JSONArray().put(new JSONObject()
                .put("physical",physical().reference(10)).put("expected","tmdb:movie:123").put("incorrect",true))));
        if(positions)result.put(BackupCategory.POSITIONS,new JSONObject().put("schema",1).put("kind","POSITIONS").put("rows",new JSONArray()
                .put(new JSONObject().put("identity","tmdb:tv:999:2:4").put("position",4000).put("duration",12000))));return result;
    }
    private PortablePhysicalReferences physical() {
        Movie video=mock(Movie.class);when(video.getId()).thenReturn(10L);when(video.getOnlineId()).thenReturn(123L);
        when(video.getFileUri()).thenReturn(Uri.parse("file:///media/PATH_CANARY.mkv"));return new PortablePhysicalReferences(app,List.of(video));
    }
    @Before public void setup()throws Exception {
        app=RuntimeEnvironment.getApplication();ViewingHistory.awaitWrites();app.deleteDatabase(ViewingHistory.DATABASE);
        prefs=PreferenceManager.getDefaultSharedPreferences(app);prefs.edit().clear().putInt("preview_accent41",7).putString("oauth_token","AUTH_CANARY").commit();
        health=app.getSharedPreferences(SupernovaLibraryPolicy.HEALTH,0);health.edit().clear().putBoolean("incorrect:10",false)
                .putBoolean("incorrect:99",true).putString("offline:source","CACHE_CANARY").commit();
        journal=new File(app.getFilesDir(),"supernova-restore/preferences-v1.json");journal.delete();transaction=new PreferenceRestoreTransaction(app);
        ViewingHistory.replaceRecovery(app,original(),()->false);
    }
    private void assertOriginal(String before)throws Exception {
        assertEquals(7,prefs.getInt("preview_accent41",0));assertFalse(health.getBoolean("incorrect:10",true));assertTrue(health.getBoolean("incorrect:99",false));
        assertEquals(before,ViewingHistory.captureRecovery(app).toString());assertEquals("AUTH_CANARY",prefs.getString("oauth_token",null));assertEquals("CACHE_CANARY",health.getString("offline:source",null));
    }
    @Test public void deathAtEveryUncompletedStoreBoundaryRecoversAllOriginalsOnFreshCoordinator()throws Exception {
        String before=ViewingHistory.captureRecovery(app).toString();
        for(var at:List.of(PreferenceRestoreTransaction.Checkpoint.JOURNAL_DURABLE,PreferenceRestoreTransaction.Checkpoint.DEFAULT_DURABLE,
                PreferenceRestoreTransaction.Checkpoint.HEALTH_DURABLE,PreferenceRestoreTransaction.Checkpoint.VIEWING_DURABLE,PreferenceRestoreTransaction.Checkpoint.PREFERENCES_DURABLE)) {
            var plan=transaction.prepare(incoming(true),null,physical());try{transaction.apply(plan,()->false,checkpoint->{if(checkpoint==at)throw new ProcessDeath();});fail();}catch(ProcessDeath expected){}
            String recovery=Files.readString(journal.toPath());assertFalse(recovery.contains("CANARY"));assertEquals(3,new JSONObject(recovery).getInt("schema"));
            assertEquals(PreferenceRestoreTransaction.Recovery.ROLLED_BACK,new PreferenceRestoreTransaction(app).recover());assertOriginal(before);assertFalse(journal.exists());
        }
    }
    @Test public void durableCompletionWinsOverLateCancellationAndRecoveryKeepsEveryNewStore()throws Exception {
        var cancel=new AtomicBoolean(false);try{transaction.apply(transaction.prepare(incoming(true),null,physical()),cancel::get,at->{
            if(at==PreferenceRestoreTransaction.Checkpoint.COMPLETION_DURABLE){cancel.set(true);throw new ProcessDeath();}
        });fail();}catch(ProcessDeath expected){}
        assertEquals(PreferenceRestoreTransaction.Recovery.COMPLETED,new PreferenceRestoreTransaction(app).recover());assertEquals(19,prefs.getInt("preview_accent41",0));assertTrue(health.getBoolean("incorrect:10",false));
        var records=ViewingHistory.read(app);assertTrue(records.get("tmdb:movie:456").completed);assertEquals(4000,records.get("tmdb:tv:999:2:4").position);assertEquals(12000,records.get("tmdb:tv:999:2:4").duration);assertEquals("AUTH_CANARY",prefs.getString("oauth_token",null));
    }
    @Test public void ioFailureAndCancellationAfterDatabaseCommitRollBackPreferencesHealthAndViewingTogether()throws Exception {
        String before=ViewingHistory.captureRecovery(app).toString();try{transaction.apply(transaction.prepare(incoming(true),null,physical()),()->false,
                at->{if(at==PreferenceRestoreTransaction.Checkpoint.VIEWING_DURABLE)throw new IOException("Injected database boundary failure");});fail();}catch(IOException expected){}assertOriginal(before);
        var cancel=new AtomicBoolean(false);try{transaction.apply(transaction.prepare(incoming(true),null,physical()),cancel::get,
                at->{if(at==PreferenceRestoreTransaction.Checkpoint.VIEWING_DURABLE)cancel.set(true);});fail();}catch(InterruptedIOException expected){}assertOriginal(before);assertFalse(journal.exists());
    }
    @Test public void staleViewingPreviewFailsBeforeAnyPreferenceWriteOrJournalCreation()throws Exception {
        var plan=transaction.prepare(incoming(true),null,physical());JSONObject changed=original();changed.getJSONArray("rows").getJSONObject(1).put("position",7777);
        ViewingHistory.replaceRecovery(app,changed,()->false);String later=ViewingHistory.captureRecovery(app).toString();
        try{transaction.apply(plan,()->false);fail();}catch(IOException expected){}assertEquals(7,prefs.getInt("preview_accent41",0));assertEquals(later,ViewingHistory.captureRecovery(app).toString());assertFalse(journal.exists());
    }
    @Test public void corruptThirdStoreRecoveryIsRejectedBeforeAnyStoreIsWrittenAndEvidenceIsRetained()throws Exception {
        try{transaction.apply(transaction.prepare(incoming(true),null,physical()),()->false,at->{if(at==PreferenceRestoreTransaction.Checkpoint.VIEWING_DURABLE)throw new ProcessDeath();});fail();}catch(ProcessDeath expected){}
        String current=ViewingHistory.captureRecovery(app).toString();JSONObject record=new JSONObject(Files.readString(journal.toPath()));
        record.getJSONObject("viewing").getJSONObject("before").getJSONArray("rows").getJSONObject(0).put("credentials","INJECTED_CANARY");Files.writeString(journal.toPath(),record.toString());
        try{new PreferenceRestoreTransaction(app).recover();fail();}catch(IOException expected){}assertTrue(journal.exists());assertEquals(19,prefs.getInt("preview_accent41",0));assertTrue(health.getBoolean("incorrect:10",false));assertEquals(current,ViewingHistory.captureRecovery(app).toString());
    }
    @Test public void historyOnlyRollbackPreservesLaterUnselectedPositionsAndRestoresNegativeCompletionMarker()throws Exception {
        try{transaction.apply(transaction.prepare(incoming(false),null,physical()),()->false,at->{if(at==PreferenceRestoreTransaction.Checkpoint.VIEWING_DURABLE)throw new ProcessDeath();});fail();}catch(ProcessDeath expected){}
        JSONObject later=ViewingHistory.captureRecovery(app);for(int i=0;i<later.getJSONArray("rows").length();i++){
            JSONObject row=later.getJSONArray("rows").getJSONObject(i);if(row.getString("identity").equals("tmdb:tv:999:2:4"))row.put("position",7777);
        }ViewingHistory.replaceRecovery(app,later,()->false);new PreferenceRestoreTransaction(app).recover();
        var rows=ViewingHistory.read(app);assertTrue(rows.get("tmdb:movie:123").completed);assertEquals(-2,rows.get("tmdb:movie:123").position);assertEquals(7777,rows.get("tmdb:tv:999:2:4").position);assertEquals(50,rows.get("tmdb:tv:999:2:4").played);
    }
    @Test public void cancelledRestoreOnInitiallyAbsentViewingStoreDoesNotLeaveNewDatabaseBehind()throws Exception {
        app.deleteDatabase(ViewingHistory.DATABASE);var plan=transaction.prepare(incoming(true),null,physical());assertFalse(app.getDatabasePath(ViewingHistory.DATABASE).exists());
        try{transaction.apply(plan,()->false,at->{if(at==PreferenceRestoreTransaction.Checkpoint.VIEWING_DURABLE)throw new ProcessDeath();});fail();}catch(ProcessDeath expected){}
        assertTrue(app.getDatabasePath(ViewingHistory.DATABASE).exists());new PreferenceRestoreTransaction(app).recover();assertFalse(app.getDatabasePath(ViewingHistory.DATABASE).exists());assertEquals(7,prefs.getInt("preview_accent41",0));
    }
}
