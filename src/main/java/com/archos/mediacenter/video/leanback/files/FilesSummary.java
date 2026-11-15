package com.archos.mediacenter.video.leanback.files;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * The facts on the right of the Files tab row (owner, 12 Nov 2026; mock-up network-files-tabs/39): each fact with its
 * own icon, words capitalised, a dot between them, ending with the scan control's "12 min ago" (or "Scanning").
 */
public final class FilesSummary
{
    public static final class Fact
    {
        public final String icon;
        public final String text;

        Fact(String icon, String text)
        {
            this.icon = icon;
            this.text = text;
        }
    }

    private FilesSummary()
    {
    }

    public static List<Fact> facts(int videos, int toCheck, long lastScanMs, long nowMs, boolean scanning)
    {
        List<Fact> facts = new ArrayList<>();
        facts.add(new Fact("film", NumberFormat.getIntegerInstance(Locale.UK).format(videos)
            + (videos == 1 ? " Video" : " Videos")));
        if (toCheck > 0)
        {
            facts.add(new Fact("health", toCheck + " To Check"));
        }
        facts.add(new Fact("scan", scanning ? "Scanning" : ago(lastScanMs, nowMs)));
        return facts;
    }

    /** This Device tab: "2 Devices · 1,513 Videos · 1.1 TB Free" (mock-up network-files-tabs/01). */
    public static List<Fact> deviceFacts(int devices, long videos, long freeBytes)
    {
        List<Fact> facts = new ArrayList<>();
        facts.add(new Fact("tv", devices + (devices == 1 ? " Device" : " Devices")));
        facts.add(new Fact("film", NumberFormat.getIntegerInstance(Locale.UK).format(videos)
            + (videos == 1 ? " Video" : " Videos")));
        facts.add(new Fact("size", DeviceLine.size(freeBytes) + " Free"));
        return facts;
    }

    /** Connections tab: "3 Connections · 2 Reachable · 1,416 Videos" (mock-up network-files-tabs/05). */
    public static List<Fact> connectionFacts(int connections, int reachable, int videos)
    {
        List<Fact> facts = new ArrayList<>();
        facts.add(new Fact("integration", connections + (connections == 1 ? " Connection" : " Connections")));
        facts.add(new Fact("done", reachable + " Reachable"));
        facts.add(new Fact("film", NumberFormat.getIntegerInstance(Locale.UK).format(videos)
            + (videos == 1 ? " Video" : " Videos")));
        return facts;
    }

    /** "Just now", "12 min ago", "3 hours ago", "2 days ago", or "Not scanned yet". */
    public static String ago(long thenMs, long nowMs)
    {
        if (thenMs <= 0)
        {
            return "Not scanned yet";
        }
        long minutes = Math.max(0, (nowMs - thenMs) / 60000);
        if (minutes < 1)
        {
            return "Just now";
        }
        if (minutes < 60)
        {
            return minutes + " min ago";
        }
        long hours = minutes / 60;
        if (hours < 24)
        {
            return hours + (hours == 1 ? " hour ago" : " hours ago");
        }
        long days = hours / 24;
        return days + (days == 1 ? " day ago" : " days ago");
    }
}
