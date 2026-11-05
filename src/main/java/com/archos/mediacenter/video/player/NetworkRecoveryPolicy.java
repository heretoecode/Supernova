package com.archos.mediacenter.video.player;

/** Pure admission policy. Every accepted recovery replaces both transport and decoder at a presentation checkpoint. */
public final class NetworkRecoveryPolicy
{
    public static final long LONG_PAUSE_MS = 60000;
    public static final long RETRY_WINDOW_MS = 30000;
    public static final int MAX_RECOVERIES = 2;
    private long pausedAt = -1, windowAt = -1;
    private int attempts;
    private boolean recovering;

    public static boolean readError(int code, int detail)
    {
        return code == -1004 || code == -110 || (code == 1 && (detail == -1004 || detail == -110)) ||
            ((code == 1001 || code == 1003) && detail == 1000);
    }

    public void paused(long now)
    {
        if (pausedAt < 0)
            pausedAt = now;
    }
    public long idleMs(long now)
    {
        return pausedAt < 0 ? 0 : Math.max(0, now - pausedAt);
    }
    public boolean longPause(long now)
    {
        return pausedAt >= 0 && idleMs(now) > LONG_PAUSE_MS;
    }
    public void resumed()
    {
        pausedAt = -1;
    }
    public boolean begin(long now)
    {
        return begin(now, true);
    }

    public boolean begin(long now, boolean readFailure)
    {
        if (recovering)
            return false;
        if (windowAt < 0 || now - windowAt > RETRY_WINDOW_MS)
        {
            windowAt = now;
            attempts = 0;
        }
        if (readFailure)
        {
            if (attempts >= MAX_RECOVERIES)
                return false;
            attempts++;
        }
        recovering = true;
        return true;
    }
    public void finished()
    {
        recovering = false;
    }
    public boolean recovering()
    {
        return recovering;
    }
    public void reset()
    {
        pausedAt = windowAt = -1;
        attempts = 0;
        recovering = false;
    }
}
