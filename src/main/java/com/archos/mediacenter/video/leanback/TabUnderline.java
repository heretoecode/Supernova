package com.archos.mediacenter.video.leanback;

import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;

/**
 * The open tab's underline on a tab row (RULES §3): bright while the remote is below the row, dull while it is on the
 * row. Drawn along the bottom of the tab, on the row's own faint line.
 */
public final class TabUnderline extends Drawable
{
    public static final int BRIGHT = 0xFFFFFFFF;
    public static final int DULL = 0x59FFFFFF;

    private final Paint paint = new Paint();
    private final int height;

    public TabUnderline(int colour, int height)
    {
        paint.setColor(colour);
        this.height = Math.max(1, height);
    }

    @Override
    public void draw(Canvas canvas)
    {
        Rect bounds = getBounds();
        canvas.drawRect(bounds.left, bounds.bottom - height, bounds.right, bounds.bottom, paint);
    }

    @Override
    public void setAlpha(int alpha)
    {
        paint.setAlpha(alpha);
    }

    @Override
    public void setColorFilter(ColorFilter filter)
    {
        paint.setColorFilter(filter);
    }

    @Override
    public int getOpacity()
    {
        return PixelFormat.TRANSLUCENT;
    }
}
