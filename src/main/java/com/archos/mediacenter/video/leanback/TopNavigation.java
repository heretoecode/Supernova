package com.archos.mediacenter.video.leanback;

import android.animation.ValueAnimator;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Shader;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.FrameLayout;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextClock;
import android.widget.TextView;
import androidx.preference.PreferenceManager;
import com.archos.mediacenter.video.BuildConfig;
import com.archos.mediacenter.video.diagnostics.Diagnostics;
import com.archos.mediacenter.video.leanback.filebrowsing.UniversalFileBrowser;
import java.util.function.IntConsumer;
import java.util.function.BooleanSupplier;

/** Optional navigation shell; the same native BrowseSupportFragment owns all library rows. */
public final class TopNavigation extends LinearLayout {
    public interface ContentFocus {
        boolean focusFromNavigation();
    }
    private final LinearLayout bar;
    private final TextClock clock;
    private int navigationInset;
    private final PreviewFocusRail group;
    private static final int[] VISUAL_ORDER = {0, 1, 2, 6, 3, 5, 4};
    private final PreviewBackdrop artwork;
    private final View content;
    private final FrameLayout stage;
    private final boolean scrollingContent;
    private final FrameLayout scanStatus;
    private TopBarSignal signal;
    private final FrameLayout status;
    private final BooleanSupplier firstRow;
    private TextView selected;
    private int selectedIndex;
    private final TextView[] tabs = new TextView[7];
    private final Paint fadePaint = new Paint(Paint.ANTI_ALIAS_FLAG);

