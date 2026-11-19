package com.archos.mediacenter.video.leanback.settings;

import android.app.Application;
import android.app.Dialog;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.StateListDrawable;
import android.os.*;
import android.view.*;
import android.widget.*;
import androidx.appcompat.widget.SwitchCompat;
import androidx.lifecycle.ViewModelProvider;
import androidx.preference.*;
import com.archos.environment.ArchosUtils;
import com.archos.mediacenter.video.CustomApplication;
import com.archos.mediacenter.video.leanback.*;
import com.archos.mediacenter.video.leanback.StandardSwitchDrawable;
import com.archos.mediacenter.video.player.SegmentSkippingPolicy;
import com.archos.mediacenter.video.utils.VideoPreferencesCommon;
import com.squareup.picasso.Picasso;
import java.io.File;
import java.io.FileOutputStream;
import java.time.Duration;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;
import org.junit.*;
import org.junit.runner.RunWith;
import org.robolectric.*;
import org.robolectric.android.controller.ActivityController;
import org.robolectric.annotation.*;
import org.robolectric.shadows.ShadowDialog;
import org.robolectric.util.ReflectionHelpers;
import static org.junit.Assert.*;
import org.slf4j.LoggerFactory;

/** Checks Settings categories, controls, persisted choices and focus across layouts and recreation. */
@RunWith(RobolectricTestRunner.class)
@Config(application = Application.class, sdk = 28, qualifiers = "w960dp-h540dp-land-xhdpi")
@GraphicsMode(GraphicsMode.Mode.NATIVE)
@SQLiteMode(SQLiteMode.Mode.NATIVE)
public class SettingsWorkspaceTest {
    private ActivityController<TopNavigationTest.Host> host;
    private VideoSettingsFragment.PrefsFragment fragment;
    private View root;
    private SettingsWorkspace workspace;

    @Before
    public void setup() {
        Application app = RuntimeEnvironment.getApplication();
        Shadows.shadowOf(app.getPackageManager()).setSystemFeature("android.software.leanback", true);
        PreviewAsyncFixtures.offlineTransport();
        ReflectionHelpers.setStaticField(ViewModelProvider.AndroidViewModelFactory.class,
                                         "_instance", null);
        ReflectionHelpers.setStaticField(CustomApplication.class, "systemLocale", Locale.getDefault());
        ReflectionHelpers.setStaticField(CustomApplication.class, "mContext", app);
        ReflectionHelpers.setStaticField(CustomApplication.class, "log",
                                         LoggerFactory.getLogger(CustomApplication.class));
        ArchosUtils.setGlobalContext(app);
        PreferenceManager.getDefaultSharedPreferences(app).edit().clear().putBoolean("try_new_ui", true).commit();
        try {
            Picasso.get();
        } catch (IllegalStateException absent) {
            Picasso.setSingletonInstance(new Picasso.Builder(app).build());
        }
        host = Robolectric.buildActivity(TopNavigationTest.Host.class).setup().visible();
        fragment = new VideoSettingsFragment.PrefsFragment();
        host.get().getSupportFragmentManager().beginTransaction().replace(android.R.id.content, fragment).commitNow();
        root = fragment.requireView();
        workspace = fragment.workspace;
        layout();
    }

    @After
    public void close() throws Exception {
        if (host != null)
            host.pause().stop().destroy();
        PreviewAsyncFixtures.drain();
    }

    private void layout() {
        float density = root.getResources().getDisplayMetrics().density;
        int w = Math.round(root.getResources().getConfiguration().screenWidthDp * density),
            h = Math.round(root.getResources().getConfiguration().screenHeightDp * density);
        for (int i = 0; i < 4; i++) {
            Shadows.shadowOf(Looper.getMainLooper()).idle();
            root.measure(View.MeasureSpec.makeMeasureSpec(w, View.MeasureSpec.EXACTLY),
                         View.MeasureSpec.makeMeasureSpec(h, View.MeasureSpec.EXACTLY));
            root.layout(0, 0, w, h);
        }
        Shadows.shadowOf(Looper.getMainLooper()).idle();
    }

    private View tagged(String tag) {
        View v = root.findViewWithTag(tag);
        assertNotNull(tag, v);
        return v;
    }

    private void key(int code) {
        root.dispatchKeyEvent(new KeyEvent(KeyEvent.ACTION_DOWN, code));
        layout();
    }

