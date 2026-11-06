package com.archos.mediacenter.video.data;

import android.graphics.Bitmap;
import android.media.ThumbnailUtils;
import android.os.Build;
import android.os.CancellationSignal;
import android.util.LruCache;
import android.util.Size;
import java.io.File;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.function.BooleanSupplier;
import java.util.function.Consumer;

/** Worker-owned folder mosaics and small local-only stills. No network thumbnail extraction. */
public final class FilesArtwork
{
    public record Artwork(String canonicalFile, String picture)
    {
    }

    private record FolderKey(String folder, long inventoryVersion)
    {
    }

    private static final Map<FolderKey, List<String>> FOLDERS = new LinkedHashMap<>(32, .75f, true);
    private static final LruCache<String, Bitmap> STILLS = new LruCache<>(8 * 1024 * 1024) {
        @Override protected int sizeOf(String key, Bitmap value)
        {
            return value.getAllocationByteCount();
        }
    };
    private static final ThreadPoolExecutor WORK = new ThreadPoolExecutor(1, 1, 30, TimeUnit.SECONDS,
        new ArrayBlockingQueue<>(4), task -> {
            Thread thread = new Thread(task, "LocalFileArtwork");
            thread.setDaemon(true);
            return thread;
        });
    private static final ScheduledExecutorService CLOCK = Executors.newSingleThreadScheduledExecutor(task -> {
        Thread thread = new Thread(task, "LocalFileArtworkDeadline");
        thread.setDaemon(true);
        return thread;
    });

    /** Worker-only. canonicalFile and folder must come from the same canonical library path inventory. */
    public static synchronized List<String> folder(String canonicalFolder, long version, List<Artwork> inventory)
    {
        FolderKey key = new FolderKey(canonicalFolder, version);
        List<String> cached = FOLDERS.get(key);
        if (cached != null)
            return cached;
        String prefix = canonicalFolder.endsWith("/") ? canonicalFolder : canonicalFolder + "/";
        LinkedHashSet<String> pictures = new LinkedHashSet<>();
        for (Artwork entry : inventory)
            if (entry.canonicalFile().startsWith(prefix) && entry.picture() != null &&
                !entry.picture().trim().isEmpty())
            {
                pictures.add(entry.picture());
                if (pictures.size() == 4)
                    break;
            }
        List<String> result = List.copyOf(pictures);
        FOLDERS.put(key, result);
        while (FOLDERS.size() > 32)
            FOLDERS.remove(FOLDERS.keySet().iterator().next());
        return result;
    }

    public static final class Job implements AutoCloseable
    {
        private final CancellationSignal cancellation = new CancellationSignal();
        private final Executor callbacks;
        private final Consumer<Bitmap> listener;
        private boolean done;
        private boolean closed;
        private java.util.concurrent.Future<?> work;
        private java.util.concurrent.ScheduledFuture<?> deadline;

        private Job(Executor callbacks, Consumer<Bitmap> listener)
        {
            this.callbacks = callbacks;
            this.listener = listener;
        }

        private synchronized void complete(Bitmap bitmap)
        {
            if (done || closed)
                return;
            done = true;
            if (deadline != null)
                deadline.cancel(false);
            if (bitmap == null)
            {
                cancellation.cancel();
                if (work != null)
                    work.cancel(true);
            }
            callbacks.execute(() -> {
                synchronized (Job.this)
                {
                    if (!closed)
                        listener.accept(bitmap);
                }
            });
        }

        @Override public synchronized void close()
        {
            closed = true;
            cancellation.cancel();
            if (work != null)
                work.cancel(true);
            if (deadline != null)
                deadline.cancel(false);
        }
    }

    /** Main-thread safe. null means unavailable/expensive/busy. Caller owns the Job, cache owns returned bitmaps. */
    public static Job still(File localFile, BooleanSupplier playbackBusy, Executor main, Consumer<Bitmap> listener)
    {
        Job job = new Job(main, listener);
        synchronized (job)
        {
            job.deadline = CLOCK.schedule(() -> job.complete(null), 2, TimeUnit.SECONDS);
            try
            {
                job.work = WORK.submit(() -> {
                    Bitmap result = null;
                    try
                    {
                        if (Build.VERSION.SDK_INT >= 29 && localFile != null && !playbackBusy.getAsBoolean())
                        {
                            File file = localFile.getCanonicalFile();
                            if (file.isFile() && (file.getPath().startsWith("/storage/") ||
                                                  file.getPath().startsWith("/sdcard/")))
                            {
                                String key = file.getPath() + ":" + file.length() + ":" + file.lastModified();
                                result = STILLS.get(key);
                                if (result == null && !job.cancellation.isCanceled())
                                {
                                    result = ThumbnailUtils.createVideoThumbnail(file, new Size(320, 180),
                                                                                  job.cancellation);
                                    if (result != null && !job.cancellation.isCanceled())
                                        STILLS.put(key, result);
                                }
                            }
                        }
                    }
                    catch (Exception unavailable)
                    {
                        // Unsupported or unreadable local files retain a plain card.
                    }
                    job.complete(result);
                });
            }
            catch (java.util.concurrent.RejectedExecutionException busy)
            {
                job.complete(null);
            }
        }
        return job;
    }

    private FilesArtwork()
    {
    }
}
