package com.archos.mediacenter.video.leanback;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.StateListDrawable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

/** Ordinary controls illuminate their contents; artwork owns its separate perimeter. */
final class PreviewContentFocus extends StateListDrawable {
    private final int accent;
    private final float radius;
    private boolean focused;
    PreviewContentFocus(Context context) {
        accent = PreviewAccent.color(context);
        radius = 4 * context.getResources().getDisplayMetrics().density;
        addState(new int[]{}, new ColorDrawable(Color.TRANSPARENT));
    }
    @Override public boolean isStateful() { return true; }
    @Override protected boolean onStateChange(int[] states) {
        boolean next = false;
        for (int state : states) if (state == android.R.attr.state_focused) next = true;
        focused = next;
        if (getCallback() instanceof View) apply((View) getCallback());
        return super.onStateChange(states);
    }
    @Override public void draw(Canvas canvas) {
        // The background may acquire its View callback after its initial state.
        // Apply again only when newly bound children need their state propagated.
        if (getCallback() instanceof View) apply((View) getCallback());
    }
    private void apply(View view) {
        if (view instanceof TextView) {
            TextView text = (TextView) view;
            float desired = focused ? radius : 0;
            if (text.getShadowRadius() != desired)
                text.setShadowLayer(desired, 0, 0, accent);
            for (Drawable icon : text.getCompoundDrawables()) applyIcon(icon);
        } else if (view instanceof ImageView) {
            applyIcon(((ImageView) view).getDrawable());
        }
        if (view instanceof ViewGroup) {
            ViewGroup group = (ViewGroup) view;
            for (int i = 0; i < group.getChildCount(); i++) apply(group.getChildAt(i));
        }
    }
    private void applyIcon(Drawable icon) {
        if (icon instanceof PreviewIcon) ((PreviewIcon) icon).focus(focused, accent);
    }
}
