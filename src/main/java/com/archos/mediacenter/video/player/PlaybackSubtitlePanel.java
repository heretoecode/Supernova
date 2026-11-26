package com.archos.mediacenter.video.player;

import com.archos.mediacenter.video.diagnostics.FocusSearchGuard;
import android.app.Activity;
import android.content.Context;
import android.graphics.*;
import android.graphics.drawable.*;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.*;
import android.widget.*;
import com.archos.mediacenter.video.R;
import com.archos.mediacenter.video.leanback.PreviewLanguageIcon;
import com.archos.mediacenter.video.leanback.StandardSwitchDrawable;
import com.archos.mediacenter.video.leanback.SubtitlePanelStyle;
import com.archos.mediacenter.video.leanback.SupernovaStyle;
import java.util.*;

/**
 * The approved Playback Settings side panel (owner, 2 Nov 2026; see
 * docs/design/PLAYBACK_SETTINGS_SIDE_PANEL_APPROVED_2026-11-02.md).
 *
 * <p>One panel, three tabs (Subtitles, Audio, Playback), docked top-right at a third of the screen
 * width with its height fitted to the rows. No sub-menus: values change in place with Left/Right;
 * the two lists that cannot be inline (subtitle and audio tracks) open as a page inside this panel.
 * Every value comes from, and every change goes through, the {@link Backend} — the existing player
 * actions. A row the backend reports as unavailable is not shown.
 *
 * <p>All sizes are written in pixels of the approved 1920×1080 mock-up and scaled by {@link #s}.
 */
final class PlaybackSubtitlePanel extends LinearLayout {
    static final int TAB_AUDIO = 0, TAB_SUBTITLES = 1, TAB_PLAYBACK = 2;
    static final int[] COLOURS = {Color.WHITE, 0xffffc93c, 0xff19e6e6, 0xff4cc554, 0xfff06a9b};
    static final String[] COLOUR_NAMES = {"White", "Yellow", "Cyan", "Green", "Pink"};
    static final int[] OPACITY = {0, 64, 128, 191, 255};
    static final String[] OPACITY_LABELS = {"0%", "25%", "50%", "75%", "100%"};
    static final String[] OPACITY_CHIPS = {"25%", "50%", "75%", "100%"};
    static final int[] OPACITY_CHIP_VALUES = {64, 128, 191, 255};
    static final int SUBTITLE_DELAY_STEP_MS = 100;
    static final float SPEED_STEP = 0.05f;

    // Approved mock-up colours.
    private static final int TAB_BORDER = 0xff5a7bd8, TAB_ACTIVE = 0xff2f6df0, FOCUS_BORDER = 0xff7fd6ff;
    private static final int HEADING = SupernovaStyle.HEADING, HEADING_RULE = SupernovaStyle.HEADING_LINE;
    /** The faint line under the tab row (about 8% white, as on Search and Files). */
    /** The chosen option's chip in a choice row (player v10 mock-up). */
    private static final int CHIP = 0xFF3A3E45;
    private static final int VALUE = 0xffffffff, META = 0xffb9c7e8, ICON = 0xffe8eeff;
    // Round two (owner, 19 Nov 2026; player/33-v15): every size below in the mock-up's 1080p pixels, see m().
    private static final int ROW_HEIGHT = 62, LIST_ROWS_VISIBLE = 7;
    /** The faint line between two rows you can select (v15: 2 px, 6% white). */
    static final int ROW_RULE = 0x0FFFFFFF;
    /** The panel: #16181C at 90% (RULES §11). */
    static final int PANEL = SupernovaStyle.GLASS;
    /** The open tab's soft grey box and faded word while the remote is elsewhere (PS1; the top bar's look). */
    static final int TAB_OPEN_BOX = 0x26FFFFFF, TAB_OPEN_INK = 0xB8FFFFFF;
    /** One shape for every small box (RULES §3): 52 px high, 20 px corners, 22 px side padding at 1080p. */
    static final int BOX_HEIGHT = 52, BOX_RADIUS = 20, BOX_SIDE = 22;

    static final class Track {
        final String label, language;
        final boolean selected, enabled;
        final Runnable select;
        /** Plain words and pills for the track lists (owner; mock-ups track-names/01–02); empty when unknown. */
        final List<String> pills;

        Track(String label, String language, boolean selected, boolean enabled, Runnable select) {
            this(label, language, selected, enabled, select, java.util.Collections.emptyList());
        }

        Track(String label, String language, boolean selected, boolean enabled, Runnable select, List<String> pills) {
            this.label = label;
            this.language = language;
            this.selected = selected;
            this.enabled = enabled;
            this.select = select;
            this.pills = pills;
        }
    }

    /** Pills for a track: Original or Audio description (or SDH, Forced), then the format, then the channels. */
    static List<String> pills(TrackDetails details) {
        List<String> pills = new ArrayList<>();
        if (details.original())
            pills.add("Original");
        if (details.audioDescription())
            pills.add("Audio description");
        if (details.sdh())
            pills.add("SDH");
        if (details.forced())
            pills.add("Forced");
        if (!details.format().isEmpty())
            pills.add(details.format());
        if (!details.channels().isEmpty())
            pills.add(details.channels());
        return pills;
    }

    /** One value of a native single-choice menu (Format, 3D Mode, Play Mode). */
    static final class Choice {
        final String label;
        final boolean selected, enabled;
        final Runnable select;

        Choice(String label, boolean selected, boolean enabled, Runnable select) {
            this.label = label;
            this.selected = selected;
            this.enabled = enabled;
            this.select = select;
        }
    }

    /**
     * Subtitle keys: enabled, size, colour, position, outline, background, opacity, delay.
     * Defaults for the Audio and Playback tabs mean "not available here": those rows are hidden.
     * Option keys: boost, night, spatial, skip. Choice keys: format, s3d, playmode.
     */
    interface Backend {
        int get(String key);
        void set(String key, int value);
        List<Track> tracks();
        void download();
        boolean available();

        default Runnable chooseFile() {
            return null;
        }

        default List<Track> audioTracks() {
            return Collections.emptyList();
        }

        default Integer audioDelay() {
            return null;
        }

        default void stepAudioDelay(int direction) {
        }

        default Boolean option(String key) {
            return null;
        }

        default boolean optionEnabled(String key) {
            return true;
        }

        default void toggleOption(String key) {
        }

        /** Audio boost 0..3 (Off · Low · Medium · High; owner, 18 Nov 2026), or null when not offered. */
        default Integer boostLevel() {
            return null;
        }

        default void setBoostLevel(int level) {
        }

        default List<Choice> choices(String key) {
            return Collections.emptyList();
        }

        default Float speed() {
            return null;
        }

        default void setSpeed(float speed) {
        }

        default Runnable pictureInPicture() {
            return null;
        }

        /** Sleep timer and "Still watching?" (0.156 #39) are offered only while a playback service is running. */
        default boolean limitsAvailable() {
            return false;
        }

        /** Sleep timer choice 0..4 (Off, 30, 60, 90, End of episode). */
        default int sleepChoice() {
            return 0;
        }

        default void setSleepChoice(int choice) {
        }

        /** Wall-clock end of a running minutes timer, or 0. */
        default long sleepEndsAt() {
            return 0;
        }

        /** "Still watching?" after this many auto-played episodes; 0 is Off. */
        /** Minutes without the remote before "Still watching?" asks; 0 when it counts episodes or is off. */
        default int stillWatchingMinutes() {
            return 0;
        }

        /** Sets "Still watching?" to a number of episodes or of minutes (one of them 0), or Off (both 0). */
        default void setStillWatching(int episodes, int minutes) {
            setStillWatchingAfter(episodes);
        }

        /** Whether an episode is playing (its last choice reads "End of episode", otherwise "End of movie"). */
        default boolean playingEpisode() {
            return true;
        }

        default int stillWatchingAfter() {
            return 0;
        }

        default void setStillWatchingAfter(int episodes) {
        }

        /** Per-series audio/subtitle choices (0.156 #42): null when this is not a series episode. */
        default Boolean seriesChoices() {
            return null;
        }

        /** The Playback tab's save row: null when this is not an episode of a known series. */
        default Boolean seriesPlayback() {
            return null;
        }

        default void setSeriesPlayback(boolean enabled) {
        }

        default void setSeriesChoices(boolean enabled) {
        }

        default String seriesName() {
            return "";
        }

        /** Hides the real subtitles while the appearance sample is shown (0.156 #25). */
        default void appearancePreview(boolean shown) {
        }

        /** A full-screen sample line drawn with the current settings, or null when not available. */
        default Bitmap appearanceSample(String text, int width, int height) {
            return null;
        }
    }

    static final String SAMPLE_TEXT = "This is how your subtitles will look.";
    static final String SAMPLE_TAG = "playback.subtitles.sample";
    private static final java.util.Set<String> APPEARANCE_ROWS = new java.util.HashSet<>(java.util.Arrays.asList(
        "subtitles.row.size", "subtitles.row.colour", "subtitles.row.position", "subtitles.row.outline",
        "subtitles.row.background", "subtitles.row.opacity"));
    private boolean appearanceShown;
    private ImageView sample;
    private final ViewTreeObserver.OnGlobalFocusChangeListener appearanceFocus =
        (before, now) -> showAppearance(inAppearance(now));

    /**
     * Owner's preview rule (0.155 notes #35): while focus is anywhere in Subtitle appearance, the real subtitles
     * are hidden and one sample line is drawn with the current settings instead; leaving the section brings them
     * back. Subtitle Delay is not part of it.
     */
    boolean inAppearance(View focused) {
        for (View v = focused; v != null && v != this; v = v.getParent() instanceof View ? (View)v.getParent() : null)
            if (v.getTag() instanceof String && APPEARANCE_ROWS.contains(v.getTag()))
                return true;
        return false;
    }

    boolean appearanceShown() {
        return appearanceShown;
    }

    private void showAppearance(boolean shown) {
        if (closed)
            shown = false;
        if (shown == appearanceShown) {
            if (shown)
                drawSample();
            return;
        }
        appearanceShown = shown;
        backend.appearancePreview(shown);
        if (shown) {
            drawSample();
            return;
        }
        if (sample != null && sample.getParent() instanceof ViewGroup)
            ((ViewGroup)sample.getParent()).removeView(sample);
        sample = null;
    }

