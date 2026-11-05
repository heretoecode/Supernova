package com.archos.mediacenter.video.leanback;
import android.app.Application;
import android.graphics.*;
import java.io.File;
import java.io.FileOutputStream;
import java.util.Arrays;
import org.junit.*;
import org.junit.runner.RunWith;
import org.robolectric.*;
import org.robolectric.annotation.*;
import static org.junit.Assert.*;

/** Checks shared switch proportions, gradient styling and independent focus presentation. */
@RunWith(RobolectricTestRunner.class)
@Config(application = Application.class, sdk = 30)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
public class StandardSwitchReferenceTest {
    /** Owner decision 2 Nov 2026: switches are ~1.7:1 (44x26dp), not the former 2.7:1. */
    @Test
    public void switchUsesNarrowerSharedProportions() {
        StandardSwitchDrawable icon = new StandardSwitchDrawable(RuntimeEnvironment.getApplication());
        float ratio = icon.getIntrinsicWidth() / (float) icon.getIntrinsicHeight();
        assertEquals(44f / 26f, ratio, 0.05f);
        assertTrue("narrower than the old 2.7:1 face", ratio < 2f);
        assertEquals(34, StandardSwitchDrawable.widthForHeight(20));
    }

    @Test
    public void referenceAspectGradientThumbAndFocusAreIndependent() throws Exception {
        Application c = RuntimeEnvironment.getApplication();
        Bitmap sheet = Bitmap.createBitmap(360, 520, Bitmap.Config.ARGB_8888);
        Canvas output = new Canvas(sheet);
        output.drawColor(0xff101820);
        int row = 0;
        for (boolean enabled : new boolean[] {
            true, false
        })
            for (boolean checked : new boolean[] {
                false, true
            }) {
                StandardSwitchDrawable icon = new StandardSwitchDrawable(c);
                int[] normal = enabled
                    ? (checked ? new int[] {
                        android.R.attr.state_enabled, android.R.attr.state_checked
                    }
                               : new int[] {
                                   android.R.attr.state_enabled
                               })
                    : (checked ? new int[] {
                        android.R.attr.state_checked
                    } : new int[] {
                    });
                icon.setState(normal);
                icon.setBounds(0, 0, 280, 104);
                Bitmap plain = Bitmap.createBitmap(280, 104, Bitmap.Config.ARGB_8888);
                icon.draw(new Canvas(plain));
                int[] pixels = new int[280 * 104];
                plain.getPixels(pixels, 0, 280, 0, 0, 280, 104);
                int[] focused = Arrays.copyOf(normal, normal.length + 1);
                focused[normal.length] = android.R.attr.state_focused;
                icon.setState(focused);
                Bitmap focus = Bitmap.createBitmap(280, 104, Bitmap.Config.ARGB_8888);
                icon.draw(new Canvas(focus));
                int[] focusedPixels = new int[pixels.length];
                focus.getPixels(focusedPixels, 0, 280, 0, 0, 280, 104);
                assertArrayEquals("Focus cannot repaint state", pixels, focusedPixels);
                if (enabled) {
                    long xSum = 0;
                    int whites = 0;
                    for (int y = 0; y < 104; y++)
                        for (int x = 0; x < 280; x++) {
                            int p = plain.getPixel(x, y);
                            if (Color.red(p) > 245 && Color.green(p) > 245 && Color.blue(p) > 245
                                && Color.alpha(p) > 240) {
                                xSum += x;
                                whites++;
                            }
                        }
                    assertTrue("Circular white thumb is painted", whites > 100);
                    assertEquals(checked, xSum / (double) whites > 140);
                    // 2 Nov 2026 proportions: in a 280x104 box the 44:26 track spans x=52..228 and the thumb
                    // (radius 40) sits at x=104 (off) or x=176 (on), so sample track pixels clear of the thumb.
                    if (checked)
                        assertTrue("Cyan to blue gradient",
                            Color.green(plain.getPixel(70, 52)) > Color.green(plain.getPixel(125, 52)) + 10);
                    else
                        assertEquals(StandardSwitchDrawable.OFF, plain.getPixel(190, 52));
                    assertEquals("No track painted outside the 44:26 face", 0, Color.alpha(plain.getPixel(30, 52)));
                } else {
                    int max = 0;
                    for (int p : pixels)
                        max = Math.max(max, Color.alpha(p));
                    assertTrue("Disabled face remains visible and dim", max > 0 && max < 255);
                }
                output.drawBitmap(plain, 40, 10 + row++ * 130, null);
            }
        File file = new File("build/reports/reference-ui/gemini-switch-states-api30.png");
        file.getParentFile().mkdirs();
        try (FileOutputStream stream = new FileOutputStream(file)) {
            sheet.compress(Bitmap.CompressFormat.PNG, 100, stream);
        }
    }
}
