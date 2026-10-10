package com.archos.mediaprovider.video;

import android.app.Application;
import android.content.*;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.*;
import org.robolectric.annotation.Config;
import org.robolectric.annotation.SQLiteMode;
import org.robolectric.shadows.ShadowContentResolver;
import org.robolectric.util.ReflectionHelpers;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith(RobolectricTestRunner.class) @Config(application=Application.class,sdk=26)
@SQLiteMode(SQLiteMode.Mode.NATIVE)
public class SupernovaLegacyVolumeVisibilityTest {
 @Test public void oldStorageIdImportKeepsHiddenUpgradeRecordVisibleAndBookmarkIntact() {
  Application app=RuntimeEnvironment.getApplication();VideoOpenHelper helper=new VideoOpenHelper(app);SQLiteDatabase database=helper.getWritableDatabase();
  ContentProvider provider=new ContentProvider(){public boolean onCreate(){return true;}public String getType(Uri uri){return null;}public Cursor query(Uri uri,String[] projection,String where,String[] args,String sort){throw new AssertionError("No storage query required");}public Uri insert(Uri uri,ContentValues values){throw new AssertionError("No insert required");}public int delete(Uri uri,String where,String[] args){throw new AssertionError("No deletion allowed");}public int update(Uri uri,ContentValues values,String where,String[] args){assertEquals(VideoStoreInternal.FILES_IMPORT,uri);assertEquals(Integer.valueOf(0),values.getAsInteger("volume_hidden"));return database.update(VideoOpenHelper.FILES_IMPORT_TABLE_NAME,values,where,args);}};
  provider.attachInfo(app,null);ShadowContentResolver.registerProviderInternal(VideoStore.AUTHORITY,provider);
  try {database.execSQL("INSERT INTO files_import(_id,_data) VALUES(77,'/storage/OFFLINE-USB/Movies/film.mkv')");database.execSQL("UPDATE files SET media_type=3,Archos_bookmark=123456 WHERE _id=77");
   database.execSQL("UPDATE files_import SET volume_hidden=1000 WHERE _id=77");
   try(Cursor hidden=database.rawQuery("SELECT count(*) FROM video WHERE _id=77",null)){assertTrue(hidden.moveToFirst());assertEquals(0,hidden.getInt(0));}
   VideoStoreImportImpl importer=mock(VideoStoreImportImpl.class,CALLS_REAL_METHODS);ReflectionHelpers.setField(importer,"mContext",app);ReflectionHelpers.setField(importer,"mCr",app.getContentResolver());assertTrue(ReflectionHelpers.<Boolean>getStaticField(VideoStoreImportImpl.class,"sRemoteProjectionHasStorageId"));ReflectionHelpers.callInstanceMethod(importer,"updateVolumeHiddenStates",ReflectionHelpers.ClassParameter.from(String.class,""));
   try(Cursor retained=database.rawQuery("SELECT _id,Archos_bookmark FROM video WHERE _id=77",null)){assertTrue("Offline record remains in the native library view",retained.moveToFirst());assertEquals(77,retained.getLong(0));assertEquals(123456,retained.getInt(1));assertFalse(retained.moveToNext());}
  } finally {helper.close();}
 }
}
