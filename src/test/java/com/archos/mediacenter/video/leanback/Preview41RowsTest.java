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
            // Customise Home (0.158, home-v3/tv/07): Hide on Watch Next's focused row hides it; its titles stay.
            Dialog editor = ShadowDialog.getLatestDialog();
            View root = editor.getWindow().getDecorView();
            View watched = root.findViewWithTag("home.customise.builtin:watchnext");
            assertNotNull(watched);
            // A row only receives keys once it has a size and focus, as on the screen.
            root.measure(View.MeasureSpec.makeMeasureSpec(960, View.MeasureSpec.AT_MOST),
                         View.MeasureSpec.makeMeasureSpec(540, View.MeasureSpec.AT_MOST));
            root.layout(0, 0, root.getMeasuredWidth(), root.getMeasuredHeight());
            assertTrue(watched.requestFocus());
            watched.dispatchKeyEvent(new android.view.KeyEvent(android.view.KeyEvent.ACTION_DOWN,
                                                               android.view.KeyEvent.KEYCODE_DPAD_RIGHT));
            watched.performClick();
            int width = editor.getWindow().getAttributes().width;
            assertTrue(width <= TvLayout.safeWidth(c));
            editor.dismiss();
            rows = new PreviewHomeRows(c);
            PreviewHomeRows.Row next = rows.rows.stream().filter(r -> r.id.equals("watchnext")).findFirst().get();
            assertFalse(next.visible);
            assertTrue(next.members.contains(e.key()));
        } finally {
            host.pause().stop().destroy();
        }
    }
}
