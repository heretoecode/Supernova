package com.archos.mediacenter.video.utils;

import android.app.Application;
import com.archos.mediacenter.video.utils.backup.*;
import org.json.*;
import org.junit.*;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;
import static org.junit.Assert.*;

@RunWith(RobolectricTestRunner.class) @Config(application=Application.class,sdk=23)
public class PortableMinimumApiTest {
    private JSONObject bookmark(String label)throws Exception{return new JSONObject().put("schema",1).put("rows",new JSONArray().put(new JSONObject().put("id","bookmark:12345678-1234-1234-1234-123456789abc:1").put("location",PortableLocations.reference("file:///media/known.mkv")).put("name",label)));}
    @Test public void minimumApiKeepsUnicodeLabelsAndCanonicalLocations()throws Exception{PortableBookmarks.validate(bookmark("Films 😀"));assertEquals("file:///media/known.mkv",PortableLocations.locator("/media/known.mkv"));assertEquals("smb://server/media",PortableLocations.locator("smb://server/media"));}
    @Test public void minimumApiRejectsRawAndEncodedControlsInsteadOfLeakingUnsafeLocators()throws Exception{for(char value:new char[]{0,7,10,13,31,127}){try{PortableBookmarks.validate(bookmark("Name"+value));fail();}catch(JSONException expected){}try{PortableLocations.locator("file:///media/item"+value+".mkv");fail();}catch(JSONException expected){}}for(String encoded:new String[]{"%00","%07","%0A","%0D","%1F","%7F"})try{PortableLocations.locator("file:///media/"+encoded+"item.mkv");fail();}catch(JSONException expected){}}
}
