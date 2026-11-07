package com.archos.mediacenter.video.player;

import android.content.Context;
import android.database.Cursor;
import android.os.CancellationSignal;
import com.archos.mediacenter.video.leanback.PreviewVariants;
import com.archos.mediacenter.video.browser.adapters.mappers.VideoCursorMapper;
import com.archos.mediacenter.video.browser.adapters.object.Video;
import com.archos.mediacenter.video.browser.loader.VideoLoader;
import com.archos.mediacenter.video.leanback.PreviewLibraryLoader.Entry;
import com.archos.mediaprovider.video.PersonalVideoActions;
import com.archos.mediaprovider.video.VideoStore;
import com.archos.mediaprovider.video.SupernovaLibraryPolicy;
import java.util.ArrayList;
import java.util.List;

/** Worker-only player inventory, independent of Home cache eviction or refresh. */
public final class PlayerEpisodeRepository
{
    public static List<Entry> load(Context context, String tmdbSeries, CancellationSignal cancelled) {
        if (tmdbSeries == null || !tmdbSeries.matches("[1-9][0-9]{0,17}"))
            return List.of();
        cancelled.throwIfCanceled();
        PreviewVariants.initialize(context);
        context.getSharedPreferences(SupernovaLibraryPolicy.HEALTH, Context.MODE_PRIVATE).getAll();
        List<Entry> result = new ArrayList<>();
        String where = VideoStore.Video.VideoColumns.SCRAPER_S_ONLINE_ID + "=? AND " + PersonalVideoActions.VISIBLE;
        try (Cursor rows = context.getContentResolver().query(VideoStore.Video.Media.EXTERNAL_CONTENT_URI,
                VideoLoader.mProjection, where, new String[] {tmdbSeries}, null, cancelled)) {
            if (rows == null)
                throw new IllegalStateException("Episode inventory unavailable");
            VideoCursorMapper mapper = new VideoCursorMapper();
            mapper.bindColumns(rows);
            int show = rows.getColumnIndexOrThrow(VideoStore.Video.VideoColumns.SCRAPER_SHOW_ID);
            int visited = 0;
            while (rows.moveToNext()) {
                cancelled.throwIfCanceled();
                if (++visited > 10000 || Thread.currentThread().isInterrupted())
                    throw new IllegalStateException("Episode inventory incomplete");
                Video video = (Video)mapper.bind(rows);
                if (!SupernovaLibraryPolicy.excluded(context, video.getFileUri()))
                    result.add(new Entry(video, 0, rows.getLong(show), ""));
            }
        }
        return List.copyOf(result);
    }

    private PlayerEpisodeRepository()
    {
    }
}
