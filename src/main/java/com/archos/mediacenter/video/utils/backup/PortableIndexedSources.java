package com.archos.mediacenter.video.utils.backup;

import android.content.Context;
import androidx.preference.PreferenceManager;
import java.net.URI;
import java.util.*;
import org.json.*;

/** Indexed folders and rescan intent. IP caches, friendly locators and access credentials never export. */
public final class PortableIndexedSources {
    public static final String PENDING="supernova_restore_pending_indexed_sources_v2";
    private static boolean putio(String path){return path.regionMatches(true,0,"putio:",0,6);}
    private static boolean excluded(String path){if(putio(path))return true;try{PortableLocations.locator(path);return false;}catch(JSONException unsafe){return true;}}
    private static void label(Object value)throws JSONException{if(value==JSONObject.NULL)return;if(!(value instanceof String))throw new JSONException("Invalid indexed-source label");String name=(String)value;if(name.length()>8192||name.indexOf('\0')>=0||name.contains("://")||name.startsWith("/")||name.startsWith("\\")||name.matches("(?is).*(?:password|passwd|token|secret|authorization|cookie|api[_-]?key)\\s*[:=].*"))throw new JSONException("Indexed-source label needs privacy review");try{java.nio.charset.StandardCharsets.UTF_8.newEncoder().encode(java.nio.CharBuffer.wrap(name));}catch(Exception invalid){throw new JSONException("Invalid indexed-source label encoding");}}
    public static void validate(JSONObject payload)throws JSONException{
        if(payload.length()!=2||!Integer.valueOf(1).equals(payload.get("schema")))throw new JSONException("Unsupported indexed-source schema");JSONArray rows=payload.getJSONArray("rows");if(rows.length()>10000)throw new JSONException("Too many indexed sources");Set<String> seen=new HashSet<>();
        for(int i=0;i<rows.length();i++){JSONObject row=rows.getJSONObject(i);if(row.length()!=3||!(row.get("location") instanceof String)||!(row.get("rescan") instanceof Boolean))throw new JSONException("Invalid indexed-source fields");String ref=row.getString("location");PortableLocations.validateReference(ref);if(!seen.add(ref))throw new JSONException("Ambiguous indexed-source location");label(row.get("name"));}
    }
    public static Set<String> locations(JSONObject image)throws JSONException{IndexedSourceStoreRecovery.validate(image);Set<String> values=new TreeSet<>();JSONArray rows=image.getJSONArray("rows");for(int i=0;i<rows.length();i++){String path=rows.getJSONObject(i).getString("path");if(!putio(path))values.add(PortableLocations.locator(path));}return Collections.unmodifiableSet(values);}
    public static JSONObject export(Context context,JSONObject image)throws JSONException{
        IndexedSourceStoreRecovery.validate(image);Map<String,JSONObject> values=new LinkedHashMap<>();JSONArray rows=image.getJSONArray("rows");for(int i=0;i<rows.length();i++){
            JSONObject row=rows.getJSONObject(i);String path=row.getString("path");if(putio(path))continue;String locator=PortableLocations.locator(path),ref=PortableLocations.reference(locator),name=row.getString("name");
            if(values.containsKey(ref))throw new JSONException("Ambiguous indexed-source location");boolean locatorName=name.contains("://")||name.startsWith("/")||name.startsWith("\\")||name.equals(path)||!row.isNull("friendly")&&name.equals(row.getString("friendly"));
            try{String component=new URI(locator).getPath();locatorName|=component!=null&&name.equals(component.substring(component.lastIndexOf('/')+1));}catch(Exception invalid){throw new JSONException("Indexed-source label cannot be classified safely");}
            Object saved=locatorName?JSONObject.NULL:name;label(saved);values.put(ref,new JSONObject().put("location",ref).put("name",saved).put("rescan",!row.isNull("rescan")&&row.getLong("rescan")==1));
        }
        String saved=PreferenceManager.getDefaultSharedPreferences(context).getString(PENDING,null);if(saved!=null){JSONObject pending=new JSONObject(saved);validate(pending);JSONArray unknown=pending.getJSONArray("rows");for(int i=0;i<unknown.length();i++){JSONObject row=unknown.getJSONObject(i);values.putIfAbsent(row.getString("location"),row);}}
        JSONObject payload=new JSONObject().put("schema",1).put("rows",new JSONArray(values.values()));validate(payload);return payload;
    }
    public static final class Prepared {
        public final JSONObject after;public final String pending;public final int unresolved;
        private Prepared(JSONObject after,JSONArray rows)throws JSONException{this.after=after;pending=new JSONObject().put("schema",1).put("rows",rows).toString();unresolved=rows.length();}
    }
    public static Prepared prepare(JSONObject before,JSONObject payload,JSONObject selectedLocations)throws JSONException{
        IndexedSourceStoreRecovery.validate(before);validate(payload);Map<String,String> locations=selectedLocations==null?new LinkedHashMap<>():new LinkedHashMap<>(PortableLocations.validate(selectedLocations));Map<String,JSONObject> configured=new HashMap<>();JSONArray preserved=new JSONArray(),original=before.getJSONArray("rows");long next=before.isNull("sequence")?0:before.getLong("sequence");
        for(int i=0;i<original.length();i++){JSONObject row=original.getJSONObject(i);next=Math.max(next,row.getLong("id"));String path=row.getString("path");if(excluded(path)){preserved.put(new JSONObject(row.toString()));continue;}String locator=PortableLocations.locator(path),ref=PortableLocations.reference(locator);if(configured.put(ref,row)!=null)throw new JSONException("Ambiguous receiving indexed-source location");locations.putIfAbsent(ref,locator);}
        JSONArray unknown=new JSONArray(),rows=payload.getJSONArray("rows");for(int i=0;i<rows.length();i++){
            JSONObject row=rows.getJSONObject(i);String ref=row.getString("location"),path=locations.get(ref);if(path==null){unknown.put(new JSONObject(row.toString()));continue;}JSONObject existing=configured.get(ref),target;
            if(existing!=null)target=new JSONObject(existing.toString());else{if(next==Long.MAX_VALUE)throw new JSONException("Indexed-source identity capacity exhausted");target=new JSONObject().put("id",++next).put("path",path).put("name",JSONObject.NULL).put("ip",JSONObject.NULL).put("friendly",JSONObject.NULL);}
            String name=row.isNull("name")?android.net.Uri.parse(path).getLastPathSegment():row.getString("name");if(name==null||name.isEmpty())name=path;target.put("name",name).put("rescan",row.getBoolean("rescan")?1:0);preserved.put(target);
        }
        List<JSONObject> sorted=new ArrayList<>();for(int i=0;i<preserved.length();i++)sorted.add(preserved.getJSONObject(i));sorted.sort(Comparator.comparingLong(row->row.optLong("id")));
        JSONObject after=new JSONObject(before.toString()).put("rows",new JSONArray(sorted));if(preserved.length()!=0&&!before.getBoolean("exists"))after.put("exists",true);if(after.getBoolean("exists"))after.put("sequence",before.isNull("sequence")&&next==0?JSONObject.NULL:next);IndexedSourceStoreRecovery.validatePair(before,after);return new Prepared(after,unknown);
    }
    private PortableIndexedSources(){}
}
