package com.archos.mediacenter.video.data;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.BooleanSupplier;
import org.json.JSONArray;
import org.json.JSONObject;

/** Three-source title merge. Identity is kind plus TMDB ID, never a translated display title. */
public final class RelatedTitles
{
    public interface Sources
    {
        JSONArray recommendations() throws Exception;
        JSONArray similar() throws Exception;
        JSONArray traktRelated() throws Exception;
        boolean onServices(String kind, long tmdbId) throws Exception;
        default boolean streamingEnabled()
        {
            return true;
        }
    }

    public record Title(String kind, long id, JSONObject metadata)
    {
    }

    public record Result(List<Title> inLibrary, List<Title> onServices)
    {
    }

    public static Result load(String kind, long sourceId, Set<String> library, Sources sources,
                               BooleanSupplier cancelled)
    {
        if (!Set.of("movie", "tv").contains(kind) || sourceId < 1)
            return new Result(List.of(), List.of());
        Map<Long, Title> merged = new LinkedHashMap<>();
        for (int source = 0; source < 3 && !cancelled.getAsBoolean(); source++)
        {
            try
            {
                JSONArray rows = switch (source)
                {
                    case 0 -> sources.recommendations();
                    case 1 -> sources.similar();
                    default -> sources.traktRelated();
                };
                for (int i = 0; i < Math.min(40, rows.length()); i++)
                {
                    JSONObject row = rows.optJSONObject(i);
                    if (row == null)
                        continue;
                    JSONObject ids = row.optJSONObject("ids");
                    long id = ids == null ? row.optLong("id") : ids.optLong("tmdb");
                    if (id > 0 && id <= Integer.MAX_VALUE && id != sourceId)
                        merged.putIfAbsent(id, new Title(kind, id, row));
                }
            }
            catch (Exception unavailable)
            {
                // Each provider can fail independently; successful sources still contribute.
            }
        }
        List<Title> local = new ArrayList<>();
        List<Title> remote = new ArrayList<>();
        for (Title title : merged.values())
        {
            if (cancelled.getAsBoolean() || Thread.currentThread().isInterrupted())
                return new Result(List.of(), List.of());
            if (library.contains(kind + ":" + title.id()))
                local.add(title);
            else
            {
                try
                {
                    if (sources.streamingEnabled() && sources.onServices(kind, title.id()) &&
                        sources.streamingEnabled())
                        remote.add(title);
                }
                catch (Exception unavailable)
                {
                    // No verified configured offer: do not imply availability.
                }
            }
        }
        return new Result(List.copyOf(local), sources.streamingEnabled() ? List.copyOf(remote) : List.of());
    }

    private RelatedTitles()
    {
    }
}
