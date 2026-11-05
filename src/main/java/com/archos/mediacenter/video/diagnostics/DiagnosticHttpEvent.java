package com.archos.mediacenter.video.diagnostics;

/** Bounded request facts, with no address, headers, body text or credentials. */
public final class DiagnosticHttpEvent
{
    public static Object[] result(String service, String endpoint, int status, long bytes, long elapsedMs, String cache,
                                  String outcome)
    {
        return new Object[] {"service",
                             service,
                             "endpoint_category",
                             endpoint,
                             "status",
                             status,
                             "body_bytes",
                             bytes,
                             "body_bytes_known",
                             bytes >= 0,
                             "duration_ms",
                             Math.max(0, elapsedMs),
                             "cache",
                             cache,
                             "outcome",
                             outcome,
                             "suspicious_small_body",
                             bytes >= 0 && bytes <= 24};
    }

    private DiagnosticHttpEvent()
    {
    }
}
