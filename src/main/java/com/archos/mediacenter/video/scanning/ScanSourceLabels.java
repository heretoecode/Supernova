package com.archos.mediacenter.video.scanning;

import android.content.Context;
import android.net.Uri;
import android.os.Build;
import android.os.storage.StorageManager;
import android.os.storage.StorageVolume;
import com.archos.mediacenter.utils.ShortcutDbAdapter;
import com.archos.mediacenter.video.diagnostics.DiagnosticRedactor;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/** Refresh on a worker; label() only reads a published immutable source-name cache. */
final class ScanSourceLabels
{
    private record Source(String id, String location, String label)
    {
    }

    private volatile List<Source> sources = List.of();
    private final Map<String, String> supplied = new ConcurrentHashMap<>();

    void remember(String id, String label)
    {
        if (id != null && id.matches("[a-f0-9]{24}") && label != null)
            supplied.put(id, safe(label));
    }

    void refresh(Context context) throws Exception
    {
        List<Source> next = new ArrayList<>();
        List<ShortcutDbAdapter.Shortcut> shortcuts = ShortcutDbAdapter.VIDEO.readCopiedShortcuts(context);
        if (shortcuts == null)
            throw new IOException("Source-name inventory unavailable");
        for (ShortcutDbAdapter.Shortcut source : shortcuts)
        {
            Uri uri = Uri.parse(source.getUri());
            String label = safe(source.getName());
            String host = uri.getHost() == null ? "" : uri.getHost().toLowerCase(Locale.ROOT);
            String provider = host.equals("put.io") || host.endsWith(".put.io") ? "Put.io" : "";
            next.add(new Source(id(uri.toString()), "", provider.isEmpty() ? label : provider + " › " + label));
        }
        if (Build.VERSION.SDK_INT >= 24)
        {
            StorageManager manager = context.getSystemService(StorageManager.class);
            if (manager != null)
            {
                for (StorageVolume volume : manager.getStorageVolumes())
                {
                    File directory = Build.VERSION.SDK_INT >= 30 ? volume.getDirectory() : null;
                    String path = directory != null          ? directory.getPath()
                                  : volume.getUuid() == null ? ""
                                                             : "/storage/" + volume.getUuid();
                    if (!path.isEmpty())
                        next.add(new Source("", path, safe(volume.getDescription(context))));
                }
            }
        }
        sources = List.copyOf(next);
    }

    String label(String id, String kind, String location)
    {
        if (id != null && supplied.containsKey(id))
            return supplied.get(id);
        Source chosen = null;
        for (Source source : sources)
        {
            if (!source.id.isEmpty() && source.id.equals(id))
                return source.label;
            if (!source.location.isEmpty() && location != null &&
                (location.equals(source.location) || location.startsWith(source.location + "/")) &&
                (chosen == null || source.location.length() > chosen.location.length()))
                chosen = source;
        }
        if (chosen != null)
            return chosen.label;
        if ("local".equals(kind) || "file".equals(kind))
            return "Local library";
        String folder = location == null ? "" : location.substring(location.lastIndexOf('/') + 1);
        return (kind == null ? "Source" : kind.toUpperCase(Locale.ROOT)) +
            (folder.isEmpty() ? "" : " › " + safe(folder));
    }

    static String safe(String label)
    {
        String clean = DiagnosticRedactor.clean(label).replaceAll("[\\p{Cntrl}]", " ").trim();
        return clean.length() > 160 ? clean.substring(0, 160) : clean;
    }

    static String id(String uri) throws Exception
    {
        byte[] digest = MessageDigest.getInstance("SHA-256").digest(uri.getBytes(StandardCharsets.UTF_8));
        StringBuilder id = new StringBuilder();
        for (int i = 0; i < 12; i++)
            id.append(String.format(Locale.ROOT, "%02x", digest[i]));
        return id.toString();
    }
}
