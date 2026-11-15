package com.archos.mediacenter.video.leanback.files;

import java.text.NumberFormat;
import java.util.Locale;

/** Wording for Files › This Device rows (mock-up network-files-tabs/01). */
public final class DeviceLine
{
    private DeviceLine()
    {
    }

    /** "14.2 GB", "1.1 TB", "512 MB": one decimal below 100, whole numbers above; decimal units as drives use. */
    public static String size(long bytes)
    {
        double value = Math.max(0, bytes);
        String[] units = {"B", "KB", "MB", "GB", "TB"};
        int unit = 0;
        while (value >= 1000 && unit < units.length - 1)
        {
            value /= 1000;
            unit++;
        }
        String number = value >= 100 || unit == 0 ? String.valueOf(Math.round(value))
            : String.format(Locale.UK, "%.1f", value).replace(".0", "");
        return number + " " + units[unit];
    }

    /** "SanDisk USB drive · 1,203 videos · 1.1 TB free of 2 TB" (the kind is left out for this device itself). */
    public static String second(String kind, long videos, long freeBytes, long totalBytes)
    {
        StringBuilder line = new StringBuilder();
        if (kind != null && !kind.isEmpty())
        {
            line.append(kind).append(" · ");
        }
        line.append(NumberFormat.getIntegerInstance(Locale.UK).format(videos));
        line.append(videos == 1 ? " video" : " videos");
        if (totalBytes > 0)
        {
            line.append(" · ").append(size(freeBytes)).append(" free of ").append(size(totalBytes));
        }
        return line.toString();
    }

    /** Share of the drive in use, 0–1000 for a progress bar. */
    public static int used(long freeBytes, long totalBytes)
    {
        if (totalBytes <= 0)
        {
            return 0;
        }
        long used = Math.max(0, totalBytes - Math.max(0, freeBytes));
        return (int) Math.min(1000, used * 1000 / totalBytes);
    }
}
