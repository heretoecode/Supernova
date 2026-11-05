package com.archos.mediacenter.video.player;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Typeface;

/** Engine bitmap only; the screen owns placement and supplies its localized sample text. */
final class SubtitleAppearanceSample
{
    static Bitmap render(SubtitleManager settings, String text, int width, int height)
    {
        if (width <= 0 || height <= 0 || text == null)
        {
            return null;
        }
        Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setTypeface(
            Typeface.create(settings.getFontFamily(), settings.getBold() ? Typeface.BOLD : Typeface.NORMAL));
        paint.setTextSize(
            settings.getFontSizePt() *
            (settings.getScreenShortSide() > 0 ? settings.getScreenShortSide() : Math.min(width, height)) / 720f *
            Math.max(0.01f, settings.getFontScale()));
        float x = Math.max(0, (width - paint.measureText(text)) / 2);
        float baseline = height - settings.getVerticalOffsetPixels() - paint.descent();
        float padding = settings.getPadding();
        if (settings.getBackgroundState())
        {
            paint.setColor((settings.getBackgroundColor() & 0x00ffffff) |
                           (Math.max(0, Math.min(255, settings.getBackgroundOpacity())) << 24));
            canvas.drawRect(x - padding, baseline + paint.ascent() - padding, x + paint.measureText(text) + padding,
                            baseline + paint.descent() + padding, paint);
        }
        if (settings.getShadowWidth() > 0)
        {
            paint.setShadowLayer(settings.getShadowWidth(), settings.getShadowWidth(), settings.getShadowWidth(),
                                 settings.getShadowColor());
        }
        if (settings.getOutlineWidth() > 0)
        {
            paint.setStyle(Paint.Style.STROKE);
            paint.setStrokeWidth(settings.getOutlineWidth() * 2);
            paint.setColor(settings.getOutlineColor());
            canvas.drawText(text, x, baseline, paint);
        }
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(settings.getColor());
        canvas.drawText(text, x, baseline, paint);
        return bitmap;
    }

    private SubtitleAppearanceSample()
    {
    }
}
