package com.archos.mediacenter.video.leanback;

import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Flag policy (owner decision 2 Nov 2026). Uses the codes real media and Settings actually
 * carry — {@code eng}, {@code fre}, {@code ger}, {@code en}, {@code pt-br} — not only
 * explicitly regional fixtures such as {@code en-GB}, which real tracks almost never have.
 */
public class PreviewLanguageIconTest {
    @Test public void realWorldTrackCodesGetTheirDefaultFlag() {
        // ISO 639-2 terminology and bibliographic codes from MKV/MP4 track metadata.
        assertEquals("GB", PreviewLanguageIcon.country("eng"));
        assertEquals("FR", PreviewLanguageIcon.country("fre"));
        assertEquals("FR", PreviewLanguageIcon.country("fra"));
        assertEquals("DE", PreviewLanguageIcon.country("ger"));
        assertEquals("DE", PreviewLanguageIcon.country("deu"));
        assertEquals("ES", PreviewLanguageIcon.country("spa"));
        assertEquals("IT", PreviewLanguageIcon.country("ita"));
        assertEquals("JP", PreviewLanguageIcon.country("jpn"));
        assertEquals("KR", PreviewLanguageIcon.country("kor"));
        assertEquals("NL", PreviewLanguageIcon.country("dut"));
        assertEquals("CN", PreviewLanguageIcon.country("chi"));
        assertEquals("GB", PreviewLanguageIcon.country(" ENG "));
    }

    @Test public void settingsPickerCodesGetTheirDefaultFlag() {
        // Values stored by the Audio/Subtitle preferred-language pickers (OpenSubtitles list).
        assertEquals("GB", PreviewLanguageIcon.country("en"));
        assertEquals("ZA", PreviewLanguageIcon.country("af"));
        assertEquals("AL", PreviewLanguageIcon.country("sq"));
        assertEquals("BR", PreviewLanguageIcon.country("pt-br"));
        assertEquals("PT", PreviewLanguageIcon.country("pt-pt"));
        assertEquals("CN", PreviewLanguageIcon.country("zh-cn"));
        assertEquals("TW", PreviewLanguageIcon.country("zh-tw"));
        assertEquals("ME", PreviewLanguageIcon.country("me"));
        assertEquals("IL", PreviewLanguageIcon.country("he"));
        assertEquals("IL", PreviewLanguageIcon.country("iw"));
        assertEquals("ID", PreviewLanguageIcon.country("id"));
    }

    @Test public void explicitRegionAlwaysWins() {
        assertEquals("GB", PreviewLanguageIcon.country("en-GB"));
        assertEquals("US", PreviewLanguageIcon.country("en-us"));
        assertEquals("BR", PreviewLanguageIcon.country("pt_BR"));
        assertEquals("CA", PreviewLanguageIcon.country("fr-CA"));
        assertEquals("TW", PreviewLanguageIcon.country("zh-Hant-TW"));
        assertEquals("TW", PreviewLanguageIcon.country("zh-Hant"));
        // Invalid or non-country regions fall back to the language default.
        assertEquals("GB", PreviewLanguageIcon.country("en-XX"));
        assertEquals("GB", PreviewLanguageIcon.country("en-001"));
    }

    @Test public void ambiguousOrUnknownLanguagesKeepTheNeutralGlobe() {
        for (String value : new String[]{null, "", "und", "und-US", "qqq-US", "xx", "commentary", "default",
                "English", "English (UK)", "system", "ar", "ara", "sw", "ca", "cy", "eu", "eo", "ab", "ku",
                "zh-ca", "yue", PreviewLanguageIcon.providerCode("zh-ca")}) {
            assertEquals("globe for " + value, "", PreviewLanguageIcon.country(value));
        }
    }

    @Test public void everyDefaultFlagIsARealCountry() {
        java.util.Set<String> countries = new java.util.HashSet<>(java.util.Arrays.asList(java.util.Locale.getISOCountries()));
        for (java.util.Map.Entry<String, String> entry : PreviewLanguageIcon.DEFAULT_COUNTRY.entrySet())
            assertTrue(entry.getKey() + " → " + entry.getValue(), countries.contains(entry.getValue()));
    }
}
