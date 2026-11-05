package com.archos.mediacenter.video.streaming;

import android.content.SharedPreferences;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Callable;
import org.json.JSONArray;
import org.json.JSONObject;

/** Persisted country-specific catalogue; a failed refresh never discards the previous usable list. */
final class ProviderCatalogueCache
{
    static final long WEEK_MS = 7L * 24 * 60 * 60 * 1000;

    static String updatedKey(String country)
    {
        return "streaming_catalogue_updated_" + country;
    }

    static long lastUpdated(SharedPreferences prefs, String country)
    {
        return prefs.getLong(updatedKey(country), 0);
    }

    static synchronized List<StreamingRepository.Provider> load(SharedPreferences prefs, String country, long now,
                                                                Callable<List<StreamingRepository.Provider>> fetch)
        throws Exception
    {
        String key = "streaming_catalogue_" + country;
        List<StreamingRepository.Provider> cached = null;
        try
        {
            if (prefs.contains(key))
            {
                JSONArray rows = new JSONArray(prefs.getString(key, "[]"));
                cached = new ArrayList<>();
                for (int i = 0; i < rows.length(); i++)
                {
                    JSONObject row = rows.getJSONObject(i);
                    cached.add(new StreamingRepository.Provider(row.getInt("id"), row.getString("name"),
                                                                row.optString("logo")));
                }
            }
        }
        catch (Exception invalid)
        {
            cached = null;
        }
        long updated = lastUpdated(prefs, country);
        if (cached != null && updated > 0 && now >= updated && now - updated < WEEK_MS)
        {
            return Collections.unmodifiableList(cached);
        }
        try
        {
            List<StreamingRepository.Provider> fresh = fetch.call();
            JSONArray rows = new JSONArray();
            for (StreamingRepository.Provider provider : fresh)
            {
                rows.put(new JSONObject().put("id", provider.id).put("name", provider.name).put("logo", provider.logo));
            }
            if (!prefs.edit().putString(key, rows.toString()).putLong(updatedKey(country), now).commit())
            {
                throw new java.io.IOException("Provider catalogue persistence unavailable");
            }
            return Collections.unmodifiableList(new ArrayList<>(fresh));
        }
        catch (Exception unavailable)
        {
            if (cached != null)
                return Collections.unmodifiableList(cached);
            throw unavailable;
        }
    }

    private ProviderCatalogueCache()
    {
    }
}
