package com.archos.mediacenter.video.leanback.files;

import android.content.Context;
import android.graphics.Color;
import android.view.Gravity;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ScrollView;
import android.widget.TextView;

/**
 * The sign that there is more (owner, 17 Nov 2026, F15 option C, F36 app-wide): no scroll bar; a down arrow in the
 * right-hand margin at the foot while there is more below, an up arrow at the top once there is something above
 * (faint when there is not). No arrows at all when everything fits.
 */
public final class ScrollArrows extends FrameLayout
{
    static final float FAINT = 0.3f;

    private final TextView up;
    private final TextView down;
    private final ScrollView scroll;

    public ScrollArrows(Context context, ScrollView scroll, int margin)
    {
        super(context);
        this.scroll = scroll;
        setClickable(false);
        setFocusable(false);
        setImportantForAccessibility(IMPORTANT_FOR_ACCESSIBILITY_NO);
        up = arrow(context, "up");
        down = arrow(context, "down");
        int size = Math.round(margin * 0.5f);
        LayoutParams top = new LayoutParams(size, size, Gravity.TOP | Gravity.END);
        top.rightMargin = (margin - size) / 2;
        top.topMargin = Math.round(12 * getResources().getDisplayMetrics().density);
        addView(up, top);
        LayoutParams foot = new LayoutParams(size, size, Gravity.BOTTOM | Gravity.END);
        foot.rightMargin = (margin - size) / 2;
        foot.bottomMargin = Math.round(12 * getResources().getDisplayMetrics().density);
        addView(down, foot);
        scroll.setOnScrollChangeListener((view, x, y, oldX, oldY) -> update());
        scroll.addOnLayoutChangeListener((view, l, t, r, b, oldL, oldT, oldR, oldB) -> post(this::update));
        update();
    }

    private static TextView arrow(Context context, String icon)
    {
        TextView arrow = new TextView(context);
        arrow.setGravity(Gravity.CENTER);
        arrow.setTextColor(Color.WHITE);
        arrow.setText(icon.equals("up") ? "▲" : "▼");
        arrow.setTextSize(14);
        arrow.setFocusable(false);
        return arrow;
    }

    /** Which arrows show, and how bright (pure, for tests): 0 none, else alpha for up and down. */
    static float[] state(boolean fits, boolean above, boolean below)
    {
        if (fits)
        {
            return new float[] {0f, 0f};
        }
        return new float[] {above ? 1f : FAINT, below ? 1f : FAINT};
    }

    void update()
    {
        boolean fits = !scroll.canScrollVertically(1) && !scroll.canScrollVertically(-1);
        float[] alpha = state(fits, scroll.canScrollVertically(-1), scroll.canScrollVertically(1));
        up.setVisibility(alpha[0] == 0f ? INVISIBLE : VISIBLE);
        down.setVisibility(alpha[1] == 0f ? INVISIBLE : VISIBLE);
        up.setAlpha(alpha[0]);
        down.setAlpha(alpha[1]);
    }

    public View upArrow()
    {
        return up;
    }

    public View downArrow()
    {
        return down;
    }
}
