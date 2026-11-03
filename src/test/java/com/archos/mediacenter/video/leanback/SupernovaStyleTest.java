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
@RunWith(RobolectricTestRunner.class)
@Config(sdk = 28, qualifiers = "w960dp-h540dp-land-xhdpi")
@org.robolectric.annotation.GraphicsMode(org.robolectric.annotation.GraphicsMode.Mode.NATIVE)
public class SupernovaStyleTest {
    private ActivityController<Activity> host;
    private FrameLayout root;

    @Before
    public void setUp() {
        host = Robolectric.buildActivity(Activity.class).setup().visible();
        root = new FrameLayout(host.get());
        host.get().setContentView(root);
    }

    @After
    public void tearDown() {
        host.pause().stop().destroy();
    }

    /** Content added before styling (the usual order) must still follow the button's focus. */
    @Test
    public void containerContentFollowsFocusEvenWhenAddedBeforeStyling() {
        LinearLayout button = new LinearLayout(host.get());
        TextView label = new TextView(host.get());
        label.setText("Library");
        button.addView(label);
        TextView other = new TextView(host.get());
        other.setFocusable(true);
        other.setFocusableInTouchMode(true);
        root.addView(button);
        root.addView(other, new FrameLayout.LayoutParams(10, 10, android.view.Gravity.END));
        button.setFocusable(true);
        button.setFocusableInTouchMode(true);
        SupernovaStyle.applyButton(button, SupernovaStyle.Surface.STANDARD);

        assertTrue(button.requestFocus());
        assertEquals("black on the white box", SupernovaStyle.FOCUS_TEXT, label.getCurrentTextColor());
        assertTrue(other.requestFocus());
        assertEquals("white at rest", SupernovaStyle.TEXT, label.getCurrentTextColor());
    }

    /** HUD-style group: a focusable icon plus a plain label; the group shows the box. */
    @Test
    public void groupLabelTurnsBlackWhileTheGroupIsFocused() {
        FrameLayout group = new FrameLayout(host.get());
        group.setAddStatesFromChildren(true);
        ImageButton icon = new ImageButton(host.get());
        icon.setFocusable(true);
        icon.setFocusableInTouchMode(true);
        TextView label = new TextView(host.get());
        label.setText("Settings");
        group.addView(icon);
        group.addView(label);
        TextView other = new TextView(host.get());
        other.setFocusable(true);
        other.setFocusableInTouchMode(true);
        root.addView(group);
        root.addView(other, new FrameLayout.LayoutParams(10, 10, android.view.Gravity.END));
        SupernovaStyle.applyButtonGroup(group, SupernovaStyle.Surface.STANDARD);

        assertTrue(icon.requestFocus());
        assertEquals(SupernovaStyle.FOCUS_TEXT, label.getCurrentTextColor());
        assertTrue(other.requestFocus());
        assertEquals(SupernovaStyle.TEXT, label.getCurrentTextColor());
    }

    @Test
    public void textButtonIsBlackOnFocus() {
        TextView button = TvButton.create(host.get(), "Resume", () -> {}, false);
        TextView other = new TextView(host.get());
        other.setFocusable(true);
        other.setFocusableInTouchMode(true);
        root.addView(button);
        root.addView(other, new FrameLayout.LayoutParams(10, 10, android.view.Gravity.END));
        assertTrue(button.requestFocus());
        assertEquals(SupernovaStyle.FOCUS_TEXT, button.getCurrentTextColor());
        assertTrue(other.requestFocus());
        assertEquals(SupernovaStyle.TEXT, button.getCurrentTextColor());
        assertEquals(View.VISIBLE, button.getVisibility());
    }

    /** A screen that sets a flat colour after styling must still not end up white on white. */
    @Test
    public void flatColourSetLaterIsMadeReadableAgain() {
        TextView button = TvButton.create(host.get(), "Delete", () -> {}, false);
        button.setTextColor(android.graphics.Color.WHITE);
        TextView other = new TextView(host.get());
        other.setFocusable(true);
        other.setFocusableInTouchMode(true);
        root.addView(button);
        root.addView(other, new FrameLayout.LayoutParams(10, 10, android.view.Gravity.END));
        assertTrue(button.requestFocus());
        assertEquals(SupernovaStyle.FOCUS_TEXT, button.getCurrentTextColor());
        button.setTextColor(SupernovaStyle.destructiveContent());
        assertEquals("destructive stays red but dark on the white box", SupernovaStyle.DESTRUCTIVE_ON_FOCUS,
                     button.getCurrentTextColor());
    }

    /** Render of a grouped pop-up menu (the details More menu's shape) for comparison with the approved mock-up. */
    @Test
    public void groupedMenuRender() throws Exception {
        String[] labels = {"— Playback", "Restart Movie",          "— Library",           "Mark as Watched",
                           "Add to Row", "Report Incorrect Match", "Remove From Library", "— Files & Media",
                           "Delete",     "Subtitles and Artwork"};
        android.app.Dialog dialog = PreviewDialog.choose(host.get(), "Actions", labels, 1, i -> {});
        org.robolectric.Shadows.shadowOf(android.os.Looper.getMainLooper()).idle();
        View decor = dialog.getWindow().getDecorView();
        assertNull("no menu title", PreviewPagesTest.findText(decor, "Actions"));
        assertNotNull("group heading present", PreviewPagesTest.findText(decor, "Library"));
        android.view.WindowManager.LayoutParams size = dialog.getWindow().getAttributes();
        int width = size.width > 0 ? size.width : 600, height = size.height > 0 ? size.height : 700;
        int screen = root.getResources().getDisplayMetrics().widthPixels;
        assertTrue("menu fits its content (width " + width + ")",
                   width <= Math.round(screen * SupernovaStyle.MENU_MAX_SCREEN_FRACTION));
        decor.measure(View.MeasureSpec.makeMeasureSpec(width, View.MeasureSpec.EXACTLY),
                      View.MeasureSpec.makeMeasureSpec(height, View.MeasureSpec.EXACTLY));
        decor.layout(0, 0, width, height);
        android.graphics.Bitmap image =
            android.graphics.Bitmap.createBitmap(width + 120, height + 120, android.graphics.Bitmap.Config.ARGB_8888);
        android.graphics.Canvas canvas = new android.graphics.Canvas(image);
        canvas.drawColor(0xFF2A3A55);
        android.graphics.Paint stripe = new android.graphics.Paint();
        stripe.setColor(0xFFE8C26A);
        stripe.setTextSize(40);
        for (int y = 60; y < height + 120; y += 70)
            canvas.drawText("Background text should not be readable", 10, y, stripe);
        canvas.translate(60, 60);
        decor.draw(canvas);
        java.io.File out = new java.io.File("build/reports/preview-ui/grouped-menu.png");
        out.getParentFile().mkdirs();
        try (java.io.FileOutputStream stream = new java.io.FileOutputStream(out)) {
            image.compress(android.graphics.Bitmap.CompressFormat.PNG, 100, stream);
        }
        dialog.dismiss();
    }
}