    /** 0.158 (page sheet SETTINGS.md): no tiles; seven tabs; Settings opens on Playback with focus on its tab. */
    @Test
    public void sevenTabsOpenOnPlaybackAndBackGoesToTheTabThenOut() {
        assertNull(root.findViewWithTag("settings.grid.0"));
        assertTrue(tagged("settings.tab.0").hasFocus());
        ViewGroup tabs = (ViewGroup)tagged("settings.tabs");
        assertEquals(7, tabs.getChildCount());
        String[] names = {"Playback", "Audio", "Subtitles", "Library", "Services", "System", "About"};
        for (int i = 0; i < names.length; i++)
            assertEquals(names[i], ((TextView)tabs.getChildAt(i)).getText().toString());
        key(KeyEvent.KEYCODE_DPAD_DOWN);
        assertTrue("Down goes into the tab's rows", tagged("settings.content").hasFocus());
        assertTrue("Back from the rows goes to the open tab", workspace.back());
        layout();
        assertTrue(tagged("settings.tab.0").hasFocus());
        assertFalse("Back from the tabs leaves for the top bar", workspace.back());
    }

    /** 0.156 page pattern (mock-ups settings/03–05): tabs in the header, rows below, values level with titles. */
    /** Moving along the tabs switches the page at once (RULES §4); a tab's sections stack with their headings. */
    @Test
    public void tabsSwitchAtOnceAndRowsKeepTheirValueLevelWithTheTitle() {
        workspace.open(0, null);
        layout();
        View first = tagged("settings.tab.0"), second = tagged("settings.tab.1");
        assertSame("tabs live in one row", tagged("settings.tabs"), first.getParent());
        assertTrue(first.requestFocus());
        key(KeyEvent.KEYCODE_DPAD_RIGHT);
        assertTrue("Right moves to the next tab", second.hasFocus());
        assertNotNull("the focused tab opens its rows", tagged("settings.preference.favAudioLang"));
        key(KeyEvent.KEYCODE_DPAD_LEFT);
        assertTrue(first.hasFocus());
        for (String heading : new String[] {"General", "Resume", "Skipping", "Picture"})
            assertNotNull(tagged("settings.heading." + heading));
        key(KeyEvent.KEYCODE_DPAD_DOWN);
        View row = root.findFocus();
        assertNotNull(row);
        assertTrue(String.valueOf(row.getTag()).startsWith("settings.preference."));
        key(KeyEvent.KEYCODE_DPAD_UP);
        assertTrue("Up from the first row returns to the open tab", first.hasFocus());
        // PL7: Seek previews sit in Playback › General.
        assertNotNull(tagged("settings.preference.seek_thumbnails_enabled"));
        ViewGroup hide = (ViewGroup)tagged("settings.preference.hide_controls_on_pause");
        View toggle = hide.findViewWithTag("settings.switch");
        // Everything in a row sits on the row's centre line (RULES; 0.157 Shield review #2).
        int[] rowAt = new int[2], toggleAt = new int[2];
        hide.getLocationInWindow(rowAt);
        toggle.getLocationInWindow(toggleAt);
        float rowCentre = rowAt[1] + hide.getHeight() / 2f, toggleCentre = toggleAt[1] + toggle.getHeight() / 2f;
        assertTrue("the switch is on the row's centre line", Math.abs(rowCentre - toggleCentre) <= 2);
    }

    /** Owner rule: up to ~5 short choices sit inline and Left/Right changes them straight away. */
    @Test
    public void shortChoicesAreInlineAndLeftRightSavesThem() {
        workspace.open(5, null);
        layout();
        ListPreference level = fragment.findPreference("supernova_pcm_speed_engine");
        assertNotNull(level);
        assertTrue(SettingsWorkspace.inline(level));
        View row = tagged("settings.preference.supernova_pcm_speed_engine");
        assertTrue(row.requestFocus());
        layout();
        View segments = row.findViewWithTag("settings.segments");
        assertEquals(View.VISIBLE, segments.getVisibility());
        int before = level.findIndexOfValue(level.getValue());
        int direction = before == 0 ? 1 : -1;
        key(direction > 0 ? KeyEvent.KEYCODE_DPAD_RIGHT : KeyEvent.KEYCODE_DPAD_LEFT);
        assertEquals(before + direction, level.findIndexOfValue(level.getValue()));
        assertTrue("focus stays on the row", row.hasFocus());
    }

