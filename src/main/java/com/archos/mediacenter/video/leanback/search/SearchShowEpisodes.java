package com.archos.mediacenter.video.leanback.search;

import android.content.Context;
import android.database.Cursor;
import com.archos.mediacenter.video.browser.adapters.mappers.VideoCursorMapper;
import com.archos.mediacenter.video.browser.adapters.object.Episode;
import com.archos.mediacenter.video.browser.adapters.object.Video;
import com.archos.mediacenter.video.browser.loader.EpisodesLoader;
import com.archos.mediacenter.video.diagnostics.Diagnostics;
import com.archos.mediacenter.video.leanback.PreviewLibraryLoader;
import java.util.ArrayList;
import java.util.List;

/**
 * A show's episodes for Search's long-press menu, so it can say "Resume S2 E3" and offer Play from the start like the
 * library pages (LIBRARY.md). One small query on the show's own rows.
 */
final class SearchShowEpisodes
{
    private SearchShowEpisodes()
    {
    }

    static List<PreviewLibraryLoader.Entry> of(Context context, long showId)
    {
        List<PreviewLibraryLoader.Entry> episodes = new ArrayList<>();
        EpisodesLoader loader = new EpisodesLoader(context, showId, -1, false);
        try (Cursor cursor = context.getContentResolver().query(loader.getUri(), loader.getProjection(),
                                                                loader.getSelection(), loader.getSelectionArgs(),
                                                                loader.getSortOrder()))
        {
            if (cursor == null)
            {
                return episodes;
            }
            VideoCursorMapper mapper = new VideoCursorMapper();
            mapper.bindColumns(cursor);
            while (cursor.moveToNext())
            {
                Object media = mapper.bind(cursor);
                if (media instanceof Episode)
                {
                    episodes.add(new PreviewLibraryLoader.Entry((Episode) media, 0, showId, ""));
                }
            }
        }
        catch (RuntimeException unavailable)
        {
            Diagnostics.error("search_show_episodes_unavailable", unavailable);
        }
        return episodes;
    }

    /** The first episode (lowest season, then number), for Play from the start. */
    static Video first(List<PreviewLibraryLoader.Entry> episodes)
    {
        Episode first = null;
        for (PreviewLibraryLoader.Entry entry : episodes)
        {
            Episode episode = (Episode) entry.media;
            if (first == null || episode.getSeasonNumber() < first.getSeasonNumber()
                || episode.getSeasonNumber() == first.getSeasonNumber()
                       && episode.getEpisodeNumber() < first.getEpisodeNumber())
            {
                first = episode;
            }
        }
        return first;
    }
}
