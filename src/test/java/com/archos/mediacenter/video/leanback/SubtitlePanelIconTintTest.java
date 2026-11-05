package com.archos.mediacenter.video.leanback;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import android.app.Application;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;
import org.robolectric.annotation.GraphicsMode;

/** 0.155 notes #3: the Audio and Playback tab icons follow the focus colour like the Subtitles one. */
@RunWith(RobolectricTestRunner.class)
@Config(application = Application.class, sdk = 28)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
public class SubtitlePanelIconTintTest {
    @Test
    public void sharedIconsTakeTheColourFilter() {
        for (String kind : new String[] {
            "cc", "sound", "play"
        }) {
            SubtitlePanelStyle.Icon icon = new SubtitlePanelStyle.Icon(kind);
            icon.setColorFilter(new PorterDuffColorFilter(Color.BLACK, PorterDuff.Mode.SRC_IN));
            icon.setBounds(0, 0, 48, 48);
            Bitmap image = Bitmap.createBitmap(48, 48, Bitmap.Config.ARGB_8888);
            icon.draw(new Canvas(image));
            boolean dark = false;
            boolean white = false;
            for (int y = 0; y < 48; y++) {
                for (int x = 0; x < 48; x++) {
                    int pixel = image.getPixel(x, y);
                    if (Color.alpha(pixel) > 200 && Color.red(pixel) < 40) {
                        dark = true;
                    }
                    if (Color.alpha(pixel) > 200 && Color.red(pixel) > 215) {
                        white = true;
                    }
                }
            }
            image.recycle();
            assertTrue(kind + " is drawn in the focus colour", dark);
            assertFalse(kind + " has no white left on the white focus box", white);
        }
    }
}
