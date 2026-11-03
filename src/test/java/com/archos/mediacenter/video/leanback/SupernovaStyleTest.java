package com.archos.mediacenter.video.leanback;

import android.app.Activity;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.android.controller.ActivityController;
import org.robolectric.annotation.Config;
import static org.junit.Assert.*;

/** The approved button rule: content is black on the white focus box and white at rest — never white on white. */
@RunWith(RobolectricTestRunner.class) @Config(sdk = 28, qualifiers = "w960dp-h540dp-land-xhdpi")
public class SupernovaStyleTest {
    private ActivityController<Activity> host;
    private FrameLayout root;

    @Before public void setUp() {
        host = Robolectric.buildActivity(Activity.class).setup().visible();
        root = new FrameLayout(host.get());
        host.get().setContentView(root);
    }

    @After public void tearDown() { host.pause().stop().destroy(); }

    /** Content added before styling (the usual order) must still follow the button's focus. */
    @Test public void containerContentFollowsFocusEvenWhenAddedBeforeStyling() {
        LinearLayout button = new LinearLayout(host.get());
        TextView label = new TextView(host.get());
        label.setText("Library");
        button.addView(label);
        TextView other = new TextView(host.get());
        other.setFocusable(true);
        root.addView(button);
        root.addView(other, new FrameLayout.LayoutParams(10, 10, android.view.Gravity.END));
        button.setFocusable(true);
        SupernovaStyle.applyButton(button, SupernovaStyle.Surface.STANDARD);

        assertTrue(button.requestFocus());
        assertEquals("black on the white box", SupernovaStyle.FOCUS_TEXT, label.getCurrentTextColor());
        assertTrue(other.requestFocus());
        assertEquals("white at rest", SupernovaStyle.TEXT, label.getCurrentTextColor());
    }

    /** HUD-style group: a focusable icon plus a plain label; the group shows the box. */
    @Test public void groupLabelTurnsBlackWhileTheGroupIsFocused() {
        FrameLayout group = new FrameLayout(host.get());
        group.setAddStatesFromChildren(true);
        ImageButton icon = new ImageButton(host.get());
        icon.setFocusable(true);
        TextView label = new TextView(host.get());
        label.setText("Settings");
        group.addView(icon);
        group.addView(label);
        TextView other = new TextView(host.get());
        other.setFocusable(true);
        root.addView(group);
        root.addView(other, new FrameLayout.LayoutParams(10, 10, android.view.Gravity.END));
        SupernovaStyle.applyButtonGroup(group, SupernovaStyle.Surface.STANDARD);

        assertTrue(icon.requestFocus());
        assertEquals(SupernovaStyle.FOCUS_TEXT, label.getCurrentTextColor());
        assertTrue(other.requestFocus());
        assertEquals(SupernovaStyle.TEXT, label.getCurrentTextColor());
    }

    @Test public void textButtonIsBlackOnFocus() {
        TextView button = TvButton.create(host.get(), "Resume", () -> {}, false);
        TextView other = new TextView(host.get());
        other.setFocusable(true);
        root.addView(button);
        root.addView(other, new FrameLayout.LayoutParams(10, 10, android.view.Gravity.END));
        assertTrue(button.requestFocus());
        assertEquals(SupernovaStyle.FOCUS_TEXT, button.getCurrentTextColor());
        assertTrue(other.requestFocus());
        assertEquals(SupernovaStyle.TEXT, button.getCurrentTextColor());
        assertEquals(View.VISIBLE, button.getVisibility());
    }
}
