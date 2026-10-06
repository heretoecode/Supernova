package com.archos.mediacenter.video.streaming.putio;

import org.json.JSONObject;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;
import static org.junit.Assert.*;

@RunWith(RobolectricTestRunner.class) @Config(application=android.app.Application.class,sdk=28)
public class PutioFileInfoTest {
    private static final String FILE="{id:42,parent_id:7,size:100,name:'Example.mkv',file_type:VIDEO}";
    @Test public void searchAcceptsDifferentParentsButFolderListingDoesNot()throws Exception {
        JSONObject response=new JSONObject("{status:OK,cursor:null,total:2,files:["+FILE+",{id:43,parent_id:8,size:10,name:'Other.mkv',file_type:VIDEO}]}");
        assertEquals(2,PutioReadClient.parseSearch(response).items.size());
        try{PutioReadClient.parse(response,7);fail();}catch(PutioReadClient.Unavailable expected){assertEquals(PutioReconciliation.Failure.INVALID_PAGE,expected.reason);}
        response.remove("total");try{PutioReadClient.parseSearch(response);fail();}catch(PutioReadClient.Unavailable expected){}
    }
    @Test public void mediaDisplaySelectsTechnicalFieldsAndExcludesTransportSecrets()throws Exception {
        JSONObject file=new JSONObject(FILE);file.put("stream_url","secret");file.put("download_token","secret");
        file.put("media_info",new JSONObject("{format:{name:matroska,duration:120.5,bit_rate:9000},streams:[{codec_type:video,codec_name:hevc,width:3840,height:2160},{codec_type:audio,codec_name:null,channels:6}],private_field:secret}"));
        PutioReadClient.FileInfo info=PutioReadClient.parseFile(new JSONObject().put("status","OK").put("file",file),42);
        assertEquals(42,info.item.id);assertTrue(info.description.contains("3840"));assertTrue(info.description.contains("120.5"));assertTrue(info.description.contains("Channels: 6"));assertFalse(info.description.contains("secret"));assertFalse(info.description.contains("null"));
    }
    @Test public void wrongIdentityAndInvalidNumbersFailSafely()throws Exception {
        for(String media:new String[]{"{format:{duration:-1}}","{streams:[{width:'private'}]}"}){
            JSONObject file=new JSONObject(FILE).put("media_info",new JSONObject(media));
            try{PutioReadClient.parseFile(new JSONObject().put("status","OK").put("file",file),42);fail();}catch(PutioReadClient.Unavailable expected){assertFalse(expected.getMessage().contains("private"));}
        }
        try{PutioReadClient.parseFile(new JSONObject().put("status","OK").put("file",new JSONObject(FILE)),43);fail();}catch(PutioReadClient.Unavailable expected){}
    }
}
