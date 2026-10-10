package com.archos.mediacenter.video.leanback;

import android.app.Application;
import android.content.*;
import android.database.MatrixCursor;
import android.net.Uri;
import androidx.preference.PreferenceManager;
import com.archos.mediaprovider.video.SupernovaLibraryPolicy;
import com.archos.mediaprovider.video.NetworkAutoRefresh;
import org.junit.*;
import org.junit.runner.RunWith;
import org.robolectric.*;
import org.robolectric.annotation.Config;
import java.util.*;
import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@RunWith(RobolectricTestRunner.class) @Config(application=Application.class,sdk=28)
public class SetupPolicyTest {
 private Context context;private ContentResolver resolver;
 @Before public void setup(){
  Context app=RuntimeEnvironment.getApplication();context=mock(Context.class);resolver=mock(ContentResolver.class);
  when(context.getPackageName()).thenReturn("app.supernova.player");when(context.getApplicationContext()).thenReturn(context);when(context.getContentResolver()).thenReturn(resolver);
  when(context.getSharedPreferences(anyString(),anyInt())).thenAnswer(call->app.getSharedPreferences(call.getArgument(0),call.getArgument(1)));
  PreferenceManager.getDefaultSharedPreferences(context).edit().clear().putBoolean("try_new_ui",true).commit();
 }
 private void inventory(String...paths){when(resolver.query(any(Uri.class),any(String[].class),nullable(String.class),nullable(String[].class),nullable(String.class))).thenAnswer(call->{
  MatrixCursor result=new MatrixCursor(new String[]{"_data"});String selection=call.getArgument(2);
  for(String path:paths)if(selection==null||path.startsWith("/")||path.startsWith("file:"))result.addRow(new Object[]{path});return result;
 });}
 @Test public void freshInventoryNeverEnablesAutomationOrMetadataBeforeConfirmation(){
  inventory();SupernovaLibraryPolicy.initialiseSetup(context);
  assertFalse(SupernovaLibraryPolicy.configured(context));assertTrue(SupernovaLibraryPolicy.excluded(context,Uri.parse("file:///storage/ABCD/Movies/new.mkv")));
  PreviewAutoScanPolicy.initialise(context);assertFalse(PreferenceManager.getDefaultSharedPreferences(context).contains(NetworkAutoRefresh.AUTO_RESCAN_PERIOD));
  assertFalse(com.archos.mediascraper.AutoScrapeService.requestService(context));verify(context,never()).startService(any());verify(context,never()).startForegroundService(any());
  assertTrue(PreferenceManager.getDefaultSharedPreferences(context).getStringSet("supernova_library_roots",Set.of()).isEmpty());
  inventory("/storage/ABCD/Movies/new.mkv");SupernovaLibraryPolicy.initialiseSetup(context);assertFalse("A completed fresh-inventory decision cannot be replaced by drive enumeration",SupernovaLibraryPolicy.configured(context));
 }
 @Test public void upgradeRetainsActualOfflineAndNetworkInventoryWithoutAddingOtherDrives(){
  inventory("/storage/OLD/Movies/kept.mkv","smb://NAS/Shows/ep1.mkv");SupernovaLibraryPolicy.initialiseSetup(context);assertTrue(SupernovaLibraryPolicy.configured(context));
  Set<String> roots=PreferenceManager.getDefaultSharedPreferences(context).getStringSet("supernova_library_roots",Set.of());assertEquals(Set.of("file:///storage/OLD","smb://nas/Shows"),roots);
  assertFalse(SupernovaLibraryPolicy.excluded(context,Uri.parse("file:///storage/OLD/Movies/kept.mkv")));assertTrue(SupernovaLibraryPolicy.excluded(context,Uri.parse("file:///storage/NEW/unselected.mkv")));
 }
 @Test public void failedInventoryReadPreservesExistingPreferencesAndLeavesSetupUnconfirmed(){
  var prefs=PreferenceManager.getDefaultSharedPreferences(context);prefs.edit().putStringSet("supernova_library_roots",Set.of("file:///storage/KEEP")).commit();
  try{SupernovaLibraryPolicy.initialiseSetup(context);fail("Unavailable inventory must be retried rather than committed as empty");}catch(IllegalStateException expected){}
  assertFalse(prefs.contains("supernova_onboarding_complete"));assertFalse(prefs.contains("supernova_library_policy_initialized"));assertEquals(Set.of("file:///storage/KEEP"),prefs.getStringSet("supernova_library_roots",Set.of()));
 }
}
