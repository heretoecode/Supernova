package com.archos.mediacenter.video.streaming.putio;

import org.json.JSONObject;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;
import static org.junit.Assert.*;

@RunWith(RobolectricTestRunner.class) @Config(application=android.app.Application.class,sdk=28)
public class PutioOAuthClientTest {
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
