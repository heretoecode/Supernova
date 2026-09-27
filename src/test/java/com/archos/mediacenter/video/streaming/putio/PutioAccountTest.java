package com.archos.mediacenter.video.streaming.putio;
import org.json.JSONObject;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;
import static org.junit.Assert.*;
@RunWith(RobolectricTestRunner.class) @Config(application=android.app.Application.class,sdk=28)
public class PutioAccountTest {
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
