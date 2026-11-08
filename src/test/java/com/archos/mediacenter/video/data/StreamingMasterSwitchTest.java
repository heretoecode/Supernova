package com.archos.mediacenter.video.data;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import android.app.Application;
import com.archos.mediacenter.video.streaming.StreamingRepository;
import java.util.Set;
import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;

@RunWith(RobolectricTestRunner.class)
@Config(application = Application.class, sdk = 30, manifest = Config.NONE)
public class StreamingMasterSwitchTest
{
    @Test public void offHidesFreshDiskAvailabilityAndDoesNotNeedValidNetworkIdentity() throws Exception
    {
        var context = RuntimeEnvironment.getApplication();
        var preferences = StreamingRepository.prefs(context);
        preferences.edit().clear().putBoolean(StreamingRepository.ENABLED, false)
            .putLong("streaming_known_at:movie:1:GB:-1", System.currentTimeMillis())
            .putStringSet("streaming_known:movie:1:GB:-1", Set.of("8")).commit();
        assertFalse(StreamingRepository.enabled(context));
        assertTrue(StreamingRepository.load(context, "invalid", -1, "GB").offers.isEmpty());
        assertTrue(StreamingRepository.cachedAvailability(context, "movie", 1, "GB", -1).offers.isEmpty());
        assertFalse(StreamingRepository.knownOn(context, "movie", 1, "8"));
    }

    @Test public void offDoesNotCallRelatedAvailabilityAndRetainsLocalRecommendations()
    {
        RelatedTitles.Sources sources = new RelatedTitles.Sources() {
            @Override public JSONArray recommendations()
            {
                return new JSONArray().put(new JSONObject().put("id", 2)).put(new JSONObject().put("id", 3));
            }
            @Override public JSONArray similar()
            {
                return new JSONArray();
            }
            @Override public JSONArray traktRelated()
            {
                return new JSONArray();
            }
            @Override public boolean onServices(String kind, long id)
            {
                throw new AssertionError("Availability must not run while disabled");
            }
            @Override public boolean streamingEnabled()
            {
                return false;
            }
        };
        var result = RelatedTitles.load("movie", 1, Set.of("movie:2"), sources, () -> false);
        assertTrue(result.onServices().isEmpty());
        assertFalse(result.inLibrary().isEmpty());
    }
}
