package com.archos.mediacenter.video.streaming.putio;

import android.content.Context;
import android.net.Uri;
import com.archos.mediaprovider.video.SupernovaLibraryPolicy;
import java.util.ArrayList;
import com.archos.mediacenter.video.scanning.ScanActivitySignal;
import android.os.SystemClock;
import androidx.preference.PreferenceManager;
import com.archos.mediacenter.video.diagnostics.Diagnostics;
import com.archos.mediacenter.video.streaming.StreamingRepository;
import java.util.Arrays;
import java.util.List;
import java.util.Collections;
import java.util.UUID;
import com.archos.mediacenter.video.scanning.LibraryScanController;
import com.archos.mediaprovider.video.ScanSchedulePolicy;
import java.util.concurrent.atomic.AtomicBoolean;

/** Uses the existing manual/foreground scan schedule, without Activity ownership or new timers. */
public final class PutioSyncScheduler
{
    private static final AtomicBoolean BUSY = new AtomicBoolean();
    private static final PutioServiceData STATUS = new PutioServiceData();
    private static volatile PutioReadClient activeClient;
    private static volatile Thread activeThread;
    private static boolean cancelled;

    public static PutioServiceStatus status()
    {
        return STATUS.current();
    }
    public static void refresh(Context context)
    {
        start(context, "manual", false, null);
    }
    public static void retry(Context context)
    {
        start(context, "manual", true, null);
    }
    public static synchronized void stop()
    {
        if (!BUSY.get())
            return;
        cancelled = true;
        if (activeClient != null)
            activeClient.cancel();
        if (activeThread != null)
            activeThread.interrupt();
    }
    public static void request(Context context)
    {
        request(context, "unknown");
    }
    public static void request(Context context, String origin)
    {
        start(context, origin, true, null);
    }
    /** Main-thread safe: only the exact saved API-owned source is admitted; no account/folder guessing. */
    public static void requestSource(Context context, Uri source)
    {
        if (source == null)
            throw new IllegalArgumentException("Put.io source required");
        start(context, "manual", true, source);
    }

    private static synchronized void start(Context context, String origin, boolean sync, Uri source)
    {
        String trigger =
            Arrays.asList("manual", "startup", "resume", "scheduled").contains(origin) ? origin : "unknown";
        if (!enabled(context) || !BUSY.compareAndSet(false, true))
            return;
        cancelled = false;
        ScanActivitySignal.LIBRARY.set("putio_sync", sync);
        Context app = context.getApplicationContext();
        LibraryScanController.install(app);
        SourceScan chosen = source == null ? null : new SourceScan(source);
        try
        {
            StreamingRepository.IO.submit(() -> {
                try
                {
                    synchronized (PutioSyncScheduler.class)
                    {
                        activeThread = Thread.currentThread();
                        if (cancelled)
                            throw new InterruptedException();
                    }
                    String token = new PutioTokenStore(app).read();
                    if (token == null)
                    {
                        STATUS.signedOut();
                        return;
                    }
                    try (PutioReadClient client = new PutioReadClient(token))
                    {
                        synchronized (PutioSyncScheduler.class)
                        {
                            activeClient = client;
                            if (cancelled)
                                throw new InterruptedException();
                        }
                        if (sync)
                            sync(app, trigger, client, source, chosen);
                        else
                            STATUS.account(client.account());
                    }
                }
                catch (Exception failure)
                {
                    STATUS.failed(failure);
                    Diagnostics.event("putio_sync_incomplete", "reason", "LOCAL_UNAVAILABLE", "trigger", trigger);
                }
                finally
                {
                    if (chosen != null)
                        chosen.close();
                    synchronized (PutioSyncScheduler.class)
                    {
                        activeClient = null;
                        activeThread = null;
                        BUSY.set(false);
                        ScanActivitySignal.LIBRARY.set("putio_sync", false);
                    }
                }
            });
        }
        catch (RuntimeException unavailable)
        {
            BUSY.set(false);
            ScanActivitySignal.LIBRARY.set("putio_sync", false);
            STATUS.failed(unavailable);
            if (chosen != null)
                chosen.close();
        }
    }
    /** Runs on the owning worker so transport operations inherit this sync's correlation. */
    static void sync(Context app, String trigger, PutioReadClient client)
    {
        sync(app, trigger, client, null);
    }

    static void sync(Context app, String trigger, PutioReadClient client, Uri selected)
    {
        SourceScan chosen = selected == null ? null : new SourceScan(selected);
        try
        {
            sync(app, trigger, client, selected, chosen);
        }
        finally
        {
            if (chosen != null)
                chosen.close();
        }
    }

