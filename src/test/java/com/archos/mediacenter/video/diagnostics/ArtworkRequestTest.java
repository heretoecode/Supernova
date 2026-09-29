package com.archos.mediacenter.video.diagnostics;

import android.app.Application;
import android.net.Uri;
import android.widget.ImageView;
import com.squareup.picasso.Callback;
import com.squareup.picasso.RequestCreator;
import org.json.JSONObject;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.robolectric.*;
import org.robolectric.annotation.Config;
import org.robolectric.util.ReflectionHelpers;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith(RobolectricTestRunner.class) @Config(application=Application.class,sdk=28)
public class ArtworkRequestTest {
    @org.junit.Before public void initialisePicasso(){
        try{com.squareup.picasso.Picasso.get();}catch(IllegalStateException uninitialised){com.squareup.picasso.Picasso.setSingletonInstance(new com.squareup.picasso.Picasso.Builder(RuntimeEnvironment.getApplication()).build());}
    }
    @Test public void rebindTerminatesOldRequestAndDoesNotLeakSourceOrFailureMessage()throws Exception{
        Application app=RuntimeEnvironment.getApplication();Diagnostics.setEnabled(app,true);
        ImageView view=new ImageView(app);
        try{
            RequestCreator first=mock(RequestCreator.class),second=mock(RequestCreator.class);
            ArtworkRequest.load(view,Uri.parse("https://private.example/art.jpg?token=secret"),78123,"details.extras","video_thumbnail",first);
            ArgumentCaptor<Callback> oldCallback=ArgumentCaptor.forClass(Callback.class);verify(first).into(eq(view),oldCallback.capture());
            ArtworkRequest.load(view,Uri.parse("file:///private/title.jpg"),78124,"details.people","portrait",second,true);
            ArgumentCaptor<Callback> callback=ArgumentCaptor.forClass(Callback.class);verify(second).into(eq(view),callback.capture());
            oldCallback.getValue().onSuccess();callback.getValue().onError(new java.io.IOException("secret private failure"));
            DiagnosticFlightRecorder recorder=ReflectionHelpers.getStaticField(Diagnostics.class,"FLIGHT");
            int cancelled=0,failed=0,late=0;
            for(String line:recorder.snapshot(android.os.SystemClock.elapsedRealtime()).split("\n")){
                if(line.isEmpty())continue;JSONObject row=new JSONObject(line);long media=row.optLong("media_id");if(media!=78123&&media!=78124)continue;
                assertFalse(line.contains("private"));assertFalse(line.contains("secret"));assertFalse(line.contains("art.jpg"));
                String event=row.optString("event");
                if(media==78123&&event.equals("artwork_cancelled"))cancelled++;
                if(media==78123&&event.equals("artwork_ready"))late++;
                if(media==78124&&event.equals("artwork_failed")){failed++;assertTrue(row.getBoolean("fallback_succeeded"));assertEquals("local",row.getString("source"));}
            }
            assertEquals(1,cancelled);assertEquals(1,failed);assertEquals(0,late);
        }finally{ArtworkRequest.cancel(view);Diagnostics.setEnabled(app,false);}
    }
}
