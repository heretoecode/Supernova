package com.archos.mediacenter.video.diagnostics;

import java.io.*;
import java.nio.charset.StandardCharsets;
import org.junit.*;
import org.junit.rules.TemporaryFolder;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;
import static org.junit.Assert.*;

@RunWith(RobolectricTestRunner.class)
@Config(application=android.app.Application.class,sdk=28)
public class DiagnosticArchiveTest {
    @Test public void summaryUsesPerProcessDropMaximaAndRetainedEvidenceSpans()throws Exception{
        write("events.jsonl","{\"process\":\"a\",\"sequence\":1,\"event\":\"startup\",\"utc_ms\":1000,\"dropped\":7}\n"
                +"{\"process\":\"a\",\"sequence\":2,\"event\":\"heartbeat\",\"utc_ms\":4000,\"dropped\":9}\n"
                +"{\"process\":\"b\",\"sequence\":1,\"event\":\"PREVIOUS_SESSION_UNCLEAN_EXIT\",\"utc_ms\":5000,\"dropped\":2}\n");
        org.json.JSONObject summary=DiagnosticArchive.manifest(temporary.getRoot());
        assertEquals(11,summary.getLong("dropped_events_retained_process_maxima"));
        assertEquals(1,summary.getInt("launches_retained"));
        assertEquals(1,summary.getInt("suspected_unclean_exits_retained"));
        assertEquals(3000,summary.getJSONArray("process_spans").getJSONObject(0).getLong("retained_span_ms"));
        assertEquals(2,summary.getJSONArray("significant_evidence").length());
        assertEquals(1,summary.getJSONObject("event_counts").getInt("heartbeat"));
    }
    @Test public void severityDoesNotTurnAnUncleanExitIntoAConfirmedCrash(){
        assertEquals("WARNING",Diagnostics.severity("PREVIOUS_SESSION_UNCLEAN_EXIT"));
        assertEquals("FATAL",Diagnostics.severity("uncaught_exception"));
        assertEquals("ERROR",Diagnostics.severity("artwork_failed"));
        assertEquals("INFO",Diagnostics.severity("focus_navigation"));
    }
    @Rule public TemporaryFolder temporary=new TemporaryFolder();
    private void write(String name,String data)throws IOException{
        try(FileOutputStream output=new FileOutputStream(new File(temporary.getRoot(),name))){output.write(data.getBytes(StandardCharsets.UTF_8));}
    }
    @Test public void sessionsInPlaybackSurviveEventRotationAndCopiesAreNotDoubleCounted()throws Exception{
        String record="{\"process\":\"a\",\"sequence\":1,\"session\":\"play1\",\"utc_ms\":1000,\"event\":\"playback_begin\"}\n";
        write("playback.jsonl.1",record);write("flight.jsonl",record);
        write("events.jsonl","{\"process\":\"a\",\"sequence\":2,\"event\":\"focus\",\"utc_ms\":2000}\n");
        assertEquals(2,DiagnosticArchive.records(temporary.getRoot()).size());
        assertEquals(1,DiagnosticArchive.manifest(temporary.getRoot()).getInt("playback_sessions"));
    }
    @Test public void manualReportsAreIndexedAndUnrelatedFilesExcluded()throws Exception{
        String marker="{\"event\":\"manual_problem_marker\",\"marker_id\":\"safe-reference\",\"utc_ms\":1000}\n";
        write("important-20260926.jsonl",marker);write("credentials.json",marker);
        assertEquals(1,DiagnosticArchive.files(temporary.getRoot()).size());
        assertEquals("safe-reference",DiagnosticArchive.manifest(temporary.getRoot()).getJSONArray("manual_reports").getJSONObject(0).getString("marker_id"));
    }
    @Test public void protectedIncidentStreamsAreIncludedAndDeduplicated()throws Exception{
        String event="{\"process\":\"a\",\"sequence\":1,\"event\":\"incident_capture\",\"utc_ms\":1000}\n";
        write("incident-manual-20260926.jsonl.3",event);write("incident-auto-20260926.jsonl",event);
        write("incident-manual-private.jsonl",event);
        assertEquals(2,DiagnosticArchive.files(temporary.getRoot()).size());
        assertEquals(1,DiagnosticArchive.manifest(temporary.getRoot()).getJSONArray("incidents").length());
    }
    @Test public void failuresAndManualReportsAreImportantButRoutineFocusIsNot(){
        assertTrue(Diagnostics.important("manual_problem_marker"));assertTrue(Diagnostics.important("artwork_failed"));
        assertTrue(Diagnostics.important("scan_partial"));assertFalse(Diagnostics.important("focus_navigation"));assertFalse(Diagnostics.important("artwork_ready"));
    }
}
