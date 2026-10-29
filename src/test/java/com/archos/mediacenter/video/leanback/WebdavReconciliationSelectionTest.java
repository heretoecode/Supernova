package com.archos.mediacenter.video.leanback;

import android.app.Application;
import android.database.MatrixCursor;
import android.net.Uri;
import com.archos.filecorelibrary.MetaFile2;
import com.archos.mediaprovider.video.*;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.*;
import org.robolectric.annotation.Config;
import org.robolectric.util.ReflectionHelpers;
import java.util.*;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

/** Exercise the production scanner's deletion selection, without destructive provider writes. */
@RunWith(RobolectricTestRunner.class) @Config(application=Application.class,sdk=28)
public class WebdavReconciliationSelectionTest {
 private Object record(long id,String path,boolean present)throws Exception{
  MatrixCursor cursor=new MatrixCursor(new String[]{"_id","_data","date_modified","unique_id"});cursor.addRow(new Object[]{id,path,0,null});cursor.moveToFirst();
  var type=Class.forName("com.archos.mediaprovider.video.NetworkScannerServiceVideo$PrescanItem");var constructor=type.getDeclaredConstructors()[0];constructor.setAccessible(true);Object item=constructor.newInstance(cursor);cursor.close();ReflectionHelpers.setField(item,"needsDelete",!present);return item;
 }
 @Test public void completeListingDeletesOnlyMissingFilesAndKeepsExcludedSubtreesAndPeers()throws Exception{check(false);}
 @Test public void anyListingFailureRetainsAllFiles()throws Exception{check(true);}
 private HashMap<String,Object> decodedIndex(HashMap<String,Object> records){
  HashMap<String,Object> decoded=new HashMap<>();for(Object item:records.values()){String path=ReflectionHelpers.getField(item,"_data");decoded.put(com.archos.filecorelibrary.FileUtils.decodeUri(Uri.parse(path)),item);}return decoded;
 }
 private void check(boolean failed)throws Exception{
  Application app=RuntimeEnvironment.getApplication();com.archos.environment.ArchosUtils.setGlobalContext(app);androidx.preference.PreferenceManager.getDefaultSharedPreferences(app).edit().clear().putBoolean("supernova_onboarding_complete",true).commit();
  NetworkScannerServiceVideo service=Robolectric.buildService(NetworkScannerServiceVideo.class).get();assertTrue(SupernovaLibraryPolicy.retainsRecords(service));
  HashMap<String,Object> records=new HashMap<>();records.put("a",record(1,"webdav://fixture/root/missing-a.mkv",false));records.put("b",record(2,"webdav://fixture/root/missing-b.mkv",false));records.put("present",record(3,"webdav://fixture/root/alternate.mkv",true));records.put("excluded",record(4,"webdav://fixture/root/skip/private.mkv",false));records.put("peer",record(5,"webdav://fixture/root/skip-peer/missing.mkv",false));
  Class<?> bulkType=Class.forName("com.archos.mediaprovider.video.NetworkScannerServiceVideo$BulkOperationHandler");Object bulk=mock(bulkType);
  Class<?> listenerType=Class.forName("com.archos.mediaprovider.video.NetworkScannerServiceVideo$FileVisitListener");var constructor=listenerType.getDeclaredConstructor(Blacklist.class,HashMap.class,HashMap.class,boolean.class,bulkType,long.class,NetworkScannerServiceVideo.class);constructor.setAccessible(true);
  FileVisitor.Listener listener=(FileVisitor.Listener)constructor.newInstance(mock(Blacklist.class),records,decodedIndex(records),false,bulk,0L,service);
  MetaFile2 skipped=mock(MetaFile2.class);when(skipped.getUri()).thenReturn(Uri.parse("webdav://fixture/root/skip"));listener.onDirectorySkipped(skipped);
  assertFalse((Boolean)ReflectionHelpers.getField(records.get("excluded"),"needsDelete"));assertTrue((Boolean)ReflectionHelpers.getField(records.get("peer"),"needsDelete"));
  MetaFile2 root=mock(MetaFile2.class);when(root.getUri()).thenReturn(Uri.parse("webdav://fixture/root"));when(root.isDirectory()).thenReturn(true);
  if(failed)listener.onListingError(root,new java.io.IOException("timeout"));listener.onStop(root);
  List<Long> selected=new ArrayList<>();for(var call:mockingDetails(bulk).getInvocations())if(call.getMethod().getName().equals("executeDelete"))selected.addAll((List<Long>)ReflectionHelpers.getField(call.getArgument(0),"mIds"));
  Collections.sort(selected);assertEquals(failed?Collections.emptyList():Arrays.asList(1L,2L,5L),selected);
 }
}
