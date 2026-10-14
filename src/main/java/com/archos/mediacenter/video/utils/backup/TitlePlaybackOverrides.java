package com.archos.mediacenter.video.utils.backup;

import android.content.Context;
import androidx.preference.PreferenceManager;
import com.archos.mediacenter.utils.videodb.VideoDbInfo;
import com.archos.mediacenter.video.browser.adapters.object.Video;
import com.archos.mediacenter.video.utils.VideoMetadata;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.*;
import org.json.*;

/** Private explicit player choices. No fabricated played timestamp, native history rewrite or locator export. */
public final class TitlePlaybackOverrides {
    private static final String PREFIX="supernova_title_playback_override:";
    public static String key(long id){return PREFIX+id;}
    public static boolean owns(String key){if(!key.matches(PREFIX+"[1-9][0-9]{0,18}"))return false;try{return Long.parseLong(key.substring(PREFIX.length()))>0;}catch(NumberFormatException invalid){return false;}}
    private static String guard(long id,String uri)throws JSONException {
        if(id<=0)throw new JSONException("Invalid receiving playback owner");try{StringBuilder value=new StringBuilder();for(byte b:MessageDigest.getInstance("SHA-256").digest(new JSONArray().put(id).put(uri==null?JSONObject.NULL:uri).toString().getBytes(StandardCharsets.UTF_8)))value.append(String.format(Locale.ROOT,"%02x",b&255));return value.toString();}catch(java.security.NoSuchAlgorithmException invalid){throw new JSONException("Playback identity guard unavailable");}
    }
    private static String uri(Video video){return video.getFileUri()==null?video.getFilePath():video.getFileUri().toString();}
    private static JSONObject choice(PortableTitlePlayback.State state)throws JSONException{return new JSONObject().put("params",state.params==null?JSONObject.NULL:state.params).put("delay",state.delay).put("ratio",state.ratio).put("language",state.language==null?JSONObject.NULL:state.language).put("subtitleNone",state.subtitleNone==null?JSONObject.NULL:state.subtitleNone);}
    private static JSONObject publicRow(String physical,PortableTitlePlayback.State state)throws JSONException{JSONObject row=new JSONObject().put("physical",physical);JSONObject values=choice(state);for(String field:List.of("params","delay","ratio","language","subtitleNone"))row.put(field,values.get(field));return row;}
    private static JSONObject record(Video video,String physical,PortableTitlePlayback.State state,boolean pending)throws JSONException{return new JSONObject().put("schema",1).put("guard",guard(video.getId(),uri(video))).put("physical",physical==null?JSONObject.NULL:physical).put("choice",choice(state)).put("pending",pending);}
    private static PortableTitlePlayback.State validate(JSONObject record)throws JSONException{
        if(record.length()!=5||!Integer.valueOf(1).equals(record.get("schema"))||!(record.get("guard") instanceof String)||!record.getString("guard").matches("[a-f0-9]{64}")||!(record.get("pending") instanceof Boolean)||!record.has("physical"))throw new JSONException("Invalid explicit playback recovery value");if(!record.isNull("physical"))PortablePhysicalReferences.validate(record.getString("physical"));JSONObject choice=record.getJSONObject("choice");if(choice.length()!=5)throw new JSONException("Invalid explicit playback choice fields");PortableTitlePlayback.State state=PortableTitlePlayback.state(choice,2);if(record.isNull("physical")&&(state.meaningful()||record.getBoolean("pending")))throw new JSONException("Missing explicit playback reference");return state;
    }
    public static final class Prepared {
        public final Map<String,Object> values;public final Set<String> keys;public final int unresolved;
        private Prepared(Map<String,Object> values,Set<String> keys,int unresolved){this.values=Collections.unmodifiableMap(values);this.keys=Collections.unmodifiableSet(keys);this.unresolved=unresolved;}
    }
    /** Neutral choices clear receiving state independently of history, even on an unplayed file. */
    public static Prepared prepare(Map<String,?> current,JSONObject payload,Map<Long,PortableTitlePlayback.State> receiving,PortablePhysicalReferences physical)throws JSONException {
        PortableTitlePlayback.validate(payload);Map<String,Object> values=new LinkedHashMap<>();Set<String> keys=new LinkedHashSet<>();keys.add(PortableTitlePlayback.PENDING);for(String key:current.keySet())if(owns(key))keys.add(key);
        for(var entry:receiving.entrySet()){Video video=physical.video(entry.getKey());if(video==null||entry.getValue()==null)throw new JSONException("Incomplete receiving playback inventory");String key=key(video.getId());keys.add(key);values.put(key,record(video,null,new PortableTitlePlayback.State(null,0,0,null),false).toString());}
        JSONArray unknown=new JSONArray(),rows=payload.getJSONArray("rows");int unresolved=0;for(int i=0;i<rows.length();i++){
            JSONObject row=rows.getJSONObject(i);PortableTitlePlayback.State state=PortableTitlePlayback.state(row,payload.getInt("schema"));Video video=physical.resolve(row.getString("physical"));
            if(video==null||!receiving.containsKey(video.getId())){unknown.put(publicRow(row.getString("physical"),state));unresolved++;continue;}
            boolean pending=state.meaningful();String key=key(video.getId());keys.add(key);values.put(key,record(video,row.getString("physical"),state,pending).toString());if(pending)unresolved++;
        }
        values.put(PortableTitlePlayback.PENDING,new JSONObject().put("schema",2).put("rows",unknown).toString());return new Prepared(values,keys,unresolved);
    }
    /** Export only public choice fields; an orphaned override keeps its original unresolved public reference. */
    public static JSONObject export(Context context,Map<Long,PortableTitlePlayback.State> indexed,PortablePhysicalReferences physical)throws JSONException{
        Map<Long,PortableTitlePlayback.State> effective=new LinkedHashMap<>(indexed);Map<String,JSONObject> orphaned=new LinkedHashMap<>();Map<String,?> preferences=PreferenceManager.getDefaultSharedPreferences(context).getAll();
        for(var item:preferences.entrySet())if(owns(item.getKey())){
            if(!(item.getValue() instanceof String))throw new JSONException("Invalid explicit playback store");long id=Long.parseLong(item.getKey().substring(PREFIX.length()));JSONObject saved=new JSONObject((String)item.getValue());PortableTitlePlayback.State state=validate(saved);Video video=physical.video(id);
            if(video!=null&&indexed.containsKey(id)&&saved.getString("guard").equals(guard(id,uri(video))))effective.put(id,state);
            else if(state.meaningful()&&!saved.isNull("physical"))orphaned.put(saved.getString("physical"),publicRow(saved.getString("physical"),state));
        }
        JSONObject payload=PortableTitlePlayback.export(context,effective,physical);Map<String,JSONObject> rows=new LinkedHashMap<>();JSONArray live=payload.getJSONArray("rows");for(int i=0;i<live.length();i++){JSONObject row=live.getJSONObject(i);rows.put(row.getString("physical"),row);}for(var row:orphaned.entrySet())rows.putIfAbsent(row.getKey(),row.getValue());payload.put("rows",new JSONArray(rows.values()));PortableTitlePlayback.validate(payload);return payload;
    }
    private static JSONObject receiving(Context context,VideoDbInfo info)throws JSONException{
        if(info==null||info.id<=0)return null;Object value=PreferenceManager.getDefaultSharedPreferences(context).getAll().get(key(info.id));if(value==null)return null;if(!(value instanceof String))throw new JSONException("Invalid explicit playback store");JSONObject saved=new JSONObject((String)value);validate(saved);return saved.getString("guard").equals(guard(info.id,info.uri==null?null:info.uri.toString()))?saved:null;
    }
    /** Clear stale native defaults while the restored indices await real backend enumeration. */
    public static boolean clearNativeDefaults(Context context,VideoDbInfo info)throws JSONException{
        if(receiving(context,info)==null)return false;info.audioTrack=-1;info.subtitleTrack=-1;info.subtitleDelay=0;info.subtitleRatio=0;info.subtitleLanguage=null;return true;
    }
    public static PortableTitlePlayback.State validatedChoice(Context context,VideoDbInfo info,VideoMetadata metadata)throws JSONException{
        JSONObject saved=receiving(context,info);if(saved==null)return null;PortableTitlePlayback.State source=validate(saved);if(!TitlePlaybackCompatibility.accepts(metadata,source))return null;Integer params=TitlePlaybackCompatibility.receivingParams(metadata,source);return new PortableTitlePlayback.State(params,source.delay,source.ratio,source.language,source.subtitleNone);
    }
    private TitlePlaybackOverrides(){}
}
