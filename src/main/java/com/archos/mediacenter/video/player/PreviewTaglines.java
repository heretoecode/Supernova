package com.archos.mediacenter.video.player;

import android.content.Context;
import com.archos.mediacenter.video.leanback.PreviewMetadataCache;
import org.json.*;

/** Only explicitly supplied provider taglines. No synopsis or invented episode copy. */
public final class PreviewTaglines {
    public static String choose(JSONObject movie,JSONObject episode,JSONObject season,JSONObject show){
        for(JSONObject source:new JSONObject[]{movie,episode,season,show})if(source!=null){String line=source.optString("tagline","").trim();if(!line.isEmpty()&&!"null".equals(line))return line;}return "";
    }
    private static JSONObject cached(Context c,String kind,long id,String section){JSONObject envelope=PreviewMetadataCache.read(c,kind,id,section);return envelope==null?null:envelope.optJSONObject("data");}
    public static String cached(Context c,boolean show,long id,int season,int episode){
        if(id<=0)return "";if(!show)return choose(cached(c,"movie",id,""),null,null,null);
        JSONObject seasonData=cached(c,"tv",id,"season/"+season),episodeData=cached(c,"tv",id,"season/"+season+"/episode/"+episode);
        if(episodeData==null&&seasonData!=null){JSONArray episodes=seasonData.optJSONArray("episodes");if(episodes!=null)for(int n=0;n<episodes.length();n++){JSONObject value=episodes.optJSONObject(n);if(value!=null&&value.optInt("episode_number",-1)==episode){episodeData=value;break;}}}
        return choose(null,episodeData,seasonData,cached(c,"tv",id,""));
    }
    private PreviewTaglines(){}
}
