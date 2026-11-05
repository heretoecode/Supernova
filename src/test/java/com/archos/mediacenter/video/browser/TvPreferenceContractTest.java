package com.archos.mediacenter.video.browser;

import android.app.Application;
import android.content.Intent;
import android.content.pm.PackageManager;
import androidx.preference.PreferenceManager;
import com.archos.mediacenter.video.leanback.settings.VideoSettingsLicencesActivity;
import com.archos.mediacenter.video.utils.VideoPreferencesCommon;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;
import static org.junit.Assert.*;

@RunWith(RobolectricTestRunner.class)
@Config(application = Application.class, sdk = 28)
public class TvPreferenceContractTest {
    @Test
    public void externalPlayerKeyDefaultAndTvLicenceComponentRemainAvailable() throws Exception {
        var context = RuntimeEnvironment.getApplication();
        assertEquals("allow_3rd_party_player", VideoPreferencesCommon.ALLOW_3RD_PARTY_PLAYER);
        assertFalse(VideoPreferencesCommon.ALLOW_3RD_PARTY_PLAYER_DEFAULT);
        var preferences = PreferenceManager.getDefaultSharedPreferences(context);
        preferences.edit().remove("allow_3rd_party_player").commit();
        assertFalse(preferences.getBoolean(VideoPreferencesCommon.ALLOW_3RD_PARTY_PLAYER,
                VideoPreferencesCommon.ALLOW_3RD_PARTY_PLAYER_DEFAULT));
        preferences.edit().putBoolean("allow_3rd_party_player", true).commit();
        assertTrue(preferences.getBoolean(VideoPreferencesCommon.ALLOW_3RD_PARTY_PLAYER, false));
        Intent licences = new Intent(context, VideoSettingsLicencesActivity.class);
        assertNotNull(context.getPackageManager().getActivityInfo(licences.getComponent(),
                PackageManager.GET_META_DATA));
    }
}