    private void drawSample() {
        ViewGroup content = owner.findViewById(android.R.id.content);
        if (content == null || content.getWidth() <= 0 || content.getHeight() <= 0)
            return;
        Bitmap bitmap = backend.appearanceSample(SAMPLE_TEXT, content.getWidth(), content.getHeight());
        if (bitmap == null)
            return;
        if (sample == null) {
            sample = new ImageView(owner);
            sample.setTag(SAMPLE_TAG);
            sample.setFocusable(false);
            sample.setImportantForAccessibility(IMPORTANT_FOR_ACCESSIBILITY_NO);
            // PS7 (owner, 18 Nov 2026): on top of the player at every position. Added first under the player's
            // layout, it showed only through the black bars and slid behind the picture from about 10% up.
            content.addView(sample, new FrameLayout.LayoutParams(-1, -1));
            // Keep the sample under the panel but over the picture.
            View panelRoot = this;
            while (panelRoot.getParent() instanceof View && panelRoot.getParent() != content)
                panelRoot = (View)panelRoot.getParent();
            if (panelRoot.getParent() == content) {
                content.removeView(sample);
                content.addView(sample, Math.max(0, content.indexOfChild(panelRoot)),
                                new FrameLayout.LayoutParams(-1, -1));
            }
        }
        sample.setImageBitmap(bitmap);
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        getViewTreeObserver().addOnGlobalFocusChangeListener(appearanceFocus);
    }

    @Override
    protected void onDetachedFromWindow() {
        getViewTreeObserver().removeOnGlobalFocusChangeListener(appearanceFocus);
        showAppearance(false);
        super.onDetachedFromWindow();
    }

    private final Activity owner;
    private final Backend backend;
    private final SubtitlePanelStyle style;
    private final float k;
    private final LinearLayout header, tabs, body;
    private final MaxHeightScroll scroll;
    private final TextView more;
    private final List<Runnable> bindings = new ArrayList<>();
    private int tab;
    private String page; // null on a tab; "subtitles" or "audio" while a track list is open
    private boolean closed, building;

    PlaybackSubtitlePanel(Activity owner, Backend backend, int initialTab) {
        super(owner);
        this.owner = owner;
        this.backend = backend;
        style = new SubtitlePanelStyle(owner, true, true);
        // Player v10/v11 mock-ups: row names about 28px at 1080p (21 reference px x 4/3).
        k = owner.getResources().getDisplayMetrics().widthPixels / 1920f * (4f / 3f);
        setOrientation(VERTICAL);
        setTag("subtitles.main");
        setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        // Panel colour (P6 / PS10, RULES §11): #16181C at 90%, no outline, no glow, no old blue.
        int glow = 0;
        setBackground(new Surface(PANEL, PANEL, 0, m(26), 0, 0, glow));
        setPadding(m(22), m(18), m(22), m(22));
        resting = getBackground();
        restingPadding = new int[] {getPaddingLeft(), getPaddingTop(), getPaddingRight(), getPaddingBottom()};

        header = new LinearLayout(owner);
        header.setOrientation(VERTICAL);
        addView(header, new LayoutParams(-1, -2));
        // 0.154: no "Playback Settings" title; the tabs say where you are (same structure as the More menu).

        tabs = new LinearLayout(owner);
        tabs.setBaselineAligned(false);
        String[] names = {"Subtitles", "Audio", "Playback"}, icons = {"cc", "sound", "play"};
        int[] ids = {TAB_SUBTITLES, TAB_AUDIO, TAB_PLAYBACK};
        for (int i = 0; i < ids.length; i++)
            tabs.addView(tab(i, ids[i], names[i], icons[i]), tabSize(i));
        LayoutParams tabsSize = new LayoutParams(-1, m(BOX_HEIGHT));
        // PS9 round two (owner, 19 Nov 2026): no line under the tab row, a clear gap instead, the first heading
        // straight under it.
        tabsSize.bottomMargin = m(22);
        header.addView(tabs, tabsSize);
        getViewTreeObserver().addOnGlobalFocusChangeListener((before, after) -> paintTabs());

        scroll = new MaxHeightScroll(owner);
        scroll.setVerticalScrollBarEnabled(true);
        style.scrollbar(scroll);
        scroll.setSmoothScrollingEnabled(false);
        scroll.setFocusable(false);
        body = new LinearLayout(owner);
        body.setOrientation(VERTICAL);
        scroll.addView(body);
        addView(scroll, new LayoutParams(-1, -2));
        scroll.getViewTreeObserver().addOnScrollChangedListener(this::updateMore);

        more = text("", 17, HEADING, false);
        more.setGravity(Gravity.CENTER);
        more.setPadding(0, s(10), 0, 0);
        more.setVisibility(GONE);
        more.setTag("playback.list.more");
        addView(more, new LayoutParams(-1, -2));

        tab = initialTab == TAB_AUDIO || initialTab == TAB_PLAYBACK ? initialTab : TAB_SUBTITLES;
        showTab(tab);
    }

    // ---- geometry --------------------------------------------------------------------------

    /** Mock-up pixels (1080p) to device pixels. */
    int s(float px) {
        return Math.max(1, Math.round(px * k));
    }

    /** The v15 mock-up's own 1080p pixels to device pixels (player/33-v15 is drawn at 1080p). */
    int m(float px) {
        return s(M(px));
    }

    /** The v15 mock-up's 1080p pixels in this panel's reference units (see {@link #s}). */
    static float M(float px) {
        return px * .75f;
    }

    int panelWidth() {
        return widthFor(tab);
    }

    /**
     * One size for every tab (P4, the brief's default from the owner's review of 0.157.1): about 40% of the width at
     * most, like the choice list, the height following the content with arrows for more (RULES §11, §16).
     */
    int widthFor(int id) {
        float screen = owner.getResources().getDisplayMetrics().widthPixels / 1920f;
        return Math.round(PANEL_WIDTH_1080P * screen);
    }

    /** PLAYER.md (owner, 21–22 Nov 2026; player-v16/tv/04–06): about 700 px at 1080p (was 820). */
    static final int PANEL_WIDTH_1080P = 700;

    private Window placed;

    private void fitWidth() {
        if (placed != null)
            placed.setLayout(widthFor(tab), ViewGroup.LayoutParams.WRAP_CONTENT);
    }

    int panelHeight() {
        return style.height;
    }

    void place(Window window) {
        style.placeSide(window);
        placed = window;
        fitWidth();
    }

    int currentTab() {
        return tab;
    }

    boolean pageOpen() {
        return page != null;
    }

    private int maxPanelHeight() {
        return owner.getResources().getDisplayMetrics().heightPixels - s(80);
    }

    // ---- building blocks -------------------------------------------------------------------

    private TextView text(String label, float px, int colour, boolean bold) {
        TextView v = new TextView(owner);
        v.setText(label);
        v.setTextColor(colour);
        v.setIncludeFontPadding(false);
        v.setTextSize(TypedValue.COMPLEX_UNIT_PX, px * k * owner.getResources().getConfiguration().fontScale);
        if (bold)
            v.setTypeface(Typeface.create("sans-serif-medium", Typeface.BOLD));
        return v;
    }

    private ImageView icon(String kind, float px) {
        ImageView image = new ImageView(owner);
        Drawable d = new SubtitlePanelStyle.Icon(kind);
        d.setColorFilter(new PorterDuffColorFilter(ICON, PorterDuff.Mode.SRC_IN));
        image.setImageDrawable(d);
        image.setImportantForAccessibility(IMPORTANT_FOR_ACCESSIBILITY_NO);
        image.setScaleType(ImageView.ScaleType.FIT_CENTER);
        image.setLayoutParams(new LayoutParams(s(px), s(px)));
        return image;
    }

    /** Approved global row focus: white outline, no fill change. */
    /** Focused rows fill white (P5, RULES §3); their text and icons turn black (see {@link #inkRow}). */
    private Drawable rowFocus() {
        return SupernovaStyle.focusFill(owner, 10);
    }

    private final java.util.Map<TextView, Integer> restingInk = new java.util.WeakHashMap<>();

    /** Black ink on the white box for a focused row's words and icons; their own colours again at rest (P5). */
    void inkRow(View view, boolean focused) {
        if (ARROW_TAG.equals(view.getTag()))
            view.setAlpha(focused ? 1f : 0f);
        if (view instanceof TextView && isChosenChip((TextView) view)) {
            // PS6 (RULES §3): on a focused row the chosen chip is black with white text.
            TextView chip = (TextView) view;
            chip.setBackground(SupernovaStyle.rounded(owner, focused ? SupernovaStyle.FOCUS_TEXT : CHIP,
                                                      BOX_RADIUS * k / owner.getResources().getDisplayMetrics().density));
            chip.setTextColor(Color.WHITE);
            return;
        }
        if (view instanceof TextView && "track.pill".equals(view.getTag())) {
            // PS5: a pill's outline turns black with its text on a focused row.
            Drawable edge = view.getBackground();
            if (edge instanceof android.graphics.drawable.GradientDrawable)
                ((android.graphics.drawable.GradientDrawable) edge.mutate())
                    .setStroke(Math.max(1, s(1.5f)), focused ? SupernovaStyle.FOCUS_TEXT : PILL_EDGE);
        }
        if (view instanceof ImageView
            && ((ImageView) view).getDrawable() instanceof StandardSwitchDrawable) {
            // PS4 (RULES §3): on a focused row the switch flips like the text: dark track, white knob.
            StandardSwitchDrawable face = (StandardSwitchDrawable) ((ImageView) view).getDrawable();
            if (face.onFocusedRow() != focused)
                ((ImageView) view).setImageDrawable(StandardSwitchDrawable.snapshot(owner, face.checked(), focused));
            return;
        }
        if (view instanceof TextView) {
            TextView words = (TextView) view;
            if (focused) {
                if (!restingInk.containsKey(words))
                    restingInk.put(words, words.getCurrentTextColor());
                words.setTextColor(SupernovaStyle.FOCUS_TEXT);
            } else if (restingInk.containsKey(words)) {
                words.setTextColor(restingInk.remove(words));
            }
        } else if (view instanceof ImageView) {
            Drawable d = ((ImageView) view).getDrawable();
            if (d instanceof SubtitlePanelStyle.Icon)
                d.setColorFilter(new PorterDuffColorFilter(focused ? SupernovaStyle.FOCUS_TEXT : ICON,
                                                           PorterDuff.Mode.SRC_IN));
        } else if (view instanceof ViewGroup) {
            ViewGroup group = (ViewGroup) view;
            for (int i = 0; i < group.getChildCount(); i++)
                inkRow(group.getChildAt(i), focused);
        }
    }

    private static final int PILL_EDGE = 0xAAFFFFFF;

    private static boolean isChosenChip(TextView text) {
        return "value".equals(text.getTag()) && text.getParent() instanceof View
            && "chips".equals(((View) text.getParent()).getTag());
    }

