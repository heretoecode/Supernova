package com.archos.mediacenter.video.leanback;

import android.app.Application;
import android.app.Dialog;
import android.content.Context;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import androidx.preference.PreferenceManager;
import com.archos.mediacenter.video.leanback.PreviewLibraryLoader.Entry;
import com.archos.mediacenter.video.leanback.PreviewLibraryLoader.Snapshot;
import com.archos.mediacenter.video.browser.adapters.object.Movie;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.android.controller.ActivityController;
import org.robolectric.annotation.Config;
import org.robolectric.annotation.GraphicsMode;
import org.robolectric.shadows.ShadowDialog;
import java.util.Arrays;
import java.util.List;
import java.util.Collections;
import java.util.LinkedHashSet;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith(RobolectricTestRunner.class)
@Config(application=Application.class,sdk=28,qualifiers="w960dp-h540dp-land-mdpi")
public class Supernova139HomeEditorTest {
    private ActivityController<TopNavigationTest.Host> host() {
        ActivityController<TopNavigationTest.Host> host=Robolectric.buildActivity(TopNavigationTest.Host.class).setup();
        PreferenceManager.getDefaultSharedPreferences(host.get()).edit().clear().commit();return host;
    }
    private Dialog open(ActivityController<TopNavigationTest.Host> host){PreviewHomeRows.customise(host.get(),()->{});return ShadowDialog.getLatestDialog();}
    private View root(Dialog dialog){return dialog.getWindow().getDecorView();}
    private void key(Dialog dialog,int code){dialog.dispatchKeyEvent(new KeyEvent(KeyEvent.ACTION_DOWN,code));dialog.dispatchKeyEvent(new KeyEvent(KeyEvent.ACTION_UP,code));}
    private EditText input(View view){if(view instanceof EditText)return (EditText)view;if(view instanceof ViewGroup)for(int i=0;i<((ViewGroup)view).getChildCount();i++){EditText result=input(((ViewGroup)view).getChildAt(i));if(result!=null)return result;}return null;}
    private void createNamed(Dialog dialog,String name){root(dialog).findViewWithTag("home.editor.create").performClick();root(dialog).findViewWithTag("home.editor.name").performClick();Dialog keyboard=ShadowDialog.getLatestDialog();input(root(keyboard)).setText(name);PreviewPagesTest.findText(root(keyboard),"Done").performClick();}
    private void fullyVisible(View view){
        // Native fixtures lay out the real hierarchy explicitly; their ViewRoot window frame is
        // not an Android WindowManager/device measurement. Intersect measured ancestor viewports.
        android.graphics.Rect bounds=new android.graphics.Rect(0,0,view.getWidth(),view.getHeight());
        View cursor=view;
        while(cursor.getParent() instanceof View){View parent=(View)cursor.getParent();
            bounds.offset(cursor.getLeft()-parent.getScrollX()+Math.round(cursor.getTranslationX()),cursor.getTop()-parent.getScrollY()+Math.round(cursor.getTranslationY()));
            assertTrue("target inside "+parent.getClass().getSimpleName()+": "+view.getTag(),bounds.intersect(0,0,parent.getWidth(),parent.getHeight()));
            assertEquals("full target height: "+view.getTag(),view.getHeight(),bounds.height());
            assertEquals("full target width: "+view.getTag(),view.getWidth(),bounds.width());cursor=parent;
        }
    }
    private void layout(Dialog dialog){View view=root(dialog);int w=dialog.getWindow().getAttributes().width,h=dialog.getWindow().getAttributes().height;view.measure(View.MeasureSpec.makeMeasureSpec(w,View.MeasureSpec.EXACTLY),View.MeasureSpec.makeMeasureSpec(h,View.MeasureSpec.EXACTLY));view.layout(0,0,w,h);view.dispatchWindowFocusChanged(true);}

