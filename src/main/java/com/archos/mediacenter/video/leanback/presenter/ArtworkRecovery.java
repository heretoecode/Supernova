package com.archos.mediacenter.video.leanback.presenter;

import android.content.Context;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import com.archos.mediacenter.video.browser.adapters.object.Base;
import com.archos.mediacenter.video.diagnostics.Diagnostics;
import com.archos.mediascraper.BaseTags;
import com.archos.mediascraper.ScraperImage;
import java.io.File;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Consumer;

/**
 * D8 (0.158.8): a poster whose cached file is gone (123 TV Shows posters in the 26 Nov diagnostics, all
 * FileNotFoundException) is fetched again from where it came from and kept again, never left blank. Each missing
 * file is tried once per app run, one at a time, off the main thread.
 */
public final class ArtworkRecovery
{
    private static final ExecutorService WORK = Executors.newSingleThreadExecutor(r -> {
        Thread thread = new Thread(r, "ArtworkRecovery");
        thread.setPriority(Thread.MIN_PRIORITY);
        thread.setDaemon(true);
        return thread;
    });
    private static final Handler MAIN = new Handler(Looper.getMainLooper());
    private static final Set<String> TRIED = new HashSet<>();

    private ArtworkRecovery()
    {
    }

    /** True for artwork that should be a file on this device. */
    static boolean isLocalFile(Uri uri)
    {
        if (uri == null)
            return false;
        String scheme = uri.getScheme();
        return scheme == null || "file".equalsIgnoreCase(scheme);
    }

    /** First time only for this file: true if a fetch was started. */
    static synchronized boolean claim(Uri missing)
    {
        return missing != null && TRIED.add(missing.toString());
    }

    public static void recover(Context context, Base item, Uri missing, Consumer<Uri> recovered)
    {
        if (context == null || item == null || !isLocalFile(missing) || !claim(missing))
            return;
        Context app = context.getApplicationContext();
        WORK.execute(() -> {
            Uri found = find(app, item, missing);
            Diagnostics.event("artwork_recovery", "outcome", found == null ? "unavailable" : "fetched_again");
            if (found != null)
                MAIN.post(() -> recovered.accept(found));
        });
    }

    private static Uri find(Context context, Base item, Uri missing)
    {
        String path = missing.getPath();
        if (path != null && new File(path).isFile())
            return Uri.fromFile(new File(path));
        try
        {
            BaseTags tags = item.getFullScraperTags(context);
            ScraperImage poster = tags == null ? null : tags.getDefaultPoster();
            if (poster == null || poster.getLargeUrl() == null || poster.getLargeUrl().isEmpty())
                return null;
            File file = poster.getLargeFileF();
            if (file != null && !file.isFile())
                poster.download(context);
            if (file != null && file.isFile())
                return Uri.fromFile(file);
            // Kept nowhere: show it straight from its source this time.
            return Uri.parse(poster.getLargeUrl());
        }
        catch (RuntimeException unavailable)
        {
            return null;
        }
    }
}
