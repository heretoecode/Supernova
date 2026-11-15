package com.archos.mediacenter.video.health;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertThrows;
import android.app.Application;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import androidx.preference.PreferenceManager;
import com.archos.mediacenter.video.utils.CanonicalMatchProviderTest;
import com.archos.mediacenter.video.utils.backup.MediaStateRecovery;
import com.archos.mediacenter.video.utils.backup.PortableHiddenItems;
import com.archos.mediacenter.video.utils.backup.PortablePhysicalReferences;
import com.archos.mediacenter.video.browser.adapters.object.Video;
import com.archos.mediacenter.video.leanback.PreviewLibraryLoader;
import com.archos.mediaprovider.video.MissingMediaActions;
import com.archos.mediaprovider.video.PersonalVideoActions;
import com.archos.mediaprovider.video.VideoOpenHelper;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;
import org.robolectric.annotation.SQLiteMode;

@RunWith(RobolectricTestRunner.class)
@Config(application = Application.class, sdk = 30)
@SQLiteMode(SQLiteMode.Mode.NATIVE)
public class PersonalVideosTest
{
    private final CanonicalMatchProviderTest fixture = new CanonicalMatchProviderTest();
    private Application app;

    @Before public void setup()
    {
        fixture.setup();
        app = RuntimeEnvironment.getApplication();
        try (VideoOpenHelper helper = new VideoOpenHelper(app))
        {
            helper.getWritableDatabase().execSQL("UPDATE files SET _size=123 WHERE _id=10");
        }
    }

    @After public void close()
    {
        fixture.close();
    }

    private MissingMediaActions.StoredFile file()
    {
        return new MissingMediaActions.StoredFile(10, "file:///one.mkv", 123);
    }

    @Test public void previewSnapshotOverrideCannotBypassPersonalFiltering() throws Exception
    {
        try (VideoOpenHelper helper = new VideoOpenHelper(app))
        {
            PreviewLibraryLoader loader = new PreviewLibraryLoader(app);
            SQLiteDatabase db = helper.getReadableDatabase();
            try (Cursor visible = db.query("video", new String[] {"_id"},
                                          "_id=10 AND (" + loader.getSelection() + ")", null, null, null, null))
            {
                assertEquals(1, visible.getCount());
            }
            PersonalVideoActions.Change undo = PersonalVideoActions.set(app, file(), true);
            try (Cursor personal = db.query("video", new String[] {"_id"},
                                           "_id=10 AND (" + loader.getSelection() + ")", null, null, null, null))
            {
                assertEquals(0, personal.getCount());
            }
            assertTrue(PersonalVideoActions.undo(app, undo));
            try (Cursor restored = db.query("video", new String[] {"_id"},
                                           "_id=10 AND (" + loader.getSelection() + ")", null, null, null, null))
            {
                assertEquals(1, restored.getCount());
            }
        }
    }

    @Test public void personalRoleIsPersistentInvisibleToLibraryButRetainsFilesAndPlayback() throws Exception
    {
        PersonalVideoActions.Change undo = PersonalVideoActions.set(app, file(), true);
        assertTrue(PersonalVideoActions.isPersonal(app, 10));
        try (VideoOpenHelper helper = new VideoOpenHelper(app))
        {
            SQLiteDatabase db = helper.getReadableDatabase();
            try (Cursor rows = db.query("video", new String[] {"_id"}, "_id=10 AND " + PersonalVideoActions.VISIBLE,
                                        null, null, null, null))
            {
                assertEquals(0, rows.getCount());
            }
            try (Cursor row = db.rawQuery("SELECT bookmark,Archos_traktSeen,ArchosMediaScraper_id "
                                           + "FROM files WHERE _id=10",
                                           null))
            {
                assertTrue(row.moveToFirst());
                assertEquals(4444, row.getInt(0));
                assertEquals(1, row.getInt(1));
                assertEquals(100, row.getInt(2));
            }
        }
        assertTrue(PersonalVideoActions.undo(app, undo));
        assertFalse(PersonalVideoActions.isPersonal(app, 10));
        assertFalse(PersonalVideoActions.undo(app, undo));
    }

    @Test public void resultFromAnAlreadyRunningMatcherCannotSaveAfterMarkingPersonal() throws Exception
    {
        com.archos.mediascraper.BaseTags tags = Mockito.mock(com.archos.mediascraper.BaseTags.class);
        PersonalVideoActions.set(app, file(), true);
        assertEquals(-2, PersonalVideoActions.saveIfMatchable(app, 10, tags));
        Mockito.verifyNoInteractions(tags);
        assertTrue(PersonalVideoActions.isPersonal(app, 10));
    }

    @Test public void staleUndoAndChangedPhysicalIdentityCannotOverwriteANewerChoice() throws Exception
    {
        var first = PersonalVideoActions.set(app, file(), true);
        PersonalVideoActions.set(app, file(), true);
        assertFalse(PersonalVideoActions.undo(app, first));
        assertThrows(java.io.IOException.class, () -> PersonalVideoActions.set(app,
            new MissingMediaActions.StoredFile(10, "file:///changed.mkv", 123), false));
        assertTrue(PersonalVideoActions.isPersonal(app, 10));
    }

    @Test public void personalFlagsTravelInExistingHiddenCategoryAndRestoreAtomically() throws Exception
    {
        Video video = Mockito.mock(Video.class);
        Mockito.when(video.getId()).thenReturn(10L);
        Mockito.when(video.getFileUri()).thenReturn(android.net.Uri.parse("file:///one.mkv"));
        PortablePhysicalReferences refs = new PortablePhysicalReferences(app, List.of(video));
        String ref = refs.reference(10);
        assertEquals(ref, PersonalVideoActions.fingerprint("file:///one.mkv"));
        JSONObject payload = PortableHiddenItems.export(app, Map.of(10L, false), refs, Set.of(10L));
        assertEquals(2, payload.getInt("schema"));
        assertEquals(ref, payload.getJSONArray("personal").getJSONObject(0).getString("physical"));
        assertFalse(payload.toString().contains("one.mkv"));
        JSONObject before = MediaStateRecovery.capture(app, false, true);
        var prepared = PortableHiddenItems.prepare(payload, MediaStateRecovery.hiddenInventory(before), refs, false);
        JSONObject after = MediaStateRecovery.hiddenAfter(before, prepared);
        try (MediaStateRecovery.Session session = MediaStateRecovery.begin(app, before, false))
        {
            session.write(after, () -> false);
            session.commit();
        }
        assertTrue(PersonalVideoActions.isPersonal(app, 10));
    }

    @Test public void unresolvedBackupRolesApplyBeforeMatchingAndExplicitUnmarkSupersedesThem() throws Exception
    {
        String ref = PersonalVideoActions.fingerprint(file().path());
        String pending = new JSONObject().put("schema", 2).put("rows", new JSONArray())
            .put("personal", new JSONArray().put(new JSONObject().put("physical", ref))).toString();
        PreferenceManager.getDefaultSharedPreferences(app).edit()
            .putString(PortableHiddenItems.PENDING, pending).commit();
        PersonalVideoActions.reconcilePending(app);
        assertTrue(PersonalVideoActions.isPersonal(app, 10));
        PersonalVideoActions.set(app, file(), false);
        PersonalVideoActions.reconcilePending(app);
        assertFalse(PersonalVideoActions.isPersonal(app, 10));
    }
}
