package com.archos.mediacenter.video.metadata;

import android.content.SharedPreferences;
import androidx.preference.PreferenceManager;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;
import okhttp3.*;
import org.json.*;

/** Exact-ID supplementary ratings only. Production access is deliberately denied pending entitlement. */
public final class OmdbSupplement {
    /** A future binding must use an authorised per-user encrypted key or approved backend, never an APK secret. */
    public interface Access {
        boolean permitted();
        /** Must atomically enforce the account's remaining daily budget; false means no request. */
        boolean acquireRequest();
        String apiKey();
    }
    public interface Transport {
        JSONObject get(HttpUrl url) throws Exception;
    }

    public static final class Ratings {
        public static final Ratings EMPTY = new Ratings("", "", "", "", "");
        public final String imdb, rottenTomatoes, metacritic, awards, imdbVotes, tomatoReviews, metacriticReviews;

        private Ratings(String imdb, String rt, String mc, String awards, String votes) {
            this(imdb, rt, mc, awards, votes, "", "");
        }

        private Ratings(
            String imdb, String rt, String mc, String awards, String votes, String rtCount, String mcCount) {
            this.imdb = imdb;
            rottenTomatoes = rt;
            metacritic = mc;
            this.awards = awards;
            imdbVotes = votes;
            tomatoReviews = rtCount;
            metacriticReviews = mcCount;
        }

        /** Counts supplied by an authorised provider adapter, never derived from another score. */
        public Ratings withReviewCounts(String rt, String mc) {
            return new Ratings(imdb, rottenTomatoes, metacritic, awards, imdbVotes, count(rt), count(mc));
        }

        public boolean hasData(int row) {
            return !value(row).isEmpty() || row == 0 && !imdbVotes.isEmpty() || row == 1 && !tomatoReviews.isEmpty()
                || row == 2 && !metacriticReviews.isEmpty();
        }

        public String countsSignature() {
            return imdbVotes + "|" + tomatoReviews + "|" + metacriticReviews;
        }

        public String value(int row) {
            return row == 0 ? imdb : row == 1 ? rottenTomatoes : row == 2 ? metacritic : awards;
        }
    }

    private static final Access DENIED = new Access() {
        public boolean permitted() {
            return false;
        }
        public boolean acquireRequest() {
            return false;
        }
        public String apiKey() {
            return "";
        }
    };
    /** Kept for callers without a Context: always denied. App code uses {@link #production(android.content.Context)}. */
    public static final OmdbSupplement PRODUCTION = new OmdbSupplement(DENIED, new HttpsTransport());

    /** Settings key holding the owner's own OMDb API key (entered on the device, never bundled in the APK). */
    public static final String PREF_API_KEY = "omdb_api_key";
    /** Stay safely under OMDb's free tier of 1,000 requests per day. */
    static final int DAILY_BUDGET = 900;
    private static final String PREF_BUDGET_DAY = "omdb_budget_day", PREF_BUDGET_USED = "omdb_budget_used";
    private static final Transport SHARED_TRANSPORT = new HttpsTransport();

    /**
     * Owner decision 2 Nov 2026: Reception shows IMDb, Rotten Tomatoes, Metacritic and awards via
     * OMDb using the owner's own key, typed once into Settings. Enabled only when a key is present.
     */
    public static OmdbSupplement production(android.content.Context context) {
        return new OmdbSupplement(new UserKeyAccess(PreferenceManager.getDefaultSharedPreferences(
                                      context.getApplicationContext())),
            SHARED_TRANSPORT);
    }

    /** Per-device key from Settings, with a persisted per-day request budget. */
    static final class UserKeyAccess implements Access {
        private final SharedPreferences prefs;

        UserKeyAccess(SharedPreferences prefs) {
            this.prefs = prefs;
        }

        @Override
        public boolean permitted() {
            return validKey(apiKey());
        }

        @Override
        public String apiKey() {
            String key = prefs.getString(PREF_API_KEY, "");
            return key == null ? "" : key.trim();
        }

        @Override
        public boolean acquireRequest() {
            synchronized (UserKeyAccess.class) {
                // java.time is unavailable on API 23; use the legacy formatter in UTC.
                SimpleDateFormat day = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
                day.setTimeZone(TimeZone.getTimeZone("UTC"));
                String today = day.format(new Date());
                int used = today.equals(prefs.getString(PREF_BUDGET_DAY, "")) ? prefs.getInt(PREF_BUDGET_USED, 0) : 0;
                if (used >= DAILY_BUDGET)
                    return false;
                prefs.edit().putString(PREF_BUDGET_DAY, today).putInt(PREF_BUDGET_USED, used + 1).apply();
                return true;
            }
        }
    }

    static boolean validKey(String key) {
        return key != null && key.matches("[A-Za-z0-9]{8,64}");
    }

    /** Settings validation that catches remote-control typos; blank (ratings off) is allowed. Null means OK. */
    public static String keyProblem(String value) {
        String key = value == null ? "" : value.trim();
        return key.isEmpty() || validKey(key)
            ? null
            : "That doesn't look like an OMDb key. It's the short code of letters and numbers in the omdbapi.com activation email.";
    }

    private final Access access;
    private final Transport transport;

