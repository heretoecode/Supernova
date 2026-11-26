package com.archos.mediacenter.video.leanback;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

/** RULES §19 logo placement (owner, 18 Nov 2026; title-style/05): one area inside the title's box (900 × 240). */
public class TitleLogoPlacementTest {
    @Test
    public void logosShareOneAreaAndStayInsideTheBox() {
        float area = 900 * 240 * .42f;
        float[] wide = OfficialTitleArtwork.placement(800, 300, 900, 240);
        assertEquals(area, wide[0] * wide[1], area * .01f);
        float[] square = OfficialTitleArtwork.placement(500, 500, 900, 240);
        assertEquals("a square logo is capped by height", 240, square[1], .5f);
        float[] veryWide = OfficialTitleArtwork.placement(2000, 100, 900, 240);
        assertEquals("very wide logos are capped by width", 900, veryWide[0], .5f);
        float[] small = OfficialTitleArtwork.placement(800, 300, 300, 60);
        assertTrue("never beyond the box", small[0] <= 300 && small[1] <= 60);
    }
}
