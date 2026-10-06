package com.archos.mediacenter.video.streaming.putio;

import android.net.Uri;
import java.util.*;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;
import static org.junit.Assert.*;

@RunWith(RobolectricTestRunner.class) @Config(application=android.app.Application.class,sdk=28)
public class PutioRootLocationTest {
    @Test public void stableIdWalkBuildsCurrentFolderPath()throws Exception {
        Map<Long,PutioReadClient.Item> folders=new HashMap<>();folders.put(7L,new PutioReadClient.Item(7,8,0,"Films","FOLDER"));folders.put(8L,new PutioReadClient.Item(8,0,0,"Archive","FOLDER"));
        assertEquals(Arrays.asList("Archive","Films"),PutioRootLocation.path(7,folders::get));
        folders.put(8L,new PutioReadClient.Item(8,7,0,"Archive","FOLDER"));
        try{PutioRootLocation.path(7,folders::get);fail();}catch(PutioReadClient.Unavailable expected){assertEquals(PutioReconciliation.Failure.INVALID_PAGE,expected.reason);}
    }
    @Test public void verifiedSuffixPreservesServerPrefixAndEscapesNewNames(){
        Uri source=Uri.parse("https://webdav.put.io/dav/Films");
        assertEquals("https://webdav.put.io/dav/Archive/Film%20%25",PutioRootLocation.rebase(source,Arrays.asList("Films"),Arrays.asList("Archive","Film %")).toString());
        try{PutioRootLocation.rebase(source,Arrays.asList("Other"),Arrays.asList("Archive"));fail();}catch(IllegalStateException expected){}
    }
    @Test public void mappingIsBoundToAccountFolderAndSource()throws Exception {
        android.content.Context c=RuntimeEnvironment.getApplication();c.getSharedPreferences("putio-library-selection-v1",0).edit().clear().commit();
        Uri source=Uri.parse("https://webdav.put.io/Films");PutioRootLocation.remember(c,1,7,source,Arrays.asList("Films"));
        assertEquals("https://webdav.put.io/Archive/Films",PutioRootLocation.target(c,1,7,source,Arrays.asList("Archive","Films")).toString());
        assertEquals(source,PutioRootLocation.target(c,2,7,source,Arrays.asList("Archive","Films")));
        Uri custom=Uri.parse("https://example.test/custom");PutioRootLocation.remember(c,1,7,custom,Arrays.asList("Films"));
        try{PutioRootLocation.target(c,1,7,custom,Arrays.asList("Archive","Films"));fail();}catch(IllegalStateException expected){}
    }
}
