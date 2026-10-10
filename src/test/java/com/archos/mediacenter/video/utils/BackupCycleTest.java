package com.archos.mediacenter.video.utils;
import android.app.Application;
import android.content.*;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import com.archos.mediaprovider.video.VideoOpenHelper;
import java.io.*;
import java.util.*;
import java.util.zip.*;
import org.junit.*;
import org.junit.runner.RunWith;
import org.robolectric.*;
import org.robolectric.annotation.Config;
import org.robolectric.util.ReflectionHelpers;
import static org.junit.Assert.*;
@RunWith(RobolectricTestRunner.class) @Config(application=Application.class,sdk=28)
public class BackupCycleTest {
 @Test public void actualExportStageAndRestoreRetainLibraryAndNonsecretState()throws Exception{
  Context context=RuntimeEnvironment.getApplication();File providerToken=new File(context.getNoBackupFilesDir(),"putio-oauth-v1.bin");providerToken.getParentFile().mkdirs();java.nio.file.Files.write(providerToken.toPath(),"planted-encrypted-provider-envelope".getBytes(java.nio.charset.StandardCharsets.UTF_8));VideoOpenHelper helper=new VideoOpenHelper(context);SQLiteDatabase db=helper.getWritableDatabase();
  db.execSQL("INSERT INTO files(_id,_data,Archos_bookmark) VALUES(77,'smb://user:planted-secret@nas/Movies/film.mkv',123456)");helper.close();
  try(SQLiteDatabase credentials=SQLiteDatabase.openOrCreateDatabase(context.getDatabasePath("credentials_db"),null)){credentials.execSQL("CREATE TABLE credentials(password TEXT)");credentials.execSQL("INSERT INTO credentials VALUES('planted-credential-secret')");}
  try(com.archos.mediacenter.video.streaming.putio.PutioAssociationStore associations=new com.archos.mediacenter.video.streaming.putio.PutioAssociationStore(context)){associations.prepare(10,20,android.net.Uri.parse("https://webdav.put.io/Films"));}
  context.getSharedPreferences("provider-discovery-ownership-v1",0).edit().putString("putio:10:20","https://webdav.put.io/Films").commit();
  var preferences=androidx.preference.PreferenceManager.getDefaultSharedPreferences(context);preferences.edit().putString("preview_home_rows41","[{\"name\":\"Saved Row\"}]").putStringSet("supernova_library_exclusions",Set.of("smb://nas/Movies/omit")).putString("supernova_custom_library_page","{\"name\":\"My Page\"}").putString("trakt_access_token","planted-token-secret").commit();
  var resume=new com.archos.mediacenter.utils.videodb.VideoDbInfo(android.net.Uri.parse("smb://nas/Movies/film.mkv"));resume.scraperMovieId="99";resume.duration=100000;resume.resume=12345;resume.lastTimePlayed=100;
  com.archos.mediacenter.video.leanback.ViewingHistory.record(context,resume,false);
  var completed=new com.archos.mediacenter.utils.videodb.VideoDbInfo(android.net.Uri.parse("file:///removed-drive/ep1.mkv"));completed.isShow=true;completed.scraperShowId="123";completed.scraperSeasonNr=1;completed.scraperEpisodeNr=1;completed.resume=-2;completed.duration=100000;completed.lastTimePlayed=100;
  com.archos.mediacenter.video.leanback.ViewingHistory.record(context,completed,true);com.archos.mediacenter.video.leanback.ViewingHistory.awaitWrites();
  MediaLibraryBackupService service=Robolectric.buildService(MediaLibraryBackupService.class).get();String archive=ReflectionHelpers.callInstanceMethod(service,"exportMediaLibrary");
  assertTrue("Export keeps device-local authentication",providerToken.isFile());File staged;try(ZipFile zip=new ZipFile(archive)){assertNull(zip.getEntry("credentials_db"));assertNotNull(zip.getEntry("RESTORE_INSTRUCTIONS.txt"));assertNotNull(zip.getEntry("manifest.json"));}
  try(InputStream input=new FileInputStream(archive)){staged=SafeBackup.stage(context,input);}
  resume.resume=54321;com.archos.mediacenter.video.leanback.ViewingHistory.record(context,resume,false);com.archos.mediacenter.video.leanback.ViewingHistory.awaitWrites();
  preferences.edit().putString("preview_home_rows41","changed").commit();SafeBackup.restore(context,staged);
  try(SQLiteDatabase restored=SQLiteDatabase.openDatabase(context.getDatabasePath("media.db").getPath(),null,SQLiteDatabase.OPEN_READONLY);Cursor row=restored.rawQuery("SELECT _id,_data,Archos_bookmark FROM files WHERE _id=77",null)){assertTrue(row.moveToFirst());assertEquals(77,row.getLong(0));assertEquals("smb://nas/Movies/film.mkv",row.getString(1));assertEquals(123456,row.getLong(2));}
  assertTrue(preferences.getString("preview_home_rows41","").contains("Saved Row"));assertEquals(Set.of("smb://nas/Movies/omit"),preferences.getStringSet("supernova_library_exclusions",Set.of()));assertTrue(preferences.getString("supernova_custom_library_page","").contains("My Page"));assertFalse(preferences.contains("trakt_access_token"));assertFalse(context.getDatabasePath("credentials_db").exists());assertFalse("Successful restore requires provider sign-in again",providerToken.exists());assertTrue(preferences.getBoolean("try_new_ui",false));
  try(com.archos.mediacenter.video.streaming.putio.PutioAssociationStore associations=new com.archos.mediacenter.video.streaming.putio.PutioAssociationStore(context)){assertEquals(1,associations.scopes(10).size());assertEquals(20,associations.scopes(10).get(0).folderId);}
  var history=com.archos.mediacenter.video.leanback.ViewingHistory.read(context);assertEquals(12345,history.get("tmdb:movie:99").position);assertTrue(history.get("tmdb:tv:123:1:1").completed);assertEquals(100,history.get("tmdb:tv:123:1:1").completedAt);
  assertEquals("https://webdav.put.io/Films",context.getSharedPreferences("provider-discovery-ownership-v1",0).getString("putio:10:20",""));
 }
 @Test public void stagedDatabaseWithCredentialsIsRejectedBeforeReplacingLiveLibrary()throws Exception{
  Context context=RuntimeEnvironment.getApplication();File database=new File(context.getCacheDir(),"hostile.db");try(SQLiteDatabase db=SQLiteDatabase.openOrCreateDatabase(database,null)){db.setVersion(VideoOpenHelper.getDatabaseVersion());db.execSQL("CREATE TABLE files(_id INTEGER,_data TEXT)");db.execSQL("INSERT INTO files VALUES(77,'smb://user:secret@nas/file.mkv')");}
  ByteArrayOutputStream bytes=new ByteArrayOutputStream();try(ZipOutputStream zip=new ZipOutputStream(bytes)){for(String name:new String[]{"manifest.json","RESTORE_INSTRUCTIONS.txt","db_version.txt","settings.json","media.db"}){zip.putNextEntry(new ZipEntry(name));if(name.equals("media.db"))java.nio.file.Files.copy(database.toPath(),zip);else zip.write((name.equals("manifest.json")?BackupFormat.manifest():name.equals("db_version.txt")?String.valueOf(VideoOpenHelper.getDatabaseVersion()):name.equals("settings.json")?"{}":"Restore guide").getBytes(java.nio.charset.StandardCharsets.UTF_8));zip.closeEntry();}}
  var preferences=androidx.preference.PreferenceManager.getDefaultSharedPreferences(context);preferences.edit().putString("sentinel","unchanged").commit();try{SafeBackup.stage(context,new ByteArrayInputStream(bytes.toByteArray()));fail();}catch(IOException expected){}assertEquals("unchanged",preferences.getString("sentinel",""));database.delete();
 }
}
