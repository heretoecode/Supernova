package com.archos.mediacenter.video.player;

import android.content.Context;
import android.content.SharedPreferences;
import androidx.preference.PreferenceManager;

/**
 * Settings › Playback › Seek preview (PLAYER.md, owner 21 Nov 2026): **Off · Chapter pictures · Video frames**. Off =
 * the time box only; Chapter pictures = the picture at the start of the chapter you are seeking into; Video frames =
 * the nearest picture to where you are. Replaces the old on / off switch, whose choice carries over (on = Video
 * frames).
 */
public final class SeekThumbnailPreferences
{
    /** The old switch, read once to carry its choice over. */
    public static final String KEY = "seek_thumbnails_enabled";
    public static final String MODE_KEY = "seek_preview_mode";
    public static final boolean DEFAULT_ENABLED = true;
    public static final String OFF = "off";
    public static final String CHAPTERS = "chapters";
    public static final String FRAMES = "frames";

    /** Off, Chapter pictures or Video frames. */
    public static String mode(Context context)
    {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        String mode = prefs.getString(MODE_KEY, null);
        if (OFF.equals(mode) || CHAPTERS.equals(mode) || FRAMES.equals(mode))
        {
            return mode;
        }
        boolean old;
        try
        {
            old = prefs.getBoolean(KEY, DEFAULT_ENABLED);
        }
        catch (ClassCastException unreadable)
        {
            old = DEFAULT_ENABLED;
        }
        return old ? FRAMES : OFF;
    }

    /** True when the engine should make pictures at all (Chapter pictures or Video frames). */
    public static boolean enabled(Context context)
    {
        return !OFF.equals(mode(context));
    }

    /** True for a change that affects seek pictures (the new setting or the old switch). */
    public static boolean affects(String key)
    {
        return KEY.equals(key) || MODE_KEY.equals(key);
    }

    /** Where the picture comes from: the chapter's start in Chapter pictures, the position itself in Video frames. */
    public static long pictureAt(String mode, long position, java.util.List<PlaybackChapters.Chapter> chapters)
    {
        if (!CHAPTERS.equals(mode) || chapters == null)
        {
            return position;
        }
        long start = 0;
        for (PlaybackChapters.Chapter chapter : chapters)
        {
            if (chapter.startMs <= position)
            {
                start = Math.max(start, chapter.startMs);
            }
        }
        return start;
    }

    private SeekThumbnailPreferences()
    {
    }
}
