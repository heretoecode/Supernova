package com.archos.mediacenter.video.leanback.details;

/**
 * Calmer hero backdrops, first version (owner, 9–14 Nov 2026): how bright and busy the backdrop is behind the text
 * (the left part). A backdrop that is too bright there gets a gentle shade, so the text stays easy to read.
 */
public final class BackdropCalm
{
    /** Above this, the area behind the text counts as too bright. */
    static final float TOO_BRIGHT = 0.55f;
    /** The share of the width the text sits over. */
    static final float TEXT_SIDE = 0.45f;

    private BackdropCalm()
    {
    }

    /** Average brightness (0–1) of the left part of an ARGB picture, from a sample grid. */
    public static float leftBrightness(int[] pixels, int width, int height)
    {
        if (pixels == null || width <= 0 || height <= 0 || pixels.length < width * height)
        {
            return 0f;
        }
        int columns = Math.max(1, Math.round(width * TEXT_SIDE));
        int stepX = Math.max(1, columns / 24);
        int stepY = Math.max(1, height / 24);
        double sum = 0;
        int count = 0;
        for (int y = 0; y < height; y += stepY)
        {
            for (int x = 0; x < columns; x += stepX)
            {
                int colour = pixels[y * width + x];
                int red = (colour >> 16) & 0xFF;
                int green = (colour >> 8) & 0xFF;
                int blue = colour & 0xFF;
                sum += (0.2126 * red + 0.7152 * green + 0.0722 * blue) / 255.0;
                count++;
            }
        }
        return count == 0 ? 0f : (float) (sum / count);
    }

    public static boolean needsShade(float brightness)
    {
        return brightness > TOO_BRIGHT;
    }
}
