package com.archos.mediacenter.video.diagnostics;

import android.view.Choreographer;

/** Main-thread lifecycle adapter. Holds a screen label, never an Activity or view. */
final class DiagnosticFrameSampler implements Choreographer.FrameCallback, AutoCloseable
{
    private final DiagnosticFrameMetrics metrics;
    private boolean closed;

    DiagnosticFrameSampler(String screen)
    {
        metrics = new DiagnosticFrameMetrics(screen, System.nanoTime(), Diagnostics::event);
        Choreographer.getInstance().postFrameCallback(this);
    }

    @Override public void doFrame(long nanos)
    {
        if (closed)
            return;
        metrics.frame(nanos);
        Choreographer.getInstance().postFrameCallback(this);
    }

    @Override public void close()
    {
        closed = true;
        Choreographer.getInstance().removeFrameCallback(this);
        metrics.flush(System.nanoTime(), "screen_paused");
    }
}
