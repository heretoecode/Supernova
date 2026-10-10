package com.archos.mediacenter.video.leanback;

import android.app.Application;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import androidx.preference.PreferenceManager;
import com.archos.mediacenter.utils.videodb.VideoDbInfo;
import org.junit.*;
import org.junit.runner.RunWith;
import org.robolectric.*;
import org.robolectric.annotation.*;
import java.io.File;
import java.util.*;
import static org.junit.Assert.*;

@RunWith(RobolectricTestRunner.class) @Config(application=Application.class,sdk=28)
@SQLiteMode(SQLiteMode.Mode.NATIVE)
public class ViewingHistoryTest {
 @org.junit.Before public void isolatePreviewTransport(){com.archos.mediacenter.video.leanback.PreviewAsyncFixtures.offlineTransport();}
 @org.junit.After public void drainPreviewWorkers() throws Exception { com.archos.mediacenter.video.leanback.PreviewAsyncFixtures.drain(); }
 private Context context;
 @Before public void setup()throws Exception{context=RuntimeEnvironment.getApplication();ViewingHistory.awaitWrites();context.deleteDatabase(ViewingHistory.DATABASE);PreferenceManager.getDefaultSharedPreferences(context).edit().clear().commit();}
 private VideoDbInfo episode(int number){VideoDbInfo info=new VideoDbInfo(Uri.parse("smb://private-nas/Show/ep"+number));info.isShow=true;info.scraperShowId="123";info.scraperSeasonNr=1;info.scraperEpisodeNr=number;info.duration=100000;info.resume=5000;info.lastTimePlayed=100;return info;}
 @Test public void identitySurvivesFileRemovalAndRestartWhileUnfinishedResumeIsIndependent()throws Exception{
  VideoDbInfo watched=episode(1);watched.resume=-2;ViewingHistory.record(context,watched,true);ViewingHistory.awaitWrites();
  assertTrue(ViewingHistory.read(context).get("tmdb:tv:123:1:1").completed);
  watched.uri=Uri.parse("file:///new-drive/different-name.mkv");assertEquals("tmdb:tv:123:1:1",ViewingHistory.identity(true,watched.scraperShowId,1,1,watched.uri.toString()));
  PreferenceManager.getDefaultSharedPreferences(context).edit().putBoolean(ViewingHistory.TRACK,false).commit();VideoDbInfo next=episode(2);ViewingHistory.record(context,next,false);ViewingHistory.awaitWrites();
  var record=ViewingHistory.read(context).get("tmdb:tv:123:1:2");assertFalse(record.completed);assertFalse(record.started);assertEquals(5000,record.position);assertTrue(ViewingHistory.read(context).get("tmdb:tv:123:1:1").completed);
  next.resume=-2;ViewingHistory.record(context,next,true);ViewingHistory.awaitWrites();record=ViewingHistory.read(context).get("tmdb:tv:123:1:2");assertFalse(record.completed);assertEquals(0,record.position);
 }
 @Test public void completedIdentityDoesNotDisappearWhenReplayedOrSourceUnavailable()throws Exception{
  var info=episode(1);info.resume=-2;ViewingHistory.record(context,info,true);ViewingHistory.awaitWrites();info.resume=3000;info.lastTimePlayed=200;ViewingHistory.record(context,info,false);ViewingHistory.awaitWrites();assertTrue(ViewingHistory.read(context).get("tmdb:tv:123:1:1").completed);assertEquals(100,ViewingHistory.read(context).get("tmdb:tv:123:1:1").completedAt);
 }
 @Test public void disablingBothTrackersWritesNothingAndReenablingDoesNotInferPastCompletion()throws Exception{
  var prefs=PreferenceManager.getDefaultSharedPreferences(context);prefs.edit().putBoolean(ViewingHistory.TRACK,false).putBoolean(ViewingHistory.RESUME,false).commit();ViewingHistory.record(context,episode(1),true);ViewingHistory.awaitWrites();assertTrue(ViewingHistory.read(context).isEmpty());
  var legacy=new PreviewPagesTest().episode(1,-2,true,100,1);legacy.onlineId=123;ViewingHistory.reconcile(context,List.of(legacy));prefs.edit().putBoolean(ViewingHistory.TRACK,true).commit();ViewingHistory.reconcile(context,List.of(legacy));assertTrue(ViewingHistory.read(context).isEmpty());
 }
 @Test public void clearBeforeMigrationRemovesLegacyCompletionButKeepsUnfinishedResume()throws Exception{
  ViewingHistory.clear(context,()->{});ViewingHistory.awaitWrites();
  var fixture=new PreviewPagesTest();var watched=fixture.episode(1,-2,true,100,1);watched.onlineId=123;
  var unfinished=fixture.episode(2,5000,false,100,1);unfinished.onlineId=123;
  ViewingHistory.reconcile(context,List.of(watched,unfinished));
  assertFalse(PreviewSeriesJourney.completed((com.archos.mediacenter.video.browser.adapters.object.Video)watched.media));
  assertEquals(0,watched.playedAt);assertEquals(5000,((com.archos.mediacenter.video.browser.adapters.object.Video)unfinished.media).getResumeMs());
  assertTrue(ViewingHistory.read(context).isEmpty());
 }
 @Test public void backupSelectionsAreIndependentOfTrackingAndNeverModifyLiveData()throws Exception{
  var watched=episode(1);watched.resume=-2;ViewingHistory.record(context,watched,true);ViewingHistory.record(context,episode(2),false);ViewingHistory.awaitWrites();var prefs=PreferenceManager.getDefaultSharedPreferences(context);prefs.edit().putBoolean(ViewingHistory.TRACK,false).commit();
  for(boolean history:new boolean[]{true,false})for(boolean resume:new boolean[]{true,false}){
   prefs.edit().putBoolean("supernova_backup_history",history).putBoolean("supernova_backup_resume",resume).commit();File file=new File(context.getCacheDir(),"history-"+history+resume);ViewingHistory.export(context,file);ViewingHistory.validate(file);
   try(SQLiteDatabase db=SQLiteDatabase.openDatabase(file.getPath(),null,SQLiteDatabase.OPEN_READONLY);Cursor rows=db.rawQuery("SELECT completed,started,position,played FROM history",null)){int count=0;while(rows.moveToNext()){count++;if(!history){assertEquals(0,rows.getInt(0));assertEquals(0,rows.getInt(1));assertEquals(0,rows.getLong(3));}if(!resume)assertEquals(0,rows.getInt(2));}assertEquals(history?2:resume?1:0,count);}
  }
  assertTrue(ViewingHistory.read(context).get("tmdb:tv:123:1:1").completed);assertEquals(5000,ViewingHistory.read(context).get("tmdb:tv:123:1:2").position);
 }
 @Test public void unknownHistoryFieldsAndCredentialTablesAreRejected()throws Exception{
  File file=new File(context.getCacheDir(),"hostile-history");ViewingHistory.export(context,file);try(SQLiteDatabase db=SQLiteDatabase.openDatabase(file.getPath(),null,SQLiteDatabase.OPEN_READWRITE)){db.execSQL("CREATE TABLE credentials(password TEXT)");}try{ViewingHistory.validate(file);fail("Unknown tables must be rejected before restore");}catch(java.io.IOException expected){}
 }
 @Test public void heroRequiresEveryPredecessorAndNewArrivalWithoutExpiryOrSeasonGuess(){
  assertTrue(HeroEligibility.sequential(1,3,Set.of("1:1","1:2"),101,100));assertFalse(HeroEligibility.sequential(1,3,Set.of("1:2"),101,100));assertFalse(HeroEligibility.sequential(2,1,Set.of("1:1"),101,100));assertFalse(HeroEligibility.sequential(1,3,Set.of("1:1","1:2"),99,100));assertTrue(HeroEligibility.unstarted(false,false,0));assertFalse(HeroEligibility.unstarted(false,true,0));assertFalse(HeroEligibility.unstarted(false,false,1));
  var fixture=new PreviewPagesTest();var previous=fixture.episode(1,-2,true,100,1);var next=fixture.episode(2,0,false,0,101);var snapshot=PreviewLibraryLoader.build(List.of(previous,next),List.of());assertTrue("Unstarted next episode belongs to hero, not Continue Watching",snapshot.continuingShows.isEmpty());((com.archos.mediacenter.video.browser.adapters.object.Video)next.media).applyIdentityHistory(false,102,2000);snapshot=PreviewLibraryLoader.build(List.of(previous,next),List.of());assertEquals(1,snapshot.continuingShows.size());
 }
}
