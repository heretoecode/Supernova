package com.archos.mediacenter.video.player;

import static org.junit.Assert.assertEquals;
import org.junit.Test;

public class SeekThumbnailPolicyTest
{
    @Test public void localAndNetworkCadenceBackOffOnlyAbove400MsAndCapAtTwoMinutes()
    {
        SeekThumbnailPolicy local = new SeekThumbnailPolicy(false);
        SeekThumbnailPolicy network = new SeekThumbnailPolicy(true);
        assertEquals(10000, local.intervalMs());
        assertEquals(45000, network.intervalMs());
        local.extracted(400);
        assertEquals(10000, local.intervalMs());
        local.extracted(401);
        assertEquals(20000, local.intervalMs());
        network.extracted(401);
        assertEquals(90000, network.intervalMs());
        network.extracted(401);
        assertEquals(120000, network.intervalMs());
    }

    @Test public void allPauseReasonsGateEvenPriorityRequestsButPausedPlaybackAllowsAnExplicitPicture()
    {
        SeekThumbnailPolicy policy = new SeekThumbnailPolicy(false);
        policy.state(true, true, false, true, false, true);
        assertEquals("buffering", policy.skipReason(true));
        policy.state(true, false, true, true, false, true);
        assertEquals("seeking", policy.skipReason(true));
        policy.state(true, false, false, false, false, true);
        assertEquals("background", policy.skipReason(true));
        policy.state(true, false, false, true, true, true);
        assertEquals("pip", policy.skipReason(true));
        policy.state(true, false, false, true, false, false);
        assertEquals("paused", policy.skipReason(false));
        assertEquals("", policy.skipReason(true));
    }
}
