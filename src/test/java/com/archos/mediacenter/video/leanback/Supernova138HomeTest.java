package com.archos.mediacenter.video.leanback;

import android.app.Application;
import android.os.Looper;
import android.view.View;
import androidx.preference.PreferenceManager;
import com.archos.mediacenter.video.browser.adapters.object.*;
import com.archos.mediacenter.video.leanback.PreviewLibraryLoader.*;
import com.archos.mediacenter.utils.videodb.VideoDbInfo;
import com.squareup.picasso.Picasso;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.*;
import java.util.stream.Collectors;
import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.*;
import org.junit.runner.RunWith;
import org.robolectric.*;
import org.robolectric.annotation.Config;
import org.robolectric.util.ReflectionHelpers;
import static org.junit.Assert.*;

/** Checks home playback targets, date sorting, row limits and refresh focus stability. */
@RunWith(RobolectricTestRunner.class)
@Config(application = Application.class, sdk = 28, qualifiers = "w960dp-h540dp-land-mdpi")
public class Supernova138HomeTest {
    Application app;
    PreviewPagesTest fixture = new PreviewPagesTest();

    @Before
    public void before() {
        app = RuntimeEnvironment.getApplication();
        PreferenceManager.getDefaultSharedPreferences(app)
            .edit()
            .clear()
            .putBoolean("supernova_history_migrated", true)
            .putBoolean("supernova_onboarding_complete", true)
            .putBoolean("supernova_welcome_dismissed", true)
            .commit();
        PreviewAsyncFixtures.offlineTransport();
    }

    @After
    public void after() throws Exception {
        PreviewAsyncFixtures.drain();
    }

    Entry episode(int season, int number, int resume, boolean watched, long time) {
        Entry e = new Entry(new Episode(number + season * 100, 7, season, number, "Episode", 0, 0, "", "", "Series",
                                "/TV/ep.mkv", null, null, 1800000, resume, 0, 0, watched, false, false, false, 1, time,
                                1920, 1080, null, null, null, null, 0, 1, 1000),
            1, 7, "Drama");
        e.onlineId = 7;
        return e;
    }

    void complete(int season, int number) {
        VideoDbInfo info = new VideoDbInfo();
        info.id = number + season * 100;
        info.isShow = true;
        info.scraperShowId = "7";
        info.scraperSeasonNr = season;
        info.scraperEpisodeNr = number;
        info.duration = 1800000;
        info.lastTimePlayed = 100;
        PreviewSeriesJourney.record(app, info, true, 1800000);
    }

    @Test
    public void committedNextEpisodeAndSeasonAppearWithoutStartingThem() {
        Entry end = episode(1, 10, -2, true, 100), next = episode(2, 2, 0, false, 0),
              later = episode(3, 1, 0, false, 0);
        complete(1, 10);
        Snapshot s = PreviewLibraryLoader.build(Arrays.asList(later, end, next), Collections.emptyList());
        new PreviewLibraryLoader(app).applyJourneys(s, Arrays.asList(later, end, next));
        assertEquals(1, s.continuingShows.size());
        assertSame(next, s.continuingShows.get(0));
        assertEquals(100, next.playedAt);
        assertTrue(next.secondary.startsWith("Up Next"));
        complete(3, 1);
        new PreviewLibraryLoader(app).applyJourneys(s, Arrays.asList(later, end, next));
        assertTrue(s.continuingShows.isEmpty());
    }

    @Test
    public void sampledInProgressEpisodeStillHasHomeResume() {
        Entry sampled = episode(1, 1, 5000, false, 100);
        VideoDbInfo info = new VideoDbInfo();
        info.id = 101;
        info.isShow = true;
        info.scraperShowId = "7";
        info.scraperSeasonNr = 1;
        info.scraperEpisodeNr = 1;
        info.duration = 1800000;
        PreviewSeriesJourney.record(app, info, false, 5000);
        Snapshot s = PreviewLibraryLoader.build(Arrays.asList(sampled), Collections.emptyList());
        new PreviewLibraryLoader(app).applyJourneys(s, Arrays.asList(sampled));
        assertSame(sampled, s.continuingShows.get(0));
    }

