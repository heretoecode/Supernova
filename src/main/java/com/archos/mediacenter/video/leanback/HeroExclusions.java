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
        prefs(context).edit().putStringSet(KEY, values).apply();
    }
}
