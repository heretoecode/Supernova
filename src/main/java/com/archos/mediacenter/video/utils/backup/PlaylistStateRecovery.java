package com.archos.mediacenter.video.utils.backup;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import com.archos.mediaprovider.video.*;
import java.io.*;
import java.util.*;
import java.util.function.BooleanSupplier;
import org.json.*;

/** Private list-table originals/redo only. Remote mappings in originals never enter a public archive. */
public final class PlaylistStateRecovery {
    private static long integer(Object value,long minimum,long maximum)throws JSONException{if(!(value instanceof Integer||value instanceof Long)||((Number)value).longValue()<minimum||((Number)value).longValue()>maximum)throw new JSONException("Invalid playlist recovery integer");return ((Number)value).longValue();}
    private static void optional(JSONObject row,String key,long minimum,long maximum)throws JSONException{if(!row.has(key))throw new JSONException("Missing playlist recovery field");if(!row.isNull(key))integer(row.get(key),minimum,maximum);}
    public static JSONObject empty()throws JSONException{return new JSONObject().put("schema",1).put("definitions",new JSONArray()).put("members",new JSONArray()).put("definitionSequence",JSONObject.NULL).put("memberSequence",JSONObject.NULL);}
    public static void validate(JSONObject image)throws JSONException{
        if(image.length()!=5||integer(image.get("schema"),1,1)!=1)throw new JSONException("Unsupported playlist recovery schema");optional(image,"definitionSequence",0,Long.MAX_VALUE);optional(image,"memberSequence",0,Long.MAX_VALUE);
        JSONArray definitions=image.getJSONArray("definitions"),members=image.getJSONArray("members");if(definitions.length()>10000||members.length()>100000)throw new JSONException("Playlist recovery scope too large");Set<Long> ids=new HashSet<>(),memberIds=new HashSet<>();long text=0;
        for(int i=0;i<definitions.length();i++){
            JSONObject row=definitions.getJSONObject(i);if(row.length()!=4||!ids.add(integer(row.get("id"),1,Long.MAX_VALUE))||!row.has("title"))throw new JSONException("Invalid playlist recovery definition");
            if(!row.isNull("title")){Object value=row.get("title");if(!(value instanceof String)||((String)value).length()>8192||(text+=((String)value).length())>4*1024*1024)throw new JSONException("Invalid private playlist title");}
            optional(row,"trakt",-1,Long.MAX_VALUE);optional(row,"status",0,VideoStore.List.SyncStatus.STATUS_LOCAL_ONLY);
        }
        for(int i=0;i<members.length();i++){
            JSONObject row=members.getJSONObject(i);if(row.length()!=5||!memberIds.add(integer(row.get("id"),1,Long.MAX_VALUE)))throw new JSONException("Invalid playlist recovery member");
            optional(row,"owner",1,Long.MAX_VALUE);if(!row.isNull("owner")&&!ids.contains(row.getLong("owner")))throw new JSONException("Orphaned playlist recovery membership");optional(row,"movie",-1,Integer.MAX_VALUE);optional(row,"episode",-1,Integer.MAX_VALUE);optional(row,"status",0,VideoStore.List.SyncStatus.STATUS_LOCAL_ONLY);
        }
    }
    private static Object value(Cursor rows,int column)throws IOException{if(rows.isNull(column))return JSONObject.NULL;if(rows.getType(column)!=Cursor.FIELD_TYPE_INTEGER)throw new IOException("Invalid native playlist recovery value");return rows.getLong(column);}
    private static Object sequence(SQLiteDatabase database,String table)throws IOException{try(Cursor rows=database.rawQuery("SELECT seq FROM sqlite_sequence WHERE name=?",new String[]{table})){return rows.moveToFirst()?value(rows,0):JSONObject.NULL;}}
    private static void columns(SQLiteDatabase database,String table,Set<String> expected)throws IOException{
        Set<String> actual=new HashSet<>();try(Cursor rows=database.rawQuery("PRAGMA table_info("+table+")",null)){while(rows.moveToNext()){String name=rows.getString(1);if(!expected.contains(name)||!actual.add(name)||!rows.getString(2).equalsIgnoreCase(name.equals("title")?"TEXT":"INTEGER")||rows.getInt(5)!=(name.equals("_id")?1:0))throw new IOException("Playlist table contract is incompatible");}}
        if(!actual.equals(expected))throw new IOException("Playlist table contract is incomplete");
    }
    /** Caller holds a single SQLite read snapshot or transaction across the selected media participants. */
    public static JSONObject capture(SQLiteDatabase database)throws IOException,JSONException{
        if(database.getVersion()!=VideoOpenHelper.getDatabaseVersion())throw new IOException("Playlist media schema is incompatible");columns(database,ListTables.LIST_TABLE,Set.of("_id","title","trakt_id","sync_status"));columns(database,ListTables.VIDEO_LIST_TABLE,Set.of("_id","list_id","m_online_id","e_online_id","sync_status"));
        JSONObject image=empty();JSONArray definitions=image.getJSONArray("definitions"),members=image.getJSONArray("members");
        try(Cursor rows=database.query(ListTables.LIST_TABLE,new String[]{"_id","title","trakt_id","sync_status"},null,null,null,null,"_id ASC")){
            while(rows.moveToNext()){if(definitions.length()>=10000||!rows.isNull(1)&&rows.getType(1)!=Cursor.FIELD_TYPE_STRING)throw new IOException("Invalid native playlist definition");definitions.put(new JSONObject().put("id",value(rows,0)).put("title",rows.isNull(1)?JSONObject.NULL:rows.getString(1)).put("trakt",value(rows,2)).put("status",value(rows,3)));}
        }
        try(Cursor rows=database.query(ListTables.VIDEO_LIST_TABLE,new String[]{"_id","list_id","m_online_id","e_online_id","sync_status"},null,null,null,null,"_id ASC")){
            while(rows.moveToNext()){if(members.length()>=100000)throw new IOException("Native playlist membership exceeds supported limits");members.put(new JSONObject().put("id",value(rows,0)).put("owner",value(rows,1)).put("movie",value(rows,2)).put("episode",value(rows,3)).put("status",value(rows,4)));}
        }
        image.put("definitionSequence",sequence(database,ListTables.LIST_TABLE)).put("memberSequence",sequence(database,ListTables.VIDEO_LIST_TABLE));validate(image);return image;
    }
    private static Map<Long,JSONObject> indexed(JSONArray rows)throws JSONException{Map<Long,JSONObject> values=new LinkedHashMap<>();for(int i=0;i<rows.length();i++){JSONObject row=rows.getJSONObject(i);values.put(row.getLong("id"),row);}return values;}
    /** Existing receiving account-owned definitions and their memberships are outside selected replacement. */
    public static void validatePair(JSONObject before,JSONObject after)throws JSONException{
        validate(before);validate(after);Set<Long> remote=new HashSet<>();Map<Long,JSONObject> target=indexed(after.getJSONArray("definitions"));JSONArray original=before.getJSONArray("definitions");
        for(int i=0;i<original.length();i++){JSONObject row=original.getJSONObject(i);if(!row.isNull("trakt")){remote.add(row.getLong("id"));if(!row.toString().equals(String.valueOf(target.get(row.getLong("id")))))throw new JSONException("Receiving account-owned playlist changed");}}
        Map<Long,JSONObject> nextMembers=indexed(after.getJSONArray("members"));original=before.getJSONArray("members");for(int i=0;i<original.length();i++){JSONObject row=original.getJSONObject(i);if(row.isNull("owner")||remote.contains(row.getLong("owner")))if(!row.toString().equals(String.valueOf(nextMembers.get(row.getLong("id")))))throw new JSONException("Unowned receiving playlist membership changed");}
        Map<Long,JSONObject> originals=indexed(before.getJSONArray("definitions"));JSONArray incoming=after.getJSONArray("definitions");for(int i=0;i<incoming.length();i++){JSONObject row=incoming.getJSONObject(i);if(!originals.containsKey(row.getLong("id"))&&(!row.isNull("trakt")||row.isNull("status")||row.getInt("status")!=VideoStore.List.SyncStatus.STATUS_LOCAL_ONLY))throw new JSONException("New playlist must remain local-only");}
    }
    public static final class Prepared {
        public final JSONObject after;public final String pending;public final int unresolved;public final Map<Long,String> references;
        private Prepared(JSONObject after,JSONObject pending,Map<Long,String> references)throws JSONException{this.after=after;this.pending=pending.toString();unresolved=pending.getJSONArray("rows").length();this.references=Collections.unmodifiableMap(references);}
    }
    public static Prepared prepare(JSONObject before,JSONObject payload,boolean exists)throws JSONException{
        validate(before);PortableSavedPlaylists.validate(payload);
        if(!exists){if(before.getJSONArray("definitions").length()!=0||before.getJSONArray("members").length()!=0||!before.isNull("definitionSequence")||!before.isNull("memberSequence"))throw new JSONException("Invalid absent playlist store image");return new Prepared(new JSONObject(before.toString()),new JSONObject(payload.toString()),new LinkedHashMap<>());}
        long nextDefinition=before.isNull("definitionSequence")?0:before.getLong("definitionSequence"),nextMember=before.isNull("memberSequence")?0:before.getLong("memberSequence");Set<Long> remote=new HashSet<>();JSONArray definitions=new JSONArray(),members=new JSONArray(),original=before.getJSONArray("definitions");
        for(int i=0;i<original.length();i++){JSONObject row=original.getJSONObject(i);nextDefinition=Math.max(nextDefinition,row.getLong("id"));if(!row.isNull("trakt")){remote.add(row.getLong("id"));definitions.put(new JSONObject(row.toString()));}}
        original=before.getJSONArray("members");for(int i=0;i<original.length();i++){JSONObject row=original.getJSONObject(i);nextMember=Math.max(nextMember,row.getLong("id"));if(row.isNull("owner")||remote.contains(row.getLong("owner")))members.put(new JSONObject(row.toString()));}
        Map<Long,String> references=new LinkedHashMap<>();JSONArray rows=payload.getJSONArray("rows");for(int i=0;i<rows.length();i++){
            if(nextDefinition>=Integer.MAX_VALUE)throw new JSONException("Receiving playlist identity capacity exhausted");JSONObject row=rows.getJSONObject(i);long id=++nextDefinition;references.put(id,row.getString("id"));definitions.put(new JSONObject().put("id",id).put("title",row.get("name")).put("trakt",JSONObject.NULL).put("status",VideoStore.List.SyncStatus.STATUS_LOCAL_ONLY));
            JSONArray values=row.getJSONArray("members");for(int n=0;n<values.length();n++){if(nextMember==Long.MAX_VALUE)throw new JSONException("Receiving member identity capacity exhausted");String identity=values.getString(n);long provider=Long.parseLong(identity.substring(identity.lastIndexOf(':')+1));boolean movie=identity.startsWith("tmdb:movie:");members.put(new JSONObject().put("id",++nextMember).put("owner",id).put("movie",movie?provider:-1).put("episode",movie?-1:provider).put("status",VideoStore.List.SyncStatus.STATUS_LOCAL_ONLY));}
        }
        JSONObject after=empty().put("definitions",definitions).put("members",members).put("definitionSequence",nextDefinition).put("memberSequence",nextMember);validatePair(before,after);return new Prepared(after,new JSONObject().put("schema",1).put("rows",new JSONArray()),references);
    }
    /** Inside the combined media transaction only; strict columns exclude unknown future data before mutation. */
    public static void write(SQLiteDatabase database,JSONObject image,BooleanSupplier cancel)throws IOException,JSONException{
        validate(image);if(!database.inTransaction())throw new IOException("Playlist replacement requires a protected media transaction");columns(database,ListTables.LIST_TABLE,Set.of("_id","title","trakt_id","sync_status"));columns(database,ListTables.VIDEO_LIST_TABLE,Set.of("_id","list_id","m_online_id","e_online_id","sync_status"));LogicalBackupCatalog.cancelled(cancel);
        database.delete(ListTables.VIDEO_LIST_TABLE,null,null);database.delete(ListTables.LIST_TABLE,null,null);
        try(var statement=database.compileStatement("INSERT INTO list_table(_id,title,trakt_id,sync_status) VALUES(?,?,?,?)")){JSONArray rows=image.getJSONArray("definitions");for(int i=0;i<rows.length();i++){LogicalBackupCatalog.cancelled(cancel);JSONObject row=rows.getJSONObject(i);statement.clearBindings();statement.bindLong(1,row.getLong("id"));if(row.isNull("title"))statement.bindNull(2);else statement.bindString(2,row.getString("title"));bind(statement,3,row,"trakt");bind(statement,4,row,"status");statement.executeInsert();}}
        try(var statement=database.compileStatement("INSERT INTO video_list_table(_id,list_id,m_online_id,e_online_id,sync_status) VALUES(?,?,?,?,?)")){JSONArray rows=image.getJSONArray("members");for(int i=0;i<rows.length();i++){LogicalBackupCatalog.cancelled(cancel);JSONObject row=rows.getJSONObject(i);statement.clearBindings();statement.bindLong(1,row.getLong("id"));bind(statement,2,row,"owner");bind(statement,3,row,"movie");bind(statement,4,row,"episode");bind(statement,5,row,"status");statement.executeInsert();}}
        for(String[] value:new String[][]{{ListTables.LIST_TABLE,"definitionSequence"},{ListTables.VIDEO_LIST_TABLE,"memberSequence"}}){database.delete("sqlite_sequence","name=?",new String[]{value[0]});if(!image.isNull(value[1]))database.execSQL("INSERT INTO sqlite_sequence(name,seq) VALUES(?,?)",new Object[]{value[0],image.getLong(value[1])});}
    }
    private static void bind(android.database.sqlite.SQLiteStatement statement,int index,JSONObject row,String key)throws JSONException{if(row.isNull(key))statement.bindNull(index);else statement.bindLong(index,row.getLong(key));}
    private PlaylistStateRecovery(){}
}
