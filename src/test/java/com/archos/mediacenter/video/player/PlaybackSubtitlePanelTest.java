package com.archos.mediacenter.video.player;

import android.app.*;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.os.Looper;
import android.view.*;
import android.widget.*;
import com.archos.mediacenter.video.leanback.*;
import com.archos.mediacenter.video.utils.OpenSubtitlesSearchResult;
import java.io.File;
import java.io.FileOutputStream;
import java.util.*;
import org.junit.*;
import org.junit.runner.RunWith;
import org.robolectric.*;
import org.robolectric.android.controller.ActivityController;
import org.robolectric.annotation.*;
import static org.junit.Assert.*;
import org.robolectric.shadows.ShadowDialog;

/**
 * The approved Playback Settings side panel (docs/design/PLAYBACK_SETTINGS_SIDE_PANEL_APPROVED_2026-11-02.md).
 * 1920×1080 screen; captures are compared by eye with docs/design/approved-mockups/playback-settings/.
 */
@RunWith(RobolectricTestRunner.class)
@Config(application = Application.class, sdk = 28, qualifiers = "w960dp-h540dp-land-xhdpi")
@GraphicsMode(GraphicsMode.Mode.NATIVE)
public class PlaybackSubtitlePanelTest {
    private ActivityController<PreviewPlaybackMenusTest.TopNavigationTestHost> host;
    private Dialog main;
    private PlaybackSubtitlePanel panel;
    private Model model;

    static final class Model implements PlaybackSubtitlePanel.Backend {
        final Map<String, Integer> values =
            new HashMap<>(Map.of("enabled", 1, "size", 50, "colour", 0xffffc93c, "position", 0, "outline", 1,
                                 "background", 0, "opacity", 255, "delay", 0));
        final Map<String, Boolean> options =
            new HashMap<>(Map.of("boost", false, "night", false, "spatial", true, "skip", true));
        final Map<String, List<String>> modes =
            new HashMap<>(Map.of("format", List.of("Original", "Fullscreen", "Stretched"), "s3d",
                                 List.of("Off", "Side by side", "Top and bottom"), "playmode",
                                 List.of("Single", "Folder", "Repeat single", "Repeat folder", "Binge watching")));
        final Map<String, Integer> modeSelected = new HashMap<>(Map.of("format", 0, "s3d", 0, "playmode", 4));
        int selected = 1, audio = 0, downloads, trackCount = 15, disabledTrack = -1, audioDelay = 0, pip;
        int subtitleWrites, optionWrites;
        float speed = 1f;

        @Override
        public boolean available() {
            return true;
        }

        @Override
        public int get(String key) {
            return values.get(key);
        }

        @Override
        public void set(String key, int value) {
            subtitleWrites++;
            values.put(key, value);
        }

        @Override
        public void download() {
            downloads++;
        }

        @Override
        public List<PlaybackSubtitlePanel.Track> tracks() {
            List<PlaybackSubtitlePanel.Track> out = new ArrayList<>();
            for (int i = 0; i < trackCount; i++) {
                final int n = i;
                out.add(new PlaybackSubtitlePanel.Track(i == 0   ? "English (SDH)"
                                                        : i == 1 ? "English"
                                                                 : "Track " + i,
                                                        i == 0   ? "en-GB"
                                                        : i == 1 ? "en"
                                                                 : "fr",
                                                        selected == i, i != disabledTrack, () -> selected = n));
            }
            return out;
        }

        @Override
        public List<PlaybackSubtitlePanel.Track> audioTracks() {
            List<PlaybackSubtitlePanel.Track> out = new ArrayList<>();
            String[] names = {"English · 5.1", "French · Stereo", "German · Stereo"};
            String[] codes = {"eng", "fre", "ger"};
            for (int i = 0; i < 3; i++) {
                final int n = i;
                out.add(new PlaybackSubtitlePanel.Track(names[i], codes[i], audio == i, true, () -> audio = n));
            }
            return out;
        }

        @Override
        public Integer audioDelay() {
            return audioDelay;
        }

