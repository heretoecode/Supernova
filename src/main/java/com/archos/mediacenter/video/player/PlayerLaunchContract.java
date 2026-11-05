package com.archos.mediacenter.video.player;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;

/** Launch-scoped stereo state, including the historical stereo entry alias. */
public final class PlayerLaunchContract {
    public static final String STEREO_MODE = "stereo_mode";

    private PlayerLaunchContract() {}

    public static boolean stereoForced(Context context, Intent intent) {
        if (intent == null) return false;
        if (intent.hasExtra(STEREO_MODE)) return intent.getBooleanExtra(STEREO_MODE, false);
        if (intent.getComponent() == null) return false;
        try {
            ActivityInfo info = context.getPackageManager().getActivityInfo(
                    intent.getComponent(), PackageManager.GET_META_DATA);
            return info.metaData != null && info.metaData.getBoolean(STEREO_MODE, false);
        } catch (PackageManager.NameNotFoundException unavailable) {
            return false;
        }
    }
}
