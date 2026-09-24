package com.archos.mediacenter.video.leanback.details;

import android.content.Context;
import com.archos.mediacenter.video.streaming.StreamingRepository;
import org.json.*;
import java.util.*;

/** Bounded metadata enrichment. Availability is checked against configured providers and country. */
final class PreviewDetailsData {
    static final class Remote {final JSONObject title;final StreamingRepository.Provider provider;Remote(JSONObject title,StreamingRepository.Provider provider){this.title=title;this.provider=provider;}}
    static final class Extra {final String name,type,key;Extra(String name,String type,String key){this.name=name;this.type=type;this.key=key;}}
    static final class Result {JSONObject details;final List<Remote> related=new ArrayList<>();final List<Extra> extras=new ArrayList<>();}
    static Result load(Context c,String kind,long id,Set<Long> localIds)throws Exception{
        Result result=new Result();result.details=StreamingRepository.metadata(c,kind,id,"");
        try{JSONArray videos=StreamingRepository.metadata(c,kind,id,"videos").optJSONArray("results");if(videos!=null)for(int i=0;i<videos.length()&&result.extras.size()<24;i++){JSONObject video=videos.getJSONObject(i);String key=video.optString("key");if("YouTube".equals(video.optString("site"))&&key.matches("[A-Za-z0-9_-]{11}"))result.extras.add(new Extra(video.optString("name","Video"),video.optString("type","Video"),key));}result.extras.sort(Comparator.comparingInt(e->e.type.equals("Trailer")?0:e.type.equals("Teaser")?1:2));}catch(Exception error){com.archos.mediacenter.video.diagnostics.Diagnostics.error("extras_metadata_unavailable",error);}
        if(!StreamingRepository.prefs(c).getBoolean(StreamingRepository.ENABLED,false)||StreamingRepository.selected(c).isEmpty())return result;
        JSONArray candidates;try{candidates=StreamingRepository.metadata(c,kind,id,"recommendations").optJSONArray("results");}catch(Exception error){com.archos.mediacenter.video.diagnostics.Diagnostics.error("recommendations_unavailable",error);return result;}if(candidates==null)return result;
        Set<String> configured=StreamingRepository.selected(c);String country=StreamingRepository.country(c);
        for(int i=0;i<Math.min(12,candidates.length());i++){if(Thread.currentThread().isInterrupted())break;JSONObject title=candidates.getJSONObject(i);long candidate=title.optLong("id");if(candidate<=0||candidate==id||localIds.contains(candidate))continue;
            try{StreamingRepository.Availability availability=StreamingRepository.load(c,kind,candidate,country);List<StreamingRepository.Offer> offers=StreamingRepository.filter(availability,configured,StreamingRepository.preferred(c));if(!offers.isEmpty())result.related.add(new Remote(title,offers.get(0).provider));}catch(Exception error){com.archos.mediacenter.video.diagnostics.Diagnostics.error("discovery_availability_unavailable",error);}
        }return result;
    }
}
