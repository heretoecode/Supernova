package com.archos.mediacenter.video.leanback.settings;

import android.annotation.SuppressLint;
import android.content.*;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.*;
import android.widget.*;
import androidx.appcompat.widget.SwitchCompat;
import androidx.preference.*;
import androidx.recyclerview.widget.RecyclerView;
import com.archos.mediacenter.video.BuildConfig;
import com.archos.mediacenter.video.diagnostics.Diagnostics;
import com.archos.mediacenter.video.leanback.*;
import com.archos.mediacenter.video.foundation.FoundationAboutWorkspace;
import com.archos.mediacenter.video.player.SegmentSkippingPolicy;
import java.util.*;

/** New grid/category presentation. The preference hierarchy is a retained model, never the visible UI. */
final class SettingsWorkspace extends LinearLayout implements TopNavigation.ContentFocus {
    private final VideoSettingsFragment.PrefsFragment fragment;
    private final BackupSettingsController backup;
    private final SettingsRegistry registry;
    private final SharedPreferences prefs;
    private final FrameLayout stage;
    private final Map<String, Row> bindings = new LinkedHashMap<>();
    private final Map<Integer, String> sections = new HashMap<>();
    private final Map<String, String> anchors = new HashMap<>();
    private final List<View> cards = new ArrayList<>();
    private final List<TextView> railButtons = new ArrayList<>();
    private LinearLayout body, rail, right;
    private ScrollView rightScroll;
    private FoundationAboutWorkspace about;
    private final java.util.Map<String, Integer> aboutSelections = new java.util.HashMap<>();
    private RecyclerView.Adapter<?> observed;
    private int category = -1, gridSelection;
    private String section = "";
    private boolean queued, disposed, subtitleEnabled, rebuilding;
    private View rowAnchor;
    private final SharedPreferences.OnSharedPreferenceChangeListener preferenceObserver = (p, k) -> schedule();
    private final RecyclerView.AdapterDataObserver observer = new RecyclerView.AdapterDataObserver() {
        @Override
        public void onChanged() {
            schedule();
        }
        @Override
        public void onItemRangeChanged(int start, int count) {
            schedule();
        }
        @Override
        public void onItemRangeInserted(int start, int count) {
            schedule();
        }
        @Override
        public void onItemRangeRemoved(int start, int count) {
            schedule();
        }
    };

    static boolean enabled(Context c) {
        return BuildConfig.FOUNDATION ||
            PreferenceManager.getDefaultSharedPreferences(c).getBoolean("try_new_ui", false);
    }

    SettingsWorkspace(VideoSettingsFragment.PrefsFragment fragment, BackupSettingsController backup, Bundle state) {
        super(fragment.requireContext());
        this.fragment = fragment;
        this.backup = backup;
        registry = new SettingsRegistry(getContext());
        prefs = PreferenceManager.getDefaultSharedPreferences(getContext());
        setTag("settings.workspace");
        setOrientation(VERTICAL);
        setBackground(new PreviewUtilityBackground(getContext()));
        setPadding(dp(28), dp(20), dp(28), dp(27));
        setClipChildren(false);
        // Approved 2 Nov 2026: no page title on top-level pages; the top bar shows where you are.
        stage = new FrameLayout(getContext());
        LayoutParams area = new LayoutParams(-1, 0, 1);
        area.topMargin = dp(4);
        addView(stage, area);
        prefs.registerOnSharedPreferenceChangeListener(preferenceObserver);
        post(() -> {
            if (disposed)
                return;
            observed = fragment.getListView().getAdapter();
            if (observed != null)
                observed.registerAdapterDataObserver(observer);
        });
        Bundle saved = state == null ? null : state.getBundle("supernova_settings_workspace");
        int requested = saved == null
                            ? fragment.requireActivity().getIntent().getIntExtra("supernova_settings_category", -1)
                            : saved.getInt("category", -1);
        String requestedSection =
            saved == null ? fragment.requireActivity().getIntent().getStringExtra("supernova_settings_section")
                          : saved.getString("section");
        if (saved != null) {
            gridSelection = saved.getInt("grid", 0);
            for (int i = 0; i < 8; i++)
                if (saved.containsKey("section_" + i))
                    sections.put(i, saved.getString("section_" + i));
            String anchor = saved.getString("row");
            if (anchor != null && requested >= 0)
                anchors.put(requested + ":" + requestedSection, anchor);
        }
        String legacy = fragment.requireActivity().getIntent().getStringExtra("preview_settings_category");
        if (fragment.requireActivity().getIntent().getBooleanExtra("show_streaming_settings", false) ||
            "Streaming".equals(legacy))
            requested = 4;
        else if (legacy != null) {
            if (Arrays.asList("Playback", "Video").contains(legacy))
                requested = 0;
            else if (legacy.equals("Audio"))
                requested = 1;
            else if (legacy.equals("Subtitles"))
                requested = 2;
            else if (Arrays.asList("Home", "Library & Metadata", "Library").contains(legacy))
                requested = 3;
            else if (Arrays.asList("Network", "Integrations").contains(legacy))
                requested = 4;
            else if (Arrays.asList("Advanced", "Appearance").contains(legacy))
                requested = 5;
            else if (legacy.equals("About"))
                requested = 7;
        }
        fragment.requireActivity().getIntent().removeExtra("preview_settings_category");
        fragment.requireActivity().getIntent().removeExtra("show_streaming_settings");
        // 0.158 (page sheet SETTINGS.md): no landing tiles; Settings opens on Playback, or the tab last used.
        if (requested == 6 || requested < 0 || requested > 7)
            requested = 0;
        open(requested, requestedSection);
    }

