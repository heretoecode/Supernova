package com.archos.mediacenter.video.player;

import java.util.List;
import java.util.Locale;
import java.util.MissingResourceException;

/** Track descriptions survive per-file index changes. A missing language never selects a different language. */
public final class TrackPreference
{
    public final String language;
    public final String codec;
    public final String channels;
    public final boolean forced;
    public final boolean none;

    public TrackPreference(String language, String codec, String channels, boolean forced, boolean none)
    {
        this.language = language(language);
        this.codec = clean(codec).replaceAll("[^a-z0-9]", "");
        this.channels = clean(channels);
        this.forced = forced;
        this.none = none;
    }

    private static String clean(String value)
    {
        return value == null ? "" : value.trim().toLowerCase(Locale.ROOT);
    }

    private static String language(String value)
    {
        String code = clean(value).replace('_', '-');
        if (code.equals("und"))
            return "";
        if (code.length() == 3)
        {
            for (String candidate : Locale.getISOLanguages())
            {
                try
                {
                    if (new Locale(candidate).getISO3Language().equals(code))
                        return candidate;
                }
                catch (MissingResourceException unavailable)
                {
                    // Keep the original code when this Android locale table does not have it.
                }
            }
        }
        return code;
    }

    /** Returns a list position or -1 when no compatible description exists; None is handled by the caller. */
    public int bestMatch(List<TrackPreference> tracks)
    {
        int result = -1;
        int best = -1;
        for (int i = 0; i < tracks.size(); i++)
        {
            TrackPreference candidate = tracks.get(i);
            if (candidate == null || candidate.none || none)
                continue;
            boolean sameLanguage = language.equals(candidate.language);
            boolean sameBase = !language.isEmpty() && !candidate.language.isEmpty() &&
                               language.split("-", 2)[0].equals(candidate.language.split("-", 2)[0]);
            if (!language.isEmpty() && !sameLanguage && !sameBase)
                continue;
            int score = sameLanguage ? 64 : sameBase ? 32 : 0;
            if (!codec.isEmpty() && codec.equals(candidate.codec))
                score += 16;
            if (!channels.isEmpty() && channels.equals(candidate.channels))
                score += 8;
            if (forced == candidate.forced)
                score += 4;
            if (score > best)
            {
                result = i;
                best = score;
            }
        }
        return result;
    }
}