        @Override
        public void stepAudioDelay(int direction) {
            audioDelay += direction * 50;
        }

        @Override
        public Boolean option(String key) {
            return options.get(key);
        }

        @Override
        public void toggleOption(String key) {
            optionWrites++;
            options.put(key, !options.get(key));
        }

        @Override
        public List<PlaybackSubtitlePanel.Choice> choices(String key) {
            List<PlaybackSubtitlePanel.Choice> out = new ArrayList<>();
            List<String> labels = modes.get(key);
            if (labels == null)
                return out;
            for (int i = 0; i < labels.size(); i++) {
                final int n = i;
                out.add(new PlaybackSubtitlePanel.Choice(labels.get(i), modeSelected.get(key) == i, true,
                                                         () -> modeSelected.put(key, n)));
            }
            return out;
        }

        @Override
        public Float speed() {
            return speed;
        }

        @Override
        public void setSpeed(float value) {
            speed = Math.round(value * 100f) / 100f;
        }

        @Override
        public Runnable pictureInPicture() {
            return () -> pip++;
        }
    }

    @Before
    public void setup() {
        host = Robolectric.buildActivity(PreviewPlaybackMenusTest.TopNavigationTestHost.class).setup().visible();
        model = new Model();
        open(PlaybackSubtitlePanel.TAB_SUBTITLES);
    }

    private void open(int tab) {
        if (panel != null) {
            panel.close();
            main.dismiss();
        }
        panel = new PlaybackSubtitlePanel(host.get(), model, tab);
        main = new Dialog(host.get());
        main.requestWindowFeature(Window.FEATURE_NO_TITLE);
        main.setContentView(panel);
        main.show();
        main.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        panel.place(main.getWindow());
        layout(main);
        panel.focusTab();
        layout(main);
    }

    @After
    public void close() {
        panel.close();
        main.dismiss();
        host.pause().stop().destroy();
    }

    static void layout(Dialog dialog) {
        View decor = dialog.getWindow().getDecorView();
        WindowManager.LayoutParams a = dialog.getWindow().getAttributes();
        int screen = dialog.getContext().getResources().getDisplayMetrics().heightPixels;
        for (int n = 0; n < 3; n++) {
            int h = a.height > 0 ? View.MeasureSpec.makeMeasureSpec(a.height, View.MeasureSpec.EXACTLY)
                                 : View.MeasureSpec.makeMeasureSpec(screen, View.MeasureSpec.AT_MOST);
            decor.measure(View.MeasureSpec.makeMeasureSpec(a.width, View.MeasureSpec.EXACTLY), h);
            decor.layout(0, 0, decor.getMeasuredWidth(), decor.getMeasuredHeight());
            Shadows.shadowOf(Looper.getMainLooper()).idle();
        }
    }

    private View row(String key) {
        return panel.findViewWithTag("subtitles.row." + key);
    }

    private View tagged(String tag) {
        return panel.findViewWithTag(tag);
    }

    private void key(View row, int code) {
        assertTrue(row.requestFocus());
        row.dispatchKeyEvent(new KeyEvent(KeyEvent.ACTION_DOWN, code));
        row.dispatchKeyEvent(new KeyEvent(KeyEvent.ACTION_UP, code));
        layout(main);
    }

    private static String value(View row) {
        return ((TextView)row.findViewWithTag("value")).getText().toString();
    }