    @Test
    public void switchTrackRemainsDistinctWithWhiteAndBlackCustomAccents() {
        var prefs = PreferenceManager.getDefaultSharedPreferences(host.get());
        for (int accent : new int[] {Color.WHITE, Color.BLACK, 0xff62bbf3}) {
            prefs.edit().putInt("preview_accent41", accent).commit();
            SwitchCompat toggle = new SwitchCompat(host.get());
            PreviewSettings.styleSwitch(toggle);
            toggle.setBackground(null);
            toggle.setChecked(true);
            PreviewSettings.styleSwitch(toggle);
            assertArrayEquals("Restyling preserves native checked/enabled state", toggle.getDrawableState(),
                              toggle.getTrackDrawable().getState());
            toggle.jumpDrawablesToCurrentState();
            toggle.measure(View.MeasureSpec.makeMeasureSpec(200, View.MeasureSpec.EXACTLY),
                           View.MeasureSpec.makeMeasureSpec(100, View.MeasureSpec.EXACTLY));
            toggle.layout(0, 0, 200, 100);
            Bitmap image =
                Bitmap.createBitmap(200, 100, Bitmap.Config.ARGB_8888);
            toggle.draw(new Canvas(image));
            Set<Integer> colours = new HashSet<>();
            for (int y = 0; y < 100; y++)
                for (int x = 0; x < 200; x++)
                    colours.add(image.getPixel(x, y));
            // No blue (RULES; player v10): on = white track with a dark knob, whatever the accent.
            assertTrue("White track visible", colours.contains(StandardSwitchDrawable.ON_TRACK));
            assertTrue("Dark knob visible", colours.contains(StandardSwitchDrawable.KNOB_ON));
            assertNotEquals(Color.WHITE, PreviewAccent.switchTrack(host.get()));
            assertNotEquals(Color.BLACK, PreviewAccent.switchTrack(host.get()));
            if (accent == 0xff62bbf3)
                assertEquals(accent, PreviewAccent.switchTrack(host.get()));
        }
    }

    @Test
    public void legacySwitchRetainsSeparateTrackAndThumbColours() {
        CheckBox box = new CheckBox(host.get());
        box.setButtonTintList(ColorStateList.valueOf(Color.WHITE));
        PreviewSettings.tintSwitches(box);
        assertNull(box.getButtonTintList());
        for (boolean checked : new boolean[] {false, true}) {
            box.setEnabled(true);
            box.setChecked(checked);
            Drawable icon = box.getButtonDrawable();
            icon.setBounds(0, 0, 60, 32);
            Bitmap image =
                Bitmap.createBitmap(60, 32, Bitmap.Config.ARGB_8888);
            icon.draw(new Canvas(image));
            Set<Integer> colours = new HashSet<>();
            for (int y = 0; y < 32; y++)
                for (int x = 0; x < 60; x++)
                    if (Color.alpha(image.getPixel(x, y)) > 240)
                        colours.add(image.getPixel(x, y));
            // No blue (RULES; player v10): on = white track, dark knob; off = dark track, white knob.
            if (checked) {
                assertTrue(colours.contains(StandardSwitchDrawable.ON_TRACK));
                assertTrue(colours.contains(StandardSwitchDrawable.KNOB_ON));
            } else {
                assertTrue(colours.contains(Color.WHITE));
                assertTrue(colours.contains(StandardSwitchDrawable.OFF));
            }
            box.setEnabled(false);
            image.eraseColor(Color.TRANSPARENT);
            icon = box.getButtonDrawable();
            icon.setBounds(0, 0, 60, 32);
            icon.draw(new Canvas(image));
            int maximumAlpha = 0;
            for (int y = 0; y < 32; y++)
                for (int x = 0; x < 60; x++)
                    maximumAlpha = Math.max(maximumAlpha, Color.alpha(image.getPixel(x, y)));
            assertTrue("Disabled switch remains visible but distinct", maximumAlpha > 0 && maximumAlpha < 255);
        }
    }

    @Test
    public void togglePersistsWithoutMovingFocusAndHiddenRowsRemainAbsent() {
        workspace.open(0, null);
        layout();
        View row = tagged("settings.preference.hide_controls_on_pause");
        row.requestFocus();
        boolean before = ((TwoStatePreference)fragment.findPreference("hide_controls_on_pause")).isChecked();
        row.performClick();
        layout();
        assertEquals(!before,
                     PreferenceManager.getDefaultSharedPreferences(host.get()).getBoolean("hide_controls_on_pause", true));
        assertTrue(row.hasFocus());
        assertNull(root.findViewWithTag("settings.preference.try_new_ui"));
        assertNull(root.findViewWithTag("settings.preference.app_theme"));
        workspace.back();
        workspace.open(0, null);
        layout();
        assertEquals(!before, ((TwoStatePreference)fragment.findPreference("hide_controls_on_pause")).isChecked());
    }

