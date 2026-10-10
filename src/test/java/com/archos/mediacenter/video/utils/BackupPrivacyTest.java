package com.archos.mediacenter.video.utils;
import android.app.Application;
import android.content.*;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import org.junit.*;
import org.junit.runner.RunWith;
import org.robolectric.*;
import org.robolectric.annotation.Config;
import org.json.*;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.*;
import java.util.zip.*;
import static org.junit.Assert.*;
@RunWith(RobolectricTestRunner.class) @Config(application=Application.class,sdk=28)
public class BackupPrivacyTest {
 @Test public void portableSettingsExcludePlantedSecretsAndPreserveUserChoices()throws Exception{
  Context c=RuntimeEnvironment.getApplication();SharedPreferences source=c.getSharedPreferences("backup-source",0),target=c.getSharedPreferences("backup-target",0);
  source.edit().putString("trakt_access_token","planted-trakt-secret").putString("putio_token","planted-putio-secret").putString("network_password","planted-network-secret").putString("preview_home_rows41","[{\"name\":\"My Row\"}]").putStringSet("supernova_library_roots",Set.of("smb://user:planted-uri-secret@nas/Movies?token=planted-query-secret")).putString("unknown_private_cache","planted-unknown-secret").commit();
  String portable=SettingsBackup.encodePortable(source,false);assertFalse(portable.contains("planted"));assertTrue(portable.contains("My Row"));
  assertTrue(SettingsBackup.decodePortable(target,portable,false).commit());assertEquals(Set.of("smb://nas/Movies"),target.getStringSet("supernova_library_roots",Set.of()));assertFalse(target.contains("trakt_access_token"));
  assertTrue("Raw recovery state remains lossless",SettingsBackup.encode(source).contains("planted-trakt-secret"));
 }
 @Test public void maliciousPreferencesCannotReintroduceAuthentication()throws Exception{
  Context c=RuntimeEnvironment.getApplication();SharedPreferences target=c.getSharedPreferences("malicious-target",0);target.edit().putString("sentinel","unchanged").commit();
  String hostile=new JSONObject().put("trakt_access_token",new JSONObject().put("type","string").put("value","planted")).toString();
  try{SettingsBackup.decodePortable(target,hostile,false);fail();}catch(JSONException expected){}assertEquals("unchanged",target.getString("sentinel",""));
  assertFalse(BackupPrivacy.named("opensubtitles_credentials"));assertTrue(BackupPrivacy.named("putio-library-selection-v1"));
 }
 @Test public void databaseCopyScrubsUrisAndFreePagesWithoutChangingLiveState()throws Exception{
  Context c=RuntimeEnvironment.getApplication();File original=new File(c.getCacheDir(),"private-library.db");
  try(SQLiteDatabase db=SQLiteDatabase.openOrCreateDatabase(original,null)){db.execSQL("CREATE TABLE library(_id INTEGER PRIMARY KEY, path TEXT, password TEXT, resume INTEGER, tmdb INTEGER)");db.execSQL("INSERT INTO library VALUES(77,'smb://user:planted-url-secret@nas/Movies/film.mkv?token=planted-query-secret','planted-password-secret',123456,550)");}
  File clean=BackupPrivacy.database(original,c.getCacheDir());
  try(SQLiteDatabase db=SQLiteDatabase.openDatabase(clean.getPath(),null,SQLiteDatabase.OPEN_READONLY);Cursor row=db.rawQuery("SELECT _id,path,password,resume,tmdb FROM library",null)){assertTrue(row.moveToFirst());assertEquals(77,row.getLong(0));assertEquals("smb://nas/Movies/film.mkv",row.getString(1));assertTrue(row.isNull(2));assertEquals(123456,row.getLong(3));assertEquals(550,row.getLong(4));}
  String bytes=new String(Files.readAllBytes(clean.toPath()),StandardCharsets.ISO_8859_1);assertFalse(bytes.contains("planted"));
  try(SQLiteDatabase db=SQLiteDatabase.openDatabase(original.getPath(),null,SQLiteDatabase.OPEN_READONLY);Cursor row=db.rawQuery("SELECT password FROM library",null)){assertTrue(row.moveToFirst());assertEquals("planted-password-secret",row.getString(0));}original.delete();clean.delete();
 }
 @Test public void newerAndLegacyFormatsFailBeforeRestore()throws Exception{
  BackupFormat.validate(BackupFormat.manifest());
  for(String value:new String[]{"{}",new JSONObject(BackupFormat.manifest()).put("formatVersion","2.0").toString(),new JSONObject(BackupFormat.manifest()).put("credentialsIncluded",true).toString()})try{BackupFormat.validate(value);fail();}catch(IOException expected){}
  Context c=RuntimeEnvironment.getApplication();File live=c.getDatabasePath("media.db");live.getParentFile().mkdirs();Files.write(live.toPath(),new byte[]{42});
  ByteArrayOutputStream archive=new ByteArrayOutputStream();try(ZipOutputStream zip=new ZipOutputStream(archive)){zip.putNextEntry(new ZipEntry("credentials_db"));zip.write("planted".getBytes(StandardCharsets.UTF_8));zip.closeEntry();}
  try{SafeBackup.stage(c,new ByteArrayInputStream(archive.toByteArray()));fail();}catch(Exception expected){}assertArrayEquals(new byte[]{42},Files.readAllBytes(live.toPath()));live.delete();
 }
 @Test public void portableSourceSchemasRejectUnknownSecretContainersAndRetainPolicyFlag()throws Exception{
  Context c=RuntimeEnvironment.getApplication();SharedPreferences source=c.getSharedPreferences("named-schema-test",0);
  source.edit().putString("opaque_account_blob","planted-secret").putString("name:10:20","Film folder").putString("root:10:20:source","https://user:planted-secret@webdav.put.io/Films").commit();
  String data=SettingsBackup.encodePortable(source,"putio-library-selection-v1");assertFalse(data.contains("planted"));assertFalse(data.contains("opaque_account_blob"));assertTrue(data.contains("Film folder"));
  String hostile=SettingsBackup.encode(source);try{SettingsBackup.decodePortable(source,hostile,"putio-library-selection-v1");fail();}catch(JSONException expected){}
  assertTrue(BackupPrivacy.setting("supernova_library_policy_initialized"));assertFalse(BackupPrivacy.setting("preview_source_kind:smb://user:password@nas/Films"));
 }
 @Test public void embeddedAuthenticationFieldsAreRemovedFromPortableStructuredSettings()throws Exception{
  Context c=RuntimeEnvironment.getApplication();SharedPreferences source=c.getSharedPreferences("structured-privacy-test",0);
  source.edit().putString("preview_home_rows41","[{\"name\":\"Saved row\",\"access_token\":\"planted-token\",\"nested\":{\"password\":\"planted-password\"}}]").commit();
  String portable=SettingsBackup.encodePortable(source,false);assertTrue(portable.contains("Saved row"));assertFalse(portable.contains("planted"));
  try{SettingsBackup.decodePortable(source,SettingsBackup.encode(source),false);fail();}catch(JSONException expected){}
 }
}
