package com.archos.mediacenter.video.player;

import android.app.Activity;
import android.app.Application;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Looper;
import android.view.View;
import com.archos.mediacenter.utils.videodb.VideoDbInfo;
import com.archos.mediacenter.video.leanback.PreviewPagesTest;
import java.io.IOException;
import java.time.Duration;
import org.json.JSONObject;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.*;
import org.robolectric.annotation.Config;
import org.robolectric.annotation.GraphicsMode;
import org.robolectric.util.ReflectionHelpers;
import static org.junit.Assert.*;

/** Checks age overlays require established playback and valid regional ratings without capturing focus. */
@RunWith(RobolectricTestRunner.class)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
@Config(application = Application.class, sdk = 28)
public class PlaybackAgeOverlayTest {
    @Test
    public void missingAndUnratedClassificationsDoNotRender() {
        for (String value : new String[] {
            "", " ", "N/A", "NR", "Unrated", "Unknown", "Not Yet Rated", "TBC",
                 "No rating available", "null", "<script>"
        })
            assertFalse(value, PlaybackAgeOverlay.valid(value));
        for (String value : new String[] {
            "12A", "15", "18", "TV-MA", "PG-13"
        })
            assertTrue(value, PlaybackAgeOverlay.valid(value));
    }

    @Test
    public void changingTitleRequiresANewPlaybackEstablishedSignal() {
        PlaybackAgeOverlay view = new PlaybackAgeOverlay(RuntimeEnvironment.getApplication(), null);
        view.playbackStarted();
        VideoDbInfo info = new VideoDbInfo();
        info.id = 2;
        view.bind(info);
        assertFalse((Boolean) ReflectionHelpers.getField(view, "output"));
    }

    @Test
    public void establishedOutputCannotBeBorrowedFromAnotherLaunchOrMetadataInstance() {
        PlayerService service = Robolectric.buildService(PlayerService.class).get();
        var info = new VideoDbInfo();
        info.uri = Uri.parse("file:///fixture/current");
        Object session = ReflectionHelpers.getField(service, "mPlaybackSession");
        ReflectionHelpers.setField(service, "mVideoInfo", info);
        ReflectionHelpers.setField(session, "uri", info.uri);
        assertFalse(service.hasEstablishedOutputFor(info));
        ReflectionHelpers.setField(session, "outputEstablished", true);
        assertTrue(service.hasEstablishedOutputFor(info));
        assertFalse(service.hasEstablishedOutputFor(new VideoDbInfo()));
        ReflectionHelpers.setField(session, "uri", Uri.parse("file:///fixture/previous"));
        assertFalse(service.hasEstablishedOutputFor(info));
        ReflectionHelpers.setField(session, "uri", info.uri);
        ReflectionHelpers.setField(session, "failed", true);
        assertFalse(service.hasEstablishedOutputFor(info));
    }

    @Test
    public void outputIsRequiredAndOverlayNeverCapturesFocus() {
        var host = Robolectric.buildActivity(Activity.class).setup().visible();
        try {
            PlaybackAgeOverlay view = new PlaybackAgeOverlay(host.get(), null);
            host.get().setContentView(view);
            ReflectionHelpers.setField(view, "classification", "15");
            assertEquals(View.GONE, view.getVisibility());
            assertFalse(view.isFocusable());
            view.playbackStarted();
            assertEquals(View.VISIBLE, view.getVisibility());
            assertEquals(10000, PlaybackAgeOverlay.DWELL_MS);
            view.stop();
            assertEquals(View.GONE, view.getVisibility());
            view.playbackStarted();
            assertEquals(View.GONE, view.getVisibility());
        } finally {
            host.pause().stop().destroy();
        }
    }

    @Test
    public void lateMetadataNeverStartsANewTenSecondWindow() {
        var host = Robolectric.buildActivity(Activity.class).setup().visible();
        try {
            PlaybackAgeOverlay view = new PlaybackAgeOverlay(host.get(), null);
            host.get().setContentView(view);
            view.playbackStarted();
            Shadows.shadowOf(Looper.getMainLooper()).idleFor(Duration.ofSeconds(11));
            ReflectionHelpers.setField(view, "classification", "PG-13");
            view.playbackStarted();
            assertEquals(View.GONE, view.getVisibility());
        } finally {
            host.pause().stop().destroy();
        }
    }

    @Test
    public void territorySelectionNeverFallsBackToAnotherRegion() throws Exception {
        var data = new JSONObject(
            "{\"results\":[{\"iso_3166_1\":\"US\",\"rating\":\"TV-MA\"},{\"iso_3166_1\":\"GB\",\"rating\":\"15\"}]}");
        assertEquals("15", PlaybackAgeOverlay.certificate(data, "GB"));
        assertEquals("TV-MA", PlaybackAgeOverlay.certificate(data, "US"));
        assertEquals("", PlaybackAgeOverlay.certificate(data, "FR"));
    }

