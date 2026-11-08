package com.archos.mediacenter.video.data;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import android.app.Application;
import java.util.List;
import java.util.Map;
import java.util.function.BooleanSupplier;
import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

@RunWith(RobolectricTestRunner.class)
@Config(application = Application.class, sdk = {23, 30}, manifest = Config.NONE)
public class SeriesFamilyLinksTest
{
    @Test public void explicitCatalogueLinksRequireExactSeedAndPositiveTvIds() throws Exception
    {
        JSONArray rows = new JSONArray();
        for (String[] binding : List.of(new String[] {"73586", "118357"}, new String[] {"73586", "118357"},
            new String[] {"73586", "73586"}, new String[] {"1", "999"}, new String[] {"73586", "-5"}))
            rows.put(new JSONObject().put("seedId", new JSONObject().put("value", binding[0]))
                .put("id", new JSONObject().put("value", binding[1])));
        assertEquals(List.of(118357L), SeriesFamilyLinks.parse(73586,
            new JSONObject().put("results", new JSONObject().put("bindings", rows))));
        String query = SeriesFamilyLinks.query(73586);
        assertTrue(query.contains("P2512"));
        assertTrue(query.contains("P8345"));
        assertFalse(query.contains("creator"));
    }

    @Test public void differentlyNamedExplicitSpinoffSurvivesNameSearchFailure() throws Exception
    {
        var adapter = new LibrarySearchMetadata(new LibrarySearchMetadata.Transport() {
            @Override public List<Long> familyLinks(long id, BooleanSupplier cancelled)
            {
                return List.of(118357L);
            }
            @Override public JSONObject request(String path, Map<String, String> parameters) throws Exception
            {
                if (path.equals("tv/73586"))
                    return new JSONObject().put("id", 73586).put("name", "Yellowstone");
                if (path.equals("tv/118357"))
                    return new JSONObject().put("id", 118357).put("name", "1883");
                throw new java.io.IOException("Name discovery unavailable");
            }
            @Override public List<String> services(LibrarySearch.Identity id, BooleanSupplier cancelled)
            {
                return List.of("Paramount+");
            }
        });
        var owned = new LibrarySearch.Title(new LibrarySearch.Identity("tv", 73586), "Yellowstone", List.of(),
                                           List.of(), List.of(), 2018);
        var result = LibrarySearch.load("Yellowstone", List.of(owned), adapter, () -> true, () -> false);
        assertEquals("1883", result.families().get(0).items().get(0).title().title());
        assertEquals(List.of("Paramount+"), result.families().get(0).items().get(0).services());
    }
}