    @Test
    public void toggleStatesAreExplicitAndUnavailableRowsKeepFocusOutline() {
        workspace.open(0, null);
        layout();
        TwoStatePreference preference = fragment.findPreference("hide_controls_on_pause");
        for (boolean checked : new boolean[] {false, true}) {
            preference.setChecked(checked);
            layout();
            ViewGroup row = (ViewGroup)tagged("settings.preference.hide_controls_on_pause");
            assertEquals(View.GONE, row.findViewWithTag("settings.value").getVisibility());
            assertTrue(row.getContentDescription().toString().endsWith(checked ? "On" : "Off"));
        }
        preference.setEnabled(false);
        layout();
        ViewGroup row = (ViewGroup)tagged("settings.preference.hide_controls_on_pause");
        assertEquals(View.GONE, row.findViewWithTag("settings.value").getVisibility());
        assertFalse(row.findViewWithTag("settings.switch").isEnabled());
        assertEquals(1f, row.getAlpha(), 0f);
        assertTrue(row.requestFocus());
        assertTrue(row.hasFocus());
        boolean before = preference.isChecked();
        row.performClick();
        assertEquals(before, preference.isChecked());
    }

    @Test
    public void regionalLanguageChoicesBindBundledFlagsAndKeepNeutralFallback() {
        for (String key : new String[] {"favAudioLang", "favSubLang"}) {
            ListPreference p = new ListPreference(host.get());
            p.setPersistent(false);
            p.setKey(key);
            p.setTitle("Language");
            p.setEntries(new String[] {"English UK", "English US", "English", "Unknown"});
            p.setEntryValues(new String[] {"en-GB", "en-US", "en", "xx"});
            p.setValue("en");
            Dialog dialog = SettingsChoiceDialog.show(host.get(), p);
            for (int n = 0; n < 4; n++) {
                ViewGroup row = dialog.getWindow().getDecorView().findViewWithTag(n);
                Drawable icon = ((ImageView)row.getChildAt(0)).getDrawable();
                assertTrue(icon instanceof PreviewLanguageIcon);
                Object flag = ReflectionHelpers.getField(icon, "flag");
                if (n < 3)
                    assertNotNull(flag);
                else
                    assertNull(flag);
                assertTrue(row.requestFocus());
            }
            dialog.dismiss();
        }
    }

    @Test
    public void providerNamesBindLogosWithoutCatalogueAndDoneKeepsActionIcon() {
        MultiSelectListPreference p = new MultiSelectListPreference(host.get());
        p.setPersistent(false);
        p.setKey("streaming_providers_GB");
        p.setTitle("Providers");
        p.setEntries(new String[] {"Netflix", "Unknown provider"});
        p.setEntryValues(new String[] {"8", "999999"});
        p.setValues(Set.of("8"));
        PreviewPreferenceDialogs.show(fragment, p);
        Dialog dialog = ShadowDialog.getLatestDialog();
        View decor = dialog.getWindow().getDecorView();
        View label = decor.findViewWithTag("preview-label:1");
        ImageView netflix = (ImageView)((ViewGroup)label.getParent()).getChildAt(0);
        assertFalse(netflix.getDrawable() instanceof PreviewIcon);
        View done = decor.findViewWithTag("preview-label:4");
        ImageView action = (ImageView)((ViewGroup)done.getParent()).getChildAt(0);
        assertEquals("done", ReflectionHelpers.getField(action.getDrawable(), "kind"));
        dialog.dismiss();
    }

    @Test
    public void positiveSubtitleProxyImmediatelyUpdatesNativeLanguageDependency() {
        workspace.open(2, null);
        layout();
        TwoStatePreference positive = fragment.findPreference("preview_subtitles_by_default");
        positive.setChecked(true);
        View row = tagged("settings.preference.preview_subtitles_by_default");
        row.performClick();
        layout();
        assertTrue(((TwoStatePreference)fragment.findPreference("subtitles_hide_default")).isChecked());
        assertFalse(fragment.findPreference("favSubLang").isEnabled());
        assertFalse(fragment.findPreference("subtitles_show_full_for_local_audio").isEnabled());
        row.performClick();
        layout();
        assertTrue(fragment.findPreference("favSubLang").isEnabled());
        assertTrue(fragment.findPreference("subtitles_show_full_for_local_audio").isEnabled());
        // 0.158: the fonts rows sit in Subtitles › Default Appearance, after the live sample and style controls.
        assertNotNull(tagged("settings.preference.subtitle_fonts_folder_uri"));
        assertNotNull(tagged("settings.preference.subtitle_default_font"));
        assertNotNull(tagged("settings.subtitle.sample"));
        assertNotNull(tagged("settings.subtitle.position"));
        assertNotNull(tagged("settings.heading.Default Appearance"));
    }

