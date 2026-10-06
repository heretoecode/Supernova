package com.archos.mediacenter.video.streaming.putio;

import android.app.Application;
import com.archos.mediacenter.video.diagnostics.Diagnostics;
import com.archos.mediacenter.video.diagnostics.DiagnosticFlightRecorder;
import org.json.JSONObject;
import org.junit.*;
import org.junit.runner.RunWith;
import org.robolectric.*;
import org.robolectric.annotation.Config;
import org.robolectric.util.ReflectionHelpers;
import java.util.*;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith(RobolectricTestRunner.class) @Config(application=Application.class,sdk=28)
public class PutioSyncSchedulerTest {
    private Application app;
    private DiagnosticFlightRecorder flight;
    @Before public void setup(){
        app=RuntimeEnvironment.getApplication();Diagnostics.setEnabled(app,true);
        flight=ReflectionHelpers.getStaticField(Diagnostics.class,"FLIGHT");flight.clear();
    }
    @After public void teardown(){Thread.interrupted();Diagnostics.setEnabled(app,false);}

    @Test public void transportInheritsSyncAndScopeDoesNotLeak()throws Exception{
        PutioReadClient client=mock(PutioReadClient.class);
        when(client.account()).thenAnswer(call->{Diagnostics.operation("fixture_transport");return new PutioReadClient.Account(42,"fixture","active",0,1);});
        String outside=Diagnostics.operation("fixture_outer");
        try(Diagnostics.OperationScope ignored=Diagnostics.operationScope(outside)){
            PutioSyncScheduler.sync(app,"manual",client);Diagnostics.operation("fixture_after");
        }
        List<JSONObject> rows=rows();String sync=kind(rows,"putio_sync").getString("operation_id");
        assertEquals(outside,kind(rows,"putio_sync").getString("parent_operation_id"));
        assertEquals(sync,kind(rows,"fixture_transport").getString("parent_operation_id"));
        assertEquals(outside,kind(rows,"fixture_after").getString("parent_operation_id"));
        assertTerminal(rows,sync,"putio_sync_complete");
    }
    @Test public void cancelledAccountReturnCannotReportSuccessfulSync()throws Exception{
        PutioReadClient client=mock(PutioReadClient.class);
        when(client.account()).thenAnswer(call->{Thread.currentThread().interrupt();return new PutioReadClient.Account(42,"fixture","active",0,1);});
        PutioSyncScheduler.sync(app,"resume",client);
        assertTrue(Thread.currentThread().isInterrupted());Thread.interrupted();
        List<JSONObject> rows=rows();assertTerminal(rows,kind(rows,"putio_sync").getString("operation_id"),"putio_sync_cancelled");
    }
    @Test public void requestFailureStillFinishesTheSameOperation()throws Exception{
        PutioReadClient client=mock(PutioReadClient.class);
        when(client.account()).thenThrow(new IllegalStateException("private fixture detail"));
        PutioSyncScheduler.sync(app,"scheduled",client);
        List<JSONObject> rows=rows();assertTerminal(rows,kind(rows,"putio_sync").getString("operation_id"),"putio_sync_incomplete");
        assertFalse(flight.snapshot(android.os.SystemClock.elapsedRealtime()).contains("private fixture detail"));
    }
    private List<JSONObject> rows()throws Exception{
        List<JSONObject> rows=new ArrayList<>();for(String line:flight.snapshot(android.os.SystemClock.elapsedRealtime()).split("\n"))if(!line.isEmpty())rows.add(new JSONObject(line));return rows;
    }
    private JSONObject kind(List<JSONObject> rows,String kind){return rows.stream().filter(r->"operation_begin".equals(r.optString("event"))&&kind.equals(r.optString("kind"))).findFirst().orElseThrow(AssertionError::new);}
    private void assertTerminal(List<JSONObject> rows,String operation,String expected){
        int terminal=0,ended=0;for(JSONObject row:rows){if(!operation.equals(row.optString("operation_id")))continue;
            String event=row.optString("event");if(event.equals("operation_end"))ended++;
            if(Arrays.asList("putio_sync_complete","putio_sync_cancelled","putio_sync_incomplete").contains(event)){terminal++;assertEquals(expected,event);}
        }assertEquals(1,terminal);assertEquals(1,ended);
    }
}
