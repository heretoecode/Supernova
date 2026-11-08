package com.archos.mediacenter.video.data;

import static org.junit.Assert.assertEquals;
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
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;

@RunWith(RobolectricTestRunner.class)
@Config(application = Application.class, sdk = {23, 30}, manifest = Config.NONE)
public class SearchStorageMetadataTest
{
    @Test public void existingRecentKeySurvivesReopenAndOneCallClearsBothConsumers()
    {
        var preferences = RuntimeEnvironment.getApplication().getSharedPreferences("search", 0);
        preferences.edit().clear().putString("preview_search_recent", "Old\nEarlier").commit();
        var store = new RecentSearches(preferences);
        assertEquals(List.of("Old", "Earlier"), store.read());
        store.remember("old");
        assertEquals(List.of("old", "Earlier"), new RecentSearches(preferences).read());
        for (int i = 0; i < 12; i++)
            store.remember("query " + i);
        assertEquals(8, store.read().size());
        new RecentSearches(preferences).clear();
        assertTrue(store.read().isEmpty());
    }

    @Test public void collectionUsesExactIdAndPersonUsesExactCredits() throws Exception
    {
        var adapter = new LibrarySearchMetadata(new LibrarySearchMetadata.Transport() {
            @Override public JSONObject request(String path, Map<String, String> parameters) throws Exception
            {
                if (path.equals("movie/1"))
                    return new JSONObject().put("id", 1).put("belongs_to_collection", new JSONObject().put("id", 9));
                if (path.equals("collection/9"))
                    return new JSONObject().put("id", 9).put("parts", new JSONArray()
                        .put(new JSONObject().put("id", 2).put("title", "Dune Part Two")));
                return new JSONObject().put("id", 99).put("cast", new JSONArray());
            }
            @Override public List<String> services(LibrarySearch.Identity title, BooleanSupplier cancelled)
            {
                return List.of("Service");
            }
        });
        var owned = new LibrarySearch.Title(new LibrarySearch.Identity("movie", 1), "Dune", List.of(), List.of(),
                                           List.of(), 2021);
        assertTrue(adapter.family(owned, () -> false).get(0).accepted("movie"));
        assertTrue(adapter.work(new LibrarySearch.Person(1, "Person", "actor"), () -> false).isEmpty());
    }
}
