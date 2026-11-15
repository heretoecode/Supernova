package com.archos.mediacenter.video.connections;

import android.content.Context;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import com.archos.filecorelibrary.samba.SambaDiscovery;
import com.archos.filecorelibrary.samba.Share;
import com.archos.filecorelibrary.samba.Workgroup;
import com.archos.mediacenter.filecoreextension.upnp2.UpnpServiceManager;
import com.archos.mediacenter.utils.ShortcutDbAdapter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.function.Consumer;
import org.jupnp.model.meta.Device;

/** Owned discovery data over the existing SMB/UPnP producers. Never saves credentials or connects to a share. */
public final class NetworkDeviceDiscovery
{
    public enum Kind
    {
        SMB_SERVER, MEDIA_SERVER, NETWORK_DEVICE
    }
    public enum Membership
    {
        NEW, ADDED
    }
    public enum State
    {
        LOADING_SAVED, DISCOVERING, FINISHED, FAILED, CLOSED
    }
    public record Row(Uri uri, String name, Kind kind, Membership membership)
    {
    }
    public record Snapshot(List<Row> devices, State state, boolean partialFailure)
    {
        public Snapshot
        {
            devices = List.copyOf(devices);
        }
    }
    record Observed(Uri uri, String name, Kind kind, List<Uri> aliases)
    {
    }
    private static final Executor IO = Executors.newSingleThreadExecutor(task -> {
        Thread thread = new Thread(task, "NetworkDeviceData");
        thread.setDaemon(true);
        return thread;
    });
    private static final Handler MAIN = new Handler(Looper.getMainLooper());

    public static Job start(Context context, Executor callbacks, Consumer<Snapshot> listener)
    {
        Context app = context.getApplicationContext();
        Job job = new Job(callbacks, listener);
        MAIN.postDelayed(job.deadline, 25000);
        IO.execute(() -> {
            try
            {
                List<ShortcutDbAdapter.Shortcut> saved = ShortcutDbAdapter.VIDEO.readCopiedShortcuts(app);
                if (saved == null)
                    throw new IllegalStateException("Saved connections unavailable");
                List<Uri> roots = new ArrayList<>();
                for (ShortcutDbAdapter.Shortcut shortcut : saved)
                    roots.add(Uri.parse(shortcut.getUri()));
                MAIN.post(() -> {
                    job.saved(roots);
                    if (!job.accepting())
                        return;
                    try
                    {
                        attach(app, job);
                    }
                    catch (RuntimeException | LinkageError unavailable)
                    {
                        job.finish(true);
                    }
                });
            }
            catch (Exception unavailable)
            {
                job.finish(true);
            }
        });
        return job;
    }

    public static final class Job implements AutoCloseable
    {
        private final Executor callbacks;
        private final Consumer<Snapshot> listener;
        private final Map<String, Observed> found = new HashMap<>();
        private Set<String> saved = Set.of();
        private State state = State.LOADING_SAVED;
        private boolean partialFailure;
        private long revision;
        private volatile Snapshot current = new Snapshot(List.of(), State.LOADING_SAVED, false);
        private Runnable detach = () -> {};
        private final Runnable deadline = () -> finish(false);

        Job(Executor callbacks, Consumer<Snapshot> listener)
        {
            this.callbacks = callbacks;
            this.listener = listener;
        }

        public Snapshot current()
        {
            return current;
        }

        /** Refresh membership after saving/removing a connection; caller supplies its already loaded inventory. */
        public synchronized void saved(Collection<Uri> roots)
        {
            if (state == State.CLOSED)
                return;
            Set<String> keys = new HashSet<>();
            for (Uri root : roots)
            {
                String key = endpoint(root);
                if (key != null)
                    keys.add(key);
            }
            saved = Set.copyOf(keys);
            if (state == State.LOADING_SAVED)
                state = State.DISCOVERING;
            publish();
        }

        synchronized boolean accepting()
        {
            return state == State.DISCOVERING;
        }

        synchronized void observe(Observed device)
        {
            if (!accepting() || found.size() >= 512 || endpoint(device.uri) == null ||
                device.uri.getUserInfo() != null || device.uri.getQuery() != null || device.uri.getFragment() != null)
                return;
            found.put(endpoint(device.uri), device);
            publish();
        }

