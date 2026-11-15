package com.archos.mediacenter.video.streaming;

import static org.junit.Assert.assertEquals;

import java.util.Set;

import android.app.Application;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

@RunWith(RobolectricTestRunner.class)
@Config(application = Application.class, sdk = 30)
public class StreamingCountryDefaultTest
{
    @Test
    public void theDeviceRegionIsUsedWhenNothingIsSaved()
    {
        assertEquals("GB", StreamingRepository.defaultCountry(Set.of("other_key"), "gb"));
        assertEquals(StreamingRepository.FALLBACK_COUNTRY, StreamingRepository.defaultCountry(Set.of(), ""));
    }

    @Test
    public void servicesSavedUnderOneCountryKeepThatCountry()
    {
        assertEquals("IE", StreamingRepository.defaultCountry(Set.of("streaming_providers_IE"), "GB"));
    }

    @Test
    public void servicesSavedUnderSeveralCountriesFollowTheDevice()
    {
        Set<String> saved = Set.of("streaming_providers_IE", "streaming_providers_FR");
        assertEquals("GB", StreamingRepository.defaultCountry(saved, "GB"));
    }
}
