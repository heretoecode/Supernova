package com.archos.mediaprovider.video;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import android.app.Application;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import com.archos.mediacenter.video.utils.CanonicalMatchProviderTest;
import com.archos.mediacenter.video.player.PlaybackSourceKind;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;
import org.robolectric.annotation.SQLiteMode;

@RunWith(RobolectricTestRunner.class)
@Config(application = Application.class, sdk = 30)
@SQLiteMode(SQLiteMode.Mode.NATIVE)
public class PhysicalOwnershipRelinkTest
{
    private final CanonicalMatchProviderTest fixture = new CanonicalMatchProviderTest();
    private VideoOpenHelper helper;
    private SQLiteDatabase db;

    @Before public void setup()
    {
        fixture.setup();
        helper = new VideoOpenHelper(RuntimeEnvironment.getApplication());
        db = helper.getWritableDatabase();
        db.execSQL("UPDATE files SET _size=123 WHERE _id=10");
        db.execSQL("INSERT INTO files_import(_id,_data,_size) VALUES(10,'file:///one.mkv',123)");
    }

    @After public void close()
    {
        helper.close();
        fixture.close();
    }

    private MissingMediaActions.StoredFile original()
    {
        return new MissingMediaActions.StoredFile(10, "file:///one.mkv", 123);
    }

    private void preserved(String path)
    {
        try (Cursor row = db.rawQuery("SELECT f._data,f.bookmark,f.Archos_traktSeen,m.m_online_id,m.Nova_pinned "
                                      + "FROM files f JOIN movie m ON m._id=f.ArchosMediaScraper_id WHERE f._id=10",
                                      null))
        {
            assertTrue(row.moveToFirst());
            assertEquals(path, row.getString(0));
            assertEquals(4444, row.getLong(1));
            assertEquals(1, row.getInt(2));
            assertEquals(123, row.getLong(3));
            assertEquals(9, row.getLong(4));
        }
    }

    @Test public void schema64RefusesAnOldHelperWithoutChangingRetainedUserState() throws Exception
    {
        assertEquals(64, db.getVersion());
        try (VideoOpenHelper old = new VideoOpenHelper(RuntimeEnvironment.getApplication(),
                                                      VideoOpenHelper.getDatabaseFile(
                                                          RuntimeEnvironment.getApplication()).getName(), 63))
        {
            org.junit.Assert.assertThrows(android.database.sqlite.SQLiteException.class, old::getWritableDatabase);
        }
        preserved(original().path());
    }

    @Test public void localToNetworkOwnsTheScannedIndexAndSurvivesOldMediaStoreIdReuse() throws Exception
    {
        String replacement = "smb://nas/Films/one.mkv";
        assertTrue(MissingMediaActions.relinkAcrossSources(RuntimeEnvironment.getApplication(), original(), null,
                                                          Uri.parse("smb://nas/Films"), replacement));
        preserved(replacement);
        assertFalse(PlaybackSourceKind.isLocal(RuntimeEnvironment.getApplication(),
            android.content.ContentUris.withAppendedId(VideoStore.Video.Media.EXTERNAL_CONTENT_URI, 10)));
        db.execSQL("INSERT INTO files_import(_id,_data,_size) VALUES(10,'/storage/Reused.mkv',999)");
        preserved(replacement);
        db.execSQL("UPDATE files SET media_type=3 WHERE _data='/storage/Reused.mkv'");
        try (Cursor row = db.rawQuery("SELECT _id,remote_id FROM files WHERE _data='/storage/Reused.mkv'", null))
        {
            assertTrue(row.moveToFirst());
            assertTrue(row.getLong(0) > 0);
            assertTrue(row.getLong(0) < 2000000000L);
            assertTrue(row.getLong(0) != 10);
            assertEquals(10, row.getLong(1));
            assertTrue(PlaybackSourceKind.isLocal(RuntimeEnvironment.getApplication(),
                android.content.ContentUris.withAppendedId(VideoStore.Video.Media.EXTERNAL_CONTENT_URI,
                                                          row.getLong(0))));
        }
        db.execSQL("UPDATE files_scanned SET _data='smb://nas/Films/Moved/one.mkv' WHERE _data=?",
                   new Object[] {replacement});
        preserved("smb://nas/Films/Moved/one.mkv");
        assertFalse(MissingMediaActions.change(db, original(), "smb://nas/Films/Again/one.mkv"));
    }

    @Test public void networkToLocalTransfersNativeOwnershipAndReimportKeepsPublicId() throws Exception
    {
        String remote = "smb://nas/Films/one.mkv";
        assertTrue(MissingMediaActions.change(db, original(), remote));
        ContentValues destination = new ContentValues();
        destination.put("_id", 77L);
        destination.put("_data", "/storage/drive/one.mkv");
        destination.put("_display_name", "one.mkv");
        destination.put("_size", 123L);
        destination.put("date_modified", 200L);
        assertTrue(MissingMediaActions.change(db, new MissingMediaActions.StoredFile(10, remote, 123),
                                              "file:///storage/drive/one.mkv", destination));
        preserved("/storage/drive/one.mkv");
        for (boolean recursive : new boolean[] {false, true})
        {
            db.execSQL("PRAGMA recursive_triggers=" + (recursive ? 1 : 0));
            assertEquals(77, PhysicalIndexOwnership.upsertImported(db, destination));
            preserved("/storage/drive/one.mkv");
        }
        preserved("/storage/drive/one.mkv");
        db.execSQL("UPDATE files_import SET volume_hidden=123 WHERE _id=77");
        try (Cursor row = db.rawQuery("SELECT remote_id,volume_hidden FROM files WHERE _id=10", null))
        {
            assertTrue(row.moveToFirst());
            assertEquals(77, row.getLong(0));
            assertEquals(123, row.getLong(1));
        }
        assertTrue(MissingMediaActions.change(db,
            new MissingMediaActions.StoredFile(10, "/storage/drive/one.mkv", 123), null));
        assertEquals(0, android.database.DatabaseUtils.queryNumEntries(db, "files_import", "_id=77"));
    }

    @Test public void destinationConflictAndStaleSizeLeaveBothOwnershipTablesUnchanged() throws Exception
    {
        String remote = "smb://nas/Films/one.mkv";
        db.execSQL("INSERT INTO files_scanned(_data,_size,media_type) VALUES(?,123,3)", new Object[] {remote});
        assertFalse(MissingMediaActions.change(db, original(), remote));
        assertFalse(MissingMediaActions.change(db, new MissingMediaActions.StoredFile(10, original().path(), 999),
                                              "smb://nas/Other/one.mkv"));
        preserved(original().path());
        assertEquals(1, android.database.DatabaseUtils.queryNumEntries(db, "files_import", "_id=10"));
    }
}