    public TopNavigation(Context c, View content, IntConsumer navigate, BooleanSupplier firstRow) {
        super(c);
        this.content = content;
        this.firstRow = firstRow;
        scanStatus = new FrameLayout(c);
        scanStatus.setFocusable(false);
        scanStatus.setDescendantFocusability(FOCUS_BLOCK_DESCENDANTS);
        scanStatus.setVisibility(GONE);
        setOrientation(VERTICAL);
        TvLayout.applyInsets(this);
        setWillNotDraw(false);
        artwork = new PreviewBackdrop(c);
        artwork.setFoundationHome(BuildConfig.FOUNDATION);
        // One plain charcoal behind every page, Home and the library pages included (owner, 17 Nov 2026; F27).
        setBackground(BuildConfig.FOUNDATION ? new PreviewUtilityBackground(c) : artwork);
        bar = new LinearLayout(c);
        bar.setClipChildren(true);
        bar.setClipToPadding(true);
        bar.setGravity(Gravity.CENTER_VERTICAL);
        bar.setPadding(TvLayout.sideMargin(c), 0, TvLayout.sideMargin(c), 0);
        bar.setBackgroundColor(Color.TRANSPARENT);
        // 0.158 top bar (owner, 18 Nov 2026; RULES §2): no SUPERNOVA wordmark, the places start at the bar's edge.
        group = new PreviewFocusRail(c);
        // Focus and path (RULES §3): the tabs show the white box and the underlines themselves.
        group.drawNothing();
        group.setClipChildren(false);
        group.setClipToPadding(false);
        group.setGravity(Gravity.START | Gravity.CENTER_VERTICAL);
        HorizontalScrollView rail = new HorizontalScrollView(c);
        rail.setHorizontalScrollBarEnabled(false);
        rail.setFillViewport(true);
        rail.setClipChildren(true);
        rail.setClipToPadding(true);
        rail.setFocusable(false);
        rail.addView(group, new FrameLayout.LayoutParams(-2, -1));
        bar.addView(rail, new LayoutParams(0, -1, 1));
        String[] labels = {"Home", "Movies", "TV Shows", "Files", "Settings", "Search", customLabel(c)};
        for (int i = 0; i < labels.length; i++) {
            final int index = i;
            TextView tab = new BarWord(c);
            tabs[i] = tab;
            tab.setText(labels[i]);
            tab.setContentDescription(labels[i]);
            tab.setTag("semantic:topnav." +
                       new String[] {"home", "movies", "tv", "network", "settings", "search", "custom"}[index]);
            tab.setTextColor(Color.WHITE);
            tab.setTextSize(19 * TvLayout.scale(c));
            tab.setGravity(Gravity.CENTER);
            // One font, weight and size for every word and the clock (owner, 18 Nov 2026; RULES §2).
            tab.setTypeface(barTypeface());
            tab.setIncludeFontPadding(false);
            if (index == 6) {
                tab.setMaxWidth(dp(140));
                tab.setEllipsize(TextUtils.TruncateAt.END);
            }
            tab.setSingleLine(true);
            tab.setFocusable(true);
            tab.setFocusableInTouchMode(true);
            tab.setClickable(true);
            // Compact gutters keep complete icon targets inside the 720p rail after pixel rounding.
            // One shape for every small box (RULES §3): about 22 px side padding at 1080p.
            int horizontal = TvLayout.width(c) < 780 ? 6 : Math.round(SupernovaStyle.SMALL_BOX_SIDE_DP);
            tab.setPadding(dp(horizontal), dp(3), dp(horizontal), dp(3));
            styleTab(tab);
            tab.setOnClickListener(v -> {
                if (index == 6 && getContext() instanceof CustomLibraryActivity)
                    return;
                requestBrowserExit(content, () -> {
                    if (index == 6) {
                        if (getContext() instanceof CustomLibraryActivity)
                            return;
                        Runnable open = ()
                            -> getContext().startActivity(
                                new Intent(getContext(), CustomLibraryActivity.class));
                        open.run();
                        return;
                    }
                    selectTab(index);
                    navigate.accept(index);
                });
            });
            tab.setOnKeyListener((v, key, event) -> {
                if (key == KeyEvent.KEYCODE_DPAD_DOWN && event.getAction() == KeyEvent.ACTION_DOWN) {
                    if (!focusLibraryEntry(content))
                        content.requestFocus();
                    return true;
                }
                if (event.getAction() == KeyEvent.ACTION_DOWN) {
                    if (key == KeyEvent.KEYCODE_DPAD_UP)
                        return true;
                    if (key == KeyEvent.KEYCODE_DPAD_LEFT || key == KeyEvent.KEYCODE_DPAD_RIGHT) {
                        for (int position = 0; position < VISUAL_ORDER.length; position++) {
                            if (VISUAL_ORDER[position] != index)
                                continue;
                            int target = position + (key == KeyEvent.KEYCODE_DPAD_LEFT ? -1 : 1);
                            if (target >= 0 && target < VISUAL_ORDER.length)
                                tabs[VISUAL_ORDER[target]].requestFocus();
                            return true;
                        }
                    }
                }
                return false;
            });
            // Tools are words like the places (Files · Search · Settings, no icons; `files-review-0157-1/09` A).
            if (i == 3) {
                View gap = new View(c);
                group.addView(gap, new LayoutParams(0, 1, 1));
                // The signal slot left of Files: scan ring, attention dot or nothing (RULES §2).
                signal = new TopBarSignal(c);
                signal.setTag("semantic:topnav.signal");
                LayoutParams slot = new LayoutParams(dp(14), dp(14));
                slot.gravity = Gravity.CENTER_VERTICAL;
                slot.rightMargin = dp(2);
                group.addView(signal, slot);
            }
            group.addView(tab, new LayoutParams(-2, dp(Math.round(SupernovaStyle.SMALL_BOX_HEIGHT_DP))));
        }
        group.removeView(tabs[6]);
        group.addView(tabs[6], group.indexOfChild(tabs[2]) + 1, new LayoutParams(-2, dp(Math.round(SupernovaStyle.SMALL_BOX_HEIGHT_DP))));
        group.removeView(tabs[5]);
        group.addView(tabs[5], group.indexOfChild(tabs[4]), new LayoutParams(-2, dp(Math.round(SupernovaStyle.SMALL_BOX_HEIGHT_DP))));
        status = new FrameLayout(c);
        bar.addView(status, new LayoutParams(dp(64), dp(46)));
        clock = new BarClock(c);
        clock.setSingleLine(true);
        clock.setTag("preview-default-clock");
        clock.setFormat12Hour("h:mm");
        clock.setFormat24Hour("HH:mm");
        clock.setTypeface(barTypeface());
        clock.setTextSize(19 * TvLayout.scale(c));
        clock.setTextColor(Color.WHITE);
        clock.setGravity(Gravity.CENTER_VERTICAL | Gravity.END);
        clock.setIncludeFontPadding(false);
        clock.setPadding(0, dp(6), 0, dp(6));
        status.addView(clock, new FrameLayout.LayoutParams(-1, -1));
        updateCustomPage();
        selected = tabs[0];
        selected.setSelected(true);
        addView(bar, new LayoutParams(-1, dp(52)));
        stage = new FrameLayout(c);
        navigationInset = dp(52);
        scrollingContent = prepareScrollingContent(content);
        stage.addView(content, new FrameLayout.LayoutParams(-1, -1));
        FrameLayout.LayoutParams scanParams =
            new FrameLayout.LayoutParams(-2, -2, Gravity.BOTTOM | Gravity.END);
        scanParams.setMargins(dp(20), dp(12), dp(20), dp(16));
        stage.addView(scanStatus, scanParams);
        addView(stage, new LayoutParams(-1, 0, 1));
        setChildrenDrawingOrderEnabled(true);
    }

