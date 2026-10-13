package com.archos.mediacenter.video.utils.backup;

import android.content.Context;
import androidx.preference.PreferenceManager;
import com.archos.mediacenter.video.browser.adapters.object.*;
import com.archos.mediacenter.video.leanback.PreviewLibraryLoader.*;
import java.util.*;
import org.json.*;

/** Logical title references never contain a filename, location, user label or credential. */
public final class PortableTitleReferences {
    private static final String ORIGIN="supernova_backup_origin_v2";
    private final String origin;
    private final Map<String,Entry> indexed=new LinkedHashMap<>();
    public PortableTitleReferences(Context context,Snapshot snapshot){
        var prefs=PreferenceManager.getDefaultSharedPreferences(context);String saved=prefs.getString(ORIGIN,"");
        if(!saved.matches("[a-f0-9]{8}(?:-[a-f0-9]{4}){3}-[a-f0-9]{12}")){saved=UUID.randomUUID().toString();if(!prefs.edit().putString(ORIGIN,saved).commit())throw new IllegalStateException("Cannot preserve backup origin");}
        origin=saved;if(snapshot!=null){for(Entry entry:snapshot.movies)indexed.put(entry.key(),entry);for(Entry entry:snapshot.shows)indexed.put(entry.key(),entry);}
    }
    public String reference(String sourceKey)throws JSONException{
        if(!sourceKey.matches("[vs][1-9][0-9]*"))throw new JSONException("Unsupported indexed membership identity");
        Entry entry=indexed.get(sourceKey);
        if(entry!=null&&entry.onlineId>0)return (entry.media instanceof Tvshow?"tmdb:tv:":"tmdb:movie:")+entry.onlineId;
        return "unresolved:"+origin+":"+sourceKey;
    }
    public static void validate(String reference)throws JSONException{
        if(!reference.matches("(?:tmdb:(?:movie|tv):[1-9][0-9]{0,18}|unresolved:[a-f0-9]{8}(?:-[a-f0-9]{4}){3}-[a-f0-9]{12}:[vs][1-9][0-9]{0,18})"))throw new JSONException("Unsafe or unsupported logical title reference");
    }
    /** Exact provider identity only. Unknown/unmatched records remain pending, without title guesses. */
    public static Map<String,String> available(Snapshot snapshot){
        Map<String,String> result=new LinkedHashMap<>();if(snapshot==null)return result;
        for(Entry entry:snapshot.movies)if(entry.onlineId>0)result.putIfAbsent("tmdb:movie:"+entry.onlineId,entry.key());
        for(Entry entry:snapshot.shows)if(entry.onlineId>0)result.putIfAbsent("tmdb:tv:"+entry.onlineId,entry.key());return result;
    }
    private PortableTitleReferences(){throw new AssertionError();}
}
