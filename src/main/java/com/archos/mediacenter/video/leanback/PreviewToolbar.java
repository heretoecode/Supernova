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
    private boolean connected;
    private View edgeLeft,edgeRight;
    public void setConnected(boolean value){connected=value;invalidate();}
    public void setSelectedSegment(View view){selectedSegment=view;invalidate();}
    public PreviewToolbar(Context context) { this(context, false); }
    public PreviewToolbar(Context context, boolean boxed) {
        super(context); this.boxed = boxed; setClipChildren(false); setClipToPadding(false);
        setPadding(0, 0, 0, PreviewDialog.dp(context, 3));
    }
    public static View viewport(PreviewToolbar toolbar){
        Context c=toolbar.getContext();android.widget.HorizontalScrollView viewport=new android.widget.HorizontalScrollView(c);viewport.setHorizontalScrollBarEnabled(false);viewport.setFillViewport(true);viewport.setClipToPadding(false);viewport.setClipChildren(false);int outline=PreviewDialog.dp(c,3);viewport.setPadding(outline,outline,outline,outline);toolbar.setMinimumWidth(0);viewport.addView(toolbar,new android.widget.FrameLayout.LayoutParams(-2,-2));return viewport;
    }
    /** Keep a trailing action visible while only the left controls scroll at narrow/large-text sizes. */
    public static View withTrailingAction(PreviewToolbar controls,android.widget.TextView action){
        Context c=controls.getContext();LinearLayout row=new LinearLayout(c);row.setClipChildren(false);row.setClipToPadding(false);row.setGravity(android.view.Gravity.TOP);
        row.addView(viewport(controls),new LinearLayout.LayoutParams(0,-2,1));
        PreviewToolbar trailing=new PreviewToolbar(c,true);trailing.setPadding(PreviewDialog.dp(c,8),PreviewDialog.dp(c,3),PreviewDialog.dp(c,3),PreviewDialog.dp(c,3));trailing.addView(action,new LinearLayout.LayoutParams(-2,-2));row.addView(trailing,new LinearLayout.LayoutParams(-2,-2));
        controls.edgeRight=action;trailing.edgeLeft=controls.getChildCount()==0?null:controls.getChildAt(controls.getChildCount()-1);return row;
    }
    @Override public void onViewAdded(View child) {
        super.onViewAdded(child);
        child.setBackground(boxed && child.isFocusable() ? SharedThreePanel.control(getContext(), 10) : null);
        if (boxed && child instanceof android.widget.TextView) {
            TvButton.style((android.widget.TextView)child,true);
            ((android.widget.TextView)child).setSingleLine(true);
            ((android.widget.TextView)child).setEllipsize(android.text.TextUtils.TruncateAt.END);
            child.setMinimumHeight(TvLayout.dimension(getContext(), 44));
            child.setPadding(TvLayout.dimension(getContext(),12),TvLayout.dimension(getContext(),6),TvLayout.dimension(getContext(),12),TvLayout.dimension(getContext(),6));
            ((android.widget.TextView)child).setCompoundDrawablePadding(TvLayout.dimension(getContext(),6));
            child.setBackground(MenuFoundation.controlBackground(getContext()));
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
                if(next<0&&edgeLeft!=null)edgeLeft.requestFocus();else if(next>=getChildCount()&&edgeRight!=null)edgeRight.requestFocus();
                return true;
            }
            return false;
        });
    }
    @Override protected void dispatchDraw(Canvas canvas) {
        super.dispatchDraw(canvas);
        if (boxed || connected) return;
        float y = getHeight() - getResources().getDisplayMetrics().density;
        View focused=findFocus();View active=focused!=null?focused:selectedSegment;
        if(active!=null&&active.getParent()==this){paint.setColor(focused!=null?android.graphics.Color.WHITE:0x88ffffff);canvas.drawRect(active.getLeft(),y,active.getRight(),getHeight(),paint);}

    }
}
