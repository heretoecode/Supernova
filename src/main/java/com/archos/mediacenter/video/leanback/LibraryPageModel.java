package com.archos.mediacenter.video.leanback;

import android.content.Context;
import android.text.format.Formatter;
import com.archos.mediacenter.video.browser.adapters.object.Episode;
import com.archos.mediacenter.video.browser.adapters.object.Movie;
import com.archos.mediacenter.video.browser.adapters.object.Tvshow;
import com.archos.mediacenter.video.browser.adapters.object.Video;
import com.archos.mediacenter.video.leanback.PreviewLibraryColumns.Column;
import com.archos.mediacenter.video.leanback.PreviewLibraryLoader.Entry;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * The library pages' choices and words (page sheet LIBRARY.md, 0.158): the grouped Sort menu, the Order button's
 * words for each sort and the poster caption's three lines. One model for Movies, TV Shows and your own page.
 */
final class LibraryPageModel
{
    static final String ADDED = "added";
    static final String TITLE = "title";
    static final String RELEASE = "release";
    static final String TRENDING = "trending";
    static final String POPULAR = "popular";

    private LibraryPageModel()
    {
    }

    /** One line of the Sort menu. {@code preset} is the old preset index (0–4) or -1 for a file/series column. */
    static final class SortItem
    {
        final String key;
        final String label;
        final String icon;
        final String group;
        final int preset;
        final Column column;

        SortItem(String key, String label, String icon, String group, int preset, Column column)
        {
            this.key = key;
            this.label = label;
            this.icon = icon;
            this.group = group;
            this.preset = preset;
            this.column = column;
        }
    }

    /**
     * General (Date added · Title · Release date / Air date) · Trakt (only when connected) · Files · Series (not on
     * Movies). No Year sort (sheet).
     */
    static List<SortItem> sortItems(boolean trakt, boolean series, boolean tvOnly)
    {
        List<SortItem> items = new ArrayList<>();
        items.add(new SortItem(ADDED, "Date added", "added", "General", 0, null));
        items.add(new SortItem(TITLE, "Title", "title", "General", 1, null));
        items.add(new SortItem(RELEASE, tvOnly ? "Air date" : "Release date", "year", "General", 2, null));
        if (trakt)
        {
            items.add(new SortItem(TRENDING, "Trending", "trending", "Trakt", 3, null));
            items.add(new SortItem(POPULAR, "Popular", "popular", "Trakt", 4, null));
        }
        Object[][] files = {
            {Column.DURATION, "runtime"}, {Column.RESOLUTION, "resolution"}, {Column.HDR, "hdr"},
            {Column.AUDIO, "audio"}, {Column.SIZE, "total size"}, {Column.MODIFIED, "edit"},
            {Column.CODEC, "codec"}, {Column.BITRATE, "speed"}, {Column.CONTAINER, "file"}, {Column.PATH, "folder"}
        };
        for (Object[] file : files)
        {
            Column column = (Column) file[0];
            items.add(new SortItem(column.name(), column.label, (String) file[1], "Files", -1, column));
        }
        if (series)
        {
            items.add(new SortItem(Column.SEASONS.name(), Column.SEASONS.label, "tv", "Series", -1, Column.SEASONS));
            items.add(new SortItem(Column.EPISODES.name(), Column.EPISODES.label, "list", "Series", -1,
                                   Column.EPISODES));
            items.add(new SortItem(Column.AVERAGE.name(), Column.AVERAGE.label, "size", "Series", -1,
                                   Column.AVERAGE));
        }
        return items;
    }

    /** The key for the current sort: a preset (0–4) or a column. */
    static String key(int preset, Column column)
    {
        if (column != null)
        {
            return column.name();
        }
        String[] presets = {ADDED, TITLE, RELEASE, TRENDING, POPULAR};
        return presets[Math.max(0, Math.min(presets.length - 1, preset))];
    }

    /** The direction a sort starts in: A → Z for words, otherwise newest, largest, longest, most, highest. */
    static boolean startsAscending(String key)
    {
        return TITLE.equals(key) || isWords(key);
    }

    private static boolean isWords(String key)
    {
        return key.equals(Column.HDR.name()) || key.equals(Column.AUDIO.name()) || key.equals(Column.CODEC.name())
            || key.equals(Column.CONTAINER.name()) || key.equals(Column.PATH.name());
    }

