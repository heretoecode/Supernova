package com.archos.mediacenter.video.health;

import android.net.Uri;
import com.archos.mediaprovider.video.SupernovaLibraryPolicy;
import java.io.IOException;
import java.io.InterruptedIOException;
import java.util.ArrayDeque;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.BooleanSupplier;

/** A match is usable only after the complete same-source traversal proves it unique. */
final class MissingFileSearch
{
    record FileEntry(Uri uri, String name, long size, boolean folder)
    {
    }
    interface Listing
    {
        List<FileEntry> list(Uri folder) throws Exception;
    }
    record Progress(long checked, int foldersRemaining)
    {
    }

    static Uri unique(Uri root, String name, long size, Listing listing, BooleanSupplier cancelled) throws Exception
    {
        return unique(java.util.Collections.singletonList(root), name, size, listing, cancelled, ignored -> {});
    }

    static Uri unique(List<Uri> roots, String name, long size, Listing listing, BooleanSupplier cancelled,
                      java.util.function.Consumer<Progress> progress) throws Exception
    {
        if (roots.isEmpty() || roots.stream().anyMatch(java.util.Objects::isNull) || size < 0)
            throw new IOException("Source or file size is unavailable");
        List<String> scopes = new java.util.ArrayList<>();
        for (Uri root : roots)
            scopes.add(SupernovaLibraryPolicy.canonical(root));
        ArrayDeque<Uri> folders = new ArrayDeque<>();
        Set<String> visited = new HashSet<>(), matches = new HashSet<>();
        folders.addAll(roots);
        int checked = 0;
        Uri found = null;
        while (!folders.isEmpty())
        {
            if (cancelled.getAsBoolean() || Thread.currentThread().isInterrupted())
                throw new InterruptedIOException("Search stopped");
            Uri folder = folders.remove();
            if (!visited.add(SupernovaLibraryPolicy.canonical(folder)))
                continue;
            List<FileEntry> entries = listing.list(folder);
            if (entries == null)
                throw new IOException("Source listing is unavailable");
            for (FileEntry entry : entries)
            {
                if (++checked > 100000 || cancelled.getAsBoolean() || Thread.currentThread().isInterrupted())
                    throw new IOException("Search did not finish");
                String key = SupernovaLibraryPolicy.canonical(entry.uri);
                if (scopes.stream().noneMatch(scope -> SupernovaLibraryPolicy.contains(scope, key)) ||
                    !SupernovaLibraryPolicy.contains(SupernovaLibraryPolicy.canonical(folder), key))
                    throw new IOException("Source listing escaped its root");
                if (entry.folder)
                    folders.add(entry.uri);
                else if (entry.size == size && name.equals(entry.name) && matches.add(key))
                    found = entry.uri;
            }
            progress.accept(new Progress(checked, folders.size()));
        }
        return matches.size() == 1 ? found : null;
    }
    private MissingFileSearch()
    {
    }
}
