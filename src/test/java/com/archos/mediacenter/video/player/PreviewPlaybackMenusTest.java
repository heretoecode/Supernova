package com.archos.mediacenter.video.player;

import android.app.*;
import android.view.*;
import android.widget.*;
import com.archos.mediacenter.video.R;
import com.archos.mediacenter.video.player.tvmenu.*;
import java.util.*;
import org.junit.*;
import org.junit.runner.RunWith;
import org.robolectric.*;
import org.robolectric.annotation.Config;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

/** Real settings window and native callbacks; no decoder/hardware acceptance is implied. */
@RunWith(RobolectricTestRunner.class)
@Config(application = Application.class, sdk = 28, qualifiers = "w960dp-h540dp-land-mdpi")
public class PreviewPlaybackMenusTest {
    Activity host;
    Button opener;

    @Before
    public void setup() {
        host = Robolectric.buildActivity(TopNavigationTestHost.class).setup().visible().get();
        opener = new Button(host);
        opener.setText("Settings");
        opener.setFocusableInTouchMode(true);
        host.setContentView(opener);
        opener.requestFocus();
        Shadows.shadowOf(host).setCurrentFocus(opener);
    }

    @After
    public void close() {
        PreviewPlaybackMenus.close();
        host.finish();
    }

    public static class TopNavigationTestHost extends Activity {
        @Override
        public void onCreate(android.os.Bundle state) {
            setTheme(R.style.MyLeanbackTheme);
            super.onCreate(state);
        }
    }

    TVMenuAdapter source(String title, TVMenu menu) {
        TVCardView card = mock(TVCardView.class);
        when(card.previewTitle()).thenReturn(title);
        when(card.previewMenu()).thenReturn(menu);
        TVMenuAdapter source = mock(TVMenuAdapter.class);
        when(source.previewCards()).thenReturn(Collections.singletonList(card));
        return source;
    }

    Dialog dialog() {
        return org.robolectric.shadows.ShadowDialog.getLatestDialog();
    }

    void layoutWindow() {
        View root = dialog().getWindow().getDecorView();
        for (int pass = 0; pass < 3; pass++) {
            root.measure(
                View.MeasureSpec.makeMeasureSpec(dialog().getWindow().getAttributes().width, View.MeasureSpec.EXACTLY),
                View.MeasureSpec.makeMeasureSpec(dialog().getWindow().getAttributes().height,
                                                 View.MeasureSpec.EXACTLY));
            root.layout(0, 0, root.getMeasuredWidth(), root.getMeasuredHeight());
            Shadows.shadowOf(android.os.Looper.getMainLooper()).idle();
        }
    }

    TextView option(String label) {
        return dialog().getWindow().getDecorView().findViewWithTag("playback.option:" + label);
    }

    View tagged(String tag) {
        return dialog().getWindow().getDecorView().findViewWithTag(tag);
    }

    void key(View view, int code) {
        assertTrue(view.requestFocus());
        view.dispatchKeyEvent(new KeyEvent(KeyEvent.ACTION_DOWN, code));
        view.dispatchKeyEvent(new KeyEvent(KeyEvent.ACTION_UP, code));
        PlaybackSubtitlePanelTest.layout(dialog());
    }

    static String value(View row) {
        return ((TextView)row.findViewWithTag("value")).getText().toString();
    }

    @Test
    @Config(sdk = 30)
    public void nativeCardOpenedOverThePanelReturnsToTheSameTab() {
        TVMenu menu = new TVMenu(host);
        menu.createAndAddTVMenuItem("English", true, true);
        PreviewPlaybackMenus.show(host, source(host.getString(R.string.menu_audio), menu),
                                  host.getString(R.string.menu_audio));
        assertNotNull(tagged("audio.row.track"));
        ViewGroup container = (ViewGroup)LayoutInflater.from(host).inflate(R.layout.card_dialog_layout, null);
        TVCardDialog card = container.findViewById(R.id.card_view);
        card.setText("Adjustment");
        card.addOtherView(new TVMenu(host));
        PreviewPlaybackMenus.showNested(host, card);
        assertNull("Panel replaced while the card is open", tagged("subtitles.main"));
        View tab = tagged("playback.tab:0");
        assertNull(PreviewPlaybackMenus.nextFocusWithin(card, tab, View.FOCUS_UP));
        TextView detached = new TextView(host);
        assertNull(PreviewPlaybackMenus.nextFocusWithin(card, detached, View.FOCUS_UP));
        assertTrue(PreviewPlaybackMenus.back());
        assertTrue(PreviewPlaybackMenus.isShowing());
        assertNotNull("Back on the card returns to the Audio tab", tagged("audio.row.track"));
    }