    @Test
    public void persistedEpochAirDatesSortCompleteCollectionBothWaysAndUnknownLast() {
        String[] names = {
            "Father Ted", "Only Fools and Horses", "DTF St. Louis", "Onar", "All Her Fault", "Horizon",
            "The O.C.", "The Office", "Orwell", "Wild Scandinavia", "Unknown"
        };
        int[] years = {
            1995, 1981, 2026, 2026, 2025, 1964, 2003, 2005, 2007, 2011, 0
        };
        List<Entry> titles = new ArrayList<>();
        for (int i = 0; i < names.length; i++) {
            Entry e = new Entry(new Movie(i + 1, "/m.mkv", names[i], i + 1, "", years[i], 0, null, null, 0, 0, 0, 0,
                                    false, false, false, false, 0, 0, 0, 0, null, null, null, null, 0, 1, 0, 0),
                0, 0, "");
            if (years[i] > 0)
                e.releaseDate = String.valueOf(LocalDate.of(years[i], 1, 1)
                        .atStartOfDay(ZoneOffset.UTC)
                        .toInstant()
                        .toEpochMilli());
            titles.add(e);
        }
        for (boolean ascending : new boolean[] {
            true, false
        }) {
            List<Entry> sorted = PreviewPages.sortEntries(titles, 2, ascending, new PreviewDiscovery());
            for (int i = 1; i < sorted.size() - 1; i++)
                assertTrue(ascending ? sorted.get(i - 1).year() <= sorted.get(i).year()
                                     : sorted.get(i - 1).year() >= sorted.get(i).year());
            assertEquals("Unknown", PreviewPages.displayName(sorted.get(sorted.size() - 1)));
            assertEquals(ascending ? "Horizon" : "DTF St. Louis", PreviewPages.displayName(sorted.get(0)));
        }
    }

    @Test
    public void dynamicMixedRowsApplyOneOrderAndOneUserLimit() {
        Snapshot s = new Snapshot();
        for (int n = 1; n <= 20; n++) {
            s.movies.add(new Entry(new Movie(n, "/movie" + n, "Film " + n, n, "", 2024, 7, null, null, 0, 0, 0, 0,
                                       false, false, false, false, 0, 0, 0, 0, null, null, null, null, 0, 1, 0, 0),
                n * 2, 0, "Drama"));
            s.shows.add(new Entry(new Tvshow(n, "Series " + n, null, 1, 2, 0, "/show" + n), n * 2 + 1, n, "Drama"));
        }
        PreviewHomeRows model = new PreviewHomeRows(app);
        PreviewHomeRows.Row row = new PreviewHomeRows.Row("custom:test", "Mixed");
        row.dynamic = true;
        row.maximum = 30;
        List<Entry> mixed = model.members(row, s);
        assertEquals(30, mixed.size());
        assertEquals(15, mixed.stream().filter(e -> e.media instanceof Movie).count());
        assertEquals(15, mixed.stream().filter(e -> e.media instanceof Tvshow).count());
        row.movies = false;
        assertTrue(model.members(row, s).stream().allMatch(e -> e.media instanceof Tvshow));
        row.movies = true;
        row.tv = false;
        assertTrue(model.members(row, s).stream().allMatch(e -> e.media instanceof Movie));
        row.tv = true;
        row.maximum = 0;
        assertEquals(40, model.members(row, s).size());
    }