    @Test public void builtinIsLockedAndMoveCancellationPreservesPersistedOrder(){
        ActivityController<TopNavigationTest.Host> host=host();try{
            Dialog dialog=open(host);layout(dialog);
            assertNull(root(dialog).findViewWithTag("home.editor.name"));
            assertNotNull(PreviewPagesTest.findText(root(dialog),"Built-in Supernova row"));
            View next=root(dialog).findViewWithTag("watchnext");next.requestFocus();next.performClick();key(dialog,KeyEvent.KEYCODE_DPAD_UP);
            assertEquals("continue",new PreviewHomeRows(host.get()).rows.get(0).id);
            key(dialog,KeyEvent.KEYCODE_BACK);assertTrue(dialog.isShowing());
            assertEquals("continue",new PreviewHomeRows(host.get()).rows.get(0).id);
            assertEquals("watchnext",String.valueOf(dialog.getCurrentFocus().getTag()));
            dialog.dismiss();
        }finally{host.pause().stop().destroy();}
    }
    @Test public void unfinishedDraftNeverPersistsAndSafeDefaultAllowsContinuedEditing(){
        ActivityController<TopNavigationTest.Host> host=host();try{
            Dialog dialog=open(host);root(dialog).findViewWithTag("home.editor.create").performClick();
            assertEquals(7,new PreviewHomeRows(host.get()).rows.size());
            key(dialog,KeyEvent.KEYCODE_BACK);Dialog confirmation=ShadowDialog.getLatestDialog();
            assertEquals("home.confirm.safe",confirmation.getCurrentFocus().getTag());
            root(confirmation).findViewWithTag("home.confirm.safe").performClick();assertTrue(dialog.isShowing());
            root(dialog).findViewWithTag("home.editor.name").performClick();Dialog keyboard=ShadowDialog.getLatestDialog();
            assertEquals("",input(root(keyboard)).getText().toString());input(root(keyboard)).setText("Named row");
            PreviewPagesTest.findText(root(keyboard),"Done").performClick();
            PreviewHomeRows model=new PreviewHomeRows(host.get());assertEquals(8,model.rows.size());assertEquals("Named row",model.rows.get(7).name);assertEquals(15,model.rows.get(7).maximum);
            key(dialog,KeyEvent.KEYCODE_BACK);assertFalse(dialog.isShowing());
        }finally{host.pause().stop().destroy();}
    }
    @Test public void inlinePreviewCancelsAndDirectionCommitsIndependently(){
        ActivityController<TopNavigationTest.Host> host=host();try{
            Dialog dialog=open(host);createNamed(dialog,"Science");layout(dialog);
            View maximum=root(dialog).findViewWithTag("home.editor.maximum");maximum.requestFocus();maximum.performClick();key(dialog,KeyEvent.KEYCODE_DPAD_DOWN);
            assertEquals(15,new PreviewHomeRows(host.get()).rows.get(7).maximum);
            key(dialog,KeyEvent.KEYCODE_BACK);assertTrue(dialog.isShowing());assertEquals("15",((android.widget.TextView)maximum).getText().toString());
            maximum.performClick();key(dialog,KeyEvent.KEYCODE_DPAD_DOWN);maximum.performClick();assertEquals(20,new PreviewHomeRows(host.get()).rows.get(7).maximum);
            root(dialog).findViewWithTag("home.editor.direction").performClick();assertFalse(new PreviewHomeRows(host.get()).rows.get(7).descending);
            dialog.dismiss();
        }finally{host.pause().stop().destroy();}
    }
    @Test public void matchAllFilteringAndFrozenManualMembershipSurviveReload(){
        ActivityController<TopNavigationTest.Host> host=host();try{
            PreviewHomeRows model=new PreviewHomeRows(host.get());PreviewHomeRows.Row row=new PreviewHomeRows.Row("custom:filters","Filters");row.dynamic=true;row.matchAll=true;row.genreRule="Crime|Drama";row.icon="Science Fiction";model.rows.add(row);
            Snapshot snapshot=new Snapshot();Entry both=movie(1,"Crime|Drama"),crime=movie(2,"Crime"),manual=movie(3,"Comedy");snapshot.movies.addAll(Arrays.asList(both,crime,manual));row.members.add(manual.key());
            assertEquals(2,model.members(row,snapshot).size());assertEquals("Science Fiction",PreviewHomeRows.iconName(row));
            model.setDynamic(row,false,snapshot);snapshot.movies.add(movie(4,"Crime|Drama"));assertEquals(2,model.members(row,snapshot).size());
            PreviewHomeRows.Row restored=new PreviewHomeRows(host.get()).rows.get(7);assertTrue(restored.matchAll);assertEquals("Science Fiction",restored.icon);assertFalse(restored.dynamic);
            assertEquals(3,HomeGenrePicker.count(snapshot,row,"Crime"));
        }finally{host.pause().stop().destroy();}
    }
    private Entry movie(long id,String genres){Movie movie=mock(Movie.class);when(movie.getId()).thenReturn(id);when(movie.getName()).thenReturn("Fixture"+id);return new Entry(movie,id,0,genres);}
    @Test public void genreCountsDeduplicatePhysicalVersionsAndReuseOnlyUnchangedSnapshotAndMediaTypes(){
        PreviewHomeRows.Row row=new PreviewHomeRows.Row("custom:counts","Counts");Snapshot snapshot=new Snapshot();Entry first=movie(1,"Drama|Crime"),second=movie(2,"DRAMA|Comedy"),unmatched=movie(3,"Crime");first.onlineId=77;second.onlineId=77;snapshot.movies.addAll(List.of(first,second,unmatched));
        com.archos.mediacenter.video.browser.adapters.object.Tvshow show=mock(com.archos.mediacenter.video.browser.adapters.object.Tvshow.class);when(show.getTvshowId()).thenReturn(10L);Entry tv=new Entry(show,0,10,"Drama");tv.onlineId=77;snapshot.shows.add(tv);
        assertEquals(3,HomeGenrePicker.count(snapshot,row,null));assertEquals(2,HomeGenrePicker.count(snapshot,row,"Drama"));assertEquals(2,HomeGenrePicker.count(snapshot,row,"Crime"));assertEquals(1,HomeGenrePicker.count(snapshot,row,"Comedy"));assertEquals(0,HomeGenrePicker.count(snapshot,row,"Unknown"));
        HomeGenrePicker.CountCache cache=new HomeGenrePicker.CountCache();var counts=cache.get(snapshot,row);assertSame(counts,cache.get(snapshot,row));row.tv=false;var movies=cache.get(snapshot,row);assertNotSame(counts,movies);assertEquals(2,movies.get(null));assertEquals(1,movies.get("drama"));
        Snapshot changed=new Snapshot();changed.movies.addAll(snapshot.movies);changed.movies.add(movie(4,"Drama"));assertEquals(2,cache.get(changed,row).get("Drama"));assertNull(cache.get(null,row));assertNotNull(cache.get(changed,row));
    }
    @Test public void legacySavedUnlimitedRowRetainsLargestApprovedCountWhileNewRowsDefaultToFifteen()throws Exception{
        Context context=org.robolectric.RuntimeEnvironment.getApplication();androidx.preference.PreferenceManager.getDefaultSharedPreferences(context).edit().putString("preview_home_rows41","[{\"id\":\"custom:legacy\",\"name\":\"Legacy\",\"maximum\":0}]").commit();
        PreviewHomeRows model=new PreviewHomeRows(context);PreviewHomeRows.Row row=model.rows.stream().filter(value->value.id.equals("custom:legacy")).findFirst().get();assertEquals(30,row.maximum);assertEquals(15,new PreviewHomeRows.Row("custom:new","New").maximum);
        PreviewLibraryLoader.Snapshot library=new PreviewLibraryLoader.Snapshot();for(int id=1;id<=40;id++){com.archos.mediacenter.video.browser.adapters.object.Movie movie=org.mockito.Mockito.mock(com.archos.mediacenter.video.browser.adapters.object.Movie.class);org.mockito.Mockito.when(movie.getId()).thenReturn((long)id);org.mockito.Mockito.when(movie.getName()).thenReturn("Film "+id);PreviewLibraryLoader.Entry entry=new PreviewLibraryLoader.Entry(movie,id,0,"");library.movies.add(entry);row.members.add(entry.key());}
        assertEquals(30,model.members(row,library).size());model.save();assertEquals(30,new PreviewHomeRows(context).rows.stream().filter(value->value.id.equals(row.id)).findFirst().get().maximum);
    }
    @Test public void legacyLimitsAndCaseInsensitiveAllGenresAreSafe(){
        assertEquals(30,PreviewHomeRows.normaliseMaximum(0));assertEquals(5,PreviewHomeRows.normaliseMaximum(1));assertEquals(30,PreviewHomeRows.normaliseMaximum(100));
        assertTrue(PreviewGenres.matches("crime|DRAMA",new LinkedHashSet<>(Arrays.asList("Crime","Drama")),true));assertFalse(PreviewGenres.matches("Crime",new LinkedHashSet<>(Arrays.asList("Crime","Drama")),true));
        assertTrue(PreviewGenres.matches("",Collections.emptySet(),true));
    }
    @Test @GraphicsMode(GraphicsMode.Mode.NATIVE) public void captureActualBuiltinCustomAndGenreStates()throws Exception{
        ActivityController<TopNavigationTest.Host> host=host();try{
            Dialog dialog=open(host);layout(dialog);PreviewPagesTest.capture(root(dialog),"0139-customise-built-in");
            createNamed(dialog,"Science Fiction");layout(dialog);
            PreviewPagesTest.capture(root(dialog),"0139-customise-custom-calibration");fullyVisible(root(dialog).findViewWithTag("home.editor.automatic"));
            assertEquals("home.editor.name",dialog.getCurrentFocus().getTag());
            PreviewPagesTest.capture(root(dialog),"0139-customise-custom");
            root(dialog).findViewWithTag("home.editor.genres").performClick();Dialog genres=ShadowDialog.getLatestDialog();layout(genres);
            assertNotNull(root(genres).findViewWithTag("home.genre:Science Fiction"));
            root(genres).findViewWithTag("home.genre:Science Fiction").performClick();
            assertEquals("Science Fiction",new PreviewHomeRows(host.get()).rows.get(7).genreRule);
            assertNull(PreviewPagesTest.findText(root(genres),"Done"));PreviewPagesTest.capture(root(genres),"0139-customise-genres");genres.dismiss();dialog.dismiss();
        }finally{host.pause().stop().destroy();}
    }
    @Test @GraphicsMode(GraphicsMode.Mode.NATIVE) public void completeTwoGenreEditorFitsNormalViewportAndFocusMovesAcrossIndependentEye()throws Exception{
        ActivityController<TopNavigationTest.Host> host=host();try{
            PreviewHomeRows model=new PreviewHomeRows(host.get());PreviewHomeRows.Row row=new PreviewHomeRows.Row("custom:two","Science Fiction");row.genreRule="Science Fiction|Action";row.dynamic=true;model.rows.add(row);model.save();
            Dialog dialog=open(host);layout(dialog);View body=root(dialog).findViewWithTag(row.id);body.requestFocus();key(dialog,KeyEvent.KEYCODE_DPAD_RIGHT);
            assertEquals(row.id+":visibility",dialog.getCurrentFocus().getTag());key(dialog,KeyEvent.KEYCODE_DPAD_RIGHT);assertEquals("home.editor.name",dialog.getCurrentFocus().getTag());
            layout(dialog);PreviewPagesTest.capture(root(dialog),"0139-customise-two-genres-calibration");fullyVisible(root(dialog).findViewWithTag("home.editor.automatic"));fullyVisible(root(dialog).findViewWithTag("home.editor.maximum"));
            PreviewPagesTest.capture(root(dialog),"0139-customise-two-genres");dialog.dismiss();
        }finally{host.pause().stop().destroy();}
    }
    @Test @GraphicsMode(GraphicsMode.Mode.NATIVE) @Config(qualifiers="w640dp-h360dp-land-mdpi") public void smallViewportKeepsEditorAndDraftConfirmationReachable()throws Exception{
        ActivityController<TopNavigationTest.Host> host=host();try{
            Dialog dialog=open(host);layout(dialog);createNamed(dialog,"Small viewport");layout(dialog);
            assertTrue(root(dialog).findViewWithTag("home.editor.name").requestFocus());
            View direction=root(dialog).findViewWithTag("home.editor.direction");direction.requestFocus();org.robolectric.Shadows.shadowOf(android.os.Looper.getMainLooper()).idle();layout(dialog);fullyVisible(direction);
            android.widget.TextView label=(android.widget.TextView)direction;assertEquals("Descending",label.getText().toString());
            View automatic=root(dialog).findViewWithTag("home.editor.automatic");assertTrue(automatic.requestFocus());org.robolectric.Shadows.shadowOf(android.os.Looper.getMainLooper()).idle();layout(dialog);fullyVisible(automatic);
            PreviewPagesTest.capture(root(dialog),"0139-customise-small");dialog.dismiss();
        }finally{host.pause().stop().destroy();}
    }
    private void held(Dialog dialog,int code,int repeats){
        long started=android.os.SystemClock.uptimeMillis();for(int repeat=0;repeat<repeats;repeat++)dialog.dispatchKeyEvent(new KeyEvent(started,started+repeat*50,KeyEvent.ACTION_DOWN,code,repeat));
        dialog.dispatchKeyEvent(new KeyEvent(started,started+repeats*50,KeyEvent.ACTION_UP,code,0));
    }
    private long focusedTargets(View view){long count=view.isFocused()?1:0;if(view instanceof ViewGroup)for(int i=0;i<((ViewGroup)view).getChildCount();i++)count+=focusedTargets(((ViewGroup)view).getChildAt(i));return count;}
    @Test public void heldReorderKeysStayBoundedBackRollsBackAndPlacedHiddenRowPersistsOnReopen(){
        ActivityController<TopNavigationTest.Host> host=host();try{
            PreviewHomeRows original=new PreviewHomeRows(host.get());original.rows.get(1).visible=false;original.save();List<String> order=original.rows.stream().map(row->row.id).collect(java.util.stream.Collectors.toList());
            Dialog dialog=open(host);layout(dialog);View moving=root(dialog).findViewWithTag("watchnext");moving.requestFocus();moving.performClick();held(dialog,KeyEvent.KEYCODE_DPAD_DOWN,20);assertEquals("watchnext",dialog.getCurrentFocus().getTag());assertEquals(1,focusedTargets(root(dialog)));assertEquals(order,new PreviewHomeRows(host.get()).rows.stream().map(row->row.id).collect(java.util.stream.Collectors.toList()));key(dialog,KeyEvent.KEYCODE_BACK);assertEquals(order,new PreviewHomeRows(host.get()).rows.stream().map(row->row.id).collect(java.util.stream.Collectors.toList()));
            root(dialog).findViewWithTag("watchnext").performClick();held(dialog,KeyEvent.KEYCODE_DPAD_UP,20);assertEquals("watchnext",dialog.getCurrentFocus().getTag());dialog.getCurrentFocus().performClick();PreviewHomeRows placed=new PreviewHomeRows(host.get());assertEquals("watchnext",placed.rows.get(0).id);assertFalse(placed.rows.get(0).visible);assertEquals(7,placed.rows.size());dialog.dismiss();dialog=open(host);layout(dialog);assertEquals("watchnext",dialog.getCurrentFocus().getTag());assertEquals(1,focusedTargets(root(dialog)));dialog.dismiss();
        }finally{host.pause().stop().destroy();}
    }
    @Test @GraphicsMode(GraphicsMode.Mode.NATIVE) public void largeFontProfileKeepsIndependentEditorControlsReachableAndPersisted()throws Exception{
        ActivityController<TopNavigationTest.Host> host=host();try{
            android.content.res.Resources resources=host.get().getResources();android.content.res.Configuration configuration=new android.content.res.Configuration(resources.getConfiguration());configuration.fontScale=1.3f;android.util.DisplayMetrics metrics=new android.util.DisplayMetrics();metrics.setTo(resources.getDisplayMetrics());metrics.scaledDensity=metrics.density*1.3f;resources.updateConfiguration(configuration,metrics);
            PreviewHomeRows model=new PreviewHomeRows(host.get());PreviewHomeRows.Row row=new PreviewHomeRows.Row("custom:font","Science Fiction and Adventure");row.genreRule="Science Fiction|Action";row.dynamic=true;model.rows.add(row);model.save();Dialog dialog=open(host);layout(dialog);root(dialog).findViewWithTag(row.id).requestFocus();key(dialog,KeyEvent.KEYCODE_DPAD_RIGHT);key(dialog,KeyEvent.KEYCODE_DPAD_RIGHT);
            for(String tag:List.of("home.editor.name","home.editor.icon","home.editor.type:0","home.editor.type:1","home.editor.type:2","home.editor.genres","home.editor.match:true","home.editor.sort","home.editor.direction","home.editor.maximum","home.editor.automatic")){View field=root(dialog).findViewWithTag(tag);assertNotNull(tag,field);assertTrue(tag,field.requestFocus());org.robolectric.Shadows.shadowOf(android.os.Looper.getMainLooper()).idle();layout(dialog);fullyVisible(field);assertEquals(tag,dialog.getCurrentFocus().getTag());assertEquals(1,focusedTargets(root(dialog)));}
            PreviewPagesTest.capture(root(dialog),"0139-customise-large-font-reachable");root(dialog).findViewWithTag("home.editor.direction").performClick();assertFalse(new PreviewHomeRows(host.get()).rows.get(7).descending);dialog.dismiss();dialog=open(host);layout(dialog);root(dialog).findViewWithTag(row.id).requestFocus();key(dialog,KeyEvent.KEYCODE_DPAD_RIGHT);key(dialog,KeyEvent.KEYCODE_DPAD_RIGHT);assertEquals("Ascending",((android.widget.TextView)root(dialog).findViewWithTag("home.editor.direction")).getText().toString());dialog.dismiss();
        }finally{host.pause().stop().destroy();}
    }

}