    /** Insets belong to the scroll viewport so scrolled pixels can travel behind the bar. */
    private boolean prepareScrollingContent(View view) {
        if (view.getVisibility() == GONE)
            return false;
        if (view instanceof PreviewPages) {
            ((PreviewPages)view).setNavigationInset(dp(52));
            return true;
        }
        if (view instanceof ScrollView) {
            view.setPadding(view.getPaddingLeft(), view.getPaddingTop() + dp(52), view.getPaddingRight(),
                            view.getPaddingBottom());
            ((ScrollView)view).setClipToPadding(false);
            return true;
        }
        if (view instanceof FrameLayout) {
            ViewGroup group = (ViewGroup)view;
            for (int i = 0; i < group.getChildCount(); i++)
                if (prepareScrollingContent(group.getChildAt(i)))
                    return true;
        }
        return false;
    }

    @Override
    protected void onMeasure(int width, int height) {
        int tabHeight = Math.max(
            dp(Math.round(SupernovaStyle.SMALL_BOX_HEIGHT_DP)), Math.round(tabs[0].getPaint().getFontMetrics().bottom - tabs[0].getPaint().getFontMetrics().top) +
                        tabs[0].getPaddingTop() + tabs[0].getPaddingBottom());
        for (TextView tab : tabs)
            tab.getLayoutParams().height = tabHeight;
        int inset = Math.max(dp(52), tabHeight + dp(12));
        bar.getLayoutParams().height = inset;
        status.getLayoutParams().width =
            Math.max(dp(64), (int)Math.ceil(clock.getPaint().measureText("00:00")) + dp(8));
        status.getLayoutParams().height = Math.max(dp(46), tabHeight);
        if (scrollingContent && inset != navigationInset)
            updateNavigationInset(content, inset, inset - navigationInset);
        navigationInset = inset;
        super.onMeasure(width, height);
        if (scrollingContent)
            stage.measure(View.MeasureSpec.makeMeasureSpec(getMeasuredWidth(), View.MeasureSpec.EXACTLY),
                          View.MeasureSpec.makeMeasureSpec(getMeasuredHeight(), View.MeasureSpec.EXACTLY));
    }

    private boolean updateNavigationInset(View view, int inset, int delta) {
        if (view.getVisibility() == GONE)
            return false;
        if (view instanceof PreviewPages) {
            ((PreviewPages)view).setNavigationInset(inset);
            return true;
        }
        if (view instanceof ScrollView) {
            view.setPadding(view.getPaddingLeft(), view.getPaddingTop() + delta, view.getPaddingRight(),
                            view.getPaddingBottom());
            return true;
        }
        if (view instanceof FrameLayout) {
            ViewGroup children = (ViewGroup)view;
            for (int n = 0; n < children.getChildCount(); n++)
                if (updateNavigationInset(children.getChildAt(n), inset, delta))
                    return true;
        }
        return false;
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        if (scrollingContent)
            stage.layout(0, 0, getWidth(), getHeight());
    }

