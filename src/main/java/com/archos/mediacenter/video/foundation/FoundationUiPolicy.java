package com.archos.mediacenter.video.foundation;

import android.content.Context;
import androidx.preference.PreferenceManager;
import com.archos.mediacenter.video.BuildConfig;

/** Foundation always uses the approved Supernova UI, including restored preferences. */
public final class FoundationUiPolicy {
    private FoundationUiPolicy() {}
    public static void apply(Context context) {
        if (!BuildConfig.FOUNDATION) return;
        android.content.SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        if (!preferences.getBoolean("try_new_ui", false))
            preferences.edit().putBoolean("try_new_ui", true).apply();
    }
}
