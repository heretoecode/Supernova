package com.archos.mediacenter.video.leanback;

import android.app.Application;
import android.view.*;
import com.archos.mediacenter.video.leanback.adapter.object.*;
import com.archos.mediacenter.video.leanback.filebrowsing.UniversalFileBrowser;
import org.junit.*;
import org.junit.runner.RunWith;
import org.robolectric.*;
import org.robolectric.annotation.*;
import org.robolectric.util.ReflectionHelpers;
import java.util.*;
import static org.junit.Assert.*;

@RunWith(RobolectricTestRunner.class) @Config(application=Application.class,sdk=30,qualifiers="w960dp-h540dp-land-xhdpi") @GraphicsMode(GraphicsMode.Mode.NATIVE)
public class Network142RenderTest {
 @Before public void setup(){PreviewAsyncFixtures.offlineTransport();androidx.preference.PreferenceManager.getDefaultSharedPreferences(RuntimeEnvironment.getApplication()).edit().putBoolean("supernova_onboarding_complete",true).commit();}
 @After public void drain()throws Exception{PreviewAsyncFixtures.drain();}
 private void layout(View root){for(int n=0;n<6;n++){root.measure(View.MeasureSpec.makeMeasureSpec(1920,View.MeasureSpec.EXACTLY),View.MeasureSpec.makeMeasureSpec(1080,View.MeasureSpec.EXACTLY));root.layout(0,0,1920,1080);Shadows.shadowOf(android.os.Looper.getMainLooper()).idleFor(java.time.Duration.ofMillis(50));}}
 // View.draw into a new bitmap must invalidate unchanged descendants too: otherwise
 // repeated native Robolectric captures can omit cached static text.
 private void invalidateCapture(View view){view.invalidate();if(view instanceof ViewGroup){ViewGroup group=(ViewGroup)view;for(int n=0;n<group.getChildCount();n++)invalidateCapture(group.getChildAt(n));}}
 private void capture(View root,String name)throws Exception{
  invalidateCapture(root);PreviewPagesTest.capture(root,name);
  android.graphics.Bitmap bitmap=android.graphics.BitmapFactory.decodeFile("build/reports/preview-ui/"+name+".png");
  assertTrue("Capture retains the static global brand: "+name,brightPixels(bitmap,52,35,266,76)>100);
  assertTrue("Capture retains the persistent Overview rail label: "+name,brightPixels(bitmap,112,195,320,228)>50);
 }
 private int brightPixels(android.graphics.Bitmap bitmap,int left,int top,int right,int bottom){int count=0;for(int y=top;y<bottom;y++)for(int x=left;x<right;x++){int pixel=bitmap.getPixel(x,y);if(android.graphics.Color.red(pixel)>180&&android.graphics.Color.green(pixel)>180&&android.graphics.Color.blue(pixel)>180)count++;}return count;}
 @Test public void actualGlobalShellCapturesAllSectionsAndWebdavStates()throws Exception{
  var host=Robolectric.buildActivity(TopNavigationTest.Host.class).setup().visible();UniversalFileBrowser browser=null;try{
   PreviewPages pages=new PreviewPages(host.get(),(holder,item)->{});ReflectionHelpers.setField(pages,"sourcesLoading",true);TopNavigation nav=new TopNavigation(host.get(),pages,pages::setTab,pages::atTop);host.get().setContentView(nav);pages.setSnapshot(new PreviewLibraryLoader.Snapshot());pages.setFiles(List.of(new Box(Box.ID.FOLDERS,"Internal Storage",0,"/fixture/internal"),new Box(Box.ID.USB,"USB fixture",0,"/fixture/usb")));pages.setTab(3);nav.selectTab(3);layout(nav);
   PreviewNetworkWorkspace workspace=ReflectionHelpers.getField(pages,"sharedBrowser");browser=workspace.browser();
   for(String name:new String[]{"overview","local_storage","network_shares","cloud_services","all_files","library_health"}){View row=nav.findViewWithTag("semantic:network.category."+name);assertNotNull(row);row.requestFocus();row.performClick();layout(nav);assertTrue(row.isShown());capture(nav,"0142-shell-"+name);}
   View network=nav.findViewWithTag("semantic:network.category.network_shares");network.requestFocus();network.performClick();layout(nav);PreviewPagesTest.findText(browser,"Add Network Source").performClick();ReflectionHelpers.setField(browser,"protocol",1);ReflectionHelpers.callInstanceMethod(browser,"connectionForm");layout(nav);capture(nav,"0142-shell-webdav-form");
   PreviewPagesTest.findText(browser,"Protocol: WebDAV HTTPS").performClick();layout(nav);capture(nav,"0142-shell-protocols");
  }finally{if(browser!=null)browser.close();host.pause().stop().destroy();}
 }
}
