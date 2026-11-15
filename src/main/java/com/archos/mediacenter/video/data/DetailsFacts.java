package com.archos.mediacenter.video.data;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import org.json.JSONArray;
import org.json.JSONObject;

/** Read-only Details facts from a verified title envelope. No network, view, or localisation work. */
public final class DetailsFacts
{
    public record Facts(List<String> aliases, long runtimeMinutes, boolean hasFinancialFields,
                        long budget, long revenue, List<String> companies)
    {
    }

    public enum Tab
    {
        DETAILS, EXTRAS, EPISODES, RELATED
    }

    public static List<Tab> tabs(boolean series)
    {
        return series ? List.of(Tab.DETAILS, Tab.EXTRAS, Tab.EPISODES, Tab.RELATED)
                      : List.of(Tab.DETAILS, Tab.EXTRAS, Tab.RELATED);
    }

    public static Facts read(String kind, long expectedId, JSONObject details, JSONObject alternatives,
                             List<Long> episodeRuntimesMs, String country)
    {
        boolean series = "tv".equals(kind);
        if (!Set.of("movie", "tv").contains(kind) || expectedId <= 0 || details.optLong("id") != expectedId ||
            !details.has(series ? "name" : "title"))
            throw new IllegalArgumentException("Details identity differs from the requested title");
        Set<String> aliases = new LinkedHashSet<>();
        Set<String> normalAliases = new LinkedHashSet<>();
        normalAliases.add(normal(details.optString(series ? "name" : "title")));
        JSONArray titles = alternatives == null || alternatives.optLong("id", expectedId) != expectedId ? null
            : alternatives.optJSONArray(series ? "results" : "titles");
        String wantedCountry = country == null ? "GB" : country.toUpperCase(Locale.ROOT);
        if (titles != null)
            for (int i = 0; i < titles.length(); i++)
            {
                JSONObject row = titles.optJSONObject(i);
                if (row == null || !wantedCountry.equals(row.optString("iso_3166_1")))
                    continue;
                String title = row.optString("title").trim();
                if (!title.isEmpty() && normalAliases.add(normal(title)))
                    aliases.add(title);
            }
        List<Long> minutes = new ArrayList<>();
        if (series)
        {
            for (long duration : episodeRuntimesMs)
                if (duration >= 60000 && duration <= 24 * 60 * 60000L)
                    minutes.add(duration / 60000);
            if (minutes.isEmpty())
            {
                JSONArray runtime = details.optJSONArray("episode_run_time");
                if (runtime != null)
                    for (int i = 0; i < runtime.length(); i++)
                        if (runtime.optLong(i) > 0 && runtime.optLong(i) <= 1440)
                            minutes.add(runtime.optLong(i));
                JSONObject last = details.optJSONObject("last_episode_to_air");
                if (minutes.isEmpty() && last != null && last.optLong("runtime") > 0)
                    minutes.add(last.optLong("runtime"));
            }
        }
        else if (details.optLong("runtime") > 0)
            minutes.add(details.optLong("runtime"));
        minutes.sort(Long::compare);
        long runtime = minutes.isEmpty() ? -1 : minutes.get(minutes.size() / 2);
        Set<String> companies = new LinkedHashSet<>();
        JSONArray production = details.optJSONArray("production_companies");
        if (production != null)
            for (int i = 0; i < production.length(); i++)
            {
                JSONObject row = production.optJSONObject(i);
                if (row != null && row.optLong("id") > 0 && !row.optString("name").trim().isEmpty())
                    companies.add(row.optString("name"));
            }
        return new Facts(List.copyOf(aliases), runtime, !series,
            series ? -1 : details.optLong("budget", -1), series ? -1 : details.optLong("revenue", -1),
            List.copyOf(companies));
    }

    private static String normal(String text)
    {
        return text.toLowerCase(Locale.ROOT).replaceAll("[^\\p{L}\\p{N}]+", "");
    }

    private DetailsFacts()
    {
    }
}
