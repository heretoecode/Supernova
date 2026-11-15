package com.archos.mediacenter.video.data;

import android.app.Application;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThrows;
import java.util.List;
import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.Test;

@RunWith(RobolectricTestRunner.class)
@Config(application = Application.class, sdk = 30)
public class DetailsFactsTest
{
    @Test public void seriesUsesEpisodeRuntimeAndHasNoFinancialFieldsOrForeignAliases()
    {
        JSONObject details = new JSONObject().put("id", 12).put("name", "Marshals")
            .put("budget", 200).put("revenue", 500)
            .put("production_companies", new JSONArray().put(new JSONObject().put("id", 1).put("name", "Correct")));
        JSONObject alternatives = new JSONObject().put("id", 12).put("results", new JSONArray()
            .put(new JSONObject().put("iso_3166_1", "FR").put("title", "Foreign"))
            .put(new JSONObject().put("iso_3166_1", "GB").put("title", "Local title")));
        var facts = DetailsFacts.read("tv", 12, details, alternatives, List.of(40 * 60000L, 45 * 60000L), "GB");
        assertEquals(List.of("Local title"), facts.aliases());
        assertEquals(45, facts.runtimeMinutes());
        assertFalse(facts.hasFinancialFields());
        assertEquals(-1, facts.budget());
        assertEquals(-1, facts.revenue());
        assertEquals(List.of("Correct"), facts.companies());
    }

    @Test public void wrongTitleEnvelopeIsRejectedAndUnknownValuesRemainExplicit()
    {
        JSONObject title = new JSONObject().put("id", 1).put("title", "A");
        assertThrows(IllegalArgumentException.class,
            () -> DetailsFacts.read("movie", 2, title, null, List.of(), "GB"));
        var facts = DetailsFacts.read("movie", 1, title, null, List.of(), "GB");
        assertEquals(-1, facts.runtimeMinutes());
        assertEquals(-1, facts.budget());
    }

    @Test public void tabPlanIsCompleteBeforeAnyContentIsFetched()
    {
        assertEquals(List.of(DetailsFacts.Tab.DETAILS, DetailsFacts.Tab.EXTRAS, DetailsFacts.Tab.RELATED),
                     DetailsFacts.tabs(false));
        assertEquals(List.of(DetailsFacts.Tab.DETAILS, DetailsFacts.Tab.EXTRAS, DetailsFacts.Tab.EPISODES,
                             DetailsFacts.Tab.RELATED), DetailsFacts.tabs(true));
    }
}
