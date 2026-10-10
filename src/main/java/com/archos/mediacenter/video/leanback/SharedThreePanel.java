package com.archos.mediacenter.video.leanback;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.StateListDrawable;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

/** Common geometry and row styling for storage, Settings and Library Health workspaces. */
public class SharedThreePanel extends LinearLayout {
    public SharedThreePanel(Context context) {
        super(context);
        setOrientation(HORIZONTAL);
        setClipChildren(false);
        setClipToPadding(false);
    }

    public void panels(View left, View centre, View right) {
        removeAllViews();
        addPanel(left, .24f, true);
        addPanel(centre, .38f, true);
        addPanel(right, .38f, false);
    }

    private void addPanel(View view, float weight, boolean gap) {
        decorate(view);
        LayoutParams size = new LayoutParams(0, LayoutParams.MATCH_PARENT, weight);
        if (gap) size.rightMargin = dp(getContext(), 16);
        addView(view, size);
    }

    public static void decorate(View panel) {
        Context context = panel.getContext();
        GradientDrawable surface = new GradientDrawable(GradientDrawable.Orientation.TOP_BOTTOM,
                new int[]{0xb3212c39, 0xc00b121d});
        surface.setCornerRadius(dp(context, 16));
        surface.setStroke(dp(context, 1), 0x557f8996);
        panel.setBackground(surface);
        panel.setElevation(dp(context, 4));
        panel.setPadding(dp(context, 20), dp(context, 20), dp(context, 20), dp(context, 20));
        if (android.os.Build.VERSION.SDK_INT >= 28) {
            panel.setOutlineAmbientShadowColor(0x337f8996);
            panel.setOutlineSpotShadowColor(0x337f8996);
        }
    }

    public static TextView text(Context context, String value, int size) {
        TextView text = new TextView(context);
        text.setText(value);
        text.setTextSize(size);
        text.setTextColor(Color.WHITE);
        text.setTypeface(Typeface.create("sans-serif-light", Typeface.NORMAL));
        text.setLineSpacing(dp(context, 3), 1);
        return text;
    }

    public static TextView action(Context context, String label, Runnable action) {
        TextView row = text(context, label, 18);
        row.setGravity(Gravity.CENTER_VERTICAL);
        row.setPadding(dp(context, 8), 0, dp(context, 8), 0);
        row.setMinHeight(dp(context, 50));
        row.setFocusable(true);
        row.setFocusableInTouchMode(true);
        row.setBackground(focus(context));
        row.setOnClickListener(view -> action.run());
        return row;
    }

    /** Permanent inset control surface; row actions retain their separate focus-only outline. */
    public static StateListDrawable control(Context context, int radius) {
        StateListDrawable states = new StateListDrawable();
        for (boolean focused : new boolean[]{true, false}) {
            GradientDrawable shape = new GradientDrawable();
            shape.setColor(focused ? 0x603b4857 : 0x303b4857);
            shape.setCornerRadius(dp(context, radius));
            shape.setStroke(dp(context, 1), focused ? Color.WHITE : 0x887f8996);
            states.addState(focused ? new int[]{android.R.attr.state_focused} : new int[]{}, shape);
        }
        return states;
    }

    public static StateListDrawable focus(Context context) {
        GradientDrawable focused = new GradientDrawable();
        focused.setColor(0x263b4857);
        focused.setCornerRadius(dp(context, 10));
        focused.setStroke(dp(context, 1), Color.WHITE);
        StateListDrawable states = new StateListDrawable();
        states.addState(new int[]{android.R.attr.state_focused}, focused);
        GradientDrawable normal = new GradientDrawable();
        normal.setColor(Color.TRANSPARENT);
        normal.setCornerRadius(dp(context, 10));
        states.addState(new int[]{}, normal);
        return states;
    }

    public static int dp(Context context, int value) { return PreviewDialog.dp(context, value); }
}
