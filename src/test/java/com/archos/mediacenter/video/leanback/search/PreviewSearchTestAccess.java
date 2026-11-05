package com.archos.mediacenter.video.leanback.search;

import android.content.Context;
import java.util.List;

/** Opens PreviewSearch's package-level helpers to tests in other packages. */
public final class PreviewSearchTestAccess
{
    public static String details(String kind, int minutes, String quality, String genres)
    {
        return PreviewSearch.details(kind, minutes, quality, genres);
    }

    public static CharSequence highlighted(String title, String typed)
    {
        return PreviewSearch.highlighted(title, typed);
    }

    public static void remember(Context context, String value)
    {
        PreviewSearch.remember(context, value);
    }

    public static List<String> recent(Context context)
    {
        return PreviewSearch.recent(context);
    }

    private PreviewSearchTestAccess()
    {
    }
}
