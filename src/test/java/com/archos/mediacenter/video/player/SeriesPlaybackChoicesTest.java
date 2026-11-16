package com.archos.mediacenter.video.player;

import static org.junit.Assert.assertEquals;

import com.archos.mediacenter.utils.introdb.IntroSegments;
import org.junit.After;
import org.junit.Test;

public class SeriesPlaybackChoicesTest
{
    @After
    public void clear()
    {
        SeriesPlaybackChoices.useSkipping(null);
    }

    @Test
    public void aSeriesSkippingChoiceAppliesToIntrosAndCreditsOnly()
    {
        SeriesPlaybackChoices.useSkipping(SegmentSkippingPolicy.Mode.AUTO);
        assertEquals(SegmentSkippingPolicy.Mode.AUTO,
                     SeriesPlaybackChoices.skipFor(IntroSegments.Type.INTRO, SegmentSkippingPolicy.Mode.PROMPT));
        assertEquals(SegmentSkippingPolicy.Mode.AUTO,
                     SeriesPlaybackChoices.skipFor(IntroSegments.Type.CREDITS, SegmentSkippingPolicy.Mode.PROMPT));
        assertEquals("Recaps keep their own setting", SegmentSkippingPolicy.Mode.PROMPT,
                     SeriesPlaybackChoices.skipFor(IntroSegments.Type.RECAP, SegmentSkippingPolicy.Mode.PROMPT));
    }

    @Test
    public void withoutASeriesChoiceTheEverydaySettingApplies()
    {
        SeriesPlaybackChoices.useSkipping(null);
        assertEquals(SegmentSkippingPolicy.Mode.NORMAL,
                     SeriesPlaybackChoices.skipFor(IntroSegments.Type.INTRO, SegmentSkippingPolicy.Mode.NORMAL));
    }
}
