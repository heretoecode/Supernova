package com.archos.mediacenter.video.utils;

import android.app.Application;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import com.archos.mediaprovider.video.VideoOpenHelper;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import org.json.JSONObject;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.*;
import org.robolectric.annotation.Config;
import org.robolectric.util.ReflectionHelpers;
import static org.junit.Assert.*;

@RunWith(RobolectricTestRunner.class) @Config(application=Application.class,sdk=28)
public class BackupStageRevalidationTest {
    @Test public void changedStagingCopyIsRefusedBeforeAnyLiveStateReplacement()throws Exception {
        Context context=RuntimeEnvironment.getApplication();VideoOpenHelper helper=new VideoOpenHelper(context);
        helper.getWritableDatabase().execSQL("INSERT INTO files(_id,_data,Archos_bookmark) VALUES(991,'file:///storage/USB/film.mkv',123456)");helper.close();
        MediaLibraryBackupService service=Robolectric.buildService(MediaLibraryBackupService.class).get();String archive=ReflectionHelpers.callInstanceMethod(service,"exportMediaLibrary");
        File stage;try(InputStream input=new FileInputStream(archive)){stage=SafeBackup.stage(context,input);}
        var preferences=androidx.preference.PreferenceManager.getDefaultSharedPreferences(context);preferences.edit().putString("preview_home_rows41","Current state").commit();
        try(SQLiteDatabase live=SQLiteDatabase.openDatabase(context.getDatabasePath("media.db").getPath(),null,SQLiteDatabase.OPEN_READWRITE)){live.execSQL("UPDATE files SET Archos_bookmark=888888 WHERE _id=991");}
        String changed=new JSONObject(BackupFormat.manifest()).put("credentialsIncluded",true).toString();Files.write(new File(stage,"manifest.json").toPath(),changed.getBytes(StandardCharsets.UTF_8));
        try {SafeBackup.restore(context,stage);fail("Altered staging data must be revalidated");}catch(IOException expected){}
        try(SQLiteDatabase live=SQLiteDatabase.openDatabase(context.getDatabasePath("media.db").getPath(),null,SQLiteDatabase.OPEN_READONLY);Cursor row=live.rawQuery("SELECT Archos_bookmark FROM files WHERE _id=991",null)){assertTrue(row.moveToFirst());assertEquals(888888,row.getLong(0));}
        assertEquals("Current state",preferences.getString("preview_home_rows41",""));
    }
}
