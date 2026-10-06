package com.archos.mediacenter.video.leanback.details;

import org.json.JSONArray;
import org.json.JSONObject;
import java.util.LinkedHashSet;

/** Read only applicable published facts; absence must not become a guessed classification/runtime. */
final class PreviewDetailsFacts {
    static JSONObject episode(java.util.Map<Integer,JSONArray> seasons,int season,int number){
        JSONArray episodes=seasons.get(season);if(episodes==null)return null;
        for(int i=0;i<episodes.length();i++){JSONObject episode=episodes.optJSONObject(i);if(episode!=null&&episode.optInt("episode_number",-1)==number)return episode;}
        return null;
    }
    static int dateYear(long date){
        if(date<=0)return 0;java.util.Calendar calendar=java.util.Calendar.getInstance(java.util.TimeZone.getTimeZone("UTC"));calendar.setTimeInMillis(date);return calendar.get(java.util.Calendar.YEAR);
    }
    static String date(long date){
        if(date<=0)return "";java.text.SimpleDateFormat format=new java.text.SimpleDateFormat("yyyy-MM-dd",java.util.Locale.ROOT);format.setTimeZone(java.util.TimeZone.getTimeZone("UTC"));return format.format(new java.util.Date(date));
    }
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
        if(details==null)return 0;String date=details.optString("release_date",details.optString("first_air_date",details.optString("air_date")));
        return date.matches("[0-9]{4}-[0-9]{2}-[0-9]{2}")?Integer.parseInt(date.substring(0,4)):0;
    }
    static String episodeRuntimes(JSONObject details){
        JSONArray values=details==null?null:details.optJSONArray("episode_run_time");LinkedHashSet<String> minutes=new LinkedHashSet<>();
        if(values!=null)for(int n=0;n<values.length();n++){int value=values.optInt(n);if(value>0)minutes.add(value+" min");}
        return android.text.TextUtils.join(", ",minutes);
    }
    private PreviewDetailsFacts(){}
}