    private static void sync(Context app, String trigger, PutioReadClient client, Uri selected, SourceScan chosen)
    {
        if (!enabled(app))
            return;
        long started = SystemClock.elapsedRealtime();
        String operation = Diagnostics.operation("putio_sync");
        try (Diagnostics.OperationScope ignored = Diagnostics.operationScope(operation))
        {
            STATUS.syncing();
            Diagnostics.event("putio_sync_started", "operation_id", operation, "trigger", trigger);
            checkInterrupted();
            PutioReadClient.Account account = client.account();
            checkInterrupted();
            STATUS.account(account);
            List<PutioAssociationStore.Scope> scopes;
            try (PutioAssociationStore store = new PutioAssociationStore(app))
            {
                scopes = store.scopes(account.id);
            }
            if (selected != null)
            {
                scopes = selectScopes(scopes, selected);
                if (scopes.isEmpty())
                    throw new IllegalArgumentException("Put.io source is not saved with API ownership");
            }
            int synced = 0, review = 0;
            for (PutioAssociationStore.Scope scope : scopes)
            {
                checkInterrupted();
                if (scope.ownership != PutioAssociationStore.Ownership.API)
                    continue;
                SourceScan scan = chosen == null ? new SourceScan(scope.source) : chosen;
                LibraryScanController.sourceName(scope.source, "Put.io › " + (scope.source.getLastPathSegment() == null
                                                                                  ? "Files"
                                                                                  : scope.source.getLastPathSegment()));
                scan.event("started", 0, -1, 0, 0, 0, null);
                PutioSync.Review result;
                try
                {
                    PutioSync.Review fetched = PutioSync.fetch(app, client, account.id, scope.folderId, scope.source);
                    checkInterrupted();
                    int count = fetched.snapshot.files().size();
                    scan.event("indexing", count, count, 0, 0, 0, null);
                    result = PutioSync.apply(app, fetched, Collections.emptyMap());
                    checkInterrupted();
                    scan.event("complete", count, count, result.newlyImported(), 0, -1, null);
                }
                catch (Exception failure)
                {
                    scan.event(Thread.currentThread().isInterrupted() ? "cancelled" : "failed",
                                                      0, -1, -1, -1, -1,
                                                      failure instanceof PutioReadClient.Unavailable
                                                          ? ((PutioReadClient.Unavailable)failure).reason.name()
                                                          : "unavailable");
                    throw failure;
                }
                if (!result.plan.missingMediaIds.isEmpty())
                    review += result.plan.missingMediaIds.size();
                synced++;
                for (PutioReconciliation.Change change : result.plan.changes)
                    if (change.decision == PutioReconciliation.Decision.NEEDS_REVIEW)
                        review++;
            }
            checkInterrupted();
            STATUS.complete(review > 0);
            if (synced > 0)
                ScanSchedulePolicy.completed(app);
            Diagnostics.event("putio_sync_complete", "operation_id", operation, "scopes", synced, "review", review);
        }
        catch (Exception failure)
        {
            STATUS.failed(failure);
            if (failure instanceof InterruptedException || Thread.currentThread().isInterrupted())
            {
                Thread.currentThread().interrupt();
                Diagnostics.event("putio_sync_cancelled", "operation_id", operation, "trigger", trigger);
            }
            else
            {
                String reason = failure instanceof PutioReadClient.Unavailable
                                    ? ((PutioReadClient.Unavailable)failure).reason.name()
                                    : "LOCAL_UNAVAILABLE";
                Diagnostics.event("putio_sync_incomplete", "operation_id", operation, "reason", reason);
            }
        }
        finally
        {
            Diagnostics.finishOperation(operation, "putio_sync", started);
        }
    }
    static List<PutioAssociationStore.Scope> selectScopes(List<PutioAssociationStore.Scope> saved,
                                                         Uri selected)
    {
        ArrayList<PutioAssociationStore.Scope> result = new ArrayList<>();
        String key = SupernovaLibraryPolicy.canonical(selected);
        for (PutioAssociationStore.Scope scope : saved)
            if (scope.ownership == PutioAssociationStore.Ownership.API &&
                SupernovaLibraryPolicy.canonical(scope.source).equals(key))
                result.add(scope);
        return List.copyOf(result);
    }

    /** One selected source owns its terminal event even if account lookup/token/queue admission fails. */
    private static final class SourceScan implements AutoCloseable
    {
        private final Uri source;
        private final String batch = UUID.randomUUID().toString();
        private boolean terminal;

        SourceScan(Uri source)
        {
            this.source = source;
            event("queued", 0, -1, 0, 0, 0, null);
        }

        synchronized void event(String phase, long checked, long total, long added, long removed, long updated,
                                String reason)
        {
            if (terminal)
                return;
            terminal = phase.equals("complete") || phase.equals("cancelled") || phase.equals("failed");
            LibraryScanController.sourceEvent(batch, source, phase, checked, total, added, removed, updated, reason);
        }

        @Override public void close()
        {
            event(Thread.currentThread().isInterrupted() ? "cancelled" : "failed", 0, -1, -1, -1, -1,
                  "unavailable");
        }
    }

    private static boolean enabled(Context context)
    {
        return PreferenceManager.getDefaultSharedPreferences(context).getBoolean("supernova_putio_enabled", true);
    }
    private static void checkInterrupted() throws InterruptedException
    {
        if (Thread.currentThread().isInterrupted())
            throw new InterruptedException();
    }
    private PutioSyncScheduler()
    {
    }
}
