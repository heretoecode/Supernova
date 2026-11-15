package com.archos.mediacenter.video.player;

import android.app.ActivityManager;
import android.app.Application;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Parcel;
import android.os.ParcelFileDescriptor;
import com.archos.environment.ArchosUtils;
import com.archos.filecorelibrary.MetaFile2;
import com.archos.filecorelibrary.samba.NetworkCredentialsDatabase;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InterruptedIOException;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

/** Task-owned cancellation exists before execute. Only an operation's private process may be stopped. */
final class NextListingProcess implements AutoCloseable
{
    private static final Semaphore SLOT = new Semaphore(1, true);
    private static final ScheduledExecutorService CONTROL = Executors.newScheduledThreadPool(2, task -> {
        Thread thread = new Thread(task, "NextListingControl");
        thread.setDaemon(true);
        return thread;
    });
    private final String token = UUID.randomUUID().toString();
    private final CountDownLatch connected = new CountDownLatch(1);
    private volatile boolean cancelled;
    private Thread worker;
    private IBinder remote;
    private Context boundContext;
    private ServiceConnection boundConnection;
    private boolean binding;
    private ScheduledFuture<?> deadline;
    private ScheduledFuture<?> cancellationCheck;
    private final CountDownLatch died = new CountDownLatch(1);
    private final java.util.concurrent.atomic.AtomicBoolean released = new java.util.concurrent.atomic.AtomicBoolean();
    private boolean linked;
    private boolean closed;
    private volatile boolean closingTransport;

    static boolean bootstrap(Context context)
    {
        String name = Build.VERSION.SDK_INT >= 28 ? Application.getProcessName() : null;
        if (name == null)
        {
            try (java.io.InputStream input = new java.io.FileInputStream("/proc/self/cmdline"))
            {
                byte[] bytes = new byte[512];
                int count = input.read(bytes);
                int end = 0;
                while (end < count && bytes[end] != 0)
                    end++;
                if (end > 0)
                    name = new String(bytes, 0, end, java.nio.charset.StandardCharsets.UTF_8);
            }
            catch (IOException unavailable)
            {
                // Older Android releases also expose this process through ActivityManager.
            }
        }
        if (name == null)
        {
            ActivityManager manager = (ActivityManager)context.getSystemService(Context.ACTIVITY_SERVICE);
            List<ActivityManager.RunningAppProcessInfo> processes =
                manager == null ? null : manager.getRunningAppProcesses();
            if (processes != null)
                for (ActivityManager.RunningAppProcessInfo process : processes)
                    if (process.pid == android.os.Process.myPid())
                        name = process.processName;
        }
        if (!(context.getPackageName() + ":next_listing").equals(name))
            return false;
        Context app = context.getApplicationContext();
        ArchosUtils.setGlobalContext(app == null ? context : app);
        return true;
    }

    synchronized void enter()
    {
        checkpoint();
        if (closed || worker != null)
            throw new IllegalStateException("Next lookup is single use");
        worker = Thread.currentThread();
        deadline = CONTROL.schedule(this::timeout, 30, TimeUnit.SECONDS);
    }

    private synchronized void timeout()
    {
        if (!closed)
            cancel();
    }

    void checkpoint()
    {
        if (cancelled || Thread.currentThread().isInterrupted())
            throw new java.util.concurrent.CancellationException("Next lookup cancelled");
    }

    synchronized void watch(java.util.function.BooleanSupplier stop)
    {
        cancellationCheck = CONTROL.scheduleWithFixedDelay(() -> {
            synchronized (NextListingProcess.this)
            {
                if (closed)
                    return;
                try
                {
                    if (stop.getAsBoolean() || worker != null && worker.isInterrupted())
                        cancel();
                }
                catch (RuntimeException invalidCancellation)
                {
                    cancel();
                }
            }
        }, 0, 50, TimeUnit.MILLISECONDS);
    }

    List<MetaFile2> list(Context context, Uri uri) throws IOException
    {
        return read(context, uri, false, false);
    }