    @Test
    public void switchDirectionsSetTheStateWithoutTogglingAgain() {
        model.values.put("enabled", 0);
        panel.refresh();
        View subtitles = row("enabled");
        int writes = model.subtitleWrites;
        key(subtitles, KeyEvent.KEYCODE_DPAD_RIGHT);
        assertEquals(1, model.get("enabled"));
        assertEquals(writes + 1, model.subtitleWrites);
        key(subtitles, KeyEvent.KEYCODE_DPAD_RIGHT);
        assertEquals(1, model.get("enabled"));
        assertEquals("Already On does not write again", writes + 1, model.subtitleWrites);
        assertTrue(subtitles.hasFocus());
        key(subtitles, KeyEvent.KEYCODE_DPAD_LEFT);
        assertEquals(0, model.get("enabled"));
        key(subtitles, KeyEvent.KEYCODE_DPAD_LEFT);
        assertEquals("Already Off does not write again", writes + 2, model.subtitleWrites);
        assertTrue(subtitles.hasFocus());
        subtitles.performClick();
        assertEquals("OK still flips", 1, model.get("enabled"));

        open(PlaybackSubtitlePanel.TAB_AUDIO);
        View boost = tagged("audio.row.boost");
        writes = model.optionWrites;
        key(boost, KeyEvent.KEYCODE_DPAD_RIGHT);
        assertTrue(model.options.get("boost"));
        key(boost, KeyEvent.KEYCODE_DPAD_RIGHT);
        assertTrue(model.options.get("boost"));
        assertEquals("Already On does not toggle the backend", writes + 1, model.optionWrites);
        assertTrue(boost.hasFocus());
        key(boost, KeyEvent.KEYCODE_DPAD_LEFT);
        assertFalse(model.options.get("boost"));
        key(boost, KeyEvent.KEYCODE_DPAD_LEFT);
        assertEquals("Already Off does not toggle the backend", writes + 2, model.optionWrites);
        assertTrue(boost.hasFocus());
        boost.performClick();
        assertTrue("OK still flips", model.options.get("boost"));
    }

    @Test
    public void sidePanelIsOneThirdWideTopRightAndFittedToItsRows() throws Exception {
        WindowManager.LayoutParams a = main.getWindow().getAttributes();
        assertEquals(640, a.width);
        assertEquals(ViewGroup.LayoutParams.WRAP_CONTENT, a.height);
        assertEquals(Gravity.TOP | Gravity.END, a.gravity);
        assertEquals(40, a.x);
        assertEquals(40, a.y);
        assertEquals("No full-screen dim: the film stays visible", 0,
                     a.flags & WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        int height = main.getWindow().getDecorView().getHeight();
        assertTrue("Fitted, not full height: " + height, height < 1000 && height > 700);
        capture(main, "playback-subtitles-tab");
    }

    @Test
    public void threeTabsInApprovedOrderAndSubtitlesRowsInOrder() {
        ViewGroup tabs = (ViewGroup)tagged("playback.tab:1").getParent();
        assertEquals(3, tabs.getChildCount());
        assertEquals("playback.tab:1", tabs.getChildAt(0).getTag());
        assertEquals("playback.tab:0", tabs.getChildAt(1).getTag());
        assertEquals("playback.tab:2", tabs.getChildAt(2).getTag());
        String[] keys = {"enabled",  "track",   "download",   "size",    "colour",
                         "position", "outline", "background", "opacity", "delay"};
        int previous = -1;
        for (String key : keys) {
            View v = row(key);
            assertNotNull(key, v);
            assertTrue(key, v.getTop() > previous);
            previous = v.getTop();
            assertTrue(v.isFocusable());
            Rect visible = new Rect();
            assertTrue(key, v.getGlobalVisibleRect(visible));
            assertEquals("Whole row visible " + key, v.getHeight(), visible.height());
        }
        key(tagged("playback.tab:1"), KeyEvent.KEYCODE_DPAD_DOWN);
        assertTrue(row("enabled").hasFocus());
        key(row("enabled"), KeyEvent.KEYCODE_DPAD_UP);
        assertTrue(tagged("playback.tab:1").hasFocus());
    }

    @Test
    public void everyInlineControlUsesRealValues() {
        row("enabled").performClick();
        assertEquals(0, model.get("enabled"));
        row("enabled").performClick();
        key(row("size"), KeyEvent.KEYCODE_DPAD_RIGHT);
        assertEquals(75, model.get("size"));
        key(row("size"), KeyEvent.KEYCODE_DPAD_LEFT);
        assertEquals(50, model.get("size"));
        key(row("colour"), KeyEvent.KEYCODE_DPAD_RIGHT);
        assertEquals(PlaybackSubtitlePanel.COLOURS[2], model.get("colour"));
        key(row("position"), KeyEvent.KEYCODE_DPAD_RIGHT);
        assertEquals(128, model.get("position"));
        row("outline").performClick();
        assertEquals(0, model.get("outline"));
        row("background").performClick();
        assertEquals(1, model.get("background"));
        key(row("opacity"), KeyEvent.KEYCODE_DPAD_LEFT);
        assertEquals(191, model.get("opacity"));
        assertEquals("75%", value(row("opacity")));
        key(row("delay"), KeyEvent.KEYCODE_DPAD_LEFT);
        assertEquals(-100, model.get("delay"));
        key(row("delay"), KeyEvent.KEYCODE_DPAD_RIGHT);
        assertEquals(0, model.get("delay"));
        row("download").performClick();
        assertEquals(1, model.downloads);
    }

    @Test
    public void opacityIsDimmedWhileBackgroundIsOff() {
        assertEquals(0, model.get("background"));
        assertFalse(row("opacity").isEnabled());
        assertTrue(row("opacity").getAlpha() < .5f);
        assertFalse("A dimmed row is skipped by the remote", row("opacity").requestFocus());
        row("opacity").dispatchKeyEvent(new KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_DPAD_LEFT));
        assertEquals("Dimmed stepper does not change", 255, model.get("opacity"));
        row("background").performClick();
        assertTrue(row("opacity").isEnabled());
        assertEquals(1f, row("opacity").getAlpha(), .001f);
    }