    /** The row a view sits in, if that row has the focus. */
    private boolean onFocusedRow(View view) {
        for (View at = view; at != null && at != this; at = at.getParent() instanceof View ? (View) at.getParent()
                                                                                          : null)
            if (at.getParent() == body)
                return at.isFocused();
        return false;
    }

    private LayoutParams tabSize(int index) {
        LayoutParams size = new LayoutParams(-2, -1);
        if (index > 0)
            size.leftMargin = m(12);
        return size;
    }

    private View tab(int index, int id, String name, String iconKind) {
        LinearLayout t = new LinearLayout(owner);
        t.setTag("playback.tab:" + id);
        t.setGravity(Gravity.CENTER);
        t.setFocusable(true);
        t.setFocusableInTouchMode(true);
        t.setDescendantFocusability(FOCUS_BLOCK_DESCENDANTS);
        t.setPadding(m(20), 0, m(20), 0);
        t.addView(icon(iconKind, M(28)));
        TextView label = text(name, M(28), Color.WHITE, false);
        label.setSingleLine(true);
        label.setPadding(m(10), 0, 0, 0);
        t.addView(label);
        t.setContentDescription(name + " tab");
        t.setOnFocusChangeListener((v, focused) -> {
            if (focused && !building && tab != id)
                showTab(id);
        });
        t.setOnClickListener(v -> {
            v.requestFocus();
            if (tab != id)
                showTab(id);
        });
        t.setOnKeyListener((v, key, event) -> {
            if (event.getAction() != KeyEvent.ACTION_DOWN)
                return false;
            if (key == KeyEvent.KEYCODE_DPAD_LEFT || key == KeyEvent.KEYCODE_DPAD_RIGHT) {
                int next = index + (key == KeyEvent.KEYCODE_DPAD_LEFT ? -1 : 1);
                if (next >= 0 && next < tabs.getChildCount())
                    tabs.getChildAt(next).requestFocus();
                return true;
            }
            if (key == KeyEvent.KEYCODE_DPAD_DOWN) {
                focusBody();
                return true;
            }
            return key == KeyEvent.KEYCODE_DPAD_UP;
        });
        return t;
    }

    private void styleTabs() {
        for (int i = 0; i < tabs.getChildCount(); i++) {
            View t = tabs.getChildAt(i);
            t.setSelected(("playback.tab:" + tab).equals(t.getTag()));
        }
        paintTabs();
    }

    /**
     * Focus and path on the tab row (RULES §3): the focused tab is the white box with black text and icon; the open
     * tab has a bright underline while the remote is below it and a dull one while the remote is on the tab row.
     */
    private void paintTabs() {
        if (tabs == null)
            return;
        boolean inRow = tabs.hasFocus();
        for (int i = 0; i < tabs.getChildCount(); i++) {
            ViewGroup t = (ViewGroup) tabs.getChildAt(i);
            boolean focused = t.isFocused();
            boolean open = ("playback.tab:" + tab).equals(t.getTag());
            int ink = focused ? SupernovaStyle.FOCUS_TEXT : open && !inRow ? TAB_OPEN_INK : Color.WHITE;
            for (int c = 0; c < t.getChildCount(); c++) {
                View part = t.getChildAt(c);
                if (part instanceof TextView)
                    ((TextView) part).setTextColor(ink);
                else if (part instanceof ImageView && ((ImageView) part).getDrawable() != null)
                    ((ImageView) part).getDrawable()
                        .setColorFilter(new PorterDuffColorFilter(focused || open && !inRow ? ink : ICON,
                                                                  PorterDuff.Mode.SRC_IN));
            }
            // PS1 (owner, 18 Nov 2026): the top bar's focus. White box where the remote is; once focus goes down the
            // open tab keeps a soft grey box with a faded word; no underlines.
            android.graphics.drawable.GradientDrawable box = new android.graphics.drawable.GradientDrawable();
            box.setCornerRadius(m(BOX_RADIUS));
            if (focused) {
                box.setColor(SupernovaStyle.FOCUS_FILL);
                t.setBackground(box);
            } else if (open && !inRow) {
                box.setColor(TAB_OPEN_BOX);
                t.setBackground(box);
            } else {
                t.setBackground(null);
            }
        }
    }

    /**
     * Section heading: the shared group heading (RULES §11): Title Case, centred on its line, with a line either
     * side. No lines between rows.
     */
    private void section(String title, boolean first) {
        LinearLayout heading = (LinearLayout) SupernovaStyle.groupHeading(owner, title, 11);
        heading.setImportantForAccessibility(IMPORTANT_FOR_ACCESSIBILITY_NO);
        // v15: 16 px above a heading, 4 px below it; the first heading sits straight under the tabs.
        heading.setPadding(m(4), first ? 0 : m(16), m(4), m(4));
        heading.setTag("playback.heading");
        for (int i = 0; i < heading.getChildCount(); i++) {
            View part = heading.getChildAt(i);
            if (part instanceof TextView) {
                // The words exactly as written ("Picture and Speed"), not each word capitalised.
                ((TextView) part).setText(title);
                ((TextView) part).setTextSize(TypedValue.COMPLEX_UNIT_PX,
                                              m(22) * owner.getResources().getConfiguration().fontScale);
            }
        }
        heading.setContentDescription(title);
        body.addView(heading, new LayoutParams(-1, -2));
    }

    static String sentenceCase(String title) {
        String lower = title.toLowerCase(java.util.Locale.ROOT);
        return lower.isEmpty() ? lower : Character.toUpperCase(lower.charAt(0)) + lower.substring(1);
    }

    private LinearLayout row(String tag, String iconKind, String title) {
        LinearLayout row = new LinearLayout(owner);
        row.setTag(tag);
        row.setGravity(Gravity.CENTER_VERTICAL);
        row.setMinimumHeight(Math.round(m(ROW_HEIGHT) * owner.getResources().getConfiguration().fontScale));
        row.setPadding(m(18), 0, m(18), 0);
        row.setFocusable(true);
        row.setFocusableInTouchMode(true);
        row.setDescendantFocusability(FOCUS_BLOCK_DESCENDANTS);
        row.setBackground(rowFocus());
        row.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        if (iconKind != null) {
            FrameLayout box = new FrameLayout(owner);
            ImageView image = icon(iconKind, M(28));
            box.addView(image, new FrameLayout.LayoutParams(m(28), m(28), Gravity.CENTER_VERTICAL));
            row.addView(box, new LayoutParams(m(30 + 16), -2));
        }
        TextView label = text(title, M(27), Color.WHITE, false);
        label.setSingleLine(true);
        label.setEllipsize(TextUtils.TruncateAt.END);
        row.addView(label, new LayoutParams(0, -2, 1));
        row.setContentDescription(title);
        row.setOnKeyListener((v, key, event) -> rowKeys(row, key, event, null));
        if (body.getChildCount() > 0 && isRow(body.getChildAt(body.getChildCount() - 1)))
            body.addView(rowRule(), ruleSize());
        body.addView(row, new LayoutParams(-1, -2));
        return row;
    }

    /** A row you can select (not a heading, a rule or a note). */
    private static boolean isRow(View view) {
        return view instanceof LinearLayout && view.getTag() instanceof String
            && ((String) view.getTag()).contains(".row.");
    }

    /** v15: a faint line between two rows you can select, hidden either side of the focused row. */
    private View rowRule() {
        View rule = new View(owner);
        rule.setTag(RULE_TAG);
        rule.setBackgroundColor(ROW_RULE);
        rule.setImportantForAccessibility(IMPORTANT_FOR_ACCESSIBILITY_NO);
        return rule;
    }

    static final String RULE_TAG = "playback.rule";

    /** The line sits across the two rows' edges and adds no height, so every tab still fits (v15: rows 62 px). */
    private LayoutParams ruleSize() {
        LayoutParams size = new LayoutParams(-1, Math.max(1, m(2)));
        size.topMargin = -size.height / 2;
        size.bottomMargin = -(size.height - size.height / 2);
        return size;
    }

    /** The lines either side of the focused row step aside, as in the v15 mock-up. */
    private void showRulesBeside(View row, boolean focused) {
        int at = body.indexOfChild(row);
        for (int i : new int[] {at - 1, at + 1})
            if (i >= 0 && i < body.getChildCount() && RULE_TAG.equals(body.getChildAt(i).getTag()))
                body.getChildAt(i).setVisibility(focused ? INVISIBLE : VISIBLE);
    }

    /** Left/Right is consumed by every row so focus never wanders sideways out of the panel. */
    private boolean rowKeys(View row, int key, KeyEvent event, Stepper stepper) {
        if (event.getAction() != KeyEvent.ACTION_DOWN)
            return false;
        if (key == KeyEvent.KEYCODE_DPAD_LEFT || key == KeyEvent.KEYCODE_DPAD_RIGHT) {
            if (stepper != null && row.isEnabled()) {
                stepper.step(key == KeyEvent.KEYCODE_DPAD_LEFT ? -1 : 1);
                refresh();
            }
            return true;
        }
        if (key == KeyEvent.KEYCODE_DPAD_UP || key == KeyEvent.KEYCODE_DPAD_DOWN) {
            View next = FocusSearchGuard.findNextFocus(
                body, row, key == KeyEvent.KEYCODE_DPAD_UP ? FOCUS_UP : FOCUS_DOWN);
            if (next == null || next == row) {
                if (key == KeyEvent.KEYCODE_DPAD_UP)
                    focusTop();
                return true;
            }
        }
        return false;
    }

    interface Stepper {
        void step(int direction);
    }

    private TextView chevron(String c) {
        TextView v = text(c, M(26), Color.WHITE, false);
        v.setGravity(Gravity.CENTER);
        return v;
    }

    /**
     * A named choice that steps in place (owner, 19 Nov 2026; player/33-v15): the value alone at rest, small ‹ ›
     * either side only on the focused row. Left / Right step; OK steps on, going round to the first after the last.
     */
    private TextView cycler(LinearLayout row, Stepper stepper, java.util.function.IntSupplier index,
                            java.util.function.IntSupplier count) {
        LinearLayout box = new LinearLayout(owner);
        box.setTag("cycle");
        box.setGravity(Gravity.CENTER_VERTICAL);
        TextView before = chevron("‹");
        TextView after = chevron("›");
        before.setTag(ARROW_TAG);
        after.setTag(ARROW_TAG);
        before.setAlpha(0f);
        after.setAlpha(0f);
        TextView value = text("", M(26), VALUE, false);
        value.setSingleLine(true);
        value.setEllipsize(TextUtils.TruncateAt.END);
        value.setGravity(Gravity.CENTER);
        value.setMaxWidth(m(360));
        value.setTag("value");
        LayoutParams v = new LayoutParams(-2, -2);
        v.leftMargin = m(14);
        v.rightMargin = m(14);
        box.addView(before);
        box.addView(value, v);
        box.addView(after);
        LayoutParams boxSize = new LayoutParams(-2, -2);
        boxSize.leftMargin = m(16);
        row.addView(box, boxSize);
        row.setOnKeyListener((view, key, event) -> rowKeys(row, key, event, stepper));
        row.setOnClickListener(view -> {
            if (!row.isEnabled())
                return;
            int n = count.getAsInt();
            if (index.getAsInt() >= n - 1)
                stepper.step(-Math.max(1, n));
            else
                stepper.step(1);
            refresh();
        });
        return value;
    }

