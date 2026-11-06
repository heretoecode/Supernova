package com.archos.mediacenter.video.player;

import android.content.Context;
import android.content.SharedPreferences;
import androidx.preference.PreferenceManager;

/**
 * The one way to read and change the subtitle style (size, colour, vertical position, outline, background,
 * background opacity) and save it, shared by Playback Settings in the player and Settings › Audio & Subtitles ›
 * Style (0.156). Both go through {@link SubtitleManager}, so the saved keys and conversions are identical.
 */
public final class SubtitleStyleControls
{
    public static final int[] SIZES = {25, 50, 75};
    public static final String[] SIZE_NAMES = {"Small", "Medium", "Large"};
    public static final int[] COLOURS = PlaybackSubtitlePanel.COLOURS;
    public static final String[] COLOUR_NAMES = PlaybackSubtitlePanel.COLOUR_NAMES;
    public static final int[] OPACITY = PlaybackSubtitlePanel.OPACITY;
    public static final String[] OPACITY_NAMES = PlaybackSubtitlePanel.OPACITY_LABELS;
    public static final int POSITION_STEP = PlaybackSubtitlePanel.POSITION_STEP;

    private SubtitleStyleControls()
    {
    }

    /** While a video is open (also in picture-in-picture) the player owns the style; Settings leaves it alone. */
    public static boolean playerActive()
    {
        Player player = Player.sPlayer;
        return player != null && player.getSubtitleEngine() != null;
    }

    /** A style manager outside the player (Settings): the saved style, no screen and no engine of its own. */
    public static SubtitleManager detached(Context context)
    {
        SubtitleManager manager = new SubtitleManager(context, null, null, false);
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        manager.restoreStyle(prefs);
        manager.restoreVerticalPosition(prefs);
        return manager;
    }

    public static int get(SubtitleManager m, String key)
    {
        switch (key) {
            case "size":
                return m.getSize();
            case "colour":
                return m.getColor();
            case "position":
                return m.getVerticalPosition();
            case "outline":
                return m.getOutlineState() ? 1 : 0;
            case "background":
                return m.getBackgroundState() ? 1 : 0;
            case "opacity":
                return m.getBackgroundOpacity();
            default:
                throw new IllegalArgumentException("Unknown subtitle style control");
        }
    }

    /** Changes one control and saves it exactly as the player always has. */
    public static void set(SubtitleManager m, SharedPreferences.Editor prefs, String key, int value)
    {
        switch (key) {
            case "size":
                m.setSize(value);
                prefs.putInt(PlayerActivity.KEY_SUBTITLE_SIZE, m.getSize())
                    .putInt(SubtitleManager.KEY_FONT_SIZE_PT, m.getFontSizePt());
                break;
            case "colour":
                m.setColor(value);
                prefs.putInt(PlayerActivity.KEY_SUBTITLE_COLOR, value);
                break;
            case "position":
                // The manager saves the percentage key itself; the old pixel key is left alone.
                m.setVerticalPosition(value);
                break;
            case "outline":
                m.setOutlineState(value != 0);
                prefs.putBoolean(PlayerActivity.KEY_SUBTITLE_OUTLINE, m.getOutlineState())
                    .putFloat(SubtitleManager.KEY_OUTLINE_WIDTH, m.getOutlineWidth());
                break;
            case "background":
                m.setBackgroundState(value != 0);
                prefs.putBoolean(PlayerActivity.KEY_SUBTITLE_BACKGROUND, m.getBackgroundState())
                    .putInt(SubtitleManager.KEY_BG_MODE, m.getBgMode());
                break;
            case "opacity":
                m.setBackgroundOpacity(value);
                prefs.putInt(PlayerActivity.KEY_SUBTITLE_BG_OPACITY, value);
                break;
            default:
                throw new IllegalArgumentException("Unknown subtitle style control");
        }
    }

    /** "Bottom", "Raised 30%", "Top". */
    public static String positionName(int percent)
    {
        return PlaybackSubtitlePanel.positionLabel(percent);
    }

    /** The next value along a list (wrapping), from the nearest current one. */
    public static int step(int[] values, int current, int direction)
    {
        int index = 0;
        int best = Integer.MAX_VALUE;
        for (int i = 0; i < values.length; i++) {
            int distance = Math.abs(values[i] - current);
            if (distance < best) {
                best = distance;
                index = i;
            }
        }
        int next = Math.floorMod(index + direction, values.length);
        return values[next];
    }

    public static String nameOf(int[] values, String[] names, int current, String otherwise)
    {
        for (int i = 0; i < values.length; i++)
            if (values[i] == current)
                return names[i];
        return otherwise;
    }
}