    /** The Order button's words (sheet): they follow the sort. {@code ascending} = smallest, oldest or A first. */
    static String orderLabel(String key, boolean ascending)
    {
        if (TITLE.equals(key) || isWords(key))
        {
            return ascending ? "A → Z" : "Z → A";
        }
        if (ADDED.equals(key) || RELEASE.equals(key) || key.equals(Column.MODIFIED.name()))
        {
            return ascending ? "Oldest first" : "Newest first";
        }
        if (TRENDING.equals(key) || POPULAR.equals(key))
        {
            return ascending ? "Lowest ranked first" : "Highest ranked first";
        }
        if (key.equals(Column.DURATION.name()))
        {
            return ascending ? "Shortest first" : "Longest first";
        }
        if (key.equals(Column.SEASONS.name()) || key.equals(Column.EPISODES.name()))
        {
            return ascending ? "Fewest first" : "Most first";
        }
        if (key.equals(Column.RESOLUTION.name()))
        {
            return ascending ? "Lowest first" : "Highest first";
        }
        return ascending ? "Smallest first" : "Largest first";
    }

    /** The order icon: mirrored for the two directions (RULES §10). */
    static String orderIcon(boolean ascending)
    {
        return ascending ? "ascending" : "descending";
    }

    /** "1 h 24 min", "42 min". */
    static String duration(long ms)
    {
        if (ms <= 0)
        {
            return "";
        }
        long minutes = Math.max(1, ms / 60000);
        if (minutes < 60)
        {
            return minutes + " min";
        }
        return minutes % 60 == 0 ? minutes / 60 + " h" : minutes / 60 + " h " + minutes % 60 + " min";
    }

    /** Dates in words: "2 Nov 2026" (RULES §6). */
    static String date(long value)
    {
        if (value <= 0)
        {
            return "";
        }
        return new SimpleDateFormat("d MMM yyyy", Locale.UK)
            .format(new Date(value < 100000000000L ? value * 1000 : value));
    }

    /** "2024-03-14" to "14 Mar 2024"; a bare year stays a year. */
    static String releaseDate(Entry e)
    {
        String raw = e.releaseDate == null ? "" : e.releaseDate.trim();
        if (raw.length() >= 10)
        {
            try
            {
                Date parsed = new SimpleDateFormat("yyyy-MM-dd", Locale.UK).parse(raw.substring(0, 10));
                if (parsed != null)
                {
                    return new SimpleDateFormat("d MMM yyyy", Locale.UK).format(parsed);
                }
            }
            catch (java.text.ParseException ignored)
            {
                // fall through to the year
            }
        }
        return e.year() > 0 ? String.valueOf(e.year()) : "";
    }

    /** Episodes still to watch in a show. */
    static int episodesLeft(Tvshow show, Entry e)
    {
        int total = show.getEpisodeCount() > 0 ? show.getEpisodeCount() : e.episodes;
        return Math.max(0, total - show.getEpisodeWatchedCount());
    }

    static boolean started(Entry e)
    {
        if (e.media instanceof Tvshow)
        {
            return e.started || ((Tvshow) e.media).getEpisodeWatchedCount() > 0;
        }
        return e.media instanceof Video && ((Video) e.media).getResumeMs() > 0;
    }

    static boolean watched(Entry e)
    {
        if (e.media instanceof Tvshow)
        {
            Tvshow show = (Tvshow) e.media;
            return show.getEpisodeCount() > 0 && show.getEpisodeWatchedCount() >= show.getEpisodeCount();
        }
        return e.media instanceof Video && PreviewLibraryLoader.watched((Video) e.media);
    }

    /**
     * Caption line two (RULES §14): a show's size ("3 seasons · 30 episodes", one season "10 episodes") or progress
     * ("S2 E3 · 18 of 30 left"); a started film "42 min left"; otherwise a film's year and running time.
     */
    static String secondLine(Entry e, Entry nextEpisode)
    {
        if (e.media instanceof Tvshow)
        {
            Tvshow show = (Tvshow) e.media;
            int seasons = show.getSeasonCount() > 0 ? show.getSeasonCount() : e.seasons;
            int episodes = show.getEpisodeCount() > 0 ? show.getEpisodeCount() : e.episodes;
            if (started(e) && !watched(e))
            {
                String left = episodesLeft(show, e) + " of " + episodes + " left";
                if (nextEpisode != null && nextEpisode.media instanceof Episode)
                {
                    Episode next = (Episode) nextEpisode.media;
                    return "S" + next.getSeasonNumber() + " E" + next.getEpisodeNumber() + " · " + left;
                }
                return left;
            }
            String count = episodes + (episodes == 1 ? " episode" : " episodes");
            return seasons > 1 ? seasons + " seasons · " + count : count;
        }
        if (e.media instanceof Video)
        {
            Video video = (Video) e.media;
            if (video.getResumeMs() > 0 && !watched(e))
            {
                long total = e.runtime > 0 ? e.runtime : video.getDurationMs();
                long left = Math.max(60000, total - video.getResumeMs());
                return duration(left) + " left";
            }
            String year = e.year() > 0 ? String.valueOf(e.year()) : "";
            String length = duration(e.runtime > 0 ? e.runtime : video.getDurationMs());
            return year.isEmpty() || length.isEmpty() ? year + length : year + " · " + length;
        }
        return "";
    }

