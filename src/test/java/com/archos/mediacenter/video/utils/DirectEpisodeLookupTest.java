package com.archos.mediacenter.video.utils;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;
import org.json.*;
import static org.junit.Assert.*;

@RunWith(RobolectricTestRunner.class) @Config(application=android.app.Application.class,sdk=28)
public class DirectEpisodeLookupTest {
    @Test public void coordinatesKeepSpecialsAndDoNotGuessTitlesOrUnboundedNumbers(){
        assertArrayEquals(new int[]{0,2},DirectEpisodeLookup.coordinates("S00 E02"));
        assertArrayEquals(new int[]{12,31},DirectEpisodeLookup.coordinates("s12e31"));
        assertNull(DirectEpisodeLookup.coordinates("12345"));assertNull(DirectEpisodeLookup.coordinates("Series S1E1"));assertNull(DirectEpisodeLookup.coordinates("S10000E1"));
    }
    @Test public void numericInputMeansAnEpisodeIdentifierNotAnEpisodeNumber()throws Exception{
        JSONObject item=new JSONObject("{\"id\":63056,\"episode_number\":1,\"name\":\"Winter Is Coming\"}");
        assertTrue(DirectEpisodeLookup.matches(item,"63056"));assertFalse(DirectEpisodeLookup.matches(item,"1"));assertTrue(DirectEpisodeLookup.matches(item,"WINTER"));assertFalse(DirectEpisodeLookup.matches(item,""));
    }
    @Test public void externalIdentifiersCannotSilentlyChangeTheParentSeries()throws Exception{
        JSONObject response=new JSONObject("{\"tv_episode_results\":[{\"show_id\":1399,\"season_number\":1,\"episode_number\":2}]}");
        assertArrayEquals(new int[]{1,2},DirectEpisodeLookup.imdbEpisode(response,1399));assertNull(DirectEpisodeLookup.imdbEpisode(response,1396));
        assertNull(DirectEpisodeLookup.imdbEpisode(new JSONObject("{\"movie_results\":[{\"id\":1399}]}"),1399));
    }
}
