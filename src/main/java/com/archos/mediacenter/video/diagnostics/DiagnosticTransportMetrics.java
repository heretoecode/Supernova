package com.archos.mediacenter.video.diagnostics;

import java.util.function.BiConsumer;

/** Existing FileCore boundary measurements; absent open/retry/status data is explicitly unknown. */
public final class DiagnosticTransportMetrics
{
    private final String id, session, protocol;
    private final BiConsumer<String, Object[]> sink;

    public DiagnosticTransportMetrics(String id, String session, String protocol, BiConsumer<String, Object[]> sink)
    {
        this.id = id;
        this.session = session;
        this.protocol = protocol;
        this.sink = sink;
    }

    public void boundary(String boundary, long bytes, long calls, long readNanos, long elapsedNanos)
    {
        sink.accept("transport_read", new Object[] {"schema",
                                                    2,
                                                    "stream_id",
                                                    id,
                                                    "playback_session",
                                                    session,
                                                    "source",
                                                    protocol,
                                                    "boundary",
                                                    boundary,
                                                    "bytes",
                                                    bytes,
                                                    "read_calls",
                                                    calls,
                                                    "read_duration_ns",
                                                    readNanos,
                                                    "stream_elapsed_ns",
                                                    elapsedNanos,
                                                    "throughput_bytes_per_second",
                                                    elapsedNanos <= 0 ? 0 : bytes * 1_000_000_000.0 / elapsedNanos,
                                                    "throughput_window",
                                                    "stream_lifetime_at_boundary",
                                                    "open_latency_available",
                                                    false,
                                                    "http_status_available",
                                                    false,
                                                    "retry_count_available",
                                                    false,
                                                    "timeout",
                                                    boundary.equals("TIMEOUT"),
                                                    "measurement_quality",
                                                    "PROXY",
                                                    "coverage",
                                                    "JAVA_STREAM_READS_ONLY"});
    }
}
