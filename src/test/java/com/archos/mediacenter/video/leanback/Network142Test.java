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
 private View rail(String name){return browser.findViewWithTag("network.back");}
 private void select(String name){
  if(name.equals("overview")){ReflectionHelpers.callInstanceMethod(browser,"networkLanding");layout();return;}
  int index=name.equals("local_storage")?0:name.equals("network_shares")||name.equals("cloud_services")?1:name.equals("all_files")?0:5;
  ReflectionHelpers.callInstanceMethod(browser,"openLandingCard",ReflectionHelpers.ClassParameter.from(int.class,index));
  if(name.equals("cloud_services"))PreviewPagesTest.findText(browser,"Put.io · Account and Files").performClick();
  if(name.equals("library_health"))PreviewPagesTest.findText(browser,"Library Health").performClick();layout();
 }
 private View exactText(View view,String text){if(view instanceof TextView&&text.contentEquals(((TextView)view).getText()))return view;if(view instanceof ViewGroup)for(int n=0;n<((ViewGroup)view).getChildCount();n++){View found=exactText(((ViewGroup)view).getChildAt(n),text);if(found!=null)return found;}return null;}
 private void key(int code){browser.dispatchKeyEvent(new KeyEvent(KeyEvent.ACTION_DOWN,code));layout();}
 @Test public void sixCardsHaveApprovedOrderAndExplicitActivation()throws Exception{
  String[] expected={"Local Storage","Network Locations","Add Connection","Manage Connections","Network Scanning","Advanced Options"};
  for(int n=0;n<6;n++){View card=browser.findViewWithTag("network.card."+n);assertNotNull(card);assertTrue(card.getContentDescription().toString().startsWith(expected[n]));}
  View first=browser.findViewWithTag("network.card.0");first.requestFocus();key(KeyEvent.KEYCODE_DPAD_RIGHT);assertTrue(browser.findViewWithTag("network.card.1").hasFocus());key(KeyEvent.KEYCODE_DPAD_DOWN);assertTrue(browser.findViewWithTag("network.card.4").hasFocus());assertNotNull(browser.findViewWithTag("network.landing"));
  browser.findViewWithTag("network.card.1").performClick();layout();assertNotNull(PreviewPagesTest.findText(browser,"Add Network Source"));assertNull(browser.findViewWithTag("network.landing"));assertTrue(browser.findViewWithTag("network.back").isShown());
  PreviewPagesTest.capture(browser,"0147-network-locations-empty");
 }
 @Test public void landingBackRestoresSelectedCard(){
  browser.findViewWithTag("network.card.5").performClick();layout();browser.back(()->fail("Must return to landing"));layout();assertTrue(browser.findViewWithTag("network.card.5").hasFocus());
 }
 @Test public void leftAndRightPreserveDeepFolder()throws Exception{
  select("local_storage");Uri deep=Uri.parse("file:///fixture-usb/Movies/Collection");browser.open(deep);layout();rail("local_storage").requestFocus();key(KeyEvent.KEYCODE_DPAD_RIGHT);assertEquals(deep,browser.location());key(KeyEvent.KEYCODE_DPAD_LEFT);assertEquals(deep,browser.location());
 }
 @Test public void switchingSectionsRestoresLastFolder(){select("local_storage");Uri deep=Uri.parse("file:///fixture-usb/Movies");browser.open(deep);select("network_shares");Uri remote=Uri.parse("smb://fixture/Movies/Collection");browser.open(remote);select("local_storage");assertEquals(deep,browser.location());select("network_shares");assertEquals(remote,browser.location());}
 @Test public void everySupportedRecoveryAliasKeepsHostPortPath(){
  for(String scheme:new String[]{"smb","smbj","webdav","webdavs","sftp","sshj","ftp","ftps"}){
   ReflectionHelpers.callInstanceMethod(browser,"launchCredentials",ReflectionHelpers.ClassParameter.from(Uri.class,Uri.parse(scheme+"://NAS:123/Shows/Season%201")));layout();String[] fields=ReflectionHelpers.getField(browser,"connection");assertEquals(scheme,"NAS",fields[0]);assertEquals("123",fields[1]);assertEquals("/Shows/Season 1",fields[2]);assertNotNull(PreviewPagesTest.findText(browser,"Password: "));if(scheme.equals("webdav"))assertNotNull(PreviewPagesTest.findText(browser,"WebDAV HTTP does not encrypt"));
  }
 }
 @Test public void failedNavigationClearsOldFilesAndRetryIsUnique(){
  browser.open(Uri.parse("file:///fixture-usb"));List<MetaFile2> files=ReflectionHelpers.getField(browser,"files");MetaFile2 old=mock(MetaFile2.class);when(old.getName()).thenReturn("old file");when(old.getUri()).thenReturn(Uri.parse("file:///fixture-usb/old.mkv"));files.add(old);
  browser.open(Uri.parse("file:///unavailable-fixture/other"));assertTrue(((List<?>)ReflectionHelpers.getField(browser,"files")).isEmpty());
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
  select("network_shares");PreviewPagesTest.findText(browser,"Add Network Source").performClick();layout();PreviewPagesTest.findText(browser,"WebDAV HTTPS").performClick();layout();assertNotNull(PreviewPagesTest.findText(browser,"Server / Address: "));assertSame("Protocol choice enters the first required input",PreviewPagesTest.findText(browser,"Server / Address: "),browser.findFocus());assertNotNull(PreviewPagesTest.findText(browser,"Save Password: On"));assertTrue(browser.findViewWithTag("network.back").isShown());View username=PreviewPagesTest.findText(browser,"Username: ");username.performClick();android.app.Dialog keyboard=org.robolectric.shadows.ShadowDialog.getLatestDialog();keyboard.getWindow().getDecorView().findViewWithTag("semantic:keyboard:Done").performClick();layout();assertSame("Keyboard Done returns to the edited field",PreviewPagesTest.findText(browser,"Username: "),browser.findFocus());PreviewPagesTest.capture(browser,"0142-connection-form");
  ReflectionHelpers.setField(browser,"connectionDirty",true);browser.back(()->{});assertNotNull(org.robolectric.shadows.ShadowDialog.getLatestDialog());assertEquals(2,(int)ReflectionHelpers.getField(browser,"section"));
 }
 @Test public void sourceRefreshPreservesFocusAndUpdatesVolumeRows(){
  select("local_storage");View drive=browser.findViewWithTag("storage:file:///fixture-usb");assertNotNull(drive);drive.requestFocus();browser.locations(List.of(new Box(Box.ID.USB,"USB fixture",0,"/fixture-usb"),new Box(Box.ID.USB,"Second drive",0,"/fixture-two")),List.of(),List.of(),kind->{});layout();assertEquals("storage:file:///fixture-usb",browser.findFocus().getTag());assertNotNull(browser.findViewWithTag("storage:file:///fixture-two"));
 }
 @Test public void lateDiscoveryCannotReplaceTheSelectedOverview(){
  var discovery=mock(com.archos.filecorelibrary.samba.SambaDiscovery.class);ReflectionHelpers.setField(browser,"discovery",discovery);select("network_shares");select("overview");ReflectionHelpers.callInstanceMethod(browser,"renderDiscovered");assertNotNull(browser.findViewWithTag("network.landing"));verify(discovery).abort();
 }
 @Test public void absentScanOnReturnPreferenceMatchesRuntimeOffDefault(){
  androidx.preference.PreferenceManager.getDefaultSharedPreferences(host.get()).edit().remove("auto_rescan_on_app_restart").commit();select("network_shares");PreviewPagesTest.findText(browser,"Network Scanning").performClick();layout();assertNotNull(PreviewPagesTest.findText(browser,"On Open / Return: Off"));View toggle=(View)PreviewPagesTest.findText(browser,"On Open / Return: Off").getParent();toggle.requestFocus();toggle.performClick();layout();assertTrue(androidx.preference.PreferenceManager.getDefaultSharedPreferences(host.get()).getBoolean("auto_rescan_on_app_restart",false));assertTrue(((View)PreviewPagesTest.findText(browser,"On Open / Return: On").getParent()).hasFocus());
 }
 @Test public void connectionConnectActionScrollsFullyIntoViewWithRemote()throws Exception{
  select("network_shares");PreviewPagesTest.findText(browser,"Add Network Source").performClick();layout();PreviewPagesTest.findText(browser,"WebDAV HTTPS").performClick();layout();
  View connect=exactText(browser,"Connect");assertNotNull(connect);StringBuilder trace=new StringBuilder();for(int n=0;n<16&&!connect.hasFocus();n++){View focus=browser.findFocus();trace.append(focus instanceof TextView?((TextView)focus).getText():focus==null?"null":focus.getClass().getSimpleName()).append(" -> ");key(KeyEvent.KEYCODE_DPAD_DOWN);}PreviewPagesTest.capture(browser,"0142-form-connect-focus");assertTrue("D-pad reaches Connect below form controls: "+trace,connect.hasFocus());android.graphics.Rect visible=new android.graphics.Rect();assertTrue(connect.getGlobalVisibleRect(visible));assertEquals("Focused action is fully revealed",connect.getHeight(),visible.height());
 }
 @Test public void navigationEntryPreservesTheOpenFolder(){
  select("local_storage");Uri deep=Uri.parse("file:///fixture-usb/Movies/Series");browser.open(deep);layout();assertTrue(browser.focusOverview());layout();assertEquals(deep,browser.location());
 }
 @Test public void cloudAndHealthRemainReachable()throws Exception{select("cloud_services");assertNotNull(PreviewPagesTest.findText(browser,"Enabled: On"));View enabled=(View)PreviewPagesTest.findText(browser,"Enabled: On").getParent();enabled.requestFocus();enabled.performClick();layout();assertTrue(((View)PreviewPagesTest.findText(browser,"Enabled: Off").getParent()).hasFocus());PreviewPagesTest.capture(browser,"0142-cloud-disconnected");select("library_health");for(String name:LibraryHealthPanel.NAMES)assertNotNull(name,PreviewPagesTest.findText(browser,name));PreviewPagesTest.capture(browser,"0142-library-health");}
}
