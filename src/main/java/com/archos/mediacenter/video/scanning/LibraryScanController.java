package com.archos.mediacenter.video.scanning;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Build;
import android.os.SystemClock;
import com.archos.mediacenter.video.diagnostics.Diagnostics;
import com.archos.mediaprovider.video.LibraryScanControl;
import com.archos.mediacenter.video.streaming.putio.PutioSyncScheduler;
import java.util.Locale;
import java.util.List;
import java.io.File;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;

/** Process-owned scan data and cancellation. progress() performs no I/O and is safe on the main thread. */
public final class LibraryScanController
{
    static final ExecutorService IO = Executors.newSingleThreadExecutor(r -> {
        Thread thread = new Thread(r, "LibraryScanData");
        thread.setDaemon(true);
        return thread;
    });
    private static final ScanSourceLabels LABELS = new ScanSourceLabels();
    private static final ExecutorService CONTROL = Executors.newSingleThreadExecutor(r -> {
        Thread thread = new Thread(r, "LibraryScanStop");
        thread.setDaemon(true);
        return thread;
    });
    private static volatile ScanProgressTracker tracker = new ScanProgressTracker(LibraryScanController::finished);
    private static final AtomicBoolean STOP_PENDING = new AtomicBoolean();
    private record SourceDescription(String kind, String location)
    {
    }
    private static final Map<String, SourceDescription> DESCRIPTIONS = new ConcurrentHashMap<>();
    private static Context application;
    private static BroadcastReceiver receiver;
    private static String requested = "";
    private static volatile ScanHistoryStore history;

    public static synchronized void install(Context context)
    {
        Context app = context.getApplicationContext();
        if (application == app)
            return;
        if (application != null)
        {
            try
            {
                application.unregisterReceiver(receiver);
            }
            catch (IllegalArgumentException alreadyRemoved)
            {
                // A test/runtime may have destroyed the previous Application's receiver registry.
            }
            tracker = new ScanProgressTracker(LibraryScanController::finished);
        }
        application = app;
        ScanHistoryStore store = new ScanHistoryStore(new File(app.getNoBackupFilesDir(), "scan-history-v1"));
        history = store;
        IO.execute(() -> {
            try
            {
                store.load();
            }
            catch (Exception failure)
            {
                Diagnostics.error("scan_history_load_failed", failure);
            }
        });
        receiver = new BroadcastReceiver() {
            @Override public void onReceive(Context context, Intent event)
            {
                accept(event);
            }
        };
        IntentFilter filter = new IntentFilter(application.getPackageName() + ".SCAN_LIFECYCLE");
        filter.addAction(application.getPackageName() + ".LOCAL_IMPORT_LIFECYCLE");
        if (Build.VERSION.SDK_INT >= 33)
            application.registerReceiver(receiver, filter, Context.RECEIVER_NOT_EXPORTED);
        else
            application.registerReceiver(receiver, filter);
        com.archos.mediaprovider.video.SavedConnectionRemoval.resume(app);
        refreshSourceNames();
    }

    /** Most recent ten finished source scans, newest first; immutable and memory-only. */
    public static List<ScanProgress> recentScans()
    {
        ScanHistoryStore store = history;
        return store == null ? List.of() : store.recent();
    }

    private static void finished(ScanProgress value)
    {
        ScanHistoryStore store = history;
        if (store == null)
            return;
        IO.execute(() -> {
            try
            {
                store.append(value);
            }
            catch (Exception failure)
            {
                Diagnostics.error("scan_history_write_failed", failure);
            }
        });
    }

    /** Asynchronous source-name refresh; no view, Activity or renderer is retained. */
    public static synchronized void refreshSourceNames()
    {
        if (application == null)
            return;
        Context context = application;
        IO.execute(() -> {
            try
            {
                LABELS.refresh(context);
            }
            catch (Exception failure)
            {
                Diagnostics.error("scan_source_names_failed", failure);
            }
        });
    }

    static synchronized void accept(Intent event)
    {
        String batch = event.getStringExtra("batch_id");
        String source = event.getStringExtra("source_id");
        String kind = event.getStringExtra("source_kind");
        if ("local".equals(kind))
            source = "local";
        if (source == null || !(source.equals("local") || source.matches("[a-f0-9]{24}")))
            return;
        String phase = event.getStringExtra("phase");
        String previousBatch = tracker.current().batchId;
        String location = event.getStringExtra("source_location");
        if (location != null && location.length() > 4096)
            location = null;
        String label = LABELS.label(source, kind, location);
        boolean accepted =
            tracker.accept(batch, source, label, phase, number(event, "checked", 0), number(event, "total", -1),
                           number(event, "added", 0), number(event, "removed", -1), number(event, "updated", 0),
                           System.currentTimeMillis(), SystemClock.elapsedRealtime(),
                           failure(event.getStringExtra("failure_class"), event.getStringExtra("failure_stage")));
        if (accepted)
        {
            DESCRIPTIONS.put(batch + ":" + source, new SourceDescription(kind, location));
            if (DESCRIPTIONS.size() > 256)
                DESCRIPTIONS.remove(DESCRIPTIONS.keySet().iterator().next());
        }
        if (accepted && !requested.isEmpty() && !String.valueOf(batch).equals(previousBatch) &&
            ("queued".equals(phase) || "started".equals(phase)))
        {
            tracker.discardRequest(requested);
            requested = "";
        }
    }

