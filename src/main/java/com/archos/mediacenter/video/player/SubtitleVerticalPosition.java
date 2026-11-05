package com.archos.mediacenter.video.player;

import android.content.SharedPreferences;

/** Percent control with an exact legacy rendering mode until the owner changes the migrated setting. */
public final class SubtitleVerticalPosition
{
    public static final String KEY_PERCENT = "subtitle_vertical_position_percent";
    public static final String KEY_LEGACY = "subtitle_vertical_position_legacy";
    public static final int DEFAULT_PERCENT = 5;

    public static int step(int percent)
    {
        return Math.max(0, Math.min(90, Math.round(percent / 5f) * 5));
    }

    public static int pixels(int height, int percent)
    {
        return (int)((long)Math.max(0, height) * step(percent) / 100);
    }

    public static void migrate(SharedPreferences prefs, String oldKey)
    {
        if (!prefs.contains(KEY_PERCENT) && prefs.contains(oldKey))
        {
            int legacy = Math.max(0, Math.min(255, prefs.getInt(oldKey, 0)));
            prefs.edit().putInt(KEY_PERCENT, step(Math.round(legacy * 100f / 765)))
                .putInt(KEY_LEGACY, legacy).commit();
        }
    }

    public static int percent(SharedPreferences prefs, String oldKey)
    {
        migrate(prefs, oldKey);
        return step(prefs.getInt(KEY_PERCENT, DEFAULT_PERCENT));
    }

    public static int restoredPixels(SharedPreferences prefs, String oldKey, int height)
    {
        migrate(prefs, oldKey);
        if (prefs.contains(KEY_LEGACY))
        {
            return (int)((long)Math.max(0, height) * prefs.getInt(KEY_LEGACY, 0) / 765) + 1;
        }
        return pixels(height, prefs.getInt(KEY_PERCENT, DEFAULT_PERCENT));
    }

    public static void save(SharedPreferences prefs, int percent)
    {
        prefs.edit().putInt(KEY_PERCENT, step(percent)).remove(KEY_LEGACY).apply();
    }

    private SubtitleVerticalPosition()
    {
    }
}
