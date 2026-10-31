package com.archos.mediacenter.video.leanback;

import android.view.*;
import org.junit.*;
import org.junit.runner.RunWith;
import org.robolectric.*;
import org.robolectric.annotation.*;
import org.robolectric.util.ReflectionHelpers;
import static org.junit.Assert.*;

/** Actual editor native-view captures with fixture artwork; physical Shield acceptance remains separate. */
@RunWith(RobolectricTestRunner.class)
@Config(application=com.archos.mediacenter.video.CustomApplication.class,sdk=30,qualifiers="w960dp-h540dp-land-xhdpi")
@GraphicsMode(GraphicsMode.Mode.NATIVE)
public class Editor142RenderTest {
 @Before public void setup(){PreviewAsyncFixtures.offlineTransport();var app=RuntimeEnvironment.getApplication();Shadows.shadowOf(app).grantPermissions("app.supernova.player.DYNAMIC_RECEIVER_NOT_EXPORTED_PERMISSION");androidx.preference.PreferenceManager.getDefaultSharedPreferences(app).edit().putBoolean("try_new_ui",true).putBoolean("supernova_onboarding_complete",true).remove(CustomLibraryPage.KEY).commit();CustomLibraryPage page=new CustomLibraryPage();page.name="Fixture library";assertTrue(page.save(app));}
 @After public void drain()throws Exception{PreviewAsyncFixtures.drain();}
 private void layout(View view){for(int n=0;n<5;n++){view.measure(View.MeasureSpec.makeMeasureSpec(1920,View.MeasureSpec.EXACTLY),View.MeasureSpec.makeMeasureSpec(1080,View.MeasureSpec.EXACTLY));view.layout(0,0,1920,1080);Shadows.shadowOf(android.os.Looper.getMainLooper()).idleFor(java.time.Duration.ofMillis(40));}}
 private android.graphics.Bitmap render(View root){android.graphics.Bitmap image=android.graphics.Bitmap.createBitmap(root.getWidth(),root.getHeight(),android.graphics.Bitmap.Config.ARGB_8888);root.draw(new android.graphics.Canvas(image));return image;}
 @Test public void captureEveryStepAndKeepControlsAndLivePreview()throws Exception{
  var host=Robolectric.buildActivity(CustomLibraryActivity.class).setup().visible();try{
   View root=host.get().findViewById(android.R.id.content);Object workspace=ReflectionHelpers.getField(host.get(),"workspace");ReflectionHelpers.callInstanceMethod(workspace,"edit",ReflectionHelpers.ClassParameter.from(int.class,0));layout(root);
   View frame=root.findViewWithTag("wizard.preview");
   for(int step=0;step<4;step++){
    View rail=root.findViewWithTag("wizard.step."+step);assertNotNull(rail);rail.requestFocus();layout(root);assertSame("Stationary shell survives section changes",frame,root.findViewWithTag("wizard.preview"));assertNotNull(PreviewPagesTest.findText(root,"Live Preview"));
    assertNull(PreviewPagesTest.findText(root,"Previous"));assertNull(PreviewPagesTest.findText(root,"Next"));assertNull(PreviewPagesTest.findText(root,"Close Editor"));
    assertEquals(step==3,PreviewPagesTest.findText(root,"Save Changes")!=null);assertNull(root.findViewWithTag("wizard.step.4"));
    PreviewPagesTest.capture(root,"next-editor-section-"+step);
    root.dispatchKeyEvent(new KeyEvent(KeyEvent.ACTION_DOWN,KeyEvent.KEYCODE_DPAD_RIGHT));assertNotSame(rail,root.findFocus());
   }
  }finally{host.pause().stop().destroy();}
 }
}
