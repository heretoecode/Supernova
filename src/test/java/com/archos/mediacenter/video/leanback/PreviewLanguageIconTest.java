package com.archos.mediacenter.video.leanback;

import org.junit.Test;
import static org.junit.Assert.*;

public class PreviewLanguageIconTest {
    @Test public void genericLanguagesNeverInventCountries(){
        for(String value:new String[]{null,"","en","eng","English","pt","Portuguese","default","English (UK)","en-001","en-XX"})assertEquals("",PreviewLanguageIcon.country(value));
    }
    @Test public void explicitLocalesKeepTheirCountry(){
        assertEquals("GB",PreviewLanguageIcon.country("en-GB"));assertEquals("BR",PreviewLanguageIcon.country("pt_BR"));
        assertEquals("TW",PreviewLanguageIcon.country("zh-Hant-TW"));assertEquals("US",PreviewLanguageIcon.country("en-us"));
    }
}
