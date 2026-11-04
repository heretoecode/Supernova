package com.archos.mediacenter.video.player;

import android.app.Application;
import android.content.pm.ActivityInfo;
import android.view.Display;
import android.view.View;
import android.view.Window;

import com.archos.mediacenter.video.utils.CodecDiscovery;

import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;
import org.robolectric.util.ReflectionHelpers;

import static org.mockito.Mockito.CALLS_REAL_METHODS;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

/** 5c721514: HDR composition remains the producer's job, with no layer transaction. */
@RunWith(RobolectricTestRunner.class)
@Config(application = Application.class, manifest = Config.NONE, sdk = 33)
public class PlayerHdrProducerContractTest {
    @After
    public void resetCapabilities() {
        CodecDiscovery.resetHdrCapabilities();
    }

    @Test
    public void hdrWindowRequestDoesNotMutateTheSurfaceController() {
        Window window = mock(Window.class);
        View decor = mock(View.class);
        Display display = mock(Display.class);
        SurfaceController surface = mock(SurfaceController.class);
        when(window.getDecorView()).thenReturn(decor);
        when(decor.getDisplay()).thenReturn(display);

        updateCapabilities(window, surface);

        verify(window).setColorMode(ActivityInfo.COLOR_MODE_HDR);
        verify(display).getHdrCapabilities();
        verifyNoInteractions(surface);
    }

    @Test
    @Config(sdk = 23)
    public void api23DoesNotRequestNewerColorModeOrTouchTheSurface() {
        Window window = mock(Window.class);
        SurfaceController surface = mock(SurfaceController.class);

        updateCapabilities(window, surface);

        verify(window).getDecorView();
        verifyNoMoreInteractions(window);
        verifyNoInteractions(surface);
    }

    @Test
    public void missingWindowDoesNotTouchTheSurface() {
        SurfaceController surface = mock(SurfaceController.class);
        updateCapabilities(null, surface);
        verifyNoInteractions(surface);
    }

    private void updateCapabilities(Window window, SurfaceController surface) {
        Player player = mock(Player.class, CALLS_REAL_METHODS);
        ReflectionHelpers.setField(player, "mWindow", window);
        ReflectionHelpers.setField(player, "mSurfaceController", surface);
        ReflectionHelpers.callInstanceMethod(player, "setHdrCapabilities");
    }
}
