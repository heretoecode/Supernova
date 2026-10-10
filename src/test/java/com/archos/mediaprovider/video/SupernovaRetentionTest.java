package com.archos.mediaprovider.video;
import android.app.Application;
import android.content.*;
import android.net.Uri;
import java.io.File;
import java.lang.reflect.*;
import java.util.*;
import org.junit.*;
import org.junit.runner.RunWith;
import org.robolectric.*;
import org.robolectric.annotation.Config;
import org.robolectric.util.ReflectionHelpers;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;
@RunWith(RobolectricTestRunner.class) @Config(application=Application.class,sdk=28)
public class SupernovaRetentionTest {
 @org.junit.Before public void confirmedSelectionFixture(){androidx.preference.PreferenceManager.getDefaultSharedPreferences(org.robolectric.RuntimeEnvironment.getApplication()).edit().putBoolean("supernova_onboarding_complete",true).commit();}

 @Test public void missingPrimaryFileReconciliationRetainsStableRecordAndNeverDeletes()throws Exception{
  Context c=RuntimeEnvironment.getApplication();assertTrue(SupernovaLibraryPolicy.retainsRecords(c));File folder=new File(c.getCacheDir(),"mounted-fixture");folder.mkdirs();String path=new File(folder,"missing.mkv").getPath();ContentResolver resolver=mock(ContentResolver.class);
  Class<?> identityClass=Class.forName("com.archos.mediaprovider.video.VideoStoreImportImpl$ImportIdentity");Constructor<?> constructor=identityClass.getDeclaredConstructors()[0];constructor.setAccessible(true);Object identity=constructor.newInstance(77L,path,"Missing",1000L,10L);
  VideoStoreImportImpl.ReconciliationSnapshot snapshot=new VideoStoreImportImpl.ReconciliationSnapshot();Map<Long,Object> imported=ReflectionHelpers.getField(snapshot,"importedById");imported.put(77L,identity);snapshot.importedIds.add(77L);snapshot.importedLocations.put(77L,new VideoStoreImportImpl.StorageLocation(folder.getPath(),0,true));
  var result=VideoStoreImportImpl.reconcileStorageSnapshot(resolver,snapshot,c);assertEquals(0,result.removed);assertTrue(snapshot.importedIds.contains(77L));verifyNoInteractions(resolver);assertTrue(c.getSharedPreferences(SupernovaLibraryPolicy.HEALTH,0).contains("media:77"));
  new File(path).createNewFile();VideoStoreImportImpl.reconcileStorageSnapshot(resolver,snapshot,c);assertFalse(c.getSharedPreferences(SupernovaLibraryPolicy.HEALTH,0).contains("media:77"));assertTrue(snapshot.importedIds.contains(77L));
 }
 @Test public void unavailableVolumeDoesNotTriggerHistoricalPurge(){Context c=RuntimeEnvironment.getApplication();ContentResolver resolver=mock(ContentResolver.class);VideoStoreImportImpl.purgeExpiredHiddenFiles(resolver,c);verifyNoInteractions(resolver);}
 @Test public void nativeAbsolutePathsMatchFileUriSelectionsAndOfflineVolumeRoots(){Context c=RuntimeEnvironment.getApplication();androidx.preference.PreferenceManager.getDefaultSharedPreferences(c).edit().putBoolean("supernova_library_policy_initialized",true).putStringSet("supernova_library_roots",Set.of("file:///storage/ABCD-1234")).commit();assertFalse(SupernovaLibraryPolicy.excluded(c,Uri.parse("/storage/ABCD-1234/Movies/film.mkv")));assertEquals(Uri.parse("file:///storage/ABCD-1234"),SupernovaLibraryPolicy.localRoot(Uri.parse("/storage/ABCD-1234/Movies/film.mkv")));assertEquals(Uri.parse("file:///storage/emulated/0"),SupernovaLibraryPolicy.localRoot(Uri.parse("/storage/emulated/0/Movies/film.mkv")));}
 @Test public void exclusionsArePersistentAndFutureSiblingsRemainMonitored(){Context c=RuntimeEnvironment.getApplication();var p=androidx.preference.PreferenceManager.getDefaultSharedPreferences(c);p.edit().putBoolean("supernova_library_policy_initialized",true).putStringSet("supernova_library_roots",Set.of("smb://nas/media")).putStringSet("supernova_library_exclusions",Set.of("smb://nas/media/kids")).commit();assertTrue(SupernovaLibraryPolicy.excluded(c,Uri.parse("smb://nas/media/kids/film.mkv")));assertFalse(SupernovaLibraryPolicy.excluded(c,Uri.parse("smb://nas/media/new/film.mkv")));assertTrue(SupernovaLibraryPolicy.excluded(c,Uri.parse("smb://nas/media2/film.mkv")));}
}
