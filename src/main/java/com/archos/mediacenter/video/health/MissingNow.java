package com.archos.mediacenter.video.health;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import com.archos.filecorelibrary.FileUtils;
import com.archos.mediacenter.filecoreextension.upnp2.MetaFileFactoryWithUpnp;
import com.archos.mediacenter.video.diagnostics.Diagnostics;
import com.archos.mediaprovider.video.SupernovaLibraryPolicy;
import com.archos.mediaprovider.video.VideoStore;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * M3 (owner, 26 Nov 2026): Missing files no longer learns only from scans. Whenever Supernova finds a library file
 * missing (a failed open, a delete, Details, artwork or subtitle work), it is marked at once, so it shows in Files ›
 * Library Care › Missing files with Find it again · Remove, and the badge and the dot beside Files update. Checked off
 * the main thread; only a server's "no such file" counts, never a lost connection.
 */
public final class MissingNow
{
    private static final ExecutorService WORKER = Executors.newSingleThreadExecutor();

    private MissingNow()
    {
    }

    /** Looks again at a file that just failed; marks it missing in the library's health record if it is gone. */
    public static void check(Context context, Uri uri)
    {
        if (context == null || uri == null)
        {
            return;
        }
        Context app = context.getApplicationContext();
        WORKER.execute(() -> {
            Exception lookup = null;
            Object found = null;
            try
            {
                found = MetaFileFactoryWithUpnp.getMetaFileForUrl(uri);
            }
            catch (Exception e)
            {
                lookup = e;
            }
            if (found != null || !com.archos.mediacenter.video.browser.Delete.alreadyGone(lookup))
            {
                return;
            }
            mark(app, uri);
        });
    }

    /** Records the file as missing since now (the same record a scan writes). */
    static void mark(Context context, Uri uri)
    {
        long id = libraryId(context, uri);
        if (id <= 0)
        {
            return;
        }
        context.getSharedPreferences(SupernovaLibraryPolicy.HEALTH, 0).edit()
            .putString("media:" + id, SupernovaLibraryPolicy.canonical(uri))
            .putLong("missing_since:" + id, System.currentTimeMillis())
            .apply();
        Diagnostics.event("library_file_missing", "source_kind", FileUtils.isLocal(uri) ? "local" : uri.getScheme(),
                          "reason", "gone_on_open", "step", "opening");
        context.getContentResolver().notifyChange(VideoStore.Video.Media.EXTERNAL_CONTENT_URI, null);
    }

    private static long libraryId(Context context, Uri uri)
    {
        String path = FileUtils.isLocal(uri) && uri.getPath() != null ? uri.getPath() : uri.toString();
        try (Cursor cursor = context.getContentResolver().query(
                 VideoStore.Video.Media.EXTERNAL_CONTENT_URI, new String[] {VideoStore.Video.VideoColumns._ID},
                 VideoStore.MediaColumns.DATA + "=?", new String[] {path}, null))
        {
            return cursor != null && cursor.moveToFirst() ? cursor.getLong(0) : -1;
        }
        catch (RuntimeException unavailable)
        {
            return -1;
        }
    }
}
