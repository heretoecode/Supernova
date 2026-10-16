package com.archos.mediacenter.video.streaming.putio;

import android.app.Application;
import okhttp3.*;
import java.util.concurrent.atomic.AtomicReference;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;
import org.robolectric.util.ReflectionHelpers;
import static org.junit.Assert.*;

@RunWith(RobolectricTestRunner.class) @Config(application=Application.class,sdk=28)
public class PutioGrantRevokeTest {
    @Test public void explicitRevokeTargetsOnlyRegisteredSupernovaGrantWithoutCredentialInUrl()throws Exception{
        PutioReadClient client=new PutioReadClient("fixture-token");AtomicReference<Request> request=new AtomicReference<>();
        OkHttpClient transport=new OkHttpClient.Builder().addInterceptor(chain->{request.set(chain.request());return new Response.Builder().request(chain.request()).protocol(Protocol.HTTP_1_1).code(200).message("OK").body(ResponseBody.create(MediaType.parse("application/json"),"{status:OK}")).build();}).build();
        ReflectionHelpers.setField(client,"http",transport);client.revokeSupernovaGrant();assertEquals("POST",request.get().method());assertEquals("https://api.put.io/v2/oauth/grants/9874/delete",request.get().url().toString());assertEquals("Token fixture-token",request.get().header("Authorization"));assertNull(request.get().url().query());
    }
}
