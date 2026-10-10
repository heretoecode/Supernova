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
  try{UniversalFileBrowser browser=new UniversalFileBrowser(host.get());TopNavigation shell=new TopNavigation(host.get(),browser,n->{},browser::atTop);shell.selectTab(3);host.get().setContentView(shell);PreviewPagesTest.layout(shell);browser.focusOverview();PreviewPagesTest.layout(shell);browser.selections().exclude(Uri.parse("file:///Movies/private"));int[] left={0};browser.requestExit(()->left[0]++);Dialog review=ShadowDialog.getLatestDialog();assertTrue(review.isShowing());assertEquals("Keep Editing",((TextView)review.getCurrentFocus()).getText().toString());assertNotNull(PreviewPagesTest.findText(review.getWindow().getDecorView(),"Excluded"));
   PreviewPagesTest.capture(shell,"0135-network-browser");View decor=review.getWindow().getDecorView();int width=review.getWindow().getAttributes().width,height=review.getWindow().getAttributes().height;decor.measure(View.MeasureSpec.makeMeasureSpec(width,View.MeasureSpec.EXACTLY),View.MeasureSpec.makeMeasureSpec(height,View.MeasureSpec.EXACTLY));decor.layout(0,0,width,height);PreviewPagesTest.capture(decor,"0135-browser-unsaved");
   PreviewPagesTest.findText(decor,"Keep Editing").performClick();assertFalse(review.isShowing());assertEquals(0,left[0]);assertTrue(browser.selections().changed());browser.requestExit(()->left[0]++);review=ShadowDialog.getLatestDialog();PreviewPagesTest.findText(review.getWindow().getDecorView(),"Discard Changes").performClick();assertEquals(1,left[0]);assertFalse(browser.selections().changed());
  }finally{host.pause().stop().destroy();}
 }
 @Test public void fiveStepEditorSavesOnePageAndReopensPrefilled()throws Exception{
  Shadows.shadowOf(RuntimeEnvironment.getApplication()).grantPermissions("app.supernova.player.DYNAMIC_RECEIVER_NOT_EXPORTED_PERMISSION");
  var host=Robolectric.buildActivity(CustomLibraryActivity.class).setup().visible();
  try{View root=host.get().getWindow().getDecorView();PreviewPagesTest.layout(root);assertNotNull(root.findViewWithTag("wizard.step.4"));assertNull(root.findViewWithTag("wizard.step.5"));PreviewPagesTest.capture(root,"0135-custom-page-editor");root.findViewWithTag("wizard.step.4").performClick();PreviewPagesTest.layout(root);PreviewPagesTest.findText(root,"Save Page").performClick();PreviewPagesTest.layout(root);assertEquals("Library Page",CustomLibraryPage.load(host.get()).name);assertNotNull(root.findViewWithTag("custom.control.filters"));assertNotNull(root.findViewWithTag("custom.control.edit"));root.findViewWithTag("custom.control.edit").performClick();PreviewPagesTest.layout(root);assertNotNull(PreviewPagesTest.findText(root,"Name: Library Page"));PreviewPagesTest.capture(root,"0135-custom-page-prefilled");
  }finally{host.pause().stop().destroy();}
 }
 @Test public void myProvidersKeepsColourWhileActionMarksStayMonochrome(){
  ImageView icon=new ImageView(RuntimeEnvironment.getApplication());PreviewProviderIcons.bind(icon,null,0,"provider.choice");assertNull(icon.getColorFilter());assertNotNull(icon.getDrawable());PreviewProviderIcons.bind(icon,null,0,"details.related");assertNotNull(icon.getColorFilter());
 }
}
