package com.archos.mediacenter.video.scanning;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.Executor;
import java.util.function.Consumer;

/** Process-owned scan activity. Subscribers supply their main executor; screens never need a timer. */
public final class ScanActivitySignal
{
    public static final ScanActivitySignal LIBRARY = new ScanActivitySignal();
    private final Set<String> active = new HashSet<>();
    private final Map<Long, Subscription> subscriptions = new LinkedHashMap<>();
    private long sequence;
    private volatile long version;

    public final class Subscription implements AutoCloseable
    {
        private final long id;
        private final Executor executor;
        private final Consumer<Boolean> listener;
        private boolean closed;

        private Subscription(long id, Executor executor, Consumer<Boolean> listener)
        {
            this.id = id;
            this.executor = executor;
            this.listener = listener;
        }

        private void dispatch(long revision, boolean running)
        {
            executor.execute(() -> {
                synchronized (ScanActivitySignal.this)
                {
                    if (!closed && revision == version)
                        listener.accept(running);
                }
            });
        }

        @Override public void close()
        {
            synchronized (this)
            {
                closed = true;
            }
            synchronized (ScanActivitySignal.this)
            {
                subscriptions.remove(id);
            }
        }
    }

    public synchronized boolean running()
    {
        return !active.isEmpty();
    }

    public Subscription observe(Executor mainExecutor, Consumer<Boolean> listener)
    {
        Subscription subscription;
        long revision;
        boolean running;
        synchronized (this)
        {
            subscription = new Subscription(++sequence, mainExecutor, listener);
            subscriptions.put(subscription.id, subscription);
            revision = version;
            running = running();
        }
        subscription.dispatch(revision, running);
        return subscription;
    }

    /** Owners use stable domain keys, not private source addresses. Terminal paths must clear their own key. */
    public void set(String owner, boolean running)
    {
        long revision;
        boolean value;
        java.util.List<Subscription> targets;
        synchronized (this)
        {
            boolean before = running();
            if (running)
                active.add(owner);
            else
                active.remove(owner);
            value = running();
            if (before == value)
                return;
            revision = ++version;
            targets = java.util.List.copyOf(subscriptions.values());
        }
        for (Subscription subscription : targets)
        {
            try
            {
                subscription.dispatch(revision, value);
            }
            catch (RuntimeException unavailable)
            {
                subscription.close();
            }
        }
    }

}
