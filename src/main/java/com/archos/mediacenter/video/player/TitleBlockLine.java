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

    /**
     * The title block's facts line without the badge (PLAYER.md): year · length · up to four genres
     * ("2019 · 2 h 51 min · Drama · Crime · Thriller · Mystery"). Empty parts are left out.
     */
    public static String facts(int year, long durationMs, java.util.Collection<String> genres)
    {
        java.util.List<String> parts = new java.util.ArrayList<>();
        if (year > 0)
            parts.add(String.valueOf(year));
        long minutes = durationMs / 60000;
        if (minutes > 0)
            parts.add(minutes >= 60 ? minutes / 60 + " h " + minutes % 60 + " min" : minutes + " min");
        int shown = 0;
        for (String genre : genres == null ? java.util.Collections.<String>emptyList() : genres)
        {
            if (genre == null || genre.trim().isEmpty() || shown == 4)
                continue;
            parts.add(genre.trim());
            shown++;
        }
        return android.text.TextUtils.join(SEPARATOR, parts);
    }

    /**
     * The facts for a video from the library in memory: a film's year, length and genres; an episode's length with
     * its show's year and genres. "" while the library has not been read.
     */
    public static String factsFor(long videoId, long durationMs)
    {
        com.archos.mediacenter.video.leanback.PreviewLibraryLoader.Snapshot library =
            com.archos.mediacenter.video.leanback.PreviewLibraryLoader.memoryCache();
        if (library == null || videoId <= 0)
            return "";
        for (java.util.List<com.archos.mediacenter.video.leanback.PreviewLibraryLoader.Entry> list
             : java.util.Arrays.asList(library.movies, library.episodes))
        {
            for (com.archos.mediacenter.video.leanback.PreviewLibraryLoader.Entry entry : list)
            {
                if (!(entry.media instanceof com.archos.mediacenter.video.browser.adapters.object.Video)
                    || ((com.archos.mediacenter.video.browser.adapters.object.Video) entry.media).getId() != videoId)
                    continue;
                long length = durationMs > 0 ? durationMs : entry.runtime;
                return facts(year(entry, library), length,
                             com.archos.mediacenter.video.leanback.PreviewGenres.parse(entry.genres));
            }
        }
        return "";
    }

    private static int year(com.archos.mediacenter.video.leanback.PreviewLibraryLoader.Entry entry,
                            com.archos.mediacenter.video.leanback.PreviewLibraryLoader.Snapshot library)
    {
        if (entry.media instanceof com.archos.mediacenter.video.browser.adapters.object.Movie)
            return ((com.archos.mediacenter.video.browser.adapters.object.Movie) entry.media).getYear();
        for (com.archos.mediacenter.video.leanback.PreviewLibraryLoader.Entry show : library.shows)
        {
            if (show.show == entry.show && show.releaseDate.length() >= 4)
                return parseYear(show.releaseDate.substring(0, 4));
        }
        return entry.releaseDate.length() >= 4 ? parseYear(entry.releaseDate.substring(0, 4)) : 0;
    }

    private static int parseYear(String text)
    {
        try
        {
            return Integer.parseInt(text);
        }
        catch (NumberFormatException notAYear)
        {
            return 0;
        }
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
