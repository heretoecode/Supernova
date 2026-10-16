package com.archos.mediacenter.video.utils;

import android.app.Application;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import androidx.preference.PreferenceManager;
import com.archos.mediacenter.video.utils.backup.*;
import com.archos.mediaprovider.video.VideoOpenHelper;
import java.io.*;
import java.nio.file.Files;
import java.util.*;
import org.json.*;
import org.junit.*;
import org.junit.runner.RunWith;
import org.robolectric.*;
import org.robolectric.annotation.*;
import static org.junit.Assert.*;

@RunWith(RobolectricTestRunner.class) @Config(application=Application.class,sdk=28) @SQLiteMode(SQLiteMode.Mode.NATIVE)
public class BackupImportPreparationTest {
    private Context app;
    @Before public void setup(){app=RuntimeEnvironment.getApplication();app.deleteDatabase("media.db");PreferenceManager.getDefaultSharedPreferences(app).edit().clear().putString("oauth_token","AUTH_CANARY").commit();}
    private File legacy()throws Exception{try(VideoOpenHelper helper=new VideoOpenHelper(app)){helper.getWritableDatabase().execSQL("INSERT INTO files(_id,media_type,_data,bookmark) VALUES(77,3,'file:///fixture.mkv',4444)");}return new File(HistoricalBackupFixture.export(app));}
    private int nativePosition()throws Exception{try(var db=SQLiteDatabase.openDatabase(VideoOpenHelper.getDatabaseFile(app).getPath(),null,SQLiteDatabase.OPEN_READONLY);var rows=db.rawQuery("SELECT bookmark FROM files WHERE _id=77",null)){assertTrue(rows.moveToFirst());return rows.getInt(0);}}
    private void changeNative()throws Exception{try(VideoOpenHelper helper=new VideoOpenHelper(app)){helper.getWritableDatabase().execSQL("UPDATE files SET bookmark=5555 WHERE _id=77");}}
    @Test @Config(sdk=23) public void logicalDispatchUsesValidatedPayloadsAndNeverClearsReceivingAuthentication()throws Exception {
        File archive=LogicalBackupExporter.preparePrivate(app,Set.of(BackupCategory.APPEARANCE),()->false);try(var prepared=BackupImportPreparation.prepare(app,new FileInputStream(archive),()->false)){assertFalse(prepared.isLegacy());assertEquals(Set.of(BackupCategory.APPEARANCE),prepared.categories());assertEquals(prepared.categories(),prepared.payloads(()->false).keySet());try{prepared.restoreLegacy(app,()->false);fail();}catch(IOException expected){}assertEquals("AUTH_CANARY",PreferenceManager.getDefaultSharedPreferences(app).getString("oauth_token",null));assertFalse(VideoOpenHelper.getDatabaseFile(app).exists());}assertTrue(archive.exists());assertEquals(0,app.getCacheDir().listFiles(file->file.getName().startsWith("supernova-format-")).length);
    }
    @Test @Config(sdk=23) public void legacyReaderRequiresExplicitWholeReplacementAndKeepsItsEstablishedAuthContract()throws Exception {
        File archive=legacy();try(var prepared=BackupImportPreparation.prepare(app,new FileInputStream(archive),()->false)){assertTrue(prepared.isLegacy());assertTrue(prepared.categories().isEmpty());changeNative();assertEquals(5555,nativePosition());assertEquals("AUTH_CANARY",PreferenceManager.getDefaultSharedPreferences(app).getString("oauth_token",null));try{prepared.payloads(()->false);fail();}catch(IOException expected){}Files.write(archive.toPath(),new byte[]{1,2,3});prepared.restoreLegacy(app,()->false);assertEquals(4444,nativePosition());assertNull(PreferenceManager.getDefaultSharedPreferences(app).getString("oauth_token",null));assertTrue(PreferenceManager.getDefaultSharedPreferences(app).getBoolean("preview_restore_artwork_pending",false));}assertTrue(archive.exists());
    }
    @Test public void privateLegacyTamperingRejectsBeforeNativeOrAuthenticationReplacement()throws Exception {
        try(var prepared=BackupImportPreparation.prepare(app,new FileInputStream(legacy()),()->false)){changeNative();File[] stages=app.getCacheDir().listFiles(file->file.getName().startsWith("restore-")&&file.isDirectory());assertEquals(1,stages.length);File settings=new File(stages[0],"settings.json");Files.writeString(settings.toPath(),Files.readString(settings.toPath())+"\n ");try{prepared.restoreLegacy(app,()->false);fail();}catch(IOException expected){}assertEquals(5555,nativePosition());assertEquals("AUTH_CANARY",PreferenceManager.getDefaultSharedPreferences(app).getString("oauth_token",null));}
    }
    @Test public void cancellationBeforeLegacyReplacementRetainsNativeAndAuthentication()throws Exception {
        try(var prepared=BackupImportPreparation.prepare(app,new FileInputStream(legacy()),()->false)){changeNative();try{prepared.restoreLegacy(app,()->true);fail();}catch(InterruptedIOException expected){}assertEquals(5555,nativePosition());assertEquals("AUTH_CANARY",PreferenceManager.getDefaultSharedPreferences(app).getString("oauth_token",null));}
    }
    @Test public void cleanupRefusesUnownedRootAndNeverFollowsLinksInsideOwnedReview()throws Exception {
        File keep=new File(app.getCacheDir(),"unrelated-keep");keep.mkdirs();File content=new File(keep,"KEEP.txt");Files.writeString(content.toPath(),"KEEP");try{SafeBackup.discardStage(app,keep);fail();}catch(IOException expected){}assertTrue(content.exists());File owned=new File(app.getCacheDir(),"restore-"+UUID.randomUUID());owned.mkdirs();Files.createSymbolicLink(new File(owned,"linked").toPath(),keep.toPath());SafeBackup.discardStage(app,owned);assertFalse(owned.exists());assertEquals("KEEP",Files.readString(content.toPath()));
    }
    @Test public void providerCloseFailureCannotYieldAReviewAndCleansOwnedSnapshot()throws Exception {
        File archive=LogicalBackupExporter.preparePrivate(app,Set.of(BackupCategory.APPEARANCE),()->false);try{BackupImportPreparation.prepare(app,new FileInputStream(archive){public void close()throws IOException{super.close();throw new IOException("Provider close failed");}},()->false);fail();}catch(IOException expected){}assertEquals(0,app.getCacheDir().listFiles(file->file.getName().startsWith("supernova-format-")).length);assertEquals("AUTH_CANARY",PreferenceManager.getDefaultSharedPreferences(app).getString("oauth_token",null));assertTrue(archive.exists());
    }
}