    @Override
    protected int getChildDrawingOrder(int count, int position) {
        return count == 2 ? 1 - position : position;
    }

    private final SharedPreferences.OnSharedPreferenceChangeListener accentListener = (prefs, key) -> {
        if ("preview_accent41".equals(key))
            refreshAccent();
        else if (CustomLibraryPage.KEY.equals(key))
            updateCustomPage();
    };

    private void refreshAccent() {
        for (TextView tab : tabs)
            styleTab(tab);
        group.refreshColour();
        invalidate();
    }

    private void styleTab(TextView tab) {
        tab.setTextColor(Color.WHITE);
        tab.setShadowLayer(0, 0, 0, 0);
        tab.setBackground(null);
        for (Drawable icon : tab.getCompoundDrawables())
            if (icon instanceof PreviewIcon)
                ((PreviewIcon)icon).focus(false, 0);
    }

    private final ViewTreeObserver.OnGlobalFocusChangeListener pathListener = (before, after) -> {
        openOnFocus(before, after);
        refreshPath();
    };

    /** The soft grey box behind the open page while focus is elsewhere (`files-review-0157-1/08`). */
    static final int OPEN_PAGE_BOX = 0x26FFFFFF;
    /** The open page's faded word inside its soft grey box. */
    static final int OPEN_PAGE_INK = 0xB8FFFFFF;
    /** How long focus rests on a media page before it opens; passing over it on the way opens nothing. */
    static final int OPEN_ON_FOCUS_MS = 250;

    /**
     * The 0.158 top bar (owner, 18 Nov 2026; RULES §2): a plain word; the white box with black text where the remote
     * is; a soft grey box with a faded word for the open page while focus is anywhere else. No underlines.
     */
    void refreshPath() {
        for (TextView tab : tabs) {
            if (tab == null)
                continue;
            boolean focused = tab.isFocused();
            // Owner, 21 Nov: once focus lands on another media page, the old page drops its grey box at once,
            // not after the new page has loaded; a tool (Files, Search, Settings) needs OK, so the page keeps it.
            boolean open = tab == selected && !focused && pendingOpen < 0;
            tab.setTextColor(focused ? SupernovaStyle.FOCUS_TEXT : open ? OPEN_PAGE_INK : Color.WHITE);
            if (tab != tabs[6] || tab.getAlpha() > .99f)
                tab.setAlpha(1f);
            for (Drawable icon : tab.getCompoundDrawables())
                if (icon != null)
                    icon.setColorFilter(new android.graphics.PorterDuffColorFilter(
                        focused ? SupernovaStyle.FOCUS_TEXT : open ? OPEN_PAGE_INK : Color.WHITE,
                        android.graphics.PorterDuff.Mode.SRC_IN));
            if (focused || open) {
                android.graphics.drawable.GradientDrawable box = new android.graphics.drawable.GradientDrawable();
                box.setColor(focused ? SupernovaStyle.FOCUS_FILL : OPEN_PAGE_BOX);
                box.setCornerRadius(dp(Math.round(SupernovaStyle.SMALL_BOX_RADIUS_DP)));
                tab.setBackground(box);
            } else {
                tab.setBackground(null);
            }
        }
    }

    /** True for the pages that open as focus lands on them: Home, Movies, TV Shows and your own page. */
    static boolean opensOnFocus(int index) {
        return index <= 2 || index == 6;
    }

    private int pendingOpen = -1;
    private final Runnable openFocusedPage = () -> {
        int index = pendingOpen;
        pendingOpen = -1;
        if (index < 0 || !tabs[index].isFocused() || index == selectedIndex) {
            refreshPath();
            return;
        }
        if (index == 6 && getContext() instanceof CustomLibraryActivity)
            return;
        tabs[index].performClick();
    };

    private int tabIndex(View view) {
        for (int i = 0; i < tabs.length; i++)
            if (tabs[i] == view)
                return i;
        return -1;
    }

    /** Media pages open once focus, moved along the bar, rests on them; tools wait for OK. */
    private void openOnFocus(View before, View after) {
        removeCallbacks(openFocusedPage);
        pendingOpen = -1;
        int index = tabIndex(after);
        if (index < 0 || tabIndex(before) < 0 || !opensOnFocus(index) || index == selectedIndex)
            return;
        pendingOpen = index;
        postDelayed(openFocusedPage, OPEN_ON_FOCUS_MS);
    }

