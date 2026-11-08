package com.archos.mediacenter.video.data;

import com.archos.mediacenter.video.leanback.search.PreviewSearchText;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.BooleanSupplier;
import org.json.JSONArray;
import org.json.JSONObject;

/** TMDB adapter: exact collections, conservative series family evidence and exact-person filmography. */
public final class LibrarySearchMetadata implements LibrarySearch.Sources
{
    public interface Transport
    {
        JSONObject request(String path, Map<String, String> parameters) throws Exception;
        List<String> services(LibrarySearch.Identity title, BooleanSupplier cancelled) throws Exception;
    }

    private final Transport transport;

    public LibrarySearchMetadata(Transport transport)
    {
        this.transport = transport;
    }

    private JSONObject get(String path, BooleanSupplier cancelled) throws Exception
    {
        if (cancelled.getAsBoolean() || Thread.currentThread().isInterrupted())
            throw new InterruptedException();
        return transport.request(path, Map.of());
    }

    @Override public List<LibrarySearch.Family> family(LibrarySearch.Title owned, BooleanSupplier cancelled)
        throws Exception
    {
        var identity = owned.identity();
        JSONObject details = get(identity.kind() + "/" + identity.id(), cancelled);
        if (details.optLong("id") != identity.id())
            return List.of();
        List<LibrarySearch.Family> result = new ArrayList<>();
        if ("movie".equals(identity.kind()))
        {
            JSONObject collection = details.optJSONObject("belongs_to_collection");
            long id = collection == null ? 0 : collection.optLong("id");
            if (id <= 0)
                return List.of();
            JSONObject family = get("collection/" + id, cancelled);
            if (family.optLong("id") != id)
                return List.of();
            JSONArray parts = family.optJSONArray("parts");
            for (int i = 0; parts != null && i < Math.min(40, parts.length()); i++)
            {
                LibrarySearch.Title title = title(parts.optJSONObject(i), "movie");
                if (title != null)
                    result.add(new LibrarySearch.Family(title, id, id, false, false, false, false));
            }
            return List.copyOf(result);
        }
        String root = owned.title().split(":", 2)[0].trim();
        JSONObject response = transport.request("search/tv", Map.of("query", root));
        JSONArray rows = response.optJSONArray("results");
        JSONObject originalCredits = get("tv/" + identity.id() + "/credits", cancelled);
        Set<Long> creators = ids(details.optJSONArray("created_by"));
        Set<String> characters = characters(originalCredits);
        for (int i = 0; rows != null && i < Math.min(8, rows.length()); i++)
        {
            LibrarySearch.Title candidate = title(rows.optJSONObject(i), "tv");
            if (candidate == null || candidate.identity().equals(identity) ||
                PreviewSearchText.rank(root, candidate.title()) == 0)
                continue;
            JSONObject next = get("tv/" + candidate.identity().id(), cancelled);
            if (next.optLong("id") != candidate.identity().id())
                continue;
            Set<Long> shared = ids(next.optJSONArray("created_by"));
            shared.retainAll(creators);
            if (shared.isEmpty())
                continue;
            Set<String> sharedCharacters = characters(get("tv/" + candidate.identity().id() + "/credits", cancelled));
            sharedCharacters.retainAll(characters);
            result.add(new LibrarySearch.Family(candidate, 0, 0, false, true, true, !sharedCharacters.isEmpty()));
        }
        return List.copyOf(result);
    }

    @Override public List<LibrarySearch.Title> work(LibrarySearch.Person person, BooleanSupplier cancelled)
        throws Exception
    {
        JSONObject credits = get("person/" + person.id() + "/combined_credits", cancelled);
        if (credits.optLong("id") != person.id())
            return List.of();
        List<LibrarySearch.Title> result = new ArrayList<>();
        for (String section : List.of("cast", "crew"))
        {
            JSONArray rows = credits.optJSONArray(section);
            for (int i = 0; rows != null && i < rows.length() && result.size() < 40; i++)
            {
                JSONObject row = rows.optJSONObject(i);
                LibrarySearch.Title title = title(row, row == null ? "" : row.optString("media_type"));
                if (title != null)
                    result.add(title);
            }
        }
        return List.copyOf(result);
    }

    @Override public List<String> services(LibrarySearch.Identity title, BooleanSupplier cancelled) throws Exception
    {
        return cancelled.getAsBoolean() ? List.of() : transport.services(title, cancelled);
    }

    private static LibrarySearch.Title title(JSONObject row, String kind)
    {
        if (row == null || row.optLong("id") <= 0 || !Set.of("movie", "tv").contains(kind))
            return null;
        String name = row.optString("tv".equals(kind) ? "name" : "title");
        if (name.trim().isEmpty())
            return null;
        return new LibrarySearch.Title(new LibrarySearch.Identity(kind, row.optLong("id")), name, List.of(),
                                       List.of(), List.of(), 0);
    }

    private static Set<Long> ids(JSONArray rows)
    {
        Set<Long> result = new HashSet<>();
        for (int i = 0; rows != null && i < rows.length(); i++)
        {
            JSONObject row = rows.optJSONObject(i);
            if (row != null && row.optLong("id") > 0)
                result.add(row.optLong("id"));
        }
        return result;
    }

    private static Set<String> characters(JSONObject credits)
    {
        Set<String> result = new HashSet<>();
        JSONArray cast = credits.optJSONArray("cast");
        for (int i = 0; cast != null && i < cast.length(); i++)
        {
            JSONObject row = cast.optJSONObject(i);
            String name = row == null ? "" : PreviewSearchText.fold(row.optString("character"));
            if (name.length() >= 5 && !Set.of("self", "himself", "herself").contains(name))
                result.add(name);
        }
        return result;
    }
}
