package com.archos.mediacenter.video.metadata;

import android.content.Context;
import java.util.Set;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/** GB-only title certificates; persistence/read APIs perform disk I/O and belong on a worker. */
public final class AgeCertificates
{
    public record Certificate(String rating, String reasons, boolean seriesFallback)
    {
        public Certificate {
            if (rating == null || !BBFC.contains(rating) || reasons == null || reasons.length() > 2048)
                throw new IllegalArgumentException("Invalid GB certificate");
        }
    }
    private static final Set<String> BBFC = Set.of("U", "PG", "12", "12A", "15", "18", "R18");

    public static Certificate parse(JSONObject data, boolean series)
    {
        JSONArray regions = data == null ? null : data.optJSONArray("results");
        Certificate fallback = null;
        if (regions != null)
            for (int i = 0; i < regions.length(); i++) {
                JSONObject region = regions.optJSONObject(i);
                if (region == null || !"GB".equals(region.optString("iso_3166_1")))
                    continue;
                if (series && BBFC.contains(region.optString("rating")))
                    return new Certificate(region.optString("rating"), descriptors(region), false);
                JSONArray releases = region.optJSONArray("release_dates");
                if (releases != null)
                    for (int j = 0; j < releases.length(); j++) {
                        JSONObject release = releases.optJSONObject(j);
                        if (release == null || !BBFC.contains(release.optString("certification")))
                            continue;
                        Certificate value = new Certificate(release.optString("certification"),
                                descriptors(release), false);
                        if (release.optInt("type") == 3)
                            return value;
                        if (fallback == null)
                            fallback = value;
                    }
            }
        // TMDB release notes are not BBFC content advice: never turn them into reasons.
        return fallback;
    }

    public static Certificate refresh(Context context, String kind, long id, int season, int episode)
            throws Exception
    {
        com.archos.mediacenter.video.streaming.StreamingRepository.metadata(context, kind, id,
                "tv".equals(kind) ? "content_ratings" : "release_dates");
        return read(context, kind, id, season, episode);
    }

    private static String descriptors(JSONObject row)
    {
        JSONArray values = row.optJSONArray("descriptors");
        java.util.List<String> reasons = new java.util.ArrayList<>();
        int length = 0;
        if (values != null)
            for (int i = 0; i < Math.min(32, values.length()); i++) {
                Object raw = values.opt(i);
                if (!(raw instanceof String))
                    continue;
                String text = ((String)raw).replaceAll("[\\p{Cntrl}]", " ").trim();
                if (!text.isEmpty() && text.length() <= 256 && length + text.length() + 2 <= 2048) {
                    reasons.add(text);
                    length += text.length() + 2;
                }
            }
        return String.join(", ", reasons);
    }

    public static synchronized void remember(Context context, String kind, long id, JSONObject data)
    {
        String identity = key(kind, id);
        if (data == null || data.optLong("id", -1) != id)
            throw new IllegalArgumentException("Certificate title identity differs");
        Certificate value = parse(data, "tv".equals(kind));
        Certificate old = readValue(context, identity);
        if (value != null && old != null && old.rating().equals(value.rating()) && value.reasons().isEmpty())
            value = old;
        write(context, identity, value);
    }

    /** Verified content-advice producer hook. Null clears this exact title/episode; no territory guessing. */
    public static synchronized void verifiedAdvice(Context context, String kind, long id, int season, int episode,
                                      Certificate certificate)
    {
        write(context, episodeKey(kind, id, season, episode), certificate);
    }

    public static synchronized Certificate read(Context context, String kind, long id, int season, int episode)
    {
        String identity = episodeKey(kind, id, season, episode);
        Certificate specific = readValue(context, identity);
        if (specific != null || episode < 0)
            return specific;
        Certificate series = readValue(context, key(kind, id));
        return series == null ? null : new Certificate(series.rating(), series.reasons(), true);
    }

    private static Certificate readValue(Context context, String identity)
    {
        try {
            JSONObject value = new JSONObject(context.getSharedPreferences("gb-certificates-v1", Context.MODE_PRIVATE)
                    .getString(identity, ""));
            return new Certificate(value.getString("rating"), value.optString("reasons"), false);
        } catch (JSONException | IllegalArgumentException unavailable) {
            return null;
        }
    }

    private static void write(Context context, String identity, Certificate certificate)
    {
        try {
            String value = certificate == null ? "" : new JSONObject().put("rating", certificate.rating())
                    .put("reasons", certificate.reasons()).toString();
            if (!context.getSharedPreferences("gb-certificates-v1", Context.MODE_PRIVATE).edit()
                    .putString(identity, value).commit())
                throw new IllegalStateException("Certificate persistence unavailable");
        } catch (JSONException invalid) {
            throw new IllegalArgumentException("Invalid certificate", invalid);
        }
    }

    private static String episodeKey(String kind, long id, int season, int episode)
    {
        String identity = key(kind, id);
        if (season == -1 && episode == -1)
            return identity;
        if (!"tv".equals(kind) || season < 0 || episode < 0)
            throw new IllegalArgumentException("Exact episode identity required");
        return identity + ":" + season + ":" + episode;
    }

    private static String key(String kind, long id)
    {
        if (!("movie".equals(kind) || "tv".equals(kind)) || id <= 0)
            throw new IllegalArgumentException("Exact title identity required");
        return kind + ":" + id;
    }

    private AgeCertificates()
    {
    }
}
