package com.archos.mediacenter.video.player;

import android.content.Context;
import android.content.SharedPreferences;
import androidx.preference.PreferenceManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import android.os.SystemClock;
import com.archos.mediacenter.video.diagnostics.Diagnostics;
import com.archos.mediacenter.video.diagnostics.ThumbnailTelemetry;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.NavigableMap;
import java.util.TreeMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import java.util.function.LongConsumer;
import java.util.function.LongSupplier;

/** Experimental, entirely asynchronous previews. One serial worker owns extraction and cache I/O across sessions. */
public final class SeekThumbnails implements PartPictures.FrameSource, AutoCloseable
{
    public static final class Source
    {
        public final Uri uri;
        public final Uri resolvedUri;
        public final String mediaKey;
        public final String kind;
        public final String sizeClass;
        public final String resolution;
        public final String codec;
        public final long durationMs;
        final String identity;
        final boolean network;

        public Source(Uri uri, Uri resolvedUri, long durationMs, long fileBytes, String resolution, String codec)
            throws Exception
        {
            if (uri == null)
                throw new IllegalArgumentException("Thumbnail source required");
            this.uri = uri;
            this.resolvedUri = resolvedUri;
            this.durationMs = Math.max(0, durationMs);
            this.resolution = resolution == null ? "" : resolution;
            this.codec = codec == null ? "" : codec;
            String scheme = uri.getScheme();
            kind = scheme == null ? "file" : scheme.toLowerCase(java.util.Locale.ROOT);
            network = !(kind.equals("file") || kind.equals("content"));
            sizeClass = fileBytes <= 0                         ? "unknown"
                        : fileBytes < 100L * 1024 * 1024       ? "under_100mb"
                        : fileBytes < 1024L * 1024 * 1024      ? "under_1gb"
                        : fileBytes < 10L * 1024 * 1024 * 1024 ? "under_10gb"
                                                               : "over_10gb";
            identity = ownerIdentity(uri) + "|" + fileBytes + "|" + durationMs;
            mediaKey = ThumbnailDiskCache.stableKey(identity);
        }
    }

    public static final class Frame
    {
        public final Bitmap bitmap;
        public final long bytesRead;

        public Frame(Bitmap bitmap, long bytesRead)
        {
            this.bitmap = bitmap;
            this.bytesRead = bytesRead;
        }
    }

    public interface FrameExtractor extends AutoCloseable
    {
        Frame extract(Source source, long positionMs) throws Exception;
        void cancel();
        @Override void close() throws Exception;
    }

    interface Factory
    {
        FrameExtractor create();
    }

    private static final Executor IO = Executors.newSingleThreadExecutor(
        task -> new Thread(() -> {
            android.os.Process.setThreadPriority(android.os.Process.THREAD_PRIORITY_LOWEST);
            task.run();
        }, "SupernovaSeekThumbnails"));
    private static final Executor CANCEL = Executors.newSingleThreadExecutor();
    private static final ScheduledExecutorService CLOCK = Executors.newSingleThreadScheduledExecutor();
    private final Context context;
    private final Source source;
    private final LongSupplier position;
    private final LongSupplier clock;
    private final Factory factory;
    private final Executor io;
    private final Handler main;
    private final SeekThumbnailPolicy policy;
    private final SharedPreferences.OnSharedPreferenceChangeListener preferenceListener;
    private final ThumbnailTelemetry telemetry;
    private final CopyOnWriteArrayList<LongConsumer> listeners = new CopyOnWriteArrayList<>();
    private final NavigableMap<Long, Bitmap> memory = new TreeMap<>();
    private final LinkedHashMap<Long, Boolean> lru = new LinkedHashMap<>(32, 0.75f, true);
    private final NavigableMap<Long, List<Consumer<Bitmap>>> priority = new TreeMap<>();
    private ThumbnailDiskCache disk;
    private ScheduledFuture<?> ticker;
    private volatile FrameExtractor active;
    private volatile boolean closed;
    private boolean initialized;
    private boolean busy;
    private boolean buffering;
    private boolean seeking;
    private boolean foreground = true;
    private boolean pip;
    private boolean playing = true;
    private boolean lookupQueued;
    private long latestLookup;
    private Long previewTarget;
    private long nextDue;
    private long backoffUntil;
    private String lastSkip = "";

    public SeekThumbnails(Context context, Source source, LongSupplier position)
    {
        this(context, source, position,
             ()
                 -> new KeyframeExtractor(context),
             IO, new Handler(Looper.getMainLooper()), SystemClock::elapsedRealtime);
        ticker = CLOCK.scheduleWithFixedDelay(this::tick, 1, 1, TimeUnit.SECONDS);
    }

