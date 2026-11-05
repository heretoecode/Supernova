package com.archos.mediacenter.video.player;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;
import java.util.Collections;
import java.util.List;

/**
 * Read-only part lines on the one continuous seek bar (0.156 #21): a thin line crosses the bar at the start of
 * every part. When a chapter card is focused, its stretch between two lines lights up. Transport and D-pad focus
 * remain owned by the real slider.
 */
public final class ChapterMarkers extends View implements PlayerPartsStrip.Highlight
{
    static final int LINE = 0xffecf4fa;
    static final int LIT = 0xB3FFFFFF;

    private final Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private List<PlaybackChapters.Chapter> chapters = Collections.emptyList();
    private long duration;
    private long litStart = -1;
    private long litEnd = -1;

    public ChapterMarkers(Context context, AttributeSet attributes)
    {
        super(context, attributes);
        setFocusable(false);
        setClickable(false);
        setImportantForAccessibility(IMPORTANT_FOR_ACCESSIBILITY_NO);
        paint.setColor(LINE);
    }

    public void bind(List<PlaybackChapters.Chapter> chapters, long duration, int left, int right)
    {
        this.chapters = chapters == null ? Collections.emptyList() : chapters;
        this.duration = duration;
        setPadding(left, 0, right, 0);
        setVisibility(duration > 0 && (!this.chapters.isEmpty() || litStart >= 0) ? VISIBLE : GONE);
        invalidate();
    }

    @Override
    public void highlight(long startMs, long endMs)
    {
        litStart = startMs;
        litEnd = endMs;
        if (litStart >= 0 && duration > 0)
            setVisibility(VISIBLE);
        invalidate();
    }

    long litStart()
    {
        return litStart;
    }

    long litEnd()
    {
        return litEnd;
    }

    @Override
    protected void onDraw(Canvas canvas)
    {
        super.onDraw(canvas);
        if (duration <= 0)
            return;
        float density = getResources().getDisplayMetrics().density;
        float width = getWidth() - getPaddingLeft() - getPaddingRight();
        float middle = getHeight() / 2f;
        float half = 3 * density;
        if (litStart >= 0 && litEnd > litStart) {
            float from = getPaddingLeft() + width * Math.min(litStart, duration) / duration;
            float to = getPaddingLeft() + width * Math.min(litEnd, duration) / duration;
            float track = 2.5f * density;
            paint.setColor(LIT);
            canvas.drawRoundRect(from, middle - track, to, middle + track, track, track, paint);
        }
        paint.setColor(LINE);
        paint.setStrokeWidth(Math.max(1, density));
        for (PlaybackChapters.Chapter chapter : chapters)
            if (chapter.startMs > 0 && chapter.startMs < duration) {
                float x = getPaddingLeft() + width * chapter.startMs / duration;
                canvas.drawLine(x, middle - half, x, middle + half, paint);
            }
    }
}
