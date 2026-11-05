package com.archos.mediacenter.video.leanback;

import android.app.Application;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.SystemClock;
import androidx.preference.PreferenceManager;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleRegistry;
import androidx.lifecycle.ProcessLifecycleOwner;
import com.archos.mediacenter.video.diagnostics.Diagnostics;
import com.archos.mediaprovider.video.PreviewLocalImportTrace;
import com.archos.mediaprovider.video.ScanSchedulePolicy;
import com.archos.mediascraper.AutoScrapeService;
import java.lang.reflect.Method;
import java.util.List;
import java.util.UUID;
import org.json.JSONObject;
import org.junit.Before;
import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.Shadows;
import org.robolectric.annotation.Config;
import org.robolectric.util.ReflectionHelpers;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.anyInt;
import static org.mockito.Mockito.anyString;

/** Checks scan lifecycle telemetry uses actual progress, batch totals and terminal outcomes. */
@RunWith(RobolectricTestRunner.class)
@Config(application = Application.class, sdk = 28)
public class PreviewScanLifecycleTest
{
    private LifecycleRegistry metadataLifecycle;

    @After public void backgroundMetadataFixture()
    {
        if (metadataLifecycle != null)
            metadataLifecycle.setCurrentState(Lifecycle.State.CREATED);
    }

    private Context foregroundMetadataContext()
    {
        Application app = RuntimeEnvironment.getApplication();
        PreferenceManager.getDefaultSharedPreferences(app)
            .edit()
            .putBoolean("supernova_onboarding_complete", true)
            .commit();
        metadataLifecycle = (LifecycleRegistry)ProcessLifecycleOwner.get().getLifecycle();
        metadataLifecycle.setCurrentState(Lifecycle.State.STARTED);
        Context context = mock(Context.class);
        when(context.getApplicationContext()).thenReturn(app);
        when(context.getContentResolver()).thenReturn(app.getContentResolver());
        when(context.getPackageName()).thenReturn(app.getPackageName());
        when(context.getSharedPreferences(anyString(), anyInt()))
            .thenAnswer(call -> app.getSharedPreferences(call.getArgument(0), call.getArgument(1)));
        return context;
    }

    @Test public void libraryControlIncludesLocalProgressButNetworkControlStaysNetworkOnly()
    {
        Context context = RuntimeEnvironment.getApplication();
        PreviewLocalScanState.accept(context, new Intent()
                                                  .putExtra("batch_id", UUID.randomUUID().toString())
                                                  .putExtra("mode", "full")
                                                  .putExtra("phase", "complete")
                                                  .putExtra("checked", 23L));
        assertTrue(PreviewNetworkWorkspace.scanStatus(context, true).contains("23 media rows checked"));
        assertFalse(PreviewNetworkWorkspace.scanStatus(context, false).contains("23 media rows checked"));
    }

    @Before public void registerReceiverForThisTestApplication()
    {
        // Robolectric replaces the Application/registered receivers between tests;
        // the production process-wide installation guard otherwise retains the old registration.
        ReflectionHelpers.setStaticField(PreviewLibraryScan.class, "installed", false);
    }

    @Test public void nativeLocalTraceReportsMeasuredCountsAndPartialOutcome()
    {
        Context context = RuntimeEnvironment.getApplication();
        PreviewLibraryScan.install(context);
        PreviewLocalImportTrace.begin(context, true);
        PreviewLocalImportTrace.reconciled(3, 2);
        PreviewLocalImportTrace.checked();
        PreviewLocalImportTrace.problem();
        PreviewLocalImportTrace.finish(true, false);
        Shadows.shadowOf(Looper.getMainLooper()).idle();
        String result = PreviewLocalScanState.status(context);
        assertTrue(result.contains("partial"));
        assertTrue(result.contains("1 media rows checked"));
        assertTrue(result.contains("3 new · 2 updated"));
        PreviewLocalImportTrace.finish(true, false);
        Shadows.shadowOf(Looper.getMainLooper()).idle();
        assertEquals(result, PreviewLocalScanState.status(context));
    }

    @Test public void queuedLocalImportReportsStartAndCancellation()
    {
        Context context = RuntimeEnvironment.getApplication();
        PreviewLibraryScan.install(context);
        Handler handler = new Handler(Looper.getMainLooper());
        Message message = Message.obtain();
        message.getData().putString("preview_trigger", "resume");
        PreviewLocalImportTrace.queued(context, handler, message, true);
        Shadows.shadowOf(Looper.getMainLooper()).idle();
        assertTrue(PreviewLocalScanState.status(context).contains("queued"));
        PreviewLocalImportTrace.starting(message);
        PreviewLocalImportTrace.begin(context, true);
        PreviewLocalImportTrace.finish(true, false);
        Shadows.shadowOf(Looper.getMainLooper()).idle();
        assertTrue(PreviewLocalScanState.status(context).contains("complete"));
        Message abandoned = Message.obtain();
        PreviewLocalImportTrace.queued(context, handler, abandoned, false);
        PreviewLocalImportTrace.cancelQueued(handler);
        Shadows.shadowOf(Looper.getMainLooper()).idle();
        assertTrue(PreviewLocalScanState.status(context).contains("cancelled"));
    }

