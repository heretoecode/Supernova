package com.archos.mediacenter.video.leanback.details;

import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;

import java.util.Locale;

/** A cast or crew card without a photo shows the person's initials (owner, 14 Nov 2026; RULES §14). */
public final class PersonInitials extends Drawable
{
    private final String initials;
    private final Paint fill = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint ink = new Paint(Paint.ANTI_ALIAS_FLAG);

    public PersonInitials(String name)
    {
        initials = initials(name);
        fill.setColor(0xFF2A2D33);
        ink.setColor(0xFFDCE3EE);
        ink.setTextAlign(Paint.Align.CENTER);
        ink.setTypeface(Typeface.create("sans-serif-medium", Typeface.NORMAL));
    }

    /** "Ada Lovelace" → "AL"; one name → its first letter; nothing → "". */
    public static String initials(String name)
    {
        if (name == null)
        {
            return "";
        }
        String[] words = name.trim().split("\\s+");
        if (words.length == 0 || words[0].isEmpty())
        {
            return "";
        }
        String first = words[0].substring(0, 1);
        String last = words.length > 1 ? words[words.length - 1].substring(0, 1) : "";
        return (first + last).toUpperCase(Locale.ROOT);
    }

    @Override
    public void draw(Canvas canvas)
    {
        Rect bounds = getBounds();
        canvas.drawRect(bounds, fill);
        ink.setTextSize(Math.min(bounds.width(), bounds.height()) * 0.34f);
        float y = bounds.exactCenterY() - (ink.descent() + ink.ascent()) / 2;
        canvas.drawText(initials, bounds.exactCenterX(), y, ink);
    }

    @Override
    public void setAlpha(int alpha)
    {
        fill.setAlpha(alpha);
        ink.setAlpha(alpha);
        invalidateSelf();
    }

    @Override
    public void setColorFilter(ColorFilter filter)
    {
        ink.setColorFilter(filter);
        invalidateSelf();
    }

    @Override
    public int getOpacity()
    {
        return PixelFormat.TRANSLUCENT;
    }
}
