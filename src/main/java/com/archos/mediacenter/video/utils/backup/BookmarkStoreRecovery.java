package com.archos.mediacenter.video.utils.backup;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import java.io.*;
import java.util.*;
import java.util.function.BooleanSupplier;
import org.json.*;

/** Private originals/redo for the browser-favourites store. Never opens indexed roots or credential stores. */
public final class BookmarkStoreRecovery {
    public static final String DATABASE="shortcuts2_db";
    private static long integer(Object value,long minimum)throws JSONException{if(!(value instanceof Integer||value instanceof Long)||((Number)value).longValue()<minimum)throw new JSONException("Invalid bookmark recovery integer");return ((Number)value).longValue();}
    private static void text(Object value,int max,boolean nullable)throws JSONException{if(nullable&&value==JSONObject.NULL)return;if(!(value instanceof String)||((String)value).length()>max)throw new JSONException("Invalid private bookmark text");}
    public static void validate(JSONObject image)throws JSONException {
        if(image.length()!=5||integer(image.get("schema"),1)!=1||!List.of(1L,2L).contains(integer(image.get("version"),1))||!(image.get("exists") instanceof Boolean)||!image.has("sequence"))throw new JSONException("Unsupported bookmark recovery schema");
        if(!image.isNull("sequence"))integer(image.get("sequence"),0);JSONArray rows=image.getJSONArray("rows");if(rows.length()>10000||!image.getBoolean("exists")&&(rows.length()!=0||!image.isNull("sequence")))throw new JSONException("Invalid absent bookmark image");
        Set<Long> ids=new HashSet<>();for(int i=0;i<rows.length();i++){JSONObject row=rows.getJSONObject(i);if(row.length()!=4||!ids.add(integer(row.get("id"),1)))throw new JSONException("Invalid bookmark recovery target");text(row.get("uri"),8192,false);text(row.get("name"),8192,false);text(row.get("friendly"),8192,true);if(image.getInt("version")==1&&!row.isNull("friendly"))throw new JSONException("Unexpected legacy bookmark field");}
    }
    private static JSONObject capture(SQLiteDatabase database)throws IOException,JSONException {
        int version=database.getVersion();if(version!=1&&version!=2)throw new IOException("Bookmark schema is incompatible; existing data retained");
        Set<String> expected=new HashSet<>(List.of("_id","uri","shortcut_name"));if(version==2)expected.add("friendly_uri");Set<String> actual=new HashSet<>();
        try(Cursor columns=database.rawQuery("PRAGMA table_info(shortcuts)",null)){while(columns.moveToNext()){String name=columns.getString(1);if(!expected.contains(name)||!actual.add(name)||!columns.getString(2).equalsIgnoreCase(name.equals("_id")?"INTEGER":"TEXT")||columns.getInt(5)!=(name.equals("_id")?1:0))throw new IOException("Bookmark column contract is incompatible");}}
        if(!actual.equals(expected))throw new IOException("Bookmark column contract is incomplete");
        JSONArray rows=new JSONArray();try(Cursor cursor=database.query("shortcuts",new String[]{"_id","uri","shortcut_name",version==1?"NULL AS friendly_uri":"friendly_uri"},null,null,null,null,"_id ASC")) {
            while(cursor.moveToNext()){if(rows.length()>=10000||cursor.getType(0)!=Cursor.FIELD_TYPE_INTEGER||cursor.getLong(0)<=0||cursor.getType(1)!=Cursor.FIELD_TYPE_STRING||cursor.getType(2)!=Cursor.FIELD_TYPE_STRING||!cursor.isNull(3)&&cursor.getType(3)!=Cursor.FIELD_TYPE_STRING)throw new IOException("Invalid existing bookmark state");rows.put(new JSONObject().put("id",cursor.getLong(0)).put("uri",cursor.getString(1)).put("name",cursor.getString(2)).put("friendly",cursor.isNull(3)?JSONObject.NULL:cursor.getString(3)));}
        }
        Object sequence=JSONObject.NULL;try(Cursor cursor=database.rawQuery("SELECT seq FROM sqlite_sequence WHERE name='shortcuts'",null)){if(cursor.moveToFirst()){if(cursor.getType(0)!=Cursor.FIELD_TYPE_INTEGER||cursor.getLong(0)<0)throw new IOException("Invalid bookmark sequence");sequence=cursor.getLong(0);}}
        JSONObject image=new JSONObject().put("schema",1).put("version",version).put("exists",true).put("rows",rows).put("sequence",sequence);validate(image);return image;
    }
    public static JSONObject capture(Context context)throws IOException,JSONException {
        File file=context.getDatabasePath(DATABASE);if(!file.exists())return new JSONObject().put("schema",1).put("version",2).put("exists",false).put("rows",new JSONArray()).put("sequence",JSONObject.NULL);
        try(SQLiteDatabase database=SQLiteDatabase.openDatabase(file.getPath(),null,SQLiteDatabase.OPEN_READONLY)){database.execSQL("SAVEPOINT supernova_bookmarks_read");try{return capture(database);}finally{database.execSQL("RELEASE SAVEPOINT supernova_bookmarks_read");}}
        catch(RuntimeException invalid){throw new IOException("Bookmark inventory unavailable; no data changed",invalid);}
    }
    public static void validatePair(JSONObject before,JSONObject after)throws JSONException {validate(before);validate(after);if(before.getInt("version")!=after.getInt("version")||before.getBoolean("exists")&&!after.getBoolean("exists"))throw new JSONException("Invalid bookmark replacement scope");}
    public static final class Session implements AutoCloseable {
        private SQLiteDatabase database;private boolean ended,created;
        private Session(Context context,JSONObject before,JSONObject after,boolean recovery)throws IOException,JSONException {
            validatePair(before,after);File file=context.getDatabasePath(DATABASE);
            if(!recovery&&!capture(context).toString().equals(before.toString()))throw new IOException("Bookmarks changed after preview; review again");
            if(!before.getBoolean("exists")&&!after.getBoolean("exists")){if(file.exists())throw new IOException("Bookmark store appeared after preview");return;}
            try{if(!file.exists()) {
                if(before.getBoolean("exists"))throw new IOException("Original bookmark store is missing");File parent=file.getParentFile();if(!parent.isDirectory()&&!parent.mkdirs())throw new IOException("Cannot prepare bookmark store");
                database=SQLiteDatabase.openOrCreateDatabase(file,null);created=true;database.execSQL("CREATE TABLE shortcuts (_id INTEGER PRIMARY KEY AUTOINCREMENT,uri TEXT NOT NULL,shortcut_name TEXT NOT NULL,friendly_uri TEXT)");database.setVersion(2);
            }else database=SQLiteDatabase.openDatabase(file.getPath(),null,SQLiteDatabase.OPEN_READWRITE);
            database.beginTransaction();JSONObject current=capture(database);if(current.getInt("version")!=before.getInt("version")||!recovery&&!created&&!current.toString().equals(before.toString()))throw new IOException("Bookmark inventory changed; refusing stale replacement");}
            catch(IOException|JSONException|RuntimeException failure){close();throw failure;}
        }
        public void write(JSONObject image,BooleanSupplier cancel)throws IOException,JSONException {
            validate(image);if(database==null)return;database.delete("shortcuts",null,null);
            try(var insert=database.compileStatement(image.getInt("version")==1?"INSERT INTO shortcuts(_id,uri,shortcut_name) VALUES(?,?,?)":"INSERT INTO shortcuts(_id,uri,shortcut_name,friendly_uri) VALUES(?,?,?,?)")) {
                JSONArray rows=image.getJSONArray("rows");for(int i=0;i<rows.length();i++){LogicalBackupCatalog.cancelled(cancel);JSONObject row=rows.getJSONObject(i);insert.clearBindings();insert.bindLong(1,row.getLong("id"));insert.bindString(2,row.getString("uri"));insert.bindString(3,row.getString("name"));if(image.getInt("version")==2){if(row.isNull("friendly"))insert.bindNull(4);else insert.bindString(4,row.getString("friendly"));}insert.executeInsert();}
            }
            database.delete("sqlite_sequence","name = ?",new String[]{"shortcuts"});if(!image.isNull("sequence"))database.execSQL("INSERT INTO sqlite_sequence(name,seq) VALUES('shortcuts',?)",new Object[]{image.getLong("sequence")});
        }
        public void commit(){if(database!=null&&!ended){database.setTransactionSuccessful();database.endTransaction();ended=true;}}
        @Override public void close(){if(database!=null){try{if(!ended&&database.inTransaction())database.endTransaction();}finally{database.close();database=null;}}}
    }
    public static Session begin(Context context,JSONObject before,JSONObject after)throws IOException,JSONException{return new Session(context,before,after,false);}
    public static void recover(Context context,JSONObject before,JSONObject after,boolean complete)throws IOException,JSONException {
        validatePair(before,after);if(!complete&&!before.getBoolean("exists")){
            if(context.getDatabasePath(DATABASE).exists()){
                JSONObject current=capture(context);
                if(current.getInt("version")!=after.getInt("version")||current.getJSONArray("rows").length()!=0&&!current.toString().equals(after.toString()))throw new IOException("New bookmark store changed independently; recovery requires inspection");
                if(!context.deleteDatabase(DATABASE))throw new IOException("Cannot remove newly created incomplete bookmark store");
            }return;
        }
        try(Session session=new Session(context,before,after,true)){session.write(complete?after:before,()->false);session.commit();}
    }
    private BookmarkStoreRecovery(){}
}
