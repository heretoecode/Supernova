package com.archos.mediacenter.video.leanback;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.*;
import android.graphics.drawable.*;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * The single source of truth for buttons, glass panels, row focus, tiles and menus
 * (docs/design/BUTTONS_GLASS_AND_MENUS_APPROVED_2026-11-02.md, approved by the owner on 2 Nov 2026).
 *
 * <p>Every screen takes these looks from here. No other class may define its own button fill, focus
 * outline, panel surface or menu styling; {@code SupernovaStyleConformanceTest} fails the build if one does.
 *
 * <p>Sizes are in dp. On a 1080p Shield (xhdpi, density 2) 1dp = 2px, so the spec's 1080p pixel values
 * are halved here (e.g. 3px focus outline = 1.5dp).
 */
public final class SupernovaStyle {
    private SupernovaStyle() { }

    // ---- tokens --------------------------------------------------------------------------------
    /** Glass: navy at 97% opacity. Text behind a panel must not be readable. */
    public static final int GLASS = 0xF71A1F5C;
    public static final int GLASS_EDGE = 0x66FFFFFF;
    /** Resting button: black at 72%. */
    public static final int BUTTON_REST = 0xB8000000;
    /** Resting surface for buttons inside a panel and for tiles: the panel's darker navy. */
    public static final int PANEL_REST = 0xFF141848;
    public static final int FOCUS_FILL = 0xFFF4F6FB;
    public static final int FOCUS_TEXT = 0xFF0B0D12;
    public static final int TEXT = Color.WHITE;
    public static final int TEXT_SECONDARY = 0xFFC9D3E6;
    public static final int TEXT_SECONDARY_ON_FOCUS = 0xFF3A4256;
    public static final int HEADING = 0xFF9CC9F0;
    public static final int HEADING_LINE = 0x599CC9F0;
    public static final int DISABLED_ALPHA = 102; // 40%

    public static final float RADIUS_DP = 12f;
    public static final float PANEL_RADIUS_DP = 13f;
    public static final float ROW_FOCUS_STROKE_DP = 1.5f;
    /** Breathing room between a row's focus outline and its icon/text. */
    public static final float ROW_INSET_DP = 8f;
    /** Menu width = longest item + this much each side inside the focus outline. */
    public static final float MENU_ITEM_SPACE_DP = 12f;
    public static final float MENU_MIN_WIDTH_DP = 180f; // 360px at 1080p
    public static final float MENU_MAX_SCREEN_FRACTION = .40f;

    /** Which resting surface a button sits on. */
    public enum Surface {
        /** Small buttons on a page (HUD, toolbars, hero, details): black at rest. */
        STANDARD,
        /** Buttons inside a glass panel (e.g. panel tabs): the panel's darker navy at rest. */
        PANEL,
        /** Large boxed choices (e.g. the Settings grid): navy glass at rest. */
        TILE
    }

    public static int dp(Context c, float value) { return Math.round(value * c.getResources().getDisplayMetrics().density); }

    // ---- buttons and tiles ---------------------------------------------------------------------

    /**
     * Button/tile background. States: focused or pressed = white box with a soft shadow;
     * selected (not focused) = resting box + thin white outline; disabled = resting at 40%;
     * otherwise resting. The box never changes size between states.
     */
    public static StateListDrawable button(Context c, Surface surface) {
        float radius = dp(c, surface == Surface.TILE ? 11 : RADIUS_DP);
        int rest = surface == Surface.STANDARD ? BUTTON_REST : surface == Surface.PANEL ? PANEL_REST : GLASS;
        int edge = surface == Surface.TILE ? 0x2EFFFFFF : 0;
        StateListDrawable states = new StateListDrawable();
        Drawable focused = new Box(FOCUS_FILL, 0, 0, radius, dp(c, 6));
        states.addState(new int[]{android.R.attr.state_focused}, focused);
        states.addState(new int[]{android.R.attr.state_pressed}, focused);
        states.addState(new int[]{-android.R.attr.state_enabled}, new Box(withAlpha(rest, DISABLED_ALPHA), edge, dp(c, 1), radius, 0));
        states.addState(new int[]{android.R.attr.state_selected}, new Box(rest, Color.WHITE, Math.max(1, dp(c, 1)), radius, 0));
        states.addState(new int[]{}, new Box(rest, edge, Math.max(1, dp(c, 1)), radius, 0));
        return states;
    }