    SeekThumbnails(Context context, Source source, LongSupplier position, Factory factory, Executor io, Handler main,
                   LongSupplier clock)
    {
        this.context = context.getApplicationContext();
        this.source = source;
        this.position = position;
        this.factory = factory;
        this.io = io;
        this.main = main;
        this.clock = clock;
        policy = new SeekThumbnailPolicy(source.network);
        preferenceListener = (prefs, key) ->
        {
            if (SeekThumbnailPreferences.KEY.equals(key))
                state(buffering, seeking, foreground, pip, playing);
        };
        PreferenceManager.getDefaultSharedPreferences(this.context)
            .registerOnSharedPreferenceChangeListener(preferenceListener);
        telemetry = new ThumbnailTelemetry(source.mediaKey, source.kind, source.sizeClass, source.resolution,
                                           source.codec, Diagnostics::event);
        io.execute(() -> {
            try
            {
                disk = new ThumbnailDiskCache(new File(this.context.getCacheDir(), "seekthumbs"), source.identity,
                                              ThumbnailDiskCache.LIMIT_BYTES,
                                              ThumbnailDiskCache.stableKey(ownerIdentity(source.uri)));
                synchronized (this)
                {
                    initialized = true;
                }
                telemetry.event("thumb_job_start", "interval_ms", policy.intervalMs(), "reason", "playback");
                tick();
            }
            catch (Exception failure)
            {
                error("cache_open", failure);
            }
        });
    }

    static void cancelIo(Runnable task)
    {
        CANCEL.execute(task);
    }

    public String mediaKey()
    {
        return source.mediaKey;
    }

    public AutoCloseable addListener(LongConsumer listener)
    {
        listeners.add(listener);
        return () -> listeners.remove(listener);
    }

    /** Memory-only on the UI thread. A miss schedules one coalesced disk lookup, never a synchronous read. */
    public synchronized Bitmap nearest(long positionMs)
    {
        if (closed || source.durationMs <= 0 || !SeekThumbnailPreferences.enabled(context))
            return null;
        Map.Entry<Long, Bitmap> before = memory.floorEntry(positionMs);
        Map.Entry<Long, Bitmap> after = memory.ceilingEntry(positionMs);
        Map.Entry<Long, Bitmap> nearest = before == null                                                ? after
                                          : after == null                                               ? before
                                          : positionMs - before.getKey() <= after.getKey() - positionMs ? before
                                                                                                        : after;
        boolean hit = nearest != null && Math.abs(nearest.getKey() - positionMs) <= policy.intervalMs();
        telemetry.scrub(hit, hit ? Math.abs(nearest.getKey() - positionMs) : -1);
        if (hit)
        {
            lru.get(nearest.getKey());
            return nearest.getValue();
        }
        if (previewTarget != null && priority.containsKey(previewTarget) && priority.get(previewTarget).isEmpty())
            priority.remove(previewTarget);
        long target = Math.max(0, Math.min(source.durationMs - 1, positionMs));
        if (priority.size() < 256 || priority.containsKey(target)) {
            previewTarget = target;
            priority.computeIfAbsent(target, ignored -> new ArrayList<>());
        }
        tick();
        latestLookup = positionMs;
        if (initialized && !lookupQueued)
        {
            lookupQueued = true;
            io.execute(this::loadNearest);
        }
        return null;
    }

    private void loadNearest()
    {
        long target;
        synchronized (this)
        {
            target = latestLookup;
        }
        try
        {
            if (closed)
                return;
            Map.Entry<Long, File> frame = disk.nearest(target, policy.intervalMs());
            if (frame != null)
            {
                Bitmap bitmap = BitmapFactory.decodeFile(frame.getValue().toString());
                if (bitmap != null)
                    remember(frame.getKey(), bitmap);
                disk.touched();
            }
        }
        catch (RuntimeException failure)
        {
            error("cache_decode", failure);
        }
        finally
        {
            synchronized (this)
            {
                lookupQueued = false;
            }
        }
    }

