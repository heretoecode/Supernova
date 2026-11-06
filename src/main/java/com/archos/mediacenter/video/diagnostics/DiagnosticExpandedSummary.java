package com.archos.mediacenter.video.diagnostics;

import java.util.ArrayList;
import java.util.ArrayDeque;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import org.json.JSONObject;

/** Summarizes only retained evidence. Stable ordering makes repeated exports comparable. */
final class DiagnosticExpandedSummary
{
    private static final String[] AREAS = {"Thumbnails",         "Playback", "Network",  "Scans",        "Metadata",
                                           "Backup and restore", "UI",       "Settings", "Process exits"};
    private static final class ErrorCount
    {
        long count, first = Long.MAX_VALUE, last;
        String example = "";
    }

    static String build(List<JSONObject> records)
    {
        Map<String, Map<String, Long>> areas = new LinkedHashMap<>();
        for (String area : AREAS)
            areas.put(area, new TreeMap<>());
        Map<String, ArrayDeque<String>> details = new LinkedHashMap<>();
        Map<String, ErrorCount> errors = new TreeMap<>();
        Map<String, List<String>> timelines = new TreeMap<>();
        for (JSONObject record : records)
        {
            String event = record.optString("event"), area = area(event);
            if (area != null)
            {
                areas.get(area).merge(event, 1L, Long::sum);
                String measured = measured(record);
                if (!measured.isEmpty())
                {
                    ArrayDeque<String> recent = details.computeIfAbsent(area, ignored -> new ArrayDeque<>());
                    recent.add(record.optLong("utc_ms") + " " + event + " session=" + record.optString("session") +
                               " " + measured);
                    while (recent.size() > 20)
                        recent.removeFirst();
                }
            }
            String severity = record.optString("severity");
            boolean error = severity.equals("ERROR") || severity.equals("FATAL");
            if (error)
            {
                String key = event + " / " + record.optString("failure_class", "unspecified");
                ErrorCount count = errors.computeIfAbsent(key, ignored -> new ErrorCount());
                count.count++;
                count.first = Math.min(count.first, record.optLong("utc_ms"));
                count.last = Math.max(count.last, record.optLong("utc_ms"));
                if (count.example.isEmpty())
                    count.example = DiagnosticRedactor.clean(record.optString("message", record.optString("trace")))
                                        .replace('\n', ' ')
                                        .replace('\r', ' ')
                                        .replace('|', '/');
                if (count.example.length() > 300)
                    count.example = count.example.substring(0, 300);
            }
            if (error || severity.equals("WARNING"))
            {
                String session = record.optString("session");
                if (session.isEmpty())
                    session = record.optString("app_session");
                if (session.isEmpty())
                    session = "process:" + record.optString("process", "unknown");
                timelines.computeIfAbsent(session, ignored -> new ArrayList<>())
                    .add(record.optLong("utc_ms") + " " + severity + " " + event + " " +
                         DiagnosticRedactor.clean(record.optString("message")));
            }
        }
        StringBuilder text = new StringBuilder("\nExpanded retained evidence by area\n");
        for (Map.Entry<String, Map<String, Long>> area : areas.entrySet())
        {
            text.append('\n').append(area.getKey()).append('\n');
            if (area.getValue().isEmpty())
                text.append("No events retained.\n");
            else
                area.getValue().forEach((event, count) -> text.append(event).append(": ").append(count).append('\n'));
            ArrayDeque<String> measured = details.get(area.getKey());
            if (measured != null)
            {
                text.append("Recent measured details (up to 20 retained records):\n");
                measured.forEach(line -> text.append(line).append('\n'));
            }
        }
        text.append("\nTop 20 errors by count\nCount | First UTC ms | Last UTC ms | Event / class | Example message\n");
        errors.entrySet()
            .stream()
            .sorted(Comparator.<Map.Entry<String, ErrorCount>>comparingLong(e -> e.getValue().count)
                        .reversed()
                        .thenComparing(Map.Entry::getKey))
            .limit(20)
            .forEach(entry -> {
                ErrorCount c = entry.getValue();
                text.append(c.count)
                    .append(" | ")
                    .append(c.first)
                    .append(" | ")
                    .append(c.last)
                    .append(" | ")
                    .append(entry.getKey())
                    .append(" | ")
                    .append(c.example)
                    .append('\n');
            });
        text.append("\nPer-session warning/error timelines (retained events only)\n");
        timelines.forEach((session, lines) -> {
            text.append("Session ").append(session).append('\n');
            lines.forEach(line -> text.append(line).append('\n'));
        });
        return text.toString();
    }

