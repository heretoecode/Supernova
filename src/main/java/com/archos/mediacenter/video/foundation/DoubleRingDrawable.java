package com.archos.mediacenter.video.foundation;

import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.Animatable;
import android.graphics.drawable.Drawable;
import android.os.SystemClock;

/**
 * The Supernova mark as the app's one loading ring (owner, 14 Nov 2026; RULES §21; mock-up brand/03 row 1): two
 * open white rings, the outer turning clockwise and the inner anticlockwise. Geometry follows the approved drawing on
 * a 100-unit square: outer radius 40, stroke 6; inner radius 24, stroke 5; each ring is an arc of 280 degrees.
 */
public final class DoubleRingDrawable extends Drawable implements Animatable, Runnable
{
    /** Below this size (about the top bar's 28 px) the strokes are drawn thicker so both rings stay clear. */
    static final float SMALL_SIZE_PX = 64f;
    static final float SMALL_STROKE_FACTOR = 1.6f;
    static final float ARC_DEGREES = 280f;

    /** Leading ends at rest, in Android's angles (0 = three o'clock, clockwise positive). */
    static final float OUTER_HEAD = 45f - 90f;
    static final float INNER_HEAD = 225f - 90f;

    private final Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final RectF circle = new RectF();
    private boolean running;
    private long started;
    private int alpha = 255;

    public DoubleRingDrawable()
    {
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeCap(Paint.Cap.ROUND);
        paint.setColor(0xffffffff);
    }

    /** Stroke width in pixels for a ring drawn with the given stroke (in mark units) at the given size. */
    static float stroke(float units, float sizePx)
    {
        float width = units * sizePx / 100f;
        if (sizePx < SMALL_SIZE_PX)
        {
            width = width * SMALL_STROKE_FACTOR;
        }
        return width;
    }

    @Override
    public void draw(Canvas canvas)
    {
        Rect bounds = getBounds();
        float size = Math.min(bounds.width(), bounds.height());
        if (size <= 0)
        {
            return;
        }
        long elapsed = running ? SystemClock.uptimeMillis() - started : 0;
        float outerTurn = DoubleRingMotion.outer(elapsed) - DoubleRingMotion.outer(0);
        float innerTurn = DoubleRingMotion.inner(elapsed) - DoubleRingMotion.inner(0);
        float x = bounds.exactCenterX();
        float y = bounds.exactCenterY();
        paint.setAlpha(alpha);
        // Outer ring: the head leads clockwise, so the arc runs from (head - 280) up to the head.
        ring(canvas, x, y, size * .40f, stroke(6f, size), OUTER_HEAD + outerTurn - ARC_DEGREES);
        // Inner ring: the head leads anticlockwise, so the arc runs from the head on for 280 degrees.
        ring(canvas, x, y, size * .24f, stroke(5f, size), INNER_HEAD + innerTurn);
    }

    private void ring(Canvas canvas, float x, float y, float radius, float width, float start)
    {
        circle.set(x - radius, y - radius, x + radius, y + radius);
        paint.setStrokeWidth(width);
        canvas.drawArc(circle, start, ARC_DEGREES, false, paint);
    }

    @Override
    public void start()
    {
        if (!running && isVisible())
        {
            running = true;
            started = SystemClock.uptimeMillis();
            run();
        }
    }

    @Override
    public void stop()
    {
        running = false;
        unscheduleSelf(this);
        invalidateSelf();
    }

    @Override
    public boolean isRunning()
    {
        return running;
    }

    @Override
    public void run()
    {
        if (!running || !isVisible() || getCallback() == null)
        {
            stop();
            return;
        }
        invalidateSelf();
        scheduleSelf(this, SystemClock.uptimeMillis() + 16);
    }

    @Override
    public boolean setVisible(boolean visible, boolean restart)
    {
        boolean changed = super.setVisible(visible, restart);
        if (!visible)
        {
            stop();
        }
        return changed;
    }

    @Override
    public void setAlpha(int value)
    {
        alpha = value;
        invalidateSelf();
    }

    @Override
    public void setColorFilter(ColorFilter filter)
    {
        paint.setColorFilter(filter);
        invalidateSelf();
    }

    @Override
    public int getOpacity()
    {
        return PixelFormat.TRANSLUCENT;
    }
}
