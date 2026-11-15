package com.archos.mediacenter.video.diagnostics;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.Test;

// org.json needs the Android runtime (plain JUnit gets "Method ... not mocked").
@org.junit.runner.RunWith(org.robolectric.RobolectricTestRunner.class)
@org.robolectric.annotation.Config(application = android.app.Application.class, sdk = 28)
public class DiagnosticDetailTest
{
    private Map<String, Object> fields(Object[] fields)
    {
        Map<String, Object> result = new LinkedHashMap<>();
        for (int i = 0; i < fields.length; i += 2)
            result.put((String)fields[i], fields[i + 1]);
        return result;
    }

    @Test public void preservesUsefulExceptionMessagesCausesSuppressedAndAllFramesButRedactsCredentials()
    {
        IllegalStateException failure = new IllegalStateException(
            "Expected rows 61050, got 0; token=CANARY_ONE",
            new IllegalArgumentException(
                "HTTP body empty https://CANARY_USER:CANARY_PASS@example/secret?api_key=CANARY_KEY"));
        failure.addSuppressed(new RuntimeException("OMDb apiKey: 'CANARY_TWO'"));
        StackTraceElement[] frames = new StackTraceElement[80];
        for (int i = 0; i < frames.length; i++)
            frames[i] = new StackTraceElement("Worker", "method" + i, "Worker.java", i);
        failure.setStackTrace(frames);
        String trace = DiagnosticRedactor.trace(failure);
        assertTrue(trace.contains("Expected rows 61050, got 0"));
        assertTrue(trace.contains("method79"));
        assertTrue(trace.contains("Suppressed:"));
        assertTrue(trace.contains("Caused by:"));
        assertFalse(trace.contains("CANARY"));
        assertFalse(trace.contains("example"));
    }

    @Test public void redactsJsonHeadersInlineKeysUrlsAndSecretSettingsWithoutHidingSafeValues() throws Exception
    {
        String value = DiagnosticRedactor.clean(
            "{\"Authorization\":\"Bearer CANARY_A with spaces\", \"opensubtitles_api_key\": "
            + "\"CANARY_B\"}\nAuthorization: Basic CANARY_C\nusername=CANARY_D "
            + "password=CANARY_E\nhttps://CANARY_F:CANARY_G@host/file?token=CANARY_H\n/storage/CANARY_I");
        assertFalse(value, value.contains("CANARY"));
        String escaped = new JSONObject().put("password", "quote\"CANARY_ESCAPED and spaces").toString();
        assertFalse(DiagnosticRedactor.clean(escaped), DiagnosticRedactor.clean(escaped).contains("CANARY"));
        assertEquals("[redacted]", DiagnosticRedactor.settingValue("omdb_api_key", "CANARY"));
        assertEquals("[redacted]", DiagnosticRedactor.settingValue("opensubtitles_password", "CANARY"));
        assertEquals(true, DiagnosticRedactor.settingValue("seek_thumbnails_enabled", true));
        assertEquals(15, DiagnosticRedactor.settingValue("subtitle_vertical_position_percent", 15));
    }

    @Test public void frameSummaryCountsOnlyDeltasOver32msAndResetsWindow()
    {
        List<Map<String, Object>> events = new ArrayList<>();
        DiagnosticFrameMetrics metrics =
            new DiagnosticFrameMetrics("Details", 1, (name, values) -> events.add(fields(values)));
        metrics.frame(1);
        metrics.frame(32_000_001);
        metrics.frame(65_000_001);
        metrics.flush(70_000_001, "pause");
        assertEquals(2L, events.get(0).get("frames"));
        assertEquals(1L, events.get(0).get("slow_frames"));
        assertEquals("Details", events.get(0).get("screen"));
        metrics.flush(80_000_001, "pause");
        assertEquals(0L, events.get(1).get("slow_frames"));
    }

    @Test public void transportBuilderKeepsMeasuredWindowAndUnknownOpenRetryAndHttpFactsExplicit()
    {
        List<Map<String, Object>> events = new ArrayList<>();
        DiagnosticTransportMetrics metrics =
            new DiagnosticTransportMetrics("stream", "session", "SMB", (name, values) -> events.add(fields(values)));
        metrics.boundary("TIMEOUT", 2000, 3, 500_000_000, 2_000_000_000L);
        Map<String, Object> result = events.get(0);
        assertEquals(1000.0, result.get("throughput_bytes_per_second"));
        assertEquals(true, result.get("timeout"));
        assertEquals(false, result.get("open_latency_available"));
        assertEquals(false, result.get("http_status_available"));
        assertEquals(false, result.get("retry_count_available"));
        assertEquals("session", result.get("playback_session"));
    }

