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
 @Test public void serializedDraftPreservesBackwardStepEditsAndValidation(){CustomLibraryPage page=new CustomLibraryPage();page.name="Custom";page.type="movies";page.genres.add("Drama");page.view="list";page.descending=true;CustomLibraryPage restored=CustomLibraryPage.decode(page.encode());assertEquals(page.encode(),restored.encode());assertNull(restored.validation());restored.name=" ";assertNotNull(restored.validation());}
 @Test public void smartRowDynamicOffFreezesMembershipAndKeepsManualAdditions(){Application c=RuntimeEnvironment.getApplication();Snapshot library=new Snapshot();Entry one=movie(1,"One",2000,"Drama",false),two=movie(2,"Two",2001,"Comedy",false);library.movies.add(one);library.movies.add(two);PreviewHomeRows model=new PreviewHomeRows(c);PreviewHomeRows.Row row=new PreviewHomeRows.Row("custom:test","Test");model.rows.add(row);row.dynamic=true;row.genreRule="Drama";row.members.add(two.key());assertEquals(2,model.members(row,library).size());model.setDynamic(row,false,library);library.movies.add(movie(3,"Three",2002,"Drama",false));assertEquals(2,model.members(row,library).size());model.setDynamic(row,true,library);assertEquals(3,model.members(row,library).size());row.visible=false;assertEquals(3,model.members(row,library).size());}
}
