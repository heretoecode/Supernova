package com.archos.mediacenter.video.player;

import android.app.Activity;
import android.app.Application;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import androidx.preference.PreferenceManager;
import java.text.SimpleDateFormat;
import java.util.Formatter;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import org.junit.Test;
import org.json.JSONObject;
import static org.junit.Assert.*;

@org.junit.runner.RunWith(org.robolectric.RobolectricTestRunner.class)
@org.robolectric.annotation.Config(application = Application.class, sdk = 28)
public class Supernova138PlaybackTest {
    @Test
    public void realHudUpdateMethodsShowTooltipOnSeekAndRemainingFinishTime() throws Exception {
        var host = org.robolectric.Robolectric.buildActivity(Activity.class).setup().visible();
        Player old = Player.sPlayer;
        try {
            var activity = host.get();
            var prefs = PreferenceManager.getDefaultSharedPreferences(activity);
            prefs.edit().putBoolean("try_new_ui", true).putString("uimode", "2").commit();
            View root = LayoutInflater.from(activity).inflate(
                com.archos.mediacenter.video.R.layout.player_controller_experimental, null);
            activity.setContentView(root);
            View bar = root.findViewById(com.archos.mediacenter.video.R.id.control_bar);
            bar.setVisibility(View.VISIBLE);
            PlayerController controller =
                org.mockito.Mockito.mock(PlayerController.class, org.mockito.Mockito.CALLS_REAL_METHODS);
            Player player = org.mockito.Mockito.mock(Player.class);
            Player.sPlayer = player;
            org.mockito.Mockito.when(player.getDuration()).thenReturn(100000);
            org.mockito.Mockito.when(player.getCurrentPosition()).thenReturn(20000);
            org.mockito.Mockito.when(player.isPlaying()).thenReturn(true);
            Map<String, Object> fields = new HashMap<>();
            fields.put("mContext", activity);
            fields.put("mControllerViewLeft", root);
            fields.put("mControlBar", bar);
            fields.put("mPauseButton", root.findViewById(com.archos.mediacenter.video.R.id.pause));
            fields.put("mProgress", root.findViewById(com.archos.mediacenter.video.R.id.seek_progress));
            fields.put("mCurrentTime", root.findViewById(com.archos.mediacenter.video.R.id.time_current));
            fields.put("mEndTime", root.findViewById(com.archos.mediacenter.video.R.id.time));
            fields.put("mClock", root.findViewById(com.archos.mediacenter.video.R.id.clock));
            fields.put("mDateFormat", new SimpleDateFormat("HH:mm", Locale.ROOT));
            fields.put("mNextSeek", -1);
            StringBuilder formatter = new StringBuilder();
            fields.put("mFormatBuilder", formatter);
            fields.put("mFormatter", new Formatter(formatter, Locale.ROOT));
            for (var field : fields.entrySet())
                org.robolectric.util.ReflectionHelpers.setField(controller, field.getKey(), field.getValue());
            org.robolectric.util.ReflectionHelpers.callInstanceMethod(controller, "configurePreviewTransport");
            root.measure(View.MeasureSpec.makeMeasureSpec(960, View.MeasureSpec.EXACTLY),
                View.MeasureSpec.makeMeasureSpec(540, View.MeasureSpec.EXACTLY));
            root.layout(0, 0, 960, 540);
            TextView pill = root.findViewById(com.archos.mediacenter.video.R.id.preview_scrub_time);
            ((TextView) root.findViewById(com.archos.mediacenter.video.R.id.time_current))
                .setText("00:20");
            root.findViewById(com.archos.mediacenter.video.R.id.pause).requestFocus();
            org.robolectric.util.ReflectionHelpers.callInstanceMethod(controller, "updatePreviewScrubber");
            assertEquals(View.VISIBLE, pill.getVisibility());
            root.findViewById(com.archos.mediacenter.video.R.id.seek_progress).requestFocus();
            org.robolectric.util.ReflectionHelpers.callInstanceMethod(controller, "updatePreviewScrubber");
            assertEquals(View.VISIBLE, pill.getVisibility());
            assertEquals("00:00:20", pill.getText().toString());
            assertFalse(
                root.findViewById(com.archos.mediacenter.video.R.id.seek_progress).getDefaultFocusHighlightEnabled());
            assertNull(bar.getForeground());
            controller.updateClock();
            assertEquals("00:01:40",
                ((TextView) root.findViewById(com.archos.mediacenter.video.R.id.time))
                    .getText()
                    .toString());
            assertTrue(((TextView) root.findViewById(com.archos.mediacenter.video.R.id.preview_end_time))
                    .getText()
                    .toString()
                    .startsWith("Ends at "));
            assertFalse(((TextView) root.findViewById(com.archos.mediacenter.video.R.id.clock))
                    .getText()
                    .toString()
                    .contains("Ends"));
            org.robolectric.util.ReflectionHelpers.callInstanceMethod(controller, "updatePausePlay");
            assertEquals("Pause",
                ((TextView) root.findViewById(com.archos.mediacenter.video.R.id.preview_pause_label))
                    .getText()
                    .toString());
            org.mockito.Mockito.when(player.isPlaying()).thenReturn(false);
            org.robolectric.util.ReflectionHelpers.callInstanceMethod(controller, "updatePausePlay");
            assertEquals("Play",
                ((TextView) root.findViewById(com.archos.mediacenter.video.R.id.preview_pause_label))
                    .getText()
                    .toString());
        } finally {
            Player.sPlayer = old;
            host.pause().stop().destroy();
        }
    }

    @Test
    public void predictionUsesPreviewAndSpeedAndPausedResumeNowSemantics() {
        assertEquals(60000, PreviewHudTiming.end(10000, 100000, 50000, 1));
        assertEquals(35000, PreviewHudTiming.end(10000, 100000, 50000, 2));
        assertEquals(20000, PreviewHudTiming.end(20000, 100000, 120000, 1));
        assertEquals(110000, PreviewHudTiming.end(10000, 100000, -1000, Float.NaN));
        assertEquals(-1, PreviewHudTiming.end(10000, 0, 0, 1));
    }

    @Test
    public void compactPillNeverClipsAtEitherBoundary() {
        assertEquals(0, PreviewHudTiming.pillLeft(-25, 400, 50), 0);
        assertEquals(350, PreviewHudTiming.pillLeft(390, 400, 50), 0);
        assertEquals(0, PreviewHudTiming.pillLeft(0, 40, 50), 0);
    }

    @Test
    public void providerTaglineFallbackSkipsSynopsisAndEpisodeNames() throws Exception {
        JSONObject movie = new JSONObject().put("tagline", "Movie tagline"),
                   episode = new JSONObject().put("tagline", "Episode tagline"),
                   season = new JSONObject().put("tagline", "Season tagline"),
                   show = new JSONObject().put("tagline", "Show tagline");
        assertEquals("Movie tagline", PreviewTaglines.choose(movie, null, null, null));
        assertEquals("Episode tagline", PreviewTaglines.choose(null, episode, season, show));
        assertEquals(
            "Season tagline", PreviewTaglines.choose(null, new JSONObject().put("overview", "Synopsis"), season, show));
        assertEquals("Show tagline", PreviewTaglines.choose(null, null, new JSONObject(), show));
        assertEquals("",
            PreviewTaglines.choose(
                new JSONObject().put("overview", "Synopsis").put("name", "Episode 1"), null, null, null));
    }
}
