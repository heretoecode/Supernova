package com.archos.mediacenter.video.player;

import android.app.Application;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.os.Looper;
import android.view.Surface;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.Shadows;
import org.robolectric.annotation.Config;
import org.robolectric.annotation.Implementation;
import org.robolectric.annotation.Implements;
import org.robolectric.util.ReflectionHelpers;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;
import static org.mockito.ArgumentMatchers.isNull;

@RunWith(RobolectricTestRunner.class)
@Config(application = Application.class, sdk = 28,
        shadows = SubtitleEngineTest.NativeEngine.class,
        instrumentedPackages = "com.archos.mediacenter.video.player")
public class SubtitleEngineTest {
    // Substitute only the JNI boundary: the real Java pull, post and wake path runs.
    @Implements(value = SubtitleEngine.class, isInAndroidSdk = false)
    public static class NativeEngine {
        @Implementation protected long nativeCreate() { return 1; }
        @Implementation protected void nativeDestroy(long handle) {}
        @Implementation protected void nativeSurfaceChanged(long handle, int width, int height) {}
        @Implementation protected void nativeSurfaceDestroyed(long handle) {}
        @Implementation protected void nativeSetUIMode(long handle, int mode) {}
        @Implementation protected int nativeFillBitmap(long handle, Bitmap bitmap,
                long lastGeneration, boolean sync, long[] generation) {
            assertFalse("Content notifications use a passive pull", sync);
            generation[0] = 7;
            return 1; // FILL_CLEAR still posts a fresh overlay buffer.
        }
    }

    @Test
    public void pausedContentPullPostsBeforeWakingThePrimedRenderer() throws Exception {
        Player previous = Player.sPlayer;
        Player player = mock(Player.class);
        VideoEffectRenderer renderer = new VideoEffectRenderer(null, VideoEffect.EFFECT_NONE);
        renderer.pause();
        when(player.getEffectRenderer()).thenReturn(renderer);
        Player.sPlayer = player;
        SubtitleEngine engine = new SubtitleEngine();
        Surface surface = mock(Surface.class);
        Canvas canvas = mock(Canvas.class);
        when(surface.isValid()).thenReturn(true);
        when(surface.lockCanvas(isNull())).thenReturn(canvas);
        doAnswer(call -> {
            assertTrue("The new overlay must be posted before the wake", renderer.mSourceFrameAvailable.isEmpty());
            return null;
        }).when(surface).unlockCanvasAndPost(canvas);
        try {
            engine.setUIMode(VideoEffect.SBS_MODE);
            engine.primeThreeDSurface(surface, 64, 32);
            ReflectionHelpers.callInstanceMethod(engine, "onNativeSubtitleContentChanged");
            Shadows.shadowOf(Looper.getMainLooper()).idle();
            verify(surface).unlockCanvasAndPost(canvas);
            assertEquals(Boolean.TRUE, renderer.mSourceFrameAvailable.poll());

            Player.sPlayer = null; // Missing player/renderer must be harmless.
            ReflectionHelpers.callInstanceMethod(engine, "pull3DSubtitles");
            Player.sPlayer = player;
            when(player.getEffectRenderer()).thenReturn(null);
            ReflectionHelpers.callInstanceMethod(engine, "pull3DSubtitles");
            when(player.getEffectRenderer()).thenReturn(renderer);
            engine.release();
            ReflectionHelpers.callInstanceMethod(engine, "pull3DSubtitles");
            assertTrue("A released engine must not queue a wake", renderer.mSourceFrameAvailable.isEmpty());
        } finally {
            engine.release();
            renderer.stop();
            Player.sPlayer = previous;
        }
    }
}
