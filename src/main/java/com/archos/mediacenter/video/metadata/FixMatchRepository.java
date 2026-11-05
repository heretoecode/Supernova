package com.archos.mediacenter.video.metadata;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import com.archos.mediacenter.video.leanback.PreviewLibraryLoader;
import com.archos.mediacenter.video.streaming.StreamingRepository;
import com.archos.mediacenter.video.utils.backup.CanonicalMatchExecutor;
import com.archos.mediacenter.video.utils.backup.CanonicalMatchMaterial;
import com.archos.mediacenter.video.utils.backup.MediaMatchInventory;
import com.archos.mediacenter.video.utils.backup.MediaMatchManualChanges;
import com.archos.mediacenter.video.utils.backup.PortableMediaMatches;
import com.archos.mediaprovider.video.VideoOpenHelper;
import com.archos.mediaprovider.video.VideoStore;
import com.archos.mediascraper.OwnedCanonicalBatch;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BooleanSupplier;
import org.json.JSONArray;
import org.json.JSONObject;

/** Worker-only explicit matching API. Provider material is prepared before an owned native transaction begins. */
public final class FixMatchRepository
{
    public interface Transport
    {
        JSONObject request(String path, Map<String, String> parameters) throws Exception;
    }

    public static final class Candidate
    {
        public final long id;
        public final String title;
        public final String year;
        public final String kind;
        public final String poster;
        public final boolean currentMatch;

        Candidate(JSONObject row, String kind, String currentKind, long currentId) throws Exception
        {
            id = row.getLong("id");
            if (id <= 0 || id > Integer.MAX_VALUE)
                throw new IOException("Invalid match identity");
            this.kind = kind;
            title = row.getString("tv".equals(kind) ? "name" : "title");
            String date = row.optString("tv".equals(kind) ? "first_air_date" : "release_date");
            year = date.matches("[0-9]{4}.*") ? date.substring(0, 4) : "";
            String path = row.optString("poster_path");
            poster = path.startsWith("/") ? "https://image.tmdb.org/t/p/w342" + path : "";
            currentMatch = kind.equals(currentKind) && id == currentId;
        }
    }

    private final Context context;
    private final Transport transport;
    private final CanonicalMatchMaterial.Resolver resolver;

    public FixMatchRepository(Context context)
    {
        this(context,
             (path, parameters)
                 -> StreamingRepository.matchSearch(context, path, parameters),
             CanonicalMatchMaterial.PROVIDER);
    }

    public FixMatchRepository(Context context, Transport transport, CanonicalMatchMaterial.Resolver resolver)
    {
        this.context = context.getApplicationContext();
        this.transport = transport;
        this.resolver = resolver;
    }

    /** Scope is movie/tv/all. Numeric TMDB IDs use scope; explicit tmdb:movie:123 or tmdb:tv:123 removes ambiguity. */
    public List<Candidate> search(String query, String scope, String currentKind, long currentId) throws Exception
    {
        if (!List.of("movie", "tv", "all").contains(scope))
            throw new IOException("Invalid match kind");
        String input = query == null ? "" : query.trim();
        if (input.isEmpty())
            return Collections.emptyList();
        if (input.length() > 240)
            throw new IOException("Match query too long");
        List<Candidate> result = new ArrayList<>();
        if (input.matches("tt[0-9]{7,10}"))
        {
            JSONObject found = transport.request("find/" + input, Map.of("external_source", "imdb_id"));
            if (!scope.equals("tv"))
                add(result, found.getJSONArray("movie_results"), "movie", currentKind, currentId);
            if (!scope.equals("movie"))
                add(result, found.getJSONArray("tv_results"), "tv", currentKind, currentId);
        }
        else
        {
            if (input.matches("tmdb:(movie|tv):[1-9][0-9]{0,9}"))
            {
                String[] identity = input.split(":");
                scope = identity[1];
                input = identity[2];
            }
            for (String kind : List.of("movie", "tv"))
            {
                if (!scope.equals("all") && !scope.equals(kind))
                    continue;
                if (input.matches("[1-9][0-9]{0,9}"))
                {
                    result.add(
                        new Candidate(transport.request(kind + "/" + input, Map.of()), kind, currentKind, currentId));
                }
                else
                {
                    add(result, transport.request("search/" + kind, Map.of("query", input)).getJSONArray("results"),
                        kind, currentKind, currentId);
                }
            }
        }
        return Collections.unmodifiableList(result);
    }

