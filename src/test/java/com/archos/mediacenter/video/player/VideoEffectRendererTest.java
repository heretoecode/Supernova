package com.archos.mediacenter.video.player;

import android.app.Application;
import java.util.concurrent.atomic.AtomicReference;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;
import static org.junit.Assert.*;

@RunWith(RobolectricTestRunner.class)
@Config(application = Application.class, sdk = 28)
public class VideoEffectRendererTest {
    @Test
    public void frameCallbackReturnsWhenPausedWithAFullWakeQueue() throws Exception {
        Player previous = Player.sPlayer;
        Player.sPlayer = null;
        VideoEffectRenderer renderer = new VideoEffectRenderer(null, VideoEffect.EFFECT_NONE);
        renderer.pause(); // No GL thread is started, so nothing can drain the latch.
        assertTrue(renderer.mSourceFrameAvailable.offer(Boolean.TRUE));
        AtomicReference<Throwable> failure = new AtomicReference<>();
        Thread callback = new Thread(() -> {
            try {
                renderer.onFrameAvailable(null);
            } catch (Throwable t) {
                failure.set(t);
            }
        });
        callback.setDaemon(true);
        try {
            callback.start();
            callback.join(1000);
            assertFalse("A late frame must not wait for the paused draw loop", callback.isAlive());
            assertNull(failure.get());
            assertEquals(1, renderer.mSourceFrameAvailable.size());
        } finally {
            callback.interrupt();
            callback.join(1000);
            renderer.stop();
            Player.sPlayer = previous;
        }
    }
}
