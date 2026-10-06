package com.archos.mediacenter.video.leanback;

import android.animation.ValueAnimator;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.*;
import org.robolectric.annotation.Config;
import org.robolectric.util.ReflectionHelpers;
import static org.junit.Assert.*;

@RunWith(RobolectricTestRunner.class) @Config(application=android.app.Application.class,sdk=28)
public class PreviewFeaturedIndicatorsTest {
    @Test public void rapidRetargetKeepsCurrentWidthsAndOnePillOfTotalEmphasis(){
        PreviewFeaturedIndicators view=new PreviewFeaturedIndicators(RuntimeEnvironment.getApplication());
        view.setPosition(3,0);view.setPosition(3,1);
        ValueAnimator first=ReflectionHelpers.getField(view,"morph");first.setCurrentFraction(.5f);
        float[] before=((float[])ReflectionHelpers.getField(view,"weights")).clone();
        view.setPosition(3,2);
        assertArrayEquals(before,ReflectionHelpers.getField(view,"weights"),.0001f);
        ValueAnimator next=ReflectionHelpers.getField(view,"morph");assertEquals(200,next.getDuration());next.end();
        assertArrayEquals(new float[]{0,0,1},ReflectionHelpers.getField(view,"weights"),.0001f);
        assertFalse(view.isFocusable());
    }
    @Test public void backwardsWrapAndEmptyCarouselAreSafe(){
        PreviewFeaturedIndicators view=new PreviewFeaturedIndicators(RuntimeEnvironment.getApplication());
        view.setPosition(3,-1);assertArrayEquals(new float[]{0,0,1},ReflectionHelpers.getField(view,"weights"),.0001f);
        view.setPosition(0,0);assertEquals(0,((float[])ReflectionHelpers.getField(view,"weights")).length);
    }
}