    /** Text and icon colour that follows the button state: black on the white focus box, white otherwise. */
    public static ColorStateList buttonContent() {
        return new ColorStateList(
                new int[][]{{android.R.attr.state_focused}, {android.R.attr.state_pressed}, {-android.R.attr.state_enabled}, {}},
                new int[]{FOCUS_TEXT, FOCUS_TEXT, 0x66FFFFFF, TEXT});
    }

    /** Secondary text (e.g. a tile's description) that stays readable on both boxes. */
    public static ColorStateList secondaryContent() {
        return new ColorStateList(
                new int[][]{{android.R.attr.state_focused}, {android.R.attr.state_pressed}, {}},
                new int[]{TEXT_SECONDARY_ON_FOCUS, TEXT_SECONDARY_ON_FOCUS, TEXT_SECONDARY});
    }

    /**
     * Styles a text button: background, content colours (icons included), padding, one line.
     * Width follows the content — callers must not give buttons fixed widths or weights.
     */
    public static void styleButton(TextView view, Surface surface, boolean large) {
        Context c = view.getContext();
        view.setBackground(button(c, surface));
        view.setTextColor(buttonContent());
        tintIcons(view);
        view.setAllCaps(false);
        view.setGravity(Gravity.CENTER);
        view.setSingleLine(true);
        view.setEllipsize(TextUtils.TruncateAt.END);
        int horizontal = dp(c, large ? 14 : 12), vertical = dp(c, large ? 9 : 7);
        view.setPadding(horizontal, vertical, horizontal, vertical);
        view.setMinimumHeight(dp(c, large ? 36 : 32));
        view.setMinHeight(dp(c, large ? 36 : 32));
        view.setCompoundDrawablePadding(dp(c, 7));
    }

    /**
     * Makes a TextView's compound icons follow the text colour (white at rest, black on focus).
     * Works for the app's own icon drawables too, which ignore Android's tint lists.
     */
    public static void tintIcons(TextView view) {
        Drawable[] d = view.getCompoundDrawablesRelative();
        boolean changed = false;
        for (int i = 0; i < 4; i++) if (isMonochromeIcon(d[i])) { d[i] = new StateIcon(d[i]); changed = true; }
        if (changed) view.setCompoundDrawablesRelative(d[0], d[1], d[2], d[3]);
    }

    /**
     * Turns any view into a button of the approved style: background, plus text and icon colours that
     * follow focus for the view and every child (children mirror the button's focused state).
     * Use this — never a bare {@link #button} background — so content can never be white on white.
     */
    public static void applyButton(View view, Surface surface) {
        view.setBackground(button(view.getContext(), surface));
        applyContent(view, true);
    }

    /**
     * A button made of a group whose focusable child is an icon button, plus non-focusable labels
     * (e.g. the playback HUD's Settings and play/pause). The group shows the box; the focused icon and
     * the labels turn black on the white focus box. The group must take its state from its children.
     */
    public static void applyButtonGroup(android.view.ViewGroup group, Surface surface) {
        group.setAddStatesFromChildren(true);
        group.setBackground(button(group.getContext(), surface));
        for (int i = 0; i < group.getChildCount(); i++) {
            View child = group.getChildAt(i);
            if (child instanceof android.widget.ImageView) {
                child.setBackgroundColor(Color.TRANSPARENT);
                ((android.widget.ImageView) child).setImageTintList(buttonContent());
                Drawable d = ((android.widget.ImageView) child).getDrawable();
                if (isMonochromeIcon(d)) ((android.widget.ImageView) child).setImageDrawable(new StateIcon(d));
            } else if (child instanceof TextView && !child.isFocusable()) {
                child.setDuplicateParentStateEnabled(true);
                ((TextView) child).setTextColor(buttonContent());
            }
        }
    }

    /** Background for any non-text view that behaves as a button (icon-only buttons, composite tiles). */
    public static void styleContainer(View view, Surface surface) { applyButton(view, surface); }

    private static void applyContent(View view, boolean root) {
        if (!root) view.setDuplicateParentStateEnabled(true);
        if (view instanceof TextView) {
            TextView text = (TextView) view;
            boolean secondary = Boolean.TRUE.equals(text.getTag(com.archos.mediacenter.video.R.id.supernova_secondary_text));
            text.setTextColor(secondary ? secondaryContent() : buttonContent());
            tintIcons(text);
        } else if (view instanceof android.widget.ImageView) {
            android.widget.ImageView image = (android.widget.ImageView) view;
            Drawable d = image.getDrawable();
            if (isMonochromeIcon(d)) image.setImageDrawable(new StateIcon(d));
        }
        if (view instanceof android.view.ViewGroup) {
            android.view.ViewGroup group = (android.view.ViewGroup) view;
            for (int i = 0; i < group.getChildCount(); i++) applyContent(group.getChildAt(i), false);
        }
    }

