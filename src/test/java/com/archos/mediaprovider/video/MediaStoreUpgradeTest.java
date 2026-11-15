package com.archos.mediaprovider.video;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThrows;
import static org.junit.Assert.assertTrue;
import android.app.Application;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import java.io.File;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;
import org.robolectric.annotation.SQLiteMode;

@RunWith(RobolectricTestRunner.class)
@Config(application = Application.class, sdk = 30)
@SQLiteMode(SQLiteMode.Mode.NATIVE)
public class MediaStoreUpgradeTest
{
    private void seed(SQLiteDatabase db)
    {
        db.execSQL("INSERT INTO files_import(_id,_data,_size) VALUES(31,'file:///one.mkv',123)");
        db.execSQL("UPDATE files SET media_type=3,bookmark=4444,Archos_traktSeen=1,"
                   + "ArchosMediaScraper_type=11,ArchosMediaScraper_id=100 WHERE _id=31");
        db.execSQL("INSERT INTO movie(_id,video_id,m_online_id,name_movie) VALUES(100,31,123,'Original')");
    }

    private void retained(SQLiteDatabase db, String path)
    {
        try (Cursor row = db.rawQuery("SELECT _data,bookmark,Archos_traktSeen,ArchosMediaScraper_id FROM files "
                                      + "WHERE _id=31", null))
        {
            assertTrue(row.moveToFirst());
            assertEquals(path, row.getString(0));
            assertEquals(4444, row.getLong(1));
            assertEquals(1, row.getInt(2));
            assertEquals(100, row.getLong(3));
        }
    }

    @Test public void previousHelperKeepsItsOriginalStoreAfterTheNewCopyRelinksAcrossIndexes() throws Exception
    {
        Application app = RuntimeEnvironment.getApplication();
        try (VideoOpenHelper previous = new VideoOpenHelper(app, "media.db", 63))
        {
            seed(previous.getWritableDatabase());
        }
        File legacy = app.getDatabasePath("media.db");
        assertTrue(legacy.isFile());
        File current = VideoOpenHelper.getDatabaseFile(app);
        assertFalse(legacy.getCanonicalPath().equals(current.getCanonicalPath()));
        try (VideoOpenHelper helper = new VideoOpenHelper(app))
        {
            SQLiteDatabase db = helper.getWritableDatabase();
            assertEquals(64, db.getVersion());
            retained(db, "file:///one.mkv");
            assertTrue(MissingMediaActions.change(db, new MissingMediaActions.StoredFile(31, "file:///one.mkv", 123),
                                                  "smb://nas/Films/one.mkv"));
            retained(db, "smb://nas/Films/one.mkv");
        }
        // The older binary still opens media.db, never the versioned ownership store.
        try (VideoOpenHelper previous = new VideoOpenHelper(app, "media.db", 63))
        {
            assertEquals(63, previous.getReadableDatabase().getVersion());
            retained(previous.getReadableDatabase(), "file:///one.mkv");
        }
        assertTrue(current.isFile());
    }

    @Test public void aNewerUnsupportedSchemaIsRejectedWithoutTheOldDeleteOnDowngradeBehaviour() throws Exception
    {
        Application app = RuntimeEnvironment.getApplication();
        File current;
        try (VideoOpenHelper helper = new VideoOpenHelper(app))
        {
            seed(helper.getWritableDatabase());
            helper.getWritableDatabase().setVersion(65);
            current = VideoOpenHelper.getDatabaseFile(app);
        }
        try (VideoOpenHelper helper = new VideoOpenHelper(app))
        {
            assertThrows(SQLiteException.class, helper::getWritableDatabase);
        }
        assertTrue(current.isFile());
        try (SQLiteDatabase raw = SQLiteDatabase.openDatabase(current.getPath(), null, SQLiteDatabase.OPEN_READONLY))
        {
            assertEquals(65, raw.getVersion());
            retained(raw, "file:///one.mkv");
        }
    }
}
