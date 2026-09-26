package com.archos.mediacenter.video.leanback;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.view.*;
import android.widget.LinearLayout;

/** Focus recolours the existing divider segment; it does not add a second underline or box. */
public final class PreviewToolbar extends LinearLayout {
    private final Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
    public PreviewToolbar(Context context) {
        super(context); setClipChildren(false); setClipToPadding(false);
        setPadding(0, 0, 0, PreviewDialog.dp(context, 3));
    }
    @Override public void onViewAdded(View child) {
        super.onViewAdded(child);
        child.setBackground(null);
        child.setOnFocusChangeListener((v, focused) -> invalidate());
        child.setOnKeyListener((v, key, event) -> {
            if (event.getAction() != KeyEvent.ACTION_DOWN) return false;
            int position = indexOfChild(v);
            if (key == KeyEvent.KEYCODE_DPAD_LEFT || key == KeyEvent.KEYCODE_DPAD_RIGHT) {
                int next = position + (key == KeyEvent.KEYCODE_DPAD_LEFT ? -1 : 1);
                if (next >= 0 && next < getChildCount()) getChildAt(next).requestFocus();
                return true;
            }
            return false;
        });
    }
    @Override protected void dispatchDraw(Canvas canvas) {
        super.dispatchDraw(canvas);
        float y = getHeight() - getResources().getDisplayMetrics().density;
        paint.setColor(0x99ffffff); canvas.drawRect(0, y, getWidth(), getHeight(), paint);
        View focused = findFocus();
        if (focused != null && focused.getParent() == this) {
            paint.setColor(PreviewAccent.color(getContext()));
            canvas.drawRect(focused.getLeft(), y, focused.getRight(), getHeight(), paint);
        }
    }
}
