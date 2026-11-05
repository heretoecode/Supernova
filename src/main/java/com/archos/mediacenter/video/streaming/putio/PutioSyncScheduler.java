package com.archos.mediacenter.video.streaming.putio;

import android.content.Context;
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
        start(context, "manual", false);
    }
    public static void retry(Context context)
    {
        start(context, "manual", true);
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
        start(context, origin, true);
    }
    private static synchronized void start(Context context, String origin, boolean sync)
    {
        String trigger =
            Arrays.asList("manual", "startup", "resume", "scheduled").contains(origin) ? origin : "unknown";
        if (!enabled(context) || !BUSY.compareAndSet(false, true))
            return;
        cancelled = false;
        Context app = context.getApplicationContext();
        LibraryScanController.install(app);
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
                        sync(app, trigger, client);
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
                synchronized (PutioSyncScheduler.class)
                {
                    activeClient = null;
                    activeThread = null;
                    BUSY.set(false);
                }
            }
        });
    }
    /** Runs on the owning worker so transport operations inherit this sync's correlation. */
    static void sync(Context app, String trigger, PutioReadClient client)
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
            int synced = 0, review = 0;
            for (PutioAssociationStore.Scope scope : scopes)
            {
                checkInterrupted();
                if (scope.ownership != PutioAssociationStore.Ownership.API)
                    continue;
                String batch = UUID.randomUUID().toString();
                LibraryScanController.sourceName(scope.source, "Put.io › " + (scope.source.getLastPathSegment() == null
                                                                                  ? "Files"
                                                                                  : scope.source.getLastPathSegment()));
                LibraryScanController.sourceEvent(batch, scope.source, "started", 0, -1, 0, 0, 0, null);
                PutioSync.Review result;
                try
                {
                    PutioSync.Review fetched = PutioSync.fetch(app, client, account.id, scope.folderId, scope.source);
                    checkInterrupted();
                    int count = fetched.snapshot.files().size();
                    LibraryScanController.sourceEvent(batch, scope.source, "indexing", count, count, 0, 0, 0, null);
                    result = PutioSync.apply(app, fetched, Collections.emptyMap());
                    checkInterrupted();
                    LibraryScanController.sourceEvent(batch, scope.source, "complete", count, count,
                                                      result.newlyImported(), 0, -1, null);
                }
                catch (Exception failure)
                {
                    LibraryScanController.sourceEvent(batch, scope.source,
                                                      Thread.currentThread().isInterrupted() ? "cancelled" : "failed",
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
