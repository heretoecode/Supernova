package com.archos.mediacenter.video.player;

import android.app.Application;
import android.content.SharedPreferences;
import androidx.preference.PreferenceManager;
import com.archos.mediacenter.video.utils.VideoPreferencesCommon;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;
import static org.junit.Assert.*;

@RunWith(RobolectricTestRunner.class)
@Config(application=Application.class,sdk=28)
public class AudioSpeedSettingsTest {
    @Test public void retainedSwitchesRemainAuthoritativeAndSonicIsAdditive(){
        SharedPreferences prefs=PreferenceManager.getDefaultSharedPreferences(RuntimeEnvironment.getApplication());
        prefs.edit().clear().putString("audio_speed_mode","0").commit();
        assertEquals("2",VideoPreferencesCommon.audioSpeedMode(prefs));
        prefs.edit().putString("supernova_pcm_speed_engine","3").commit();
        assertEquals("3",VideoPreferencesCommon.audioSpeedMode(prefs));
        prefs.edit().putBoolean("audio_speed_audiotrack",true).commit();
        assertEquals("1",VideoPreferencesCommon.audioSpeedMode(prefs));
        prefs.edit().putBoolean("playback_speed",false).commit();
        assertEquals("0",VideoPreferencesCommon.audioSpeedMode(prefs));
        assertFalse(VideoPreferencesCommon.isAudioSpeedEnabled(prefs));
        assertTrue(prefs.getBoolean("audio_speed_audiotrack",false));
        assertEquals("3",prefs.getString("supernova_pcm_speed_engine",""));
    }
    @Test public void passthroughAndHardwareAudioStillGateSpeed(){
        SharedPreferences prefs=PreferenceManager.getDefaultSharedPreferences(RuntimeEnvironment.getApplication());
        prefs.edit().clear().putString("supernova_pcm_speed_engine","3").commit();
        assertTrue(VideoPreferencesCommon.isAudioSpeedEnabled(prefs));
        prefs.edit().putString("force_audio_passthrough_multiple","1").commit();
        assertFalse(VideoPreferencesCommon.isAudioSpeedEnabled(prefs));
        prefs.edit().putString("force_audio_passthrough_multiple","0").putString("audio_decoder_choice","2").commit();
        assertFalse(VideoPreferencesCommon.isAudioSpeedEnabled(prefs));
    }
}
