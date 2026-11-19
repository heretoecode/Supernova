package com.archos.mediacenter.video.leanback;

import android.content.Context;
import android.graphics.*;
import android.graphics.drawable.Drawable;

/** The owner's Gemini switch face; focus is drawn separately by the containing row. */
public final class StandardSwitchDrawable extends Drawable {
    /** No blue (RULES): "on" is a white track with a dark knob; "off" a dark track with a white knob (player v10). */
    public static final int ON_TRACK = 0xfff4f6fb, KNOB_ON = 0xff16181c, OFF = 0xff3a3e45;
    /**
     * Switch proportions shared by every toggle in the app. Owner decision 2 Nov 2026: the former
     * 70x26dp (2.7:1) face was too wide; 44x26dp (~1.7:1) matches familiar Android/iOS switches.
     */
    public static final int WIDTH_DP = 44, HEIGHT_DP = 26;

    /** Width for a switch drawn at {@code heightPx}, keeping the shared proportions. */
    public static int widthForHeight(int heightPx) {
        return Math.round(heightPx * (float) WIDTH_DP / HEIGHT_DP);
    }

    private final Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final int width, height, padding;
    private final boolean thumb;
    private final Boolean fixed;
    private boolean checked, enabled;
    private boolean onFocusedRow;
    private int alpha = 255;
    /** On a focused row (RULES §3): on = dark track and white knob; off = light grey track and dark knob. */
    public static final int FOCUSED_ON_TRACK = 0xff0b0d12, FOCUSED_OFF_TRACK = 0xffb4b9c2;

    public StandardSwitchDrawable(Context c) {
        this(c, true, null);
    }

    public StandardSwitchDrawable(Context c, boolean drawThumb) {
        this(c, drawThumb, null);
    }

    private StandardSwitchDrawable(Context c, boolean drawThumb, Boolean state) {
        thumb = drawThumb;
        fixed = state;
        checked = Boolean.TRUE.equals(state);
        enabled = state != null;
        width = PreviewDialog.dp(c, WIDTH_DP);
        height = PreviewDialog.dp(c, HEIGHT_DP);
        padding = PreviewDialog.dp(c, 2);
    }

    public static StandardSwitchDrawable snapshot(Context c, boolean checked) {
        return new StandardSwitchDrawable(c, true, checked);
    }

    /** A fixed switch face for a row that is focused (inverted like the row's text) or at rest. */
    public static StandardSwitchDrawable snapshot(Context c, boolean checked, boolean focusedRow) {
        StandardSwitchDrawable face = new StandardSwitchDrawable(c, true, checked);
        face.onFocusedRow = focusedRow;
        return face;
    }

    public boolean onFocusedRow() {
        return onFocusedRow;
    }

    public boolean checked() {
        return checked;
    }

    @Override
    public int getIntrinsicWidth() {
        return width;
    }

    @Override
    public int getIntrinsicHeight() {
        return height;
    }

    @Override
    public boolean getPadding(Rect out) {
        out.set(padding, 0, padding, 0);
        return true;
    }

    @Override
    public boolean isStateful() {
        return fixed == null;
    }

    @Override
    protected boolean onStateChange(int[] states) {
        if (fixed != null)
            return false;
        boolean on = false, active = false;
        for (int state : states) {
            on |= state == android.R.attr.state_checked;
            active |= state == android.R.attr.state_enabled;
        }
        boolean changed = checked != on || enabled != active;
        checked = on;
        enabled = active;
        if (changed)
            invalidateSelf();
        return changed;
    }

    @Override
    public void draw(Canvas c) {
        Rect b = getBounds();
        float h = Math.min(b.height(), b.width() * (float) HEIGHT_DP / WIDTH_DP), radius = h / 2;
        float w = h * WIDTH_DP / HEIGHT_DP, left = b.exactCenterX() - w / 2;
        RectF track = new RectF(left, b.exactCenterY() - radius, left + w, b.exactCenterY() + radius);
        paint.setStyle(Paint.Style.FILL);
        paint.setShader(null);
        if (onFocusedRow)
            paint.setColor(checked ? FOCUSED_ON_TRACK : FOCUSED_OFF_TRACK);
        else
            paint.setColor(checked ? ON_TRACK : OFF);
        paint.setAlpha(Math.round(alpha * (enabled ? 1f : .4f)));
        c.drawRoundRect(track, radius, radius, paint);
        if (onFocusedRow) {
            paint.setColor(checked ? Color.WHITE : FOCUSED_ON_TRACK);
            paint.setAlpha(Math.round(alpha * (enabled ? 1f : .4f)));
            c.drawCircle(checked ? track.right - radius : track.left + radius, track.centerY(), h * 10f / 26f, paint);
            return;
        }
        paint.setShader(null);
        paint.setColor(0xff353d44);
        paint.setAlpha(Math.round(alpha * (enabled ? 1f : .4f)));
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(Math.max(1, h / 26));
        c.drawRoundRect(track, radius, radius, paint);
        if (thumb) {
            paint.setStyle(Paint.Style.FILL);
            paint.setColor(checked ? KNOB_ON : Color.WHITE);
            paint.setAlpha(Math.round(alpha * (enabled ? 1f : .4f)));
            c.drawCircle(checked ? track.right - radius : track.left + radius, track.centerY(), h * 10f / 26f, paint);
        }
    }

    @Override
    public void setAlpha(int value) {
        alpha = value;
        invalidateSelf();
    }

    @Override
    public void setColorFilter(ColorFilter ignored) {
    }

    @Override
    public int getOpacity() {
        return PixelFormat.TRANSLUCENT;
    }
}
