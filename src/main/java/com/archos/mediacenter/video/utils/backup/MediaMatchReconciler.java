package com.archos.mediacenter.video.utils.backup;

import android.content.Context;
import android.os.SystemClock;
import androidx.preference.PreferenceManager;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import org.json.JSONObject;

/** Bounded metadata preparation runs separately from Library queries and viewing-history writes. */
public final class MediaMatchReconciler {
    private static final ScheduledExecutorService WORKER=Executors.newSingleThreadScheduledExecutor(task->{Thread thread=new Thread(task,"SupernovaRestoredMatches");thread.setDaemon(true);return thread;});
    private static final AtomicBoolean RUNNING=new AtomicBoolean();private static volatile long retryAt;
    static boolean pending(Context context){
        try{for(var entry:PreferenceManager.getDefaultSharedPreferences(context).getAll().entrySet()){
            if(entry.getKey().equals(PortableMediaMatches.PENDING)&&entry.getValue() instanceof String&&PortableMediaMatches.pendingPayload(new JSONObject((String)entry.getValue())).getJSONArray("rows").length()>0)return true;
            if(MediaMatchIntents.owns(entry.getKey())&&entry.getValue() instanceof String&&new JSONObject((String)entry.getValue()).getBoolean("pending"))return true;
        }}catch(Exception invalid){com.archos.mediacenter.video.diagnostics.Diagnostics.error("match_restore_pending_state",invalid);}return false;
    }
    public static void request(Context context){
        Context app=context.getApplicationContext();if(SystemClock.elapsedRealtime()<retryAt||!pending(app)||!RUNNING.compareAndSet(false,true))return;
        WORKER.execute(()->{boolean changed=false;try{changed=new PreferenceRestoreTransaction(app).reconcileMatches(CanonicalMatchMaterial.PROVIDER,()->Thread.currentThread().isInterrupted());}
            catch(Exception unavailable){com.archos.mediacenter.video.diagnostics.Diagnostics.error("match_restore_pending",unavailable);}
            finally{retryAt=changed?0:SystemClock.elapsedRealtime()+30000;RUNNING.set(false);}
            if(changed&&pending(app))WORKER.schedule(()->request(app),250,TimeUnit.MILLISECONDS);
        });
    }
    private MediaMatchReconciler(){}
}