    /**
     * Secondary text inside a button that keeps following the button's state: it takes whatever colour the
     * text currently has (white at rest, black on focus) at 72% strength, instead of a fixed grey that
     * would vanish on the white focus box.
     */
    public static final class FadedSpan extends android.text.style.CharacterStyle {
        @Override public void updateDrawState(android.text.TextPaint paint) {
            paint.setColor((paint.getColor() & 0x00FFFFFF) | 0xB8000000);
        }
    }

    /**
     * True for single-colour line icons, which follow the button's content colour. Pictures (posters,
     * artwork, bitmaps) and flags keep their own colours and are never recoloured.
     */
    static boolean isMonochromeIcon(Drawable d) {
        if (d == null || d instanceof StateIcon) return false;
        if (d instanceof PreviewLanguageIcon || d instanceof BitmapDrawable) return false;
        return d instanceof PreviewIcon || d instanceof SubtitlePanelStyle.Icon || d instanceof ProviderMonogram
                || d instanceof PreviewGenres.Icon || d instanceof VectorDrawable;
    }

    /** Marks a TextView inside a button or tile as secondary (description) text. */
    public static void markSecondary(TextView text) { text.setTag(com.archos.mediacenter.video.R.id.supernova_secondary_text, Boolean.TRUE); }

    /**
     * Wraps any icon so it is drawn in the button's content colour for the current state
     * (white at rest, black on the white focus box). Delegates size and drawing to the icon.
     */
    public static final class StateIcon extends Drawable implements Drawable.Callback {
        private final Drawable icon;
        private final ColorStateList colours = buttonContent();
        private int current = 0;
        public StateIcon(Drawable icon) {
            this.icon = icon;
            icon.setCallback(this);
            apply(colours.getDefaultColor());
        }
        public Drawable icon() { return icon; }
        private void apply(int colour) { if (colour != current) { current = colour; icon.setColorFilter(new PorterDuffColorFilter(colour, PorterDuff.Mode.SRC_IN)); } }
        @Override public boolean isStateful() { return true; }
        @Override protected boolean onStateChange(int[] state) {
            int before = current;
            apply(colours.getColorForState(state, colours.getDefaultColor()));
            icon.setState(state);
            if (before != current) { invalidateSelf(); return true; }
            return false;
        }
        @Override protected void onBoundsChange(Rect bounds) { icon.setBounds(bounds); }
        @Override public void draw(Canvas canvas) { icon.draw(canvas); }
        @Override public int getIntrinsicWidth() { return icon.getIntrinsicWidth(); }
        @Override public int getIntrinsicHeight() { return icon.getIntrinsicHeight(); }
        @Override public void setAlpha(int a) { icon.setAlpha(a); }
        @Override public void setColorFilter(ColorFilter f) { /* colour follows state only */ }
        @Override public int getOpacity() { return PixelFormat.TRANSLUCENT; }
        @Override public void invalidateDrawable(Drawable who) { invalidateSelf(); }
        @Override public void scheduleDrawable(Drawable who, Runnable what, long when) { scheduleSelf(what, when); }
        @Override public void unscheduleDrawable(Drawable who, Runnable what) { unscheduleSelf(what); }
    }

    // ---- glass ---------------------------------------------------------------------------------

    /** The one material for every pop-up, menu, dialog and floating panel. */
    public static Drawable glass(Context c) { return glass(c, PANEL_RADIUS_DP); }

    public static Drawable glass(Context c, float radiusDp) {
        return new Box(GLASS, GLASS_EDGE, Math.max(1, dp(c, 1)), dp(c, radiusDp), 0);
    }

    // ---- rows ----------------------------------------------------------------------------------

    /** Row focus: white outline, no fill change. Use with {@link #ROW_INSET_DP} padding inside the row. */
    public static StateListDrawable rowFocus(Context c) {
        StateListDrawable states = new StateListDrawable();
        states.addState(new int[]{android.R.attr.state_focused}, rowFocusOutline(c));
        states.addState(new int[]{}, new ColorDrawable(Color.TRANSPARENT));
        return states;
    }

    /** The row focus outline itself (white, rounded, no fill), e.g. for foreground focus on cards. */
    public static Drawable rowFocusOutline(Context c) {
        return new Box(Color.TRANSPARENT, Color.WHITE, Math.max(2, dp(c, ROW_FOCUS_STROKE_DP)), dp(c, RADIUS_DP), 0);
    }

