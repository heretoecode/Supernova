package com.archos.mediacenter.video.player;

import static org.junit.Assert.assertEquals;

import android.graphics.Bitmap;
import android.graphics.Canvas;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.GraphicsMode;

/** Owner-approved Skip button (notes #17, option B): white drains to light grey over 10 seconds. */
@RunWith(RobolectricTestRunner.class)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
public class SkipButtonDrainTest {
    @Test
    public void buttonShowsForTenSecondsAndItsWhiteDrainsFromTheRight() {
        assertEquals(10_000, PreviewUpNext.SKIP_SHOWN_MS);
        PreviewUpNext.SkipDrain drain = new PreviewUpNext.SkipDrain(0);
        drain.setBounds(0, 0, 200, 40);
        drain.fraction = .5f;
        Bitmap image = Bitmap.createBitmap(200, 40, Bitmap.Config.ARGB_8888);
        drain.draw(new Canvas(image));
        assertEquals("time left: white", 0xFFF4F6FB, image.getPixel(40, 20));
        assertEquals("time used: light grey", 0xFFAEB4BF, image.getPixel(160, 20));
        image.recycle();
    }
}
