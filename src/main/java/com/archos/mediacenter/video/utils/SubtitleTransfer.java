package com.archos.mediacenter.video.utils;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.function.BooleanSupplier;

/** Bounded staging copy: a failed/empty/cancelled response must never reach a subtitle destination. */
final class SubtitleTransfer {
    static final long MAX_BYTES = 32L * 1024 * 1024;

    static long stage(InputStream source, OutputStream staging, BooleanSupplier cancelled) throws IOException {
        byte[] buffer = new byte[8192];
        long total = 0;
        for (;;) {
            if (cancelled.getAsBoolean()) throw new IOException("Subtitle transfer cancelled");
            int count = source.read(buffer);
            if (count < 0) break;
            if (count == 0) continue;
            total += count;
            if (total > MAX_BYTES) throw new IOException("Subtitle response exceeds safety limit");
            staging.write(buffer, 0, count);
        }
        if (cancelled.getAsBoolean()) throw new IOException("Subtitle transfer cancelled");
        if (total == 0) throw new IOException("Empty subtitle response");
        staging.flush();
        return total;
    }

    private SubtitleTransfer() {}
}
