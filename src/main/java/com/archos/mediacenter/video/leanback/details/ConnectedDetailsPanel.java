package com.archos.mediacenter.video.leanback.details;

import android.content.Context;
import android.graphics.*;
import android.view.View;
import android.widget.LinearLayout;

/** One compound perimeter and fill, with an open join beneath the selected tab. */
final class ConnectedDetailsPanel extends LinearLayout {
    private final Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private View selected;

    ConnectedDetailsPanel(Context context) {
        super(context);
        setOrientation(VERTICAL);
        setClipChildren(false);
        setClipToPadding(false);
        setWillNotDraw(false);
        setTag("details.connected.frame");
    }

    void selected(View tab) {
        selected = tab;
        invalidate();
    }

    /**
     * The shared tab row's lines (RULES §3; details-background/21): a faint line across under the tabs and the open
     * tab underlined on it, bright while the remote is below the row, dull while it is on the row. No box, no fill:
     * the tab content sits straight on the charcoal page.
     */
    @Override
    protected void dispatchDraw(Canvas canvas) {
        super.dispatchDraw(canvas);
        if (selected == null || !(selected.getParent() instanceof View))
            return;
        View row = (View) selected.getParent();
        float density = getResources().getDisplayMetrics().density;
        float line = row.getBottom();
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(0x24FFFFFF);
        canvas.drawRect(0, line - Math.max(1, density), getWidth(), line, paint);
        int[] at = new int[2];
        int[] mine = new int[2];
        selected.getLocationInWindow(at);
        getLocationInWindow(mine);
        float left = at[0] - mine[0];
        // The focused tab is the white box alone: no underline under it (D4, RULES §3).
        if (selected.isFocused())
            return;
        paint.setColor(row.hasFocus() ? 0x59FFFFFF : Color.WHITE);
        canvas.drawRect(left, line - 2 * density, left + selected.getWidth(), line, paint);
    }
}
