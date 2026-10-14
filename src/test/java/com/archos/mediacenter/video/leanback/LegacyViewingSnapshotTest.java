package com.archos.mediacenter.video.leanback;

import android.app.Application;
import android.content.Context;
import android.net.Uri;
import androidx.preference.PreferenceManager;
import com.archos.mediacenter.video.browser.adapters.object.*;
import com.archos.mediacenter.video.leanback.PreviewLibraryLoader.*;
import com.archos.mediacenter.video.utils.backup.*;
import com.archos.mediaprovider.video.VideoOpenHelper;
import java.io.*;
import java.util.*;
import org.json.*;
import org.junit.*;
import org.junit.runner.RunWith;
import org.robolectric.*;
import org.robolectric.annotation.*;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith(RobolectricTestRunner.class) @Config(application=Application.class,sdk=28) @SQLiteMode(SQLiteMode.Mode.NATIVE)
public class LegacyViewingSnapshotTest {
    private Context app;
    @Before public void setup(){app=RuntimeEnvironment.getApplication();PreferenceManager.getDefaultSharedPreferences(app).edit().clear().putString("oauth_token","AUTH_CANARY").commit();app.deleteDatabase("media.db");app.deleteDatabase(ViewingHistory.DATABASE);}
    private Entry movie(long id,long provider,String uri,boolean watched,int position,int duration,long played){Movie video=mock(Movie.class);when(video.getId()).thenReturn(id);when(video.getOnlineId()).thenReturn(provider);when(video.getFileUri()).thenReturn(Uri.parse(uri));when(video.getFilePath()).thenReturn(uri);when(video.isWatched()).thenReturn(watched);when(video.getResumeMs()).thenReturn(position);when(video.getDurationMs()).thenReturn(duration);when(video.getLastPlayed()).thenReturn(played);Entry entry=new Entry(video,0,0,"");entry.onlineId=provider;return entry;}
    @Test public void legacyCompletionAndPositiveResumeStayIndependentAndNoCompletionTimeIsInvented()throws Exception{
        Snapshot snapshot=new Snapshot();snapshot.movies.add(movie(10,123,"file:///PRIVATE.mkv",true,4444,10000,1700000000));var result=LegacyViewingSnapshot.merge(app,Map.of(),snapshot);var record=result.get("tmdb:movie:123");assertTrue(record.completed);assertTrue(record.started);assertEquals(4444,record.position);assertEquals(10000,record.duration);assertEquals(1700000000000L,record.played);assertEquals(0,record.completedAt);String text=PortableViewingState.export(result,PortableViewingState.Kind.HISTORY).toString();assertFalse(text.contains("PRIVATE"));assertFalse(text.contains("AUTH_CANARY"));
    }
    @Test public void newestVariantProvidesOffsetWhileAnyActualWatchedStateRemainsPreserved()throws Exception{
        Snapshot snapshot=new Snapshot();snapshot.movies.add(movie(10,123,"file:///first.mkv",true,-2,10000,100));snapshot.movies.add(movie(11,123,"file:///second.mkv",false,4444,12000,200));var result=LegacyViewingSnapshot.merge(app,Map.of(),snapshot).get("tmdb:movie:123");assertTrue(result.completed);assertEquals(200000,result.played);assertEquals(4444,result.position);assertEquals(12000,result.duration);assertEquals(0,result.completedAt);
    }
    @Test public void structuredExplicitUnwatchedAndZeroPositionWinOverStaleLegacyData()throws Exception{
        Snapshot snapshot=new Snapshot();snapshot.movies.add(movie(10,123,"file:///first.mkv",true,-2,10000,100));var original=new ViewingHistory.Record(false,false,0,0,0,0);var result=LegacyViewingSnapshot.merge(app,Map.of("tmdb:movie:123",original),snapshot);assertSame(original,result.get("tmdb:movie:123"));assertEquals(0,PortableViewingState.export(result,PortableViewingState.Kind.HISTORY).getJSONArray("rows").length());
    }
    @Test public void existingHistoryClearAndPositionReplacementSuppressOnlyTheirLegacyDomains()throws Exception{
        Snapshot snapshot=new Snapshot();snapshot.movies.add(movie(10,123,"file:///first.mkv",true,4444,10000,100));var prefs=PreferenceManager.getDefaultSharedPreferences(app);prefs.edit().putBoolean("supernova_history_cleared",true).commit();var history=LegacyViewingSnapshot.merge(app,Map.of(),snapshot).get("tmdb:movie:123");assertFalse(history.completed);assertFalse(history.started);assertEquals(0,history.played);assertEquals(4444,history.position);
        prefs.edit().remove("supernova_history_cleared").putBoolean("supernova_positions_replaced_v2",true).commit();var position=LegacyViewingSnapshot.merge(app,Map.of(),snapshot).get("tmdb:movie:123");assertTrue(position.completed);assertEquals(100000,position.played);assertEquals(0,position.position);
    }
    @Test public void putioFileAssociationsAreExcludedAndUnmatchedLocalHistoryUsesOnlyOpaqueFingerprint()throws Exception{
        Snapshot snapshot=new Snapshot();snapshot.unmatched.add(movie(10,0,"putio:///ACCOUNT_CANARY/file",false,4444,10000,100));snapshot.unmatched.add(movie(11,0,"file:///PRIVATE_FILE.mkv",false,5555,10000,200));String excluded=ViewingHistory.identity(snapshot.unmatched.get(0));var original=new ViewingHistory.Record(false,true,4444,10000,100,0);var result=LegacyViewingSnapshot.merge(app,Map.of(excluded,original),snapshot);assertEquals(1,result.size());assertFalse(result.containsKey(excluded));assertTrue(result.keySet().iterator().next().matches("file:[a-f0-9]{64}"));String json=PortableViewingState.export(result,PortableViewingState.Kind.POSITIONS).toString();assertFalse(json.contains("PRIVATE_FILE"));assertFalse(json.contains("ACCOUNT_CANARY"));
    }
    @Test public void nativeSourceExportIncludesLegacyWatchedResumeAndTimeWithoutCreatingViewingStore()throws Exception{
        try(VideoOpenHelper helper=new VideoOpenHelper(app)){var db=helper.getWritableDatabase();db.execSQL("INSERT INTO files(_id,remote_id,media_type,_data,ArchosMediaScraper_type,ArchosMediaScraper_id,bookmark,duration,Archos_lastTimePlayed,Archos_traktSeen) VALUES(10,10,3,'file:///PRIVATE.mkv',11,100,4444,10000,1700000000,1)");db.execSQL("INSERT INTO MOVIE(_id,video_id,m_online_id,name_movie) VALUES(100,10,123,'PRIVATE_TITLE')");}
        File archive=LogicalBackupExporter.preparePrivate(app,Set.of(BackupCategory.HISTORY,BackupCategory.POSITIONS),()->false);var read=LogicalBackupArchive.readCategories(archive,()->false);JSONObject history=read.get(BackupCategory.HISTORY).getJSONArray("rows").getJSONObject(0);assertTrue(history.getBoolean("completed"));assertEquals(1700000000000L,history.getLong("played"));assertEquals(0,history.getLong("completedAt"));assertEquals(4444,read.get(BackupCategory.POSITIONS).getJSONArray("rows").getJSONObject(0).getInt("position"));assertFalse(app.getDatabasePath(ViewingHistory.DATABASE).exists());assertTrue(archive.delete());
    }
    @Test public void invalidOrOverflowingNativeTimesFailBeforeAnyViewingStateWrite()throws Exception{
        for(long played:List.of(-1L,Long.MAX_VALUE)){Snapshot snapshot=new Snapshot();snapshot.movies.add(movie(10,123,"file:///first.mkv",false,0,10000,played));try{LegacyViewingSnapshot.merge(app,Map.of(),snapshot);fail();}catch(JSONException expected){}assertFalse(app.getDatabasePath(ViewingHistory.DATABASE).exists());}
    }
}
