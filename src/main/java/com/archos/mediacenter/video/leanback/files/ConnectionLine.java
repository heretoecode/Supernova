package com.archos.mediacenter.video.leanback.files;

import java.util.Locale;

/** Wording for a row on Files › Connections (mock-up network-files-tabs/05). */
public final class ConnectionLine
{
    private ConnectionLine()
    {
    }

    /** "SMB", "WebDAV", "SFTP", "FTP", "UPnP" from a connection's address scheme. */
    public static String kind(String scheme)
    {
        String value = scheme == null ? "" : scheme.toLowerCase(Locale.ROOT);
        switch (value)
        {
            case "smb":
                return "SMB";
            case "webdav":
            case "webdavs":
                return "WebDAV";
            case "sftp":
                return "SFTP";
            case "ftp":
            case "ftps":
                return "FTP";
            case "upnp":
                return "UPnP";
            default:
                return value.toUpperCase(Locale.ROOT);
        }
    }

    /** Reachable: "SMB · 512 videos in your library"; not reachable: "SFTP · last seen yesterday". */
    public static String second(String scheme, Boolean reachable, long videos, long lastSeenMs, long nowMs)
    {
        String kind = kind(scheme);
        if (Boolean.FALSE.equals(reachable))
        {
            return kind + " · " + (lastSeenMs <= 0 ? "not reached yet" : "last seen " + seen(lastSeenMs, nowMs));
        }
        return kind + " · " + videos + (videos == 1 ? " video" : " videos") + " in your library";
    }

    public static String pill(Boolean reachable)
    {
        if (reachable == null)
        {
            return "Checking…";
        }
        return reachable ? "Reachable" : "Not reachable";
    }

    static String seen(long thenMs, long nowMs)
    {
        long hours = Math.max(0, (nowMs - thenMs) / 3_600_000L);
        if (hours < 1)
        {
            return "just now";
        }
        if (hours < 24)
        {
            return hours + (hours == 1 ? " hour ago" : " hours ago");
        }
        long days = hours / 24;
        return days == 1 ? "yesterday" : days + " days ago";
    }
}
