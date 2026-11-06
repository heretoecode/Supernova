package com.archos.mediacenter.video.data;

import android.graphics.Bitmap;
import com.squareup.picasso.Picasso;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.Executor;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.function.ToLongFunction;

/** Own beside the Home data model. Warm neighbours before changing the hero; keep the current card until ready. */
public final class HeroArtworkPrefetch<T> implements AutoCloseable
{
    public record Assets(String backdrop, String logo)
    {
    }

    public record Ready<T>(T backdrop, T logo)
    {
    }

    public interface Loader<T>
    {
        T load(String location, boolean logo) throws Exception;
    }

    private final Loader<T> loader;
    private final ToLongFunction<T> size;
    private final long budget;
    private final Executor callbacks;
    private final Runnable changed;
    private final Map<Assets, Ready<T>> ready = new LinkedHashMap<>(4, .75f, true);
    private final class Work
    {
        Future<?> future;
        boolean cancelled;
    }

    private final Map<Assets, Work> tasks = new LinkedHashMap<>();
    private Set<Assets> wanted = Set.of();
    private boolean closed;
    private final ThreadPoolExecutor worker = new ThreadPoolExecutor(2, 2, 30, TimeUnit.SECONDS,
        new ArrayBlockingQueue<>(4), task -> {
            Thread thread = new Thread(task, "HeroArtworkWarmup");
            thread.setDaemon(true);
            return thread;
        });

    public HeroArtworkPrefetch(Loader<T> loader, ToLongFunction<T> size, long budget, Executor main, Runnable changed)
    {
        this.loader = loader;
        this.size = size;
        this.budget = budget;
        callbacks = main;
        this.changed = changed;
    }

    public static HeroArtworkPrefetch<Bitmap> bitmaps(Executor main, Runnable changed)
    {
        return new HeroArtworkPrefetch<>((url, logo) -> Picasso.get().load(url)
            .resize(logo ? 720 : 1920, logo ? 360 : 1080).centerInside().onlyScaleDown().get(),
            Bitmap::getAllocationByteCount, 40L * 1024 * 1024, main, changed);
    }

    public synchronized Ready<T> ready(Assets assets)
    {
        return ready.get(assets);
    }

    public synchronized void warm(List<Assets> neighbours)
    {
        if (closed)
            return;
        wanted = new HashSet<>(neighbours.subList(0, Math.min(3, neighbours.size())));
        for (Assets previous : List.copyOf(tasks.keySet()))
            if (!wanted.contains(previous))
            {
                Work work = tasks.remove(previous);
                work.cancelled = true;
                work.future.cancel(true);
            }
        for (Assets assets : wanted)
        {
            if (assets.backdrop() == null || assets.backdrop().trim().isEmpty() || ready.containsKey(assets) ||
                tasks.containsKey(assets))
                continue;
            try
            {
                Work work = new Work();
                tasks.put(assets, work);
                work.future = worker.submit(() -> load(assets, work));
            }
            catch (java.util.concurrent.RejectedExecutionException busy)
            {
                tasks.remove(assets);
                // At most three requested neighbours; an old interrupted transport may still be unwinding.
            }
        }
    }

    private void load(Assets assets, Work work)
    {
        try
        {
            T backdrop = loader.load(assets.backdrop(), false);
            T logo = assets.logo() == null || assets.logo().trim().isEmpty() ? null : loader.load(assets.logo(), true);
            if (backdrop == null || assets.logo() != null && !assets.logo().trim().isEmpty() && logo == null)
                return;
            synchronized (this)
            {
                if (closed || work.cancelled || tasks.get(assets) != work || !wanted.contains(assets) ||
                    Thread.currentThread().isInterrupted())
                    return;
                ready.put(assets, new Ready<>(backdrop, logo));
                long bytes = 0;
                for (Ready<T> value : ready.values())
                    bytes += Math.max(0, size.applyAsLong(value.backdrop())) +
                             (value.logo() == null ? 0 : Math.max(0, size.applyAsLong(value.logo())));
                while (bytes > budget || ready.size() > 3)
                {
                    Ready<T> removed = ready.remove(ready.keySet().iterator().next());
                    bytes -= Math.max(0, size.applyAsLong(removed.backdrop())) +
                             (removed.logo() == null ? 0 : Math.max(0, size.applyAsLong(removed.logo())));
                }
            }
            callbacks.execute(() -> {
                synchronized (HeroArtworkPrefetch.this)
                {
                    if (!closed && wanted.contains(assets) && ready.containsKey(assets))
                        changed.run();
                }
            });
        }
        catch (Exception unavailable)
        {
            // Failed warmup keeps the current hero. There is no empty-frame swap.
        }
        finally
        {
            synchronized (this)
            {
                if (tasks.get(assets) == work)
                    tasks.remove(assets);
            }
        }
    }

    @Override public synchronized void close()
    {
        closed = true;
        tasks.values().forEach(task -> {
            task.cancelled = true;
            task.future.cancel(true);
        });
        tasks.clear();
        ready.clear();
        worker.shutdownNow();
    }
}
