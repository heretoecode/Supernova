package com.archos.mediacenter.video.utils.backup;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import java.io.*;
import java.util.*;
import java.util.function.BooleanSupplier;
import org.json.*;

/** Private indexed-folder originals/redo. Separate from browser favourites and FTP authentication. */
public final class IndexedSourceStoreRecovery {
    public static final String DATABASE="shortcuts_db",TABLE="shortcuts_table_video";
    private static long integer(Object value,long minimum)throws JSONException{if(!(value instanceof Integer||value instanceof Long)||((Number)value).longValue()<minimum)throw new JSONException("Invalid indexed-source recovery integer");return ((Number)value).longValue();}
    private static void text(Object value,boolean nullable)throws JSONException{if(nullable&&value==JSONObject.NULL)return;if(!(value instanceof String)||((String)value).length()>8192)throw new JSONException("Invalid private indexed-source text");}
    public static void validate(JSONObject image)throws JSONException{
        if(image.length()!=5||integer(image.get("schema"),1)!=1||integer(image.get("version"),5)!=5||!(image.get("exists") instanceof Boolean)||!image.has("sequence"))throw new JSONException("Unsupported indexed-source recovery image");if(!image.isNull("sequence"))integer(image.get("sequence"),0);
        JSONArray rows=image.getJSONArray("rows");if(rows.length()>10000||!image.getBoolean("exists")&&(rows.length()!=0||!image.isNull("sequence")))throw new JSONException("Invalid absent indexed-source image");Set<Long> ids=new HashSet<>();
        for(int i=0;i<rows.length();i++){JSONObject row=rows.getJSONObject(i);if(row.length()!=6||!ids.add(integer(row.get("id"),1))||!row.has("rescan"))throw new JSONException("Invalid indexed-source recovery row");text(row.get("path"),false);text(row.get("name"),false);text(row.get("ip"),true);text(row.get("friendly"),true);if(!row.isNull("rescan")&&integer(row.get("rescan"),0)>1)throw new JSONException("Invalid indexed-source rescan intent");}
    }
    private static boolean excluded(JSONObject row)throws JSONException{String path=row.getString("path");if(path.regionMatches(true,0,"putio:",0,6))return true;try{PortableLocations.locator(path);return false;}catch(JSONException unsafe){return true;}}
    public static void validatePair(JSONObject before,JSONObject after)throws JSONException{
        validate(before);validate(after);if(before.getBoolean("exists")&&!after.getBoolean("exists"))throw new JSONException("Indexed-source store cannot disappear during replacement");Map<Long,JSONObject> target=new HashMap<>();JSONArray rows=after.getJSONArray("rows");for(int i=0;i<rows.length();i++){JSONObject row=rows.getJSONObject(i);target.put(row.getLong("id"),row);}
        Map<Long,JSONObject> originals=new HashMap<>();rows=before.getJSONArray("rows");for(int i=0;i<rows.length();i++){JSONObject row=rows.getJSONObject(i);originals.put(row.getLong("id"),row);if(excluded(row)&&!row.toString().equals(String.valueOf(target.get(row.getLong("id")))))throw new JSONException("Excluded live source association changed");}
        for(var row:target.values())if(excluded(row)&&!row.toString().equals(String.valueOf(originals.get(row.getLong("id")))))throw new JSONException("New indexed source cannot recreate excluded ownership");
    }
    private static JSONObject empty()throws JSONException{return new JSONObject().put("schema",1).put("version",5).put("exists",false).put("rows",new JSONArray()).put("sequence",JSONObject.NULL);}
    private static JSONObject capture(SQLiteDatabase database)throws IOException,JSONException{
        if(database.getVersion()!=5)throw new IOException("Indexed-source schema is incompatible; no migration performed");Set<String> expected=Set.of("_id","path","name","ippath","friendly_url","rescan"),actual=new HashSet<>();
        try(Cursor columns=database.rawQuery("PRAGMA table_info("+TABLE+")",null)){while(columns.moveToNext()){String name=columns.getString(1);if(!expected.contains(name)||!actual.add(name)||!columns.getString(2).equalsIgnoreCase(name.equals("_id")||name.equals("rescan")?"INTEGER":"TEXT")||columns.getInt(5)!=(name.equals("_id")?1:0))throw new IOException("Indexed-source column contract is incompatible");}}if(!actual.equals(expected))throw new IOException("Indexed-source columns are incomplete");
        JSONObject image=empty().put("exists",true);JSONArray rows=image.getJSONArray("rows");try(Cursor cursor=database.query(TABLE,new String[]{"_id","path","name","ippath","friendly_url","rescan"},null,null,null,null,"_id ASC")){
            while(cursor.moveToNext()){if(rows.length()>=10000||cursor.getType(0)!=Cursor.FIELD_TYPE_INTEGER||cursor.getLong(0)<=0||cursor.getType(1)!=Cursor.FIELD_TYPE_STRING||cursor.getType(2)!=Cursor.FIELD_TYPE_STRING||!cursor.isNull(3)&&cursor.getType(3)!=Cursor.FIELD_TYPE_STRING||!cursor.isNull(4)&&cursor.getType(4)!=Cursor.FIELD_TYPE_STRING||!cursor.isNull(5)&&cursor.getType(5)!=Cursor.FIELD_TYPE_INTEGER)throw new IOException("Invalid indexed-source row");rows.put(new JSONObject().put("id",cursor.getLong(0)).put("path",cursor.getString(1)).put("name",cursor.getString(2)).put("ip",cursor.isNull(3)?JSONObject.NULL:cursor.getString(3)).put("friendly",cursor.isNull(4)?JSONObject.NULL:cursor.getString(4)).put("rescan",cursor.isNull(5)?JSONObject.NULL:cursor.getLong(5)));}
        }
        try(Cursor cursor=database.rawQuery("SELECT seq FROM sqlite_sequence WHERE name=?",new String[]{TABLE})){if(cursor.moveToFirst()){if(cursor.getType(0)!=Cursor.FIELD_TYPE_INTEGER||cursor.getLong(0)<0)throw new IOException("Invalid indexed-source sequence");image.put("sequence",cursor.getLong(0));}}validate(image);return image;
    }
    public static JSONObject capture(Context context)throws IOException,JSONException{
        File file=context.getDatabasePath(DATABASE);if(!file.exists())return empty();try(SQLiteDatabase database=SQLiteDatabase.openDatabase(file.getPath(),null,SQLiteDatabase.OPEN_READONLY)){database.execSQL("SAVEPOINT supernova_sources_read");try{return capture(database);}finally{database.execSQL("RELEASE SAVEPOINT supernova_sources_read");}}catch(RuntimeException invalid){throw new IOException("Indexed-source inventory unavailable",invalid);}
    }
    public static final class Session implements AutoCloseable{
        private SQLiteDatabase database;private final JSONObject before,after;private boolean ended,created;
        private Session(Context context,JSONObject before,JSONObject after,boolean recovery)throws IOException,JSONException{
            validatePair(before,after);this.before=new JSONObject(before.toString());this.after=new JSONObject(after.toString());File file=context.getDatabasePath(DATABASE);if(!recovery&&!capture(context).toString().equals(before.toString()))throw new IOException("Indexed sources changed after preview");
            if(!before.getBoolean("exists")&&!after.getBoolean("exists")){if(file.exists())throw new IOException("Indexed-source store appeared independently");return;}
            try{if(!file.exists()){if(before.getBoolean("exists"))throw new IOException("Original indexed-source store is missing");File folder=file.getParentFile();if(!folder.isDirectory()&&!folder.mkdirs())throw new IOException("Cannot prepare indexed-source store");database=SQLiteDatabase.openOrCreateDatabase(file,null);created=true;database.execSQL("CREATE TABLE shortcuts_table_video(_id INTEGER PRIMARY KEY AUTOINCREMENT,path TEXT NOT NULL,name TEXT NOT NULL,ippath TEXT,friendly_url TEXT,rescan INTEGER DEFAULT(1))");database.setVersion(5);}else database=SQLiteDatabase.openDatabase(file.getPath(),null,SQLiteDatabase.OPEN_READWRITE);
                database.beginTransaction();JSONObject current=capture(database);boolean original=current.toString().equals(before.toString()),redo=current.toString().equals(after.toString()),newEmpty=!before.getBoolean("exists")&&current.getJSONArray("rows").length()==0&&current.isNull("sequence");if(!created&&!(recovery?original||redo||newEmpty:original))throw new IOException("Indexed sources changed independently; refusing stale recovery");
            }catch(IOException|JSONException|RuntimeException failure){close();throw failure;}
        }
        public void write(JSONObject image,BooleanSupplier cancel)throws IOException,JSONException{
            validate(image);if(!image.toString().equals(before.toString())&&!image.toString().equals(after.toString()))throw new IOException("Indexed-source replacement changed its prepared scope");if(database==null)return;LogicalBackupCatalog.cancelled(cancel);database.delete(TABLE,null,null);
            try(var insert=database.compileStatement("INSERT INTO shortcuts_table_video(_id,path,name,ippath,friendly_url,rescan) VALUES(?,?,?,?,?,?)")){JSONArray rows=image.getJSONArray("rows");for(int i=0;i<rows.length();i++){LogicalBackupCatalog.cancelled(cancel);JSONObject row=rows.getJSONObject(i);insert.clearBindings();insert.bindLong(1,row.getLong("id"));insert.bindString(2,row.getString("path"));insert.bindString(3,row.getString("name"));for(int n=4;n<=5;n++){String key=n==4?"ip":"friendly";if(row.isNull(key))insert.bindNull(n);else insert.bindString(n,row.getString(key));}if(row.isNull("rescan"))insert.bindNull(6);else insert.bindLong(6,row.getLong("rescan"));insert.executeInsert();}}
            database.delete("sqlite_sequence","name=?",new String[]{TABLE});if(!image.isNull("sequence"))database.execSQL("INSERT INTO sqlite_sequence(name,seq) VALUES(?,?)",new Object[]{TABLE,image.getLong("sequence")});
        }
        public void commit(){if(database!=null&&!ended){database.setTransactionSuccessful();database.endTransaction();ended=true;}}
        @Override public void close(){if(database!=null){try{if(!ended&&database.inTransaction())database.endTransaction();}finally{database.close();database=null;}}}
    }
    public static Session begin(Context context,JSONObject before,JSONObject after)throws IOException,JSONException{return new Session(context,before,after,false);}
    public static void recover(Context context,JSONObject before,JSONObject after,boolean complete)throws IOException,JSONException{
        validatePair(before,after);if(!complete&&!before.getBoolean("exists")){if(context.getDatabasePath(DATABASE).exists()){JSONObject current=capture(context);if(current.getJSONArray("rows").length()!=0&&!current.toString().equals(after.toString()))throw new IOException("New indexed-source store changed independently; retain for inspection");if(!context.deleteDatabase(DATABASE))throw new IOException("Indexed-source rollback cleanup pending");}return;}
        try(Session session=new Session(context,before,after,true)){session.write(complete?after:before,()->false);session.commit();}
    }
    private IndexedSourceStoreRecovery(){}
}
