package com.archos.mediacenter.video.data;

import android.app.Application;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import java.util.List;
import java.util.Set;
import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.Test;

@RunWith(RobolectricTestRunner.class)
@Config(application = Application.class, sdk = 30)
public class RelatedTitlesTest
{
    @Test public void mergeKeepsLibraryFirstAndOnlyVerifiedServicesWithStableIds()
    {
        RelatedTitles.Sources sources = new RelatedTitles.Sources() {
            @Override public JSONArray recommendations()
            {
                return new JSONArray().put(new JSONObject().put("id", 1)).put(new JSONObject().put("id", 2));
            }
            @Override public JSONArray similar()
            {
                return new JSONArray().put(new JSONObject().put("id", 2)).put(new JSONObject().put("id", 3));
            }
            @Override public JSONArray traktRelated()
            {
                return new JSONArray().put(new JSONObject().put("ids", new JSONObject().put("tmdb", 3)))
                    .put(new JSONObject().put("ids", new JSONObject().put("tmdb", 4)));
            }
            @Override public boolean onServices(String kind, long id)
            {
                return id == 3;
            }
        };
        var result = RelatedTitles.load("movie", 1, Set.of("movie:2"), sources, () -> false);
        assertEquals(List.of(2L), result.inLibrary().stream().map(RelatedTitles.Title::id).toList());
        assertEquals(List.of(3L), result.onServices().stream().map(RelatedTitles.Title::id).toList());
        assertTrue(RelatedTitles.load("movie", 1, Set.of(), sources, () -> true).inLibrary().isEmpty());
    }
}
