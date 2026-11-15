package com.archos.mediacenter.video.data;

import android.database.Cursor;
import android.net.Uri;
import java.util.ArrayList;
import java.util.List;
import org.json.JSONArray;
import org.json.JSONObject;

/** Single-row indexed file adapter. Caller owns the worker query and closes its cursor. No second network probe. */
public final class IndexedTechnical
{
    public static DetailsTechnical.File read(Cursor row, long expectedVideoId)
    {
        if (row.isBeforeFirst() || row.isAfterLast() || number(row, "_id") != expectedVideoId || expectedVideoId <= 0)
            throw new IllegalArgumentException("Technical row differs from the requested file");
        String path = text(row, "_data");
        String revision = expectedVideoId + ":" + number(row, "date_modified") + ":" + number(row, "_size");
        List<DetailsTechnical.Audio> audio = new ArrayList<>();
        try
        {
            JSONArray tracks = new JSONObject(text(row, "Archos_bestAudiotrackFormat")).optJSONArray("audiotracks");
            for (int i = 0; tracks != null && i < Math.min(64, tracks.length()); i++)
            {
                JSONObject track = tracks.optJSONObject(i);
                if (track != null)
                    audio.add(new DetailsTechnical.Audio(track.optString("format"), track.optString("channels"),
                                                        track.optString("language")));
            }
        }
        catch (Exception absent)
        {
            // Old indexed rows may have no measured track package.
        }
        Uri source = Uri.parse(path);
        String connection = source.getScheme() == null || "file".equals(source.getScheme()) ? "Local"
            : source.getHost() == null ? "" : source.getHost();
        String sourcePath = source.getPath();
        int slash = sourcePath == null ? -1 : sourcePath.lastIndexOf('/');
        String folder = slash < 0 ? "" : sourcePath.substring(0, slash + 1);
        long added = number(row, "date_added");
        long bitrate = number(row, "Archos_videoBitRate");
        return new DetailsTechnical.File(expectedVideoId, revision, dimension(row, "width"), dimension(row, "height"),
            text(row, "Archos_calculatedVideoFormat"), -1, "", -1, -1,
            bitrate > 0 && bitrate <= Long.MAX_VALUE / 1000 ? bitrate * 1000 : -1,
            audio, List.of(), text(row, "mime_type"), number(row, "_size"), connection, folder,
            added > 0 && added <= Long.MAX_VALUE / 1000 ? added * 1000 : -1);
    }

    private static int dimension(Cursor row, String column)
    {
        long value = number(row, column);
        return value > 0 && value <= Integer.MAX_VALUE ? (int) value : -1;
    }

    private static long number(Cursor row, String column)
    {
        int index = row.getColumnIndex(column);
        return index < 0 || row.isNull(index) ? -1 : row.getLong(index);
    }

    private static String text(Cursor row, String column)
    {
        int index = row.getColumnIndex(column);
        return index < 0 || row.isNull(index) ? "" : row.getString(index);
    }

    private IndexedTechnical()
    {
    }
}
