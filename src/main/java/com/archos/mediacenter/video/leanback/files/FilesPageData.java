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
        // M6 (owner, 26 Nov): the name belongs to the server, whatever folder the form saved, so every place that
        // names the connection (Connections, Library Care, scans, Missing files) finds the new name at once.
        context.getSharedPreferences(NAMES, android.content.Context.MODE_PRIVATE).edit()
            .putString(serverNameKey(root), name).putString(nameKey(root), name).commit();
    }

    /** The name given to the server a file or folder lives on, or null when none was given. */
    public static String givenName(android.content.Context context, Uri anywhere)
    {
        if (anywhere == null)
        {
            return null;
        }
        android.content.SharedPreferences names =
            context.getSharedPreferences(NAMES, android.content.Context.MODE_PRIVATE);
        String name = names.getString(serverNameKey(anywhere), null);
        return name != null ? name : names.getString(nameKey(anywhere), null);
    }

    /** One key per server: the kind of connection (smbj and smb alike), host and port; never a login. */
    static String serverNameKey(Uri uri)
    {
        String scheme = uri.getScheme() == null ? "" : uri.getScheme().toLowerCase(java.util.Locale.ROOT);
        String family = scheme.startsWith("smb") ? "smb" : scheme.equals("sshj") ? "sftp"
            : scheme.startsWith("webdav") ? "webdav" : scheme.startsWith("ftp") ? "ftp" : scheme;
        String host = uri.getHost() == null ? "" : uri.getHost().toLowerCase(java.util.Locale.ROOT);
        return "server:" + Integer.toHexString((family + "://" + host + ":" + uri.getPort()).hashCode());
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
                String given = givenName(context, connection.root);
                name = given != null ? given : name;
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
        // One button per server (F52), named by the connection's name, so two WebDAV servers stay apart.
        java.util.LinkedHashMap<String, List<Uri>> servers = new java.util.LinkedHashMap<>();
        java.util.Map<String, String> names = new java.util.HashMap<>();
        for (Shortcut shortcut : shortcuts)
        {
            if (shortcut.getUri() != null)
            {
                String name = shortcut.getName() == null || shortcut.getName().isEmpty()
                    ? shortcut.getUri().getHost()
                    : shortcut.getName();
                String key = serverKey(shortcut.getUri(), shortcut.getUri().getUserInfo());
                servers.computeIfAbsent(key, k -> new ArrayList<>()).add(shortcut.getUri());
                names.putIfAbsent(key, name);
            }
        }
        for (java.util.Map.Entry<String, List<Uri>> server : servers.entrySet())
        {
            sources.add(new FilesPage.Source(names.get(server.getKey()), server.getValue().get(0), false,
                                             server.getValue()));
        }
        return sources;
    }

    /** The server and login a folder belongs to (F52): scheme, host, port and user name. */
    static String serverKey(Uri root, String account)
    {
        if (root == null)
        {
            return "";
        }
        return root.getScheme() + "://" + (account == null ? "" : account) + "@" + root.getHost() + ":" + root.getPort();
    }

    /** "Films" from webdavs://webdav.put.io/Films/ ; the server's name for its top folder. */
    public static String folderName(Uri root)
    {
        java.util.List<String> parts = root.getPathSegments();
        return parts == null || parts.isEmpty() ? root.getHost() : parts.get(parts.size() - 1);
    }

    /** "Films, TV". */
    public static String folderNames(java.util.List<Uri> folders)
    {
        java.util.List<String> names = new ArrayList<>();
        for (Uri folder : folders)
        {
            names.add(folderName(folder));
        }
        return android.text.TextUtils.join(", ", names);
    }

    /**
     * One connection per server and login (F52, approved 17 Nov): the folders chosen are part of it. Named by the
     * first folder's given name; reachable if any folder answered; videos add up.
     */
    public static List<FilesPage.Connection> byServer(List<FilesPage.Connection> connections)
    {
        java.util.LinkedHashMap<String, List<FilesPage.Connection>> groups = new java.util.LinkedHashMap<>();
        for (FilesPage.Connection connection : connections)
        {
            String key = connection.root == null ? "#" + groups.size() : serverKey(connection.root,
                                                                                   connection.account);
            groups.computeIfAbsent(key, k -> new ArrayList<>()).add(connection);
        }
        List<FilesPage.Connection> result = new ArrayList<>();
        for (List<FilesPage.Connection> members : groups.values())
        {
            FilesPage.Connection first = members.get(0);
            List<Uri> folders = new ArrayList<>();
            long videos = 0;
            long seen = 0;
            Boolean reachable = Boolean.FALSE;
            for (FilesPage.Connection member : members)
            {
                folders.addAll(member.folders);
                videos += Math.max(0, member.videos);
                seen = Math.max(seen, member.lastSeenMs);
                if (Boolean.TRUE.equals(member.reachable))
                {
                    reachable = Boolean.TRUE;
                }
                else if (member.reachable == null && !Boolean.TRUE.equals(reachable))
                {
                    reachable = null;
                }
            }
            result.add(new FilesPage.Connection(first.name, first.root, reachable, videos, seen, first.account,
                                                folders));
        }
        return result;
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
