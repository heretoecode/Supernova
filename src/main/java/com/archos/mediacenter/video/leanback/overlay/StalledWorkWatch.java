package com.archos.mediacenter.video.leanback.overlay;

/**
 * Notices work that claims to be running but has made no progress for a while, so a status pill does not sit on
 * screen for ever (owner report: "Scanning local library…" stayed for more than ten minutes).
 */
public final class StalledWorkWatch
{
    private final long limitMs;
    private boolean active;
    private String lastProgress = "";
    private long lastChangeAt;
    private boolean reported;

    public StalledWorkWatch(long limitMs)
    {
        if (limitMs <= 0)
        {
            throw new IllegalArgumentException("The limit must be positive");
        }
        this.limitMs = limitMs;
    }

    /**
     * Call regularly. Returns true while the work has been active with the same progress for longer than the limit.
     *
     * @param working  whether the work says it is running
     * @param progress anything that changes as the work moves on (counts, phase)
     * @param nowMs    a clock that never goes backwards
     */
    public synchronized boolean stalled(boolean working, String progress, long nowMs)
    {
        String current = progress == null ? "" : progress;
        if (!working)
        {
            active = false;
            reported = false;
            return false;
        }
        if (!active || !current.equals(lastProgress))
        {
            active = true;
            lastProgress = current;
            lastChangeAt = nowMs;
            reported = false;
            return false;
        }
        return nowMs - lastChangeAt >= limitMs;
    }

    /** True once per stall, for logging it a single time. */
    public synchronized boolean firstReport()
    {
        if (reported)
        {
            return false;
        }
        reported = true;
        return true;
    }
}
