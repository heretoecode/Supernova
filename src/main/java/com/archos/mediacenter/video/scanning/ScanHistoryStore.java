package com.archos.mediacenter.video.scanning;

import android.util.AtomicFile;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
import java.util.ArrayList;
import java.util.List;

/**
 * Worker-owned bounded history. The published immutable list can be read without disk access.
 * Scans finish in more than one process, so every write holds a file lock and starts from what is on disk; a file
 * that cannot be read is reported once and set aside, so the next scan starts a clean history.
 */
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
        try
        {
            List<ScanProgress> loaded = read();
            if (loaded != null)
                recent = loaded;
        }
        catch (IOException unreadable)
        {
            setAside();
            throw unreadable;
        }
    }

    /** Null when there is no history yet. */
    private List<ScanProgress> read() throws IOException
    {
        if (!file.getBaseFile().exists() && !new File(file.getBaseFile() + ".bak").exists())
            return null;
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
            return List.copyOf(loaded);
        }
    }

    void append(ScanProgress value) throws IOException
    {
        if (value.running)
            throw new IllegalArgumentException("Only finished scans belong in history");
        File lockLocation = new File(file.getBaseFile().getPath() + ".lock");
        try (RandomAccessFile lockFile = new RandomAccessFile(lockLocation, "rw");
             FileChannel channel = lockFile.getChannel();
             FileLock lock = channel.lock())
        {
            write(value, current());
        }
    }

    /** What is on disk now (another process may have added to it), or the published list when unreadable. */
    private List<ScanProgress> current()
    {
        try
        {
            List<ScanProgress> onDisk = read();
            if (onDisk != null)
                return onDisk;
        }
        catch (IOException unreadable)
        {
            setAside();
        }
        return recent;
    }

    private void setAside()
    {
        File base = file.getBaseFile();
        File aside = new File(base.getPath() + ".unreadable");
        if (aside.exists() && !aside.delete())
            return;
        if (base.exists() && !base.renameTo(aside))
            file.delete();
    }

    private void write(ScanProgress value, List<ScanProgress> previous) throws IOException
    {
        List<ScanProgress> next = new ArrayList<>(previous);
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
