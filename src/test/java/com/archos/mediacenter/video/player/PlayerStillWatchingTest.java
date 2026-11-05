package com.archos.mediacenter.video.player;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import android.app.Activity;
import android.app.Application;
import android.view.KeyEvent;
import android.view.View;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.android.controller.ActivityController;
import org.robolectric.annotation.Config;

/** Checks the "Still watching?" card: it follows the engine's question and answers with the remote. */
@RunWith(RobolectricTestRunner.class)
@Config(application = Application.class, sdk = 28)
public class PlayerStillWatchingTest
{
    private static final class Fixture implements PlayerStillWatching.Question
    {
        boolean pending;
        Boolean answer;

        @Override
        public boolean pending()
        {
            return pending;
        }

        @Override
        public void answer(boolean keepWatching)
        {
            answer = keepWatching;
            pending = false;
        }

        @Override
        public String title()
        {
            return "Dark Matter";
        }

        @Override
        public String next()
        {
            return "Season 2 · Episode 13";
        }

        @Override
        public int episodes()
        {
            return 3;
        }
    }

    private static boolean press(PlayerStillWatching card, int key)
    {
        boolean down = card.handleKey(new KeyEvent(KeyEvent.ACTION_DOWN, key));
        card.handleKey(new KeyEvent(KeyEvent.ACTION_UP, key));
        return down;
    }

    @Test
    public void cardAppearsWithKeepWatchingFocusedAndOkKeepsWatching()
    {
        ActivityController<Activity> host = Robolectric.buildActivity(Activity.class).setup();
        Fixture fixture = new Fixture();
        PlayerStillWatching card = new PlayerStillWatching(host.get(), fixture);
        try {
            View content = host.get().findViewById(android.R.id.content);
            card.check();
            assertNull(content.findViewWithTag(PlayerStillWatching.TAG));
            fixture.pending = true;
            card.check();
            View shown = content.findViewWithTag(PlayerStillWatching.TAG);
            assertNotNull(shown);
            String text = PlayerPartsStripTestText.of(shown);
            assertTrue(text.contains("Still watching Dark Matter?"));
            assertTrue(text.contains("You've watched 3 episodes in a row. Next: Season 2 · Episode 13"));
            assertTrue(text.contains("If nothing is pressed, playback stops in 60 seconds"));
            assertTrue(press(card, KeyEvent.KEYCODE_DPAD_CENTER));
            assertEquals(Boolean.TRUE, fixture.answer);
            assertFalse(card.showing());
        } finally {
            card.stop();
            host.pause().stop().destroy();
        }
    }

    @Test
    public void rightThenOkStopsAndTheCardGoesWhenTheEngineGivesUp()
    {
        ActivityController<Activity> host = Robolectric.buildActivity(Activity.class).setup();
        Fixture fixture = new Fixture();
        PlayerStillWatching card = new PlayerStillWatching(host.get(), fixture);
        try {
            fixture.pending = true;
            card.check();
            assertTrue(press(card, KeyEvent.KEYCODE_DPAD_RIGHT));
            assertTrue(press(card, KeyEvent.KEYCODE_DPAD_CENTER));
            assertEquals(Boolean.FALSE, fixture.answer);
            fixture.answer = null;
            fixture.pending = true;
            card.check();
            assertTrue(card.showing());
            fixture.pending = false;
            card.check();
            assertFalse(card.showing());
            assertNull(fixture.answer);
        } finally {
            card.stop();
            host.pause().stop().destroy();
        }
    }

    @Test
    public void sleepTimerNoteSaysWhenPlaybackStops()
    {
        assertEquals("", PlaybackSubtitlePanel.sleepNote(0, 0));
        assertEquals("Stops at the end of this episode", PlaybackSubtitlePanel.sleepNote(4, 0));
        assertTrue(PlaybackSubtitlePanel.sleepNote(1, System.currentTimeMillis() + 1800000).startsWith("Stops at "));
    }
}
