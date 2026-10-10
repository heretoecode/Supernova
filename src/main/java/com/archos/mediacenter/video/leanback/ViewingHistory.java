package com.archos.mediacenter.video.leanback;

import android.content.Context;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import androidx.preference.PreferenceManager;
import com.archos.mediacenter.utils.videodb.VideoDbInfo;
import com.archos.mediacenter.video.browser.adapters.object.*;
import com.archos.mediacenter.video.player.PrivateMode;
import java.util.*;
import java.util.concurrent.*;

/** Durable identity history, independent of file rows, cache eviction and source availability. */
public final class ViewingHistory {
    public static final String DATABASE="supernova-viewing-history.db", TRACK="supernova_track_viewing_history", RESUME="supernova_save_resume";
    private static final ExecutorService WRITER=Executors.newSingleThreadExecutor();
    private static final Object LOCK=new Object();
    public static boolean tracking(Context c){return PreferenceManager.getDefaultSharedPreferences(c).getBoolean(TRACK,true);}
    public static boolean resumeEnabled(Context c){return PreferenceManager.getDefaultSharedPreferences(c).getBoolean(RESUME,true);}
    private static final class Store extends SQLiteOpenHelper {
        Store(Context c){super(c,DATABASE,null,1);}
        public void onCreate(SQLiteDatabase db){db.execSQL("CREATE TABLE history(identity TEXT PRIMARY KEY, completed INTEGER NOT NULL, started INTEGER NOT NULL, position INTEGER NOT NULL, duration INTEGER NOT NULL, played INTEGER NOT NULL)");}
        public void onUpgrade(SQLiteDatabase db,int old,int next){throw new IllegalStateException("Unsupported history schema");}
    }
    public static String identity(boolean episode,String provider,int season,int number,String path){
        if(provider!=null&&provider.matches("[1-9][0-9]*"))return episode&&season>=0&&number>0?"tmdb:tv:"+provider+":"+season+":"+number:episode?null:"tmdb:movie:"+provider;
        // No weak title/year guesses. Unmatched files retain a private exact-location fingerprint.
        if(path==null||path.isEmpty())return null;
        try{android.net.Uri uri=android.net.Uri.parse(path);String safe=uri.buildUpon().encodedAuthority(uri.getHost()==null?"":uri.getHost()+(uri.getPort()>0?":"+uri.getPort():"")).clearQuery().fragment(null).build().toString();byte[] digest=java.security.MessageDigest.getInstance("SHA-256").digest(safe.getBytes(java.nio.charset.StandardCharsets.UTF_8));StringBuilder key=new StringBuilder("file:");for(byte b:digest)key.append(String.format(java.util.Locale.ROOT,"%02x",b&255));return key.toString();}catch(Exception unavailable){return null;}
    }
    public static String identity(PreviewLibraryLoader.Entry e){if(!(e.media instanceof Video))return null;Video v=(Video)e.media;Episode ep=v instanceof Episode?(Episode)v:null;return identity(ep!=null,e.onlineId>0?String.valueOf(e.onlineId):null,ep==null?-1:ep.getSeasonNumber(),ep==null?-1:ep.getEpisodeNumber(),v.getFilePath());}
    public static final class Record implements java.io.Serializable {
        public final boolean completed,started;public final int position,duration;public final long played;
        Record(boolean c,boolean s,int p,int d,long t){completed=c;started=s;position=p;duration=d;played=t;}
    }
    public static Map<String,Record> read(Context c){synchronized(LOCK){Map<String,Record> records=new HashMap<>();try(Store helper=new Store(c);Cursor rows=helper.getReadableDatabase().query("history",null,null,null,null,null,null)){while(rows.moveToNext())records.put(rows.getString(0),new Record(rows.getInt(1)!=0,rows.getInt(2)!=0,rows.getInt(3),rows.getInt(4),rows.getLong(5)));}return records;}}
    private static void write(Context c,String key,boolean complete,boolean started,int position,int duration,long played,boolean track,boolean resume){
        if(key==null||(!track&&!resume))return;
        synchronized(LOCK){try(Store helper=new Store(c)){SQLiteDatabase db=helper.getWritableDatabase();ContentValues values=new ContentValues();values.put("identity",key);
            Record previous=null;try(Cursor row=db.query("history",null,"identity=?",new String[]{key},null,null,null)){if(row.moveToFirst())previous=new Record(row.getInt(1)!=0,row.getInt(2)!=0,row.getInt(3),row.getInt(4),row.getLong(5));}
            values.put("completed",track?complete:previous!=null&&previous.completed);values.put("started",track?started:previous!=null&&previous.started);
            values.put("position",resume?position:previous==null?0:previous.position);values.put("duration",Math.max(0,duration));values.put("played",played);
            db.insertWithOnConflict("history",null,values,SQLiteDatabase.CONFLICT_REPLACE);
        }}
    }
    public static void record(Context c,VideoDbInfo info,boolean complete){if(PrivateMode.isActive())return;Context app=c.getApplicationContext();String key=identity(info.isShow,info.isShow?info.scraperShowId:info.scraperMovieId,info.scraperSeasonNr,info.scraperEpisodeNr,info.uri==null?null:info.uri.toString());int position=info.resume,duration=info.duration;long played=info.lastTimePlayed;boolean track=tracking(c),resume=resumeEnabled(c);WRITER.execute(()->{try{write(app,key,complete,true,position,duration,played,track,resume);}catch(RuntimeException failure){com.archos.mediacenter.video.diagnostics.Diagnostics.error("history_write_failed",failure);}});}
    /** Existing history migrates once per identity; turning tracking off never erases it. Worker only. */
    public static Map<String,Record> reconcile(Context c,List<PreviewLibraryLoader.Entry> entries){Map<String,Record> records=read(c);if(PrivateMode.isActive())return records;for(PreviewLibraryLoader.Entry e:entries){Video v=(Video)e.media;String key=identity(e);Record previous=records.get(key);if(tracking(c)&&previous==null&&(v.getLastPlayed()>0||PreviewSeriesJourney.completed(v)))write(c,key,PreviewSeriesJourney.completed(v),true,v.getResumeMs(),v.getDurationMs(),v.getLastPlayed(),true,true);else if(previous!=null&&v.getLastPlayed()==0&&resumeEnabled(c)&&previous.position!=0)v.setResumeMs(previous.position);}return read(c);}
    public static void clear(Context c,Runnable done){Context app=c.getApplicationContext();WRITER.execute(()->{synchronized(LOCK){try(Store helper=new Store(app)){helper.getWritableDatabase().delete("history",null,null);}}new android.os.Handler(android.os.Looper.getMainLooper()).post(done);});}
    public static void export(Context c,java.io.File destination)throws java.io.IOException{synchronized(LOCK){try(Store helper=new Store(c)){SQLiteDatabase db=helper.getWritableDatabase();db.rawQuery("PRAGMA wal_checkpoint(FULL)",null).close();try(java.io.InputStream in=new java.io.FileInputStream(c.getDatabasePath(DATABASE));java.io.OutputStream out=new java.io.FileOutputStream(destination)){byte[] bytes=new byte[65536];int n;while((n=in.read(bytes))!=-1)out.write(bytes,0,n);}}}}
    private ViewingHistory(){}
}
