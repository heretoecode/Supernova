package com.archos.mediacenter.video.leanback.details;

import com.archos.mediacenter.video.browser.adapters.object.Episode;
import org.json.*;
import java.util.*;

/** One logical episode, preferring an indexed physical version over provider metadata. */
final class PreviewEpisodeChoice {
    final int season,number;final Episode local;final JSONObject remote;
    PreviewEpisodeChoice(int season,int number,Episode local,JSONObject remote){this.season=season;this.number=number;this.local=local;this.remote=remote;}
    static SortedMap<Integer,List<PreviewEpisodeChoice>> reconcile(Map<Integer,List<Episode>> local,Map<Integer,JSONArray> remote){
        SortedMap<Integer,SortedMap<Integer,PreviewEpisodeChoice>> merged=new TreeMap<>();
        for(Map.Entry<Integer,JSONArray> row:remote.entrySet())for(int i=0;i<row.getValue().length();i++){
            JSONObject episode=row.getValue().optJSONObject(i);if(episode==null)continue;int number=episode.optInt("episode_number",-1);
            if(row.getKey()<0||number<1||episode.optInt("season_number",row.getKey())!=row.getKey())continue;
            merged.computeIfAbsent(row.getKey(),key->new TreeMap<>()).putIfAbsent(number,new PreviewEpisodeChoice(row.getKey(),number,null,episode));
        }
        for(Map.Entry<Integer,List<Episode>> row:local.entrySet())for(Episode episode:row.getValue()){
            int number=episode.getEpisodeNumber();if(row.getKey()<0||number<1)continue;
            SortedMap<Integer,PreviewEpisodeChoice> season=merged.computeIfAbsent(row.getKey(),key->new TreeMap<>());
            PreviewEpisodeChoice previous=season.get(number);
            if(previous==null||previous.local==null)season.put(number,new PreviewEpisodeChoice(row.getKey(),number,episode,previous==null?null:previous.remote));
        }
        SortedMap<Integer,List<PreviewEpisodeChoice>> result=new TreeMap<>();for(Map.Entry<Integer,SortedMap<Integer,PreviewEpisodeChoice>> row:merged.entrySet())result.put(row.getKey(),new ArrayList<>(row.getValue().values()));return result;
    }
}
