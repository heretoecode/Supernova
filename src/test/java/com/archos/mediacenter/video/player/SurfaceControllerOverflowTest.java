package com.archos.mediacenter.video.player;

import android.app.Application;
import android.content.Context;
import android.view.Gravity;
import android.view.SurfaceView;
import android.view.TextureView;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.archos.medialib.IMediaPlayer;
import com.archos.mediacenter.video.R;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;
import org.robolectric.util.ReflectionHelpers;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.mockito.Mockito.mock;

/** Faithful 8bbfb666 geometry through the actual updateSurface path, without JNI. */
@RunWith(RobolectricTestRunner.class)
@Config(application = Application.class, sdk = 33)
public class SurfaceControllerOverflowTest {
    private SurfaceController controller;
    private SurfaceView video;
    private TextureView subtitles;
    private int[] box;

    @Before
    public void createSurfaces() {
        Context context = RuntimeEnvironment.getApplication();
        FrameLayout root = new FrameLayout(context);
        video = new SurfaceView(context);
        video.setId(R.id.surface_view);
        TextureView effect = new TextureView(context);
        effect.setId(R.id.gl_surface_view);
        subtitles = new TextureView(context);
        subtitles.setId(R.id.gl_subtitle_view);
        root.addView(video, centered());
        root.addView(effect, centered());
        root.addView(subtitles, centered());
        controller = new SurfaceController(root);
        controller.setEffectMode(0);
        controller.setMediaPlayer(mock(IMediaPlayer.class));
        controller.setVideoBoxListener((x, y, w, h) -> box = new int[] {x, y, w, h});
    }

    private FrameLayout.LayoutParams centered() {
        return new FrameLayout.LayoutParams(1, 1, Gravity.CENTER);
    }

    private void fitWidth() {
        controller.setScreenSize(2400, 1080);
        controller.setVideoSize(1920, 1080, 1.0);
        controller.setVideoFormat(SurfaceController.VideoFormat.STRETCH_XY);
    }

    @Test
    public void clipsCanvasButKeepsVideoDimensionsAndNegativeCenteredBox() {
        fitWidth();
        assertEquals(2400, video.getLayoutParams().width);
        assertEquals(1350, video.getLayoutParams().height);
        assertEquals(2400, subtitles.getLayoutParams().width);
        assertEquals(1080, subtitles.getLayoutParams().height);
        assertArrayEquals(new int[] {0, -135, 2400, 1350}, box);
        assertFalse(subtitles.isOpaque());
        FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) subtitles.getLayoutParams();
        assertEquals(Gravity.CENTER, params.gravity);
        assertEquals(0, params.topMargin);
    }

    @Test
    public void clippingDoesNotDependOnSubtitleKindOrUseMarginsPreference() {
        fitWidth();
        for (int kind : new int[] {SurfaceController.SUBTITLE_CATEGORY_PLAIN_TEXT,
                SurfaceController.SUBTITLE_CATEGORY_ASS, SurfaceController.SUBTITLE_CATEGORY_GFX}) {
            for (boolean margins : new boolean[] {false, true}) {
                controller.setSubtitleLayoutMode(kind, margins);
                assertEquals(1080, subtitles.getLayoutParams().height);
                assertArrayEquals(new int[] {0, -135, 2400, 1350}, box);
            }
        }
    }

    @Test
    public void lateListenerReplaysTheClippedVideoBox() {
        fitWidth();
        int[] previous = box.clone();
        box = null;
        controller.setVideoBoxListener((x, y, w, h) -> box = new int[] {x, y, w, h});
        assertArrayEquals(previous, box);
    }

    @Test
    public void returningToFullscreenClearsOverflowOffset() {
        fitWidth();
        controller.setVideoFormat(SurfaceController.VideoFormat.FULL_SCREEN);
        assertEquals(1080, subtitles.getLayoutParams().height);
        assertEquals(1080, video.getLayoutParams().height);
        assertArrayEquals(new int[] {0, 0, 2400, 1080}, box);
    }

    @Test
    public void letterboxExtensionKeepsItsExistingPositiveOffset() {
        controller.setScreenSize(1920, 1080);
        controller.setVideoSize(1920, 800, 1.0);
        controller.setVideoFormat(SurfaceController.VideoFormat.ORIGINAL);
        assertEquals(ViewGroup.LayoutParams.MATCH_PARENT, subtitles.getLayoutParams().height);
        assertArrayEquals(new int[] {0, 140, 1920, 800}, box);
    }

    @Test
    public void tbAndSbsKeepVideoSizedCanvasAndZeroOffset() {
        fitWidth();
        for (int mode : new int[] {VideoEffect.TB_MODE, VideoEffect.SBS_MODE}) {
            controller.setEffectMode(mode);
            assertEquals(video.getLayoutParams().height, subtitles.getLayoutParams().height);
            assertArrayEquals(new int[] {0, 0, 2400, 1350}, box);
        }
    }

    @Test
    public void hdmiUsesRemappedVisibleHeightRatherThanExternalPixels() {
        controller.setScreenSize(2400, 1080);
        controller.setVideoSize(1440, 1080, 1.0);
        controller.setHdmiPlugged(true, 3840, 2160);
        controller.setVideoFormat(SurfaceController.VideoFormat.STRETCH_XY);
        assertEquals(1440, video.getLayoutParams().height);
        assertEquals(1080, subtitles.getLayoutParams().height);
        assertArrayEquals(new int[] {0, -180, 2400, 1440}, box);
    }

    @Test
    public void nonMarginLayoutParamsAlsoClipToVisibleHeight() {
        TextureView plain = new TextureView(RuntimeEnvironment.getApplication());
        plain.setLayoutParams(new ViewGroup.LayoutParams(1, 1));
        ReflectionHelpers.setField(controller, "mSubtitleView", plain);
        fitWidth();
        assertEquals(1080, plain.getLayoutParams().height);
        assertEquals(2400, plain.getLayoutParams().width);
        assertArrayEquals(new int[] {0, -135, 2400, 1350}, box);
    }
}
