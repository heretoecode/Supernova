package com.archos.mediacenter.video.leanback.files;

import android.content.Context;
import android.graphics.Canvas;
import android.widget.ScrollView;

/**
 * FL1 (owner, 18 Nov 2026): a Files list that scrolls clips at its own top and bottom, so nothing shows under the
 * breadcrumb, toolbar, tabs or top bar. The sides stay open, so a grown row or its ring may reach past them (D6).
 */
final class BelowBarScroll extends ScrollView {
    BelowBarScroll(Context context) {
        super(context);
    }

    @Override
    protected void dispatchDraw(Canvas canvas) {
        int saved = canvas.save();
        int side = getResources().getDisplayMetrics().widthPixels;
        canvas.clipRect(-side, getScrollY(), getWidth() + side, getScrollY() + getHeight());
        super.dispatchDraw(canvas);
        canvas.restoreToCount(saved);
    }
}
