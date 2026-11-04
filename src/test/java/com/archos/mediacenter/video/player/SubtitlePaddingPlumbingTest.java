package com.archos.mediacenter.video.player;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.widget.FrameLayout;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;

import org.robolectric.annotation.Config;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.CALLS_REAL_METHODS;
import static org.mockito.Mockito.clearInvocations;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

/** 73df6e4d plumbing only: old preferences retain their existing box spacing. */
@RunWith(RobolectricTestRunner.class)
@Config(application = Application.class, sdk = 33)
public class SubtitlePaddingPlumbingTest {
    private Player previous;
    private SubtitleManager manager;
    private SharedPreferences prefs;
    private SubtitleEngine engine;

    @Before
    public void setUp() {
        previous = Player.sPlayer;
        Player.sPlayer = null;
        Context context = RuntimeEnvironment.getApplication();
        manager = new SubtitleManager(context, new FrameLayout(context), null, false);
        prefs = context.getSharedPreferences("padding-plumbing", Context.MODE_PRIVATE);
        prefs.edit().clear().commit();
        engine = mock(SubtitleEngine.class);
        Player player = mock(Player.class);
        when(player.getSubtitleEngine()).thenReturn(engine);
        Player.sPlayer = player;
    }

    @After
    public void restorePlayer() {
        Player.sPlayer = previous;
    }

    @Test
    public void absentPreferenceKeepsFloatingDefaultsAndDoesNotRewritePreferences() {
        Map<String, ?> before = new HashMap<>(prefs.getAll());
        manager.restoreStyle(prefs);
        assertEquals(2f, manager.getDefaults().padding, 0f);
        assertEquals(2f, manager.getPadding(), 0f);
        assertEquals(before, prefs.getAll());
        assertFalse(prefs.contains(SubtitleManager.KEY_PADDING));
        assertTrue(SubtitleManager.STYLE_KEYS.contains(SubtitleManager.KEY_PADDING));
        assertEquals("pref_play_subtitle_padding_key", SubtitleManager.KEY_PADDING);
    }

    @Test
    public void absentKeyReproducesLegacyLineAndBlockModesEvenAfterWidthEdits() {
        prefs.edit().putInt(SubtitleManager.KEY_BG_MODE, SubtitleManager.BG_MODE_BOXED_LINE)
                .putFloat(SubtitleManager.KEY_OUTLINE_WIDTH, 7f)
                .putFloat(SubtitleManager.KEY_SHADOW_WIDTH, 3f).commit();
        Map<String, ?> before = new HashMap<>(prefs.getAll());
        manager.restoreStyle(prefs);
        assertEquals(7f, manager.getPadding(), 0f);
        manager.setBgMode(SubtitleManager.BG_MODE_BOXED_BLOCK);
        assertEquals(3f, manager.getPadding(), 0f);
        manager.setShadowWidth(6f);
        assertEquals(6f, manager.getPadding(), 0f);
        manager.setBgMode(SubtitleManager.BG_MODE_BOXED_LINE);
        manager.setOutlineWidth(9f);
        assertEquals(9f, manager.getPadding(), 0f);
        assertEquals(before, prefs.getAll());
    }

    @Test
    public void savedLegacyZeroIsNotClampedToTheUpstreamSettingsMinimum() {
        prefs.edit().putInt(SubtitleManager.KEY_BG_MODE, SubtitleManager.BG_MODE_BOXED_LINE)
                .putFloat(SubtitleManager.KEY_OUTLINE_WIDTH, 0f).commit();
        manager.restoreStyle(prefs);
        assertEquals(0f, manager.getPadding(), 0f);
        clearInvocations(engine);
        manager.setBgMode(SubtitleManager.BG_MODE_BOXED_LINE);
        verify(engine).setPadding(0f);
    }

    @Test
    public void explicitPaddingRemainsIndependentAcrossWidthsAndModes() {
        prefs.edit().putFloat(SubtitleManager.KEY_PADDING, 4f)
                .putFloat(SubtitleManager.KEY_OUTLINE_WIDTH, 1f)
                .putFloat(SubtitleManager.KEY_SHADOW_WIDTH, 10f).commit();
        manager.restoreStyle(prefs);
        manager.setBgMode(SubtitleManager.BG_MODE_BOXED_LINE);
        manager.setOutlineWidth(8f);
        manager.setBgMode(SubtitleManager.BG_MODE_BOXED_BLOCK);
        manager.setShadowWidth(12f);
        assertEquals(4f, manager.getPadding(), 0f);
        clearInvocations(engine);
        manager.setPadding(5f);
        assertEquals(8f, manager.getOutlineWidth(), 0f);
        assertEquals(12f, manager.getShadowWidth(), 0f);
        verify(engine).setPadding(5f);
        verifyNoMoreInteractions(engine);
    }

    @Test
    public void styleResetRemovesPaddingAndRestoresCompatibilityWithoutDeletingOtherSettings() {
        prefs.edit().putFloat(SubtitleManager.KEY_PADDING, 8f).putString("unrelated", "keep").commit();
        manager.restoreStyle(prefs);
        assertEquals(8f, manager.getPadding(), 0f);
        SharedPreferences.Editor edit = prefs.edit();
        for (String key : SubtitleManager.STYLE_KEYS) edit.remove(key);
        edit.commit();
        manager.restoreStyle(prefs);
        assertEquals(2f, manager.getPadding(), 0f);
        assertFalse(prefs.contains(SubtitleManager.KEY_PADDING));
        assertEquals("keep", prefs.getString("unrelated", null));
        manager.setBgMode(SubtitleManager.BG_MODE_BOXED_LINE);
        assertEquals(manager.getOutlineWidth(), manager.getPadding(), 0f);
    }

    @Test
    public void engineRecreationReplaysTheSavedPadding() {
        prefs.edit().putFloat(SubtitleManager.KEY_PADDING, 6f)
                .putInt(SubtitleManager.KEY_BG_MODE, SubtitleManager.BG_MODE_BOXED_BLOCK).commit();
        manager.restoreStyle(prefs);
        clearInvocations(engine);
        SubtitleEngine replacement = mock(SubtitleEngine.class);
        Player newPlayer = mock(Player.class);
        when(newPlayer.getSubtitleEngine()).thenReturn(replacement);
        Player.sPlayer = newPlayer;
        manager.restoreStyle(prefs);
        verify(replacement).setBackgroundMode(SubtitleManager.BG_MODE_BOXED_BLOCK);
        // Several style setters may replay the same independent value during restoration.
        assertEquals(6f, manager.getPadding(), 0f);
        verify(replacement, org.mockito.Mockito.atLeastOnce()).setPadding(6f);
        verifyNoInteractions(engine);
    }

    @Test
    public void nonFiniteAndOutOfNativeIntegerRangeCannotReachJni() {
        for (float value : new float[] {Float.NaN, Float.POSITIVE_INFINITY, Float.MAX_VALUE, -1f}) {
            manager.setPadding(value);
            assertEquals(2f, manager.getPadding(), 0f);
        }
    }

    @Test
    public void closedEngineSkipsJniAndTheNewNativeSignatureMatches() throws Exception {
        SubtitleEngine closed = mock(SubtitleEngine.class, CALLS_REAL_METHODS);
        closed.setPadding(3f); // zero handle: must not invoke any native method
        Method method = SubtitleEngine.class.getDeclaredMethod("nativeSetPadding", long.class, float.class);
        assertTrue(Modifier.isNative(method.getModifiers()));
        assertEquals(void.class, method.getReturnType());
    }
}
