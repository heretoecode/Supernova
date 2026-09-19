package com.archos.mediacenter.video.player;
import com.archos.mediacenter.utils.introdb.IntroSegments;
import org.junit.Test;
import static org.junit.Assert.*;
public class PreviewUpNextTest {
 @Test public void realCreditsTimingOverridesFallback(){IntroSegments segments=new IntroSegments();segments.add(IntroSegments.Type.CREDITS,new IntroSegments.Segment(1700000L,1800000L,0,0,"fixture"));assertEquals(1700000,PreviewUpNext.trigger(1800000,segments));assertEquals(1770000,PreviewUpNext.trigger(1800000,null));}
}
