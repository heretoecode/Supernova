package com.archos.mediacenter.video.data;

import android.content.SharedPreferences;
import java.util.ArrayList;
import java.util.List;

/** Shares the existing Search key; one instance can serve Search and Settings. No network or JSON. */
public final class RecentSearches
{
    private static final String KEY = "preview_search_recent";
    private static final Object LOCK = new Object();
    private final SharedPreferences preferences;

    public RecentSearches(SharedPreferences preferences)
    {
        this.preferences = preferences;
    }

    public List<String> read()
    {
        synchronized (LOCK)
        {
            List<String> result = new ArrayList<>();
            for (String value : preferences.getString(KEY, "").split("\\n"))
                if (!value.trim().isEmpty() && result.size() < 8)
                    result.add(value);
            return List.copyOf(result);
        }
    }

    public void remember(String query)
    {
        String clean = query == null ? "" : query.replace('\n', ' ').replace('\r', ' ').trim();
        if (clean.isEmpty() || clean.length() > 240)
            return;
        synchronized (LOCK)
        {
            List<String> values = new ArrayList<>(read());
            values.removeIf(value -> value.equalsIgnoreCase(clean));
            values.add(0, clean);
            preferences.edit().putString(KEY, android.text.TextUtils.join("\n", values.subList(0, Math.min(8, values.size())))).apply();
        }
    }

    public void clear()
    {
        synchronized (LOCK)
        {
            preferences.edit().remove(KEY).apply();
        }
    }
}
