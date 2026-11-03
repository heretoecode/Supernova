package com.archos.mediacenter.video.leanback;
import android.graphics.*;
import android.graphics.drawable.Animatable;
import android.graphics.drawable.Drawable;
import android.os.SystemClock;

/** Thin indeterminate ring; driven only while its host is visible. */
public final class ThinSpinner extends Drawable implements Animatable, Runnable {
    private final Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private boolean running;
    private float angle;

    @Override
    public void draw(Canvas c) {
        Rect b = getBounds();
        float stroke = Math.max(1, b.width() / 14f);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(stroke);
        paint.setStrokeCap(Paint.Cap.ROUND);
        RectF r = new RectF(b);
        r.inset(stroke, stroke);
        paint.setColor(0x304ca9ce);
        c.drawOval(r, paint);
        paint.setColor(0xff59d8ff);
        c.drawArc(r, angle, 95, false, paint);
    }

    @Override
    public void start() {
        if (!running) {
            running = true;
            run();
        }
    }

    @Override
    public void stop() {
        running = false;
        unscheduleSelf(this);
    }

    @Override
    public boolean isRunning() {
        return running;
    }

    @Override
    public void run() {
        if (!running)
            return;
        angle = (angle + 9) % 360;
        invalidateSelf();
        scheduleSelf(this, SystemClock.uptimeMillis() + 25);
    }

    @Override
    public void setAlpha(int a) {
        paint.setAlpha(a);
    }

    @Override
    public void setColorFilter(ColorFilter f) {
        paint.setColorFilter(f);
    }

    @Override
    public int getOpacity() {
        return PixelFormat.TRANSLUCENT;
    }
}
