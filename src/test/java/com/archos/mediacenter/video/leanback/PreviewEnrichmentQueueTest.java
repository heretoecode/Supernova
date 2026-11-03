package com.archos.mediacenter.video.leanback;

import android.app.Application;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.*;
import android.view.View;
import android.widget.Button;
import androidx.preference.PreferenceManager;
import androidx.recyclerview.widget.RecyclerView;
import com.archos.mediacenter.video.browser.adapters.object.Episode;
import com.archos.mediacenter.video.browser.adapters.object.Movie;
import com.squareup.picasso.Picasso;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.robolectric.*;
import org.robolectric.annotation.Config;
import org.robolectric.util.ReflectionHelpers;
import static org.junit.Assert.*;

/** Checks metadata enrichment priorities, viewport changes and removal of obsolete queued work. */
@RunWith(RobolectricTestRunner.class)
@Config(application = Application.class, sdk = 28)
public class PreviewEnrichmentQueueTest {
    @Before
    public void isolatePreviewTransport() {
        PreviewAsyncFixtures.offlineTransport();
        try {
            Picasso.get();
        } catch (IllegalStateException absent) {
            Picasso.setSingletonInstance(
                new Picasso.Builder(RuntimeEnvironment.getApplication()).build());
        }
    }

    @After
    public void drainPreviewWorkers() throws Exception {
        PreviewAsyncFixtures.drain();
    }

    @Test
    public void oldCountryOrLanguageJobsCannotRunUnderCurrentScope() throws Exception {
        Context context = RuntimeEnvironment.getApplication();
        SQLiteOpenHelper helper = (SQLiteOpenHelper) ReflectionHelpers.callConstructor(
            Class.forName("com.archos.mediacenter.video.leanback.PreviewEnrichmentQueue$Store"),
            ReflectionHelpers.ClassParameter.from(Context.class, context));
        try {
            SQLiteDatabase db = helper.getWritableDatabase();
            db.execSQL("INSERT INTO jobs VALUES('movie:1:en:US:true','movie',1,0,3,0,0,0)");
            db.execSQL("INSERT INTO jobs VALUES('movie:2:en:IE:true','movie',2,40,1,0,0,0)");
            try (Cursor row = PreviewEnrichmentQueue.nextJob(db, "en:IE:true", 1)) {
                assertTrue(row.moveToFirst());
                assertEquals(2, row.getLong(2));
            }
            try (Cursor row = PreviewEnrichmentQueue.nextJob(db, "fr:IE:true", 1)) {
                assertFalse(row.moveToFirst());
            }
            try (Cursor row = PreviewEnrichmentQueue.nextJob(db, "en:US:true", 1)) {
                assertTrue(row.moveToFirst());
                assertEquals(1, row.getLong(2));
                assertEquals(3, row.getInt(3));
            }
        } finally {
            helper.close();
        }
    }

    @Test
    public void horizontalViewportUsesScrolledCardsRatherThanFirstSix() {
        RecyclerView rail =
            Mockito.mock(RecyclerView.class);
        Mockito.when(rail.getWidth()).thenReturn(300);
        Mockito.when(rail.getChildCount()).thenReturn(3);
        List<PreviewLibraryLoader.Entry> entries = new ArrayList<>(),
                                                   visible = new ArrayList<>(),
                                                   next = new ArrayList<>();
        for (int i = 0; i < 20; i++)
            entries.add(new PreviewLibraryLoader.Entry(
                Mockito.mock(Movie.class), 0, 0, ""));
        for (int i = 0; i < 3; i++) {
            View child = Mockito.mock(View.class);
            Mockito.when(rail.getChildAt(i)).thenReturn(child);
            Mockito.when(rail.getChildAdapterPosition(child)).thenReturn(i + 7);
            Mockito.when(child.getLeft()).thenReturn(i * 100 - 100);
            Mockito.when(child.getRight()).thenReturn(i * 100);
        }
        PreviewPages.collectVisibleRail(rail, entries, visible, next);
        assertEquals(Arrays.asList(entries.get(8), entries.get(9)), visible);
        assertEquals(entries.subList(10, 16), next);
    }

