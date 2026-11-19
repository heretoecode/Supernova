package com.archos.mediacenter.video.leanback;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.ApplicationExitInfo;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;

import java.util.List;

/**
 * FL12 (owner, 18 Nov 2026; RULES §16): closed fully → Home; only in the background → where you left it. When
 * Android brings back a page after the app was stopped by you (force stop or the like), the app starts again at Home;
 * when Android itself ended it in the background, the page comes back as before.
 */
public final class ClosedFully {
    private static boolean checked;

    private ClosedFully() {
    }

    /** Called from every page's onCreate; true when this page was replaced by a fresh Home. */
    public static boolean reopenHome(Activity activity, Bundle saved) {
        if (checked)
            return false;
        checked = true;
        if (saved == null || !stoppedByYou(activity))
            return false;
        activity.startActivity(new Intent(activity, MainActivityLeanback.class)
                                   .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK));
        activity.finish();
        return true;
    }

    /** Android 11 and later say why the app last ended; a stop you asked for means "closed fully". */
    static boolean stoppedByYou(Context context) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.R)
            return false;
        try {
            ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
            List<ApplicationExitInfo> exits = manager.getHistoricalProcessExitReasons(null, 0, 1);
            if (exits == null || exits.isEmpty())
                return false;
            int reason = exits.get(0).getReason();
            return reason == ApplicationExitInfo.REASON_USER_REQUESTED
                || reason == ApplicationExitInfo.REASON_USER_STOPPED;
        } catch (RuntimeException unavailable) {
            return false;
        }
    }
}
