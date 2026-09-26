package com.archos.mediacenter.video.streaming.putio;

import android.app.Application;
import android.net.Uri;
import org.junit.*;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;
import java.util.*;
import static org.junit.Assert.*;

@RunWith(RobolectricTestRunner.class) @Config(application=Application.class,sdk=28)
public class PutioAssociationStoreTest {
    private PutioAssociationStore store;
    @Before public void setup(){RuntimeEnvironment.getApplication().deleteDatabase("putio-associations.db");store=new PutioAssociationStore(RuntimeEnvironment.getApplication());store.prepare(10,20,Uri.parse("https://webdav.put.io/Films"));}
    @After public void close(){store.close();}
    private PutioReconciliation.File file(long id,String path){return new PutioReconciliation.File(id,20,path,100);}
    private PutioReconciliation.Existing existing(long media,String path,long id){return new PutioReconciliation.Existing(media,path,100,id);}
    private PutioReconciliation.Snapshot snapshot(PutioAssociationStore.Session session,PutioReconciliation.File... files){
        PutioReconciliation.Snapshot s=new PutioReconciliation.Snapshot(session.scope,"first");s.page("first",Arrays.asList(files),Collections.emptyList());assertTrue(s.finish());return s;
    }
    @Test public void completeAssociationRetainsMediaIdentityAcrossRenameAndMarksMissingWithoutDeleting(){
        PutioAssociationStore.Session first=store.begin(10,20);
        assertTrue(store.commit(first,snapshot(first,file(30,"a.mkv")),Collections.singletonList(existing(5,"a.mkv",0))));
        assertTrue(store.activate(first));assertEquals(PutioAssociationStore.Ownership.API,store.ownership(10,20));
        PutioAssociationStore.Session rename=store.begin(10,20);
        assertTrue(store.commit(rename,snapshot(rename,file(30,"renamed/a.mkv")),Collections.singletonList(existing(5,"a.mkv",30))));
        assertEquals(5,store.links(10,20).get(0).mediaId);assertEquals("renamed/a.mkv",store.links(10,20).get(0).relativePath);
        PutioAssociationStore.Session missing=store.begin(10,20);
        assertTrue(store.commit(missing,snapshot(missing),Collections.singletonList(existing(5,"renamed/a.mkv",30))));
        assertEquals(1,store.links(10,20).size());assertTrue(store.links(10,20).get(0).missing);
    }
    @Test public void partialAndStaleResultsCannotAttachOrActivate(){
        PutioAssociationStore.Session old=store.begin(10,20),current=store.begin(10,20);
        assertFalse(store.commit(old,snapshot(old,file(30,"a.mkv")),Collections.singletonList(existing(5,"a.mkv",0))));
        PutioReconciliation.Snapshot partial=new PutioReconciliation.Snapshot(current.scope,"first");
        partial.page("first",Collections.singletonList(file(30,"a.mkv")),Collections.singletonList("more"));
        partial.fail(PutioReconciliation.Failure.RATE_LIMITED);
        assertFalse(store.commit(current,partial,Collections.singletonList(existing(5,"a.mkv",0))));
        assertFalse(store.activate(current));assertTrue(store.links(10,20).isEmpty());
    }
    @Test public void identityConflictRollsBackEarlierLinksInSameSnapshot(){
        PutioAssociationStore.Session first=store.begin(10,20);
        assertTrue(store.commit(first,snapshot(first,file(30,"a.mkv")),Collections.singletonList(existing(5,"a.mkv",0))));
        PutioAssociationStore.Session conflict=store.begin(10,20);
        try{
            store.commit(conflict,snapshot(conflict,file(31,"b.mkv"),file(30,"a.mkv")),Arrays.asList(existing(6,"b.mkv",0),existing(7,"a.mkv",30)));
            fail("Conflicting provider identity accepted");
        }catch(IllegalStateException expected){}
        assertEquals(1,store.links(10,20).size());assertEquals(5,store.links(10,20).get(0).mediaId);
    }
    @Test public void unresolvedNewFilesPreventDiscoveryHandoff(){
        PutioAssociationStore.Session first=store.begin(10,20);
        assertTrue(store.commit(first,snapshot(first,file(30,"new.mkv")),Collections.emptyList()));
        assertFalse(store.activate(first));assertEquals(PutioAssociationStore.Ownership.PREPARING,store.ownership(10,20));
    }
    @Test public void disconnectRequiresChoiceRetainsLinksAndInvalidatesRunningSync(){
        PutioAssociationStore.Session first=store.begin(10,20);
        assertTrue(store.commit(first,snapshot(first,file(30,"a.mkv")),Collections.singletonList(existing(5,"a.mkv",0))));
        assertTrue(store.activate(first));PutioAssociationStore.Session pending=store.begin(10,20);
        try{store.disconnect(10,20,null);fail("Missing choice accepted");}catch(IllegalArgumentException expected){}
        store.disconnect(10,20,PutioAssociationStore.DisconnectChoice.KEEP_INACTIVE);
        assertFalse(store.commit(pending,snapshot(pending),Collections.singletonList(existing(5,"a.mkv",30))));
        assertEquals(PutioAssociationStore.Ownership.INACTIVE,store.ownership(10,20));assertFalse(store.links(10,20).get(0).missing);
        store.disconnect(10,20,PutioAssociationStore.DisconnectChoice.REVERT_TO_GENERIC);
        assertEquals(PutioAssociationStore.Ownership.GENERIC,store.ownership(10,20));assertEquals(1,store.links(10,20).size());
    }
    @Test public void changingScopeSourceOrPersistingUriCredentialsIsRejected(){
        for(String uri:Arrays.asList("https://user:password@webdav.put.io/TV","https://webdav.put.io/TV?token=secret")){
            try{store.prepare(10,21,Uri.parse(uri));fail("Credential-bearing source accepted");}catch(IllegalArgumentException expected){}
        }
        try{store.prepare(10,20,Uri.parse("https://webdav.put.io/Other"));fail("Silent reassignment accepted");}catch(IllegalStateException expected){}
    }
}