    @Override
    public boolean focusFromNavigation() {
        if (category < 0)
            return !cards.isEmpty() && cards.get(Math.max(0, Math.min(7, gridSelection))).requestFocus();
        return selectedRail().requestFocus();
    }

    boolean atNavigationBoundary() {
        View focused = findFocus();
        if (category < 0)
            return cards.indexOf(focused) >= 0 && cards.indexOf(focused) < 4;
        // Up from any tab reaches the top bar (tabs are the top row of every Settings page).
        if (railButtons.contains(focused))
            return true;
        return false;
    }

    private float scale() {
        return Math.max(.5f, Math.min(1.25f, Math.min(getResources().getConfiguration().screenWidthDp / 960f,
                                                      getResources().getConfiguration().screenHeightDp / 540f)));
    }

    private int dp(float value) {
        return Math.max(1, Math.round(value * scale() * getResources().getDisplayMetrics().density));
    }

    private TextView text(String value, int size) {
        TextView v = SharedThreePanel.text(getContext(), value, size);
        v.setTextSize(size * scale());
        v.setIncludeFontPadding(false);
        return v;
    }

    private void schedule() {
        if (queued || disposed)
            return;
        queued = true;
        post(() -> {
            queued = false;
            if (!disposed && category >= 0) {
                if (section.equals("OpenSubtitles") &&
                    subtitleEnabled != prefs.getBoolean("supernova_integration_opensubtitles", true))
                    select(section);
                else if (!special())
                    renderRows();
            }
        });
    }

    private boolean special() {
        return category == 7 || section.equals("Backup & restore") || section.equals("OpenSubtitles");
    }

    private void clearPage() {
        if (backup.isVisible())
            backup.hide();
        if (about != null) {
            about.hide();
            about = null;
        }
        bindings.clear();
        railButtons.clear();
        stage.removeAllViews();
    }

    private void remember() {
        if (category < 0)
            return;
        View focused = findFocus();
        if (focused != null && focused.getTag() instanceof String)
            anchors.put(category + ":" + section, (String)focused.getTag());
        sections.put(category, section);
    }

    void showGrid() {
        remember();
        clearPage();
        category = -1;
        section = "";
        cards.clear();
        fragment.requireActivity().getIntent().putExtra("supernova_settings_category", -1);
        ScrollView scroll = new ScrollView(getContext());
        scroll.setFillViewport(true);
        scroll.setClipChildren(false);
        scroll.setClipToPadding(false);
        scroll.setVerticalScrollBarEnabled(false);
        LinearLayout grid = new LinearLayout(getContext()) {
            @Override
            protected void onMeasure(int w, int h) {
                int available = MeasureSpec.getSize(h);
                int minimum = getResources().getConfiguration().fontScale > 1.2f
                                  ? dp(500 * getResources().getConfiguration().fontScale)
                                  : available;
                super.onMeasure(w, MeasureSpec.makeMeasureSpec(Math.max(available, minimum), MeasureSpec.EXACTLY));
            }
        };
        grid.setTag("settings.grid");
        grid.setOrientation(VERTICAL);
        grid.setClipChildren(false);
        scroll.addView(grid, new ScrollView.LayoutParams(-1, -1));
        stage.addView(scroll, new FrameLayout.LayoutParams(-1, -1));
        for (int r = 0; r < 2; r++) {
            LinearLayout line = new LinearLayout(getContext());
            line.setBaselineAligned(false);
            line.setClipChildren(false);
            LayoutParams rowSize = new LayoutParams(-1, 0, 1);
            if (r == 0)
                rowSize.bottomMargin = dp(12);
            grid.addView(line, rowSize);
            for (int col = 0; col < 4; col++) {
                final int index = r * 4 + col;
                // Shared landing tile (Settings and Network & Files use the same one).
                LinearLayout card = LandingTile.create(getContext(), SettingsRegistry.ICONS[index],
                                                       SettingsRegistry.LABELS[index],
                                                       SettingsRegistry.DESCRIPTIONS[index], scale());
                card.setTag("settings.grid." + index);
                LayoutParams size = new LayoutParams(0, -1, 1);
                if (col < 3)
                    size.rightMargin = dp(12);
                line.addView(card, size);
                cards.add(card);
                card.setOnClickListener(v -> open(index, null));
                card.setOnFocusChangeListener((v, yes) -> {
                    if (yes)
                        gridSelection = index;
                });
                card.setOnKeyListener((v, key, event) -> {
                    if (event.getAction() != KeyEvent.ACTION_DOWN)
                        return false;
                    int next = index;
                    if (key == KeyEvent.KEYCODE_DPAD_LEFT && index % 4 > 0)
                        next--;
                    else if (key == KeyEvent.KEYCODE_DPAD_RIGHT && index % 4 < 3)
                        next++;
                    else if (key == KeyEvent.KEYCODE_DPAD_UP && index >= 4)
                        next -= 4;
                    else if (key == KeyEvent.KEYCODE_DPAD_DOWN && index < 4)
                        next += 4;
                    else if (key != KeyEvent.KEYCODE_DPAD_LEFT && key != KeyEvent.KEYCODE_DPAD_RIGHT &&
                             key != KeyEvent.KEYCODE_DPAD_UP && key != KeyEvent.KEYCODE_DPAD_DOWN)
                        return false;
                    cards.get(next).requestFocus();
                    return true;
                });
            }
        }
        cards.get(Math.max(0, Math.min(7, gridSelection))).requestFocus();
    }

