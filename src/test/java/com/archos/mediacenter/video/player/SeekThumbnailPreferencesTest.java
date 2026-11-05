package com.archos.mediacenter.video.player;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import android.app.Application;
import androidx.preference.PreferenceManager;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;

@RunWith(RobolectricTestRunner.class)
@Config(application = Application.class, sdk = 30)
public class SeekThumbnailPreferencesTest
{
    @Test
    public void experimentIsOnByDefaultAndExplicitOffIsRetained()
    {
        Application app = RuntimeEnvironment.getApplication();
        assertTrue(SeekThumbnailPreferences.enabled(app));
        PreferenceManager.getDefaultSharedPreferences(app).edit().putBoolean(SeekThumbnailPreferences.KEY, false).commit();
        assertFalse(SeekThumbnailPreferences.enabled(app));
    }
}
