package com.archos.mediacenter.video.leanback.files;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;

import com.archos.mediacenter.filecoreextension.upnp2.RawListerFactoryWithUpnp;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.function.BiConsumer;

/**
 * Whether a saved connection answers: its top folder is listed (saved logins apply), with a time limit, on a worker
 * thread. A success is remembered as "last seen" (Files › Connections, mock-up network-files-tabs/05).
 */
public final class ConnectionCheck
{
    static final long LIMIT_SECONDS = 15;
    private static final String SEEN = "files_connection_last_seen";
    private static final ExecutorService WORK = Executors.newSingleThreadExecutor(task -> {
        Thread thread = new Thread(task, "FilesConnectionCheck");
        thread.setDaemon(true);
        return thread;
    });
    private static final ExecutorService LIST = Executors.newCachedThreadPool(task -> {
        Thread thread = new Thread(task, "FilesConnectionList");
        thread.setDaemon(true);
        return thread;
    });

    private ConnectionCheck()
    {
    }

    /** Checks each root in turn; {@code done} gets the root and whether it answered (on the worker thread). */
    public static void check(Context context, List<Uri> roots, BiConsumer<Uri, Boolean> done)
    {
        Context app = context.getApplicationContext();
        WORK.execute(() -> {
            for (Uri root : roots)
            {
                boolean reachable = answers(root);
                if (reachable)
                {
                    prefs(app).edit().putLong(key(root), System.currentTimeMillis()).apply();
                }
                done.accept(root, reachable);
            }
        });
    }

    public static long lastSeen(Context context, Uri root)
    {
        return prefs(context).getLong(key(root), 0);
    }

    private static boolean answers(Uri root)
    {
        Future<?> listing = LIST.submit(() -> {
            RawListerFactoryWithUpnp.getRawListerForUrl(root).getFileList();
            return null;
        });
        try
        {
            listing.get(LIMIT_SECONDS, TimeUnit.SECONDS);
            return true;
        }
        catch (Exception failed)
        {
            listing.cancel(true);
            return false;
        }
    }

    private static SharedPreferences prefs(Context context)
    {
        return context.getSharedPreferences(SEEN, Context.MODE_PRIVATE);
    }

    /** No address (which may carry a login) is stored: only a short hash of it. */
    private static String key(Uri root)
    {
        return Integer.toHexString(root.toString().hashCode());
    }
}
