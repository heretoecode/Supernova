package com.archos.mediacenter.video.player;
import android.app.Activity;
import android.app.Application;
import android.view.KeyEvent;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.archos.mediacenter.utils.introdb.IntroSegments;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.android.controller.ActivityController;
import org.robolectric.annotation.Config;
import org.robolectric.util.ReflectionHelpers;

/** Checks Up Next protects unseen post-credit material and consumes remote releases without pausing playback. */
@RunWith(RobolectricTestRunner.class)
@Config(application = Application.class, sdk = 28)
public class PreviewUpNextTest {
    @Test
    public void backFromSkipPillRestoresOriginAndConsumesRelease() {
        ActivityController<Activity> host = Robolectric.buildActivity(Activity.class).setup();
        PreviewUpNext prompt = new PreviewUpNext(host.get());
        try {
            LinearLayout root = new LinearLayout(host.get());
            root.setOrientation(LinearLayout.VERTICAL);
            TextView origin = new TextView(host.get());
            origin.setFocusableInTouchMode(true);
            root.addView(origin);
            TextView pill = new TextView(host.get());
            pill.setFocusableInTouchMode(true);
            root.addView(pill);
            host.get().setContentView(root);
            ReflectionHelpers.setField(prompt, "skipPill", pill);
            ReflectionHelpers.setField(prompt, "skipOrigin", origin);
            assertTrue(pill.requestFocus());
            assertTrue(prompt.handleKey(
                    new KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_DPAD_UP), () -> {}));
            assertTrue(pill.hasFocus());
            assertTrue(prompt.handleKey(
                    new KeyEvent(KeyEvent.ACTION_UP, KeyEvent.KEYCODE_DPAD_UP), () -> {}));
            assertTrue(prompt.handleKey(
                    new KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_BACK), () -> {}));
            assertTrue(origin.hasFocus());
            assertTrue(prompt.handleKey(
                    new KeyEvent(KeyEvent.ACTION_UP, KeyEvent.KEYCODE_BACK), () -> {}));
            assertTrue(origin.hasFocus());
        } finally {
            prompt.stop();
            host.pause().stop().destroy();
        }
    }

    @Test
    public void creditsAndUnknownMarkersCannotAdvancePastUnseenPostCredits() {
        IntroSegments segments = new IntroSegments();
        segments.add(IntroSegments.Type.CREDITS, new IntroSegments.Segment(1700000L, 1800000L, 0, 0, "fixture"));
        assertEquals(1795000, PreviewUpNext.trigger(1800000, segments));
        assertEquals(1795000, PreviewUpNext.trigger(1800000, null));
    }

    @Test
    public void remoteSelectionConsumesReleaseWithoutPausingPlayback() {
        ActivityController<Activity> host =
            Robolectric.buildActivity(Activity.class).setup();
        PreviewUpNext prompt = new PreviewUpNext(host.get());
        try {
            LinearLayout root = new LinearLayout(host.get());
            root.setOrientation(LinearLayout.VERTICAL);
            TextView video = new TextView(host.get());
            video.setFocusableInTouchMode(true);
            root.addView(video);
            LinearLayout panel = new LinearLayout(host.get()),
                                        actions = new LinearLayout(host.get());
            panel.addView(actions);
            root.addView(panel);
            host.get().setContentView(root);
            int[] chosen = {
                0
            };
            for (int i = 0; i < 2; i++) {
                TextView b = new TextView(host.get());
                b.setFocusableInTouchMode(true);
                b.setOnClickListener(v -> {
                    chosen[0]++;
                    ReflectionHelpers.setField(prompt, "panel", null);
                });
                actions.addView(b);
            }
            video.requestFocus();
            ReflectionHelpers.setField(prompt, "panel", panel);
            for (int key : new int[] {
                KeyEvent.KEYCODE_DPAD_UP, KeyEvent.KEYCODE_DPAD_RIGHT,
                     KeyEvent.KEYCODE_DPAD_CENTER
            }) {
                assertTrue(
                    prompt.handleKey(new KeyEvent(KeyEvent.ACTION_DOWN, key), () -> {
                    }));
                assertTrue(prompt.handleKey(new KeyEvent(KeyEvent.ACTION_UP, key), () -> {
                }));
            }
            assertEquals(1, chosen[0]);
        } finally {
            prompt.stop();
            host.pause().stop().destroy();
        }
    }
}
