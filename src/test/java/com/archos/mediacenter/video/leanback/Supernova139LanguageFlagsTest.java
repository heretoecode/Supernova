package com.archos.mediacenter.video.leanback;

import android.app.Application;
import android.graphics.*;
import java.util.*;
import java.io.*;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.*;
import org.robolectric.annotation.*;
import static org.junit.Assert.*;

@RunWith(RobolectricTestRunner.class)
@Config(application = Application.class, sdk = 28)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
public class Supernova139LanguageFlagsTest {
    @Test
    public void unknownTrackMetadataStillGetsVisibleNeutralInlineIcon() {
        Application c = RuntimeEnvironment.getApplication();
        for (String code : new String[] {null, "", "und", "ab", "tp", "unknown"}) {
            CharSequence value = PreviewLanguageIcon.inline(c, code, "Track label");
            assertTrue(value instanceof android.text.Spanned);
            android.text.style.ImageSpan[] icons =
                ((android.text.Spanned)value).getSpans(0, value.length(), android.text.style.ImageSpan.class);
            assertEquals(1, icons.length);
            assertTrue(icons[0].getDrawable().getBounds().width() > 0);
            assertNull(org.robolectric.util.ReflectionHelpers.getField(icons[0].getDrawable(), "flag"));
        }
    }

    @Test
    public void imageViewRendersRegionalAndNeutralIconsWithoutManualDrawableBounds() {
        Application c = RuntimeEnvironment.getApplication();
        for (String code : new String[] {"en-GB", "eng", "pt_BR", "zh-ca", "cy", null}) {
            android.widget.ImageView image = new android.widget.ImageView(c);
            image.setImageDrawable(new PreviewLanguageIcon(c, code));
            image.measure(android.view.View.MeasureSpec.makeMeasureSpec(80, 1073741824),
                          android.view.View.MeasureSpec.makeMeasureSpec(48, 1073741824));
            image.layout(0, 0, 80, 48);
            Bitmap pixels = Bitmap.createBitmap(80, 48, Bitmap.Config.ARGB_8888);
            image.draw(new Canvas(pixels));
            int painted = 0;
            for (int y = 0; y < 48; y++)
                for (int x = 0; x < 80; x++)
                    if (Color.alpha(pixels.getPixel(x, y)) > 128)
                        painted++;
            assertTrue("Visible ImageView language icon: " + code, painted > 20);
        }
    }

    /** Owner 2 Nov 2026: every flag the rules can choose must be bundled, or it silently falls back to a globe. */
    @Test
    public void everyMappedFlagKeyHasBundledArtwork() {
        Application context = RuntimeEnvironment.getApplication();
        java.util.Set<String> keys = new java.util.TreeSet<>(PreviewLanguageIcon.DEFAULT_COUNTRY.values());
        for (String code : new String[] {"sp", "ea", "ze", "ex", "zh-ca", "me", "pm", "pr", "ma"})
            keys.add(PreviewLanguageIcon.country(code));
        for (String key : keys) {
            int id = context.getResources().getIdentifier(PreviewLanguageIcon.resourceName(key), "drawable",
                                                          context.getPackageName());
            assertTrue("Missing bundled flag for " + key, id != 0);
            Bitmap flag = BitmapFactory.decodeResource(context.getResources(), id);
            assertNotNull(key, flag);
            assertEquals(72, flag.getWidth());
            assertEquals(72, flag.getHeight());
        }
    }

    @Test
    public void everyExplicitIsoRegionHasOriginalBundledArtworkWithoutDependingOnEmojiFonts() {
        Application context = RuntimeEnvironment.getApplication();
        for (String country : Locale.getISOCountries()) {
            int id = context.getResources().getIdentifier("region_" + country.toLowerCase(Locale.ROOT), "drawable",
                                                          context.getPackageName());
            assertTrue("Missing bundled region " + country, id != 0);
            Bitmap flag = BitmapFactory.decodeResource(context.getResources(), id);
            assertNotNull(country, flag);
            assertEquals(72, flag.getWidth());
            assertEquals(72, flag.getHeight());
            assertEquals(country, PreviewLanguageIcon.country("en-" + country));
        }
    }

    @Test
    public void regionlessLanguageIsNeutralAndRegionalFlagRetainsFullColourAfterMonochromeTint() throws Exception {
        Application context = RuntimeEnvironment.getApplication();
        Bitmap sheet = Bitmap.createBitmap(560, 160, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(sheet);
        canvas.drawColor(0xff14212c);
        String[] locales = {"en", "en-GB", "pt-BR", "zh-Hant-TW", "en-XX", "en-US", "fr-CA"};
        for (int i = 0; i < locales.length; i++) {
            PreviewLanguageIcon icon = new PreviewLanguageIcon(context, locales[i]);
            icon.setBounds(i * 80, 16, i * 80 + 72, 88);
            icon.setColorFilter(new PorterDuffColorFilter(Color.WHITE, PorterDuff.Mode.SRC_IN));
            icon.draw(canvas);
        }
        Set<Integer> regional = new HashSet<>();
        for (int y = 16; y < 88; y++)
            for (int x = 80; x < 152; x++)
                regional.add(sheet.getPixel(x, y));
        assertTrue(regional.stream().anyMatch(color -> Color.red(color) > 170 && Color.blue(color) < 150));
        assertTrue(regional.stream().anyMatch(color -> Color.blue(color) > Color.red(color) + 30));
        Bitmap neutral = Bitmap.createBitmap(72, 72, Bitmap.Config.ARGB_8888);
        PreviewLanguageIcon generic = new PreviewLanguageIcon(context, "und");
        generic.setBounds(0, 0, 72, 72);
        generic.draw(new Canvas(neutral));
        int painted = 0;
        for (int y = 0; y < 72; y++)
            for (int x = 0; x < 72; x++) {
                int color = neutral.getPixel(x, y);
                if (Color.alpha(color) >= 128) {
                    painted++;
                    float[] hsv = new float[3];
                    Color.colorToHSV(color, hsv);
                    assertTrue("Neutral canonical soft-white stroke", hsv[1] <= .12f);
                    assertTrue("Readable canonical stroke", hsv[2] >= .8f);
                }
            }
        assertTrue(painted > 0);
        File image = new File("build/reports/preview-ui/0139-regional-language-flags.png");
        image.getParentFile().mkdirs();
        try (FileOutputStream output = new FileOutputStream(image)) {
            sheet.compress(Bitmap.CompressFormat.PNG, 100, output);
        }
    }
}
