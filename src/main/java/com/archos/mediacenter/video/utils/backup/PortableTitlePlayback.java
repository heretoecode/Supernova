package com.archos.mediacenter.video.utils.backup;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import androidx.preference.PreferenceManager;
import com.archos.mediacenter.video.browser.adapters.object.Video;
import com.archos.mediaprovider.video.VideoOpenHelper;
import com.archos.mediaprovider.video.VideoStore;
import java.io.IOException;
import java.util.*;
import org.json.*;

/** Per-file player choices only. No offsets, watched state, timestamps, subtitle bytes or track labels. */
public final class PortableTitlePlayback {
    public static final String PENDING="supernova_restore_pending_title_playback_v2";
    public static final class State {
        public final Integer params;
        public final int delay,ratio;
        public final String language;
        public final Boolean subtitleNone;
        public State(Integer params,int delay,int ratio,String language) {
            this(params,delay,ratio,language,params!=null&&VideoStore.paramsToSubtitleTrack(params)>=0&&language!=null?Boolean.FALSE:null);
        }
        public State(Integer params,int delay,int ratio,String language,Boolean subtitleNone) {
            if(params!=null&&(params<0||params>0xfffff)||ratio<0||ratio>2||
                    language!=null&&!language.matches("(?:[a-z]{2}|srt|vtt)")||subtitleNone!=null&&(params==null||VideoStore.paramsToSubtitleTrack(params)<0)||Boolean.TRUE.equals(subtitleNone)&&language!=null)
                throw new IllegalArgumentException("Unsupported per-title playback choice");
            this.params=params;this.delay=delay;this.ratio=ratio;this.language=language;this.subtitleNone=subtitleNone;
        }
        private boolean meaningful(){return params!=null||delay!=0||ratio!=0||language!=null;}
    }
    private static int integer(Cursor rows,int index,int fallback)throws IOException {
        if(rows.isNull(index))return fallback;
        if(rows.getType(index)!=Cursor.FIELD_TYPE_INTEGER||rows.getLong(index)<Integer.MIN_VALUE||rows.getLong(index)>Integer.MAX_VALUE)
            throw new IOException("Invalid indexed per-title playback integer");
        return rows.getInt(index);
    }
    /** A timestamp is read only to recognize the existing zero-valued track choice; it is never exported. */
    public static Map<Long,State> read(SQLiteDatabase database)throws IOException {
        Map<Long,State> result=new LinkedHashMap<>();
        try(Cursor rows=database.query(VideoOpenHelper.FILES_TABLE_NAME,new String[]{"_id",
                VideoStore.Video.VideoColumns.ARCHOS_PLAYER_PARAMS,VideoStore.Video.VideoColumns.ARCHOS_PLAYER_SUBTITLE_DELAY,
                VideoStore.Video.VideoColumns.ARCHOS_PLAYER_SUBTITLE_RATIO,VideoStore.Video.VideoColumns.ARCHOS_SUBTITLE_LANGUAGE,
                VideoStore.Video.VideoColumns.ARCHOS_LAST_TIME_PLAYED,VideoStore.Video.VideoColumns.ARCHOS_NUMBER_OF_SUBTITLE_TRACKS},"media_type = ?",new String[]{"3"},null,null,"_id ASC")) {
            while(rows.moveToNext()) {
                long id=rows.getLong(0);int params=integer(rows,1,0);
                if(result.size()>=100000||rows.getType(0)!=Cursor.FIELD_TYPE_INTEGER||id<=0||params<0||params>0xfffff||
                        !rows.isNull(4)&&rows.getType(4)!=Cursor.FIELD_TYPE_STRING||
                        !rows.isNull(5)&&rows.getType(5)!=Cursor.FIELD_TYPE_INTEGER)
                    throw new IOException("Invalid indexed per-title playback state");
                String language=rows.isNull(4)?null:rows.getString(4);if(language!=null&&language.isEmpty())language=null;
                Integer explicit=params!=0||!rows.isNull(5)&&rows.getLong(5)>0?params:null;int subtitle=explicit==null?-1:VideoStore.paramsToSubtitleTrack(explicit),count=integer(rows,6,-1);if(count< -1)throw new IOException("Invalid indexed subtitle count");Boolean none=subtitle<0?null:language!=null?Boolean.FALSE:count>=0&&subtitle==count?Boolean.TRUE:count>=0&&subtitle<count?Boolean.FALSE:null;
                result.put(id,new State(explicit,integer(rows,2,0),integer(rows,3,0),language,none));
            }
        }catch(RuntimeException invalid){throw new IOException("Per-title playback inventory unavailable",invalid);}
        return Collections.unmodifiableMap(result);
    }
    private static boolean integer(Object value){return value instanceof Integer||value instanceof Long;}
    private static State state(JSONObject row,int schema)throws JSONException {
        if(!row.has("params")||!row.has("language")||!integer(row.get("delay"))||!integer(row.get("ratio"))||
                !row.isNull("params")&&!integer(row.get("params"))||!row.isNull("language")&&!(row.get("language") instanceof String))
            throw new JSONException("Invalid per-title playback fields");
        long delay=row.getLong("delay"),ratio=row.getLong("ratio"),params=row.isNull("params")?0:row.getLong("params");
        if(delay<Integer.MIN_VALUE||delay>Integer.MAX_VALUE||ratio<0||ratio>2||params<0||params>0xfffff)
            throw new JSONException("Unsupported per-title playback range");
        if(schema==2&&!row.has("subtitleNone")||schema==2&&!row.isNull("subtitleNone")&&!(row.get("subtitleNone") instanceof Boolean))throw new JSONException("Invalid subtitle selection intent");
        try{return schema==1?new State(row.isNull("params")?null:(int)params,(int)delay,(int)ratio,row.isNull("language")?null:row.getString("language")):new State(row.isNull("params")?null:(int)params,(int)delay,(int)ratio,row.isNull("language")?null:row.getString("language"),row.isNull("subtitleNone")?null:row.getBoolean("subtitleNone"));}
        catch(IllegalArgumentException invalid){throw new JSONException("Unsupported per-title playback choice");}
    }
    public static void validate(JSONObject payload)throws JSONException {
        if(payload.length()!=2||!integer(payload.get("schema"))||!List.of(1L,2L).contains(payload.getLong("schema")))
            throw new JSONException("Unsupported per-title playback schema");
        JSONArray rows=payload.getJSONArray("rows");if(rows.length()>100000)throw new JSONException("Too many per-title playback choices");Set<String> seen=new HashSet<>();
        for(int i=0;i<rows.length();i++) {
            JSONObject row=rows.getJSONObject(i);if(row.length()!=(payload.getInt("schema")==1?5:6)||!(row.get("physical") instanceof String))throw new JSONException("Invalid per-title playback fields");
            String ref=row.getString("physical");PortablePhysicalReferences.validate(ref);state(row,payload.getInt("schema"));
            if(!seen.add(ref))throw new JSONException("Conflicting per-title playback choices");
        }
    }
    public static JSONObject export(Context context,Map<Long,State> indexed,PortablePhysicalReferences references)throws JSONException {
        Map<String,JSONObject> rows=new LinkedHashMap<>();for(var entry:indexed.entrySet()) {
            if(entry.getKey()==null||entry.getKey()<=0||entry.getValue()==null)throw new JSONException("Invalid indexed playback identity");
            State value=entry.getValue();if(!value.meaningful())continue;String ref=references.reference(entry.getKey());
            rows.put(ref,new JSONObject().put("physical",ref).put("params",value.params==null?JSONObject.NULL:value.params)
                    .put("delay",value.delay).put("ratio",value.ratio).put("language",value.language==null?JSONObject.NULL:value.language).put("subtitleNone",value.subtitleNone==null?JSONObject.NULL:value.subtitleNone));
        }
        String pending=PreferenceManager.getDefaultSharedPreferences(context).getString(PENDING,null);
        if(pending!=null){JSONObject saved=new JSONObject(pending);validate(saved);JSONArray unknown=saved.getJSONArray("rows");for(int i=0;i<unknown.length();i++){JSONObject row=new JSONObject(unknown.getJSONObject(i).toString());State value=state(row,saved.getInt("schema"));row.put("subtitleNone",value.subtitleNone==null?JSONObject.NULL:value.subtitleNone);rows.putIfAbsent(row.getString("physical"),row);}}
        JSONObject payload=new JSONObject().put("schema",2).put("rows",new JSONArray(rows.values()));validate(payload);return payload;
    }
    public interface TrackCompatibility { boolean accepts(long receivingVideoId,State choice); }
    public static final class Prepared {
        public final Map<Long,State> choices;
        public final String pending;
        public final int unresolved;
        private Prepared(Map<Long,State> choices,JSONArray unknown)throws JSONException {
            this.choices=Collections.unmodifiableMap(choices);pending=new JSONObject().put("schema",2).put("rows",unknown).toString();unresolved=unknown.length();
        }
    }
    /** Pure replacement. No guessed index remapping, invented playback timestamp, or filename opt-in. */
    public static Prepared prepare(JSONObject payload,Map<Long,State> receiving,PortablePhysicalReferences references,
            boolean matchingValidated,TrackCompatibility compatibility)throws JSONException {
        validate(payload);Map<Long,State> choices=new LinkedHashMap<>();for(var entry:receiving.entrySet()) {
            if(entry.getKey()==null||entry.getKey()<=0||entry.getValue()==null)throw new JSONException("Invalid receiving playback identity");
            choices.put(entry.getKey(),new State(null,0,0,null));
        }
        JSONArray unknown=new JSONArray(),rows=payload.getJSONArray("rows");for(int i=0;i<rows.length();i++) {
            JSONObject row=rows.getJSONObject(i);State value=state(row,payload.getInt("schema"));Video target=references.resolve(row.getString("physical"));
            if(!matchingValidated||target==null||!choices.containsKey(target.getId())||compatibility==null||!compatibility.accepts(target.getId(),value)) {
                unknown.put(new JSONObject(row.toString()).put("subtitleNone",value.subtitleNone==null?JSONObject.NULL:value.subtitleNone));continue;
            }
            choices.put(target.getId(),value);
        }
        return new Prepared(choices,unknown);
    }
    private PortableTitlePlayback(){}
}