    private Context localTraceContext()
    {
        Application app = RuntimeEnvironment.getApplication();
        PreferenceManager.getDefaultSharedPreferences(app).edit().remove(ScanSchedulePolicy.LAST_COMPLETED).commit();
        Context context = mock(Context.class);
        when(context.getPackageName()).thenReturn("test");
        when(context.getSharedPreferences(anyString(), anyInt()))
            .thenAnswer(call -> app.getSharedPreferences(call.getArgument(0), call.getArgument(1)));
        return context;
    }

    private void assertSuccessfulLocalCompletionRecorded()
    {
        assertTrue(PreferenceManager.getDefaultSharedPreferences(RuntimeEnvironment.getApplication())
                       .getLong(ScanSchedulePolicy.LAST_COMPLETED, 0) > 0);
    }

    @Test public void localQueueBoundariesShareBatchAndObservedTrigger()
    {
        Context context = localTraceContext();
        Handler handler = new Handler(Looper.getMainLooper());
        Message message = Message.obtain();
        message.getData().putString("preview_trigger", "resume");
        PreviewLocalImportTrace.queued(context, handler, message, true);
        PreviewLocalImportTrace.starting(message);
        PreviewLocalImportTrace.begin(context, true);
        PreviewLocalImportTrace.finish(true, false);
        assertSuccessfulLocalCompletionRecorded();
        ArgumentCaptor<Intent> sent = ArgumentCaptor.forClass(Intent.class);
        verify(context, times(4)).sendBroadcast(sent.capture());
        List<Intent> rows = sent.getAllValues();
        String batch = rows.get(0).getStringExtra("batch_id");
        String[] phases = {"requested", "queued", "started", "complete"};
        for (int n = 0; n < 4; n++)
        {
            assertEquals(batch, rows.get(n).getStringExtra("batch_id"));
            assertEquals(phases[n], rows.get(n).getStringExtra("phase"));
            assertEquals("resume", rows.get(n).getStringExtra("trigger"));
        }
    }

    @Test public void localMetadataQueueReportsActualServiceAcceptance()
    {
        Context context = foregroundMetadataContext();
        when(context.startService(any(Intent.class))).thenReturn(null);
        assertFalse(AutoScrapeService.requestService(context));
        when(context.startService(any(Intent.class))).thenReturn(new ComponentName("test", "Metadata"));
        assertTrue(AutoScrapeService.requestService(context));
        when(context.startService(any(Intent.class))).thenThrow(new IllegalStateException("restricted"));
        assertFalse(AutoScrapeService.requestService(context));
        verify(context, times(3)).startService(any(Intent.class));
    }

    @Test public void interruptedLocalImportCannotReportCompletionOrInventCounts()
    {
        Context context = RuntimeEnvironment.getApplication();
        PreviewLibraryScan.install(context);
        PreviewLocalImportTrace.begin(context, false);
        PreviewLocalImportTrace.finish(true, true);
        Shadows.shadowOf(Looper.getMainLooper()).idle();
        String result = PreviewLocalScanState.status(context);
        assertTrue(result.contains("cancelled"));
        assertFalse(result.contains("complete"));
        assertTrue(result.contains("counts unavailable"));
    }

    @Test public void localTelemetryBroadcastFailureDoesNotBreakImport()
    {
        Context context = localTraceContext();
        doThrow(new IllegalStateException("receiver unavailable")).when(context).sendBroadcast(any(Intent.class));
        PreviewLocalImportTrace.begin(context, true);
        PreviewLocalImportTrace.checked();
        PreviewLocalImportTrace.finish(true, false);
        assertSuccessfulLocalCompletionRecorded();
    }

    @Test public void libraryProgressKeepsConcurrentPhasesAndHonestRemainingCounts()
    {
        String value = PreviewLibraryScan.formatLibraryStatus("Scanning indexed sources · 5 checked", true, 7, true, 3);
        assertTrue(value.contains("Importing local library · 7 remaining"));
        assertTrue(value.contains("Identifying library titles · 3 remaining"));
        assertTrue(value.contains("5 checked"));
        assertFalse(value.contains("7 checked"));
        assertFalse(value.contains("%"));
        assertEquals("Importing local library · progress unavailable",
                     PreviewLibraryScan.formatLibraryStatus("", true, -1, false, 0));
        assertEquals("Last result", PreviewLibraryScan.formatLibraryStatus("Last result", false, 0, false, 0));
    }

