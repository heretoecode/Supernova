package com.archos.mediacenter.video.metadata;

import java.text.Normalizer;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.function.BooleanSupplier;
import org.json.JSONArray;
import org.json.JSONObject;

/** Worker-only read-only discovery. Never applies a match or changes viewing state. */
public final class ForgivingMatchSearch
{
    public interface Gateway
    {
        JSONObject tmdb(String path, Map<String, String> parameters) throws Exception;
        JSONArray trakt(String path) throws Exception;
    }

    public record Hit(JSONObject row, String kind, int score, boolean bestMatch)
    {
    }

    public static List<Hit> search(String query, String scope, Gateway gateway, BooleanSupplier cancelled)
    {
        if (query == null || query.trim().isEmpty() || query.length() > 240 ||
            !Set.of("movie", "tv", "all").contains(scope))
            return List.of();
        if (cancelled.getAsBoolean() || Thread.currentThread().isInterrupted())
            return List.of();
        String input = query.trim();
        long deadline = System.nanoTime() + java.util.concurrent.TimeUnit.SECONDS.toNanos(12);
        Map<String, Hit> hits = new LinkedHashMap<>();
        try
        {
            String lower = input.toLowerCase(Locale.ROOT);
            if (lower.matches("tt[0-9]{7,10}") || lower.matches("tvdb:[1-9][0-9]{0,9}"))
            {
                boolean imdb = lower.startsWith("tt");
                String id = imdb ? lower : lower.substring(5);
                JSONObject found = gateway.tmdb("find/" + id, Map.of("external_source", imdb ? "imdb_id" : "tvdb_id"));
                for (String kind : List.of("movie", "tv"))
                    if (scope.equals("all") || scope.equals(kind))
                        add(hits, found.optJSONArray(kind + "_results"), kind, input, 100);
            }
            else if (lower.matches("trakt:(?:(?:movie|tv):)?[a-z0-9]+(?:-[a-z0-9]+)*"))
            {
                String[] parts = lower.split(":");
                if (parts.length == 3)
                    scope = parts[1];
                String id = parts[parts.length - 1];
                JSONArray found = new JSONArray();
                if (id.matches("[0-9]+"))
                    found = gateway.trakt("search/trakt/" + id);
                else
                    for (String kind : List.of("movie", "tv"))
                        if ((scope.equals("all") || scope.equals(kind)) && !stopped(cancelled, deadline))
                        {
                            try
                            {
                                JSONArray rows = gateway.trakt((kind.equals("movie") ? "movies/" : "shows/") + id);
                                for (int i = 0; i < rows.length(); i++)
                                    found.put(rows.get(i));
                            }
                            catch (Exception missingKind)
                            {
                                // A slug may belong to only one kind. Continue with the other scoped kind.
                            }
                        }
                for (int i = 0; i < found.length(); i++)
                {
                    JSONObject result = found.optJSONObject(i);
                    if (result == null)
                        continue;
                    for (String kind : List.of("movie", "tv"))
                    {
                        if (!scope.equals("all") && !scope.equals(kind))
                            continue;
                        JSONObject title = result.optJSONObject(kind.equals("movie") ? "movie" : "show");
                        JSONObject ids = title == null ? null : title.optJSONObject("ids");
                        if (ids == null || ids.optLong("tmdb") <= 0 || stopped(cancelled, deadline))
                            continue;
                        JSONObject row = gateway.tmdb(kind + "/" + ids.getLong("tmdb"), Map.of());
                        add(hits, new JSONArray().put(row), kind, input, 100);
                    }
                }
            }
            else if (lower.matches("(?:tmdb:(?:movie|tv):)?[1-9][0-9]{0,9}"))
            {
                String[] parts = lower.split(":");
                String id = parts[parts.length - 1];
                if (parts.length == 3)
                    scope = parts[1];
                for (String kind : List.of("movie", "tv"))
                    if ((scope.equals("all") || scope.equals(kind)) && !stopped(cancelled, deadline))
                        add(hits, new JSONArray().put(gateway.tmdb(kind + "/" + id, Map.of())), kind, input, 100);
            }
            else
            {
                for (String variant : variants(input))
                {
                    for (String kind : List.of("movie", "tv"))
                    {
                        if (stopped(cancelled, deadline))
                            break;
                        if (!scope.equals("all") && !scope.equals(kind))
                            continue;
                        JSONObject found = gateway.tmdb("search/" + kind, Map.of("query", variant));
                        add(hits, found.optJSONArray("results"), kind, input, -1);
                    }
                    if (hits.values().stream().anyMatch(hit -> hit.score() == 100) || stopped(cancelled, deadline))
                        break;
                }
            }
        }
        catch (Exception unavailable)
        {
            // Offline, failed identifiers and unavailable providers yield no new candidates.
        }
        if (cancelled.getAsBoolean() || Thread.currentThread().isInterrupted())
            return List.of();
        List<Hit> result = new ArrayList<>(hits.values());
        result.sort(Comparator.comparingInt(Hit::score).reversed()
            .thenComparing(hit -> hit.kind() + ":" + hit.row().optLong("id")));
        if (result.size() > 40)
            result = new ArrayList<>(result.subList(0, 40));
        if (!result.isEmpty())
        {
            Hit best = result.get(0);
            result.set(0, new Hit(best.row(), best.kind(), best.score(), true));
        }
        return List.copyOf(result);
    }

