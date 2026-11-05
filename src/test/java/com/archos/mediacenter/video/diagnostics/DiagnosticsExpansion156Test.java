package com.archos.mediacenter.video.diagnostics;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import android.app.Application;
import android.app.ApplicationExitInfo;
import android.os.SystemClock;
import java.lang.reflect.Method;
import java.util.concurrent.ThreadPoolExecutor;
import org.json.JSONObject;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;
import org.robolectric.util.ReflectionHelpers;

@RunWith(RobolectricTestRunner.class)
@Config(application = Application.class, sdk = 30)
public class DiagnosticsExpansion156Test
{
    @Test public void onlyOsCrashNativeCrashAndAnrAreClassifiedUnclean()
    {
        for (int reason : new int[] {ApplicationExitInfo.REASON_CRASH, ApplicationExitInfo.REASON_CRASH_NATIVE,
                                     ApplicationExitInfo.REASON_ANR})
            assertEquals("previous_session_unclean_exit", Diagnostics.exitEventName(reason));
        assertEquals("android_process_exit_low_memory",
                     Diagnostics.exitEventName(ApplicationExitInfo.REASON_LOW_MEMORY));
        assertEquals("android_process_exit_user_stop",
                     Diagnostics.exitEventName(ApplicationExitInfo.REASON_USER_REQUESTED));
        assertEquals("android_process_exit_user_stop",
                     Diagnostics.exitEventName(ApplicationExitInfo.REASON_USER_STOPPED));
        assertEquals("android_process_exit", Diagnostics.exitEventName(ApplicationExitInfo.REASON_SIGNALED));
    }

    @Test public void largerQueuesAndBudgetAndErrorCallSiteFallbackAreExplicit() throws Exception
    {
        assertEquals(32 * 1024 * 1024, Diagnostics.STORAGE_LIMIT);
        ThreadPoolExecutor routine = ReflectionHelpers.getStaticField(Diagnostics.class, "WORK");
        assertEquals(8192, routine.getQueue().size() + routine.getQueue().remainingCapacity());
        Method record = Diagnostics.class.getDeclaredMethod("record", String.class, Object[].class);
        record.setAccessible(true)
        ;
        JSONObject fallback =
            new JSONObject((String)record.invoke(null, "scan_failed", new Object[] {"message", "Enumeration denied"}));
        assertEquals("Enumeration denied", fallback.getString("message"));
        assertEquals("reporting_call_site", fallback.getString("trace_origin"));
        assertTrue(fallback.getString("trace").contains("Failure reported at scan_failed"));
    }

    @Test public void errorBuilderKeepsActualMessageAndRedactsItsStack() throws Exception
    {
        Application app = RuntimeEnvironment.getApplication();
        Diagnostics.setEnabled(app, true);
        Diagnostics.error("backup_failed", new java.io.IOException("Provider refused close; token=CANARY"), "stage",
                          "copy");
        DiagnosticFlightRecorder flight = ReflectionHelpers.getStaticField(Diagnostics.class, "FLIGHT");
        String text = flight.snapshot(SystemClock.elapsedRealtime());
        assertTrue(text.contains("Provider refused close"));
        assertTrue(text.contains("java.io.IOException"));
        assertFalse(text.contains("CANARY"));
        Diagnostics.setEnabled(app, false);
    }
}
