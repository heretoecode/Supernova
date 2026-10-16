package com.archos.mediacenter.video.utils;

import android.app.Application;
import android.content.SharedPreferences;
import com.archos.mediacenter.video.utils.backup.PortableSettingsRegistry;
import com.archos.mediacenter.video.utils.backup.PortableSettingsRegistry.Category;
import java.util.*;
import org.json.*;
import org.junit.*;
import org.junit.runner.RunWith;
import org.robolectric.*;
import org.robolectric.annotation.Config;
import static org.junit.Assert.*;

@RunWith(RobolectricTestRunner.class) @Config(application=Application.class,sdk=28)
public class PortableSettingsRegistryTest {
    private SharedPreferences prefs(String name){return RuntimeEnvironment.getApplication().getSharedPreferences(name,0);}
    @Test public void explicitDefaultAndPreviouslyOmittedSubtitleAndDiagnosticChoicesRoundTrip()throws Exception{
        SharedPreferences source=prefs("source"),target=prefs("target");
        source.edit().putBoolean("subtitles_hide_default",false).putBoolean("supernova_subtitle_discovery",false).putBoolean("supernova_subtitle_season_prefetch",true).putBoolean("pref_play_subtitle_outline_key",true).putInt("pref_play_subtitle_color_key",-1).putInt("pref_play_subtitle_size_key",22).putInt("pref_play_subtitle_vpos_key",12).putInt("subtitle_bg_opacity",128).putBoolean("subtitle_background",false).putString("favSubLang","system").putStringSet("languages_list",new HashSet<>(Arrays.asList("eng","fra"))).putBoolean("supernova_diagnostic_logging",false).commit();
        JSONObject subtitles=PortableSettingsRegistry.export(source,Category.SUBTITLES);assertTrue(subtitles.getJSONObject("values").has("subtitles_hide_default"));
        assertTrue(PortableSettingsRegistry.replace(target,Category.SUBTITLES,new JSONObject(subtitles.toString())).commit());
        assertTrue(PortableSettingsRegistry.replace(target,Category.DIAGNOSTICS,PortableSettingsRegistry.export(source,Category.DIAGNOSTICS)).commit());
        assertEquals(source.getAll(),target.getAll());
    }
    @Test public void selectedReplacementNeverClearsUnselectedAuthenticationOrUnownedState()throws Exception{
        SharedPreferences source=prefs("source"),target=prefs("target");source.edit().putBoolean("subtitles_hide_default",false).commit();
        target.edit().putString("auth_token","CANARY").putInt("preview_accent41",123).putBoolean("subtitle_background",true).putInt("subtitle_bg_opacity",80).putBoolean("supernova_diagnostic_logging",false).commit();
        PortableSettingsRegistry.replace(target,Category.SUBTITLES,PortableSettingsRegistry.export(source,Category.SUBTITLES)).commit();
        assertEquals("CANARY",target.getString("auth_token",""));assertEquals(123,target.getInt("preview_accent41",0));assertFalse(target.getBoolean("supernova_diagnostic_logging",true));assertFalse(target.contains("subtitle_background"));assertFalse(target.contains("subtitle_bg_opacity"));assertFalse(target.getBoolean("subtitles_hide_default",true));
        PortableSettingsRegistry.replace(target,Category.SUBTITLES,PortableSettingsRegistry.export(prefs("empty"),Category.SUBTITLES)).commit();assertFalse(target.contains("subtitles_hide_default"));assertTrue(target.contains("auth_token"));
    }
    @Test public void credentialsPathsMediaCachesPutioAndLegacyCollectionsCannotEnterExport()throws Exception{
        SharedPreferences source=prefs("source");source.edit().putString("username","CANARY").putString("oauth_token","CANARY").putString("OPENSUBTITLES_PASSWORD","CANARY").putString("putio-library-selection-v1","CANARY").putString("user_collection","CANARY").putString("streaming_snapshot:movie:1","CANARY").putString("preview_scan_result","CANARY").putString("supernova_library_roots","smb://person:CANARY@host/share?password=CANARY").putString("media.db","CANARY").putString("preview_columns_arbitrary_auth_token","CANARY").putStringSet("streaming_providers_IE",Set.of("8","337")).putString("streaming_country","IE").commit();
        for(Category category:Category.values()){String json=PortableSettingsRegistry.export(source,category).toString();assertFalse(json.contains("CANARY"));assertFalse(json.contains("smb://"));}
        JSONObject streaming=PortableSettingsRegistry.export(source,Category.STREAMING);assertEquals(2,streaming.getJSONObject("values").length());
    }
    @Test public void malformedOrInjectedPayloadIsRejectedBeforeAnyDestinationMutation()throws Exception{
        SharedPreferences source=prefs("source"),target=prefs("target");source.edit().putBoolean("subtitles_hide_default",true).commit();target.edit().putBoolean("subtitles_hide_default",false).commit();Map<String,?> before=target.getAll();
        JSONObject payload=PortableSettingsRegistry.export(source,Category.SUBTITLES);payload.getJSONObject("values").put("oauth_token",new JSONObject().put("type","CHOICE").put("value","CANARY"));
        try{PortableSettingsRegistry.replace(target,Category.SUBTITLES,payload);fail();}catch(JSONException expected){}assertEquals(before,target.getAll());
        payload=PortableSettingsRegistry.export(source,Category.SUBTITLES);payload.getJSONObject("values").getJSONObject("subtitles_hide_default").put("value","true");
        try{PortableSettingsRegistry.replace(target,Category.SUBTITLES,payload);fail();}catch(JSONException expected){}assertEquals(before,target.getAll());
        source.edit().putString("favSubLang","https://person:CANARY@host/a?token=CANARY").commit();try{PortableSettingsRegistry.export(source,Category.SUBTITLES);fail();}catch(JSONException expected){}
    }
}
