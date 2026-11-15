package com.archos.mediacenter.video.player;

import java.util.Locale;
import com.archos.mediacenter.video.utils.VideoMetadata;
import com.archos.mediacenter.video.utils.VideoUtils;

/** Separate fields for the screen owner; no chooser text or track selection changes. */
public record TrackDetails(String language, boolean original, String format, String channels,
                           boolean sdh, boolean forced, boolean audioDescription)
{
    public static TrackDetails of(String language, String format, String channels, int disposition) {
        String code = new TrackPreference(language, "", "", false, false).language;
        String name = code.isEmpty() ? "" : Locale.forLanguageTag(code).getDisplayLanguage(Locale.UK);
        String codec = format == null ? "" : format.trim().toUpperCase(Locale.ROOT);
        String canonical = codec.replaceAll("[^A-Z0-9]", "");
        if (canonical.startsWith("EAC3"))
            codec = "Dolby Digital Plus";
        else if (canonical.startsWith("AC3"))
            codec = "Dolby Digital";
        else if (canonical.startsWith("TRUEHD"))
            codec = "Dolby TrueHD";
        else if (canonical.equals("TEXT"))
            codec = "";
        else if (canonical.startsWith("DTSHDMA"))
            codec = "DTS-HD Master Audio";
        String count = channels == null ? "" : channels.trim().replaceAll("(?i)\\s*(channels?|ch)$", "").trim();
        count = count.replaceAll("\\s*\\([^)]*\\)$", "").trim();
        count = switch (count) {
            case "1", "1.0" -> "Mono";
            case "2", "2.0" -> "Stereo";
            case "6" -> "5.1";
            case "8" -> "7.1";
            default -> count;
        };
        return new TrackDetails(name, (disposition & VideoUtils.AV_DISPOSITION_ORIGINAL) != 0, codec, count,
                (disposition & VideoUtils.AV_DISPOSITION_HEARING_IMPAIRED) != 0,
                (disposition & VideoUtils.AV_DISPOSITION_FORCED) != 0,
                (disposition & (VideoUtils.AV_DISPOSITION_VISUAL_IMPAIRED
                        | VideoUtils.AV_DISPOSITION_DESCRIPTIONS)) != 0);
    }

    public static TrackDetails audio(VideoMetadata.AudioTrack track) {
        return of(track.language, track.format, track.channels, track.disposition);
    }

    public static TrackDetails subtitle(VideoMetadata.SubtitleTrack track) {
        return of(track.language, "", "", track.disposition);
    }
}
