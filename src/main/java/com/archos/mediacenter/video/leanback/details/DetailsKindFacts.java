package com.archos.mediacenter.video.leanback.details;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 * The facts the Details tab's four kinds need beyond the page's own (DETAILS.md › Details tab): an episode's
 * "Season 2 · Episode 3" and guest stars; a season's "2 of 2", episodes, episode length and total running time; a
 * show's status with its seasons; Files for seasons and shows. Pure, for tests.
 */
final class DetailsKindFacts
{
    private DetailsKindFacts()
    {
    }

    /** "movie", "episode", "season" or "show". */
    static String kind(boolean episode, boolean film, int seasonNumber, boolean show, String remoteKind)
    {
        if (episode)
        {
            return "episode";
        }
        if (film)
        {
            return "movie";
        }
        if (seasonNumber >= 0)
        {
            return "season";
        }
        if (show || "tv".equals(remoteKind))
        {
            return "show";
        }
        return "movie";
    }

    static void add(String kind, Map<String, String> key, Map<String, String> technical, JSONObject show,
                    JSONObject episode, SortedMap<Integer, JSONArray> seasons, int season, int number,
                    int localEpisodes)
    {
        switch (kind)
        {
        case "episode":
            key.put("Episode", "Season " + season + " · Episode " + number);
            key.put("Guest stars", longList(names(episode == null ? null : episode.optJSONArray("guest_stars"))));
            break;
        case "season":
            JSONArray episodes = seasons == null ? null : seasons.get(season);
            int count = episodes == null ? 0 : episodes.length();
            int all = show == null ? 0 : show.optInt("number_of_seasons");
            key.put("Season", all > 0 ? season + " of " + all : String.valueOf(season));
            key.put("Episodes", episodes(count, localEpisodes));
            runtime(key, minutes(episodes), count);
            key.put("Season aired", aired(episodes));
            technical.put("Files", localEpisodes + (localEpisodes == 1 ? " episode" : " episodes"));
            break;
        case "show":
            int total = show == null ? 0 : show.optInt("number_of_episodes");
            int seasonCount = show == null ? 0 : show.optInt("number_of_seasons");
            key.put("Episodes", episodes(total, localEpisodes));
            List<Integer> lengths = new ArrayList<>();
            if (seasons != null)
            {
                for (JSONArray each : seasons.values())
                {
                    lengths.addAll(minutes(each));
                }
            }
            runtime(key, lengths, total);
            String status = key.get("Status");
            if (status != null && !status.isEmpty() && seasonCount > 0)
            {
                key.put("Status", status + " · " + seasonCount + (seasonCount == 1 ? " season" : " seasons"));
            }
            technical.put("Files", localEpisodes + (localEpisodes == 1 ? " episode" : " episodes"));
            break;
        default:
            break;
        }
    }

    /** "18 · 16 in your library". */
    static String episodes(int total, int local)
    {
        if (total <= 0)
        {
            return local > 0 ? local + " in your library" : "";
        }
        return total + " · " + local + " in your library";
    }

    /** Episode length "Average 50 min" and Total running time "15 h 10 min", from the known runtimes. */
    private static void runtime(Map<String, String> key, List<Integer> minutes, int episodes)
    {
        if (minutes.isEmpty())
        {
            return;
        }
        long sum = 0;
        for (int value : minutes)
        {
            sum += value;
        }
        long average = Math.round((double) sum / minutes.size());
        key.put("Episode length", "Average " + average + " min");
        long total = average * Math.max(episodes, minutes.size());
        key.put("Total running time", total >= 60 ? total / 60 + " h " + total % 60 + " min" : total + " min");
    }

    private static List<Integer> minutes(JSONArray episodes)
    {
        List<Integer> out = new ArrayList<>();
        for (int i = 0; episodes != null && i < episodes.length(); i++)
        {
            JSONObject episode = episodes.optJSONObject(i);
            int value = episode == null ? 0 : episode.optInt("runtime");
            if (value > 0)
            {
                out.add(value);
            }
        }
        return out;
    }

    /** "2024-03-01 – 2024-05-03": the season's first and last air dates. */
    private static String aired(JSONArray episodes)
    {
        String first = "";
        String last = "";
        for (int i = 0; episodes != null && i < episodes.length(); i++)
        {
            JSONObject episode = episodes.optJSONObject(i);
            String date = episode == null ? "" : episode.optString("air_date");
            if (date.isEmpty())
            {
                continue;
            }
            if (first.isEmpty() || date.compareTo(first) < 0)
            {
                first = date;
            }
            if (date.compareTo(last) > 0)
            {
                last = date;
            }
        }
        return first.isEmpty() ? "" : first.equals(last) ? first : first + " – " + last;
    }

    private static List<String> names(JSONArray people)
    {
        List<String> out = new ArrayList<>();
        for (int i = 0; people != null && i < people.length(); i++)
        {
            JSONObject person = people.optJSONObject(i);
            String name = person == null ? "" : person.optString("name").trim();
            if (!name.isEmpty())
            {
                out.add(name);
            }
        }
        return out;
    }

    /** The long-lists rule: at most two, then "and N more". */
    static String longList(List<String> values)
    {
        if (values.size() <= 2)
        {
            return android.text.TextUtils.join(", ", values);
        }
        return values.get(0) + ", " + values.get(1) + " and " + (values.size() - 2) + " more";
    }
}
