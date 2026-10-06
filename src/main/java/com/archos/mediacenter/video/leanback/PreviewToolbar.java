package com.archos.mediacenter.video.leanback;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.view.*;
import android.widget.LinearLayout;

/** Focus recolours the existing divider segment; it does not add a second underline or box. */
public final class PreviewToolbar extends LinearLayout {
    private final Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private View selectedSegment;
    public void setSelectedSegment(View view){selectedSegment=view;invalidate();}
    public PreviewToolbar(Context context) {
        super(context); setClipChildren(false); setClipToPadding(false);
        setPadding(0, 0, 0, PreviewDialog.dp(context, 3));setGravity(Gravity.BOTTOM);
    }
    @Override public void onViewAdded(View child) {
        super.onViewAdded(child);
        child.setBackground(null);
        int shift=PreviewDialog.dp(getContext(),6);
        child.setPadding(child.getPaddingLeft(),child.getPaddingTop()+shift,child.getPaddingRight(),Math.max(0,child.getPaddingBottom()-shift));
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
