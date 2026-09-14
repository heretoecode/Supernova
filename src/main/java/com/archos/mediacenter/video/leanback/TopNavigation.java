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
    private final android.widget.FrameLayout status;
    private final BooleanSupplier firstRow;
    private TextView selected;
    private final TextView[] tabs = new TextView[6];

    public TopNavigation(Context c, View content, IntConsumer navigate, BooleanSupplier firstRow) {
        super(c);
        this.content = content; this.firstRow = firstRow;
        setOrientation(VERTICAL);
        setBackgroundColor(ThemeManager.getInstance(c).getLeanbackBackgroundColor());
        bar = new LinearLayout(c); bar.setGravity(Gravity.CENTER_VERTICAL);
        bar.setPadding(dp(26), 0, dp(16), 0);
        bar.setBackgroundColor(0xff10283d);
        TextView brand = new TextView(c);
        brand.setText("NOVA"); brand.setTypeface(android.graphics.Typeface.create("sans-serif-light", 0)); brand.setTextSize(22); brand.setTextColor(0xffb7d7f5);
        brand.setPadding(0, 0, dp(20), 0); bar.addView(brand);
        String[] labels = {"Home", "Movies", "TV shows", "Network & files", "Settings", "Search"};
        for (int i = 0; i < labels.length; i++) {
            final int index = i;
            TextView tab = new TextView(c); tabs[i] = tab;
            tab.setText(labels[i]); tab.setContentDescription(labels[i]);
            tab.setTextColor(Color.WHITE); tab.setTextSize(15); tab.setGravity(Gravity.CENTER);
            tab.setTypeface(android.graphics.Typeface.create("sans-serif-medium", android.graphics.Typeface.NORMAL));
            tab.setSingleLine(true); tab.setFocusable(true); tab.setClickable(true);
            tab.setPadding(dp(12), dp(10), dp(12), dp(10));
            tab.setTextColor(new android.content.res.ColorStateList(new int[][]{new int[]{android.R.attr.state_selected}, new int[]{android.R.attr.state_focused}, new int[]{}}, new int[]{Color.WHITE, Color.WHITE, 0xffb4cbe0}));
            StateListDrawable bg = new StateListDrawable();
            GradientDrawable focus = new GradientDrawable();
            focus.setColor(0xff345571); focus.setCornerRadius(dp(4)); focus.setStroke(dp(2), 0xff8fceff);
            bg.addState(new int[]{android.R.attr.state_focused}, focus);
            GradientDrawable active = new GradientDrawable();
            active.setColor(0xff62bbf3); active.setCornerRadius(dp(2));
            android.graphics.drawable.LayerDrawable underline = new android.graphics.drawable.LayerDrawable(new android.graphics.drawable.Drawable[]{active});
            underline.setLayerHeight(0, dp(3)); underline.setLayerGravity(0, Gravity.BOTTOM);
            underline.setLayerInset(0, dp(10), 0, dp(10), dp(3));
            bg.addState(new int[]{android.R.attr.state_selected}, underline);
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
            if (i == 5) {
                bar.addView(new View(c), new LayoutParams(0, 1, 1));
                tab.setText("");
                android.graphics.drawable.Drawable search = c.getDrawable(com.archos.mediacenter.video.R.drawable.preview_search);
                search.setBounds(0, 0, dp(22), dp(22)); tab.setCompoundDrawables(search, null, null, null);
            }
            bar.addView(tab, new LayoutParams(-2, dp(46)));
        }
        status = new android.widget.FrameLayout(c);
        bar.addView(status, new LayoutParams(dp(68), dp(46)));
        selected = tabs[0]; selected.setSelected(true);
        addView(bar, new LayoutParams(-1, -2));
        addView(content, new LayoutParams(-1, 0, 1));
    }
    public android.widget.FrameLayout getStatusContainer() { return status; }
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
