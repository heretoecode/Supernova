package com.archos.mediacenter.video.leanback;

import android.app.*;
import android.view.*;
import android.widget.*;
import com.archos.mediacenter.video.leanback.search.PreviewSearchText;
import java.util.*;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.*;
import org.robolectric.annotation.Config;
import static org.junit.Assert.*;

@RunWith(RobolectricTestRunner.class)
@Config(application=Application.class,sdk=28,qualifiers="w960dp-h540dp-land-mdpi")
public class PreviewCorrectiveAuthorityTest {
 @Test public void forgivingTitlesMatchWithoutLosingExactRanking(){
  assertTrue(PreviewSearchText.matches("OC","The O.C."));assertTrue(PreviewSearchText.matches("dark knight","The Dark Knight"));assertTrue(PreviewSearchText.matches("schitts creek","Schitt’s Creek"));
  assertTrue(PreviewSearchText.matches("spider man","Spider-Man"));assertTrue(PreviewSearchText.matches("amelie","Amélie"));
  assertTrue(PreviewSearchText.rank("The Dark Knight","The Dark Knight")<PreviewSearchText.rank("The Dark Knight","The Dark Knight Rises"));
 }
 @Test public void matchHardEdgesNeverTransientlyEnterInputOrResults(){
  Activity activity=Robolectric.buildActivity(Activity.class).setup().get();PreviewMatchSearch page=new PreviewMatchSearch(activity,"",q->{},t->{});activity.setContentView(page);PreviewPagesTest.layout(page);
  View input=page.findViewWithTag("semantic:match.query");assertFalse(input.isFocusable());
  List<View> transitions=new ArrayList<>();page.getViewTreeObserver().addOnGlobalFocusChangeListener((old,next)->transitions.add(next));
  for(String key:new String[]{"1","Q","Clear","Space","Backspace"}){
   View view=page.findViewWithTag("semantic:keyboard:"+key);assertTrue(view.requestFocus());transitions.clear();int direction=key.equals("1")?KeyEvent.KEYCODE_DPAD_UP:key.equals("Q")?KeyEvent.KEYCODE_DPAD_LEFT:KeyEvent.KEYCODE_DPAD_DOWN;
   page.dispatchKeyEvent(new KeyEvent(KeyEvent.ACTION_DOWN,direction));page.dispatchKeyEvent(new KeyEvent(KeyEvent.ACTION_UP,direction));assertSame(view,page.findFocus());assertTrue("No corrective focus hop",transitions.isEmpty());
  }activity.finish();
 }
 @Test public void filterSummaryHasBoundedFootprintAcrossManySelections(){
  assertEquals("Filters",PreviewPages.filterSummary("","",""));assertEquals("Filters: Crime",PreviewPages.filterSummary("Crime","",""));
  assertEquals("Filters (5)",PreviewPages.filterSummary("Crime|Drama","2020|2021","8"));
 }
 @Test public void liveGenresUpdateBehindOpenPickerAndAllGenresClears(){
  Activity activity=Robolectric.buildActivity(Activity.class).setup().get();List<Set<String>> changes=new ArrayList<>();Dialog menu=PreviewGenres.chooseLive(activity,Arrays.asList("Crime","Drama"),Collections.emptySet(),changes::add);
  View crime=menu.getWindow().getDecorView().findViewWithTag("preview-label:1");((View)crime.getParent()).performClick();assertTrue(menu.isShowing());assertEquals(Collections.singleton("Crime"),changes.get(0));
  View all=menu.getWindow().getDecorView().findViewWithTag("preview-label:0");((View)all.getParent()).performClick();assertTrue(menu.isShowing());assertTrue(changes.get(1).isEmpty());assertNull(PreviewPagesTest.findText(menu.getWindow().getDecorView(),"Done"));menu.dismiss();activity.finish();
 }
}
