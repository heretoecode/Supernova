package com.archos.mediacenter.video.leanback.details;

import android.app.Application;
import org.json.JSONObject;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;
import static org.junit.Assert.*;

@RunWith(RobolectricTestRunner.class) @Config(application=Application.class,sdk=28)
public class PreviewDetailsFactsTest {
    @Test public void classificationNeverBorrowsAnotherCountrysRating()throws Exception{
        JSONObject movie=new JSONObject("{results:[{iso_3166_1:'US',release_dates:[{certification:'R',type:3}]},{iso_3166_1:'IE',release_dates:[{certification:'18',type:4},{certification:'15A',type:3}]}]}");
        assertEquals("15A (IE)",PreviewDetailsFacts.certificate(movie,"IE"));assertEquals("",PreviewDetailsFacts.certificate(movie,"FR"));
        assertEquals("12 (IE)",PreviewDetailsFacts.certificate(new JSONObject("{results:[{iso_3166_1:'IE',rating:'12'}]}"),"IE"));
    }
    @Test public void remoteYearAndEpisodeRuntimesUseOnlyPublishedValues()throws Exception{
        assertEquals(2024,PreviewDetailsFacts.year(new JSONObject("{first_air_date:'2024-03-12'}")));
        assertEquals(0,PreviewDetailsFacts.year(new JSONObject()));
        assertEquals("42 min, 60 min",PreviewDetailsFacts.episodeRuntimes(new JSONObject("{episode_run_time:[42,0,42,60,-1]}")));
        assertEquals("",PreviewDetailsFacts.episodeRuntimes(new JSONObject()));
    }
}
