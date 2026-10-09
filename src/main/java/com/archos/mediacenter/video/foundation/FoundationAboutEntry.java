package com.archos.mediacenter.video.foundation;

import android.content.Intent;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;

/** Existing classic/mobile About entry points share the approved offline view. */
public final class FoundationAboutEntry {
    private FoundationAboutEntry() {}
    public static void install(PreferenceFragmentCompat fragment) {
        link(fragment,"preferences_version",0);
        link(fragment,"preferences_video_licences",2);
        for(String key:new String[]{"preferences_video_os","preferences_video_tmdb","preferences_video_trakt"})link(fragment,key,3);
    }
    private static void link(PreferenceFragmentCompat fragment,String key,int section) {
        Preference preference=fragment.findPreference(key);
        if(preference!=null)preference.setOnPreferenceClickListener(p->{
            fragment.startActivity(new Intent(fragment.requireContext(),FoundationAboutActivity.class).putExtra("section",section));return true;
        });
    }
}
