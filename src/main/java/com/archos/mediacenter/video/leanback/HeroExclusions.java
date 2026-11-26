package com.archos.mediacenter.video.leanback;

import android.content.Context;
import android.content.SharedPreferences;
import com.archos.mediacenter.video.browser.adapters.object.Movie;
import com.archos.mediacenter.video.browser.adapters.object.Tvshow;
import java.util.HashSet;
import java.util.Set;

/**
 * Exclude from hero (owner, 21 Nov 2026; BRIEF_0158_B › Home review): the title leaves the carousel at once and
 * stays out, stored by its identity (the TMDB id), even if it is removed and added again, until included again.
 */
public final class HeroExclusions
{
    static final String PREFS = "supernova_hero_exclusions";
    static final String KEY = "identities";

    private HeroExclusions()
    {
    }

    /** "movie:603" or "tv:1396"; titles without a TMDB id use their library key. */
    static String identity(PreviewLibraryLoader.Entry entry)
    {
        if (entry == null)
            return "";
        String kind = entry.media instanceof Tvshow ? "tv" : entry.media instanceof Movie ? "movie" : "other";
        return entry.onlineId > 0 ? kind + ":" + entry.onlineId : "key:" + entry.key();
    }

    private static SharedPreferences prefs(Context context)
    {
        return context.getSharedPreferences(PREFS, Context.MODE_PRIVATE);
    }

    public static boolean excluded(Context context, PreviewLibraryLoader.Entry entry)
    {
        return excluded(context, identity(entry));
    }

    static boolean excluded(Context context, String identity)
    {
        return context != null && prefs(context).getStringSet(KEY, new HashSet<>()).contains(identity);
    }

    public static void exclude(Context context, PreviewLibraryLoader.Entry entry)
    {
        change(context, identity(entry), true);
    }

    public static void include(Context context, PreviewLibraryLoader.Entry entry)
    {
        change(context, identity(entry), false);
    }

    public static int count(Context context)
    {
        return prefs(context).getStringSet(KEY, new HashSet<>()).size();
    }

    static void change(Context context, String identity, boolean add)
    {
        if (context == null || identity.isEmpty())
            return;
        Set<String> values = new HashSet<>(prefs(context).getStringSet(KEY, new HashSet<>()));
        if (add)
            values.add(identity);
        else
            values.remove(identity);
        SharedPreferences.Editor edit = prefs(context).edit().putStringSet(KEY, values);
        if (add)
            edit.putLong("at:" + identity, System.currentTimeMillis());
        else
            edit.remove("at:" + identity);
        edit.apply();
    }

    /** When a title was excluded (0 when not known), for "Movie · 2019 · excluded 14 Oct 2026". */
    public static long excludedAt(Context context, PreviewLibraryLoader.Entry entry)
    {
        return prefs(context).getLong("at:" + identity(entry), 0);
    }

    /**
     * Files › Library Care › Excluded from hero (FILES.md, tv/19): the excluded titles still in the library, A–Z.
     */
    public static java.util.List<PreviewLibraryLoader.Entry> titles(Context context,
                                                                   PreviewLibraryLoader.Snapshot snapshot)
    {
        java.util.List<PreviewLibraryLoader.Entry> found = new java.util.ArrayList<>();
        if (snapshot == null)
            return found;
        java.util.List<PreviewLibraryLoader.Entry> all = new java.util.ArrayList<>(snapshot.movies);
        all.addAll(snapshot.shows);
        for (PreviewLibraryLoader.Entry entry : all)
            if (excluded(context, entry))
                found.add(entry);
        found.sort((a, b) -> PreviewPages.displayName(a).compareToIgnoreCase(PreviewPages.displayName(b)));
        return found;
    }

    /** Include all again (top right of the page). */
    public static void includeAll(Context context)
    {
        prefs(context).edit().clear().apply();
    }
}
