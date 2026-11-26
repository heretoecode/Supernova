package com.archos.mediacenter.video.leanback;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.LinearInterpolator;
import androidx.core.graphics.ColorUtils;

/**
 * The carousel dots under Home's hero (HOME.md; BRIEF_0158_B › Home review): soft white dots, the current one a pill
 * (about 56 px at 1080p) that fills with white over the time to the next hero; the fill pauses while focus is on the
 * hero and starts again from empty after any press. Retargets from the visible pill widths, never flashing new dots.
 */
final class PreviewFeaturedIndicators extends View
{
    static final float PILL_DP = 28f;
    static final float DOT_DP = 5f;

    private final Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private float[] weights = new float[0];
    private int current = -1;
    private ValueAnimator morph;
    private ValueAnimator filling;
    private float fill;

    PreviewFeaturedIndicators(Context context)
    {
        super(context);
        setFocusable(false);
        setTag("hero:indicators");
        setContentDescription("Featured carousel, Left or Right on More to change title");
    }

    void setPosition(int count, int selected)
    {
        if (count < 0)
        {
            throw new IllegalArgumentException("Invalid Featured count");
        }
        if (morph != null)
        {
            morph.cancel();
        }
        resetFill();
        if (count == 0)
        {
            weights = new float[0];
            current = -1;
            invalidate();
            return;
        }
        int actual = count;
        int position = Math.floorMod(selected, actual);
        int shown = Math.min(8, actual);
        int target = actual <= 8 ? position : Math.min(7, Math.round(position * 7f / (actual - 1)));
        current = target;
        setContentDescription("Featured title " + (position + 1) + " of " + actual
                              + ", Left or Right on More to change title");
        if (weights.length != shown)
        {
            weights = new float[shown];
            weights[target] = 1;
            invalidate();
            return;
        }
        float[] origin = weights.clone();
        morph = ValueAnimator.ofFloat(0, 1);
        morph.setDuration(200);
        morph.setInterpolator(new DecelerateInterpolator());
        morph.addUpdateListener(value -> {
            float progress = (float) value.getAnimatedValue();
            for (int i = 0; i < weights.length; i++)
            {
                weights[i] = origin[i] + ((i == target ? 1 : 0) - origin[i]) * progress;
            }
            invalidate();
        });
        morph.start();
    }

    /** Fills the current pill from where it is to full over {@code remainingMs} (the time to the next hero). */
    void fillOver(long remainingMs)
    {
        if (filling != null)
        {
            filling.cancel();
        }
        filling = ValueAnimator.ofFloat(fill, 1f);
        filling.setDuration(Math.max(1, remainingMs));
        filling.setInterpolator(new LinearInterpolator());
        filling.addUpdateListener(value -> {
            fill = (float) value.getAnimatedValue();
            invalidate();
        });
        filling.start();
    }

    /** Focus on the hero: the fill stops where it is. */
    void pauseFill()
    {
        if (filling != null)
        {
            filling.cancel();
            filling = null;
        }
    }

    /** A press, or a new hero: the pill starts empty again. */
    void resetFill()
    {
        pauseFill();
        fill = 0f;
        invalidate();
    }

    float fill()
    {
        return fill;
    }

    @Override
    protected void onDraw(Canvas canvas)
    {
        super.onDraw(canvas);
        if (weights.length == 0)
        {
            return;
        }
        float density = getResources().getDisplayMetrics().density;
        float gap = 6 * density;
        float total = (weights.length - 1) * (DOT_DP * density + gap) + PILL_DP * density;
        float left = (getWidth() - total) / 2;
        float height = DOT_DP * density;
        float top = (getHeight() - height) / 2;
        float radius = height / 2;
        for (int i = 0; i < weights.length; i++)
        {
            float weight = weights[i];
            float width = (DOT_DP + (PILL_DP - DOT_DP) * weight) * density;
            // Soft white for the rest and for the current pill's empty part; white as it fills (no blue, RULES §6).
            paint.setColor(i == current ? 0x66FFFFFF : ColorUtils.blendARGB(0x66FFFFFF, 0xFFFFFFFF, weight));
            canvas.drawRoundRect(left, top, left + width, top + height, radius, radius, paint);
            if (i == current && fill > 0f)
            {
                paint.setColor(0xFFFFFFFF);
                float filled = Math.max(height, width * fill);
                canvas.drawRoundRect(left, top, left + filled, top + height, radius, radius, paint);
            }
            left += width + gap;
        }
    }

    @Override
    protected void onDetachedFromWindow()
    {
        if (morph != null)
        {
            morph.cancel();
        }
        pauseFill();
        super.onDetachedFromWindow();
    }
}
