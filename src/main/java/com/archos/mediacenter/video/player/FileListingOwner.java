package com.archos.mediacenter.video.player;

import android.content.Context;
import android.net.Uri;
import com.archos.filecorelibrary.MetaFile2;
import java.io.IOException;
import java.util.List;
import java.util.function.BooleanSupplier;

/** Worker-only immutable file snapshots. Every setup/list/stat is owned, including DNS and late UPnP callbacks. */
public final class FileListingOwner
{
    public static List<MetaFile2> directory(Context context, Uri uri, BooleanSupplier cancelled) throws IOException
    {
        return read(context, uri, cancelled, false);
    }

    public static MetaFile2 inspect(Context context, Uri uri, BooleanSupplier cancelled) throws IOException
    {
        List<MetaFile2> result = read(context, uri, cancelled, true);
        if (result.size() != 1)
            throw new IOException("No unique file snapshot");
        return result.get(0);
    }

    private static List<MetaFile2> read(Context context, Uri uri, BooleanSupplier cancelled, boolean inspect)
        throws IOException
    {
        try (NextListingProcess operation = new NextListingProcess())
        {
            operation.enter();
            operation.watch(cancelled);
            return operation.read(context, uri, true, inspect);
        }
    }

    private FileListingOwner()
    {
    }
}
