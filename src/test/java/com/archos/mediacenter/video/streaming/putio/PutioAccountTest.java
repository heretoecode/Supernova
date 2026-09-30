package com.archos.mediacenter.video.streaming.putio;
import org.json.JSONObject;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;
import static org.junit.Assert.*;
@RunWith(RobolectricTestRunner.class) @Config(application=android.app.Application.class,sdk=28)
public class PutioAccountTest {
    @Test public void malformedAccountIsCorrelatedWithSuccessfulTransportWithoutLeakingBody()throws Exception{
        android.app.Application app=org.robolectric.RuntimeEnvironment.getApplication();
        com.archos.mediacenter.video.diagnostics.Diagnostics.setEnabled(app,true);
        try{
            PutioReadClient client=new PutioReadClient("fixture-private-token");
            okhttp3.OkHttpClient fake=new okhttp3.OkHttpClient.Builder().addInterceptor(chain->new okhttp3.Response.Builder()
                    .request(chain.request()).protocol(okhttp3.Protocol.HTTP_1_1).code(200).message("OK")
                    .body(okhttp3.ResponseBody.create(okhttp3.MediaType.parse("application/json"),"{status:OK,info:{username:'fixture-private-name'}}")).build()).build();
            org.robolectric.util.ReflectionHelpers.setField(client,"http",fake);
            try{client.account();fail();}catch(PutioReadClient.Unavailable expected){assertEquals(PutioReconciliation.Failure.INVALID_PAGE,expected.reason);}
            Object recorder=org.robolectric.util.ReflectionHelpers.getStaticField(com.archos.mediacenter.video.diagnostics.Diagnostics.class,"FLIGHT");
            java.lang.reflect.Method snapshot=recorder.getClass().getDeclaredMethod("snapshot",long.class);snapshot.setAccessible(true);
            String evidence=(String)snapshot.invoke(recorder,android.os.SystemClock.elapsedRealtime());
            assertFalse(evidence.contains("fixture-private-token"));assertFalse(evidence.contains("fixture-private-name"));
            String operation=null;int failures=0;
            for(String line:evidence.split("\n")){if(line.isEmpty())continue;JSONObject row=new JSONObject(line);
                if("account_info".equals(row.optString("operation_type"))&&"provider_request_failed".equals(row.optString("event"))){
                    failures++;assertEquals("INVALID_PAGE",row.getString("outcome"));assertEquals(200,row.getInt("status"));operation=row.getString("operation_id");}}
            assertEquals(1,failures);int transports=0,ends=0;
            for(String line:evidence.split("\n")){if(line.isEmpty())continue;JSONObject row=new JSONObject(line);if(!operation.equals(row.optString("operation_id")))continue;
                if("provider_transport_complete".equals(row.optString("event")))transports++;if("operation_end".equals(row.optString("event")))ends++;}
            assertEquals(1,transports);assertEquals(1,ends);
        }finally{com.archos.mediacenter.video.diagnostics.Diagnostics.setEnabled(app,false);}
    }
    @Test public void selectsOnlyDisplayAndStorageFields()throws Exception{
        PutioReadClient.Account account=PutioReadClient.parseAccount(new JSONObject("{status:OK,info:{user_id:7,username:viewer,account_status:active,disk:{used:500,size:1000},download_token:must_not_escape,mail:private}}"));
        assertEquals(7,account.id);assertEquals("viewer",account.username);assertEquals(500,account.usedBytes);assertEquals(1000,account.totalBytes);
    }
    @Test public void incompleteAndInvalidAccountsFailWithSanitisedErrors()throws Exception{
        for(String body:new String[]{"{status:ERROR,error_message:private}","{status:OK,info:{user_id:7,username:viewer,account_status:active,disk:{used:-1,size:1000}}}"}){
            try{PutioReadClient.parseAccount(new JSONObject(body));fail();}catch(PutioReadClient.Unavailable expected){assertEquals(PutioReconciliation.Failure.INVALID_PAGE,expected.reason);assertFalse(expected.getMessage().contains("private"));}
        }
    }
}
