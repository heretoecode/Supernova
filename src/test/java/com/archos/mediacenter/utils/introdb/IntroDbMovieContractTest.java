package com.archos.mediacenter.utils.introdb;

import android.app.Application;
import org.json.JSONObject;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;
import static org.junit.Assert.*;

@RunWith(RobolectricTestRunner.class) @Config(application=Application.class,sdk=28)
public class IntroDbMovieContractTest {
    @Test public void movieReadsRequireImdbAndOmitEpisodeIdentifiers() {
        IntroDbAppQueryParams params=new IntroDbAppQueryParams();params.setImdbId("tt0371746");
        assertFalse(params.isValid());params.setMovie(true);assertTrue(params.isValid());
        assertNull(params.getSeason());assertNull(params.getEpisode());
        params.setImdbId("unsupported");assertFalse(params.isValid());
    }
    @Test public void tvReadsStillRequirePositiveEpisodeIdentifiers() {
        IntroDbAppQueryParams params=new IntroDbAppQueryParams();params.setImdbId("tt0903747");
        params.setSeason(1);params.setEpisode(1);assertTrue(params.isValid());
        params.setEpisode(0);assertFalse(params.isValid());
    }
    @Test public void reportedPostCreditSceneLimitsCreditsSkip() throws Exception {
        // Documented API schema, synthetic timings; no playback/provider coverage claim.
        JSONObject json=new JSONObject("{\"imdb_id\":\"tt0371746\",\"is_movie\":true,\"outro\":{\"start_ms\":600000,\"end_ms\":750000,\"confidence\":1,\"submission_count\":2},\"post_credits\":{\"start_ms\":700000,\"end_ms\":730000}}");
        IntroDbAppResult result=IntroDbAppApiHelper.parseSegments(json);
        assertEquals(700000,result.getOutro().endMs);assertEquals(700000,result.getPostCredits().startMs);
        assertNull(result.getIntro());
    }
    @Test public void contradictorySceneMarkerCannotSkipProtectedMaterial() {
        IntroDbAppResult.Segment outro=new IntroDbAppResult.Segment(600000,750000,1,2);
        assertNull(IntroDbAppResult.protectedOutro(outro,new IntroDbAppResult.Segment(590000,650000,1,2)));
        assertSame(outro,IntroDbAppResult.protectedOutro(outro,null));
    }
}
