package com.archos.mediacenter.video.leanback;

import android.content.SharedPreferences;
import android.net.Uri;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.regex.Pattern;

/** Classification is a hint only. It never assigns a metadata identity or rewrites a library record. */
public final class PreviewMediaClassification {
    public enum Kind {
        MOVIE, TV, UNKNOWN
    }

    private static final Pattern EPISODE =
        Pattern.compile("(?i)(?:^|[^a-z0-9])(?:s\\d{1,3}[ ._-]*e\\d{1,4}|\\d{1,2}x\\d{2,3})(?:[^0-9]|$)");
    private static final Pattern YEAR = Pattern.compile("(?:^|[ ._(\\[])(?:19\\d{2}|20\\d{2})(?:[ ._)\\]]|$)");

    public static Kind classify(String filename) {
        if (filename == null)
            return Kind.UNKNOWN;
        if (EPISODE.matcher(filename).find())
            return Kind.TV;
        if (YEAR.matcher(filename).find())
            return Kind.MOVIE;
        return Kind.UNKNOWN;
    }

    public static Map<Uri, Kind> hints(SharedPreferences preferences) {
        Map<Uri, Kind> hints = new HashMap<>();
        for (Map.Entry<String, ?> entry : preferences.getAll().entrySet())
            if (entry.getKey().startsWith("preview_source_kind:")) {
                Kind kind = "movie".equals(entry.getValue()) ? Kind.MOVIE
                    : "tv".equals(entry.getValue()) ? Kind.TV
                                                             : Kind.UNKNOWN;
                if (kind != Kind.UNKNOWN)
                    hints.put(Uri.parse(entry.getKey().substring("preview_source_kind:".length())), kind);
            }
        return hints;
    }

    public static Kind classify(String filename, Uri file, Map<Uri, Kind> hints) {
        int longest = -1;
        Kind result = Kind.UNKNOWN;
        if (file != null && file.getPath() != null)
            for (Map.Entry<Uri, Kind> entry : hints.entrySet()) {
                Uri folder = entry.getKey();
                String path = folder.getPath();
                if (path == null || !scheme(folder).equals(scheme(file))
                    || !Objects.equals(folder.getAuthority(), file.getAuthority()))
                    continue;
                if (!path.endsWith("/"))
                    path += "/";
                if (file.getPath().startsWith(path) && path.length() > longest) {
                    longest = path.length();
                    result = entry.getValue();
                }
            }
        return longest >= 0 ? result : classify(filename);
    }

    private static String scheme(Uri uri) {
        String scheme = uri.getScheme();
        if (scheme == null)
            return "file";
        switch (scheme.toLowerCase(Locale.ROOT)) {
            case "webdav":
            case "dav":
                return "http";
            case "webdavs":
            case "davs":
                return "https";
            default:
                return scheme.toLowerCase(Locale.ROOT);
        }
    }

    private PreviewMediaClassification() {
    }
}
