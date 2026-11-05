package com.archos.mediacenter.video.foundation;

import android.app.Activity;
import android.app.Application;
import android.os.Looper;
import android.view.KeyEvent;
import android.view.View;
import android.widget.LinearLayout;
import androidx.recyclerview.widget.RecyclerView;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;
import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.*;
import org.junit.runner.RunWith;
import org.robolectric.*;
import org.robolectric.annotation.Config;
import java.util.*;
import static org.junit.Assert.*;
import org.robolectric.util.ReflectionHelpers;

/** Checks lossless bundled licence reading, bounded text layout and safe Unicode page boundaries. */
@RunWith(RobolectricTestRunner.class)
@Config(application = Application.class, sdk = 30, qualifiers = "w960dp-h540dp-land-mdpi")
public class BundledNoticePagerTest {
    @Test
    public void completeActualNoticesAreLosslessAndEveryTextLayoutIsBounded() throws Exception {
        JSONArray catalogue;
        try (InputStream input =
                 RuntimeEnvironment.getApplication().getAssets().open("foundation/licences.json")) {
            catalogue =
                new JSONObject(new String(input.readAllBytes(), StandardCharsets.UTF_8))
                    .getJSONArray("components");
        }
        assertTrue(catalogue.length() >= 60);
        for (int index = 0; index < catalogue.length(); index++) {
            String asset = catalogue.getJSONObject(index).getString("text_asset");
            String notice;
            try (InputStream in = RuntimeEnvironment.getApplication().getAssets().open(asset)) {
                notice = new String(in.readAllBytes(), StandardCharsets.UTF_8);
            }
            List<String> pages = BundledNoticeReader.blocks(notice);
            assertEquals(notice, String.join("", pages));
            assertFalse(pages.isEmpty());
            for (String page : pages)
                assertTrue(page.length() <= BundledNoticeReader.BLOCK_CHARS);
        }
    }

    @Test
    @Config(sdk = 28)
    public void retainedLicenceEntryLoadsFullOfflineDisclosureOnShieldEraApi() throws Exception {
        var host = Robolectric.buildActivity(Activity.class).setup().visible();
        try {
            LicenseDisclosure disclosure = new LicenseDisclosure(host.get());
            host.get().setContentView(disclosure);
            ExecutorService io = ReflectionHelpers.getField(disclosure, "io");
            io.submit(() -> {
            }).get(5, TimeUnit.SECONDS);
            Shadows.shadowOf(Looper.getMainLooper()).idle();
            io.submit(() -> {
            }).get(5, TimeUnit.SECONDS);
            Shadows.shadowOf(Looper.getMainLooper()).idle();
            assertNotNull(disclosure.findViewWithTag("licence.continuous.reader"));
            LinearLayout catalogue =
                ReflectionHelpers.getField(disclosure, "catalogue");
            assertTrue(catalogue.getChildCount() >= 60);
        } finally {
            host.pause().stop().destroy();
        }
    }

    @Test
    public void pageBoundariesCannotSplitUnicodeSurrogatePairs() {
        String body = "a".repeat(3999) + "😀"
            + "z".repeat(4001);
        List<String> pages = BundledNoticeReader.blocks(body);
        assertEquals(body, String.join("", pages));
        for (String page : pages)
            assertFalse(Character.isHighSurrogate(page.charAt(page.length() - 1)));
    }

    @Test
    public void continuousReaderHasNoPaginationAndReturnsToCatalogue() {
        var host = Robolectric.buildActivity(Activity.class).setup().visible();
        try {
            int[] returned = {
                0
            };
            BundledNoticeReader reader =
                new BundledNoticeReader(host.get(), "Notice\n".repeat(3000), () -> returned[0]++);
            host.get().setContentView(reader);
            reader.measure(View.MeasureSpec.makeMeasureSpec(400, View.MeasureSpec.EXACTLY),
                View.MeasureSpec.makeMeasureSpec(440, View.MeasureSpec.EXACTLY));
            reader.layout(0, 0, 400, 440);
            assertEquals(2, reader.getChildCount());
            assertTrue(reader.getChildAt(1) instanceof RecyclerView);
            var list = (RecyclerView) reader.getChildAt(1);
            assertTrue(list.getAdapter().getItemCount() > 1);
            reader.focusReader();
            assertTrue(list.hasFocus());
            list.dispatchKeyEvent(
                new KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_DPAD_LEFT));
            assertEquals(1, returned[0]);
        } finally {
            host.pause().stop().destroy();
        }
    }
}
