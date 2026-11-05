package com.archos.mediacenter.video.leanback;

import android.graphics.*;
import android.graphics.drawable.Drawable;
import java.util.Locale;

/**
 * Last-resort provider icon (approved 2 Nov 2026): the provider's initials in a rounded tile outline,
 * for providers without an official single-colour mark. Drawn in one colour so it follows the row's
 * content colour (white at rest, black on a focused white box) like every other icon.
 */
public final class ProviderMonogram extends Drawable {
    private final String letters;
    private final Paint stroke = new Paint(Paint.ANTI_ALIAS_FLAG), text = new Paint(Paint.ANTI_ALIAS_FLAG);

    public ProviderMonogram(String providerName) {
        letters = initials(providerName);
        stroke.setStyle(Paint.Style.STROKE);
        stroke.setColor(Color.WHITE);
        text.setColor(Color.WHITE);
        text.setTypeface(Typeface.create("sans-serif-medium", Typeface.BOLD));
        text.setTextAlign(Paint.Align.CENTER);
    }

    /** "JustWatch TV Free" → "JW", "Sky Go" → "SG", "Hayu" → "Ha", "NOW" → "NO". */
    public static String initials(String name) {
        String n = name == null ? "" : name.trim();
        if (n.isEmpty())
            return "?";
        String[] words = n.split("\\s+");
        String first = words[0];
        StringBuilder caps = new StringBuilder();
        for (char ch : first.toCharArray())
            if (Character.isUpperCase(ch))
                caps.append(ch);
        if (caps.length() >= 2 && caps.length() < first.length())
            return caps.substring(0, 2);
        if (words.length >= 2 && Character.isLetterOrDigit(words[1].charAt(0)) && !words[1].equalsIgnoreCase("tv") &&
            !words[1].equals("+"))
            return (first.substring(0, 1) + words[1].substring(0, 1)).toUpperCase(Locale.ROOT);
        if (first.length() == 1)
            return first.toUpperCase(Locale.ROOT);
        return first.substring(0, 1).toUpperCase(Locale.ROOT) + first.substring(1, 2);
    }

    @Override
    public void draw(Canvas canvas) {
        Rect b = getBounds();
        float h = b.height(), w = Math.min(b.width(), h * 1.25f);
        float cx = b.exactCenterX(), cy = b.exactCenterY();
        float sw = Math.max(1f, h * .06f);
        stroke.setStrokeWidth(sw);
        RectF tile = new RectF(cx - w / 2 + sw, cy - h / 2 + sw, cx + w / 2 - sw, cy + h / 2 - sw);
        canvas.drawRoundRect(tile, h * .2f, h * .2f, stroke);
        text.setTextSize(h * .42f);
        Paint.FontMetrics m = text.getFontMetrics();
        canvas.drawText(letters, cx, cy - (m.ascent + m.descent) / 2, text);
    }

    @Override
    public void setAlpha(int a) {
        stroke.setAlpha(a);
        text.setAlpha(a);
        invalidateSelf();
    }

    @Override
    public void setColorFilter(ColorFilter f) {
        stroke.setColorFilter(f);
        text.setColorFilter(f);
        invalidateSelf();
    }

    @Override
    public int getOpacity() {
        return PixelFormat.TRANSLUCENT;
    }

    @Override
    public int getIntrinsicWidth() {
        return -1;
    }

    @Override
    public int getIntrinsicHeight() {
        return -1;
    }
}
