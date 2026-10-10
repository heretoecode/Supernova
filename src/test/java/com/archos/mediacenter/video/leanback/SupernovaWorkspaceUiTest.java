package com.archos.mediacenter.video.leanback;
import android.app.*;
import android.net.Uri;
import android.view.*;
import android.widget.*;
import com.archos.mediacenter.video.leanback.filebrowsing.UniversalFileBrowser;
import com.archos.mediacenter.video.streaming.PreviewProviderIcons;
import org.junit.*;
import org.junit.runner.RunWith;
import org.robolectric.*;
import org.robolectric.annotation.*;
import org.robolectric.shadows.ShadowDialog;
import static org.junit.Assert.*;
@RunWith(RobolectricTestRunner.class)
@Config(application=Application.class,sdk=28,qualifiers="w960dp-h540dp-land-mdpi")
@GraphicsMode(GraphicsMode.Mode.NATIVE)
public class SupernovaWorkspaceUiTest {
 @Test public void panelsKeepApprovedGeometryAndFocusOutline()throws Exception{
  var host=Robolectric.buildActivity(TopNavigationTest.Host.class).setup().visible();
  try{SharedThreePanel panels=new SharedThreePanel(host.get());TextView left=SharedThreePanel.action(host.get(),"Subtitles",()->{});TextView middle=SharedThreePanel.action(host.get(),"Subtitle size",()->{});TextView right=SharedThreePanel.text(host.get(),"Choose your subtitle appearance",16);panels.panels(left,middle,right);host.get().setContentView(panels);PreviewPagesTest.layout(panels);
   assertEquals(16,middle.getLeft()-left.getRight());assertEquals(16,right.getLeft()-middle.getRight());assertEquals(.24,left.getWidth()/928d,.003);assertEquals(.38,middle.getWidth()/928d,.003);assertEquals(20,left.getPaddingLeft());assertEquals(18,left.getTextSize(),.01);assertEquals(16,right.getTextSize(),.01);assertTrue(middle.requestFocus());assertFalse(right.isFocusable());PreviewPagesTest.capture(panels,"0135-panel-geometry");
  }finally{host.pause().stop().destroy();}
 }
 @Test public void browserReviewKeepsDraftOrDiscardsWithoutChangingStorage()throws Exception{
  var host=Robolectric.buildActivity(TopNavigationTest.Host.class).setup().visible();
  try{UniversalFileBrowser browser=new UniversalFileBrowser(host.get());int[] navigated={-1};TopNavigation shell=new TopNavigation(host.get(),browser,n->navigated[0]=n,browser::atTop);shell.selectTab(3);host.get().setContentView(shell);PreviewPagesTest.layout(shell);browser.focusOverview();PreviewPagesTest.layout(shell);browser.selections().exclude(Uri.parse("file:///Movies/private"));int[] left={0};browser.requestExit(()->left[0]++);Dialog review=ShadowDialog.getLatestDialog();assertTrue(review.isShowing());assertEquals("Keep Editing",((TextView)review.getCurrentFocus()).getText().toString());assertNotNull(PreviewPagesTest.findText(review.getWindow().getDecorView(),"Excluded"));
   PreviewPagesTest.capture(shell,"0135-network-browser");View decor=review.getWindow().getDecorView();int width=review.getWindow().getAttributes().width,height=review.getWindow().getAttributes().height;decor.measure(View.MeasureSpec.makeMeasureSpec(width,View.MeasureSpec.EXACTLY),View.MeasureSpec.makeMeasureSpec(height,View.MeasureSpec.EXACTLY));decor.layout(0,0,width,height);PreviewPagesTest.capture(decor,"0135-browser-unsaved");
   PreviewPagesTest.findText(decor,"Keep Editing").performClick();assertFalse(review.isShowing());assertEquals(0,left[0]);assertTrue(browser.selections().changed());browser.requestExit(()->left[0]++);review=ShadowDialog.getLatestDialog();PreviewPagesTest.findText(review.getWindow().getDecorView(),"Discard Changes").performClick();assertEquals(1,left[0]);assertFalse(browser.selections().changed());
   browser.selections().exclude(Uri.parse("file:///Movies/another-private"));View home=shell.findViewWithTag("semantic:topnav.home");assertTrue(home.requestFocus());home.performClick();review=ShadowDialog.getLatestDialog();assertTrue(review.isShowing());assertEquals(-1,navigated[0]);PreviewPagesTest.findText(review.getWindow().getDecorView(),"Keep Editing").performClick();assertSame("Top-nav exit review restores its exact opener",home,shell.findFocus());assertTrue(browser.selections().changed());home.performClick();review=ShadowDialog.getLatestDialog();PreviewPagesTest.findText(review.getWindow().getDecorView(),"Discard Changes").performClick();assertEquals(0,navigated[0]);assertFalse(browser.selections().changed());
  }finally{host.pause().stop().destroy();}
 }
 @Test public void compactBrowserScrollsEveryContextActionIntoViewAndReadsLongInformation()throws Exception{
  var host=Robolectric.buildActivity(TopNavigationTest.Host.class).setup().visible();
  try{UniversalFileBrowser browser=new UniversalFileBrowser(host.get());FrameLayout container=new FrameLayout(host.get());container.addView(browser,new FrameLayout.LayoutParams(-1,300));host.get().setContentView(container);
   Runnable compact=()->{PreviewPagesTest.layout(container);Shadows.shadowOf(android.os.Looper.getMainLooper()).idleFor(java.time.Duration.ofMillis(300));PreviewPagesTest.layout(container);};
   compact.run();View first=PreviewPagesTest.findText(browser,"Scan Library");assertTrue(first.requestFocus());compact.run();
   for(String label:new String[]{"Network Scanning","Library Health"}){
    browser.dispatchKeyEvent(new KeyEvent(KeyEvent.ACTION_DOWN,KeyEvent.KEYCODE_DPAD_DOWN));compact.run();View action=PreviewPagesTest.findText(browser,label);assertSame("Focus moves to "+label,action,browser.findFocus());android.graphics.Rect visible=new android.graphics.Rect();assertTrue(label+" is visible after compact scrolling",action.getGlobalVisibleRect(visible));assertEquals("The full target remains visible in compact Home",action.getHeight(),visible.height());
   }
   first.requestFocus();browser.dispatchKeyEvent(new KeyEvent(KeyEvent.ACTION_DOWN,KeyEvent.KEYCODE_DPAD_UP));compact.run();assertTrue(browser.findFocus() instanceof ScrollView);
   ScrollView info=(ScrollView)browser.findFocus();((TextView)info.getChildAt(0)).setText("Library information\n".repeat(30));compact.run();browser.dispatchKeyEvent(new KeyEvent(KeyEvent.ACTION_DOWN,KeyEvent.KEYCODE_DPAD_DOWN));assertTrue(info.getScrollY()>0);assertSame(info,browser.findFocus());
   info.scrollTo(0,100000);browser.dispatchKeyEvent(new KeyEvent(KeyEvent.ACTION_DOWN,KeyEvent.KEYCODE_DPAD_DOWN));assertSame(first,browser.findFocus());compact.run();PreviewPagesTest.capture(browser,"0135-compact-context-scrolled");((TextView)info.getChildAt(0)).setText("");assertFalse("Empty context does not become a focus target",info.isFocusable());
  }finally{host.pause().stop().destroy();}
 }
 @Test public void fiveStepEditorSavesOnePageAndReopensPrefilled()throws Exception{
  Shadows.shadowOf(RuntimeEnvironment.getApplication()).grantPermissions("app.supernova.player.DYNAMIC_RECEIVER_NOT_EXPORTED_PERMISSION");
  var host=Robolectric.buildActivity(CustomLibraryActivity.class).setup().visible();
  try{View root=host.get().getWindow().getDecorView();PreviewPagesTest.layout(root);assertNotNull(root.findViewWithTag("wizard.step.4"));assertNull(root.findViewWithTag("wizard.step.5"));PreviewPagesTest.capture(root,"0135-custom-page-editor");root.findViewWithTag("wizard.step.4").performClick();PreviewPagesTest.layout(root);PreviewPagesTest.findText(root,"Save Page").performClick();PreviewPagesTest.layout(root);assertEquals("Library Page",CustomLibraryPage.load(host.get()).name);assertNotNull(root.findViewWithTag("custom.control.filters"));assertNotNull(root.findViewWithTag("custom.control.edit"));View viewControl=root.findViewWithTag("custom.control.view"),editControl=root.findViewWithTag("custom.control.edit");assertNotNull(viewControl.getBackground());viewControl.requestFocus();root.dispatchKeyEvent(new KeyEvent(KeyEvent.ACTION_DOWN,KeyEvent.KEYCODE_DPAD_RIGHT));assertSame("Right skips the layout spacer before Edit",editControl,root.findFocus());root.dispatchKeyEvent(new KeyEvent(KeyEvent.ACTION_DOWN,KeyEvent.KEYCODE_DPAD_LEFT));assertSame(viewControl,root.findFocus());PreviewPagesTest.capture(root,"0135-custom-page-library");editControl.performClick();PreviewPagesTest.layout(root);assertNotNull(PreviewPagesTest.findText(root,"Name: Library Page"));PreviewPagesTest.capture(root,"0135-custom-page-prefilled");
  }finally{host.pause().stop().destroy();}
 }
 @Test public void myProvidersKeepsColourWhileActionMarksStayMonochrome(){
  ImageView icon=new ImageView(RuntimeEnvironment.getApplication());PreviewProviderIcons.bind(icon,null,0,"provider.choice");assertNull(icon.getColorFilter());assertNotNull(icon.getDrawable());PreviewProviderIcons.bind(icon,null,0,"details.related");assertNotNull(icon.getColorFilter());
 }
 @Test public void settingsExpansionKeepsParentThenDownEntersChildAndRightEntersOptions()throws Exception{
  var host=Robolectric.buildActivity(TopNavigationTest.Host.class).setup().visible();
  try{host.get().setTheme(com.archos.mediacenter.video.R.style.MyLeanbackTheme);PreviewSettingsEntryTest.NestedSettings settings=new PreviewSettingsEntryTest.NestedSettings();host.get().getSupportFragmentManager().beginTransaction().add(android.R.id.content,settings).commitNow();View root=settings.requireView();PreviewPagesTest.layout(root);
   View playback=root.findViewWithTag("semantic:settings:category:Playback");playback.requestFocus();playback.performClick();PreviewPagesTest.layout(root);assertSame(playback,root.findFocus());assertNotNull(root.findViewWithTag("semantic:settings:section:first"));assertNotNull(root.findViewWithTag("semantic:settings:category:About"));
   root.dispatchKeyEvent(new KeyEvent(KeyEvent.ACTION_DOWN,KeyEvent.KEYCODE_DPAD_DOWN));View child=root.findViewWithTag("semantic:settings:section:first");assertSame(child,root.findFocus());
   root.dispatchKeyEvent(new KeyEvent(KeyEvent.ACTION_DOWN,KeyEvent.KEYCODE_DPAD_RIGHT));PreviewPagesTest.layout(root);assertTrue(settings.getListView().hasFocus());PreviewPagesTest.capture(root,"0135-settings-expanded");
  }finally{host.pause().stop().destroy();}
 }
 @Test public void libraryHealthEmptyStateAndSourceOnlyProblemAreDistinct()throws Exception{
  Shadows.shadowOf(RuntimeEnvironment.getApplication()).grantPermissions("app.supernova.player.DYNAMIC_RECEIVER_NOT_EXPORTED_PERMISSION");
  var host=Robolectric.buildActivity(LibraryHealthActivity.class).setup().visible();
  try{LibraryHealthActivity.Workspace workspace=host.get().new Workspace();host.get().setContentView(workspace);workspace.update(new PreviewLibraryLoader.Snapshot());PreviewPagesTest.layout(workspace);assertNotNull(PreviewPagesTest.findText(workspace,"Your library is healthy\n\nNo issues need your attention."));assertEquals(5,workspace.categoryRows.size());PreviewPagesTest.capture(workspace,"0135-library-health-empty");
   host.get().getSharedPreferences(com.archos.mediaprovider.video.SupernovaLibraryPolicy.HEALTH,0).edit().putLong("offline:smb://nas/Movies",42).commit();workspace.update(new PreviewLibraryLoader.Snapshot());PreviewPagesTest.layout(workspace);assertNull(PreviewPagesTest.findText(workspace,"Your library is healthy\n\nNo issues need your attention."));workspace.categoryRows.get(4).requestFocus();workspace.dispatchKeyEvent(new KeyEvent(KeyEvent.ACTION_DOWN,KeyEvent.KEYCODE_DPAD_RIGHT));assertTrue(workspace.issues.hasFocus());workspace.dispatchKeyEvent(new KeyEvent(KeyEvent.ACTION_DOWN,KeyEvent.KEYCODE_DPAD_RIGHT));assertEquals("Scan Library",((TextView)workspace.findFocus()).getText().toString());workspace.update(new PreviewLibraryLoader.Snapshot());assertEquals("Scan Library",((TextView)workspace.findFocus()).getText().toString());workspace.dispatchKeyEvent(new KeyEvent(KeyEvent.ACTION_DOWN,KeyEvent.KEYCODE_DPAD_LEFT));assertTrue(workspace.issues.hasFocus());PreviewPagesTest.layout(workspace);PreviewPagesTest.capture(workspace,"0135-library-health-source-problem");
  }finally{host.pause().stop().destroy();}
 }
 @Test public void customWizardKeepsDeterministicEdgesAndCentreRestoration()throws Exception{
  Shadows.shadowOf(RuntimeEnvironment.getApplication()).grantPermissions("app.supernova.player.DYNAMIC_RECEIVER_NOT_EXPORTED_PERMISSION");
  var host=Robolectric.buildActivity(CustomLibraryActivity.class).setup().visible();
  try{View root=host.get().getWindow().getDecorView();PreviewPagesTest.layout(root);View step=root.findViewWithTag("wizard.step.0");step.requestFocus();root.dispatchKeyEvent(new KeyEvent(KeyEvent.ACTION_DOWN,KeyEvent.KEYCODE_DPAD_RIGHT));View option=root.findFocus();assertTrue(((TextView)option).getText().toString().startsWith("Name:"));root.dispatchKeyEvent(new KeyEvent(KeyEvent.ACTION_DOWN,KeyEvent.KEYCODE_DPAD_RIGHT));assertTrue(root.findFocus() instanceof ScrollView);root.dispatchKeyEvent(new KeyEvent(KeyEvent.ACTION_DOWN,KeyEvent.KEYCODE_DPAD_LEFT));assertSame(option,root.findFocus());root.dispatchKeyEvent(new KeyEvent(KeyEvent.ACTION_DOWN,KeyEvent.KEYCODE_DPAD_LEFT));assertSame(step,root.findFocus());
  }finally{host.pause().stop().destroy();}
 }
}
