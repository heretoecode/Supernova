// Copyright 2026
// Licensed under the Apache License, Version 2.0
package com.archos.mediacenter.video.browser.loader;

import android.content.Context;

import com.archos.mediaprovider.video.LoaderUtils;
import com.archos.mediaprovider.video.VideoStore;

/** Lists scraped TV shows whose existing metadata identifies them as documentaries. */
public class DocumentaryTvshowsLoader extends AllTvshowsLoader {

    public DocumentaryTvshowsLoader(Context context, String sortOrder, boolean showWatched,
                                    boolean applyThrottleDelay, int throttleDelay) {
        super(context, sortOrder, showWatched, applyThrottleDelay, throttleDelay);
    }

    @Override
    public String getSelection() {
        StringBuilder selection = new StringBuilder();
        selection.append(super.getSelection());
        // AllTvshowsLoader has already closed and grouped its query.  Build the documentary
        // query directly instead so the genre restriction is applied before grouping episodes.
        selection.setLength(0);
        selection.append(super.getSelection().replace(") GROUP BY (" +
                VideoStore.Video.VideoColumns.SCRAPER_SHOW_ID, ""));
        selection.append(" AND ")
                .append(VideoStore.Video.VideoColumns.SCRAPER_S_GENRES)
                .append(" LIKE '%")
                .append(getContext().getString(com.archos.medialib.R.string.tvshow_genre_documentary))
                .append("%') GROUP BY (")
                .append(VideoStore.Video.VideoColumns.SCRAPER_SHOW_ID);
        return selection.toString();
    }
}