    @Override public synchronized void frameAt(long positionMs, Consumer<Bitmap> callback)
    {
        if (closed || source.durationMs <= 0 || !SeekThumbnailPreferences.enabled(context))
        {
            if (!closed)
                main.post(() -> {
                    if (!closed)
                        callback.accept(null);
                });
            return;
        }
        long target = Math.max(0, Math.min(source.durationMs - 1, positionMs));
        Bitmap cached = memory.get(target);
        if (cached != null)
        {
            main.post(() -> {
                if (!closed)
                    callback.accept(cached);
            });
            return;
        }
        if (priority.size() >= 256 && !priority.containsKey(target))
        {
            telemetry.event("thumb_skip", "reason", "priority_queue_full");
            main.post(() -> {
                if (!closed)
                    callback.accept(null);
            });
            return;
        }
        priority.computeIfAbsent(target, ignored -> new ArrayList<>()).add(callback);
        tick();
    }

    synchronized void tick()
    {
        if (closed || busy || !initialized || source.durationMs <= 0)
            return;
        policy.state(SeekThumbnailPreferences.enabled(context), buffering, seeking, foreground, pip, playing);
        boolean explicit = !priority.isEmpty();
        String reason = policy.skipReason(explicit);
        if (!reason.isEmpty())
        {
            if (!reason.equals(lastSkip))
                telemetry.event("thumb_skip", "reason", reason);
            lastSkip = reason;
            return;
        }
        lastSkip = "";
        if (clock.getAsLong() < backoffUntil || (!explicit && clock.getAsLong() < nextDue))
            return;
        busy = true;
        io.execute(this::work);
    }

    private void work()
    {
        Map.Entry<Long, List<Consumer<Bitmap>>> request;
        synchronized (this)
        {
            if (previewTarget != null && priority.containsKey(previewTarget)) {
                request = new java.util.AbstractMap.SimpleImmutableEntry<>(previewTarget,
                        priority.remove(previewTarget));
                previewTarget = null;
            } else {
                request = priority.pollFirstEntry();
            }
        }
        boolean explicit = request != null;
        long target = request == null ? samplePosition() : request.getKey();
        Bitmap bitmap = null;
        String stage = "cache_read";
        try
        {
            if (closed || !policy.skipReason(explicit).isEmpty())
                return;
            File cached = disk.exact(target);
            if (cached != null)
                bitmap = BitmapFactory.decodeFile(cached.toString());
            if (bitmap == null)
            {
                stage = "sync_frame_extract";
                FrameExtractor extractor = factory.create();
                active = extractor;
                long started = clock.getAsLong();
                Frame frame;
                ScheduledFuture<?> deadline = CLOCK.schedule(extractor::cancel, 20, TimeUnit.SECONDS);
                try (FrameExtractor owned = extractor)
                {
                    frame = owned.extract(source, target);
                }
                finally
                {
                    deadline.cancel(false);
                    active = null;
                }
                long elapsed = Math.max(0, clock.getAsLong() - started);
                policy.extracted(elapsed);
                if (elapsed > 400)
                    synchronized (this)
                    {
                        backoffUntil = clock.getAsLong() + policy.intervalMs();
                        telemetry.event("thumb_skip", "reason", "slow_extraction_backoff", "interval_ms",
                                        policy.intervalMs());
                    }
                if (closed || !policy.skipReason(explicit).isEmpty())
                    return;
                bitmap = frame.bitmap;
                if (bitmap == null)
                    throw new IOException("Extractor returned no frame");
                ByteArrayOutputStream output = new ByteArrayOutputStream();
                if (!bitmap.compress(Bitmap.CompressFormat.JPEG, 70, output))
                    throw new IOException("JPEG encode failed");
                stage = "cache_write";
                byte[] jpeg = output.toByteArray();
                disk.put(target, jpeg);
                telemetry.frame(elapsed, frame.bytesRead, jpeg.length, target);
                telemetry.event("thumb_cache", "size_bytes", disk.bytes(), "evictions", disk.drainEvictions());
            }
            if (closed)
                return;
            remember(target, bitmap);
            disk.touched();
            if (request != null)
            {
                Bitmap ready = bitmap;
                main.post(() -> {
                    if (!closed)
                        for (Consumer<Bitmap> callback : request.getValue())
                            callback.accept(ready);
                });
            }
        }
        catch (Exception failure)
        {
            if (!closed)
                error(stage, failure);
            if (request != null && !closed && policy.skipReason(true).isEmpty())
                main.post(() -> {
                    if (!closed)
                        for (Consumer<Bitmap> callback : request.getValue())
                            callback.accept(null);
                });
        }
        finally
        {
            synchronized (this)
            {
                if (request != null && !closed && !policy.skipReason(true).isEmpty())
                    priority.computeIfAbsent(target, ignored -> new ArrayList<>()).addAll(request.getValue());
                busy = false;
                nextDue = clock.getAsLong() + policy.intervalMs();
            }
            tick();
        }
    }

