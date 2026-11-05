package com.archos.mediacenter.video.utils;

import android.util.SparseArray;
import com.archos.mediacenter.video.R;
import com.archos.mediacenter.video.browser.loader.AnimesLoader;
import com.archos.mediacenter.video.browser.loader.FilmsLoader;
import com.archos.mediacenter.video.browser.loader.MoviesLoader;
import com.archos.mediacenter.video.leanback.animes.AnimesSortOrderEntry;
import com.archos.mediacenter.video.leanback.movies.MoviesSortOrderEntry;
import com.archos.mediacenter.video.leanback.tvshow.TvshowsSortOrderEntry;
import com.archos.mediacenter.video.tvshow.TvshowSortOrderEntries;
import com.archos.mediaprovider.video.VideoStore;

/** Stable persisted keys, defaults and index-to-SQL mappings shared by TV hosts and settings. */
public final class LibrarySortPreferences {
    private LibrarySortPreferences() {}

    // These historical key bytes must survive the eventual retirement of their original screens.
    public static final String MOVIES_SORT_KEY = "com.archos.mediacenter.video.leanback.movies.AllMoviesGridFragment_SORT";
    public static final String MOVIES_SHOW_WATCHED_KEY = "com.archos.mediacenter.video.leanback.movies.AllMoviesGridFragment_SHOW_WATCHED";
    public static final String TVSHOWS_SORT_KEY = "com.archos.mediacenter.video.leanback.tvshow.AllTvshowsGridFragment_SORT";
    public static final String TVSHOWS_SHOW_WATCHED_KEY = "com.archos.mediacenter.video.leanback.tvshow.AllTvshowsGridFragment_SHOW_WATCHED";
    public static final String ANIMES_SORT_KEY = "com.archos.mediacenter.video.leanback.animes.AllAnimesGridFragment_SORT";
    public static final String ANIMES_SHOW_WATCHED_KEY = "com.archos.mediacenter.video.leanback.animes.AllAnimesGridFragment_SHOW_WATCHED";
    public static final boolean SHOW_WATCHED_DEFAULT = true;
    public static final String MOVIES_DEFAULT_SORT = MoviesLoader.DEFAULT_SORT;
    public static final String FILMS_DEFAULT_SORT = FilmsLoader.DEFAULT_SORT;
    public static final String ANIMES_DEFAULT_SORT = AnimesLoader.DEFAULT_SORT;
    public static final String TVSHOWS_DEFAULT_SORT = TvshowSortOrderEntries.DEFAULT_SORT;

    public static final SparseArray<MoviesSortOrderEntry> MOVIES_SORT_ORDERS = new SparseArray<MoviesSortOrderEntry>();
    static {
        MOVIES_SORT_ORDERS.put(0, new MoviesSortOrderEntry(R.string.sort_by_name_asc,        "name COLLATE LOCALIZED ASC"));
        MOVIES_SORT_ORDERS.put(1, new MoviesSortOrderEntry(R.string.sort_by_date_added_desc, VideoStore.MediaColumns.DATE_ADDED + " DESC"));
        MOVIES_SORT_ORDERS.put(2, new MoviesSortOrderEntry(R.string.sort_by_year_desc,       VideoStore.Video.VideoColumns.SCRAPER_M_YEAR + " DESC, " + VideoStore.Video.VideoColumns.SCRAPER_M_RELEASE_DATE + " DESC, name COLLATE NOCASE ASC"));
        MOVIES_SORT_ORDERS.put(3, new MoviesSortOrderEntry(R.string.sort_by_duration_asc,    SortOrder.DURATION.getAsc()));
        MOVIES_SORT_ORDERS.put(4, new MoviesSortOrderEntry(R.string.sort_by_rating_asc,      SortOrder.SCRAPER_M_RATING.getDesc()));
    }

    public static final SparseArray<TvshowsSortOrderEntry> TVSHOWS_SORT_ORDERS = new SparseArray<TvshowsSortOrderEntry>();
    static {
        TVSHOWS_SORT_ORDERS.put(0, new TvshowsSortOrderEntry(R.string.sort_by_name_asc,        VideoStore.Video.VideoColumns.SCRAPER_TITLE + " COLLATE LOCALIZED ASC"));
        TVSHOWS_SORT_ORDERS.put(1, new TvshowsSortOrderEntry(R.string.sort_by_date_added_desc, "max(" + VideoStore.Video.VideoColumns.DATE_ADDED + ") DESC"));
        TVSHOWS_SORT_ORDERS.put(2, new TvshowsSortOrderEntry(R.string.sort_by_date_played_desc, "max(" + VideoStore.Video.VideoColumns.ARCHOS_LAST_TIME_PLAYED + ") DESC"));
        TVSHOWS_SORT_ORDERS.put(3, new TvshowsSortOrderEntry(R.string.sort_by_date_premiered_desc,       VideoStore.Video.VideoColumns.SCRAPER_S_PREMIERED + " DESC"));
        TVSHOWS_SORT_ORDERS.put(4, new TvshowsSortOrderEntry(R.string.sort_by_date_aired_desc, "max(" + VideoStore.Video.VideoColumns.SCRAPER_E_AIRED + ") DESC"));
        TVSHOWS_SORT_ORDERS.put(5, new TvshowsSortOrderEntry(R.string.sort_by_rating_asc,      "IFNULL(" + VideoStore.Video.VideoColumns.SCRAPER_S_RATING + ", 0) DESC"));
    }

    public static final SparseArray<AnimesSortOrderEntry> ANIMES_SORT_ORDERS = new SparseArray<AnimesSortOrderEntry>();
    static {
        ANIMES_SORT_ORDERS.put(0, new AnimesSortOrderEntry(R.string.sort_by_name_asc,        "name COLLATE LOCALIZED ASC"));
        ANIMES_SORT_ORDERS.put(1, new AnimesSortOrderEntry(R.string.sort_by_date_added_desc, VideoStore.MediaColumns.DATE_ADDED + " DESC"));
        ANIMES_SORT_ORDERS.put(2, new AnimesSortOrderEntry(R.string.sort_by_year_desc,       VideoStore.Video.VideoColumns.SCRAPER_M_YEAR + " DESC, " + VideoStore.Video.VideoColumns.SCRAPER_M_RELEASE_DATE + " DESC, name COLLATE NOCASE ASC"));
        ANIMES_SORT_ORDERS.put(3, new AnimesSortOrderEntry(R.string.sort_by_duration_asc,    SortOrder.DURATION.getAsc()));
        ANIMES_SORT_ORDERS.put(4, new AnimesSortOrderEntry(R.string.sort_by_rating_asc,      SortOrder.SCRAPER_M_RATING.getDesc()));
    }
}
