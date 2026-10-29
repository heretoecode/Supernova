package com.archos.mediacenter.video.streaming.putio;

import android.app.*;
import android.view.*;
import android.widget.*;
import com.archos.mediacenter.video.leanback.filebrowsing.UniversalFileBrowser;
import org.junit.*;
import org.junit.runner.RunWith;
import org.robolectric.*;
import org.robolectric.annotation.Config;
import org.robolectric.util.ReflectionHelpers;
import java.util.*;
import java.util.concurrent.*;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith(RobolectricTestRunner.class) @Config(application=Application.class,sdk=30,qualifiers="w960dp-h540dp-land-mdpi")
public class Network142PutioTest {
 private org.robolectric.android.controller.ActivityController<Activity> host;private UniversalFileBrowser browser;private PutioBrowserController controller;
 @Before public void setup(){host=Robolectric.buildActivity(Activity.class).setup().visible();androidx.preference.PreferenceManager.getDefaultSharedPreferences(host.get()).edit().putBoolean("supernova_onboarding_complete",true).commit();browser=new UniversalFileBrowser(host.get());browser.networkWorkspace();browser.locations(List.of(),List.of(),List.of(),s->{});host.get().setContentView(browser);layout();}
 @After public void close(){if(controller!=null)controller.close();browser.close();host.pause().stop().destroy();}
 private void layout(){for(int n=0;n<3;n++){browser.measure(View.MeasureSpec.makeMeasureSpec(960,View.MeasureSpec.EXACTLY),View.MeasureSpec.makeMeasureSpec(540,View.MeasureSpec.EXACTLY));browser.layout(0,0,960,540);Shadows.shadowOf(android.os.Looper.getMainLooper()).idle();}}
 private void drain()throws Exception{Future<?> task=ReflectionHelpers.getField(controller,"task");if(task!=null)try{task.get(5,TimeUnit.SECONDS);}catch(CancellationException ignored){}layout();}
 @Test public void embeddedBrowseUsesFullWorkspaceAndRestoresParentSelection()throws Exception{
  PutioReadClient client=mock(PutioReadClient.class);var folder=new PutioReadClient.Item(100,0,0,"Movies","FOLDER");when(client.list(0,null)).thenReturn(new PutioReadClient.Page(List.of(folder),null,1));when(client.list(100,null)).thenReturn(new PutioReadClient.Page(List.of(),null,0));
  controller=new PutioBrowserController(host.get(),browser,42,0,"put.io",()->{},()->{},client);drain();LinearLayout left=ReflectionHelpers.getField(browser,"left");assertNull("No permanent navigation rail",left.getParent());assertTrue(browser.findViewWithTag("network.back").isShown());View row=browser.findViewWithTag("semantic:putio.file.100");assertNotNull(row);row.requestFocus();row.performClick();drain();assertEquals(100,controller.folder());controller.back();drain();assertEquals(0,controller.folder());assertEquals("semantic:putio.file.100",browser.findFocus().getTag());assertNotNull(browser.findViewWithTag("browser.provider_options"));
 }
 @Test public void closingDuringListingRejectsLateRowsAndDialogs()throws Exception{
  PutioReadClient client=mock(PutioReadClient.class);CountDownLatch started=new CountDownLatch(1),release=new CountDownLatch(1);when(client.list(0,null)).thenAnswer(call->{started.countDown();release.await(5,TimeUnit.SECONDS);return new PutioReadClient.Page(List.of(new PutioReadClient.Item(10,0,42,"Late file","VIDEO")),null,1);});
  controller=new PutioBrowserController(host.get(),browser,42,0,"put.io",()->{},()->{},client);assertTrue(started.await(5,TimeUnit.SECONDS));controller.close();release.countDown();drain();assertNull(browser.findViewWithTag("semantic:putio.file.10"));Dialog dialog=org.robolectric.shadows.ShadowDialog.getLatestDialog();assertTrue(dialog==null||!dialog.isShowing());
 }
 @Test public void providerOptionsAreReachableAndCloseOwnsTheDialog()throws Exception{
  PutioReadClient client=mock(PutioReadClient.class);when(client.list(0,null)).thenReturn(new PutioReadClient.Page(List.of(),null,0));controller=new PutioBrowserController(host.get(),browser,42,0,"put.io",()->{},()->{},client);drain();browser.findViewWithTag("browser.provider_options").performClick();Dialog dialog=org.robolectric.shadows.ShadowDialog.getLatestDialog();assertTrue(dialog.isShowing());controller.close();assertFalse(dialog.isShowing());
 }
}
