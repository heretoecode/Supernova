package com.archos.mediacenter.video.leanback.search;

import android.content.Context;
import com.archos.mediacenter.video.browser.adapters.object.Tvshow;
import com.archos.mediacenter.video.browser.adapters.object.Video;
import java.util.ArrayList;
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

    /** Puts a made-up landing in place: {name, titles, artwork} per genre and per person. */
    public static void landing(PreviewSearch search, String[][] genres, String[][] people)
    {
        List<SearchLanding.Card> g = new ArrayList<>();
        for (String[] genre : genres)
        {
            g.add(new SearchLanding.Card(genre[0], Integer.parseInt(genre[1]), genre[2]));
        }
        List<SearchLanding.Card> p = new ArrayList<>();
        for (String[] person : people)
        {
            p.add(new SearchLanding.Card(person[0], Integer.parseInt(person[1]), person[2]));
        }
        org.robolectric.util.ReflectionHelpers.setField(search, "landing", new SearchLanding(g, p));
        search.bindLanding();
    }

    /** Shows results as a finished search would: films, episodes and shows; a reason makes it a person match. */
    public static void results(PreviewSearch search, String typed, List<Object> media, List<String> reasons)
        throws Exception
    {
        Class<?> type = Class.forName(PreviewSearch.class.getName() + "$Result");
        java.lang.reflect.Constructor<?> forVideo = type.getDeclaredConstructor(Video.class, String.class);
        java.lang.reflect.Constructor<?> forShow = type.getDeclaredConstructor(Tvshow.class, String.class);
        forVideo.setAccessible(true);
        forShow.setAccessible(true);
        List found = new ArrayList<>();
        for (int i = 0; i < media.size(); i++)
        {
            Object item = media.get(i);
            Object result = item instanceof Tvshow ? forShow.newInstance(item, "") : forVideo.newInstance(item, "");
            String reason = reasons == null || i >= reasons.size() ? "" : reasons.get(i);
            org.robolectric.util.ReflectionHelpers.setField(result, "reason", reason);
            org.robolectric.util.ReflectionHelpers.setField(result, "people", !reason.isEmpty());
            found.add(result);
        }
        search.present(found, typed, null, search.generation());
    }

    /** Adds the streaming group's "Looking on your services" row under its heading. */
    public static void loading(PreviewSearch search, String heading) throws Exception
    {
        Class<?> type = Class.forName(PreviewSearch.class.getName() + "$Result");
        java.lang.reflect.Constructor<?> forStream =
            type.getDeclaredConstructor(SearchStreaming.Title.class, String.class);
        forStream.setAccessible(true);
        Object waiting = forStream.newInstance(null, heading);
        org.robolectric.util.ReflectionHelpers.setField(waiting, "loading", true);
        List items = org.robolectric.util.ReflectionHelpers.getField(search, "items");
        items.add(waiting);
        androidx.recyclerview.widget.RecyclerView results =
            org.robolectric.util.ReflectionHelpers.getField(search, "results");
        results.getAdapter().notifyDataSetChanged();
    }

    public static void listening(PreviewSearch search, boolean on)
    {
        search.showListening(on);
    }

    public static String[] nothingWords(String typed, boolean streaming)
    {
        return PreviewSearch.nothingWords(typed, streaming);
    }

    public static String heading(String title, boolean nonFiction)
    {
        return SearchStreaming.heading(title, nonFiction);
    }

    private PreviewSearchTestAccess()
    {
    }
}
