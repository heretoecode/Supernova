package com.archos.mediacenter.video.data;

import android.content.Context;
import androidx.preference.PreferenceManager;
import com.archos.mediacenter.video.streaming.StreamingRepository;
import java.util.Set;
import java.util.function.BooleanSupplier;
import org.json.JSONArray;
import org.json.JSONObject;

/** Worker-only production adapter. Uses existing country/provider preferences and metadata transport/cache. */
public final class RelatedTitlesRepository
{
    public static RelatedTitles.Result load(Context context, String kind, long id, Set<String> library,
                                            BooleanSupplier cancelled)
    {
        Context app = context.getApplicationContext();
        return RelatedTitles.load(kind, id, library, new RelatedTitles.Sources() {
            @Override public JSONArray recommendations() throws Exception
            {
                return StreamingRepository.metadata(app, kind, id, "recommendations").getJSONArray("results");
            }

            @Override public JSONArray similar() throws Exception
            {
                return StreamingRepository.metadata(app, kind, id, "similar").getJSONArray("results");
            }

            @Override public JSONArray traktRelated() throws Exception
            {
                JSONArray identities = StreamingRepository.traktDiscovery(app, "search/tmdb/" + id,
                    StreamingRepository.readOnlyNetworkClient());
                for (int i = 0; i < identities.length(); i++)
                {
                    JSONObject row = identities.getJSONObject(i).optJSONObject(kind.equals("movie") ? "movie" : "show");
                    JSONObject ids = row == null ? null : row.optJSONObject("ids");
                    if (ids == null || ids.optLong("tmdb") != id || ids.optLong("trakt") <= 0)
                        continue;
                    return StreamingRepository.traktDiscovery(app,
                        (kind.equals("movie") ? "movies/" : "shows/") + ids.getLong("trakt") + "/related",
                        StreamingRepository.readOnlyNetworkClient());
                }
                return new JSONArray();
            }

            @Override public boolean onServices(String titleKind, long titleId) throws Exception
            {
                if (cancelled.getAsBoolean() || !PreferenceManager.getDefaultSharedPreferences(app)
                        .getBoolean(StreamingRepository.ENABLED, true))
                    return false;
                return !StreamingRepository.filter(StreamingRepository.load(app, titleKind, titleId,
                    StreamingRepository.country(app)), StreamingRepository.selected(app),
                    StreamingRepository.preferred(app)).isEmpty();
            }
        }, cancelled);
    }

    private RelatedTitlesRepository()
    {
    }
}