    @Test
    public void rapidUnindexedFileSwitchResetsOutputEvenWhenDatabaseIdsMatch() {
        PlaybackAgeOverlay view = new PlaybackAgeOverlay(RuntimeEnvironment.getApplication(), null);
        var first = new VideoDbInfo();
        first.uri = Uri.parse("file:///one.mkv");
        view.bind(first);
        view.playbackStarted();
        var second = new VideoDbInfo();
        second.uri = Uri.parse("file:///two.mkv");
        view.bind(second);
        assertFalse((Boolean) ReflectionHelpers.getField(view, "output"));
    }

    @Test
    public void conflictingRegionalRatingsAreHiddenAndTheatricalRatingHasPriority() throws Exception {
        var conflict = new JSONObject(
            "{\"results\":[{\"iso_3166_1\":\"GB\",\"rating\":\"15\"},{\"iso_3166_1\":\"GB\",\"rating\":\"18\"}]}");
        assertEquals("", PlaybackAgeOverlay.certificate(conflict, "GB"));
        var movie = new JSONObject(
            "{\"results\":[{\"iso_3166_1\":\"GB\",\"release_dates\":[{\"type\":1,\"certification\":\"18\"},{\"type\":3,\"certification\":\"15\"}]}]}");
        assertEquals("15", PlaybackAgeOverlay.certificate(movie, "GB"));
        assertEquals("", PlaybackAgeOverlay.certificate(movie, "US"));
    }

    @Test
    public void tenSecondPlaybackDeadlineIsNotExtendedByRepeatedStartSignals() {
        var host = Robolectric.buildActivity(Activity.class).setup().visible();
        try {
            PlaybackAgeOverlay view = new PlaybackAgeOverlay(host.get(), null);
            host.get().setContentView(view);
            ReflectionHelpers.setField(view, "classification", "15");
            view.playbackStarted();
            Shadows.shadowOf(Looper.getMainLooper()).idleFor(Duration.ofMillis(9999));
            assertEquals(View.VISIBLE, view.getVisibility());
            view.playbackStarted();
            Shadows.shadowOf(Looper.getMainLooper()).idleFor(Duration.ofMillis(1000));
            assertEquals(View.GONE, view.getVisibility());
        } finally {
            host.pause().stop().destroy();
        }
    }

    private void renderRating(String suffix) throws Exception {
        var host = Robolectric.buildActivity(Activity.class).setup().visible();
        try {
            PlaybackAgeOverlay view = new PlaybackAgeOverlay(host.get(), null);
            host.get().setContentView(view);
            ReflectionHelpers.setField(view, "classification", "TV-MA");
            view.playbackStarted();
            var config = view.getResources().getConfiguration();
            float density = view.getResources().getDisplayMetrics().density;
            int w = Math.round(config.screenWidthDp * density), h = Math.round(config.screenHeightDp * density);
            view.measure(View.MeasureSpec.makeMeasureSpec(w, View.MeasureSpec.EXACTLY),
                View.MeasureSpec.makeMeasureSpec(h, View.MeasureSpec.EXACTLY));
            view.layout(0, 0, w, h);
            assertNull(view.getBackground());
            assertFalse(view.isFocusable());
            assertFalse(view.isClickable());
            PreviewPagesTest.capture(view, "0147-age-rating-" + suffix);
            Bitmap bitmap = BitmapFactory.decodeFile(
                "build/reports/preview-ui/0147-age-rating-" + suffix + ".png");
            int red = 0;
            for (int y = 0; y < h / 3; y++)
                for (int x = 0; x < w / 3; x++) {
                    int c = bitmap.getPixel(x, y);
                    if (Color.red(c) > 180 && Color.green(c) < 80
                        && Color.blue(c) < 100)
                        red++;
                }
            assertTrue("Slim red accent is drawn", red > 0);
        } finally {
            host.pause().stop().destroy();
        }
    }

    @Test
    @Config(qualifiers = "w640dp-h360dp-land-xhdpi")
    public void certificationRenderAt720p() throws Exception {
        renderRating("720p");
    }

    @Test
    @Config(qualifiers = "w960dp-h540dp-land-xhdpi")
    public void certificationRenderAt1080p() throws Exception {
        renderRating("1080p");
    }

    @Test
    @Config(qualifiers = "w960dp-h540dp-land-xxxhdpi")
    public void certificationRenderAt4k() throws Exception {
        renderRating("4k");
    }

    @Test
    public void coldDirectPlaybackLoadsClassificationButCachedMissingRegionStaysHidden() throws Exception {
        JSONObject data = new JSONObject("{results:[{iso_3166_1:'GB',rating:'15'}]}");
        int[] requests = {
            0
        };
        assertEquals("15", PlaybackAgeOverlay.loadCertificate(null, "GB", () -> {
            requests[0]++;
            return data;
        }));
        assertEquals(1, requests[0]);
        JSONObject cached = new JSONObject().put("data", data);
        assertEquals("", PlaybackAgeOverlay.loadCertificate(cached, "IE", () -> {
            fail("A complete cached response must not trigger a regional guess or retry");
            return data;
        }));
        assertEquals(1, requests[0]);
        assertEquals(
            "", PlaybackAgeOverlay.loadCertificate(null, "GB", () -> {
                throw new IOException("Offline");
            }));
    }
}