    private static Typeface barTypeface() {
        return Typeface.create("sans-serif", Typeface.NORMAL);
    }

    /**
     * How far to move a line of text so its letters, not the font's box, sit on the view's centre (owner, 18 Nov
     * 2026): room kept for accents and descenders otherwise makes words sit low.
     */
    static float letterCentreShift(TextView view) {
        if (view.getLayout() == null || view.getHeight() == 0)
            return 0f;
        Rect letters = new Rect();
        view.getPaint().getTextBounds("H0", 0, 2, letters);
        float letterCentre = view.getBaseline() + letters.exactCenterY();
        return view.getHeight() / 2f - letterCentre;
    }

    /** A top-bar word centred on its letters. */
    @android.annotation.SuppressLint("AppCompatCustomView")
    static final class BarWord extends TextView {
        BarWord(Context context) {
            super(context);
        }

        @Override
        protected void onDraw(Canvas canvas) {
            float shift = letterCentreShift(this);
            canvas.save();
            canvas.translate(0, shift);
            super.onDraw(canvas);
            canvas.restore();
        }
    }

    /** The clock, in the bar's one font and centred on its digits like the words. */
    @android.annotation.SuppressLint("AppCompatCustomView")
    static final class BarClock extends TextClock {
        BarClock(Context context) {
            super(context);
        }

        @Override
        protected void onDraw(Canvas canvas) {
            float shift = letterCentreShift(this);
            canvas.save();
            canvas.translate(0, shift);
            super.onDraw(canvas);
            canvas.restore();
        }
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        PreferenceManager.getDefaultSharedPreferences(getContext())
            .registerOnSharedPreferenceChangeListener(accentListener);
        for (TextView tab : tabs)
            styleTab(tab);
        getViewTreeObserver().addOnGlobalFocusChangeListener(pathListener);
        refreshPath();
    }

    private boolean scrolled;
    private ValueAnimator scrimAnimation;
    private int scrimAlpha;

    public void setScrolled(boolean value) {
        value = value && (selectedIndex >= 0 && selectedIndex <= 2 || selectedIndex == 6);
        if (value && selectedIndex >= 3 && selectedIndex <= 5)
            value = false;
        if (scrolled == value)
            return;
        scrolled = value;
        if (scrimAnimation != null)
            scrimAnimation.cancel();
        scrimAnimation = ValueAnimator.ofInt(scrimAlpha, value ? 255 : 0);
        scrimAnimation.setDuration(220);
        scrimAnimation.addUpdateListener(animation -> {
            scrimAlpha = (int)animation.getAnimatedValue();
            invalidate();
        });
        scrimAnimation.start();
    }

    @Override
    protected boolean drawChild(Canvas canvas, View child, long time) {
        if (child == stage) {
            // The floating "Scanning…" pill is retired: the top bar's signal slot shows scans (RULES §2).
            if (scanStatus.getVisibility() != GONE)
                scanStatus.setVisibility(GONE);
        }

        if (child == bar)
            drawFade(canvas);
        return super.drawChild(canvas, child, time);
    }

    /** The speckle's base colour at its strongest behind the tabs, clear ~115dp down. */
    static final int FADE_COLOUR = 0x111215;
    static final float FADE_TOP = .88f, FADE_AT_TABS = .60f;
    static final int FADE_HEIGHT_DP = 115, FADE_IN_DP = 60;

    /**
     * Owner-approved 4 Nov 2026 (docs/design/proposals/top-bar/01–04): content passes behind the bar under a charcoal
     * fade that eases in over the first ~60dp of scroll and out again; nothing at all at the top of a page; no blur
     * and no blue.
     */
    private void drawFade(Canvas canvas) {
        if (scrimAlpha <= 0)
            return;
        float height = dp(FADE_HEIGHT_DP);
        int top = Math.round(255 * FADE_TOP), tabs = Math.round(255 * FADE_AT_TABS);
        fadePaint.setShader(new LinearGradient(0, 0, 0, height,
                                               new int[] {top << 24 | FADE_COLOUR, tabs << 24 | FADE_COLOUR,
                                                          FADE_COLOUR},
                                               new float[] {0, .49f, 1}, Shader.TileMode.CLAMP));
        fadePaint.setAlpha(scrimAlpha);
        canvas.drawRect(0, 0, getWidth(), height, fadePaint);
        fadePaint.setShader(null);
    }