    @Test
    public void exactHorizontalTabsSubtitlesFirstAndNoPlaybackDiagnosticEntries() throws Exception {
        PreviewPlaybackMenus.show(host, source(host.getString(R.string.menu_subtitles), new TVMenu(host)), null);
        View root = dialog().getWindow().getDecorView();
        assertNotNull(root.findViewWithTag("subtitles.main"));
        ViewGroup tabs = (ViewGroup)root.findViewWithTag("playback.tab:1").getParent();
        assertEquals("playback.tab:1", tabs.getChildAt(0).getTag());
        assertEquals(LinearLayout.HORIZONTAL, ((LinearLayout)tabs).getOrientation());
        root.findViewWithTag("playback.tab:0").performClick();
        root = dialog().getWindow().getDecorView();
        assertNull(option("Technical Information"));
        assertNull(option("Report a Problem"));
        assertTrue(PreviewPlaybackMenus.back());
        assertFalse(PreviewPlaybackMenus.isShowing());
        assertTrue(opener.hasFocus());
        for (String fieldName :
             new String[] {"current", "owner", "adapter", "origin", "tabs", "rows", "trackRows", "adjustmentRows",
                           "content", "scroll", "restoreParent", "backAction", "nested", "liveCard", "legacyPanel",
                           "subtitlePanel", "presentationHost"}) {
            java.lang.reflect.Field field = PreviewPlaybackMenus.class.getDeclaredField(fieldName);
            field.setAccessible(true);
            assertNull("Closed playback settings must release Activity-owned " + fieldName, field.get(null));
        }
    }

    @Test
    public void downloadAndLocalActionsRetainCallbacks() {
        TVMenu menu = new TVMenu(host);
        int[] calls = {0, 0};
        menu.createAndAddTVMenuItem(host.getString(R.string.get_subtitles_online), false, false)
            .setOnClickListener(v -> calls[0]++);
        menu.createAndAddTVMenuItem(host.getString(R.string.get_subtitles_on_drive), false, false)
            .setOnClickListener(v -> calls[1]++);
        PreviewPlaybackMenus.show(host, source(host.getString(R.string.menu_subtitles), menu), null);
        dialog().getWindow().getDecorView().findViewWithTag("subtitles.row.download").performClick();
        dialog().getWindow().getDecorView().findViewWithTag("subtitles.row.track").performClick();
        dialog().getWindow().getDecorView().findViewWithTag("subtitles.choose.file").performClick();
        assertArrayEquals(new int[] {1, 1}, calls);
    }

    @Test
    public void nestedAdjustmentRetainsTabsAndSavesOnBackTabChangeAndProgrammaticClose() {
        TVMenu menu = new TVMenu(host);
        PreviewPlaybackMenus.show(host, source(host.getString(R.string.menu_audio), menu),
                                  host.getString(R.string.menu_audio));
        Dialog original = dialog();
        int[] saves = {0};
        for (int exit = 0; exit < 3; exit++) {
            if (!PreviewPlaybackMenus.isShowing())
                PreviewPlaybackMenus.show(host, source(host.getString(R.string.menu_audio), menu),
                                          host.getString(R.string.menu_audio));
            ViewGroup container = (ViewGroup)LayoutInflater.from(host).inflate(R.layout.card_dialog_layout, null);
            TVCardDialog card = container.findViewById(R.id.card_view);
            card.setText("Adjustment");
            TVMenu controls = new TVMenu(host);
            controls.addView(LayoutInflater.from(host).inflate(R.layout.audio_speed_tv_picker, controls, false));
            card.addOtherView(controls);
            card.setOnDialogResultListener(code -> saves[0]++);
            PreviewPlaybackMenus.showNested(host, card);
            assertSame(original, dialog());
            assertNotNull(dialog().getWindow().getDecorView().findViewWithTag("playback.tab:0"));
            if (exit == 0)
                PreviewPlaybackMenus.back();
            else if (exit == 1)
                dialog().getWindow().getDecorView().findViewWithTag("playback.tab:2").performClick();
            else
                PreviewPlaybackMenus.close();
            assertEquals(exit + 1, saves[0]);
        }
    }

