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
            String name = id == Box.ID.FOLDERS ? "This device" : box.getName();
            String kind = id == Box.ID.USB ? "USB drive" : id == Box.ID.SDCARD ? "Memory card" : "";
            devices.add(new FilesPage.Device(name, kind, removable, countUnder(media, path), free, total));
        }
        return devices;
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
