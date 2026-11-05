package com.archos.mediacenter.video.scanning;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;

/** Worker lifecycle projection; repeated source notifications replace counters instead of adding them twice. */
final class ScanProgressTracker
{
    private static final Set<String> PHASES =
        Set.of("requested", "queued", "started", "indexing", "reconciled", "partial", "complete", "failed", "cancelled",
               "coalesced", "not_started", "batch_complete", "batch_failed");
    private static final Set<String> TERMINAL = Set.of("complete", "failed", "cancelled", "coalesced", "not_started");
    private static final Set<String> FAILED = Set.of("partial", "failed", "batch_failed", "not_started");
    private static final class Source
    {
        final long started;
        ScanProgress value;
        String phase;
        long order;

        Source(long started)
        {
            this.started = started;
        }
    }

    private final Map<String, Source> sources = new LinkedHashMap<>();
    private final Map<String, Boolean> finishedBatches = new LinkedHashMap<>();
    private final Map<String, Boolean> finishedSources = new LinkedHashMap<>();
    private final Consumer<ScanProgress> finished;
    private ScanProgress current = empty();
    private boolean stopping;
    private long sequence;

    ScanProgressTracker(Consumer<ScanProgress> finished)
    {
        this.finished = finished;
    }

    static ScanProgress empty()
    {
        return new ScanProgress("", "", "", 0, -1, 0, -1, 0, 0, 0, false, false, ScanProgress.Failure.NONE);
    }

    synchronized ScanProgress current()
    {
        return current;
    }

    synchronized void discardRequest(String batch)
    {
        sources.remove(batch + ":request");
        current = aggregate(current);
    }

    synchronized void stopping()
    {
        stopping = current.running;
        current = aggregate(current);
    }

    synchronized boolean accept(String batch, String source, String label, String phase, long checked, long total,
                                long added, long removed, long updated, long utc, long elapsed,
                                ScanProgress.Failure failure)
    {
        if (batch == null || !batch.matches("[A-Za-z0-9-]{1,100}") || source == null || source.length() > 100 ||
            phase == null || !PHASES.contains(phase) || finishedBatches.containsKey(batch))
            return false;
        boolean terminalBatch = phase.startsWith("batch_");
        String key = batch + ":" + source;
        Source row = sources.get(key);
        if (row == null && finishedSources.containsKey(key))
        {
            if (terminalBatch)
            {
                closeBatch(batch, elapsed, failure);
                current = aggregate(current);
            }
            return false;
        }
        if (row != null && !row.value.running && !terminalBatch)
            return false;
        if (row == null)
        {
            sources.entrySet().removeIf(
                entry -> !entry.getValue().value.running && !entry.getValue().value.batchId.equals(batch));
            row = new Source(elapsed);
            sources.put(key, row);
        }
        boolean terminal =
            terminalBatch || TERMINAL.contains(phase) || source.equals("local") && phase.equals("partial");
        ScanProgress.Failure reason = row.value == null ? ScanProgress.Failure.NONE : row.value.failure;
        if (phase.equals("cancelled") || stopping && FAILED.contains(phase))
            reason = ScanProgress.Failure.CANCELLED;
        else if (phase.equals("coalesced"))
            reason = ScanProgress.Failure.BUSY;
        else if (FAILED.contains(phase) && reason == ScanProgress.Failure.NONE)
            reason = failure == ScanProgress.Failure.NONE ? ScanProgress.Failure.UNAVAILABLE : failure;
        boolean wasRunning = row.value == null || row.value.running;
        row.phase = phase;
        row.order = ++sequence;
        row.value = new ScanProgress(batch, source, label, Math.max(0, checked), total, added, removed, updated,
                                     row.value == null ? utc : row.value.startedUtcMs,
                                     Math.max(0, elapsed - row.started), !terminal, stopping, reason);
        if (terminal && wasRunning)
        {
            remember(finishedSources, key, 256);
            finished.accept(row.value);
        }
        if (terminalBatch)
            closeBatch(batch, elapsed, reason);
        current = aggregate(row.value);
        return true;
    }

    private void closeBatch(String batch, long elapsed, ScanProgress.Failure reason)
    {
        for (Source other : sources.values())
        {
            ScanProgress item = other.value;
            if (item.batchId.equals(batch) && item.running)
            {
                other.value = new ScanProgress(batch, item.sourceId, item.sourceLabel, item.checked, item.total,
                                               item.added, item.removed, item.updated, item.startedUtcMs,
                                               Math.max(0, elapsed - other.started), false, stopping, reason);
                remember(finishedSources, batch + ":" + item.sourceId, 256);
                finished.accept(other.value);
            }
        }
        remember(finishedBatches, batch, 32);
    }

    private static void remember(Map<String, Boolean> seen, String key, int limit)
    {
        seen.put(key, true);
        while (seen.size() > limit)
            seen.remove(seen.keySet().iterator().next());
    }

    private ScanProgress aggregate(ScanProgress latest)
    {
        long checked = 0, total = 0, added = 0, removed = 0, updated = 0;
        boolean running = false;
        ScanProgress selected = latest;
        Source selectedSource = null;
        ScanProgress.Failure failure = latest.failure;
        for (Source source : sources.values())
        {
            ScanProgress item = source.value;
            checked += item.checked;
            added = added < 0 || item.added < 0 ? -1 : added + item.added;
            updated = updated < 0 || item.updated < 0 ? -1 : updated + item.updated;
            total = total < 0 || item.total < 0 ? -1 : total + item.total;
            removed = removed < 0 || item.removed < 0 ? -1 : removed + item.removed;
            if (item.running)
            {
                running = true;
                if (selectedSource == null || priority(source) > priority(selectedSource) ||
                    priority(source) == priority(selectedSource) && source.order > selectedSource.order)
                {
                    selected = item;
                    selectedSource = source;
                }
            }
            if (item.failure != ScanProgress.Failure.NONE &&
                (failure == ScanProgress.Failure.NONE || failure == ScanProgress.Failure.UNAVAILABLE))
                failure = item.failure;
        }
        if (!running)
            stopping = false;
        return new ScanProgress(selected.batchId, selected.sourceId, selected.sourceLabel, checked, total, added,
                                removed, updated, selected.startedUtcMs, selected.durationMs, running, stopping,
                                failure);
    }

    private static int priority(Source source)
    {
        if (source.value.sourceId.equals("request"))
            return 0;
        return source.phase.equals("queued") || source.phase.equals("requested") ? 1 : 2;
    }
}
