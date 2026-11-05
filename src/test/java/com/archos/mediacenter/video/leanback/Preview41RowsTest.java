package com.archos.mediacenter.video.leanback;
import android.app.*;
import android.content.Context;
import android.view.*;
import androidx.preference.PreferenceManager;
import org.junit.*;
import org.junit.After;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.robolectric.*;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.android.controller.ActivityController;
import org.robolectric.annotation.*;
import static org.junit.Assert.*;
import org.robolectric.shadows.ShadowDialog;

/** Checks that Watch Next membership survives hiding and reordering home rows. */
@RunWith(RobolectricTestRunner.class)
@Config(application = Application.class, sdk = 28, qualifiers = "w960dp-h540dp-land-mdpi")
public class Preview41RowsTest {
    @Before
    public void isolatePreviewTransport() {
        PreviewAsyncFixtures.offlineTransport();
    }

    @After
    public void drainPreviewWorkers() throws Exception {
        PreviewAsyncFixtures.drain();
    }

    @Before
    public void configuredLibraryFixture() {
        PreferenceManager
            .getDefaultSharedPreferences(RuntimeEnvironment.getApplication())
            .edit()
            .putBoolean("supernova_onboarding_complete", true)
            .commit();
    }

    @Test
    @GraphicsMode(GraphicsMode.Mode.NATIVE)
    public void watchNextMembershipSurvivesHidingAndReorder() throws Exception {
        ActivityController<TopNavigationTest.Host> host =
            Robolectric.buildActivity(TopNavigationTest.Host.class).setup();
        try {
            Context c = host.get();
            PreferenceManager.getDefaultSharedPreferences(c)
                .edit()
                .clear()
                .putBoolean("supernova_onboarding_complete", true)
                .commit();
            PreviewLibraryLoader.Entry e = new PreviewPagesTest().episode(1, 0, false, 0, 0);
            PreviewHomeRows.add(c, e, () -> {
            });
            Dialog chooser = ShadowDialog.getLatestDialog();
            View label = PreviewPagesTest.findText(chooser.getWindow().getDecorView(), "Watch Next");
            assertNotNull(label);
            ((View) label.getParent()).performClick();
            assertTrue("Membership toggle remains open", chooser.isShowing());
            chooser.dismiss();
            PreviewHomeRows rows = new PreviewHomeRows(c);
            assertTrue(
                rows.rows.stream().filter(r -> r.id.equals("watchnext")).findFirst().get().members.contains(e.key()));
            PreviewHomeRows.customise(c, () -> {
            });
            Dialog editor = ShadowDialog.getLatestDialog();
            View root = editor.getWindow().getDecorView();
            View watched = root.findViewWithTag("watchnext");
            assertNotNull(watched);
            watched.performClick();
            watched.dispatchKeyEvent(new KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_DPAD_UP));
            root.findViewWithTag("watchnext").performClick();
            root.findViewWithTag("watchnext:visibility").performClick();
            int width = editor.getWindow().getAttributes().width, height = editor.getWindow().getAttributes().height;
            assertTrue(width <= TvLayout.safeWidth(c));
            assertTrue(height <= TvLayout.safeHeight(c));
            root.measure(View.MeasureSpec.makeMeasureSpec(width, View.MeasureSpec.EXACTLY),
                View.MeasureSpec.makeMeasureSpec(height, View.MeasureSpec.EXACTLY));
            root.layout(0, 0, width, height);
            PreviewPagesTest.capture(root, "customise-home");
            editor.dismiss();
            rows = new PreviewHomeRows(c);
            assertEquals("watchnext", rows.rows.get(0).id);
            assertFalse(rows.rows.get(0).visible);
            assertTrue(rows.rows.get(0).members.contains(e.key()));
        } finally {
            host.pause().stop().destroy();
        }
    }
}
