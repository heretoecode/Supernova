package com.archos.mediacenter.video.leanback;

import android.app.Application;
import android.os.SystemClock;
import com.archos.mediacenter.video.diagnostics.DiagnosticFlightRecorder;
import com.archos.mediacenter.video.diagnostics.Diagnostics;
import com.archos.mediacenter.video.leanback.search.PreviewSearch;
import java.util.HashSet;
import java.util.Set;
import org.json.JSONObject;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.*;
import org.robolectric.annotation.Config;
import static org.junit.Assert.*;
import org.robolectric.util.ReflectionHelpers;

@RunWith(RobolectricTestRunner.class)
@Config(application=Application.class,sdk=28,qualifiers="w960dp-h540dp-land-mdpi")
public class PreviewRebuildTelemetryTest {
 @Before public void isolatePreviewTransport(){PreviewAsyncFixtures.offlineTransport();}
 @After public void drainPreviewWorkers() throws Exception { PreviewAsyncFixtures.drain(); }
    @Test public void newLibraryAndSearchAdaptersReportCreationSeparatelyFromRebind()throws Exception{
        var host=Robolectric.buildActivity(TopNavigationTest.Host.class).setup().visible();
        Diagnostics.setEnabled(host.get(),true);
        try{
            for(int i=0;i<2;i++){
                PreviewPages pages=new PreviewPages(host.get(),(holder,item)->{});
                host.get().setContentView(pages);pages.setSnapshot(new PreviewLibraryLoader.Snapshot());pages.setTab(1);
                host.get().setContentView(new PreviewSearch(host.get(),0,null));
            }
            DiagnosticFlightRecorder recorder=ReflectionHelpers.getStaticField(Diagnostics.class,"FLIGHT");
            Set<Integer> libraries=new HashSet<>(),searches=new HashSet<>();
            boolean rebind=false;
            for(String line:recorder.snapshot(SystemClock.elapsedRealtime()).split("\n")){
                if(line.isEmpty())continue;JSONObject row=new JSONObject(line);
                if(!"ui_rebuild".equals(row.optString("event")))continue;
                if("adapter_created".equals(row.optString("reason"))){
                    assertTrue(row.getBoolean("adapter_recreated"));assertEquals(1,row.getInt("rebuild_count"));
                    assertEquals(0,row.getInt("previous_items"));assertEquals(0,row.getInt("items"));
                    if("library.adapter".equals(row.getString("surface")))libraries.add(row.getInt("view_instance"));
                    if("search.results".equals(row.getString("surface")))searches.add(row.getInt("view_instance"));
                }else if("movies.library".equals(row.optString("surface"))){
                    assertFalse(row.getBoolean("adapter_recreated"));assertTrue(row.getInt("rebuild_count")>1);rebind=true;
                }
            }
            assertEquals(2,libraries.size());assertEquals(2,searches.size());assertTrue(rebind);
        }finally{Diagnostics.setEnabled(host.get(),false);host.pause().stop().destroy();}
    }
}
