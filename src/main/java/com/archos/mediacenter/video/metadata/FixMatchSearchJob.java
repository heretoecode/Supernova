package com.archos.mediacenter.video.metadata;

import android.content.Context;
import com.archos.mediacenter.video.streaming.StreamingRepository;
import com.archos.mediacenter.video.utils.backup.CanonicalMatchMaterial;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import okhttp3.Call;
import okhttp3.OkHttpClient;
import org.json.JSONArray;
import org.json.JSONObject;

/** Main-thread-safe matching job with a hard callback deadline and caller-owned HTTP cancellation. */
public final class FixMatchSearchJob implements AutoCloseable
{
    private static final ThreadPoolExecutor WORK = new ThreadPoolExecutor(2, 2, 30, TimeUnit.SECONDS,
        new ArrayBlockingQueue<>(4), task -> daemon(task, "ForgivingFixMatch"));
    private static final ScheduledExecutorService CLOCK = Executors.newSingleThreadScheduledExecutor(
        task -> daemon(task, "FixMatchDeadline"));
    private final Executor callbacks;
    private final Consumer<List<FixMatchRepository.Candidate>> listener;
    private boolean closed;
    private boolean finished;
    private Future<?> work;
    private ScheduledFuture<?> deadline;
    private Call call;

    private static Thread daemon(Runnable task, String name)
    {
        Thread thread = new Thread(task, name);
        thread.setDaemon(true);
        return thread;
    }

    private FixMatchSearchJob(Executor callbacks, Consumer<List<FixMatchRepository.Candidate>> listener)
    {
        this.callbacks = callbacks;
        this.listener = listener;
    }

    public static FixMatchSearchJob start(Context context, String query, String scope, String currentKind,
                                          long currentId, Executor main,
                                          Consumer<List<FixMatchRepository.Candidate>> listener)
    {
        return start(context, query, scope, currentKind, currentId, main, listener,
                     StreamingRepository.readOnlyNetworkClient(), 12000);
    }

    static FixMatchSearchJob start(Context context, String query, String scope, String currentKind, long currentId,
                                   Executor main, Consumer<List<FixMatchRepository.Candidate>> listener,
                                   OkHttpClient base, long timeoutMs)
    {
        FixMatchSearchJob job = new FixMatchSearchJob(main, listener);
        Context app = context.getApplicationContext();
        OkHttpClient client = base.newBuilder().callTimeout(timeoutMs, TimeUnit.MILLISECONDS)
            .addInterceptor(chain -> {
                synchronized (job)
                {
                    job.call = chain.call();
                    if (job.closed || job.finished)
                        job.call.cancel();
                }
                return chain.proceed(chain.request());
            }).build();
        synchronized (job)
        {
            job.deadline = CLOCK.schedule(() -> job.complete(List.of()), timeoutMs, TimeUnit.MILLISECONDS);
            try
            {
                job.work = WORK.submit(() -> {
                    List<FixMatchRepository.Candidate> result = List.of();
                    try
                    {
                        FixMatchRepository.Transport transport = new FixMatchRepository.Transport() {
                            @Override public JSONObject request(String path, Map<String, String> params)
                                throws Exception
                            {
                                return StreamingRepository.matchSearch(app, path, params, client);
                            }
                            @Override public JSONArray trakt(String path) throws Exception
                            {
                                return StreamingRepository.traktDiscovery(app, path, client);
                            }
                        };
                        result = new FixMatchRepository(app, transport, CanonicalMatchMaterial.PROVIDER)
                            .search(query, scope, currentKind, currentId, job::cancelled);
                    }
                    catch (Exception unavailable)
                    {
                        // A read failure is no candidate, never a mutation or a token-bearing error.
                    }
                    job.complete(result);
                });
            }
            catch (java.util.concurrent.RejectedExecutionException busy)
            {
                job.complete(List.of());
            }
        }
        return job;
    }

    private synchronized boolean cancelled()
    {
        return closed || finished;
    }

    private synchronized void complete(List<FixMatchRepository.Candidate> candidates)
    {
        if (closed || finished)
            return;
        finished = true;
        stop();
        List<FixMatchRepository.Candidate> snapshot = List.copyOf(candidates);
        callbacks.execute(() -> {
            synchronized (FixMatchSearchJob.this)
            {
                if (!closed)
                    listener.accept(snapshot);
            }
        });
    }

    private void stop()
    {
        if (call != null)
            call.cancel();
        if (work != null)
            work.cancel(true);
        if (deadline != null)
            deadline.cancel(false);
    }

    @Override public synchronized void close()
    {
        closed = true;
        stop();
    }
}
