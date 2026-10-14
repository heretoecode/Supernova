package com.archos.mediacenter.video.utils.backup;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import com.archos.mediaprovider.video.VideoOpenHelper;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.*;
import org.json.*;

/** Read-only preview precondition for explicit preference overlays; never mutates the media store. */
public final class TitlePlaybackInventory {
    public final Map<Long,PortableTitlePlayback.State> choices;
    public final String signature;
    private TitlePlaybackInventory(Map<Long,PortableTitlePlayback.State> choices,JSONObject signature){this.choices=choices;this.signature=signature.toString();}
    public static TitlePlaybackInventory capture(Context context)throws IOException,JSONException{
        File file=VideoOpenHelper.getDatabaseFile(context);JSONObject image=new JSONObject().put("exists",file.exists()).put("rows",new JSONArray());if(!file.exists())return new TitlePlaybackInventory(Collections.emptyMap(),image);
        try(SQLiteDatabase database=SQLiteDatabase.openDatabase(file.getPath(),null,SQLiteDatabase.OPEN_READONLY)){
            if(database.getVersion()!=VideoOpenHelper.getDatabaseVersion())throw new IOException("Receiving title playback schema is incompatible");database.execSQL("SAVEPOINT supernova_title_playback_read");try{
                Map<Long,PortableTitlePlayback.State> choices=PortableTitlePlayback.read(database);Map<Long,String> guards=new HashMap<>();try(Cursor rows=database.query("files",new String[]{"_id","_data"},"media_type=? AND (_data IS NULL OR _data NOT LIKE ?)",new String[]{"3","putio:%"},null,null,"_id ASC")){
                    while(rows.moveToNext()){if(rows.getType(0)!=Cursor.FIELD_TYPE_INTEGER||!rows.isNull(1)&&rows.getType(1)!=Cursor.FIELD_TYPE_STRING)throw new IOException("Invalid receiving playback identity");String uri=rows.isNull(1)?null:rows.getString(1);StringBuilder hash=new StringBuilder();try{for(byte b:MessageDigest.getInstance("SHA-256").digest(new JSONArray().put(uri==null?JSONObject.NULL:uri).toString().getBytes(StandardCharsets.UTF_8)))hash.append(String.format(Locale.ROOT,"%02x",b&255));}catch(java.security.NoSuchAlgorithmException invalid){throw new IOException("Receiving identity guard unavailable",invalid);}if(guards.put(rows.getLong(0),hash.toString())!=null)throw new IOException("Duplicate receiving playback identity");}
                }
                JSONArray rows=image.getJSONArray("rows");for(var item:choices.entrySet()){String guard=guards.get(item.getKey());if(guard==null)throw new IOException("Incomplete receiving playback identity");var state=item.getValue();rows.put(new JSONObject().put("id",item.getKey()).put("guard",guard).put("params",state.params==null?JSONObject.NULL:state.params).put("delay",state.delay).put("ratio",state.ratio).put("language",state.language==null?JSONObject.NULL:state.language).put("subtitleNone",state.subtitleNone==null?JSONObject.NULL:state.subtitleNone));}return new TitlePlaybackInventory(choices,image);
            }finally{database.execSQL("RELEASE SAVEPOINT supernova_title_playback_read");}
        }catch(RuntimeException invalid){throw new IOException("Receiving title playback inventory unavailable",invalid);}
    }
    private TitlePlaybackInventory(){throw new AssertionError();}
}
