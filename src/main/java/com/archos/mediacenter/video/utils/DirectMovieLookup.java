package com.archos.mediacenter.video.utils;

import android.content.Context;
import android.net.Uri;
import com.archos.mediascraper.*;
import com.archos.mediascraper.xml.MovieScraper3;
import java.io.IOException;
import java.util.Collections;
import org.json.*;

/** Identifier lookup produces the same native SearchResult and explicit Match Preview path. */
public final class DirectMovieLookup {
    public static ScrapeSearchResult find(Context context,String input,Uri file)throws Exception{
        String identifier=DirectShowLookup.identifier(input,"movie");if(identifier==null)return null;
        int id;
        if(identifier.startsWith("tt"))id=movieId(DirectShowLookup.request(context,"find/"+identifier,true));
        else{try{id=Integer.parseInt(identifier);}catch(NumberFormatException invalid){throw new IOException("That TMDb ID is too large.");}}
        JSONObject movie=DirectShowLookup.request(context,"movie/"+id,false);
        SearchResult result=new SearchResult();result.setMovie();result.setId(id);result.setTitle(movie.getString("title"));
        result.setOriginalTitle(movie.optString("original_title",movie.getString("title")));result.setLanguage(Scraper.getLanguage(context));result.setFile(file);
        result.setScraper(new MovieScraper3(context));if(!movie.isNull("poster_path"))result.setPosterPath(movie.getString("poster_path"));
        return new ScrapeSearchResult(Collections.singletonList(result),true,ScrapeStatus.OKAY,null);
    }
    static int movieId(JSONObject response)throws IOException{
        JSONArray movies=response.optJSONArray("movie_results");
        if(movies==null||movies.length()!=1)throw new IOException("No unique TMDb movie is linked to that IMDb ID.");
        JSONObject movie=movies.optJSONObject(0);long id=movie==null?0:movie.optLong("id");
        if(id<=0||id>Integer.MAX_VALUE)throw new IOException("Invalid movie identifier.");return (int)id;
    }
    private DirectMovieLookup(){}
}
