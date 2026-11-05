package com.archos.mediacenter.video.leanback.filebrowsing;

import android.content.Context;
import android.net.Uri;
import com.archos.mediacenter.video.browser.loader.VideosInFolderLoader;
import com.archos.mediaprovider.video.VideoStore;

/** Browser enrichment uses physical rows, independently of Library's watched/hidden display filters. */
public final class LibraryFileLookup extends VideosInFolderLoader
{
    public LibraryFileLookup(Context context, Uri folder)
    {
        super(context, databasePath(folder));
    }

    static String databasePath(Uri uri)
    {
        return "file".equalsIgnoreCase(uri.getScheme()) ? uri.getPath() : uri.toString();
    }

    public static String key(Uri uri)
    {
        return "file".equalsIgnoreCase(uri.getScheme()) ? "file:" + uri.getPath() : uri.toString();
    }

    public static String keyForDatabasePath(String path)
    {
        return path.startsWith("/") ? "file:" + path : key(Uri.parse(path));
    }

    @Override
    public String getSelection()
    {
        String data = VideoStore.MediaColumns.DATA;
        return data + " NOT LIKE ? ESCAPE '\\' AND " + data + " LIKE ? ESCAPE '\\'";
    }

    @Override
    public String[] getSelectionArgs()
    {
        // Parent loader adds the trailing slash. Preserve literal '%' and '_' in real folder names.
        String[] original = super.getSelectionArgs();
        String folder = original[0].substring(0, original[0].length() - 3);
        String escaped = folder.replace("\\", "\\\\").replace("%", "\\%").replace("_", "\\_");
        return new String[] {escaped + "%/%", escaped + "%"};
    }
}