    void open(int index, String requestedSection) {
        remember();
        clearPage();
        category = index;
        gridSelection = index;
        List<String> names = registry.sections(index);
        section = requestedSection != null && names.contains(requestedSection) && index != 7
                      ? requestedSection
                      : ALL;
        if (index == 7)
            section = requestedSection != null && names.contains(requestedSection) ? requestedSection : names.get(0);
        // 0.158 (page sheet SETTINGS.md): a full page with tabs, like Files; no tiles, no panel, no page title.
        LinearLayout panel = new LinearLayout(getContext());
        panel.setTag("settings.category");
        panel.setOrientation(VERTICAL);
        panel.setBackground(null);
        panel.setPadding(dp(14), dp(11), dp(14), dp(8));
        stage.addView(panel, new FrameLayout.LayoutParams(-1, -1));
        rail = new LinearLayout(getContext());
        rail.setTag("settings.tabs");
        rail.setGravity(Gravity.CENTER_VERTICAL);
        LayoutParams railSize = new LayoutParams(-1, -2);
        railSize.bottomMargin = dp(13);
        panel.addView(rail, railSize);
        right = new LinearLayout(getContext());
        right.setTag("settings.content");
        right.setOrientation(VERTICAL);
        panel.addView(right, new LayoutParams(-1, 0, 1));
        for (int t = 0; t < SettingsLayout.TABS.length; t++) {
            final int tabCategory = SettingsLayout.TABS[t];
            String name = SettingsLayout.TAB_NAMES[t];
            TextView tab = text(name, 12);
            tab.setTag("settings.tab." + tabCategory);
            PreviewIcon.apply(tab, SettingsLayout.TAB_ICONS[t], Math.round(14 * scale()));
            tab.setFocusable(true);
            tab.setFocusableInTouchMode(true);
            tab.setSingleLine(true);
            SupernovaStyle.applyButton(tab, SupernovaStyle.Surface.STANDARD);
            tab.setPadding(dp(11), 0, dp(11), 0);
            tab.setGravity(Gravity.CENTER_VERTICAL);
            tab.setMinHeight(dp(SupernovaStyle.SMALL_BOX_HEIGHT_DP));
            LayoutParams size = new LayoutParams(-2, dp(SupernovaStyle.SMALL_BOX_HEIGHT_DP));
            if (!railButtons.isEmpty())
                size.leftMargin = dp(6);
            rail.addView(tab, size);
            railButtons.add(tab);
            tab.setOnFocusChangeListener((v, yes) -> {
                // Moving along the tabs switches the page at once (RULES §4); a rebuild passing over is ignored.
                if (yes && !rebuilding && category != tabCategory)
                    switchTab(tabCategory);
                post(this::paintRail);
            });
            tab.setOnClickListener(v -> {
                if (category != tabCategory)
                    switchTab(tabCategory);
                enter();
            });
            tab.setOnKeyListener((v, key, event) -> {
                if (event.getAction() != KeyEvent.ACTION_DOWN)
                    return false;
                if (key == KeyEvent.KEYCODE_DPAD_DOWN) {
                    enter();
                    return true;
                }
                if (key == KeyEvent.KEYCODE_DPAD_LEFT || key == KeyEvent.KEYCODE_DPAD_RIGHT) {
                    int n = railButtons.indexOf(tab) + (key == KeyEvent.KEYCODE_DPAD_LEFT ? -1 : 1);
                    if (n >= 0 && n < railButtons.size())
                        railButtons.get(n).requestFocus();
                    return true;
                }
                return false;
            });
        }
        select(section);
        selectedRail().requestFocus();
    }

    private TextView selectedRail() {
        for (TextView v : railButtons)
            if (("settings.tab." + category).equals(v.getTag()))
                return v;
        return railButtons.get(0);
    }

    /** Shows another tab's rows; the tab row stays as it is so focus never moves. */
    private void switchTab(int tabCategory) {
        remember();
        category = tabCategory;
        gridSelection = tabCategory;
        List<String> names = registry.sections(tabCategory);
        select(tabCategory == 7 ? names.get(0) : ALL);
    }

    /**
     * The tabs follow the 0.158 top bar (PS1 / DT2; page sheet): the white box where the remote is; once focus
     * goes down, the open tab keeps a soft grey box with a faded word; no underlines.
     */
    private void paintRail() {
        boolean inRail = rail != null && rail.hasFocus();
        for (TextView v : railButtons) {
            boolean open = ("settings.tab." + category).equals(v.getTag());
            v.setSelected(false);
            if (v.isFocused()) {
                SupernovaStyle.applyButton(v, SupernovaStyle.Surface.STANDARD);
                v.setBackground(SupernovaStyle.focusFill(getContext(), SupernovaStyle.SMALL_BOX_RADIUS_DP));
            } else if (open && !inRail) {
                v.setBackground(SupernovaStyle.rounded(getContext(), 0x26FFFFFF, SupernovaStyle.SMALL_BOX_RADIUS_DP));
                v.setTextColor(0xB8FFFFFF);
                SupernovaStyle.inkIcons(v, 0xB8FFFFFF);
            } else {
                v.setBackground(null);
                v.setTextColor(SupernovaStyle.buttonContent());
                SupernovaStyle.tintIcons(v);
            }
        }
    }

