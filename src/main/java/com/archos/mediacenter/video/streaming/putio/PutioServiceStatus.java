package com.archos.mediacenter.video.streaming.putio;

/** Safe display data only. No token, URL, cursor, request or response body is retained here. */
public final class PutioServiceStatus
{
    public enum SyncState
    {
        UNKNOWN,
        UP_TO_DATE,
        SYNCING,
        FAILED
    }

    public final boolean signedIn;
    public final String signedInAs;
    public final long storageUsed;
    public final long storageTotal;
    public final boolean reachable;
    public final long lastCheckedUtcMs;
    public final SyncState syncState;
    public final String failureReason;

    PutioServiceStatus(boolean signedIn, String name, long used, long total, boolean reachable, long checked,
                       SyncState syncState, String failureReason)
    {
        this.signedIn = signedIn;
        signedInAs = name;
        storageUsed = used;
        storageTotal = total;
        this.reachable = reachable;
        lastCheckedUtcMs = checked;
        this.syncState = syncState;
        this.failureReason = failureReason;
    }
}
