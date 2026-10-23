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
 @Test public void actualGlobalShellCapturesAllSectionsAndWebdavStates()throws Exception{
  var host=Robolectric.buildActivity(TopNavigationTest.Host.class).setup().visible();UniversalFileBrowser browser=null;try{
   PreviewPages pages=new PreviewPages(host.get(),(holder,item)->{});ReflectionHelpers.setField(pages,"sourcesLoading",true);TopNavigation nav=new TopNavigation(host.get(),pages,pages::setTab,pages::atTop);host.get().setContentView(nav);pages.setSnapshot(new PreviewLibraryLoader.Snapshot());pages.setFiles(List.of(new Box(Box.ID.FOLDERS,"Internal Storage",0,"/fixture/internal"),new Box(Box.ID.USB,"USB fixture",0,"/fixture/usb")));pages.setTab(3);nav.selectTab(3);layout(nav);
   PreviewNetworkWorkspace workspace=ReflectionHelpers.getField(pages,"sharedBrowser");browser=workspace.browser();
   for(String name:new String[]{"overview","local_storage","network_shares","cloud_services","all_files","library_health"}){View row=nav.findViewWithTag("semantic:network.category."+name);assertNotNull(row);row.requestFocus();row.performClick();layout(nav);assertTrue(row.isShown());PreviewPagesTest.capture(nav,"0142-shell-"+name);}
   View network=nav.findViewWithTag("semantic:network.category.network_shares");network.requestFocus();network.performClick();layout(nav);PreviewPagesTest.findText(browser,"Add Network Source").performClick();ReflectionHelpers.setField(browser,"protocol",1);ReflectionHelpers.callInstanceMethod(browser,"connectionForm");layout(nav);PreviewPagesTest.capture(nav,"0142-shell-webdav-form");
   PreviewPagesTest.findText(browser,"Protocol: WebDAV HTTPS").performClick();layout(nav);PreviewPagesTest.capture(nav,"0142-shell-protocols");
  }finally{if(browser!=null)browser.close();host.pause().stop().destroy();}
 }
}
