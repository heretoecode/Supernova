package com.archos.mediacenter.video.utils;

import android.app.Application;
import android.content.*;
import android.database.Cursor;
import android.database.CursorWrapper;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.os.OperationCanceledException;
import androidx.preference.PreferenceManager;
import com.archos.environment.ArchosUtils;
import com.archos.mediacenter.video.CustomApplication;
import com.archos.mediacenter.video.browser.adapters.mappers.VideoCursorMapper;
import com.archos.mediacenter.video.browser.adapters.object.Movie;
import com.archos.mediacenter.video.leanback.*;
import com.archos.mediacenter.video.utils.backup.*;
import com.archos.mediaprovider.DbHolder;
import com.archos.mediaprovider.video.*;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;
import org.json.*;
import org.junit.*;
import org.junit.runner.RunWith;
import org.robolectric.*;
import org.robolectric.annotation.*;
import org.robolectric.shadows.ShadowContentResolver;
import org.robolectric.util.ReflectionHelpers;
import static org.junit.Assert.*;
import org.slf4j.LoggerFactory;

/** Checks stale library queries cannot republish old snapshots after a pin restore. */
@RunWith(RobolectricTestRunner.class)
@Config(application = Application.class, sdk = 28)
@SQLiteMode(SQLiteMode.Mode.NATIVE)
public class PreviewLibraryRestoreRaceTest {
    private final CanonicalMatchProviderTest source = new CanonicalMatchProviderTest();

    @Before
    public void setup() {
        PreviewAsyncFixtures.offlineTransport();
        ReflectionHelpers.setStaticField(CustomApplication.class, "systemLocale", Locale.getDefault());
        ReflectionHelpers.setStaticField(CustomApplication.class, "mContext", RuntimeEnvironment.getApplication());
        ReflectionHelpers.setStaticField(
            CustomApplication.class, "log", LoggerFactory.getLogger(CustomApplication.class));
        ArchosUtils.setGlobalContext(RuntimeEnvironment.getApplication());
        source.setup();
    }

    @After
    public void close() throws Exception {
        PreviewAsyncFixtures.drain();
        ExecutorService writer =
            ReflectionHelpers.getStaticField(PreviewLibraryLoader.class, "cacheWriter");
        writer.submit(() -> {
        }).get(10, TimeUnit.SECONDS);
        PreviewLibraryLoader.cached = null;
        source.close();
    }

    @Test
    public void olderNativeQueryCannotRepublishItsSnapshotAfterSelectedPinRestore() throws Exception {
        checkRace(false);
    }

    @Test
    public void partiallyRejectedNativeQueryCannotPublishStaleSnapshotAfterRestore() throws Exception {
        checkRace(true);
    }

    private void checkRace(boolean rejectOneRecord) throws Exception {
        Application app = RuntimeEnvironment.getApplication();
        PreferenceManager.getDefaultSharedPreferences(app)
            .edit()
            .putBoolean("supernova_onboarding_complete", true)
            .commit();
        DbHolder holder = ReflectionHelpers.getField(source, "holder");
        holder.get().execSQL(
            "UPDATE files SET title='Native fixture',mime_type='video/x-matroska',duration=100000,date_added=1");
        boolean[] restore = {
            true
        };
        ContentProvider provider = new ContentProvider() {
            @Override
            public boolean onCreate() {
                return true;
            }
            @Override
            public String getType(Uri uri) {
                return null;
            }
            @Override
            public Cursor query(Uri uri, String[] columns, String where, String[] args, String order) {
                SQLiteQueryBuilder query = new SQLiteQueryBuilder();
                query.setTables(VideoOpenHelper.VIDEO_VIEW_NAME);
                Cursor rows = query.query(holder.get(), columns, where, args, null, null, order);
                rows.getCount();
                if (!Arrays.asList(columns).contains(VideoStore.Video.VideoColumns.DATE_ADDED))
                    return rows;
                try {
                    VideoCursorMapper mapper =
                        new VideoCursorMapper();
                    mapper.bindColumns(rows);
                    assertTrue(rows.moveToFirst());
                    assertNotNull(mapper.bind(rows));
                    rows.moveToPosition(-1);
                } catch (RuntimeException invalidFixture) {
                    throw new AssertionError("Native fixture mapping failed", invalidFixture);
                }
                if (restore[0]) {
                    restore[0] = false;
                    try {
                        new PreferenceRestoreTransaction(app).apply(
                            new PreferenceRestoreTransaction(app).prepare(
                                Map.of(BackupCategory.PINNED,
                                    new JSONObject().put("schema", 1).put("rows", new JSONArray())),
                                null),
                            () -> false);
                    } catch (Exception failure) {
                        rows.close();
                        throw new IllegalStateException(failure);
                    }
                    if (rejectOneRecord)
                        return new CursorWrapper(rows) {
                            @Override
                            public long getLong(int column) {
                                if (getPosition() == 0
                                    && column == getColumnIndex(VideoStore.Video.VideoColumns.DATE_ADDED))
                                    throw new IllegalArgumentException("Unreadable fixture record");
                                return super.getLong(column);
                            }
                        };
                }
                return rows;
            }
            @Override
            public Uri insert(Uri uri, ContentValues values) {
                throw new AssertionError();
            }
            @Override
            public int update(Uri uri, ContentValues values, String where, String[] args) {
                throw new AssertionError();
            }
            @Override
            public int delete(Uri uri, String where, String[] args) {
                throw new AssertionError();
            }
        };
        provider.attachInfo(app, null);
        ShadowContentResolver.registerProviderInternal(VideoStore.AUTHORITY, provider);
        PreviewLibraryLoader stale = new PreviewLibraryLoader(app);
        try {
            Cursor rows = stale.loadInBackground();
            if (rows != null)
                rows.close();
            fail("Expected cancellation: warning=" + ReflectionHelpers.getField(stale, "loadWarning")
                + ", cache=" + PreviewLibraryLoader.memoryCache());
        } catch (OperationCanceledException expected) {
        }
        assertNull(PreviewLibraryLoader.memoryCache());
        PreviewLibraryLoader fresh = new PreviewLibraryLoader(app);
        try (Cursor rows = fresh.loadInBackground()) {
            assertNotNull(rows);
            assertNotNull(PreviewLibraryLoader.memoryCache());
            assertFalse(PreviewLibraryLoader.memoryCache().movies.isEmpty());
            assertTrue(PreviewLibraryLoader.memoryCache().movies.stream().noneMatch(
                entry -> ((Movie) entry.media).isPinned()));
        }
    }
}
