package com.archos.mediacenter.video.player;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import android.app.Application;
import android.view.WindowManager;
import android.widget.FrameLayout;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;

@RunWith(RobolectricTestRunner.class)
@Config(application = Application.class, sdk = 30)
public class SubtitleAppearanceSampleTest
{
    @Test public void sampleIsAvailableWithNoSubtitleTrackAndRestoresPresentationWhenHidden()
    {
        Application app = RuntimeEnvironment.getApplication();
        Player previous = Player.sPlayer;
        Player player = mock(Player.class);
        Player.sPlayer = player;
        try
        {
            SubtitleManager manager =
                new SubtitleManager(app, new FrameLayout(app),
                                    (WindowManager)app.getSystemService(android.content.Context.WINDOW_SERVICE), true);
            manager.setScreenSize(1920, 1080);
            manager.restoreStyle(androidx.preference.PreferenceManager.getDefaultSharedPreferences(app));
            assertNull(manager.appearanceSample("Sample", 1920, 1080));
            manager.setAppearancePreview(true);
            assertNotNull(manager.appearanceSample("Sample", 1920, 1080));
            verify(player).setSubtitlePresentationSuppressed("appearance", true);
            manager.setAppearancePreview(false);
            verify(player).setSubtitlePresentationSuppressed("appearance", false);
            assertNull(manager.appearanceSample("Sample", 1920, 1080));
        }
        finally
        {
            Player.sPlayer = previous;
        }
    }
}
