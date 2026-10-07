package com.archos.mediacenter.video.leanback;

import org.junit.Test;
import static org.junit.Assert.*;

public class PreviewLanguageIconTest {
    @Test public void unknownAndInvalidRegionsDoNotInventCountries(){
        for(String value:new String[]{null,"","English","Portuguese","default","English (UK)","en-001","en-XX"})assertEquals("",PreviewLanguageIcon.country(value));
    }
    @Test public void genericLanguageIdentifiersAreDistinctWithoutInventingFlags(){
        assertEquals("EN",PreviewLanguageIcon.languageLabel("eng"));assertEquals("PT",PreviewLanguageIcon.languageLabel("pt"));
        assertEquals("ZH",PreviewLanguageIcon.languageLabel("zh-Hant-TW"));assertEquals("",PreviewLanguageIcon.languageLabel("English"));
        assertEquals("",PreviewLanguageIcon.languageLabel("und"));assertEquals("",PreviewLanguageIcon.languageLabel(null));
    }
    @Test public void approvedGenericDefaultsAreSharedAcrossIsoAliases(){
        assertEquals("US",PreviewLanguageIcon.country("en"));assertEquals("US",PreviewLanguageIcon.country("eng"));
        assertEquals("BR",PreviewLanguageIcon.country("pt"));assertEquals("CN",PreviewLanguageIcon.country("zh"));
        assertEquals("ES",PreviewLanguageIcon.country("es"));assertEquals("SA",PreviewLanguageIcon.country("ar"));
        assertEquals("FR",PreviewLanguageIcon.country("fre"));assertEquals("DE",PreviewLanguageIcon.country("ger"));assertEquals("CN",PreviewLanguageIcon.country("chi"));assertEquals("FR",PreviewLanguageIcon.country("fr"));assertEquals("DE",PreviewLanguageIcon.country("de"));assertEquals("JP",PreviewLanguageIcon.country("ja"));
    }
    @Test public void explicitLocalesKeepTheirCountry(){
        assertEquals("GB",PreviewLanguageIcon.country("en-GB"));assertEquals("BR",PreviewLanguageIcon.country("pt_BR"));assertEquals("PH",PreviewLanguageIcon.country("fil"));
        assertEquals("TW",PreviewLanguageIcon.country("zh-Hant-TW"));assertEquals("US",PreviewLanguageIcon.country("en-us"));
    }
}
