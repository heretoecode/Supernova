package com.archos.mediacenter.video.leanback;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.widget.FrameLayout;

/**
 * The progress badge (RULES §14c, owner 19 Nov 2026; replaces every progress bar on artwork): a white circle in the
 * artwork's top-right corner, 48 px at 1080p, inset about 8 px, with a soft shadow. Inside, black fills like a clock
 * from 12 o'clock clockwise, inside a 3 px white rim. Watched: the same circle with a black tick. Not started: no
 * badge. Search's small thumbnails use the same badge at 30 px (`search-v6/tv/08` A).
 */
public final class ProgressBadge extends Drawable
{
    /** Full size at 1080p (24 dp on the Shield's 960 × 540 dp screen). */
    public static final int SIZE_PX_1080 = 48;
    /** Search's thumbnails (owner, 25 Nov). */
    public static final int SMALL_PX_1080 = 30;
    public static final int INSET_PX_1080 = 8;
    static final int RIM_PX_1080 = 3;

    private final Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final float fraction;
    private final boolean watched;
    private final float rimRatio;
    private int alpha = 255;

    /**
     * @param fraction how far through (0..1); ignored when watched
     * @param watched  draws the tick instead of the pie
     */
    public ProgressBadge(float fraction, boolean watched)
    {
        this.fraction = Math.max(0f, Math.min(1f, fraction));
        this.watched = watched;
        this.rimRatio = (float) RIM_PX_1080 / SIZE_PX_1080;
    }

    /** True when there is anything to show: started or watched (RULES §14c: not started, no badge). */
    public static boolean shows(float fraction, boolean watched)
    {
        return watched || fraction > 0f;
    }

    /** How far through a film or episode, from its resume point and length; 0 when unknown or not started. */
    public static float fraction(long resumeMs, long durationMs)
    {
        if (resumeMs <= 0 || durationMs <= 0)
        {
            return 0f;
        }
        return Math.max(0.02f, Math.min(1f, (float) resumeMs / durationMs));
    }

    /** Pixels at 1080p to this screen's pixels (the Shield's 1080p is 960 dp wide). */
    public static int px(Context context, int px1080)
    {
        float density = context.getResources().getDisplayMetrics().density;
        return Math.max(1, Math.round(px1080 / 2f * density));
    }

    /**
     * Puts the badge in the top-right corner of a piece of artwork held in a FrameLayout; removes it when there is
     * nothing to show. One badge view per frame, found again by its tag.
     */
    public static void apply(FrameLayout frame, float fraction, boolean watched, int sizePx1080)
    {
        View badge = frame.findViewWithTag(TAG);
        if (!shows(fraction, watched))
        {
            if (badge != null)
            {
                badge.setVisibility(View.GONE);
            }
            return;
        }
        Context context = frame.getContext();
        if (badge == null)
        {
            badge = new View(context);
            badge.setTag(TAG);
            badge.setFocusable(false);
            int size = px(context, sizePx1080);
            int inset = px(context, sizePx1080 == SIZE_PX_1080 ? INSET_PX_1080 : 5);
            int corner = android.view.Gravity.TOP | android.view.Gravity.END;
            FrameLayout.LayoutParams where = new FrameLayout.LayoutParams(size, size, corner);
            where.topMargin = inset;
            where.rightMargin = inset;
            frame.addView(badge, where);
        }
        badge.setVisibility(View.VISIBLE);
        badge.setBackground(new ProgressBadge(fraction, watched));
        badge.setContentDescription(watched ? "Watched" : Math.round(fraction * 100) + "% watched");
        badge.setElevation(px(context, 4));
    }

    static final String TAG = "progress.badge";

    @Override
    public void draw(Canvas canvas)
    {
        Rect b = getBounds();
        float size = Math.min(b.width(), b.height());
        float cx = b.exactCenterX();
        float cy = b.exactCenterY();
        float radius = size / 2f;
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(0xFFFFFFFF);
        paint.setAlpha(alpha);
        canvas.drawCircle(cx, cy, radius, paint);
        paint.setColor(0xFF000000);
        paint.setAlpha(alpha);
        if (watched)
        {
            paint.setStyle(Paint.Style.STROKE);
            paint.setStrokeWidth(radius * .2f);
            paint.setStrokeCap(Paint.Cap.ROUND);
            paint.setStrokeJoin(Paint.Join.ROUND);
            Path tick = new Path();
            tick.moveTo(cx - radius * .42f, cy + radius * .02f);
            tick.lineTo(cx - radius * .1f, cy + radius * .34f);
            tick.lineTo(cx + radius * .45f, cy - radius * .3f);
            canvas.drawPath(tick, paint);
            return;
        }
        float inner = radius * (1f - 2f * rimRatio);
        RectF pie = new RectF(cx - inner, cy - inner, cx + inner, cy + inner);
        canvas.drawArc(pie, -90f, 360f * fraction, true, paint);
    }

    /** For tests: the sweep in degrees from 12 o'clock. */
    float sweep()
    {
        return watched ? 0f : 360f * fraction;
    }

    boolean watched()
    {
        return watched;
    }

    @Override
    public void setAlpha(int alpha)
    {
        this.alpha = alpha;
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
