package com.archos.mediacenter.video.diagnostics;

import java.util.Map;
import java.util.TreeMap;
import java.util.function.BiConsumer;

/** Session measurements carry source identity on every event; unknown counters stay explicitly unknown. */
public final class ThumbnailTelemetry
{
    private final Object[] identity;
    private final BiConsumer<String, Object[]> sink;
    private final Map<Long, Long> histogram = new TreeMap<>();
    private long frames;
    private long extractMs;
    private long knownReadBytes;
    private long unknownReads;
    private long hits;
    private long misses;
    private long bufferingActive;
    private long bufferingIdle;
    private long droppedActive;
    private long droppedIdle;
    private boolean droppedAvailable;

    public ThumbnailTelemetry(String mediaKey, String sourceKind, String sizeClass, String resolution, String codec,
                              BiConsumer<String, Object[]> sink)
    {
        identity = new Object[] {"media_key", mediaKey,     "source_kind", sourceKind, "file_size_class",
                                 sizeClass,   "resolution", resolution,    "codec",    codec};
        this.sink = sink;
    }

    public synchronized void event(String name, Object... fields)
    {
        Object[] all = new Object[identity.length + fields.length];
        System.arraycopy(identity, 0, all, 0, identity.length);
        System.arraycopy(fields, 0, all, identity.length, fields.length);
        sink.accept(name, all);
    }

    public synchronized void frame(long elapsedMs, long bytesRead, int jpegBytes, long positionMs)
    {
        frames++;
        extractMs += elapsedMs;
        long bucket = Math.min(60000, ((elapsedMs + 9) / 10) * 10);
        histogram.merge(bucket, 1L, Long::sum);
        if (bytesRead < 0)
            unknownReads++;
        else
            knownReadBytes += bytesRead;
        event("thumb_frame", "extract_ms", elapsedMs, "read_bytes", bytesRead, "read_bytes_known", bytesRead >= 0,
              "jpeg_bytes", jpegBytes, "position_ms", positionMs, "frame_position_quality", "requested_sync_target");
    }

    public synchronized void scrub(boolean hit, long distanceMs)
    {
        if (hit)
            hits++;
        else
            misses++;
        event("thumb_scrub", "hit", hit, "distance_ms", distanceMs);
    }

    public synchronized void buffering(boolean extracting)
    {
        if (extracting)
            bufferingActive++;
        else
            bufferingIdle++;
    }

    /** Native/device hook calls this only when an actual dropped-frame delta is available. */
    public synchronized void dropped(long delta, boolean extracting)
    {
        if (delta < 0)
            return;
        droppedAvailable = true;
        if (extracting)
            droppedActive += delta;
        else
            droppedIdle += delta;
    }

    public synchronized void summary(String reason)
    {
        long threshold = (frames * 95 + 99) / 100;
        long count = 0;
        long p95 = 0;
        for (Map.Entry<Long, Long> bucket : histogram.entrySet())
        {
            count += bucket.getValue();
            p95 = bucket.getKey();
            if (count >= threshold)
                break;
        }
        event("thumb_session_summary", "reason", reason, "frames_made", frames, "mean_extract_ms",
              frames == 0 ? 0 : (double)extractMs / frames, "p95_extract_ms", p95, "p95_quality",
              "10ms_bucket_upper_bound_capped_60s", "known_read_bytes", knownReadBytes, "unknown_read_frames",
              unknownReads, "cache_hit_rate", hits + misses == 0 ? 0 : (double)hits / (hits + misses),
              "buffering_while_extracting", bufferingActive, "buffering_while_idle", bufferingIdle,
              "dropped_frames_available", droppedAvailable, "dropped_while_extracting", droppedActive,
              "dropped_while_idle", droppedIdle);
    }
}
