package com.archos.mediacenter.video.health;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.storage.StorageManager;
import android.os.storage.StorageVolume;
import android.os.Build;
import androidx.preference.PreferenceManager;
import com.archos.filecorelibrary.MetaFile2;
import com.archos.filecorelibrary.webdav.ScanIoScope;
import com.archos.mediacenter.filecoreextension.upnp2.MetaFileFactoryWithUpnp;
import com.archos.mediacenter.utils.ShortcutDbAdapter;
import com.archos.mediacenter.video.diagnostics.DiagnosticRedactor;
import com.archos.mediacenter.video.leanback.LibraryAttentionCount;
import com.archos.mediacenter.video.leanback.PreviewLibraryLoader;
import com.archos.mediaprovider.video.MissingMediaActions;
import com.archos.mediaprovider.video.SupernovaLibraryPolicy;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.function.BooleanSupplier;

/** Worker-only inventory and recovery. current() is the memory-only snapshot shared with attention counts. */
public final class MissingFilesRepository
{
    record Source(Uri root, String label)
    {
    }
    public static final class MissingFile
    {
        public final long id, size, sinceUtcMs;
        public final String title, sourceName, folder, oldFileName;
        private final MissingMediaActions.StoredFile stored;
        private final Uri source;

        private MissingFile(MissingMediaActions.StoredFile stored, String title, Source source, long since)
        {
            this.stored = stored;
            id = stored.id();
            size = stored.size();
            this.title = title == null || title.trim().isEmpty() ? "Unknown title" : title;
            this.source = source == null ? null : source.root;
            sourceName = source == null ? "Source unavailable" : source.label;
            String path = path(stored.path());
            int slash = path.lastIndexOf('/');
            folder = slash < 0 ? "" : path.substring(0, slash);
            oldFileName = slash < 0 ? path : path.substring(slash + 1);
            sinceUtcMs = since;
        }
    }
    public static final class Inventory
    {
        public final List<MissingFile> missing;
        public final LibraryAttentionCount attention;
        Inventory(List<MissingFile> missing, LibraryAttentionCount attention)
        {
            this.missing = List.copyOf(missing);
            this.attention = attention;
        }
    }
    private static volatile Inventory current;
    public static Inventory current()
    {
        return current;
    }

    /** indexedTitles is the same policy-visible population used by the library count worker. */
    public static Inventory refresh(Context context, Map<Long, String> indexedTitles, Set<Long> metadataIssues)
        throws IOException
    {
        SharedPreferences health = context.getSharedPreferences(SupernovaLibraryPolicy.HEALTH, 0);
        for (int attempt = 0; attempt < 3; attempt++)
        {
            Map<String, ?> before = health.getAll();
            List<MissingMediaActions.StoredFile> rows = MissingMediaActions.read(context);
            List<Source> sources = sources(context);
            if (!before.equals(health.getAll()))
                continue;
            Inventory inventory = build(rows, indexedTitles, metadataIssues, before, sources);
            current = inventory;
            return inventory;
        }
        throw new IOException("Library health changed during inventory");
    }

    static Inventory build(List<MissingMediaActions.StoredFile> rows, Map<Long, String> titles, Set<Long> metadata,
                           Map<String, ?> evidence, List<Source> sources)
    {
        Map<String, Object> active = new HashMap<>(evidence);
        Set<Long> indexed = new HashSet<>();
        List<MissingFile> missing = new ArrayList<>();
        for (MissingMediaActions.StoredFile row : rows)
        {
            if (!titles.containsKey(row.id()))
                continue;
            indexed.add(row.id());
            String marker = "media:" + row.id();
            String key = SupernovaLibraryPolicy.canonical(Uri.parse(row.path()));
            if (!key.equals(evidence.get(marker)))
            {
                active.remove(marker);
                continue;
            }
            boolean offline = evidence.keySet().stream().anyMatch(
                k -> k.startsWith("offline:") && SupernovaLibraryPolicy.contains(k.substring(8), key));
            if (offline)
                continue;
            Object since = evidence.get("missing_since:" + row.id());
            Source chosen = null;
            for (Source source : sources)
                if (SupernovaLibraryPolicy.contains(SupernovaLibraryPolicy.canonical(source.root), key) &&
                    (chosen == null || source.root.toString().length() > chosen.root.toString().length()))
                    chosen = source;
            missing.add(new MissingFile(row, titles.get(row.id()), chosen, since instanceof Long ? (long)since : 0));
        }
        missing.sort(java.util.Comparator.comparing(row -> row.title, String.CASE_INSENSITIVE_ORDER));
        return new Inventory(missing, LibraryAttentionCount.count(indexed, metadata, active));
    }

