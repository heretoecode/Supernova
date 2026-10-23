package com.archos.mediacenter.video.leanback;

import android.app.Application;
import android.net.Uri;
import android.view.*;
import android.widget.*;
import com.archos.mediacenter.video.leanback.adapter.object.*;
import com.archos.mediacenter.video.leanback.filebrowsing.UniversalFileBrowser;
import com.archos.filecorelibrary.MetaFile2;
import org.junit.*;
import org.junit.runner.RunWith;
import org.robolectric.*;
import org.robolectric.annotation.*;
import org.robolectric.util.ReflectionHelpers;
import java.util.*;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith(RobolectricTestRunner.class)
@Config(application=Application.class,sdk=30,qualifiers="w960dp-h540dp-land-xhdpi")
@GraphicsMode(GraphicsMode.Mode.NATIVE)
public class Network142Test {
 private org.robolectric.android.controller.ActivityController<TopNavigationTest.Host> host;
 private UniversalFileBrowser browser;
 @Before public void setup(){PreviewAsyncFixtures.offlineTransport();host=Robolectric.buildActivity(TopNavigationTest.Host.class).setup().visible();androidx.preference.PreferenceManager.getDefaultSharedPreferences(host.get()).edit().putBoolean("supernova_onboarding_complete",true).commit();browser=new UniversalFileBrowser(host.get());browser.networkWorkspace();host.get().setContentView(browser);browser.locations(List.of(new Box(Box.ID.USB,"USB fixture",0,"/fixture-usb")),List.of(),List.of(),kind->{});layout();}
 @After public void close()throws Exception{browser.close();host.pause().stop().destroy();PreviewAsyncFixtures.drain();}
 private void layout(){for(int n=0;n<4;n++){browser.measure(View.MeasureSpec.makeMeasureSpec(1920,View.MeasureSpec.EXACTLY),View.MeasureSpec.makeMeasureSpec(1080,View.MeasureSpec.EXACTLY));browser.layout(0,0,1920,1080);Shadows.shadowOf(android.os.Looper.getMainLooper()).idleFor(java.time.Duration.ofMillis(30));}}
 private View rail(String name){return browser.findViewWithTag("semantic:network.category."+name);}
 private void select(String name){rail(name).requestFocus();rail(name).performClick();layout();}
 private void key(int code){browser.dispatchKeyEvent(new KeyEvent(KeyEvent.ACTION_DOWN,code));layout();}
 @Test public void sixSectionsUseExplicitActivationAndFixedRail()throws Exception{
  LinearLayout left=ReflectionHelpers.getField(browser,"left");assertEquals(6,left.getChildCount());assertEquals("Overview",((TextView)left.getChildAt(0)).getText());
  rail("network_shares").requestFocus();assertNotNull(PreviewPagesTest.findText(browser,"Your storage, connections and library at a glance"));
  rail("network_shares").performClick();layout();assertNotNull(PreviewPagesTest.findText(browser,"Add Network Source"));assertEquals(6,left.getChildCount());
  key(KeyEvent.KEYCODE_DPAD_RIGHT);assertFalse(left.hasFocus());key(KeyEvent.KEYCODE_DPAD_LEFT);assertSame(rail("network_shares"),browser.findFocus());
  PreviewPagesTest.capture(browser,"0142-network-shares-empty");
 }
 @Test public void overviewHasFourCardsAndTruthfulStatusWithoutQuickActions()throws Exception{
  assertNotNull(PreviewPagesTest.findText(browser,"Storage  ›"));assertNotNull(PreviewPagesTest.findText(browser,"Network Shares  ›"));assertNotNull(PreviewPagesTest.findText(browser,"Cloud Services  ›"));assertNotNull(PreviewPagesTest.findText(browser,"Library Status  ›"));assertNull(PreviewPagesTest.findText(browser,"Quick Actions"));
  rail("overview").requestFocus();PreviewPagesTest.capture(browser,"0142-overview");
 }
 @Test public void leftAndRightPreserveDeepFolder()throws Exception{
  select("local_storage");Uri deep=Uri.parse("file:///fixture-usb/Movies/Collection");browser.open(deep);layout();rail("local_storage").requestFocus();key(KeyEvent.KEYCODE_DPAD_RIGHT);assertEquals(deep,browser.location());key(KeyEvent.KEYCODE_DPAD_LEFT);assertEquals(deep,browser.location());
 }
 @Test public void switchingSectionsRestoresLastFolder(){select("local_storage");Uri deep=Uri.parse("file:///fixture-usb/Movies");browser.open(deep);select("network_shares");select("local_storage");assertEquals(deep,browser.location());}
 @Test public void everySupportedRecoveryAliasKeepsHostPortPath(){
  for(String scheme:new String[]{"smb","smbj","webdav","webdavs","sftp","sshj","ftp","ftps"}){
   ReflectionHelpers.callInstanceMethod(browser,"launchCredentials",ReflectionHelpers.ClassParameter.from(Uri.class,Uri.parse(scheme+"://NAS:123/Shows/Season%201")));layout();String[] fields=ReflectionHelpers.getField(browser,"connection");assertEquals(scheme,"NAS",fields[0]);assertEquals("123",fields[1]);assertEquals("/Shows/Season 1",fields[2]);assertNotNull(PreviewPagesTest.findText(browser,"Password: "));
  }
 }
 @Test public void failedNavigationClearsOldFilesAndRetryIsUnique(){
  browser.open(Uri.parse("file:///fixture-usb"));List<MetaFile2> files=ReflectionHelpers.getField(browser,"files");MetaFile2 old=mock(MetaFile2.class);when(old.getName()).thenReturn("old file");when(old.getUri()).thenReturn(Uri.parse("file:///fixture-usb/old.mkv"));files.add(old);
  browser.open(Uri.parse("webdavs://unavailable.test/other"));assertTrue(((List<?>)ReflectionHelpers.getField(browser,"files")).isEmpty());
  for(int i=0;i<4;i++)ReflectionHelpers.callInstanceMethod(browser,"failed",ReflectionHelpers.ClassParameter.from(String.class,"Fixture failure"));
  LinearLayout actions=ReflectionHelpers.getField(browser,"actions");int retries=0;for(int n=0;n<actions.getChildCount();n++)if(actions.getChildAt(n) instanceof TextView&&((TextView)actions.getChildAt(n)).getText().equals("Retry"))retries++;assertEquals(1,retries);
 }
 @Test public void healthActionEntrySkipsPlainHeading(){
  ReflectionHelpers.callInstanceMethod(browser,"health",ReflectionHelpers.ClassParameter.from(int.class,4));LinearLayout actions=ReflectionHelpers.getField(browser,"actions");actions.removeAllViews();SharedThreePanel.heading(actions,"Source Problem");TextView scan=SharedThreePanel.accentAction(host.get(),"Scan Library",()->{});actions.addView(scan);layout();ReflectionHelpers.callInstanceMethod(browser,"focusActions");assertSame(scan,browser.findFocus());
 }
 @Test public void sortUsesReturnedMetadataEvenWhenProviderDoesNotSort(){
  MetaFile2 a=mock(MetaFile2.class),z=mock(MetaFile2.class);when(a.getName()).thenReturn("A");when(z.getName()).thenReturn("Z");when(a.length()).thenReturn(90L);when(z.length()).thenReturn(10L);List<MetaFile2> files=ReflectionHelpers.getField(browser,"files");files.add(a);files.add(z);
  androidx.preference.PreferenceManager.getDefaultSharedPreferences(host.get()).edit().putInt("supernova_browser_sort",1).commit();ReflectionHelpers.callInstanceMethod(browser,"sortFiles");assertSame(z,files.get(0));
  androidx.preference.PreferenceManager.getDefaultSharedPreferences(host.get()).edit().putInt("supernova_browser_sort",4).commit();ReflectionHelpers.callInstanceMethod(browser,"sortFiles");assertSame(z,files.get(0));
 }
 @Test public void connectionFormRetainsRailAndAllSupportedFields()throws Exception{
  select("network_shares");PreviewPagesTest.findText(browser,"Add Network Source").performClick();layout();PreviewPagesTest.findText(browser,"WebDAV HTTPS").performClick();layout();assertNotNull(PreviewPagesTest.findText(browser,"Server / Address: "));assertNotNull(PreviewPagesTest.findText(browser,"Save Password: On"));assertTrue(rail("network_shares").isShown());PreviewPagesTest.capture(browser,"0142-connection-form");
  ReflectionHelpers.setField(browser,"connectionDirty",true);select("local_storage");assertNotNull(org.robolectric.shadows.ShadowDialog.getLatestDialog());assertEquals(2,(int)ReflectionHelpers.getField(browser,"section"));
 }
 @Test public void sourceRefreshPreservesFocusAndUpdatesVolumeRows(){
  select("local_storage");View drive=browser.findViewWithTag("storage:file:///fixture-usb");assertNotNull(drive);drive.requestFocus();browser.locations(List.of(new Box(Box.ID.USB,"USB fixture",0,"/fixture-usb"),new Box(Box.ID.USB,"Second drive",0,"/fixture-two")),List.of(),List.of(),kind->{});layout();assertEquals("storage:file:///fixture-usb",browser.findFocus().getTag());assertNotNull(browser.findViewWithTag("storage:file:///fixture-two"));
 }
 @Test public void lateDiscoveryCannotReplaceTheSelectedOverview(){
  var discovery=mock(com.archos.filecorelibrary.samba.SambaDiscovery.class);ReflectionHelpers.setField(browser,"discovery",discovery);select("network_shares");select("overview");ReflectionHelpers.callInstanceMethod(browser,"renderDiscovered");assertNotNull(PreviewPagesTest.findText(browser,"Your storage, connections and library at a glance"));verify(discovery).abort();
 }
 @Test public void absentScanOnReturnPreferenceMatchesRuntimeOffDefault(){
  androidx.preference.PreferenceManager.getDefaultSharedPreferences(host.get()).edit().remove("auto_rescan_on_app_restart").commit();select("network_shares");PreviewPagesTest.findText(browser,"Network Scanning").performClick();layout();assertNotNull(PreviewPagesTest.findText(browser,"On Open / Return: Off"));
 }
 @Test public void cloudAndHealthRemainReachable()throws Exception{select("cloud_services");assertNotNull(PreviewPagesTest.findText(browser,"Enabled: On"));PreviewPagesTest.capture(browser,"0142-cloud-disconnected");select("library_health");for(String name:LibraryHealthPanel.NAMES)assertNotNull(name,PreviewPagesTest.findText(browser,name));PreviewPagesTest.capture(browser,"0142-library-health");}
}
