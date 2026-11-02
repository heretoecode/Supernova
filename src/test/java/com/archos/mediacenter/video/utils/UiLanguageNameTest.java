package com.archos.mediacenter.video.utils;

import java.util.Locale;
import org.junit.Test;
import static org.junit.Assert.*;

/** Owner report 2 Nov 2026: the interface-language list showed the raw code "kmr (Turkey)". */
public class UiLanguageNameTest {
    @Test public void kurmanjiShowsAReadableName() {
        String name = VideoPreferencesCommon.uiLanguageName(Locale.forLanguageTag("kmr-TR"));
        assertTrue(name, name.startsWith("Kurmanji"));
        assertFalse(name, name.startsWith("kmr"));
    }

    @Test public void namedLanguagesAreUnchanged() {
        Locale french = Locale.forLanguageTag("fr");
        assertEquals(french.getDisplayName(french), VideoPreferencesCommon.uiLanguageName(french));
    }
}
