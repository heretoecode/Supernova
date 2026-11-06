package com.archos.mediaprovider.video;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import android.app.Application;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import androidx.preference.PreferenceManager;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.nio.file.Files;
import java.security.MessageDigest;
import java.util.zip.GZIPInputStream;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;
import org.robolectric.annotation.SQLiteMode;

/** Frozen released schemas, never the current helper with its user_version changed. No owner data. */
@RunWith(RobolectricTestRunner.class)
@Config(application = Application.class, sdk = 30)
@SQLiteMode(SQLiteMode.Mode.NATIVE)
public class HistoricalUpgrade157Test
{
    @Test public void bothReleasedSchemasPreserveLibraryCollectionsHiddenResumePreferencesAndAuthenticationFiles()
        throws Exception
    {
        Application app = RuntimeEnvironment.getApplication();
        for (String release : new String[] {"0.153", "0.154"})
        {
            app.deleteDatabase("media-v64.db");
            app.deleteDatabase("media.db");
            File original = app.getDatabasePath("media.db");
            assertTrue(original.getParentFile().isDirectory() || original.getParentFile().mkdirs());
            try (InputStream compressed = getClass().getResourceAsStream("/upgrade157/" + release + "-media.db.gz");
                 GZIPInputStream input = new GZIPInputStream(compressed);
                 FileOutputStream output = new FileOutputStream(original))
            {
                byte[] buffer = new byte[65536];
                int count;
                while ((count = input.read(buffer)) != -1)
                    output.write(buffer, 0, count);
            }
            byte[] before = Files.readAllBytes(original.toPath());
            assertEquals("c4cec3061dd3cdcffdf412cf6f8a886492e38680fe2efb59f400f99aa74b19fc",
                         hex(MessageDigest.getInstance("SHA-256").digest(before)));
            File credential = new File(app.getNoBackupFilesDir(), "putio-oauth-v1.bin");
            byte[] opaque = new byte[] {17, 42, 23};
            Files.write(credential.toPath(), opaque);
            var preferences = PreferenceManager.getDefaultSharedPreferences(app);
            preferences.edit().putString("oauth_token", "OPAQUE_TEST_VALUE")
                .putString("preview_home_rows41", "[]").putBoolean("auto_rescan_on_app_restart", false).commit();
            try (VideoOpenHelper helper = new VideoOpenHelper(app))
            {
                SQLiteDatabase db = helper.getWritableDatabase();
                assertEquals(64, db.getVersion());
                try (Cursor row = db.rawQuery("SELECT _data,bookmark,Archos_traktSeen,Archos_hiddenByUser,"
                    + "Supernova_personalVideo FROM files WHERE _id=31", null))
                {
                    assertTrue(row.moveToFirst());
                    assertEquals("/storage/old/Film.mkv", row.getString(0));
                    assertEquals(4444, row.getLong(1));
                    assertEquals(1, row.getInt(2));
                    assertEquals(1, row.getInt(3));
                    assertEquals(0, row.getInt(4));
                }
                try (Cursor row = db.rawQuery("SELECT name,position,hidden FROM user_collection WHERE _id=7", null))
                {
                    assertTrue(row.moveToFirst());
                    assertEquals("Saved collection", row.getString(0));
                    assertEquals(2, row.getInt(1));
                    assertEquals(1, row.getInt(2));
                }
                try (Cursor row = db.rawQuery("SELECT media_id FROM user_collection_item WHERE collection_id=7", null))
                {
                    assertTrue(row.moveToFirst());
                    assertEquals(123, row.getLong(0));
                }
                assertTrue(MissingMediaActions.change(db,
                    new MissingMediaActions.StoredFile(31, "/storage/old/Film.mkv", 123), "smb://nas/Film.mkv"));
            }
            assertEquals("OPAQUE_TEST_VALUE", preferences.getString("oauth_token", null));
            assertEquals("[]", preferences.getString("preview_home_rows41", null));
            assertFalse(preferences.getBoolean("auto_rescan_on_app_restart", true));
            assertArrayEquals(opaque, Files.readAllBytes(credential.toPath()));
            // Opening 0.157 preserves the rollback snapshot; new 0.157 edits belong only to the new store.
            try (SQLiteDatabase old = SQLiteDatabase.openDatabase(original.getPath(), null,
                                                                   SQLiteDatabase.OPEN_READONLY))
            {
                assertEquals(63, old.getVersion());
                try (Cursor retained = old.rawQuery("SELECT _data,bookmark,Archos_hiddenByUser FROM files "
                                                   + "WHERE _id=31", null))
                {
                    assertTrue(retained.moveToFirst());
                    assertEquals("/storage/old/Film.mkv", retained.getString(0));
                    assertEquals(4444, retained.getLong(1));
                    assertEquals(1, retained.getInt(2));
                }
            }
        }
    }

    private static String hex(byte[] bytes)
    {
        StringBuilder text = new StringBuilder();
        for (byte part : bytes)
            text.append(String.format(java.util.Locale.ROOT, "%02x", part & 255));
        return text.toString();
    }
}
