package com.archos.mediacenter.video.leanback;

import android.content.Context;
import com.archos.mediacenter.video.leanback.PreviewLibraryLoader.Snapshot;
import com.archos.mediacenter.video.utils.backup.PortableTitleReferences;
import java.util.*;
import org.json.*;

/** Positive Home definition schema. Watch Next membership is independently owned and never exported here. */
public final class HomeIntentCodec {
    public static final String PENDING="supernova_restore_pending_home_v2";
    private static void label(String value,int max)throws JSONException{
        if(value.length()>max||value.trim().isEmpty()||value.contains("://")||value.contains("/")||value.contains("\\")||value.matches("(?is).*(?:password|token|secret|authorization|cookie)\\s*[:=].*"))throw new JSONException("Home label needs privacy review before backup");
    }
    private static JSONArray refs(Set<String> keys,PortableTitleReferences identities)throws JSONException{
        JSONArray result=new JSONArray();Set<String> distinct=new LinkedHashSet<>();for(String key:keys)distinct.add(identities.reference(key));for(String ref:distinct)result.put(ref);return result;
    }
    public static JSONObject export(Context context,Snapshot snapshot)throws JSONException{
        PreviewHomeRows home=new PreviewHomeRows(context);PortableTitleReferences ids=new PortableTitleReferences(context,snapshot);JSONArray rows=new JSONArray();
        for(PreviewHomeRows.Row row:home.rows){
            label(row.name,row.custom()?40:100);
            JSONObject item=new JSONObject().put("id",row.id).put("name",row.name).put("visible",row.visible).put("dynamic",row.dynamic).put("movies",row.movies).put("tv",row.tv).put("genres",row.genreRule).put("sort",row.sort).put("icon",row.icon).put("matchAll",row.matchAll).put("descending",row.descending).put("maximum",row.maximum);
            item.put("members",row.id.equals("watchnext")?new JSONArray():refs(row.members,ids));item.put("calculated",row.id.equals("watchnext")?new JSONArray():refs(row.calculated,ids));rows.put(item);
        }
        String saved=androidx.preference.PreferenceManager.getDefaultSharedPreferences(context).getString(PENDING,null);
        if(saved!=null){JSONObject pending=new JSONObject(saved);validatePending(pending);JSONArray unresolved=pending.getJSONArray("rows");
            for(int i=0;i<unresolved.length();i++){JSONObject entry=unresolved.getJSONObject(i);for(int n=0;n<rows.length();n++){JSONObject row=rows.getJSONObject(n);if(!row.getString("id").equals(entry.getString("id"))||row.getString("id").equals("watchnext"))continue;
                for(String key:new String[]{"members","calculated"}){LinkedHashSet<String> values=new LinkedHashSet<>();JSONArray existing=row.getJSONArray(key),extra=entry.getJSONArray(key);for(int k=0;k<existing.length();k++)values.add(existing.getString(k));for(int k=0;k<extra.length();k++)values.add(extra.getString(k));row.put(key,new JSONArray(values));}
            }}
        }
        JSONObject payload=new JSONObject().put("schema",1).put("rows",rows);validate(payload);return payload;
    }
    private static void validatePending(JSONObject pending)throws JSONException{
        if(pending.length()!=2||pending.getInt("schema")!=1)throw new JSONException("Unsupported pending Home schema");JSONArray rows=pending.getJSONArray("rows");if(rows.length()>200)throw new JSONException("Too many pending Home rows");Set<String> ids=new HashSet<>();
        for(int i=0;i<rows.length();i++){JSONObject row=rows.getJSONObject(i);String id=row.getString("id");if(row.length()!=3||!ids.add(id)||!(Arrays.asList(PreviewHomeRows.SYSTEM).contains(id)||id.matches("custom:[a-zA-Z0-9-]{1,80}")))throw new JSONException("Invalid pending Home row");for(String key:new String[]{"members","calculated"}){JSONArray refs=row.getJSONArray(key);if(refs.length()>100000)throw new JSONException("Too many pending memberships");for(int n=0;n<refs.length();n++)PortableTitleReferences.validate(refs.getString(n));}}
    }
    public static void validate(JSONObject payload)throws JSONException{
        if(payload.length()!=2||payload.getInt("schema")!=1)throw new JSONException("Unsupported Home intent schema");
        JSONArray rows=payload.getJSONArray("rows");if(rows.length()>200)throw new JSONException("Too many Home rows");Set<String> ids=new HashSet<>();
        for(int i=0;i<rows.length();i++){JSONObject row=rows.getJSONObject(i);String id=row.getString("id");
            if(row.length()!=14||!ids.add(id)||!(Arrays.asList(PreviewHomeRows.SYSTEM).contains(id)||id.matches("custom:[a-zA-Z0-9-]{1,80}")))throw new JSONException("Invalid Home row identity");label(row.getString("name"),id.startsWith("custom:")?40:100);
            for(String key:new String[]{"visible","dynamic","movies","tv","matchAll","descending"})if(!(row.get(key) instanceof Boolean))throw new JSONException("Invalid Home row Boolean");
            if(!row.getBoolean("movies")&&!row.getBoolean("tv"))throw new JSONException("Empty Home media types");
            if(!Arrays.asList("added","title","year").contains(row.getString("sort")))throw new JSONException("Unsupported Home sort");
            int maximum=row.getInt("maximum");if(maximum<0||maximum>30||id.startsWith("custom:")&&!Arrays.asList(5,10,15,20,25,30).contains(maximum))throw new JSONException("Invalid Home maximum");
            String genres=row.getString("genres"),icon=row.getString("icon");if(genres.length()>2048||genres.contains("://")||genres.contains("/")||genres.contains("\\")||icon.length()>80||icon.contains("://"))throw new JSONException("Unsafe Home genre or icon value");
            for(String key:new String[]{"members","calculated"}){JSONArray refs=row.getJSONArray(key);if(refs.length()>100000||id.equals("watchnext")&&refs.length()>0)throw new JSONException("Watch Next membership belongs to its own category");Set<String> unique=new HashSet<>();for(int n=0;n<refs.length();n++){String ref=refs.getString(n);PortableTitleReferences.validate(ref);if(!unique.add(ref))throw new JSONException("Duplicate Home title reference");}}
        }
    }
    public static final class Prepared {
        public final String rows,pending;
        public final int unresolved;
        private Prepared(String rows,String pending,int unresolved){this.rows=rows;this.pending=pending;this.unresolved=unresolved;}
    }
    /** Returns staged values only. Selected Home intent cannot clear unselected Watch Next or media state. */
    public static Prepared prepare(Context destination,JSONObject payload,Snapshot snapshot)throws JSONException{
        validate(payload);Map<String,String> available=PortableTitleReferences.available(snapshot);PreviewHomeRows existing=new PreviewHomeRows(destination);
        Set<String> watchNext=new LinkedHashSet<>();for(PreviewHomeRows.Row row:existing.rows)if(row.id.equals("watchnext"))watchNext.addAll(row.members);
        JSONArray rows=new JSONArray(),pendingRows=new JSONArray();int unresolved=0;JSONArray definitions=payload.getJSONArray("rows");
        for(int i=0;i<definitions.length();i++){JSONObject source=definitions.getJSONObject(i),row=new JSONObject(source.toString()),pending=new JSONObject().put("id",source.getString("id"));
            for(String key:new String[]{"members","calculated"}){JSONArray resolved=new JSONArray(),unknown=new JSONArray(),refs=source.getJSONArray(key);
                for(int n=0;n<refs.length();n++){String ref=refs.getString(n),local=available.get(ref);if(local==null){unknown.put(ref);unresolved++;}else resolved.put(local);}
                if(source.getString("id").equals("watchnext")&&key.equals("members"))resolved=new JSONArray(watchNext);
                row.put(key,resolved);pending.put(key,unknown);
            }
            rows.put(row);if(pending.getJSONArray("members").length()+pending.getJSONArray("calculated").length()>0)pendingRows.put(pending);
        }
        // Present-empty resets definitions; preserve separately owned Watch Next membership.
        if(definitions.length()==0&& !watchNext.isEmpty())rows.put(new JSONObject().put("id","watchnext").put("name","Watch Next").put("members",new JSONArray(watchNext)));
        return new Prepared(rows.toString(),new JSONObject().put("schema",1).put("rows",pendingRows).toString(),unresolved);
    }
    private HomeIntentCodec(){}
}
