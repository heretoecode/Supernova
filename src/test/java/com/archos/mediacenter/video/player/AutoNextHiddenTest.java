package com.archos.mediacenter.video.player;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.nullable;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import android.app.Application;
import android.content.ContentResolver;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import com.archos.mediaprovider.video.VideoStore;
import java.util.ArrayList;
import java.util.List;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

@RunWith(RobolectricTestRunner.class) @Config(application = Application.class, sdk = 30) public class AutoNextHiddenTest
{
    @Test public void filtersBeforeLimitAndRepeatWrapAndRemoteQueueSkipHidden() throws Exception
    {
        try (SQLiteDatabase db = SQLiteDatabase.create(null))
        {
            db.execSQL("CREATE TABLE videos (_id INTEGER, _data TEXT, bucket_id INTEGER, Archos_hideFile INTEGER)");
            db.execSQL("INSERT INTO videos VALUES (1,'file:///a.mkv',7,0),(2,'file:///b.mkv',7,1),"
                       + "(3,'file:///c.mkv',7,0)");
            ContentResolver resolver = mock(ContentResolver.class);
            when(resolver.query(any(Uri.class), any(String[].class), nullable(String.class), nullable(String[].class),
                                nullable(String.class)))
                .thenAnswer(call
                            -> db.query("videos", call.getArgument(1), call.getArgument(2), call.getArgument(3), null,
                                        null, call.getArgument(4)));
            try (Cursor next = UpdateNextTask.getNextInBucket(resolver, 7, "file:///a.mkv"))
            {
                assertEquals(1, next.getCount());
                next.moveToFirst();
                assertEquals(3, next.getInt(0));
            }
            db.execSQL("UPDATE videos SET Archos_hideFile=1 WHERE _id=1");
            try (Cursor first = UpdateNextTask.getFirstInBucket(resolver, 7))
            {
                first.moveToFirst();
                assertEquals(3, first.getInt(0));
            }
            db.execSQL("UPDATE videos SET Archos_hideFile=1 WHERE _id=3");
            try (Cursor empty = UpdateNextTask.getFirstInBucket(resolver, 7))
            {
                assertFalse(empty.moveToFirst());
            }
            db.execSQL("UPDATE videos SET Archos_hideFile=0 WHERE _id=3");
            UpdateNextTask task =
                new UpdateNextTask(resolver, null, Uri.parse("file:///a.mkv"),
                                   new ArrayList<>(List.of("file:///a.mkv", "file:///b.mkv", "file:///c.mkv")), 0, -1);
            assertEquals(Uri.parse("file:///c.mkv"), task.run(false, false).uri);
            assertEquals(VideoStore.MediaColumns.DATA, "_data");
        }
    }
}
