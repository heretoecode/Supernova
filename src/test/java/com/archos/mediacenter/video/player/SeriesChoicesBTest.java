package com.archos.mediacenter.video.player;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import android.app.Application;
import android.content.Context;
import java.util.ArrayList;
import java.util.List;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;

@RunWith(RobolectricTestRunner.class)
@Config(application = Application.class, sdk = 30)
public class SeriesChoicesBTest
{
    @Test public void legacySwitchMigratesLazilyAndSplitChoicesPersistIndependently()
    {
        Context app = RuntimeEnvironment.getApplication();
        app.getSharedPreferences("series-track-choices", 0).edit()
                .putBoolean("tmdb:tv:123:enabled", true).commit();
        SeriesTrackChoices store = new SeriesTrackChoices(app);
        assertTrue(store.enabled("123", true));
        assertTrue(store.enabled("123", false));
        store.remember("123", true, new TrackPreference("eng", "AAC", "2", false, false));
        store.remember("123", false, new TrackPreference("", "", "", false, true));
        store.setEnabled("123", true, false);
        SeriesTrackChoices reopened = new SeriesTrackChoices(app);
        assertNull(reopened.choice("123", true));
        assertTrue(reopened.choice("123", false).none);
        reopened.setEnabled("123", true, true);
        assertEquals("en", reopened.choice("123", true).language);
        assertNull(reopened.choice("124", false));
    }


}
