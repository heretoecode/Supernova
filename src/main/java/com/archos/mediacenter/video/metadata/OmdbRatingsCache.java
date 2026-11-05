package com.archos.mediacenter.video.metadata;

import android.content.Context;
import android.util.AtomicFile;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.Callable;
import org.json.JSONObject;

/** Language-independent per-title supplement, persisted alongside the Details enrichment packages. */
final class OmdbRatingsCache
{
    static final long WEEK_MS = 7L * 24 * 60 * 60 * 1000;
    private static final Object LOCK = new Object();
    private final AtomicFile file;

    OmdbRatingsCache(Context context, String kind, long id)
    {
        if (!("movie".equals(kind) || "tv".equals(kind)) || id <= 0)
        {
            throw new IllegalArgumentException("Exact title identity required");
        }
        File directory = new File(context.getFilesDir(), "preview-metadata");
        file = new AtomicFile(new File(directory, kind + "_" + id + "_omdb.json"));
    }

    private JSONObject read()
    {
        try
        {
            if (file.getBaseFile().length() > 131072)
            {
                return new JSONObject();
            }
            return new JSONObject(new String(file.readFully(), StandardCharsets.UTF_8));
        }
        catch (Exception unavailable)
        {
            return new JSONObject();
        }
    }

    private void write(JSONObject state) throws IOException
    {
        FileOutputStream output = file.startWrite();
        try
        {
            output.write(state.toString().getBytes(StandardCharsets.UTF_8));
            file.finishWrite(output);
        }
        catch (IOException failure)
        {
            file.failWrite(output);
            throw failure;
        }
    }

    JSONObject cached()
    {
        synchronized (LOCK)
        {
            return read().optJSONObject("data");
        }
    }

    JSONObject load(long now, Callable<JSONObject> fetch)
    {
        synchronized (LOCK)
        {
            JSONObject state = read();
            JSONObject previous = state.optJSONObject("data");
            long attempted = state.optLong("attempted_at", -1);
            // A backwards clock must not cause repeated requests either.
            if (attempted >= 0 && now - attempted < WEEK_MS)
            {
                return previous;
            }
            try
            {
                state.put("attempted_at", now);
                write(state); // Reserve durably before spending a request, including offline failures.
                JSONObject fresh = fetch.call();
                state.put("data", fresh).put("updated_at", now);
                write(state);
                return fresh;
            }
            catch (Exception unavailable)
            {
                return previous;
            }
        }
    }
}