    @Test
    public void subtitleTrackIsAStepperWithItsFlag() {
        assertEquals("English", value(row("track")));
        assertTrue(((TextView)row("track").findViewWithTag("value")).getCompoundDrawables()[0] instanceof
                   PreviewLanguageIcon);
        key(row("track"), KeyEvent.KEYCODE_DPAD_RIGHT);
        assertEquals(2, model.selected);
        assertEquals("Track 2", value(row("track")));
        model.disabledTrack = 1;
        key(row("track"), KeyEvent.KEYCODE_DPAD_LEFT);
        assertEquals("Skips a disabled track", 0, model.selected);
        key(row("track"), KeyEvent.KEYCODE_DPAD_LEFT);
        assertEquals("Stops at the first track", 0, model.selected);
    }

    @Test
    public void okOpensTheTrackListInsideThePanelAndBackReturns() throws Exception {
        Dialog before = ShadowDialog.getLatestDialog();
        row("track").performClick();
        layout(main);
        assertSame("No second pop-up", before, ShadowDialog.getLatestDialog());
        assertTrue(panel.pageOpen());
        assertFalse("Tabs hidden on the list page", tagged("playback.tab:1").isShown());
        assertNotNull(tagged("playback.page.back"));
        View selected = tagged("subtitles.track.1");
        assertTrue("Opens on the current track", selected.hasFocus());
        assertEquals(View.VISIBLE, ((ViewGroup)selected).getChildAt(2).getVisibility());
        TextView more = (TextView)tagged("playback.list.more");
        assertEquals(View.VISIBLE, more.getVisibility());
        assertTrue(more.getText().toString().matches("▾ \\d+ more"));
        capture(main, "playback-subtitle-track-list");
        assertTrue(panel.dismissPopup());
        layout(main);
        assertFalse(panel.pageOpen());
        assertTrue(row("track").hasFocus());
        assertFalse(panel.dismissPopup());
        row("track").performClick();
        layout(main);
        View third = tagged("subtitles.track.3");
        third.requestFocus();
        third.performClick();
        layout(main);
        assertEquals(3, model.selected);
        assertFalse("Choosing a track returns to the tab", panel.pageOpen());
        assertTrue(row("track").hasFocus());
    }

