package com.archos.mediacenter.video.about;

import android.content.Context;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/** Offline credit data. Call load off the UI thread; textAsset contains the bundled full notices. */
public final class AboutCredits
{
    public static final String ASSET = "supernova/credits.json";

    public static final class Entry
    {
        public final String id, name, version, licence, url, role, textAsset, licenceSource;
        public final boolean service;

        private Entry(JSONObject row) throws JSONException
        {
            id = row.getString("id");
            name = row.getString("name");
            version = row.getString("version");
            licence = row.getString("licence");
            url = row.getString("url");
            role = row.getString("role");
            textAsset = row.optString("text_asset");
            licenceSource = row.optString("licence_source");
            service = "service".equals(row.optString("kind"));
            if (id.isEmpty() || name.isEmpty() || version.isEmpty() || licence.isEmpty() || role.isEmpty()
                || !url.startsWith("https://") || (textAsset.isEmpty() && licenceSource.isEmpty()))
            {
                throw new JSONException("Incomplete credit");
            }
        }
    }

    public static List<Entry> load(Context context) throws IOException
    {
        try (InputStream input = context.getAssets().open(ASSET))
        {
            return read(input);
        }
    }

    /** Does not close the caller's stream. A malformed catalogue fails as a whole. */
    public static List<Entry> read(InputStream input) throws IOException
    {
        try
        {
            JSONObject document = new JSONObject(new String(input.readAllBytes(), StandardCharsets.UTF_8));
            if (document.getInt("schema") != 1)
                throw new JSONException("Unsupported credit schema");
            JSONArray rows = document.getJSONArray("entries");
            List<Entry> result = new ArrayList<>();
            java.util.Set<String> identities = new java.util.HashSet<>();
            for (int i = 0; i < rows.length(); i++)
            {
                Entry entry = new Entry(rows.getJSONObject(i));
                if (!identities.add(entry.id))
                    throw new JSONException("Duplicate credit");
                result.add(entry);
            }
            return Collections.unmodifiableList(result);
        }
        catch (JSONException invalid)
        {
            throw new IOException("Invalid credits catalogue", invalid);
        }
    }

    private AboutCredits()
    {
    }
}