    @Test
    public void subtitlesRedesignPreservesStoredLanguageAndPreparationPreferences() {
        var prefs = androidx.preference.PreferenceManager.getDefaultSharedPreferences(host);
        prefs.edit()
            .putBoolean("subtitles_hide_default", true)
            .putString("favSubLang", "fra")
            .putStringSet("languages_list", Set.of("fra", "eng"))
            .commit();
        PreviewPlaybackMenus.show(host, source(host.getString(R.string.menu_subtitles), new TVMenu(host)), null);
        assertNotNull(dialog().getWindow().getDecorView().findViewWithTag("subtitles.row.colour"));
        PreviewPlaybackMenus.back();
        assertTrue(prefs.getBoolean("subtitles_hide_default", false));
        assertEquals("fra", prefs.getString("favSubLang", null));
        assertEquals(Set.of("fra", "eng"), prefs.getStringSet("languages_list", Set.of()));
    }

    @Test
    public void settingsAlwaysOpensOnSubtitlesInTheSidePanel() {
        PreviewPlaybackMenus.show(host, source(host.getString(R.string.menu_subtitles), new TVMenu(host)), null);
        assertTrue(tagged("playback.tab:1").hasFocus());
        assertNotNull(tagged("subtitles.row.track"));
        WindowManager.LayoutParams a = dialog().getWindow().getAttributes();
        assertEquals(Gravity.TOP | Gravity.END, a.gravity);
        assertEquals(ViewGroup.LayoutParams.WRAP_CONTENT, a.height);
        ViewGroup tabs = (ViewGroup)tagged("playback.tab:1").getParent();
        assertEquals(3, tabs.getChildCount());
    }

    @Test
    public void audioTrackStepperUsesNativeCallbacksInTheSameWindow() {
        TVMenu menu = new TVMenu(host);
        TVMenuItem english = menu.createAndAddTVMenuItem("English", true, true),
                   french = menu.createAndAddTVMenuItem("French", true, false);
        english.setTag(R.id.preview_track_language, "en-GB");
        french.setOnClickListener(v -> {
            english.setChecked(false);
            french.setChecked(true);
        });
        PreviewPlaybackMenus.show(host, source(host.getString(R.string.menu_audio), menu),
                                  host.getString(R.string.menu_audio));
        Dialog original = dialog();
        PlaybackSubtitlePanelTest.layout(original);
        View track = tagged("audio.row.track");
        assertEquals("English", value(track));
        assertTrue(((TextView)track.findViewWithTag("value")).getCompoundDrawables()[0] instanceof
                   com.archos.mediacenter.video.leanback.PreviewLanguageIcon);
        key(track, KeyEvent.KEYCODE_DPAD_RIGHT);
        assertSame(original, dialog());
        assertTrue(french.isChecked());
        assertFalse(english.isChecked());
        assertEquals("French", value(tagged("audio.row.track")));
    }

    @Test
    public void identicallyNamedTracksAreChosenByPositionInTheList() {
        TVMenu menu = new TVMenu(host);
        TVMenuItem first = menu.createAndAddTVMenuItem("English · AAC", true, true),
                   second = menu.createAndAddTVMenuItem("English · AAC", true, false);
        second.setOnClickListener(v -> {
            first.setChecked(false);
            second.setChecked(true);
        });
        PreviewPlaybackMenus.show(host, source(host.getString(R.string.menu_audio), menu),
                                  host.getString(R.string.menu_audio));
        PlaybackSubtitlePanelTest.layout(dialog());
        tagged("audio.row.track").performClick();
        PlaybackSubtitlePanelTest.layout(dialog());
        View row = tagged("audio.track.1");
        row.requestFocus();
        row.performClick();
        assertTrue(second.isChecked());
        assertFalse(first.isChecked());
        assertTrue(tagged("audio.row.track").hasFocus());
    }

