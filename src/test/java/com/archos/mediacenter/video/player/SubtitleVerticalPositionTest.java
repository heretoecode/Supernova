package com.archos.mediacenter.video.player;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

import android.app.Application;
import android.content.SharedPreferences;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;

@RunWith(RobolectricTestRunner.class)
@Config(application = Application.class, sdk = 30)
public class SubtitleVerticalPositionTest
{
    @Test
    public void everyLegacyPositionKeepsItsExactPixelsAcrossMigrationAndRestart()
    {
        SharedPreferences prefs = RuntimeEnvironment.getApplication().getSharedPreferences("vpos-test", 0);
        for (int legacy = 0; legacy <= 255; legacy++)
        {
            prefs.edit().clear().putInt(SubtitleManager.KEY_VPOS, legacy).commit();
            assertEquals(1080 * legacy / 765 + 1,
                SubtitleVerticalPosition.restoredPixels(prefs, SubtitleManager.KEY_VPOS, 1080));
            assertEquals(720 * legacy / 765 + 1,
                SubtitleVerticalPosition.restoredPixels(prefs, SubtitleManager.KEY_VPOS, 720));
        }
        SubtitleVerticalPosition.save(prefs, 90);
        assertFalse(prefs.contains(SubtitleVerticalPosition.KEY_LEGACY));
        assertEquals(972, SubtitleVerticalPosition.restoredPixels(prefs, SubtitleManager.KEY_VPOS, 1080));
        prefs.edit().clear().commit();
        assertEquals(5, SubtitleVerticalPosition.percent(prefs, SubtitleManager.KEY_VPOS));
        assertEquals(0, SubtitleVerticalPosition.step(-1));
        assertEquals(90, SubtitleVerticalPosition.step(255));
    }
}
