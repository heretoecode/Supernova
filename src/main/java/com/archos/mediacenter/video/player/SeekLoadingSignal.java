package com.archos.mediacenter.video.player;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.function.Consumer;

/** Memory-only per-player seek state. Seek acknowledgements and timers deliberately cannot make a picture ready. */
public final class SeekLoadingSignal
{
    public enum State
    {
        IDLE, WAITING, READY, FAILED, UNAVAILABLE
    }
    public record Snapshot(State state, long positionMs, int chapterIndex, String chapterTitle)
    {
        public boolean loading()
        {
            return state == State.WAITING;
        }
    }
    private final List<Subscription> listeners = new ArrayList<>();
    private Snapshot current = new Snapshot(State.IDLE, -1, -1, "");
    private Object owner;
    private int token;
    private long revision;

    public synchronized Snapshot current()
    {
        return current;
    }

    public synchronized Subscription observe(Executor executor, Consumer<Snapshot> listener)
    {
        Subscription subscription = new Subscription(executor, listener);
        listeners.add(subscription);
        subscription.deliver(current, revision);
        return subscription;
    }

    public final class Subscription implements AutoCloseable
    {
        private final Executor executor;
        private final Consumer<Snapshot> listener;
        private boolean closed;

        private Subscription(Executor executor, Consumer<Snapshot> listener)
        {
            this.executor = executor;
            this.listener = listener;
        }

        private void deliver(Snapshot value, long version)
        {
            try
            {
                executor.execute(() -> {
                    synchronized (SeekLoadingSignal.this)
                    {
                        if (closed || version != revision)
                            return;
                        try
                        {
                            listener.accept(value);
                        }
                        catch (RuntimeException unavailable)
                        {
                            close();
                        }
                    }
                });
            }
            catch (RuntimeException unavailable)
            {
                close();
            }
        }

        @Override public void close()
        {
            synchronized (SeekLoadingSignal.this)
            {
                closed = true;
                listeners.remove(this);
            }
        }
    }

    synchronized int begin(Object backend, long position, List<PlaybackChapters.Chapter> chapters)
    {
        owner = backend;
        int index = -1;
        for (int i = 0; i < chapters.size(); i++)
            if (chapters.get(i).startMs <= position)
                index = i;
        State state = token == Integer.MAX_VALUE ? State.UNAVAILABLE : State.WAITING;
        if (state == State.WAITING)
            token++;
        publish(new Snapshot(state, position, index, index < 0 ? "" : chapters.get(index).title));
        return state == State.WAITING ? token : 0;
    }

    synchronized void result(Object backend, int generation, State state)
    {
        if (backend != owner || generation <= 0 || generation != token || current.state != State.WAITING)
            return;
        publish(new Snapshot(state, current.positionMs, current.chapterIndex, current.chapterTitle));
    }

    synchronized void reset()
    {
        owner = null;
        publish(new Snapshot(State.IDLE, -1, -1, ""));
    }

    private void publish(Snapshot value)
    {
        current = value;
        long version = ++revision;
        for (Subscription subscription : List.copyOf(listeners))
            subscription.deliver(value, version);
    }
}
