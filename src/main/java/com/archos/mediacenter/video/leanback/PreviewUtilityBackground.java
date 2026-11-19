package com.archos.mediacenter.video.leanback;

import android.content.Context;
import android.graphics.*;
import android.graphics.drawable.Drawable;
import android.os.Build;
import com.archos.mediacenter.video.BuildConfig;
import com.archos.mediacenter.video.R;

/** Exact supplied 4.1.2 asset, aspect-preserving viewport cover; decoded once. */
public final class PreviewUtilityBackground extends Drawable {
    /** The one page colour (RULES.md). */
    public static final int CHARCOAL = 0xFF16181C;
    /** The speckled background's base (RULES §1, 0.158 part B). */
    public static final int SPECKLE_BASE = 0xFF111215;
    private static Bitmap image;
    private static Bitmap speckle;
    private final Paint speckPaint = new Paint();
    private final Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG);

    public PreviewUtilityBackground(Context context) {
        if (BuildConfig.FOUNDATION)
            return;
        synchronized (PreviewUtilityBackground.class) {
            if (image == null)
                image = BitmapFactory.decodeResource(
                    context.getResources(), R.drawable.preview_utility_background);
        }
    }

    @Override
    public void draw(Canvas canvas) {
        Bitmap image = PreviewUtilityBackground.image;
        Rect b = getBounds();
        int flat = previewColour("SUPERNOVA_PREVIEW_BG", 0);
        if (flat != 0) {
            canvas.drawColor(flat);
            return;
        }
        if (BuildConfig.FOUNDATION) {
            // Every page sits on the speckled background (RULES §1): fixed grains, drawn once, never smoothed.
            drawSpeckle(canvas, b);
            return;
        }
        canvas.drawColor(0xff09111a);
        if (image == null)
            return;
        float scale = Math.max(b.width() / (float) image.getWidth(), b.height() / (float) image.getHeight());
        float w = image.getWidth() * scale, h = image.getHeight() * scale;
        canvas.drawBitmap(image, null,
            new RectF(
                b.exactCenterX() - w / 2, b.exactCenterY() - h / 2, b.exactCenterX() + w / 2, b.exactCenterY() + h / 2),
            paint);
        canvas.drawColor(0x44050b12);
    }

    private final int[] where = new int[2];

    /**
     * One pattern for the whole window, so a page drawn inside another lines up grain for grain: the bitmap is the
     * window's size and is placed at the window's corner.
     */
    private void drawSpeckle(Canvas canvas, Rect b) {
        canvas.drawColor(SPECKLE_BASE);
        if (b.width() <= 0 || b.height() <= 0)
            return;
        int width = b.width();
        int height = b.height();
        int left = b.left;
        int top = b.top;
        Callback owner = getCallback();
        if (owner instanceof android.view.View) {
            android.view.View view = (android.view.View) owner;
            android.view.View root = view.getRootView();
            if (root != null && root.getWidth() > 0 && root.getHeight() > 0) {
                view.getLocationInWindow(where);
                width = Math.max(root.getWidth(), where[0] + b.right);
                height = Math.max(root.getHeight(), where[1] + b.bottom);
                left = -where[0];
                top = -where[1];
            }
        }
        Bitmap grains = speckleFor(width, height);
        if (grains != null)
            canvas.drawBitmap(grains, left, top, speckPaint);
    }

    /** The one speckle pattern at this size, made once (the size of the screen, so it is never scaled). */
    static synchronized Bitmap speckleFor(int width, int height) {
        if (speckle != null && speckle.getWidth() == width && speckle.getHeight() == height)
            return speckle;
        try {
            int[] pixels = speckle(width, height);
            speckle = Bitmap.createBitmap(pixels, width, height, Bitmap.Config.ARGB_8888);
        } catch (OutOfMemoryError e) {
            speckle = null;
        }
        return speckle;
    }

    /**
     * The approved recipe (docs/design/proposals/background-speckle/tv3.html, density 20): base #111215, about 20
     * grains in every 1,000 pixels at 7–22% white, about 1 in 7 of them at 45%, from a fixed seed.
     */
    static int[] speckle(int width, int height) {
        final double density = .02;
        final double low = .07;
        final double high = .22;
        final double rare = density * .135;
        final double rareAlpha = .45;
        int baseR = 0x11;
        int baseG = 0x12;
        int baseB = 0x15;
        int[] pixels = new int[width * height];
        long seed = 7;
        for (int i = 0; i < pixels.length; i++) {
            seed = seed * 16807 % 2147483647;
            double r = seed / 2147483647.0;
            double a = 0;
            if (r < rare) {
                seed = seed * 16807 % 2147483647;
                a = rareAlpha * (.7 + .3 * (seed / 2147483647.0));
            } else if (r < density) {
                seed = seed * 16807 % 2147483647;
                double first = seed / 2147483647.0;
                seed = seed * 16807 % 2147483647;
                double second = seed / 2147483647.0;
                a = low + (high - low) * first * second;
            }
            int red = baseR;
            int green = baseG;
            int blue = baseB;
            if (a > 0) {
                red = (int) (baseR + (232 - baseR) * a);
                green = (int) (baseG + (232 - baseG) * a);
                blue = (int) (baseB + (232 - baseB) * a);
            }
            pixels[i] = 0xFF000000 | red << 16 | green << 8 | blue;
        }
        return pixels;
    }

    /**
     * Render tool only: a colour (hex RRGGBB or AARRGGBB) the comparison renders use in place of the normal one.
     * Never read on a device.
     */
    public static int previewColour(String name, int normal) {
        if (!"robolectric".equals(Build.FINGERPRINT))
            return normal;
        String value = System.getenv(name);
        if (value == null || value.isEmpty())
            return normal;
        long parsed = Long.parseLong(value, 16);
        if (value.length() <= 6)
            parsed |= 0xff000000L;
        return (int) parsed;
    }

    /** Render tool only: a whole number in place of the normal one. Never read on a device. */
    public static int previewNumber(String name, int normal) {
        if (!"robolectric".equals(Build.FINGERPRINT))
            return normal;
        String value = System.getenv(name);
        if (value == null || value.isEmpty())
            return normal;
        return Integer.parseInt(value);
    }

    @Override
    public void setAlpha(int alpha) {
        paint.setAlpha(alpha);
    }

    @Override
    public void setColorFilter(ColorFilter filter) {
        paint.setColorFilter(filter);
    }

    @Override
    public int getOpacity() {
        return PixelFormat.OPAQUE;
    }
}
