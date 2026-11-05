package com.archos.mediacenter.video.leanback;

import android.content.Context;
import android.graphics.*;
import android.graphics.drawable.*;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.*;
import android.widget.*;
import com.archos.mediacenter.video.R;

/**
 * Building blocks for the approved side panels (owner, 2 Nov 2026): rows of icon · label · value,
 * straight separators, a glowing focus outline, {@code ‹ value ›} steppers and {@code A | B | C}
 * choices. Sizes are written in pixels of the approved 1920×1080 mock-ups and scaled to the screen.
 * The Playback Settings panel draws the same tokens; the Edit Page panel is built from this kit.
 */
public final class SidePanelKit {
    public static final int ACCENT = 0xff2f6df0, FOCUS_BORDER = 0xff7fd6ff, OUTLINE = 0xff5a7bd8;
    public static final int HEADING = SupernovaStyle.HEADING, HEADING_RULE = SupernovaStyle.HEADING_LINE;
    public static final int VALUE = 0xff3fb6ff, META = 0xffb9c7e8, BAR = 0xffcfd8ff, ICON = 0xffe8eeff;
    public static final int MATCH = 0xff4cd3a0, WARNING = 0xffffc04d, DANGER = 0xffff6b7a;
    public static final int ROW_HEIGHT = 74; // More-menu row height (0.154)

    public interface Stepper {
        void step(int direction);
    }

    public final Context context;
    private final float k;
    private int rowHeight = ROW_HEIGHT, sectionGap = 18;

    /** Compact rows (54 instead of 74) and tighter headings, for panels that must fit without scrolling. */
    public void compact() {
        rowHeight = 54;
        sectionGap = 10;
    }

    public int rowHeight() {
        return rowHeight;
    }

    /** {@code scale}: screen pixels per mock-up pixel (screen width / 1920). */
    public SidePanelKit(Context context) {
        this.context = context;
        k = context.getResources().getDisplayMetrics().widthPixels / 1920f;
    }

    public int s(float px) {
        return Math.max(1, Math.round(px * k));
    }

    public TextView text(String label, float px, int colour, boolean bold) {
        TextView v = new TextView(context);
        v.setText(label);
        v.setTextColor(colour);
        v.setIncludeFontPadding(false);
        v.setTextSize(TypedValue.COMPLEX_UNIT_PX, px * k * context.getResources().getConfiguration().fontScale);
        if (bold)
            v.setTypeface(Typeface.create("sans-serif-medium", Typeface.BOLD));
        return v;
    }

    public ImageView icon(String kind, float px, int colour) {
        ImageView image = new ImageView(context);
        Drawable d = new SubtitlePanelStyle.Icon(kind);
        d.setColorFilter(new PorterDuffColorFilter(colour, PorterDuff.Mode.SRC_IN));
        image.setImageDrawable(d);
        image.setImportantForAccessibility(View.IMPORTANT_FOR_ACCESSIBILITY_NO);
        image.setScaleType(ImageView.ScaleType.FIT_CENTER);
        image.setLayoutParams(new LinearLayout.LayoutParams(s(px), s(px)));
        return image;
    }

    /** The shared glass: one 97% charcoal material for every panel (0.154; was navy). */
    public Drawable panelSurface(int glow) {
        return new Surface(SupernovaStyle.GLASS, SupernovaStyle.GLASS, SupernovaStyle.GLASS_EDGE, s(26),
                           Math.max(1, s(2)), 0x66000000, glow);
    }

    /** The shared row focus: white outline, no fill change. */
    public Drawable rowFocus() {
        return SupernovaStyle.rowFocus(context);
    }

    /** More-menu heading: sentence case, soft grey, with a thin line alongside. Rows have no lines between them. */
    public void section(LinearLayout body, String title, boolean first) {
        LinearLayout heading = new LinearLayout(context);
        heading.setGravity(Gravity.CENTER_VERTICAL);
        heading.setImportantForAccessibility(View.IMPORTANT_FOR_ACCESSIBILITY_NO);
        heading.setPadding(s(6), s(first ? 4 : sectionGap), s(6), s(4));
        heading.setTag("panel.heading");
        String lower = title.toLowerCase(java.util.Locale.ROOT);
        heading.addView(text(lower.isEmpty() ? lower : Character.toUpperCase(lower.charAt(0)) + lower.substring(1),
                             18, HEADING, false));
        View line = new View(context);
        line.setBackgroundColor(HEADING_RULE);
        LinearLayout.LayoutParams lineSize = new LinearLayout.LayoutParams(0, Math.max(1, s(2)), 1);
        lineSize.leftMargin = s(14);
        heading.addView(line, lineSize);
        body.addView(heading, new LinearLayout.LayoutParams(-1, -2));
    }

    public void rule(LinearLayout body, int colour) {
        View line = new View(context);
        line.setImportantForAccessibility(View.IMPORTANT_FOR_ACCESSIBILITY_NO);
        line.setBackgroundColor(colour);
        body.addView(line, new LinearLayout.LayoutParams(-1, Math.max(1, s(1.5f))));
    }

    /** Removes a separator left after the last row. */
    public static void dropTrailingRule(LinearLayout body) {
        int last = body.getChildCount() - 1;
        if (last >= 0 && !(body.getChildAt(last) instanceof LinearLayout) &&
            !(body.getChildAt(last) instanceof TextView))
            body.removeViewAt(last);
    }

