package com.archos.mediacenter.video.leanback;

import android.content.Context;
import com.archos.mediacenter.video.leanback.PreviewLibraryLoader.Entry;
import com.archos.mediacenter.video.leanback.PreviewLibraryLoader.Snapshot;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;
import java.util.TreeSet;
import java.util.function.IntConsumer;

/**
 * The library pages' filters (page sheet LIBRARY.md; mock-ups `library-pages/23`, `24`): Watched, Year (quick
 * choices that hold titles, then your own range) with counts, shared by the Filters menu and Customise page.
 */
final class LibraryFilters
{
    static final int ALL = 0;
    static final int UNWATCHED = 1;
    static final int IN_PROGRESS = 2;
    static final int WATCHED = 3;
    static final String[] WATCHED_LABELS = {"All", "Unwatched", "In progress", "Watched"};
    static final String[] WATCHED_ICONS = {"close", "eye", "continue", "check"};

    private LibraryFilters()
    {
    }

    static boolean watchedMatches(Entry e, int filter)
    {
        boolean watched = LibraryPageModel.watched(e);
        boolean started = LibraryPageModel.started(e);
        switch (filter)
        {
            case UNWATCHED:
                return !watched && !started;
            case IN_PROGRESS:
                return started && !watched;
            case WATCHED:
                return watched;
            default:
                return true;
        }
    }

    static boolean yearMatches(Entry e, int from, int to)
    {
        if (from <= 0 && to <= 0)
        {
            return true;
        }
        int year = e.year();
        return year > 0 && (from <= 0 || year >= from) && (to <= 0 || year <= to);
    }

    static int count(List<Entry> entries, int watchedFilter)
    {
        int count = 0;
        for (Entry e : entries)
        {
            if (watchedMatches(e, watchedFilter))
            {
                count++;
            }
        }
        return count;
    }

    static int count(List<Entry> entries, int from, int to)
    {
        int count = 0;
        for (Entry e : entries)
        {
            if (yearMatches(e, from, to))
            {
                count++;
            }
        }
        return count;
    }

    /** Every year that holds a title, newest first. */
    static List<Integer> years(Snapshot snapshot)
    {
        List<Entry> all = new ArrayList<>(snapshot.movies);
        all.addAll(snapshot.shows);
        return years(all);
    }

    static List<Integer> years(List<Entry> entries)
    {
        TreeSet<Integer> years = new TreeSet<>(Collections.reverseOrder());
        for (Entry e : entries)
        {
            if (e.year() > 0)
            {
                years.add(e.year());
            }
        }
        return new ArrayList<>(years);
    }

    /**
     * Quick choices (mock-up `24`): Any year, This year, Last 5 years, then each decade that holds titles. Adds a
     * label (with its year span) and a range {from, to} for each; only ranges that hold titles.
     */
    static void quickYears(List<Integer> present, List<String> labels, List<int[]> ranges)
    {
        labels.add("— Quick Choices");
        ranges.add(null);
        labels.add("Any year");
        ranges.add(new int[] {0, 0});
        int now = Calendar.getInstance().get(Calendar.YEAR);
        if (present.contains(now))
        {
            labels.add("This year · " + now);
            ranges.add(new int[] {now, now});
        }
        boolean recent = false;
        for (int year : present)
        {
            recent |= year >= now - 4 && year <= now;
        }
        if (recent)
        {
            labels.add("Last 5 years · " + (now - 4) + " – " + now);
            ranges.add(new int[] {now - 4, now});
        }
        TreeSet<Integer> decades = new TreeSet<>(Collections.reverseOrder());
        for (int year : present)
        {
            decades.add(year / 10 * 10);
        }
        for (int decade : decades)
        {
            labels.add(decade + "s");
            ranges.add(new int[] {decade, decade + 9});
        }
    }

    /** One year from those that hold titles (newest first), or Any. */
    static void chooseYear(Context context, List<Integer> present, String title, int current, IntConsumer chosen)
    {
        List<String> labels = new ArrayList<>();
        labels.add("Any");
        for (int year : present)
        {
            labels.add(String.valueOf(year));
        }
        int at = current <= 0 ? 0 : Math.max(0, present.indexOf(current) + 1);
        String[] icons = new String[labels.size()];
        java.util.Arrays.fill(icons, "year");
        icons[0] = "close";
        PreviewDialog.menuBelow(context, null, title, labels.toArray(new String[0]), icons,
                                Collections.singleton(at), at, true, true,
                                n -> chosen.accept(n == 0 ? 0 : present.get(n - 1)));
    }
}
