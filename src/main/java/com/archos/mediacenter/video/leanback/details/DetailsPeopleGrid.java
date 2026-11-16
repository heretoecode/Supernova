package com.archos.mediacenter.video.leanback.details;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * Cast or crew on the Details tab (approved 14 Nov 2026, details-background/21): one row of up to eight portrait
 * cards, 2:3 like TMDB's photos, the name and role under each. No focus stops (the tab is for reading).
 */
final class DetailsPeopleGrid extends LinearLayout {
    static final int COLUMNS = 8;

    DetailsPeopleGrid(Context c) {
        super(c);
        setOrientation(HORIZONTAL);
        setFocusable(false);
        setDescendantFocusability(FOCUS_BLOCK_DESCENDANTS);
    }

    /** The space between two cards. */
    int gap() {
        return Math.round(16 * getResources().getDisplayMetrics().density);
    }

    @Override
    protected void onMeasure(int widthSpec, int heightSpec) {
        int width = MeasureSpec.getSize(widthSpec);
        int cell = Math.max(1, (width - gap() * (COLUMNS - 1)) / COLUMNS);
        int tallest = 0;
        for (int i = 0; i < getChildCount(); i++) {
            View child = getChildAt(i);
            if (child instanceof ViewGroup) {
                ViewGroup group = (ViewGroup) child;
                if (group.getChildCount() > 0 && group.getChildAt(0) instanceof ImageView) {
                    ViewGroup.LayoutParams portrait = group.getChildAt(0).getLayoutParams();
                    portrait.width = cell;
                    portrait.height = Math.round(cell * 1.5f);
                }
                for (int n = 1; n < group.getChildCount(); n++)
                    if (group.getChildAt(n) instanceof TextView)
                        group.getChildAt(n).getLayoutParams().height = ViewGroup.LayoutParams.WRAP_CONTENT;
            }
            child.measure(MeasureSpec.makeMeasureSpec(cell, MeasureSpec.EXACTLY),
                          MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED));
            tallest = Math.max(tallest, child.getMeasuredHeight());
        }
        setMeasuredDimension(width, resolveSize(tallest, heightSpec));
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        int cell = Math.max(1, (getWidth() - gap() * (COLUMNS - 1)) / COLUMNS);
        for (int i = 0; i < getChildCount(); i++) {
            View child = getChildAt(i);
            int left = i * (cell + gap());
            child.layout(left, 0, left + cell, child.getMeasuredHeight());
        }
    }
}