    static final String ARROW_TAG = "cycle.arrow";

    /**
     * A row that opens a list (RULES §8; PS11): its current value, the value's pills close beside it, then ›.
     * Left / Right still change the value in place.
     */
    private TextView opensList(LinearLayout row, Stepper stepper) {
        LinearLayout box = new LinearLayout(owner);
        box.setGravity(Gravity.CENTER_VERTICAL);
        TextView value = text("", M(26), VALUE, false);
        value.setSingleLine(true);
        value.setEllipsize(TextUtils.TruncateAt.END);
        value.setMaxWidth(m(340));
        value.setTag("value");
        box.addView(value, new LayoutParams(-2, -2));
        TextView arrow = chevron("›");
        arrow.setPadding(m(12), 0, 0, 0);
        box.addView(arrow);
        LayoutParams boxSize = new LayoutParams(-2, -2);
        boxSize.leftMargin = m(16);
        row.addView(box, boxSize);
        row.setOnKeyListener((view, key, event) -> rowKeys(row, key, event, stepper));
        return value;
    }

    /** {@code − value +}: used for the millisecond delays. */
    private TextView plusMinus(LinearLayout row, Stepper stepper) {
        TextView minus = chevron("−"), plus = chevron("+");
        TextView value = text("", M(26), Color.WHITE, false);
        value.setGravity(Gravity.CENTER);
        value.setTag("value");
        row.addView(minus);
        row.addView(value, new LayoutParams(m(140), -2));
        row.addView(plus);
        row.setOnKeyListener((view, key, event) -> rowKeys(row, key, event, stepper));
        return value;
    }

    private ImageView switchView(LinearLayout row) {
        ImageView value = new ImageView(owner);
        int h = m(36);
        LayoutParams size = new LayoutParams(StandardSwitchDrawable.widthForHeight(h), h);
        size.rightMargin = s(2);
        row.addView(value, size);
        return value;
    }

    private void dim(View row, boolean enabled) {
        row.setEnabled(enabled);
        row.setAlpha(enabled ? 1f : .42f);
    }

    // ---- tabs ------------------------------------------------------------------------------

    /** Each tab's last row, so going back into a tab returns there (focus audit F15). */
    private final java.util.Map<Integer, String> rowByTab = new java.util.HashMap<>();
    private boolean watchingRows;

    private void watchRows() {
        if (watchingRows)
            return;
        watchingRows = true;
        getViewTreeObserver().addOnGlobalFocusChangeListener((before, after) -> {
            if (before != null && before.getParent() == body) {
                inkRow(before, false);
                showRulesBeside(before, false);
            }
            if (after != null && after.getParent() == body) {
                inkRow(after, true);
                showRulesBeside(after, true);
            }
            if (building || after == null || page != null)
                return;
            View at = after;
            while (at != null && at != this) {
                if (at.getParent() == body && at.getTag() instanceof String) {
                    rowByTab.put(tab, (String) at.getTag());
                    return;
                }
                at = at.getParent() instanceof View ? (View) at.getParent() : null;
            }
        });
    }

    void showTab(int id) {
        if (closed)
            return;
        watchRows();
        building = true;
        try {
            page = null;
            tab = id;
            header.setVisibility(VISIBLE);
            styleTabs();
            body.removeAllViews();
            bindings.clear();
            scroll.setMax(maxPanelHeight() - m(18 + BOX_HEIGHT + 22 + 22) - s(28));
            if (id == TAB_AUDIO)
                buildAudio();
            else if (id == TAB_PLAYBACK)
                buildPlayback();
            else
                buildSubtitles();
            dropTrailingRule();
            if (body.getChildCount() == 0) {
                TextView none = text("No options available for this video", 19, META, false);
                none.setPadding(s(4), s(18), 0, s(18));
                body.addView(none);
            }
            refresh();
            fitWidth();
            scroll.scrollTo(0, 0);
        } finally {
            building = false;
        }
        updateMore();
    }

    private void dropTrailingRule() {
        int last = body.getChildCount() - 1;
        if (last >= 0 && !(body.getChildAt(last) instanceof LinearLayout) &&
            !(body.getChildAt(last) instanceof TextView))
            body.removeViewAt(last);
    }

    /** Playback Settings opens with focus on the open tab (P3), also when asked before the panel is on screen. */
    void focusTab() {
        View t = tabs.findViewWithTag("playback.tab:" + tab);
        if (t == null)
            return;
        if (!t.requestFocus() || !t.isAttachedToWindow())
            t.post(t::requestFocus);
    }

    private void focusTop() {
        if (page != null) {
            View back = findViewWithTag("playback.page.back");
            if (back != null)
                back.requestFocus();
        } else
            focusTab();
    }

    private void focusBody() {
        String remembered = rowByTab.get(tab);
        View last = remembered == null ? null : body.findViewWithTag(remembered);
        if (last != null && last.isFocusable() && last.isEnabled() && last.requestFocus())
            return;
        for (int i = 0; i < body.getChildCount(); i++) {
            View child = body.getChildAt(i);
            if (child.isFocusable() && child.isEnabled() && child.requestFocus())
                return;
        }
    }

    private void buildSubtitles() {
        section("Subtitle Selection", true);
        toggle("enabled", "cc", "Subtitles");

        LinearLayout track = row("subtitles.row.track", "list", "Subtitle Track");
        TextView current = opensList(track, direction -> stepTrack(backend.tracks(), direction));
        bindings.add(() -> bindTrack(track, current, backend.tracks(), "Subtitle Track", "Off"));
        track.setOnClickListener(v -> openTracks("subtitles"));

        LinearLayout download = row("subtitles.row.download", "download", "Download Subtitles");
        TextView arrow = chevron("›");
        arrow.setPadding(s(12), 0, 0, 0);
        download.addView(arrow);
        download.setOnClickListener(v -> backend.download());

        // PS11 (approved): Subtitle delay sits in Subtitle Selection; timing is judged against the subtitles.
        LinearLayout delay = row("subtitles.row.delay", "time", "Subtitle Delay");
        TextView ms = plusMinus(
            delay, direction -> backend.set("delay", backend.get("delay") + direction * SUBTITLE_DELAY_STEP_MS));
        bindings.add(() -> {
            ms.setText(String.format(Locale.ROOT, "%d ms", backend.get("delay")));
            delay.setContentDescription("Subtitle Delay, " + backend.get("delay") +
                                        " milliseconds. Left decreases and Right increases by 100 milliseconds.");
        });
        delay.setOnClickListener(v -> {
            backend.set("delay", backend.get("delay") + SUBTITLE_DELAY_STEP_MS);
            refresh();
        });

        section("Subtitle Appearance", false);
        choices("size", "size", "Text Size", new String[] {"Small", "Medium", "Large", "Extra large"},
                new int[] {25, 50, 75, 100});
        swatches();
        // Approved 0.156 #24, PS11: − + ; Right (or +) raises by 5% of the screen, Left lowers; moves live.
        LinearLayout position = row("subtitles.row.position", "position", "Vertical Position");
        TextView height = plusMinus(position, direction -> backend.set(
            "position", SubtitleVerticalPosition.step(backend.get("position") + direction * POSITION_STEP)));
        height.getLayoutParams().width = s(150);
        bindings.add(() -> {
            String label = positionLabel(backend.get("position"));
            height.setText(label);
            position.setContentDescription("Vertical Position, " + label);
        });
        toggle("outline", "outline", "Outline");
        toggle("background", "background", "Background");

        // Round two (owner, 19 Nov 2026): Background opacity steps 25 · 50 · 75 · 100% with Left / Right.
        choices("opacity", "opacity", "Background Opacity", OPACITY_CHIPS, OPACITY_CHIP_VALUES);
        LinearLayout opacity = body.findViewWithTag("subtitles.row.opacity");

        bindings.add(() -> {
            boolean available = backend.available(), on = backend.get("enabled") != 0;
            for (String key : new String[] {"size", "colour", "position", "outline", "background"})
                dim(body.findViewWithTag("subtitles.row." + key), available);
            dim(opacity, available && backend.get("background") != 0);
            dim(delay, available && on);
        });
        // Owner decision (16 Nov 2026): For This Series is the save row at the bottom of Subtitles and Audio.
        seriesChoicesRow();
    }

    private void buildAudio() {
        List<Track> tracks = backend.audioTracks();
        if (!tracks.isEmpty()) {
            section("Audio Selection", true);
            LinearLayout track = row("audio.row.track", "note", "Audio Track");
            TextView current = opensList(track, direction -> stepTrack(backend.audioTracks(), direction));
            bindings.add(() -> bindTrack(track, current, backend.audioTracks(), "Audio Track", "—"));
            track.setOnClickListener(v -> openTracks("audio"));
        }
        boolean adjustments = backend.audioDelay() != null || backend.option("boost") != null ||
                              backend.option("night") != null || backend.option("spatial") != null;
        if (adjustments)
            audioAdjustments(tracks.isEmpty());
        // Owner decision (16 Nov 2026): For This Series is the save row at the bottom of Subtitles and Audio.
        seriesChoicesRow();
    }

    private void audioAdjustments(boolean first) {
        section("Audio Adjustments", first);
        if (backend.audioDelay() != null) {
            LinearLayout delay = row("audio.row.delay", "time", "Audio Delay");
            TextView ms = plusMinus(delay, backend::stepAudioDelay);
            bindings.add(() -> {
                Integer value = backend.audioDelay();
                ms.setText(String.format(Locale.ROOT, "%d ms", value == null ? 0 : value));
                delay.setContentDescription("Audio Delay, " + (value == null ? 0 : value) + " milliseconds");
            });
            delay.setOnClickListener(v -> {
                backend.stepAudioDelay(1);
                refresh();
            });
        }
        boostChips();
        option("night", "moon", "Night Mode");
        option("spatial", "surround", "Spatial Audio");
    }

    static final String[] BOOST_LABELS = {"Off", "Low", "Medium", "High"};

