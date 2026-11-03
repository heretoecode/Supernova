package com.archos.mediacenter.video.foundation;

import android.content.Context;
import android.content.SharedPreferences;
import androidx.preference.PreferenceManager;

/** Supernova always uses the approved TV UI, including restored preferences. */
public final class FoundationUiPolicy {
    private FoundationUiPolicy() {}
    public static void apply(Context context) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        if (!preferences.getBoolean("try_new_ui", false))
            preferences.edit().putBoolean("try_new_ui", true).apply();
    }
}
