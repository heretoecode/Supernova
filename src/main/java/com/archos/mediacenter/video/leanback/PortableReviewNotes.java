package com.archos.mediacenter.video.leanback;

import android.content.Context;
import android.content.SharedPreferences;
import com.archos.mediacenter.video.browser.adapters.object.*;
import com.archos.mediacenter.video.utils.backup.PortablePhysicalReferences;
import com.archos.mediaprovider.video.SupernovaLibraryPolicy;
import java.util.*;
import org.json.*;

/** Deliberate incorrect-match flags only; system health/offline/missing caches are never portable. */
public final class PortableReviewNotes {
    public static final String PENDING="supernova_restore_pending_review_v2";
    private static String expected(Video video){
        if(video instanceof Movie&&((Movie)video).getOnlineId()>0)return "tmdb:movie:"+((Movie)video).getOnlineId();
        if(video instanceof Episode){Episode episode=(Episode)video;if(episode.getOnlineId()>0&&episode.getSeasonNumber()>=0&&episode.getEpisodeNumber()>0)return "tmdb:tv:"+episode.getOnlineId()+":"+episode.getSeasonNumber()+":"+episode.getEpisodeNumber();}
        return null;
    }
    private static String key(JSONObject row)throws JSONException{return row.getString("physical")+"|"+(row.isNull("expected")?"none":row.getString("expected"));}
    public static void validate(JSONObject payload)throws JSONException{
        Object version=payload.get("schema");if(payload.length()!=2||!(version instanceof Integer||version instanceof Long)||((Number)version).longValue()!=1)throw new JSONException("Unsupported review schema");JSONArray rows=payload.getJSONArray("rows");if(rows.length()>100000)throw new JSONException("Too many review notes");Set<String> keys=new HashSet<>();
        for(int i=0;i<rows.length();i++){
            JSONObject row=rows.getJSONObject(i);if(row.length()!=3||!(row.get("physical") instanceof String)||!row.has("expected")||!(row.get("incorrect") instanceof Boolean))throw new JSONException("Invalid review fields");PortablePhysicalReferences.validate(row.getString("physical"));
            if(!row.isNull("expected")){
                if(!(row.get("expected") instanceof String)||!row.getString("expected").matches("tmdb:(?:movie:[1-9][0-9]{0,18}|tv:[1-9][0-9]{0,18}:(?:0|[1-9][0-9]{0,9}):[1-9][0-9]{0,9})"))throw new JSONException("Invalid expected match");
                String[] parts=row.getString("expected").split(":");try{Long.parseLong(parts[2]);if(parts.length==5){Integer.parseInt(parts[3]);Integer.parseInt(parts[4]);}}catch(NumberFormatException invalid){throw new JSONException("Expected match exceeds supported identity range");}
            }
            if(!keys.add(key(row)))throw new JSONException("Duplicate review note");
        }
    }
    public static JSONObject export(Context context,PortablePhysicalReferences references)throws JSONException{
        SharedPreferences health=context.getSharedPreferences(SupernovaLibraryPolicy.HEALTH,0);Map<String,JSONObject> rows=new LinkedHashMap<>();
        for(Map.Entry<String,?> entry:health.getAll().entrySet())if(entry.getKey().startsWith("incorrect:")){
            if(!entry.getKey().matches("incorrect:[1-9][0-9]{0,18}")||!(entry.getValue() instanceof Boolean))throw new JSONException("Saved review note needs inspection");long id;try{id=Long.parseLong(entry.getKey().substring(10));}catch(NumberFormatException invalid){throw new JSONException("Review identity exceeds supported range");}
            JSONObject row=new JSONObject().put("physical",references.reference(id)).put("expected",expected(references.video(id))==null?JSONObject.NULL:expected(references.video(id))).put("incorrect",entry.getValue());rows.put(key(row),row);
        }
        String pending=health.getString(PENDING,null);if(pending!=null){JSONObject saved=new JSONObject(pending);validate(saved);JSONArray unresolved=saved.getJSONArray("rows");for(int i=0;i<unresolved.length();i++){JSONObject row=unresolved.getJSONObject(i);rows.putIfAbsent(key(row),row);}}
        JSONObject payload=new JSONObject().put("schema",1).put("rows",new JSONArray(rows.values()));validate(payload);return payload;
    }
    public static final class Prepared {
        public final Map<String,Object> values;public final Set<String> keys;public final int unresolved;
        private Prepared(Map<String,Object> values,Set<String> keys,int unresolved){this.values=Collections.unmodifiableMap(values);this.keys=Collections.unmodifiableSet(keys);this.unresolved=unresolved;}
    }
    /** Staged named-store edits only; apply requires the combined multi-store recovery engine. */
    public static Prepared prepare(Context destination,JSONObject payload,PortablePhysicalReferences references)throws JSONException{
        validate(payload);SharedPreferences health=destination.getSharedPreferences(SupernovaLibraryPolicy.HEALTH,0);Set<String> keys=new LinkedHashSet<>();Map<String,Object> values=new LinkedHashMap<>();JSONArray pending=new JSONArray(),rows=payload.getJSONArray("rows");
        for(String key:health.getAll().keySet())if(key.matches("incorrect:[1-9][0-9]{0,18}"))keys.add(key);
        for(int i=0;i<rows.length();i++){
            JSONObject row=rows.getJSONObject(i);Video target=references.resolve(row.getString("physical"));String match=row.isNull("expected")?null:row.getString("expected");
            if(target==null||!Objects.equals(expected(target),match)){pending.put(new JSONObject(row.toString()));continue;}
            String key="incorrect:"+target.getId();keys.add(key);values.put(key,row.getBoolean("incorrect"));
        }
        keys.add(PENDING);values.put(PENDING,new JSONObject().put("schema",1).put("rows",pending).toString());return new Prepared(values,keys,pending.length());
    }
    private PortableReviewNotes(){}
}