    /** Audio boost Off · Low · Medium · High, stepping with Left / Right (owner, 19 Nov 2026; player/33-v15). */
    private void boostChips() {
        if (backend.boostLevel() == null) {
            option("boost", "boost", "Audio Boost");
            return;
        }
        LinearLayout row = row("audio.row.boost", "boost", "Audio Boost");
        TextView value = cycler(row, direction -> {
            Integer level = backend.boostLevel();
            if (level != null && row.isEnabled())
                backend.setBoostLevel(Math.max(0, Math.min(BOOST_LABELS.length - 1, level + direction)));
        }, () -> {
            Integer level = backend.boostLevel();
            return level == null ? 0 : level;
        }, () -> BOOST_LABELS.length);
        bindings.add(() -> {
            Integer level = backend.boostLevel();
            int chosen = level == null ? 0 : level;
            value.setText(BOOST_LABELS[chosen]);
            row.setContentDescription("Audio Boost, " + BOOST_LABELS[chosen]);
            dim(row, level != null && backend.optionEnabled("boost"));
        });
    }

    private void buildPlayback() {
        boolean picture = !backend.choices("format").isEmpty() || !backend.choices("s3d").isEmpty() ||
                          backend.pictureInPicture() != null;
        boolean speed = backend.speed() != null;
        // Round two (owner, 19 Nov 2026; player/33-v15): quiet headings Picture and Speed · Playing On · This Show.
        if (picture || speed)
            section("Picture and Speed", true);
        if (picture) {
            choiceStepper("format", "aspect", "Format");
            choiceStepper("s3d", "cube", "3D Mode");
            Runnable pip = backend.pictureInPicture();
            if (pip != null) {
                LinearLayout row = row("playback.row.pip", "pip", "Picture in Picture");
                row.addView(text("Start", 20, Color.WHITE, false));
                TextView arrow = chevron("›");
                arrow.setPadding(s(12), 0, 0, 0);
                row.addView(arrow);
                row.setContentDescription("Picture in Picture, Start");
                row.setOnClickListener(v -> {
                    Runnable action = backend.pictureInPicture();
                    if (action != null)
                        action.run();
                });
            }
        }
        boolean playback = backend.limitsAvailable() || !backend.choices("playmode").isEmpty()
            || backend.option("skip") != null || !backend.choices("skipmode").isEmpty();
        if (speed)
            speedChips();
        if (!playback && !speed)
            return;
        if (backend.limitsAvailable() || !backend.choices("playmode").isEmpty() || backend.option("skip") != null
            || !backend.choices("skipmode").isEmpty())
            section("Playing On", !picture && !speed);
        if (backend.limitsAvailable())
            sleepAndStillWatching();
        choiceStepper("playmode", "repeat", "Play mode");
        // Skipping (renamed from "Skip intros & credits", owner 19 Nov 2026) keeps its three chips.
        List<Choice> skipModes = backend.choices("skipmode");
        if (!skipModes.isEmpty())
            choiceChips("skipmode", "skip", "Skipping", skipModes);
        else
            option("skip", "skip", "Skipping");
        seriesPlaybackRow();
    }

    /**
     * The Playback tab's save row (player v11, approved 13 Nov 2026): "Use this format and skipping for all of
     * <show>". While it is on, changing the format or skipping here updates what the series remembers.
     */
    private void seriesPlaybackRow() {
        if (backend.seriesPlayback() == null)
            return;
        // Round two (owner, 19 Nov 2026): "This Show" heading, "Remember for this show" (RULES §6: never "series").
        section("This Show", false);
        String label = "Remember for this show";
        LinearLayout row = row("playback.row.series", "repeat", label);
        ImageView value = switchView(row);
        bindings.add(() -> {
            Boolean on = backend.seriesPlayback();
            boolean enabled = on != null && on;
            if (enabled)
                backend.setSeriesPlayback(true);
            value.setImageDrawable(StandardSwitchDrawable.snapshot(owner, enabled, onFocusedRow(value)));
            row.setContentDescription(label + ", " + (enabled ? "On" : "Off"));
        });
        row.setOnClickListener(v -> {
            Boolean on = backend.seriesPlayback();
            backend.setSeriesPlayback(on == null || !on);
            refresh();
        });
    }

    /** Approved mock-up {@code suggestions/06}: remember this episode's choices for the whole series. */
    private void seriesChoicesRow() {
        if (backend.seriesChoices() == null)
            return;
        // Round two (owner, 19 Nov 2026): "This Show" heading, "Remember for this show" (RULES §6: never "series").
        section("This Show", false);
        String label = "Remember for this show";
        LinearLayout row = row("audio.row.series", "repeat", label);
        ImageView value = switchView(row);
        bindings.add(() -> {
            Boolean on = backend.seriesChoices();
            boolean enabled = on != null && on;
            value.setImageDrawable(StandardSwitchDrawable.snapshot(owner, enabled, onFocusedRow(value)));
            row.setContentDescription(label + ", " + (enabled ? "On" : "Off"));
        });
        row.setOnClickListener(v -> {
            Boolean on = backend.seriesChoices();
            backend.setSeriesChoices(on == null || !on);
            refresh();
        });
        row.setOnKeyListener((v, keyCode, event) -> rowKeys(row, keyCode, event, direction -> {
            backend.setSeriesChoices(direction > 0);
        }));
    }

    static final String[] SLEEP_LABELS = {"Off", "30", "60", "90", "End of episode"};

    /**
     * "Stop playing at" (owner, 6 and 12 Nov 2026; replaces Sleep timer): Off, three clock times that roll with the
     * clock (now + 30, 60, 90 minutes; a running timer shows its own end time), then End of episode or End of movie.
     */
    static String[] stopLabels(long nowMs, int choice, long endsAt, boolean episode) {
        java.text.DateFormat format = java.text.DateFormat.getTimeInstance(java.text.DateFormat.SHORT);
        String[] labels = new String[SLEEP_LABELS.length];
        labels[0] = "Off";
        int[] minutes = {30, 60, 90};
        for (int i = 0; i < minutes.length; i++) {
            long at = choice == i + 1 && endsAt > 0 ? endsAt : nowMs + minutes[i] * 60_000L;
            labels[i + 1] = format.format(new java.util.Date(at));
        }
        labels[4] = episode ? "End of episode" : "End of movie";
        return labels;
    }

    /** Approved mock-up {@code suggestions/02}: five inline choices and a stepper for the episode count. */
    private void sleepAndStillWatching() {
        // Round two (owner, 19 Nov 2026; player/33-v15): one line, the value alone; Left / Right step through Off,
        // the three clock times and the end of the episode. The clock time is the value, so no note line under it.
        LinearLayout sleep = row("playback.row.sleep", "moon", "Stop playing at");
        TextView sleepValue = cycler(sleep, direction -> {
            int next = Math.max(0, Math.min(SLEEP_LABELS.length - 1, backend.sleepChoice() + direction));
            backend.setSleepChoice(next);
        }, backend::sleepChoice, () -> SLEEP_LABELS.length);
        bindings.add(() -> {
            int choice = backend.sleepChoice();
            String[] labels =
                stopLabels(System.currentTimeMillis(), choice, backend.sleepEndsAt(), backend.playingEpisode());
            sleepValue.setText(labels[choice]);
            String line = sleepNote(choice, backend.sleepEndsAt());
            sleep.setContentDescription("Stop playing at, " + labels[choice] + (line.isEmpty() ? "" : ". " + line));
        });

        // Episodes and time for a TV show, only time for a film (owner, 12 Nov 2026).
        LinearLayout still = row("playback.row.still", "help", "Still watching?");
        TextView stillValue = cycler(still, direction -> {
            boolean episode = backend.playingEpisode();
            List<StillWatchingChoices.Choice> options = StillWatchingChoices.options(episode);
            int n = StillWatchingChoices.index(
                options, backend.stillWatchingAfter(), backend.stillWatchingMinutes(), episode);
            StillWatchingChoices.Choice next = options.get(Math.max(0, Math.min(options.size() - 1, n + direction)));
            backend.setStillWatching(next.episodes, next.minutes);
        }, () -> {
            boolean episode = backend.playingEpisode();
            return StillWatchingChoices.index(StillWatchingChoices.options(episode), backend.stillWatchingAfter(),
                                              backend.stillWatchingMinutes(), episode);
        }, () -> StillWatchingChoices.options(backend.playingEpisode()).size());
        bindings.add(() -> {
            boolean episode = backend.playingEpisode();
            List<StillWatchingChoices.Choice> options = StillWatchingChoices.options(episode);
            int chosen = StillWatchingChoices.index(
                options, backend.stillWatchingAfter(), backend.stillWatchingMinutes(), episode);
            stillValue.setText(options.get(chosen).label);
            still.setContentDescription("Still watching, " + options.get(chosen).label);
        });
    }

    /** The line under the Sleep timer: when playback will stop. */
    static String sleepNote(int choice, long endsAt) {
        if (choice == 4)
            return "Stops at the end of this episode";
        if (choice <= 0 || endsAt <= 0)
            return "";
        java.text.DateFormat format = java.text.DateFormat.getTimeInstance(java.text.DateFormat.SHORT);
        return "Stops at " + format.format(new java.util.Date(endsAt));
    }

    static String formatSpeed(float speed) {
        float rounded = Math.round(speed * 100f) / 100f;
        return (Math.round(rounded * 100) % 10 == 0 ? String.format(Locale.ROOT, "%.1f×", rounded)
                                                    : String.format(Locale.ROOT, "%.2f×", rounded));
    }

    // ---- row kinds -------------------------------------------------------------------------

    private void toggle(String key, String iconKind, String label) {
        LinearLayout row = row("subtitles.row." + key, iconKind, label);
        ImageView value = switchView(row);
        bindings.add(() -> {
            boolean on = backend.get(key) != 0;
            value.setImageDrawable(StandardSwitchDrawable.snapshot(owner, on, onFocusedRow(value)));
            row.setContentDescription(label + ", " + (on ? "On" : "Off"));
        });
        row.setOnKeyListener((v, keyCode, event) -> rowKeys(row, keyCode, event, direction -> {
            boolean on = direction > 0;
            if ((backend.get(key) != 0) != on)
                backend.set(key, on ? 1 : 0);
        }));
        row.setOnClickListener(v -> {
            backend.set(key, backend.get(key) == 0 ? 1 : 0);
            refresh();
        });
    }

