package com.archos.mediacenter.video.leanback;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class TopBarSignalTest
{
    @Test
    public void oneThingAtATimeWithTheScanFirst()
    {
        assertEquals(TopBarSignal.State.RING, TopBarSignal.decide(true, 3));
        assertEquals(TopBarSignal.State.RING, TopBarSignal.decide(true, 0));
        assertEquals(TopBarSignal.State.DOT, TopBarSignal.decide(false, 1));
        assertEquals(TopBarSignal.State.NONE, TopBarSignal.decide(false, 0));
    }
}
