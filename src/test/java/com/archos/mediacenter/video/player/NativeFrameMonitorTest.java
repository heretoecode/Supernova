package com.archos.mediacenter.video.player;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.junit.Test;

public class NativeFrameMonitorTest
{
    @Test public void unknownIsNeverZeroAndOnlyMeasuredDropDeltasReachThumbnailBackoff()
    {
        List<Long> drops = new ArrayList<>();
        List<Map<String, Object>> events = new ArrayList<>();
        NativeFrameMonitor monitor = new NativeFrameMonitor((name, fields) -> {
            Map<String, Object> event = new HashMap<>();
            for (int i = 0; i < fields.length; i += 2)
                event.put((String)fields[i], fields[i + 1]);
            events.add(event);
        }, drops::add);
        monitor.sample(null, null, 0);
        assertEquals(-1L, events.get(0).get("late_dropped_frames"));
        assertFalse((Boolean)events.get(0).get("native_available"));
        monitor.sample(new long[] {1, 31, 100, 2, 99, 3, 600, -7, 0}, "decoder", 5000);
        monitor.sample(new long[] {1, 31, 110, 4, 999, 4, 900, -3, 0}, "decoder", 60_000);
        assertEquals(List.of(3L), drops);
        assertEquals(2, events.size());
        assertEquals(-3L, events.get(1).get("scheduled_audio_video_phase_ms"));
        assertFalse((Boolean)events.get(1).get("hardware_av_drift_available"));
        monitor.sample(new long[] {1, 1, 1, 0, 0, 0, 0, 0, 0}, null, 120_000);
        assertNull(events.get(2).get("scheduled_audio_video_phase_ms"));
        assertEquals(-1L, events.get(2).get("early_dropped_frames"));
        monitor.sample(new long[] {1, 31, 0, 0, 0, 0, 0, 0, 0}, null, 125_000);
        assertEquals(List.of(3L), drops);
    }
}
