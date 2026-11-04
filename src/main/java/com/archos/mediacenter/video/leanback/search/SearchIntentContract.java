package com.archos.mediacenter.video.leanback.search;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import com.archos.mediacenter.video.info.VideoInfoContract;
import com.archos.mediacenter.video.leanback.details.VideoDetailsActivity;

/** Adapts Android SEARCH and search-suggestion VIEW launches to TV hosts. */
public final class SearchIntentContract {
    private SearchIntentContract() {}

    public static String query(Intent intent) {
        String query = intent == null ? null : intent.getStringExtra(SearchManager.QUERY);
        return query == null ? "" : query;
    }

    public static Intent detailsIntent(Context context, Intent origin) {
        Intent details = new Intent(origin).setClass(context, VideoDetailsActivity.class);
        Uri uri = origin.getData();
        if (uri != null && "content".equals(uri.getScheme())
                && "com.archos.media".equals(uri.getAuthority())
                && uri.getPath() != null && uri.getPath().startsWith("/external/video/media/")) {
            try {
                details.putExtra(VideoInfoContract.EXTRA_VIDEO_ID, Long.parseLong(uri.getLastPathSegment()));
            } catch (NumberFormatException ignored) {
                // A non-indexed URI follows the retained Details path loader instead.
            }
        }
        return details;
    }
}
