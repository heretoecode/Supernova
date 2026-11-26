package com.archos.mediacenter.video.leanback.files;

import android.content.Context;
import android.database.Cursor;
import com.archos.mediaprovider.video.VideoStore;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * M5 (owner, 26 Nov 2026): titles whose TMDB look-up hasn't run yet are not "Not matched". The library marks a video
 * still waiting for its look-up with scraper id 0 (a failed look-up has -1 or "auto not found"). Read off the main
 * thread; Library Care uses the latest answer.
 */
public final class PendingLookups
{
    private static final ExecutorService WORKER = Executors.newSingleThreadExecutor();
    private static volatile Set<Long> pending = Collections.emptySet();

    private PendingLookups()
    {
    }

    /** The videos still waiting for their look-up, as last read; asks for a fresh read in the background. */
    public static Set<Long> ids(Context context)
    {
        Context app = context.getApplicationContext();
        WORKER.execute(() -> pending = read(app));
        return pending;
    }

    static Set<Long> read(Context context)
    {
        Set<Long> ids = new HashSet<>();
        String waiting = VideoStore.Video.VideoColumns.ARCHOS_MEDIA_SCRAPER_ID + "=0 AND "
            + VideoStore.Video.VideoColumns.ARCHOS_HIDDEN_BY_USER + "=0";
        try (Cursor cursor = context.getContentResolver().query(VideoStore.Video.Media.EXTERNAL_CONTENT_URI,
                                                                new String[] {VideoStore.Video.VideoColumns._ID},
                                                                waiting, null, null))
        {
            while (cursor != null && cursor.moveToNext())
            {
                ids.add(cursor.getLong(0));
            }
        }
        catch (RuntimeException unavailable)
        {
            return pending;
        }
        return ids;
    }
}
