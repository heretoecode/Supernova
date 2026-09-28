package com.archos.mediacenter.video.leanback.details;

import android.content.Context;
import com.archos.mediacenter.video.streaming.StreamingRepository;
import org.json.*;
import java.util.*;

/** Bounded metadata enrichment. Availability is checked against configured providers and country. */
final class PreviewDetailsData {
    static final class Remote {final JSONObject title;final StreamingRepository.Provider provider;Remote(JSONObject title,StreamingRepository.Provider provider){this.title=title;this.provider=provider;}}
    static final class Extra {final String name,type,key;Extra(String name,String type,String key){this.name=name;this.type=type;this.key=key;}}
    static final class Result {JSONObject details,credits;final List<Remote> related=new ArrayList<>();final List<Extra> extras=new ArrayList<>();final SortedMap<Integer,JSONArray> episodes=new TreeMap<>();final Map<Integer,StreamingRepository.Availability> seasonAvailability=new HashMap<>();}
    private static JSONObject cachedSection(Context c,String kind,long id,String section){
        JSONObject envelope=com.archos.mediacenter.video.leanback.PreviewMetadataCache.read(c,kind,id,section);
        return envelope==null?null:envelope.optJSONObject("data");
    }
    /** Disk-only first delivery; never wait for a refresh of another package section. */
    static Result cached(Context c,String kind,long id,Set<Long> localIds){
        Result result=new Result();result.details=cachedSection(c,kind,id,"");result.credits=cachedSection(c,kind,id,"credits");
        if(result.details==null&&result.credits==null)return null;
        if(result.details==null)result.details=new JSONObject();
        JSONArray seasons=result.details.optJSONArray("seasons");
        if("tv".equals(kind)&&seasons!=null)for(int i=0;i<seasons.length();i++){
            JSONObject season=seasons.optJSONObject(i);int number=season==null?-1:season.optInt("season_number",-1);
            if(number<0||number>9999)continue;
            JSONObject data=cachedSection(c,kind,id,"season/"+number);JSONArray episodes=data==null?null:data.optJSONArray("episodes");
            if(episodes!=null)result.episodes.put(number,episodes);
        }
        addExtras(result,cachedSection(c,kind,id,"videos"));
        JSONObject recommendations=cachedSection(c,kind,id,"recommendations");JSONArray candidates=recommendations==null?null:recommendations.optJSONArray("results");
        Set<Long> seen=new HashSet<>();
        if(candidates!=null)for(int i=0;i<Math.min(60,candidates.length())&&result.related.size()<12;i++){
            JSONObject title=candidates.optJSONObject(i);long candidate=title==null?0:title.optLong("id");
            if(candidate>0&&candidate!=id&&localIds.contains(candidate)&&seen.add(candidate))result.related.add(new Remote(title,null));
        }
        return result;
    }
    private static void addExtras(Result result,JSONObject metadata){
        JSONArray videos=metadata==null?null:metadata.optJSONArray("results");
        if(videos!=null)for(int i=0;i<videos.length()&&result.extras.size()<24;i++){
            JSONObject video=videos.optJSONObject(i);if(video==null)continue;String key=video.optString("key");
            if("YouTube".equals(video.optString("site"))&&key.matches("[A-Za-z0-9_-]{11}"))result.extras.add(new Extra(video.optString("name","Video"),video.optString("type","Video"),key));
        }
        result.extras.sort(Comparator.comparingInt(e->e.type.equals("Trailer")?0:e.type.equals("Teaser")?1:2));
    }
    static Result load(Context c,String kind,long id,Set<Long> localIds)throws Exception{
        Result result=new Result();result.details=StreamingRepository.metadata(c,kind,id,"");
        try{result.credits=StreamingRepository.metadata(c,kind,id,"credits");}catch(Exception unavailable){com.archos.mediacenter.video.diagnostics.Diagnostics.event("credits_metadata_unavailable");}
        if("tv".equals(kind)){
            JSONArray seasons=result.details.optJSONArray("seasons");
            if(seasons!=null)for(int i=0;i<seasons.length();i++){
                if(Thread.currentThread().isInterrupted())throw new java.io.InterruptedIOException();
                JSONObject season=seasons.optJSONObject(i);int number=season==null?-1:season.optInt("season_number",-1);if(number<0||number>9999)continue;
                try{JSONArray episodes=StreamingRepository.metadata(c,kind,id,"season/"+number).optJSONArray("episodes");if(episodes!=null)result.episodes.put(number,episodes);}catch(Exception unavailable){com.archos.mediacenter.video.diagnostics.Diagnostics.event("season_metadata_unavailable","season",number);}
                if(StreamingRepository.prefs(c).getBoolean(StreamingRepository.ENABLED,false)&&!StreamingRepository.selected(c).isEmpty())try{result.seasonAvailability.put(number,StreamingRepository.load(c,kind,id,StreamingRepository.country(c),number));}catch(Exception unavailable){com.archos.mediacenter.video.diagnostics.Diagnostics.event("season_availability_unavailable","season",number);}
            }
        }
        try{addExtras(result,StreamingRepository.metadata(c,kind,id,"videos"));}catch(Exception error){com.archos.mediacenter.video.diagnostics.Diagnostics.error("extras_metadata_unavailable",error);}
        JSONArray candidates;try{candidates=StreamingRepository.metadata(c,kind,id,"recommendations").optJSONArray("results");}catch(Exception error){com.archos.mediacenter.video.diagnostics.Diagnostics.error("recommendations_unavailable",error);return result;}if(candidates==null)return result;
        Set<String> configured=StreamingRepository.selected(c);String country=StreamingRepository.country(c);
        boolean providersEnabled=StreamingRepository.prefs(c).getBoolean(StreamingRepository.ENABLED,false)&&!configured.isEmpty();
        Set<Long> seen=new HashSet<>();int providerChecks=0;
        for(int i=0;i<Math.min(60,candidates.length())&&result.related.size()<12;i++){if(Thread.currentThread().isInterrupted())break;JSONObject title=candidates.getJSONObject(i);long candidate=title.optLong("id");if(candidate<=0||candidate==id||!seen.add(candidate))continue;
            if(localIds.contains(candidate)){result.related.add(new Remote(title,null));continue;}
            if(!providersEnabled||providerChecks++>=12)continue;
            try{StreamingRepository.Availability availability=StreamingRepository.load(c,kind,candidate,country);List<StreamingRepository.Offer> offers=StreamingRepository.filter(availability,configured,StreamingRepository.preferred(c));if(!offers.isEmpty())result.related.add(new Remote(title,offers.get(0).provider));}catch(Exception error){com.archos.mediacenter.video.diagnostics.Diagnostics.error("discovery_availability_unavailable",error);}
        }return result;
    }
}
