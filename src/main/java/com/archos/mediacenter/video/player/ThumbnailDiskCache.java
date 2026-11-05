package com.archos.mediacenter.video.player;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.NavigableMap;
import java.util.TreeMap;
import org.json.JSONArray;
import org.json.JSONObject;

/** Worker-only crash-recoverable JPEG sets. The index is rebuilt from owned filenames if interrupted or corrupt. */
final class ThumbnailDiskCache
{
    static final long LIMIT_BYTES = 500L * 1024 * 1024;
    private final File root;
    private final File directory;
    private final long limit;
    private final String ownerKey;
    private long evictedSets;
    private final NavigableMap<Long, File> frames = new TreeMap<>();

    ThumbnailDiskCache(File root, String mediaKey, long limit) throws Exception
    {
        this(root, mediaKey, limit, "");
    }

    ThumbnailDiskCache(File root, String mediaKey, long limit, String ownerKey) throws Exception
    {
        this.root = root;
        this.limit = limit;
        this.ownerKey = ownerKey;
        directory = new File(root, stableKey(mediaKey));
        if (!directory.isDirectory() && !directory.mkdirs())
            throw new IOException("Thumbnail cache unavailable");
        File[] files = directory.listFiles();
        if (files != null)
        {
            for (File file : files)
            {
                if (file.getName().matches("[0-9]{1,18}\\.jpg") && file.length() > 0)
                {
                    long position = Long.parseLong(file.getName().substring(0, file.getName().length() - 4));
                    frames.put(position, file);
                }
                else if (file.getName().endsWith(".part"))
                {
                    file.delete();
                }
            }
        }
        writeIndex();
        trim();
    }

    static String stableKey(String identity) throws Exception
    {
        StringBuilder key = new StringBuilder();
        for (byte value : MessageDigest.getInstance("SHA-256").digest(identity.getBytes(StandardCharsets.UTF_8)))
        {
            key.append(String.format(Locale.ROOT, "%02x", value & 255));
        }
        return key.toString();
    }

    File exact(long position)
    {
        File file = frames.get(position);
        return file != null && file.isFile() ? file : null;
    }

    Map.Entry<Long, File> nearest(long position, long interval)
    {
        Map.Entry<Long, File> before = frames.floorEntry(position);
        Map.Entry<Long, File> after = frames.ceilingEntry(position);
        Map.Entry<Long, File> selected = before == null                                            ? after
                                         : after == null                                           ? before
                                         : position - before.getKey() <= after.getKey() - position ? before
                                                                                                   : after;
        return selected == null || Math.abs(position - selected.getKey()) > interval || !selected.getValue().isFile()
            ? null
            : selected;
    }

    void put(long position, byte[] jpeg) throws Exception
    {
        if (position < 0 || jpeg.length < 4 || (jpeg[0] & 255) != 255 || (jpeg[1] & 255) != 216)
            throw new IOException("Invalid thumbnail JPEG");
        if (!directory.isDirectory() && !directory.mkdirs())
            throw new IOException("Thumbnail cache unavailable");
        File target = new File(directory, position + ".jpg");
        File partial = new File(directory, position + ".jpg.part");
        try (FileOutputStream output = new FileOutputStream(partial))
        {
            output.write(jpeg);
            output.getFD().sync();
        }
        if (!partial.renameTo(target))
            throw new IOException("Cannot publish thumbnail");
        frames.put(position, target);
        writeIndex();
        trim();
    }

    void touched()
    {
        new File(directory, "index.json").setLastModified(System.currentTimeMillis());
    }

    long bytes()
    {
        return bytes(root);
    }

    private static long bytes(File file)
    {
        if (file.isFile())
            return file.length();
        long result = 0;
        File[] children = file.listFiles();
        if (children != null)
            for (File child : children)
                result += bytes(child);
        return result;
    }

    private void writeIndex() throws Exception
    {
        JSONArray rows = new JSONArray();
        for (Map.Entry<Long, File> frame : frames.entrySet())
        {
            rows.put(new JSONObject().put("position_ms", frame.getKey()).put("bytes", frame.getValue().length()));
        }
        byte[] data = new JSONObject()
                          .put("schema", 1)
                          .put("owner_key", ownerKey)
                          .put("frames", rows)
                          .toString()
                          .getBytes(StandardCharsets.UTF_8);
        File partial = new File(directory, "index.json.part");
        try (FileOutputStream output = new FileOutputStream(partial))
        {
            output.write(data);
            output.getFD().sync();
        }
        if (!partial.renameTo(new File(directory, "index.json")))
            throw new IOException("Cannot publish frame index");
    }

    long trim() throws IOException
    {
        File[] directories = root.listFiles();
        if (directories == null)
            return 0;
        List<File> sets = new ArrayList<>();
        Map<File, Long> times = new java.util.HashMap<>();
        for (File set : directories)
        {
            if (set.isDirectory() && set.getName().matches("[a-f0-9]{64}"))
            {
                sets.add(set);
                times.put(set, new File(set, "index.json").lastModified());
            }
        }
        // Snapshot timestamps before sorting, just like diagnostic retention.
        sets.sort(Comparator.comparingLong((File set) -> times.get(set)).thenComparing(File::getName));
        long total = bytes(root);
        long evictions = 0;
        for (File set : sets)
        {
            if (total <= limit)
                break;
            long size = bytes(set);
            delete(set);
            total -= size;
            evictions++;
            if (set.equals(directory))
                frames.clear();
        }
        evictedSets += evictions;
        if (total > limit)
            throw new IOException("Thumbnail cache exceeds cap after eviction");
        return evictions;
    }

    long drainEvictions()
    {
        long count = evictedSets;
        evictedSets = 0;
        return count;
    }

    void watched() throws IOException
    {
        delete(directory);
        frames.clear();
    }

    static void deleteOwner(File root, String ownerKey) throws Exception
    {
        deleteOwners(root, java.util.Set.of(ownerKey));
    }

    static void deleteOwners(File root, java.util.Set<String> ownerKeys) throws Exception
    {
        File[] sets = root.listFiles();
        if (sets == null)
            return;
        for (File set : sets)
        {
            if (!set.isDirectory() || !set.getName().matches("[a-f0-9]{64}"))
                continue;
            File index = new File(set, "index.json");
            if (!index.isFile() || index.length() > 4 * 1024 * 1024)
                continue;
            String json = new String(java.nio.file.Files.readAllBytes(index.toPath()), StandardCharsets.UTF_8);
            try
            {
                if (ownerKeys.contains(new JSONObject(json).optString("owner_key")))
                    delete(set);
            }
            catch (org.json.JSONException corrupt)
            {
                // A corrupt set is still subject to the global LRU bound.
            }
        }
    }

    private static void delete(File file) throws IOException
    {
        File[] children = file.listFiles();
        if (children != null)
            for (File child : children)
                delete(child);
        if (file.exists() && !file.delete())
            throw new IOException("Thumbnail eviction unavailable");
    }
}