    @Test
    public void providerInlineChecksKeepRowIdentityGroupAndFocus() {
        LinearLayout target = new LinearLayout(host.get());
        target.setOrientation(LinearLayout.VERTICAL);
        MultiSelectListPreference p = new MultiSelectListPreference(host.get());
        p.setKey("streaming_providers_IE");
        p.setEntries(new String[] {"Alpha", "Beta"});
        p.setEntryValues(new String[] {"1", "2"});
        p.setValues(new HashSet<>(Set.of("1")));
        host.get().addContentView(target, new ViewGroup.LayoutParams(800, 600));
        InlinePreferenceChoices.show(target, p);
        target.measure(View.MeasureSpec.makeMeasureSpec(800, View.MeasureSpec.EXACTLY),
                       View.MeasureSpec.makeMeasureSpec(600, View.MeasureSpec.EXACTLY));
        target.layout(0, 0, 800, 600);
        View alpha = target.findViewWithTag("1");
        assertNotNull(alpha);
        alpha.requestFocus();
        alpha.performClick();
        assertSame(alpha, target.findViewWithTag("1"));
        assertTrue(alpha.hasFocus());
        assertFalse(p.getValues().contains("1"));
        alpha.performClick();
        assertSame(alpha, target.findViewWithTag("1"));
        assertTrue(p.getValues().contains("1"));
    }

    @Test
    public void mapAccountsForBaselineAndNativeSubtitleExtensionWithoutResurrectingHiddenState() {
        SettingsRegistry registry = new SettingsRegistry(host.get());
        assertEquals(173, registry.entries.size());
        assertEquals(2, registry.entries.stream().filter(e -> e.id.startsWith("N158")).count());
        assertEquals(1, registry.entries.stream().filter(e -> e.id.startsWith("N151") && !e.nested()).count());
        assertEquals(165, registry.entries.stream().filter(e -> e.id.startsWith("S")).count());
        assertEquals(5, registry.entries.stream().filter(e -> e.id.startsWith("N146") && !e.nested()).count());
        Set<String> ids = new HashSet<>();
        for (SettingsRegistry.Entry e : registry.entries) {
            assertTrue(ids.add(e.id));
            assertFalse(e.disposition.isEmpty());
            if (e.excluded())
                assertFalse(e.integration());
        }
        for (int i : SettingsLayout.TABS) {
            workspace.open(i, null);
            layout();
            assertEquals("tabs, content", 2, ((ViewGroup)tagged("settings.category")).getChildCount());
        }
    }

    /** Owner, 19 Nov 2026: the skip rows use the sheet's words, as chips, values unchanged. */
    @Test
    public void skipRowsUseTheSheetsWords() {
        workspace.open(0, null);
        layout();
        ListPreference intro = fragment.findPreference("supernova_segment_intro");
        assertNotNull(intro);
        View row = tagged("settings.preference.supernova_segment_intro");
        assertNotNull(row);
        assertEquals(java.util.Arrays.asList("Off", "Show button", "Skip", "Skip when binge-watching"),
                     java.util.Arrays.asList(java.util.Arrays.stream(intro.getEntries())
                                                 .map(CharSequence::toString).toArray(String[]::new)));
        assertEquals(java.util.Arrays.asList("NORMAL", "PROMPT", "AUTO", "SMART"),
                     java.util.Arrays.asList(java.util.Arrays.stream(intro.getEntryValues())
                                                 .map(CharSequence::toString).toArray(String[]::new)));
        assertTrue(SettingsWorkspace.inline(intro));
        assertFalse("No NOVA words", containsText(row, "Play Normally"));
    }

    /** Owner question 2 Nov 2026: the OMDb key must be reachable; 0.158: Settings › Services › Ratings. */
    @Test
    public void ratingsKeyIsVisibleUnderLibraryAndMetadata() {
        workspace.open(4, null);
        layout();
        View row = tagged("settings.preference.omdb_api_key");
        assertTrue("row is shown", row.isShown());
        assertTrue("row is reachable with the remote", row.isFocusable());
        assertTrue("row is titled for the owner", containsText(row, "OMDb ratings key"));
    }

    private static boolean containsText(View view, String text) {
        if (view instanceof android.widget.TextView &&
            ((android.widget.TextView)view).getText().toString().contains(text))
            return true;
        if (view instanceof ViewGroup)
            for (int i = 0; i < ((ViewGroup)view).getChildCount(); i++)
                if (containsText(((ViewGroup)view).getChildAt(i), text))
                    return true;
        return false;
    }

