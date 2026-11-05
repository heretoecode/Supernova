package com.archos.mediacenter.video.player;

import static org.junit.Assert.assertEquals;
import org.junit.Test;

public class SubtitlePresentationGateTest
{
    @Test
    public void pictureInPictureAndAppearanceRestoreTheLatestChoiceOnlyAfterBothClose()
    {
        SubtitlePresentationGate gate = new SubtitlePresentationGate();
        gate.set("pip", true);
        gate.set("appearance", true);
        assertEquals(7, gate.track(2, 7));
        gate.set("appearance", false);
        assertEquals(7, gate.track(3, 7));
        gate.set("pip", false);
        assertEquals(3, gate.track(3, 7));
    }
}
