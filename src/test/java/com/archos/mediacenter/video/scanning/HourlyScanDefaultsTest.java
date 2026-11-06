package com.archos.mediacenter.video.scanning;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import android.app.Application;
import android.content.SharedPreferences;
import androidx.preference.PreferenceManager;
import com.archos.mediacenter.video.leanback.PreviewAutoScanPolicy;
import com.archos.mediaprovider.video.NetworkAutoRefresh;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;

@RunWith(RobolectricTestRunner.class)
@Config(application = Application.class, sdk = 30)
public class HourlyScanDefaultsTest
{
    @Test public void freshConfirmedSetupUsesHourlyAndPreservesExplicitOffAndCustomPeriod()
    {
        Application app = RuntimeEnvironment.getApplication();
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(app);
        prefs.edit().putBoolean("try_new_ui", true).putBoolean("supernova_onboarding_complete", true).commit();
        PreviewAutoScanPolicy.initialise(app);
        assertEquals(3600000, prefs.getInt(NetworkAutoRefresh.AUTO_RESCAN_PERIOD, 0));
        assertTrue(prefs.getBoolean("auto_rescan_on_app_restart", false));
        prefs.edit()
            .putBoolean("auto_rescan_on_app_restart", false)
            .putInt(NetworkAutoRefresh.AUTO_RESCAN_PERIOD, 0)
            .commit();
        PreviewAutoScanPolicy.initialise(app);
        assertFalse(prefs.getBoolean("auto_rescan_on_app_restart", true));
        assertEquals(0, prefs.getInt(NetworkAutoRefresh.AUTO_RESCAN_PERIOD, -1));
        prefs.edit().putInt(NetworkAutoRefresh.AUTO_RESCAN_PERIOD, 7200000).commit();
        PreviewAutoScanPolicy.initialise(app);
        assertEquals(7200000, prefs.getInt(NetworkAutoRefresh.AUTO_RESCAN_PERIOD, -1));
    }
    @Test public void removedTwelveHourChoiceMigratesWithoutChangingTheOpenSwitch()
    {
        Application app = RuntimeEnvironment.getApplication();
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(app);
        prefs.edit().putBoolean("try_new_ui", true).putBoolean("supernova_onboarding_complete", true)
            .putBoolean("auto_rescan_on_app_restart", false)
            .putInt(NetworkAutoRefresh.AUTO_RESCAN_PERIOD, 12 * 3600000).commit();
        PreviewAutoScanPolicy.initialise(app);
        assertEquals(6 * 3600000, prefs.getInt(NetworkAutoRefresh.AUTO_RESCAN_PERIOD, 0));
        assertFalse(prefs.getBoolean("auto_rescan_on_app_restart", true));
    }

}
