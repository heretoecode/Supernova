package com.archos.mediacenter.video.leanback;

import android.app.Dialog;
import android.content.Context;
import android.graphics.*;
import android.graphics.drawable.*;
import android.os.Build;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.*;
import android.widget.*;

/** Presentation tokens shared only by the approved playback subtitle surfaces. */
public final class SubtitlePanelStyle {
    public final Context context;
    public final float scale;
    public final int width, height;
    public final boolean side;

    public SubtitlePanelStyle(Context c, boolean main) {
        this(c, main, false);
    }

    /**
     * {@code side}: the approved Playback Settings side panel (owner, 2 Nov 2026) — one third of the screen wide,
     * docked top-right, height fitted to its rows. Reference sizes are the Image B values scaled so row text is
     * ~21px on a 1920px-wide screen, as in the approved mock-up.
     */
    public SubtitlePanelStyle(Context c, boolean main, boolean side) {
        context = c;
        this.side = side;
        int safeH =
            Math.min(TvLayout.safeHeight(c), Math.round(c.getResources().getDisplayMetrics().heightPixels * .9f));
        if (side) {
            width = Math.round(c.getResources().getDisplayMetrics().widthPixels / 3f);
            height = safeH;
            scale = width / 853f;
            return;
        }
        width = Math.min(Math.round(TvLayout.safeWidth(c) * .85f),
                         Math.round(safeH * (main ? 1067f / 945f : 1115f / 765f)));
        height = main ? safeH : Math.min(safeH, Math.round(width * 765f / 1115f));
        scale = width / (main ? 1067f : 1115f);
    }

    /** Places a window as the side panel: top-right, 40px (at 1080p) from the edges, fitted height, no full-screen dim. */
    public void placeSide(Window window) {
        DisplayMetrics m = context.getResources().getDisplayMetrics();
        window.clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        window.setGravity(Gravity.TOP | Gravity.END);
        WindowManager.LayoutParams attrs = window.getAttributes();
        attrs.x = Math.round(m.widthPixels * 40f / 1920f);
        attrs.y = Math.round(m.heightPixels * 40f / 1080f);
        window.setAttributes(attrs);
        window.setLayout(width, ViewGroup.LayoutParams.WRAP_CONTENT);
    }

    /** A fitted side panel grows with its content up to the screen height less the 40px margins, then its list scrolls. */
    public void capSideHeight(Dialog dialog, View panel) {
        if (!side)
            return;
        int max = context.getResources().getDisplayMetrics().heightPixels -
                  Math.round(context.getResources().getDisplayMetrics().heightPixels * 80f / 1080f);
        panel.addOnLayoutChangeListener((v, l, t, r, b, ol, ot, or, ob) -> {
            if (b - t > max && dialog.getWindow() != null)
                dialog.getWindow().setLayout(width, max);
        });
    }

    public int px(float reference) {
        return Math.max(1, Math.round(reference * scale));
    }

    public TextView text(String label, int size) {
        TextView v = new TextView(context);
        v.setText(label);
        v.setTextColor(Color.WHITE);
        v.setTextSize(TypedValue.COMPLEX_UNIT_PX,
                      size * scale * context.getResources().getConfiguration().fontScale);
        v.setIncludeFontPadding(false);
        return v;
    }

    public GradientDrawable surface(int color, int stroke, int radius) {
        GradientDrawable d = new GradientDrawable();
        d.setColor(color);
        d.setCornerRadius(px(radius));
        if (stroke != 0)
            d.setStroke(px(2), stroke);
        return d;
    }

    /** The shared glass (approved 2 Nov 2026). */
    public Drawable panelSurface() {
        return new Glow(SupernovaStyle.GLASS, SupernovaStyle.GLASS_EDGE, side ? 35 : 36, 2, side ? 14 : 6);
    }