    private void select(String name) {
        remember();
        if (backup.isVisible())
            backup.hide();
        if (about != null) {
            about.hide();
            about = null;
        }
        section = name;
        sections.put(category, name);
        bindings.clear();
        right.removeAllViews();
        fragment.requireActivity()
            .getIntent()
            .putExtra("supernova_settings_category", category)
            .putExtra("supernova_settings_section", section);
        paintRail();
        if (category == 7) {
            LinearLayout shadowRail = new LinearLayout(getContext()), host = new LinearLayout(getContext());
            right.addView(host, new LayoutParams(-1, -1));
            about = new FoundationAboutWorkspace(getContext(), host, new View(getContext()), new View(getContext()),
                                                 shadowRail);
            about.useSelections(aboutSelections);
            about.useSingleContent(() -> selectedRail().requestFocus());
            TextView parent = text("App information", 18);
            shadowRail.addView(parent);
            about.show(parent);
            int position = registry.sections(7).indexOf(section);
            if (position > 0)
                about.openSection(position - 1);
            return;
        }
        if (section.equals("Backup & restore")) {
            backup.attachTwoColumn(right);
            backup.show(selectedRail());
            return;
        }
        rightScroll = new ScrollView(getContext());
        rightScroll.setFillViewport(false);
        rightScroll.setVerticalScrollBarEnabled(false);
        body = new LinearLayout(getContext());
        body.setOrientation(VERTICAL);
        rightScroll.addView(body);
        right.addView(rightScroll, new LayoutParams(-1, 0, 1));
        if (section.equals("OpenSubtitles")) {
            subtitleEnabled = prefs.getBoolean("supernova_integration_opensubtitles", true);
            if (!prefs.getBoolean("supernova_integration_opensubtitles", true)) {
                body.addView(
                    text("OpenSubtitles is disabled. Enable it in Service integrations to use the account form.", 16));
                return;
            }
            TextView information = text("", 15);
            LinearLayout form = new LinearLayout(getContext());
            form.setOrientation(VERTICAL);
            body.addView(form);
            body.addView(information);
            new InlineOpenSubtitles(getContext(), form, information);
            return;
        }
        renderRows();
    }


    private String subsectionIcon(String name) {
        switch (name) {
            case "Playback": return "play";
            case "General": return "settings.gear";
            case "Resume": return "resume";
            case "Picture": return "tv";
            case "Language": return "audio";
            case "Sound": return "audio";
            case "When To Show": return "sub";
            case "Languages": return "sub";
            case "Downloading": return "sub";
            case "Default Appearance": return "edit";
            case "Your Library": return "library";
            case "Details": return "details";
            case "Home Hero": return "home";
            case "Viewing History": return "history";
            case "Where To Watch": return "streaming";
            case "Ratings": return "details";
            case "Resume & Progress": return "resume";
            case "Skipping": return "next";
            case "Video & Display": return "tv";
            case "Audio output & tracks": return "audio";
            case "Subtitles": return "sub";
            case "Subtitle style": return "edit";
            case "Library": return "library";
            case "Library & metadata": return "details";
            case "Home": return "home";
            case "Watch history": return "history";
            case "Streaming services": return "streaming";
            case "Interface & theme": return "appearance";
            case "Service integrations": return "integrations";
            case "Trakt": return "trakt";
            case "OpenSubtitles": return "sub";
            case "Backup & restore": return "save";
            case "Diagnostics": return "health";
            case "Advanced": return "advanced";
            default: return "info";
        }
    }

    private boolean integrationDisabled() {
        return integrationDisabled(section);
    }

    private boolean integrationDisabled(String section) {
        return section.equals("Trakt") && !prefs.getBoolean("supernova_integration_trakt", true) ||
            (section.equals("IntroDB") || section.equals("Skipping")) &&
                !prefs.getBoolean(SegmentSkippingPolicy.MASTER, true);
    }

    private void refreshStoredValues() {
        // External restores can update SharedPreferences while the native model stays alive.
        // Setters reconcile model/dependency state, but never replay change/action listeners.
        PreferenceScreen screen = fragment.getPreferenceScreen();
        for (SettingsRegistry.Entry entry : registry.entries) {
            Preference p = entry.resolve(screen);
            if (p == null || !p.isPersistent() || p.getKey() == null || p.getPreferenceDataStore() != null)
                continue;
            SharedPreferences store = p.getSharedPreferences();
            if (store == null || !store.contains(p.getKey()))
                continue;
            Object value = store.getAll().get(p.getKey());
            if (p instanceof TwoStatePreference && value instanceof Boolean)
                ((TwoStatePreference)p).setChecked((Boolean)value);
            else if (p instanceof ListPreference && value instanceof String)
                ((ListPreference)p).setValue((String)value);
            else if (p instanceof MultiSelectListPreference && value instanceof Set &&
                     !((MultiSelectListPreference)p).getValues().equals(value))
                ((MultiSelectListPreference)p).setValues(new HashSet<>((Set<String>)value));
            else if (p instanceof EditTextPreference && value instanceof String &&
                     !Objects.equals(((EditTextPreference)p).getText(), value))
                ((EditTextPreference)p).setText((String)value);
        }
        TwoStatePreference inverse = screen.findPreference("subtitles_hide_default"),
                           positive = screen.findPreference("preview_subtitles_by_default");
        if (inverse != null && positive != null) {
            positive.setChecked(!inverse.isChecked());
            for (String key : new String[] {"favSubLang", "subtitles_show_full_for_local_audio"}) {
                Preference dependent = screen.findPreference(key);
                if (dependent != null) {
                    dependent.setEnabled(!inverse.isChecked());
                    dependent.setSelectable(!inverse.isChecked());
                }
            }
        }
    }

