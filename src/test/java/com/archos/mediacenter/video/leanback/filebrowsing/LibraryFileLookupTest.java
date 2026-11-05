package com.archos.mediacenter.video.leanback.filebrowsing;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import android.app.Application;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;

@RunWith(RobolectricTestRunner.class)
@Config(application = Application.class, sdk = 30)
public class LibraryFileLookupTest
{
    @Test
    public void rawUsbDatabasePathsMatchEncodedBrowserUrisWithoutIncludingSubfoldersOrSqlWildcards()
    {
        Uri folder = Uri.parse("file:///storage/1234/Films_100%25");
        LibraryFileLookup lookup = new LibraryFileLookup(RuntimeEnvironment.getApplication(), folder);
        try (SQLiteDatabase db = SQLiteDatabase.create(null))
        {
            db.execSQL("CREATE TABLE files (_data TEXT)");
            db.execSQL("INSERT INTO files VALUES ('/storage/1234/Films_100%/A #1.mkv'),"
                + "('/storage/1234/Films_100%/nested/B.mkv'),('/storage/1234/FilmsX100Z/C.mkv')");
            try (Cursor rows = db.query("files", null, lookup.getSelection(), lookup.getSelectionArgs(),
                     null, null, null))
            {
                assertEquals(1, rows.getCount());
                assertTrue(rows.moveToFirst());
                assertEquals(LibraryFileLookup.keyForDatabasePath(rows.getString(0)),
                    LibraryFileLookup.key(Uri.parse("file:///storage/1234/Films_100%25/A%20%231.mkv")));
            }
        }
    }
}
