package com.archos.mediacenter.video.leanback;

import android.app.Application;
import android.app.Dialog;
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
    private void fullyVisible(View view){android.graphics.Rect visible=new android.graphics.Rect();assertTrue("target must be visible: "+view.getTag(),view.getGlobalVisibleRect(visible));assertEquals("full target height: "+view.getTag(),view.getHeight(),visible.height());}
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
    @Test public void legacyLimitsAndCaseInsensitiveAllGenresAreSafe(){
        assertEquals(15,PreviewHomeRows.normaliseMaximum(0));assertEquals(5,PreviewHomeRows.normaliseMaximum(1));assertEquals(30,PreviewHomeRows.normaliseMaximum(100));
        assertTrue(PreviewGenres.matches("crime|DRAMA",new LinkedHashSet<>(Arrays.asList("Crime","Drama")),true));assertFalse(PreviewGenres.matches("Crime",new LinkedHashSet<>(Arrays.asList("Crime","Drama")),true));
        assertTrue(PreviewGenres.matches("",Collections.emptySet(),true));
    }
    @Test @GraphicsMode(GraphicsMode.Mode.NATIVE) public void captureActualBuiltinCustomAndGenreStates()throws Exception{
        ActivityController<TopNavigationTest.Host> host=host();try{
            Dialog dialog=open(host);layout(dialog);PreviewPagesTest.capture(root(dialog),"0139-customise-built-in");
            createNamed(dialog,"Science Fiction");layout(dialog);
            fullyVisible(root(dialog).findViewWithTag("home.editor.automatic"));
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
            layout(dialog);fullyVisible(root(dialog).findViewWithTag("home.editor.automatic"));fullyVisible(root(dialog).findViewWithTag("home.editor.maximum"));
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
}
