package com.archos.mediacenter.video.utils.backup;

import android.content.Context;
import androidx.preference.PreferenceManager;
import java.net.URI;
import java.util.*;
import org.json.*;

/** Browser favourites from shortcuts2_db only. Indexed roots and the inactive FTP credential store are separate. */
public final class PortableBookmarks {
    public static final String PENDING="supernova_restore_pending_bookmarks_v2";
    private static void name(Object value)throws JSONException {
        if(value==JSONObject.NULL)return;
        if(!(value instanceof String))throw new JSONException("Invalid bookmark label");String text=(String)value;
        if(text.isBlank()||text.length()>200||PortableLocations.controls(text)||text.contains("://")||
                text.startsWith("/")||text.startsWith("\\")||text.matches("(?is).*(?:password|passwd|token|secret|authorization|cookie|api[_-]?key)\\s*[:=].*"))throw new JSONException("Bookmark label needs privacy review");
        try{java.nio.charset.StandardCharsets.UTF_8.newEncoder().encode(java.nio.CharBuffer.wrap(text));}catch(Exception invalid){throw new JSONException("Invalid bookmark label encoding");}
    }
    public static void validate(JSONObject payload)throws JSONException {
        if(payload.length()!=2||!Integer.valueOf(1).equals(payload.get("schema")))throw new JSONException("Unsupported bookmark schema");
        JSONArray rows=payload.getJSONArray("rows");if(rows.length()>10000)throw new JSONException("Too many file bookmarks");Set<String> ids=new HashSet<>();
        for(int i=0;i<rows.length();i++) {
            JSONObject row=rows.getJSONObject(i);if(row.length()!=3||!(row.get("id") instanceof String)||!(row.get("location") instanceof String)||!row.has("name"))throw new JSONException("Invalid bookmark fields");
            String id=row.getString("id");if(!id.matches("bookmark:[a-f0-9]{8}(?:-[a-f0-9]{4}){3}-[a-f0-9]{12}:[1-9][0-9]{0,18}")||!ids.add(id))throw new JSONException("Invalid or duplicate bookmark identity");
            try{if(Long.parseLong(id.substring(id.lastIndexOf(':')+1))<=0)throw new NumberFormatException();}catch(NumberFormatException invalid){throw new JSONException("Bookmark identity out of range");}
            PortableLocations.validateReference(row.getString("location"));name(row.get("name"));
        }
    }
    private static boolean excluded(String value){return value!=null&&value.regionMatches(true,0,"putio:",0,6);}
    public static Set<String> locations(JSONObject inventory)throws JSONException {
        BookmarkStoreRecovery.validate(inventory);Set<String> values=new TreeSet<>();JSONArray rows=inventory.getJSONArray("rows");
        for(int i=0;i<rows.length();i++){String value=rows.getJSONObject(i).getString("uri");if(!excluded(value))values.add(PortableLocations.locator(value));}return values;
    }
    public static JSONObject export(Context context,JSONObject inventory)throws JSONException {
        BookmarkStoreRecovery.validate(inventory);String origin=new PortableTitleReferences(context,null).origin();Map<String,JSONObject> values=new LinkedHashMap<>();JSONArray rows=inventory.getJSONArray("rows");
        for(int i=0;i<rows.length();i++) {
            JSONObject row=rows.getJSONObject(i);String uri=row.getString("uri");if(excluded(uri))continue;
            String canonical=PortableLocations.locator(uri),label=row.getString("name");boolean locatorLabel=label.contains("://")||label.startsWith("/")||label.startsWith("\\");
            try{String path=new URI(canonical).getPath();String leaf=path.substring(path.lastIndexOf('/')+1);locatorLabel|=label.equals(leaf)||label.equals(uri)||!row.isNull("friendly")&&label.equals(row.getString("friendly"));}catch(Exception invalid){throw new JSONException("Bookmark label cannot be classified safely");}
            Object saved=locatorLabel?JSONObject.NULL:label;name(saved);String id="bookmark:"+origin+":"+row.getLong("id");
            values.put(id,new JSONObject().put("id",id).put("location",PortableLocations.reference(canonical)).put("name",saved));
        }
        String pending=PreferenceManager.getDefaultSharedPreferences(context).getString(PENDING,null);if(pending!=null){JSONObject saved=new JSONObject(pending);validate(saved);JSONArray unknown=saved.getJSONArray("rows");for(int i=0;i<unknown.length();i++){JSONObject row=unknown.getJSONObject(i);values.putIfAbsent(row.getString("id"),row);}}
        JSONObject payload=new JSONObject().put("schema",1).put("rows",new JSONArray(values.values()));validate(payload);return payload;
    }
    public static final class Prepared {
        public final JSONObject after;public final String pending;public final int unresolved;
        private Prepared(JSONObject after,JSONArray pending)throws JSONException{this.after=after;this.pending=new JSONObject().put("schema",1).put("rows",pending).toString();unresolved=pending.length();}
    }
    /** Explicit replacement of favourites, independent of scanner roots, access grants and credentials. */
    public static Prepared prepare(JSONObject before,JSONObject payload,JSONObject selectedLocations)throws JSONException {
        BookmarkStoreRecovery.validate(before);validate(payload);Map<String,String> locations=new LinkedHashMap<>();
        if(selectedLocations!=null)locations.putAll(PortableLocations.validate(selectedLocations));
        JSONArray old=before.getJSONArray("rows");long maximum=before.isNull("sequence")?0:before.getLong("sequence");
        for(int i=0;i<old.length();i++){JSONObject row=old.getJSONObject(i);maximum=Math.max(maximum,row.getLong("id"));try{locations.putIfAbsent(PortableLocations.reference(row.getString("uri")),PortableLocations.locator(row.getString("uri")));}catch(JSONException unsafe){/* Never bind by stripping authentication or unsupported route material. */}}
        JSONObject after=new JSONObject(before.toString());JSONArray rows=new JSONArray(),unknown=new JSONArray(),incoming=payload.getJSONArray("rows");
        // Put.io favourites remain owned by that integration, outside new-format selected replacement.
        for(int i=0;i<old.length();i++)if(excluded(old.getJSONObject(i).getString("uri")))rows.put(new JSONObject(old.getJSONObject(i).toString()));
        for(int i=0;i<incoming.length();i++) {
            JSONObject row=incoming.getJSONObject(i);String uri=locations.get(row.getString("location"));
            if(uri==null){unknown.put(new JSONObject(row.toString()));continue;}
            if(maximum==Long.MAX_VALUE)throw new JSONException("Bookmark identity capacity exhausted");String label=row.isNull("name")?uri:row.getString("name");
            rows.put(new JSONObject().put("id",++maximum).put("uri",uri).put("name",label).put("friendly",before.getInt("version")==1?JSONObject.NULL:uri));
        }
        after.put("exists",before.getBoolean("exists")||rows.length()>0).put("rows",rows).put("sequence",after.getBoolean("exists")?maximum:JSONObject.NULL);BookmarkStoreRecovery.validate(after);return new Prepared(after,unknown);
    }
    private PortableBookmarks(){}
}
