package com.archos.mediacenter.video.diagnostics;

import org.json.*;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;

/** Allow-listed diagnostic streams, deduplicated across events, playback and incident copies. */
final class DiagnosticArchive {
    static List<File> files(File directory) {
        File[] all = directory.listFiles();
        List<File> result = new ArrayList<>();
        if (all != null) for (File file : all) if (file.isFile() && file.getName().matches(
                "(?:(?:events|playback|flight)\\.jsonl(?:\\.[1-4])?|(?:important|incident-manual|incident-auto)-[0-9]{8}\\.jsonl(?:\\.[1-3])?)")) result.add(file);
        result.sort(Comparator.comparing(File::getName));
        return result;
    }

    static List<JSONObject> records(File directory) {
        Map<String,JSONObject> unique = new LinkedHashMap<>();
        for (File file : files(directory)) {
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(file), StandardCharsets.UTF_8))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    if (line.length() > 32768) continue;
                    try {
                        JSONObject record = new JSONObject(line);
                        String identity = record.has("sequence") ? record.optString("process") + ":" + record.optLong("sequence") : line;
                        unique.putIfAbsent(identity, record);
                    } catch (JSONException ignored) { /* A partial final write is not a complete event. */ }
                }
            } catch (IOException ignored) { /* Preserve the other available streams. */ }
        }
        List<JSONObject> records = new ArrayList<>(unique.values());
        records.sort(Comparator.comparingLong(record -> record.optLong("utc_ms")));
        return records;
    }

    static JSONObject manifest(File directory) throws JSONException {
        JSONObject manifest = new JSONObject();
        JSONArray markers = new JSONArray(), incidents = new JSONArray();
        Set<String> processes = new HashSet<>(), sessions = new HashSet<>();
        Map<String,Integer> counts = new TreeMap<>();
        Map<String,Long> dropped = new HashMap<>();
        Map<String,long[]> spans = new TreeMap<>();
        JSONArray evidence = new JSONArray();
        long first = Long.MAX_VALUE, last = 0;
        for (JSONObject record : records(directory)) {
            long time = record.optLong("utc_ms"); if (time > 0) { first = Math.min(first, time); last = Math.max(last, time); }
            String process = record.optString("process"), session = record.optString("session");
            if (!process.isEmpty()) processes.add(process);
            if (!session.isEmpty()) sessions.add(session);
            String event = record.optString("event");
            counts.put(event, counts.getOrDefault(event, 0) + 1);
            dropped.put(process, Math.max(dropped.getOrDefault(process, 0L), record.optLong("dropped")));
            if (!process.isEmpty() && time > 0) {
                long[] span = spans.get(process);
                if (span == null) spans.put(process, new long[]{time,time});
                else { span[0] = Math.min(span[0],time); span[1] = Math.max(span[1],time); }
            }
            if (Diagnostics.important(event) || event.equals("library_scan_requested") || event.equals("artwork_failed")) {
                evidence.put(new JSONObject().put("utc_ms", time).put("process", process)
                        .put("sequence", record.optLong("sequence")).put("event", event)
                        .put("operation_id", record.optString("operation_id")).put("session", session));
            }
            if (record.optString("event").equals("manual_problem_marker")) markers.put(record);
            if (record.optString("event").equals("incident_capture")) incidents.put(record);
        }
        JSONObject eventCounts = new JSONObject();
        for (Map.Entry<String,Integer> count : counts.entrySet()) eventCounts.put(count.getKey(),count.getValue());
        JSONArray processSpans = new JSONArray();
        for (Map.Entry<String,long[]> span : spans.entrySet()) processSpans.put(new JSONObject()
                .put("process",span.getKey()).put("first_retained_utc_ms",span.getValue()[0])
                .put("last_retained_utc_ms",span.getValue()[1])
                .put("retained_span_ms",span.getValue()[1]-span.getValue()[0]));
        long retainedDropped = 0; for (long count : dropped.values()) retainedDropped += count;
        return manifest.put("schema", 2).put("completeness", "PARTIAL")
                .put("first_utc_ms", first == Long.MAX_VALUE ? 0 : first).put("last_utc_ms", last)
                .put("processes", processes.size()).put("playback_sessions", sessions.size())
                .put("manual_reports", markers).put("incidents", incidents)
                .put("event_counts", eventCounts).put("process_spans",processSpans)
                .put("dropped_events_retained_process_maxima",retainedDropped)
                .put("launches_retained",counts.getOrDefault("startup",0))
                .put("clean_shutdowns_retained",counts.getOrDefault("session_clean_shutdown",0))
                .put("suspected_unclean_exits_retained",counts.getOrDefault("PREVIOUS_SESSION_UNCLEAN_EXIT",0))
                .put("significant_evidence", evidence)
                .put("duration_interpretation","Retained event spans, not complete process lifetimes or verified foreground time")
                .put("unclean_exit_interpretation", "Missing clean marker; not evidence of a confirmed crash");
    }
    private DiagnosticArchive() {}
}
