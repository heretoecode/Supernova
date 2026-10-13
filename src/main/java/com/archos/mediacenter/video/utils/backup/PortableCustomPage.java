package com.archos.mediacenter.video.utils.backup;

import android.content.Context;
import android.content.SharedPreferences;
import androidx.preference.PreferenceManager;
import com.archos.mediacenter.video.leanback.CustomLibraryPage;
import java.util.*;
import org.json.*;

/** Custom Library definition only. The franchise filter is unrelated to legacy user Collections. */
public final class PortableCustomPage {
    private static void text(String value,int limit,boolean blank)throws JSONException{
        if(value.length()>limit||!blank&&value.trim().isEmpty()||value.contains("://")||value.trim().startsWith("/")||value.trim().startsWith("\\")||value.matches("(?is)\\s*(?:[a-z]:|file:|content:)[/\\\\].*")||value.matches("(?is).*(?:password|passwd|token|credential|authorization|secret|cookie)\\s*[:=].*"))throw new JSONException("Custom Library text needs privacy review before backup");
    }
    public static JSONObject export(Context context)throws JSONException{
        String saved=PreferenceManager.getDefaultSharedPreferences(context).getString(CustomLibraryPage.KEY,null);JSONObject payload=new JSONObject().put("schema",1);
        if(saved==null)return payload.put("page",JSONObject.NULL);
        CustomLibraryPage page=CustomLibraryPage.decode(saved);if(page==null||page.validation()!=null)throw new JSONException("Saved Library page needs review");
        JSONObject definition=new JSONObject(page.encode());definition.put("franchise",definition.getString("collection"));definition.remove("collection");payload.put("page",definition);validate(payload);return payload;
    }
    public static void validate(JSONObject payload)throws JSONException{
        if(payload.length()!=2||!(payload.get("schema") instanceof Integer||payload.get("schema") instanceof Long)||payload.getLong("schema")!=1)throw new JSONException("Unsupported Library page schema");
        if(payload.isNull("page"))return;JSONObject page=payload.getJSONObject("page");
        Set<String> expected=new HashSet<>(Arrays.asList("name","icon","type","view","sort","watched","allGenres","descending","year","rating","language","studio","franchise","genres")),actual=new HashSet<>();for(Iterator<String> keys=page.keys();keys.hasNext();)actual.add(keys.next());if(!actual.equals(expected))throw new JSONException("Unsupported Library definition fields");
        text(page.getString("name"),40,false);text(page.getString("studio"),256,true);text(page.getString("franchise"),256,true);
        if(!Arrays.asList("Library","Movies","TV Shows","Documentaries").contains(page.getString("icon"))||!Arrays.asList("movies","tv","both").contains(page.getString("type"))||!Arrays.asList("grid","list").contains(page.getString("view"))||!Arrays.asList("title","year","added").contains(page.getString("sort"))||!Arrays.asList("all","watched","unwatched","progress").contains(page.getString("watched")))throw new JSONException("Unsupported Library page option");
        for(String key:new String[]{"allGenres","descending"})if(!(page.get(key) instanceof Boolean))throw new JSONException("Invalid Library page Boolean");
        Object year=page.get("year"),rating=page.get("rating");if(!(year instanceof Integer||year instanceof Long)||((Number)year).longValue()<0||((Number)year).longValue()>9999||!(rating instanceof Number)||!Double.isFinite(page.getDouble("rating"))||page.getDouble("rating")<0||page.getDouble("rating")>10)throw new JSONException("Invalid Library page year or rating");
        if(!page.getString("language").matches("(?:|[a-zA-Z]{2,3}(?:[-_][A-Za-z0-9]{2,8})*)"))throw new JSONException("Unsupported Library language");
        JSONArray genres=page.getJSONArray("genres");if(genres.length()>500)throw new JSONException("Too many Library genres");Set<String> unique=new HashSet<>();for(int i=0;i<genres.length();i++){if(!(genres.get(i) instanceof String))throw new JSONException("Library genre must be text");String genre=genres.getString(i);text(genre,100,false);if(!unique.add(genre))throw new JSONException("Duplicate Library genre");}
    }
    /** Stages one owned key; present-empty removes the page, never media or other view preferences. */
    public static SharedPreferences.Editor replace(Context destination,JSONObject payload)throws JSONException{
        validate(payload);SharedPreferences.Editor edit=PreferenceManager.getDefaultSharedPreferences(destination).edit();if(payload.isNull("page"))return edit.remove(CustomLibraryPage.KEY);
        JSONObject page=new JSONObject(payload.getJSONObject("page").toString());page.put("collection",page.getString("franchise"));page.remove("franchise");return edit.putString(CustomLibraryPage.KEY,page.toString());
    }
    private PortableCustomPage(){}
}
