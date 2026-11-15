package com.archos.mediacenter.video.scanning;

import java.util.ArrayDeque;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

/** Bounded saved-source refresh. Probe owns an isolated, cancellable connection, never a playback session. */
public final class ConnectionReachability implements AutoCloseable
{
    public record Status(boolean reachable, long lastSeenUtcMs, boolean checking)
    {
    }

    public interface Probe
    {
        AutoCloseable start(String id, Consumer<Boolean> completed) throws Exception;
    }

    private static final ScheduledExecutorService CLOCK = Executors.newSingleThreadScheduledExecutor(task -> {
        Thread thread = new Thread(task, "ConnectionReachabilityDeadline");
        thread.setDaemon(true);
        return thread;
    });
    private final Executor worker;
    private final Probe probe;
    private final Consumer<Map<String, Status>> persist;
    private final Map<String, Status> statuses = new LinkedHashMap<>();
    private final Map<String, Attempt> active = new LinkedHashMap<>();
    private final Queue<String> waiting = new ArrayDeque<>();
    private boolean closed;
    private boolean pumping;
    private long generation;
    private final long timeoutMs;

    private final class Attempt
    {
        final long generation;
        AutoCloseable resource;
        java.util.concurrent.ScheduledFuture<?> deadline;
        boolean done;

        Attempt(long generation)
        {
            this.generation = generation;
        }
    }

    public ConnectionReachability(Executor worker, Probe probe, Map<String, Status> saved,
                                   Consumer<Map<String, Status>> persist)
    {
        this(worker, probe, saved, persist, 25000);
    }

    ConnectionReachability(Executor worker, Probe probe, Map<String, Status> saved,
                           Consumer<Map<String, Status>> persist, long timeoutMs)
    {
        this.worker = worker;
        this.probe = probe;
        this.persist = persist;
        this.timeoutMs = timeoutMs;
        saved.forEach((id, status) -> statuses.put(id, new Status(false, status.lastSeenUtcMs(), false)));
    }

    public synchronized Map<String, Status> snapshot()
    {
        return Map.copyOf(statuses);
    }

    public synchronized void refresh(List<String> savedConnectionIds)
    {
        if (closed)
            return;
        generation++;
        for (Attempt attempt : active.values())
            release(attempt);
        active.clear();
        waiting.clear();
        statuses.keySet().retainAll(savedConnectionIds);
        for (String id : new java.util.LinkedHashSet<>(savedConnectionIds))
        {
            Status previous = statuses.get(id);
            statuses.put(id, new Status(false, previous == null ? 0 : previous.lastSeenUtcMs(), true));
            waiting.add(id);
        }
        pump();
    }

    private void pump()
    {
        if (pumping)
            return;
        pumping = true;
        try
        {
            while (!closed && active.size() < 2 && !waiting.isEmpty())
            {
                String id = waiting.remove();
                Attempt attempt = new Attempt(generation);
                active.put(id, attempt);
                attempt.deadline = CLOCK.schedule(() -> finish(id, attempt, false), timeoutMs, TimeUnit.MILLISECONDS);
                try
                {
                    worker.execute(() -> {
                        try
                        {
                            synchronized (ConnectionReachability.this)
                            {
                                if (attempt.done || attempt.generation != generation || closed)
                                    return;
                            }
                            AutoCloseable resource = probe.start(id, result -> finish(id, attempt, result));
                            synchronized (ConnectionReachability.this)
                            {
                                attempt.resource = resource;
                                if (attempt.done)
                                    release(attempt);
                            }
                        }
                        catch (Exception unavailable)
                        {
                            finish(id, attempt, false);
                        }
                    });
                }
                catch (RuntimeException unavailable)
                {
                    finish(id, attempt, false);
                }
            }
        }
        finally
        {
            pumping = false;
        }
    }

    private synchronized void finish(String id, Attempt attempt, boolean reachable)
    {
        if (closed || attempt.done || attempt.generation != generation)
            return;
        release(attempt);
        active.remove(id);
        Status previous = statuses.get(id);
        statuses.put(id, new Status(reachable, reachable ? System.currentTimeMillis() : previous.lastSeenUtcMs(),
                                    false));
        Map<String, Status> value = snapshot();
        try
        {
            worker.execute(() -> persist.accept(value));
        }
        finally
        {
            pump();
        }
    }

    private static void release(Attempt attempt)
    {
        attempt.done = true;
        if (attempt.deadline != null)
            attempt.deadline.cancel(false);
        if (attempt.resource != null)
        {
            try
            {
                attempt.resource.close();
            }
            catch (Exception ignored)
            {
                // A failed close cannot change the already recorded connection result.
            }
        }
    }

    @Override public synchronized void close()
    {
        closed = true;
        generation++;
        waiting.clear();
        for (Attempt attempt : active.values())
            release(attempt);
        active.clear();
    }
}
