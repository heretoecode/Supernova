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
  assertNotNull("Global navigation retained",root.findViewWithTag("semantic:topnav.network"));
 }
 private int brightPixels(android.graphics.Bitmap bitmap,int left,int top,int right,int bottom){int count=0;for(int y=top;y<bottom;y++)for(int x=left;x<right;x++){int pixel=bitmap.getPixel(x,y);if(android.graphics.Color.red(pixel)>180&&android.graphics.Color.green(pixel)>180&&android.graphics.Color.blue(pixel)>180)count++;}return count;}
 @Test public void actualGlobalShellCapturesAllSectionsAndWebdavStates()throws Exception{
  var host=Robolectric.buildActivity(TopNavigationTest.Host.class).setup().visible();UniversalFileBrowser browser=null;try{
   PreviewPages pages=new PreviewPages(host.get(),(holder,item)->{});ReflectionHelpers.setField(pages,"sourcesLoading",true);TopNavigation nav=new TopNavigation(host.get(),pages,pages::setTab,pages::atTop);host.get().setContentView(nav);pages.setSnapshot(new PreviewLibraryLoader.Snapshot());pages.setFiles(List.of(new Box(Box.ID.FOLDERS,"Internal Storage",0,"/fixture/internal"),new Box(Box.ID.USB,"USB fixture",0,"/fixture/usb")));pages.setTab(3);nav.selectTab(3);layout(nav);
   PreviewNetworkWorkspace workspace=ReflectionHelpers.getField(pages,"sharedBrowser");browser=workspace.browser();
   capture(nav,"0147-shell-landing");
   for(int n=0;n<6;n++){browser.findViewWithTag("network.card."+n).performClick();layout(nav);capture(nav,"0147-shell-card-"+n);ReflectionHelpers.setField(browser,"connectionDirty",false);ReflectionHelpers.callInstanceMethod(browser,"networkLanding");layout(nav);}
   browser.findViewWithTag("network.card.2").performClick();layout(nav);PreviewPagesTest.findText(browser,"WebDAV HTTPS").performClick();layout(nav);capture(nav,"0147-shell-webdav-form");
   PreviewPagesTest.findText(browser,"Protocol: WebDAV HTTPS").performClick();layout(nav);capture(nav,"0142-shell-protocols");
  }finally{if(browser!=null)browser.close();host.pause().stop().destroy();}
 }
 private void responsiveLanding(String suffix)throws Exception{
  var host=Robolectric.buildActivity(TopNavigationTest.Host.class).setup().visible();UniversalFileBrowser browser=new UniversalFileBrowser(host.get());try{
   browser.networkWorkspace();browser.locations(List.of(),List.of(),List.of(),kind->{});
   TopNavigation nav=new TopNavigation(host.get(),browser,index->{},browser::atTop);nav.selectTab(3);host.get().setContentView(nav);
   float density=nav.getResources().getDisplayMetrics().density;int w=Math.round(nav.getResources().getConfiguration().screenWidthDp*density),h=Math.round(nav.getResources().getConfiguration().screenHeightDp*density);
   for(int n=0;n<4;n++){nav.measure(View.MeasureSpec.makeMeasureSpec(w,View.MeasureSpec.EXACTLY),View.MeasureSpec.makeMeasureSpec(h,View.MeasureSpec.EXACTLY));nav.layout(0,0,w,h);Shadows.shadowOf(android.os.Looper.getMainLooper()).idleFor(java.time.Duration.ofMillis(50));}
   for(int n=0;n<6;n++){View card=nav.findViewWithTag("network.card."+n);assertNotNull(card);assertTrue(card.getWidth()>0&&card.getHeight()>0);int[] xy=new int[2];card.getLocationOnScreen(xy);assertTrue("Safe horizontal bounds",xy[0]>=0&&xy[0]+card.getWidth()<=w);assertTrue("Cards fit vertically",xy[1]>=0&&xy[1]+card.getHeight()<=h);}
   for(String tab:new String[]{"home","movies","tv","network","search","settings"}){View control=nav.findViewWithTag("semantic:topnav."+tab);android.graphics.Rect visible=new android.graphics.Rect();assertTrue(tab,control.getGlobalVisibleRect(visible));assertEquals("Complete navigation control at "+suffix+": "+tab,control.getWidth(),visible.width());}
   invalidateCapture(nav);PreviewPagesTest.capture(nav,"0147-network-"+suffix);
  }finally{browser.close();host.pause().stop().destroy();}
 }
 @Test @Config(qualifiers="w640dp-h360dp-land-xhdpi") public void landingAt720p()throws Exception{responsiveLanding("720p");}
 @Test @Config(qualifiers="w960dp-h540dp-land-xxxhdpi") public void landingAt4k()throws Exception{responsiveLanding("4k");}
}
