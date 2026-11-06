package com.archos.mediacenter.video.leanback.filebrowsing;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import android.app.Activity;
import android.app.Application;
import android.os.Looper;
import android.view.KeyEvent;
import android.view.View;
import android.widget.LinearLayout;
import java.util.ArrayList;
import java.util.List;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.Shadows;
import org.robolectric.annotation.Config;

@RunWith(RobolectricTestRunner.class)
@Config(application = Application.class, sdk = 28, qualifiers = "w960dp-h540dp-land-mdpi")
public class ConnectionRowTest
{
    @Test
    public void focusedRowShowsItsButtonsAndTheRemoteMovesAlongThem()
    {
        Activity activity = Robolectric.buildActivity(Activity.class).setup().visible().get();
        List<String> pressed = new ArrayList<>();
        LinearLayout page = new LinearLayout(activity);
        page.setOrientation(LinearLayout.VERTICAL);
        ConnectionRow first = new ConnectionRow(activity, "Living-room NAS", "SMB · in your library",
                                                () -> pressed.add("open-row"));
        first.addButton("folder", "Open", () -> pressed.add("open"));
        first.addButton("edit", "Edit", () -> pressed.add("edit"));
        first.addButton("check", "Test", () -> pressed.add("test"));
        first.addButton("remove", "Remove", () -> pressed.add("remove"));
        ConnectionRow second = new ConnectionRow(activity, "Office PC", "SFTP", () -> pressed.add("second"));
        second.addButton("folder", "Open", () -> pressed.add("open-second"));
        page.addView(first);
        page.addView(second);
        activity.setContentView(page);
        assertTrue(first.requestFocus());
        Shadows.shadowOf(Looper.getMainLooper()).idle();
        assertEquals(View.VISIBLE, first.buttons.getVisibility());
        assertEquals(View.GONE, second.buttons.getVisibility());
        first.dispatchKeyEvent(new KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_DPAD_RIGHT));
        assertSame(first.buttons.getChildAt(0), first.findFocus());
        first.findFocus().dispatchKeyEvent(new KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_DPAD_RIGHT));
        assertSame(first.buttons.getChildAt(1), first.findFocus());
        first.findFocus().performClick();
        first.findFocus().dispatchKeyEvent(new KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_BACK));
        assertSame(first, first.findFocus());
        first.performClick();
        assertEquals(List.of("edit", "open-row"), pressed);
        assertTrue(second.requestFocus());
        Shadows.shadowOf(Looper.getMainLooper()).idle();
        assertEquals(View.GONE, first.buttons.getVisibility());
        assertEquals(View.VISIBLE, second.buttons.getVisibility());
    }
}
