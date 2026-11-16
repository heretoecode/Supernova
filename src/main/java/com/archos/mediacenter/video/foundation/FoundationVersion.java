package com.archos.mediacenter.video.foundation;

/** Literal APK counter, deliberately separate from NOVA's semantic/date version. */
public final class FoundationVersion {
    private FoundationVersion() {}
    /** "0.N", or a point release "0.N.P" (owner, 16 Nov 2026: 0.157.1). */
    public static final String PATTERN = "0\\.[1-9][0-9]*(\\.[1-9][0-9]*)?";

    public static int[] array(String version) {
        if (version == null || !version.matches(PATTERN))
            throw new IllegalArgumentException("Malformed Foundation counter");
        String[] parts = version.split("\\.");
        int point = parts.length > 2 ? Integer.parseInt(parts[2]) : 0;
        return new int[] {0, Integer.parseInt(parts[1]), point, 0, 0, 0, 0, 0};
    }
}