    @Test public void backupBuildersKeepLastStageCountsAndActualExceptionChain() throws Exception
    {
        Map<String, Map<String, Object>> events = new LinkedHashMap<>();
        DiagnosticBackupState state =
            new DiagnosticBackupState("op", "export", (name, values) -> events.put(name, fields(values)));
        JSONObject payload = new JSONObject().put("rows", new JSONArray().put(1).put(2));
        assertEquals(2, DiagnosticBackupState.rows(payload));
        state.stage("verify", "MATCHES", 61050, 6_000_000);
        state.failure(new java.io.IOException("Destination not verified",
                                              new java.io.IOException("Readback truncated; password=CANARY")));
        Map<String, Object> failure = events.get("backup_failed");
        assertEquals("verify", failure.get("stage"));
        assertTrue(((String)failure.get("message")).contains("Readback truncated"));
        assertEquals(61050L, failure.get("rows"));
        assertEquals(6_000_000L, failure.get("bytes"));
        assertTrue(((String)failure.get("trace")).contains("Readback truncated"));
        assertFalse(((String)failure.get("trace")).contains("CANARY"));
        state.complete();
        for (Map<String, Object> event : events.values())
            assertEquals("op", event.get("operation_id"));
    }

    @Test public void summaryIncludesEveryAreaTopErrorsAndSeparateSessionTimelines() throws Exception
    {
        List<JSONObject> records = new ArrayList<>();
        for (String event : new String[] {"thumb_frame", "player_state", "transport_read", "scan_partial",
                                          "metadata_semantic_failed", "backup_failed", "ui_frame_summary",
                                          "configuration_changed", "android_process_exit_low_memory"})
            records.add(new JSONObject().put("event", event).put("severity", "INFO"));
        for (int i = 0; i < 3; i++)
            records.add(new JSONObject()
                            .put("event", "backup_failed")
                            .put("severity", "ERROR")
                            .put("failure_class", "IOException")
                            .put("utc_ms", 100 + i)
                            .put("session", "one")
                            .put("message", "Readback truncated password=CANARY"));
        records.add(new JSONObject()
                        .put("event", "scan_partial")
                        .put("severity", "WARNING")
                        .put("session", "two")
                        .put("utc_ms", 200));
        records.add(new JSONObject()
                        .put("event", "thumb_session_summary")
                        .put("mean_extract_ms", 17)
                        .put("cache_hit_rate", 0.5));
        records.add(new JSONObject()
                        .put("event", "stream_reconnect")
                        .put("idle_ms", 61000)
                        .put("recovery_ms", 120)
                        .put("result", "ready"));
        records.add(new JSONObject().put("event", "transport_operation").put("open_ns", 123)
                        .put("window_bytes", 456).put("explicit_retries", 1));
        records.add(new JSONObject().put("event", "playback_frame_metrics").put("native_available", false)
                        .put("late_dropped_frames", -1).put("measurement_quality", "NATIVE_OUTPUT_SUBMISSION"));
        String result = DiagnosticExpandedSummary.build(records);
        assertTrue(result.contains("open_ns=123"));
        assertTrue(result.contains("window_bytes=456"));
        assertTrue(result.contains("explicit_retries=1"));
        assertTrue(result.contains("late_dropped_frames=-1"));
        for (String area : new String[] {"Thumbnails", "Playback", "Network", "Scans", "Metadata", "Backup and restore",
                                         "UI", "Settings", "Process exits"})
            assertTrue(result.contains(area));
        assertTrue(result.contains("3 | 100 | 102 | backup_failed / IOException"));
        assertTrue(result.contains("mean_extract_ms=17"));
        assertTrue(result.contains("cache_hit_rate=0.5"));
        assertTrue(result.contains("recovery_ms=120"));
        assertTrue(result.contains("stream_reconnect: 1"));
        assertTrue(result.contains("Session one"));
        assertTrue(result.contains("Session two"));
        assertFalse(result.contains("CANARY"));
    }
    @Test public void codecCandidateBuilderNamesCandidateWithoutClaimingSuccessfulInitialization()
    {
        Map<String, Object> candidate = fields(
            DiagnosticCodecEvent.candidate("c2.vendor.hevc", "video/hevc", 2, 3840, 2160, Double.NaN, 1, 0, false));
        assertEquals("c2.vendor.hevc", candidate.get("decoder_name"));
        assertEquals(1, candidate.get("candidate_index"));
        assertEquals(false, candidate.get("initialization_confirmed"));
        assertEquals(0.0, ((Number)candidate.get("frame_rate")).doubleValue(), 0);
        assertEquals(false, candidate.get("software"));
    }
    @Test public void httpBuilderFlagsTinyMeasuredBodiesAndKeepsUnreadBodyUnknown()
    {
        Map<String, Object> small =
            fields(DiagnosticHttpEvent.result("chaptersdb", "show", 200, 24, 40, "network", "success"));
        assertEquals(true, small.get("suspicious_small_body"));
        assertEquals(true, small.get("body_bytes_known"));
        Map<String, Object> cached =
            fields(DiagnosticHttpEvent.result("chaptersdb", "show", 404, -1, 0, "fresh", "cache_hit"));
        assertEquals(false, cached.get("suspicious_small_body"));
        assertEquals(false, cached.get("body_bytes_known"));
        assertEquals("fresh", cached.get("cache"));
    }
}
