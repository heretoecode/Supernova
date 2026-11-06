package com.archos.mediacenter.video.leanback;

import android.content.Context;
import com.archos.mediacenter.video.scanning.AutomaticScanChoices;
import android.content.SharedPreferences;
import androidx.preference.PreferenceManager;
import com.archos.mediaprovider.video.NetworkAutoRefresh;
import com.archos.mediaprovider.video.NetworkScannerUtil;
import com.archos.mediaprovider.video.SupernovaLibraryPolicy;

/** Initialise defaults and migrate the cadence choices without changing the separate on-open switch. */
public final class PreviewAutoScanPolicy
{
    public static void initialise(Context c)
    {
        SharedPreferences p = PreferenceManager.getDefaultSharedPreferences(c);
        if (!SupernovaLibraryPolicy.configured(c))
            return;
        SharedPreferences.Editor e = p.edit();
        if (!p.contains("auto_rescan_on_app_restart"))
            e.putBoolean("auto_rescan_on_app_restart", true);
        // An absent period previously meant no foreground polling and no scheduled job.
        if (!p.contains(NetworkAutoRefresh.AUTO_RESCAN_PERIOD))
            e.putInt(NetworkAutoRefresh.AUTO_RESCAN_PERIOD, 60 * 60 * 1000);
        e.apply();
        int previous = p.getInt(NetworkAutoRefresh.AUTO_RESCAN_PERIOD, 0);
        int period = AutomaticScanChoices.nearest(previous);
        if (previous != period)
            p.edit().putInt(NetworkAutoRefresh.AUTO_RESCAN_PERIOD, period).apply();
        int start = p.getInt(NetworkAutoRefresh.AUTO_RESCAN_STARTING_TIME_PREF, 0);
        NetworkScannerUtil.scheduleNewRescan(c, start < 0 ? 0 : start, period, true);
    }
    private PreviewAutoScanPolicy()
    {
    }
}
