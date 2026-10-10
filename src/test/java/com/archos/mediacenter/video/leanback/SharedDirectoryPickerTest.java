package com.archos.mediacenter.video.leanback;

import android.app.Application;
import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.view.View;
import com.archos.mediacenter.video.utils.FolderPicker;
import com.archos.mediacenter.video.leanback.filebrowsing.UniversalFileBrowser;
import java.io.File;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.*;
import org.robolectric.annotation.Config;
import org.robolectric.util.ReflectionHelpers;
import static org.junit.Assert.*;

@RunWith(RobolectricTestRunner.class) @Config(application=Application.class,sdk=28,qualifiers="w960dp-h540dp-land-mdpi")
public class SharedDirectoryPickerTest {
 @org.junit.Before public void isolatePreviewTransport(){com.archos.mediacenter.video.leanback.PreviewAsyncFixtures.offlineTransport();}
 @org.junit.After public void drainPreviewWorkers() throws Exception { com.archos.mediacenter.video.leanback.PreviewAsyncFixtures.drain(); }
 @Test public void nativeDownloadPickerUsesSharedBrowserAndPreservesResultPath()throws Exception {
  Application app=RuntimeEnvironment.getApplication();androidx.preference.PreferenceManager.getDefaultSharedPreferences(app).edit().putBoolean("try_new_ui",true).commit();
  File folder=new File(app.getFilesDir(),"download-destination");assertTrue(folder.mkdirs());
  Intent intent=new Intent(app,FolderPicker.class).putExtra(FolderPicker.EXTRA_CURRENT_SELECTION,new File(folder,"existing-child").getAbsolutePath());
  var activity=Robolectric.buildActivity(FolderPicker.class,intent).setup().visible();UniversalFileBrowser browser=ReflectionHelpers.getField(activity.get(),"sharedBrowser");
  try{assertNotNull(browser);assertEquals(Uri.fromFile(folder),browser.location());PreviewPagesTest.layout(browser);View choose=PreviewPagesTest.findText(browser,"Choose This Folder");assertNotNull(choose);assertNull(PreviewPagesTest.findText(browser,"Add to Library"));assertFalse(browser.selections().changed());choose.performClick();assertEquals(Activity.RESULT_OK,Shadows.shadowOf(activity.get()).getResultCode());assertEquals(folder.getAbsolutePath(),Shadows.shadowOf(activity.get()).getResultIntent().getStringExtra(FolderPicker.EXTRA_SELECTED_FOLDER));assertFalse(browser.selections().changed());}
  finally{activity.pause().stop().destroy();}
 }
}
