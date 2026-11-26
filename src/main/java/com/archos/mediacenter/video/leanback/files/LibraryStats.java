package com.archos.mediacenter.video.leanback.files;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * The figures on Files › Your Library (approved 13 Nov 2026, mock-up network-files-tabs/44): the whole library, each
 * library page, and the shows that take the most space per episode. Pure arithmetic over one line per video.
 */
public final class LibraryStats
{
    /** One video in the library. A film has no show; an episode names its show. */
    public static final class Item
    {
        final String show;
        final long bytes;
        final boolean local;
        final long durationMs;
        final long resumeMs;
        final boolean watched;
        final boolean uhd;
        final boolean hdr;
        final long addedSeconds;
        /** The picture's height in pixels; 0 when unknown. */
        final int height;

        public Item(String show, long bytes, boolean local, long durationMs, long resumeMs,
                    boolean watched, boolean uhd, boolean hdr, long addedSeconds)
        {
            this(show, bytes, local, durationMs, resumeMs, watched, uhd, hdr, addedSeconds, uhd ? 2160 : 0);
        }

        public Item(String show, long bytes, boolean local, long durationMs, long resumeMs,
                    boolean watched, boolean uhd, boolean hdr, long addedSeconds, int height)
        {
            this.height = height;
            this.show = show;
            this.bytes = bytes;
            this.local = local;
            this.durationMs = durationMs;
            this.resumeMs = resumeMs;
            this.watched = watched;
            this.uhd = uhd;
            this.hdr = hdr;
            this.addedSeconds = addedSeconds;
        }

        boolean episode()
        {
            return show != null;
        }
    }

    /** A library page's line: its titles, videos and bytes on this device and on the network. */
    public static final class Page
    {
        public final String name;
        public int titles;
        public int videos;
        public long localBytes;
        public long networkBytes;

        Page(String name)
        {
            this.name = name;
        }

        public long bytes()
        {
            return localBytes + networkBytes;
        }
    }

    /** A show and how much space each of its episodes takes on average. */
    public static final class Heavy
    {
        public final String show;
        public final int episodes;
        public final long bytes;

        Heavy(String show, int episodes, long bytes)
        {
            this.show = show;
            this.episodes = episodes;
            this.bytes = bytes;
        }

        public long perEpisode()
        {
            return episodes == 0 ? 0 : bytes / episodes;
        }
    }

    public final Page all = new Page("All library pages");
    public final Page movies = new Page("Movies");
    public final Page shows = new Page("TV Shows");
    public int watchedTitles;
    public long hoursToWatch;
    public int uhdTitles;
    /** Videos by picture (F21): 4K, 1080p, 720p, SD, then HDR; unknown sizes are not counted. */
    public final int[] pictures = new int[5];
    public static final String[] PICTURES = {"4K", "1080p", "720p", "SD", "HDR"};
    public int hdrTitles;
    public int addedThisMonth;
    public final List<Heavy> heaviest = new ArrayList<>();

