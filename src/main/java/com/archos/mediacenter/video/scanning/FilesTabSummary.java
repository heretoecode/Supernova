package com.archos.mediacenter.video.scanning;

import com.archos.mediacenter.video.leanback.LibraryAttentionCount;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/** Immutable data for the Files tab headers. Build on the inventory worker, publish once per refresh. */
public final class FilesTabSummary
{
    public record Device(String id, long freeBytes)
    {
    }

    public record FileRow(String identity, boolean video, boolean inLibrary, boolean excluded)
    {
    }

    public record Connection(String id, boolean reachable, long videos)
    {
    }

    public record Shield(int devices, long videos, long freeBytes)
    {
    }

    public record Folder(long videos, long inLibrary, long notAdded)
    {
    }

    public record Connections(int connections, int reachable, long videos)
    {
    }

    public record Care(long videos, int toCheck, int sources)
    {
    }

    public final Shield shield;
    public final Folder folder;
    public final Connections connections;
    public final Care care;

    public FilesTabSummary(List<Device> devices, List<FileRow> local, List<FileRow> folder,
                           List<Connection> connections, long libraryVideos, int sourceCount,
                           LibraryAttentionCount attention)
    {
        Set<String> seen = new HashSet<>();
        long free = 0;
        for (Device device : devices)
        {
            if (!seen.add(device.id()))
                continue;
            free = sum(free, device.freeBytes());
        }
        shield = new Shield(seen.size(), folder(local).videos(), free);
        this.folder = folder(folder);
        seen.clear();
        int reachable = 0;
        long videos = 0;
        for (Connection connection : connections)
        {
            if (!seen.add(connection.id()))
                continue;
            if (connection.reachable())
                reachable++;
            videos = sum(videos, connection.videos());
        }
        this.connections = new Connections(seen.size(), reachable, videos);
        care = new Care(libraryVideos, attention.count, sourceCount);
    }

    private static Folder folder(List<FileRow> files)
    {
        Set<String> seen = new HashSet<>();
        long videos = 0;
        long library = 0;
        long notAdded = 0;
        for (FileRow file : files)
        {
            if (!file.video() || !seen.add(file.identity()))
                continue;
            videos++;
            if (file.inLibrary())
                library++;
            else if (!file.excluded())
                notAdded++;
        }
        return new Folder(videos, library, notAdded);
    }

    private static long sum(long total, long next)
    {
        return total < 0 || next < 0 || Long.MAX_VALUE - total < next ? -1 : total + next;
    }
}
