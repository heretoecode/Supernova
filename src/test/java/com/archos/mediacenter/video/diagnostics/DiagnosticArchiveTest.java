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
    @Test public void failuresAndManualReportsAreImportantButRoutineFocusIsNot(){
        assertTrue(Diagnostics.important("manual_problem_marker"));assertTrue(Diagnostics.important("artwork_failed"));
        assertTrue(Diagnostics.important("scan_partial"));assertFalse(Diagnostics.important("focus_navigation"));assertFalse(Diagnostics.important("artwork_ready"));
    }
}
