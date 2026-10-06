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
        Map<String,JSONObject> operations=new LinkedHashMap<>(),playback=new LinkedHashMap<>(),appSessions=new LinkedHashMap<>();
        long first = Long.MAX_VALUE, last = 0;
        for (JSONObject record : records(directory)) {
            long time = record.optLong("utc_ms"); if (time > 0) { first = Math.min(first, time); last = Math.max(last, time); }
            String process = record.optString("process"), session = record.optString("session");
            if (!process.isEmpty()) processes.add(process);
            if (!session.isEmpty()) sessions.add(session);
            String event = record.optString("event");
            String operation=record.optString("operation_id");
            String appSession=record.optString("app_session");
            if(!appSession.isEmpty()){
                JSONObject item=aggregate(appSessions,process+":"+appSession,record,"session",appSession);
                if(event.equals("app_session_begin")&&record.has("elapsed_ms"))item.put("started_elapsed_ms",record.getLong("elapsed_ms"));
                if(event.equals("app_session_end")&&record.has("elapsed_ms"))item.put("ended_elapsed_ms",record.getLong("elapsed_ms"));
            }
            if(!operation.isEmpty()){
                JSONObject item=aggregate(operations,process+":"+operation,record,"operation_id",operation);
                if((event.equals("operation_begin")||event.equals("artwork_operation_begin"))&&record.has("elapsed_ms"))item.put("started_elapsed_ms",record.getLong("elapsed_ms"));
                if(event.equals("operation_end")&&record.has("elapsed_ms"))item.put("ended_elapsed_ms",record.getLong("elapsed_ms"));
                if(!record.optString("parent_operation_id").isEmpty())item.put("parent_operation_id",record.getString("parent_operation_id"));
            }
            if(!session.isEmpty()){
                JSONObject item=aggregate(playback,process+":"+session,record,"session",session);
                if(event.equals("playback_begin")){item.put("started_utc_ms",time);if(record.has("elapsed_ms"))item.put("started_elapsed_ms",record.getLong("elapsed_ms"));}
                if(event.equals("playback_end")){item.put("ended_utc_ms",time);if(record.has("elapsed_ms"))item.put("ended_elapsed_ms",record.getLong("elapsed_ms"));}
            }
            counts.put(event, counts.getOrDefault(event, 0) + 1);
            dropped.put(process, Math.max(dropped.getOrDefault(process, 0L), record.optLong("dropped")));
            if (!process.isEmpty() && time > 0) {
                long[] span = spans.get(process);
                if (span == null) spans.put(process, new long[]{time,time});
                else { span[0] = Math.min(span[0],time); span[1] = Math.max(span[1],time); }
            }
            if (Diagnostics.important(event) || event.equals("library_scan_requested") || event.startsWith("scan_") || event.startsWith("artwork_")) {
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
        List<JSONObject> timed=new ArrayList<>(playback.values());timed.addAll(appSessions.values());timed.addAll(operations.values());
        for(JSONObject item:timed){
            boolean complete=item.has("started_elapsed_ms")&&item.has("ended_elapsed_ms")&&item.getLong("ended_elapsed_ms")>=item.getLong("started_elapsed_ms");
            item.put("duration_ms",complete?item.getLong("ended_elapsed_ms")-item.getLong("started_elapsed_ms"):JSONObject.NULL);
            item.put("duration_status",complete?"measured_begin_to_end":"incomplete_retained_evidence");
        }
        return manifest.put("schema", 3).put("completeness", "PARTIAL")
                .put("first_utc_ms", first == Long.MAX_VALUE ? 0 : first).put("last_utc_ms", last)
                .put("processes", processes.size()).put("playback_sessions", sessions.size())
                .put("manual_reports", markers).put("incidents", incidents)
                .put("event_counts", eventCounts).put("process_spans",processSpans)
                .put("operation_summaries",new JSONArray(operations.values())).put("playback_session_summaries",new JSONArray(playback.values()))
                .put("app_session_summaries",new JSONArray(appSessions.values())).put("app_session_interpretation","Foreground usage intervals, not Android process lifetime")
                .put("scans_requested_retained",counts.getOrDefault("scan_requested",0)+counts.getOrDefault("library_scan_requested",0))
                .put("artwork_requests_retained",counts.getOrDefault("artwork_request",0)+counts.getOrDefault("artwork_requested",0)+counts.getOrDefault("artwork_backdrop_request",0))
                .put("artwork_failures_retained",counts.getOrDefault("artwork_failed",0)+counts.getOrDefault("artwork_backdrop_failed",0))
                .put("dropped_events_retained_process_maxima",retainedDropped)
                .put("launches_retained",counts.getOrDefault("startup",0))
                .put("clean_shutdowns_retained",counts.getOrDefault("session_clean_shutdown",0))
                .put("suspected_unclean_exits_retained",counts.getOrDefault("PREVIOUS_SESSION_UNCLEAN_EXIT",0))
                .put("significant_evidence", evidence)
                .put("duration_interpretation","Retained event spans, not complete process lifetimes or verified foreground time")
                .put("unclean_exit_interpretation", "Missing clean marker; not evidence of a confirmed crash");
    }
    private static JSONObject aggregate(Map<String,JSONObject> groups,String key,JSONObject record,String idName,String id)throws JSONException{
        JSONObject item=groups.get(key);
        if(item==null){item=new JSONObject().put(idName,id).put("process",record.optString("process"))
                .put("first_utc_ms",record.optLong("utc_ms")).put("first_sequence",record.optLong("sequence")).put("first_event",record.optString("event")).put("event_count",0);groups.put(key,item);}
        return item.put("last_utc_ms",record.optLong("utc_ms")).put("last_sequence",record.optLong("sequence"))
                .put("last_event",record.optString("event")).put("event_count",item.getInt("event_count")+1);
    }
    static String linkedSummary(File directory)throws JSONException{
        JSONObject manifest=manifest(directory);StringBuilder text=new StringBuilder("\nRetained scan requests: ").append(manifest.getInt("scans_requested_retained"))
                .append("; artwork requests: ").append(manifest.getInt("artwork_requests_retained")).append("; artwork failures: ").append(manifest.getInt("artwork_failures_retained")).append('\n');
        for(String field:new String[]{"app_session_summaries","playback_session_summaries","operation_summaries"}){
            JSONArray items=manifest.getJSONArray(field);text.append(field).append(" (process + sequence references identify raw evidence):\n");
            for(int n=0;n<Math.min(200,items.length());n++){
                JSONObject item=items.getJSONObject(n);text.append(item.optString("session",item.optString("operation_id"))).append(" process=").append(item.getString("process"))
                    .append(" UTC=").append(item.getLong("first_utc_ms")).append("..").append(item.getLong("last_utc_ms"))
                    .append(" sequence=").append(item.getLong("first_sequence")).append("..").append(item.getLong("last_sequence"));
                if(item.has("duration_status"))text.append(" duration_ms=").append(item.opt("duration_ms")).append(" ").append(item.getString("duration_status"));
                text.append(" events=").append(item.getInt("event_count")).append('\n');
            }
            if(items.length()>200)text.append("Further entries are in manifest.json: ").append(items.length()-200).append('\n');
        }
        return text.toString();
    }
    private DiagnosticArchive() {}
}
