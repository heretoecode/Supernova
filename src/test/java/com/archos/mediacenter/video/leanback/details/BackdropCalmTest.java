package com.archos.mediacenter.video.leanback.details;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;

import org.junit.Test;

public class BackdropCalmTest
{
    @Test
    public void aBrightSkyBehindTheTextGetsAShadeADarkOneDoesNot()
    {
        int width = 100;
        int height = 50;
        int[] bright = new int[width * height];
        Arrays.fill(bright, 0xFFF0F0F0);
        assertTrue(BackdropCalm.needsShade(BackdropCalm.leftBrightness(bright, width, height)));
        int[] dark = new int[width * height];
        Arrays.fill(dark, 0xFF202020);
        assertFalse(BackdropCalm.needsShade(BackdropCalm.leftBrightness(dark, width, height)));
    }

    @Test
    public void onlyTheTextSideCounts()
    {
        int width = 100;
        int height = 10;
        int[] pixels = new int[width * height];
        for (int y = 0; y < height; y++)
        {
            for (int x = 0; x < width; x++)
            {
                pixels[y * width + x] = x < 50 ? 0xFF101010 : 0xFFFFFFFF;
            }
        }
        assertFalse(BackdropCalm.needsShade(BackdropCalm.leftBrightness(pixels, width, height)));
    }
}
