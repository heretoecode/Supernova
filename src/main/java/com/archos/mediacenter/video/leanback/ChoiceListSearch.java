package com.archos.mediacenter.video.leanback;

import java.util.Locale;

/** Rules for the shared choice list's search (RULES §9): when it appears, how it is worded, what matches. */
public final class ChoiceListSearch
{
    /** Search appears when a choice list has more than this many items. */
    public static final int THRESHOLD = 12;

    private ChoiceListSearch()
    {
    }

    public static boolean shown(String[] labels)
    {
        return choices(labels) > THRESHOLD;
    }

    /** Items you can choose: group headings ("— Name") do not count. */
    public static int choices(String[] labels)
    {
        int count = 0;
        for (String label : labels)
        {
            if (!heading(label))
            {
                count++;
            }
        }
        return count;
    }

    public static boolean heading(String label)
    {
        return label.startsWith("— ");
    }

    /** "Search countries", "Search languages", "Search services": worded for the list it searches. */
    public static String prompt(String title)
    {
        String noun = title == null ? "" : title.trim().toLowerCase(Locale.ROOT);
        if (noun.startsWith("your "))
        {
            noun = noun.substring(5);
        }
        if (noun.isEmpty())
        {
            return "Search";
        }
        return "Search " + plural(noun);
    }

    static String plural(String noun)
    {
        if (noun.endsWith("s"))
        {
            return noun;
        }
        if (noun.endsWith("y") && noun.length() > 1 && "aeiou".indexOf(noun.charAt(noun.length() - 2)) < 0)
        {
            return noun.substring(0, noun.length() - 1) + "ies";
        }
        return noun + "s";
    }

    /** Case-insensitive match anywhere in the name; an empty search matches everything. */
    public static boolean matches(String label, String query)
    {
        if (query == null || query.trim().isEmpty())
        {
            return true;
        }
        return label.toLowerCase(Locale.ROOT).contains(query.trim().toLowerCase(Locale.ROOT));
    }
}
