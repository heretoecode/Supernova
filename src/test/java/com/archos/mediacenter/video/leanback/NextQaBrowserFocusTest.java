package com.archos.mediacenter.video.leanback;
import android.app.Application;
import android.net.Uri;
import android.view.*;
import com.archos.filecorelibrary.MetaFile2;
import com.archos.mediacenter.video.leanback.filebrowsing.UniversalFileBrowser;
import org.junit.*;
import org.junit.runner.RunWith;
import org.robolectric.*;
import org.robolectric.annotation.*;
import org.robolectric.util.ReflectionHelpers;
import static org.junit.Assert.*;
@RunWith(RobolectricTestRunner.class) @Config(application=Application.class,sdk=30,qualifiers="w960dp-h540dp-land-mdpi") @GraphicsMode(GraphicsMode.Mode.NATIVE)
public class NextQaBrowserFocusTest {
 @Before public void offline(){PreviewAsyncFixtures.offlineTransport();}
 @After public void drain() throws Exception {PreviewAsyncFixtures.drain();}
 @Test public void directoryTraversalScrollsBeyondViewportAndRefreshKeepsNearbyFocus(){check(1);check(2);}
 private void check(int columns){
  var host=Robolectric.buildActivity(TopNavigationTest.Host.class).setup().visible();try{
   androidx.preference.PreferenceManager.getDefaultSharedPreferences(host.get()).edit().putBoolean(ReflectionHelpers.getStaticField(UniversalFileBrowser.class,"GRID"),columns==2).commit();UniversalFileBrowser browser=new UniversalFileBrowser(host.get());browser.setTag("browser.probe");host.get().setContentView(browser);Uri current=Uri.parse("file:///fixture/drive");ReflectionHelpers.setField(browser,"current",current);((View)ReflectionHelpers.getField(browser,"toolbar")).setVisibility(View.VISIBLE);ReflectionHelpers.callInstanceMethod(browser,"breadcrumbs");
   java.util.List<MetaFile2> files=ReflectionHelpers.getField(browser,"files");for(int i=0;i<50;i++){MetaFile2 folder=org.mockito.Mockito.mock(MetaFile2.class);org.mockito.Mockito.when(folder.isDirectory()).thenReturn(true);org.mockito.Mockito.when(folder.getName()).thenReturn("Folder "+i);org.mockito.Mockito.when(folder.getUri()).thenReturn(current.buildUpon().appendPath("folder"+i).build());files.add(folder);}
   ReflectionHelpers.callInstanceMethod(browser,"renderItems");PreviewPagesTest.layout(browser);View first=browser.findViewWithTag(current.buildUpon().appendPath("folder0").build().toString());assertNotNull(first);assertTrue(first.requestFocus());
   for(int i=1;i<=20;i++){assertTrue(browser.dispatchKeyEvent(new KeyEvent(KeyEvent.ACTION_DOWN,KeyEvent.KEYCODE_DPAD_DOWN)));PreviewPagesTest.layout(browser);assertEquals("D-pad reaches row "+i,current.buildUpon().appendPath("folder"+(i*columns)).build().toString(),browser.findFocus().getTag());}
   files.remove(20*columns);ReflectionHelpers.callInstanceMethod(browser,"renderItems");PreviewPagesTest.layout(browser);assertEquals(current.buildUpon().appendPath("folder"+(20*columns+1)).build().toString(),browser.findFocus().getTag());assertEquals(current,ReflectionHelpers.getField(browser,"current"));
   for(int i=19;i>=0;i--){assertTrue(browser.dispatchKeyEvent(new KeyEvent(KeyEvent.ACTION_DOWN,KeyEvent.KEYCODE_DPAD_UP)));PreviewPagesTest.layout(browser);assertEquals(current.buildUpon().appendPath("folder"+(i*columns)).build().toString(),browser.findFocus().getTag());}
   files.clear();ReflectionHelpers.callInstanceMethod(browser,"renderItems");PreviewPagesTest.layout(browser);assertEquals("Empty folder retains its path",current,ReflectionHelpers.getField(browser,"current"));assertEquals("browser.view",browser.findFocus().getTag());
  }catch(RuntimeException failure){throw new AssertionError("Browser refresh failed; current="+ReflectionHelpers.getField(host.get().findViewById(android.R.id.content).findViewWithTag("browser.probe"),"current"),failure);}finally{host.pause().stop().destroy();}
 }
}