    private static String measured(JSONObject record)
    {
        String event = record.optString("event");
        if (!(event.endsWith("_summary") || event.startsWith("backup_") || event.contains("decoder") ||
              event.equals("transport_read") || event.equals("transport_operation") ||
              event.equals("playback_frame_metrics") || event.equals("metadata_network_outcome") ||
              event.equals("metadata_request_result") || event.equals("metadata_semantic_failed") ||
              event.equals("stream_reconnect") || event.equals("configuration_changed") ||
              event.equals("memory_trim") || event.equals("screen_open") || event.contains("process_exit") ||
              event.equals("previous_session_unclean_exit")))
            return "";
        StringBuilder fields = new StringBuilder();
        for (String key : new String[] {"media_key",
                                        "source_kind",
                                        "interval_ms",
                                        "frames_made",
                                        "mean_extract_ms",
                                        "p95_extract_ms",
                                        "known_read_bytes",
                                        "unknown_read_frames",
                                        "cache_hit_rate",
                                        "buffering_while_extracting",
                                        "buffering_while_idle",
                                        "dropped_frames_available",
                                        "dropped_while_extracting",
                                        "dropped_while_idle",
                                        "status",
                                        "http_status",
                                        "body_bytes",
                                        "body_bytes_known",
                                        "service",
                                        "endpoint_category",
                                        "duration_ms",
                                        "source",
                                        "boundary",
                                        "throughput_bytes_per_second",
                                        "open_ns",
                                        "explicit_retries",
                                        "window_ns",
                                        "window_bytes",
                                        "native_available",
                                        "accepted_output_frames",
                                        "late_dropped_frames",
                                        "stale_seek_frames",
                                        "early_dropped_frames",
                                        "scheduled_audio_video_phase_ms",
                                        "initialized_decoder",
                                        "measurement_quality",
                                        "stage",
                                        "category",
                                        "rows",
                                        "bytes",
                                        "decoder_type",
                                        "decoder_name",
                                        "candidate_index",
                                        "codec_profile",
                                        "implementation",
                                        "dolby_vision_profile",
                                        "frames",
                                        "slow_frames",
                                        "window_ms",
                                        "longest_ms",
                                        "screen",
                                        "open_ms",
                                        "level",
                                        "heap_bytes",
                                        "native_heap_bytes",
                                        "idle_ms",
                                        "recovery_ms",
                                        "checkpoint_ms",
                                        "position_quality",
                                        "cause",
                                        "result",
                                        "reason",
                                        "description",
                                        "importance",
                                        "pss_kb",
                                        "rss_kb",
                                        "setting",
                                        "value"})
        {
            if (!record.has(key))
                continue;
            Object value = key.equals("value")
                               ? DiagnosticRedactor.settingValue(record.optString("setting"), record.opt(key))
                               : record.opt(key);
            String clean = DiagnosticRedactor.clean(String.valueOf(value)).replace('\n', ' ').replace('\r', ' ');
            if (clean.length() > 300)
                clean = clean.substring(0, 300);
            fields.append(key).append('=').append(clean).append(' ');
        }
        return fields.toString();
    }

    private static String area(String event)
    {
        if (event.startsWith("thumb_"))
            return "Thumbnails";
        if (event.startsWith("backup_") || event.startsWith("restore_"))
            return "Backup and restore";
        if (event.startsWith("scan_") || event.startsWith("scanner_"))
            return "Scans";
        if (event.startsWith("metadata_") || event.startsWith("omdb_") || event.contains("introdb") ||
            event.contains("chaptersdb"))
            return "Metadata";
        if (event.startsWith("transport_") || event.startsWith("network_") || event.startsWith("http_"))
            return "Network";
        if (event.startsWith("configuration_") || event.startsWith("setting_"))
            return "Settings";
        if (event.equals("uncaught_exception") || event.contains("process_exit") || event.contains("unclean_exit") ||
            event.contains("UNCLEAN") || event.contains("previous_session"))
            return "Process exits";
        if (event.startsWith("ui_") || event.startsWith("focus") || event.startsWith("screen_") ||
            event.startsWith("memory_"))
            return "UI";
        if (event.startsWith("playback_") || event.startsWith("player_") || event.startsWith("stream_") ||
            event.startsWith("seek_") || event.startsWith("audio_") || event.startsWith("subtitle_"))
            return "Playback";
        return null;
    }
}
