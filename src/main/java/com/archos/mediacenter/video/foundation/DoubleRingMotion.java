package com.archos.mediacenter.video.foundation;

/** Fixed geometry, linear angular motion, independent of frame rate. */
public final class DoubleRingMotion {
    private DoubleRingMotion() { }
    public static float outer(long elapsedMillis) { return -45f + (elapsedMillis % 3200L) * 360f / 3200f; }
    public static float inner(long elapsedMillis) { return 135f - (elapsedMillis % 3200L) * 360f / 3200f; }
}
