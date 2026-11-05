package com.archos.mediacenter.video.player;

import android.content.Context;
import android.net.Uri;
import com.archos.mediacenter.video.browser.adapters.object.Episode;
import com.archos.mediacenter.video.browser.adapters.object.Video;
import com.archos.mediacenter.video.leanback.PreviewLibraryLoader.Entry;
import com.archos.mediacenter.video.leanback.PreviewVariants;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/** Current-season strip data uses the same physical-version policy as normal title playback. */
public final class PlaybackEpisodes
{
    public static final class Item
    {
        public final long id;
        public final int number;
        public final String title;
        public final Uri still;
        public final Uri uri;
        public final long durationMs;
        public final long resumeMs;
        public final boolean watched;

        Item(Episode episode)
        {
            id = episode.getId();
            number = episode.getEpisodeNumber();
            title = episode.getEpisodeName();
            still = episode.getPictureUri();
            uri = episode.getFileUri();
            durationMs = Math.max(0, episode.getDurationMs());
            resumeMs = Math.max(0, episode.getResumeMs());
            watched = episode.isWatched();
        }
    }

    public static List<Item> season(Context context, List<Entry> source, long currentId)
    {
        Entry current = current(source, currentId);
        if (current == null)
        {
            return Collections.emptyList();
        }
        Episode now = (Episode)current.media;
        Map<Integer, List<Episode>> groups = new TreeMap<>();
        for (Entry entry : source)
        {
            if (entry.show == current.show && entry.media instanceof Episode)
            {
                Episode candidate = (Episode)entry.media;
                if (!candidate.isUserHidden() && candidate.getFileUri() != null &&
                    candidate.getSeasonNumber() == now.getSeasonNumber())
                {
                    groups.computeIfAbsent(candidate.getEpisodeNumber(), ignored -> new ArrayList<>()).add(candidate);
                }
            }
        }
        List<Item> result = new ArrayList<>();
        for (List<Episode> choices : groups.values())
        {
            result.add(new Item((Episode)PreviewVariants.choose(context, choices)));
        }
        return Collections.unmodifiableList(result);
    }

    public static Episode adjacent(List<Entry> source, long currentId, int direction)
    {
        List<Entry> playable = new ArrayList<>();
        for (Entry entry : source)
        {
            if (entry.media instanceof Episode)
            {
                Episode episode = (Episode)entry.media;
                // Keep an explicitly played hidden current row solely as the navigation origin.
                if (episode.getId() == currentId || !episode.isUserHidden() && episode.getFileUri() != null)
                {
                    playable.add(entry);
                }
            }
        }
        return PreviewVariants.adjacentEpisode(playable, currentId, direction);
    }

    private static Entry current(List<Entry> source, long currentId)
    {
        for (Entry entry : source)
        {
            if (entry.media instanceof Episode && ((Video)entry.media).getId() == currentId)
            {
                return entry;
            }
        }
        return null;
    }

    private PlaybackEpisodes()
    {
    }
}
