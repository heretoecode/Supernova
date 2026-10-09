package com.archos.mediacenter.video.foundation;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import androidx.preference.PreferenceManager;
import com.archos.mediacenter.video.BuildConfig;
import com.archos.mediacenter.video.CustomApplication;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;
import static org.junit.Assert.*;

@RunWith(RobolectricTestRunner.class)
@Config(application=Application.class,sdk=28)
public class FoundationApplicationIdentityTest {
    @Test public void literalVersionPopulatesRuntimeIdentityWithoutNovaParsing() throws Exception {
        org.junit.Assume.assumeTrue(BuildConfig.FOUNDATION);
        Context context=RuntimeEnvironment.getApplication();
        android.content.pm.PackageInfo info=new android.content.pm.PackageInfo();info.packageName=context.getPackageName();info.versionName=BuildConfig.VERSION_NAME;info.versionCode=BuildConfig.VERSION_CODE;
        org.robolectric.Shadows.shadowOf(context.getPackageManager()).installPackage(info);
        java.lang.reflect.Field logger=CustomApplication.class.getDeclaredField("log");logger.setAccessible(true);logger.set(null,org.slf4j.LoggerFactory.getLogger(CustomApplication.class));
        java.lang.reflect.Field state=CustomApplication.class.getDeclaredField("novaVersionStateInitialized");state.setAccessible(true);state.setBoolean(null,false);
        java.lang.reflect.Method update=CustomApplication.class.getDeclaredMethod("updateVersionState",Context.class);update.setAccessible(true);update.invoke(null,context);
        assertEquals("Supernova "+BuildConfig.VERSION_NAME,CustomApplication.getNovaLongVersion());
        assertEquals("v"+BuildConfig.VERSION_NAME,CustomApplication.getNovaShortVersion());
        assertEquals(BuildConfig.VERSION_CODE,CustomApplication.getNovaVersionCode());
        assertEquals(BuildConfig.VERSION_CODE,CustomApplication.getNovaVersionArray()[1]);
    }
    @Test public void foundationCounterDoesNotRunLegacyPreferenceResetMigrations() throws Exception {
        org.junit.Assume.assumeTrue(BuildConfig.FOUNDATION);
        Context context=RuntimeEnvironment.getApplication();SharedPreferences preferences=PreferenceManager.getDefaultSharedPreferences(context);
        preferences.edit().putBoolean("force_audio_passthrough",true).putBoolean("enable_dynamic_audio_delay",false).putBoolean("playback_speed",false).putBoolean(com.archos.mediacenter.video.utils.VideoPreferencesCommon.KEY_SMBJ,true).commit();
        java.util.Map<String,?> before=preferences.getAll();
        java.lang.reflect.Method migrate=CustomApplication.class.getDeclaredMethod("upgradeActions",Context.class);migrate.setAccessible(true);migrate.invoke(new CustomApplication(),context);
        assertEquals(before,preferences.getAll());
    }
    @Test public void restoredFalseUiPreferenceUsesApprovedFoundationUiWithoutResettingUserChoices() {
        org.junit.Assume.assumeTrue(BuildConfig.FOUNDATION);
        SharedPreferences p=PreferenceManager.getDefaultSharedPreferences(RuntimeEnvironment.getApplication());
        p.edit().putBoolean("try_new_ui",false).putBoolean("force_audio_passthrough",true).putString("streaming_country","GB").commit();
        FoundationUiPolicy.apply(RuntimeEnvironment.getApplication());
        assertTrue(p.getBoolean("try_new_ui",false));assertTrue(p.getBoolean("force_audio_passthrough",false));assertEquals("GB",p.getString("streaming_country",""));
        java.util.Map<String,?> before=p.getAll();FoundationUiPolicy.apply(RuntimeEnvironment.getApplication());assertEquals(before,p.getAll());
    }
    @Test public void freshFoundationUiPreferenceIsEnabled() {
        org.junit.Assume.assumeTrue(BuildConfig.FOUNDATION);
        SharedPreferences p=PreferenceManager.getDefaultSharedPreferences(RuntimeEnvironment.getApplication());p.edit().remove("try_new_ui").commit();
        FoundationUiPolicy.apply(RuntimeEnvironment.getApplication());assertTrue(p.getBoolean("try_new_ui",false));
    }
    @Test public void foundationSettingsOrganisationDoesNotExposeObsoleteUiToggle() {
        org.junit.Assume.assumeTrue(BuildConfig.FOUNDATION);
        var host=org.robolectric.Robolectric.buildActivity(androidx.fragment.app.FragmentActivity.class).setup();
        try {
            FoundationAboutTest.Prefs fragment=new FoundationAboutTest.Prefs();host.get().getSupportFragmentManager().beginTransaction().add(fragment,"preferences").commitNow();
            androidx.preference.SwitchPreferenceCompat toggle=new androidx.preference.SwitchPreferenceCompat(host.get());toggle.setKey("try_new_ui");toggle.setTitle("Try New UI");fragment.getPreferenceScreen().addPreference(toggle);
            com.archos.mediacenter.video.leanback.settings.PreviewSettings.organise(fragment);
            androidx.preference.Preference hidden=fragment.findPreference("try_new_ui");
            assertNotNull(hidden);assertFalse(hidden.isEnabled());assertFalse(hidden.isSelectable());assertFalse(hidden.getParent().isVisible());
        } finally {host.pause().stop().destroy();}
    }
}
