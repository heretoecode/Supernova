package com.archos.mediacenter.video.player;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertFalse;

import android.app.Application;
import android.view.KeyEvent;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

@RunWith(RobolectricTestRunner.class)
@Config(application = Application.class, sdk = 30)
public class BarPlayPauseKeysTest
{
    @Test
    public void okTogglesOnceAndConsumesRepeatsAndReleaseWithoutStealingArrows()
    {
        BarPlayPauseKeys keys = new BarPlayPauseKeys();
        int[] toggles = {0};
        assertTrue(keys.handle(new KeyEvent(0, 0, KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_DPAD_CENTER, 0),
            () -> toggles[0]++));
        assertTrue(keys.handle(new KeyEvent(0, 10, KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_DPAD_CENTER, 1),
            () -> toggles[0]++));
        assertTrue(keys.handle(new KeyEvent(KeyEvent.ACTION_UP, KeyEvent.KEYCODE_DPAD_CENTER), () -> toggles[0]++));
        assertEquals(1, toggles[0]);
        assertFalse(keys.handle(new KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_DPAD_RIGHT), () -> toggles[0]++));
    }
}
