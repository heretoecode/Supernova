package com.archos.mediacenter.video.diagnostics;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.LinkedHashMap;
import java.util.Map;
import org.junit.Test;

public class ThumbnailTelemetryTest
{
    @Test public void everyBuilderRetainsIdentityAndSummarySeparatesMeasuredAndUnknownCounters()
    {
        Map<String, Map<String, Object>> events = new LinkedHashMap<>();
        ThumbnailTelemetry telemetry =
            new ThumbnailTelemetry("key", "webdav", "large", "3840x2160", "HEVC", (name, fields) -> {
                Map<String, Object> values = new LinkedHashMap<>();
                for (int i = 0; i < fields.length; i += 2)
                    values.put((String)fields[i], fields[i + 1]);
                events.put(name, values);
            });
        telemetry.frame(101, -1, 5000, 45000);
        telemetry.frame(299, 200, 4000, 90000);
        telemetry.scrub(true, 1000);
        telemetry.scrub(false, -1);
        telemetry.buffering(true);
        telemetry.buffering(false);
        telemetry.dropped(2, true);
        telemetry.summary("stop");
        for (Map<String, Object> event : events.values())
        {
            assertEquals("key", event.get("media_key"));
            assertEquals("webdav", event.get("source_kind"));
            assertEquals("HEVC", event.get("codec"));
        }
        Map<String, Object> summary = events.get("thumb_session_summary");
        assertEquals(200.0, summary.get("mean_extract_ms"));
        assertEquals(300L, summary.get("p95_extract_ms"));
        assertEquals(1L, summary.get("unknown_read_frames"));
        assertEquals(0.5, summary.get("cache_hit_rate"));
        assertTrue((Boolean)summary.get("dropped_frames_available"));
    }
}
