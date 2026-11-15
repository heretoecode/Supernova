package com.archos.mediacenter.video.leanback.overlay;

import android.os.SystemClock;

import com.archos.mediacenter.video.diagnostics.Diagnostics;
import com.archos.mediaprovider.ImportState;

/** Whether the local library import is really working: one stuck for three minutes no longer counts. */
public final class LocalImportActivity
{
    private static final StalledWorkWatch WATCH = new StalledWorkWatch(3 * 60 * 1000L);

    private LocalImportActivity()
    {
    }

    public static boolean moving()
    {
        boolean importing = ImportState.VIDEO.isInitialImport() || ImportState.VIDEO.isRegularImport();
        String progress = ImportState.VIDEO.getNumberOfFilesRemainingToImport() + "/"
            + ImportState.VIDEO.getNumberOfFilesRemainingToDelete() + "/" + ImportState.VIDEO.isDeleting();
        boolean stalled = WATCH.stalled(importing, progress, SystemClock.elapsedRealtime());
        if (stalled && WATCH.firstReport())
        {
            Diagnostics.event("local_import_stalled", "progress", progress);
        }
        return importing && !stalled;
    }
}
