package com.archos.mediacenter.video.metadata;

import android.app.Application;
import android.content.SharedPreferences;
import androidx.preference.PreferenceManager;
import org.json.JSONObject;
import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;
import static org.junit.Assert.*;

/** Owner decision 2 Nov 2026: OMDb ratings use the owner's own key from Settings, never an APK secret. */
@RunWith(RobolectricTestRunner.class) @Config(application = Application.class, sdk = 28)
public class OmdbUserKeyTest {
    private SharedPreferences prefs() {
        return PreferenceManager.getDefaultSharedPreferences(RuntimeEnvironment.getApplication());
    }

    @After public void clear() {
        prefs().edit().clear().commit();
    }

    @Test public void disabledUntilAKeyIsEntered() {
        assertFalse(OmdbSupplement.production(RuntimeEnvironment.getApplication()).enabled());
        prefs().edit().putString(OmdbSupplement.PREF_API_KEY, "  ").commit();
        assertFalse(OmdbSupplement.production(RuntimeEnvironment.getApplication()).enabled());
        prefs().edit().putString(OmdbSupplement.PREF_API_KEY, "not a key!").commit();
        assertFalse(OmdbSupplement.production(RuntimeEnvironment.getApplication()).enabled());
    }

    @Test public void enabledWithAValidKey() {
        prefs().edit().putString(OmdbSupplement.PREF_API_KEY, " a1b2c3d4 ").commit();
        assertTrue(OmdbSupplement.production(RuntimeEnvironment.getApplication()).enabled());
    }

    @Test public void dailyBudgetStaysUnderTheFreeTier() {
        OmdbSupplement.UserKeyAccess access = new OmdbSupplement.UserKeyAccess(prefs());
        for (int n = 0; n < OmdbSupplement.DAILY_BUDGET; n++) assertTrue(access.acquireRequest());
        assertFalse("no request beyond the daily budget", access.acquireRequest());
        assertTrue(OmdbSupplement.DAILY_BUDGET < 1000);
    }

    @Test public void userKeyIsSentAndRatingsParsed() throws Exception {
        prefs().edit().putString(OmdbSupplement.PREF_API_KEY, "a1b2c3d4").commit();
        String[] requested = {null};
        OmdbSupplement omdb = new OmdbSupplement(new OmdbSupplement.UserKeyAccess(prefs()), url -> {
            requested[0] = url.toString();
            return new JSONObject("{Response:'True',imdbID:'tt0089218',Type:'movie',imdbRating:'7.7',imdbVotes:'447,000',Metascore:'62',Awards:'4 wins & 8 nominations.',Ratings:[{Source:'Rotten Tomatoes',Value:'77%'}]}");
        });
        OmdbSupplement.Ratings ratings = omdb.load("tt0089218", "movie");
        assertTrue(requested[0].contains("apikey=a1b2c3d4"));
        assertTrue(requested[0].contains("i=tt0089218"));
        assertEquals("7.7/10", ratings.imdb);
        assertEquals("77%", ratings.rottenTomatoes);
        assertEquals("62/100", ratings.metacritic);
        assertEquals("4 wins & 8 nominations.", ratings.awards);
    }

    @Test public void settingsCatchesRemoteTypos() {
        assertNull(OmdbSupplement.keyProblem(""));
        assertNull(OmdbSupplement.keyProblem(" a1b2c3d4 "));
        assertNotNull(OmdbSupplement.keyProblem("a1b2"));
        assertNotNull(OmdbSupplement.keyProblem("a1b2-c3d4"));
    }
}
