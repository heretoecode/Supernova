package com.archos.mediacenter.video.player;

import android.content.Context;
import android.net.Uri;
import com.archos.filecorelibrary.FileEditor;
import com.archos.filecorelibrary.MetaFile2;
import com.archos.filecorelibrary.RawLister;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.function.BooleanSupplier;

/** Private bounded pipe format. Carries precisely the properties used by the unchanged name-order selection. */
final class NextListingWire
{
    private static final int MAGIC = 0x534e3137;
    private static final int MAX_ROWS = 100000;

    static void write(DataOutputStream output, List<MetaFile2> files) throws IOException
    {
        write(output, files, false);
    }

    static void write(DataOutputStream output, List<MetaFile2> files, boolean includeSize) throws IOException
    {
        if (files == null || files.size() > MAX_ROWS)
            throw new IOException("Next listing did not complete");
        output.writeInt(MAGIC);
        output.writeInt(files.size());
        output.writeBoolean(includeSize);
        for (MetaFile2 file : files)
        {
            output.writeUTF(bounded(file.getUri().toString(), 16000));
            output.writeUTF(bounded(file.getName(), 16000));
            output.writeBoolean(file.isFile());
            output.writeBoolean(file.isDirectory());
            String mime = file.getMimeType();
            output.writeBoolean(mime != null);
            if (mime != null)
                output.writeUTF(bounded(mime, 1024));
            if (includeSize)
                output.writeLong(file.length());
        }
        output.writeInt(MAGIC);
        output.flush();
    }

    static List<MetaFile2> read(DataInputStream input, BooleanSupplier cancelled) throws IOException
    {
        if (input.readInt() != MAGIC)
            throw new IOException("Invalid next listing");
        int count = input.readInt();
        if (count < 0 || count > MAX_ROWS)
            throw new IOException("Next listing exceeds its bound");
        boolean includeSize = input.readBoolean();
        List<MetaFile2> files = new ArrayList<>();
        long characters = 0;
        for (int i = 0; i < count; i++)
        {
            if (cancelled.getAsBoolean())
                throw new java.io.InterruptedIOException("Next listing cancelled");
            String uri = bounded(input.readUTF(), 16000);
            String name = bounded(input.readUTF(), 16000);
            characters += uri.length() + name.length();
            if (characters > 4 * 1024 * 1024)
                throw new IOException("Next listing text exceeds its bound");
            boolean file = input.readBoolean();
            boolean directory = input.readBoolean();
            String mime = input.readBoolean() ? bounded(input.readUTF(), 1024) : null;
            long size = includeSize ? input.readLong() : -1;
            files.add(new Entry(uri, name, file, directory, mime, size));
        }
        if (input.readInt() != MAGIC || cancelled.getAsBoolean())
            throw new IOException("Incomplete or obsolete next listing");
        return files;
    }

    private static String bounded(String value, int limit) throws IOException
    {
        if (value == null || value.length() > limit)
            throw new IOException("Invalid next listing property");
        return value;
    }

    private static final class Entry extends MetaFile2
    {
        private final String uri, name, mime;
        private final boolean file, directory;
        private final long size;

        Entry(String uri, String name, boolean file, boolean directory, String mime, long size)
        {
            this.uri = uri;
            this.name = name;
            this.file = file;
            this.directory = directory;
            this.mime = mime;
            this.size = size;
        }

        @Override public String getName()
        {
            return name;
        }
        @Override public boolean isDirectory()
        {
            return directory;
        }
        @Override public boolean isFile()
        {
            return file;
        }
        @Override public String getMimeType()
        {
            return mime;
        }
        @Override public Uri getUri()
        {
            return Uri.parse(uri);
        }
        @Override public boolean isRemote()
        {
            return true;
        }
        @Override public long lastModified()
        {
            return -1;
        }
        @Override public long length()
        {
            return size;
        }
        @Override public boolean canRead()
        {
            return false;
        }
        @Override public boolean canWrite()
        {
            return false;
        }
        @Override public RawLister getRawListerInstance()
        {
            throw new UnsupportedOperationException("Listing snapshot only");
        }
        @Override public FileEditor getFileEditorInstance(Context context)
        {
            throw new UnsupportedOperationException("Listing snapshot only");
        }
    }

    private NextListingWire()
    {
    }
}
