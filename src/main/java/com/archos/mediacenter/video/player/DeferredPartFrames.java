package com.archos.mediacenter.video.player;

import android.graphics.Bitmap;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

/** A source-owned bridge across decoder startup; no I/O and at most 256 pending part requests. */
public final class DeferredPartFrames implements PartPictures.FrameSource, AutoCloseable
{
    private record Request(long position, Consumer<Bitmap> callback) {}
    private final List<Request> pending = new ArrayList<>();
    private PartPictures.FrameSource source;
    private boolean closed;

    @Override public void frameAt(long positionMs, Consumer<Bitmap> callback)
    {
        PartPictures.FrameSource ready;
        synchronized (this) {
            if (closed)
                return;
            ready = source;
            if (ready == null) {
                if (pending.size() < 256)
                    pending.add(new Request(positionMs, callback));
                else
                    callback.accept(null);
                return;
            }
        }
        deliver(ready, new Request(positionMs, callback));
    }

    public void attach(PartPictures.FrameSource ready)
    {
        List<Request> requests;
        synchronized (this) {
            if (closed || source != null)
                return;
            source = java.util.Objects.requireNonNull(ready);
            requests = new ArrayList<>(pending);
            pending.clear();
        }
        for (Request request : requests)
            deliver(ready, request);
    }

    private void deliver(PartPictures.FrameSource ready, Request request)
    {
        ready.frameAt(request.position(), bitmap -> {
            synchronized (DeferredPartFrames.this) {
                if (!closed)
                    request.callback().accept(bitmap);
            }
        });
    }

    @Override public synchronized void close()
    {
        closed = true;
        pending.clear();
        source = null;
    }
}