    /**
     * A Home row card's soft line (mock-up `home-v2/01`): an episode "S1 E2 · 31 min left" (or "S1 E2" not yet
     * started), a started film "42 min left", otherwise a title's year and length or size.
     */
    static String rowLine(Entry e)
    {
        if (e.media instanceof Episode)
        {
            Episode episode = (Episode) e.media;
            String where = "S" + episode.getSeasonNumber() + " E" + episode.getEpisodeNumber();
            if (episode.getResumeMs() > 0 && episode.getDurationMs() > 0)
            {
                long left = Math.max(60000, episode.getDurationMs() - episode.getResumeMs());
                return where + " · " + duration(left) + " left";
            }
            return where;
        }
        return secondLine(e, null);
    }

    /** How far into the film or episode, for a Home row card's inset bar. */
    static int rowProgress(Entry e)
    {
        if (!(e.media instanceof Video))
        {
            return 0;
        }
        Video video = (Video) e.media;
        if (video.getResumeMs() <= 0 || video.getDurationMs() <= 0 || PreviewLibraryLoader.watched(video))
        {
            return 0;
        }
        return (int) Math.max(1, Math.min(100, 100L * video.getResumeMs() / video.getDurationMs()));
    }

    /** Caption line three: follows the sort (e.g. "1.0 GB per episode"); empty for Title. */
    static String thirdLine(Context context, PreviewLibraryColumns columns, String key, Entry e)
    {
        if (TITLE.equals(key) || TRENDING.equals(key) || POPULAR.equals(key))
        {
            return "";
        }
        if (ADDED.equals(key))
        {
            String added = date(e.added);
            return added.isEmpty() ? "" : "Added " + added;
        }
        if (RELEASE.equals(key))
        {
            return releaseDate(e);
        }
        Column column;
        try
        {
            column = Column.valueOf(key);
        }
        catch (IllegalArgumentException unknown)
        {
            return "";
        }
        if (column == Column.AVERAGE)
        {
            String value = columns.value(e, Column.AVERAGE);
            if (!value.isEmpty())
            {
                return value + " per episode";
            }
            return e.media instanceof Movie ? columns.value(e, Column.SIZE) : "Unknown";
        }
        if (column == Column.SEASONS && e.seasons > 0)
        {
            return e.seasons + (e.seasons == 1 ? " season" : " seasons");
        }
        if (column == Column.EPISODES && e.episodes > 0)
        {
            return e.episodes + (e.episodes == 1 ? " episode" : " episodes");
        }
        if (column == Column.MODIFIED)
        {
            String modified = date(e.modified);
            return modified.isEmpty() ? "Unknown" : "Modified " + modified;
        }
        String value = columns.value(e, column);
        return value.isEmpty() ? "Unknown" : value;
    }

    /** How far into the film, or into the show's current episode, for the inset bar (0 = none). */
    static int progress(Entry e, Entry nextEpisode)
    {
        Video video = e.media instanceof Video ? (Video) e.media
            : nextEpisode != null && nextEpisode.media instanceof Video ? (Video) nextEpisode.media
                                                                        : null;
        if (video == null || watched(e) || video.getResumeMs() <= 0)
        {
            return 0;
        }
        long total = video.getDurationMs() > 0 ? video.getDurationMs() : e.runtime;
        return total <= 0 ? 0 : (int) Math.max(1, Math.min(100, 100L * video.getResumeMs() / total));
    }

    /** "3.1 GB" (short form, as Files). */
    static String size(Context context, long bytes)
    {
        return bytes <= 0 ? "" : Formatter.formatShortFileSize(context, bytes);
    }
}
