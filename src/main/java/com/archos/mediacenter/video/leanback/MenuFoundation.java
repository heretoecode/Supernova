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

    private MenuFoundation() {
    }

    public static int dp(Context context, float value) {
        return TvLayout.dimension(context, value);
    }

    public static GradientDrawable surface(Context context, boolean raised) {
        GradientDrawable drawable =
            new GradientDrawable(GradientDrawable.Orientation.TOP_BOTTOM,
                                 raised ? new int[] {0xf01c2632, 0xf0131c27} : new int[] {0xf0141e29, 0xf00e1620});
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
        if (icon != null)
            icon(text, icon);
        SupernovaStyle.applyButton(text, SupernovaStyle.Surface.STANDARD);
        text.setOnClickListener(view -> action.run());
        return text;
    }

    public static void icon(TextView target, String meaning) {
        Drawable drawable = PreviewGenres.known(meaning) ? new PreviewGenres.Icon(meaning) : new PreviewIcon(meaning);
        int size = dp(target.getContext(), 19);
        drawable.setBounds(0, 0, size, size);
        target.setCompoundDrawables(drawable, null, null, null);
        target.setCompoundDrawablePadding(dp(target.getContext(), 9));
    }

    /** Button background from the shared style. Prefer {@link SupernovaStyle#applyButton}, which also sets content colours. */
    public static Drawable controlBackground(Context context) {
        return SupernovaStyle.button(context, SupernovaStyle.Surface.STANDARD);
    }

    /** Tracks the window boundary, so a covered parent never displays a second focus outline. */
    public static class WindowPanel extends android.widget.LinearLayout {
        boolean active = true;

        public WindowPanel(Context context) {
            super(context);
        }

        @Override
        public void onWindowFocusChanged(boolean focused) {
            super.onWindowFocusChanged(focused);
            active = focused;
            invalidateChildren(this);
        }

        private static void invalidateChildren(View view) {
            view.invalidate();
            if (view instanceof android.view.ViewGroup)
                for (int i = 0; i < ((android.view.ViewGroup)view).getChildCount(); i++)
                    invalidateChildren(((android.view.ViewGroup)view).getChildAt(i));
        }
    }
}
