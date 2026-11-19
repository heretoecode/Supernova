package com.archos.mediacenter.video.leanback.settings;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import com.archos.mediacenter.video.leanback.PreviewDialog;
import com.archos.mediacenter.video.leanback.StandardSwitchDrawable;

/**
 * The switch knob. SwitchCompat gives thumbs the track's full height; this keeps the visible knob circular. Its
 * colour follows the switch (player v10 mock-up): dark on the white "on" track, white on the dark "off" track.
 */
final class SettingsSwitchThumb extends Drawable
{
    private final int size;
    private final Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private boolean checked;

    SettingsSwitchThumb(Context context)
    {
        // Two knobs plus padding make the 44dp switch width.
        size = PreviewDialog.dp(context, 20);
        paint.setColor(Color.WHITE);
    }

    @Override
    public int getIntrinsicWidth()
    {
        return size;
    }

    @Override
    public int getIntrinsicHeight()
    {
        return size;
    }

    @Override
    public boolean isStateful()
    {
        return true;
    }

    @Override
    protected boolean onStateChange(int[] state)
    {
        boolean on = false;
        for (int value : state)
        {
            on |= value == android.R.attr.state_checked;
        }
        if (on == checked)
        {
            return false;
        }
        checked = on;
        invalidateSelf();
        return true;
    }

    @Override
    public void draw(Canvas canvas)
    {
        Rect b = getBounds();
        int alpha = paint.getAlpha();
        paint.setColor(checked ? StandardSwitchDrawable.KNOB_ON : Color.WHITE);
        paint.setAlpha(alpha);
        canvas.drawCircle(b.exactCenterX(), b.exactCenterY(), Math.min(b.width(), b.height()) / 2f, paint);
    }

    @Override
    public void setAlpha(int alpha)
    {
        paint.setAlpha(alpha);
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