    public OmdbSupplement(Access access, Transport transport) {
        this.access = access;
        this.transport = transport;
    }

    public boolean enabled() {
        return access.permitted();
    }

    /** Call on the existing metadata worker. No title search, logging of URLs/keys, or persistent cache. */
    public Ratings load(String imdbId, String kind) {
        if (!access.permitted() || imdbId == null || !imdbId.matches("tt[0-9]{7,10}")
            || !("movie".equals(kind) || "series".equals(kind) || "episode".equals(kind)))
            return Ratings.EMPTY;
        String key = access.apiKey();
        if (key == null || !key.matches("[A-Za-z0-9]{8,64}") || !access.acquireRequest())
            return Ratings.EMPTY;
        HttpUrl url = HttpUrl.parse("https://www.omdbapi.com/")
                          .newBuilder()
                          .addQueryParameter("apikey", key)
                          .addQueryParameter("i", imdbId)
                          .addQueryParameter("type", kind)
                          .addQueryParameter("r", "json")
                          .build();
        try {
            return parse(transport.get(url), imdbId, kind);
        } catch (Exception unavailable) {
            return Ratings.EMPTY;
        }
    }

    public static Ratings parse(JSONObject data, String imdbId, String kind) {
        if (data == null || !"True".equalsIgnoreCase(data.optString("Response"))
            || !imdbId.equals(data.optString("imdbID")) || !kind.equals(data.optString("Type")))
            return Ratings.EMPTY;
        String imdb = score(data.optString("imdbRating"), 10, "/10"), rt = "",
               mc = score(data.optString("Metascore"), 100, "/100");
        JSONArray rows = data.optJSONArray("Ratings");
        if (rows != null)
            for (int n = 0; n < Math.min(rows.length(), 12); n++) {
                JSONObject row = rows.optJSONObject(n);
                if (row == null)
                    continue;
                String source = row.optString("Source"), value = row.optString("Value");
                if ("Internet Movie Database".equals(source) && value.endsWith("/10"))
                    imdb = score(value.substring(0, value.length() - 3), 10, "/10");
                else if ("Rotten Tomatoes".equals(source) && value.endsWith("%"))
                    rt = score(value.substring(0, value.length() - 1), 100, "%");
                else if ("Metacritic".equals(source) && value.endsWith("/100"))
                    mc = score(value.substring(0, value.length() - 4), 100, "/100");
            }
        String votes = clean(data.optString("imdbVotes"), 32);
        if (!votes.matches("[0-9]{1,12}|[0-9]{1,3}(,[0-9]{3}){1,3}"))
            votes = "";
        return new Ratings(
            imdb, rt, mc, awardText(data.optString("Awards")), votes, count(data.optString("tomatoReviews")), "");
    }

    /** Preserve genuine provider line breaks in award details. */
    private static String awardText(String value) {
        if (value == null)
            return "";
        value = value.replace("\r\n", "\n").replace('\r', '\n').trim();
        if (value.isEmpty() || value.equalsIgnoreCase("N/A") || value.equalsIgnoreCase("null"))
            return "";
        value = value.replaceAll("[\\p{Cntrl}&&[^\\n]]", " ");
        return value.substring(0, Math.min(512, value.length()));
    }

    private static String count(String value) {
        String clean = clean(value, 32);
        return clean.matches("[0-9]{1,12}|[0-9]{1,3}(,[0-9]{3}){1,3}") ? clean : "";
    }

    private static String score(String text, int maximum, String suffix) {
        try {
            if (!text.matches("[0-9]{1,3}(\\.[0-9]{1,2})?"))
                return "";
            double value = Double.parseDouble(text);
            return value >= 0 && value <= maximum ? text + suffix : "";
        } catch (Exception invalid) {
            return "";
        }
    }

    private static String clean(String value, int limit) {
        if (value == null)
            return "";
        value = value.trim();
        return value.isEmpty() || value.equalsIgnoreCase("N/A") || value.equalsIgnoreCase("null")
            ? ""
            : value.replaceAll("[\\p{Cntrl}]", " ").substring(0, Math.min(limit, value.length()));
    }

    private static final class HttpsTransport implements Transport {
        private final OkHttpClient http = new OkHttpClient.Builder()
                                              .connectTimeout(5, TimeUnit.SECONDS)
                                              .readTimeout(8, TimeUnit.SECONDS)
                                              .callTimeout(10, TimeUnit.SECONDS)
                                              .followRedirects(false)
                                              .followSslRedirects(false)
                                              .build();

        public JSONObject get(HttpUrl url) throws Exception {
            try (Response response =
                     http.newCall(new Request.Builder().url(url).header("Accept", "application/json").build())
                         .execute()) {
                if (!response.isSuccessful() || response.body() == null || response.body().contentLength() > 65536)
                    throw new IOException("Supplement unavailable");
                try (InputStream in = response.body().byteStream();
                    ByteArrayOutputStream out = new ByteArrayOutputStream()) {
                    byte[] buffer = new byte[4096];
                    int length;
                    while ((length = in.read(buffer)) != -1) {
                        if (out.size() + length > 65536)
                            throw new IOException("Supplement too large");
                        out.write(buffer, 0, length);
                    }
                    return new JSONObject(out.toString("UTF-8"));
                }
            }
        }
    }
}
