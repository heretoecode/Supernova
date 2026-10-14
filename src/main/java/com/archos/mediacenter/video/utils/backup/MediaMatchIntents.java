package com.archos.mediacenter.video.utils.backup;

import android.content.Context;
import androidx.preference.PreferenceManager;
import java.util.*;
import org.json.*;

/** Guarded selected identification intent; native canonical application is a separate participant. */
public final class MediaMatchIntents {
    private static final String PREFIX="supernova_match_intent:";
    public static String key(long id){if(id<=0)throw new IllegalArgumentException("Invalid match owner");return PREFIX+id;}
    public static boolean owns(String key){return key!=null&&key.matches("supernova_match_intent:[1-9][0-9]{0,18}");}
    private static JSONObject choice(PortableMediaMatches.Match match)throws JSONException{return match==null?null:new JSONObject().put("identity",match.identity).put("episodeProvider",match.episodeProvider==null?JSONObject.NULL:match.episodeProvider);}
    private static PortableMediaMatches.Match match(JSONObject value)throws JSONException {
        if(value.length()!=2||!(value.get("identity") instanceof String)||!value.has("episodeProvider"))throw new JSONException("Invalid private match choice");Object episode=value.get("episodeProvider");if(episode!=JSONObject.NULL&&(!(episode instanceof Integer||episode instanceof Long)||((Number)episode).longValue()<=0))throw new JSONException("Invalid private episode identity");return new PortableMediaMatches.Match(value.getString("identity"),episode==JSONObject.NULL?null:((Number)episode).longValue());
    }
    private static boolean same(PortableMediaMatches.Match left,PortableMediaMatches.Match right){return left==null?right==null:right!=null&&left.identity.equals(right.identity)&&Objects.equals(left.episodeProvider,right.episodeProvider);}
    static boolean satisfies(PortableMediaMatches.Match actual,PortableMediaMatches.Match requested){return requested==null?actual==null:actual!=null&&actual.identity.equals(requested.identity)&&(requested.episodeProvider==null||Objects.equals(actual.episodeProvider,requested.episodeProvider));}
    private static long integer(Object value,long minimum)throws JSONException{if(!(value instanceof Integer||value instanceof Long)||((Number)value).longValue()<minimum)throw new JSONException("Invalid private match integer");return ((Number)value).longValue();}
    public static void validate(JSONObject record)throws JSONException {
        long schema=integer(record.get("schema"),1);if(schema!=1&&schema!=2||record.length()!=(schema==2?7:6)||!(record.get("guard") instanceof String)||!record.getString("guard").matches("[a-f0-9]{64}")||!(record.get("physical") instanceof String)||!(record.get("pending") instanceof Boolean)||!record.has("requested"))throw new JSONException("Invalid private match intent");if(schema==2)integer(record.get("manualAt"),0);PortablePhysicalReferences.validate(record.getString("physical"));if(!record.isNull("requested"))match(record.getJSONObject("requested"));JSONObject before=record.getJSONObject("before");if(before.length()!=3)throw new JSONException("Invalid private match originals");integer(before.get("type"),-1);integer(before.get("local"),-1);if(!before.has("choice"))throw new JSONException("Missing private match originals");if(!before.isNull("choice"))match(before.getJSONObject("choice"));
    }
    private static JSONObject record(MediaMatchInventory.Owner owner,String physical,PortableMediaMatches.Match requested,long manualAt)throws JSONException {
        JSONObject before=new JSONObject().put("type",owner.type).put("local",owner.localId).put("choice",owner.match==null?JSONObject.NULL:choice(owner.match));JSONObject result=new JSONObject().put("schema",2).put("manualAt",manualAt).put("guard",owner.guard).put("physical",physical).put("before",before).put("requested",requested==null?JSONObject.NULL:choice(requested)).put("pending",!satisfies(owner.match,requested));validate(result);return result;
    }
    public static final class Prepared {
        public final Set<String> keys;public final Map<String,Object> values;public final int unresolved;
        private Prepared(Set<String> keys,Map<String,Object> values,int unresolved){this.keys=Collections.unmodifiableSet(keys);this.values=Collections.unmodifiableMap(values);this.unresolved=unresolved;}
    }
    /** Selected replacement also expresses explicit clearing of receiving matches omitted by the payload. */
    public static Prepared prepare(Map<String,?> preferences,JSONObject payload,MediaMatchInventory inventory,PortablePhysicalReferences physical)throws java.io.IOException,JSONException {
        return prepare(preferences,payload,inventory,physical,MediaMatchManualChanges.epoch(preferences));
    }
    static Prepared prepare(Map<String,?> preferences,JSONObject payload,MediaMatchInventory inventory,PortablePhysicalReferences physical,long manualAt)throws java.io.IOException,JSONException {
        PortableMediaMatches.validate(payload);inventory.validatePhysical(physical);Set<String> keys=new LinkedHashSet<>();for(String name:preferences.keySet())if(owns(name)){Object value=preferences.get(name);if(!(value instanceof String))throw new JSONException("Invalid saved match intent");validate(new JSONObject((String)value));keys.add(name);}keys.add(PortableMediaMatches.PENDING);
        Map<Long,JSONObject> incoming=new LinkedHashMap<>();JSONArray pending=new JSONArray(),rows=payload.getJSONArray("rows");for(int i=0;i<rows.length();i++){JSONObject row=rows.getJSONObject(i);var video=physical.resolve(row.getString("physical"));if(video==null||!inventory.owners.containsKey(video.getId())){pending.put(new JSONObject(row.toString()));continue;}if(incoming.put(video.getId(),row)!=null)throw new JSONException("Multiple restored references target the same receiving file");}
        Map<String,Object> values=new LinkedHashMap<>();int unresolved=pending.length();for(var owner:inventory.owners.values()){JSONObject row=incoming.get(owner.videoId);if(row==null&&owner.match==null)continue;String reference=row==null?physical.reference(owner.videoId):row.getString("physical");PortableMediaMatches.Match requested=row==null?null:match(new JSONObject().put("identity",row.get("identity")).put("episodeProvider",row.get("episodeProvider")));JSONObject intent=record(owner,reference,requested,manualAt);keys.add(key(owner.videoId));values.put(key(owner.videoId),intent.toString());if(intent.getBoolean("pending"))unresolved++;}
        values.put(PortableMediaMatches.PENDING,PortableMediaMatches.pending(pending,manualAt).toString());return new Prepared(keys,values,unresolved);
    }
    static boolean eligible(JSONObject saved,MediaMatchInventory.Owner owner,Map<String,?> preferences)throws JSONException {
        if(owner==null||!saved.getString("guard").equals(owner.guard))return false;if(saved.getInt("schema")==2&&MediaMatchManualChanges.after(preferences,owner.videoId,saved.getLong("manualAt")))return false;JSONObject before=saved.getJSONObject("before");PortableMediaMatches.Match original=before.isNull("choice")?null:match(before.getJSONObject("choice")),requested=saved.isNull("requested")?null:match(saved.getJSONObject("requested"));return saved.getInt("schema")==2&&saved.getBoolean("pending")||owner.type==before.getLong("type")&&owner.localId==before.getLong("local")&&same(owner.match,original)||satisfies(owner.match,requested);
    }
    /** Pending bound intent wins over stale native identification, while a later receiving choice wins over intent. */
    public static JSONObject export(Context context,Map<Long,PortableMediaMatches.Match> indexed,MediaMatchInventory inventory,PortablePhysicalReferences physical)throws JSONException {
        JSONObject payload=PortableMediaMatches.export(context,indexed,physical);Map<String,JSONObject> rows=new LinkedHashMap<>();JSONArray live=payload.getJSONArray("rows");for(int i=0;i<live.length();i++){JSONObject row=live.getJSONObject(i);rows.put(row.getString("physical"),row);}
        Map<String,?> preferences=PreferenceManager.getDefaultSharedPreferences(context).getAll();
        for(var entry:preferences.entrySet())if(owns(entry.getKey())){if(!(entry.getValue() instanceof String))throw new JSONException("Invalid saved match intent");long id=Long.parseLong(entry.getKey().substring(PREFIX.length()));JSONObject saved=new JSONObject((String)entry.getValue());validate(saved);MediaMatchInventory.Owner owner=inventory.owners.get(id);String ref=saved.getString("physical");if(owner==null||!saved.getString("guard").equals(owner.guard)){if(!saved.isNull("requested")){JSONObject orphan=new JSONObject(saved.getJSONObject("requested").toString()).put("physical",ref),present=rows.get(ref);if(present!=null&&(!present.getString("identity").equals(orphan.getString("identity"))||!Objects.equals(present.get("episodeProvider"),orphan.get("episodeProvider"))))throw new JSONException("Unresolved identification conflicts with a reused physical reference");rows.putIfAbsent(ref,orphan);}continue;}if(!eligible(saved,owner,preferences))continue;PortableMediaMatches.Match requested=saved.isNull("requested")?null:match(saved.getJSONObject("requested"));if(satisfies(owner.match,requested))continue;String receiving=physical.reference(id);rows.remove(receiving);rows.remove(ref);if(!saved.isNull("requested"))rows.put(ref,new JSONObject(saved.getJSONObject("requested").toString()).put("physical",ref));}
        payload.put("rows",new JSONArray(rows.values()));PortableMediaMatches.validate(payload);return payload;
    }
    private MediaMatchIntents(){}
}
