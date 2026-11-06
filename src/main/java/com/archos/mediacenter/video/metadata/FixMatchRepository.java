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

        default JSONArray trakt(String path) throws Exception
        {
            return new JSONArray();
        }
    }

    public static final class Candidate
    {
        public final long id;
        public final String title;
        public final String year;
        public final String kind;
        public final String poster;
        public final boolean currentMatch;
        public final int matchScore;
        public final boolean bestMatch;

        Candidate(JSONObject row, String kind, String currentKind, long currentId) throws Exception
        {
            this(row, kind, currentKind, currentId, 0, false);
        }

        Candidate(JSONObject row, String kind, String currentKind, long currentId, int score, boolean best)
            throws Exception
        {
            matchScore = score;
            bestMatch = best;
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
             new Transport() {
                 @Override public JSONObject request(String path, Map<String, String> parameters) throws Exception
                 {
                     return StreamingRepository.matchSearch(context, path, parameters);
                 }

                 @Override public JSONArray trakt(String path) throws Exception
                 {
                     return StreamingRepository.traktDiscovery(context, path,
                                                               StreamingRepository.readOnlyNetworkClient());
                 }
             },
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
        return search(input, scope, currentKind, currentId, () -> false);
    }

    /** Worker-only; cancellation returns no candidates. Existing apply() remains the only mutation. */
    public List<Candidate> search(String query, String scope, String currentKind, long currentId,
                                   BooleanSupplier cancelled) throws Exception
    {
        List<ForgivingMatchSearch.Hit> hits = ForgivingMatchSearch.search(query, scope,
            new ForgivingMatchSearch.Gateway() {
                @Override public JSONObject tmdb(String path, Map<String, String> parameters) throws Exception
                {
                    return transport.request(path, parameters);
                }

                @Override public JSONArray trakt(String path) throws Exception
                {
                    return transport.trakt(path);
                }
            }, cancelled);
        List<Candidate> result = new ArrayList<>();
        for (ForgivingMatchSearch.Hit hit : hits)
        {
            Candidate candidate = new Candidate(hit.row(), hit.kind(), currentKind, currentId,
                                                  hit.score(), hit.bestMatch());
            result.add(candidate);
        }
        return List.copyOf(result);
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
