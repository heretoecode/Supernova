package com.archos.mediacenter.video.leanback;

import android.app.Application;
import com.archos.mediacenter.video.diagnostics.DiagnosticFlightRecorder;
import com.archos.mediacenter.video.diagnostics.Diagnostics;
import com.archos.mediacenter.video.leanback.search.PreviewSearch;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.*;
import org.robolectric.annotation.Config;
import static org.junit.Assert.*;

@RunWith(RobolectricTestRunner.class)
@Config(application=Application.class,sdk=28,qualifiers="w960dp-h540dp-land-mdpi")
public class PreviewRebuildTelemetryTest {
    @Test public void newLibraryAndSearchAdaptersReportCreationSeparatelyFromRebind()throws Exception{
        var host=Robolectric.buildActivity(TopNavigationTest.Host.class).setup().visible();
        Diagnostics.setEnabled(host.get(),true);
        try{
            for(int i=0;i<2;i++){
                PreviewPages pages=new PreviewPages(host.get(),(holder,item)->{});
                host.get().setContentView(pages);pages.setSnapshot(new PreviewLibraryLoader.Snapshot());pages.setTab(1);
                host.get().setContentView(new PreviewSearch(host.get(),0,null));
            }
            DiagnosticFlightRecorder recorder=org.robolectric.util.ReflectionHelpers.getStaticField(Diagnostics.class,"FLIGHT");
            java.util.Set<Integer> libraries=new java.util.HashSet<>(),searches=new java.util.HashSet<>();
            boolean rebind=false;
            for(String line:recorder.snapshot(android.os.SystemClock.elapsedRealtime()).split("\n")){
                if(line.isEmpty())continue;org.json.JSONObject row=new org.json.JSONObject(line);
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
