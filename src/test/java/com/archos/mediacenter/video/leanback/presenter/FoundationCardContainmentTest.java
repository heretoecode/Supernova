package com.archos.mediacenter.video.leanback.presenter;

import android.app.Activity;
import android.app.Application;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Looper;
import android.view.View;
import android.widget.FrameLayout;
import com.squareup.picasso.*;
import java.io.File;
import java.io.FileOutputStream;
import java.time.Duration;
import org.junit.*;
import org.junit.runner.RunWith;
import org.robolectric.*;
import org.robolectric.annotation.Config;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith(RobolectricTestRunner.class)
@Config(application = Application.class, sdk = 28)
public class FoundationCardContainmentTest {
    @Test
    @org.robolectric.annotation.GraphicsMode(org.robolectric.annotation.GraphicsMode.Mode.NATIVE)
    public void artworkPixelsStayInsideRoundedBody() {
        PreviewCardPresenter.Card card =
            new PreviewCardPresenter.Card(RuntimeEnvironment.getApplication(), PreviewCardPresenter.Style.CONTINUE);
        card.image.setImageDrawable(new ColorDrawable(Color.RED));
        card.measure(View.MeasureSpec.makeMeasureSpec(172, View.MeasureSpec.EXACTLY),
            View.MeasureSpec.makeMeasureSpec(105, View.MeasureSpec.EXACTLY));
        card.layout(0, 0, 172, 105);
        View body = card.getChildAt(0);
        Bitmap pixels =
            Bitmap.createBitmap(172, 105, Bitmap.Config.ARGB_8888);
        body.draw(new Canvas(pixels));
        assertNotEquals(Color.RED, pixels.getPixel(0, 0));
        assertNotEquals(Color.RED, pixels.getPixel(171, 0));
        assertEquals(Color.RED, pixels.getPixel(86, 8));
        pixels.recycle();
    }

    @Test
    @org.robolectric.annotation.GraphicsMode(org.robolectric.annotation.GraphicsMode.Mode.NATIVE)
    public void focusedArtworkAndBoundaryEnlargeTogetherInRenderedPixels() throws Exception {
        Activity host = Robolectric.buildActivity(Activity.class).setup().visible().get();
        FrameLayout root = new FrameLayout(host);
        root.setClipChildren(false);
        root.setFocusableInTouchMode(true);
        root.setBackgroundColor(Color.BLACK);
        PreviewCardPresenter.Card card = new PreviewCardPresenter.Card(host, PreviewCardPresenter.Style.CONTINUE);
        card.image.setImageDrawable(new ColorDrawable(Color.RED));
        FrameLayout.LayoutParams size = new FrameLayout.LayoutParams(172, 105);
        size.leftMargin = 60;
        size.topMargin = 50;
        root.addView(card, size);
        host.setContentView(root);
        root.measure(View.MeasureSpec.makeMeasureSpec(320, View.MeasureSpec.EXACTLY),
            View.MeasureSpec.makeMeasureSpec(220, View.MeasureSpec.EXACTLY));
        root.layout(0, 0, 320, 220);
        root.requestFocus();
        Shadows.shadowOf(Looper.getMainLooper()).idleFor(Duration.ofMillis(200));
        Bitmap before =
            Bitmap.createBitmap(320, 220, Bitmap.Config.ARGB_8888);
        root.draw(new Canvas(before));
        card.requestFocus();
        Shadows.shadowOf(Looper.getMainLooper()).idleFor(Duration.ofMillis(200));
        Bitmap after =
            Bitmap.createBitmap(320, 220, Bitmap.Config.ARGB_8888);
        root.draw(new Canvas(after));
        int[] old = redBounds(before, 85), now = redBounds(after, 85);
        assertTrue("Artwork left edge enlarges with the unit", now[0] < old[0] - 3);
        assertTrue("Artwork right edge enlarges with the unit", now[1] > old[1] + 3);
        boolean leftGlow = false, rightGlow = false;
        for (int x = now[0] - 7; x < now[0]; x++) {
            int c = after.getPixel(x, 85);
            leftGlow |= Color.blue(c) > Color.red(c) + 10;
        }
        for (int x = now[1] + 1; x < now[1] + 8; x++) {
            int c = after.getPixel(x, 85);
            rightGlow |= Color.blue(c) > Color.red(c) + 10;
        }
        assertTrue("Boundary/glow follows enlarged artwork on both sides", leftGlow && rightGlow);
        File file = new File("build/reports/preview-ui/focused-card-unit.png");
        file.getParentFile().mkdirs();
        try (FileOutputStream out = new FileOutputStream(file)) {
            after.compress(Bitmap.CompressFormat.PNG, 100, out);
        }
        before.recycle();
        after.recycle();
        host.finish();
    }

    private int[] redBounds(Bitmap bitmap, int y) {
        int first = bitmap.getWidth(), last = -1;
        for (int x = 0; x < bitmap.getWidth(); x++) {
            int c = bitmap.getPixel(x, y);
            if (Color.red(c) > 200 && Color.green(c) < 80
                && Color.blue(c) < 80) {
                first = Math.min(first, x);
                last = x;
            }
        }
        assertTrue("Rendered artwork exists", last > first);
        return new int[] {
            first, last
        };
    }
}
