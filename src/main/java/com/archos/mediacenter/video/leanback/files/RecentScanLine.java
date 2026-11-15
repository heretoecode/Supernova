package com.archos.mediacenter.video.leanback.files;

import com.archos.mediacenter.video.scanning.ScanProgress;

import java.util.ArrayList;
import java.util.List;

/** Wording for one line of Library Care's Recent scans: "12 min ago · 340 checked · 2 new" and a status pill. */
public final class RecentScanLine
{
    private RecentScanLine()
    {
    }

    public static String pill(ScanProgress.Outcome outcome)
    {
        switch (outcome)
        {
            case RUNNING:
                return "Scanning";
            case STOPPED:
                return "Stopped";
            case FAILED:
                return "Failed";
            default:
                return "Done";
        }
    }

    public static String second(ScanProgress.Outcome outcome, String failureReason, long startedMs, long nowMs,
                                long checked, long added, long removed)
    {
        List<String> parts = new ArrayList<>();
        parts.add(FilesSummary.ago(startedMs, nowMs));
        if (outcome == ScanProgress.Outcome.FAILED && failureReason != null && !failureReason.isEmpty())
        {
            parts.add(failureReason);
        }
        else
        {
            parts.add(checked + " checked");
            if (added > 0)
            {
                parts.add(added + " new");
            }
            if (removed > 0)
            {
                parts.add(removed + " removed");
            }
        }
        return String.join(" · ", parts);
    }
}
