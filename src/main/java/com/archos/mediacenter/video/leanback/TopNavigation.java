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
    private final PreviewFocusRail group;
    private static final int[] VISUAL_ORDER = {0, 1, 2, 3, 5, 4};
    private final PreviewBackdrop artwork;
    private final View content;
    private final android.widget.FrameLayout scanStatus;
    private final android.widget.FrameLayout status;
    private final BooleanSupplier firstRow;
    private TextView selected;
    private final TextView[] tabs = new TextView[6];

    public TopNavigation(Context c, View content, IntConsumer navigate, BooleanSupplier firstRow) {
        super(c);
        this.content = content; this.firstRow = firstRow;
        scanStatus = new android.widget.FrameLayout(c);scanStatus.setFocusable(false);scanStatus.setDescendantFocusability(FOCUS_BLOCK_DESCENDANTS);
        setOrientation(VERTICAL);
        artwork = new PreviewBackdrop(c); setBackground(artwork);
        bar = new LinearLayout(c); bar.setClipChildren(false);bar.setClipToPadding(false); bar.setGravity(Gravity.CENTER_VERTICAL);
        bar.setPadding(dp(26), 0, dp(26), 0);
        bar.setBackgroundColor(Color.TRANSPARENT);
        TextView brand = new TextView(c);
        brand.setText("SUPERNOVA"); brand.setTypeface(android.graphics.Typeface.create("sans-serif-light", android.graphics.Typeface.NORMAL)); brand.setTextSize(19); brand.setTextColor(Color.WHITE);
        brand.setGravity(Gravity.CENTER_VERTICAL); brand.setPadding(0, 0, 0, 0); bar.addView(brand, new LayoutParams(dp(136), -1));
        group = new PreviewFocusRail(c);group.setClipChildren(false);group.setClipToPadding(false); group.setGravity(Gravity.START|Gravity.CENTER_VERTICAL);
        bar.addView(group, new LayoutParams(0, -1, 1));
        String[] labels = {"Home", "Movies", "TV Shows", "Network & Files", "Settings", "Search"};
        for (int i = 0; i < labels.length; i++) {
            final int index = i;
            TextView tab = new TextView(c); tabs[i] = tab;
            tab.setText(labels[i]); tab.setContentDescription(labels[i]); tab.setTag("semantic:nav:" + index);
            tab.setTextColor(Color.WHITE); tab.setTextSize(19); tab.setGravity(Gravity.CENTER);
            tab.setTypeface(android.graphics.Typeface.create("sans-serif-light", android.graphics.Typeface.NORMAL));
            tab.setSingleLine(true); tab.setFocusable(true); tab.setFocusableInTouchMode(true); tab.setClickable(true);
            tab.setPadding(dp(7), dp(6), dp(7), dp(6));
            styleTab(tab);
            tab.setOnClickListener(v -> {
                if (index < 4) {
                    for (TextView t : tabs) t.setSelected(false);
                    selected = tab; tab.setSelected(true);
                }
                navigate.accept(index);

            });
            tab.setOnKeyListener((v, key, event) -> {
                if (key == KeyEvent.KEYCODE_DPAD_DOWN && event.getAction() == KeyEvent.ACTION_DOWN) {
                    content.requestFocus(); return true;
                }
                if (event.getAction() == KeyEvent.ACTION_DOWN) {
                    if (key == KeyEvent.KEYCODE_DPAD_UP) return true;
                    if (key == KeyEvent.KEYCODE_DPAD_LEFT || key == KeyEvent.KEYCODE_DPAD_RIGHT) {
                        for (int position = 0; position < VISUAL_ORDER.length; position++) {
                            if (VISUAL_ORDER[position] != index) continue;
                            int target = position + (key == KeyEvent.KEYCODE_DPAD_LEFT ? -1 : 1);
                            if (target >= 0 && target < VISUAL_ORDER.length) tabs[VISUAL_ORDER[target]].requestFocus();
                            return true;
                        }
                    }
                }
                return false;
            });
            if (i == 5 || i == 4) {
                tab.setText("");
                // Custom drawables do not automatically apply Drawable.setTint to their Paint.
                tab.setLayerType(View.LAYER_TYPE_SOFTWARE,null);
                android.graphics.drawable.Drawable search = new PreviewIcon(i==4?"cog":"search");
                search.setBounds(0, 0, dp(22), dp(22)); tab.setCompoundDrawables(search, null, null, null);
            }
            if(i==3){View gap=new View(c);group.addView(gap,new LayoutParams(0,1,1));}
            group.addView(tab, new LayoutParams(-2, dp(36)));
        }
        group.removeView(tabs[5]);group.addView(tabs[5],group.indexOfChild(tabs[4]),new LayoutParams(-2,dp(36)));
        status = new android.widget.FrameLayout(c);
        bar.addView(status, new LayoutParams(dp(64), dp(46)));
        android.widget.TextClock clock=new android.widget.TextClock(c);clock.setTag("preview-default-clock");clock.setFormat12Hour("h:mm");clock.setFormat24Hour("HH:mm");clock.setTypeface(android.graphics.Typeface.create("sans-serif-light",android.graphics.Typeface.NORMAL));clock.setTextSize(19);clock.setTextColor(0xffd6e5f3);clock.setGravity(Gravity.CENTER);status.addView(clock,new android.widget.FrameLayout.LayoutParams(-1,-1));
        selected = tabs[0]; selected.setSelected(true);
        addView(bar, new LayoutParams(-1, dp(52)));
        android.widget.FrameLayout stage=new android.widget.FrameLayout(c);
        stage.addView(content,new android.widget.FrameLayout.LayoutParams(-1,-1));
        android.widget.FrameLayout.LayoutParams scanParams=new android.widget.FrameLayout.LayoutParams(-2,-2,Gravity.BOTTOM|Gravity.END);
        scanParams.setMargins(dp(20),dp(12),dp(20),dp(16));stage.addView(scanStatus,scanParams);
        addView(stage,new LayoutParams(-1,0,1));
    }
    private final android.content.SharedPreferences.OnSharedPreferenceChangeListener accentListener=(prefs,key)->{if("preview_accent41".equals(key)){for(TextView tab:tabs)styleTab(tab);group.refreshColour();invalidate();}};
    private void styleTab(TextView tab){
        tab.setTextColor(Color.WHITE);
        tab.setShadowLayer(0,0,0,0);
        tab.setBackground(null);
        for(android.graphics.drawable.Drawable icon:tab.getCompoundDrawables())if(icon instanceof PreviewIcon)((PreviewIcon)icon).focus(false,0);

    }
    @Override protected void onAttachedToWindow(){super.onAttachedToWindow();androidx.preference.PreferenceManager.getDefaultSharedPreferences(getContext()).registerOnSharedPreferenceChangeListener(accentListener);for(TextView tab:tabs)styleTab(tab);}
    private boolean scrolled;private android.animation.ValueAnimator scrimAnimation;private int scrimAlpha;
    public void setScrolled(boolean value){scrolled=value;if(scrimAnimation!=null)scrimAnimation.cancel();scrimAlpha=0;bar.setBackgroundColor(Color.TRANSPARENT);}
    public void setArtwork(android.net.Uri uri) { artwork.load(uri); }
    public void setFeaturedDirection(int direction){artwork.setMotionDirection(direction);}
    public boolean readyForFirstFrame(){return artwork.readyForFirstFrame();}
    public void selectTab(int index) { if(index<0||index>=6)return;setBackground(index>=3?new PreviewUtilityBackground(getContext()):artwork); for(TextView t:tabs)t.setSelected(false); selected=tabs[index];selected.setSelected(true);if(index>=3)setScrolled(false); }
    @Override protected void onDetachedFromWindow() { androidx.preference.PreferenceManager.getDefaultSharedPreferences(getContext()).unregisterOnSharedPreferenceChangeListener(accentListener);artwork.release();super.onDetachedFromWindow(); }
    /** Exactly one visible scan-status owner: the landing panel or this shell. */
    public void setEmbeddedScanStatus(boolean embedded){scanStatus.setVisibility(embedded?GONE:VISIBLE);}
    public android.widget.FrameLayout getScanContainer() { return scanStatus; }
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
