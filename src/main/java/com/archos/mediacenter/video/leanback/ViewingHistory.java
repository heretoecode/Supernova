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
    private static final ExecutorService WRITER=Executors.newSingleThreadExecutor(r->{Thread worker=new Thread(r,"SupernovaViewingHistory");worker.setDaemon(true);return worker;});
    public static final Object LOCK=new Object();
    /** Called by backup/restore workers, never the main thread. */
    public static void awaitWrites() throws java.io.IOException {
        try { WRITER.submit(()->{}).get(15,TimeUnit.SECONDS); }
        catch(Exception failure){if(failure instanceof InterruptedException)Thread.currentThread().interrupt();throw new java.io.IOException("Viewing history is busy; retry the operation",failure);}
    }
    public static boolean tracking(Context c){return PreferenceManager.getDefaultSharedPreferences(c).getBoolean(TRACK,true);}
    public static boolean resumeEnabled(Context c){return PreferenceManager.getDefaultSharedPreferences(c).getBoolean(RESUME,true);}
    private static final class Store extends SQLiteOpenHelper {
        Store(Context c){super(c,DATABASE,null,1);}
        public void onCreate(SQLiteDatabase db){db.execSQL("CREATE TABLE history(identity TEXT PRIMARY KEY, completed INTEGER NOT NULL, started INTEGER NOT NULL, position INTEGER NOT NULL, duration INTEGER NOT NULL, played INTEGER NOT NULL, completed_at INTEGER NOT NULL)");}
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
        public final boolean completed,started;public final int position,duration;public final long played,completedAt;
        Record(boolean c,boolean s,int p,int d,long t,long completion){completed=c;started=s;position=p;duration=d;played=t;completedAt=completion;}
    }
    public static Map<String,Record> read(Context c){synchronized(LOCK){Map<String,Record> records=new HashMap<>();try(Store helper=new Store(c);Cursor rows=helper.getReadableDatabase().query("history",null,null,null,null,null,null)){while(rows.moveToNext())records.put(rows.getString(0),new Record(rows.getInt(1)!=0,rows.getInt(2)!=0,rows.getInt(3),rows.getInt(4),rows.getLong(5),rows.getLong(6)));}return records;}}
    private static void write(Context c,String key,boolean complete,boolean started,int position,int duration,long played,boolean track,boolean resume){
        if(key==null||(!track&&!resume))return;
        synchronized(LOCK){try(Store helper=new Store(c)){SQLiteDatabase db=helper.getWritableDatabase();ContentValues values=new ContentValues();values.put("identity",key);
            Record previous=null;try(Cursor row=db.query("history",null,"identity=?",new String[]{key},null,null,null)){if(row.moveToFirst())previous=new Record(row.getInt(1)!=0,row.getInt(2)!=0,row.getInt(3),row.getInt(4),row.getLong(5),row.getLong(6));}
            values.put("completed_at",previous!=null&&previous.completedAt>0?previous.completedAt:track&&complete?played:0);
            values.put("completed",track?(complete||previous!=null&&previous.completed):previous!=null&&previous.completed);values.put("started",track?started:previous!=null&&previous.started);
            values.put("position",resume?position:previous==null?0:previous.position);values.put("duration",Math.max(0,duration));values.put("played",played);
            db.insertWithOnConflict("history",null,values,SQLiteDatabase.CONFLICT_REPLACE);
        }}
    }
    public static void record(Context c,VideoDbInfo info,boolean complete){if(PrivateMode.isActive())return;Context app=c.getApplicationContext();String key=identity(info.isShow,info.isShow?info.scraperShowId:info.scraperMovieId,info.scraperSeasonNr,info.scraperEpisodeNr,info.uri==null?null:info.uri.toString());int position=!tracking(c)&&complete?0:info.resume,duration=info.duration;long played=Math.max(0,info.lastTimePlayed);boolean track=tracking(c),resume=resumeEnabled(c);WRITER.execute(()->{try{write(app,key,complete,true,position,duration,played,track,resume);}catch(RuntimeException failure){com.archos.mediacenter.video.diagnostics.Diagnostics.error("history_write_failed",failure);}});}
    /** Existing explicit watched/unwatched commands update the same identity, including bulk scopes. */
    public static void manualChange(Context c,String where,String[] arguments,boolean complete){
        if(PrivateMode.isActive())return;Context app=c.getApplicationContext();
        WRITER.execute(()->{try(Cursor rows=app.getContentResolver().query(VideoDbInfo.URI,VideoDbInfo.COLUMNS,where,arguments,null)){
            if(rows==null)return;while(rows.moveToNext()){VideoDbInfo info=VideoDbInfo.fromCursor(rows,false);String key=identity(info.isShow,info.isShow?info.scraperShowId:info.scraperMovieId,info.scraperSeasonNr,info.scraperEpisodeNr,info.uri==null?null:info.uri.toString());if(key==null)continue;
                synchronized(LOCK){try(Store helper=new Store(app)){ContentValues record=new ContentValues();record.put("identity",key);record.put("completed",complete);record.put("started",complete);record.put("position",complete?-2:0);record.put("duration",Math.max(0,info.duration));record.put("played",complete?System.currentTimeMillis()/1000L:0);record.put("completed_at",complete?System.currentTimeMillis()/1000L:0);helper.getWritableDatabase().insertWithOnConflict("history",null,record,SQLiteDatabase.CONFLICT_REPLACE);}}
            }
        }catch(RuntimeException failure){com.archos.mediacenter.video.diagnostics.Diagnostics.error("manual_history_write_failed",failure);}});
    }
    /** Existing history migrates once per identity; turning tracking off never erases it. Worker only. */
    public static Map<String,Record> reconcile(Context c,List<PreviewLibraryLoader.Entry> entries){Map<String,Record> records=read(c);if(PrivateMode.isActive())return records;android.content.SharedPreferences prefs=PreferenceManager.getDefaultSharedPreferences(c);boolean migrate=!prefs.getBoolean("supernova_history_migrated",false);for(PreviewLibraryLoader.Entry e:entries){Video v=(Video)e.media;String key=identity(e);Record previous=records.get(key);if(migrate&&tracking(c)&&previous==null&&(v.getLastPlayed()>0||PreviewSeriesJourney.completed(v)))write(c,key,PreviewSeriesJourney.completed(v),true,v.getResumeMs(),v.getDurationMs(),v.getLastPlayed(),true,true);else if(previous!=null){int position=resumeEnabled(c)?previous.position:0;v.applyIdentityHistory(previous.completed,previous.played,position);e.playedAt=previous.played;}}prefs.edit().putBoolean("supernova_history_migrated",true).commit();return read(c);}
    public static void clear(Context c,Runnable done){Context app=c.getApplicationContext();WRITER.execute(()->{synchronized(LOCK){try(Store helper=new Store(app)){helper.getWritableDatabase().execSQL("UPDATE history SET completed=0, started=0, played=0, completed_at=0, position=CASE WHEN position<0 THEN 0 ELSE position END");android.content.SharedPreferences p=PreferenceManager.getDefaultSharedPreferences(app);android.content.SharedPreferences.Editor edit=p.edit().putBoolean("supernova_history_migrated",true);for(String key:p.getAll().keySet())if(key.startsWith("preview_journey41:"))edit.remove(key);edit.commit();}}new android.os.Handler(android.os.Looper.getMainLooper()).post(done);});}
    public static void export(Context c,java.io.File destination)throws java.io.IOException{awaitWrites();synchronized(LOCK){try(Store helper=new Store(c)){SQLiteDatabase db=helper.getWritableDatabase();try(Cursor checkpoint=db.rawQuery("PRAGMA wal_checkpoint(FULL)",null)){if(!checkpoint.moveToFirst()||checkpoint.getInt(0)!=0)throw new java.io.IOException("Viewing history is busy");}try(java.io.InputStream in=new java.io.FileInputStream(c.getDatabasePath(DATABASE));java.io.OutputStream out=new java.io.FileOutputStream(destination)){byte[] bytes=new byte[65536];int n;while((n=in.read(bytes))!=-1)out.write(bytes,0,n);}}try(SQLiteDatabase copy=SQLiteDatabase.openDatabase(destination.getPath(),null,SQLiteDatabase.OPEN_READWRITE)){android.content.SharedPreferences p=PreferenceManager.getDefaultSharedPreferences(c);boolean history=p.getBoolean("supernova_backup_history",true),resume=p.getBoolean("supernova_backup_resume",true);if(!history)copy.execSQL("UPDATE history SET completed=0,started=0,played=0,completed_at=0,position=CASE WHEN position<0 THEN 0 ELSE position END");if(!resume)copy.execSQL("UPDATE history SET position=0");if(!history)copy.execSQL("DELETE FROM history WHERE position<=0");copy.execSQL("VACUUM");}}}
    public static void validate(java.io.File file)throws java.io.IOException {
        try(SQLiteDatabase db=SQLiteDatabase.openDatabase(file.getPath(),null,SQLiteDatabase.OPEN_READONLY);
            Cursor check=db.rawQuery("PRAGMA quick_check",null)) {
            if(db.getVersion()!=1||!check.moveToFirst()||!"ok".equals(check.getString(0)))throw new java.io.IOException("Unsupported or damaged viewing history");
            try(Cursor tables=db.rawQuery("SELECT name,type FROM sqlite_master WHERE name NOT LIKE 'sqlite_%'",null)) {
                while(tables.moveToNext())if(!tables.getString(1).equals("table")||!Set.of("history","android_metadata").contains(tables.getString(0)))throw new java.io.IOException("Unexpected history database objects");
            }
            Set<String> columns=new HashSet<>();
            try(Cursor schema=db.rawQuery("PRAGMA table_info(history)",null)){while(schema.moveToNext())columns.add(schema.getString(1));}
            if(!columns.equals(Set.of("identity","completed","started","position","duration","played","completed_at")))throw new java.io.IOException("Unexpected history fields");
            try(Cursor rows=db.rawQuery("SELECT identity,completed,started,position,duration,played,completed_at FROM history",null)) {
                while(rows.moveToNext())if(rows.isNull(0)||!rows.getString(0).matches("tmdb:(movie:[1-9][0-9]*|tv:[1-9][0-9]*:[0-9]+:[1-9][0-9]*)|file:[0-9a-f]{64}")||rows.getInt(1)<0||rows.getInt(1)>1||rows.getInt(2)<0||rows.getInt(2)>1||rows.getLong(3)<-2||rows.getLong(3)>Integer.MAX_VALUE||rows.getLong(4)<0||rows.getLong(4)>Integer.MAX_VALUE||rows.getLong(5)<0||rows.getLong(6)<0)throw new java.io.IOException("Invalid viewing history");
            }
        }catch(RuntimeException failure){throw new java.io.IOException("Invalid viewing history database",failure);}
    }
    private ViewingHistory(){}
}