    @Test
    public void automaticHomeRowsCapAfterEligibilityAndWatchNextDoesNot() {
        try {
            Picasso.get();
        } catch (IllegalStateException absent) {
            Picasso.setSingletonInstance(new Picasso.Builder(app).build());
        }
        Snapshot snapshot = new Snapshot();
        for (int n = 1; n <= 30; n++) {
            Entry e = new Entry(new Movie(n, "/film" + n, "Film " + n, n, "", 2024, 0, null, null, 100000, 1000, 0, 0,
                                    false, false, false, false, 0, n, 0, 0, null, null, null, null, 0, 1, 0, 0),
                n, 0, "");
            snapshot.movies.add(e);
            snapshot.continuingMovies.add(e);
            snapshot.recent.add(e);
        }
        JSONArray rows = new JSONArray();
        try {
            rows.put(new JSONObject()
                    .put("id", "watchnext")
                    .put("name", "Watch Next")
                    .put("members",
                        new JSONArray(
                            snapshot.movies.stream().map(Entry::key).collect(Collectors.toList()))));
        } catch (Exception impossible) {
            throw new AssertionError(impossible);
        }
        PreferenceManager.getDefaultSharedPreferences(app)
            .edit()
            .putString("preview_home_rows41", rows.toString())
            .commit();
        var host = Robolectric.buildActivity(TopNavigationTest.Host.class).setup().visible();
        try {
            PreviewPages pages = new PreviewPages(host.get(), (holder, item) -> {
            });
            host.get().setContentView(pages);
            pages.setSnapshot(snapshot);
            List<PreviewPages.Cell> cells = ReflectionHelpers.getField(pages, "cells");
            assertFalse(cells.stream().anyMatch(c -> c.title.contains("Library files need attention")));
            for (PreviewPages.Cell cell : cells)
                if ("continue".equals(cell.homeRowId) && cell.value instanceof List)
                    assertEquals(15, ((List<?>) cell.value).size());
            snapshot.continuingMovies.clear();
            PreferenceManager.getDefaultSharedPreferences(app)
                .edit()
                .putString("preview_home_rows41", rows.toString())
                .commit();
            pages.setSnapshot(snapshot);
            cells = ReflectionHelpers.getField(pages, "cells");
            assertEquals(30,
                ((List<?>) cells.stream()
                        .filter(c -> "watchnext".equals(c.homeRowId) && c.value instanceof List)
                        .findFirst()
                        .get()
                        .value)
                    .size());
            assertEquals(15,
                ((List<?>) cells.stream()
                        .filter(c -> "recent".equals(c.homeRowId) && c.value instanceof List)
                        .findFirst()
                        .get()
                        .value)
                    .size());
        } finally {
            host.pause().stop().destroy();
        }
    }

    @Test
    public void noOpRefreshAndCarouselRefreshDoNotStealHomeRowFocus() {
        try {
            Picasso.get();
        } catch (IllegalStateException absent) {
            Picasso.setSingletonInstance(new Picasso.Builder(app).build());
        }
        Snapshot snapshot = new Snapshot();
        Entry entry = new Entry(new Movie(1, "/film1", "Film 1", 1, "", 2024, 0, null, null, 100000, 1000, 0, 0, false,
                                    false, false, false, 0, 1, 0, 0, null, null, null, null, 0, 1, 0, 0),
            1, 0, "");
        snapshot.movies.add(entry);
        snapshot.recent.add(entry);
        var host = Robolectric.buildActivity(TopNavigationTest.Host.class).setup().visible();
        try {
            PreviewPages pages = new PreviewPages(host.get(), (holder, item) -> {
            });
            host.get().setContentView(pages);
            pages.setSnapshot(snapshot);
            for (int n = 0; n < 3; n++) {
                pages.measure(View.MeasureSpec.makeMeasureSpec(960, 1073741824),
                    View.MeasureSpec.makeMeasureSpec(540, 1073741824));
                pages.layout(0, 0, 960, 540);
                Shadows.shadowOf(Looper.getMainLooper()).idle();
            }
            View card = pages.findViewWithTag(entry.key());
            assertNotNull(card);
            assertTrue(card.requestFocus());
            java.util.List<View> changes = new ArrayList<>();
            pages.getViewTreeObserver().addOnGlobalFocusChangeListener((old, next) -> changes.add(next));
            pages.setSnapshot(snapshot);
            ReflectionHelpers.callInstanceMethod(pages, "renderFeatured");
            Shadows.shadowOf(Looper.getMainLooper()).idle();
            assertSame(card, pages.findFocus());
            assertTrue("No transient focus transfers during unrelated refresh", changes.isEmpty());
        } finally {
            host.pause().stop().destroy();
        }
    }

    @Test
    public void metadataCompactsAndNeverOrphansVotes() {
        SharedMetadata.Facts facts = new SharedMetadata.Facts();
        facts.genres = "Drama";
        facts.votes = 8200;
        assertEquals("Drama", SharedMetadata.primary(facts).toString());
        facts.rating = 8.1;
        facts.context = SharedMetadata.join(SharedMetadata.count(1, "Season"), SharedMetadata.count(24, "Episode"));
        assertEquals(
            "Drama · 1 Season · 24 Episodes · TMDB 8.1/10 · 8.2k votes", SharedMetadata.primary(facts).toString());
        assertEquals("2h 14m", SharedMetadata.runtime(134));
    }
}
