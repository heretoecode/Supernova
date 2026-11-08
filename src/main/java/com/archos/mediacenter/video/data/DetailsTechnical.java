package com.archos.mediacenter.video.data;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;

/** One immutable, measured file snapshot. Missing measurements remain unknown, never inferred from a filename. */
public final class DetailsTechnical
{
    public record Audio(String format, String channels, String language)
    {
    }

    public record File(long videoId, String revision, int width, int height, String codec, int bitDepth,
                       String hdr, long fpsNumerator, long fpsDenominator, long bitrateBitsPerSecond,
                       List<Audio> audio, List<String> subtitles, String container, long bytes,
                       String connection, String folder, long addedUtcMs)
    {
        public File
        {
            if (videoId <= 0 || revision == null || revision.isEmpty())
                throw new IllegalArgumentException("Exact indexed file revision required");
            audio = List.copyOf(audio);
            subtitles = List.copyOf(subtitles);
        }
    }

    public record Facts(long videoId, String revision, String resolution, String codec, int bitDepth, String hdr,
                        double framesPerSecond, long bitrateBitsPerSecond, List<Audio> audio,
                        List<String> audioLanguages, List<String> subtitleLanguages, String container,
                        long bytes, String connection, String folder, long addedUtcMs)
    {
    }

    /** Pure projection, main-thread safe. Numeric -1 and text Unknown are honest missing values. */
    public static Facts read(File file)
    {
        List<Audio> audio = new ArrayList<>();
        List<String> languages = new ArrayList<>();
        for (Audio track : file.audio())
        {
            audio.add(new Audio(known(track.format()), known(track.channels()), known(track.language())));
            languages.add(known(track.language()));
        }
        return new Facts(file.videoId(), file.revision(), file.width() > 0 && file.height() > 0
            ? file.width() + " × " + file.height() : "Unknown", known(file.codec()),
            file.bitDepth() > 0 ? file.bitDepth() : -1, known(file.hdr()),
            file.fpsNumerator() > 0 && file.fpsDenominator() > 0
                ? file.fpsNumerator() / (double) file.fpsDenominator() : -1,
            positive(file.bitrateBitsPerSecond()), List.copyOf(audio), unique(languages), unique(file.subtitles()),
            known(file.container()), positive(file.bytes()), known(file.connection()), known(file.folder()),
            positive(file.addedUtcMs()));
    }

    /** Average only when every distinct episode has a known size; duplicate versions do not inflate it. */
    public static long averageEpisodeBytes(List<File> episodes)
    {
        if (episodes.isEmpty())
            return -1;
        long total = 0;
        LinkedHashSet<Long> seen = new LinkedHashSet<>();
        for (File file : episodes)
        {
            if (!seen.add(file.videoId()))
                continue;
            if (file.bytes() <= 0 || Long.MAX_VALUE - total < file.bytes())
                return -1;
            total += file.bytes();
        }
        return total / seen.size();
    }

    private static long positive(long value)
    {
        return value > 0 ? value : -1;
    }

    private static List<String> unique(List<String> values)
    {
        LinkedHashSet<String> result = new LinkedHashSet<>();
        for (String value : values)
            result.add(known(value));
        return result.isEmpty() ? List.of("Unknown") : List.copyOf(result);
    }

    private static String known(String value)
    {
        return value == null || value.trim().isEmpty() || "N/A".equalsIgnoreCase(value.trim())
            ? "Unknown" : value.trim();
    }

    private DetailsTechnical()
    {
    }
}
