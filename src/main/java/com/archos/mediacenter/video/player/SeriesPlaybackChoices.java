package com.archos.mediacenter.video.player;

import android.content.Context;
import android.content.SharedPreferences;
import com.archos.mediacenter.utils.introdb.IntroSegments;

/**
 * "Use this format and skipping for all of <show>" (Playback tab save row, player v11, approved 13 Nov 2026): one
 * picture format and one intro/credits skipping choice remembered per series, used whenever one of its episodes
 * plays. Only the series' own id is stored, never a file name or address.
 */
public final class SeriesPlaybackChoices
{
    private static final String FILE = "supernova_series_playback";

    /** The skipping choice of the series now playing, or null to use the everyday setting. */
    private static volatile SegmentSkippingPolicy.Mode skipOverride;

    private final SharedPreferences prefs;

    public SeriesPlaybackChoices(Context context)
    {
        prefs = context.getApplicationContext().getSharedPreferences(FILE, Context.MODE_PRIVATE);
    }

    public boolean enabled(String series)
    {
        return series != null && prefs.getBoolean(series + ".on", false);
    }

    /** Turns the save row on (remembering today's format and skipping) or off. */
    public void set(String series, boolean on, int format, SegmentSkippingPolicy.Mode skip)
    {
        if (series == null)
            return;
        SharedPreferences.Editor edit = prefs.edit().putBoolean(series + ".on", on);
        if (on)
            edit.putInt(series + ".format", format).putString(series + ".skip", skip.name());
        edit.apply();
    }

    /** The remembered picture format, or -1 for none. */
    public int format(String series)
    {
        return enabled(series) ? prefs.getInt(series + ".format", -1) : -1;
    }

    /** The remembered skipping choice, or null for none. */
    public SegmentSkippingPolicy.Mode skip(String series)
    {
        if (!enabled(series))
            return null;
        try
        {
            return SegmentSkippingPolicy.Mode.valueOf(prefs.getString(series + ".skip", ""));
        }
        catch (IllegalArgumentException unknown)
        {
            return null;
        }
    }

    /** Called when an episode starts: its series' skipping choice applies until another video starts. */
    public static void useSkipping(SegmentSkippingPolicy.Mode mode)
    {
        skipOverride = mode;
    }

    /** The skipping mode to use for intros and credits: the series' choice when it has one. */
    public static SegmentSkippingPolicy.Mode skipFor(IntroSegments.Type type, SegmentSkippingPolicy.Mode everyday)
    {
        SegmentSkippingPolicy.Mode series = skipOverride;
        boolean shared = type == IntroSegments.Type.INTRO || type == IntroSegments.Type.OUTRO
                         || type == IntroSegments.Type.CREDITS;
        return series != null && shared ? series : everyday;
    }
}
