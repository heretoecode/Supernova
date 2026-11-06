package com.archos.mediacenter.video.utils;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import android.app.Application;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import androidx.preference.PreferenceManager;
import com.archos.mediaprovider.video.VideoOpenHelper;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import org.json.JSONObject;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;

/** Checks a changed staged backup is rejected before replacing live state. */
@RunWith(RobolectricTestRunner.class)
@Config(application = Application.class, sdk = 28)
public class BackupStageRevalidationTest
{
    @Test public void changedStagingCopyIsRefusedBeforeAnyLiveStateReplacement() throws Exception
    {
        Context context = RuntimeEnvironment.getApplication();
        try (VideoOpenHelper helper = new VideoOpenHelper(context))
        {
            helper.getWritableDatabase().execSQL("INSERT INTO files(_id,_data,Archos_bookmark) "
                + "VALUES(991,'file:///storage/USB/film.mkv',123456)");
        }
        String archive = HistoricalBackupFixture.export(context);
        File stage;
        try (InputStream input = new FileInputStream(archive))
        {
            stage = SafeBackup.stage(context, input);
        }
        var preferences = PreferenceManager.getDefaultSharedPreferences(context);
        preferences.edit().putString("preview_home_rows41", "Current state").commit();
        File database = VideoOpenHelper.getDatabaseFile(context);
        try (SQLiteDatabase live = SQLiteDatabase.openDatabase(database.getPath(), null, SQLiteDatabase.OPEN_READWRITE))
        {
            live.execSQL("UPDATE files SET Archos_bookmark=888888 WHERE _id=991");
        }
        String changed = new JSONObject(BackupFormat.manifest()).put("credentialsIncluded", true).toString();
        Files.write(new File(stage, "manifest.json").toPath(), changed.getBytes(StandardCharsets.UTF_8));
        try
        {
            SafeBackup.restore(context, stage);
            fail("Altered staging data must be revalidated");
        }
        catch (IOException expected)
        {
        }
        try (SQLiteDatabase live = SQLiteDatabase.openDatabase(database.getPath(), null, SQLiteDatabase.OPEN_READONLY);
             Cursor row = live.rawQuery("SELECT Archos_bookmark FROM files WHERE _id=991", null))
        {
            assertTrue(row.moveToFirst());
            assertEquals(888888, row.getLong(0));
        }
        assertEquals("Current state", preferences.getString("preview_home_rows41", ""));
    }
}
