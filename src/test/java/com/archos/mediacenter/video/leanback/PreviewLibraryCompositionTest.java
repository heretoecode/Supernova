package com.archos.mediacenter.video.leanback;

import android.app.Application;
import com.archos.mediacenter.video.browser.adapters.object.*;
import com.archos.mediacenter.video.leanback.PreviewLibraryLoader.*;
import java.util.*;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.*;
import org.robolectric.annotation.Config;
import org.robolectric.util.ReflectionHelpers;
import static org.junit.Assert.*;

@RunWith(RobolectricTestRunner.class) @Config(application=Application.class,sdk=28)
public class PreviewLibraryCompositionTest {
 @Test public void moviesAndTvKeepSymmetricTypedContinueWatchingBeforeTheLibrary() {
  try{com.squareup.picasso.Picasso.get();}catch(IllegalStateException missing){com.squareup.picasso.Picasso.setSingletonInstance(new com.squareup.picasso.Picasso.Builder(RuntimeEnvironment.getApplication()).build());}
  var host=Robolectric.buildActivity(TopNavigationTest.Host.class).setup().visible();
  try {Movie movie=new Movie(7,"/Movies/film.mkv","Film",70,"Plot",2020,7,"PG",null,100000,5000,0,0,false,false,false,false,700,100,1920,1080,"AAC","h264","","",0,1,1000,0);
   Entry film=new Entry(movie,1,0,"Drama"),show=new Entry(new Episode(23,23,1,2,"Episode 2",0,0,"","","Series","/TV/Series/ep2.mkv",null,null,100000,5000,0,0,false,false,false,false,1,100,1920,1080,null,null,null,null,0,1,1000),1,23,"Drama");Snapshot source=new Snapshot();source.movies.add(film);source.shows.add(show);source.continuingMovies.addAll(Arrays.asList(film,show));source.continuingShows.addAll(Arrays.asList(show,film));
   PreviewPages pages=new PreviewPages(host.get(),(holder,item)->{});host.get().setContentView(pages);pages.setSnapshot(source);
   for(int tab:new int[]{1,2}){pages.setTab(tab);List<PreviewPages.Cell> cells=ReflectionHelpers.getField(pages,"cells");int row=-1,library=-1;List<Entry> entries=null;for(int n=0;n<cells.size();n++){PreviewPages.Cell cell=cells.get(n);if("Continue Watching".equals(cell.title)&&cell.value instanceof List){row=n;entries=(List<Entry>)cell.value;}if((tab==1?"Movie library":"TV show library").equals(cell.title))library=n;}assertTrue("Continue Watching appears above the library",row>=0&&library>row);assertNotNull(entries);assertEquals(1,entries.size());assertSame(tab==1?film:show,entries.get(0));}
   source.continuingMovies.clear();pages.setSnapshot(source);pages.setTab(1);List<PreviewPages.Cell> cells=ReflectionHelpers.getField(pages,"cells");assertFalse(cells.stream().anyMatch(cell->"Continue Watching".equals(cell.title)));
  } finally {host.pause().stop().destroy();}
 }
}
