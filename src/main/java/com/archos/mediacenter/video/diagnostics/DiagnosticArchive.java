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
                "(?:(?:events|playback|flight)\\.jsonl(?:\\.[1-4])?|important-[0-9]{8}\\.jsonl(?:\\.[1-3])?)")) result.add(file);
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
        long first = Long.MAX_VALUE, last = 0;
        for (JSONObject record : records(directory)) {
            long time = record.optLong("utc_ms"); if (time > 0) { first = Math.min(first, time); last = Math.max(last, time); }
            String process = record.optString("process"), session = record.optString("session");
            if (!process.isEmpty()) processes.add(process);
            if (!session.isEmpty()) sessions.add(session);
            if (record.optString("event").equals("manual_problem_marker")) markers.put(record);
            if (record.optString("event").equals("incident_capture")) incidents.put(record);
        }
        return manifest.put("schema", 1).put("completeness", "PARTIAL")
                .put("first_utc_ms", first == Long.MAX_VALUE ? 0 : first).put("last_utc_ms", last)
                .put("processes", processes.size()).put("playback_sessions", sessions.size())
                .put("manual_reports", markers).put("incidents", incidents)
                .put("unclean_exit_interpretation", "Missing clean marker; not evidence of a confirmed crash");
    }
    private DiagnosticArchive() {}
}
