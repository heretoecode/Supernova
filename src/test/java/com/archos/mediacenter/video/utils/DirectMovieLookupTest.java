package com.archos.mediacenter.video.utils;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;
import org.json.*;
import static org.junit.Assert.*;

@RunWith(RobolectricTestRunner.class) @Config(application=android.app.Application.class,sdk=28)
public class DirectMovieLookupTest {
    @Test public void oneFieldAcceptsMovieIdentifiersAndUppercaseKeyboardImdbInput(){
        assertEquals("550",DirectShowLookup.identifier("550","movie"));
        assertEquals("550",DirectShowLookup.identifier("https://www.themoviedb.org/movie/550-fight-club","movie"));
        assertEquals("tt0137523",DirectShowLookup.identifier("TT0137523","movie"));
        assertNull(DirectShowLookup.identifier("https://www.themoviedb.org/tv/550","movie"));
        assertNull(DirectShowLookup.identifier("https://themoviedb.org.evil.invalid/movie/550","movie"));
        assertNull(DirectShowLookup.identifier("A real title","movie"));
    }
    @Test public void imdbMovieResolutionNeverAcceptsATvResultOrAmbiguousMatch()throws Exception{
        assertEquals(550,DirectMovieLookup.movieId(new JSONObject("{\"movie_results\":[{\"id\":550}]}")));
        for(String response:new String[]{"{\"tv_results\":[{\"id\":550}]}","{\"movie_results\":[{\"id\":1},{\"id\":2}]}","{\"movie_results\":[{\"id\":-1}]}"})
            try{DirectMovieLookup.movieId(new JSONObject(response));fail("Invalid identity accepted");}catch(java.io.IOException expected){}
    }
}
