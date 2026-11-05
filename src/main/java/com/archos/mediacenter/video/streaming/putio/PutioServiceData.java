package com.archos.mediacenter.video.streaming.putio;

/** Synchronized status reducer; main-thread reads never touch encrypted storage or the network. */
final class PutioServiceData
{
    private volatile PutioServiceStatus current =
        new PutioServiceStatus(false, "", -1, -1, false, 0, PutioServiceStatus.SyncState.UNKNOWN, "");

    PutioServiceStatus current()
    {
        return current;
    }

    synchronized void signedOut()
    {
        current = new PutioServiceStatus(false, "", -1, -1, false, System.currentTimeMillis(),
                                         PutioServiceStatus.SyncState.UNKNOWN, "");
    }

    synchronized void account(PutioReadClient.Account account)
    {
        PutioServiceStatus old = current;
        String name = account.username.replaceAll("[\\p{Cntrl}]", "");
        current = new PutioServiceStatus(true, name, account.usedBytes, account.totalBytes, true,
                                         System.currentTimeMillis(), old.syncState, old.failureReason);
    }

    synchronized void syncing()
    {
        update(current.reachable, current.lastCheckedUtcMs, PutioServiceStatus.SyncState.SYNCING, "");
    }

    synchronized void complete(boolean needsReview)
    {
        update(true, System.currentTimeMillis(),
               needsReview ? PutioServiceStatus.SyncState.FAILED : PutioServiceStatus.SyncState.UP_TO_DATE,
               needsReview ? "Some library files need review" : "");
    }

    synchronized void failed(Exception failure)
    {
        PutioReconciliation.Failure reason = failure instanceof PutioReadClient.Unavailable
                                                 ? ((PutioReadClient.Unavailable)failure).reason
                                                 : PutioReconciliation.Failure.INVALID_PAGE;
        boolean cancelled = failure instanceof InterruptedException || Thread.currentThread().isInterrupted() ||
                            reason == PutioReconciliation.Failure.CANCELLED;
        String text = cancelled ? "Library sync stopped" : switch (reason)
        {
            case UNAUTHORISED -> "Sign in to Put.io again";
            case RATE_LIMITED -> "Put.io is busy; try again shortly";
            case OFFLINE -> "Put.io could not be reached";
            default -> "Library sync could not finish";
        };
        if (reason == PutioReconciliation.Failure.UNAUTHORISED)
            signedOut();
        boolean reached =
            failure instanceof PutioReadClient.Unavailable && ((PutioReadClient.Unavailable)failure).status > 0;
        update(reason == PutioReconciliation.Failure.OFFLINE ? false : reached || current.reachable,
               cancelled ? current.lastCheckedUtcMs : System.currentTimeMillis(), PutioServiceStatus.SyncState.FAILED,
               text);
    }

    private void update(boolean reachable, long checked, PutioServiceStatus.SyncState state, String reason)
    {
        PutioServiceStatus old = current;
        current = new PutioServiceStatus(old.signedIn, old.signedInAs, old.storageUsed, old.storageTotal, reachable,
                                         checked, state, reason);
    }
}
