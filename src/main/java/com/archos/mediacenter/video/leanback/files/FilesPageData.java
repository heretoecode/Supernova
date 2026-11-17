package com.archos.mediacenter.video.leanback.files;

import android.net.Uri;
import android.os.StatFs;

import com.archos.mediacenter.video.browser.adapters.object.Base;
import com.archos.mediacenter.video.browser.adapters.object.Video;
import com.archos.mediacenter.video.leanback.adapter.object.Box;
import com.archos.mediacenter.video.leanback.adapter.object.Shortcut;

import java.util.ArrayList;
import java.util.List;

/** Turns the old Files page's lists (storage boxes, indexed connections, library media) into the new page's rows. */
public final class FilesPageData
{
    private FilesPageData()
    {
    }

    public static List<FilesPage.Device> devices(List<Box> boxes, List<Base> media)
    {
        List<FilesPage.Device> devices = new ArrayList<>();
        for (Box box : boxes)
        {
            Box.ID id = box.getBoxId();
            if (id != Box.ID.FOLDERS && id != Box.ID.USB && id != Box.ID.SDCARD && id != Box.ID.OTHER)
            {
                continue;
            }
            String path = box.getPath();
            if ((path == null || path.isEmpty()) && id == Box.ID.FOLDERS)
            {
                // Internal storage has no path on its box: its space is the device's shared storage (F14).
                path = android.os.Environment.getExternalStorageDirectory().getPath();
            }
            long free = 0;
            long total = 0;
            try
            {
                if (path != null)
                {
                    StatFs stats = new StatFs(path);
                    free = stats.getAvailableBytes();
                    total = stats.getTotalBytes();
                }
            }
            catch (IllegalArgumentException unreadable)
            {
                // A drive that was just removed: shown without its space rather than not at all.
            }
            boolean removable = id != Box.ID.FOLDERS;
            // The tab is already This Device, so its row is Internal storage (F19).
            String name = id == Box.ID.FOLDERS ? "Internal storage" : box.getName();
            String kind = id == Box.ID.USB ? "USB drive" : id == Box.ID.SDCARD ? "Memory card" : "";
            devices.add(new FilesPage.Device(name, kind, removable, countUnder(media, path), free, total));
        }
        return devices;
    }

    private static final String NAMES = "files_connection_names";

    /** The name given on the connection form (F52: suggested from the address, changeable with Edit). */
    public static void rename(android.content.Context context, Uri root, String name)
    {
        context.getSharedPreferences(NAMES, android.content.Context.MODE_PRIVATE).edit()
            .putString(nameKey(root), name).apply();
    }

    /** No address (which may carry a login) is stored as a key: only a short hash of it. */
    private static String nameKey(Uri root)
    {
        return Integer.toHexString(root.buildUpon().clearQuery().build().toString().hashCode());
    }

    /** Your connections with the names given on the form and the login's user name (F38). */
    public static List<FilesPage.Connection> connections(android.content.Context context, List<Shortcut> shortcuts,
                                                         List<Base> media)
    {
        List<FilesPage.Connection> plain = connections(shortcuts, media);
        List<FilesPage.Connection> named = new ArrayList<>();
        android.content.SharedPreferences names =
            context.getSharedPreferences(NAMES, android.content.Context.MODE_PRIVATE);
        for (FilesPage.Connection connection : plain)
        {
            String name = connection.name;
            String account = "";
            if (connection.root != null)
            {
                name = names.getString(nameKey(connection.root), name);
                account = account(connection.root);
            }
            named.add(new FilesPage.Connection(name, connection.root, connection.reachable, connection.videos,
                                               connection.lastSeenMs, account));
        }
        return named;
    }