        synchronized void failedProducer()
        {
            if (!accepting())
                return;
            partialFailure = true;
            publish();
        }

        synchronized void finish(boolean failure)
        {
            if (state != State.LOADING_SAVED && state != State.DISCOVERING)
                return;
            state = failure || state == State.LOADING_SAVED ? State.FAILED : State.FINISHED;
            partialFailure |= failure;
            publish();
            MAIN.removeCallbacks(deadline);
            MAIN.post(detach);
        }

        private void publish()
        {
            List<Row> rows = new ArrayList<>();
            for (Observed device : found.values())
            {
                boolean added = saved.contains(endpoint(device.uri));
                for (Uri alias : device.aliases)
                    added |= saved.contains(endpoint(alias));
                String name = device.name == null ? "" : device.name.replaceAll("[\\p{Cntrl}]", " ").trim();
                if (name.length() > 160)
                    name = name.substring(0, 160);
                rows.add(new Row(device.uri, name, device.kind, added ? Membership.ADDED : Membership.NEW));
            }
            rows.sort(Comparator.comparing(Row::name, String.CASE_INSENSITIVE_ORDER)
                          .thenComparing(row -> row.uri.toString()));
            current = new Snapshot(rows, state, partialFailure);
            long version = ++revision;
            Snapshot value = current;
            callbacks.execute(() -> {
                synchronized (Job.this)
                {
                    if (state == State.CLOSED || version != revision)
                        return;
                    listener.accept(value);
                }
            });
        }

        @Override public synchronized void close()
        {
            if (state == State.CLOSED)
                return;
            state = State.CLOSED;
            revision++;
            current = new Snapshot(current.devices, state, partialFailure);
            MAIN.removeCallbacks(deadline);
            MAIN.post(detach);
        }
    }

    static String endpoint(Uri uri)
    {
        if (uri == null || uri.getHost() == null || uri.getScheme() == null)
            return null;
        String scheme = uri.getScheme().toLowerCase(Locale.ROOT);
        if (!scheme.equals("smb") && !scheme.equals("upnp"))
            return null;
        int port = uri.getPort() > 0 ? uri.getPort() : scheme.equals("smb") ? 445 : -1;
        return scheme + "://" + uri.getHost().toLowerCase(Locale.ROOT) + ":" + port;
    }

    private static void attach(Context context, Job job)
    {
        SambaDiscovery smb = new SambaDiscovery(context);
        SambaDiscovery.Listener smbListener = new SambaDiscovery.Listener() {
            @Override public void onDiscoveryStart()
            {
            }
            @Override public void onDiscoveryEnd()
            {
                // UPnP keeps discovering until this job's bounded deadline.
            }
            @Override public void onDiscoveryFatalError()
            {
                job.failedProducer();
            }
            @Override public void onDiscoveryUpdate(List<Workgroup> groups)
            {
                for (Workgroup group : groups)
                    for (Share share : group.getShares())
                        job.observe(new Observed(Uri.parse(share.getAddress()), share.getDisplayName(), Kind.SMB_SERVER,
                                                 List.of(share.toUri())));
            }
        };
        UpnpServiceManager manager = UpnpServiceManager.getSingleton(context);
        UpnpServiceManager.Listener upnpListener = devices -> {
            for (Device device : devices)
            {
                Kind kind = device.getType() != null && "MediaServer".equals(device.getType().getType())
                    ? Kind.MEDIA_SERVER : Kind.NETWORK_DEVICE;
                job.observe(new Observed(UpnpServiceManager.getDeviceUri(device),
                    UpnpServiceManager.getDeviceFriendlyName(device), kind, List.of()));
            }
        };
        synchronized (job)
        {
            if (!job.accepting())
                return;
            job.detach = () -> {
                smb.removeListener(smbListener);
                manager.removeListener(upnpListener);
                IO.execute(smb::abort);
            };
            try
            {
                smb.addListener(smbListener);
                manager.addListener(upnpListener);
                manager.start();
                smb.start();
            }
            catch (RuntimeException | LinkageError unavailable)
            {
                job.finish(true);
            }
        }
    }

    private NetworkDeviceDiscovery()
    {
    }
}
