package com.archos.mediacenter.video.utils.backup;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import com.archos.mediaprovider.video.*;
import com.archos.mediascraper.OwnedCanonicalBatch;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.*;
import java.util.function.BooleanSupplier;
import org.json.*;

/** Private canonical redo guards. Hashes contain no readable paths, facts, media or credentials. */
public final class CanonicalNativeRecovery {
    private static final List<String> TABLES=List.of("MOVIE","SHOW","EPISODE","ACTOR","GENRE","DIRECTOR","WRITER","STUDIO",
        "FILMS_MOVIE","WRITERS_MOVIE","FILMS_SHOW","WRITERS_SHOW","FILMS_EPISODE","WRITERS_EPISODE","GUESTS",
        "PRODUCES_MOVIE","PRODUCES_SHOW","PLAYS_MOVIE","PLAYS_SHOW","BELONGS_MOVIE","BELONGS_SHOW",
        "movie_posters","movie_trailers","movie_backdrops","show_posters","show_backdrops","movie_collection","user_collection","user_collection_item");
    private static void checkTransaction(SQLiteDatabase database)throws IOException {
        if(database==null||!database.inTransaction()||database.getVersion()!=VideoOpenHelper.getDatabaseVersion())throw new IOException("Canonical recovery requires the coordinated native transaction");
    }
    private static void add(MessageDigest digest,String value){byte[] bytes=value.getBytes(StandardCharsets.UTF_8);digest.update((byte)(bytes.length>>>24));digest.update((byte)(bytes.length>>>16));digest.update((byte)(bytes.length>>>8));digest.update((byte)bytes.length);digest.update(bytes);}
    private static void rows(MessageDigest digest,Cursor rows,BooleanSupplier cancel)throws IOException,JSONException {
        add(digest,new JSONArray(Arrays.asList(rows.getColumnNames())).toString());int count=0;
        while(rows.moveToNext()){
            if(cancel.getAsBoolean()||Thread.currentThread().isInterrupted())throw new InterruptedIOException("Canonical recovery cancelled");
            if(++count>100000)throw new IOException("Canonical recovery inventory exceeds limit");JSONArray row=new JSONArray();
            for(int i=0;i<rows.getColumnCount();i++)switch(rows.getType(i)){
                case Cursor.FIELD_TYPE_NULL:row.put(JSONObject.NULL);break;
                case Cursor.FIELD_TYPE_INTEGER:row.put(new JSONArray().put("integer").put(rows.getLong(i)));break;
                case Cursor.FIELD_TYPE_FLOAT:row.put(new JSONArray().put("float").put(rows.getDouble(i)));break;
                case Cursor.FIELD_TYPE_STRING:String text=rows.getString(i);if(text.length()>1024*1024)throw new IOException("Canonical recovery field exceeds limit");row.put(new JSONArray().put("text").put(text));break;
                default:throw new IOException("Unsupported canonical recovery field");
            }
            add(digest,row.toString());
        }
        add(digest,Integer.toString(count));
    }
    /** Broad metadata guard deliberately refuses an independently changed graph instead of overwriting it. */
    public static String fingerprint(SQLiteDatabase database,JSONArray files,BooleanSupplier cancel)throws IOException,JSONException {
        checkTransaction(database);validateFiles(files);
        try {
            MessageDigest digest=MessageDigest.getInstance("SHA-256");
            for(String table:TABLES){add(digest,table);try(Cursor cursor=database.query(table,null,null,null,null,null,"rowid ASC")){rows(digest,cursor,cancel);}}
            add(digest,"owned files");
            for(int i=0;i<files.length();i++)try(Cursor cursor=database.query(VideoOpenHelper.FILES_TABLE_NAME,new String[]{"_id","_data","media_type","ArchosMediaScraper_type","ArchosMediaScraper_id","Archos_hiddenByUser"},"_id=?",new String[]{Long.toString(files.getLong(i))},null,null,null)){rows(digest,cursor,cancel);}
            add(digest,PlaylistStateRecovery.capture(database).toString());
            StringBuilder hash=new StringBuilder();for(byte value:digest.digest())hash.append(String.format(Locale.ROOT,"%02x",value&255));return hash.toString();
        }catch(java.security.NoSuchAlgorithmException|RuntimeException invalid){throw new IOException("Canonical recovery inventory unavailable",invalid);}
    }
    private static void validateFiles(JSONArray files)throws JSONException {
        if(files.length()==0||files.length()>100000)throw new JSONException("Invalid canonical file scope");long previous=0;
        for(int i=0;i<files.length();i++){Object value=files.get(i);if(!(value instanceof Integer||value instanceof Long)||((Number)value).longValue()<=previous)throw new JSONException("Invalid canonical file owner");previous=((Number)value).longValue();}
    }
    public static JSONObject begin(SQLiteDatabase database,Collection<Long> files,List<OwnedCanonicalBatch> batches,BooleanSupplier cancel)throws IOException,JSONException {
        TreeSet<Long> ordered=new TreeSet<>(files);if(ordered.size()!=files.size())throw new JSONException("Duplicate canonical file owner");JSONArray scope=new JSONArray(ordered);validateFiles(scope);JSONArray redo=new JSONArray();if(batches.isEmpty()||batches.size()>100000)throw new JSONException("Invalid canonical batch scope");for(var batch:batches)redo.put(batch.encodeRecovery());
        return new JSONObject().put("schema",1).put("files",scope).put("before",fingerprint(database,scope,cancel)).put("after",JSONObject.NULL).put("batches",redo);
    }
    public static void seal(SQLiteDatabase database,JSONObject record,BooleanSupplier cancel)throws IOException,JSONException {
        if(!record.isNull("after"))throw new JSONException("Canonical recovery already sealed");record.put("after",fingerprint(database,record.getJSONArray("files"),cancel));validate(record);
    }
    public static List<OwnedCanonicalBatch> validate(JSONObject record)throws JSONException {
        if(record.length()!=5||!(record.get("schema") instanceof Integer||record.get("schema") instanceof Long)||record.getLong("schema")!=1)throw new JSONException("Invalid canonical recovery schema");validateFiles(record.getJSONArray("files"));for(String name:List.of("before","after"))if(!(record.get(name) instanceof String)||!record.getString(name).matches("[a-f0-9]{64}"))throw new JSONException("Invalid canonical recovery state guard");JSONArray redo=record.getJSONArray("batches");if(redo.length()==0||redo.length()>100000)throw new JSONException("Invalid canonical redo scope");List<OwnedCanonicalBatch> result=new ArrayList<>();for(int i=0;i<redo.length();i++)result.add(OwnedCanonicalBatch.decodeRecovery(redo.getJSONObject(i)));return Collections.unmodifiableList(result);
    }
    /** Caller commits only after the same native transaction also recovers selected pin/hidden/playlist state. */
    public static boolean replay(Context context,SQLiteDatabase database,JSONObject record,boolean complete,BooleanSupplier cancel)throws IOException,JSONException {
        List<OwnedCanonicalBatch> redo=validate(record);String current=fingerprint(database,record.getJSONArray("files"),cancel);
        if(complete&&current.equals(record.getString("after")))return false;
        if(!current.equals(record.getString("before")))throw new IOException("Canonical metadata changed independently; recovery requires inspection");
        if(!complete)return false;
        for(var batch:redo)CanonicalMatchExecutor.apply(context,database,batch,cancel);return true;
    }
    public static void verifyComplete(SQLiteDatabase database,JSONObject record,BooleanSupplier cancel)throws IOException,JSONException {
        validate(record);if(!fingerprint(database,record.getJSONArray("files"),cancel).equals(record.getString("after")))throw new IOException("Canonical redo did not reproduce the coordinated native state");
    }
    private CanonicalNativeRecovery(){}
}
