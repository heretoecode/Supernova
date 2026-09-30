package com.archos.mediacenter.video.leanback.details;

import org.json.JSONArray;
import org.json.JSONObject;
import java.util.LinkedHashSet;

/** Read only applicable published facts; absence must not become a guessed classification/runtime. */
final class PreviewDetailsFacts {
    static String certificate(JSONObject classification,String country){
        JSONArray regions=classification==null?null:classification.optJSONArray("results");
        if(regions==null)return "";
        for(int i=0;i<regions.length();i++){
            JSONObject region=regions.optJSONObject(i);if(region==null||!country.equals(region.optString("iso_3166_1")))continue;
            String rating=region.optString("rating");if(!rating.isEmpty())return rating+" ("+country+")";
            JSONArray releases=region.optJSONArray("release_dates");if(releases==null)continue;
            String fallback="";
            for(int n=0;n<releases.length();n++){JSONObject release=releases.optJSONObject(n);if(release==null)continue;String value=release.optString("certification");if(value.isEmpty())continue;if(release.optInt("type")==3)return value+" ("+country+")";if(fallback.isEmpty())fallback=value;}
            if(!fallback.isEmpty())return fallback+" ("+country+")";
        }
        return "";
    }
    static int year(JSONObject details){
        if(details==null)return 0;String date=details.optString("release_date",details.optString("first_air_date"));
        return date.matches("[0-9]{4}-[0-9]{2}-[0-9]{2}")?Integer.parseInt(date.substring(0,4)):0;
    }
    static String episodeRuntimes(JSONObject details){
        JSONArray values=details==null?null:details.optJSONArray("episode_run_time");LinkedHashSet<String> minutes=new LinkedHashSet<>();
        if(values!=null)for(int n=0;n<values.length();n++){int value=values.optInt(n);if(value>0)minutes.add(value+" min");}
        return android.text.TextUtils.join(", ",minutes);
    }
    private PreviewDetailsFacts(){}
}
