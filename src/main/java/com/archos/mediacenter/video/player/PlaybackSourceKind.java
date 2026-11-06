package com.archos.mediacenter.video.player;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import com.archos.mediaprovider.video.VideoStore;

/** Resolve the actual locator: a public library ID no longer identifies the owning physical index. Worker only. */
public final class PlaybackSourceKind
{
    static boolean localBeforeResolution(Uri uri)
    {
        String scheme = uri.getScheme();
        return scheme == null || scheme.equals("file") || scheme.equals("content") &&
            (VideoStore.AUTHORITY.equals(uri.getAuthority()) || "media".equals(uri.getAuthority()));
    }

    public static boolean isLocal(Context context, Uri uri)
    {
        String scheme = uri.getScheme();
        if (scheme == null || scheme.equals("file"))
            return true;
        if (!scheme.equals("content"))
            return false;
        if (!VideoStore.AUTHORITY.equals(uri.getAuthority()))
            return "media".equals(uri.getAuthority());
        try (Cursor row = context.getContentResolver().query(uri, new String[] {"_data"}, null, null, null))
        {
            if (row == null || !row.moveToFirst() || row.isNull(0))
                return false;
            String actual = Uri.parse(row.getString(0)).getScheme();
            return actual == null || actual.equals("file");
        }
        catch (RuntimeException unavailable)
        {
            return false;
        }
    }

    private PlaybackSourceKind()
    {
    }
}
