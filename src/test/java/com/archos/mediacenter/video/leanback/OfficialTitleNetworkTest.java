package com.archos.mediacenter.video.leanback;

import android.app.Application;
import android.os.SystemClock;
import com.archos.mediacenter.video.diagnostics.Diagnostics;
import java.io.IOException;
import java.lang.reflect.Method;
import org.json.JSONObject;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;
import okhttp3.*;
import static org.junit.Assert.*;
import org.robolectric.util.ReflectionHelpers;

@RunWith(RobolectricTestRunner.class) @Config(application=Application.class,sdk=28)
public class OfficialTitleNetworkTest {
    @Test public void boundedLogoTransportCorrelatesWithoutSensitiveUrl()throws Exception{
        Application app=RuntimeEnvironment.getApplication();
        Diagnostics.setEnabled(app,true);
        try{
            OkHttpClient client=new OkHttpClient.Builder().addInterceptor(chain->new Response.Builder().request(chain.request())
                    .protocol(Protocol.HTTP_1_1).code(200).message("OK").body(ResponseBody.create(MediaType.parse("image/png"),new byte[]{1,2,3})).build()).build();
            try{OfficialTitleArtwork.download(client,"https://example.test/fixture-private-path?api_key=fixture-private-key",2,"fixture-logo-parent","logo_image");fail();}catch(IOException expected){}
            Object recorder=ReflectionHelpers.getStaticField(Diagnostics.class,"FLIGHT");
            Method snapshot=recorder.getClass().getDeclaredMethod("snapshot",long.class);snapshot.setAccessible(true);
            String evidence=(String)snapshot.invoke(recorder,SystemClock.elapsedRealtime());
            assertFalse(evidence.contains("fixture-private-path"));assertFalse(evidence.contains("fixture-private-key"));
            int failures=0;
            for(String line:evidence.split("\n")){if(line.isEmpty())continue;JSONObject row=new JSONObject(line);
                if("fixture-logo-parent".equals(row.optString("parent_operation_id"))){failures++;assertEquals("size_limit",row.getString("outcome"));assertEquals(200,row.getInt("status"));assertEquals("tmdb",row.getString("provider"));}}
            assertEquals(1,failures);
        }finally{Diagnostics.setEnabled(app,false);}
    }
}
