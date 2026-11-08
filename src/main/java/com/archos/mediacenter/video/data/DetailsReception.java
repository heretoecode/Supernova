package com.archos.mediacenter.video.data;

import android.content.Context;
import com.archos.mediacenter.video.leanback.PreviewMetadataCache;
import com.archos.mediacenter.video.metadata.OmdbSupplement;
import org.json.JSONObject;

/** Complete five-row Reception snapshot. Offline reads never require an API key or start a refresh. */
public final class DetailsReception
{
    public record Score(String rating, String votes)
    {
    }

    public record Facts(Score imdb, Score rottenTomatoes, Score metacritic, Score tmdb, String awards)
    {
    }

    /** Worker-only disk reads from the same persisted envelopes used by Details enrichment. */
    public static Facts cached(Context context, String kind, long id)
    {
        if (!("movie".equals(kind) || "tv".equals(kind)) || id <= 0)
            throw new IllegalArgumentException("Exact title identity required");
        JSONObject envelope = PreviewMetadataCache.read(context, kind, id, "");
        JSONObject details = envelope == null ? null : envelope.optJSONObject("data");
        return read(kind, id, details, OmdbSupplement.cached(context, kind, id));
    }

    /** Pure projection; all rows and independent vote counts remain present, including absent scores. */
    public static Facts read(String kind, long id, JSONObject details, OmdbSupplement.Ratings supplementary)
    {
        boolean exact = details != null && id > 0 && details.optLong("id") == id &&
            ("movie".equals(kind) && details.has("title") || "tv".equals(kind) && details.has("name"));
        double rating = exact ? details.optDouble("vote_average", -1) : -1;
        long votes = exact ? details.optLong("vote_count", -1) : -1;
        return new Facts(new Score(known(supplementary.imdb), known(supplementary.imdbVotes)),
            new Score(known(supplementary.rottenTomatoes), known(supplementary.tomatoReviews)),
            new Score(known(supplementary.metacritic), known(supplementary.metacriticReviews)),
            new Score(!Double.isNaN(rating) && !Double.isInfinite(rating) && rating > 0 && rating <= 10 ? Double.toString(rating) + "/10" : "Unknown",
                      votes >= 0 ? Long.toString(votes) : "Unknown"), known(supplementary.awards));
    }

    private static String known(String value)
    {
        return value == null || value.trim().isEmpty() || "N/A".equalsIgnoreCase(value.trim())
            ? "Unknown" : value.trim();
    }

    private DetailsReception()
    {
    }
}