    private void option(String key, String iconKind, String label) {
        if (backend.option(key) == null)
            return;
        String tag = (key.equals("skip") ? "playback" : "audio") + ".row." + key;
        LinearLayout row = row(tag, iconKind, label);
        // PLAYER.md: Spatial audio keeps its switch; where the device can't do it, the switch stays visible and a
        // short note says so, rather than the whole row fading away.
        TextView note = new TextView(owner);
        note.setText("Not on this device");
        note.setTextSize(TypedValue.COMPLEX_UNIT_PX, m(22));
        note.setTextColor(SupernovaStyle.TEXT_SECONDARY);
        note.setVisibility(GONE);
        LayoutParams noteSize = new LayoutParams(-2, -2);
        noteSize.rightMargin = s(8);
        row.addView(note, noteSize);
        ImageView value = switchView(row);
        bindings.add(() -> {
            Boolean on = backend.option(key);
            boolean checked = on != null && on;
            boolean enabled = on != null && backend.optionEnabled(key);
            value.setImageDrawable(StandardSwitchDrawable.snapshot(owner, checked, onFocusedRow(value)));
            row.setContentDescription(label + ", " + (checked ? "On" : "Off") + (enabled ? "" : ", not on this device"));
            row.setEnabled(enabled);
            row.setAlpha(enabled ? 1f : .72f);
            note.setVisibility(enabled ? GONE : VISIBLE);
        });
        row.setOnKeyListener((v, keyCode, event) -> rowKeys(row, keyCode, event, direction -> {
            Boolean on = backend.option(key);
            if (on != null && backend.optionEnabled(key) && on != (direction > 0))
                backend.toggleOption(key);
        }));
        row.setOnClickListener(v -> {
            if (row.isEnabled()) {
                backend.toggleOption(key);
                refresh();
            }
        });
    }

    private void choices(String key, String iconKind, String title, String[] labels, int[] values) {
        LinearLayout row = row("subtitles.row." + key, iconKind, title);
        TextView value = cycler(row, direction -> stepValue(key, values, direction),
                                () -> indexOf(backend.get(key), values), () -> values.length);
        bindings.add(() -> {
            int current = backend.get(key);
            String shown = key.equals("opacity") ? labelFor(current, values, labels) : "Custom";
            for (int i = 0; i < values.length; i++)
                if (current == values[i])
                    shown = labels[i];
            value.setText(shown);
            row.setContentDescription(title + ", " + shown + ". Left and Right change it.");
        });
    }

    private static int indexOf(int value, int[] values) {
        for (int i = 0; i < values.length; i++)
            if (values[i] == value)
                return i;
        return -1;
    }

    /**
     * Choice words on a row (RULES: in choice rows the chosen option sits in a small chip; no blue for values;
     * player v10): evenly spaced, after a gap from the row's name so they never touch it.
     */
    private TextView[] chips(LinearLayout row, String[] labels) {
        LinearLayout box = new LinearLayout(owner);
        box.setTag("chips");
        box.setGravity(Gravity.CENTER_VERTICAL);
        TextView[] texts = new TextView[labels.length];
        float chipsWidth = 0;
        for (int i = 0; i < labels.length; i++) {
            texts[i] = text(labels[i], M(25), Color.WHITE, false);
            texts[i].setSingleLine(true);
            texts[i].setGravity(Gravity.CENTER);
            texts[i].setMinHeight(m(46));
            texts[i].setPadding(m(16), 0, m(16), 0);
            LayoutParams size = new LayoutParams(-2, m(46));
            if (i > 0)
                size.leftMargin = m(4);
            box.addView(texts[i], size);
            chipsWidth += texts[i].getPaint().measureText(labels[i]) + m(32) + (i > 0 ? m(4) : 0);
        }
        // PS11 (owner's photos, 18 Nov 2026): a row's name is never squeezed out by its chips. When name and chips
        // don't fit on one line, the chips go on a line of their own under the name.
        TextView label = rowLabel(row);
        float labelWidth = label == null ? 0 : label.getPaint().measureText(label.getText().toString());
        float room = widthFor(tab) - getPaddingLeft() - getPaddingRight() - m(36) - m(46) - m(16);
        if (label != null && labelWidth + s(24) + chipsWidth > room) {
            int at = row.indexOfChild(label);
            row.removeView(label);
            LinearLayout column = new LinearLayout(owner);
            column.setOrientation(VERTICAL);
            column.setPadding(0, s(10), 0, s(10));
            column.addView(label, new LayoutParams(-1, -2));
            LayoutParams under = new LayoutParams(-2, -2);
            under.topMargin = s(6);
            under.leftMargin = -s(16);
            column.addView(box, under);
            row.addView(column, at, new LayoutParams(0, -2, 1));
        } else {
            LayoutParams boxSize = new LayoutParams(-2, -2);
            boxSize.leftMargin = m(16);
            row.addView(box, boxSize);
        }
        return texts;
    }

    /** The row's name (the first text after its icon). */
    private static TextView rowLabel(LinearLayout row) {
        for (int i = 0; i < row.getChildCount(); i++)
            if (row.getChildAt(i) instanceof TextView)
                return (TextView) row.getChildAt(i);
        return null;
    }

    private void paintChips(TextView[] texts, int chosen) {
        boolean focused = texts.length > 0 && onFocusedRow(texts[0]);
        float radius = BOX_RADIUS * k / owner.getResources().getDisplayMetrics().density;
        for (int i = 0; i < texts.length; i++) {
            texts[i].setTag(i == chosen ? "value" : null);
            if (i == chosen) {
                texts[i].setTextColor(Color.WHITE);
                texts[i].setBackground(
                    SupernovaStyle.rounded(owner, focused ? SupernovaStyle.FOCUS_TEXT : CHIP, radius));
            } else {
                texts[i].setTextColor(focused ? SupernovaStyle.FOCUS_TEXT : Color.WHITE);
                texts[i].setBackground(null);
            }
        }
    }

    private void swatches() {
        LinearLayout row = row("subtitles.row.colour", "palette", "Text Colour");
        ImageView[] dots = new ImageView[COLOURS.length];
        for (int i = 0; i < COLOURS.length; i++) {
            dots[i] = new ImageView(owner);
            LayoutParams size = new LayoutParams(m(44), m(44));
            row.addView(dots[i], size);
        }
        bindings.add(() -> {
            int selected = backend.get("colour");
            String spoken = "Custom colour";
            for (int n = 0; n < COLOURS.length; n++)
                if (selected == COLOURS[n])
                    spoken = COLOUR_NAMES[n];
            row.setContentDescription("Text Colour, " + spoken);
            for (int i = 0; i < COLOURS.length; i++)
                dots[i].setImageDrawable(new Swatch(COLOURS[i], COLOURS[i] == selected));
        });
        row.setOnClickListener(v -> {
            stepValue("colour", COLOURS, 1);
            refresh();
        });
        row.setOnKeyListener(
            (v, key, event) -> rowKeys(row, key, event, direction -> stepValue("colour", COLOURS, direction)));
    }

    /** Speed 0.75×, 1×, 1.25×, 1.5×, 2×, stepping with Left / Right (round two, player/33-v15). */
    private void speedChips() {
        LinearLayout row = row("playback.row.speed", "speed", "Speed");
        TextView value = cycler(row, direction -> {
            Float speed = backend.speed();
            int next = nearestSpeed(speed == null ? 1f : speed) + direction;
            backend.setSpeed(SPEEDS[Math.max(0, Math.min(SPEEDS.length - 1, next))]);
        }, () -> {
            Float speed = backend.speed();
            return nearestSpeed(speed == null ? 1f : speed);
        }, () -> SPEEDS.length);
        bindings.add(() -> {
            Float speed = backend.speed();
            float now = speed == null ? 1f : speed;
            int chosen = nearestSpeed(now);
            value.setText(Math.abs(SPEEDS[chosen] - now) < .01f ? speedChip(SPEEDS[chosen]) : formatSpeed(now));
            row.setContentDescription("Speed, " + formatSpeed(now));
        });
    }

    static final float[] SPEEDS = {.75f, 1f, 1.25f, 1.5f, 2f};

    static int nearestSpeed(float speed) {
        int best = 0;
        for (int i = 1; i < SPEEDS.length; i++)
            if (Math.abs(SPEEDS[i] - speed) < Math.abs(SPEEDS[best] - speed))
                best = i;
        return best;
    }

    /** "0.75×", "1×", "1.25×", "1.5×", "2×": no trailing zeros. */
    static String speedChip(float speed) {
        String plain = new java.math.BigDecimal(Float.toString(speed)).stripTrailingZeros().toPlainString();
        return plain + "×";
    }

    private void choiceStepper(String key, String iconKind, String title) {
        List<Choice> initial = backend.choices(key);
        if (initial.isEmpty())
            return;
        // Round two (owner, 19 Nov 2026; player/33-v15): named choices step in place, whatever their number.
        LinearLayout row = row("playback.row." + key, iconKind, title);
        TextView value = cycler(row, direction -> {
            List<Choice> options = backend.choices(key);
            int current = selectedIndex(options);
            if (direction < -1 && current >= 0) {
                // OK after the last choice: back round to the first one that can be chosen.
                for (Choice c : options)
                    if (c.enabled) {
                        c.select.run();
                        return;
                    }
                return;
            }
            for (int i = current + direction; i >= 0 && i < options.size(); i += direction) {
                if (options.get(i).enabled) {
                    options.get(i).select.run();
                    return;
                }
            }
        }, () -> lastEnabledOrBefore(backend.choices(key)), () -> backend.choices(key).size());
        bindings.add(() -> {
            String label = "—";
            for (Choice c : backend.choices(key))
                if (c.selected)
                    label = c.label;
            value.setText(label);
            row.setContentDescription(title + ", " + label);
        });
    }

    private static int selectedIndex(List<Choice> options) {
        for (int i = 0; i < options.size(); i++)
            if (options.get(i).selected)
                return i;
        return -1;
    }

    /**
     * For OK's going round: the chosen index, or the list's end when nothing after it can be chosen (so OK on the
     * last choice that can be chosen goes back to the first).
     */
    private static int lastEnabledOrBefore(List<Choice> options) {
        int current = selectedIndex(options);
        for (int i = current + 1; i < options.size(); i++)
            if (options.get(i).enabled)
                return current;
        return options.size() - 1;
    }

    /** A short list of choices shown as chips: only Skipping keeps them (round two, player/33-v15). */
    private void choiceChips(String key, String iconKind, String title, List<Choice> initial) {
        LinearLayout row = row("playback.row." + key, iconKind, title);
        String[] labels = new String[initial.size()];
        for (int i = 0; i < labels.length; i++)
            labels[i] = initial.get(i).label;
        TextView[] texts = chips(row, labels);
        bindings.add(() -> {
            List<Choice> options = backend.choices(key);
            int chosen = -1;
            String label = "—";
            for (int i = 0; i < options.size() && i < texts.length; i++) {
                texts[i].setText(options.get(i).label);
                texts[i].setAlpha(options.get(i).enabled ? 1f : .42f);
                if (options.get(i).selected) {
                    chosen = i;
                    label = options.get(i).label;
                }
            }
            paintChips(texts, chosen);
            row.setContentDescription(title + ", " + label);
        });
        row.setOnKeyListener((v, keyCode, event) -> rowKeys(row, keyCode, event, direction -> {
            List<Choice> options = backend.choices(key);
            int current = -1;
            for (int i = 0; i < options.size(); i++)
                if (options.get(i).selected)
                    current = i;
            for (int i = current + direction; i >= 0 && i < options.size(); i += direction) {
                if (options.get(i).enabled) {
                    options.get(i).select.run();
                    return;
                }
            }
        }));
    }

