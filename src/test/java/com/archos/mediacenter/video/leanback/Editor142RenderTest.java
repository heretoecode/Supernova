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
   int largest=0;for(int step=0;step<5;step++){
    View rail=root.findViewWithTag("wizard.step."+step);assertNotNull(rail);rail.requestFocus();layout(root);assertNotNull(PreviewPagesTest.findText(root,"Live Preview"));assertNotNull(PreviewPagesTest.findText(root,step==4?"Save Page":"Next"));assertNotNull(PreviewPagesTest.findText(root,"Close Editor"));
    android.view.View body=ReflectionHelpers.getField(workspace,"libraryBody");
    body.setBackgroundColor(android.graphics.Color.BLACK);android.graphics.Bitmap dark=render(root);body.setBackgroundColor(android.graphics.Color.WHITE);android.graphics.Bitmap bright=render(root);
    int maximum=0;for(int x:new int[]{600,800,1000})for(int y:new int[]{600,700,780}){int a=dark.getPixel(x,y),b=bright.getPixel(x,y);maximum=Math.max(maximum,Math.abs(android.graphics.Color.red(a)-android.graphics.Color.red(b)));maximum=Math.max(maximum,Math.abs(android.graphics.Color.green(a)-android.graphics.Color.green(b)));maximum=Math.max(maximum,Math.abs(android.graphics.Color.blue(a)-android.graphics.Color.blue(b)));}
    java.io.File receipt=new java.io.File("build/reports/preview-ui/0142-editor-step-"+step+"-contrast.json");receipt.getParentFile().mkdirs();try(java.io.FileWriter out=new java.io.FileWriter(receipt)){out.write("{\"background_channel_delta\":"+maximum+",\"viewport\":\"1920x1080\"}\n");}
    body.setBackground(new android.graphics.drawable.Drawable(){private final android.graphics.Paint paint=new android.graphics.Paint();public void draw(android.graphics.Canvas canvas){for(int y=0;y<getBounds().height();y+=48)for(int x=0;x<getBounds().width();x+=48){paint.setColor((x/48+y/48)%2==0?0xfff5bd3a:0xffd52b57);canvas.drawRect(x,y,x+48,y+48,paint);}}public void setAlpha(int alpha){}public void setColorFilter(android.graphics.ColorFilter filter){}public int getOpacity(){return android.graphics.PixelFormat.OPAQUE;}});
    PreviewPagesTest.capture(root,"0142-editor-step-"+step);largest=Math.max(largest,maximum);dark.recycle();bright.recycle();
    root.dispatchKeyEvent(new KeyEvent(KeyEvent.ACTION_DOWN,KeyEvent.KEYCODE_DPAD_RIGHT));assertNotSame(rail,root.findFocus());
   }
   assertTrue("Artwork must not alter panel text backgrounds, delta="+largest,largest<=2);
  }finally{host.pause().stop().destroy();}
 }
}