    @Test
    public void replacingViewportRestoresPagePriorityWithoutLosingForegroundOrProgress() throws Exception {
        Context context = RuntimeEnvironment.getApplication();
        SQLiteOpenHelper helper = (SQLiteOpenHelper) ReflectionHelpers.callConstructor(
            Class.forName("com.archos.mediacenter.video.leanback.PreviewEnrichmentQueue$Store"),
            ReflectionHelpers.ClassParameter.from(Context.class, context));
        try {
            SQLiteDatabase db = helper.getWritableDatabase();
            db.execSQL("INSERT INTO jobs VALUES('movie:1:test','movie',1,10,3,1234,0,2)");
            db.execSQL("INSERT INTO jobs VALUES('movie:2:test','movie',2,0,4,0,0,0)");
            db.execSQL("INSERT INTO jobs VALUES('tv:3:test','tv',3,10,2,0,0,0)");
            Map<String, Integer> baseline = new HashMap<>();
            baseline.put("movie:1:test", PreviewEnrichmentQueue.CURRENT_PAGE);
            baseline.put("movie:2:test", PreviewEnrichmentQueue.CURRENT_PAGE);
            PreviewEnrichmentQueue.restorePagePriorities(db, baseline);
            try (Cursor cursor =
                     db.rawQuery("SELECT priority,stage,next_at,season_cursor FROM jobs ORDER BY identity", null)) {
                assertTrue(cursor.moveToNext());
                assertEquals(PreviewEnrichmentQueue.CURRENT_PAGE, cursor.getInt(0));
                assertEquals(3, cursor.getInt(1));
                assertEquals(1234, cursor.getLong(2));
                assertEquals(2, cursor.getInt(3));
                assertTrue(cursor.moveToNext());
                assertEquals(PreviewEnrichmentQueue.FOREGROUND, cursor.getInt(0));
                assertEquals(4, cursor.getInt(1));
                assertTrue(cursor.moveToNext());
                assertEquals(PreviewEnrichmentQueue.BACKGROUND, cursor.getInt(0));
            }
        } finally {
            helper.close();
        }
    }

    @Test
    public void settledFocusPriorityIsTemporaryAndNeverDemotesOpenDetails() throws Exception {
        Context c = RuntimeEnvironment.getApplication();
        SQLiteOpenHelper helper = (SQLiteOpenHelper) ReflectionHelpers.callConstructor(
            Class.forName("com.archos.mediacenter.video.leanback.PreviewEnrichmentQueue$Store"),
            ReflectionHelpers.ClassParameter.from(Context.class, c));
        try {
            SQLiteDatabase db = helper.getWritableDatabase();
            db.execSQL("INSERT INTO jobs VALUES('movie:1:test','movie',1,5,1,0,0,0)");
            db.execSQL("INSERT INTO jobs VALUES('movie:2:test','movie',2,0,2,0,0,0)");
            PreviewEnrichmentQueue.restorePagePriorities(db, Map.of("movie:1:test", 20));
            try (Cursor rows = db.rawQuery("SELECT priority,stage FROM jobs ORDER BY media", null)) {
                assertTrue(rows.moveToNext());
                assertEquals(20, rows.getInt(0));
                assertEquals(1, rows.getInt(1));
                assertTrue(rows.moveToNext());
                assertEquals(0, rows.getInt(0));
                assertEquals(2, rows.getInt(1));
            }
        } finally {
            helper.close();
        }
    }

