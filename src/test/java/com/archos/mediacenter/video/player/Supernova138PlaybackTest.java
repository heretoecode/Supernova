package com.archos.mediacenter.video.player;

import org.junit.Test;
import org.json.JSONObject;
import static org.junit.Assert.*;

@org.junit.runner.RunWith(org.robolectric.RobolectricTestRunner.class) @org.robolectric.annotation.Config(application=android.app.Application.class,sdk=28)
public class Supernova138PlaybackTest {
    @Test public void predictionUsesPreviewAndSpeedAndPausedResumeNowSemantics(){assertEquals(60000,PreviewHudTiming.end(10000,100000,50000,1));assertEquals(35000,PreviewHudTiming.end(10000,100000,50000,2));assertEquals(20000,PreviewHudTiming.end(20000,100000,120000,1));assertEquals(110000,PreviewHudTiming.end(10000,100000,-1000,Float.NaN));assertEquals(-1,PreviewHudTiming.end(10000,0,0,1));}
    @Test public void compactPillNeverClipsAtEitherBoundary(){assertEquals(0,PreviewHudTiming.pillLeft(-25,400,50),0);assertEquals(350,PreviewHudTiming.pillLeft(390,400,50),0);assertEquals(0,PreviewHudTiming.pillLeft(0,40,50),0);}
    @Test public void providerTaglineFallbackSkipsSynopsisAndEpisodeNames()throws Exception{JSONObject movie=new JSONObject().put("tagline","Movie tagline"),episode=new JSONObject().put("tagline","Episode tagline"),season=new JSONObject().put("tagline","Season tagline"),show=new JSONObject().put("tagline","Show tagline");assertEquals("Movie tagline",PreviewTaglines.choose(movie,null,null,null));assertEquals("Episode tagline",PreviewTaglines.choose(null,episode,season,show));assertEquals("Season tagline",PreviewTaglines.choose(null,new JSONObject().put("overview","Synopsis"),season,show));assertEquals("Show tagline",PreviewTaglines.choose(null,null,new JSONObject(),show));assertEquals("",PreviewTaglines.choose(new JSONObject().put("overview","Synopsis").put("name","Episode 1"),null,null,null));}
}
