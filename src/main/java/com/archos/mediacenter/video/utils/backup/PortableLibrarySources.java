package com.archos.mediacenter.video.utils.backup;

import android.content.Context;
import androidx.preference.PreferenceManager;
import java.util.*;
import org.json.*;

/** Scanner roots and exclusions only. Put.io selection/ownership and permissions have no codec. */
public final class PortableLibrarySources {
    public static final String ROOTS="supernova_library_roots",EXCLUSIONS="supernova_library_exclusions",PENDING="supernova_restore_pending_sources_v2";
    private static boolean excluded(String value){return value!=null&&value.regionMatches(true,0,"putio:",0,6);}
    private static Set<String> stored(Map<String,?> preferences,String key)throws JSONException {
        Object value=preferences.get(key);if(value==null)return Collections.emptySet();
        if(!(value instanceof Set)||((Set<?>)value).size()>10000)throw new JSONException("Invalid saved source configuration");Set<String> result=new TreeSet<>();
        for(Object item:(Set<?>)value){if(!(item instanceof String))throw new JSONException("Invalid saved source configuration");result.add((String)item);}return result;
    }
    public static Set<String> locations(Context context)throws JSONException {
        Map<String,?> preferences=PreferenceManager.getDefaultSharedPreferences(context).getAll();Set<String> result=new TreeSet<>();
        for(String key:List.of(ROOTS,EXCLUSIONS))for(String value:stored(preferences,key))if(!excluded(value))result.add(PortableLocations.locator(value));return Collections.unmodifiableSet(result);
    }
    public static JSONObject export(Context context)throws JSONException {
        Map<String,?> preferences=PreferenceManager.getDefaultSharedPreferences(context).getAll();Map<String,Set<String>> values=new LinkedHashMap<>();
        values.put("roots",new TreeSet<>());values.put("exclusions",new TreeSet<>());
        for(String field:values.keySet())for(String value:stored(preferences,field.equals("roots")?ROOTS:EXCLUSIONS))if(!excluded(value))values.get(field).add(PortableLocations.reference(value));
        Object pending=preferences.get(PENDING);if(pending!=null) {
            if(!(pending instanceof String))throw new JSONException("Invalid pending sources");JSONObject saved=new JSONObject((String)pending);validate(saved);
            for(String field:values.keySet()){JSONArray rows=saved.getJSONArray(field);for(int i=0;i<rows.length();i++)values.get(field).add(rows.getString(i));}
        }
        JSONObject payload=new JSONObject().put("schema",1);for(var entry:values.entrySet())payload.put(entry.getKey(),new JSONArray(entry.getValue()));validate(payload);return payload;
    }
    public static void validate(JSONObject payload)throws JSONException {
        if(payload.length()!=3||!Integer.valueOf(1).equals(payload.get("schema")))throw new JSONException("Unsupported source schema");
        for(String field:List.of("roots","exclusions")) {
            JSONArray rows=payload.getJSONArray(field);if(rows.length()>10000)throw new JSONException("Too many saved sources");Set<String> seen=new HashSet<>();
            for(int i=0;i<rows.length();i++){if(!(rows.get(i) instanceof String))throw new JSONException("Invalid source reference");String ref=rows.getString(i);PortableLocations.validateReference(ref);if(!seen.add(ref))throw new JSONException("Duplicate source reference");}
        }
    }
    public static final class Prepared {
        public final Map<String,Object> values;public final Set<String> keys=Set.of(ROOTS,EXCLUSIONS,PENDING);public final int unresolved;
        private Prepared(Map<String,Object> values,int unresolved){this.values=Collections.unmodifiableMap(values);this.unresolved=unresolved;}
    }
    /** Locators must be explicitly selected at restore; no automatic opt-in or permission recreation. */
    public static Prepared prepare(Map<String,?> current,JSONObject payload,JSONObject selectedLocations)throws JSONException {
        validate(payload);Map<String,String> locations=new LinkedHashMap<>();
        if(selectedLocations!=null)locations.putAll(PortableLocations.validate(selectedLocations));
        // An exact already-configured receiving location needs no new locator disclosure or creation.
        for(String key:List.of(ROOTS,EXCLUSIONS))for(String value:stored(current,key))if(!excluded(value)) {
            try{locations.putIfAbsent(PortableLocations.reference(value),PortableLocations.locator(value));}
            catch(JSONException unsafe){/* Existing unsupported routes remain outside automatic matching. */}
        }
        Map<String,Object> values=new LinkedHashMap<>();JSONObject pending=new JSONObject().put("schema",1);int unresolved=0;
        for(String field:List.of("roots","exclusions")) {
            String key=field.equals("roots")?ROOTS:EXCLUSIONS;Set<String> restored=new LinkedHashSet<>();
            // Excluded live associations remain owned by their integration, even for a selected empty category.
            for(String value:stored(current,key))if(excluded(value))restored.add(value);
            JSONArray unknown=new JSONArray(),rows=payload.getJSONArray(field);for(int i=0;i<rows.length();i++) {
                String ref=rows.getString(i),uri=locations.get(ref);if(uri==null){unknown.put(ref);unresolved++;}else restored.add(uri);
            }values.put(key,Collections.unmodifiableSet(restored));pending.put(field,unknown);
        }values.put(PENDING,pending.toString());return new Prepared(values,unresolved);
    }
    public static boolean owns(String key){return key.equals(ROOTS)||key.equals(EXCLUSIONS)||key.equals(PENDING);}
    private PortableLibrarySources(){}
}
