package com.archos.mediacenter.video.utils;

import android.app.Application;
import android.database.sqlite.SQLiteDatabase;
import androidx.preference.PreferenceManager;
import com.archos.mediaprovider.video.VideoOpenHelper;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.security.MessageDigest;
import java.util.*;
import java.util.zip.*;
import org.json.JSONObject;
import org.junit.*;
import org.junit.runner.RunWith;
import org.robolectric.*;
import org.robolectric.annotation.*;
import org.robolectric.util.ReflectionHelpers;
import static org.junit.Assert.*;

/** Synthetic format1/schema62 compatibility; never a recovered owner incident archive. */
@RunWith(RobolectricTestRunner.class) @Config(application=Application.class,sdk=28) @SQLiteMode(SQLiteMode.Mode.NATIVE)
public class LegacySchema62MigrationTest {
    private Application app;
    @Before public void setup(){app=RuntimeEnvironment.getApplication();app.deleteDatabase("media.db");PreferenceManager.getDefaultSharedPreferences(app).edit().clear().putInt("preview_accent41",7).putString("oauth_token","AUTH_CANARY").commit();com.archos.environment.ArchosUtils.setGlobalContext(app);}
    private File database(boolean historical)throws Exception {
        try(VideoOpenHelper helper=new VideoOpenHelper(app)){
            var db=helper.getWritableDatabase();db.execSQL("INSERT INTO files(_id,remote_id,media_type,_data,bookmark,Archos_lastTimePlayed,Archos_traktSeen) VALUES(10,10,3,'file:///fixture.mkv',4444,100,1)");db.execSQL("INSERT INTO MOVIE(_id,video_id,m_online_id,name_movie,plot_movie,Nova_pinned) VALUES(100,10,123,'Historical title','Historical facts',9)");db.execSQL("INSERT INTO user_collection(_id,name,sort_name,position,hidden) VALUES(7,'Receiving collection','Receiving collection',2,0)");db.execSQL("INSERT INTO user_collection_item(_id,collection_id,media_type,media_id) VALUES(9,7,1,100)");
            if(historical){String old=ReflectionHelpers.getStaticField(VideoOpenHelper.class,"CREATE_VIDEO_VIEW_V60");db.execSQL("DROP VIEW video");db.execSQL(old);db.setVersion(62);}
        }
        File copy=new File(app.getCacheDir(),"schema-"+(historical?62:63)+"-"+UUID.randomUUID()+".db");Files.copy(VideoOpenHelper.getDatabaseFile(app).toPath(),copy.toPath());return copy;
    }
    private File archive(File media)throws Exception {
        File zip=new File(app.getCacheDir(),"synthetic-schema62-"+UUID.randomUUID()+".zip");var preferences=PreferenceManager.getDefaultSharedPreferences(app);
        Map<String,byte[]> entries=new LinkedHashMap<>();entries.put("manifest.json",new JSONObject(BackupFormat.manifest()).put("applicationVersion","0.138").toString().getBytes(StandardCharsets.UTF_8));entries.put("RESTORE_INSTRUCTIONS.txt",BackupFormat.instructions().getBytes(StandardCharsets.UTF_8));entries.put("db_version.txt","62".getBytes(StandardCharsets.UTF_8));entries.put("media.db",Files.readAllBytes(media.toPath()));entries.put("settings.json",SettingsBackup.encodePortable(preferences,false).getBytes(StandardCharsets.UTF_8));
        try(ZipOutputStream output=new ZipOutputStream(new FileOutputStream(zip))){for(var entry:entries.entrySet()){output.putNextEntry(new ZipEntry(entry.getKey()));output.write(entry.getValue());output.closeEntry();}}
        return zip;
    }
    @Test public void schema62LegacyRestoreMigratesPrivateReplacementBeforeCommitAndPreservesOriginalArchive()throws Exception {
        File old=database(true),zip=archive(old);byte[] original=MessageDigest.getInstance("SHA-256").digest(Files.readAllBytes(zip.toPath()));
        File stage;try(InputStream input=new FileInputStream(zip)){stage=SafeBackup.stage(app,input);}try(var reviewed=SQLiteDatabase.openDatabase(new File(stage,"media.db").getPath(),null,SQLiteDatabase.OPEN_READONLY)){assertEquals(62,reviewed.getVersion());}
        SafeBackup.restore(app,stage);
        try(var db=SQLiteDatabase.openDatabase(VideoOpenHelper.getDatabaseFile(app).getPath(),null,SQLiteDatabase.OPEN_READONLY);var row=db.rawQuery("SELECT m.m_online_id,m.name_movie,m.plot_movie,m.Nova_pinned,f.bookmark,f.Archos_lastTimePlayed,f.Archos_traktSeen,i.media_id FROM MOVIE m JOIN files f ON f._id=m.video_id JOIN user_collection_item i ON i.media_id=m._id WHERE m._id=100",null)){
            assertEquals(63,db.getVersion());assertTrue(row.moveToFirst());assertEquals(123,row.getLong(0));assertEquals("Historical title",row.getString(1));assertEquals("Historical facts",row.getString(2));assertEquals(9,row.getLong(3));assertEquals(4444,row.getLong(4));assertEquals(100,row.getLong(5));assertEquals(1,row.getLong(6));assertEquals(100,row.getLong(7));
        }
        assertArrayEquals(original,MessageDigest.getInstance("SHA-256").digest(Files.readAllBytes(zip.toPath())));assertFalse(stage.exists());assertFalse(PreferenceManager.getDefaultSharedPreferences(app).contains("oauth_token"));
    }
    @Test public void failedPrivateMigrationDoesNotReplaceLiveLibraryOrDisconnectReceivingCredentials()throws Exception {
        database(false);File broken=new File(app.getCacheDir(),"incompatible62.db");try(var db=SQLiteDatabase.openOrCreateDatabase(broken,null)){db.execSQL("CREATE TABLE files(_id INTEGER PRIMARY KEY)");db.setVersion(62);}File zip=archive(broken),stage;try(InputStream input=new FileInputStream(zip)){stage=SafeBackup.stage(app,input);}
        try{SafeBackup.restore(app,stage);fail("Invalid legacy schema migrated");}catch(IOException expected){}
        try(var db=SQLiteDatabase.openDatabase(VideoOpenHelper.getDatabaseFile(app).getPath(),null,SQLiteDatabase.OPEN_READONLY);var row=db.rawQuery("SELECT bookmark FROM files WHERE _id=10",null)){assertEquals(63,db.getVersion());assertTrue(row.moveToFirst());assertEquals(4444,row.getLong(0));}
        assertEquals("AUTH_CANARY",PreferenceManager.getDefaultSharedPreferences(app).getString("oauth_token",null));assertFalse(new File(app.getFilesDir(),"pending-restore.json").exists());
    }
}
