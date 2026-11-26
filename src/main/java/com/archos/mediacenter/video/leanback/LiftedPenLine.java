package com.archos.mediacenter.video.leanback;

import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;

/**
 * The toolbar line (owner, 19 Nov 2026, judged on the TV, `rows-captions/tv/08-toolbar-line-35.png`; LIBRARY.md):
 * white at 35%, about 2 px in the middle, thinning towards both ends like a lifted pen. The library toolbar, the
 * Details tab row and the player's tabs all use it.
 */
public final class LiftedPenLine extends Drawable
{
    static final int COLOUR = 0x59FFFFFF;
    /** How much of each end tapers. */
    static final float TAPER = 0.18f;

    private final Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);

    public LiftedPenLine()
    {
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(COLOUR);
    }

    @Override
    public void draw(Canvas canvas)
    {
        Rect b = getBounds();
        float width = b.width();
        float half = b.height() / 2f;
        float middle = b.exactCenterY();
        float taper = width * TAPER;
        Path pen = new Path();
        pen.moveTo(b.left, middle);
        pen.quadTo(b.left + taper / 2f, middle - half, b.left + taper, middle - half);
        pen.lineTo(b.right - taper, middle - half);
        pen.quadTo(b.right - taper / 2f, middle - half, b.right, middle);
        pen.quadTo(b.right - taper / 2f, middle + half, b.right - taper, middle + half);
        pen.lineTo(b.left + taper, middle + half);
        pen.quadTo(b.left + taper / 2f, middle + half, b.left, middle);
        pen.close();
        canvas.drawPath(pen, paint);
    }

    @Override
    public void setAlpha(int alpha)
    {
        paint.setAlpha(alpha * 0x59 / 255);
        invalidateSelf();
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
