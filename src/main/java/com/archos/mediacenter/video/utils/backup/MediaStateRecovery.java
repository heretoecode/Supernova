package com.archos.mediacenter.video.utils.backup;

import android.content.Context;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import com.archos.mediaprovider.video.*;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.*;
import java.util.function.BooleanSupplier;
import org.json.*;

/** Private selected-column media images. Never copies a media DB, paths, artwork or account stores. */
public final class MediaStateRecovery {
    private static final String PIN=VideoStore.Video.VideoColumns.NOVA_PINNED;
    private static final String HIDDEN=VideoStore.Video.VideoColumns.ARCHOS_HIDDEN_BY_USER;
    private static boolean number(Object value){return value instanceof Integer||value instanceof Long;}
    private static long integer(Object value,long minimum)throws JSONException {
        if(!number(value)||((Number)value).longValue()<minimum)throw new JSONException("Invalid media recovery integer");return ((Number)value).longValue();
    }
    public static void validate(JSONObject image)throws JSONException {
        long schema=integer(image.get("schema"),1);
        if((schema!=1&&schema!=2)||image.length()!=(schema==1?4:5)||!(image.get("exists") instanceof Boolean)||!image.has("pins")||!image.has("hidden")||schema==2&&!image.has("playlists")||
                image.isNull("pins")&&image.isNull("hidden")&&(schema==1||image.isNull("playlists")))throw new JSONException("Unsupported media recovery image");
        if(schema==2){if(image.isNull("playlists"))throw new JSONException("Missing selected playlist scope");PlaylistStateRecovery.validate(image.getJSONObject("playlists"));if(!image.getBoolean("exists")&&!image.getJSONObject("playlists").toString().equals(PlaylistStateRecovery.empty().toString()))throw new JSONException("Invalid absent playlist scope");}
        for(String category:List.of("pins","hidden"))if(!image.isNull(category)) {
            JSONArray rows=image.getJSONArray(category);if(rows.length()>100000||!image.getBoolean("exists")&&rows.length()!=0)throw new JSONException("Invalid media recovery scope");
            Set<String> seen=new HashSet<>();for(int i=0;i<rows.length();i++) {
                JSONObject row=rows.getJSONObject(i);long id=integer(row.get("id"),1);String key=Long.toString(id);
                if(category.equals("pins")) {
                    if(row.length()!=4||!(row.get("kind") instanceof String)||!List.of("MOVIE","TV").contains(row.getString("kind"))||!row.has("provider")||!row.has("value"))throw new JSONException("Invalid pin recovery fields");
                    if(!row.isNull("provider"))integer(row.get("provider"),-1);if(!row.isNull("value"))integer(row.get("value"),0);key=row.getString("kind")+":"+id;
                }else {
                    if(row.length()!=3||!row.has("value")||!row.isNull("value")&&!(row.get("value") instanceof Boolean)||
                            !(row.get("guard") instanceof String)||!row.getString("guard").matches("[a-f0-9]{64}"))throw new JSONException("Invalid hidden recovery fields");
                }
                if(!seen.add(key))throw new JSONException("Duplicate media recovery target");
            }
        }
    }
    private static Object integer(Cursor rows,int index,long minimum)throws IOException {
        if(rows.isNull(index))return JSONObject.NULL;
        if(rows.getType(index)!=Cursor.FIELD_TYPE_INTEGER||rows.getLong(index)<minimum)throw new IOException("Invalid indexed media recovery value");return rows.getLong(index);
    }
    private static JSONObject capture(SQLiteDatabase database,boolean pins,boolean hidden,boolean playlists)throws IOException,JSONException {
        if(database.getVersion()!=VideoOpenHelper.getDatabaseVersion())throw new IOException("Media schema is not compatible with this application");
        JSONArray pinRows=new JSONArray(),hiddenRows=new JSONArray();
        if(pins)for(String kind:List.of("MOVIE","TV")) {
            String table=kind.equals("MOVIE")?ScraperTables.MOVIE_TABLE_NAME:ScraperTables.SHOW_TABLE_NAME;
            String provider=kind.equals("MOVIE")?ScraperStore.Movie.ONLINE_ID:ScraperStore.Show.ONLINE_ID;
            try(Cursor rows=database.query(table,new String[]{"_id",provider,PIN},null,null,null,null,"_id ASC")) {
                while(rows.moveToNext()) {
                    if(pinRows.length()>=100000)throw new IOException("Media pin recovery exceeds supported limits");Object id=integer(rows,0,1);
                    if(id==JSONObject.NULL)throw new IOException("Indexed media identity required");
                    pinRows.put(new JSONObject().put("kind",kind).put("id",id).put("provider",integer(rows,1,-1)).put("value",integer(rows,2,0)));
                }
            }
        }
        if(hidden)try(Cursor rows=database.query(VideoOpenHelper.FILES_TABLE_NAME,new String[]{"_id",HIDDEN,"_data",
                VideoStore.Video.VideoColumns.ARCHOS_MEDIA_SCRAPER_TYPE,VideoStore.Video.VideoColumns.ARCHOS_MEDIA_SCRAPER_ID},
                "media_type = ?",new String[]{"3"},null,null,"_id ASC")) {
            while(rows.moveToNext()) {
                if(hiddenRows.length()>=100000)throw new IOException("Hidden recovery exceeds supported limits");Object id=integer(rows,0,1),value=integer(rows,1,0);
                if(id==JSONObject.NULL||value!=JSONObject.NULL&&((Number)value).longValue()>1||!rows.isNull(2)&&rows.getType(2)!=Cursor.FIELD_TYPE_STRING)
                    throw new IOException("Invalid indexed hidden state");
                JSONArray identity=new JSONArray().put(rows.isNull(2)?JSONObject.NULL:rows.getString(2)).put(integer(rows,3,-1)).put(integer(rows,4,-1));
                StringBuilder guard=new StringBuilder();try{for(byte part:MessageDigest.getInstance("SHA-256").digest(identity.toString().getBytes(StandardCharsets.UTF_8)))guard.append(String.format(Locale.ROOT,"%02x",part&255));}
                catch(java.security.NoSuchAlgorithmException unavailable){throw new IOException("Media identity validation unavailable",unavailable);}
                hiddenRows.put(new JSONObject().put("id",id).put("value",value==JSONObject.NULL?JSONObject.NULL:((Number)value).longValue()==1).put("guard",guard.toString()));
            }
        }
        JSONObject result=new JSONObject().put("schema",playlists?2:1).put("exists",true).put("pins",pins?pinRows:JSONObject.NULL).put("hidden",hidden?hiddenRows:JSONObject.NULL);if(playlists)result.put("playlists",PlaylistStateRecovery.capture(database));validate(result);return result;
    }
    public static JSONObject capture(Context context,boolean pins,boolean hidden)throws IOException,JSONException{return capture(context,pins,hidden,false);}
    public static JSONObject capture(Context context,boolean pins,boolean hidden,boolean playlists)throws IOException,JSONException {
        if(!pins&&!hidden&&!playlists)throw new JSONException("No media recovery category selected");File file=VideoOpenHelper.getDatabaseFile(context);
        if(!file.exists()){JSONObject image=new JSONObject().put("schema",playlists?2:1).put("exists",false).put("pins",pins?new JSONArray():JSONObject.NULL).put("hidden",hidden?new JSONArray():JSONObject.NULL);if(playlists)image.put("playlists",PlaylistStateRecovery.empty());validate(image);return image;}
        try(SQLiteDatabase database=SQLiteDatabase.openDatabase(file.getPath(),null,SQLiteDatabase.OPEN_READONLY)){database.execSQL("SAVEPOINT supernova_media_recovery_read");try{return capture(database,pins,hidden,playlists);}finally{database.execSQL("RELEASE SAVEPOINT supernova_media_recovery_read");}}
        catch(RuntimeException invalid){throw new IOException("Media recovery inventory unavailable",invalid);}
    }
    public static JSONObject playlistAfter(JSONObject before,PlaylistStateRecovery.Prepared prepared)throws JSONException {
        JSONObject after=new JSONObject(before.toString());after.put("playlists",prepared.after);validatePair(before,after);return after;
    }
    public static JSONObject pinAfter(JSONObject before,PortablePinnedTitles.Prepared prepared)throws JSONException {
        JSONObject after=new JSONObject(before.toString());JSONArray rows=after.getJSONArray("pins");for(int i=0;i<rows.length();i++) {
            JSONObject row=rows.getJSONObject(i);Long value=prepared.pins.get(row.getString("kind")+":"+row.getLong("id"));if(value==null)throw new JSONException("Missing receiving pin target");row.put("value",value);
        }validate(after);return after;
    }
    public static JSONObject hiddenAfter(JSONObject before,PortableHiddenItems.Prepared prepared)throws JSONException {
        JSONObject after=new JSONObject(before.toString());JSONArray rows=after.getJSONArray("hidden");for(int i=0;i<rows.length();i++) {
            JSONObject row=rows.getJSONObject(i);Boolean value=prepared.hidden.get(row.getLong("id"));if(value==null)throw new JSONException("Missing receiving hidden target");row.put("value",value);
        }validate(after);return after;
    }
    public static List<PortablePinnedTitles.Title> pinInventory(JSONObject image)throws JSONException {
        validate(image);List<PortablePinnedTitles.Title> titles=new ArrayList<>();JSONArray rows=image.getJSONArray("pins");for(int i=0;i<rows.length();i++) {
            JSONObject row=rows.getJSONObject(i);titles.add(new PortablePinnedTitles.Title(PortablePinnedTitles.Kind.valueOf(row.getString("kind")),row.getLong("id"),
                    row.isNull("provider")?0:Math.max(0,row.getLong("provider")),row.isNull("value")?0:row.getLong("value")));
        }return Collections.unmodifiableList(titles);
    }
    public static Map<Long,Boolean> hiddenInventory(JSONObject image)throws JSONException {
        validate(image);Map<Long,Boolean> state=new LinkedHashMap<>();JSONArray rows=image.getJSONArray("hidden");for(int i=0;i<rows.length();i++) {
            JSONObject row=rows.getJSONObject(i);state.put(row.getLong("id"),!row.isNull("value")&&row.getBoolean("value"));
        }return Collections.unmodifiableMap(state);
    }
    private static JSONObject guards(JSONObject image)throws JSONException {
        JSONObject guard=new JSONObject(image.toString());for(String category:List.of("pins","hidden"))if(!guard.isNull(category)) {
            JSONArray rows=guard.getJSONArray(category);for(int i=0;i<rows.length();i++)rows.getJSONObject(i).remove("value");
        }if(guard.getInt("schema")==2)guard.put("playlists",JSONObject.NULL);return guard;
    }
    public static void validatePair(JSONObject before,JSONObject after)throws JSONException {
        validate(before);validate(after);if(!guards(before).toString().equals(guards(after).toString()))throw new JSONException("Conflicting media recovery identity scopes");
        if(before.getInt("schema")==2&&!before.isNull("playlists"))PlaylistStateRecovery.validatePair(before.getJSONObject("playlists"),after.getJSONObject("playlists"));
    }
    public static final class Session implements AutoCloseable {
        private SQLiteDatabase database;private final JSONObject before;private final Context context;private boolean ended;
        private Session(Context context,JSONObject image,JSONObject redo,boolean recovery)throws IOException,JSONException {
            this(context,image,redo,recovery,null);
        }
        private Session(Context context,JSONObject image,JSONObject redo,boolean recovery,JSONObject canonical)throws IOException,JSONException {
            this.context=context;validate(image);before=new JSONObject(image.toString());File file=VideoOpenHelper.getDatabaseFile(context);
            if(!image.getBoolean("exists")){if(file.exists())throw new IOException("Media store appeared after restore preview");return;}
            if(!file.exists())throw new IOException("Media store is unavailable for recovery");
            database=SQLiteDatabase.openDatabase(file.getPath(),null,SQLiteDatabase.OPEN_READWRITE);
            try {
                database.setForeignKeyConstraintsEnabled(true);
                database.beginTransaction();JSONObject current=capture(database,!image.isNull("pins"),!image.isNull("hidden"),image.getInt("schema")==2&&!image.isNull("playlists"));
                if(canonical==null){
                    if(!(recovery?guards(current).toString().equals(guards(image).toString()):current.toString().equals(image.toString())))throw new IOException("Media inventory changed; refusing stale restore or recovery");
                }else{
                    CanonicalNativeRecovery.validate(canonical);String state=CanonicalNativeRecovery.fingerprint(database,canonical.getJSONArray("files"),()->false);
                    if(!state.equals(canonical.getString("before"))&&!state.equals(canonical.getString("after")))throw new IOException("Canonical metadata changed independently; recovery requires inspection");
                }
                if(canonical==null&&recovery&&image.getInt("schema")==2&&!image.isNull("playlists")){String state=current.getJSONObject("playlists").toString();if(!state.equals(image.getJSONObject("playlists").toString())&&(redo==null||!state.equals(redo.getJSONObject("playlists").toString())))throw new IOException("Playlists changed independently; recovery requires inspection");}
            }catch(IOException|JSONException|RuntimeException failure){close();throw failure;}
        }
        public void write(JSONObject image,BooleanSupplier cancel)throws IOException,JSONException {
            validatePair(before,image);if(database==null)return;
            for(String category:List.of("pins","hidden"))if(!image.isNull(category)) {
                JSONArray rows=image.getJSONArray(category);for(int i=0;i<rows.length();i++) {
                    if(cancel.getAsBoolean()||Thread.currentThread().isInterrupted())throw new InterruptedIOException("Media restore cancelled");JSONObject row=rows.getJSONObject(i);ContentValues value=new ContentValues();String column=category.equals("pins")?PIN:HIDDEN;
                    if(row.isNull("value"))value.putNull(column);else if(category.equals("pins"))value.put(column,row.getLong("value"));else value.put(column,row.getBoolean("value")?1:0);
                    String table=category.equals("hidden")?VideoOpenHelper.FILES_TABLE_NAME:row.getString("kind").equals("MOVIE")?ScraperTables.MOVIE_TABLE_NAME:ScraperTables.SHOW_TABLE_NAME;
                    if(database.update(table,value,"_id = ?",new String[]{Long.toString(row.getLong("id"))})!=1)throw new IOException("Media restore target unavailable");
                }
            }
            if(image.getInt("schema")==2&&!image.isNull("playlists"))PlaylistStateRecovery.write(database,image.getJSONObject("playlists"),cancel);
        }
        /** Stage on the same connection as selected pins/hidden/playlists. Caller seals after all native writes. */
        public JSONObject stageCanonical(Collection<Long> files,List<com.archos.mediascraper.OwnedCanonicalBatch> batches,BooleanSupplier cancel)throws IOException,JSONException {
            if(database==null||ended)throw new IOException("Canonical receiving media store is unavailable");
            JSONObject record=CanonicalNativeRecovery.begin(database,files,batches,cancel);
            for(var batch:batches)CanonicalMatchExecutor.apply(context,database,batch,cancel);
            return record;
        }
        public void sealCanonical(JSONObject record,BooleanSupplier cancel)throws IOException,JSONException {
            if(database==null||ended)throw new IOException("Canonical receiving transaction is unavailable");CanonicalNativeRecovery.seal(database,record,cancel);
        }
        public void commit()throws IOException {if(database!=null&&!ended){database.setTransactionSuccessful();database.endTransaction();ended=true;}}
        @Override public void close(){if(database!=null){try{if(!ended&&database.inTransaction())database.endTransaction();}finally{database.close();database=null;}}}
    }
    public static Session begin(Context context,JSONObject image,boolean recovery)throws IOException,JSONException {return new Session(context,image,null,recovery);}
    public static void recover(Context context,JSONObject before,JSONObject after,boolean complete)throws IOException,JSONException {
        validatePair(before,after);
        try(Session session=new Session(context,before,after,true)){session.write(complete?after:before,()->false);session.commit();}
    }
    /** Native commit is atomic across canonical matches and selected pin/hidden/playlist state. */
    public static void recoverCanonical(Context context,JSONObject before,JSONObject after,JSONObject canonical,boolean complete)throws IOException,JSONException {
        validatePair(before,after);CanonicalNativeRecovery.validate(canonical);
        try(Session session=new Session(context,before,after,true,canonical)){
            if(session.database==null)throw new IOException("Canonical recovery media store is unavailable");
            boolean replayed=CanonicalNativeRecovery.replay(context,session.database,canonical,complete,()->false);
            if(!complete||replayed)session.write(complete?after:before,()->false);
            if(complete)CanonicalNativeRecovery.verifyComplete(session.database,canonical,()->false);
            session.commit();
        }
    }
    private MediaStateRecovery(){}
}