    private long samplePosition()
    {
        try
        {
            return Math.max(
                0, Math.min(source.durationMs - 1, position.getAsLong() / policy.intervalMs() * policy.intervalMs()));
        }
        catch (RuntimeException unavailable)
        {
            error("position", unavailable);
            return 0;
        }
    }

    /** Manual watched mutations use this even after a playback session has ended. No caller-thread I/O. */
    static String ownerIdentity(Uri uri)
    {
        String scheme = uri.getScheme();
        if (scheme == null || "file".equalsIgnoreCase(scheme))
            return Uri.fromFile(new File(scheme == null ? uri.toString() : uri.getPath())).toString();
        return uri.toString();
    }

    public static void markWatched(Context context, Uri uri)
    {
        if (uri != null)
            markWatched(context, java.util.List.of(uri));
    }

    /** Bulk commands scan the cache once, regardless of library size. */
    public static void markWatched(Context context, java.util.Collection<Uri> uris)
    {
        if (uris.isEmpty())
            return;
        java.util.List<Uri> snapshot = new ArrayList<>(uris);
        PlayerService service = PlayerService.sPlayerService;
        if (service != null)
            for (Uri uri : snapshot)
                service.thumbnailMarkedWatched(uri);
        Context app = context.getApplicationContext();
        IO.execute(() -> {
            try
            {
                java.util.Set<String> owners = new java.util.HashSet<>();
                for (Uri uri : snapshot)
                    owners.add(ThumbnailDiskCache.stableKey(ownerIdentity(uri)));
                ThumbnailDiskCache.deleteOwners(new File(app.getCacheDir(), "seekthumbs"), owners);
            }
            catch (Exception failure)
            {
                Diagnostics.error("thumb_watched_cleanup_failed", failure, "media_key", "bulk", "source_kind", "mixed",
                                  "file_size_class", "unknown", "resolution", "unknown", "codec", "unknown");
            }
        });
    }

    private synchronized void remember(long positionMs, Bitmap bitmap)
    {
        if (closed)
            return;
        memory.put(positionMs, bitmap);
        lru.put(positionMs, true);
        while (lru.size() > 32)
        {
            Long oldest = lru.keySet().iterator().next();
            lru.remove(oldest);
            memory.remove(oldest);
        }
        main.post(() -> {
            if (!closed)
                for (LongConsumer listener : listeners)
                    listener.accept(positionMs);
        });
    }

    private void error(String stage, Exception failure)
    {
        telemetry.event("thumb_error", "stage", stage, "failure_class", failure.getClass().getName(), "message",
                        failure.getMessage());
        Diagnostics.error("thumb_error_trace", failure, "media_key", source.mediaKey, "source_kind", source.kind,
                          "file_size_class", source.sizeClass, "resolution", source.resolution, "codec", source.codec,
                          "stage", stage);
    }

    public synchronized void state(boolean buffering, boolean seeking, boolean foreground, boolean pip, boolean playing)
    {
        if (buffering && !this.buffering)
            telemetry.buffering(active != null);
        this.buffering = buffering;
        this.seeking = seeking;
        this.foreground = foreground;
        this.pip = pip;
        this.playing = playing;
        policy.state(SeekThumbnailPreferences.enabled(context), buffering, seeking, foreground, pip, playing);
        if (!policy.skipReason(true).isEmpty() && active != null)
            active.cancel();
        tick();
    }

    public void droppedFrames(long delta)
    {
        telemetry.dropped(delta, active != null);
    }

    public void watched()
    {
        close("watched");
        io.execute(() -> {
            try
            {
                if (disk != null)
                    disk.watched();
            }
            catch (IOException failure)
            {
                error("watched_cleanup", failure);
            }
        });
    }

    private synchronized void close(String reason)
    {
        if (closed)
            return;
        closed = true;
        PreferenceManager.getDefaultSharedPreferences(context).unregisterOnSharedPreferenceChangeListener(
            preferenceListener);
        if (ticker != null)
            ticker.cancel(false);
        if (active != null)
            active.cancel();
        priority.clear();
        memory.clear();
        lru.clear();
        listeners.clear();
        io.execute(() -> {
            telemetry.event("thumb_job_stop", "reason", reason, "interval_ms", policy.intervalMs());
            telemetry.summary(reason);
        });
    }

    @Override public void close()
    {
        close("stop");
    }
}