    private void stepValue(String key, int[] values, int direction) {
        int n = -1, current = backend.get(key);
        for (int i = 0; i < values.length; i++)
            if (current == values[i])
                n = i;
        int target =
            n < 0 ? (direction > 0 ? 0 : values.length - 1) : Math.max(0, Math.min(values.length - 1, n + direction));
        backend.set(key, values[target]);
    }

    static final int POSITION_STEP = 5;
    static final int POSITION_TOP = 90;

    /** "Bottom", "Raised 5%" … "Raised 85%", "Top". */
    static String positionLabel(int percent) {
        int step = SubtitleVerticalPosition.step(percent);
        if (step <= 0)
            return "Bottom";
        if (step >= POSITION_TOP)
            return "Top";
        return "Raised " + step + "%";
    }

    private static String labelFor(int value, int[] values, String[] labels) {
        for (int i = 0; i < values.length; i++)
            if (values[i] == value)
                return labels[i];
        return Math.round(value * 100f / 255f) + "%";
    }

    private void stepTrack(List<Track> tracks, int direction) {
        int current = -1;
        for (int i = 0; i < tracks.size(); i++)
            if (tracks.get(i).selected)
                current = i;
        for (int i = current + direction; i >= 0 && i < tracks.size(); i += direction) {
            if (tracks.get(i).enabled) {
                tracks.get(i).select.run();
                return;
            }
        }
    }

    private void bindTrack(LinearLayout row, TextView value, List<Track> tracks, String title, String none) {
        Track selected = null;
        for (Track t : tracks)
            if (t.selected)
                selected = t;
        value.setText(selected == null ? none : selected.label);
        PreviewLanguageIcon.apply(value, selected == null ? null : selected.language, s(30));
        value.setCompoundDrawablePadding(s(10));
        showTrackPills(value, selected == null || selected.pills == null ? Collections.emptyList() : selected.pills);
        String pills = selected == null || selected.pills == null || selected.pills.isEmpty() ? ""
            : ", " + String.join(", ", selected.pills);
        row.setContentDescription(title + ", " + value.getText() + pills +
                                  ". Left and Right change track; OK shows all tracks.");
    }

    /**
     * The chosen track's pills beside its name on the main page, as in the lists (owner, 16 Nov 2026: yes to
     * REVIEW_0157 #11), e.g. English · Original · SDH.
     */
    private void showTrackPills(TextView value, List<String> pills) {
        if (!(value.getParent() instanceof ViewGroup))
            return;
        ViewGroup box = (ViewGroup) value.getParent();
        for (int i = box.getChildCount() - 1; i >= 0; i--)
            if ("track.pill".equals(box.getChildAt(i).getTag()))
                box.removeViewAt(i);
        int at = box.indexOfChild(value) + 1;
        for (String pill : pills) {
            TextView chip = text(pill, 15, Color.WHITE, false);
            chip.setTag("track.pill");
            chip.setSingleLine(true);
            chip.setPadding(s(12), s(4), s(12), s(4));
            android.graphics.drawable.GradientDrawable edge = new android.graphics.drawable.GradientDrawable();
            edge.setCornerRadius(s(BOX_RADIUS));
            edge.setStroke(Math.max(1, s(1.5f)), PILL_EDGE);
            chip.setBackground(edge);
            LayoutParams gap = new LayoutParams(-2, -2);
            gap.leftMargin = s(8);
            box.addView(chip, at++, gap);
        }
    }

    // ---- in-panel track list ---------------------------------------------------------------

    private void openTracks(String which) {
        if (closed)
            return;
        if (page == null)
            stackOverPanel();
        building = true;
        try {
            page = which;
            header.setVisibility(GONE);
            body.removeAllViews();
            bindings.clear();
            boolean subtitles = which.equals("subtitles");
            String title = subtitles ? "Subtitle Track" : "Audio Track";

            LinearLayout back = new LinearLayout(owner);
            back.setTag("playback.page.back");
            back.setGravity(Gravity.CENTER_VERTICAL);
            back.setFocusable(true);
            back.setFocusableInTouchMode(true);
            back.setBackground(rowFocus());
            back.setPadding(s(4), s(4), s(4), s(4));
            TextView chevron = text("‹", 30, HEADING, true);
            back.addView(chevron);
            TextView heading = text(title, 28, Color.WHITE, true);
            heading.setPadding(s(12), 0, 0, 0);
            back.addView(heading);
            back.setContentDescription("Back, " + title);
            back.setOnClickListener(v -> dismissPopup());
            back.setOnKeyListener((v, key, event)
                                      -> event.getAction() == KeyEvent.ACTION_DOWN &&
                                             (key == KeyEvent.KEYCODE_DPAD_UP || key == KeyEvent.KEYCODE_DPAD_LEFT ||
                                              key == KeyEvent.KEYCODE_DPAD_RIGHT));
            LayoutParams backSize = new LayoutParams(-1, -2);
            backSize.bottomMargin = s(14);
            removeHeaderPage();
            // No title or back header on a list opened from a row (owner, 13 Nov 2026; fixes 0.156 P6): Back returns.
            back.setVisibility(GONE);
            addView(back, 1, backSize);

            List<Track> tracks = subtitles ? backend.tracks() : backend.audioTracks();
            scroll.setMax(s(LIST_ROWS_VISIBLE * ROW_HEIGHT));
            View initial = null;
            for (int i = 0; i < tracks.size(); i++) {
                View row = trackRow(which, i, tracks.get(i));
                if (initial == null && tracks.get(i).enabled || tracks.get(i).selected && tracks.get(i).enabled)
                    initial = row;
            }
            if (tracks.isEmpty()) {
                TextView none =
                    text(subtitles ? "No subtitle tracks available" : "No audio tracks available", 19, META, false);
                none.setPadding(s(4), s(18), 0, s(18));
                body.addView(none);
            }
            Runnable chooseFile = subtitles ? backend.chooseFile() : null;
            if (chooseFile != null) {
                LinearLayout choose = row("subtitles.choose.file", "file", "Choose subtitle file…");
                choose.setOnClickListener(v -> chooseFile.run());
            }
            dropTrailingRule();
            scroll.scrollTo(0, 0);
            // While the remote is on the tab row, moving along the tabs keeps it there (P3, RULES §4).
            boolean onTabs = tabs != null && tabs.hasFocus();
            if (!onTabs && initial != null)
                initial.requestFocus();
            else if (!onTabs && !body.requestFocus())
                requestFocus();
        } finally {
            building = false;
        }
        scroll.post(this::updateMore);
    }

    // ---- a list on top of the panel ---------------------------------------------------------

    private final Drawable resting;
    private final int[] restingPadding;

    /**
     * A list opened from a row sits on top of the panel (RULES §11, mock-up player/28 bottom): the panel behind is
     * darkened and pushed back slightly, the list in front is offset a little down and in, with the faint edge and a
     * deeper shadow. Back closes the list and the panel comes forward again (0.157 Shield review #12, #22).
     */
    private void stackOverPanel() {
        if (getWidth() <= 0 || getHeight() <= 0)
            return;
        Bitmap behind = Bitmap.createBitmap(getWidth(), getHeight(), Bitmap.Config.ARGB_8888);
        draw(new Canvas(behind));
        // PS8 (owner, 18 Nov 2026): the panel behind keeps its width and height, darkened and pushed back slightly
        // (about 96%); the list in front is the same width, a little lower. The panel never collapses to a strip.
        setMinimumHeight(getHeight());
        int top = restingPadding[1] + header.getHeight();
        setBackground(new StackedList(behind, 0, top, 0, s(26), 0, s(18)));
        setPadding(restingPadding[0], top + s(12), restingPadding[2], restingPadding[3] + s(18));
    }

    private void unstack() {
        setMinimumHeight(0);
        setBackground(resting);
        setPadding(restingPadding[0], restingPadding[1], restingPadding[2], restingPadding[3]);
    }

    /** How far the panel behind a list is pushed back (PS8). */
    static final float BEHIND_SCALE = .96f;

    /** The panel as it was (pushed back, darkened) with the list's own surface in front of it. */
    private static final class StackedList extends Drawable {
        private final Bitmap behind;
        private final int left, top, right, radius, edge, shadow;
        private final Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG);

        StackedList(Bitmap behind, int left, int top, int right, int radius, int edge, int shadow) {
            this.behind = behind;
            this.left = left;
            this.top = top;
            this.right = right;
            this.radius = radius;
            this.edge = edge;
            this.shadow = shadow;
        }

        @Override
        public void draw(Canvas canvas) {
            Rect b = getBounds();
            float scale = BEHIND_SCALE;
            int saved = canvas.save();
            canvas.scale(scale, scale, behind.getWidth() / 2f, behind.getHeight() / 2f);
            paint.setColorFilter(null);
            canvas.drawBitmap(behind, 0, 0, paint);
            paint.setColor(com.archos.mediacenter.video.leanback.PreviewDialog.BEHIND_SHADE);
            canvas.drawRoundRect(new RectF(0, 0, behind.getWidth(), behind.getHeight()), radius, radius, paint);
            canvas.restoreToCount(saved);
            RectF card = new RectF(left, top, b.right - right, b.bottom - shadow);
            paint.setColor(PANEL | 0xFF000000);
            paint.setShadowLayer(shadow, 0, shadow / 3f, 0xAA000000);
            canvas.drawRoundRect(card, radius, radius, paint);
            paint.clearShadowLayer();
            if (edge > 0) {
                paint.setStyle(Paint.Style.STROKE);
                paint.setStrokeWidth(edge);
                paint.setColor(SupernovaStyle.GLASS_EDGE);
                canvas.drawRoundRect(card, radius, radius, paint);
                paint.setStyle(Paint.Style.FILL);
            }
        }

        @Override
        public void setAlpha(int alpha) {
            paint.setAlpha(alpha);
        }

        @Override
        public void setColorFilter(ColorFilter filter) {
            paint.setColorFilter(filter);
        }