    @Test
    public void trackListRefreshBindsTheLatestNativeCallbacks() {
        TVMenu menu = new TVMenu(host);
        menu.createAndAddTVMenuItem("None", true, false);
        menu.createAndAddTVMenuItem("English", true, true);
        menu.createAndAddTVMenuItem("French", true, false);
        TVMenuAdapter source = source(host.getString(R.string.menu_subtitles), menu);
        PreviewPlaybackMenus.show(host, source, null);
        PlaybackSubtitlePanelTest.layout(dialog());
        tagged("subtitles.row.track").performClick();
        PlaybackSubtitlePanelTest.layout(dialog());
        ViewGroup row = (ViewGroup)tagged("subtitles.track.2");
        row.requestFocus();
        int[] calls = {0};
        menu.clean();
        menu.createAndAddTVMenuItem("None", true, false);
        menu.createAndAddTVMenuItem("English", true, false);
        menu.createAndAddTVMenuItem("French", true, true).setOnClickListener(v -> calls[0]++);
        PreviewPlaybackMenus.refresh(host, source.previewCards().get(0));
        assertTrue(row.hasFocus());
        assertTrue(row.getContentDescription().toString().contains("selected"));
        row.performClick();
        assertEquals(1, calls[0]);
    }

    @Test
    public void playbackTabUsesNativeFormatAndPlayModeChoices() {
        TVMenu format = new TVMenu(host);
        format.createAndAddTVMenuItem("Original", true, true);
        TVMenuItem full = format.createAndAddTVMenuItem("Fullscreen", true, false);
        int[] pip = {0};
        format.createAndAddTVMenuItem(host.getString(R.string.picture_in_picture), false, false)
            .setOnClickListener(v -> pip[0]++);
        full.setOnClickListener(v -> {
            ((TVMenuItem)format.getChildAt(0)).setChecked(false);
            full.setChecked(true);
        });
        TVMenu mode = new TVMenu(host);
        mode.createAndAddTVMenuItem("Single", true, true);
        TVMenuItem skip = mode.createAndAddTVSwitchableMenuItem("Segment Skipping (this session)", false);
        skip.setOnClickListener(v -> skip.setChecked(!skip.isChecked()));
        TVCardView formats = mock(TVCardView.class), modes = mock(TVCardView.class);
        when(formats.previewTitle()).thenReturn(host.getString(R.string.pref_format_mode_title));
        when(formats.previewMenu()).thenReturn(format);
        when(modes.previewTitle()).thenReturn(host.getString(R.string.pref_play_mode_title));
        when(modes.previewMenu()).thenReturn(mode);
        TVMenuAdapter adapter = mock(TVMenuAdapter.class);
        when(adapter.previewCards()).thenReturn(Arrays.asList(formats, modes));
        PreviewPlaybackMenus.show(host, adapter, null);
        PlaybackSubtitlePanelTest.layout(dialog());
        key(tagged("playback.tab:1"), KeyEvent.KEYCODE_DPAD_RIGHT);
        key(tagged("playback.tab:0"), KeyEvent.KEYCODE_DPAD_RIGHT);
        assertNotNull(tagged("playback.row.format"));
        assertEquals("Original", value(tagged("playback.row.format")));
        assertNull("No 3D row without a 3D card", tagged("playback.row.s3d"));
        key(tagged("playback.row.format"), KeyEvent.KEYCODE_DPAD_RIGHT);
        assertTrue(full.isChecked());
        assertEquals("Fullscreen", value(tagged("playback.row.format")));
        assertEquals("Single", value(tagged("playback.row.playmode")));
        tagged("playback.row.skip").performClick();
        assertTrue(skip.isChecked());
        tagged("playback.row.pip").performClick();
        assertEquals(1, pip[0]);
    }

    @Test
    public void trackListBackReturnsToTheRowThenClosesOneLevel() {
        PreviewPlaybackMenus.show(host, source(host.getString(R.string.menu_subtitles), new TVMenu(host)), null);
        Dialog main = dialog();
        PlaybackSubtitlePanelTest.layout(main);
        View row = tagged("subtitles.row.track");
        row.requestFocus();
        row.performClick();
        assertSame("The list opens inside the panel", main, dialog());
        main.dispatchKeyEvent(new KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_BACK));
        main.dispatchKeyEvent(new KeyEvent(KeyEvent.ACTION_UP, KeyEvent.KEYCODE_BACK));
        assertTrue(main.isShowing());
        assertTrue(tagged("subtitles.row.track").hasFocus());
        PreviewPlaybackMenus.back();
        assertFalse(main.isShowing());
        assertTrue(opener.hasFocus());
    }
}
