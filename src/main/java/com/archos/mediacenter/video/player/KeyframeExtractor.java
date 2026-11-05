package com.archos.mediacenter.video.player;

import android.content.Context;
import android.graphics.Bitmap;
import com.archos.medialib.AndroidMediaMetadataRetriever;
import com.archos.mediaprovider.video.ScanProcessLock;
import java.io.File;
import java.io.InterruptedIOException;
import java.util.concurrent.atomic.AtomicBoolean;

/** A second, owned sync-frame reader; it never touches the playing decoder or its proxy. */
final class KeyframeExtractor implements SeekThumbnails.FrameExtractor
{
    private final Context context;
    private final AtomicBoolean cancelled = new AtomicBoolean();
    private volatile AndroidMediaMetadataRetriever retriever;
    private volatile SeekThumbnails.Source activeSource;

    KeyframeExtractor(Context context)
    {
        this.context = context.getApplicationContext();
    }

    @Override public SeekThumbnails.Frame extract(SeekThumbnails.Source source, long positionMs) throws Exception
    {
        activeSource = source;
        try (ScanProcessLock lease = ScanProcessLock.acquire(
                 new File(context.getFilesDir(), "seek-thumbnail-extraction.lock"), cancelled::get))
        {
            if (cancelled.get())
                throw new InterruptedIOException("Thumbnail cancelled");
            AndroidMediaMetadataRetriever current = new AndroidMediaMetadataRetriever();
            retriever = current;
            if (cancelled.get())
            {
                current.release();
                throw new InterruptedIOException("Thumbnail cancelled before source open");
            }
            // The MediaLib wrapper resolves SMB/WebDAV/SFTP/content/UPnP with its own FileCore proxy.
            android.net.Uri input = source.resolvedUri == null ? source.uri : source.resolvedUri;
            if (input.getScheme() == null)
                input = android.net.Uri.fromFile(new File(input.toString()));
            current.setDataSource(context, input);
            Bitmap frame = current.getScaledSyncFrame(Math.multiplyExact(positionMs, 1000), 320, 180);
            if (frame == null)
                throw new java.io.IOException("Platform sync-frame decoder returned no frame");
            float scale = Math.min(320f / frame.getWidth(), 180f / frame.getHeight());
            if (scale < 1)
            {
                Bitmap small = Bitmap.createScaledBitmap(frame, Math.max(1, Math.round(frame.getWidth() * scale)),
                                                         Math.max(1, Math.round(frame.getHeight() * scale)), true);
                if (small != frame)
                    frame.recycle();
                frame = small;
            }
            return new SeekThumbnails.Frame(frame, -1);
        }
    }

    @Override public void cancel()
    {
        if (cancelled.compareAndSet(false, true))
        {
            AndroidMediaMetadataRetriever current = retriever;
            if (current != null)
            {
                SeekThumbnails.cancelIo(() -> {
                    try
                    {
                        current.release();
                    }
                    catch (Exception failure)
                    {
                        SeekThumbnails.Source source = activeSource;
                        if (source != null)
                            com.archos.mediacenter.video.diagnostics.Diagnostics.error(
                                "thumb_release_failed", failure, "stage", "cancel_release", "media_key",
                                source.mediaKey, "source_kind", source.kind, "file_size_class", source.sizeClass,
                                "resolution", source.resolution, "codec", source.codec);
                    }
                });
            }
        }
    }

    @Override public void close() throws Exception
    {
        AndroidMediaMetadataRetriever current = retriever;
        if (current != null)
            current.release();
    }
}
