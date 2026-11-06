package com.archos.mediacenter.video.metadata;

import android.content.Context;
import android.net.Uri;
import com.archos.mediacenter.video.streaming.StreamingRepository;
import com.archos.mediascraper.preprocess.MovieSearchInfo;
import com.archos.mediascraper.preprocess.SearchInfo;
import com.archos.mediascraper.preprocess.SearchPreprocessor;
import com.archos.mediascraper.preprocess.TvShowSearchInfo;
import java.io.File;
import java.io.InterruptedIOException;
import java.text.Normalizer;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.Executor;
import java.util.concurrent.Future;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import okhttp3.Call;
import okhttp3.OkHttpClient;
import org.json.JSONArray;
import org.json.JSONObject;

/** Read-only filename matching. Never invokes scraping, indexing or FixMatchRepository.apply. */
public final class UnlibraryLookup
{
    public enum Kind
    {
        FILM, EPISODE, SHOW, UNKNOWN
    }

    public enum Confidence
    {
        CONFIDENT, POSSIBLE, NONE
    }

    public static final class Result
    {
        public final Kind kind;
        public final Confidence confidence;
        public final long tmdbId;
        public final String title, year;
        public final int season, episode;
        public final Uri poster, backdrop;

        private Result(Kind kind, Confidence confidence, JSONObject row, int season, int episode)
        {
            this.kind = kind;
            this.confidence = confidence;
            tmdbId = row.optLong("id");
            title = row.optString(kind == Kind.FILM ? "title" : "name");
            String date = row.optString(kind == Kind.FILM ? "release_date" : "first_air_date");
            year = date.matches("[0-9]{4}.*") ? date.substring(0, 4) : "";
            this.season = season;
            this.episode = episode;
            poster = image(row.optString("poster_path"), "w342");
            backdrop = image(row.optString("backdrop_path"), "w780");
        }
    }

    public static final Result NONE = new Result(Kind.UNKNOWN, Confidence.NONE, new JSONObject(), -1, -1);
    private static final long DEADLINE_SECONDS = 12;
    private static final ThreadPoolExecutor WORK = new ThreadPoolExecutor(
        2, 2, 30, TimeUnit.SECONDS, new ArrayBlockingQueue<>(4), task -> {
            Thread thread = new Thread(task, "FilenameLookup");
            thread.setDaemon(true);
            return thread;
        });
    private static final ScheduledThreadPoolExecutor CLOCK = new ScheduledThreadPoolExecutor(1, task -> {
        Thread thread = new Thread(task, "FilenameLookupDeadline");
        thread.setDaemon(true);
        return thread;
    });
    private static final OkHttpClient HTTP = new OkHttpClient.Builder()
        .connectTimeout(4, TimeUnit.SECONDS).readTimeout(6, TimeUnit.SECONDS)
        .callTimeout(DEADLINE_SECONDS, TimeUnit.SECONDS).build();

    static
    {
        CLOCK.setRemoveOnCancelPolicy(true);
    }

    public static final class Job implements AutoCloseable
    {
        private boolean cancelled, finished;
        private Future<?> work;
        private ScheduledFuture<?> deadline;
        private Call call;
        private final Executor callbacks;
        private final Consumer<Result> consumer;

        private Job(Executor callbacks, Consumer<Result> consumer)
        {
            this.callbacks = java.util.Objects.requireNonNull(callbacks);
            this.consumer = java.util.Objects.requireNonNull(consumer);
        }

        public synchronized boolean isCancelled()
        {
            return cancelled;
        }

        public synchronized void cancel()
        {
            cancelled = true;
            stop();
        }

        @Override
        public void close()
        {
            cancel();
        }

        private synchronized void stop()
        {
            if (call != null)
                call.cancel();
            if (work != null)
                work.cancel(true);
            if (deadline != null)
                deadline.cancel(false);
        }

        private synchronized void attach(Call next) throws InterruptedIOException
        {
            if (cancelled || finished)
            {
                next.cancel();
                throw new InterruptedIOException("Lookup ended");
            }
            call = next;
        }

        private synchronized void complete(Result result)
        {
            if (cancelled || finished)
                return;
            finished = true;
            stop();
            try
            {
                callbacks.execute(() -> {
                    synchronized (Job.this)
                    {
                        if (!cancelled)
                            consumer.accept(result);
                    }
                });
            }
            catch (RejectedExecutionException unavailable)
            {
                // The owner has disposed its callback executor.
            }
        }
    }

