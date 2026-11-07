package com.archos.mediacenter.video.player;

import android.graphics.Bitmap;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;

/** Non-blocking part pictures; Part D's SeekThumbnails implements FrameSource. */
public final class PartPictures implements AutoCloseable
{
    public interface FrameSource
    {
        void frameAt(long positionMs, Consumer<Bitmap> callback);
    }

    public static final class Picture
    {
        public final PlaybackParts.Part part;
        public final long positionMs;
        public volatile Bitmap bitmap;

        private Picture(PlaybackParts.Part part, Bitmap still)
        {
            this.part = part;
            positionMs = part.startMs + Math.min(2000, part.endMs - part.startMs - 1);
            bitmap = still;
        }
    }

    private final List<Picture> pictures;
    private boolean closed;
    private final FrameSource source;
    private final Consumer<Picture> ready;
    private int next;
    private int inFlight;
    private boolean pumping;

    public PartPictures(PlaybackParts parts, Bitmap episodeStill, FrameSource source, Consumer<Picture> ready)
    {
        List<Picture> rows = new ArrayList<>();
        for (PlaybackParts.Part part : parts.parts())
        {
            rows.add(new Picture(part, episodeStill));
        }
        pictures = Collections.unmodifiableList(rows);
        this.source = source;
        this.ready = ready;
        requestMore();
    }

    /** Small batches ensure long timelines do not overflow the thumbnail engine's bounded queue. */
    private synchronized void requestMore()
    {
        if (pumping)
            return;
        pumping = true;
        try {
            while (!closed && inFlight < 4 && next < pictures.size()) {
                Picture picture = pictures.get(next++);
                inFlight++;
                source.frameAt(picture.positionMs, bitmap -> {
                    synchronized (PartPictures.this) {
                        inFlight--;
                        try {
                            if (!closed && bitmap != null) {
                                picture.bitmap = bitmap;
                                ready.accept(picture);
                            }
                        } finally {
                            requestMore();
                        }
                    }
                });
            }
        } finally {
            pumping = false;
        }
    }

    public List<Picture> pictures()
    {
        return pictures;
    }

    @Override public synchronized void close()
    {
        closed = true;
    }
}
