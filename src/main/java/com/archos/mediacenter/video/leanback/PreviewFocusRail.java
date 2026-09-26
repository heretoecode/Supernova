package com.archos.mediacenter.video.leanback;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.RectF;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.LinearLayout;

/** A single travelling boundary for adjacent navigation controls. Cards keep their own focus. */
public final class PreviewFocusRail extends LinearLayout {
    private PreviewFocusGlow boundary;
    private final RectF current = new RectF();
    private ValueAnimator movement;
    private boolean visible;
    private final ViewTreeObserver.OnGlobalFocusChangeListener focusListener = (oldView, next) -> moveTo(next);

    public PreviewFocusRail(Context context) {
        super(context);
        boundary = new PreviewFocusGlow(context);
        setClipChildren(false);
        setClipToPadding(false);
    }

    public void refreshColour() {
        boundary = new PreviewFocusGlow(getContext());
        invalidate();
    }

    private void moveTo(View next) {
        if (movement != null) movement.cancel();
        if (next == null || next.getParent() != this) {
            visible = false;
            invalidate();
            return;
        }
        Rect bounds = new Rect();
        next.getDrawingRect(bounds);
        offsetDescendantRectToMyCoords(next, bounds);
        RectF destination = new RectF(bounds);
        if (!visible || current.isEmpty()) {
            current.set(destination);
            visible = true;
            invalidate();
            return;
        }
        RectF origin = new RectF(current);
        movement = ValueAnimator.ofFloat(0f, 1f);
        movement.setDuration(160);
        movement.addUpdateListener(animation -> {
            float progress = (float) animation.getAnimatedValue();
            current.set(origin.left + (destination.left - origin.left) * progress,
                    origin.top + (destination.top - origin.top) * progress,
                    origin.right + (destination.right - origin.right) * progress,
                    origin.bottom + (destination.bottom - origin.bottom) * progress);
            invalidate();
        });
        movement.start();
    }

    @Override protected void dispatchDraw(Canvas canvas) {
        super.dispatchDraw(canvas);
        if (visible) {
            boundary.setBounds(Math.round(current.left), Math.round(current.top),
                    Math.round(current.right), Math.round(current.bottom));
            boundary.draw(canvas);
        }
    }

    @Override protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        getViewTreeObserver().addOnGlobalFocusChangeListener(focusListener);
        post(() -> moveTo(findFocus()));
    }

    @Override protected void onDetachedFromWindow() {
        getViewTreeObserver().removeOnGlobalFocusChangeListener(focusListener);
        if (movement != null) movement.cancel();
        visible = false;
        super.onDetachedFromWindow();
    }
}
