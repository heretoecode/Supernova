package com.archos.mediacenter.video.leanback;

import android.content.*;
import android.database.Cursor;
import android.database.sqlite.*;
import com.archos.mediacenter.video.streaming.StreamingRepository;
import com.archos.mediacenter.video.diagnostics.Diagnostics;
import java.util.*;
import java.util.concurrent.*;

/** Persistent priority queue. One package section per turn lets foreground work pre-empt background work. */
public final class PreviewEnrichmentQueue {
    public static final int FOREGROUND=0, HOME=10, CURRENT_PAGE=20, BACKGROUND=40;
    private static final ScheduledExecutorService WORK=Executors.newSingleThreadScheduledExecutor(r->new Thread(r,"SupernovaEnrichment"));
    private static Store store;
    private static boolean draining;
    private static ScheduledFuture<?> retry;
    private static final String[] SECTIONS={"","credits","images","videos","recommendations","external_ids","providers"};
    private static final long STALE=7L*24*60*60*1000;
    private static Store store(Context context){if(store==null)store=new Store(context.getApplicationContext());return store;}
    public static void enqueue(Context c,String kind,long id,int priority) {
        if(id<=0||!(kind.equals("movie")||kind.equals("tv")))return;Context app=c.getApplicationContext();
        WORK.execute(()->{offer(store(app).getWritableDatabase(),kind,id,priority);start(app);});
    }
    public static void library(Context c,PreviewLibraryLoader.Snapshot snapshot,int tab) {
        Context app=c.getApplicationContext();
        WORK.execute(()->{SQLiteDatabase db=store(app).getWritableDatabase();db.beginTransaction();try{
            for(PreviewLibraryLoader.Entry entry:snapshot.movies)offer(db,"movie",entry.onlineId,tab==1?CURRENT_PAGE:BACKGROUND);
            for(PreviewLibraryLoader.Entry entry:snapshot.shows)offer(db,"tv",entry.onlineId,tab==2?CURRENT_PAGE:BACKGROUND);
            for(int i=0;i<Math.min(30,snapshot.recent.size());i++){PreviewLibraryLoader.Entry entry=snapshot.recent.get(i);offer(db,entry.media instanceof com.archos.mediacenter.video.browser.adapters.object.Movie?"movie":"tv",entry.onlineId,HOME);}
            db.setTransactionSuccessful();
        }finally{db.endTransaction();}start(app);});
    }
    private static void offer(SQLiteDatabase db,String kind,long id,int priority) {
        if(id<=0)return;String key=kind+":"+id;long now=System.currentTimeMillis();
        db.execSQL("INSERT OR IGNORE INTO jobs(identity,kind,media,priority,stage,next_at,completed_at) VALUES(?,?,?,?,0,0,0)",new Object[]{key,kind,id,priority});
        db.execSQL("UPDATE jobs SET priority=MIN(priority,?),stage=CASE WHEN completed_at>0 AND completed_at<? THEN 0 ELSE stage END,next_at=CASE WHEN completed_at>0 AND completed_at<? THEN 0 ELSE next_at END WHERE identity=?",new Object[]{priority,now-STALE,now-STALE,key});
    }
    private static void start(Context app){if(retry!=null){retry.cancel(false);retry=null;}if(!draining){draining=true;WORK.execute(()->drain(app));}}
    private static void waitForRetry(Context app,SQLiteDatabase db){
        draining=false;
        try(Cursor pending=db.rawQuery("SELECT MIN(next_at) FROM jobs WHERE stage<?",new String[]{String.valueOf(SECTIONS.length)})){
            if(pending.moveToFirst()&&!pending.isNull(0))retry=WORK.schedule(()->start(app),Math.max(250,pending.getLong(0)-System.currentTimeMillis()),TimeUnit.MILLISECONDS);
        }
    }
    private static void drain(Context app) {
        SQLiteDatabase db=store(app).getWritableDatabase();String key,kind;long id;int stage;
        try(Cursor cursor=db.rawQuery("SELECT identity,kind,media,stage FROM jobs WHERE stage<? AND next_at<=? ORDER BY priority,next_at,identity LIMIT 1",new String[]{String.valueOf(SECTIONS.length),String.valueOf(System.currentTimeMillis())})){
            if(!cursor.moveToFirst()){waitForRetry(app,db);return;}key=cursor.getString(0);kind=cursor.getString(1);id=cursor.getLong(2);stage=cursor.getInt(3);
        }
        long started=android.os.SystemClock.elapsedRealtime();String operation=Diagnostics.operation("metadata_package");
        try {
            if(SECTIONS[stage].equals("providers")) {
                if(StreamingRepository.prefs(app).getBoolean(StreamingRepository.ENABLED,false))StreamingRepository.load(app,kind,id,StreamingRepository.country(app));
            } else {
                StreamingRepository.metadata(app,kind,id,SECTIONS[stage]);
                if(!PreviewMetadataCache.fresh(app,kind,id,SECTIONS[stage]))throw new java.io.IOException("Metadata refresh retained stale cache");
            }
            db.execSQL("UPDATE jobs SET stage=?,next_at=0,completed_at=? WHERE identity=?",new Object[]{stage+1,stage+1==SECTIONS.length?System.currentTimeMillis():0,key});
            Diagnostics.event("metadata_package_stage","operation_id",operation,"media_id",key,"section",SECTIONS[stage],"complete",stage+1==SECTIONS.length);
        }catch(Exception failure){db.execSQL("UPDATE jobs SET next_at=? WHERE identity=?",new Object[]{System.currentTimeMillis()+30*60*1000,key});Diagnostics.error("metadata_package_failed",failure);}
        finally{Diagnostics.finishOperation(operation,"metadata_package",started);WORK.schedule(()->drain(app),250,TimeUnit.MILLISECONDS);}
    }
    private static final class Store extends SQLiteOpenHelper {
        Store(Context c){super(c,"preview-enrichment.db",null,1);}
        public void onCreate(SQLiteDatabase db){db.execSQL("CREATE TABLE jobs(identity TEXT PRIMARY KEY,kind TEXT NOT NULL,media INTEGER NOT NULL,priority INTEGER NOT NULL,stage INTEGER NOT NULL,next_at INTEGER NOT NULL,completed_at INTEGER NOT NULL)");}
        public void onUpgrade(SQLiteDatabase db,int oldVersion,int newVersion){throw new IllegalStateException("Explicit enrichment migration required");}
    }
    private PreviewEnrichmentQueue(){}
}
