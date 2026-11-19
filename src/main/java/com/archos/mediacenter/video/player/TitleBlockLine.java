package com.archos.mediacenter.video.player;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.ReplacementSpan;

/**
 * The details line under the title, one design for Preparing playback and the player (owner, 18 Nov 2026; PP2, PP3,
 * PL1): the age badge always comes first, inline, then the rest ("[18] · Season 2 · Episode 3 · Title"). No reasons.
 */
public final class TitleBlockLine
{
    static final String SEPARATOR = " · ";

    private TitleBlockLine()
    {
    }

    /** The line, with the badge first when the age rating is known. */
    public static CharSequence line(Context context, String age, String details)
    {
        String rest = details == null ? "" : details.trim();
        if (age == null || age.trim().isEmpty() || !PlaybackAgeOverlay.valid(age))
            return rest;
        SpannableStringBuilder out = new SpannableStringBuilder();
        String badge = age.trim();
        out.append(badge);
        out.setSpan(new Badge(context.getResources().getDisplayMetrics().density), 0, badge.length(),
                    Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        if (!rest.isEmpty())
            out.append(SEPARATOR).append(rest);
        return out;
    }

    /** The age rating in a thin outlined box, sized to the line's own text (outline = information, RULES §3). */
    static final class Badge extends ReplacementSpan
    {
        private final float density;

        Badge(float density)
        {
            this.density = density;
        }

        @Override
        public int getSize(Paint paint, CharSequence text, int start, int end, Paint.FontMetricsInt fm)
        {
            if (fm != null)
            {
                Paint.FontMetricsInt own = paint.getFontMetricsInt();
                fm.ascent = own.ascent;
                fm.descent = own.descent;
                fm.top = own.top;
                fm.bottom = own.bottom;
            }
            return Math.round(paint.measureText(text, start, end) + 14 * density);
        }

        @Override
        public void draw(Canvas canvas, CharSequence text, int start, int end, float x, int top, int y, int bottom,
                         Paint paint)
        {
            float width = paint.measureText(text, start, end) + 14 * density;
            Paint.FontMetrics metrics = paint.getFontMetrics();
            RectF box = new RectF(x + density, y + metrics.ascent - density, x + width - density,
                                  y + metrics.descent + density);
            int colour = paint.getColor();
            Paint.Style style = paint.getStyle();
            float stroke = paint.getStrokeWidth();
            paint.setStyle(Paint.Style.STROKE);
            paint.setStrokeWidth(Math.max(1, density));
            paint.setColor(0xDDFFFFFF);
            canvas.drawRoundRect(box, 4 * density, 4 * density, paint);
            paint.setStyle(style);
            paint.setStrokeWidth(stroke);
            paint.setColor(colour);
            canvas.drawText(text, start, end, x + 7 * density, y, paint);
        }
    }
}
