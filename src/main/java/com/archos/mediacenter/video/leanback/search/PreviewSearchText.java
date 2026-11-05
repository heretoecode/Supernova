package com.archos.mediacenter.video.leanback.search;
import java.text.Normalizer;
import java.util.Locale;
import java.util.List;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

/** Bounded Unicode matching and ranking over actual indexed library fields. */
public final class PreviewSearchText
{
    public static String fold(String value)
    {
        return value == null ? ""
                             : Normalizer.normalize(value, Normalizer.Form.NFKD)
                                   .replaceAll("\\p{M}+", "")
                                   .toLowerCase(Locale.ROOT)
                                   .replace('ł', 'l')
                                   .replace('ø', 'o')
                                   .replace("ß", "ss")
                                   .replaceAll("[^\\p{L}\\p{N}]+", " ")
                                   .trim()
                                   .replaceAll(" +", " ");
    }

    private static String articles(String text)
    {
        return text.replaceFirst("^(the|an|a)(?: +|$)", "");
    }

    public static int rank(String query, String... values)
    {
        String needle = articles(fold(query));
        if (needle.isEmpty())
            return 0;
        int best = 0;
        for (String value : values)
        {
            String candidate = articles(fold(value));
            if (candidate.equals(needle))
                best = Math.max(best, 100);
            else if (candidate.startsWith(needle))
                best = Math.max(best, 90);
            else
            {
                boolean match = true;
                for (String token : needle.split(" "))
                {
                    boolean present = false;
                    for (String word : candidate.split(" "))
                        if (word.startsWith(token) || token.length() >= 4 && oneEdit(token, word))
                        {
                            present = true;
                            break;
                        }
                    if (!present)
                    {
                        match = false;
                        break;
                    }
                }
                if (match)
                    best = Math.max(best, 50);
            }
        }
        return best;
    }

    /** Titles/original titles outrank people/studios; a filename-only hit is always last. */
    public static int rankFields(String query, List<String> titles, List<String> people, String filename)
    {
        int title = rank(query, titles.toArray(new String[0]));
        if (title > 0)
            return 1000 + title;
        int person = rank(query, people.toArray(new String[0]));
        if (person > 0)
            return 500 + person;
        List<String> combined = new ArrayList<>(titles);
        combined.addAll(people);
        combined.removeIf(value -> value == null || value.isEmpty());
        int acrossFields = rank(query, String.join(" ", combined));
        if (acrossFields > 0)
            return 400 + acrossFields;
        int file = rank(query, filename);
        return file == 0 ? 0 : 100 + file;
    }

    public static final class Document<T>
    {
        public final T value;
        public final List<String> titles;
        public final List<String> people;
        public final String filename;

        public Document(T value, List<String> titles, List<String> people, String filename)
        {
            this.value = value;
            this.titles = titles;
            this.people = people;
            this.filename = filename;
        }
    }

    public static final class Ranked<T>
    {
        public final T value;
        public final int rank;

        private Ranked(T value, int rank)
        {
            this.value = value;
            this.rank = rank;
        }
    }

    public static <T> List<Ranked<T>> ranked(String query, List<Document<T>> documents)
    {
        List<Ranked<T>> result = new ArrayList<>();
        if (articles(fold(query)).isEmpty())
            return Collections.emptyList();
        for (Document<T> document : documents)
        {
            int score = rankFields(query, document.titles, document.people, document.filename);
            if (score > 0)
                result.add(new Ranked<>(document.value, score));
        }
        result.sort(Comparator.comparingInt((Ranked<T> item) -> item.rank).reversed());
        return Collections.unmodifiableList(result);
    }

    public static boolean matches(String query, String... values)
    {
        return fold(query).isEmpty() || rank(query, values) > 0;
    }

    /** At most one substitution/insertion/deletion; no unbounded matrix allocation. */
    static boolean oneEdit(String a, String b)
    {
        if (Math.abs(a.length() - b.length()) > 1)
            return false;
        int i = 0, j = 0, edits = 0;
        while (i < a.length() && j < b.length())
        {
            if (a.charAt(i) == b.charAt(j))
            {
                i++;
                j++;
                continue;
            }
            if (++edits > 1)
                return false;
            if (a.length() <= b.length())
                j++;
            if (a.length() >= b.length())
                i++;
        }
        return edits + (a.length() - i) + (b.length() - j) <= 1;
    }

    private PreviewSearchText()
    {
    }
}