    /** Applies the row focus and the approved breathing room to a row. */
    public static void styleRow(View row) {
        Context c = row.getContext();
        row.setBackground(rowFocus(c));
        int inset = dp(c, ROW_INSET_DP);
        row.setPadding(Math.max(row.getPaddingLeft(), inset), row.getPaddingTop(), Math.max(row.getPaddingRight(), inset), row.getPaddingBottom());
    }

    // ---- menus ---------------------------------------------------------------------------------

    /** A group heading followed on the same line by a thin line spanning the rest of the menu. */
    public static View groupHeading(Context c, String title, float textSizeSp) {
        LinearLayout heading = new LinearLayout(c);
        heading.setGravity(Gravity.CENTER_VERTICAL);
        heading.setFocusable(false);
        heading.setPadding(dp(c, ROW_INSET_DP), dp(c, 6), dp(c, ROW_INSET_DP), dp(c, 2));
        TextView label = new TextView(c);
        label.setText(title);
        label.setTextSize(textSizeSp);
        label.setTextColor(HEADING);
        label.setSingleLine(true);
        heading.addView(label);
        View line = new View(c);
        line.setBackgroundColor(HEADING_LINE);
        LinearLayout.LayoutParams size = new LinearLayout.LayoutParams(0, Math.max(1, dp(c, .75f)), 1);
        size.leftMargin = dp(c, 7);
        heading.addView(line, size);
        heading.setTag("menu.heading");
        return heading;
    }

    /**
     * Menu width for the longest item: item + {@link #MENU_ITEM_SPACE_DP} each side inside the focus
     * outline + the panel's own horizontal padding; at least {@link #MENU_MIN_WIDTH_DP}, at most
     * {@link #MENU_MAX_SCREEN_FRACTION} of the screen. Longer items end in "…".
     */
    public static int menuWidth(Context c, int longestItemPx, int panelHorizontalPaddingPx) {
        int width = longestItemPx + 2 * dp(c, MENU_ITEM_SPACE_DP) + 2 * dp(c, ROW_INSET_DP) + panelHorizontalPaddingPx;
        int screen = c.getResources().getDisplayMetrics().widthPixels;
        return Math.max(dp(c, MENU_MIN_WIDTH_DP), Math.min(width, Math.round(screen * MENU_MAX_SCREEN_FRACTION)));
    }

    // ---- drawing -------------------------------------------------------------------------------

    static int withAlpha(int colour, int alpha) { return (colour & 0x00FFFFFF) | (Math.round(Color.alpha(colour) * alpha / 255f) << 24); }

    /**
     * Rounded fill with an optional outline and an optional soft shadow drawn outside the bounds
     * (so a focused box is exactly the size of a resting one).
     */
    static final class Box extends Drawable {
        private final int fill, edge, stroke, shadow;
        private final float radius;
        private final Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        private int alpha = 255;
        Box(int fill, int edge, int stroke, float radius, int shadow) {
            this.fill = fill; this.edge = edge; this.stroke = stroke; this.radius = radius; this.shadow = shadow;
        }
        @Override public void draw(Canvas canvas) {
            RectF r = new RectF(getBounds());
            if (shadow > 0) {
                paint.setStyle(Paint.Style.FILL);
                for (int i = 4; i >= 1; i--) {
                    RectF s = new RectF(r);
                    s.inset(-shadow * i / 8f, -shadow * i / 8f);
                    s.offset(0, shadow / 3f);
                    paint.setColor(withAlpha(0x26000000, alpha));
                    canvas.drawRoundRect(s, radius + shadow * i / 8f, radius + shadow * i / 8f, paint);
                }
            }
            paint.setStyle(Paint.Style.FILL);
            paint.setColor(withAlpha(fill, alpha));
            canvas.drawRoundRect(r, radius, radius, paint);
            if (edge != 0 && stroke > 0) {
                paint.setStyle(Paint.Style.STROKE);
                paint.setStrokeWidth(stroke);
                paint.setColor(withAlpha(edge, alpha));
                RectF e = new RectF(r);
                e.inset(stroke / 2f, stroke / 2f);
                canvas.drawRoundRect(e, Math.max(0, radius - stroke / 2f), Math.max(0, radius - stroke / 2f), paint);
            }
        }
        @Override public void setAlpha(int a) { alpha = a; invalidateSelf(); }
        @Override public int getAlpha() { return alpha; }
        @Override public void setColorFilter(ColorFilter f) { paint.setColorFilter(f); invalidateSelf(); }
        @Override public int getOpacity() { return PixelFormat.TRANSLUCENT; }
    }
}
