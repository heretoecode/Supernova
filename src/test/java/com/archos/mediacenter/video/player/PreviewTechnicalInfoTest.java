package com.archos.mediacenter.video.player;

import android.app.Activity;
import android.app.Application;
import android.app.Dialog;
import android.net.Uri;
import android.os.Looper;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.archos.mediacenter.video.leanback.PreviewDialog;
import com.archos.mediacenter.video.utils.VideoMetadata;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.*;
import org.robolectric.android.controller.ActivityController;
import org.robolectric.annotation.Config;
import static org.junit.Assert.*;
import org.robolectric.shadows.ShadowDialog;

@RunWith(RobolectricTestRunner.class)
@Config(application = Application.class, sdk = 28)
public class PreviewTechnicalInfoTest {
    @Test
    @Config(qualifiers = "w960dp-h540dp-land-mdpi")
    public void readPanelsFitShortContentAndCapLongContent() {
        ActivityController<Activity> host =
            Robolectric.buildActivity(Activity.class).setup();
        try {
            Dialog small =
                PreviewDialog.read(host.get(), "Information", "One short line");
            int shortHeight = small.getWindow().getAttributes().height;
            assertTrue(shortHeight < 200);
            small.dismiss();
            StringBuilder lines = new StringBuilder();
            for (int i = 0; i < 100; i++)
                lines.append("A technical line\n");
            Dialog large =
                PreviewDialog.read(host.get(), "Information", lines.toString());
            assertTrue(large.getWindow().getAttributes().height <= 360);
            assertTrue(large.getWindow().getAttributes().height > shortHeight);
            large.dismiss();
        } finally {
            host.pause().stop().destroy();
        }
    }

    @Test
    public void missingActiveMetadataDoesNotStartProbe() {
        assertTrue(PreviewTechnicalInfo.describe(null, "webdavs", 0).contains("not available"));
    }

    @Test
    public void emptySnapshotHasNoNullTrackFailure() {
        String text = PreviewTechnicalInfo.describe(new VideoMetadata(), "webdavs", 0);
        assertTrue(text.contains("Source: webdavs"));
        assertFalse(text.contains("null"));
    }

    @Test
    public void dismissRevealsHiddenHudBeforeRestoringExactInfoOpener() {
        ActivityController<Activity> host =
            Robolectric.buildActivity(Activity.class).setup().visible();
        try {
            Activity activity = host.get();
            LinearLayout hud = new LinearLayout(activity);
            Button pause = new Button(activity),
                                  info = new Button(activity);
            pause.setFocusableInTouchMode(true);
            info.setFocusableInTouchMode(true);
            hud.addView(pause);
            hud.addView(info);
            activity.setContentView(hud);
            Shadows.shadowOf(Looper.getMainLooper()).idle();
            info.requestFocus();
            assertSame(info, hud.findFocus());
            // ShadowActivity uses an explicit field for getCurrentFocus rather
            // than delegating to the window. Seed only the initial opener;
            // verify the real view-tree focus after dismissal below.
            Shadows.shadowOf(activity).setCurrentFocus(info);
            assertSame(info, activity.getCurrentFocus());
            PreviewTechnicalInfo.show(activity, null, null, 0, () -> {
                hud.setVisibility(View.VISIBLE);
                pause.requestFocus();
            });
            Dialog technical = ShadowDialog.getLatestDialog();
            hud.setVisibility(View.GONE); // The player's inactivity fade while Info is open.
            technical.dismiss();
            Shadows.shadowOf(Looper.getMainLooper()).idle();
            assertEquals(View.VISIBLE, hud.getVisibility());
            assertSame(info, hud.findFocus());
            assertFalse(pause.hasFocus());
        } finally {
            host.pause().stop().destroy();
        }
    }

    @Test
    public void episodeInformationToTechnicalPanelKeepsOwningActivity() {
        ActivityController<Activity> host =
            Robolectric.buildActivity(Activity.class).setup();
        try {
            Activity activity = host.get();
            activity.setContentView(new TextView(activity));
            PreviewTechnicalInfo.show(
                activity, new VideoMetadata(), Uri.parse("webdavs://example/file.mkv"), 0);
            Dialog technical = ShadowDialog.getLatestDialog();
            assertTrue(technical.isShowing());
            for (String label : new String[] {
                "Video", "Audio", "File", "Source"
            })
                assertNotNull(find(technical.getWindow().getDecorView(), label));
            for (String label : new String[] {
                "File & Technical Details", "file.mkv", "Resume", "Close"
            })
                assertNull(find(technical.getWindow().getDecorView(), label));
            assertNull(Shadows.shadowOf(activity).getNextStartedActivity());
            assertFalse(activity.isFinishing());
            technical.dismiss();
        } finally {
            host.pause().stop().destroy();
        }
    }

    private View find(View root, String text) {
        if (root instanceof TextView && text.contentEquals(((TextView) root).getText()))
            return root;
        if (root instanceof ViewGroup)
            for (int i = 0; i < ((ViewGroup) root).getChildCount(); i++) {
                View found = find(((ViewGroup) root).getChildAt(i), text);
                if (found != null)
                    return found;
            }
        return null;
    }
}
