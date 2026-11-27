package com.archos.mediacenter.video.scanning;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * Scan details › Step by step (FILES.md, `tv/24`): each scan's transcript, times to the second, built from the
 * progress events the scanner already sends: started, listing (every quarter), the missing check, finished with its
 * totals, any failure with its reason. Kept in memory for the last ten scans.
 */
public final class ScanTranscript
{
    private static final int SCANS = 10;
    private static final int LINES = 60;
    private static final Map<String, List<Line>> SCRIPTS = new LinkedHashMap<>();

    private ScanTranscript()
    {
    }

    /** One step: its time, words and whether it went wrong (shown in soft red). */
    public static final class Line
    {
        public final long utc;
        public final String words;
        public final boolean failed;
        final String phase;
        final long quarter;

        Line(long utc, String words, boolean failed, String phase, long quarter)
        {
            this.utc = utc;
            this.words = words;
            this.failed = failed;
            this.phase = phase;
            this.quarter = quarter;
        }

        /** "18:24:07  Listing files · 120 of 450". */
        public String text()
        {
            return new SimpleDateFormat("HH:mm:ss", Locale.UK).format(new Date(utc)) + "  " + words;
        }
    }

    static synchronized void record(ScanProgress scan, String phase, long utc)
    {
        String key = scan.batchId + ":" + scan.sourceId;
        List<Line> lines = SCRIPTS.get(key);
        if (lines == null)
        {
            lines = new ArrayList<>();
            SCRIPTS.put(key, lines);
            while (SCRIPTS.size() > SCANS)
                SCRIPTS.remove(SCRIPTS.keySet().iterator().next());
        }
        long quarter = scan.total > 0 ? Math.min(4, scan.checked * 4 / scan.total) : 0;
        Line last = lines.isEmpty() ? null : lines.get(lines.size() - 1);
        if (last != null && last.phase.equals(phase) && (!phase.equals("indexing") || last.quarter == quarter))
            return;
        if (lines.size() >= LINES)
            return;
        lines.add(new Line(utc, words(scan, phase), failed(phase, scan), phase, quarter));
    }

    /** The scan's steps, oldest first; empty when it is no longer kept. */
    public static synchronized List<Line> lines(String batchId, String sourceId)
    {
        List<Line> lines = SCRIPTS.get(batchId + ":" + sourceId);
        return lines == null ? new ArrayList<>() : new ArrayList<>(lines);
    }

    static synchronized void clear()
    {
        SCRIPTS.clear();
    }

    static String words(ScanProgress scan, String phase)
    {
        switch (phase)
        {
        case "requested":
            return "Asked to scan";
        case "queued":
            return "Waiting for its turn";
        case "started":
            return "Started";
        case "indexing":
            return scan.total > 0 ? "Listing files · " + scan.checked + " of " + scan.total + " checked"
                : "Listing files · " + scan.checked + " checked";
        case "reconciled":
            return "Checked for missing files · " + Math.max(0, scan.removed) + " missing";
        case "partial":
            return "Stopped part way · " + scan.failureReason();
        case "complete":
        case "batch_complete":
            return "Finished · " + Math.max(0, scan.added) + " new · " + Math.max(0, scan.updated) + " updated · "
                + Math.max(0, scan.removed) + " missing";
        case "failed":
        case "batch_failed":
            return "Failed · " + scan.failureReason();
        case "cancelled":
            return "Stopped";
        case "coalesced":
            return "Joined the scan already running";
        case "not_started":
            return "Didn't start · " + scan.failureReason();
        default:
            return phase;
        }
    }

    private static boolean failed(String phase, ScanProgress scan)
    {
        return phase.equals("failed") || phase.equals("batch_failed") || phase.equals("not_started")
            || phase.equals("partial") && scan.failure != ScanProgress.Failure.CANCELLED;
    }
}