    public static LibraryStats of(List<Item> items, long monthStartSeconds)
    {
        LibraryStats stats = new LibraryStats();
        Map<String, List<Item>> byShow = new LinkedHashMap<>();
        long unwatchedMs = 0;
        for (Item item : items)
        {
            Page page = item.episode() ? stats.shows : stats.movies;
            page.videos++;
            stats.all.videos++;
            if (item.local)
            {
                page.localBytes += item.bytes;
                stats.all.localBytes += item.bytes;
            }
            else
            {
                page.networkBytes += item.bytes;
                stats.all.networkBytes += item.bytes;
            }
            if (!item.watched)
                unwatchedMs += Math.max(0, item.durationMs - Math.max(0, item.resumeMs));
            int picture = picture(item.uhd, item.height);
            if (picture >= 0)
                stats.pictures[picture]++;
            if (item.hdr)
                stats.pictures[4]++;
            if (item.episode())
            {
                byShow.computeIfAbsent(item.show, key -> new ArrayList<>()).add(item);
                continue;
            }
            stats.movies.titles++;
            stats.countTitle(item.watched, item.uhd, item.hdr, item.addedSeconds >= monthStartSeconds);
        }
        for (Map.Entry<String, List<Item>> show : byShow.entrySet())
        {
            stats.shows.titles++;
            boolean watched = true;
            boolean uhd = false;
            boolean hdr = false;
            boolean added = false;
            long bytes = 0;
            for (Item episode : show.getValue())
            {
                watched &= episode.watched;
                uhd |= episode.uhd;
                hdr |= episode.hdr;
                added |= episode.addedSeconds >= monthStartSeconds;
                bytes += episode.bytes;
            }
            stats.countTitle(watched, uhd, hdr, added);
            stats.heaviest.add(new Heavy(show.getKey(), show.getValue().size(), bytes));
        }
        stats.all.titles = stats.movies.titles + stats.shows.titles;
        stats.hoursToWatch = Math.round(unwatchedMs / 3_600_000d);
        stats.heaviest.sort((a, b) -> Long.compare(b.perEpisode(), a.perEpisode()));
        while (stats.heaviest.size() > 3)
            stats.heaviest.remove(stats.heaviest.size() - 1);
        return stats;
    }

    private void countTitle(boolean watched, boolean uhd, boolean hdr, boolean added)
    {
        if (watched)
            watchedTitles++;
        if (uhd)
            uhdTitles++;
        if (hdr)
            hdrTitles++;
        if (added)
            addedThisMonth++;
    }

    /** 0 for 4K, 1 for 1080p, 2 for 720p, 3 for SD, -1 when the size is unknown. Pure, for tests. */
    static int picture(boolean uhd, int height)
    {
        if (uhd || height >= 1800)
            return 0;
        if (height >= 900)
            return 1;
        if (height >= 600)
            return 2;
        return height > 0 ? 3 : -1;
    }

    /** "4K 12 · 1080p 340 · 720p 80 · SD 15 · HDR 30" (every quality, owner idea F21). */
    public String pictureCounts()
    {
        List<String> parts = new ArrayList<>();
        for (int index = 0; index < PICTURES.length; index++)
            parts.add(PICTURES[index] + " " + String.format(java.util.Locale.UK, "%,d", pictures[index]));
        return String.join(" · ", parts);
    }

    /** "12% 4K · 70% 1080p · 15% 720p · 3% SD · 8% HDR", of the videos whose picture size is known. */
    public String picturePercents()
    {
        int known = pictures[0] + pictures[1] + pictures[2] + pictures[3];
        List<String> parts = new ArrayList<>();
        for (int index = 0; index < PICTURES.length; index++)
            parts.add((known == 0 ? 0 : Math.round(100f * pictures[index] / known)) + "% " + PICTURES[index]);
        return String.join(" · ", parts);
    }

    /** The most common picture size ("4K", "1080p", "720p", "SD"), or "" when none is known. */
    public String topPicture()
    {
        int best = -1;
        for (int index = 0; index < 4; index++)
        {
            if (pictures[index] > 0 && (best < 0 || pictures[index] > pictures[best]))
                best = index;
        }
        return best < 0 ? "" : PICTURES[best];
    }

    /** The share of the most common picture size, "70% of videos" (Your Library's stacked value). */
    public String topPictureShare()
    {
        int known = pictures[0] + pictures[1] + pictures[2] + pictures[3];
        String top = topPicture();
        if (known == 0 || top.isEmpty())
            return "";
        int index = java.util.Arrays.asList(PICTURES).indexOf(top);
        return Math.round(100f * pictures[index] / known) + "% of videos";
    }

    /** Whole percent of the library's titles, 0 when there are none. */
    public int percent(int count)
    {
        return all.titles == 0 ? 0 : Math.round(100f * count / all.titles);
    }
}
