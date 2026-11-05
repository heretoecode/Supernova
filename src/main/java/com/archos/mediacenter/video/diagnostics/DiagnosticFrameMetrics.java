package com.archos.mediacenter.video.diagnostics;

import java.util.function.BiConsumer;

/** Frame callback deltas are UI scheduling evidence, not decoded-video dropped-frame counters. */
public final class DiagnosticFrameMetrics
{
    private final String screen;
    private final BiConsumer<String, Object[]> sink;
    private long previous, frames, slow, longest, started;

    public DiagnosticFrameMetrics(String screen, long openedNanos, BiConsumer<String, Object[]> sink)
    {
        this.screen = screen;
        this.started = openedNanos;
        this.sink = sink;
    }

    public void frame(long nanos)
    {
        if (previous > 0 && nanos > previous)
        {
            long delta = nanos - previous;
            frames++;
            if (delta > 32_000_000)
                slow++;
            longest = Math.max(longest, delta);
        }
        previous = nanos;
        if (nanos - started >= 60_000_000_000L)
            flush(nanos, "minute");
    }

    public void flush(long nanos, String reason)
    {
        sink.accept("ui_frame_summary", new Object[] {"screen", screen, "frames", frames, "slow_frames", slow,
                                                      "threshold_ms", 32, "longest_ms", longest / 1_000_000.0,
                                                      "window_ms", Math.max(0, nanos - started) / 1_000_000, "reason",
                                                      reason, "measurement", "choreographer_callback_delta"});
        started = nanos;
        frames = slow = longest = previous = 0;
    }
}
