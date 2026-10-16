package com.archos.mediacenter.video.leanback;

import android.content.Context;
import android.content.SharedPreferences;
import androidx.preference.PreferenceManager;
import com.archos.mediacenter.video.leanback.PreviewLibraryLoader.Snapshot;
import com.archos.mediacenter.video.utils.backup.PortableTitleReferences;
import java.util.*;
import org.json.*;

/** User queue/dismissal intent only; does not own history, playback positions or media records. */
public final class PortableViewingChoices {
    public static final String QUEUE_PENDING="supernova_restore_pending_watch_next_v2";
    public static final String DISMISS_PENDING="supernova_restore_pending_continue_v2";
    public static final String DISMISS_PREFIX="preview_cw_dismiss:";
    public enum Kind { WATCH_NEXT, CONTINUE_CHOICES }
    private static String pendingKey(Kind kind){return kind==Kind.WATCH_NEXT?QUEUE_PENDING:DISMISS_PENDING;}
    private static JSONObject payload(Kind kind,Map<String,Long> items)throws JSONException{
        JSONArray choices=new JSONArray();for(Map.Entry<String,Long> entry:items.entrySet()){
            if(kind==Kind.WATCH_NEXT)choices.put(entry.getKey());else choices.put(new JSONObject().put("ref",entry.getKey()).put("dismissedAt",entry.getValue()));
        }return new JSONObject().put("schema",1).put("kind",kind.name()).put("choices",choices);
    }
    public static Map<String,Long> validate(JSONObject payload,Kind kind)throws JSONException{
        Object version=payload.get("schema");if(payload.length()!=3||!(version instanceof Integer||version instanceof Long)||((Number)version).longValue()!=1||!kind.name().equals(payload.get("kind")))throw new JSONException("Unsupported viewing choice schema");
        JSONArray choices=payload.getJSONArray("choices");if(choices.length()>100000)throw new JSONException("Too many viewing choices");Map<String,Long> result=new LinkedHashMap<>();
        for(int i=0;i<choices.length();i++){
            String ref;long time=0;
            if(kind==Kind.WATCH_NEXT){if(!(choices.get(i) instanceof String))throw new JSONException("Queue reference must be text");ref=choices.getString(i);}
            else{JSONObject choice=choices.getJSONObject(i);Object timestamp=choice.get("dismissedAt");if(choice.length()!=2||!(choice.get("ref") instanceof String)||!(timestamp instanceof Integer||timestamp instanceof Long)||((Number)timestamp).longValue()<0)throw new JSONException("Invalid Continue Watching dismissal");ref=choice.getString("ref");time=((Number)timestamp).longValue();}
            PortableTitleReferences.validate(ref);if(result.put(ref,time)!=null)throw new JSONException("Duplicate viewing choice");
        }return Collections.unmodifiableMap(result);
    }
    public static JSONObject export(Context context,Snapshot snapshot,Kind kind)throws JSONException{
        SharedPreferences prefs=PreferenceManager.getDefaultSharedPreferences(context);PortableTitleReferences refs=new PortableTitleReferences(context,snapshot);Map<String,Long> items=new LinkedHashMap<>();
        if(kind==Kind.WATCH_NEXT){for(PreviewHomeRows.Row row:new PreviewHomeRows(context).rows)if(row.id.equals("watchnext"))for(String key:row.members)items.put(refs.reference(key),0L);}
        else{for(Map.Entry<String,?> entry:prefs.getAll().entrySet())if(entry.getKey().startsWith(DISMISS_PREFIX)){
            String key=entry.getKey().substring(DISMISS_PREFIX.length());Object value=entry.getValue();if(!key.matches("[vs][1-9][0-9]*")||!(value instanceof Long||value instanceof Integer)||((Number)value).longValue()<0)throw new JSONException("Unsupported saved Continue Watching choice");
            String ref=refs.reference(key);items.merge(ref,((Number)value).longValue(),Math::max);
        }}
        String saved=prefs.getString(pendingKey(kind),null);if(saved!=null)for(Map.Entry<String,Long> entry:validate(new JSONObject(saved),kind).entrySet())items.merge(entry.getKey(),entry.getValue(),Math::max);
        JSONObject result=payload(kind,items);validate(result,kind);return result;
    }
    public static final class Prepared {
        public final Map<String,Object> values;public final Set<String> keys;public final int unresolved;
        private Prepared(Map<String,Object> values,Set<String> keys,int unresolved){this.values=Collections.unmodifiableMap(values);this.keys=Collections.unmodifiableSet(keys);this.unresolved=unresolved;}
    }
    /** A staged Home definition may be supplied when restoring both independently selected categories. */
    public static Prepared prepare(Context destination,JSONObject payload,Snapshot snapshot,Kind kind,String stagedHome)throws JSONException{
        Map<String,Long> choices=validate(payload,kind),unknown=new LinkedHashMap<>();Map<String,String> available=PortableTitleReferences.available(snapshot);
        Map<String,Object> values=new LinkedHashMap<>();Set<String> keys=new LinkedHashSet<>();JSONArray resolved=new JSONArray();
        if(kind==Kind.CONTINUE_CHOICES)for(String key:PreferenceManager.getDefaultSharedPreferences(destination).getAll().keySet())if(key.startsWith(DISMISS_PREFIX)&&key.substring(DISMISS_PREFIX.length()).matches("[vs][1-9][0-9]*"))keys.add(key);
        for(Map.Entry<String,Long> entry:choices.entrySet()){
            String local=available.get(entry.getKey());if(local==null){unknown.put(entry.getKey(),entry.getValue());continue;}
            if(kind==Kind.WATCH_NEXT)resolved.put(local);else{String key=DISMISS_PREFIX+local;keys.add(key);values.put(key,entry.getValue());}
        }
        if(kind==Kind.WATCH_NEXT){
            JSONArray rows=new JSONArray(stagedHome==null?new PreviewHomeRows(destination).encode():stagedHome);JSONObject queue=null;
            for(int i=0;i<rows.length();i++)if(rows.getJSONObject(i).getString("id").equals("watchnext")){queue=rows.getJSONObject(i);break;}
            if(queue==null){queue=new JSONObject().put("id","watchnext").put("name","Watch Next");rows.put(queue);}
            queue.put("members",resolved).put("calculated",new JSONArray());keys.add("preview_home_rows41");values.put("preview_home_rows41",rows.toString());
        }
        keys.add(pendingKey(kind));values.put(pendingKey(kind),payload(kind,unknown).toString());return new Prepared(values,keys,unknown.size());
    }
    private PortableViewingChoices(){}
}
