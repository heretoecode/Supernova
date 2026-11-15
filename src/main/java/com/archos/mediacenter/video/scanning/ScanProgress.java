package com.archos.mediacenter.video.scanning;

/** Immutable scan data. Counts of -1 are unknown; running stays true while cancelled workers unwind. */
public final class ScanProgress
{
    public enum Outcome
    {
        RUNNING, FINISHED, STOPPED, FAILED
    }

    public Outcome outcome()
    {
        if (running)
            return Outcome.RUNNING;
        if (failure == Failure.CANCELLED)
            return Outcome.STOPPED;
        return failure == Failure.NONE ? Outcome.FINISHED : Outcome.FAILED;
    }

    public enum Failure
    {
        NONE,
        CANCELLED,
        BUSY,
        ADDRESS,
        SIGN_IN,
        PERMISSION,
        CERTIFICATE,
        TIMEOUT,
        STORAGE,
        UNAVAILABLE
    }

    public final String batchId;
    public final String sourceId;
    public final String sourceLabel;
    public final long checked;
    public final long total;
    public final long added;
    public final long removed;
    public final long updated;
    public final long startedUtcMs;
    public final long durationMs;
    public final boolean running;
    public final boolean stopping;
    public final Failure failure;

    ScanProgress(String batchId, String sourceId, String sourceLabel, long checked, long total, long added,
                 long removed, long updated, long startedUtcMs, long durationMs, boolean running, boolean stopping,
                 Failure failure)
    {
        this.batchId = batchId;
        this.sourceId = sourceId;
        this.sourceLabel = sourceLabel;
        this.checked = checked;
        this.total = total;
        this.added = added;
        this.removed = removed;
        this.updated = updated;
        this.startedUtcMs = startedUtcMs;
        this.durationMs = durationMs;
        this.running = running;
        this.stopping = stopping;
        this.failure = failure;
    }

    /** Safe short reason, derived from failure type/stage rather than private exception messages. */
    public String failureReason()
    {
        return switch (failure)
        {
            case NONE -> "";
            case CANCELLED -> "Scan stopped";
            case BUSY -> "A scan was already running";
            case ADDRESS -> "Source address could not be reached";
            case SIGN_IN -> "Source sign-in failed";
            case PERMISSION -> "Permission to read the source was denied";
            case CERTIFICATE -> "Source certificate could not be verified";
            case TIMEOUT -> "Source did not respond in time";
            case STORAGE -> "Library storage could not be updated";
            case UNAVAILABLE -> "Source scan could not finish";
        };
    }
}