    private static long number(Intent event, String name, long unknown)
    {
        Object value = event.getExtras() == null ? null : event.getExtras().get(name);
        return value instanceof Number ? ((Number)value).longValue() : unknown;
    }

    static ScanProgress.Failure failure(String type, String stage)
    {
        String evidence = (String.valueOf(type) + " " + stage).toLowerCase(Locale.ROOT);
        if (evidence.contains("cancel"))
            return ScanProgress.Failure.CANCELLED;
        if (evidence.contains("certificate") || evidence.contains("ssl"))
            return ScanProgress.Failure.CERTIFICATE;
        if (evidence.contains("authentication") || evidence.contains("unauthorised"))
            return ScanProgress.Failure.SIGN_IN;
        if (evidence.contains("security") || evidence.contains("accessdenied"))
            return ScanProgress.Failure.PERMISSION;
        if (evidence.contains("timeout") || evidence.contains("deadline"))
            return ScanProgress.Failure.TIMEOUT;
        if (evidence.contains("unknownhost") || evidence.contains("connectexception"))
            return ScanProgress.Failure.ADDRESS;
        if (evidence.contains("sqlite") || evidence.contains("storage"))
            return ScanProgress.Failure.STORAGE;
        return ScanProgress.Failure.UNAVAILABLE;
    }

    public static ScanProgress progress()
    {
        ScanProgress value = tracker.current();
        SourceDescription description = DESCRIPTIONS.get(value.batchId + ":" + value.sourceId);
        if (description != null)
        {
            String label = LABELS.label(value.sourceId, description.kind, description.location);
            if (!label.equals(value.sourceLabel))
                value = new ScanProgress(value.batchId, value.sourceId, label, value.checked, value.total, value.added,
                                         value.removed, value.updated, value.startedUtcMs, value.durationMs,
                                         value.running, value.stopping, value.failure);
        }
        if (!STOP_PENDING.get())
            return value;
        return new ScanProgress(value.batchId, value.sourceId, value.sourceLabel, value.checked, value.total,
                                value.added, value.removed, value.updated, value.startedUtcMs, value.durationMs, true,
                                true, value.failure);
    }

    /** Provider worker adapter; bounded safe reason codes only, never exception messages. */
    public static void sourceEvent(String batch, Uri source, String phase, long checked, long total, long added,
                                   long removed, long updated, String reason)
    {
        try
        {
            accept(new Intent()
                       .putExtra("batch_id", batch)
                       .putExtra("source_id", ScanSourceLabels.id(source.toString()))
                       .putExtra("source_kind", "putio")
                       .putExtra("phase", phase)
                       .putExtra("checked", checked)
                       .putExtra("total", total)
                       .putExtra("added", added)
                       .putExtra("removed", removed)
                       .putExtra("updated", updated)
                       .putExtra("failure_class", reason));
        }
        catch (java.security.GeneralSecurityException invalid)
        {
            throw new IllegalArgumentException("Invalid source identity", invalid);
        }
    }

    /** Record the scheduler acknowledgement window without claiming a source or a known file total. */
    public static synchronized void requested(String operationId)
    {
        requested = operationId;
        tracker.accept(operationId, "request", "Library", "queued", 0, -1, 0, -1, 0, System.currentTimeMillis(),
                       SystemClock.elapsedRealtime(), ScanProgress.Failure.NONE);
    }

    public static synchronized void notStarted(String operationId, boolean unreachable)
    {
        if (!requested.equals(operationId))
            return;
        tracker.accept(requested, "request", "Library", "not_started", 0, -1, 0, -1, 0, System.currentTimeMillis(),
                       SystemClock.elapsedRealtime(),
                       unreachable ? ScanProgress.Failure.ADDRESS : ScanProgress.Failure.UNAVAILABLE);
        requested = "";
    }

    private static synchronized void stoppedRequest()
    {
        if (!requested.isEmpty())
        {
            tracker.accept(requested, "request", "Library", "cancelled", 0, -1, 0, -1, 0, System.currentTimeMillis(),
                           SystemClock.elapsedRealtime(), ScanProgress.Failure.CANCELLED);
            requested = "";
        }
    }

    /** Optional source-list cache update; sourceId is the lifecycle source ID, never an address or credential. */
    public static void sourceName(String sourceId, String friendlyName)
    {
        LABELS.remember(sourceId, friendlyName);
    }

    /** Use the indexed root URI from a source-list model; the locator itself is never exposed by progress(). */
    public static void sourceName(Uri root, String friendlyName)
    {
        try
        {
            LABELS.remember(ScanSourceLabels.id(root.toString()), friendlyName);
        }
        catch (Exception unavailable)
        {
            throw new IllegalArgumentException("Invalid source identity", unavailable);
        }
    }

    /** Request cancellation; running remains true until the worker's terminal lifecycle event. */
    public static synchronized void stop()
    {
        if (application == null || !STOP_PENDING.compareAndSet(false, true))
            return;
        tracker.stopping();
        Context context = application;
        CONTROL.execute(() -> {
            try
            {
                PutioSyncScheduler.stop();
                LibraryScanControl.stop(context);
            }
            catch (RuntimeException failure)
            {
                Diagnostics.error("scan_stop_failed", failure);
            }
            finally
            {
                stoppedRequest();
                STOP_PENDING.set(false);
            }
        });
    }

    private LibraryScanController()
    {
    }
}