    @Test
    public void trackListFollowsReenumerationWithoutClosing() {
        row("track").performClick();
        layout(main);
        tagged("subtitles.track.0").requestFocus();
        model.trackCount = 3;
        model.selected = 2;
        panel.refresh();
        layout(main);
        assertTrue(panel.pageOpen());
        assertNull(tagged("subtitles.track.3"));
        ViewGroup selected = (ViewGroup)tagged("subtitles.track.2");
        assertEquals(View.VISIBLE, selected.getChildAt(2).getVisibility());
        assertTrue(tagged("subtitles.track.0").hasFocus());
        model.disabledTrack = 1;
        panel.refresh();
        assertFalse(tagged("subtitles.track.1").isFocusable());
        model.disabledTrack = -1;
        panel.refresh();
        assertTrue(tagged("subtitles.track.1").isFocusable());
    }

    @Test
    public void audioTabRowsAndControls() throws Exception {
        open(PlaybackSubtitlePanel.TAB_AUDIO);
        String[] tags = {"audio.row.track", "audio.row.delay", "audio.row.boost", "audio.row.night",
                         "audio.row.spatial"};
        int previous = -1;
        for (String tag : tags) {
            View v = tagged(tag);
            assertNotNull(tag, v);
            assertTrue(tag, v.getTop() > previous);
            previous = v.getTop();
        }
        assertEquals("English · 5.1", value(tagged("audio.row.track")));
        capture(main, "playback-audio-tab");
        key(tagged("audio.row.track"), KeyEvent.KEYCODE_DPAD_RIGHT);
        assertEquals(1, model.audio);
        key(tagged("audio.row.delay"), KeyEvent.KEYCODE_DPAD_RIGHT);
        assertEquals(50, model.audioDelay);
        assertEquals("50 ms", value(tagged("audio.row.delay")));
        tagged("audio.row.boost").performClick();
        assertTrue(model.options.get("boost"));
        tagged("audio.row.track").performClick();
        layout(main);
        assertTrue(panel.pageOpen());
        assertNotNull(tagged("audio.track.2"));
        assertTrue(panel.dismissPopup());
        assertTrue(tagged("audio.row.track").hasFocus());
    }

    @Test
    public void playbackTabRowsAndControls() throws Exception {
        open(PlaybackSubtitlePanel.TAB_PLAYBACK);
        String[] tags = {"playback.row.format", "playback.row.s3d",      "playback.row.pip",
                         "playback.row.speed",  "playback.row.playmode", "playback.row.skip"};
        int previous = -1;
        for (String tag : tags) {
            View v = tagged(tag);
            assertNotNull(tag, v);
            assertTrue(tag, v.getTop() > previous);
            previous = v.getTop();
        }
        assertEquals("1.0×", value(tagged("playback.row.speed")));
        assertEquals("Binge watching", value(tagged("playback.row.playmode")));
        capture(main, "playback-playback-tab");
        key(tagged("playback.row.format"), KeyEvent.KEYCODE_DPAD_RIGHT);
        assertEquals(1, (int)model.modeSelected.get("format"));
        assertEquals("Fullscreen", value(tagged("playback.row.format")));
        key(tagged("playback.row.speed"), KeyEvent.KEYCODE_DPAD_RIGHT);
        assertEquals(1.05f, model.speed, .001f);
        assertEquals("1.05×", value(tagged("playback.row.speed")));
        key(tagged("playback.row.playmode"), KeyEvent.KEYCODE_DPAD_RIGHT);
        assertEquals("Last mode stays", 4, (int)model.modeSelected.get("playmode"));
        tagged("playback.row.skip").performClick();
        assertFalse(model.options.get("skip"));
        tagged("playback.row.pip").performClick();
        assertEquals(1, model.pip);
    }

    @Test
    public void unavailableRowsAreNotShown() {
        model.options.remove("spatial");
        model.modes.remove("s3d");
        open(PlaybackSubtitlePanel.TAB_AUDIO);
        assertNull(tagged("audio.row.spatial"));
        assertNotNull(tagged("audio.row.night"));
        open(PlaybackSubtitlePanel.TAB_PLAYBACK);
        assertNull(tagged("playback.row.s3d"));
        assertNotNull(tagged("playback.row.format"));
    }

