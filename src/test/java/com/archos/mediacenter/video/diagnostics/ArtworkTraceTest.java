package com.archos.mediacenter.video.diagnostics;

import android.app.Application;
import org.json.JSONObject;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.*;
import org.robolectric.annotation.Config;
import org.robolectric.util.ReflectionHelpers;
import static org.junit.Assert.*;

@RunWith(RobolectricTestRunner.class) @Config(application=Application.class,sdk=28)
public class ArtworkTraceTest {
    @Test public void sharedTraceCorrelatesOnceAndExpectedFallbackIsNotAnIncident()throws Exception{
        Application app=RuntimeEnvironment.getApplication();Diagnostics.setEnabled(app,true);
        try{
            ArtworkTrace trace=new ArtworkTrace(987654,"fixture_logo","title_logo","cache_only");
            trace.fallback("not_cached");trace.ready("memory");trace.cancelled();
            DiagnosticFlightRecorder recorder=ReflectionHelpers.getStaticField(Diagnostics.class,"FLIGHT");
            int requests=0,fallbacks=0,other=0;String operation=null;
            for(String line:recorder.snapshot(android.os.SystemClock.elapsedRealtime()).split("\n")){
                if(line.isEmpty())continue;JSONObject row=new JSONObject(line);if(row.optLong("media_id")!=987654)continue;
                if(operation==null)operation=row.getString("operation_id");assertEquals(operation,row.getString("operation_id"));
                switch(row.getString("event")){case "artwork_request":requests++;break;case "artwork_fallback":fallbacks++;assertTrue(row.getBoolean("fallback_succeeded"));break;default:other++;}
            }
            assertEquals(1,requests);assertEquals(1,fallbacks);assertEquals(0,other);assertFalse(Diagnostics.important("artwork_fallback"));
            int ends=0;
            for(String line:recorder.snapshot(android.os.SystemClock.elapsedRealtime()).split("\n")){
                if(line.isEmpty())continue;JSONObject row=new JSONObject(line);
                if(operation.equals(row.optString("operation_id"))&&"operation_end".equals(row.optString("event")))ends++;
            }
            assertEquals(1,ends);
        }finally{Diagnostics.setEnabled(app,false);}
    }
    @Test public void focusReturnReusesEntryIdentityWithoutControlText()throws Exception{
        Application app=RuntimeEnvironment.getApplication();Diagnostics.setEnabled(app,true);
        try{
            android.widget.TextView opener=new android.widget.TextView(app);opener.setText("private-media-name");Diagnostics.semantic(opener,"dialog.choice.7");
            String token=Diagnostics.focusEntry(opener,"fixture_dialog");Diagnostics.focusRestored(token,opener,opener,false,true);
            DiagnosticFlightRecorder recorder=ReflectionHelpers.getStaticField(Diagnostics.class,"FLIGHT");int entries=0,returns=0;
            for(String line:recorder.snapshot(android.os.SystemClock.elapsedRealtime()).split("\n")){
                if(line.isEmpty())continue;JSONObject row=new JSONObject(line);if(!token.equals(row.optString("operation_id")))continue;
                assertFalse(line.contains("private-media-name"));if(row.optString("event").equals("focus_entry"))entries++;if(row.optString("event").equals("focus_restoration")){returns++;assertEquals("semantic:dialog.choice.7",row.getString("restored"));}
            }
            assertEquals(1,entries);assertEquals(1,returns);
        }finally{Diagnostics.setEnabled(app,false);}
    }
}