    private void event(Context context, String source, String phase, int checked)
    {
        context.sendBroadcast(new Intent(context.getPackageName() + ".SCAN_LIFECYCLE")
                                  .setPackage(context.getPackageName())
                                  .putExtra("batch_id", "42")
                                  .putExtra("source_id", source)
                                  .putExtra("phase", phase)
                                  .putExtra("sources_total", 2)
                                  .putExtra("checked", checked)
                                  .putExtra("added", checked / 2)
                                  .putExtra("updated", 0));
        Shadows.shadowOf(Looper.getMainLooper()).idle();
    }

    @Test public void queuedSourcesAndMetadataEventsPreserveMeasuredBatchTotals()
    {
        Context context = RuntimeEnvironment.getApplication();
        PreviewLibraryScan.install(context);
        String first = "aaaaaaaaaaaaaaaaaaaaaaaa", second = "bbbbbbbbbbbbbbbbbbbbbbbb";
        event(context, first, "queued", 0);
        event(context, second, "queued", 0);
        event(context, first, "started", 0);
        event(context, first, "complete", 10);
        event(context, second, "started", 0);
        event(context, second, "complete", 4);
        event(context, second, "metadata_queued", 4);
        event(context, second, "batch_complete", 4);
        assertEquals(2, (int)ReflectionHelpers.getStaticField(PreviewLibraryScan.class, "sourcesTotal"));
        assertEquals(2L, (long)ReflectionHelpers.getStaticField(PreviewLibraryScan.class, "completed"));
        assertEquals(14L, (long)ReflectionHelpers.getStaticField(PreviewLibraryScan.class, "checked"));
        assertEquals("metadata_queued", ReflectionHelpers.getStaticField(PreviewLibraryScan.class, "metadataOutcome"));
        assertTrue(PreferenceManager.getDefaultSharedPreferences(context)
                       .getString("preview_scan_result", "")
                       .contains("14 checked"));
    }

    @Test public void batchTerminalClosesOperationOnceNotAtIndividualSourceCompletion() throws Exception
    {
        Context context = RuntimeEnvironment.getApplication();
        PreviewLibraryScan.install(context);
        Diagnostics.setEnabled(context, true);
        try
        {
            String source = "aaaaaaaaaaaaaaaaaaaaaaaa";
            event(context, source, "queued", 0);
            String operation = ReflectionHelpers.getStaticField(PreviewLibraryScan.class, "operation");
            event(context, source, "complete", 10);
            assertFalse((boolean)ReflectionHelpers.getStaticField(PreviewLibraryScan.class, "operationEnded"));
            event(context, source, "batch_complete", 10);
            event(context, source, "batch_complete", 10);
            assertTrue((boolean)ReflectionHelpers.getStaticField(PreviewLibraryScan.class, "operationEnded"));
            Object recorder = ReflectionHelpers.getStaticField(Diagnostics.class, "FLIGHT");
            Method snapshot = recorder.getClass().getDeclaredMethod("snapshot", long.class);
            snapshot.setAccessible(true);
            int ends = 0;
            for (String line : ((String)snapshot.invoke(recorder, SystemClock.elapsedRealtime())).split("\n"))
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
            Diagnostics.setEnabled(context, false);
        }
    }

    @Test public void metadataQueueDistinguishesAcceptedNullAndRestrictedStarts()
    {
        Context context = foregroundMetadataContext();
        when(context.startService(any(Intent.class))).thenReturn(new ComponentName("test", "Metadata"));
        assertTrue(enqueue(context));
        when(context.startService(any(Intent.class))).thenReturn(null);
        assertFalse(enqueue(context));
        when(context.startService(any(Intent.class))).thenThrow(new IllegalStateException("background restriction"));
        assertFalse(enqueue(context));
        ArgumentCaptor<Intent> starts = ArgumentCaptor.forClass(Intent.class);
        verify(context, times(3)).startService(starts.capture());
        for (Intent intent : starts.getAllValues())
            assertTrue(intent.getBooleanExtra("FORCE_AFTER_NETWORK_SCAN", false));
    }

    private boolean enqueue(Context context)
    {
        return ReflectionHelpers.callStaticMethod(AutoScrapeService.class, "requestServiceAfterNetworkScan",
                                                  ReflectionHelpers.ClassParameter.from(Context.class, context));
    }
}
