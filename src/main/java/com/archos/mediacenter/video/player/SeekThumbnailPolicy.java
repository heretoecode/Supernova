package com.archos.mediacenter.video.player;

/** Thread-safe scheduling decisions; no disk, network or playback work occurs on the caller. */
public final class SeekThumbnailPolicy
{
    public static final long LOCAL_INTERVAL_MS = 10000;
    public static final long NETWORK_INTERVAL_MS = 45000;
    public static final long MAX_INTERVAL_MS = 120000;
    private long intervalMs;
    private boolean enabled = true;
    private boolean buffering;
    private boolean seeking;
    private boolean foreground = true;
    private boolean pip;
    private boolean playing = true;

    public SeekThumbnailPolicy(boolean network)
    {
        intervalMs = network ? NETWORK_INTERVAL_MS : LOCAL_INTERVAL_MS;
    }

    public synchronized long intervalMs()
    {
        return intervalMs;
    }

    public synchronized void extracted(long elapsedMs)
    {
        if (elapsedMs > 400)
            intervalMs = Math.min(MAX_INTERVAL_MS, intervalMs * 2);
    }

    public synchronized void state(boolean enabled, boolean buffering, boolean seeking, boolean foreground, boolean pip,
                                   boolean playing)
    {
        this.enabled = enabled;
        this.buffering = buffering;
        this.seeking = seeking;
        this.foreground = foreground;
        this.pip = pip;
        this.playing = playing;
    }

    public synchronized String skipReason(boolean explicit)
    {
        if (!enabled)
            return "disabled";
        if (buffering)
            return "buffering";
        if (seeking)
            return "seeking";
        if (!foreground)
            return "background";
        if (pip)
            return "pip";
        if (!explicit && !playing)
            return "paused";
        return "";
    }
}