    @Test
    public void tabRowSwitchesTabsWithLeftRight() {
        key(tagged("playback.tab:1"), KeyEvent.KEYCODE_DPAD_RIGHT);
        assertTrue(tagged("playback.tab:0").hasFocus());
        assertEquals(PlaybackSubtitlePanel.TAB_AUDIO, panel.currentTab());
        assertNotNull(tagged("audio.row.track"));
        key(tagged("playback.tab:0"), KeyEvent.KEYCODE_DPAD_RIGHT);
        assertEquals(PlaybackSubtitlePanel.TAB_PLAYBACK, panel.currentTab());
        key(tagged("playback.tab:2"), KeyEvent.KEYCODE_DPAD_RIGHT);
        assertTrue("Right stops at the last tab", tagged("playback.tab:2").hasFocus());
        key(tagged("playback.tab:2"), KeyEvent.KEYCODE_DPAD_DOWN);
        assertTrue(tagged("playback.row.format").hasFocus());
    }

    @Test
    public void speedLabels() {
        assertEquals("1.0×", PlaybackSubtitlePanel.formatSpeed(1f));
        assertEquals("1.25×", PlaybackSubtitlePanel.formatSpeed(1.25f));
        assertEquals("0.5×", PlaybackSubtitlePanel.formatSpeed(.5f));
        assertEquals("2.0×", PlaybackSubtitlePanel.formatSpeed(2f));
    }

    @Test
    public void downloadResultsUseTheSidePanelWithOnlyProviderData() throws Exception {
        List<OpenSubtitlesSearchResult> results = new ArrayList<>();
        for (int i = 0; i < 12; i++) {
            OpenSubtitlesSearchResult r = new OpenSubtitlesSearchResult(
                "fixture" + i,
                i == 0 ? "Dune.Part.Two.2024.English.SDH.with.a.long.provider.filename.for.ellipsis.srt"
                       : "Provider result " + i,
                "en-GB");
            r.setHearingImpaired(i == 0);
            results.add(r);
        }
        Dialog popup = SubtitleResultsPanel.show(host.get(), results, r -> {});
        layout(popup);
        assertEquals(640, popup.getWindow().getAttributes().width);
        assertEquals(Gravity.TOP | Gravity.END, popup.getWindow().getAttributes().gravity);
        ViewGroup row = popup.getWindow().getDecorView().findViewWithTag("subtitles.result.0");
        assertTrue(row.hasFocus());
        LinearLayout copy = (LinearLayout)row.getChildAt(1);
        TextView filename = (TextView)copy.getChildAt(0);
        assertEquals(1, filename.getLineCount());
        assertNotNull(filename.getEllipsize());
        String meta = ((TextView)copy.getChildAt(1)).getText().toString();
        assertFalse("No invented size label", meta.contains("Size"));
        assertTrue(meta, meta.endsWith("· Hearing impaired"));
        assertFalse(((TextView)((LinearLayout)((ViewGroup)popup.getWindow().getDecorView().findViewWithTag(
                                                   "subtitles.result.1"))
                                    .getChildAt(1))
                         .getChildAt(1))
                        .getText()
                        .toString()
                        .contains("Hearing"));
        capture(popup, "playback-download-subtitles");
        popup.dismiss();
    }

    static void capture(Dialog dialog, String name) throws Exception {
        View v = dialog.getWindow().getDecorView();
        Bitmap bitmap = Bitmap.createBitmap(
            Math.max(1, v.getWidth()), Math.max(1, v.getHeight()), Bitmap.Config.ARGB_8888);
        bitmap.eraseColor(0xff3d4f7a);
        v.draw(new Canvas(bitmap));
        for (File file : new File[] {new File("build/reports/subtitles-ui/" + name + ".png"),
                                                     new File("build/reports/preview-ui/" + name + ".png")}) {
            file.getParentFile().mkdirs();
            try (FileOutputStream out = new FileOutputStream(file)) {
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, out);
            }
        }
        bitmap.recycle();
    }
}