    @Test
    public void episodePriorityTargetsParentSeriesAndKeepsPackageProgress() throws Exception {
        Context context = RuntimeEnvironment.getApplication();
        SQLiteOpenHelper helper = (SQLiteOpenHelper) ReflectionHelpers.callConstructor(
            Class.forName("com.archos.mediacenter.video.leanback.PreviewEnrichmentQueue$Store"),
            ReflectionHelpers.ClassParameter.from(Context.class, context));
        try {
            SQLiteDatabase db = helper.getWritableDatabase();
            db.execSQL("INSERT INTO jobs VALUES('tv:42:test','tv',42,40,3,0,0,2)");
            Episode episode =
                Mockito.mock(Episode.class);
            Mockito.when(episode.getOnlineId()).thenReturn(999L);
            PreviewLibraryLoader.Entry entry = new PreviewLibraryLoader.Entry(episode, 0, 7, "");
            entry.onlineId = 42;
            ReflectionHelpers.callStaticMethod(PreviewEnrichmentQueue.class, "offerEntry",
                ReflectionHelpers.ClassParameter.from(SQLiteDatabase.class, db),
                ReflectionHelpers.ClassParameter.from(PreviewLibraryLoader.Entry.class, entry),
                ReflectionHelpers.ClassParameter.from(int.class, PreviewEnrichmentQueue.HOME),
                ReflectionHelpers.ClassParameter.from(String.class, "test"));
            try (Cursor cursor = db.rawQuery("SELECT media,priority,stage,season_cursor FROM jobs", null)) {
                assertEquals(1, cursor.getCount());
                assertTrue(cursor.moveToFirst());
                assertEquals(42, cursor.getLong(0));
                assertEquals(PreviewEnrichmentQueue.HOME, cursor.getInt(1));
                assertEquals(3, cursor.getInt(2));
                assertEquals(2, cursor.getInt(3));
            }
        } finally {
            helper.close();
        }
    }

    @Test
    public void seasonQueueUpgradePreservesPendingJobs() throws Exception {
        Context context = RuntimeEnvironment.getApplication();
        try (SQLiteDatabase db = context.openOrCreateDatabase("preview-enrichment.db", 0, null)) {
            db.execSQL(
                "CREATE TABLE jobs(identity TEXT PRIMARY KEY,kind TEXT NOT NULL,media INTEGER NOT NULL,priority INTEGER NOT NULL,stage INTEGER NOT NULL,next_at INTEGER NOT NULL,completed_at INTEGER NOT NULL)");
            db.execSQL("INSERT INTO jobs VALUES('tv:42','tv',42,10,3,1234,0)");
            db.setVersion(1);
        }
        SQLiteOpenHelper helper = (SQLiteOpenHelper) ReflectionHelpers.callConstructor(
            Class.forName("com.archos.mediacenter.video.leanback.PreviewEnrichmentQueue$Store"),
            ReflectionHelpers.ClassParameter.from(Context.class, context));
        try (Cursor cursor =
                 helper.getWritableDatabase().rawQuery("SELECT identity,stage,next_at,season_cursor FROM jobs", null)) {
            assertTrue(cursor.moveToFirst());
            assertEquals("tv:42", cursor.getString(0));
            assertEquals(3, cursor.getInt(1));
            assertEquals(1234, cursor.getLong(2));
            assertEquals(0, cursor.getInt(3));
        } finally {
            helper.close();
        }
    }