    public static Job start(Context context, Uri fileUri, Executor callbackExecutor, Consumer<Result> consumer)
    {
        Context app = context.getApplicationContext();
        return start(fileUri, callbackExecutor, consumer, job -> {
            OkHttpClient client = HTTP.newBuilder().addInterceptor(chain -> {
                job.attach(chain.call());
                return chain.proceed(chain.request());
            }).build();
            return (path, parameters) -> StreamingRepository.matchSearch(app, path, parameters, client);
        }, DEADLINE_SECONDS, TimeUnit.SECONDS);
    }

    interface TransportFactory
    {
        FixMatchRepository.Transport create(Job job);
    }

    static Job start(Uri uri, Executor callbacks, Consumer<Result> consumer, TransportFactory factory,
                     long timeout, TimeUnit unit)
    {
        Job job = new Job(callbacks, consumer);
        synchronized (job)
        {
            job.deadline = CLOCK.schedule(() -> job.complete(NONE), timeout, unit);
            try
            {
                job.work = WORK.submit(() -> {
                    Result result = NONE;
                    try
                    {
                        result = lookup(uri, factory.create(job));
                    }
                    catch (Exception unavailable)
                    {
                        // Offline, rate-limited, malformed and unavailable results all have no match.
                    }
                    job.complete(result);
                });
            }
            catch (RejectedExecutionException busy)
            {
                job.complete(NONE);
            }
        }
        return job;
    }

    static Result lookup(Uri uri, FixMatchRepository.Transport transport) throws Exception
    {
        if (uri == null || uri.getLastPathSegment() == null)
            return NONE;
        // Only the filename is parsed or sent to TMDB; hosts, credentials and parent paths are discarded.
        String leaf = uri.getLastPathSegment();
        if (leaf.length() > 240 || leaf.isBlank())
            return NONE;
        Uri name = Uri.fromFile(new File("/" + leaf));
        SearchInfo info = SearchPreprocessor.instance().parseFileBased(name, name);
        if (info == null || info.skipScraping || Thread.currentThread().isInterrupted())
            return NONE;
        String title;
        String year;
        int season = -1;
        int episode = -1;
        if (info instanceof TvShowSearchInfo)
        {
            TvShowSearchInfo show = (TvShowSearchInfo) info;
            title = show.getShowName();
            year = show.getFirstAiredYear();
            season = show.getSeason();
            episode = show.getEpisode();
        }
        else
        {
            MovieSearchInfo movie = (MovieSearchInfo) info;
            title = movie.getName();
            year = movie.getYear();
        }
        if (title == null || normal(title).isEmpty())
            return NONE;
        String scope = info.isTvShow() ? "tv" : "movie";
        JSONArray rows = transport.request("search/" + scope, Map.of("query", title)).getJSONArray("results");
        List<JSONObject> valid = new ArrayList<>();
        List<JSONObject> exact = new ArrayList<>();
        for (int i = 0; i < Math.min(rows.length(), 40); i++)
        {
            JSONObject row = rows.optJSONObject(i);
            if (row == null || row.optLong("id") < 1 || row.optLong("id") > Integer.MAX_VALUE)
                continue;
            String candidate = row.optString(info.isTvShow() ? "name" : "title");
            if (candidate.isBlank())
                continue;
            valid.add(row);
            String date = row.optString(info.isTvShow() ? "first_air_date" : "release_date");
            if (normal(title).equals(normal(candidate)) && (year == null || year.isEmpty() || date.startsWith(year)))
                exact.add(row);
        }
        if (valid.isEmpty())
            return NONE;
        JSONObject chosen = exact.isEmpty() ? valid.get(0) : exact.get(0);
        Kind kind = info.isTvShow() ? season >= 0 && episode > 0 ? Kind.EPISODE : Kind.SHOW : Kind.FILM;
        Confidence confidence = exact.size() == 1 ? Confidence.CONFIDENT : Confidence.POSSIBLE;
        return new Result(kind, confidence, chosen, season, episode);
    }

    private static String normal(String title)
    {
        return Normalizer.normalize(title, Normalizer.Form.NFKC).toLowerCase(Locale.ROOT)
            .replaceAll("[^\\p{L}\\p{N}]+", " ").trim();
    }

    private static Uri image(String path, String size)
    {
        return path.matches("/[A-Za-z0-9._-]+") ? Uri.parse("https://image.tmdb.org/t/p/" + size + path) : null;
    }

    private UnlibraryLookup()
    {
    }
}
