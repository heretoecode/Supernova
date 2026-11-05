package com.archos.mediacenter.video.player;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import android.app.Activity;
import android.app.Application;
import android.view.KeyEvent;
import android.widget.TextView;
import android.view.View;
import android.view.ViewGroup;
import androidx.preference.PreferenceManager;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.android.controller.ActivityController;
import org.robolectric.annotation.Config;

/** Checks Supernova's first-play tip: shown once, three remote lines, gone at the first press without eating it. */
@RunWith(RobolectricTestRunner.class)
@Config(application = Application.class, sdk = 28)
public class PlayerFirstPlayTipTest
{
    @Test
    public void tipShowsOnceAndAnyPressDismissesItWithoutConsumingThePress()
    {
        ActivityController<Activity> host = Robolectric.buildActivity(Activity.class).setup();
        PlayerFirstPlayTip tip = new PlayerFirstPlayTip(host.get());
        try {
            tip.show();
            View root = host.get().findViewById(android.R.id.content);
            View card = root.findViewWithTag(PlayerFirstPlayTip.TAG);
            assertNotNull(card);
            assertTrue(text(card).contains("Seek"));
            assertTrue(text(card).contains("Play / pause"));
            assertTrue(text(card).contains("Play/pause and Settings buttons"));
            assertTrue(text(card).contains("Chapters and Episodes"));
            assertTrue(PlayerFirstPlayTip.alreadyShown(PreferenceManager.getDefaultSharedPreferences(host.get())));
            assertFalse(tip.handleKey(new KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_DPAD_LEFT)));
            assertFalse(tip.showing());
            assertNull(root.findViewWithTag(PlayerFirstPlayTip.TAG));
        } finally {
            tip.stop();
            host.pause().stop().destroy();
        }
    }

    private static String text(View view)
    {
        if (view instanceof TextView)
            return ((TextView)view).getText().toString() + "\n";
        StringBuilder all = new StringBuilder();
        if (view instanceof ViewGroup)
            for (int i = 0; i < ((ViewGroup)view).getChildCount(); i++)
                all.append(text(((ViewGroup)view).getChildAt(i)));
        return all.toString();
    }
}