    private static List<Source> sources(Context context) throws IOException
    {
        List<Source> roots = new ArrayList<>();
        List<ShortcutDbAdapter.Shortcut> shortcuts = ShortcutDbAdapter.VIDEO.readCopiedShortcuts(context);
        if (shortcuts == null)
            throw new IOException("Source inventory unavailable");
        for (ShortcutDbAdapter.Shortcut shortcut : shortcuts)
            roots.add(new Source(Uri.parse(shortcut.getUri()), safe(shortcut.getName())));
        Set<String> configured =
            PreferenceManager.getDefaultSharedPreferences(context).getStringSet("supernova_library_roots", Set.of());
        for (String root : configured)
        {
            Uri uri = Uri.parse(root);
            String name = "file".equals(uri.getScheme()) ? "Local storage" : "Network share";
            StorageManager manager = (StorageManager)context.getSystemService(Context.STORAGE_SERVICE);
            if (Build.VERSION.SDK_INT >= 24 && manager != null && "file".equals(uri.getScheme()))
                for (StorageVolume volume : manager.getStorageVolumes())
                {
                    String location = Build.VERSION.SDK_INT >= 30 && volume.getDirectory() != null
                                          ? volume.getDirectory().getPath()
                                      : volume.getUuid() == null ? ""
                                                                 : "/storage/" + volume.getUuid();
                    if (!location.isEmpty() &&
                        (uri.getPath().equals(location) || uri.getPath().startsWith(location + "/")))
                        name = safe(volume.getDescription(context));
                }
            roots.add(new Source(uri, name));
        }
        return roots;
    }

    /** Complete the search before mutating; ambiguous/no matches return false and change nothing. */
    public static boolean findAgain(Context context, MissingFile row, BooleanSupplier cancelled) throws Exception
    {
        return findAgain(context, row, false, cancelled, ignored -> {});
    }

    public record SearchProgress(long checked, int foldersRemaining)
    {
    }

    /** Worker-only; all sources must finish before a globally unique replacement is committed. */
    public static boolean findAgain(Context context, MissingFile row, boolean everywhere,
                                    BooleanSupplier cancelled, java.util.function.Consumer<SearchProgress> progress)
        throws Exception
    {
        List<Uri> roots = new ArrayList<>();
        if (row.source != null)
            roots.add(row.source);
        if (everywhere)
            for (Source source : sources(context))
                if (!roots.contains(source.root))
                    roots.add(source.root);
        try (ScanIoScope scope = new ScanIoScope(60, TimeUnit.SECONDS))
        {
            Uri candidate = MissingFileSearch.unique(roots, row.oldFileName, row.size, folder -> {
                ScanIoScope.checkpoint();
                MetaFile2 directory = MetaFileFactoryWithUpnp.getMetaFileForUrl(folder);
                if (directory == null || !directory.isDirectory())
                    throw new IOException("Source folder is unavailable");
                List<MetaFile2> files = directory.getRawListerInstance().getFileList();
                if (files == null)
                    throw new IOException("Source listing is unavailable");
                List<MissingFileSearch.FileEntry> entries = new ArrayList<>();
                for (MetaFile2 file : files)
                {
                    Uri uri = file.getUri();
                    // A local symlink must not move the traversal to another physical source.
                    if ("file".equals(uri.getScheme()))
                        uri = Uri.fromFile(new File(uri.getPath()).getCanonicalFile());
                    if (file.isDirectory() || file.isFile())
                        entries.add(
                            new MissingFileSearch.FileEntry(uri, file.getName(), file.length(), file.isDirectory()));
                }
                return entries;
            }, cancelled, value -> progress.accept(new SearchProgress(value.checked(), value.foldersRemaining())));
            if (candidate == null)
                return false;
            ScanIoScope.checkpoint();
            MetaFile2 file = MetaFileFactoryWithUpnp.getMetaFileForUrl(candidate);
            if (cancelled.getAsBoolean() || file == null || !file.isFile() || file.length() != row.size)
                throw new IOException("Replacement changed during search");
            String replacement = row.stored.path().startsWith("/") ? candidate.getPath() : candidate.toString();
            Uri targetRoot = null;
            for (Uri root : roots)
                if (SupernovaLibraryPolicy.contains(SupernovaLibraryPolicy.canonical(root),
                                                     SupernovaLibraryPolicy.canonical(candidate)))
                    targetRoot = root;
            boolean changed = everywhere
                ? MissingMediaActions.relinkAcrossSources(context, row.stored, row.source, targetRoot, replacement)
                : MissingMediaActions.relink(context, row.stored, row.source, replacement);
            if (changed)
                PreviewLibraryLoader.invalidateHistoryCache(context);
            return changed;
        }
    }
    public static boolean remove(Context context, MissingFile row) throws IOException
    {
        boolean changed = MissingMediaActions.remove(context, row.stored);
        if (changed)
            PreviewLibraryLoader.invalidateHistoryCache(context);
        return changed;
    }
    private static String safe(String value)
    {
        String result = DiagnosticRedactor.clean(value == null ? "Source" : value).replaceAll("[\\p{Cntrl}]", " ");
        return result.length() > 160 ? result.substring(0, 160) : result;
    }
    private static String path(String value)
    {
        return value.startsWith("/") ? value : Uri.parse(value).getPath();
    }
    private MissingFilesRepository()
    {
    }
}
