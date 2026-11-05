package com.archos.mediacenter.video.diagnostics;

import android.app.Application;
import android.os.SystemClock;
import android.widget.TextView;
import java.util.HashMap;
import java.util.Map;
import org.json.JSONObject;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;
import org.robolectric.util.ReflectionHelpers;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/** Checks diagnostic trace correlation, nested scopes and focus-return identity without exposing control text. */
@RunWith(RobolectricTestRunner.class) @Config(application = Application.class, sdk = 28) public class ArtworkTraceTest
{
    @Test public void workerOperationScopeLinksChildrenAndRestoresAfterNestedFailure() throws Exception
    {
        Application app = RuntimeEnvironment.getApplication();
        Diagnostics.setEnabled(app, true);
        try
        {
            String child, nested = "", restored, outside;
            try (Diagnostics.OperationScope parent = Diagnostics.operationScope("fixture-parent"))
            {
                child = Diagnostics.operation("fixture_child");
                try (Diagnostics.OperationScope inner = Diagnostics.operationScope(child))
                {
                    nested = Diagnostics.operation("fixture_nested");
                    throw new IllegalStateException("fixture");
                }
                catch (IllegalStateException expected)
                {
                }
                restored = Diagnostics.operation("fixture_restored");
            }
            outside = Diagnostics.operation("fixture_outside");
            DiagnosticFlightRecorder recorder = ReflectionHelpers.getStaticField(Diagnostics.class, "FLIGHT");
            Map<String, String> parents = new HashMap<>();
            for (String line : recorder.snapshot(SystemClock.elapsedRealtime()).split("\n"))
            {
                if (line.isEmpty())
                    continue;
                JSONObject row = new JSONObject(line);
                if ("operation_begin".equals(row.optString("event")))
                    parents.put(row.optString("operation_id"), row.optString("parent_operation_id"));
            }
            assertEquals("fixture-parent", parents.get(child));
            assertEquals(child, parents.get(nested));
            assertEquals("fixture-parent", parents.get(restored));
            assertEquals("", parents.get(outside));
        }
        finally
        {
            Diagnostics.setEnabled(app, false);
        }
    }
    @Test public void sharedTraceCorrelatesOnceAndExpectedFallbackIsNotAnIncident() throws Exception
    {
        Application app = RuntimeEnvironment.getApplication();
        Diagnostics.setEnabled(app, true);
        try
        {
            ArtworkTrace trace = new ArtworkTrace(987654, "fixture_logo", "title_logo", "cache_only");
            trace.fallback("not_cached");
            trace.ready("memory");
            trace.cancelled();
            DiagnosticFlightRecorder recorder = ReflectionHelpers.getStaticField(Diagnostics.class, "FLIGHT");
            int requests = 0, fallbacks = 0, other = 0;
            String operation = null;
            for (String line : recorder.snapshot(SystemClock.elapsedRealtime()).split("\n"))
            {
                if (line.isEmpty())
                    continue;
                JSONObject row = new JSONObject(line);
                if (row.optLong("media_id") != 987654)
                    continue;
                if (operation == null)
                    operation = row.getString("operation_id");
                assertEquals(operation, row.getString("operation_id"));
                switch (row.getString("event"))
                {
                case "artwork_request":
                    requests++;
                    break;
                case "artwork_fallback":
                    fallbacks++;
                    assertTrue(row.getBoolean("fallback_succeeded"));
                    break;
                default:
                    other++;
                }
            }
            assertEquals(1, requests);
            assertEquals(1, fallbacks);
            assertEquals(0, other);
            assertFalse(Diagnostics.important("artwork_fallback"));
            int ends = 0;
            for (String line : recorder.snapshot(SystemClock.elapsedRealtime()).split("\n"))
            {
                if (line.isEmpty())
                    continue;
                JSONObject row = new JSONObject(line);
                if (operation.equals(row.optString("operation_id")) && "operation_end".equals(row.optString("event")))
                    ends++;
            }
            assertEquals(1, ends);
        }
        finally
        {
            Diagnostics.setEnabled(app, false);
        }
    }
    @Test public void focusReturnReusesEntryIdentityWithoutControlText() throws Exception
    {
        Application app = RuntimeEnvironment.getApplication();
        Diagnostics.setEnabled(app, true);
        try
        {
            TextView opener = new TextView(app);
            opener.setText("private-media-name");
            Diagnostics.semantic(opener, "dialog.choice.7");
            String token = Diagnostics.focusEntry(opener, "fixture_dialog");
            Diagnostics.focusRestored(token, opener, opener, false, true);
            DiagnosticFlightRecorder recorder = ReflectionHelpers.getStaticField(Diagnostics.class, "FLIGHT");
            int entries = 0, returns = 0;
            for (String line : recorder.snapshot(SystemClock.elapsedRealtime()).split("\n"))
            {
                if (line.isEmpty())
                    continue;
                JSONObject row = new JSONObject(line);
                if (!token.equals(row.optString("operation_id")))
                    continue;
                assertFalse(line.contains("private-media-name"));
                if (row.optString("event").equals("focus_entry"))
                    entries++;
                if (row.optString("event").equals("focus_restoration"))
                {
                    returns++;
                    assertEquals("semantic:dialog.choice.7", row.getString("restored"));
                }
            }
            assertEquals(1, entries);
            assertEquals(1, returns);
        }
        finally
        {
            Diagnostics.setEnabled(app, false);
        }
    }
}