    @Test
    @Config(qualifiers = "w960dp-h540dp-land-mdpi")
    public void settledPreparationResolvesNestedHomeCardsAndClearsObsoleteFocus() {
        Context c = RuntimeEnvironment.getApplication();
        PreferenceManager.getDefaultSharedPreferences(c)
            .edit()
            .putBoolean("supernova_onboarding_complete", true)
            .commit();
        var host = Robolectric.buildActivity(TopNavigationTest.Host.class).setup().visible();
        try {
            PreviewPages page = new PreviewPages(host.get(), (holder, item) -> {
            });
            host.get().setContentView(page);
            page.setDiscovery(new PreviewDiscovery());
            var movie = new Movie(1, "/fixture/movie",
                "Indexed fixture", 42, "Indexed synopsis", 2024, 7, "", null, 100000, 0, 0, 0, false, false, false,
                false, 1, 0, 1920, 1080, null, null, null, null, 0, 1, 1000, 0);
            PreviewLibraryLoader.Entry entry = new PreviewLibraryLoader.Entry(movie, 1, 0, "Drama");
            entry.onlineId = 42;
            PreviewLibraryLoader.Snapshot snapshot = new PreviewLibraryLoader.Snapshot();
            snapshot.movies.add(entry);
            snapshot.recent.add(entry);
            page.setSnapshot(snapshot);
            PreviewPagesTest.layout(page);
            View card = page.findViewWithTag(entry.key());
            assertNotNull("Actual nested Recently Added card", card);
            Button outside = new Button(host.get());
            ReflectionHelpers.callInstanceMethod(
                page, "prepareDetails", ReflectionHelpers.ClassParameter.from(View.class, outside));
            assertNull(ReflectionHelpers.getField(page, "preparingDetails"));
            ReflectionHelpers.callInstanceMethod(
                page, "prepareDetails", ReflectionHelpers.ClassParameter.from(View.class, card));
            assertSame(entry, ReflectionHelpers.getField(page, "preparingDetails"));
            ReflectionHelpers.callInstanceMethod(
                page, "prepareDetails", ReflectionHelpers.ClassParameter.from(View.class, outside));
            assertNull("Pending old item cannot survive focus leaving media",
                ReflectionHelpers.getField(page, "preparingDetails"));
        } finally {
            host.pause().stop().destroy();
        }
    }

    @Test
    @Config(qualifiers = "w960dp-h540dp-land-mdpi")
    public void leavingMediaForGlobalNavigationClearsPreparationAndReturningReschedules() {
        Context c = RuntimeEnvironment.getApplication();
        PreferenceManager.getDefaultSharedPreferences(c)
            .edit()
            .putBoolean("supernova_onboarding_complete", true)
            .commit();
        var host = Robolectric.buildActivity(TopNavigationTest.Host.class).setup().visible();
        try {
            PreviewPages page = new PreviewPages(host.get(), (holder, item) -> {
            });
            TopNavigation nav = new TopNavigation(host.get(), page, index -> {
            }, () -> true);
            host.get().setContentView(nav);
            page.setDiscovery(new PreviewDiscovery());
            var movie = new Movie(1, "/fixture/movie",
                "Indexed fixture", 42, "Indexed synopsis", 2024, 7, "", null, 100000, 0, 0, 0, false, false, false,
                false, 1, 0, 1920, 1080, null, null, null, null, 0, 1, 1000, 0);
            PreviewLibraryLoader.Entry entry = new PreviewLibraryLoader.Entry(movie, 1, 0, "Drama");
            entry.onlineId = 42;
            PreviewLibraryLoader.Snapshot snapshot = new PreviewLibraryLoader.Snapshot();
            snapshot.movies.add(entry);
            snapshot.recent.add(entry);
            page.setSnapshot(snapshot);
            PreviewPagesTest.layout(nav);
            View card = page.findViewWithTag(entry.key());
            assertNotNull(card);
            assertTrue("Real nested card takes focus", card.requestFocus());
            assertSame(entry, ReflectionHelpers.getField(page, "preparingDetails"));
            assertTrue("Global navigation takes focus", nav.findViewWithTag("semantic:topnav.home").requestFocus());
            assertFalse(page.hasFocus());
            assertNull(
                "Leaving the page clears the pending identity", ReflectionHelpers.getField(page, "preparingDetails"));
            assertEquals("", ReflectionHelpers.getField(page, "preparingKey"));
            assertTrue(card.requestFocus());
            assertSame("Returning to the same card schedules preparation again", entry,
                ReflectionHelpers.getField(page, "preparingDetails"));
            assertFalse(((String) ReflectionHelpers.getField(page, "preparingKey")).isEmpty());
        } finally {
            host.pause().stop().destroy();
        }
    }
}
