package com.archos.mediacenter.video.leanback;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.view.*;
import android.widget.LinearLayout;

/** Shared library boxes and preserved Details tabs, with deterministic remote edges. */
public final class PreviewToolbar extends LinearLayout {
    private final Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private View selectedSegment;
    private final boolean boxed;
    public void setSelectedSegment(View view){selectedSegment=view;invalidate();}
    public PreviewToolbar(Context context) { this(context, false); }
    public PreviewToolbar(Context context, boolean boxed) {
        super(context); this.boxed = boxed; setClipChildren(false); setClipToPadding(false);
        setPadding(0, 0, 0, PreviewDialog.dp(context, 3));
    }
    @Override public void onViewAdded(View child) {
        super.onViewAdded(child);
        child.setBackground(boxed && child.isFocusable() ? SharedThreePanel.control(getContext(), 10) : null);
        if (boxed && child instanceof android.widget.TextView) {
            ((android.widget.TextView)child).setTextSize(14);
            ((android.widget.TextView)child).setSingleLine(true);
            ((android.widget.TextView)child).setEllipsize(android.text.TextUtils.TruncateAt.END);
            ((android.widget.TextView)child).setMaxWidth(PreviewDialog.dp(getContext(), 220));
            child.setMinimumHeight(PreviewDialog.dp(getContext(), 40));
        }
        child.setOnFocusChangeListener((v, focused) -> invalidate());
        child.setOnKeyListener((v, key, event) -> {
            if (event.getAction() != KeyEvent.ACTION_DOWN) return false;
            int position = indexOfChild(v);
            if (key == KeyEvent.KEYCODE_DPAD_LEFT || key == KeyEvent.KEYCODE_DPAD_RIGHT) {
                int next = position + (key == KeyEvent.KEYCODE_DPAD_LEFT ? -1 : 1);
                int step = key == KeyEvent.KEYCODE_DPAD_LEFT ? -1 : 1;
                while (next >= 0 && next < getChildCount()) {
                    View candidate = getChildAt(next);
                    if (candidate.getVisibility() == VISIBLE && candidate.isEnabled() && candidate.isFocusable() && candidate.requestFocus()) break;
                    next += step;
                }
                return true;
            }
            return false;
        });
    }
    @Override protected void dispatchDraw(Canvas canvas) {
        super.dispatchDraw(canvas);
        if (boxed) return;
        float y = getHeight() - getResources().getDisplayMetrics().density;
        paint.setColor(0x99ffffff); canvas.drawRect(0, y, getWidth(), getHeight(), paint);
        View focused = selectedSegment!=null?selectedSegment:findFocus();
        if (focused != null && focused.getParent() == this) {
            int colour=PreviewAccent.color(getContext());float halo=8*getResources().getDisplayMetrics().density;
            paint.setShader(new android.graphics.LinearGradient(0,y-halo,0,y,new int[]{colour&0xffffff,(colour&0xffffff)|0x30000000},null,android.graphics.Shader.TileMode.CLAMP));
            canvas.drawRect(focused.getLeft(),y-halo,focused.getRight(),y,paint);paint.setShader(null);
            paint.setColor(colour);
            canvas.drawRect(focused.getLeft(), y, focused.getRight(), getHeight(), paint);
        }
    }
}
