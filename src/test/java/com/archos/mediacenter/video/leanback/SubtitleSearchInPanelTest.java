package com.archos.mediacenter.video.leanback;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;

import android.app.Activity;
import android.app.Application;
import android.app.Dialog;
import android.os.Looper;
import android.view.View;
import android.widget.TextView;
import java.util.concurrent.atomic.AtomicInteger;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.Shadows;
import org.robolectric.annotation.Config;

/** 0.155 notes #5: "Searching OpenSubtitles…" happens inside the side panel and Back cancels it. */
@RunWith(RobolectricTestRunner.class)
@Config(application = Application.class, sdk = 28, qualifiers = "w960dp-h540dp-land-mdpi")
public class SubtitleSearchInPanelTest {
    @Test
    public void searchingStateIsInTheSidePanelAndBackCancels() {
        Activity host = Robolectric.buildActivity(Activity.class).setup().get();
        AtomicInteger cancelled = new AtomicInteger();
        Dialog dialog = SubtitleResultsPanel.working(host, "Searching OpenSubtitles…", "Dark Matter S02E10.mkv",
            "Back cancels the search", cancelled::incrementAndGet);
        View root = dialog.getWindow().getDecorView();
        assertNotNull(root.findViewWithTag("subtitles.download.working"));
        TextView message = root.findViewWithTag("subtitles.download.message");
        assertEquals("Searching OpenSubtitles…", message.getText().toString());
        TextView footer = root.findViewWithTag("subtitles.download.footer");
        assertEquals("Back cancels the search", footer.getText().toString());
        assertEquals("subtitles.download.status", dialog.getCurrentFocus().getTag());

        dialog.onBackPressed();
        Shadows.shadowOf(Looper.getMainLooper()).idle();
        assertEquals(1, cancelled.get());
        assertFalse(dialog.isShowing());
    }

    @Test
    public void dismissingForTheResultsDoesNotCancel() {
        Activity host = Robolectric.buildActivity(Activity.class).setup().get();
        AtomicInteger cancelled = new AtomicInteger();
        Dialog dialog = SubtitleResultsPanel.working(host, "Searching OpenSubtitles…", "",
            "Back cancels the search", cancelled::incrementAndGet);
        dialog.dismiss();
        Shadows.shadowOf(Looper.getMainLooper()).idle();
        assertEquals(0, cancelled.get());
    }
}
