package com.archos.mediacenter.video.leanback.filebrowsing;

import com.archos.mediacenter.video.streaming.putio.PutioServiceStatus;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/** Plain words for the Put.io row on Network Shares (owner notes 0.154 #14, mock-up network-shares/01). */
public final class PutioServiceRow
{
    private PutioServiceRow()
    {
    }

    public static String title(PutioServiceStatus status)
    {
        if (status == null || !status.signedIn)
            return "Put.io";
        if (status.signedInAs == null || status.signedInAs.isEmpty())
            return "Put.io";
        return "Put.io · " + status.signedInAs;
    }

    public static String summary(PutioServiceStatus status, long nowUtcMs)
    {
        if (status == null || !status.signedIn)
            return "Not signed in";
        List<String> parts = new ArrayList<>();
        if (status.storageUsed >= 0 && status.storageTotal > 0)
            parts.add(size(status.storageUsed) + " of " + size(status.storageTotal) + " used");
        if (!status.reachable && status.lastCheckedUtcMs > 0)
            parts.add("Not reachable");
        String sync = sync(status);
        if (!sync.isEmpty())
            parts.add(sync);
        parts.add(checked(status.lastCheckedUtcMs, nowUtcMs));
        return String.join(" · ", parts);
    }

    /** Retry is offered only when something went wrong. */
    public static boolean offersRetry(PutioServiceStatus status)
    {
        if (status == null || !status.signedIn)
            return false;
        boolean unreachable = !status.reachable && status.lastCheckedUtcMs > 0;
        return unreachable || status.syncState == PutioServiceStatus.SyncState.FAILED;
    }

    private static String sync(PutioServiceStatus status)
    {
        switch (status.syncState) {
            case UP_TO_DATE:
                return "Library up to date";
            case SYNCING:
                return "Library syncing…";
            case FAILED:
                String reason = status.failureReason;
                if (reason == null || reason.isEmpty())
                    return "Library sync failed";
                return "Library sync failed: " + reason;
            default:
                return "";
        }
    }

    static String checked(long checkedUtcMs, long nowUtcMs)
    {
        if (checkedUtcMs <= 0)
            return "Not checked yet";
        long minutes = Math.max(0, nowUtcMs - checkedUtcMs) / 60_000L;
        if (minutes < 1)
            return "checked just now";
        if (minutes < 60)
            return "checked " + minutes + " min ago";
        long hours = minutes / 60;
        if (hours < 24)
            return "checked " + hours + (hours == 1 ? " hour ago" : " hours ago");
        long days = hours / 24;
        return "checked " + days + (days == 1 ? " day ago" : " days ago");
    }

    static String size(long bytes)
    {
        double gb = bytes / 1_000_000_000d;
        if (gb >= 1000)
            return String.format(Locale.UK, "%.1f TB", gb / 1000);
        if (gb >= 100)
            return String.format(Locale.UK, "%.0f GB", gb);
        return String.format(Locale.UK, "%.1f GB", gb);
    }
}
