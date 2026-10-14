package com.archos.mediacenter.video.utils.backup;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import androidx.preference.PreferenceManager;
import com.archos.mediaprovider.video.ListTables;
import com.archos.mediaprovider.video.VideoOpenHelper;
import com.archos.mediaprovider.video.VideoStore;
import java.io.IOException;
import java.util.*;
import org.json.*;

/** Existing saved-playlist definitions and provider membership. No Trakt account mapping or physical locator. */
public final class PortableSavedPlaylists {
    public static final String PENDING="supernova_restore_pending_playlists_v2";
    private static final String REFERENCE="supernova_playlist_reference:";
    public static String referenceKey(long id){return REFERENCE+id;}
    public static boolean owns(String key){return key.equals(PENDING)||key.matches(REFERENCE+"[1-9][0-9]{0,18}");}
    public static void identity(String id)throws JSONException {
        if(id==null||!id.matches("playlist:[a-f0-9]{8}(?:-[a-f0-9]{4}){3}-[a-f0-9]{12}:[1-9][0-9]{0,18}"))throw new JSONException("Invalid saved-playlist identity");
        try{Long.parseLong(id.substring(id.lastIndexOf(':')+1));}catch(NumberFormatException invalid){throw new JSONException("Playlist identity exceeds supported range");}
    }
    private static final int MAX_LISTS=10000,MAX_MEMBERS=100000;
    private static long integer(Object value,long minimum)throws JSONException{if(!(value instanceof Integer||value instanceof Long)||((Number)value).longValue()<minimum)throw new JSONException("Invalid playlist integer");return ((Number)value).longValue();}
    private static void label(Object value)throws JSONException{
        if(value==JSONObject.NULL)return;if(!(value instanceof String))throw new JSONException("Invalid playlist name");String text=(String)value;
        if(text.length()>8192||text.indexOf('\0')>=0||text.contains("://")||text.startsWith("/")||text.startsWith("\\")||text.matches("(?is)^[a-z]:[\\\\/].*")||text.matches("(?is).*(?:password|passwd|token|secret|authorization|cookie|api[_-]?key)\\s*[:=].*"))throw new JSONException("Playlist name requires privacy review");
        try{java.nio.charset.StandardCharsets.UTF_8.newEncoder().encode(java.nio.CharBuffer.wrap(text));}catch(Exception invalid){throw new JSONException("Invalid playlist name encoding");}
    }
    public static void member(String identity)throws JSONException{
        if(identity==null||!identity.matches("tmdb:(?:movie|episode):[1-9][0-9]{0,9}"))throw new JSONException("Invalid saved-playlist member");
        try{if(Long.parseLong(identity.substring(identity.lastIndexOf(':')+1))>Integer.MAX_VALUE)throw new NumberFormatException();}catch(NumberFormatException invalid){throw new JSONException("Playlist provider identity exceeds receiving API");}
    }
    public static final class Definition {
        public final long id;public final String name;public final Set<String> members;
        public Definition(long id,String name,Collection<String> members)throws JSONException{
            if(id<=0)throw new JSONException("Invalid native playlist identity");label(name==null?JSONObject.NULL:name);if(members.size()>MAX_MEMBERS)throw new JSONException("Too many playlist members");
            LinkedHashSet<String> copy=new LinkedHashSet<>();for(String identity:members){member(identity);copy.add(identity);}this.id=id;this.name=name;this.members=Collections.unmodifiableSet(copy);
        }
    }
    /** Called within the caller's single read snapshot. Never reads remote IDs, paths, metadata or credentials. */
    public static Map<Long,Definition> read(SQLiteDatabase database)throws IOException {
        if(database.getVersion()!=VideoOpenHelper.getDatabaseVersion())throw new IOException("Saved-playlist schema is incompatible");
        Map<Long,Definition> result=new LinkedHashMap<>();Map<Long,LinkedHashSet<String>> members=new LinkedHashMap<>();Map<Long,String> names=new LinkedHashMap<>();
        try{
            try(Cursor rows=database.query(ListTables.LIST_TABLE,new String[]{VideoStore.List.Columns.ID,VideoStore.List.Columns.TITLE,VideoStore.List.Columns.SYNC_STATUS},null,null,null,null,"_id ASC")){
                while(rows.moveToNext()){
                    if(rows.getType(0)!=Cursor.FIELD_TYPE_INTEGER||rows.getLong(0)<=0||!rows.isNull(1)&&rows.getType(1)!=Cursor.FIELD_TYPE_STRING||!rows.isNull(2)&&rows.getType(2)!=Cursor.FIELD_TYPE_INTEGER)throw new IOException("Invalid saved-playlist definition");
                    long status=rows.isNull(2)?VideoStore.List.SyncStatus.STATUS_OK:rows.getLong(2);if(status<0||status>VideoStore.List.SyncStatus.STATUS_LOCAL_ONLY)throw new IOException("Unknown saved-playlist sync status");if(status==VideoStore.List.SyncStatus.STATUS_DELETED)continue;
                    if(names.size()>=MAX_LISTS||members.put(rows.getLong(0),new LinkedHashSet<>())!=null)throw new IOException("Ambiguous or excessive playlist definitions");names.put(rows.getLong(0),rows.isNull(1)?null:rows.getString(1));
                }
            }
            int count=0;try(Cursor rows=database.query(ListTables.VIDEO_LIST_TABLE,new String[]{VideoStore.VideoList.Columns.LIST_ID,VideoStore.VideoList.Columns.M_ONLINE_ID,VideoStore.VideoList.Columns.E_ONLINE_ID,VideoStore.List.Columns.SYNC_STATUS},null,null,null,null,"_id ASC")){
                while(rows.moveToNext()){
                    if(rows.isNull(0))continue;if(rows.getType(0)!=Cursor.FIELD_TYPE_INTEGER)throw new IOException("Invalid playlist owner");Set<String> target=members.get(rows.getLong(0));if(target==null)continue;
                    if(!rows.isNull(3)&&rows.getType(3)!=Cursor.FIELD_TYPE_INTEGER)throw new IOException("Invalid playlist membership status");long status=rows.isNull(3)?0:rows.getLong(3);
                    if(status<0||status>VideoStore.List.SyncStatus.STATUS_LOCAL_ONLY)throw new IOException("Unknown playlist membership status");if(status==VideoStore.List.SyncStatus.STATUS_DELETED)continue;
                    if(!rows.isNull(1)&&rows.getType(1)!=Cursor.FIELD_TYPE_INTEGER||!rows.isNull(2)&&rows.getType(2)!=Cursor.FIELD_TYPE_INTEGER)throw new IOException("Invalid playlist provider identity");long movie=rows.isNull(1)?-1:rows.getLong(1),episode=rows.isNull(2)?-1:rows.getLong(2);
                    if((movie>0)==(episode>0)||++count>MAX_MEMBERS)throw new IOException("Ambiguous or excessive playlist membership");String identity="tmdb:"+(movie>0?"movie:":"episode:")+(movie>0?movie:episode);member(identity);target.add(identity);
                }
            }
            for(var item:names.entrySet())result.put(item.getKey(),new Definition(item.getKey(),item.getValue(),members.get(item.getKey())));
            return Collections.unmodifiableMap(result);
        }catch(JSONException|RuntimeException invalid){throw new IOException("Saved-playlist inventory cannot be exported safely",invalid);}
    }
    public static void validate(JSONObject payload)throws JSONException {
        if(payload.length()!=2||integer(payload.get("schema"),1)!=1)throw new JSONException("Unsupported saved-playlist schema");JSONArray rows=payload.getJSONArray("rows");if(rows.length()>MAX_LISTS)throw new JSONException("Too many saved playlists");Set<String> ids=new HashSet<>();int count=0;
        for(int i=0;i<rows.length();i++){
            JSONObject row=rows.getJSONObject(i);if(row.length()!=3||!(row.get("id") instanceof String)||!row.has("name"))throw new JSONException("Invalid saved-playlist fields");String id=row.getString("id");
            identity(id);if(!ids.add(id))throw new JSONException("Duplicate saved-playlist identity");label(row.get("name"));
            JSONArray values=row.getJSONArray("members");Set<String> seen=new HashSet<>();for(int n=0;n<values.length();n++){if(!(values.get(n) instanceof String)||++count>MAX_MEMBERS)throw new JSONException("Invalid or excessive playlist members");String identity=values.getString(n);member(identity);if(!seen.add(identity))throw new JSONException("Duplicate playlist member");}
        }
    }
    public static JSONObject export(Context context,Map<Long,Definition> inventory)throws JSONException {
        String origin=new PortableTitleReferences(context,null).origin();Map<String,JSONObject> rows=new LinkedHashMap<>();var preferences=PreferenceManager.getDefaultSharedPreferences(context);
        for(var item:inventory.entrySet()){
            Definition definition=item.getValue();if(definition==null||item.getKey()==null||item.getKey()!=definition.id)throw new JSONException("Invalid indexed playlist ownership");String id=preferences.getString(referenceKey(definition.id),"playlist:"+origin+":"+definition.id);identity(id);
            if(rows.containsKey(id))throw new JSONException("Ambiguous saved-playlist references");
            rows.put(id,new JSONObject().put("id",id).put("name",definition.name==null?JSONObject.NULL:definition.name).put("members",new JSONArray(definition.members)));
        }
        String saved=PreferenceManager.getDefaultSharedPreferences(context).getString(PENDING,null);if(saved!=null){JSONObject pending=new JSONObject(saved);validate(pending);JSONArray values=pending.getJSONArray("rows");for(int i=0;i<values.length();i++){JSONObject row=values.getJSONObject(i);rows.putIfAbsent(row.getString("id"),row);}}
        JSONObject payload=new JSONObject().put("schema",1).put("rows",new JSONArray(rows.values()));validate(payload);return payload;
    }
    private PortableSavedPlaylists(){}
}