    public Drawable glow(int radius) {
        return side ? new Glow(0x145fd0ff, 0xff7fd6ff, Math.min(radius, 14), 3, 5)
                    : new Glow(0xcc0b2a4a, 0xff2fb6ff, radius, 3, 8);
    }

    private final class Glow extends Drawable {
        final int fill, border, radius, stroke, blur;
        final Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);

        Glow(int fill, int border, int radius, int stroke, int blur) {
            this.fill = fill;
            this.border = border;
            this.radius = radius;
            this.stroke = stroke;
            this.blur = blur;
        }

        public void draw(Canvas canvas) {
            RectF rect = new RectF(getBounds());
            rect.inset(px(3), px(3));
            paint.setStyle(Paint.Style.FILL);
            paint.setColor(fill);
            paint.setShadowLayer(px(blur), 0, 0, (border & 0x00ffffff) | 0x99000000);
            canvas.drawRoundRect(rect, px(radius), px(radius), paint);
            paint.clearShadowLayer();
            paint.setStyle(Paint.Style.STROKE);
            paint.setStrokeWidth(px(stroke));
            paint.setColor(border);
            canvas.drawRoundRect(rect, px(radius), px(radius), paint);
        }

        public void setAlpha(int a) {
            paint.setAlpha(a);
            invalidateSelf();
        }

        public void setColorFilter(ColorFilter f) {
            paint.setColorFilter(f);
            invalidateSelf();
        }

