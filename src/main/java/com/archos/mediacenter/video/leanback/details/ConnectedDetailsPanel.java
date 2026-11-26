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

    private boolean focusInContent;

    /** Whether the remote is in the open tab's content (bright underline) or not (dull underline). */
    void focusInContent(boolean value) {
        focusInContent = value;
    }

    /**
     * The tab row's line is the library toolbar line (owner, 21 Nov; DETAILS.md): white 35%, about 2 px, fading out at
     * both ends. The open tab keeps a white underline on it, bright while the remote is in its content, dull while the
     * remote is on the row (RULES §3); none under the focused tab's own box.
     */
    @Override
    protected void dispatchDraw(Canvas canvas) {
        super.dispatchDraw(canvas);
        if (selected == null || !(selected.getParent() instanceof View))
            return;
        View row = (View) selected.getParent();
        float density = getResources().getDisplayMetrics().density;
        float line = row.getBottom();
        float thickness = Math.max(1, density);
        paint.setStyle(Paint.Style.FILL);
        paint.setShader(new LinearGradient(0, 0, getWidth(), 0,
                                           new int[] {0x00FFFFFF, 0x59FFFFFF, 0x59FFFFFF, 0x00FFFFFF},
                                           new float[] {0f, .08f, .92f, 1f}, Shader.TileMode.CLAMP));
        canvas.drawRect(0, line - thickness, getWidth(), line, paint);
        paint.setShader(null);
        if (selected.isFocused())
            return;
        int[] at = new int[2];
        int[] mine = new int[2];
        selected.getLocationInWindow(at);
        getLocationInWindow(mine);
        float left = at[0] - mine[0] + selected.getPaddingLeft();
        float right = at[0] - mine[0] + selected.getWidth() - selected.getPaddingRight();
        paint.setColor(focusInContent ? 0xFFFFFFFF : 0x59FFFFFF);
        canvas.drawRect(left, line - 2 * thickness, right, line, paint);
    }
}