    @Test
    public void navigationStateSurvivesRecreationWithoutReplayingAnAction() {
        workspace.open(5, null);
        layout();
        View row = tagged("settings.preference.supernova_pcm_speed_engine");
        row.requestFocus();
        Bundle state = new Bundle();
        fragment.onSaveInstanceState(state);
        SettingsWorkspace copy =
            new SettingsWorkspace(fragment, ReflectionHelpers.getField(fragment, "backupController"), state);
        assertNotNull(copy.findViewWithTag("settings.heading.Diagnostics"));
        copy.dispose();
    }

    @Test
    public void choiceCancelAndAcceptedSelectionRestoreOpenerAndRespectVeto() {
        workspace.open(5, null);
        layout();
        View opener = tagged("settings.preference.supernova_pcm_speed_engine");
        ListPreference level = fragment.findPreference("supernova_pcm_speed_engine");
        assertNotNull(level);
        opener.requestFocus();
        Dialog dialog = SettingsChoiceDialog.show(host.get(), level);
        assertNotNull(dialog);
        dialog.getWindow().getDecorView().findViewWithTag(0).requestFocus();
        String original = level.getValue();
        // The shared choice list has no Cancel button: Back closes it.
        dialog.onBackPressed();
        layout();
        assertEquals(original, level.getValue());
        assertTrue(opener.hasFocus());
        level.setOnPreferenceChangeListener((p, value) -> false);
        dialog = SettingsChoiceDialog.show(host.get(), level);
        dialog.getWindow().getDecorView().findViewWithTag(0).performClick();
        assertTrue(dialog.isShowing());
        assertEquals(original, level.getValue());
        dialog.dismiss();
        level.setOnPreferenceChangeListener((p, value) -> true);
        dialog = SettingsChoiceDialog.show(host.get(), level);
        dialog.getWindow().getDecorView().findViewWithTag(0).performClick();
        layout();
        assertFalse(dialog.isShowing());
        assertEquals(level.getEntryValues()[0].toString(), level.getValue());
        assertTrue(opener.hasFocus());
    }

    /** "Look up skip times online" off hides the skipping choices but never itself, so it can be turned back on. */
    @Test
    public void integrationDisableAndReenableRetainsPolicyAndReconcilesChildren() {
        String master = SegmentSkippingPolicy.MASTER;
        workspace.open(0, null);
        layout();
        String child = null;
        SettingsRegistry registry = new SettingsRegistry(host.get());
        for (SettingsRegistry.Entry e : registry.in(0, "Skipping"))
            if (!e.key.equals(master) && root.findViewWithTag("settings.preference." + e.key) != null) {
                child = e.key;
                break;
            }
        assertNotNull(child);
        Preference policy = fragment.findPreference(child);
        assertNotNull(policy);
        String value = ((ListPreference)policy).getValue();
        tagged("settings.preference." + master).performClick();
        layout();
        assertNull(root.findViewWithTag("settings.preference." + child));
        assertNotNull("the switch itself stays", root.findViewWithTag("settings.preference." + master));
        tagged("settings.preference." + master).performClick();
        layout();
        assertNotNull(root.findViewWithTag("settings.preference." + child));
        assertEquals(value, ((ListPreference)fragment.findPreference(child)).getValue());
    }

    @Test
    public void advancedUnlockReparentsNativePreferencesWithoutResettingSavedValues() {
        workspace.open(5, null);
        layout();
        View original = tagged("settings.preference.force_software_decoding");
        original.requestFocus();
        androidx.preference.TwoStatePreference force = fragment.findPreference("force_software_decoding");
        assertNotNull(force);
        for (int i = 0; i < 9; i++)
            force.performClick();
        layout();
        assertTrue(PreferenceManager.getDefaultSharedPreferences(host.get())
                       .getBoolean(VideoPreferencesCommon.KEY_ADVANCED_VIDEO_ENABLED,
                                   false));
        assertNotNull(fragment.findPreference("dec_choice"));
        assertNull("Basic decoder control must leave the model after advanced unlock",
                   fragment.findPreference("force_software_decoding"));
        assertNull(root.findViewWithTag("settings.preference.try_new_ui"));
        assertNotNull(root.findFocus());
        assertTrue(String.valueOf(root.findFocus().getTag()).startsWith("settings.preference."));
        Preference quit =
            fragment.findPreference(VideoPreferencesCommon.KEY_ADVANCED_VIDEO_QUIT);
        assertNotNull(quit);
        quit.performClick();
        layout();
        assertFalse(
            PreferenceManager.getDefaultSharedPreferences(host.get())
                .getBoolean(VideoPreferencesCommon.KEY_ADVANCED_VIDEO_ENABLED,
                            true));
        assertNull(fragment.findPreference("dec_choice"));
        assertNotNull(fragment.findPreference("force_software_decoding"));
        assertNotNull(root.findFocus());
    }