    private void renderRows() {
        if (body == null || special())
            return;
        refreshStoredValues();
        // 0.158 page sheet: a tab shows every one of its sections, each under its heading.
        List<String> shownSections = ALL.equals(section) ? registry.sections(category)
                                                          : java.util.Collections.singletonList(section);
        List<Object> items = new ArrayList<>();
        List<SettingsRegistry.Entry> candidates = new ArrayList<>();
        for (String s : shownSections) {
            boolean link = s.equals("Backup & restore") || s.equals("OpenSubtitles");
            List<SettingsRegistry.Entry> found = new ArrayList<>();
            // The page sheet has no master switches for services: only "Look up skip times online" stays, and with
            // it off the skipping choices hide while the switch itself stays, so it can be turned back on.
            boolean skippingOff = s.equals("Skipping") && !prefs.getBoolean(SegmentSkippingPolicy.MASTER, true);
            if (!link)
                for (SettingsRegistry.Entry e : registry.in(category, s)) {
                    Preference p = e.resolve(fragment.getPreferenceScreen());
                    if (skippingOff && !e.key.equals(SegmentSkippingPolicy.MASTER))
                        continue;
                    if (SettingsRegistry.visible(p, e.integration()))
                        found.add(e);
                }
            boolean block = s.equals("Default Appearance");
            if (found.isEmpty() && !link && !block)
                continue;
            if (ALL.equals(section))
                items.add(new Heading(s));
            if (block)
                items.add(new StyleBlock());
            if (link)
                items.add(new Link(s));
            items.addAll(found);
            candidates.addAll(found);
        }
        List<String> ids = new ArrayList<>();
        for (SettingsRegistry.Entry e : candidates)
            ids.add(e.id);
        boolean same = ids.equals(new ArrayList<>(bindings.keySet()));
        if (same)
            for (SettingsRegistry.Entry e : candidates)
                if (bindings.get(e.id).preference != e.resolve(fragment.getPreferenceScreen())) {
                    same = false;
                    break;
                }
        if (!same || bindings.isEmpty()) {
            View focused = body.findFocus();
            int focusedIndex = new ArrayList<>(bindings.values()).indexOf(focused);
            String anchor = focused == null ? anchors.get(category + ":" + section) : String.valueOf(focused.getTag());
            Map<String, Row> previous = new LinkedHashMap<>(bindings);
            bindings.clear();
            rebuilding = true;
            body.removeAllViews();
            boolean first = true;
            for (Object item : items) {
                if (item instanceof Heading) {
                    body.addView(heading(((Heading) item).name, first));
                    first = false;
                } else if (item instanceof StyleBlock) {
                    // The player's own Subtitle Appearance rows, one for one (page sheet: Default Appearance).
                    body.addView(new SubtitleStyleBlock(getContext()), new LayoutParams(-1, -2));
                } else if (item instanceof Link) {
                    body.addView(linkRow(((Link) item).name), new LayoutParams(-1, -2));
                } else {
                    SettingsRegistry.Entry e = (SettingsRegistry.Entry) item;
                    Preference p = e.resolve(fragment.getPreferenceScreen());
                    Row row = previous.get(e.id);
                    if (row == null || row.preference != p)
                        row = new Row(e, p);
                    bindings.put(e.id, row);
                    body.addView(row, new LayoutParams(-1, -2));
                }
            }
            if (items.isEmpty())
                body.addView(text(
                    integrationDisabled(section)
                        ? "This service is disabled. Enable it in Service integrations to use its options."
                        : "No additional options apply to this device or account. Existing saved values are retained.",
                    16));
            if (anchor != null) {
                View restore = body.findViewWithTag(anchor);
                if (focused != null) {
                    if (restore != null)
                        restore.requestFocus();
                    else if (!bindings.isEmpty()) {
                        List<Row> remaining = new ArrayList<>(bindings.values());
                        remaining.get(Math.max(0, Math.min(focusedIndex, remaining.size() - 1))).requestFocus();
                    } else
                        selectedRail().requestFocus();
                }
            }
            rebuilding = false;
        }
        for (Row row : bindings.values())
            row.refresh();
    }

    /**
     * B1 (0.158.8): every row of the open tab in the order it is drawn, preference rows, the Backup and OpenSubtitles
     * rows and the subtitle style block alike. Before 0.158.8 Up / Down walked the preference rows only, so the
     * Backup row on System could never be reached.
     */
    List<View> verticalOrder() {
        List<View> order = new ArrayList<>();
        if (body == null)
            return order;
        for (int i = 0; i < body.getChildCount(); i++) {
            View child = body.getChildAt(i);
            if (child.getVisibility() != View.VISIBLE)
                continue;
            if (child.isFocusable() || child instanceof ViewGroup && child.hasFocusable())
                order.add(child);
        }
        return order;
    }

    private boolean moveVertically(View from, boolean up) {
        List<View> order = verticalOrder();
        int n = order.indexOf(from) + (up ? -1 : 1);
        if (n >= 0 && n < order.size())
            order.get(n).requestFocus(up ? View.FOCUS_UP : View.FOCUS_DOWN);
        else if (n < 0)
            selectedRail().requestFocus();
        return true;
    }

