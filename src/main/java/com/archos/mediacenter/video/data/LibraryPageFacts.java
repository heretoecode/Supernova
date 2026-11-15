package com.archos.mediacenter.video.data;

import com.archos.mediacenter.video.leanback.LibraryAttentionCount;
import com.archos.mediacenter.video.leanback.LibraryStatistics;
import com.archos.mediacenter.video.scanning.ScanProgress;

/** Eight page-scoped statistics and an explicit status reason. No disk/provider work on the main thread. */
public record LibraryPageFacts(int titles, int unwatched, int inProgress, long totalBytes, int addedThisMonth,
                               int localTitles, long localBytes, int networkTitles, long networkBytes,
                               long lastUpdatedUtcMs, Status status, Reason reason)
{
    public enum Status
    {
        UNKNOWN, SCANNING, NEEDS_ATTENTION, UP_TO_DATE
    }

    public enum Reason
    {
        NEVER_SCANNED, WORKER_ACTIVE, HEALTH_ITEMS, SCAN_STOPPED, SCAN_FAILED, NONE
    }

    public static LibraryPageFacts from(LibraryStatistics values, long lastUpdatedUtcMs,
                                        LibraryAttentionCount attention, ScanProgress scan)
    {
        Status status = Status.UP_TO_DATE;
        Reason reason = Reason.NONE;
        if (scan.running)
        {
            status = Status.SCANNING;
            reason = Reason.WORKER_ACTIVE;
        }
        else if (attention.count > 0 || scan.failure != ScanProgress.Failure.NONE)
        {
            status = Status.NEEDS_ATTENTION;
            reason = attention.count > 0 ? Reason.HEALTH_ITEMS
                : scan.failure == ScanProgress.Failure.CANCELLED ? Reason.SCAN_STOPPED : Reason.SCAN_FAILED;
        }
        else if (lastUpdatedUtcMs <= 0)
        {
            status = Status.UNKNOWN;
            reason = Reason.NEVER_SCANNED;
        }
        long total = values.unknownLocal > 0 || values.unknownNetwork > 0 ||
                     Long.MAX_VALUE - values.localBytes < values.networkBytes ? -1
                     : values.localBytes + values.networkBytes;
        return new LibraryPageFacts(values.total, values.unwatched, values.inProgress, total, values.addedThisMonth,
            values.localTitles, values.unknownLocal > 0 ? -1 : values.localBytes, values.networkTitles,
            values.unknownNetwork > 0 ? -1 : values.networkBytes, lastUpdatedUtcMs, status, reason);
    }
}