    @Test
    public void externalRestoreReconcilesNativeValuesWithoutReplayingActions() {
        workspace.open(0, null);
        layout();
        View row = tagged("settings.preference.hide_controls_on_pause");
        row.requestFocus();
        TwoStatePreference nativeValue = fragment.findPreference("hide_controls_on_pause");
        final int[] called = {0};
        nativeValue.setOnPreferenceChangeListener((p, v) -> {
            called[0]++;
            return true;
        });
        boolean updated = !nativeValue.isChecked();
        PreferenceManager.getDefaultSharedPreferences(host.get())
            .edit()
            .putBoolean("hide_controls_on_pause", updated)
            .putBoolean("subtitles_hide_default", true)
            .commit();
        layout();
        assertEquals(updated, nativeValue.isChecked());
        assertTrue(row.hasFocus());
        assertEquals(0, called[0]);
        workspace.open(2, null);
        layout();
        assertFalse(((TwoStatePreference)fragment.findPreference("preview_subtitles_by_default")).isChecked());
        assertFalse(fragment.findPreference("favSubLang").isEnabled());
    }

    @Test
    public void allTabsRenderTheirRealContent() throws Exception {
        for (int i : SettingsLayout.TABS) {
            workspace.open(i, null);
            layout();
            save("tab-" + i);
            assertTrue(tagged("settings.tab." + i).isShown());
        }
        workspace.open(5, "Backup & restore");
        layout();
        save("backup");
        workspace.open(2, "OpenSubtitles");
        layout();
        save("opensubtitles");
    }

    @Test
    @Config(qualifiers = "w640dp-h360dp-land-xhdpi")
    public void render720p() throws Exception {
        render("720p");
    }

    @Test
    public void render1080p() throws Exception {
        render("1080p");
    }

    @Test
    @Config(qualifiers = "w960dp-h540dp-land-xxxhdpi")
    public void render4k() throws Exception {
        render("4k");
    }

    @Test
    public void renderEnlargedText() throws Exception {
        RuntimeEnvironment.setFontScale(1.5f);
        try {
            workspace.open(0, null);
            layout();
            render("large");
        } finally {
            RuntimeEnvironment.setFontScale(1f);
        }
    }

    private void render(String name) throws Exception {
        layout();
        // Every tab's word fits its box, unclipped, at every size (0.158 page sheet).
        ViewGroup tabs = (ViewGroup)tagged("settings.tabs");
        for (int i = 0; i < tabs.getChildCount(); i++) {
            TextView t = (TextView)tabs.getChildAt(i);
            assertNotNull(t.getLayout());
            assertEquals(name + " tab " + i + " not cut", 0, t.getLayout().getEllipsisCount(0));
        }
        save(name + "-playback");
        workspace.open(5, null);
        layout();
        save(name + "-system");
    }

    private void save(String name) throws Exception {
        Bitmap image = Bitmap.createBitmap(root.getWidth(), root.getHeight(),
                                                                             Bitmap.Config.ARGB_8888);
        root.draw(new Canvas(image));
        File file = new File("build/reports/settings-0143/" + name + ".png");
        file.getParentFile().mkdirs();
        try (FileOutputStream out = new FileOutputStream(file)) {
            image.compress(Bitmap.CompressFormat.PNG, 100, out);
        }
        image.recycle();
    }

    @Test
    public void audioTabUsesPurposeSpecificCopy() {
        workspace.open(1, null);
        layout();
        assertNotNull(tagged("settings.heading.Language"));
        assertNotNull(tagged("settings.heading.Sound"));
        View preferred = tagged("settings.preference.favAudioLang");
        assertTrue(containsText(preferred, "Preferred audio language"));
    }

    @Test
    public void switchThumbStaysCircularWhenCompatAssignsTallTrackBounds() {
        SettingsSwitchThumb thumb = new SettingsSwitchThumb(host.get());
        int diameter = thumb.getIntrinsicWidth();
        Bitmap image =
            Bitmap.createBitmap(diameter, diameter * 2, Bitmap.Config.ARGB_8888);
        thumb.setBounds(0, 0, diameter, diameter * 2);
        thumb.draw(new Canvas(image));
        assertEquals(0, Color.alpha(image.getPixel(diameter / 2, 0)));
        assertEquals(255, Color.alpha(image.getPixel(diameter / 2, diameter)));
        assertEquals(0, Color.alpha(image.getPixel(diameter / 2, diameter * 2 - 1)));
        image.recycle();
    }

