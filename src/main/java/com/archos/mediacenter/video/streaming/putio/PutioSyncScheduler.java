package com.archos.mediacenter.video.streaming.putio;

import android.content.Context;
import com.archos.mediacenter.video.diagnostics.Diagnostics;
import com.archos.mediacenter.video.streaming.StreamingRepository;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;

/** Uses the existing manual/foreground scan schedule, without Activity ownership or new timers. */
public final class PutioSyncScheduler {
    private static final AtomicBoolean BUSY=new AtomicBoolean();
    public static void request(Context context){
        if(!BUSY.compareAndSet(false,true))return;Context app=context.getApplicationContext();
        StreamingRepository.IO.submit(()->{String operation="";try{
            String token=new PutioTokenStore(app).read();if(token==null)return;
            operation=Diagnostics.operation("putio_sync");PutioReadClient client=new PutioReadClient(token);PutioReadClient.Account account=client.account();
            List<PutioAssociationStore.Scope> scopes;try(PutioAssociationStore store=new PutioAssociationStore(app)){scopes=store.scopes(account.id);}
            int synced=0,review=0;
            for(PutioAssociationStore.Scope scope:scopes){
                if(Thread.currentThread().isInterrupted())return;
                if(scope.ownership!=PutioAssociationStore.Ownership.API)continue;
                PutioSync.Review result=PutioSync.apply(app,PutioSync.fetch(app,client,account.id,scope.folderId,scope.source),Collections.emptyMap());
                synced++;for(PutioReconciliation.Change change:result.plan.changes)if(change.decision==PutioReconciliation.Decision.NEEDS_REVIEW)review++;
            }
            Diagnostics.event("putio_sync_complete","operation_id",operation,"scopes",synced,"review",review);
        }catch(Exception failure){
            String reason=failure instanceof PutioReadClient.Unavailable?((PutioReadClient.Unavailable)failure).reason.name():"LOCAL_UNAVAILABLE";
            Diagnostics.event("putio_sync_incomplete","operation_id",operation,"reason",reason);
        }finally{BUSY.set(false);}});
    }
    private PutioSyncScheduler(){}
}
