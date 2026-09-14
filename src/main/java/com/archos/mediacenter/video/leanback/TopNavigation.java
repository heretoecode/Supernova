package com.archos.mediacenter.video.leanback;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.StateListDrawable;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.archos.mediacenter.video.utils.ThemeManager;
import java.util.function.IntConsumer;
import java.util.function.BooleanSupplier;

/** Optional navigation shell; the same native BrowseSupportFragment owns all library rows. */
public final class TopNavigation extends LinearLayout {
    private final LinearLayout bar;
    private final View content;
    private final BooleanSupplier firstRow;
    private TextView selected;
    private final TextView[] tabs = new TextView[6];

    public TopNavigation(Context c, View content, IntConsumer navigate, BooleanSupplier firstRow) {
        super(c);
        this.content = content; this.firstRow = firstRow;
        setOrientation(VERTICAL);
        setBackgroundColor(ThemeManager.getInstance(c).getLeanbackBackgroundColor());
        bar = new LinearLayout(c); bar.setGravity(Gravity.CENTER_VERTICAL);
        bar.setPadding(dp(24), dp(8), dp(24), dp(8));
        TextView brand = new TextView(c);
        brand.setText("NOVA"); brand.setTextSize(22); brand.setTextColor(0xffb7d7f5);
        brand.setPadding(0, 0, dp(20), 0); bar.addView(brand);
        String[] labels = {"Home", "Movies", "TV shows", "Network & files", "Settings", "Search"};
        for (int i = 0; i < labels.length; i++) {
            final int index = i;
            TextView tab = new TextView(c); tabs[i] = tab;
            tab.setText(labels[i]); tab.setContentDescription(labels[i]);
            tab.setTextColor(Color.WHITE); tab.setTextSize(15); tab.setGravity(Gravity.CENTER);
            tab.setTypeface(android.graphics.Typeface.create("sans-serif-medium", android.graphics.Typeface.NORMAL));
            tab.setSingleLine(true); tab.setFocusable(true); tab.setClickable(true);
            tab.setPadding(dp(10), dp(12), dp(10), dp(12));
            StateListDrawable bg = new StateListDrawable();
            GradientDrawable focus = new GradientDrawable();
            focus.setColor(0xff345571); focus.setCornerRadius(dp(4)); focus.setStroke(dp(2), 0xff8fceff);
            bg.addState(new int[]{android.R.attr.state_focused}, focus);
            GradientDrawable active = new GradientDrawable();
            active.setColor(0xff223b50); active.setCornerRadius(dp(4));
            bg.addState(new int[]{android.R.attr.state_selected}, active);
            bg.addState(new int[]{}, new android.graphics.drawable.ColorDrawable(Color.TRANSPARENT));
            tab.setBackground(bg);
            tab.setOnClickListener(v -> {
                if (index < 4) {
                    for (TextView t : tabs) t.setSelected(false);
                    selected = tab; tab.setSelected(true);
                }
                navigate.accept(index);
                if (index < 4) content.requestFocus();
            });
            tab.setOnKeyListener((v, key, event) -> {
                if (key == KeyEvent.KEYCODE_DPAD_DOWN && event.getAction() == KeyEvent.ACTION_DOWN) {
                    content.requestFocus(); return true;
                }
                return false;
            });
            bar.addView(tab, new LayoutParams(0, -2, i == 3 ? 1.5f : 1));
        }
        selected = tabs[0]; selected.setSelected(true);
        addView(bar, new LayoutParams(-1, -2));
        addView(content, new LayoutParams(-1, 0, 1));
    }
    private int dp(int n) { return (int)(n * getResources().getDisplayMetrics().density + .5f); }
    public boolean focusNavigation() {
        if (bar.hasFocus()) return false;
        selected.requestFocus(); return true;
    }
    @Override public boolean dispatchKeyEvent(KeyEvent event) {
        if (event.getAction() == KeyEvent.ACTION_DOWN && event.getKeyCode() == KeyEvent.KEYCODE_DPAD_UP
                && !bar.hasFocus() && firstRow.getAsBoolean()) {
            selected.requestFocus(); return true;
        }
        return super.dispatchKeyEvent(event);
    }
}
