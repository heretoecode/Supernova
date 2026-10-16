package com.archos.mediacenter.video.leanback;

import android.app.Application;
import android.net.Uri;
import com.archos.mediacenter.video.browser.adapters.object.*;
import com.archos.mediacenter.video.leanback.PreviewLibraryLoader.*;
import java.util.*;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith(RobolectricTestRunner.class) @Config(application=Application.class,sdk=28)
public class LibraryStatisticsTest {
    private final long now=java.time.Instant.parse("2026-10-16T18:00:00Z").toEpochMilli();
    private Entry movie(long file,long title,long added,String uri,long bytes,int resume,boolean watched){Movie video=mock(Movie.class);when(video.getId()).thenReturn(file);when(video.getOnlineId()).thenReturn(title);when(video.getFileUri()).thenReturn(Uri.parse(uri));when(video.getSize()).thenReturn(bytes);when(video.getResumeMs()).thenReturn(resume);when(video.isWatched()).thenReturn(watched);return new Entry(video,added,0,"");}
    private LibraryStatistics calculate(Snapshot s,List<Entry> selected){return LibraryStatistics.calculate(s,selected,now,TimeZone.getTimeZone("UTC"));}
    @Test public void scopedLogicalCountsAndPhysicalStorageIncludeAllVariantsOnce(){Snapshot s=new Snapshot();Entry local=movie(1,10,now/1000-86400,"file:///one.mkv",100,0,false),remote=movie(2,10,now/1000-86400,"webdavs://host/one.mkv",200,0,false),outside=movie(3,11,1,"file:///two.mkv",300,0,false);s.technical.addAll(List.of(local,remote,remote,outside));LibraryStatistics result=calculate(s,List.of(local));assertEquals(1,result.total);assertEquals(1,result.localTitles);assertEquals(1,result.networkTitles);assertEquals(100,result.localBytes);assertEquals(200,result.networkBytes);assertEquals(1,result.unwatched);assertEquals(1,result.addedThisMonth);}
    @Test public void completedAndWatchedTitlesAreNeverUnwatchedWithoutResume(){Snapshot s=new Snapshot();Entry completed=movie(1,10,1,"file:///one",1,com.archos.mediacenter.video.player.PlayerActivity.LAST_POSITION_END,false),watched=movie(2,11,1,"file:///two",1,0,true),resume=movie(3,12,1,"file:///three",1,90000,false);s.technical.addAll(List.of(completed,watched,resume));LibraryStatistics result=calculate(s,s.technical);assertEquals(0,result.unwatched);assertEquals(1,result.inProgress);}
    @Test public void oneCompletedEpisodeDoesNotCompleteOrMakeAnEntireSeriesUnwatched(){Snapshot s=new Snapshot();Entry show=new Entry(new Tvshow(7,"Series",null,1,2,1,"/series"),1,7,"");s.shows.add(show);for(int n=1;n<=2;n++){Episode episode=mock(Episode.class);when(episode.getId()).thenReturn((long)n);when(episode.getSeasonNumber()).thenReturn(1);when(episode.getEpisodeNumber()).thenReturn(n);when(episode.getResumeMs()).thenReturn(n==1?com.archos.mediacenter.video.player.PlayerActivity.LAST_POSITION_END:30000);when(episode.getFileUri()).thenReturn(Uri.parse("file:///series/"+n));s.technical.add(new Entry(episode,1,7,""));}LibraryStatistics result=calculate(s,s.shows);assertEquals(1,result.total);assertEquals(0,result.unwatched);assertEquals(1,result.inProgress);}
    @Test public void completedVariantSuppressesStaleResumeEvenWithOtherUnstartedEpisodes(){Snapshot s=new Snapshot();Entry show=new Entry(new Tvshow(7,"Series",null,1,2,1,"/series"),1,7,"");s.shows.add(show);for(int n=1;n<=3;n++){Episode episode=mock(Episode.class);when(episode.getId()).thenReturn((long)n);when(episode.getSeasonNumber()).thenReturn(1);when(episode.getEpisodeNumber()).thenReturn(n==3?2:1);when(episode.getResumeMs()).thenReturn(n==1?com.archos.mediacenter.video.player.PlayerActivity.LAST_POSITION_END:n==2?30000:0);when(episode.getFileUri()).thenReturn(Uri.parse("file:///series/"+n));s.technical.add(new Entry(episode,1,7,""));}LibraryStatistics result=calculate(s,s.shows);assertEquals(0,result.inProgress);assertEquals(0,result.unwatched);}
    @Test public void calendarMonthUsesLocalBoundaryAndExcludesFutureAndUnknownDates(){Snapshot s=new Snapshot();long boundary=java.time.Instant.parse("2026-10-01T00:00:00Z").getEpochSecond();s.technical.addAll(List.of(movie(1,1,boundary,"file:///1",0,0,false),movie(2,2,boundary-1,"file:///2",0,0,false),movie(3,3,now/1000+1,"file:///3",0,0,false),movie(4,4,0,"file:///4",0,0,false)));LibraryStatistics result=calculate(s,s.technical);assertEquals(1,result.addedThisMonth);assertEquals(4,result.unknownLocal);assertEquals("0% of library",calculate(new Snapshot(),List.of()).share(0));}
}
