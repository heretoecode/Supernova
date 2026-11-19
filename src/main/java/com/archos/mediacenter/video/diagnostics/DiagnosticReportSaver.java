package com.archos.mediacenter.video.diagnostics;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;

import androidx.preference.PreferenceManager;

import java.io.OutputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Settings › System › Diagnostics › Save a diagnostic report (page sheet SETTINGS.md): saved in the row itself, no
 * Android file picker. The ZIP goes to Download › Supernova; the row reads "Saving…", then "Done · saved at 23:51".
 */
public final class DiagnosticReportSaver {
    /** The row's live second line; empty when nothing has been saved in this run of the app. */
    public static final String STATUS = "supernova_diagnostic_report_status";
    public static final String FOLDER = "Download/Supernova";

    private DiagnosticReportSaver() {
    }

    public static void save(Context context) {
        Context app = context.getApplicationContext();
        status(app, "Saving…");
        new Thread(() -> {
            boolean saved = false;
            try {
                saved = write(app);
            } catch (Exception failure) {
                Diagnostics.error("diagnostic_export_failed", failure);
            }
            String time = DateFormat.getTimeInstance(DateFormat.SHORT).format(new Date());
            status(app, saved ? "Done · saved at " + time + " in Download › Supernova · never includes passwords"
                              : "Couldn't save the report. Try again.");
        }, "DiagnosticReportSaver").start();
    }

    private static boolean write(Context app) throws Exception {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q)
            return false;
        String name = "Supernova Diagnostics - "
            + new SimpleDateFormat("yyyy-MM-dd HH-mm-ss", Locale.ROOT).format(new Date()) + ".zip";
        ContentValues values = new ContentValues();
        values.put(MediaStore.MediaColumns.DISPLAY_NAME, name);
        values.put(MediaStore.MediaColumns.MIME_TYPE, "application/zip");
        values.put(MediaStore.MediaColumns.RELATIVE_PATH, FOLDER);
        values.put(MediaStore.MediaColumns.IS_PENDING, 1);
        ContentResolver resolver = app.getContentResolver();
        Uri target = resolver.insert(MediaStore.Downloads.EXTERNAL_CONTENT_URI, values);
        if (target == null)
            return false;
        try (OutputStream out = resolver.openOutputStream(target, "w")) {
            if (out == null)
                return false;
            Diagnostics.export(app, out);
        }
        values.clear();
        values.put(MediaStore.MediaColumns.IS_PENDING, 0);
        resolver.update(target, values, null, null);
        return true;
    }

    private static void status(Context app, String line) {
        PreferenceManager.getDefaultSharedPreferences(app).edit().putString(STATUS, line).apply();
    }
}
