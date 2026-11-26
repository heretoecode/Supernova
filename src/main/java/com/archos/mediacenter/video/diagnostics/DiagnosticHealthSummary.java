package com.archos.mediacenter.video.diagnostics;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TreeMap;
import java.util.TreeSet;
import org.json.JSONObject;

/**
 * The health summary at the top of each report (BRIEF_0158_B › Diagnostics upgrade): memory per page, start-up time,
 * the slowest page switch, slow frames, freezes, focus slips and NOVA pieces that ran, each against a limit, the lines
 * over their limit first.
 */
final class DiagnosticHealthSummary
{
    static final long PAGE_MEMORY_LIMIT_MB = 450;
    static final long COLD_START_LIMIT_MS = 4000;
    static final long WARM_START_LIMIT_MS = 1500;
    static final long PAGE_SWITCH_LIMIT_MS = 400;
    static final double SLOW_FRAME_LIMIT_PERCENT = 2.0;
    static final long FOCUS_SLIP_LIMIT = 20;

    private DiagnosticHealthSummary()
    {
    }

    private static final class Page
    {
        long peakKb;
        long totalKb;
        long samples;
    }

    static String build(List<JSONObject> records)
    {
        Map<String, Page> pages = new TreeMap<>();
        Map<String, long[]> frames = new TreeMap<>();
        long coldMax = -1;
        long warmMax = -1;
        long switchMax = -1;
        String switchWorst = "";
        long freezes = 0;
        long freezeWorst = 0;
        long focusSlips = 0;
        TreeSet<String> legacy = new TreeSet<>();
        Map<String, Integer> reasons = new java.util.HashMap<>();
        for (JSONObject r : records)
        {
            String event = r.optString("event");
            String severity = r.optString("severity");
            if ("ERROR".equals(severity) || "FATAL".equals(severity))
                reasons.merge(reasonLine(r), 1, Integer::sum);
            switch (event)
            {
                case "performance_trend":
                {
                    String page = r.optString("page", "unknown");
                    long kb = r.optLong("pss_kb", 0);
                    if (kb <= 0)
                        break;
                    Page p = pages.computeIfAbsent(page.isEmpty() ? "unknown" : page, k -> new Page());
                    p.peakKb = Math.max(p.peakKb, kb);
                    p.totalKb += kb;
                    p.samples++;
                    break;
                }
                case "startup_ready":
                    if ("cold".equals(r.optString("start")))
                        coldMax = Math.max(coldMax, r.optLong("first_frame_ms"));
                    else
                        warmMax = Math.max(warmMax, r.optLong("first_frame_ms"));
                    break;
                case "page_switch":
                    if (r.optLong("drawn_ms") > switchMax)
                    {
                        switchMax = r.optLong("drawn_ms");
                        switchWorst = r.optString("from") + " → " + r.optString("to");
                    }
                    break;
                case "ui_frame_summary":
                {
                    long[] f = frames.computeIfAbsent(r.optString("screen", "unknown"), k -> new long[2]);
                    f[0] += r.optLong("frames");
                    f[1] += r.optLong("slow_frames");
                    break;
                }
                case "main_thread_freeze":
                    freezes++;
                    freezeWorst = Math.max(freezeWorst, r.optLong("waited_ms"));
                    break;
                case "focus_anomaly_suspected":
                    focusSlips++;
                    break;
                case "legacy_component_started":
                    legacy.add(r.optString("component") + " (" + r.optString("page") + ")");
                    break;
                default:
                    break;
            }
        }
        List<String> over = new ArrayList<>();
        List<String> within = new ArrayList<>();
        for (Map.Entry<String, Page> page : pages.entrySet())
        {
            Page p = page.getValue();
            long peak = p.peakKb / 1024;
            line(peak > PAGE_MEMORY_LIMIT_MB, over, within, String.format(Locale.ROOT,
                "Memory on %s: peak %d MB, average %d MB (limit %d MB)", page.getKey(), peak,
                p.totalKb / Math.max(1, p.samples) / 1024, PAGE_MEMORY_LIMIT_MB));
        }
        if (coldMax >= 0)
            line(coldMax > COLD_START_LIMIT_MS, over, within, String.format(Locale.ROOT,
                "Cold start to Home: slowest %d ms (limit %d ms)", coldMax, COLD_START_LIMIT_MS));
        if (warmMax >= 0)
            line(warmMax > WARM_START_LIMIT_MS, over, within, String.format(Locale.ROOT,
                "Warm start to Home: slowest %d ms (limit %d ms)", warmMax, WARM_START_LIMIT_MS));
        if (switchMax >= 0)
            line(switchMax > PAGE_SWITCH_LIMIT_MS, over, within, String.format(Locale.ROOT,
                "Slowest page switch: %s, %d ms (limit %d ms)", switchWorst, switchMax, PAGE_SWITCH_LIMIT_MS));
        for (Map.Entry<String, long[]> f : frames.entrySet())
        {
            if (f.getValue()[0] <= 0)
                continue;
            double percent = 100.0 * f.getValue()[1] / f.getValue()[0];
            line(percent > SLOW_FRAME_LIMIT_PERCENT, over, within, String.format(Locale.ROOT,
                "Slow frames on %s: %.1f %% (limit %.1f %%)", f.getKey(), percent, SLOW_FRAME_LIMIT_PERCENT));
        }
        line(freezes > 0, over, within, String.format(Locale.ROOT,
            "Freezes of a second or more: %d, longest seen %d ms (limit 0)", freezes, freezeWorst));
        line(focusSlips > FOCUS_SLIP_LIMIT, over, within, String.format(Locale.ROOT,
            "Focus slips: %d (limit %d)", focusSlips, FOCUS_SLIP_LIMIT));
        line(!legacy.isEmpty(), over, within, "NOVA pieces that ran: " +
            (legacy.isEmpty() ? "none" : legacy.size() + ": " + android.text.TextUtils.join(", ", legacy)) + " (limit none)");
        StringBuilder text = new StringBuilder("Health summary (over the limit first)\n");
        List<Map.Entry<String, Integer>> top = new ArrayList<>(reasons.entrySet());
        top.sort((a, b) -> b.getValue() - a.getValue() != 0 ? b.getValue() - a.getValue()
                                                             : a.getKey().compareTo(b.getKey()));
        if (!top.isEmpty())
        {
            text.append("Top reasons for failures\n");
            for (int i = 0; i < Math.min(TOP_REASONS, top.size()); i++)
                text.append("  ").append(top.get(i).getKey().replace(" ×", " ×" + top.get(i).getValue()))
                    .append('\n');
        }
        for (String l : over)
            text.append("OVER  ").append(l).append('\n');
        for (String l : within)
            text.append("ok    ").append(l).append('\n');
        return text.append('\n').toString();
    }

    static final int TOP_REASONS = 8;

    /**
     * Item 9: "Delete failed ×: server refused (403)". The event in words, then its reason and the server's status
     * when there is one; the count is filled in by the caller.
     */
    static String reasonLine(JSONObject r)
    {
        String event = r.optString("event").replace('_', ' ');
        String words = event.isEmpty() ? "Failure" : Character.toUpperCase(event.charAt(0)) + event.substring(1);
        String reason = r.optString("reason", "not_known").replace('_', ' ');
        long status = r.optLong("http_status", -1);
        if (status <= 0)
            status = r.optLong("status", -1);
        return words + " ×: " + reason + (status > 0 ? " (" + status + ")" : "");
    }

    private static void line(boolean isOver, List<String> over, List<String> within, String text)
    {
        (isOver ? over : within).add(text);
    }
}
