package com.archos.mediacenter.video.utils;

import android.app.Application;
import android.content.Context;
import androidx.preference.PreferenceManager;
import com.archos.mediacenter.video.leanback.animes.AnimesSortOrderEntry;
import com.archos.mediacenter.video.leanback.movies.MoviesSortOrderEntry;
import com.archos.mediacenter.video.leanback.tvshow.TvshowsSortOrderEntry;
import com.archos.mediaprovider.video.VideoStore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;
import static org.junit.Assert.*;

@RunWith(RobolectricTestRunner.class)
@Config(application = Application.class, sdk = 28)
public class LibrarySortPreferencesTest {
    @Test
    public void restoredKeysDefaultsAndEverySortIndexKeepTheirHistoricalMeaning() {
        Context context = RuntimeEnvironment.getApplication();
        String movies = "com.archos.mediacenter.video.leanback.movies.AllMoviesGridFragment";
        String shows = "com.archos.mediacenter.video.leanback.tvshow.AllTvshowsGridFragment";
        String animes = "com.archos.mediacenter.video.leanback.animes.AllAnimesGridFragment";
        assertEquals(movies + "_SORT", LibrarySortPreferences.MOVIES_SORT_KEY);
        assertEquals(shows + "_SORT", LibrarySortPreferences.TVSHOWS_SORT_KEY);
        assertEquals(animes + "_SORT", LibrarySortPreferences.ANIMES_SORT_KEY);
        assertEquals(movies + "_SHOW_WATCHED", LibrarySortPreferences.MOVIES_SHOW_WATCHED_KEY);
        assertEquals(shows + "_SHOW_WATCHED", LibrarySortPreferences.TVSHOWS_SHOW_WATCHED_KEY);
        assertEquals(animes + "_SHOW_WATCHED", LibrarySortPreferences.ANIMES_SHOW_WATCHED_KEY);
        var prefs = PreferenceManager.getDefaultSharedPreferences(context);
        prefs.edit().clear().putString(movies + "_SORT", "restored SQL")
                .putBoolean(movies + "_SHOW_WATCHED", false).commit();
        assertEquals("restored SQL", prefs.getString(LibrarySortPreferences.MOVIES_SORT_KEY,
                LibrarySortPreferences.MOVIES_DEFAULT_SORT));
        assertFalse(prefs.getBoolean(LibrarySortPreferences.MOVIES_SHOW_WATCHED_KEY,
                LibrarySortPreferences.SHOW_WATCHED_DEFAULT));
        assertTrue(prefs.getBoolean(LibrarySortPreferences.TVSHOWS_SHOW_WATCHED_KEY,
                LibrarySortPreferences.SHOW_WATCHED_DEFAULT));
        assertEquals(com.archos.mediacenter.video.browser.loader.MoviesLoader.DEFAULT_SORT,
                LibrarySortPreferences.MOVIES_DEFAULT_SORT);
        assertEquals(com.archos.mediacenter.video.browser.loader.FilmsLoader.DEFAULT_SORT,
                LibrarySortPreferences.FILMS_DEFAULT_SORT);
        assertEquals(com.archos.mediacenter.video.browser.loader.AnimesLoader.DEFAULT_SORT,
                LibrarySortPreferences.ANIMES_DEFAULT_SORT);
        assertEquals(com.archos.mediacenter.video.tvshow.TvshowSortOrderEntries.DEFAULT_SORT,
                LibrarySortPreferences.TVSHOWS_DEFAULT_SORT);
        CharSequence[] movieSql = {"name COLLATE LOCALIZED ASC",
                VideoStore.MediaColumns.DATE_ADDED + " DESC",
                VideoStore.Video.VideoColumns.SCRAPER_M_YEAR + " DESC, "
                        + VideoStore.Video.VideoColumns.SCRAPER_M_RELEASE_DATE + " DESC, name COLLATE NOCASE ASC",
                SortOrder.DURATION.getAsc(), SortOrder.SCRAPER_M_RATING.getDesc()};
        assertArrayEquals(movieSql, MoviesSortOrderEntry.getSortOrderEntryValues(context,
                LibrarySortPreferences.MOVIES_SORT_ORDERS));
        assertArrayEquals(movieSql, AnimesSortOrderEntry.getSortOrderEntryValues(context,
                LibrarySortPreferences.ANIMES_SORT_ORDERS));
        assertArrayEquals(new CharSequence[] {
                VideoStore.Video.VideoColumns.SCRAPER_TITLE + " COLLATE LOCALIZED ASC",
                "max(" + VideoStore.Video.VideoColumns.DATE_ADDED + ") DESC",
                "max(" + VideoStore.Video.VideoColumns.ARCHOS_LAST_TIME_PLAYED + ") DESC",
                VideoStore.Video.VideoColumns.SCRAPER_S_PREMIERED + " DESC",
                "max(" + VideoStore.Video.VideoColumns.SCRAPER_E_AIRED + ") DESC",
                "IFNULL(" + VideoStore.Video.VideoColumns.SCRAPER_S_RATING + ", 0) DESC"},
                TvshowsSortOrderEntry.getSortOrderEntryValues(context, LibrarySortPreferences.TVSHOWS_SORT_ORDERS));
        // Labels must stay paired with the corresponding SQL entry, not reordered in settings.
        assertEquals(movieSql.length, MoviesSortOrderEntry.getSortOrderEntries(context,
                LibrarySortPreferences.MOVIES_SORT_ORDERS).length);
        assertEquals(6, TvshowsSortOrderEntry.getSortOrderEntries(context,
                LibrarySortPreferences.TVSHOWS_SORT_ORDERS).length);
    }
}
