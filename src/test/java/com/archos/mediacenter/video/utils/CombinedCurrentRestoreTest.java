package com.archos.mediacenter.video.utils;

import android.app.Application;
import com.archos.mediacenter.video.leanback.ViewingHistory;
import com.archos.mediacenter.video.utils.backup.*;
import com.archos.mediaprovider.video.VideoOpenHelper;
import com.archos.mediaprovider.video.VideoStore;
import java.util.*;
import org.json.*;
import org.junit.*;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.*;
import static org.junit.Assert.*;

/** Six-store recovery with title choices and both pending and canonical identification. */
@RunWith(RobolectricTestRunner.class) @Config(application=Application.class,sdk=28) @SQLiteMode(SQLiteMode.Mode.NATIVE)
public class CombinedCurrentRestoreTest {
    private final CombinedIndexedSourcesTest source=new CombinedIndexedSourcesTest();
    private CombinedViewingRestoreTest fixture;
    private static class ProcessDeath extends Error {}
    @Before public void setup()throws Exception{
        source.setup();fixture=source.fixture;
        try(VideoOpenHelper helper=new VideoOpenHelper(fixture.app)){helper.getWritableDatabase().execSQL("UPDATE files SET ArchosMediaScraper_type=11,ArchosMediaScraper_id=100,Archos_playerParams=1,Archos_playerSubtitleDelay=20,Archos_playerSubtitleRatio=1 WHERE _id=10");}
    }
    private Map<BackupCategory,JSONObject> all()throws Exception{
        Map<BackupCategory,JSONObject> result=source.all();
        result.put(BackupCategory.TITLE_PLAYBACK,PortableTitlePlayback.export(fixture.app,Map.of(10L,new PortableTitlePlayback.State(VideoStore.paramsFromTracks(0,5),321,2,null,true)),fixture.physical()));
        result.put(BackupCategory.MATCHES,new JSONObject().put("schema",1).put("rows",new JSONArray().put(new JSONObject().put("physical",fixture.physical().reference(10)).put("identity","tmdb:movie:789").put("episodeProvider",JSONObject.NULL))));return result;
    }
    private Map<String,Object> state()throws Exception{
        Map<String,Object> result=new LinkedHashMap<>();result.put("preferences",fixture.prefs.getAll());result.put("health",fixture.health.getAll());result.put("viewing",ViewingHistory.captureRecovery(fixture.app).toString());result.put("media",MediaStateRecovery.capture(fixture.app,true,true,true).toString());result.put("bookmarks",BookmarkStoreRecovery.capture(fixture.app).toString());result.put("sources",IndexedSourceStoreRecovery.capture(fixture.app).toString());result.put("identification",MediaMatchInventory.capture(fixture.app).signature);
        try(VideoOpenHelper helper=new VideoOpenHelper(fixture.app);var rows=helper.getReadableDatabase().rawQuery("SELECT _id,Archos_playerParams,Archos_playerSubtitleDelay,Archos_playerSubtitleRatio,Archos_lastTimePlayed,bookmark FROM files ORDER BY _id",null)){JSONArray data=new JSONArray();while(rows.moveToNext()){JSONArray row=new JSONArray();for(int i=0;i<rows.getColumnCount();i++)row.put(rows.isNull(i)?JSONObject.NULL:rows.getLong(i));data.put(row);}result.put("native playback",data.toString());}return result;
    }
    @Test public void currentCombinedIntentAndSixStoreRestoreRollsBackAtAllEightPreDecisionBoundaries()throws Exception{
        Map<String,Object> before=state();for(var boundary:List.of(PreferenceRestoreTransaction.Checkpoint.JOURNAL_DURABLE,PreferenceRestoreTransaction.Checkpoint.DEFAULT_DURABLE,PreferenceRestoreTransaction.Checkpoint.HEALTH_DURABLE,PreferenceRestoreTransaction.Checkpoint.VIEWING_DURABLE,PreferenceRestoreTransaction.Checkpoint.MEDIA_PREPARED,PreferenceRestoreTransaction.Checkpoint.BOOKMARKS_PREPARED,PreferenceRestoreTransaction.Checkpoint.SOURCES_PREPARED,PreferenceRestoreTransaction.Checkpoint.PREFERENCES_DURABLE)){
            try{fixture.transaction.apply(fixture.transaction.prepare(all(),null,fixture.physical()),()->false,at->{if(at==boundary)throw new ProcessDeath();});fail("boundary not reached: "+boundary);}catch(ProcessDeath expected){}
            assertEquals(boundary.toString(),PreferenceRestoreTransaction.Recovery.ROLLED_BACK,new PreferenceRestoreTransaction(fixture.app).recover());assertEquals(boundary.toString(),before,state());assertFalse(fixture.journal.exists());
        }
    }
    @Test public void decisionAndThreeAcknowledgementsRecoverCurrentCombinedRestoreWithoutCanonicalMatchOrNativePlaybackWrites()throws Exception{
        for(var boundary:List.of(PreferenceRestoreTransaction.Checkpoint.COMMIT_MARKER_DURABLE,PreferenceRestoreTransaction.Checkpoint.MEDIA_DURABLE,PreferenceRestoreTransaction.Checkpoint.BOOKMARKS_DURABLE,PreferenceRestoreTransaction.Checkpoint.SOURCES_DURABLE)){
            setup();Map<String,Object> before=state();try{fixture.transaction.apply(fixture.transaction.prepare(all(),null,fixture.physical()),()->false,at->{if(at==boundary)throw new ProcessDeath();});fail("boundary not reached: "+boundary);}catch(ProcessDeath expected){}
            PreferenceRestoreTransaction.recoverAtStartup(fixture.app);source.assertNew();assertTrue(fixture.prefs.contains(TitlePlaybackOverrides.key(10)));assertTrue(new JSONObject(fixture.prefs.getString(MediaMatchIntents.key(10),null)).getBoolean("pending"));assertEquals(before.get("identification"),state().get("identification"));assertEquals(before.get("native playback"),state().get("native playback"));assertFalse(fixture.journal.exists());assertEquals(PreferenceRestoreTransaction.Recovery.NONE,fixture.transaction.recover());
        }
    }
    private PreferenceRestoreTransaction.Plan canonicalPlan()throws Exception {
        return fixture.transaction.prepareCanonicalMatches(all(),null,fixture.physical(),(context,request,file)->{
            var tags=new com.archos.mediascraper.MovieTags();tags.setOnlineId(789);tags.setTitle("Canonical combined title");tags.setPlot("Canonical combined facts");tags.addGenreIfAbsent("Drama");
            return new com.archos.mediascraper.ScrapeDetailResult(tags,true,null,com.archos.mediascraper.ScrapeStatus.OKAY,null);
        },()->false);
    }
    private String canonicalFacts()throws Exception {
        try(VideoOpenHelper helper=new VideoOpenHelper(fixture.app);var rows=helper.getReadableDatabase().rawQuery("SELECT m_online_id,name_movie,plot_movie,m_genres,Nova_pinned FROM MOVIE WHERE _id=100",null)){
            assertTrue(rows.moveToFirst());JSONArray data=new JSONArray();for(int i=0;i<rows.getColumnCount();i++)data.put(rows.isNull(i)?JSONObject.NULL:rows.getString(i));return data.toString();
        }
    }
    @Test public void canonicalCombinedRestoreRollsBackEveryStoreAndScrapedFactsAtAllPreDecisionBoundaries()throws Exception {
        Map<String,Object> before=state();String facts=canonicalFacts();
        for(var boundary:List.of(PreferenceRestoreTransaction.Checkpoint.JOURNAL_DURABLE,PreferenceRestoreTransaction.Checkpoint.DEFAULT_DURABLE,PreferenceRestoreTransaction.Checkpoint.HEALTH_DURABLE,PreferenceRestoreTransaction.Checkpoint.VIEWING_DURABLE,PreferenceRestoreTransaction.Checkpoint.MEDIA_PREPARED,PreferenceRestoreTransaction.Checkpoint.BOOKMARKS_PREPARED,PreferenceRestoreTransaction.Checkpoint.SOURCES_PREPARED,PreferenceRestoreTransaction.Checkpoint.PREFERENCES_DURABLE)){
            try{fixture.transaction.apply(canonicalPlan(),()->false,at->{if(at==boundary)throw new ProcessDeath();});fail("boundary not reached: "+boundary);}catch(ProcessDeath expected){}
            assertEquals(PreferenceRestoreTransaction.Recovery.ROLLED_BACK,new PreferenceRestoreTransaction(fixture.app).recover());assertEquals(boundary.toString(),before,state());assertEquals(facts,canonicalFacts());assertFalse(fixture.journal.exists());
        }
    }
    @Test public void canonicalDecisionAndEveryNativeAcknowledgementRecoverAllStoresAndRemainIdempotent()throws Exception {
        for(var boundary:List.of(PreferenceRestoreTransaction.Checkpoint.COMMIT_MARKER_DURABLE,PreferenceRestoreTransaction.Checkpoint.MEDIA_DURABLE,PreferenceRestoreTransaction.Checkpoint.BOOKMARKS_DURABLE,PreferenceRestoreTransaction.Checkpoint.SOURCES_DURABLE)){
            setup();String playback=(String)state().get("native playback");
            try{fixture.transaction.apply(canonicalPlan(),()->false,at->{if(at==boundary)throw new ProcessDeath();});fail("boundary not reached: "+boundary);}catch(ProcessDeath expected){}
            PreferenceRestoreTransaction.recoverAtStartup(fixture.app);source.assertNew();assertEquals(playback,state().get("native playback"));
            assertEquals("[\"789\",\"Canonical combined title\",\"Canonical combined facts\",\"Drama\",\"0\"]",canonicalFacts());
            assertFalse(new JSONObject(fixture.prefs.getString(MediaMatchIntents.key(10),null)).getBoolean("pending"));assertTrue(fixture.prefs.contains(TitlePlaybackOverrides.key(10)));assertFalse(fixture.journal.exists());
            Map<String,Object> after=state();assertEquals(PreferenceRestoreTransaction.Recovery.NONE,new PreferenceRestoreTransaction(fixture.app).recover());assertEquals(after,state());
        }
    }

}
