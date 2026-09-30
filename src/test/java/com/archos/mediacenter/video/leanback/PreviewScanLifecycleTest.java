package com.archos.mediacenter.video.leanback;

import android.app.Application;
import android.content.*;
import android.os.Looper;
import com.archos.mediascraper.AutoScrapeService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.*;
import org.robolectric.annotation.Config;
import org.robolectric.util.ReflectionHelpers;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith(RobolectricTestRunner.class) @Config(application=Application.class,sdk=28)
public class PreviewScanLifecycleTest {
    @org.junit.Before public void registerReceiverForThisTestApplication(){
        // Robolectric replaces the Application/registered receivers between tests;
        // the production process-wide installation guard otherwise retains the old registration.
        ReflectionHelpers.setStaticField(PreviewLibraryScan.class,"installed",false);
    }
    @Test public void nativeLocalTraceReportsMeasuredCountsAndPartialOutcome(){
        Context context=RuntimeEnvironment.getApplication();PreviewLibraryScan.install(context);
        com.archos.mediaprovider.video.PreviewLocalImportTrace.begin(context,true);
        com.archos.mediaprovider.video.PreviewLocalImportTrace.reconciled(3,2);
        com.archos.mediaprovider.video.PreviewLocalImportTrace.checked();
        com.archos.mediaprovider.video.PreviewLocalImportTrace.problem();
        com.archos.mediaprovider.video.PreviewLocalImportTrace.finish(true,false);
        Shadows.shadowOf(Looper.getMainLooper()).idle();
        String result=PreviewLocalScanState.status(context);
        assertTrue(result.contains("partial"));assertTrue(result.contains("1 media rows checked"));
        assertTrue(result.contains("3 new · 2 updated"));
        com.archos.mediaprovider.video.PreviewLocalImportTrace.finish(true,false);
        Shadows.shadowOf(Looper.getMainLooper()).idle();assertEquals(result,PreviewLocalScanState.status(context));
    }
    @Test public void queuedLocalImportReportsStartAndCancellation(){
        Context context=RuntimeEnvironment.getApplication();PreviewLibraryScan.install(context);
        android.os.Handler handler=new android.os.Handler(Looper.getMainLooper());android.os.Message message=android.os.Message.obtain();
        message.getData().putString("preview_trigger","resume");
        com.archos.mediaprovider.video.PreviewLocalImportTrace.queued(context,handler,message,true);
        Shadows.shadowOf(Looper.getMainLooper()).idle();assertTrue(PreviewLocalScanState.status(context).contains("queued"));
        com.archos.mediaprovider.video.PreviewLocalImportTrace.starting(message);
        com.archos.mediaprovider.video.PreviewLocalImportTrace.begin(context,true);
        com.archos.mediaprovider.video.PreviewLocalImportTrace.finish(true,false);
        Shadows.shadowOf(Looper.getMainLooper()).idle();assertTrue(PreviewLocalScanState.status(context).contains("complete"));
        android.os.Message abandoned=android.os.Message.obtain();
        com.archos.mediaprovider.video.PreviewLocalImportTrace.queued(context,handler,abandoned,false);
        com.archos.mediaprovider.video.PreviewLocalImportTrace.cancelQueued(handler);
        Shadows.shadowOf(Looper.getMainLooper()).idle();assertTrue(PreviewLocalScanState.status(context).contains("cancelled"));
    }
    @Test public void localQueueBoundariesShareBatchAndObservedTrigger(){
        Context context=mock(Context.class);when(context.getPackageName()).thenReturn("test");
        android.os.Handler handler=new android.os.Handler(Looper.getMainLooper());android.os.Message message=android.os.Message.obtain();message.getData().putString("preview_trigger","resume");
        com.archos.mediaprovider.video.PreviewLocalImportTrace.queued(context,handler,message,true);
        com.archos.mediaprovider.video.PreviewLocalImportTrace.starting(message);
        com.archos.mediaprovider.video.PreviewLocalImportTrace.begin(context,true);
        com.archos.mediaprovider.video.PreviewLocalImportTrace.finish(true,false);
        org.mockito.ArgumentCaptor<Intent> sent=org.mockito.ArgumentCaptor.forClass(Intent.class);verify(context,times(4)).sendBroadcast(sent.capture());
        java.util.List<Intent> rows=sent.getAllValues();String batch=rows.get(0).getStringExtra("batch_id");
        String[] phases={"requested","queued","started","complete"};
        for(int n=0;n<4;n++){assertEquals(batch,rows.get(n).getStringExtra("batch_id"));assertEquals(phases[n],rows.get(n).getStringExtra("phase"));assertEquals("resume",rows.get(n).getStringExtra("trigger"));}
    }
    @Test public void localMetadataQueueReportsActualServiceAcceptance(){
        Context context=mock(Context.class);when(context.getApplicationContext()).thenReturn(RuntimeEnvironment.getApplication());
        when(context.startService(any(Intent.class))).thenReturn(null);
        assertFalse(AutoScrapeService.requestService(context));
        when(context.startService(any(Intent.class))).thenReturn(new ComponentName("test","Metadata"));
        assertTrue(AutoScrapeService.requestService(context));
        when(context.startService(any(Intent.class))).thenThrow(new IllegalStateException("restricted"));
        assertFalse(AutoScrapeService.requestService(context));
    }
    @Test public void interruptedLocalImportCannotReportCompletionOrInventCounts(){
        Context context=RuntimeEnvironment.getApplication();PreviewLibraryScan.install(context);
        com.archos.mediaprovider.video.PreviewLocalImportTrace.begin(context,false);
        com.archos.mediaprovider.video.PreviewLocalImportTrace.finish(true,true);
        Shadows.shadowOf(Looper.getMainLooper()).idle();
        String result=PreviewLocalScanState.status(context);
        assertTrue(result.contains("cancelled"));assertFalse(result.contains("complete"));
        assertTrue(result.contains("counts unavailable"));
    }
    @Test public void localTelemetryBroadcastFailureDoesNotBreakImport(){
        Context context=mock(Context.class);when(context.getPackageName()).thenReturn("test");
        doThrow(new IllegalStateException("receiver unavailable")).when(context).sendBroadcast(any(Intent.class));
        com.archos.mediaprovider.video.PreviewLocalImportTrace.begin(context,true);
        com.archos.mediaprovider.video.PreviewLocalImportTrace.checked();
        com.archos.mediaprovider.video.PreviewLocalImportTrace.finish(true,false);
    }
    @Test public void libraryProgressKeepsConcurrentPhasesAndHonestRemainingCounts(){
        String value=PreviewLibraryScan.formatLibraryStatus("Scanning indexed sources · 5 checked",true,7,true,3);
        assertTrue(value.contains("Importing local library · 7 remaining"));
        assertTrue(value.contains("Identifying library titles · 3 remaining"));
        assertTrue(value.contains("5 checked"));
        assertFalse(value.contains("7 checked"));
        assertFalse(value.contains("%"));
        assertEquals("Importing local library · progress unavailable",PreviewLibraryScan.formatLibraryStatus("",true,-1,false,0));
        assertEquals("Last result",PreviewLibraryScan.formatLibraryStatus("Last result",false,0,false,0));
    }
    private void event(Context context,String source,String phase,int checked){
        context.sendBroadcast(new Intent(context.getPackageName()+".SCAN_LIFECYCLE").setPackage(context.getPackageName())
            .putExtra("batch_id","42").putExtra("source_id",source).putExtra("phase",phase).putExtra("sources_total",2)
            .putExtra("checked",checked).putExtra("added",checked/2).putExtra("updated",0));
        Shadows.shadowOf(Looper.getMainLooper()).idle();
    }
    @Test public void queuedSourcesAndMetadataEventsPreserveMeasuredBatchTotals(){
        Context context=RuntimeEnvironment.getApplication();PreviewLibraryScan.install(context);
        String first="aaaaaaaaaaaaaaaaaaaaaaaa",second="bbbbbbbbbbbbbbbbbbbbbbbb";
        event(context,first,"queued",0);event(context,second,"queued",0);
        event(context,first,"started",0);event(context,first,"complete",10);
        event(context,second,"started",0);event(context,second,"complete",4);
        event(context,second,"metadata_queued",4);event(context,second,"batch_complete",4);
        assertEquals(2,(int)ReflectionHelpers.getStaticField(PreviewLibraryScan.class,"sourcesTotal"));
        assertEquals(2L,(long)ReflectionHelpers.getStaticField(PreviewLibraryScan.class,"completed"));
        assertEquals(14L,(long)ReflectionHelpers.getStaticField(PreviewLibraryScan.class,"checked"));
        assertEquals("metadata_queued",ReflectionHelpers.getStaticField(PreviewLibraryScan.class,"metadataOutcome"));
        assertTrue(androidx.preference.PreferenceManager.getDefaultSharedPreferences(context).getString("preview_scan_result","").contains("14 checked"));
    }
    @Test public void batchTerminalClosesOperationOnceNotAtIndividualSourceCompletion()throws Exception{
        Context context=RuntimeEnvironment.getApplication();PreviewLibraryScan.install(context);
        com.archos.mediacenter.video.diagnostics.Diagnostics.setEnabled(context,true);
        try{
            String source="aaaaaaaaaaaaaaaaaaaaaaaa";event(context,source,"queued",0);
            String operation=ReflectionHelpers.getStaticField(PreviewLibraryScan.class,"operation");
            event(context,source,"complete",10);assertFalse((boolean)ReflectionHelpers.getStaticField(PreviewLibraryScan.class,"operationEnded"));
            event(context,source,"batch_complete",10);event(context,source,"batch_complete",10);
            assertTrue((boolean)ReflectionHelpers.getStaticField(PreviewLibraryScan.class,"operationEnded"));
            Object recorder=ReflectionHelpers.getStaticField(com.archos.mediacenter.video.diagnostics.Diagnostics.class,"FLIGHT");
            java.lang.reflect.Method snapshot=recorder.getClass().getDeclaredMethod("snapshot",long.class);snapshot.setAccessible(true);
            int ends=0;for(String line:((String)snapshot.invoke(recorder,android.os.SystemClock.elapsedRealtime())).split("\n")){
                if(line.isEmpty())continue;org.json.JSONObject row=new org.json.JSONObject(line);
                if(operation.equals(row.optString("operation_id"))&&"operation_end".equals(row.optString("event")))ends++;
            }assertEquals(1,ends);
        }finally{com.archos.mediacenter.video.diagnostics.Diagnostics.setEnabled(context,false);}
    }
    @Test public void metadataQueueDistinguishesAcceptedNullAndRestrictedStarts(){
        Context context=mock(Context.class);when(context.getApplicationContext()).thenReturn(RuntimeEnvironment.getApplication());
        when(context.getPackageName()).thenReturn(RuntimeEnvironment.getApplication().getPackageName());
        when(context.startService(any(Intent.class))).thenReturn(new ComponentName("test","Metadata"));
        assertTrue(enqueue(context));
        when(context.startService(any(Intent.class))).thenReturn(null);assertFalse(enqueue(context));
        when(context.startService(any(Intent.class))).thenThrow(new IllegalStateException("background restriction"));assertFalse(enqueue(context));
    }
    private boolean enqueue(Context context){
        return ReflectionHelpers.callStaticMethod(AutoScrapeService.class,"requestServiceAfterNetworkScan",ReflectionHelpers.ClassParameter.from(Context.class,context));
    }
}
