package com.archos.mediacenter.video.metadata;

import android.database.Cursor;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.List;
import java.util.function.BooleanSupplier;

/** Worker adapter for the provider's full video-view cursor, including every episode and Put.io row. */
public final class WrongMatchInventory
{
    // Native BaseTags types; these are persisted schema values, not presentation categories.
    private static final int MOVIE = 11;
    private static final int TV_SHOW = 12;

    private static final String[] PROJECTION = {"_id", "_data", "ArchosMediaScraper_type", "ArchosMediaScraper_id",
                                              "m_online_id", "s_online_id"};

    public static String[] projection()
    {
        return PROJECTION.clone();
    }

    public static List<WrongMatches.Member> read(Cursor rows, BooleanSupplier cancelled) throws Exception
    {
        List<WrongMatches.Member> result = new ArrayList<>();
        int id = rows.getColumnIndexOrThrow("_id");
        int path = rows.getColumnIndexOrThrow("_data");
        int type = rows.getColumnIndexOrThrow("ArchosMediaScraper_type");
        int local = rows.getColumnIndexOrThrow("ArchosMediaScraper_id");
        int movie = rows.getColumnIndexOrThrow("m_online_id");
        int show = rows.getColumnIndexOrThrow("s_online_id");
        rows.moveToPosition(-1);
        while (rows.moveToNext())
        {
            if (cancelled.getAsBoolean() || Thread.currentThread().isInterrupted())
                throw new java.io.InterruptedIOException("Wrong-match inventory cancelled");
            int kind = rows.getInt(type);
            long online = kind == MOVIE ? rows.getLong(movie) : kind == TV_SHOW ? rows.getLong(show) : 0;
            if (rows.getLong(id) <= 0 || online <= 0 || rows.getLong(local) <= 0)
                continue;
            String owner = rows.getLong(id) + ":" + rows.getString(path) + ":" + kind + ":" + rows.getLong(local)
                + ":" + online;
            byte[] digest = MessageDigest.getInstance("SHA-256").digest(owner.getBytes(StandardCharsets.UTF_8));
            StringBuilder revision = new StringBuilder();
            for (byte value : digest)
                revision.append(String.format(java.util.Locale.ROOT, "%02x", value & 255));
            result.add(new WrongMatches.Member(rows.getLong(id), kind == MOVIE ? "movie" : "tv", online,
                                               revision.toString()));
        }
        return List.copyOf(result);
    }

    private WrongMatchInventory()
    {
    }
}
