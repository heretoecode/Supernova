package com.archos.mediacenter.video.leanback;
import android.app.Application;
import com.archos.mediacenter.video.browser.adapters.object.*;
import com.archos.mediacenter.video.leanback.PreviewLibraryLoader.*;
import org.junit.*;
import org.junit.runner.RunWith;
import org.robolectric.*;
import org.robolectric.annotation.Config;
import java.util.*;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;
@RunWith(RobolectricTestRunner.class) @Config(application=Application.class,sdk=28)
public class CustomLibraryPageTest {
 private Entry movie(long id,String name,int year,String genres,boolean watched){Movie video=mock(Movie.class);when(video.getId()).thenReturn(id);when(video.getName()).thenReturn(name);when(video.getYear()).thenReturn(year);when(video.isWatched()).thenReturn(watched);when(video.getRating()).thenReturn(8f);return new Entry(video,id,0,genres);}
 @Test public void oneSlotSurvivesRenameAndDeleteNeverChangesMedia(){Application c=RuntimeEnvironment.getApplication();CustomLibraryPage page=new CustomLibraryPage();page.name="Films";assertTrue(page.save(c));page.name="My Films";assertEquals("Films",CustomLibraryPage.load(c).name);assertTrue(page.save(c));assertEquals("My Films",CustomLibraryPage.load(c).name);assertTrue(CustomLibraryPage.delete(c));assertNull(CustomLibraryPage.load(c));}
 @Test public void genreAnyAllAndWatchedFiltersUseActualLibraryData(){Snapshot library=new Snapshot();library.movies.add(movie(1,"One",2000,"Drama|Comedy",false));library.movies.add(movie(2,"Two",2001,"Drama",true));CustomLibraryPage page=new CustomLibraryPage();page.genres.addAll(Set.of("Drama","Comedy"));assertEquals(2,page.entries(library).size());page.allGenres=true;assertEquals("One",page.entries(library).get(0).media.getName());page.allGenres=false;page.watched="watched";assertEquals("Two",page.entries(library).get(0).media.getName());page.year=2000;assertTrue(page.entries(library).isEmpty());}
 @Test public void tvWatchedFilterRequiresTheCompleteSeriesRatherThanOneCompletedEpisode(){
  Snapshot library=new Snapshot();
  Entry partial=new Entry(new Tvshow(10,"Partly watched",null,1,3,1,"/tv/partial"),1,10,"Drama");
  Entry complete=new Entry(new Tvshow(11,"Completed",null,1,3,3,"/tv/complete"),1,11,"Drama");
  Entry empty=new Entry(new Tvshow(12,"No indexed episodes",null,0,0,0,"/tv/empty"),1,12,"Drama");
  library.shows.addAll(Arrays.asList(partial,complete,empty));
  Episode episode=new Episode(30,30,1,1,"Episode 1",0,0,"","","Partly watched","/tv/partial/1",null,null,100000,0,0,0,true,false,false,false,1,100,1920,1080,null,null,null,null,0,1,1000);
  library.watched.add(new Entry(episode,1,10,"Drama"));
  library.continuingShows.add(new Entry(episode,1,10,"Drama"));
  CustomLibraryPage page=new CustomLibraryPage();page.type="tv";page.watched="watched";
  assertEquals(Collections.singletonList(complete),page.entries(library));
  page.watched="unwatched";assertEquals(2,page.entries(library).size());assertTrue(page.entries(library).contains(partial));assertTrue(page.entries(library).contains(empty));
  page.watched="progress";assertEquals(Collections.singletonList(partial),page.entries(library));
 }
 @Test public void serializedDraftPreservesBackwardStepEditsAndValidation(){CustomLibraryPage page=new CustomLibraryPage();page.name="Custom";page.type="movies";page.genres.add("Drama");page.view="list";page.descending=true;CustomLibraryPage restored=CustomLibraryPage.decode(page.encode());assertEquals(page.encode(),restored.encode());assertNull(restored.validation());restored.name=" ";assertNotNull(restored.validation());}
 @Test public void smartRowDynamicOffFreezesMembershipAndKeepsManualAdditions(){Application c=RuntimeEnvironment.getApplication();Snapshot library=new Snapshot();Entry one=movie(1,"One",2000,"Drama",false),two=movie(2,"Two",2001,"Comedy",false);library.movies.add(one);library.movies.add(two);PreviewHomeRows model=new PreviewHomeRows(c);PreviewHomeRows.Row row=new PreviewHomeRows.Row("custom:test","Test");model.rows.add(row);row.dynamic=true;row.genreRule="Drama";row.members.add(two.key());assertEquals(2,model.members(row,library).size());model.setDynamic(row,false,library);library.movies.add(movie(3,"Three",2002,"Drama",false));assertEquals(2,model.members(row,library).size());model.setDynamic(row,true,library);assertEquals(3,model.members(row,library).size());row.visible=false;assertEquals(3,model.members(row,library).size());}
 @Test public void customPageInitialArtworkComesFromDisplayedOrderRatherThanMovies(){
  Application c=RuntimeEnvironment.getApplication();androidx.preference.PreferenceManager.getDefaultSharedPreferences(c).edit().clear().putBoolean("supernova_onboarding_complete",true).commit();
  try{com.squareup.picasso.Picasso.get();}catch(IllegalStateException absent){com.squareup.picasso.Picasso.setSingletonInstance(new com.squareup.picasso.Picasso.Builder(c).build());}
  Snapshot library=new Snapshot();library.movies.add(movie(1,"Movie",2000,"Drama",false));
  Entry doc=movie(2,"Documentary",2001,"Documentary",false);library.movies.add(doc);
  CustomLibraryPage page=new CustomLibraryPage();page.name="Documentaries";page.type="movies";page.genres.add("Documentary");
  PreviewPages pages=new PreviewPages(c,(holder,item)->{});pages.configureCustomLibrary(page,()->{});pages.setSnapshot(library);
  assertSame(doc,org.robolectric.util.ReflectionHelpers.callInstanceMethod(pages,"featured"));
  page.genres.clear();page.genres.add("No matches");pages.configureCustomLibrary(page,()->{});
  assertNull(org.robolectric.util.ReflectionHelpers.callInstanceMethod(pages,"featured"));
 }
 @Test public void customTvStatisticsRebindWhenOnlyEpisodeStorageChanges(){
  Application c=RuntimeEnvironment.getApplication();androidx.preference.PreferenceManager.getDefaultSharedPreferences(c).edit().putBoolean("supernova_onboarding_complete",true).commit();
  Snapshot snapshot=new Snapshot();Entry show=new Entry(new Tvshow(10,"Documentary series",null,1,1,0,"/tv"),1,10,"Documentary");snapshot.shows.add(show);Episode episode=mock(Episode.class);when(episode.getId()).thenReturn(101L);when(episode.getName()).thenReturn("Episode");Entry file=new Entry(episode,1,10,"Documentary");snapshot.episodes.add(file);
  CustomLibraryPage page=new CustomLibraryPage();page.type="tv";page.name="Documentaries";PreviewPages pages=new PreviewPages(c,(holder,item)->{});pages.configureCustomLibrary(page,()->{});pages.setSnapshot(snapshot);
  androidx.recyclerview.widget.RecyclerView list=(androidx.recyclerview.widget.RecyclerView)pages.getChildAt(0);int[] changes={0};list.getAdapter().registerAdapterDataObserver(new androidx.recyclerview.widget.RecyclerView.AdapterDataObserver(){@Override public void onItemRangeChanged(int start,int count,Object payload){if(start==0)changes[0]++;}@Override public void onItemRangeChanged(int start,int count){if(start==0)changes[0]++;}});
  file.bytes=123456;file.knownSizes=1;pages.setSnapshot(snapshot);assertTrue("Statistics must refresh without recycling the header by scrolling",changes[0]>0);
 }

}