    /** D5 (0.158.8): the focused row is always wholly on screen, with a little room above and below it. */
    void reveal(View row) {
        ScrollView scroll = rightScroll;
        if (scroll == null || row == null)
            return;
        scroll.post(() -> {
            if (!row.isAttachedToWindow() || row.getParent() == null || scroll.getHeight() == 0)
                return;
            android.graphics.Rect bounds = new android.graphics.Rect();
            row.getDrawingRect(bounds);
            try {
                scroll.offsetDescendantRectToMyCoords(row, bounds);
            } catch (IllegalArgumentException notInside) {
                return;
            }
            int margin = dp(24);
            int top = scroll.getScrollY();
            int bottom = top + scroll.getHeight();
            if (bounds.top - margin < top)
                scroll.scrollTo(0, Math.max(0, bounds.top - margin));
            else if (bounds.bottom + margin > bottom)
                scroll.scrollTo(0, bounds.bottom + margin - scroll.getHeight());
        });
    }

    private static final class Heading {
        final String name;

        Heading(String name) {
            this.name = name;
        }
    }

    private static final class StyleBlock {
    }

    private static final class Link {
        final String name;

        Link(String name) {
            this.name = name;
        }
    }

    /**
     * A section heading with its icon (RULES §5): clearly closer to its own rows (26 px) than to the section above
     * (64 px); never a line.
     */
    private View heading(String name, boolean first) {
        TextView heading = text(SettingsLayout.label(name), 12);
        heading.setTag("settings.heading." + name);
        heading.setTextColor(SupernovaStyle.HEADING);
        heading.setTypeface(SupernovaStyle.LABEL);
        PreviewIcon.apply(heading, subsectionIcon(name), Math.round(14 * scale()));
        SupernovaStyle.inkIcons(heading, SupernovaStyle.HEADING);
        heading.setCompoundDrawablePadding(dp(7));
        heading.setFocusable(false);
        heading.setPadding(dp(8), first ? dp(4) : dp(32), dp(8), dp(13));
        return heading;
    }

    /** A row that opens a view of its own (Backup, OpenSubtitles): its name, a plain second line and ›. */
    private View linkRow(String name) {
        boolean backupRow = name.equals("Backup & restore");
        TextView row = text((backupRow ? "Back up and restore" : "OpenSubtitles") + "   ›", 14);
        row.setTag("settings.link." + name);
        row.setFocusable(true);
        row.setFocusableInTouchMode(true);
        row.setGravity(Gravity.CENTER_VERTICAL);
        row.setMinHeight(dp(44));
        row.setPadding(dp(12), 0, dp(12), 0);
        SupernovaStyle.applyButton(row, SupernovaStyle.Surface.STANDARD);
        row.setBackground(SupernovaStyle.focusFill(getContext(), SupernovaStyle.SMALL_BOX_RADIUS_DP));
        PreviewIcon.apply(row, subsectionIcon(name), Math.round(16 * scale()));
        SupernovaStyle.tintIcons(row);
        row.setOnClickListener(v -> {
            remember();
            select(name);
            enter();
        });
        // B1 (0.158.8): Up / Down reach this row like any other (Backup was skipped from General to Diagnostics).
        row.setOnKeyListener((v, key, event) -> {
            if (event.getAction() != KeyEvent.ACTION_DOWN)
                return false;
            if (key == KeyEvent.KEYCODE_DPAD_UP || key == KeyEvent.KEYCODE_DPAD_DOWN)
                return moveVertically(row, key == KeyEvent.KEYCODE_DPAD_UP);
            return false;
        });
        row.setOnFocusChangeListener((v, yes) -> {
            if (yes)
                reveal(row);
        });
        return row;
    }

    /**
     * The page sheet's names for a list's choices (SETTINGS.md), in the sheet's order. Only the names and order
     * change; every value stays, so saved choices and backups are untouched. Skipped if the values differ.
     */
    static void useSheetNames(ListPreference list, String key) {
        String[][] sheet = SettingsLayout.choices(key);
        CharSequence[] values = list.getEntryValues();
        if (sheet == null || values == null || values.length != sheet[0].length)
            return;
        java.util.Set<String> have = new java.util.HashSet<>();
        for (CharSequence value : values)
            have.add(value.toString());
        if (!have.containsAll(Arrays.asList(sheet[0])))
            return;
        list.setEntryValues(sheet[0]);
        list.setEntries(sheet[1]);
    }

    /** Short choices are shown inline as a segmented control and changed with Left/Right (owner rule: ≤ ~5). */
    static boolean inline(ListPreference list) {
        CharSequence[] entries = list.getEntries();
        if (entries == null || entries.length < 2 || entries.length > 5)
            return false;
        int total = 0;
        for (CharSequence entry : entries) {
            // "Skip when binge-watching" (24 letters) is the longest chip name in the sheet.
            if (entry == null || entry.length() > 24)
                return false;
            total += entry.length();
        }
        return total <= 44;
    }

    private final class Row extends LinearLayout {
        final SettingsRegistry.Entry entry;
        final Preference preference;
        final TextView title, description, value;
        final LinearLayout segments;
        final SwitchCompat toggle;

