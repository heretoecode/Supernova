package com.archos.mediacenter.video.streaming.putio;

import android.content.Context;
import com.archos.mediacenter.video.R;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

/** Headless adapter for the existing out-of-band code flow. No credential is part of the public snapshot. */
public final class PutioCodeSignIn implements AutoCloseable
{
    public enum State
    {
        WAITING, SIGNED_IN, EXPIRED, FAILED
    }

    public record Code(String code, String link)
    {
    }

    public record Snapshot(State state, String code, String link)
    {
    }

    interface Provider
    {
        Code begin() throws Exception;
        String poll(Code code) throws Exception;
        void save(String token) throws Exception;
    }

    private static ScheduledExecutorService executor(String name)
    {
        return Executors.newSingleThreadScheduledExecutor(task -> {
            Thread thread = new Thread(task, name);
            thread.setDaemon(true);
            return thread;
        });
    }

    private final ScheduledExecutorService worker = executor("PutioCodeLink");
    private final ScheduledExecutorService clock = executor("PutioCodeExpiry");
    private final Provider provider;
    private final Executor callbacks;
    private final Consumer<Snapshot> listener;
    private final long pollMs;
    private boolean closed;
    private Snapshot snapshot = new Snapshot(State.WAITING, "", "");
    private ScheduledFuture<?> pending;
    private ScheduledFuture<?> expiry;

    public static PutioCodeSignIn start(Context context, Executor main, Consumer<Snapshot> listener)
    {
        Context app = context.getApplicationContext();
        return new PutioCodeSignIn(new Provider() {
            private PutioOAuthClient client;

            @Override public Code begin() throws Exception
            {
                client = new PutioOAuthClient(app.getString(R.string.putio_registered_client_id),
                                               app.getString(R.string.putio_validated_link_template));
                PutioOAuthClient.Code code = client.begin();
                return new Code(code.code, code.link);
            }

            @Override public String poll(Code code) throws Exception
            {
                return client.poll(new PutioOAuthClient.Code(code.code(), code.link()));
            }

            @Override public void save(String token) throws Exception
            {
                new PutioTokenStore(app).save(token);
            }
        }, main, listener, TimeUnit.MINUTES.toMillis(10), 5000);
    }

    PutioCodeSignIn(Provider provider, Executor callbacks, Consumer<Snapshot> listener, long timeoutMs, long pollMs)
    {
        this.provider = provider;
        this.callbacks = callbacks;
        this.listener = listener;
        this.pollMs = pollMs;
        expiry = clock.schedule(() -> finish(State.EXPIRED), timeoutMs, TimeUnit.MILLISECONDS);
        synchronized (this)
        {
            pending = worker.schedule(this::begin, 0, TimeUnit.MILLISECONDS);
        }
    }

    public synchronized Snapshot snapshot()
    {
        return snapshot;
    }

    private void begin()
    {
        try
        {
            Code code = provider.begin();
            synchronized (this)
            {
                if (!active())
                    return;
                snapshot = new Snapshot(State.WAITING, code.code(), code.link());
                publish();
                pending = worker.schedule(() -> poll(code), pollMs, TimeUnit.MILLISECONDS);
            }
        }
        catch (Exception unavailable)
        {
            finish(State.FAILED);
        }
    }

    private synchronized boolean active()
    {
        return !closed && snapshot.state() == State.WAITING;
    }

    private void poll(Code code)
    {
        try
        {
            String token = provider.poll(code);
            synchronized (this)
            {
                if (!active())
                    return;
                if (token == null)
                    pending = worker.schedule(() -> poll(code), pollMs, TimeUnit.MILLISECONDS);
                else
                {
                    provider.save(token);
                    finish(State.SIGNED_IN);
                }
            }
        }
        catch (Exception unavailable)
        {
            finish(State.FAILED);
        }
    }

    private synchronized void finish(State state)
    {
        if (!active())
            return;
        snapshot = new Snapshot(state, "", "");
        if (pending != null)
            pending.cancel(true);
        if (expiry != null)
            expiry.cancel(false);
        worker.shutdownNow();
        clock.shutdown();
        publish();
    }

    private void publish()
    {
        Snapshot value = snapshot;
        callbacks.execute(() -> {
            synchronized (PutioCodeSignIn.this)
            {
                if (!closed && snapshot == value)
                    listener.accept(value);
            }
        });
    }

    @Override public synchronized void close()
    {
        closed = true;
        if (expiry != null)
            expiry.cancel(false);
        if (pending != null)
            pending.cancel(true);
        worker.shutdownNow();
        clock.shutdownNow();
    }
}
