package com.archos.mediacenter.video.leanback.search;

import android.content.Context;
import com.archos.mediacenter.video.streaming.StreamingRepository;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.function.BooleanSupplier;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 * Search's streaming group for a film in the library (page sheet SEARCH.md; RULES §18): the other films of the same
 * story — its TMDB collection, so sequels and prequels — that are not in the library and are on your services.
 * Never "similar" titles (they stay in More Like This). Worker thread only; nothing when Where to Watch is off.
 */
final class SearchStreaming
{
    static final int MOST = 6;

    private SearchStreaming()
    {
    }

    /** One title to stream: what it is, where, and the link that opens it in the service's app. */
    static final class Title
    {
        final long id;
        final String name;
        final int year;
        final String relation;
        final String service;
        final String poster;
        final String link;

        Title(long id, String name, int year, String relation, String service, String poster, String link)
        {
            this.id = id;
            this.name = name;
            this.year = year;
            this.relation = relation;
            this.service = service;
            this.poster = poster;
            this.link = link;
        }
    }

    /** "Stream more from the world of <title>" and its titles; null when there is nothing to show. */
    static final class Group
    {
        final String heading;
        final List<Title> titles;

        Group(String heading, List<Title> titles)
        {
            this.heading = heading;
            this.titles = titles;
        }
    }

    static Group worldOf(Context context, long movieId, String libraryTitle, int libraryYear, Set<Long> owned,
                         BooleanSupplier cancelled)
    {
        if (movieId <= 0 || !StreamingRepository.enabled(context))
        {
            return null;
        }
        try
        {
            JSONObject details = StreamingRepository.metadata(context, "movie", movieId, "");
            JSONObject collection = details.optJSONObject("belongs_to_collection");
            if (collection == null || collection.optLong("id") <= 0 || cancelled.getAsBoolean())
            {
                return null;
            }
            JSONArray parts = StreamingRepository.collection(context, collection.getLong("id")).optJSONArray("parts");
            List<Title> titles = new ArrayList<>();
            String country = StreamingRepository.country(context);
            Set<String> selected = StreamingRepository.selected(context);
            String preferred = StreamingRepository.preferred(context);
            for (int i = 0; parts != null && i < parts.length() && titles.size() < MOST; i++)
            {
                if (cancelled.getAsBoolean())
                {
                    return null;
                }
                JSONObject part = parts.getJSONObject(i);
                long id = part.optLong("id");
                if (id <= 0 || id == movieId || owned.contains(id))
                {
                    continue;
                }
                StreamingRepository.Availability availability =
                    StreamingRepository.load(context, "movie", id, country);
                List<StreamingRepository.Offer> offers = StreamingRepository.filter(availability, selected, preferred);
                if (offers.isEmpty())
                {
                    continue;
                }
                StreamingRepository.Offer top = offers.get(0);
                int year = year(part.optString("release_date"));
                String relation = year <= 0 || libraryYear <= 0 ? "" : year < libraryYear ? "prequel" : "sequel";
                String link = StreamingRepository.titleLink(context, "movie", id, country, availability,
                                                            top.provider.id);
                titles.add(new Title(id, part.optString("title"), year, relation, top.provider.name,
                                     part.optString("poster_path"), link));
            }
            return titles.isEmpty() ? null : new Group(heading(libraryTitle, documentary(details)), titles);
        }
        catch (Exception unavailable)
        {
            return null;
        }
    }

    /**
     * One heading starting "Stream" (SEARCH.md): "Stream more from the world of Tidelands" (fiction: sequels,
     * prequels, spin-offs, same collection), "Stream more related to Our Blue Planet II" (non-fiction). Never
     * "similar".
     */
    static String heading(String libraryTitle, boolean nonFiction)
    {
        return (nonFiction ? "Stream more related to " : "Stream more from the world of ") + libraryTitle;
    }

    /** People results: "Stream more from Mara Ellison". */
    static String personHeading(String name)
    {
        return "Stream more from " + name;
    }

    /** True for a documentary, judged from TMDB's genres on the title. */
    static boolean documentary(JSONObject details)
    {
        JSONArray genres = details == null ? null : details.optJSONArray("genres");
        for (int i = 0; genres != null && i < genres.length(); i++)
        {
            JSONObject genre = genres.optJSONObject(i);
            if (genre != null && (genre.optInt("id") == 99 || "Documentary".equalsIgnoreCase(genre.optString("name"))))
            {
                return true;
            }
        }
        return false;
    }

    static int year(String date)
    {
        if (date == null || date.length() < 4)
        {
            return 0;
        }
        try
        {
            return Integer.parseInt(date.substring(0, 4));
        }
        catch (NumberFormatException notYear)
        {
            return 0;
        }
    }
}
