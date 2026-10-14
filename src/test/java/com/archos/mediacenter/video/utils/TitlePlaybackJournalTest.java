package com.archos.mediacenter.video.utils;

import android.app.Application;
import com.archos.mediacenter.video.utils.backup.*;
import com.archos.mediacenter.video.leanback.ViewingHistory;
import com.archos.mediaprovider.video.VideoOpenHelper;
import com.archos.mediaprovider.video.VideoStore;
import java.io.*;
import java.util.*;
import org.json.*;
import org.junit.*;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.*;
import static org.junit.Assert.*;

@RunWith(RobolectricTestRunner.class) @Config(application=Application.class,sdk=28) @SQLiteMode(SQLiteMode.Mode.NATIVE)
public class TitlePlaybackJournalTest {
    private final CombinedViewingRestoreTest fixture=new CombinedViewingRestoreTest();
    private static class ProcessDeath extends Error {}
    @Before public void setup()throws Exception{fixture.setup();fixture.app.deleteDatabase("media.db");try(VideoOpenHelper helper=new VideoOpenHelper(fixture.app)){var db=helper.getWritableDatabase();db.execSQL("INSERT INTO files(_id,remote_id,media_type,_data,Archos_playerParams,Archos_playerSubtitleDelay,Archos_playerSubtitleRatio,Archos_lastTimePlayed,bookmark) VALUES(10,10,3,'file:///media/PATH_CANARY.mkv',1,20,1,0,4444),(11,11,3,'putio:///ACCOUNT_CANARY/file',2,30,0,100,5555)");db.execSQL("INSERT INTO MOVIE(_id,video_id,m_online_id) VALUES(100,10,123)");}}
    private JSONObject payload()throws Exception{return PortableTitlePlayback.export(fixture.app,Map.of(10L,new PortableTitlePlayback.State(VideoStore.paramsFromTracks(0,5),321,2,null,true)),fixture.physical());}
    private Map<BackupCategory,JSONObject> all()throws Exception{var result=fixture.incoming(true);result.put(BackupCategory.TITLE_PLAYBACK,payload());return result;}
    private String nativeState()throws Exception{try(VideoOpenHelper helper=new VideoOpenHelper(fixture.app);var rows=helper.getReadableDatabase().rawQuery("SELECT _id,_data,Archos_playerParams,Archos_playerSubtitleDelay,Archos_playerSubtitleRatio,Archos_lastTimePlayed,bookmark FROM files ORDER BY _id",null)){JSONArray result=new JSONArray();while(rows.moveToNext()){JSONArray row=new JSONArray();for(int i=0;i<rows.getColumnCount();i++)row.put(rows.isNull(i)?JSONObject.NULL:rows.getType(i)==android.database.Cursor.FIELD_TYPE_STRING?rows.getString(i):rows.getLong(i));result.put(row);}return result.toString();}}
    @Test public void combinedDefaultHealthAndViewingRollbackRestoresOriginalTitleIntentAtEveryBoundary()throws Exception{
        String nativeBefore=nativeState(),viewing=ViewingHistory.captureRecovery(fixture.app).toString();Map<String,?> prefs=fixture.prefs.getAll();for(var boundary:List.of(PreferenceRestoreTransaction.Checkpoint.JOURNAL_DURABLE,PreferenceRestoreTransaction.Checkpoint.DEFAULT_DURABLE,PreferenceRestoreTransaction.Checkpoint.HEALTH_DURABLE,PreferenceRestoreTransaction.Checkpoint.VIEWING_DURABLE,PreferenceRestoreTransaction.Checkpoint.PREFERENCES_DURABLE)){
            try{fixture.transaction.apply(fixture.transaction.prepare(all(),null,fixture.physical()),()->false,at->{if(at==boundary)throw new ProcessDeath();});fail();}catch(ProcessDeath expected){}assertEquals(PreferenceRestoreTransaction.Recovery.ROLLED_BACK,new PreferenceRestoreTransaction(fixture.app).recover());fixture.assertOriginal(viewing);assertEquals(prefs,fixture.prefs.getAll());assertEquals(nativeBefore,nativeState());assertFalse(fixture.journal.exists());
        }
    }
    @Test public void durableDecisionKeepsExplicitZeroAudioAndNoneWithoutInventingNativePlayedTimestamp()throws Exception{
        String before=nativeState();try{fixture.transaction.apply(fixture.transaction.prepare(all(),null,fixture.physical()),()->false,at->{if(at==PreferenceRestoreTransaction.Checkpoint.COMMIT_MARKER_DURABLE)throw new ProcessDeath();});fail();}catch(ProcessDeath expected){}PreferenceRestoreTransaction.recoverAtStartup(fixture.app);assertEquals(before,nativeState());assertEquals(19,fixture.prefs.getInt("preview_accent41",0));assertTrue(fixture.health.getBoolean("incorrect:10",false));assertTrue(ViewingHistory.read(fixture.app).get("tmdb:movie:456").completed);assertTrue(fixture.prefs.contains(TitlePlaybackOverrides.key(10)));assertFalse(fixture.prefs.contains(TitlePlaybackOverrides.key(11)));assertEquals("AUTH_CANARY",fixture.prefs.getString("oauth_token",null));assertFalse(fixture.journal.exists());
    }
    @Test public void nativeTrackOrPathChangesInvalidatePreviewBeforeAnyPreferenceOrJournalWrite()throws Exception{
        var plan=fixture.transaction.prepare(all(),null,fixture.physical());try(VideoOpenHelper helper=new VideoOpenHelper(fixture.app)){helper.getWritableDatabase().execSQL("UPDATE files SET Archos_playerParams=2 WHERE _id=10");}String before=nativeState();try{fixture.transaction.apply(plan,()->false);fail();}catch(IOException expected){}assertEquals(before,nativeState());assertEquals(7,fixture.prefs.getInt("preview_accent41",0));assertFalse(fixture.journal.exists());
        plan=fixture.transaction.prepare(all(),null,fixture.physical());try(VideoOpenHelper helper=new VideoOpenHelper(fixture.app)){helper.getWritableDatabase().execSQL("UPDATE files SET _data='file:///moved.mkv' WHERE _id=10");}before=nativeState();try{fixture.transaction.apply(plan,()->false);fail();}catch(IOException expected){}assertEquals(before,nativeState());assertFalse(fixture.journal.exists());
    }
    @Test public void selectedEmptyClearsOverlayedChoicesWhileOmittedTitleCategoryLeavesIntentUntouched()throws Exception{
        fixture.transaction.apply(fixture.transaction.prepare(Map.of(BackupCategory.TITLE_PLAYBACK,payload()),null,fixture.physical()),()->false);String original=fixture.prefs.getString(TitlePlaybackOverrides.key(10),null),nativeBefore=nativeState();fixture.transaction.apply(fixture.transaction.prepare(Map.of(BackupCategory.APPEARANCE,all().get(BackupCategory.APPEARANCE)),null),()->false);assertEquals(original,fixture.prefs.getString(TitlePlaybackOverrides.key(10),null));
        fixture.transaction.apply(fixture.transaction.prepare(Map.of(BackupCategory.TITLE_PLAYBACK,new JSONObject().put("schema",2).put("rows",new JSONArray())),null,fixture.physical()),()->false);JSONObject exported=TitlePlaybackOverrides.export(fixture.app,TitlePlaybackInventory.capture(fixture.app).choices,fixture.physical());assertEquals(0,exported.getJSONArray("rows").length());assertEquals(nativeBefore,nativeState());assertFalse(fixture.prefs.contains(TitlePlaybackOverrides.key(11)));
    }
    @Test public void newDeviceKeepsExactPendingSchemaTwoWithoutCreatingNativeMediaStore()throws Exception{
        JSONObject payload=payload();fixture.app.deleteDatabase("media.db");var plan=fixture.transaction.prepare(Map.of(BackupCategory.TITLE_PLAYBACK,payload),null,new PortablePhysicalReferences(fixture.app,List.of()));assertEquals(1,plan.unresolved);fixture.transaction.apply(plan,()->false);assertFalse(VideoOpenHelper.getDatabaseFile(fixture.app).exists());assertEquals(payload.toString(),new JSONObject(fixture.prefs.getString(PortableTitlePlayback.PENDING,null)).toString());assertEquals("AUTH_CANARY",fixture.prefs.getString("oauth_token",null));
    }
    @Test public void cancelledTitleReplacementRecoversOriginalsAndPreservesInterruptedWorkerStatus()throws Exception{
        String nativeBefore=nativeState(),viewing=ViewingHistory.captureRecovery(fixture.app).toString();try{try{fixture.transaction.apply(fixture.transaction.prepare(all(),null,fixture.physical()),()->false,at->{if(at==PreferenceRestoreTransaction.Checkpoint.DEFAULT_DURABLE)Thread.currentThread().interrupt();});fail();}catch(PreferenceRestoreTransaction.Cancelled expected){assertEquals(PreferenceRestoreTransaction.Recovery.ROLLED_BACK,expected.outcome);assertFalse(expected.recoveryRequired);assertTrue(Thread.currentThread().isInterrupted());}}finally{Thread.interrupted();}fixture.assertOriginal(viewing);assertEquals(nativeBefore,nativeState());assertFalse(fixture.prefs.contains(TitlePlaybackOverrides.key(10)));assertFalse(fixture.journal.exists());
    }
}
