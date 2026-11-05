package com.archos.mediacenter.video.diagnostics;

import java.io.File;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/** File metadata is sampled once; concurrent writers cannot change the comparator's ordering. */
final class RetentionSnapshot
{
    static final class Entry
    {
        final File file;
        final long modified;
        final long bytes;
        final String name;

        Entry(File file)
        {
            this.file = file;
            modified = file.lastModified();
            bytes = file.length();
            name = file.getName();
        }
    }

    static List<Entry> oldestFirst(List<File> files)
    {
        List<Entry> entries = new ArrayList<>();
        for (File file : files)
        {
            entries.add(new Entry(file));
        }
        entries.sort(Comparator.comparingLong((Entry entry) -> entry.modified).thenComparing(entry -> entry.name));
        return entries;
    }

    private RetentionSnapshot()
    {
    }
}
