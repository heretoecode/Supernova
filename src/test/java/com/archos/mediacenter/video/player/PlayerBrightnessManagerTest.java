package com.archos.mediacenter.video.player;

import android.app.Activity;
import android.app.Application;
import android.provider.Settings;
import android.view.Window;
import android.view.WindowManager;
import androidx.preference.PreferenceManager;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;
import static org.junit.Assert.*;

@RunWith(RobolectricTestRunner.class)
@Config(application = Application.class, sdk = 28)
public class PlayerBrightnessManagerTest {
    @Test
    public void decreaseFromSystemBrightnessEstablishesOneStepLowerManualOverride() {
        try (var controller = Robolectric.buildActivity(Activity.class).setup()) {
            Activity activity = controller.get();
            Window window = activity.getWindow();
            Settings.System.putInt(activity.getContentResolver(), Settings.System.SCREEN_BRIGHTNESS, 160);
            WindowManager.LayoutParams params = window.getAttributes();
            params.screenBrightness = WindowManager.LayoutParams.BRIGHTNESS_OVERRIDE_NONE;
            window.setAttributes(params);
            int startingLevel = PlayerBrightnessManager.getLinearBrightness(window);
            int expectedLevel = Math.round(30f * (float) Math.pow(160f / 255f, 1f / 2.2f));
            assertEquals(expectedLevel, startingLevel);
            PlayerBrightnessManager.setLinearBrightness(startingLevel - 1, false, window);
            float manual = window.getAttributes().screenBrightness;
            assertTrue(manual >= 0f && manual < 160f / 255f);
            assertEquals(startingLevel - 1, PlayerBrightnessManager.getLinearBrightness(window));
            assertEquals((int) (manual * 255f), PreferenceManager
                    .getDefaultSharedPreferences(activity).getInt("brightness_saved", -1));
            PlayerBrightnessManager.setLinearBrightness(startingLevel - 2, false, window);
            assertTrue(window.getAttributes().screenBrightness < manual);
            PlayerBrightnessManager.setLinearBrightness(0, false, window);
            assertEquals(0f, window.getAttributes().screenBrightness, 0f);
            PlayerBrightnessManager.setLinearBrightness(30, true, window);
            assertEquals(1f, window.getAttributes().screenBrightness, 0f);
        }
    }
}
