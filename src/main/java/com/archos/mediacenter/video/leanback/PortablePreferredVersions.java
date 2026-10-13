package com.archos.mediacenter.video.leanback;

import android.content.Context;
import androidx.preference.PreferenceManager;
import com.archos.mediacenter.video.browser.adapters.object.Video;
import com.archos.mediacenter.video.leanback.filebrowsing.BrowserSelection;
import com.archos.mediacenter.video.utils.backup.PortablePhysicalReferences;
import java.util.*;
import org.json.*;

/** Manual physical-version intent without exporting local IDs, preference path keys or stored URI values. */
public final class PortablePreferredVersions {
    public static final String PREFIX="preview_version_choice:",PENDING="supernova_restore_pending_versions_v2";
    private static String expected(String choice)throws JSONException{
        if(choice.startsWith("file:")){String identity=ViewingHistory.identity(false,null,-1,-1,choice.substring(5));if(identity==null)throw new JSONException("Unmatched version parent needs a valid indexed location");return identity;}
        if(choice.matches("movie:[1-9][0-9]{0,18}")){try{Long.parseLong(choice.substring(6));}catch(NumberFormatException e){throw new JSONException("Version title exceeds supported range");}return "tmdb:"+choice;}
        if(choice.matches("episode:[1-9][0-9]{0,18}:(?:0|[1-9][0-9]{0,9}):[1-9][0-9]{0,9}")){
            String[] parts=choice.split(":");try{Long.parseLong(parts[1]);Integer.parseInt(parts[2]);Integer.parseInt(parts[3]);}catch(NumberFormatException e){throw new JSONException("Version episode exceeds supported range");}return "tmdb:tv:"+parts[1]+":"+parts[2]+":"+parts[3];
        }throw new JSONException("Unsupported version parent identity");
    }
    public static boolean ownsKey(String key){if(!key.startsWith(PREFIX))return false;try{expected(key.substring(PREFIX.length()));return true;}catch(JSONException invalid){return false;}}
    public static void validate(JSONObject payload)throws JSONException{
        Object schema=payload.get("schema");if(payload.length()!=2||!(schema instanceof Integer||schema instanceof Long)||((Number)schema).longValue()!=1)throw new JSONException("Unsupported preferred-version schema");JSONArray rows=payload.getJSONArray("rows");if(rows.length()>100000)throw new JSONException("Too many preferred versions");Set<String> distinct=new HashSet<>();
        for(int i=0;i<rows.length();i++){
            JSONObject row=rows.getJSONObject(i);if(row.length()!=2||!(row.get("physical") instanceof String)||!row.has("expected"))throw new JSONException("Invalid preferred-version fields");PortablePhysicalReferences.validate(row.getString("physical"));
            if(!row.isNull("expected")){if(!(row.get("expected") instanceof String))throw new JSONException("Expected version title must be text");String title=row.getString("expected");if(!title.matches("file:[a-f0-9]{64}")){String parent=title.startsWith("tmdb:movie:")?title.substring(5):title.startsWith("tmdb:tv:")?"episode:"+title.substring(8):"";if(!title.equals(expected(parent)))throw new JSONException("Invalid expected version title");}}
            if(!distinct.add(key(row)))throw new JSONException("Conflicting preferred versions for one parent");
        }
    }
    private static String key(JSONObject row)throws JSONException{return row.isNull("expected")?row.getString("physical"):row.getString("expected");}
    public static JSONObject export(Context context,PortablePhysicalReferences references)throws JSONException{
        var prefs=PreferenceManager.getDefaultSharedPreferences(context);Map<String,JSONObject> rows=new LinkedHashMap<>();
        for(Map.Entry<String,?> entry:prefs.getAll().entrySet())if(ownsKey(entry.getKey())){
            String title=expected(entry.getKey().substring(PREFIX.length()));if(!(entry.getValue() instanceof String))throw new JSONException("Saved version choice needs review");String[] selection=((String)entry.getValue()).split("\\|",2);long id;
            try{if(selection.length!=2||!selection[0].matches("[1-9][0-9]{0,18}"))throw new NumberFormatException();id=Long.parseLong(selection[0]);}catch(NumberFormatException e){throw new JSONException("Unsupported saved version selection");}
            Video video=references.video(id);String ref;
            if(video!=null&&video.getFileUri()!=null&&selection[1].equals(BrowserSelection.canonical(video.getFileUri())))ref=references.reference(id);else ref=references.pendingReference(id);
            JSONObject row=new JSONObject().put("physical",ref).put("expected",title==null?JSONObject.NULL:title);rows.put(key(row),row);
        }
        String pending=prefs.getString(PENDING,null);if(pending!=null){JSONObject saved=new JSONObject(pending);validate(saved);JSONArray unresolved=saved.getJSONArray("rows");for(int i=0;i<unresolved.length();i++){JSONObject row=unresolved.getJSONObject(i);rows.putIfAbsent(key(row),row);}}
        JSONObject payload=new JSONObject().put("schema",1).put("rows",new JSONArray(rows.values()));validate(payload);return payload;
    }
    public static final class Prepared {
        public final Map<String,Object> values;public final Set<String> keys;public final int unresolved;
        private Prepared(Map<String,Object> values,Set<String> keys,int unresolved){this.values=Collections.unmodifiableMap(values);this.keys=Collections.unmodifiableSet(keys);this.unresolved=unresolved;}
    }
    public static Prepared prepare(Context destination,JSONObject payload,PortablePhysicalReferences references)throws JSONException{
        return prepare(destination,payload,references,false);
    }
    /** Only a validated media-matching dependency may enable physical version binding; privacy choices stay unchanged. */
    public static Prepared prepare(Context destination,JSONObject payload,PortablePhysicalReferences references,boolean matchingValidated)throws JSONException{
        validate(payload);Set<String> keys=new LinkedHashSet<>();Map<String,Object> values=new LinkedHashMap<>();JSONArray pending=new JSONArray(),rows=payload.getJSONArray("rows");
        for(String key:PreferenceManager.getDefaultSharedPreferences(destination).getAll().keySet())if(ownsKey(key))keys.add(key);
        for(int i=0;i<rows.length();i++){
            JSONObject row=rows.getJSONObject(i);Video target=references.resolve(row.getString("physical"));String parent=row.isNull("expected")?null:row.getString("expected");
            if(!matchingValidated||target==null||target.getFileUri()==null||!Objects.equals(parent,expected(PreviewVariants.choiceKey(target)))){pending.put(new JSONObject(row.toString()));continue;}
            String key=PREFIX+PreviewVariants.choiceKey(target);keys.add(key);values.put(key,target.getId()+"|"+BrowserSelection.canonical(target.getFileUri()));
        }
        keys.add(PENDING);values.put(PENDING,new JSONObject().put("schema",1).put("rows",pending).toString());return new Prepared(values,keys,pending.length());
    }
    static String supersede(String pending,String parentChoice)throws JSONException{
        JSONObject payload=new JSONObject(pending);validate(payload);String parent=expected(parentChoice);JSONArray rows=payload.getJSONArray("rows"),kept=new JSONArray();for(int i=0;i<rows.length();i++){JSONObject row=rows.getJSONObject(i);if(row.isNull("expected")||!parent.equals(row.getString("expected")))kept.put(row);}
        return new JSONObject().put("schema",1).put("rows",kept).toString();
    }
    private PortablePreferredVersions(){}
}
