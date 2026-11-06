package com.archos.mediacenter.video.metadata;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.Test;

public class ForgivingMatchSearchTest
{
    private static JSONObject movie(long id, String title)
    {
        return new JSONObject().put("id", id).put("title", title);
    }

    private static ForgivingMatchSearch.Gateway gateway(List<String> calls, JSONObject... rows)
    {
        return new ForgivingMatchSearch.Gateway() {
            @Override public JSONObject tmdb(String path, Map<String, String> parameters)
            {
                calls.add(path + parameters);
                if (path.startsWith("find/"))
                    return new JSONObject().put("movie_results", new JSONArray(List.of(rows)))
                        .put("tv_results", new JSONArray());
                if (path.startsWith("movie/"))
                    return rows[0];
                return new JSONObject().put("results", new JSONArray(List.of(rows)));
            }

            @Override public JSONArray trakt(String path)
            {
                calls.add(path);
                return new JSONArray().put(new JSONObject().put("movie",
                    new JSONObject().put("ids", new JSONObject().put("tmdb", rows[0].optLong("id")))));
            }
        };
    }

    @Test public void articleCasePunctuationAndOriginalTitleRankAheadOfProviderOrder()
    {
        var results = ForgivingMatchSearch.search("THE u.n.c.l.e.", "movie", gateway(new ArrayList<>(),
            movie(1, "Other"), movie(2, "Translated").put("original_title", "U N C L E")), () -> false);
        assertEquals(2, results.get(0).row().optLong("id"));
        assertTrue(results.get(0).bestMatch());
        assertEquals(100, results.get(0).score());
        assertFalse(results.get(1).bestMatch());
    }

    @Test public void allIdentifiersReachTheCorrectReadOnlyProvider()
    {
        for (String query : List.of("tt1234567", "tvdb:123", "tmdb:movie:12", "12", "trakt:123", "trakt:movie:dune"))
        {
            List<String> calls = new ArrayList<>();
            var result = ForgivingMatchSearch.search(query, "movie", gateway(calls, movie(12, "Dune")), () -> false);
            assertEquals(query, 1, result.size());
            assertEquals(12, result.get(0).row().optLong("id"));
            assertTrue(query, !calls.isEmpty());
        }
    }

    @Test public void retryIsBoundedAndSingleLetterErrorsRankAboveUnrelatedTitles()
    {
        List<String> calls = new ArrayList<>();
        var result = ForgivingMatchSearch.search("Conclavf", "movie",
            gateway(calls, movie(1, "Other"), movie(2, "Conclave")), () -> false);
        assertEquals(2, result.get(0).row().optLong("id"));
        assertEquals(85, result.get(0).score());
        assertTrue(calls.size() <= 32);
        assertTrue(ForgivingMatchSearch.variants("U.N.C.L.E.").contains("uncle"));
    }

    @Test public void offlineAndCancelledReturnNoneWithoutMutationOrFurtherRequests()
    {
        List<String> calls = new ArrayList<>();
        assertTrue(ForgivingMatchSearch.search("Dune", "movie", gateway(calls, movie(1, "Dune")),
                                             () -> true).isEmpty());
        assertTrue(calls.isEmpty());
        ForgivingMatchSearch.Gateway offline = new ForgivingMatchSearch.Gateway() {
            @Override public JSONObject tmdb(String path, Map<String, String> params) throws Exception
            {
                throw new java.io.IOException("Offline");
            }
            @Override public JSONArray trakt(String path) throws Exception
            {
                throw new java.io.IOException("Offline");
            }
        };
        assertTrue(ForgivingMatchSearch.search("Dune", "movie", offline, () -> false).isEmpty());
    }
}
