package com.archos.mediacenter.video.leanback;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;

/**
 * The artwork edge (owner, 7 and 12 Nov 2026; RULES §14): at rest one thin white line exactly on the edge (about 60%)
 * with a soft inner shade just inside it, so the edge shows on light and dark artwork; focused, a solid thick white
 * ring. Every piece of artwork has it all the time.
 */
public final class ArtworkEdge extends Drawable
{
    static final int REST_LINE_ALPHA = 153;
    static final int SHADE_ALPHA = 64;

    private final Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final float density;
    private final float radiusDp;
    private final boolean focused;
    private int alpha = 255;

    public ArtworkEdge(Context context, float radiusDp, boolean focused)
    {
        this.density = context.getResources().getDisplayMetrics().density;
        this.radiusDp = radiusDp;
        this.focused = focused;
        paint.setStyle(Paint.Style.STROKE);
    }

    /** Line width in dp: hairline at rest, thick when focused. */
    static float lineDp(boolean focused)
    {
        return focused ? 3f : 0.75f;
    }

    @Override
    public void draw(Canvas canvas)
    {
        Rect bounds = getBounds();
        float radius = radiusDp * density;
        float line = Math.max(1f, lineDp(focused) * density);
        if (!focused)
        {
            // The soft inner shade: a wider, dark stroke just inside the line.
            float shade = 3f * density;
            RectF inner = new RectF(bounds.left + shade / 2, bounds.top + shade / 2,
                                    bounds.right - shade / 2, bounds.bottom - shade / 2);
            paint.setStrokeWidth(shade);
            paint.setColor(0xFF000000);
            paint.setAlpha(SHADE_ALPHA * alpha / 255);
            canvas.drawRoundRect(inner, Math.max(0, radius - shade / 2), Math.max(0, radius - shade / 2), paint);
        }
        RectF edge = new RectF(bounds.left + line / 2, bounds.top + line / 2,
                               bounds.right - line / 2, bounds.bottom - line / 2);
        paint.setStrokeWidth(line);
        paint.setColor(0xFFFFFFFF);
        paint.setAlpha((focused ? 255 : REST_LINE_ALPHA) * alpha / 255);
        canvas.drawRoundRect(edge, radius, radius, paint);
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
