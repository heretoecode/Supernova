package com.archos.mediacenter.video.leanback;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.StateListDrawable;
import android.view.Gravity;
import android.view.View;
import android.widget.TextView;

/** Scoped 0.139 Menu Foundation. Only approved Customise Home/Backup consumers opt in. */
public final class MenuFoundation {
    public static final int TEXT = 0xffffffff, SECONDARY = 0xffb7c0cc;
    public static final int DIVIDER = 0x337f8996;
    private MenuFoundation() { }

    public static int dp(Context context, float value) { return TvLayout.dimension(context, value); }

    public static GradientDrawable surface(Context context, boolean raised) {
        GradientDrawable drawable = new GradientDrawable(GradientDrawable.Orientation.TOP_BOTTOM,
                raised ? new int[]{0xf01c2632, 0xf0131c27} : new int[]{0xf0141e29, 0xf00e1620});
        drawable.setCornerRadius(dp(context, 6));
        drawable.setStroke(Math.max(1, dp(context, 1)), DIVIDER);
        return drawable;
    }

    public static TextView text(Context context, String label, float size) {
        TextView text = new TextView(context);
        text.setText(label);
        text.setTextSize(size * TvLayout.scale(context));
        text.setTextColor(TEXT);
        text.setTypeface(Typeface.create("sans-serif-light", Typeface.NORMAL));
        text.setIncludeFontPadding(false);
        return text;
    }

    public static TextView control(Context context, String label, String icon, Runnable action) {
        TextView text = text(context, label, 15);
        text.setSingleLine(true);
        text.setEllipsize(android.text.TextUtils.TruncateAt.END);
        text.setGravity(Gravity.CENTER_VERTICAL);
        text.setPadding(dp(context, 12), dp(context, 10), dp(context, 12), dp(context, 10));
        text.setMinimumHeight(dp(context, 44));
        text.setId(View.generateViewId());
        text.setFocusable(true);
        text.setFocusableInTouchMode(true);
        text.setClickable(true);
        if (icon != null) icon(text, icon);
        text.setBackground(controlBackground(context));
        text.setOnClickListener(view -> action.run());
        return text;
    }

    public static void icon(TextView target, String meaning) {
        Drawable drawable = PreviewGenres.known(meaning)
                ? new PreviewGenres.Icon(meaning) : new PreviewIcon(meaning);
        int size = dp(target.getContext(), 19);
        drawable.setBounds(0, 0, size, size);
        target.setCompoundDrawables(drawable, null, null, null);
        target.setCompoundDrawablePadding(dp(target.getContext(), 9));
    }

    public static Drawable controlBackground(Context context) {
        StateListDrawable states = new StateListDrawable();
        states.addState(new int[]{android.R.attr.state_focused}, new Outline(context));
        states.addState(new int[]{}, surface(context, true));
        return states;
    }

    /** Focus adds only a soft-white perimeter. It never recolours text or fills with accent. */
    private static final class Outline extends Drawable {
        private final Drawable resting;
        private final Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        private final float radius, stroke;
        Outline(Context context) {
            resting = surface(context, true);
            radius = dp(context, 6);
            stroke = Math.max(1, context.getResources().getDisplayMetrics().density);
            paint.setColor(0xddebf0f5);
            paint.setStyle(Paint.Style.STROKE);
            paint.setStrokeWidth(stroke);
        }
        @Override public void draw(Canvas canvas) {
            resting.setBounds(getBounds()); resting.draw(canvas);
            Drawable.Callback owner=getCallback();
            while(owner instanceof Drawable)owner=((Drawable)owner).getCallback();
            if(owner instanceof View&&!((View)owner).hasWindowFocus())return;
            RectF border = new RectF(getBounds()); border.inset(-stroke / 2, -stroke / 2);
            canvas.drawRoundRect(border, radius, radius, paint);
        }
        @Override public void setAlpha(int alpha) { paint.setAlpha(alpha); resting.setAlpha(alpha); }
        @Override public void setColorFilter(ColorFilter filter) { paint.setColorFilter(filter); }
        @Override public int getOpacity() { return PixelFormat.TRANSLUCENT; }
    }
}
