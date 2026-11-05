package com.archos.mediacenter.video.leanback;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import android.app.Application;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.view.Gravity;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import java.lang.reflect.Method;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;
import org.robolectric.annotation.GraphicsMode;

/** 0.155 notes #1 and #2: the hero's text patch and the bottom-aligned title logo. */
@RunWith(RobolectricTestRunner.class)
@Config(application = Application.class, sdk = 28, qualifiers = "w960dp-h540dp-land-mdpi")
@GraphicsMode(GraphicsMode.Mode.NATIVE)
public class HeroReadabilityTest {
    @Test
    public void textPatchSitsUnderTheTextAndDarkensOnlyAroundIt() {
        Application c = RuntimeEnvironment.getApplication();
        CinematicCard card = new CinematicCard(c);
        View art = new View(c);
        art.setBackgroundColor(0xffffe060);
        card.addView(art, new FrameLayout.LayoutParams(-1, -1));
        LinearLayout copy = new LinearLayout(c);
        copy.setOrientation(LinearLayout.VERTICAL);
        copy.setPadding(24, 120, 0, 0);
        card.addView(copy, new FrameLayout.LayoutParams(400, -1));
        TextView metadata = new TextView(c);
        metadata.setText("2025 · Documentary");
        copy.addView(metadata, new LinearLayout.LayoutParams(300, 24));
        TextView synopsis = new TextView(c);
        synopsis.setText("Filmed across five years in a single spectacular location.");
        copy.addView(synopsis, new LinearLayout.LayoutParams(300, 60));

        View patch = card.addTextPatch(metadata, synopsis);
        assertTrue("patch is drawn under the text", card.indexOfChild(patch) < card.indexOfChild(copy));
        assertTrue("patch is drawn over the artwork", card.indexOfChild(patch) > card.indexOfChild(art));

        card.measure(View.MeasureSpec.makeMeasureSpec(900, View.MeasureSpec.EXACTLY),
            View.MeasureSpec.makeMeasureSpec(500, View.MeasureSpec.EXACTLY));
        card.layout(0, 0, 900, 500);
        CinematicCard.TextPatch drawn = (CinematicCard.TextPatch) patch;
        RectF text = drawn.textBounds();
        RectF ellipse = drawn.patchBounds();
        assertNotNull(text);
        assertEquals(120, text.top, 0.5f);
        assertTrue("ellipse grows clear of the text", ellipse.contains(text));

        Bitmap picture = Bitmap.createBitmap(900, 500, Bitmap.Config.ARGB_8888);
        picture.eraseColor(0xffffe060);
        patch.draw(new Canvas(picture));
        int centre = picture.getPixel(Math.round(text.centerX()), Math.round(text.centerY()));
        assertTrue("darker behind the text", Color.red(centre) < 200);
        assertEquals("far corner untouched", 0xffffe060, picture.getPixel(880, 480));
        picture.recycle();
    }

    @Test
    public void textPatchDrawsNothingWhenTheTextIsHidden() {
        Application c = RuntimeEnvironment.getApplication();
        CinematicCard card = new CinematicCard(c);
        TextView synopsis = new TextView(c);
        synopsis.setVisibility(View.GONE);
        card.addView(synopsis);
        CinematicCard.TextPatch patch = (CinematicCard.TextPatch) card.addTextPatch(synopsis);
        card.measure(View.MeasureSpec.makeMeasureSpec(900, View.MeasureSpec.EXACTLY),
            View.MeasureSpec.makeMeasureSpec(500, View.MeasureSpec.EXACTLY));
        card.layout(0, 0, 900, 500);
        assertEquals(null, patch.patchBounds());
    }

    @Test
    public void wideLogoSitsAtTheBottomOfABottomAlignedTitle() throws Exception {
        TextView title = new TextView(RuntimeEnvironment.getApplication());
        title.setGravity(Gravity.BOTTOM | Gravity.START);
        assertEquals("logo bottom", 86, logoTop(title) + 20);

        TextView centred = new TextView(RuntimeEnvironment.getApplication());
        centred.setGravity(Gravity.CENTER);
        assertEquals("other title views keep the logo at the top", 0, logoTop(centred));
    }

    /** Draws a 400×40 white logo into a 200×86 box and returns the first row that has logo pixels. */
    private static int logoTop(TextView view) throws Exception {
        Bitmap logo = Bitmap.createBitmap(400, 40, Bitmap.Config.ARGB_8888);
        logo.eraseColor(Color.WHITE);
        Method logoFor = OfficialTitleArtwork.class.getDeclaredMethod("logoFor", TextView.class, Bitmap.class,
            boolean.class);
        logoFor.setAccessible(true);
        Drawable drawable = (Drawable) logoFor.invoke(null, view, logo, false);
        drawable.setBounds(0, 0, 200, 86);
        Bitmap box = Bitmap.createBitmap(200, 86, Bitmap.Config.ARGB_8888);
        drawable.draw(new Canvas(box));
        int top = -1;
        for (int y = 0; y < 86 && top < 0; y++) {
            if (Color.alpha(box.getPixel(100, y)) > 200) {
                top = y;
            }
        }
        box.recycle();
        logo.recycle();
        return top;
    }
}
