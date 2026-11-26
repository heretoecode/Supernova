package com.archos.mediacenter.video.leanback.search;

import android.content.Context;
import android.database.Cursor;
import com.archos.mediacenter.video.diagnostics.Diagnostics;
import com.archos.mediaprovider.video.VideoStore;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

/**
 * What Search's landing shows before typing (page sheet SEARCH.md, owner 25 Nov 2026; `search-v6/tv/09`, `10`):
 * **Genres in your library**, most titles first, each with a backdrop from a library title of that genre; and
 * **People in your library**, the six people with the most titles. Counted per title (a show counts once, not per
 * episode). Read from the library database only: no network.
 */
public final class SearchLanding
{
    static final int PEOPLE = 6;
    /** Four across: one row with recent searches, two rows without. */
    static final int GENRES_ACROSS = 4;

    /** A genre or a person and how many library titles have them. */
    public static final class Card
    {
        public final String name;
        public final int titles;
        /** A backdrop from one of those titles (genres), or a known photo (people); empty when none. */
        public final String artwork;

        Card(String name, int titles, String artwork)
        {
            this.name = name;
            this.titles = titles;
            this.artwork = artwork == null ? "" : artwork;
        }

        /** "42 titles" / "1 title". */
        public String count()
        {
            return titles + (titles == 1 ? " title" : " titles");
        }
    }

    /** One library title, as the landing needs it. */
    static final class Title
    {
        final String key;
        final String genres;
        final String people;
        final String backdrop;

        Title(String key, String genres, String people, String backdrop)
        {
            this.key = key;
            this.genres = genres;
            this.people = people;
            this.backdrop = backdrop;
        }
    }

    public final List<Card> genres;
    public final List<Card> people;

    SearchLanding(List<Card> genres, List<Card> people)
    {
        this.genres = genres;
        this.people = people;
    }

    /** Genres most titles first (ties by name); every genre with at least one title. */
    static List<Card> genres(List<Title> titles)
    {
        Map<String, Set<String>> keys = new LinkedHashMap<>();
        Map<String, String> names = new HashMap<>();
        Map<String, String> art = new HashMap<>();
        for (Title title : titles)
        {
            for (String genre : com.archos.mediacenter.video.leanback.PreviewGenres.parse(title.genres))
            {
                String key = genre.toLowerCase(Locale.ROOT);
                names.putIfAbsent(key, genre);
                keys.computeIfAbsent(key, k -> new LinkedHashSet<>()).add(title.key);
                if (!art.containsKey(key) && title.backdrop != null && !title.backdrop.isEmpty())
                {
                    art.put(key, title.backdrop);
                }
            }
        }
        return ranked(keys, names, art, Integer.MAX_VALUE);
    }

    /** The six people with the most titles (cast and directors), ties by name. */
    static List<Card> people(List<Title> titles, Map<String, String> photos)
    {
        Map<String, Set<String>> keys = new LinkedHashMap<>();
        Map<String, String> names = new HashMap<>();
        for (Title title : titles)
        {
            for (String person : names(title.people))
            {
                String key = person.toLowerCase(Locale.ROOT);
                names.putIfAbsent(key, person);
                keys.computeIfAbsent(key, k -> new LinkedHashSet<>()).add(title.key);
            }
        }
        Map<String, String> art = new HashMap<>();
        for (Map.Entry<String, String> photo : photos.entrySet())
        {
            art.put(photo.getKey().toLowerCase(Locale.ROOT), photo.getValue());
        }
        return ranked(keys, names, art, PEOPLE);
    }

    /** "Ada Lovelace (Herself), Mara Ellison" → [Ada Lovelace, Mara Ellison]; roles in brackets dropped. */
    static List<String> names(String value)
    {
        List<String> result = new ArrayList<>();
        if (value == null)
        {
            return result;
        }
        for (String part : value.split("[,|]"))
        {
            String name = part.replaceAll("\\(.*?\\)", "").trim();
            if (!name.isEmpty() && !result.contains(name))
            {
                result.add(name);
            }
        }
        return result;
    }

    private static List<Card> ranked(Map<String, Set<String>> keys, Map<String, String> names,
                                     Map<String, String> art, int most)
    {
        List<Card> cards = new ArrayList<>();
        for (Map.Entry<String, Set<String>> entry : keys.entrySet())
        {
            cards.add(new Card(names.get(entry.getKey()), entry.getValue().size(), art.get(entry.getKey())));
        }
        cards.sort(Comparator.comparingInt((Card card) -> card.titles)
                       .reversed()
                       .thenComparing(card -> card.name, String.CASE_INSENSITIVE_ORDER));
        return cards.size() > most ? new ArrayList<>(cards.subList(0, most)) : cards;
    }

    /** Reads the library (worker thread). */
    static SearchLanding load(Context context)
    {
        String[] columns = {
            VideoStore.Video.VideoColumns.SCRAPER_MOVIE_ID,  VideoStore.Video.VideoColumns.SCRAPER_SHOW_ID,
            VideoStore.Video.VideoColumns.SCRAPER_M_GENRES,  VideoStore.Video.VideoColumns.SCRAPER_S_GENRES,
            VideoStore.Video.VideoColumns.SCRAPER_M_ACTORS,  VideoStore.Video.VideoColumns.SCRAPER_S_ACTORS,
            VideoStore.Video.VideoColumns.SCRAPER_M_DIRECTORS, VideoStore.Video.VideoColumns.SCRAPER_BACKDROP_URL};
        Map<String, Title> titles = new LinkedHashMap<>();
        String visible = VideoStore.Video.VideoColumns.ARCHOS_HIDDEN_BY_USER + "=0";
        try (Cursor cursor = context.getContentResolver().query(
                 VideoStore.Video.Media.EXTERNAL_CONTENT_URI, columns, visible, null, null))
        {
            while (cursor != null && cursor.moveToNext())
            {
                long movie = cursor.isNull(0) ? 0 : cursor.getLong(0);
                long show = cursor.isNull(1) ? 0 : cursor.getLong(1);
                if (movie <= 0 && show <= 0)
                {
                    continue;
                }
                String key = movie > 0 ? "m" + movie : "s" + show;
                if (titles.containsKey(key))
                {
                    continue;
                }
                String genres = text(cursor, movie > 0 ? 2 : 3);
                String people = movie > 0 ? text(cursor, 4) + "," + text(cursor, 6) : text(cursor, 5);
                titles.put(key, new Title(key, genres, people, text(cursor, 7)));
            }
        }
        catch (RuntimeException unavailable)
        {
            Diagnostics.error("search_landing_unavailable", unavailable);
        }
        List<Title> list = new ArrayList<>(titles.values());
        return new SearchLanding(genres(list), people(list, PersonPhotos.all(context)));
    }

    private static String text(Cursor cursor, int index)
    {
        return cursor.isNull(index) ? "" : cursor.getString(index);
    }
}
