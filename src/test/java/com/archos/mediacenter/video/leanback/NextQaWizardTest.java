package com.archos.mediacenter.video.leanback;
import android.view.*;
import androidx.preference.PreferenceManager;
import org.junit.*;
import org.junit.runner.RunWith;
import org.robolectric.*;
import org.robolectric.annotation.*;
import org.robolectric.util.ReflectionHelpers;
import static org.junit.Assert.*;
@RunWith(RobolectricTestRunner.class) @Config(application=com.archos.mediacenter.video.CustomApplication.class,sdk=28,qualifiers="w960dp-h540dp-land-mdpi") @GraphicsMode(GraphicsMode.Mode.NATIVE)
public class NextQaWizardTest {
 @Before public void offline(){Shadows.shadowOf(RuntimeEnvironment.getApplication()).grantPermissions("app.supernova.player.DYNAMIC_RECEIVER_NOT_EXPORTED_PERMISSION");PreviewAsyncFixtures.offlineTransport();PreferenceManager.getDefaultSharedPreferences(RuntimeEnvironment.getApplication()).edit().putBoolean("try_new_ui",true).putBoolean("supernova_onboarding_complete",true).remove(CustomLibraryPage.KEY).commit();}
 @After public void drain() throws Exception {PreviewAsyncFixtures.drain();}
 @Test public void firstStepUpReachesNavigationAndReviewRightReachesSave() throws Exception {
  var host=Robolectric.buildActivity(CustomLibraryActivity.class).setup().visible();try{View root=host.get().findViewById(android.R.id.content);PreviewPagesTest.layout(root);View first=root.findViewWithTag("wizard.step.0");assertNotNull(first);first.requestFocus();root.dispatchKeyEvent(new KeyEvent(KeyEvent.ACTION_DOWN,KeyEvent.KEYCODE_DPAD_UP));assertSame(root.findViewWithTag("semantic:topnav.custom"),root.findFocus());View review=root.findViewWithTag("wizard.step.4");review.requestFocus();PreviewPagesTest.layout(root);review=root.findViewWithTag("wizard.step.4");review.requestFocus();root.dispatchKeyEvent(new KeyEvent(KeyEvent.ACTION_DOWN,KeyEvent.KEYCODE_DPAD_RIGHT));assertSame(PreviewPagesTest.findText(root,"Save Page"),root.findFocus());PreviewPagesTest.capture(root,"next-qa-wizard-review-save");}finally{host.pause().stop().destroy();}
 }
 @Test public void dirtyExitStartsSafelyAndSaveShowsCustomLibraryWithEditAtRight() throws Exception {
  var host=Robolectric.buildActivity(CustomLibraryActivity.class).setup().visible();try{View root=host.get().findViewById(android.R.id.content);PreviewPagesTest.layout(root);Object workspace=ReflectionHelpers.getField(host.get(),"workspace");CustomLibraryPage draft=ReflectionHelpers.getField(workspace,"draft");draft.name="QA Films";root.findViewWithTag("wizard.step.0").requestFocus();int[] exits={0};((PageExitGuard)workspace).requestExit(()->{exits[0]++;ReflectionHelpers.callInstanceMethod(workspace,"render");});android.app.Dialog dialog=org.robolectric.shadows.ShadowDialog.getLatestDialog();View decor=dialog.getWindow().getDecorView();assertSame(PreviewPagesTest.findText(decor,"Continue Editing"),decor.findFocus());PreviewPagesTest.findText(decor,"Save Page").performClick();PreviewPagesTest.layout(root);assertEquals(1,exits[0]);assertEquals("QA Films",CustomLibraryPage.load(host.get()).name);View edit=root.findViewWithTag("custom.control.edit");assertNotNull(edit);View filters=root.findViewWithTag("custom.control.filters");android.graphics.Rect editBounds=new android.graphics.Rect(),filterBounds=new android.graphics.Rect();assertTrue(edit.getGlobalVisibleRect(editBounds));assertTrue(filters.getGlobalVisibleRect(filterBounds));assertTrue(editBounds.left>filterBounds.right);assertNotNull(((android.widget.TextView)edit).getCompoundDrawables()[0]);}finally{host.pause().stop().destroy();}
 }
 @Test public void discardUsesRequestedDestinationAndInvalidSaveStaysInEditor() throws Exception {
  var host=Robolectric.buildActivity(CustomLibraryActivity.class).setup().visible();try{
   Object workspace=ReflectionHelpers.getField(host.get(),"workspace");CustomLibraryPage draft=ReflectionHelpers.getField(workspace,"draft");draft.name="";int[] exits={0};
   ((PageExitGuard)workspace).requestExit(()->exits[0]++);View decor=org.robolectric.shadows.ShadowDialog.getLatestDialog().getWindow().getDecorView();PreviewPagesTest.findText(decor,"Save Page").performClick();assertEquals(0,exits[0]);assertTrue(ReflectionHelpers.getField(workspace,"editing"));assertNull(CustomLibraryPage.load(host.get()));
   org.robolectric.shadows.ShadowDialog.getLatestDialog().dismiss();((PageExitGuard)workspace).requestExit(()->exits[0]++);decor=org.robolectric.shadows.ShadowDialog.getLatestDialog().getWindow().getDecorView();PreviewPagesTest.findText(decor,"Discard Changes").performClick();assertEquals(1,exits[0]);assertNull(CustomLibraryPage.load(host.get()));
  }finally{host.pause().stop().destroy();}
 }

 @Test public void previewFrameStaysOutsideScrollAndSemanticReturnSurvivesRebuild() throws Exception {
  var host=Robolectric.buildActivity(CustomLibraryActivity.class).setup().visible();try{View root=host.get().findViewById(android.R.id.content);PreviewPagesTest.layout(root);Object workspace=ReflectionHelpers.getField(host.get(),"workspace");View name=root.findViewWithTag("wizard.option.0.Name");assertNotNull(name);name.requestFocus();root.dispatchKeyEvent(new KeyEvent(KeyEvent.ACTION_DOWN,KeyEvent.KEYCODE_DPAD_RIGHT));View frame=root.findViewWithTag("wizard.preview");assertTrue(frame.hasFocus());assertNotNull(frame.getForeground());android.widget.ScrollView scroll=ReflectionHelpers.getField(workspace,"wizardPreview");assertSame(frame,scroll.getParent());assertNull(scroll.getForeground());
   ReflectionHelpers.callInstanceMethod(workspace,"render");PreviewPagesTest.layout(root);assertNotSame(name,root.findViewWithTag("wizard.option.0.Name"));root.findViewWithTag("wizard.preview").requestFocus();root.dispatchKeyEvent(new KeyEvent(KeyEvent.ACTION_DOWN,KeyEvent.KEYCODE_DPAD_LEFT));assertSame(root.findViewWithTag("wizard.option.0.Name"),root.findFocus());
  }finally{host.pause().stop().destroy();}
 }

}
