package com.archos.mediacenter.video.diagnostics;

import android.os.Build;
import android.view.Choreographer;

/** Main-thread lifecycle adapter. Holds a screen label, never an Activity or view. */
final class DiagnosticFrameSampler implements Choreographer.FrameCallback, AutoCloseable
{
    private final DiagnosticFrameMetrics metrics;
    /**
     * Under Robolectric's paused looper a callback that re-posts itself every frame means "idle" never ends, so
     * every Activity-based test hung (conformance run 42021765313). Frame sampling is a device measurement only.
     */
    static final boolean SAMPLING = !"robolectric".equals(Build.FINGERPRINT);
    private boolean closed;

    DiagnosticFrameSampler(String screen)
    {
        metrics = new DiagnosticFrameMetrics(screen, System.nanoTime(), Diagnostics::event);
        if (SAMPLING)
        {
            Choreographer.getInstance().postFrameCallback(this);
        }
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