    /**
     * A focusable row: icon, bold label, then whatever value views the caller adds. Up from the first row
     * runs {@code top}; Left/Right is consumed (and steps the value when a stepper is attached).
     */
    public LinearLayout row(LinearLayout body, String tag, String iconKind, String title, Runnable top) {
        LinearLayout row = new LinearLayout(context);
        row.setTag(tag);
        row.setGravity(Gravity.CENTER_VERTICAL);
        row.setMinimumHeight(Math.round(s(rowHeight) * context.getResources().getConfiguration().fontScale));
        row.setPadding(s(16), 0, s(16), 0);
        row.setFocusable(true);
        row.setFocusableInTouchMode(true);
        row.setDescendantFocusability(ViewGroup.FOCUS_BLOCK_DESCENDANTS);
        row.setBackground(rowFocus());
        row.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        if (iconKind != null) {
            FrameLayout box = new FrameLayout(context);
            box.addView(icon(iconKind, 26, ICON), new FrameLayout.LayoutParams(s(26), s(26), Gravity.CENTER_VERTICAL));
            row.addView(box, new LinearLayout.LayoutParams(s(56), -2));
        }
        TextView label = text(title, 21, Color.WHITE, true);
        label.setSingleLine(true);
        label.setEllipsize(TextUtils.TruncateAt.END);
        row.addView(label, new LinearLayout.LayoutParams(0, -2, 1));
        row.setContentDescription(title);
        row.setTag(R.id.preview_row_top, top);
        attach(body, row, null);
        body.addView(row, new LinearLayout.LayoutParams(-1, -2));
        return row;
    }

    /** (Re)binds the remote handling of a row: optional stepper on Left/Right, Up at the top leaves the list. */
    public void attach(LinearLayout body, View row, Stepper stepper) {
        row.setOnKeyListener((v, key, event) -> {
            if (event.getAction() != KeyEvent.ACTION_DOWN)
                return false;
            if (key == KeyEvent.KEYCODE_DPAD_LEFT || key == KeyEvent.KEYCODE_DPAD_RIGHT) {
                if (stepper != null && row.isEnabled())
                    stepper.step(key == KeyEvent.KEYCODE_DPAD_LEFT ? -1 : 1);
                return true;
            }
            if (key == KeyEvent.KEYCODE_DPAD_UP || key == KeyEvent.KEYCODE_DPAD_DOWN) {
                View next = FocusFinder.getInstance().findNextFocus(
                    body, row, key == KeyEvent.KEYCODE_DPAD_UP ? View.FOCUS_UP : View.FOCUS_DOWN);
                if (next == null || next == row) {
                    Object top = row.getTag(R.id.preview_row_top);
                    if (key == KeyEvent.KEYCODE_DPAD_UP && top instanceof Runnable)
                        ((Runnable)top).run();
                    return true;
                }
            }
            return false;
        });
    }

    public TextView chevron(String c) {
        TextView v = text(c, 26, Color.WHITE, false);
        v.setGravity(Gravity.CENTER);
        return v;
    }

    /** {@code ‹ value ›} with the value in accent blue. Returns the value view (tagged "value"). */
    public TextView stepper(LinearLayout body, LinearLayout row, float minWidth, Stepper stepper) {
        LinearLayout box = new LinearLayout(context);
        box.setGravity(Gravity.CENTER_VERTICAL);
        box.addView(chevron("‹"));
        TextView value = text("", 20, VALUE, false);
        value.setSingleLine(true);
        value.setEllipsize(TextUtils.TruncateAt.END);
        value.setGravity(Gravity.CENTER);
        value.setMinWidth(s(minWidth));
        value.setMaxWidth(s(280));
        value.setTag("value");
        LinearLayout.LayoutParams v = new LinearLayout.LayoutParams(-2, -2);
        v.leftMargin = s(18);
        v.rightMargin = s(18);
        box.addView(value, v);
        box.addView(chevron("›"));
        row.addView(box);
        attach(body, row, stepper);
        return value;
    }

    /** {@code value ›}: opens a list or keyboard on OK. Returns the value view (tagged "value"). */
    public TextView opener(LinearLayout row) {
        TextView value = text("", 20, Color.WHITE, false);
        value.setSingleLine(true);
        value.setEllipsize(TextUtils.TruncateAt.END);
        value.setMaxWidth(s(330));
        value.setTag("value");
        row.addView(value);
        TextView arrow = chevron("›");
        arrow.setPadding(s(14), 0, 0, 0);
        row.addView(arrow);
        return value;
    }

    /** {@code A | B | C}; returns the option views so the caller can colour the selected one. */
    public TextView[] choices(LinearLayout row, String[] labels) {
        LinearLayout box = new LinearLayout(context);
        box.setGravity(Gravity.CENTER_VERTICAL);
        row.addView(box);
        TextView[] texts = new TextView[labels.length];
        for (int i = 0; i < labels.length; i++) {
            if (i > 0) {
                TextView bar = text("|", 20, BAR, false);
                bar.setPadding(s(10), 0, s(10), 0);
                box.addView(bar);
            }
            texts[i] = text(labels[i], 20, Color.WHITE, false);
            box.addView(texts[i]);
        }
        return texts;
    }

    public static void select(TextView[] texts, int selected) {
        for (int i = 0; i < texts.length; i++)
            texts[i].setTextColor(i == selected ? VALUE : Color.WHITE);
    }

    /** Rounded fill (vertical gradient) with a border and an optional outer glow, inset by the glow. */
    public static final class Surface extends Drawable {
        private final int top, bottom, border, radius, stroke, glow, blur;
        private final Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);

        public Surface(int top, int bottom, int border, int radius, int stroke, int glow, int blur) {
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

    /** Wraps its content up to a maximum height, then scrolls. */
    public static final class FittedScroll extends ScrollView {
        private int max = Integer.MAX_VALUE;

        public FittedScroll(Context c) {
            super(c);
            setSmoothScrollingEnabled(false);
            setFocusable(false);
        }

        public void setMax(int px) {
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
