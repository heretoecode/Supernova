package com.archos.mediacenter.video.leanback;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;
import okhttp3.*;
import static org.junit.Assert.*;

@RunWith(RobolectricTestRunner.class) @Config(application=android.app.Application.class,sdk=28)
public class OfficialTitleNetworkTest {
    @Test public void boundedLogoTransportCorrelatesWithoutSensitiveUrl()throws Exception{
        android.app.Application app=org.robolectric.RuntimeEnvironment.getApplication();
        com.archos.mediacenter.video.diagnostics.Diagnostics.setEnabled(app,true);
        try{
            OkHttpClient client=new OkHttpClient.Builder().addInterceptor(chain->new Response.Builder().request(chain.request())
                    .protocol(Protocol.HTTP_1_1).code(200).message("OK").body(ResponseBody.create(MediaType.parse("image/png"),new byte[]{1,2,3})).build()).build();
            try{OfficialTitleArtwork.download(client,"https://example.test/fixture-private-path?api_key=fixture-private-key",2,"fixture-logo-parent","logo_image");fail();}catch(java.io.IOException expected){}
            Object recorder=org.robolectric.util.ReflectionHelpers.getStaticField(com.archos.mediacenter.video.diagnostics.Diagnostics.class,"FLIGHT");
            java.lang.reflect.Method snapshot=recorder.getClass().getDeclaredMethod("snapshot",long.class);snapshot.setAccessible(true);
            String evidence=(String)snapshot.invoke(recorder,android.os.SystemClock.elapsedRealtime());
            assertFalse(evidence.contains("fixture-private-path"));assertFalse(evidence.contains("fixture-private-key"));
            int failures=0;
            for(String line:evidence.split("\n")){if(line.isEmpty())continue;org.json.JSONObject row=new org.json.JSONObject(line);
                if("fixture-logo-parent".equals(row.optString("parent_operation_id"))){failures++;assertEquals("size_limit",row.getString("outcome"));assertEquals(200,row.getInt("status"));assertEquals("tmdb",row.getString("provider"));}}
            assertEquals(1,failures);
        }finally{com.archos.mediacenter.video.diagnostics.Diagnostics.setEnabled(app,false);}
    }
}
