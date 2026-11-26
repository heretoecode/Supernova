package com.archos.mediacenter.video.leanback.search;

import android.content.Context;
import android.content.SharedPreferences;
import java.util.HashMap;
import java.util.Map;

/**
 * People's photos, remembered when a Details page shows them (TMDB's profile picture), so Search's landing can show
 * People in your library with photos without asking the network (SEARCH.md: initials when no photo). Small: one
 * address per name, the newest few hundred kept.
 */
public final class PersonPhotos
{
    static final String STORE = "supernova_person_photos";
    static final int MOST = 400;

    private PersonPhotos()
    {
    }

    /** Remembers a person's photo address (an https TMDB image), by name. */
    public static void remember(Context context, String name, String url)
    {
        if (context == null || name == null || name.trim().isEmpty() || url == null || !url.startsWith("https://"))
        {
            return;
        }
        SharedPreferences store = context.getSharedPreferences(STORE, Context.MODE_PRIVATE);
        if (url.equals(store.getString(name.trim(), null)))
        {
            return;
        }
        if (store.getAll().size() >= MOST)
        {
            store.edit().clear().apply();
        }
        store.edit().putString(name.trim(), url).apply();
    }

    /** Every remembered photo, name → address. */
    static Map<String, String> all(Context context)
    {
        Map<String, String> result = new HashMap<>();
        for (Map.Entry<String, ?> entry : context.getSharedPreferences(STORE, Context.MODE_PRIVATE).getAll().entrySet())
        {
            if (entry.getValue() instanceof String)
            {
                result.put(entry.getKey(), (String) entry.getValue());
            }
        }
        return result;
    }
}
