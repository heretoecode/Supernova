package com.archos.mediacenter.video.leanback;

import android.app.Activity;
import android.app.Dialog;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Looper;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import java.io.File;
import java.io.FileOutputStream;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.Shadows;
import org.robolectric.android.controller.ActivityController;
import org.robolectric.annotation.Config;
import static org.junit.Assert.*;
import org.robolectric.annotation.GraphicsMode;

/** The approved button rule: content is black on the white focus box and white at rest — never white on white. */
@RunWith(RobolectricTestRunner.class)
@Config(sdk = 28, qualifiers = "w960dp-h540dp-land-xhdpi")
@GraphicsMode(GraphicsMode.Mode.NATIVE)
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
        root.addView(other, new FrameLayout.LayoutParams(10, 10, Gravity.END));
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
        root.addView(other, new FrameLayout.LayoutParams(10, 10, Gravity.END));
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
        root.addView(other, new FrameLayout.LayoutParams(10, 10, Gravity.END));
        assertTrue(button.requestFocus());
        assertEquals(SupernovaStyle.FOCUS_TEXT, button.getCurrentTextColor());
        assertTrue(other.requestFocus());
        assertEquals(SupernovaStyle.TEXT, button.getCurrentTextColor());
        assertEquals(View.VISIBLE, button.getVisibility());
    }

    /** 0.154: labels in Medium weight; the white focus box has no shadow; button icons turn black on focus. */
    @Test
    public void labelsAreMediumFocusBoxHasNoShadowAndIconsTurnBlack() {
        TextView button = TvButton.create(host.get(), "More Info", () -> {}, false);
        TextView other = new TextView(host.get());
        other.setFocusable(true);
        other.setFocusableInTouchMode(true);
        root.addView(button);
        root.addView(other, new FrameLayout.LayoutParams(10, 10, Gravity.END));
        assertSame(SupernovaStyle.LABEL, button.getTypeface());
        assertTrue(button.requestFocus());
        assertEquals(0, ((SupernovaStyle.Box)button.getBackground().getCurrent()).shadow());
        android.graphics.drawable.Drawable icon = button.getCompoundDrawables()[0];
        assertTrue(icon instanceof SupernovaStyle.StateIcon);
        assertEquals(SupernovaStyle.FOCUS_TEXT, ((SupernovaStyle.StateIcon)icon).colour());
        assertTrue(other.requestFocus());
        assertEquals(SupernovaStyle.TEXT, ((SupernovaStyle.StateIcon)icon).colour());
    }

    /** 0.154: landing tiles (Settings and Network & Files) — title, description and icon readable on focus. */
    @Test
    public void landingTileContentIsDarkOnTheWhiteBox() {
        LinearLayout tile = LandingTile.create(host.get(), "plus", "Add Connection",
                                               "Connect to a share using SMB, WebDAV, SFTP or FTP.", 1f);
        TextView other = new TextView(host.get());
        other.setFocusable(true);
        other.setFocusableInTouchMode(true);
        root.addView(tile, new FrameLayout.LayoutParams(400, 300));
        root.addView(other, new FrameLayout.LayoutParams(10, 10, Gravity.END));
        assertEquals(SupernovaStyle.GLASS, ((SupernovaStyle.Box)tile.getBackground().getCurrent()).fill());
        assertTrue(tile.requestFocus());
        LinearLayout heading = (LinearLayout)tile.getChildAt(0);
        android.widget.ImageView icon = (android.widget.ImageView)heading.getChildAt(0);
        TextView title = (TextView)heading.getChildAt(1);
        TextView description = (TextView)tile.getChildAt(1);
        assertEquals(SupernovaStyle.FOCUS_TEXT, title.getCurrentTextColor());
        assertEquals(SupernovaStyle.TEXT_SECONDARY_ON_FOCUS, description.getCurrentTextColor());
        assertTrue(icon.getDrawable() instanceof SupernovaStyle.StateIcon);
        assertEquals(SupernovaStyle.FOCUS_TEXT, ((SupernovaStyle.StateIcon)icon.getDrawable()).colour());
        assertSame(SupernovaStyle.LABEL, title.getTypeface());
    }

    /** A screen that sets a flat colour after styling must still not end up white on white. */
    @Test
    public void flatColourSetLaterIsMadeReadableAgain() {
        TextView button = TvButton.create(host.get(), "Delete", () -> {}, false);
        button.setTextColor(Color.WHITE);
        TextView other = new TextView(host.get());
        other.setFocusable(true);
        other.setFocusableInTouchMode(true);
        root.addView(button);
        root.addView(other, new FrameLayout.LayoutParams(10, 10, Gravity.END));
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
        Dialog dialog = PreviewDialog.choose(host.get(), "Actions", labels, 1, i -> {});
        Shadows.shadowOf(Looper.getMainLooper()).idle();
        View decor = dialog.getWindow().getDecorView();
        assertNull("no menu title", PreviewPagesTest.findText(decor, "Actions"));
        assertNotNull("group heading present", PreviewPagesTest.findText(decor, "Library"));
        WindowManager.LayoutParams size = dialog.getWindow().getAttributes();
        int width = size.width > 0 ? size.width : 600, height = size.height > 0 ? size.height : 700;
        int screen = root.getResources().getDisplayMetrics().widthPixels;
        assertTrue("menu fits its content (width " + width + ")",
                   width <= Math.round(screen * SupernovaStyle.MENU_MAX_SCREEN_FRACTION));
        decor.measure(View.MeasureSpec.makeMeasureSpec(width, View.MeasureSpec.EXACTLY),
                      View.MeasureSpec.makeMeasureSpec(height, View.MeasureSpec.EXACTLY));
        decor.layout(0, 0, width, height);
        Bitmap image =
            Bitmap.createBitmap(width + 120, height + 120, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(image);
        canvas.drawColor(0xFF2A3A55);
        Paint stripe = new Paint();
        stripe.setColor(0xFFE8C26A);
        stripe.setTextSize(40);
        for (int y = 60; y < height + 120; y += 70)
            canvas.drawText("Background text should not be readable", 10, y, stripe);
        canvas.translate(60, 60);
        decor.draw(canvas);
        File out = new File("build/reports/preview-ui/grouped-menu.png");
        out.getParentFile().mkdirs();
        try (FileOutputStream stream = new FileOutputStream(out)) {
            image.compress(Bitmap.CompressFormat.PNG, 100, stream);
        }
        dialog.dismiss();
    }

    /** Owner report 4 Nov 2026 (notes #37): an icon added after styling (Edit Page's Save Changes tick) vanished on focus. */
    @Test
    public void iconAddedAfterButtonStylingStillTurnsDarkOnFocus() {
        TextView save = new TextView(host.get());
        save.setText("Save Changes");
        save.setFocusable(true);
        save.setFocusableInTouchMode(true);
        SupernovaStyle.applyButton(save, SupernovaStyle.Surface.PANEL);
        PreviewIcon.apply(save, "check", 18);
        root.addView(save);
        android.graphics.drawable.Drawable icon = save.getCompoundDrawables()[0];
        assertTrue("the tick follows focus", icon instanceof SupernovaStyle.StateIcon);
        assertTrue(save.requestFocus());
        icon.setState(save.getDrawableState());
        assertEquals("dark on the white focus box", SupernovaStyle.FOCUS_TEXT,
                     ((SupernovaStyle.StateIcon)icon).colour());

        TextView plain = new TextView(host.get());
        plain.setTextColor(Color.WHITE);
        PreviewIcon.apply(plain, "check", 18);
        assertTrue("plain labels keep their own icon colour", plain.getCompoundDrawables()[0] instanceof PreviewIcon);
    }
}
