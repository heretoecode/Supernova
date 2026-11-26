package com.archos.mediacenter.video.player;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

/**
 * Languages in words, never bare codes (owner, 22 Nov 2026; RULES §9: "chi" must read Chinese). Files often carry the
 * ISO 639-2 bibliographic codes ("chi", "fre", "ger" …), which Android does not name; they are mapped to the codes it
 * knows first.
 */
public final class LanguageNames
{
    private static final Map<String, String> BIBLIOGRAPHIC = new HashMap<>();

    static
    {
        String[] pairs = {"alb", "sq", "arm", "hy", "baq", "eu", "bur", "my", "chi", "zh", "cze", "cs", "dut", "nl",
                          "fre", "fr", "geo", "ka", "ger", "de", "gre", "el", "ice", "is", "mac", "mk", "mao", "mi",
                          "may", "ms", "per", "fa", "rum", "ro", "slo", "sk", "tib", "bo", "wel", "cy", "zho", "zh"};
        for (int i = 0; i < pairs.length; i += 2)
            BIBLIOGRAPHIC.put(pairs[i], pairs[i + 1]);
    }

    private LanguageNames()
    {
    }

    /** The code Android names: bibliographic codes become their terminology or two-letter form. */
    public static String code(String language)
    {
        if (language == null)
            return "";
        String clean = language.trim().toLowerCase(Locale.ROOT);
        String mapped = BIBLIOGRAPHIC.get(clean);
        return mapped == null ? clean : mapped;
    }

    /** "Chinese" for "chi", "French" for "fre"; anything that isn't a code is returned as it is. */
    public static String name(String language)
    {
        if (language == null)
            return "";
        String trimmed = language.trim();
        if (!trimmed.matches("[a-zA-Z]{2,3}"))
            return trimmed;
        String named = Locale.forLanguageTag(code(trimmed)).getDisplayLanguage(Locale.UK);
        return named == null || named.isEmpty() || named.equalsIgnoreCase(trimmed) ? trimmed : named;
    }
}
