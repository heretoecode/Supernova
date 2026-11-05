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
    static final int SUBTITLE_DELAY_STEP_MS = 100;
    static final float SPEED_STEP = 0.05f;

    // Approved mock-up colours.
    private static final int TAB_BORDER = 0xff5a7bd8, TAB_ACTIVE = 0xff2f6df0, FOCUS_BORDER = 0xff7fd6ff;
    private static final int HEADING = SupernovaStyle.HEADING, HEADING_RULE = SupernovaStyle.HEADING_LINE;
    private static final int VALUE = 0xff3fb6ff, META = 0xffb9c7e8, BAR = 0xffcfd8ff, ICON = 0xffe8eeff;
    private static final int ROW_HEIGHT = 74, LIST_ROWS_VISIBLE = 7;

    static final class Track {
        final String label, language;
        final boolean selected, enabled;
        final Runnable select;

        Track(String label, String language, boolean selected, boolean enabled, Runnable select) {
            this.label = label;
            this.language = language;
            this.selected = selected;
            this.enabled = enabled;
            this.select = select;
        }
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
            content.addView(sample, 0, new FrameLayout.LayoutParams(-1, -1));
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
        k = style.width / 640f;
        setOrientation(VERTICAL);
        setTag("subtitles.main");
        setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        int glow = s(14);
        setBackground(new Surface(SupernovaStyle.GLASS, SupernovaStyle.GLASS, SupernovaStyle.GLASS_EDGE, s(26),
                                  Math.max(1, s(2)), 0x66000000, glow));
        setPadding(s(30) + glow, s(30) + glow, s(30) + glow, s(26) + glow);

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
        LayoutParams tabsSize = new LayoutParams(-1, s(72));
        tabsSize.bottomMargin = s(12);
        header.addView(tabs, tabsSize);

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

    int panelWidth() {
        return style.width;
    }

    int panelHeight() {
        return style.height;
    }

    void place(Window window) {
        style.placeSide(window);
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
    private Drawable rowFocus() {
        return SupernovaStyle.rowFocus(owner);
    }

    private LayoutParams tabSize(int index) {
        LayoutParams size = new LayoutParams(0, -1, 1);
        if (index > 0)
            size.leftMargin = s(10);
        return size;
    }

    private View tab(int index, int id, String name, String iconKind) {
        LinearLayout t = new LinearLayout(owner);
        t.setTag("playback.tab:" + id);
        t.setGravity(Gravity.CENTER);
        t.setFocusable(true);
        t.setFocusableInTouchMode(true);
        t.setDescendantFocusability(FOCUS_BLOCK_DESCENDANTS);
        t.addView(icon(iconKind, 22));
        TextView label = text(name, 19, Color.WHITE, true);
        label.setSingleLine(true);
        label.setPadding(s(8), 0, 0, 0);
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
            boolean active = ("playback.tab:" + tab).equals(t.getTag());
            t.setSelected(active);
            // Approved global button style: tabs never change size; selected = thin white outline; focus = white box.
            SupernovaStyle.applyButton(t, SupernovaStyle.Surface.PANEL);
        }
    }

    /** More-menu heading: sentence case, soft grey, with a thin line alongside. No lines between rows. */
    private void section(String title, boolean first) {
        LinearLayout heading = new LinearLayout(owner);
        heading.setGravity(Gravity.CENTER_VERTICAL);
        heading.setImportantForAccessibility(IMPORTANT_FOR_ACCESSIBILITY_NO);
        heading.setPadding(s(6), s(first ? 10 : 22), s(6), s(6));
        heading.setTag("playback.heading");
        TextView label = text(sentenceCase(title), 18, HEADING, false);
        heading.addView(label);
        View line = new View(owner);
        line.setBackgroundColor(HEADING_RULE);
        LayoutParams lineSize = new LayoutParams(0, Math.max(1, s(2)), 1);
        lineSize.leftMargin = s(14);
        heading.addView(line, lineSize);
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
        row.setMinimumHeight(Math.round(s(ROW_HEIGHT) * owner.getResources().getConfiguration().fontScale));
        row.setPadding(s(16), 0, s(16), 0);
        row.setFocusable(true);
        row.setFocusableInTouchMode(true);
        row.setDescendantFocusability(FOCUS_BLOCK_DESCENDANTS);
        row.setBackground(rowFocus());
        row.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        if (iconKind != null) {
            FrameLayout box = new FrameLayout(owner);
            ImageView image = icon(iconKind, 28);
            box.addView(image, new FrameLayout.LayoutParams(s(28), s(28), Gravity.CENTER_VERTICAL));
            row.addView(box, new LayoutParams(s(44), -2));
        }
        TextView label = text(title, 21, Color.WHITE, true);
        label.setSingleLine(true);
        label.setEllipsize(TextUtils.TruncateAt.END);
        row.addView(label, new LayoutParams(0, -2, 1));
        row.setContentDescription(title);
        row.setOnKeyListener((v, key, event) -> rowKeys(row, key, event, null));
        body.addView(row, new LayoutParams(-1, -2));
        return row;
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
        TextView v = text(c, 26, Color.WHITE, false);
        v.setGravity(Gravity.CENTER);
        return v;
    }

    /** {@code ‹ value ›}: the value in accent blue between two chevrons. */
    private TextView stepper(LinearLayout row, float minWidth, Stepper stepper) {
        LinearLayout box = new LinearLayout(owner);
        box.setGravity(Gravity.CENTER_VERTICAL);
        box.addView(chevron("‹"));
        TextView value = text("", 20, VALUE, false);
        value.setSingleLine(true);
        value.setEllipsize(TextUtils.TruncateAt.END);
        value.setGravity(Gravity.CENTER);
        value.setMinWidth(s(minWidth));
        value.setMaxWidth(s(300));
        value.setTag("value");
        LayoutParams v = new LayoutParams(-2, -2);
        v.leftMargin = s(18);
        v.rightMargin = s(18);
        box.addView(value, v);
        box.addView(chevron("›"));
        row.addView(box);
        row.setOnKeyListener((view, key, event) -> rowKeys(row, key, event, stepper));
        return value;
    }

    /** {@code − value +}: used for the millisecond delays. */
    private TextView plusMinus(LinearLayout row, Stepper stepper) {
        TextView minus = chevron("−"), plus = chevron("+");
        TextView value = text("", 20, Color.WHITE, false);
        value.setGravity(Gravity.CENTER);
        value.setTag("value");
        row.addView(minus);
        row.addView(value, new LayoutParams(s(104), -2));
        row.addView(plus);
        row.setOnKeyListener((view, key, event) -> rowKeys(row, key, event, stepper));
        return value;
    }

    private ImageView switchView(LinearLayout row) {
        ImageView value = new ImageView(owner);
        int h = s(30);
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

    void showTab(int id) {
        if (closed)
            return;
        building = true;
        try {
            page = null;
            tab = id;
            header.setVisibility(VISIBLE);
            styleTabs();
            body.removeAllViews();
            bindings.clear();
            scroll.setMax(maxPanelHeight() - s(30 + 72 + 12 + 26) - s(28));
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

    void focusTab() {
        View t = tabs.findViewWithTag("playback.tab:" + tab);
        if (t != null)
            t.requestFocus();
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
        for (int i = 0; i < body.getChildCount(); i++) {
            View child = body.getChildAt(i);
            if (child.isFocusable() && child.isEnabled() && child.requestFocus())
                return;
        }
    }

    private void buildSubtitles() {
        section("SUBTITLE SELECTION", true);
        toggle("enabled", "cc", "Subtitles");

        LinearLayout track = row("subtitles.row.track", "list", "Subtitle Track");
        TextView current = stepper(track, 70, direction -> stepTrack(backend.tracks(), direction));
        bindings.add(() -> bindTrack(track, current, backend.tracks(), "Subtitle Track", "None"));
        track.setOnClickListener(v -> openTracks("subtitles"));

        LinearLayout download = row("subtitles.row.download", "download", "Download Subtitles");
        download.addView(text("Find & Download", 20, Color.WHITE, false));
        TextView arrow = chevron("›");
        arrow.setPadding(s(12), 0, 0, 0);
        download.addView(arrow);
        download.setOnClickListener(v -> backend.download());

        section("SUBTITLE APPEARANCE", false);
        choices("size", "size", "Text Size", new String[] {"Small", "Medium", "Large"}, new int[] {25, 50, 75});
        swatches();
        // Approved 0.156 #24: one step control; Right raises by 5% of the screen, Left lowers; moves live.
        LinearLayout position = row("subtitles.row.position", "position", "Vertical Position");
        TextView height = stepper(position, 110, direction -> backend.set(
            "position", SubtitleVerticalPosition.step(backend.get("position") + direction * POSITION_STEP)));
        bindings.add(() -> {
            String label = positionLabel(backend.get("position"));
            height.setText(label);
            position.setContentDescription("Vertical Position, " + label);
        });
        toggle("outline", "outline", "Outline");
        toggle("background", "background", "Background");

        LinearLayout opacity = row("subtitles.row.opacity", "opacity", "Background Opacity");
        TextView amount = stepper(opacity, 70, direction -> stepValue("opacity", OPACITY, direction));
        bindings.add(() -> {
            String label = labelFor(backend.get("opacity"), OPACITY, OPACITY_LABELS);
            amount.setText(label);
            opacity.setContentDescription("Background Opacity, " + label);
        });

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

        bindings.add(() -> {
            boolean available = backend.available(), on = backend.get("enabled") != 0;
            for (String key : new String[] {"size", "colour", "position", "outline", "background"})
                dim(body.findViewWithTag("subtitles.row." + key), available);
            dim(opacity, available && backend.get("background") != 0);
            dim(delay, available && on);
        });
    }

    private void buildAudio() {
        List<Track> tracks = backend.audioTracks();
        if (!tracks.isEmpty()) {
            section("AUDIO SELECTION", true);
            LinearLayout track = row("audio.row.track", "note", "Audio Track");
            TextView current = stepper(track, 70, direction -> stepTrack(backend.audioTracks(), direction));
            bindings.add(() -> bindTrack(track, current, backend.audioTracks(), "Audio Track", "—"));
            track.setOnClickListener(v -> openTracks("audio"));
        }
        boolean adjustments = backend.audioDelay() != null || backend.option("boost") != null ||
                              backend.option("night") != null || backend.option("spatial") != null;
        if (!adjustments)
            return;
        section("AUDIO ADJUSTMENTS", tracks.isEmpty());
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
        option("boost", "boost", "Audio Boost");
        option("night", "moon", "Night Mode");
        option("spatial", "surround", "Spatial Audio");
    }

    private void buildPlayback() {
        boolean picture = !backend.choices("format").isEmpty() || !backend.choices("s3d").isEmpty() ||
                          backend.pictureInPicture() != null;
        if (picture) {
            section("PICTURE", true);
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
        boolean playback =
            backend.speed() != null || !backend.choices("playmode").isEmpty() || backend.option("skip") != null;
        if (!playback)
            return;
        section("PLAYBACK", !picture);
        if (backend.speed() != null) {
            LinearLayout row = row("playback.row.speed", "speed", "Playback Speed");
            TextView value = stepper(row, 70, direction -> {
                Float speed = backend.speed();
                if (speed != null)
                    backend.setSpeed(speed + direction * SPEED_STEP);
            });
            bindings.add(() -> {
                Float speed = backend.speed();
                String label = formatSpeed(speed == null ? 1f : speed);
                value.setText(label);
                row.setContentDescription("Playback Speed, " + label);
            });
        }
        choiceStepper("playmode", "repeat", "Play Mode");
        option("skip", "skip", "Skip Intros & Credits");
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
            value.setImageDrawable(StandardSwitchDrawable.snapshot(owner, on));
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
        ImageView value = switchView(row);
        bindings.add(() -> {
            Boolean on = backend.option(key);
            boolean checked = on != null && on;
            value.setImageDrawable(StandardSwitchDrawable.snapshot(owner, checked));
            row.setContentDescription(label + ", " + (checked ? "On" : "Off"));
            dim(row, on != null && backend.optionEnabled(key));
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
        LinearLayout box = new LinearLayout(owner);
        box.setGravity(Gravity.CENTER_VERTICAL);
        row.addView(box);
        TextView[] texts = new TextView[labels.length];
        for (int i = 0; i < labels.length; i++) {
            if (i > 0) {
                TextView bar = text("|", 20, BAR, false);
                bar.setPadding(s(9), 0, s(9), 0);
                box.addView(bar);
            }
            texts[i] = text(labels[i], 20, Color.WHITE, false);
            box.addView(texts[i]);
        }
        bindings.add(() -> {
            int value = backend.get(key);
            for (int i = 0; i < values.length; i++)
                texts[i].setTextColor(value == values[i] ? VALUE : Color.WHITE);
            String spoken = "Custom (" + value + ")";
            for (int i = 0; i < values.length; i++)
                if (value == values[i])
                    spoken = labels[i];
            row.setContentDescription(title + ", " + spoken);
        });
        row.setOnClickListener(v -> {
            stepValue(key, values, 1);
            refresh();
        });
        row.setOnKeyListener(
            (v, keyCode, event) -> rowKeys(row, keyCode, event, direction -> stepValue(key, values, direction)));
    }

    private void swatches() {
        LinearLayout row = row("subtitles.row.colour", "palette", "Text Colour");
        ImageView[] dots = new ImageView[COLOURS.length];
        for (int i = 0; i < COLOURS.length; i++) {
            dots[i] = new ImageView(owner);
            LayoutParams size = new LayoutParams(s(38), s(38));
            if (i > 0)
                size.leftMargin = s(3);
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

    private void choiceStepper(String key, String iconKind, String title) {
        if (backend.choices(key).isEmpty())
            return;
        LinearLayout row = row("playback.row." + key, iconKind, title);
        TextView value = stepper(row, 70, direction -> {
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
        });
        bindings.add(() -> {
            String label = "—";
            for (Choice c : backend.choices(key))
                if (c.selected)
                    label = c.label;
            value.setText(label);
            row.setContentDescription(title + ", " + label);
        });
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
        row.setContentDescription(title + ", " + value.getText() +
                                  ". Left and Right change track; OK shows all tracks.");
    }

    // ---- in-panel track list ---------------------------------------------------------------

    private void openTracks(String which) {
        if (closed)
            return;
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
            if (initial != null)
                initial.requestFocus();
            else
                back.requestFocus();
        } finally {
            building = false;
        }
        scroll.post(this::updateMore);
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
        row.addView(label, new LayoutParams(0, -2, 1));
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
