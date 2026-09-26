package com.archos.mediacenter.video.leanback;

import java.util.regex.Pattern;

/** Classification is a hint only. It never assigns a metadata identity or rewrites a library record. */
public final class PreviewMediaClassification {
    public enum Kind { MOVIE, TV, UNKNOWN }
    private static final Pattern EPISODE = Pattern.compile("(?i)(?:^|[^a-z0-9])(?:s\\d{1,3}[ ._-]*e\\d{1,4}|\\d{1,2}x\\d{2,3})(?:[^0-9]|$)");
    private static final Pattern YEAR = Pattern.compile("(?:^|[ ._(\\[])(?:19\\d{2}|20\\d{2})(?:[ ._)\\]]|$)");
    public static Kind classify(String filename) {
        if (filename == null) return Kind.UNKNOWN;
        if (EPISODE.matcher(filename).find()) return Kind.TV;
        if (YEAR.matcher(filename).find()) return Kind.MOVIE;
        return Kind.UNKNOWN;
    }
    private PreviewMediaClassification() {}
}
