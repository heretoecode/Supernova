package com.archos.mediacenter.video.player;

import android.content.Context;
import androidx.preference.PreferenceManager;

public final class SeekThumbnailPreferences
{
    public static final String KEY = "seek_thumbnails_enabled";
    public static final boolean DEFAULT_ENABLED = true;

    public static boolean enabled(Context context)
    {
        return PreferenceManager.getDefaultSharedPreferences(context).getBoolean(KEY, DEFAULT_ENABLED);
    }

    private SeekThumbnailPreferences()
    {
    }
}
