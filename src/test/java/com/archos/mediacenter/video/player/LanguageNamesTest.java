package com.archos.mediacenter.video.player;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

/** Languages in words (owner, 22 Nov 2026): "chi" reads Chinese. */
public class LanguageNamesTest
{
    @Test public void bibliographicCodesReadAsWords()
    {
        assertEquals("Chinese", LanguageNames.name("chi"));
        assertEquals("French", LanguageNames.name("fre"));
        assertEquals("German", LanguageNames.name("ger"));
        assertEquals("English", LanguageNames.name("eng"));
        assertEquals("English", LanguageNames.name("en"));
        assertEquals("Director's commentary", LanguageNames.name("Director's commentary"));
    }
}