    /** How far the page has scrolled, in pixels: the fade follows it (0 at the top, full after ~60dp). */
    public void setScrollOffset(int pixels) {
        boolean allowed = selectedIndex >= 0 && selectedIndex <= 2 || selectedIndex == 6;
        int alpha = allowed ? Math.max(0, Math.min(255, Math.round(255f * pixels / dp(FADE_IN_DP)))) : 0;
        if (scrimAnimation != null)
            scrimAnimation.cancel();
        scrolled = alpha > 0;
        if (alpha != scrimAlpha) {
            scrimAlpha = alpha;
            invalidate();
        }
    }

    int fadeAlpha() {
        return scrimAlpha;
    }

    public void setArtwork(Uri uri) {
        artwork.load(uri);
    }

    public void setFeaturedDirection(int direction) {
        artwork.setMotionDirection(direction);
    }

    public boolean readyForFirstFrame() {
        return artwork.readyForFirstFrame();
    }

    /** Gives D-pad focus to the currently selected top-navigation tab. */
    public boolean focusSelectedTab() {
        return selected != null && selected.requestFocus();
    }

    public void selectTab(int index) {
        if (index < 0 || index >= 7)
            return;
        boolean moveNavigationFocus = bar.hasFocus() && selectedIndex != index;
        // D18: leaving Home or a library page for Files or Settings releases the artwork held in memory.
        boolean artworkPage = selectedIndex <= 2 || selectedIndex == 6;
        if (artworkPage && selectedIndex != index && (index == 3 || index == 5))
            com.archos.mediacenter.video.CustomApplication.releaseArtwork();
        selectedIndex = index;
        artwork.setPage(index);
        artwork.setFoundationHome(BuildConfig.FOUNDATION && index == 0);
        // Plain charcoal on every page (F27): Home and the library pages match Files, Settings and Search exactly.
        setBackground(BuildConfig.FOUNDATION || index >= 3 && index <= 5 ? new PreviewUtilityBackground(getContext())
                                                                         : artwork);
        for (TextView t : tabs)
            t.setSelected(false);
        selected = tabs[index];
        selected.setSelected(true);
        refreshPath();
        if (index >= 3 && index <= 5)
            setScrolled(false);
        if (moveNavigationFocus)
            selected.requestFocus();
    }

    @Override
    protected void onDetachedFromWindow() {
        PreferenceManager.getDefaultSharedPreferences(getContext())
            .unregisterOnSharedPreferenceChangeListener(accentListener);
        if (scrimAnimation != null)
            scrimAnimation.cancel();
        scrimAlpha = scrolled ? 255 : 0;
        artwork.release();
        getViewTreeObserver().removeOnGlobalFocusChangeListener(pathListener);
        removeCallbacks(openFocusedPage);
        super.onDetachedFromWindow();
    }

    /** Exactly one visible scan-status owner: the landing panel or this shell. */
    public void setEmbeddedScanStatus(boolean embedded) {
        // Kept for callers; the pill no longer shows anywhere under the top bar (the signal slot replaces it).
        scanStatus.setVisibility(GONE);
    }

    public FrameLayout getScanContainer() {
        return scanStatus;
    }

    public FrameLayout getStatusContainer() {
        return status;
    }

    private int dp(int n) {
        return TvLayout.dimension(getContext(), n);
    }

    private static UniversalFileBrowser visibleBrowser(View view) {
        if (!view.isShown())
            return null;
        if (view instanceof UniversalFileBrowser)
            return (UniversalFileBrowser)view;
        if (view instanceof ViewGroup) {
            ViewGroup group = (ViewGroup)view;
            for (int i = 0; i < group.getChildCount(); i++) {
                UniversalFileBrowser result =
                    visibleBrowser(group.getChildAt(i));
                if (result != null)
                    return result;
            }
        }
        return null;
    }

