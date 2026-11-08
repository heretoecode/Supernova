package com.archos.mediacenter.video.data;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.Executor;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.function.BooleanSupplier;
import java.util.function.Consumer;

/** One owner per Search page. submit/close are main-thread safe; all discovery runs on the bounded worker. */
public final class LibrarySearchSession implements AutoCloseable
{
    private record Key(String query, List<LibrarySearch.Title> library, String serviceScope)
    {
    }

    private record Cached(long at, LibrarySearch.Result result)
    {
    }

    private final LibrarySearch.Sources sources;
    private final BooleanSupplier enabled;
    private final Executor callbacks;
    private final Map<Key, Cached> cache = new LinkedHashMap<>(24, .75f, true);
    private final ThreadPoolExecutor worker = new ThreadPoolExecutor(1, 1, 30, TimeUnit.SECONDS,
        new ArrayBlockingQueue<>(1), task -> {
            Thread thread = new Thread(task, "LibrarySearch");
            thread.setDaemon(true);
            return thread;
        });
    private long generation;
    private Future<?> work;
    private boolean closed;

    public LibrarySearchSession(LibrarySearch.Sources sources, BooleanSupplier enabled, Executor callbacks)
    {
        this.sources = sources;
        this.enabled = enabled;
        this.callbacks = callbacks;
    }

    /** serviceScope must include country and selected service IDs; replace snapshot after any library change. */
    public synchronized void submit(String query, List<LibrarySearch.Title> library, String serviceScope,
                                     Consumer<LibrarySearch.Result> listener)
    {
        if (closed)
            return;
        long request = ++generation;
        if (work != null)
            work.cancel(true);
        worker.getQueue().clear();
        Key key = new Key(query, List.copyOf(library), serviceScope + ":" + enabled.getAsBoolean());
        work = worker.submit(() -> {
            LibrarySearch.Result result;
            synchronized (this)
            {
                Cached previous = cache.get(key);
                result = previous != null && System.nanoTime() - previous.at() < TimeUnit.MINUTES.toNanos(10)
                    ? previous.result() : null;
            }
            if (result == null)
            {
                result = LibrarySearch.load(query, key.library(), sources, enabled, () -> stale(request));
                synchronized (this)
                {
                    if (stale(request))
                        return;
                    cache.put(key, new Cached(System.nanoTime(), result));
                    while (cache.size() > 24)
                        cache.remove(cache.keySet().iterator().next());
                }
            }
            LibrarySearch.Result snapshot = result;
            callbacks.execute(() -> {
                synchronized (this)
                {
                    if (!stale(request))
                        listener.accept(enabled.getAsBoolean() ? snapshot
                            : new LibrarySearch.Result(snapshot.library(), List.of(), List.of()));
                }
            });
        });
    }

    private synchronized boolean stale(long request)
    {
        return closed || request != generation;
    }

    @Override public synchronized void close()
    {
        closed = true;
        generation++;
        if (work != null)
            work.cancel(true);
        worker.shutdownNow();
        cache.clear();
    }
}
