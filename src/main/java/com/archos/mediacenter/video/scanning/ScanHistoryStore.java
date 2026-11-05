package com.archos.mediacenter.video.scanning;

import android.util.AtomicFile;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/** Worker-owned bounded history. The published immutable list can be read without disk access. */
final class ScanHistoryStore
{
    private static final int VERSION = 1;
    private static final int LIMIT = 10;
    private final AtomicFile file;
    private volatile List<ScanProgress> recent = List.of();

    ScanHistoryStore(File location)
    {
        file = new AtomicFile(location);
    }

    List<ScanProgress> recent()
    {
        return recent;
    }

    void load() throws IOException
    {
        if (!file.getBaseFile().exists() && !new File(file.getBaseFile() + ".bak").exists())
            return;
        try (DataInputStream input = new DataInputStream(file.openRead()))
        {
            if (input.readInt() != VERSION)
                throw new IOException("Unsupported scan history");
            int count = input.readInt();
            if (count < 0 || count > LIMIT)
                throw new IOException("Invalid scan history count");
            List<ScanProgress> loaded = new ArrayList<>();
            for (int i = 0; i < count; i++)
            {
                String batch = input.readUTF(), source = input.readUTF(), label = input.readUTF();
                if (batch.length() > 100 || source.length() > 100 || label.length() > 160)
                    throw new IOException("Invalid scan history identity");
                long checked = input.readLong(), total = input.readLong(), added = input.readLong();
                long removed = input.readLong(), updated = input.readLong(), started = input.readLong();
                long duration = input.readLong();
                ScanProgress.Failure failure;
                try
                {
                    failure = ScanProgress.Failure.valueOf(input.readUTF());
                }
                catch (IllegalArgumentException invalid)
                {
                    throw new IOException("Invalid scan history outcome", invalid);
                }
                loaded.add(new ScanProgress(batch, source, label, checked, total, added, removed, updated, started,
                                            duration, false, false, failure));
            }
            if (input.read() != -1)
                throw new IOException("Trailing scan history data");
            recent = List.copyOf(loaded);
        }
    }

    void append(ScanProgress value) throws IOException
    {
        if (value.running)
            throw new IllegalArgumentException("Only finished scans belong in history");
        List<ScanProgress> next = new ArrayList<>(recent);
        next.add(0, value);
        if (next.size() > LIMIT)
            next.subList(LIMIT, next.size()).clear();
        FileOutputStream stream = file.startWrite();
        try
        {
            DataOutputStream output = new DataOutputStream(stream);
            output.writeInt(VERSION);
            output.writeInt(next.size());
            for (ScanProgress item : next)
            {
                output.writeUTF(item.batchId);
                output.writeUTF(item.sourceId);
                output.writeUTF(item.sourceLabel);
                output.writeLong(item.checked);
                output.writeLong(item.total);
                output.writeLong(item.added);
                output.writeLong(item.removed);
                output.writeLong(item.updated);
                output.writeLong(item.startedUtcMs);
                output.writeLong(item.durationMs);
                output.writeUTF(item.failure.name());
            }
            output.flush();
            file.finishWrite(stream);
            recent = List.copyOf(next);
        }
        catch (IOException | RuntimeException failure)
        {
            file.failWrite(stream);
            throw failure;
        }
    }
}
