package com.archos.mediacenter.video;

import android.content.Context;
import android.content.SharedPreferences;
import androidx.preference.PreferenceManager;

/** Retained UI-mode contract; Supernova supports the TV interface only. */
public final class UiModePolicy {
    public static final String UI_CHOICE_LEANBACK_KEY = "uimode_leanback";
    public static final String UI_CHOICE_LEANBACK_TABLET_VALUE = "tablet";
    public static final String UI_CHOICE_LEANBACK_TV_VALUE = "tv";

    private UiModePolicy() {}

    public static boolean applicationIsInLeanbackMode(Context context) { return true; }

    public static void updateUiModePreferences(Context context, boolean ignored) {
        SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(context).edit();
        updateUiModePreferencesInEditor(context, editor);
        editor.apply();
    }

    public static void updateUiModePreferencesInEditor(Context context, SharedPreferences.Editor editor) {
        editor.putString(UI_CHOICE_LEANBACK_KEY, UI_CHOICE_LEANBACK_TV_VALUE);
        editor.putString("uimode", "2");
    }
}
