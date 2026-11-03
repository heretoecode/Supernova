package com.archos.mediacenter.video.leanback;

import android.app.*;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.os.Looper;
import android.view.*;
import android.widget.*;
import androidx.preference.PreferenceManager;
import androidx.test.platform.app.InstrumentationRegistry;
import java.time.Duration;
import org.junit.*;
import org.junit.runner.RunWith;
import org.robolectric.*;
import org.robolectric.annotation.*;
import java.util.*;
import static org.junit.Assert.*;

/** Capture the production Home child controls, including their focused viewport bounds. */
@RunWith(RobolectricTestRunner.class)
@Config(application = Application.class, sdk = 28, qualifiers = "w960dp-h540dp-land-mdpi")
@GraphicsMode(GraphicsMode.Mode.NATIVE)
public class PreviewHomeVisualTest {
    @org.junit.Before
    public void isolatePreviewTransport() {
        com.archos.mediacenter.video.leanback.PreviewAsyncFixtures.offlineTransport();
    }

    @org.junit.After
    public void drainPreviewWorkers() throws Exception {
        com.archos.mediacenter.video.leanback.PreviewAsyncFixtures.drain();
    }

    @Before
    public void remoteInputMode() {
        InstrumentationRegistry.getInstrumentation().setInTouchMode(false);
    }

    /** Approved global rule (2 Nov 2026): pop-up menus have no title; the match mode is still announced to TalkBack. */
    @Test
    public void genreMenuHasNoTitleAndChoicesPersistWithoutDone() throws Exception {
        var host = Robolectric.buildActivity(TopNavigationTest.Host.class).setup().visible();
        try {
            PreviewGenres.choose(host.get(), Arrays.asList(PreviewGenres.NAMES),
                                 new HashSet<>(Arrays.asList("Drama", "Science Fiction")), values -> {});
            Dialog dialog = org.robolectric.shadows.ShadowDialog.getLatestDialog();
            View root = measure(dialog);
            assertNull("no visible title", PreviewPagesTest.findText(root, "Genres · Match Any Selected"));
            assertNotNull("title kept for accessibility", findDescription(root, "Genres · Match Any Selected"));
            assertNull(PreviewPagesTest.findText(root, "Done"));
            View western = (View)PreviewPagesTest.findText(root, "Western").getParent();
            PreviewPagesTest.capture(root, "home-genres-initial");
            ScrollView scroll = findScroll(root);
            assertNotNull(scroll);
            assertTrue(scroll.getChildAt(0).getHeight() > scroll.getHeight());
            scroll.fullScroll(View.FOCUS_DOWN);
            western.requestFocus();
            Shadows.shadowOf(Looper.getMainLooper()).idle();
            root = measure(dialog);
            assertTrue(western.hasFocus());
            bounds(root, western);
            PreviewPagesTest.capture(root, "home-genres-footer-focused");
            dialog.dismiss();
        } finally {
            host.pause().stop().destroy();
        }
    }

    private static View findDescription(View v, String text) {
        if (v.getContentDescription() != null && v.getContentDescription().toString().contains(text))
            return v;
        if (v instanceof android.view.ViewGroup)
            for (int i = 0; i < ((android.view.ViewGroup)v).getChildCount(); i++) {
                View found = findDescription(((android.view.ViewGroup)v).getChildAt(i), text);
                if (found != null)
                    return found;
            }
        return null;
    }

    @Test
    public void deleteConfirmationKeepsBothButtonsInsideWindow() throws Exception {
        var host = Robolectric.buildActivity(TopNavigationTest.Host.class).setup().visible();
        try {
            PreferenceManager.getDefaultSharedPreferences(host.get())
                .edit()
                .putBoolean("try_new_ui", true)
                .commit();
            boolean[] deleted = {false};
            AlertDialog dialog = (AlertDialog)PreviewDialog.confirmDelete(
                host.get(), "Delete row?", "Titles remain in your library. Only this Home row is removed.",
                () -> deleted[0] = true);
            View root = measure(dialog);
            View cancel = dialog.getButton(AlertDialog.BUTTON_NEGATIVE),
                 confirm = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
            PreviewPagesTest.capture(root, "home-delete-initial");
            assertTrue("Cancel should initially own remote focus", cancel.hasFocus());
            bounds(root, cancel);
            bounds(root, confirm);
            confirm.requestFocus();
            assertTrue(confirm.hasFocus());
            PreviewPagesTest.capture(root, "home-delete-focused");
            cancel.performClick();
            Shadows.shadowOf(Looper.getMainLooper()).idle();
            assertFalse(deleted[0]);
            assertFalse(dialog.isShowing());
        } finally {
            host.pause().stop().destroy();
        }
    }

    static View measure(Dialog dialog) {
        // Match real attachment/onShow/layout turns; a single manual measure precedes window setup.
        View root = dialog.getWindow().getDecorView();
        for (int pass = 0; pass < 3; pass++) {
            Shadows.shadowOf(Looper.getMainLooper()).idleFor(Duration.ofMillis(60));
            int width = dialog.getWindow().getAttributes().width, height = dialog.getWindow().getAttributes().height;
            root.measure(
                View.MeasureSpec.makeMeasureSpec(width > 0 ? width : 912,
                                                 width > 0 ? View.MeasureSpec.EXACTLY : View.MeasureSpec.AT_MOST),
                View.MeasureSpec.makeMeasureSpec(height > 0 ? height : 492,
                                                 height > 0 ? View.MeasureSpec.EXACTLY : View.MeasureSpec.AT_MOST));
            root.layout(0, 0, root.getMeasuredWidth(), root.getMeasuredHeight());
        }
        return root;
    }

    private static Bitmap renderRegion(View root, View child) {
        Bitmap image = Bitmap.createBitmap(root.getWidth(), root.getHeight(),
                                                                             Bitmap.Config.ARGB_8888);
        root.draw(new Canvas(image));
        Rect area = new Rect(0, 0, child.getWidth(), child.getHeight());
        ((ViewGroup)root).offsetDescendantRectToMyCoords(child, area);
        return Bitmap.createBitmap(image, area.left, area.top, area.width(), area.height());
    }

    private static Rect bounds(View root, View child) {
        Rect result = new Rect();
        assertTrue("Control must be visible: " + child, child.getGlobalVisibleRect(result));
        assertEquals(child.getWidth(), result.width());
        assertEquals(child.getHeight(), result.height());
        Rect window = new Rect();
        assertTrue(root.getGlobalVisibleRect(window));
        assertTrue("Window " + window + " must contain control " + result, window.contains(result));
        return result;
    }

    private static ScrollView findScroll(View root) {
        if (root instanceof ScrollView)
            return (ScrollView)root;
        if (root instanceof ViewGroup)
            for (int i = 0; i < ((ViewGroup)root).getChildCount(); i++) {
                ScrollView result = findScroll(((ViewGroup)root).getChildAt(i));
                if (result != null)
                    return result;
            }
        return null;
    }
}
