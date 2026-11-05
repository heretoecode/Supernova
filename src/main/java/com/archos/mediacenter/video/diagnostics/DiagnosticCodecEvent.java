package com.archos.mediacenter.video.diagnostics;

/** Candidate identity is known at the JNI selection boundary; successful decoder initialization is not. */
public final class DiagnosticCodecEvent
{
    public static Object[] candidate(String name, String mime, int profile, int width, int height, double rate,
                                     int index, int rank, boolean software)
    {
        return new Object[] {"decoder_name",
                             name,
                             "mime",
                             mime,
                             "codec_profile",
                             profile,
                             "width",
                             width,
                             "height",
                             height,
                             "frame_rate",
                             Double.isFinite(rate) ? rate : 0,
                             "candidate_index",
                             index,
                             "rank",
                             rank,
                             "software",
                             software,
                             "initialization_confirmed",
                             false,
                             "measurement",
                             "jni_candidate_selection"};
    }

    private DiagnosticCodecEvent()
    {
    }
}
