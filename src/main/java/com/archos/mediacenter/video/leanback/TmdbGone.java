package com.archos.mediacenter.video.leanback;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * D16 (0.158.9 report): titles TMDB no longer has (a 404 for the title itself). Their look-ups stop, and Library Care
 * › To fix lists them ("TMDB no longer knows this show: Fix match") until they are matched again.
 */
public final class TmdbGone
{
    static final String STORE = "supernova_tmdb_gone";

    private TmdbGone()
    {
    }

    public static void mark(Context context, String kind, long id)
    {
        store(context).edit().putLong(kind + ":" + id, System.currentTimeMillis()).apply();
    }

    public static boolean gone(Context context, String kind, long id)
    {
        return store(context).contains(kind + ":" + id);
    }

    /** A new match clears it. */
    public static void clear(Context context, String kind, long id)
    {
        store(context).edit().remove(kind + ":" + id).apply();
    }

    public static int count(Context context)
    {
        return store(context).getAll().size();
    }

    /** "TMDB no longer knows this show: Fix match" / "… this film …". */
    public static String line(String kind)
    {
        return "TMDB no longer knows this " + ("tv".equals(kind) ? "show" : "film") + ": Fix match";
    }

    private static SharedPreferences store(Context context)
    {
        return context.getApplicationContext().getSharedPreferences(STORE, Context.MODE_PRIVATE);
    }
}
