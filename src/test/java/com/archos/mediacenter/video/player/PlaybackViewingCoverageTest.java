package com.archos.mediacenter.video.player;
import org.junit.Test;
import static org.junit.Assert.*;
@org.junit.runner.RunWith(org.robolectric.RobolectricTestRunner.class)
@org.robolectric.annotation.Config(application=android.app.Application.class,sdk=28)
public class PlaybackViewingCoverageTest {
 @Test public void repeatedScenesDoNotCountTwice(){PlaybackViewingCoverage coverage=new PlaybackViewingCoverage();coverage.add(0,200);coverage.add(50,250);assertEquals(250,coverage.viewed());assertFalse(coverage.eligible(1000));}
 @Test public void seekToEndIsNotWatched(){PlaybackViewingCoverage coverage=new PlaybackViewingCoverage();coverage.add(990000,1000000);assertFalse(coverage.eligible(1000000));}
 @Test public void ninetyPercentOfDistinctPlaybackQualifiesAndPersists(){PlaybackViewingCoverage coverage=new PlaybackViewingCoverage();coverage.add(0,450);coverage.add(500,950);assertTrue(coverage.eligible(1000));assertEquals(900,PlaybackViewingCoverage.decode(coverage.encode()).viewed());}
 @Test public void invalidIntervalsAndUnknownDurationNeverQualify(){PlaybackViewingCoverage coverage=new PlaybackViewingCoverage();coverage.add(-1,100);coverage.add(100,99);assertEquals(0,coverage.viewed());assertFalse(coverage.eligible(0));}
}
