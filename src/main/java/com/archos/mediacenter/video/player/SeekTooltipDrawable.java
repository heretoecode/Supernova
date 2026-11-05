package com.archos.mediacenter.video.player;

import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import com.archos.mediacenter.video.leanback.SupernovaStyle;

/**
 * The time bubble above the seek dot (approved 0.156 HUD, owner notes 0.155 #8): a charcoal glass box with a faint
 * edge, raised clearly above the dot. The pointer position is kept so callers can still follow the dot.
 */
final class SeekTooltipDrawable extends Drawable
{
    private final Paint fill = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint edge = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final float density;
    private float pointer;

    SeekTooltipDrawable(float density, float pointer)
    {
        this.density = density;
        this.pointer = pointer;
        fill.setColor(SupernovaStyle.GLASS);
        fill.setStyle(Paint.Style.FILL);
        edge.setColor(SupernovaStyle.GLASS_EDGE);
        edge.setStyle(Paint.Style.STROKE);
        edge.setStrokeWidth(Math.max(1f, density));
    }

    void setPointer(float pointer)
    {
        this.pointer = pointer;
        invalidateSelf();
    }

    float pointer()
    {
        return pointer;
    }

    @Override
    public void draw(Canvas canvas)
    {
        Rect bounds = getBounds();
        float radius = 8 * density;
        float inset = edge.getStrokeWidth() / 2f;
        RectF box = new RectF(bounds.left + inset, bounds.top + inset, bounds.right - inset, bounds.bottom - inset);
        canvas.drawRoundRect(box, radius, radius, fill);
        canvas.drawRoundRect(box, radius, radius, edge);
    }

    @Override
    public void setAlpha(int alpha)
    {
        fill.setAlpha(alpha);
        edge.setAlpha(alpha);
        invalidateSelf();
    }

    @Override
    public void setColorFilter(ColorFilter filter)
    {
        fill.setColorFilter(filter);
        edge.setColorFilter(filter);
        invalidateSelf();
    }

    @Override
    public int getOpacity()
    {
        return PixelFormat.TRANSLUCENT;
    }
}
