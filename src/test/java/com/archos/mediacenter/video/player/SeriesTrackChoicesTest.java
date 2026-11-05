package com.archos.mediacenter.video.player;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;

import android.app.Application;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;

@RunWith(RobolectricTestRunner.class)
@Config(application = Application.class, sdk = 30)
public class SeriesTrackChoicesTest
{
    @Test public void persistsDescriptionsOnlyForEnabledSeriesAndNoneIsDistinct()
    {
        SeriesTrackChoices choices = new SeriesTrackChoices(RuntimeEnvironment.getApplication());
        assertFalse(choices.enabled("123"));
        choices.setEnabled("123", true);
        choices.remember("123", true, new TrackPreference("en", "AAC", "2.0", false, false));
        choices.remember("123", false, new TrackPreference("", "", "", false, true));
        SeriesTrackChoices reopened = new SeriesTrackChoices(RuntimeEnvironment.getApplication());
        assertEquals("en", reopened.choice("123", true).language);
        assertEquals(true, reopened.choice("123", false).none);
        assertNull(reopened.choice("124", true));
        choices.setEnabled("123", false);
        assertNull(choices.choice("123", true));
    }
}
