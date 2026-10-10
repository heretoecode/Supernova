package com.archos.mediacenter.video.player;
import android.app.Application;
import com.archos.mediacenter.utils.introdb.IntroSegments;
import org.junit.*;
import org.junit.runner.RunWith;
import org.robolectric.*;
import org.robolectric.annotation.Config;
import static org.junit.Assert.*;
@RunWith(RobolectricTestRunner.class) @Config(application=Application.class,sdk=28)
public class SegmentSkippingPolicyTest {
 @Test public void defaultsAreIndependentAndMasterIsOn(){var prefs=androidx.preference.PreferenceManager.getDefaultSharedPreferences(RuntimeEnvironment.getApplication());assertTrue(prefs.getBoolean(SegmentSkippingPolicy.MASTER,true));assertEquals(SegmentSkippingPolicy.Mode.PROMPT,SegmentSkippingPolicy.mode(prefs,IntroSegments.Type.INTRO));assertEquals(SegmentSkippingPolicy.Mode.SMART,SegmentSkippingPolicy.mode(prefs,IntroSegments.Type.RECAP));assertEquals(SegmentSkippingPolicy.Mode.AUTO,SegmentSkippingPolicy.mode(prefs,IntroSegments.Type.OUTRO));prefs.edit().putString(SegmentSkippingPolicy.key(IntroSegments.Type.INTRO),"NORMAL").commit();assertEquals(SegmentSkippingPolicy.Mode.SMART,SegmentSkippingPolicy.mode(prefs,IntroSegments.Type.RECAP));}
 @Test public void smartRecapRequiresAutomaticBingeArrivalAndUnclassifiedPreviewPlays(){assertFalse(SegmentSkippingPolicy.auto(SegmentSkippingPolicy.Mode.SMART,IntroSegments.Type.RECAP,false));assertTrue(SegmentSkippingPolicy.auto(SegmentSkippingPolicy.Mode.SMART,IntroSegments.Type.RECAP,true));assertFalse(SegmentSkippingPolicy.auto(SegmentSkippingPolicy.Mode.SMART,IntroSegments.Type.PREVIEW,true));}
 @Test public void openEndedCreditsAndOutOfRangeTargetsCannotSkipPostCredits(){assertFalse(SegmentSkippingPolicy.safe(IntroSegments.Type.CREDITS,new IntroSegments.Segment(90000L,null,0,0,"fixture"),91000,100000));assertFalse(SegmentSkippingPolicy.safe(IntroSegments.Type.CREDITS,new IntroSegments.Segment(90000L,100000L,0,0,"fixture"),91000,100000));assertTrue(SegmentSkippingPolicy.safe(IntroSegments.Type.CREDITS,new IntroSegments.Segment(80000L,90000L,0,0,"fixture"),81000,100000));}
}