    List<MetaFile2> read(Context context, Uri uri, boolean sizes, boolean inspect) throws IOException
    {
        Context app = context.getApplicationContext();
        boolean acquired = false;
        boolean bound = false;
        ParcelFileDescriptor[] pipe = null;
        ServiceConnection connection = null;
        try
        {
            while (!SLOT.tryAcquire(25, TimeUnit.MILLISECONDS))
                checkpoint();
            acquired = true;
            checkpoint();
            pipe = ParcelFileDescriptor.createPipe();
            ParcelFileDescriptor sending = pipe[1];
            com.archos.filecorelibrary.NetworkInitialization.awaitReady(app);
            NetworkCredentialsDatabase.Credential credential =
                NetworkCredentialsDatabase.getInstance().getCredential(uri.toString());
            Bundle request = new Bundle();
            request.putString("token", token);
            request.putString("uri", uri.toString());
            request.putBoolean("sizes", sizes);
            request.putBoolean("inspect", inspect);
            request.putParcelable("output", sending);
            if (credential != null)
            {
                request.putString("user", credential.getUsername());
                request.putString("password", credential.getPassword());
                request.putString("domain", credential.getDomain());
                request.putString("credential_uri", credential.getUriString());
            }
            connection = new ServiceConnection() {
                @Override public void onServiceConnected(ComponentName name, IBinder binder)
                {
                    synchronized (NextListingProcess.this)
                    {
                        if (closingTransport)
                        {
                            closeDescriptor(sending);
                            connected.countDown();
                            return;
                        }
                        remote = binder;
                        try
                        {
                            binder.linkToDeath(() -> {
                                try
                                {
                                    detach();
                                }
                                finally
                                {
                                    died.countDown();
                                    releaseSlot();
                                }
                            }, 0);
                            linked = true;
                            if (!cancelled)
                                send(remote, NextListingService.START, request);
                            else
                                sendStop(remote);
                        }
                        catch (Exception unavailable)
                        {
                            cancelled = true;
                        }
                        finally
                        {
                            closeDescriptor(sending);
                            connected.countDown();
                        }
                    }
                }
                @Override public void onServiceDisconnected(ComponentName name)
                {
                    if (!closingTransport)
                        cancel();
                    connected.countDown();
                }
            };
            synchronized (this)
            {
                checkpoint();
                boundContext = app;
                boundConnection = connection;
                bound = app.bindService(new Intent(app, NextListingService.class), connection,
                                        Context.BIND_AUTO_CREATE);
                binding = bound;
            }
            if (!bound)
                throw new IOException("Next listing service unavailable");
            connected.await();
            checkpoint();
            try (DataInputStream input = new DataInputStream(new ParcelFileDescriptor.AutoCloseInputStream(pipe[0])))
            {
                return NextListingWire.read(input, () -> cancelled);
            }
        }
        catch (InterruptedException interrupted)
        {
            Thread.currentThread().interrupt();
            throw new InterruptedIOException("Next listing interrupted");
        }
        finally
        {
            // A binder death releases the slot only after every transport in that process has died.
            closingTransport = true;
            synchronized (this)
            {
                if (remote != null)
                    sendStop(remote);
            }
            if (linked)
            {
                try
                {
                    died.await(2, TimeUnit.SECONDS);
                }
                catch (InterruptedException stopping)
                {
                    Thread.currentThread().interrupt();
                }
            }
            if (!linked)
                detach();
            if (pipe != null)
                for (ParcelFileDescriptor descriptor : pipe)
                    closeDescriptor(descriptor);
            if (acquired && !linked)
                releaseSlot();
        }
    }

    private void detach()
    {
        Context context;
        ServiceConnection connection;
        synchronized (this)
        {
            if (!binding)
                return;
            binding = false;
            context = boundContext;
            connection = boundConnection;
        }
        try
        {
            context.unbindService(connection);
        }
        catch (RuntimeException alreadyDetached)
        {
            // Only this operation registered the binding; concurrent death/close may already have detached it.
        }
    }

    private void releaseSlot()
    {
        if (released.compareAndSet(false, true))
            SLOT.release();
    }

    boolean cancelled()
    {
        return cancelled;
    }

    private static void send(IBinder target, int code, Bundle values) throws android.os.RemoteException
    {
        Parcel data = Parcel.obtain();
        try
        {
            data.writeInterfaceToken(NextListingService.DESCRIPTOR);
            data.writeBundle(values);
            target.transact(code, data, null, IBinder.FLAG_ONEWAY);
        }
        finally
        {
            data.recycle();
        }
    }

    private void sendStop(IBinder target)
    {
        CONTROL.execute(() -> {
            try
            {
                Bundle data = new Bundle();
                data.putString("token", token);
                send(target, NextListingService.STOP, data);
            }
            catch (Exception alreadyGone)
            {
                // Binder death already owns process/resource cleanup.
            }
        });
    }

    synchronized void cancel()
    {
        cancelled = true;
        if (remote != null)
            sendStop(remote);
        connected.countDown();
        if (worker != null && worker != Thread.currentThread())
            worker.interrupt();
    }

    @Override public synchronized void close()
    {
        if (deadline != null)
            deadline.cancel(false);
        if (cancellationCheck != null)
            cancellationCheck.cancel(false);
        closed = true;
        worker = null;
        if (remote != null)
            sendStop(remote);
    }

    private static void closeDescriptor(ParcelFileDescriptor descriptor)
    {
        try
        {
            descriptor.close();
        }
        catch (IOException alreadyClosed)
        {
            // Pipe ownership is released on all admission and cancellation paths.
        }
    }
}
