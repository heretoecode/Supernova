package com.archos.mediacenter.video.leanback;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.drawable.Animatable;
import android.view.View;

import com.archos.mediacenter.video.foundation.DoubleRingDrawable;
import com.archos.mediacenter.video.leanback.overlay.LocalImportActivity;
import com.archos.mediaprovider.video.NetworkScannerReceiver;

/**
 * The one signal slot on the left of "Files" in the top bar (owner, 12 Nov 2026; RULES §2): the spinning ring while
 * a scan runs, otherwise a small white dot when the library needs attention, otherwise nothing. It keeps its size, so
 * nothing in the bar shifts when it changes. Not focusable.
 */
public final class TopBarSignal extends View
{
    public enum State
    {
        NONE,
        DOT,
        RING
    }

    private static final long CHECK_MS = 1000;

    private final DoubleRingDrawable ring = new DoubleRingDrawable();
    private final Paint dot = new Paint(Paint.ANTI_ALIAS_FLAG);
    private State state = State.NONE;
    private final Runnable check = new Runnable()
    {
        @Override
        public void run()
        {
            show(decide(scanning(), LibraryHealth.needsAttentionCount(getContext())));
            postDelayed(this, CHECK_MS);
        }
    };

    public TopBarSignal(Context context)
    {
        super(context);
        setFocusable(false);
        setImportantForAccessibility(IMPORTANT_FOR_ACCESSIBILITY_YES);
        dot.setColor(0xffffffff);
        ring.setCallback(this);
    }

    /** A scan outranks attention; only one thing shows at a time. */
    public static State decide(boolean scanning, int needsAttention)
    {
        if (scanning)
        {
            return State.RING;
        }
        return needsAttention > 0 ? State.DOT : State.NONE;
    }

    private static boolean scanning()
    {
        return NetworkScannerReceiver.isScannerWorking() || LocalImportActivity.moving();
    }

    public State state()
    {
        return state;
    }

    void show(State next)
    {
        if (next == state)
        {
            return;
        }
        state = next;
        if (state == State.RING)
        {
            ring.setVisible(true, false);
            ring.start();
            setContentDescription("Scanning the library");
        }
        else
        {
            ring.stop();
            setContentDescription(state == State.DOT ? "The library needs attention" : null);
        }
        invalidate();
    }

    @Override
    protected void onAttachedToWindow()
    {
        super.onAttachedToWindow();
        removeCallbacks(check);
        post(check);
    }

    @Override
    protected void onDetachedFromWindow()
    {
        removeCallbacks(check);
        ((Animatable) ring).stop();
        super.onDetachedFromWindow();
    }

    @Override
    protected boolean verifyDrawable(android.graphics.drawable.Drawable who)
    {
        return who == ring || super.verifyDrawable(who);
    }

    @Override
    protected void onDraw(Canvas canvas)
    {
        super.onDraw(canvas);
        int size = Math.min(getWidth(), getHeight());
        if (size <= 0)
        {
            return;
        }
        int left = (getWidth() - size) / 2;
        int top = (getHeight() - size) / 2;
        if (state == State.RING)
        {
            ring.setBounds(left, top, left + size, top + size);
            ring.draw(canvas);
        }
        else if (state == State.DOT)
        {
            canvas.drawCircle(getWidth() / 2f, getHeight() / 2f, size * 0.18f, dot);
        }
    }
}
