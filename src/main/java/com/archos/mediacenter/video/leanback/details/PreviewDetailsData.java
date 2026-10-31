package com.archos.mediacenter.video.leanback.details;

import android.content.Context;
import com.archos.mediacenter.video.metadata.OmdbSupplement;
import com.archos.mediacenter.video.streaming.StreamingRepository;
import org.json.*;
import java.util.*;

/** Bounded metadata enrichment. Availability is checked against configured providers and country. */
final class PreviewDetailsData {
    static final java.util.concurrent.ExecutorService IO=java.util.concurrent.Executors.newFixedThreadPool(2,r->new Thread(r,"SupernovaDetails"));
    interface Metadata {JSONObject load(String section)throws Exception;}
    static final class Remote {final JSONObject title;final StreamingRepository.Provider provider;Remote(JSONObject title,StreamingRepository.Provider provider){this.title=title;this.provider=provider;}}
    static final class Extra {final String name,type,key;Extra(String name,String type,String key){this.name=name;this.type=type;this.key=key;}}
    static final class Result {boolean extrasLoaded,relatedLoaded;OmdbSupplement.Ratings supplementary=OmdbSupplement.Ratings.EMPTY;JSONObject details,credits,classification,alternatives;final List<Remote> related=new ArrayList<>();final List<Extra> extras=new ArrayList<>();final SortedMap<Integer,JSONArray> episodes=new TreeMap<>();final Map<Integer,StreamingRepository.Availability> seasonAvailability=new HashMap<>();}
    /** Snapshot collections before posting across threads; section JSON is never mutated by the loader. */
    static Result copy(Result value){
        Result result=new Result();result.extrasLoaded=value.extrasLoaded;result.relatedLoaded=value.relatedLoaded;result.details=value.details;result.credits=value.credits;result.classification=value.classification;result.alternatives=value.alternatives;result.supplementary=value.supplementary;
        result.related.addAll(value.related);result.extras.addAll(value.extras);result.episodes.putAll(value.episodes);result.seasonAvailability.putAll(value.seasonAvailability);return result;
    }
    static Result merge(Result known,Result incoming){
        if(known==null)return copy(incoming);Result result=copy(known);
        result.details=mergeFields(known.details,incoming.details);
        if(incoming.credits!=null)result.credits=incoming.credits;
        if(incoming.classification!=null)result.classification=incoming.classification;
        if(incoming.alternatives!=null)result.alternatives=incoming.alternatives;
        if(incoming.supplementary!=OmdbSupplement.Ratings.EMPTY)result.supplementary=incoming.supplementary;
        if(incoming.relatedLoaded||!incoming.related.isEmpty()){result.related.clear();result.related.addAll(incoming.related);result.relatedLoaded=incoming.relatedLoaded;}
        if(incoming.extrasLoaded||!incoming.extras.isEmpty()){result.extras.clear();result.extras.addAll(incoming.extras);result.extrasLoaded=incoming.extrasLoaded;}
        result.episodes.putAll(incoming.episodes);result.seasonAvailability.putAll(incoming.seasonAvailability);return result;
    }
    static JSONObject mergeFields(JSONObject known,JSONObject incoming){
        if(known==null)return incoming;if(incoming==null)return known;
        try{JSONObject result=new JSONObject(known.toString());Iterator<String> keys=incoming.keys();while(keys.hasNext()){
            String key=keys.next();Object value=incoming.opt(key);
            if(value==null||value==JSONObject.NULL||value instanceof String&&((String)value).trim().isEmpty()||value instanceof JSONArray&&((JSONArray)value).length()==0||value instanceof Number&&((Number)value).doubleValue()==0&&known.optDouble(key,0)>0)continue;
            result.put(key,value);
        }return result;}catch(JSONException invalid){return known;}
    }
    private static void publish(Result result,java.util.function.Consumer<Result> progress){progress.accept(copy(result));}
    private static JSONObject cachedSection(Context c,String kind,long id,String section){
        JSONObject envelope=com.archos.mediacenter.video.leanback.PreviewMetadataCache.read(c,kind,id,section);
        return envelope==null?null:envelope.optJSONObject("data");
    }
    /** Disk-only first delivery; never wait for a refresh of another package section. */
    static Result cachedCore(Context c,String kind,long id){
        Result result=new Result();result.details=cachedSection(c,kind,id,"");result.credits=cachedSection(c,kind,id,"credits");
        result.classification=cachedSection(c,kind,id,"tv".equals(kind)?"content_ratings":"release_dates");result.alternatives=cachedSection(c,kind,id,"alternative_titles");
        if(result.details==null&&result.credits==null)return null;
        if(result.details==null)result.details=new JSONObject();return result;
    }
    static Result cached(Context c,String kind,long id,Set<Long> localIds){
        Result result=cachedCore(c,kind,id);if(result==null)return null;
        JSONArray seasons=result.details.optJSONArray("seasons");
        if("tv".equals(kind)&&seasons!=null)for(int i=0;i<seasons.length();i++){
            JSONObject season=seasons.optJSONObject(i);int number=season==null?-1:season.optInt("season_number",-1);
            if(number<0||number>9999)continue;
            JSONObject data=cachedSection(c,kind,id,"season/"+number);JSONArray episodes=data==null?null:data.optJSONArray("episodes");
            if(episodes!=null)result.episodes.put(number,episodes);
            if(StreamingRepository.prefs(c).getBoolean(StreamingRepository.ENABLED,false)){
                StreamingRepository.Availability known=StreamingRepository.cachedAvailability(c,kind,id,StreamingRepository.country(c),number);
                if(known!=null)result.seasonAvailability.put(number,known);
            }
        }
        addExtras(result,cachedSection(c,kind,id,"videos"));
        JSONObject recommendations=cachedSection(c,kind,id,"recommendations");JSONArray candidates=recommendations==null?null:recommendations.optJSONArray("results");
        result.relatedLoaded=candidates!=null;Set<Long> seen=new HashSet<>();
        if(candidates!=null)for(int i=0;i<Math.min(60,candidates.length())&&result.related.size()<12;i++){
            JSONObject title=candidates.optJSONObject(i);long candidate=title==null?0:title.optLong("id");
            if(candidate<=0||candidate==id||!seen.add(candidate))continue;
            if(localIds.contains(candidate)){result.related.add(new Remote(title,null));continue;}
            if(StreamingRepository.prefs(c).getBoolean(StreamingRepository.ENABLED,false)){
                StreamingRepository.Availability known=StreamingRepository.cachedAvailability(c,kind,candidate,StreamingRepository.country(c),-1);
                if(known!=null){List<StreamingRepository.Offer> offers=StreamingRepository.filter(known,StreamingRepository.selected(c),StreamingRepository.preferred(c));if(!offers.isEmpty())result.related.add(new Remote(title,offers.get(0).provider));}
            }
        }
        return result;
    }
    private static void addExtras(Result result,JSONObject metadata){
        JSONArray videos=metadata==null?null:metadata.optJSONArray("results");result.extrasLoaded=videos!=null;
        if(videos!=null)for(int i=0;i<videos.length()&&result.extras.size()<24;i++){
            JSONObject video=videos.optJSONObject(i);if(video==null)continue;String key=video.optString("key");
            if("YouTube".equals(video.optString("site"))&&key.matches("[A-Za-z0-9_-]{11}"))result.extras.add(new Extra(video.optString("name","Video"),video.optString("type","Video"),key));
        }
        result.extras.sort(Comparator.comparingInt(e->e.type.equals("Trailer")?0:e.type.equals("Teaser")?1:2));
    }
    static Result load(Context c,String kind,long id,Set<Long> localIds)throws Exception{
        return load(c,kind,id,localIds,result->{});
    }
    static Result load(Context c,String kind,long id,Set<Long> localIds,java.util.function.Consumer<Result> progress)throws Exception{
        return load(c,kind,id,localIds,progress,section->StreamingRepository.metadata(c,kind,id,section));
    }
    static Result load(Context c,String kind,long id,Set<Long> localIds,java.util.function.Consumer<Result> progress,Metadata source)throws Exception{
        Result result=new Result();result.details=source.load("");
        publish(result,progress);
        try{result.credits=source.load("credits");publish(result,progress);}catch(Exception unavailable){com.archos.mediacenter.video.diagnostics.Diagnostics.event("credits_metadata_unavailable");}
        if(OmdbSupplement.PRODUCTION.enabled())try{
            JSONObject ids=source.load("external_ids");
            result.supplementary=OmdbSupplement.PRODUCTION.load(ids.optString("imdb_id"),"tv".equals(kind)?"series":"movie");
        }catch(Exception unavailable){/* Supplementary data must never fail the primary metadata package. */}
        try{result.classification=source.load("tv".equals(kind)?"content_ratings":"release_dates");}catch(Exception unavailable){com.archos.mediacenter.video.diagnostics.Diagnostics.event("classification_metadata_unavailable");}
        try{result.alternatives=source.load("alternative_titles");}catch(Exception unavailable){com.archos.mediacenter.video.diagnostics.Diagnostics.event("alternative_titles_unavailable");}
        publish(result,progress);
        // Deliver extras before potentially many season/provider lookups.
        try{addExtras(result,source.load("videos"));publish(result,progress);}catch(Exception error){com.archos.mediacenter.video.diagnostics.Diagnostics.error("extras_metadata_unavailable",error);}
        if("tv".equals(kind)){
            JSONArray seasons=result.details.optJSONArray("seasons");
            if(seasons!=null)for(int i=0;i<seasons.length();i++){
                if(Thread.currentThread().isInterrupted())throw new java.io.InterruptedIOException();
                JSONObject season=seasons.optJSONObject(i);int number=season==null?-1:season.optInt("season_number",-1);if(number<0||number>9999)continue;
                try{JSONArray episodes=source.load("season/"+number).optJSONArray("episodes");if(episodes!=null)result.episodes.put(number,episodes);}catch(Exception unavailable){com.archos.mediacenter.video.diagnostics.Diagnostics.event("season_metadata_unavailable","season",number);}
                if(StreamingRepository.prefs(c).getBoolean(StreamingRepository.ENABLED,false)&&!StreamingRepository.selected(c).isEmpty())try{result.seasonAvailability.put(number,StreamingRepository.load(c,kind,id,StreamingRepository.country(c),number));}catch(Exception unavailable){com.archos.mediacenter.video.diagnostics.Diagnostics.event("season_availability_unavailable","season",number);}
                publish(result,progress);
            }
        }
        JSONArray candidates;try{candidates=source.load("recommendations").optJSONArray("results");}catch(Exception error){com.archos.mediacenter.video.diagnostics.Diagnostics.error("recommendations_unavailable",error);return result;}if(candidates==null)return result;result.relatedLoaded=true;
        Set<String> configured=StreamingRepository.selected(c);String country=StreamingRepository.country(c);
        boolean providersEnabled=StreamingRepository.prefs(c).getBoolean(StreamingRepository.ENABLED,false)&&!configured.isEmpty();
        Set<Long> seen=new HashSet<>();int providerChecks=0;
        for(int i=0;i<Math.min(60,candidates.length())&&result.related.size()<12;i++){if(Thread.currentThread().isInterrupted())break;JSONObject title=candidates.getJSONObject(i);long candidate=title.optLong("id");if(candidate<=0||candidate==id||!seen.add(candidate))continue;
            if(localIds.contains(candidate)){result.related.add(new Remote(title,null));continue;}
            if(!providersEnabled||providerChecks++>=12)continue;
            try{StreamingRepository.Availability availability=StreamingRepository.load(c,kind,candidate,country);List<StreamingRepository.Offer> offers=StreamingRepository.filter(availability,configured,StreamingRepository.preferred(c));if(!offers.isEmpty())result.related.add(new Remote(title,offers.get(0).provider));}catch(Exception error){com.archos.mediacenter.video.diagnostics.Diagnostics.error("discovery_availability_unavailable",error);}
        }return result;
    }
    static Result cachedSeason(Context c,long id,int number){
        Result result=new Result();result.details=cachedSection(c,"tv",id,"season/"+number);result.credits=cachedSection(c,"tv",id,"season/"+number+"/credits");
        if(result.details==null)return null;JSONArray episodes=result.details.optJSONArray("episodes");if(episodes!=null)result.episodes.put(number,episodes);addExtras(result,cachedSection(c,"tv",id,"season/"+number+"/videos"));return result;
    }
    static Result loadSeason(Context c,long id,int number)throws Exception{
        return loadSeason(c,id,number,result->{});
    }
    static Result loadSeason(Context c,long id,int number,java.util.function.Consumer<Result> progress)throws Exception{
        Result result=new Result();result.details=StreamingRepository.metadata(c,"tv",id,"season/"+number);publish(result,progress);JSONArray episodes=result.details.optJSONArray("episodes");if(episodes!=null)result.episodes.put(number,episodes);
        try{result.credits=StreamingRepository.metadata(c,"tv",id,"season/"+number+"/credits");}catch(Exception unavailable){/* Missing season credits are not borrowed from the series. */}
        publish(result,progress);
        try{addExtras(result,StreamingRepository.metadata(c,"tv",id,"season/"+number+"/videos"));}catch(Exception unavailable){/* No fabricated extras. */}
        if(StreamingRepository.prefs(c).getBoolean(StreamingRepository.ENABLED,false))try{result.seasonAvailability.put(number,StreamingRepository.load(c,"tv",id,StreamingRepository.country(c),number));}catch(Exception unavailable){/* Provider scope remains unknown. */}return result;
    }
}
