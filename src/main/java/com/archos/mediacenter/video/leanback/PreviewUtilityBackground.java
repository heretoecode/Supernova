package com.archos.mediacenter.video.leanback;

import android.content.Context;
import android.graphics.*;
import android.graphics.drawable.Drawable;
import android.os.Build;
import com.archos.mediacenter.video.BuildConfig;
import com.archos.mediacenter.video.R;

/** Exact supplied 4.1.2 asset, aspect-preserving viewport cover; decoded once. */
public final class PreviewUtilityBackground extends Drawable {
    private static Bitmap image, foundationImage;
    private final Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG);

    public PreviewUtilityBackground(Context context) {
        synchronized (PreviewUtilityBackground.class) {
            if (BuildConfig.FOUNDATION) {
                if (foundationImage == null) {
                    BitmapFactory.Options options = new BitmapFactory.Options();
                    options.inSampleSize = 2;
                    foundationImage = BitmapFactory.decodeResource(context.getResources(),
                        R.drawable.foundation_space_black, options);
                }
            } else if (image == null)
                image = BitmapFactory.decodeResource(
                    context.getResources(), R.drawable.preview_utility_background);
        }
    }

    @Override
    public void draw(Canvas canvas) {
        Bitmap image =
            BuildConfig.FOUNDATION ? foundationImage : PreviewUtilityBackground.image;
        Rect b = getBounds();
        int flat = previewColour("SUPERNOVA_PREVIEW_BG", 0);
        if (flat != 0) {
            canvas.drawColor(flat);
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
        if (!BuildConfig.FOUNDATION)
            canvas.drawColor(0x44050b12);
    }

    /**
     * Render tool only: a colour (hex RRGGBB) the comparison renders use in place of the normal one.
     * Never read on a device.
     */
    public static int previewColour(String name, int normal) {
        if (!"robolectric".equals(Build.FINGERPRINT))
            return normal;
        String value = System.getenv(name);
        if (value == null || value.isEmpty())
            return normal;
        return 0xff000000 | Integer.parseInt(value, 16);
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
