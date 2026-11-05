package com.archos.mediacenter.video.player;

import android.app.Activity;
import android.app.Application;
import android.os.Looper;
import android.os.SystemClock;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;
import com.archos.mediacenter.video.R;
import java.time.Duration;
import java.util.concurrent.atomic.AtomicInteger;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;
import org.robolectric.annotation.LooperMode;
import static org.junit.Assert.*;
import static org.robolectric.Shadows.shadowOf;

@RunWith(RobolectricTestRunner.class)
@Config(application = Application.class, sdk = 28)
@LooperMode(LooperMode.Mode.PAUSED)
public class AudioPickerRepeatTest {
    @Test
    public void cancelAndDetachStopBothDirectionsForBothPickers() {
        try (var controller = Robolectric.buildActivity(Activity.class).setup()) {
            Activity activity = controller.get();
            FrameLayout root = new FrameLayout(activity);
            activity.setContentView(root);
            AtomicInteger changes = new AtomicInteger();
            AudioDelayPicker delay = new AudioDelayPicker(activity);
            delay.init(0, (view, value) -> changes.incrementAndGet());
            AudioSpeedPicker speed = new AudioSpeedPicker(activity);
            speed.init(1f, (view, value) -> changes.incrementAndGet());
            for (View picker : new View[] {delay, speed}) {
                for (int id : new int[] {R.id.minus, R.id.plus}) {
                    root.addView(picker);
                    assertTrue(picker.isAttachedToWindow());
                    View button = picker.findViewById(id);
                    int before = changes.get();
                    touch(button, MotionEvent.ACTION_DOWN);
                    touch(button, MotionEvent.ACTION_CANCEL);
                    // Wait past both initial and subsequent repeat deadlines.
                    shadowOf(Looper.getMainLooper()).idleFor(Duration.ofSeconds(1));
                    assertEquals(before, changes.get());
                    // Prove the hold path really schedules repeats, then cancel it.
                    touch(button, MotionEvent.ACTION_DOWN);
                    shadowOf(Looper.getMainLooper()).idleFor(Duration.ofMillis(350));
                    assertTrue(changes.get() > before);
                    touch(button, MotionEvent.ACTION_CANCEL);
                    before = changes.get();
                    shadowOf(Looper.getMainLooper()).idleFor(Duration.ofSeconds(1));
                    assertEquals(before, changes.get());
                    touch(button, MotionEvent.ACTION_DOWN);
                    root.removeView(picker);
                    // Reattach: an old pending repeat must not revive here either.
                    root.addView(picker);
                    shadowOf(Looper.getMainLooper()).idleFor(Duration.ofSeconds(1));
                    assertEquals(before, changes.get());
                    root.removeView(picker);
                }
            }
        }
    }

    private static void touch(View view, int action) {
        long now = SystemClock.uptimeMillis();
        MotionEvent event = MotionEvent.obtain(now, now, action, 1f, 1f, 0);
        try {
            view.dispatchTouchEvent(event);
        } finally {
            event.recycle();
        }
    }
}
