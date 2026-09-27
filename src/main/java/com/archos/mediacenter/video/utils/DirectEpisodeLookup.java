package com.archos.mediacenter.video.utils;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import com.archos.mediascraper.*;
import com.archos.mediascraper.xml.ShowScraper4;
import com.archos.mediacenter.video.streaming.StreamingRepository;
import org.json.*;
import java.util.*;
import java.util.regex.*;
import java.io.IOException;

/** Episode correction is constrained to the current parent; changing it is a separate action. */
public final class DirectEpisodeLookup {
    /** Resolve from the physical media record after series correction, not a stale Episode object. */
    public static EpisodeTags current(Context context,long mediaId)throws IOException{
        try(android.database.Cursor cursor=context.getContentResolver().query(com.archos.mediaprovider.video.VideoStore.Video.Media.EXTERNAL_CONTENT_URI,TagsFactory.VIDEO_COLUMNS,"_id=?",new String[]{String.valueOf(mediaId)},null)){
            if(cursor!=null){List<BaseTags> tags=TagsFactory.buildTagsFromVideoCursor(cursor);if(tags!=null&&tags.size()==1&&tags.get(0) instanceof EpisodeTags)return (EpisodeTags)tags.get(0);}
        }
        throw new IOException("Current episode metadata is unavailable.");
    }
    private static final Pattern COORDINATES=Pattern.compile("(?i)^S([0-9]{1,4})\\s*E([0-9]{1,4})$");
    public static ScrapeSearchResult find(Context context,ShowTags parent,String input,Uri file,int limit)throws Exception{
        if(parent==null||parent.getOnlineId()<=0)throw new IOException("The current series match is unavailable.");
        String query=input==null?"":input.trim();int[] coordinate=coordinates(query);
        if(query.matches("(?i)tt[0-9]{5,12}")){
            JSONObject found=DirectShowLookup.request(context,"find/"+query.toLowerCase(Locale.ROOT),true);
            coordinate=imdbEpisode(found,parent.getOnlineId());
            if(coordinate==null)return empty();
        }
        JSONObject show=StreamingRepository.metadata(context,"tv",parent.getOnlineId(),"");
        JSONArray seasons=show.optJSONArray("seasons");List<SearchResult> results=new ArrayList<>();
        if(seasons==null)throw new IOException("Season metadata is unavailable.");
        for(int i=0;i<seasons.length()&&results.size()<limit;i++){
            if(Thread.currentThread().isInterrupted())throw new java.io.InterruptedIOException();
            JSONObject season=seasons.optJSONObject(i);int number=season==null?-1:season.optInt("season_number",-1);
            if(number<0||number>9999||coordinate!=null&&coordinate[0]!=number)continue;
            JSONObject data=StreamingRepository.metadata(context,"tv",parent.getOnlineId(),"season/"+number);
            JSONArray episodes=data.optJSONArray("episodes");if(episodes==null)throw new IOException("Episode metadata is unavailable.");
            for(int j=0;j<episodes.length()&&results.size()<limit;j++){
                JSONObject episode=episodes.optJSONObject(j);if(episode==null)continue;
                int episodeNumber=episode.optInt("episode_number",-1);
                if(episodeNumber<1||episodeNumber>9999)continue;
                boolean matches=coordinate!=null?coordinate[1]==episodeNumber:matches(episode,query);
                if(matches)results.add(result(context,parent,file,number,episodeNumber));
            }
        }
        return new ScrapeSearchResult(results,false,ScrapeStatus.OKAY,null);
    }
    static int[] coordinates(String input){Matcher match=COORDINATES.matcher(input);return match.matches()?new int[]{Integer.parseInt(match.group(1)),Integer.parseInt(match.group(2))}:null;}
    static boolean matches(JSONObject episode,String query){
        if(query.matches("[1-9][0-9]*"))return query.equals(String.valueOf(episode.optLong("id",-1)));
        return !query.isEmpty()&&episode.optString("name").toLowerCase(Locale.ROOT).contains(query.toLowerCase(Locale.ROOT));
    }
    static int[] imdbEpisode(JSONObject response,long parentId)throws IOException{
        JSONArray episodes=response.optJSONArray("tv_episode_results");if(episodes==null||episodes.length()==0)return null;
        if(episodes.length()!=1)throw new IOException("The episode identifier is ambiguous.");
        JSONObject episode=episodes.optJSONObject(0);if(episode==null||episode.optLong("show_id",-1)!=parentId)return null;
        int season=episode.optInt("season_number",-1),number=episode.optInt("episode_number",-1);
        return season>=0&&season<=9999&&number>0&&number<=9999?new int[]{season,number}:null;
    }
    static SearchResult result(Context context,ShowTags parent,Uri file,int season,int episode)throws IOException{
        long id=parent.getOnlineId();if(id<=0||id>Integer.MAX_VALUE)throw new IOException("Invalid series identifier.");
        SearchResult result=new SearchResult();result.setTvShow();result.setId((int)id);result.setTitle(parent.getTitle());result.setOriginalTitle(parent.getTitle());
        result.setLanguage(Scraper.getLanguage(context));result.setFile(file);result.setScraper(new ShowScraper4(context));
        result.setOriginSearchSeason(season);result.setOriginSearchEpisode(episode);
        Bundle extra=new Bundle();extra.putString(ShowUtils.SEASON,String.valueOf(season));extra.putString(ShowUtils.EPNUM,String.valueOf(episode));result.setExtra(extra);return result;
    }
    private static ScrapeSearchResult empty(){return new ScrapeSearchResult(Collections.emptyList(),false,ScrapeStatus.OKAY,null);}
    private DirectEpisodeLookup(){}
}
