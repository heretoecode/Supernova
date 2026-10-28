package com.archos.mediacenter.video.player;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.DisplayMetrics;
import android.widget.FrameLayout;
import org.junit.*;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;
import static org.junit.Assert.*;

/** Native seek_pause_feed/style_resize_paused tests replace the retired Java cue
 * worker timing tests. These cases preserve its user-visible settings contract. */
@RunWith(RobolectricTestRunner.class)
@Config(sdk=30, application=android.app.Application.class)
public class SubtitleLegacyPreferencesTest {
    private SubtitleManager manager;
    private SharedPreferences prefs;
    @Before public void setup() {
        Player.sPlayer=null;
        Context context=RuntimeEnvironment.getApplication();
        manager=new SubtitleManager(context,new FrameLayout(context),null,true);
        prefs=context.getSharedPreferences("legacy_subtitles",0);prefs.edit().clear().commit();
    }
    @Test public void saved145PreferencesRestoreWithoutChangingRollbackKeys() {
        prefs.edit().putInt(PlayerActivity.KEY_SUBTITLE_SIZE,73).putBoolean(PlayerActivity.KEY_SUBTITLE_OUTLINE,true).putBoolean(PlayerActivity.KEY_SUBTITLE_BACKGROUND,true).putInt(SubtitleManager.KEY_BG_OPACITY,99).commit();
        java.util.Map<String,?> before=prefs.getAll();
        manager.restoreStyle(prefs);
        assertEquals(73,manager.getSize());assertTrue(manager.getOutlineState());assertTrue(manager.getBackgroundState());assertEquals(99,manager.getBackgroundOpacity());assertEquals(before,prefs.getAll());
    }
    @Test public void missing145OutlineAndBackgroundRemainOff() {
        manager.restoreStyle(prefs);assertFalse(manager.getOutlineState());assertFalse(manager.getBackgroundState());
    }
    @Test public void legacySizeOverridesStaleCandidatePointSize() {
        prefs.edit().putInt(PlayerActivity.KEY_SUBTITLE_SIZE,80).putInt(SubtitleManager.KEY_FONT_SIZE_PT,12).commit();manager.restoreStyle(prefs);assertEquals(80,manager.getSize());
        DisplayMetrics metrics=RuntimeEnvironment.getApplication().getResources().getDisplayMetrics();
        assertEquals(SubtitleManager.legacySizeToNativePt(80,metrics,0),manager.getFontSizePt());
    }
    @Test public void legacySpSizeSurvivesDensityResolutionAndOrientation() {
        for(float density:new float[]{1f,1.5f,2f,3f}) for(int shortSide:new int[]{480,720,1080,2160}) for(int value:new int[]{0,25,50,75,100}) {
            DisplayMetrics metrics=new DisplayMetrics();metrics.density=density;metrics.scaledDensity=density*1.25f;metrics.widthPixels=shortSide*16/9;metrics.heightPixels=shortSide;
            int pt=SubtitleManager.legacySizeToNativePt(value,metrics,shortSide);
            float expected=android.util.TypedValue.applyDimension(android.util.TypedValue.COMPLEX_UNIT_SP,SubtitleManager.calcTextSize(value),metrics);
            assertEquals(expected,pt*shortSide/720f,shortSide/1440f+0.01f);
            int oldWidth=metrics.widthPixels;metrics.widthPixels=metrics.heightPixels;metrics.heightPixels=oldWidth;
            assertEquals(pt,SubtitleManager.legacySizeToNativePt(value,metrics,shortSide));
        }
    }
    @Test public void resizeRecomputesPointsToPreserveLegacyPhysicalTextSize() {
        manager.setSize(64);manager.setScreenSize(1280,720);int smaller=manager.getFontSizePt();manager.setScreenSize(3840,2160);int larger=manager.getFontSizePt();assertEquals(64,manager.getSize());assertEquals(smaller/3f,larger,1f);
    }
    @Test public void graphicTracksKeepSavedSizeAndPositionWithoutRestylingBitmap() {
        manager.setSize(60);manager.setVerticalPosition(35);manager.setSubtitleKind(SubtitleManager.KIND_GRAPHIC);assertFalse(manager.supportsUserStyle());assertEquals(60,manager.getSize());assertEquals(35,manager.getVerticalPosition());manager.setSubtitleKind(SubtitleManager.KIND_PLAIN_TEXT);assertTrue(manager.supportsUserStyle());assertEquals(60,manager.getSize());assertEquals(35,manager.getVerticalPosition());
    }
}