    private static String customLabel(Context c) {
        CustomLibraryPage page = CustomLibraryPage.load(c);
        return page == null ? "+" : page.name;
    }

    /** While a page is being edited the tab shows the draft name; a page never saved yet is dimmed with a draft dot. */
    public void previewCustomPage(String name, boolean draft) {
        tabs[6].setCompoundDrawables(null, null, null, null);
        tabs[6].setText(draft ? name + "  •" : name);
        tabs[6].setAlpha(draft ? .6f : 1f);
        tabs[6].setContentDescription(draft ? name + ", unsaved new page" : name);
    }

    public void updateCustomPage() {
        tabs[6].setAlpha(1f);
        CustomLibraryPage page = CustomLibraryPage.load(getContext());
        PreviewIcon.apply(tabs[6], page == null ? "plus" : "", 22);
        if (page != null)
            tabs[6].setCompoundDrawables(null, null, null, null);
        tabs[6].setText(page == null ? "" : page.name);
        tabs[6].setContentDescription(CustomLibraryPage.load(getContext()) == null ? "Create a New Page"
                                                                                   : customLabel(getContext()));
    }

    private static PageExitGuard editor(View view) {
        if (!view.isShown())
            return null;
        if (view instanceof PageExitGuard)
            return (PageExitGuard)view;
        if (view instanceof ViewGroup) {
            ViewGroup group = (ViewGroup)view;
            for (int i = 0; i < group.getChildCount(); i++) {
                PageExitGuard found = editor(group.getChildAt(i));
                if (found != null)
                    return found;
            }
        }
        return null;
    }

    private static void requestBrowserExit(View content, Runnable action) {
        PageExitGuard guard = editor(content);
        if (guard != null) {
            guard.requestExit(action);
            return;
        }

        UniversalFileBrowser browser = visibleBrowser(content);
        if (browser == null)
            action.run();
        else
            browser.requestExit(action);
    }

    /** FD-033 lower tabs retain the selected title, without the unrelated application bar. */
    public void setDetailsExpanded(boolean expanded) {
        int visibility = expanded ? INVISIBLE : VISIBLE;
        if (bar.getVisibility() != visibility)
            bar.setVisibility(visibility);
    }

    public boolean focusNavigation() {
        if (bar.hasFocus())
            return false;
        selected.requestFocus();
        return true;
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        if (event.getAction() == KeyEvent.ACTION_DOWN || event.getAction() == KeyEvent.ACTION_UP)
            homeInput(content);
        View previous = findFocus();
        boolean consumed = routeKeyEvent(event);
        if (event.getAction() == KeyEvent.ACTION_DOWN)
            Diagnostics.navigation(previous, findFocus(), event.getKeyCode(),
                                                                            consumed);
        return consumed;
    }

    private static boolean focusLibraryEntry(View view) {
        if (view instanceof ContentFocus)
            return ((ContentFocus)view).focusFromNavigation();
        if (view instanceof PreviewPages)
            return ((PreviewPages)view).focusFromNavigation();
        if (view instanceof ViewGroup)
            for (int i = 0; i < ((ViewGroup)view).getChildCount(); i++)
                if (focusLibraryEntry(((ViewGroup)view).getChildAt(i)))
                    return true;
        return false;
    }

    private static void homeInput(View view) {
        if (view instanceof PreviewPages) {
            ((PreviewPages)view).recordRemoteInput();
            return;
        }
        if (view instanceof ViewGroup)
            for (int i = 0; i < ((ViewGroup)view).getChildCount(); i++)
                homeInput(((ViewGroup)view).getChildAt(i));
    }

    private boolean routeKeyEvent(KeyEvent event) {
        if (event.getAction() == KeyEvent.ACTION_DOWN && event.getKeyCode() == KeyEvent.KEYCODE_DPAD_UP &&
            !bar.hasFocus() && firstRow.getAsBoolean()) {
            selected.requestFocus();
            return true;
        }
        return super.dispatchKeyEvent(event);
    }
}
