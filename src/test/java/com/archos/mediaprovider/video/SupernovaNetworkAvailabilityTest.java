package com.archos.mediaprovider.video;

import android.app.Application;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import com.archos.filecorelibrary.MetaFile2;
import com.archos.mediaprovider.ArchosMediaCommon;
import java.util.HashMap;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.*;
import org.robolectric.annotation.Config;
import org.robolectric.annotation.SQLiteMode;
import org.robolectric.util.ReflectionHelpers;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith(RobolectricTestRunner.class) @Config(application=Application.class,sdk=28)
@SQLiteMode(SQLiteMode.Mode.NATIVE)
public class SupernovaNetworkAvailabilityTest {
 @Test @SuppressWarnings({"rawtypes","unchecked"}) public void scannerAvailabilityUsesTheNativeMediaIdAndReconnectionClearsIt()throws Exception {
  Application app=RuntimeEnvironment.getApplication();VideoOpenHelper helper=new VideoOpenHelper(app);SQLiteDatabase database=helper.getWritableDatabase();
  long scannedId=77,mediaId=scannedId+ArchosMediaCommon.SCANNED_ID_OFFSET;String path="smb://nas/Movies/film.mkv";
  database.execSQL("INSERT INTO files_scanned(_id,_data,media_type,date_modified) VALUES(77,'smb://nas/Movies/film.mkv',3,100)");database.execSQL("UPDATE files SET Archos_bookmark=123456 WHERE _id="+mediaId);
  Class itemType=Class.forName(NetworkScannerServiceVideo.class.getName()+"$PrescanItem");MatrixCursor cursor=new MatrixCursor(new String[]{"_id","_data","date_modified","unique_id"});cursor.addRow(new Object[]{scannedId,path,100,null});assertTrue(cursor.moveToFirst());Object item=ReflectionHelpers.callConstructor(itemType,ReflectionHelpers.ClassParameter.from(Cursor.class,cursor));HashMap map=new HashMap();map.put(path,item);
  Class bulkType=Class.forName(NetworkScannerServiceVideo.class.getName()+"$BulkOperationHandler");Object bulk=mock(bulkType);NetworkScannerServiceVideo service=Robolectric.buildService(NetworkScannerServiceVideo.class).get();
  Class listenerType=Class.forName(NetworkScannerServiceVideo.class.getName()+"$FileVisitListener");Object listener=ReflectionHelpers.callConstructor(listenerType,ReflectionHelpers.ClassParameter.from(Blacklist.class,mock(Blacklist.class)),ReflectionHelpers.ClassParameter.from(HashMap.class,map),ReflectionHelpers.ClassParameter.from(boolean.class,false),ReflectionHelpers.ClassParameter.from(bulkType,bulk),ReflectionHelpers.ClassParameter.from(long.class,0L),ReflectionHelpers.ClassParameter.from(NetworkScannerServiceVideo.class,service));
  MetaFile2 root=mock(MetaFile2.class);when(root.getUri()).thenReturn(Uri.parse("smb://nas/Movies"));
  try {ReflectionHelpers.callInstanceMethod(listener,"onStop",ReflectionHelpers.ClassParameter.from(MetaFile2.class,root));var health=app.getSharedPreferences(SupernovaLibraryPolicy.HEALTH,0);assertTrue("Availability is keyed to the library/video ID, not files_scanned ID",health.contains("media:"+mediaId));assertFalse(health.contains("media:"+scannedId));assertFalse(ReflectionHelpers.<Boolean>getField(item,"needsDelete"));
   try(Cursor retained=database.rawQuery("SELECT _id,Archos_bookmark FROM video WHERE _id="+mediaId,null)){assertTrue(retained.moveToFirst());assertEquals(mediaId,retained.getLong(0));assertEquals(123456,retained.getInt(1));}
   MetaFile2 file=mock(MetaFile2.class);when(file.getUri()).thenReturn(Uri.parse(path));when(file.getName()).thenReturn("film.mkv");when(file.getExtension()).thenReturn("mkv");when(file.lastModified()).thenReturn(100000L);ReflectionHelpers.callInstanceMethod(listener,"onFile",ReflectionHelpers.ClassParameter.from(MetaFile2.class,file));assertFalse("Reconnecting the same native ID clears unavailable state",health.contains("media:"+mediaId));
  } finally {cursor.close();helper.close();}
 }
}