    private static boolean stopped(BooleanSupplier cancelled, long deadline)
    {
        return cancelled.getAsBoolean() || Thread.currentThread().isInterrupted() || System.nanoTime() >= deadline;
    }

    static List<String> variants(String input)
    {
        Set<String> variants = new LinkedHashSet<>();
        variants.add(input);
        String clean = normal(input);
        if (clean.isEmpty())
            return List.of();
        variants.add(clean);
        variants.add("the " + clean);
        variants.add(clean.replaceAll("(?<=\\b\\p{L}) (?=\\p{L}\\b)", ""));
        // Removing the mistyped character lets TMDB's partial-title matching retry a one-letter spelling error.
        // Bound query fan-out as well as wall time; exact results stop expansion immediately.
        for (int i = 0; i < clean.length() && variants.size() < 32; i++)
            if (Character.isLetterOrDigit(clean.charAt(i)))
                variants.add((clean.substring(0, i) + clean.substring(i + 1)).trim());
        variants.remove("");
        return List.copyOf(variants);
    }

    static String normal(String text)
    {
        return Normalizer.normalize(text, Normalizer.Form.NFKD).replaceAll("\\p{M}+", "")
            .toLowerCase(Locale.ROOT).replaceAll("[^\\p{L}\\p{N}]+", " ").trim().replaceFirst("^the +", "");
    }

    private static void add(Map<String, Hit> hits, JSONArray rows, String kind, String query, int forcedScore)
    {
        if (rows == null)
            return;
        for (int i = 0; i < Math.min(40, rows.length()); i++)
        {
            JSONObject row = rows.optJSONObject(i);
            if (row == null || row.optLong("id") < 1 || row.optLong("id") > Integer.MAX_VALUE)
                continue;
            String title = row.optString(kind.equals("movie") ? "title" : "name");
            if (title.trim().isEmpty())
                continue;
            int score = forcedScore >= 0 ? forcedScore : Math.max(score(query, title),
                score(query, row.optString(kind.equals("movie") ? "original_title" : "original_name")));
            Hit next = new Hit(row, kind, score, false);
            String key = kind + ":" + row.optLong("id");
            Hit previous = hits.get(key);
            if (previous == null || next.score() > previous.score())
                hits.put(key, next);
        }
    }

    private static int score(String query, String title)
    {
        String wanted = normal(query).replace(" ", "");
        String actual = normal(title).replace(" ", "");
        if (actual.isEmpty())
            return 0;
        if (wanted.equals(actual))
            return 100;
        if (oneEdit(wanted, actual))
            return 85;
        return wanted.contains(actual) || actual.contains(wanted) ? 60 : 10;
    }

    private static boolean oneEdit(String left, String right)
    {
        if (Math.abs(left.length() - right.length()) > 1)
            return false;
        int edits = 0;
        int a = 0;
        int b = 0;
        while (a < left.length() && b < right.length())
        {
            if (left.charAt(a) == right.charAt(b))
            {
                a++;
                b++;
            }
            else
            {
                if (++edits > 1)
                    return false;
                if (left.length() >= right.length())
                    a++;
                if (right.length() >= left.length())
                    b++;
            }
        }
        return edits + left.length() - a + right.length() - b <= 1;
    }

    private ForgivingMatchSearch()
    {
    }
}
