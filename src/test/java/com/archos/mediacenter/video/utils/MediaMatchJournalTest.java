package com.archos.mediacenter.video.utils;

import android.app.Application;
import com.archos.mediacenter.video.utils.backup.*;
import com.archos.mediacenter.video.leanback.ViewingHistory;
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

@RunWith(RobolectricTestRunner.class) @Config(application=Application.class,sdk=28) @SQLiteMode(SQLiteMode.Mode.NATIVE)
public class MediaMatchJournalTest {
    private final CombinedViewingRestoreTest fixture=new CombinedViewingRestoreTest();
    private static class ProcessDeath extends Error {}
    @Before public void setup()throws Exception{fixture.setup();fixture.app.deleteDatabase("media.db");try(VideoOpenHelper helper=new VideoOpenHelper(fixture.app)){var db=helper.getWritableDatabase();db.execSQL("INSERT INTO files(_id,remote_id,media_type,_data,ArchosMediaScraper_type,ArchosMediaScraper_id,bookmark,Archos_lastTimePlayed,Archos_traktSeen) VALUES(10,10,3,'file:///media/PATH_CANARY.mkv',11,100,4444,100,1)");db.execSQL("INSERT INTO MOVIE(_id,video_id,m_online_id,Nova_pinned,name_movie,plot_movie) VALUES(100,10,123,9,'Original','Original facts')");}}
    private JSONObject match()throws Exception{return new JSONObject().put("schema",1).put("rows",new JSONArray().put(new JSONObject().put("physical",fixture.physical().reference(10)).put("identity","tmdb:movie:789").put("episodeProvider",JSONObject.NULL)));}
    private Map<BackupCategory,JSONObject> all()throws Exception{var result=fixture.incoming(true);result.put(BackupCategory.MATCHES,match());return result;}
    private String nativeState()throws Exception{try(VideoOpenHelper helper=new VideoOpenHelper(fixture.app);var row=helper.getReadableDatabase().rawQuery("SELECT f._data,f.ArchosMediaScraper_type,f.ArchosMediaScraper_id,f.bookmark,f.Archos_lastTimePlayed,f.Archos_traktSeen,m.Nova_pinned,m.name_movie,m.plot_movie FROM files f JOIN MOVIE m ON m.video_id=f._id WHERE f._id=10",null)){assertTrue(row.moveToFirst());JSONArray values=new JSONArray();for(int i=0;i<row.getColumnCount();i++)values.put(row.getType(i)==android.database.Cursor.FIELD_TYPE_STRING?row.getString(i):row.getLong(i));return values.toString();}}
    @Test public void everyPreDecisionDeathRollsBackGuardedIntentWithDefaultHealthAndViewingAndLeavesNativeFactsExact()throws Exception{String nativeBefore=nativeState(),viewing=ViewingHistory.captureRecovery(fixture.app).toString();var prefs=fixture.prefs.getAll();for(var at:List.of(PreferenceRestoreTransaction.Checkpoint.JOURNAL_DURABLE,PreferenceRestoreTransaction.Checkpoint.DEFAULT_DURABLE,PreferenceRestoreTransaction.Checkpoint.HEALTH_DURABLE,PreferenceRestoreTransaction.Checkpoint.VIEWING_DURABLE,PreferenceRestoreTransaction.Checkpoint.PREFERENCES_DURABLE)){try{fixture.transaction.apply(fixture.transaction.prepare(all(),null,fixture.physical()),()->false,point->{if(point==at)throw new ProcessDeath();});fail();}catch(ProcessDeath expected){}assertFalse(Files.readString(fixture.journal.toPath()).contains("CANARY"));assertEquals(PreferenceRestoreTransaction.Recovery.ROLLED_BACK,new PreferenceRestoreTransaction(fixture.app).recover());fixture.assertOriginal(viewing);assertEquals(prefs,fixture.prefs.getAll());assertEquals(nativeBefore,nativeState());}}
    @Test public void durableIntentDecisionKeepsCorrectionPendingWithoutClaimingCanonicalNativeApplication()throws Exception{String before=nativeState();try{fixture.transaction.apply(fixture.transaction.prepare(all(),null,fixture.physical()),()->false,at->{if(at==PreferenceRestoreTransaction.Checkpoint.COMMIT_MARKER_DURABLE)throw new ProcessDeath();});fail();}catch(ProcessDeath expected){}PreferenceRestoreTransaction.recoverAtStartup(fixture.app);assertEquals(before,nativeState());assertTrue(fixture.prefs.contains(MediaMatchIntents.key(10)));assertTrue(new JSONObject(fixture.prefs.getString(MediaMatchIntents.key(10),null)).getBoolean("pending"));assertEquals("AUTH_CANARY",fixture.prefs.getString("oauth_token",null));assertEquals(19,fixture.prefs.getInt("preview_accent41",0));assertTrue(ViewingHistory.read(fixture.app).get("tmdb:movie:456").completed);assertFalse(fixture.journal.exists());}
    @Test public void laterNativeIdentificationInvalidatesPreviewBeforeAnyJournalOrPreferenceWrite()throws Exception{var plan=fixture.transaction.prepare(all(),null,fixture.physical());try(VideoOpenHelper helper=new VideoOpenHelper(fixture.app)){helper.getWritableDatabase().execSQL("UPDATE MOVIE SET m_online_id=999 WHERE _id=100");}String before=nativeState();try{fixture.transaction.apply(plan,()->false);fail();}catch(IOException expected){}assertEquals(before,nativeState());assertEquals(7,fixture.prefs.getInt("preview_accent41",0));assertFalse(fixture.prefs.contains(MediaMatchIntents.key(10)));assertFalse(fixture.journal.exists());}
}
