package com.archos.mediacenter.video.streaming.putio;

import android.content.Context;
import android.net.Uri;
import java.util.*;
import org.junit.*;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;
import static org.junit.Assert.*;

@RunWith(RobolectricTestRunner.class) @Config(application=android.app.Application.class,sdk=28)
public class PutioSyncTest {
    Context context;Uri source=Uri.parse("https://webdav.put.io/Films");FakeLibrary library;
    @Before public void setup(){context=RuntimeEnvironment.getApplication();context.deleteDatabase("putio-associations.db");context.getSharedPreferences("provider-discovery-ownership-v1",0).edit().clear().commit();library=new FakeLibrary();}
    PutioReconciliation.File file(long id,String name){return new PutioReconciliation.File(id,20,name,100);}
    PutioSync.Review review(boolean complete,PutioReconciliation.File...files){
        try(PutioAssociationStore store=new PutioAssociationStore(context)){
            store.prepare(10,20,source);PutioAssociationStore.Session session=store.begin(10,20);
            PutioReconciliation.Snapshot snapshot=new PutioReconciliation.Snapshot(session.scope,"first");snapshot.page("first",Arrays.asList(files),complete?Collections.emptyList():Collections.singletonList("more"));if(complete)snapshot.finish();
            List<PutioReconciliation.Existing> rows=library.existing(source,store.links(10,20));return new PutioSync.Review(session,source,snapshot,PutioReconciliation.plan(snapshot,rows),rows);
        }
    }
    @Test public void existingIdentitySurvivesRenameAndNewFileIsIndexedOnce(){
        library.rows.put(7L,new PutioReconciliation.Existing(7,"old.mkv",100,0));
        PutioSync.apply(context,review(true,file(30,"old.mkv"),file(31,"new.mkv")),Collections.emptyMap(),library);assertEquals(1,library.inserts);
        PutioSync.apply(context,review(true,file(30,"renamed.mkv"),file(31,"new.mkv")),Collections.emptyMap(),library);
        assertEquals(1,library.inserts);assertEquals("renamed.mkv",library.rows.get(7L).relativePath);
        try(PutioAssociationStore store=new PutioAssociationStore(context)){assertEquals(7,store.links(10,20).get(0).mediaId);assertEquals(PutioAssociationStore.Ownership.API,store.ownership(10,20));}
    }
    @Test public void incompleteAndDisconnectedSessionsCannotWriteNativeLibrary(){
        try{PutioSync.apply(context,review(false,file(30,"new.mkv")),Collections.emptyMap(),library);fail();}catch(IllegalStateException expected){}
        PutioSync.Review pending=review(true,file(30,"new.mkv"));try(PutioAssociationStore store=new PutioAssociationStore(context)){store.disconnectAccount(10,PutioAssociationStore.DisconnectChoice.KEEP_INACTIVE);}
        try{PutioSync.apply(context,pending,Collections.emptyMap(),library);fail();}catch(IllegalStateException expected){}assertEquals(0,library.inserts);
    }
    @Test public void ambiguousAssociationWaitsForExplicitSameFileChoice(){
        library.rows.put(7L,new PutioReconciliation.Existing(7,"old/title.mkv",100,0));
        PutioSync.Review pending=PutioSync.apply(context,review(true,file(30,"new/title.mkv")),Collections.emptyMap(),library);
        assertEquals(0,library.inserts);assertEquals(PutioReconciliation.Decision.NEEDS_REVIEW,pending.plan.changes.get(0).decision);
        try(PutioAssociationStore store=new PutioAssociationStore(context)){assertEquals(PutioAssociationStore.Ownership.PREPARING,store.ownership(10,20));}
        PutioSync.apply(context,pending,Collections.singletonMap(30L,7L),library);
        assertEquals("new/title.mkv",library.rows.get(7L).relativePath);assertEquals(0,library.inserts);
    }
    @Test public void keepSeparateLeavesOriginalRecordUntouched(){
        library.rows.put(7L,new PutioReconciliation.Existing(7,"old/title.mkv",100,0));PutioSync.Review pending=review(true,file(30,"new/title.mkv"));
        PutioSync.apply(context,pending,Collections.singletonMap(30L,0L),library);assertEquals(1,library.inserts);assertEquals("old/title.mkv",library.rows.get(7L).relativePath);
    }
    @Test public void playbackPathEncodesNamesWithoutTurningPercentTextIntoSeparators(){
        assertEquals("https://webdav.put.io/Films/100%25%20real/a%252Fb.mkv",PutioLibraryBridge.playback(source,"100% real/a%2Fb.mkv").toString());
        try{PutioLibraryBridge.playback(source,"../escape.mkv");fail();}catch(IllegalArgumentException expected){}
    }
    static final class FakeLibrary implements PutioSync.Library {
        Map<Long,PutioReconciliation.Existing> rows=new LinkedHashMap<>();int inserts;
        public List<PutioReconciliation.Existing> existing(Uri source,List<PutioAssociationStore.Link> links){Map<Long,Long> ids=new HashMap<>();for(PutioAssociationStore.Link link:links)ids.put(link.mediaId,link.fileId);List<PutioReconciliation.Existing> result=new ArrayList<>();for(PutioReconciliation.Existing row:rows.values())result.add(new PutioReconciliation.Existing(row.mediaId,row.relativePath,row.size,ids.getOrDefault(row.mediaId,0L)));return result;}
        public long insert(Uri source,PutioReconciliation.File file){long id=100+(++inserts);rows.put(id,new PutioReconciliation.Existing(id,file.relativePath,file.size,0));return id;}
        public void relocate(Uri source,PutioReconciliation.Existing old,PutioReconciliation.File file){rows.put(old.mediaId,new PutioReconciliation.Existing(old.mediaId,file.relativePath,file.size,old.providerFileId));}
        public void enrich(){}
    }
}
