package com.archos.mediacenter.video.streaming.putio;

import android.app.Application;
import java.util.*;
import org.json.*;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;
import static org.junit.Assert.*;

@RunWith(RobolectricTestRunner.class) @Config(application=Application.class,sdk=28)
public class PutioSnapshotReaderTest {
    private static PutioReadClient.Item file(long id,long parent,String name,String type){return new PutioReadClient.Item(id,parent,100,name,type);}
    @Test public void paginatedRecursiveTraversalKeepsSourceRelativePaths(){
        PutioReconciliation.Snapshot snapshot=PutioSnapshotReader.read("a:1",1,(parent,cursor)->{
            if(parent==2)return new PutioReadClient.Page(Arrays.asList(file(4,2,"Episode.mkv","VIDEO")),null,1);
            if(cursor==null)return new PutioReadClient.Page(Arrays.asList(file(2,1,"Show","FOLDER")),"next",2);
            return new PutioReadClient.Page(Arrays.asList(file(3,1,"Movie.mkv","VIDEO")),null,-1);
        });
        assertTrue(snapshot.complete());assertEquals(2,snapshot.files().size());assertEquals("Show/Episode.mkv",snapshot.files().get(1).relativePath);
    }
    @Test public void truncatedListingCannotComplete(){PutioReconciliation.Snapshot s=PutioSnapshotReader.read("a:1",1,(parent,cursor)->new PutioReadClient.Page(Collections.emptyList(),null,100));assertFalse(s.complete());assertEquals(PutioReconciliation.Failure.INVALID_PAGE,s.failure());}
    @Test public void authFailureOnLaterPageKeepsSnapshotIncomplete(){PutioReconciliation.Snapshot s=PutioSnapshotReader.read("a:1",1,(parent,cursor)->{if(cursor==null)return new PutioReadClient.Page(Arrays.asList(file(3,1,"Movie.mkv","VIDEO")),"next",2);throw new PutioReadClient.Unavailable(PutioReconciliation.Failure.UNAUTHORISED,401);});assertFalse(s.complete());assertEquals(PutioReconciliation.Failure.UNAUTHORISED,s.failure());assertTrue(PutioReconciliation.plan(s,Collections.emptyList()).changes.isEmpty());}
    @Test public void repeatedCursorCannotLoopOrComplete(){PutioReconciliation.Snapshot s=PutioSnapshotReader.read("a:1",1,(parent,cursor)->new PutioReadClient.Page(Collections.emptyList(),"same",-1));assertFalse(s.complete());}
    @Test public void missingCursorFieldIsNotTreatedAsLastPage()throws Exception{try{PutioReadClient.parse(new JSONObject("{\"status\":\"OK\",\"files\":[]}"),1);fail();}catch(PutioReadClient.Unavailable failure){assertEquals(PutioReconciliation.Failure.INVALID_PAGE,failure.reason);}}
    @Test public void unrelatedParentIsRejected()throws Exception{JSONObject response=new JSONObject("{\"status\":\"OK\",\"cursor\":null,\"files\":[{\"id\":4,\"parent_id\":2,\"size\":1,\"name\":\"A.mkv\",\"file_type\":\"VIDEO\"}]}");try{PutioReadClient.parse(response,1);fail();}catch(PutioReadClient.Unavailable expected){assertEquals(PutioReconciliation.Failure.INVALID_PAGE,expected.reason);}}
    @Test public void invalidAuthorisationNeverAppearsInException(){String privateInput="private-value\ninvalid";try{new PutioReadClient(privateInput);fail();}catch(IllegalArgumentException safe){assertFalse(safe.toString().contains("private-value"));}}
}
