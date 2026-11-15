package com.archos.mediacenter.video.leanback;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.SystemClock;

import androidx.preference.PreferenceManager;

/**
 * Remembers the page and the focused item while the app is open, so that when Android ends the app in the
 * background it reopens where the owner was (owner decision 23.2). A restart of the device, or leaving with Back,
 * starts again on Home.
 */
public final class ReopenMemory
{
    /** Two readings of the device's start time may differ slightly; a real restart differs by far more. */
    static final long BOOT_TOLERANCE_MS = 60_000L;

    private static final String TAB = "reopen_tab";
    private static final String CELL = "reopen_cell";
    private static final String CHILD = "reopen_child";
    private static final String POSITION = "reopen_position";
    private static final String INNER = "reopen_inner";
    private static final String BOOT = "reopen_boot_ms";

    public static final class Place
    {
        public final int tab;
        public final String cell;
        public final String child;
        public final int position;
        public final int inner;

        public Place(int tab, String cell, String child, int position, int inner)
        {
            this.tab = tab;
            this.cell = cell;
            this.child = child;
            this.position = position;
            this.inner = inner;
        }
    }

    private ReopenMemory()
    {
    }

    static boolean sameBoot(long savedBootMs, long currentBootMs)
    {
        return savedBootMs > 0 && Math.abs(savedBootMs - currentBootMs) <= BOOT_TOLERANCE_MS;
    }

    static boolean validTab(int tab)
    {
        return tab >= 0 && tab < 4;
    }

    private static long bootMs()
    {
        return System.currentTimeMillis() - SystemClock.elapsedRealtime();
    }

    public static void save(Context context, Place place)
    {
        if (place == null || !validTab(place.tab))
        {
            return;
        }
        SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(context).edit();
        editor.putInt(TAB, place.tab);
        editor.putString(CELL, place.cell);
        editor.putString(CHILD, place.child);
        editor.putInt(POSITION, place.position);
        editor.putInt(INNER, place.inner);
        editor.putLong(BOOT, bootMs());
        editor.apply();
    }

    /** The remembered place, or null on a first launch, after a device restart or after leaving with Back. */
    public static Place load(Context context)
    {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        int tab = prefs.getInt(TAB, -1);
        if (!validTab(tab) || !sameBoot(prefs.getLong(BOOT, 0), bootMs()))
        {
            return null;
        }
        return new Place(tab, prefs.getString(CELL, null), prefs.getString(CHILD, null), prefs.getInt(POSITION, 0),
                         prefs.getInt(INNER, 0));
    }

    public static void clear(Context context)
    {
        SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(context).edit();
        editor.remove(TAB);
        editor.remove(CELL);
        editor.remove(CHILD);
        editor.remove(POSITION);
        editor.remove(INNER);
        editor.remove(BOOT);
        editor.apply();
    }
}
