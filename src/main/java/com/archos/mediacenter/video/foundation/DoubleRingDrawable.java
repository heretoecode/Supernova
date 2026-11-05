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

/** Approved concentric geometry; only clockwise/anticlockwise highlights move. */
public final class DoubleRingDrawable extends Drawable implements Animatable, Runnable {
    private final Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final RectF circle = new RectF();
    private boolean running;
    private long started;
    private int alpha = 255;

    @Override public void draw(Canvas canvas) {
        Rect bounds = getBounds();
        float size = Math.min(bounds.width(), bounds.height());
        if (size <= 0) return;
        long elapsed = running ? SystemClock.uptimeMillis() - started : 0;
        ring(canvas, bounds.exactCenterX(), bounds.exactCenterY(), size * .40f, size,
                DoubleRingMotion.outer(elapsed), 1);
        ring(canvas, bounds.exactCenterX(), bounds.exactCenterY(), size * .27f, size,
                DoubleRingMotion.inner(elapsed), -1);
    }
    private void ring(Canvas canvas, float x, float y, float radius, float size, float angle, int direction) {
        circle.set(x-radius,y-radius,x+radius,y+radius);
        paint.setStyle(Paint.Style.STROKE); paint.setStrokeCap(Paint.Cap.ROUND);
        float stroke = Math.max(.8f,size*.025f);
        paint.setStrokeWidth(stroke*4); paint.setColor(0x185477ff); paint.setAlpha(alpha*24/255);
        canvas.drawOval(circle,paint);
        paint.setStrokeWidth(stroke*2); paint.setColor(0x405a98ff); paint.setAlpha(alpha*64/255);
        canvas.drawOval(circle,paint);
        paint.setStrokeWidth(stroke); paint.setColor(0xff18d8ff); paint.setAlpha(alpha*150/255);
        canvas.drawOval(circle,paint);
        // Short fading trail, constant speed; no oscillation, flashing or geometry rotation.
        for(int step=18;step>=0;step--) {
            paint.setColor(0xff80efff); paint.setAlpha(alpha*(19-step)/19);
            canvas.drawArc(circle,angle-direction*step*4f,direction*4.5f,false,paint);
        }
        paint.setColor(0xffe8ffff); paint.setAlpha(alpha); paint.setStrokeWidth(stroke*1.35f);
        canvas.drawArc(circle,angle-direction*2f,direction*3f,false,paint);
    }
    @Override public void start() {
        if (!running && isVisible()) { running=true; started=SystemClock.uptimeMillis(); run(); }
    }
    @Override public void stop() { running=false; unscheduleSelf(this); invalidateSelf(); }
    @Override public boolean isRunning() { return running; }
    @Override public void run() {
        if (!running || !isVisible() || getCallback()==null) { stop(); return; }
        invalidateSelf(); scheduleSelf(this,SystemClock.uptimeMillis()+16);
    }
    @Override public boolean setVisible(boolean visible, boolean restart) {
        boolean changed=super.setVisible(visible,restart);
        if (!visible) stop();
        return changed;
    }
    @Override public void setAlpha(int value) { alpha=value; invalidateSelf(); }
    @Override public void setColorFilter(ColorFilter filter) { paint.setColorFilter(filter); invalidateSelf(); }
    @Override public int getOpacity() { return PixelFormat.TRANSLUCENT; }
}
