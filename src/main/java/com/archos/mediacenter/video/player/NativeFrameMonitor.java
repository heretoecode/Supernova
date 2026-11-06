package com.archos.mediacenter.video.player;

import java.util.function.BiConsumer;
import java.util.function.LongConsumer;

/** Per-playback numeric samples. Submission time and scheduled clock phase never imply physical display time. */
public final class NativeFrameMonitor
{
    private final BiConsumer<String, Object[]> sink;
    private final LongConsumer dropped;
    private long previousDropped = -1, emittedAt = -1;

    public NativeFrameMonitor(BiConsumer<String, Object[]> sink, LongConsumer dropped)
    {
        this.sink = sink;
        this.dropped = dropped;
    }

    public void sample(long[] values, String decoder, long now)
    {
        boolean available = values != null && values.length == 9 && values[0] == 1;
        long coverage = available ? values[1] : 0;
        boolean dropAvailable = available && (coverage & 18) == 18 && values[3] >= 0 && values[5] >= 0;
        if (dropAvailable)
        {
            long count = values[3] + values[5];
            if (previousDropped >= 0 && count >= previousDropped)
                dropped.accept(count - previousDropped);
            previousDropped = count;
        }
        else
            previousDropped = -1;
        if (emittedAt >= 0 && now - emittedAt < 60_000)
            return;
        long window = emittedAt < 0 ? 0 : Math.max(0, now - emittedAt);
        emittedAt = now;
        sink.accept("playback_frame_metrics", new Object[] {
            "schema", 1, "window_ms", window, "native_available", available, "coverage_bits", coverage,
            "accepted_output_frames", available && (coverage & 1) != 0 ? values[2] : -1,
            "late_dropped_frames", available && (coverage & 2) != 0 ? values[3] : -1,
            "stale_seek_frames", available && (coverage & 4) != 0 ? values[4] : -1,
            "early_dropped_frames", available && (coverage & 16) != 0 ? values[5] : -1,
            "last_submit_monotonic_ns", available && values[6] > 0 ? values[6] : -1,
            "scheduled_audio_video_phase_ms", available && (coverage & 8) != 0 ? values[7] : null,
            "initialized_decoder", decoder == null ? "Unknown" : decoder,
            "physical_display_time_available", false, "hardware_av_drift_available", false,
            "measurement_quality", "NATIVE_OUTPUT_SUBMISSION"});
    }
}
