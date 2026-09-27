package com.archos.mediacenter.video.streaming.putio;

import android.content.Context;
import android.net.Uri;
import com.archos.mediaprovider.video.ProviderDiscoveryGate;
import java.util.*;

/** Complete-snapshot sync. Native writes are fenced against generic discovery and disconnect. */
final class PutioSync {
    interface Library {
        List<PutioReconciliation.Existing> existing(Uri source,List<PutioAssociationStore.Link> links);
        long insert(Uri source,PutioReconciliation.File file);
        void relocate(Uri source,PutioReconciliation.Existing old,PutioReconciliation.File file);
        void enrich();
    }
    static final class Review {
        final PutioAssociationStore.Session session;final Uri source;final PutioReconciliation.Snapshot snapshot;
        final PutioReconciliation.Plan plan;final List<PutioReconciliation.Existing> existing;
        final Set<Long> initialMediaIds;
        Review(PutioAssociationStore.Session session,Uri source,PutioReconciliation.Snapshot snapshot,PutioReconciliation.Plan plan,List<PutioReconciliation.Existing> existing){this(session,source,snapshot,plan,existing,mediaIds(existing));}
        private Review(PutioAssociationStore.Session session,Uri source,PutioReconciliation.Snapshot snapshot,PutioReconciliation.Plan plan,List<PutioReconciliation.Existing> existing,Set<Long> initialMediaIds){this.session=session;this.source=source;this.snapshot=snapshot;this.plan=plan;this.existing=existing;this.initialMediaIds=initialMediaIds;}
        private static Set<Long> mediaIds(List<PutioReconciliation.Existing> rows){Set<Long> ids=new HashSet<>();for(PutioReconciliation.Existing row:rows)ids.add(row.mediaId);return Collections.unmodifiableSet(ids);}
        int matchedExisting(){int count=0;for(PutioReconciliation.Change change:plan.changes)if(change.mediaId>0&&initialMediaIds.contains(change.mediaId))count++;return count;}
        int newlyImported(){int count=0;for(PutioReconciliation.Change change:plan.changes)if(change.mediaId>0&&!initialMediaIds.contains(change.mediaId))count++;return count;}
    }
    static Review fetch(Context context,PutioReadClient client,long account,long folder,Uri source)throws Exception {
        List<String> rootPath=PutioRootLocation.readPath(context,client,folder);
        Uri currentSource=PutioRootLocation.target(context,account,folder,source,rootPath);
        if(!source.equals(currentSource)){
            PutioReconciliation.Snapshot movedSnapshot=PutioSnapshotReader.read(account+":"+folder,folder,client::list);
            if(!movedSnapshot.complete())throw new PutioReadClient.Unavailable(movedSnapshot.failure(),0);
            if(!rootPath.equals(PutioRootLocation.readPath(context,client,folder)))throw new PutioReadClient.Unavailable(PutioReconciliation.Failure.INVALID_PAGE,0);
            Review moved=PutioReassignment.apply(context,account,folder,folder,currentSource,PutioAssociationStore.DisconnectChoice.KEEP_INACTIVE,movedSnapshot);
            PutioRootLocation.remember(context,account,folder,currentSource,rootPath);return moved;
        }
        PutioAssociationStore.Session session;try(PutioAssociationStore store=new PutioAssociationStore(context)){store.prepare(account,folder,source);session=store.begin(account,folder);}
        PutioReconciliation.Snapshot snapshot=PutioSnapshotReader.read(session.scope,folder,client::list);
        if(!snapshot.complete())throw new PutioReadClient.Unavailable(snapshot.failure(),0);
        if(!rootPath.equals(PutioRootLocation.readPath(context,client,folder)))throw new PutioReadClient.Unavailable(PutioReconciliation.Failure.INVALID_PAGE,0);
        try(PutioAssociationStore store=new PutioAssociationStore(context)){
            List<PutioReconciliation.Existing> rows=new PutioLibraryBridge(context).existing(source,store.links(account,folder));
            PutioRootLocation.remember(context,account,folder,source,rootPath);
            return new Review(session,source,snapshot,PutioReconciliation.plan(snapshot,rows),rows);
        }
    }
    /** Explicit review choices map a provider ID to one existing ID; zero means Keep Separate. */
    static Review apply(Context context,Review review,Map<Long,Long> choices){
        return apply(context,review,choices,new PutioLibraryBridge(context));
    }
    static Review apply(Context context,Review review,Map<Long,Long> choices,Library library){
        boolean changed=false;
        try(PutioAssociationStore store=new PutioAssociationStore(context)){
            synchronized(store){synchronized(ProviderDiscoveryGate.LOCK){
                if(!store.isCurrent(review.session)||!review.snapshot.complete())throw new IllegalStateException("Sync has been superseded");
                List<PutioReconciliation.Existing> rows=library.existing(review.source,store.links(review.session.accountId,review.session.folderId));
                PutioReconciliation.Plan plan=PutioReconciliation.plan(review.snapshot,rows);
                Map<Long,PutioReconciliation.Existing> byMedia=new HashMap<>();for(PutioReconciliation.Existing row:rows)byMedia.put(row.mediaId,row);
                Set<Long> claimed=new HashSet<>();
                for(PutioReconciliation.Change change:plan.changes){
                    if(Thread.currentThread().isInterrupted())throw new IllegalStateException("Sync interrupted");
                    long media=change.mediaId;
                    if(change.decision==PutioReconciliation.Decision.NEEDS_REVIEW){
                        Long decision=choices.get(change.file.id);if(decision==null)continue;media=decision;
                        if(media!=0&&!change.candidates.contains(media))throw new IllegalArgumentException("Review candidate is no longer available");
                    }
                    if(media==0){media=library.insert(review.source,change.file);changed=true;}
                    if(!claimed.add(media))throw new IllegalArgumentException("Two provider files cannot own one media record");
                    PutioReconciliation.Existing old=byMedia.get(media);
                    if(old!=null){if(old.providerFileId>0&&old.providerFileId!=change.file.id)throw new IllegalArgumentException("Media already belongs to another file");library.relocate(review.source,old,change.file);rows.remove(old);}
                    PutioReconciliation.Existing linked=new PutioReconciliation.Existing(media,change.file.relativePath,change.file.size,change.file.id);rows.add(linked);byMedia.put(media,linked);
                }
                if(!store.commit(review.session,review.snapshot,rows))throw new IllegalStateException("Sync has been superseded");
                PutioReconciliation.Plan finalPlan=PutioReconciliation.plan(review.snapshot,rows);
                boolean unresolved=false;for(PutioReconciliation.Change change:finalPlan.changes)if(change.decision==PutioReconciliation.Decision.NEEDS_REVIEW||change.decision==PutioReconciliation.Decision.NEW_FILE)unresolved=true;
                if(!unresolved&&store.ownership(review.session.accountId,review.session.folderId)==PutioAssociationStore.Ownership.PREPARING&&!store.activate(review.session))throw new IllegalStateException("Discovery hand-off could not finish");
                context.getSharedPreferences("putio-library-selection-v1",0).edit().putLong("sync:"+review.session.scope,System.currentTimeMillis()).putString("status:"+review.session.scope,unresolved||!finalPlan.missingMediaIds.isEmpty()?"Needs review":"Up to date").apply();
                if(changed)library.enrich();
                return new Review(review.session,review.source,review.snapshot,finalPlan,rows,review.initialMediaIds);
            }}
        }
    }
    private PutioSync(){}
}
