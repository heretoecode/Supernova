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
import java.util.zip.*;
import org.junit.*;
import org.junit.runner.RunWith;
import org.robolectric.*;
import org.robolectric.annotation.*;
import org.robolectric.util.ReflectionHelpers;
import static org.junit.Assert.*;

@RunWith(RobolectricTestRunner.class) @Config(application=Application.class,sdk=28) @SQLiteMode(SQLiteMode.Mode.NATIVE)
public class LegacyExportRetirementTest {
    private Context app;private MediaLibraryBackupService service;
    @Before public void setup(){app=RuntimeEnvironment.getApplication();app.deleteDatabase("media.db");app.deleteDatabase(BookmarkStoreRecovery.DATABASE);PreferenceManager.getDefaultSharedPreferences(app).edit().clear().putString("oauth_token","AUTH_CANARY").commit();service=Robolectric.buildService(MediaLibraryBackupService.class).get();}
    private void choose(Set<BackupCategory> selection){var editor=PreferenceManager.getDefaultSharedPreferences(app).edit();for(BackupCategory category:BackupCategory.values())editor.putBoolean(category.preference(),selection.contains(category));editor.commit();}
    private String export(){return ReflectionHelpers.callInstanceMethod(service,"exportMediaLibrary");}
    @Test public void legacyServiceEntryProducesOnlyPositiveLogicalPayloadsAndNoWholeDatabaseOrArtwork()throws Exception{
        choose(Set.of(BackupCategory.APPEARANCE));File artwork=new File(app.getFilesDir(),"PRIVATE_ARTWORK_CANARY.png");Files.writeString(artwork.toPath(),"ARTWORK_BYTES_CANARY");Files.writeString(app.getDatabasePath("ftp_shortcuts_db").toPath(),"PASSWORD_CANARY");File archive=new File(export());assertEquals(Set.of(BackupCategory.APPEARANCE),LogicalBackupArchive.readCategories(archive,()->false).keySet());try(ZipFile zip=new ZipFile(archive)){for(var entries=zip.entries();entries.hasMoreElements();){var entry=entries.nextElement();assertTrue(entry.getName(),entry.getName().equals("manifest.json")||entry.getName().equals("RESTORE_INSTRUCTIONS.txt")||entry.getName().startsWith("categories/"));try(InputStream input=zip.getInputStream(entry)){String text=new String(input.readAllBytes(),java.nio.charset.StandardCharsets.UTF_8);assertFalse(text.contains("CANARY"));}}}assertFalse(VideoOpenHelper.getDatabaseFile(app).exists());assertTrue(artwork.exists());assertEquals("PASSWORD_CANARY",Files.readString(app.getDatabasePath("ftp_shortcuts_db").toPath()));
    }
    @Test public void fullSupportedSelectionCannotFallBackToLegacyWriter()throws Exception{choose(LogicalBackupArchive.registered());File archive=new File(export());assertEquals(LogicalBackupArchive.registered(),LogicalBackupArchive.readCategories(archive,()->false).keySet());try(ZipFile zip=new ZipFile(archive)){assertNull(zip.getEntry("media.db"));assertNull(zip.getEntry("db_version.txt"));assertNull(zip.getEntry("credentials_db"));assertNull(zip.getEntry("putio-associations.db"));}}
    @Test public void unsupportedSelectionFailsBeforeArchiveAndRetainsAllLiveIntent(){choose(Set.of(BackupCategory.APPEARANCE,BackupCategory.MATCHES));try{export();fail();}catch(RuntimeException expected){Throwable cause=expected;while(cause.getCause()!=null)cause=cause.getCause();assertTrue(cause instanceof IOException);}assertFalse(new File(app.getCacheDir(),"supernova-logical-backups").exists());assertEquals("AUTH_CANARY",PreferenceManager.getDefaultSharedPreferences(app).getString("oauth_token",null));assertFalse(VideoOpenHelper.getDatabaseFile(app).exists());}
    @Test public void dedicatedLegacyReaderKeepsPrivateRollbackAndDoesNotCreateNewLegacyArchive()throws Exception{
        try(VideoOpenHelper helper=new VideoOpenHelper(app)){helper.getWritableDatabase().execSQL("INSERT INTO files(_id,media_type,_data,bookmark) VALUES(77,3,'file:///fixture.mkv',4444)");}
        String historical=HistoricalBackupFixture.export(app);try(var database=SQLiteDatabase.openDatabase(VideoOpenHelper.getDatabaseFile(app).getPath(),null,SQLiteDatabase.OPEN_READWRITE)){database.execSQL("UPDATE files SET bookmark=5555 WHERE _id=77");}
        ReflectionHelpers.callInstanceMethod(service,"importMediaLibrary",ReflectionHelpers.ClassParameter.from(String.class,historical));
        try(var database=SQLiteDatabase.openDatabase(VideoOpenHelper.getDatabaseFile(app).getPath(),null,SQLiteDatabase.OPEN_READONLY);var cursor=database.rawQuery("SELECT bookmark FROM files WHERE _id=77",null)){assertTrue(cursor.moveToFirst());assertEquals(4444,cursor.getLong(0));}
        File[] external=app.getExternalFilesDir(null).listFiles(file->file.getName().endsWith(".zip"));assertNotNull(external);assertEquals(0,external.length);
        // Preserve the established1.0 reader contract: reconnect authentication after whole-library restore.
        // New logical selected restoration has its own credential-preservation checks.
        assertNull(PreferenceManager.getDefaultSharedPreferences(app).getString("oauth_token",null));
    }
}
