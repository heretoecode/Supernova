package com.archos.mediacenter.video.leanback.details;

import static org.junit.Assert.assertEquals;

import android.app.Application;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.TreeMap;
import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

/** DETAILS.md › Details tab: each of the four kinds has its own rows. */
@RunWith(RobolectricTestRunner.class)
@Config(application = Application.class, sdk = 28)
public class DetailsKindFactsTest
{
    @Test
    public void kindsAndTheirRows() throws Exception
    {
        assertEquals("episode", DetailsKindFacts.kind(true, true, -1, true, null));
        assertEquals("season", DetailsKindFacts.kind(false, false, 2, true, null));
        assertEquals("show", DetailsKindFacts.kind(false, false, -1, true, null));
        assertEquals("Released", DetailsBlueprintPanel.keyFields("movie")[0][0]);
        assertEquals("First aired", DetailsBlueprintPanel.keyFields("show")[0][0]);
        assertEquals("File", DetailsBlueprintPanel.techFields("episode")[8][0]);

        JSONObject show = new JSONObject("{\"number_of_seasons\":2,\"number_of_episodes\":18}");
        TreeMap<Integer, JSONArray> seasons = new TreeMap<>();
        seasons.put(2, new JSONArray("[{\"runtime\":50,\"air_date\":\"2025-03-01\"},"
            + "{\"runtime\":50,\"air_date\":\"2025-03-08\"}]"));
        Map<String, String> key = new LinkedHashMap<>();
        Map<String, String> tech = new LinkedHashMap<>();
        DetailsKindFacts.add("season", key, tech, show, null, seasons, 2, 0, 2);
        assertEquals("2 of 2", key.get("Season"));
        assertEquals("2 · 2 in your library", key.get("Episodes"));
        assertEquals("Average 50 min", key.get("Episode length"));
        assertEquals("1 h 40 min", key.get("Total running time"));
        assertEquals("2025-03-01 – 2025-03-08", key.get("Season aired"));
        assertEquals("2 episodes", tech.get("Files"));

        key.clear();
        key.put("Status", "Returning");
        DetailsKindFacts.add("show", key, tech, show, null, seasons, -1, 0, 16);
        assertEquals("Returning · 2 seasons", key.get("Status"));
        assertEquals("18 · 16 in your library", key.get("Episodes"));
        assertEquals("Ruth Kane, Ada Moss and 2 more",
                     DetailsKindFacts.longList(Arrays.asList("Ruth Kane", "Ada Moss", "Lee Park", "Jo Hart")));
    }
}
