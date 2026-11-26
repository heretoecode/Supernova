package com.archos.mediacenter.video.leanback.search;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import android.app.Application;
import android.content.Context;
import androidx.preference.PreferenceManager;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;

/** Settings › Library › Viewing History: Keep search history and Clear search history (owner, 25 Nov 2026). */
@RunWith(RobolectricTestRunner.class)
@Config(application = Application.class, sdk = 30)
public class SearchHistorySettingTest
{
    @Test public void offKeepsNothingAndClearEmptiesTheList()
    {
        Context context = RuntimeEnvironment.getApplication();
        PreferenceManager.getDefaultSharedPreferences(context).edit().clear().commit();
        PreviewSearch.remember(context, "Example Show");
        assertEquals(1, PreviewSearch.recent(context).size());
        PreviewSearch.clearHistory(context);
        assertTrue(PreviewSearch.recent(context).isEmpty());
        PreferenceManager.getDefaultSharedPreferences(context).edit()
            .putBoolean(PreviewSearch.KEEP_HISTORY, false).commit();
        PreviewSearch.remember(context, "Another Show");
        assertTrue("with Keep search history off nothing is recorded", PreviewSearch.recent(context).isEmpty());
    }
}
