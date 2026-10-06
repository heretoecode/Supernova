package com.archos.mediacenter.video.streaming.putio;

import org.json.JSONObject;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;
import static org.junit.Assert.*;

@RunWith(RobolectricTestRunner.class) @Config(application=android.app.Application.class,sdk=28)
public class PutioOAuthClientTest {
    @Test public void oauthDiagnosticsIncludeSemanticFailureButNeverPayloadOrLinkCode()throws Exception{
        android.app.Application app=org.robolectric.RuntimeEnvironment.getApplication();
        com.archos.mediacenter.video.diagnostics.Diagnostics.setEnabled(app,true);
        try{
            PutioOAuthClient client=new PutioOAuthClient("1","https://put.io/test-only/{code}");
            okhttp3.OkHttpClient fake=new okhttp3.OkHttpClient.Builder().addInterceptor(chain->new okhttp3.Response.Builder()
                    .request(chain.request()).protocol(okhttp3.Protocol.HTTP_1_1).code(200).message("OK")
                    .body(okhttp3.ResponseBody.create(okhttp3.MediaType.parse("application/json"),"{status:OK,private_data:'fixture-private-payload'}")).build()).build();
            org.robolectric.util.ReflectionHelpers.setField(client,"http",fake);
            try{client.poll(new PutioOAuthClient.Code("fixture-private-code","unused"));fail();}catch(java.io.IOException expected){}
            Object recorder=org.robolectric.util.ReflectionHelpers.getStaticField(com.archos.mediacenter.video.diagnostics.Diagnostics.class,"FLIGHT");
            java.lang.reflect.Method snapshot=recorder.getClass().getDeclaredMethod("snapshot",long.class);snapshot.setAccessible(true);
            String evidence=(String)snapshot.invoke(recorder,android.os.SystemClock.elapsedRealtime());
            assertFalse(evidence.contains("fixture-private-payload"));assertFalse(evidence.contains("fixture-private-code"));
            String operation=null;
            for(String line:evidence.split("\n")){if(line.isEmpty())continue;JSONObject row=new JSONObject(line);
                if("link_poll".equals(row.optString("operation_type"))){assertEquals("invalid_payload",row.getString("outcome"));assertEquals(200,row.getInt("status"));operation=row.getString("operation_id");}}
            assertNotNull(operation);int ends=0;
            for(String line:evidence.split("\n")){if(line.isEmpty())continue;JSONObject row=new JSONObject(line);if(operation.equals(row.optString("operation_id"))&&"operation_end".equals(row.optString("event")))ends++;}
            assertEquals(1,ends);
        }finally{com.archos.mediacenter.video.diagnostics.Diagnostics.setEnabled(app,false);}
    }
    @Test public void emptyOrUnsafeProductionConfigurationCannotStartLinking(){
        assertFalse(PutioOAuthClient.configured("",""));
        for(String template:new String[]{"http://put.io/{code}","https://other.example/{code}","https://user:password@put.io/{code}","https://put.io/{code}?token=secret","https://put.io/{code}/{code}"})assertFalse(PutioOAuthClient.configured("1",template));
        // This is a parser fixture, not an assertion that this is the production linking route.
        assertTrue(PutioOAuthClient.configured("1","https://put.io/test-only/{code}"));
    }
    @Test public void qrPayloadUsesOnlyTemporaryCodeAndValidatedTemplate()throws Exception{
        PutioOAuthClient.Code code=PutioOAuthClient.parseCode(new JSONObject("{status:OK,code:'A&B',oauth_token:'never-in-qr'}"),"https://put.io/test-only/{code}");
        assertEquals("https://put.io/test-only/A%26B",code.link);assertFalse(code.link.contains("never-in-qr"));
    }
    @Test public void pendingIsDifferentFromMalformedAuthorisation()throws Exception{
        assertNull(PutioOAuthClient.parseToken(new JSONObject("{status:OK,oauth_token:null}")));
        assertEquals("test-only-credential",PutioOAuthClient.parseToken(new JSONObject("{status:OK,oauth_token:'test-only-credential'}")));
        for(String data:new String[]{"{status:OK}","{status:OK,oauth_token:123}","{status:ERROR,error_message:'private-response'}"}){
            try{PutioOAuthClient.parseToken(new JSONObject(data));fail();}catch(java.io.IOException expected){assertFalse(expected.getMessage().contains("private-response"));}
        }
    }
}