    private static void add(List<Candidate> target, JSONArray rows, String kind, String currentKind, long currentId)
        throws Exception
    {
        for (int i = 0; i < Math.min(40, rows.length()); i++)
        {
            target.add(new Candidate(rows.getJSONObject(i), kind, currentKind, currentId));
        }
    }

    /** TV candidates require the file's known season/episode. This API never guesses episode numbers. */
    public void apply(long videoId, Candidate candidate, int season, int episode, BooleanSupplier cancelled)
        throws Exception
    {
        if (candidate == null || videoId <= 0)
            throw new IOException("Missing match target");
        if ("tv".equals(candidate.kind) && (season < 0 || episode < 1))
            throw new IOException("Episode numbers required");
        PortableMediaMatches.Match match = new PortableMediaMatches.Match(
            "movie".equals(candidate.kind) ? "tmdb:movie:" + candidate.id
                                             : "tmdb:tv:" + candidate.id + ":" + season + ":" + episode,
            null);
        MediaMatchInventory.Owner owner = MediaMatchInventory.capture(context).owners.get(videoId);
        if (owner == null)
            throw new IOException("File no longer indexed");
        Uri file;
        try (SQLiteDatabase read = SQLiteDatabase.openDatabase(VideoOpenHelper.getDatabaseFile(context).getPath(), null,
                                                               SQLiteDatabase.OPEN_READONLY);
             Cursor row =
                 read.query("files", new String[] {"_data"}, "_id=?", new String[] {"" + videoId}, null, null, null))
        {
            if (!row.moveToFirst() || row.isNull(0))
                throw new IOException("File location unavailable");
            file = Uri.parse(row.getString(0)); // Preserve the database's exact raw local path for the ownership guard.
        }
        List<OwnedCanonicalBatch> batches =
            new CanonicalMatchMaterial.Preparation(context).prepare(owner, match, file, resolver);
        if (cancelled.getAsBoolean())
            throw new java.io.InterruptedIOException("Match cancelled");
        MediaMatchManualChanges.mark(context, videoId);
        try (VideoOpenHelper helper = new VideoOpenHelper(context))
        {
            SQLiteDatabase database = helper.getWritableDatabase();
            database.beginTransaction();
            try
            {
                Map<String, Object> before = userState(database, videoId);
                for (OwnedCanonicalBatch batch : batches)
                {
                    CanonicalMatchExecutor.apply(context, database, batch, cancelled);
                }
                if (!before.equals(userState(database, videoId)))
                    throw new IOException("Match operation changed playback state");
                if (cancelled.getAsBoolean() || Thread.currentThread().isInterrupted())
                    throw new java.io.InterruptedIOException("Match cancelled before commit");
                database.setTransactionSuccessful();
            }
            finally
            {
                database.endTransaction();
            }
        }
        PreviewLibraryLoader.invalidateHistoryCache(context);
        context.getContentResolver().notifyChange(VideoStore.Video.Media.EXTERNAL_CONTENT_URI, null);
    }

    private static Map<String, Object> userState(SQLiteDatabase database, long id) throws IOException
    {
        Map<String, Object> state = new LinkedHashMap<>();
        try (Cursor row = database.query("files",
                                         new String[] {"bookmark", "Archos_bookmark", "Archos_lastTimePlayed",
                                                       "Archos_traktSeen", "Archos_hiddenByUser"},
                                         "_id=?", new String[] {"" + id}, null, null, null))
        {
            if (!row.moveToFirst())
                throw new IOException("Match target disappeared");
            for (int i = 0; i < row.getColumnCount(); i++)
            {
                state.put(row.getColumnName(i), row.isNull(i) ? null : row.getLong(i));
            }
        }
        return state;
    }
}