    @Test
    public void sharedNavigationReachesEveryTabFromTheTopBar() throws Exception {
        ViewGroup old = (ViewGroup)root.getParent();
        old.removeView(root);
        TopNavigation nav = new TopNavigation(host.get(), root, index -> {}, workspace::atNavigationBoundary);
        nav.selectTab(4);
        host.get().setContentView(nav);
        nav.measure(View.MeasureSpec.makeMeasureSpec(1920, View.MeasureSpec.EXACTLY),
                    View.MeasureSpec.makeMeasureSpec(1080, View.MeasureSpec.EXACTLY));
        nav.layout(0, 0, 1920, 1080);
        for (int n : SettingsLayout.TABS) {
            workspace.open(n, null);
            layout();
            assertTrue(workspace.atNavigationBoundary());
            nav.dispatchKeyEvent(new KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_DPAD_UP));
            assertTrue(nav.findViewWithTag("semantic:topnav.settings").hasFocus());
            nav.dispatchKeyEvent(new KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_DPAD_DOWN));
            assertTrue(workspace.atNavigationBoundary());
        }
    }

    private void invalidateAll(View view) {
        view.invalidate();
        if (view instanceof ViewGroup)
            for (int n = 0; n < ((ViewGroup)view).getChildCount(); n++)
                invalidateAll(((ViewGroup)view).getChildAt(n));
    }

    @Test
    public void productionActivityRendersTheSharedHeaderOnGridAndCategory() throws Exception {
        // As in existing production-Activity fixtures, model AndroidX's installed signature permission.
        Shadows.shadowOf(RuntimeEnvironment.getApplication())
            .grantPermissions("app.supernova.player.DYNAMIC_RECEIVER_NOT_EXPORTED_PERMISSION");
        var activity = Robolectric.buildActivity(VideoSettingsActivity.class).setup().visible();
        try {
            activity.get().getSupportFragmentManager().executePendingTransactions();
            Shadows.shadowOf(Looper.getMainLooper()).idle();
            View nav = ((ViewGroup)activity.get().findViewById(android.R.id.content)).getChildAt(0);
            assertTrue(nav instanceof TopNavigation);
            for (int n = 0; n < 4; n++) {
                nav.measure(View.MeasureSpec.makeMeasureSpec(1920, View.MeasureSpec.EXACTLY),
                            View.MeasureSpec.makeMeasureSpec(1080, View.MeasureSpec.EXACTLY));
                nav.layout(0, 0, 1920, 1080);
                Shadows.shadowOf(Looper.getMainLooper()).idleFor(Duration.ofMillis(50));
            }
            invalidateAll(nav);
            PreviewPagesTest.capture(nav, "0147-settings-global-grid");
            Bitmap pixels =
                BitmapFactory.decodeFile("build/reports/preview-ui/0147-settings-global-grid.png");
            int bright = 0;
            for (int y = 35; y < 76; y++)
                for (int x = 52; x < 266; x++) {
                    int colour = pixels.getPixel(x, y);
                    if (Color.red(colour) > 180 && Color.green(colour) > 180 &&
                        Color.blue(colour) > 180)
                        bright++;
                }
            assertTrue("Rendered SUPERNOVA brand remains visible", bright > 100);
            assertNotNull(nav.findViewWithTag("semantic:topnav.home"));
            assertNotNull(nav.findViewWithTag("semantic:topnav.network"));
            var actual =
                (VideoSettingsFragment.PrefsFragment)activity.get().getSupportFragmentManager().findFragmentById(
                    com.archos.mediacenter.video.R.id.settingsFragment);
            actual.workspace.open(1, null);
            for (int n = 0; n < 3; n++) {
                nav.measure(View.MeasureSpec.makeMeasureSpec(1920, View.MeasureSpec.EXACTLY),
                            View.MeasureSpec.makeMeasureSpec(1080, View.MeasureSpec.EXACTLY));
                nav.layout(0, 0, 1920, 1080);
                Shadows.shadowOf(Looper.getMainLooper()).idle();
            }
            invalidateAll(nav);
            PreviewPagesTest.capture(nav, "0147-settings-global-category");
            assertNotNull(nav.findViewWithTag("semantic:topnav.settings"));
        } finally {
            activity.pause().stop().destroy();
        }
    }
}
