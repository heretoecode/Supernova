package com.archos.mediacenter.video.leanback;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

/** RULES §19 logo placement (owner, 18 Nov 2026; title-style/05): same area inside 900 × 240 at 1080p. */
public class TitleLogoPlacementTest {
    @Test
    public void logosShareOneAreaAndStayInsideTheBox() {
        float[] square = OfficialTitleArtwork.placement(500, 500, 2000, 2000, 1f);
        float[] wide = OfficialTitleArtwork.placement(800, 300, 2000, 2000, 1f);
        float area = 900 * 240 * .42f;
        assertEquals(area, wide[0] * wide[1], area * .01f);
        // A square logo is capped by height: 240 tall at most.
        assertEquals(240, square[1], .5f);
        float[] veryWide = OfficialTitleArtwork.placement(2000, 100, 2000, 2000, 1f);
        assertEquals("very wide logos are capped by width", 900, veryWide[0], .5f);
        float[] small = OfficialTitleArtwork.placement(800, 300, 300, 60, 1f);
        assertTrue("never beyond the view", small[0] <= 300 && small[1] <= 60);
    }
}