        public int getOpacity() {
            return PixelFormat.TRANSLUCENT;
        }
    }

    public void scrollbar(ScrollView view) {
        view.setScrollBarSize(px(6));
        if (Build.VERSION.SDK_INT >= 29) {
            view.setVerticalScrollbarThumbDrawable(surface(0xb36e7f90, 0, 3));
            view.setVerticalScrollbarTrackDrawable(new ColorDrawable(Color.TRANSPARENT));
        }
    }

    /** The shared row focus: white outline, no fill change. */
    public Drawable focus() {
        return SupernovaStyle.rowFocus(context);
    }

    public LinearLayout panel() {
        LinearLayout panel = new LinearLayout(context);
        panel.setOrientation(LinearLayout.VERTICAL);
        panel.setPadding(px(44), px(24), px(44), px(30));
        panel.setBackground(panelSurface());
        panel.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        panel.setClipToPadding(false);
        return panel;
    }

    public void rule(LinearLayout parent) {
        View line = new View(context);
        line.setImportantForAccessibility(View.IMPORTANT_FOR_ACCESSIBILITY_NO);
        line.setBackgroundColor(0xb32a4a6b);
        parent.addView(line, new LinearLayout.LayoutParams(-1, px(1)));
    }

    public ImageView icon(String kind, int size) {
        ImageView image = new ImageView(context);
        image.setImageDrawable(new Icon(kind));
        image.setImportantForAccessibility(View.IMPORTANT_FOR_ACCESSIBILITY_NO);
        image.setScaleType(ImageView.ScaleType.FIT_CENTER);
        image.setLayoutParams(new LinearLayout.LayoutParams(px(size), px(size)));
        return image;
    }

    public LinearLayout row(String tag) {
        LinearLayout row = new LinearLayout(context);
        row.setGravity(Gravity.CENTER_VERTICAL);
        row.setMinimumHeight(Math.round(px(60) * context.getResources().getConfiguration().fontScale));
        row.setPadding(px(12), px(6), px(12), px(6));
        row.setTag(tag);
        row.setFocusable(true);
        row.setFocusableInTouchMode(true);
        row.setDescendantFocusability(ViewGroup.FOCUS_BLOCK_DESCENDANTS);
        row.setBackground(focus());
        row.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        return row;
    }

    public void show(Dialog dialog, View panel, int desiredHeight) {
        dialog.setContentView(panel);
        dialog.show();
        Window window = dialog.getWindow();
        window.setBackgroundDrawableResource(android.R.color.transparent);
        if (side) {
            placeSide(window);
            return;
        }
        window.addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        window.setDimAmount(.32f);
        window.setGravity(Gravity.CENTER);
        window.setLayout(width, Math.min(height, desiredHeight));
    }

    public TextView header(LinearLayout panel, String title, Runnable back) {
        TextView v = text("‹   " + title, side ? 37 : 44);
        v.setTypeface(null, Typeface.BOLD);
        v.setFocusable(true);
        v.setFocusableInTouchMode(true);
        v.setBackground(focus());
        v.setPadding(px(4), px(4), px(4), px(22));
        v.setContentDescription("Back, " + title);
        v.setOnClickListener(w -> back.run());
        panel.addView(v, new LinearLayout.LayoutParams(-1, -2));
        return v;
    }

    /** Thin white semantic marks; country flags are rendered separately from bundled assets. */
    public static final class Icon extends Drawable {
        private final String kind;
        private final Paint p = new Paint(Paint.ANTI_ALIAS_FLAG);

        public Icon(String kind) {
            this.kind = kind;
            p.setColor(Color.WHITE);
            p.setStrokeWidth(1.5f);
            p.setStyle(Paint.Style.STROKE);
            p.setStrokeCap(Paint.Cap.ROUND);
            p.setStrokeJoin(Paint.Join.ROUND);
        }

        private void line(Canvas c, float... xy) {
            Path path = new Path();
            path.moveTo(xy[0], xy[1]);
            for (int i = 2; i < xy.length; i += 2)
                path.lineTo(xy[i], xy[i + 1]);
            c.drawPath(path, p);
        }

        @Override
        public void draw(Canvas c) {
            c.save();
            c.translate(getBounds().left, getBounds().top);
            c.scale(getBounds().width() / 24f, getBounds().height() / 24f);
            switch (kind) {
            case "cc":
                c.drawRoundRect(1, 5, 23, 19, 2, 2, p);
                p.setStyle(Paint.Style.FILL);
                p.setTypeface(Typeface.create("sans-serif", Typeface.BOLD));
                p.setTextSize(10);
                c.drawText("CC", 4, 16, p);
                p.setStyle(Paint.Style.STROKE);
                break;
            case "size":
            case "outline":
                p.setStyle(Paint.Style.FILL);
                p.setTextSize(15);
                p.setTypeface(Typeface.create("sans-serif", Typeface.NORMAL));
                c.drawText(kind.equals("size") ? "Aa" : "A", 3, 18, p);
                p.setStyle(Paint.Style.STROKE);
                if (kind.equals("outline")) {
                    p.setPathEffect(new DashPathEffect(new float[] {2, 2}, 0));
                    c.drawRect(1, 2, 23, 23, p);
                    p.setPathEffect(null);
                }
                break;
            case "palette":
                c.drawOval(2, 2, 22, 22, p);
                for (float[] xy : new float[][] {{7, 8}, {12, 5}, {18, 8}, {7, 15}})
                    c.drawCircle(xy[0], xy[1], 1, p);
                break;
            case "position":
                line(c, 12, 2, 12, 22);
                line(c, 6, 8, 12, 2, 18, 8);
                line(c, 6, 16, 12, 22, 18, 16);
                break;
            case "background":
                c.drawRect(2, 4, 22, 21, p);
                for (int i = 0; i < 24; i += 5)
                    line(c, Math.max(2, i - 12), Math.min(21, 4 + i), Math.min(22, i + 4), Math.max(4, i - 14));
                break;
            case "opacity":
                Path drop = new Path();
                drop.moveTo(12, 2);
                drop.cubicTo(10, 6, 3, 12, 3, 16);
                drop.cubicTo(3, 25, 21, 25, 21, 16);
                drop.cubicTo(21, 12, 14, 6, 12, 2);
                c.drawPath(drop, p);
                break;
            case "time":
                c.drawCircle(12, 14, 8, p);
                line(c, 9, 2, 15, 2);
                line(c, 12, 2, 12, 6);
                line(c, 18, 5, 20, 7);
                line(c, 12, 9, 12, 14, 15, 16);
                break;
            case "download":
                line(c, 12, 2, 12, 17);
                line(c, 6, 11, 12, 17, 18, 11);
                line(c, 3, 17, 3, 22, 21, 22, 21, 17);
                break;
            case "check":
                line(c, 3, 12, 9, 18, 21, 5);
                break;
            case "note":
                line(c, 9, 18, 9, 4, 21, 2, 21, 16);
                c.drawCircle(6, 18, 3, p);
                c.drawCircle(18, 16, 3, p);
                break;
            case "boost":
                line(c, 3, 9, 7, 9, 12, 5, 12, 19, 7, 15, 3, 15, 3, 9);
                line(c, 17, 8, 17, 16);
                line(c, 13, 12, 21, 12);
                break;
            case "moon": {
                Path m = new Path();
                m.moveTo(20, 14.5f);
                m.cubicTo(18, 20, 10, 22, 6, 17);
                m.cubicTo(2, 12, 5, 5, 10, 4);
                m.cubicTo(8, 9, 11, 15, 20, 14.5f);
                c.drawPath(m, p);
                break;
            }
            case "surround":
                c.drawCircle(12, 12, 2.5f, p);
                c.drawArc(5, 5, 19, 19, 135, 90, false, p);
                c.drawArc(5, 5, 19, 19, -45, 90, false, p);
                c.drawArc(1.5f, 1.5f, 22.5f, 22.5f, 135, 90, false, p);
                c.drawArc(1.5f, 1.5f, 22.5f, 22.5f, -45, 90, false, p);
                break;
            case "aspect":
                c.drawRoundRect(2, 6, 22, 18, 1.5f, 1.5f, p);
                line(c, 5, 11, 5, 9, 8, 9);
                line(c, 19, 13, 19, 15, 16, 15);
                break;
            case "cube":
                line(c, 12, 3, 20, 7.5f, 20, 16.5f, 12, 21, 4, 16.5f, 4, 7.5f, 12, 3);
                line(c, 12, 12, 20, 7.5f);
                line(c, 12, 12, 12, 21);
                line(c, 12, 12, 4, 7.5f);
                break;
            case "pip":
                c.drawRoundRect(2, 5, 22, 19, 1.5f, 1.5f, p);
                p.setStyle(Paint.Style.FILL);
                c.drawRoundRect(12, 11, 19.5f, 17, 1, 1, p);
                p.setStyle(Paint.Style.STROKE);
                break;
            case "speed":
                c.drawArc(4, 6, 20, 22, 180, 180, false, p);
                line(c, 12, 14, 16, 9);
                break;
            case "repeat":
                line(c, 17, 2, 20, 5, 17, 8);
                line(c, 4, 11, 4, 9);
                c.drawArc(4, 5, 12, 13, 180, 90, false, p);
                line(c, 8, 5, 20, 5);
                line(c, 7, 22, 4, 19, 7, 16);
                line(c, 20, 13, 20, 15);
                c.drawArc(12, 11, 20, 19, 0, 90, false, p);
                line(c, 16, 19, 4, 19);
                break;
            case "skip":
                line(c, 5, 5, 14, 12, 5, 19, 5, 5);
                line(c, 18, 5, 18, 19);
                break;
            default:
                PreviewIcon icon = new PreviewIcon(kind);
                icon.setBounds(0, 0, 24, 24);
                icon.draw(c);
                break;
            }
            c.restore();
        }

        @Override
        public void setAlpha(int a) {
            p.setAlpha(a);
            invalidateSelf();
        }

        @Override
        public void setColorFilter(ColorFilter f) {
            p.setColorFilter(f);
            invalidateSelf();
        }

        @Override
        public int getOpacity() {
            return PixelFormat.TRANSLUCENT;
        }
    }
}