        @Override
        public int getOpacity() {
            return PixelFormat.TRANSLUCENT;
        }
    }

    private void removeHeaderPage() {
        View old = findViewWithTag("playback.page.back");
        if (old != null)
            removeView(old);
    }

    private View trackRow(String which, int index, Track track) {
        LinearLayout row = new LinearLayout(owner);
        row.setTag(which + ".track." + index);
        row.setGravity(Gravity.CENTER_VERTICAL);
        row.setMinimumHeight(s(ROW_HEIGHT));
        row.setPadding(s(16), 0, s(16), 0);
        row.setFocusable(track.enabled);
        row.setFocusableInTouchMode(track.enabled);
        row.setEnabled(track.enabled);
        row.setAlpha(track.enabled ? 1f : .42f);
        row.setDescendantFocusability(FOCUS_BLOCK_DESCENDANTS);
        row.setBackground(rowFocus());
        row.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        row.setTag(R.id.preview_track_language, track.language);
        ImageView flag = new ImageView(owner);
        flag.setImageDrawable(new PreviewLanguageIcon(owner, PreviewLanguageIcon.providerCode(track.language)));
        flag.setScaleType(ImageView.ScaleType.FIT_CENTER);
        LayoutParams f = new LayoutParams(s(34), s(34));
        f.rightMargin = s(14);
        row.addView(flag, f);
        TextView label = text(track.label, 21, Color.WHITE, true);
        label.setSingleLine(true);
        label.setEllipsize(TextUtils.TruncateAt.END);
        if (track.pills.isEmpty()) {
            row.addView(label, new LayoutParams(0, -2, 1));
        } else {
            row.addView(label, new LayoutParams(-2, -2));
            for (String pill : track.pills) {
                TextView chip = text(pill, 16, Color.WHITE, false);
                chip.setTag("track.pill");
                chip.setSingleLine(true);
                chip.setPadding(s(12), s(4), s(12), s(4));
                android.graphics.drawable.GradientDrawable edge = new android.graphics.drawable.GradientDrawable();
                edge.setCornerRadius(s(BOX_RADIUS));
                edge.setStroke(Math.max(1, s(1.5f)), PILL_EDGE);
                chip.setBackground(edge);
                LayoutParams gap = new LayoutParams(-2, -2);
                gap.leftMargin = s(12);
                row.addView(chip, gap);
            }
            row.addView(new View(owner), new LayoutParams(0, 1, 1));
        }
        TextView tick = text("✓", 24, VALUE, false);
        tick.setVisibility(track.selected ? VISIBLE : INVISIBLE);
        row.addView(tick, new LayoutParams(s(28), -2));
        row.setContentDescription(track.label + (track.selected ? ", selected" : ""));
        row.setOnClickListener(v -> {
            track.select.run();
            dismissPopup();
        });
        row.setOnKeyListener((v, key, event) -> rowKeys(row, key, event, null));
        body.addView(row, new LayoutParams(-1, -2));
        return row;
    }

    private void updateMore() {
        if (page == null || closed) {
            more.setVisibility(GONE);
            return;
        }
        int bottom = scroll.getScrollY() + scroll.getHeight(), hidden = 0;
        for (int i = 0; i < body.getChildCount(); i++) {
            View child = body.getChildAt(i);
            Object tag = child.getTag();
            if (tag instanceof String && ((String)tag).startsWith(page + ".track.") && child.getBottom() > bottom + 1)
                hidden++;
        }
        if (scroll.getHeight() == 0)
            hidden = 0;
        more.setText("▾ " + hidden + " more");
        more.setVisibility(hidden > 0 ? VISIBLE : GONE);
    }

    /** Closes an open track list and returns to its tab row. False when no list is open. */
    boolean dismissPopup() {
        if (page == null || closed)
            return false;
        String was = page;
        removeHeaderPage();
        unstack();
        showTab(was.equals("audio") ? TAB_AUDIO : TAB_SUBTITLES);
        View row = body.findViewWithTag(was.equals("audio") ? "audio.row.track" : "subtitles.row.track");
        if (row != null)
            row.requestFocus();
        return true;
    }

    void refresh() {
        if (closed)
            return;
        if (page != null) {
            List<Track> tracks = page.equals("audio") ? backend.audioTracks() : backend.tracks();
            int shown = 0;
            for (int i = 0; i < body.getChildCount(); i++) {
                Object tag = body.getChildAt(i).getTag();
                if (tag instanceof String && ((String)tag).startsWith(page + ".track."))
                    shown++;
            }
            if (shown != tracks.size()) {
                View focused = body.findFocus();
                String label = null;
                if (focused instanceof ViewGroup && ((ViewGroup)focused).getChildCount() > 1)
                    label = ((TextView)((ViewGroup)focused).getChildAt(1)).getText().toString();
                openTracks(page);
                if (label != null)
                    for (int i = 0; i < tracks.size(); i++) {
                        if (label.equals(tracks.get(i).label)) {
                            View row = body.findViewWithTag(page + ".track." + i);
                            if (row != null && row.isFocusable())
                                row.requestFocus();
                            break;
                        }
                    }
                return;
            }
            for (int i = 0; i < tracks.size(); i++) {
                ViewGroup row = body.findViewWithTag(page + ".track." + i);
                Track track = tracks.get(i);
                row.setEnabled(track.enabled);
                row.setFocusable(track.enabled);
                row.setFocusableInTouchMode(track.enabled);
                row.setAlpha(track.enabled ? 1f : .42f);
                row.setTag(R.id.preview_track_language, track.language);
                ((ImageView)row.getChildAt(0))
                    .setImageDrawable(new PreviewLanguageIcon(owner, PreviewLanguageIcon.providerCode(track.language)));
                ((TextView)row.getChildAt(1)).setText(track.label);
                row.getChildAt(2).setVisibility(track.selected ? VISIBLE : INVISIBLE);
                row.setContentDescription(track.label + (track.selected ? ", selected" : ""));
                row.setOnClickListener(v -> {
                    track.select.run();
                    dismissPopup();
                });
            }
            return;
        }
        for (Runnable binding : bindings)
            binding.run();
        View focusedRow = body.getFocusedChild();
        if (focusedRow != null && focusedRow.isFocused())
            inkRow(focusedRow, true);
        if (appearanceShown)
            drawSample();
    }

    void close() {
        showAppearance(false);
        closed = true;
        bindings.clear();
    }

    // ---- drawing ---------------------------------------------------------------------------

    /** Rounded fill (vertical gradient) with a border and an optional outer glow, inset by the glow. */
    private static final class Surface extends Drawable {
        private final int top, bottom, border, radius, stroke, glow, blur;
        private final Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);

        Surface(int top, int bottom, int border, int radius, int stroke, int glow, int blur) {
            this.top = top;
            this.bottom = bottom;
            this.border = border;
            this.radius = radius;
            this.stroke = stroke;
            this.glow = glow;
            this.blur = blur;
        }

        @Override
        public void draw(Canvas canvas) {
            RectF r = new RectF(getBounds());
            r.inset(blur + stroke / 2f, blur + stroke / 2f);
            if (glow != 0 && blur > 0) {
                paint.setShader(null);
                paint.setStyle(Paint.Style.STROKE);
                paint.setStrokeWidth(stroke);
                paint.setColor(border);
                paint.setShadowLayer(blur, 0, 0, glow);
                canvas.drawRoundRect(r, radius, radius, paint);
                paint.clearShadowLayer();
            }
            paint.setStyle(Paint.Style.FILL);
            if (top == bottom) {
                paint.setShader(null);
                paint.setColor(top);
            } else {
                paint.setColor(Color.WHITE);
                paint.setShader(new LinearGradient(0, r.top, 0, r.bottom, top, bottom, Shader.TileMode.CLAMP));
            }
            canvas.drawRoundRect(r, radius, radius, paint);
            paint.setShader(null);
            if (border != 0) {
                paint.setStyle(Paint.Style.STROKE);
                paint.setStrokeWidth(stroke);
                paint.setColor(border);
                canvas.drawRoundRect(r, radius, radius, paint);
            }
        }

        @Override
        public void setAlpha(int a) {
            paint.setAlpha(a);
            invalidateSelf();
        }

        @Override
        public void setColorFilter(ColorFilter f) {
            paint.setColorFilter(f);
            invalidateSelf();
        }

        @Override
        public int getOpacity() {
            return PixelFormat.TRANSLUCENT;
        }
    }

    /** A colour dot; the selected one has a white ring with a small gap. */
    private final class Swatch extends Drawable {
        private final int colour;
        private final boolean selected;
        private final Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);

        Swatch(int colour, boolean selected) {
            this.colour = colour;
            this.selected = selected;
        }

        /**
         * The colour dot, and for the chosen colour a dark gap and a white ring round it, all inside the 38-unit
         * view: dot 13, gap 2.5, ring 2.5, so the ring's outer edge (18) stays inside it (0.155 notes #7).
         */
        @Override
        public void draw(Canvas canvas) {
            Rect b = getBounds();
            float cx = b.exactCenterX();
            float cy = b.exactCenterY();
            float band = s(5) / 2f;
            float dot = Math.min(s(13), Math.min(b.width(), b.height()) / 2f - 2 * band - s(1));
            paint.setStyle(Paint.Style.FILL);
            paint.setColor(colour);
            canvas.drawCircle(cx, cy, dot, paint);
            if (selected) {
                paint.setStyle(Paint.Style.STROKE);
                paint.setStrokeWidth(band);
                paint.setColor(0xFF16181C);
                canvas.drawCircle(cx, cy, dot + band / 2f, paint);
                paint.setColor(Color.WHITE);
                canvas.drawCircle(cx, cy, dot + band * 1.5f, paint);
            }
        }

        @Override
        public void setAlpha(int a) {
            paint.setAlpha(a);
        }

        @Override
        public void setColorFilter(ColorFilter f) {
            paint.setColorFilter(f);
        }

        @Override
        public int getOpacity() {
            return PixelFormat.TRANSLUCENT;
        }
    }

    /** Wraps its content up to a maximum height, then scrolls; this is what fits the panel to its rows. */
    private static final class MaxHeightScroll extends ScrollView {
        private int max = Integer.MAX_VALUE;

        MaxHeightScroll(Context c) {
            super(c);
        }

        void setMax(int px) {
            max = Math.max(1, px);
            requestLayout();
        }

        @Override
        protected void onMeasure(int widthSpec, int heightSpec) {
            int mode = MeasureSpec.getMode(heightSpec), size = MeasureSpec.getSize(heightSpec);
            int limit = mode == MeasureSpec.UNSPECIFIED ? max : Math.min(max, size);
            super.onMeasure(widthSpec, MeasureSpec.makeMeasureSpec(limit, MeasureSpec.AT_MOST));
        }
    }
}
