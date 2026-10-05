package com.archos.mediacenter.video.streaming.putio;

import android.content.Context;
import com.archos.mediacenter.video.diagnostics.Diagnostics;
import com.archos.mediacenter.video.streaming.StreamingRepository;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;

/** Uses the existing manual/foreground scan schedule, without Activity ownership or new timers. */
public final class PutioSyncScheduler {
    private static final AtomicBoolean BUSY=new AtomicBoolean();
    public static void request(Context context){request(context,"unknown");}
    public static void request(Context context,String origin){
        final String trigger=Arrays.asList("manual","startup","resume","scheduled").contains(origin)?origin:"unknown";
        if(!BUSY.compareAndSet(false,true))return;Context app=context.getApplicationContext();
        StreamingRepository.IO.submit(()->{try{
            String token=new PutioTokenStore(app).read();if(token==null)return;
            sync(app,trigger,new PutioReadClient(token));
        }catch(Exception failure){
            Diagnostics.event("putio_sync_incomplete","reason","LOCAL_UNAVAILABLE","trigger",trigger);
        }finally{BUSY.set(false);}});
    }
    /** Runs on the owning worker so transport operations inherit this sync's correlation. */
    static void sync(Context app,String trigger,PutioReadClient client){
        long started=android.os.SystemClock.elapsedRealtime();String operation=Diagnostics.operation("putio_sync");
        try(Diagnostics.OperationScope ignored=Diagnostics.operationScope(operation)){
            Diagnostics.event("putio_sync_started","operation_id",operation,"trigger",trigger);
            checkInterrupted();PutioReadClient.Account account=client.account();checkInterrupted();
            List<PutioAssociationStore.Scope> scopes;try(PutioAssociationStore store=new PutioAssociationStore(app)){scopes=store.scopes(account.id);}
            int synced=0,review=0;
            for(PutioAssociationStore.Scope scope:scopes){
                checkInterrupted();
                if(scope.ownership!=PutioAssociationStore.Ownership.API)continue;
                PutioSync.Review result=PutioSync.apply(app,PutioSync.fetch(app,client,account.id,scope.folderId,scope.source),Collections.emptyMap());
                synced++;for(PutioReconciliation.Change change:result.plan.changes)if(change.decision==PutioReconciliation.Decision.NEEDS_REVIEW)review++;
            }
            checkInterrupted();Diagnostics.event("putio_sync_complete","operation_id",operation,"scopes",synced,"review",review);
        }catch(Exception failure){
            if(failure instanceof InterruptedException||Thread.currentThread().isInterrupted()){
                Thread.currentThread().interrupt();Diagnostics.event("putio_sync_cancelled","operation_id",operation,"trigger",trigger);
            }else{
                String reason=failure instanceof PutioReadClient.Unavailable?((PutioReadClient.Unavailable)failure).reason.name():"LOCAL_UNAVAILABLE";
                Diagnostics.event("putio_sync_incomplete","operation_id",operation,"reason",reason);
            }
        }finally{Diagnostics.finishOperation(operation,"putio_sync",started);}
    }
    private static void checkInterrupted()throws InterruptedException{if(Thread.currentThread().isInterrupted())throw new InterruptedException();}
    private PutioSyncScheduler(){}
}
