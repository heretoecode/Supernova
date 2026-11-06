package com.archos.mediacenter.video.player;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import androidx.preference.PreferenceManager;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;

/** Settings › Style and the player's Playback Settings save the subtitle style the same way. */
@RunWith(RobolectricTestRunner.class)
@Config(sdk = 33, application = Application.class)
public class SubtitleStyleControlsTest
{
    private Context context;
    private SharedPreferences prefs;

    @Before
    public void setUp()
    {
        context = RuntimeEnvironment.getApplication();
        prefs = PreferenceManager.getDefaultSharedPreferences(context);
        prefs.edit().clear().commit();
        Player.sPlayer = null;
    }

    @Test
    public void settingsChangesAreSavedWithThePlayersOwnKeys()
    {
        assertFalse(SubtitleStyleControls.playerActive());
        SubtitleManager style = SubtitleStyleControls.detached(context);
        SharedPreferences.Editor editor = prefs.edit();
        SubtitleStyleControls.set(style, editor, "size", 75);
        SubtitleStyleControls.set(style, editor, "colour", SubtitleStyleControls.COLOURS[1]);
        SubtitleStyleControls.set(style, editor, "outline", 1);
        SubtitleStyleControls.set(style, editor, "background", 1);
        SubtitleStyleControls.set(style, editor, "opacity", 128);
        editor.commit();
        SubtitleStyleControls.set(style, prefs.edit(), "position", 30);
        assertEquals(75, prefs.getInt(PlayerActivity.KEY_SUBTITLE_SIZE, -1));
        assertTrue(prefs.contains(SubtitleManager.KEY_FONT_SIZE_PT));
        assertEquals(SubtitleStyleControls.COLOURS[1], prefs.getInt(PlayerActivity.KEY_SUBTITLE_COLOR, 0));
        assertTrue(prefs.getBoolean(PlayerActivity.KEY_SUBTITLE_OUTLINE, false));
        assertTrue(prefs.getBoolean(PlayerActivity.KEY_SUBTITLE_BACKGROUND, false));
        assertEquals(128, prefs.getInt(PlayerActivity.KEY_SUBTITLE_BG_OPACITY, -1));
        assertEquals(30, prefs.getInt(SubtitleVerticalPosition.KEY_PERCENT, -1));
        SubtitleManager reopened = SubtitleStyleControls.detached(context);
        assertEquals(75, SubtitleStyleControls.get(reopened, "size"));
        assertEquals(SubtitleStyleControls.COLOURS[1], SubtitleStyleControls.get(reopened, "colour"));
        assertEquals(30, SubtitleStyleControls.get(reopened, "position"));
        assertEquals(1, SubtitleStyleControls.get(reopened, "outline"));
        assertEquals(1, SubtitleStyleControls.get(reopened, "background"));
    }

    @Test
    public void stepWrapsFromTheNearestValue()
    {
        int[] sizes = SubtitleStyleControls.SIZES;
        assertEquals(50, SubtitleStyleControls.step(sizes, 25, 1));
        assertEquals(25, SubtitleStyleControls.step(sizes, 75, 1));
        assertEquals(75, SubtitleStyleControls.step(sizes, 25, -1));
        assertEquals(75, SubtitleStyleControls.step(sizes, 60, 1));
        assertEquals("Large", SubtitleStyleControls.nameOf(sizes, SubtitleStyleControls.SIZE_NAMES, 75, "Custom"));
        assertEquals("Custom", SubtitleStyleControls.nameOf(sizes, SubtitleStyleControls.SIZE_NAMES, 40, "Custom"));
    }
}
