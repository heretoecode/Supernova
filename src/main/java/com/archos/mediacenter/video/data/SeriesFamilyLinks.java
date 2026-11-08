package com.archos.mediacenter.video.data;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.function.BooleanSupplier;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.json.JSONArray;
import org.json.JSONObject;

/** Explicit public catalogue relationships, not creator overlap or recommendations. No API/account credential. */
public final class SeriesFamilyLinks
{
    public static String query(long tmdbId)
    {
        if (tmdbId <= 0 || tmdbId > Integer.MAX_VALUE)
            throw new IllegalArgumentException("Exact TV identity required");
        String edge = "(wdt:P2512|^wdt:P2512|wdt:P155|^wdt:P155|wdt:P156|^wdt:P156|" +
                      "wdt:P8345/^wdt:P8345)";
        return "SELECT DISTINCT ?seedId ?id WHERE { ?seed wdt:P4983 ?seedId . FILTER(?seedId=\"" + tmdbId +
            "\") ?seed " + edge + "/" + edge + "? ?relative . ?relative wdt:P4983 ?id . " +
            "FILTER(?id != ?seedId) } LIMIT 40";
    }

    public static List<Long> parse(long tmdbId, JSONObject response)
    {
        JSONObject results = response.optJSONObject("results");
        JSONArray rows = results == null ? null : results.optJSONArray("bindings");
        LinkedHashSet<Long> ids = new LinkedHashSet<>();
        for (int i = 0; rows != null && i < Math.min(40, rows.length()); i++)
        {
            JSONObject row = rows.optJSONObject(i);
            JSONObject seed = row == null ? null : row.optJSONObject("seedId");
            JSONObject target = row == null ? null : row.optJSONObject("id");
            if (seed == null || target == null || !Long.toString(tmdbId).equals(seed.optString("value")) ||
                !target.optString("value").matches("[1-9][0-9]{0,9}"))
                continue;
            long id = Long.parseLong(target.optString("value"));
            if (id != tmdbId && id <= Integer.MAX_VALUE)
                ids.add(id);
        }
        return List.copyOf(ids);
    }

    /** Worker-only, caller dispatcher owns cancellation. At most 512 KiB and eight seconds for this request. */
    public static List<Long> load(long tmdbId, OkHttpClient base, BooleanSupplier cancelled) throws Exception
    {
        if (cancelled.getAsBoolean())
            return List.of();
        HttpUrl url = HttpUrl.get("https://query.wikidata.org/sparql").newBuilder()
            .addQueryParameter("query", query(tmdbId)).addQueryParameter("format", "json").build();
        Request request = new Request.Builder().url(url).header("Accept", "application/sparql-results+json")
            .header("User-Agent", "Supernova/0.157 (https://github.com/heretoecode/Supernova)").build();
        OkHttpClient client = base.newBuilder().callTimeout(8, TimeUnit.SECONDS)
            .followRedirects(false).followSslRedirects(false).build();
        try (Response response = client.newCall(request).execute())
        {
            if (!response.isSuccessful() || response.body() == null)
                throw new IOException("Family catalogue unavailable");
            try (InputStream input = response.body().byteStream();
                 ByteArrayOutputStream output = new ByteArrayOutputStream())
            {
                byte[] buffer = new byte[8192];
                int count;
                while ((count = input.read(buffer)) != -1)
                {
                    if (cancelled.getAsBoolean() || Thread.currentThread().isInterrupted())
                        return List.of();
                    if (output.size() + count > 512 * 1024)
                        throw new IOException("Family catalogue exceeds bound");
                    output.write(buffer, 0, count);
                }
                return parse(tmdbId, new JSONObject(output.toString(StandardCharsets.UTF_8.name())));
            }
        }
    }

    private SeriesFamilyLinks()
    {
    }
}