    private static String account(Uri root)
    {
        try
        {
            com.archos.filecorelibrary.samba.NetworkCredentialsDatabase.Credential credential =
                com.archos.filecorelibrary.samba.NetworkCredentialsDatabase.getInstance().getCredential(
                    root.toString());
            String user = credential == null ? "" : credential.getUsername();
            return user == null || "anonymous".equals(user) ? "" : user;
        }
        catch (RuntimeException unavailable)
        {
            return "";
        }
    }

    public static List<FilesPage.Connection> connections(List<Shortcut> shortcuts, List<Base> media)
    {
        List<FilesPage.Connection> connections = new ArrayList<>();
        for (Shortcut shortcut : shortcuts)
        {
            Uri root = shortcut.getUri();
            String name = shortcut.getName() == null || shortcut.getName().isEmpty()
                ? (root == null ? "Connection" : root.getHost())
                : shortcut.getName();
            long videos = root == null ? 0 : countUnder(media, root.toString());
            connections.add(new FilesPage.Connection(name, root, null, videos, 0));
        }
        return connections;
    }

    public static List<FilesPage.Source> sources(List<FilesPage.Device> devices, List<Shortcut> shortcuts)
    {
        List<FilesPage.Source> sources = new ArrayList<>();
        if (!devices.isEmpty())
        {
            sources.add(new FilesPage.Source("This device", Uri.parse("file:///"), true));
        }
        for (Shortcut shortcut : shortcuts)
        {
            if (shortcut.getUri() != null)
            {
                String name = shortcut.getName() == null || shortcut.getName().isEmpty()
                    ? shortcut.getUri().getHost()
                    : shortcut.getName();
                sources.add(new FilesPage.Source(name, shortcut.getUri(), false));
            }
        }
        return sources;
    }

    /** Worker thread only: how many files MediaLib recorded as unreadable. */
    public static int unreadable(android.content.Context context)
    {
        try (com.archos.mediaprovider.video.LibraryHealthEvidence store =
                 new com.archos.mediaprovider.video.LibraryHealthEvidence(context);
             android.database.Cursor rows = store.getReadableDatabase().rawQuery(
                 "SELECT COUNT(*) FROM evidence WHERE kind=?", new String[] {"COULD_NOT_READ"}))
        {
            return rows.moveToFirst() ? rows.getInt(0) : 0;
        }
        catch (RuntimeException unavailable)
        {
            return 0;
        }
    }

    /** New devices from the network search (mock-up network-files-tabs/05, "Found on your network"). */
    public static List<FilesPage.Found> found(com.archos.mediacenter.video.connections.NetworkDeviceDiscovery.Snapshot
                                                  snapshot)
    {
        List<FilesPage.Found> found = new ArrayList<>();
        if (snapshot == null)
        {
            return found;
        }
        for (com.archos.mediacenter.video.connections.NetworkDeviceDiscovery.Row row : snapshot.devices())
        {
            if (row.membership() != com.archos.mediacenter.video.connections.NetworkDeviceDiscovery.Membership.NEW)
            {
                continue;
            }
            String line;
            switch (row.kind())
            {
                case SMB_SERVER:
                    line = "SMB · Shared folders";
                    break;
                case MEDIA_SERVER:
                    line = "UPnP · Media server";
                    break;
                default:
                    line = "Network device";
                    break;
            }
            found.add(new FilesPage.Found(row.name(), line, row.uri()));
        }
        return found;
    }

    public static int videoCount(List<Base> media)
    {
        int count = 0;
        for (Base item : media)
        {
            if (item instanceof Video)
            {
                count++;
            }
        }
        return count;
    }

    static long countUnder(List<Base> media, String root)
    {
        if (root == null || root.isEmpty())
        {
            return 0;
        }
        long count = 0;
        for (Base item : media)
        {
            if (!(item instanceof Video))
            {
                continue;
            }
            Video video = (Video) item;
            String path = video.getFilePath();
            Uri uri = video.getFileUri();
            if (path != null && path.startsWith(root) || uri != null && uri.toString().startsWith(root))
            {
                count++;
            }
        }
        return count;
    }
}
