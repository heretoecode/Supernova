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
        assertEquals(133,CustomApplication.getNovaVersionArray()[1]);
    }
    @Test public void foundationCounterDoesNotRunLegacyPreferenceResetMigrations() throws Exception {
        org.junit.Assume.assumeTrue(BuildConfig.FOUNDATION);
        Context context=RuntimeEnvironment.getApplication();SharedPreferences preferences=PreferenceManager.getDefaultSharedPreferences(context);
        preferences.edit().putBoolean("force_audio_passthrough",true).putBoolean("enable_dynamic_audio_delay",false).putBoolean("playback_speed",false).putBoolean("smbj",true).commit();
        java.util.Map<String,?> before=preferences.getAll();
        java.lang.reflect.Method migrate=CustomApplication.class.getDeclaredMethod("upgradeActions",Context.class);migrate.setAccessible(true);migrate.invoke(new CustomApplication(),context);
        assertEquals(before,preferences.getAll());
    }
}