        // Intentional bridge to the pinned AndroidX 1.2.1 model: preserve its native click/dependency/action semantics.
        @SuppressLint("RestrictedApi")
        Row(SettingsRegistry.Entry entry, Preference preference) {
            super(SettingsWorkspace.this.getContext());
            this.entry = entry;
            this.preference = preference;
            // Everything in a row sits on the row's centre line (RULES; 0.157 Shield review #2): the title with its
            // explanation under it on the left, the value, choices or switch centred on the right.
            setOrientation(HORIZONTAL);
            setGravity(Gravity.CENTER_VERTICAL);
            setPadding(dp(11), dp(8), dp(11), dp(8));
            setMinimumHeight(Math.max(dp(52), Math.round(48 * getResources().getDisplayMetrics().density)));
            setFocusable(true);
            setFocusableInTouchMode(true);
            setTag("settings.preference." + entry.actualKey(getContext()));
            Diagnostics.semantic(this, "settings:preference:" + entry.actualKey(getContext()));
            setBackground(SupernovaStyle.rowFocus(getContext()));
            SharedThreePanel.rowSeparator(this);
            LinearLayout words = new LinearLayout(getContext());
            words.setOrientation(VERTICAL);
            words.setGravity(Gravity.CENTER_VERTICAL);
            title = text("", 13);
            title.setTypeface(SupernovaStyle.LABEL);
            words.addView(title, new LayoutParams(-1, -2));
            addView(words, new LayoutParams(0, -2, 1));
            LinearLayout line = new LinearLayout(getContext());
            line.setGravity(Gravity.CENTER_VERTICAL);
            value = text("", 12);
            value.setTag("settings.value");
            value.setTextColor(0xFFDFE5F1);
            value.setGravity(Gravity.RIGHT | Gravity.CENTER_VERTICAL);
            value.setPadding(dp(12), 0, 0, 0);
            value.setMaxWidth(dp(220));
            value.setSingleLine(true);
            value.setEllipsize(TextUtils.TruncateAt.END);
            line.addView(value, new LayoutParams(-2, -2));
            segments = new LinearLayout(getContext());
            segments.setTag("settings.segments");
            segments.setGravity(Gravity.CENTER_VERTICAL);
            segments.setPadding(dp(2), dp(2), dp(2), dp(2));
            segments.setBackground(SupernovaStyle.rounded(getContext(), 0xFF1B1D22, 9));
            LayoutParams segmentsSize = new LayoutParams(-2, -2);
            segmentsSize.leftMargin = dp(12);
            line.addView(segments, segmentsSize);
            toggle = new SwitchCompat(getContext());
            toggle.setTag("settings.switch");
            toggle.setShowText(false);
            toggle.setTextOn("");
            toggle.setTextOff("");
            toggle.setFocusable(false);
            toggle.setClickable(false);
            toggle.setImportantForAccessibility(IMPORTANT_FOR_ACCESSIBILITY_NO);
            LayoutParams toggleSize = new LayoutParams(-2, dp(30));
            toggleSize.leftMargin = dp(12);
            line.addView(toggle, toggleSize);
            PreviewSettings.styleSwitch(toggle);
            addView(line, new LayoutParams(-2, -2));
            description = text("", 10);
            description.setTextColor(SupernovaStyle.TEXT_SECONDARY);
            description.setPadding(0, dp(3), dp(4), 0);
            words.addView(description, new LayoutParams(-1, -2));
            setOnClickListener(v -> {
                if (!preference.isEnabled() || !preference.isSelectable())
                    return;
                if (preference instanceof ListPreference && inline((ListPreference)preference))
                    return; // inline choices change with Left/Right
                if (!PreviewPreferenceDialogs.show(fragment, preference))
                    preference.performClick();
                refresh();
                schedule();
            });
            setOnFocusChangeListener((v, yes) -> {
                if (yes) {
                    anchors.put(category + ":" + section, String.valueOf(getTag()));
                    rowAnchor = this;
                    reveal(this);
                }
                refresh();
            });
            setOnKeyListener((v, key, event) -> {
                if (event.getAction() != KeyEvent.ACTION_DOWN)
                    return false;
                if (key == KeyEvent.KEYCODE_DPAD_LEFT || key == KeyEvent.KEYCODE_DPAD_RIGHT) {
                    step(key == KeyEvent.KEYCODE_DPAD_LEFT ? -1 : 1);
                    return true;
                }
                if (key == KeyEvent.KEYCODE_DPAD_UP || key == KeyEvent.KEYCODE_DPAD_DOWN)
                    return moveVertically(this, key == KeyEvent.KEYCODE_DPAD_UP);
                return false;
            });
        }

        /** Left/Right on an inline choice: picks the neighbouring value straight away (and saves it). */
        void step(int direction) {
            if (!(preference instanceof ListPreference) || !preference.isEnabled())
                return;
            ListPreference list = (ListPreference)preference;
            if (!inline(list))
                return;
            int current = Math.max(0, list.findIndexOfValue(list.getValue()));
            int next = current + direction;
            if (next < 0 || next >= list.getEntryValues().length)
                return;
            String chosen = list.getEntryValues()[next].toString();
            if (list.callChangeListener(chosen))
                list.setValue(chosen);
            refresh();
            schedule();
        }

