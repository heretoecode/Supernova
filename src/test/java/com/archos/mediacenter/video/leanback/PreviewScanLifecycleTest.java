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
