package com.archos.mediacenter.video.diagnostics;

import org.junit.Test;
import static org.junit.Assert.*;

public class DiagnosticFlightRecorderTest {
    @Test public void capturedFailureWindowSurvivesLaterRotationAndClear(){
        DiagnosticFlightRecorder recorder=new DiagnosticFlightRecorder(128,60000);
        recorder.add(1000,"before\n");recorder.add(2000,"failure\n");
        DiagnosticFlightRecorder.Capture captured=recorder.capture(2000);
        recorder.add(63000,"later\n");assertEquals("later\n",recorder.snapshot(63000));
        recorder.clear();assertEquals("before\nfailure\n",captured.text());
        assertEquals(2000,captured.elapsed);assertEquals(0,captured.evicted);
    }
    @Test public void captureStillHonoursByteAndAgeBudgets(){
        DiagnosticFlightRecorder recorder=new DiagnosticFlightRecorder(8,100);
        recorder.add(0,"1111\n");recorder.add(1,"22\n");recorder.add(2,"333\n");
        DiagnosticFlightRecorder.Capture captured=recorder.capture(2);
        assertEquals("22\n333\n",captured.text());assertEquals(1,captured.evicted);
        assertEquals("",recorder.capture(200).text());assertEquals("22\n333\n",captured.text());
    }
}