        void refresh() {
            if (preference instanceof ListPreference)
                useSheetNames((ListPreference)preference, entry.key);
            String[] copy = SettingsLayout.copy(entry.key);
            title.setText(copy != null ? copy[0] : preference.getTitle());
            CharSequence summary = copy != null ? copy[1] : preference.getSummary();
            String help = summary == null ? "" : summary.toString();
            if (!preference.isEnabled())
                help += (help.isEmpty() ? "" : "\n") + "Unavailable for the current device or account configuration.";
            boolean state = preference instanceof TwoStatePreference;
            boolean choices = preference instanceof ListPreference && inline((ListPreference)preference);
            toggle.setVisibility(state ? VISIBLE : GONE);
            segments.setVisibility(choices ? VISIBLE : GONE);
            value.setVisibility(state || choices ? GONE : VISIBLE);
            toggle.setEnabled(preference.isEnabled());
            String spoken;
            if (state) {
                toggle.setChecked(((TwoStatePreference)preference).isChecked());
                spoken = toggle.isChecked() ? "On" : "Off";
            } else if (choices) {
                ListPreference list = (ListPreference)preference;
                bindSegments(list);
                CharSequence selected = list.getEntry();
                spoken = selected == null ? "Not set" : selected.toString();
                // The chosen value is visible in the control; a summary repeating it would say it twice.
                if (selected != null && help.contentEquals(selected))
                    help = "";
            } else if (preference instanceof ListPreference) {
                CharSequence selected = ((ListPreference)preference).getEntry();
                value.setText(selected == null ? "Not set" : selected);
                spoken = value.getText().toString();
                if (selected != null && help.contentEquals(selected))
                    help = "";
            } else if (preference instanceof MultiSelectListPreference) {
                value.setText(((MultiSelectListPreference)preference).getValues().size() + " selected");
                spoken = value.getText().toString();
            } else {
                String verb = SettingsLayout.verb(entry.key);
                value.setText(verb != null ? verb : "›");
                spoken = verb != null ? verb : "";
            }
            if ("export_diagnostic_report".equals(entry.key)) {
                String status = prefs.getString(
                    com.archos.mediacenter.video.diagnostics.DiagnosticReportSaver.STATUS, "");
                if (!status.isEmpty())
                    help = status;
            }
            description.setText(help);
            description.setVisibility(help.isEmpty() ? GONE : VISIBLE);
            setAlpha(1f);
            toggle.setAlpha(preference.isEnabled() ? 1f : .4f);
            setContentDescription(title.getText() + ". " + help + ". " + spoken);
        }

        private void bindSegments(ListPreference list) {
            CharSequence[] entries = list.getEntries();
            int chosen = list.findIndexOfValue(list.getValue());
            boolean focused = hasFocus();
            if (segments.getChildCount() != entries.length) {
                segments.removeAllViews();
                for (CharSequence label : entries) {
                    TextView option = text(label.toString(), 10);
                    option.setTypeface(SupernovaStyle.LABEL);
                    option.setSingleLine(true);
                    option.setPadding(dp(8), dp(5), dp(8), dp(5));
                    segments.addView(option, new LayoutParams(-2, -2));
                }
            }
            for (int i = 0; i < entries.length; i++) {
                TextView option = (TextView)segments.getChildAt(i);
                boolean on = i == chosen;
                option.setTag(on ? "settings.segment.on" : "settings.segment");
                option.setTextColor(on ? (focused ? SupernovaStyle.FOCUS_TEXT : SupernovaStyle.TEXT) : 0xFFAAB3C4);
                option.setBackground(on ? SupernovaStyle.rounded(getContext(),
                                                                 focused ? SupernovaStyle.FOCUS_FILL
                                                                         : SupernovaStyle.TAB_OPEN, 7)
                                        : null);
            }
        }
    }

    void enter() {
        if (backup.isVisible()) {
            backup.enter();
            return;
        }
        if (about != null) {
            about.enterContent();
            return;
        }
        String anchor = anchors.get(category + ":" + section);
        View restore = anchor == null ? null : right.findViewWithTag(anchor);
        if (restore != null && restore.isShown() && restore.requestFocus())
            return;
        for (View v : right.getFocusables(FOCUS_FORWARD))
            if (v.isShown() && v.requestFocus())
                return;
    }

    boolean back() {
        if (category < 0)
            return false;
        if (backup.isVisible() && backup.hasOperation())
            return backup.back();
        if (about != null && about.handleBack())
            return true;
        // A backup or OpenSubtitles view opened from its row goes back to the tab's rows (page sheet).
        if (!ALL.equals(section) && category != 7) {
            select(ALL);
            enter();
            return true;
        }
        // From the rows to the open tab; from the tabs to the top bar.
        if (right != null && right.hasFocus()) {
            selectedRail().requestFocus();
            return true;
        }
        return false;
    }

    /** The whole tab: every section of it, one under the other with its heading (0.158 page sheet). */
    static final String ALL = "*";

    void save(Bundle out) {
        remember();
        Bundle state = new Bundle();
        state.putInt("category", category);
        state.putString("section", section);
        state.putInt("grid", gridSelection);
        state.putString("row", anchors.get(category + ":" + section));
        for (Map.Entry<Integer, String> e : sections.entrySet())
            state.putString("section_" + e.getKey(), e.getValue());
        out.putBundle("supernova_settings_workspace", state);
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        if (category >= 0 && event.getAction() == KeyEvent.ACTION_DOWN &&
            event.getKeyCode() == KeyEvent.KEYCODE_DPAD_LEFT && right.hasFocus() && !(findFocus() instanceof Row)) {
            if (about != null && about.handleBack())
                return true;
            selectedRail().requestFocus();
            return true;
        }
        if (backup.isVisible() && backup.navigation(event))
            return true;
        return super.dispatchKeyEvent(event);
    }

    void dispose() {
        if (disposed)
            return;
        disposed = true;
        prefs.unregisterOnSharedPreferenceChangeListener(preferenceObserver);
        if (observed != null)
            observed.unregisterAdapterDataObserver(observer);
    }

    @Override
    protected void onDetachedFromWindow() {
        dispose();
        super.onDetachedFromWindow();
    }
}
