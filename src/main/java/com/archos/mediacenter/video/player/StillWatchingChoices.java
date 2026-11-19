package com.archos.mediacenter.video.player;

import java.util.ArrayList;
import java.util.List;

/**
 * "Still watching?" choices (owner, 8 and 12 Nov 2026): episodes and time for a TV show, only time for a film; an
 * episode choice seen while a film plays counts as three hours.
 */
public final class StillWatchingChoices
{
    public static final class Choice
    {
        public final String label;
        public final int episodes;
        public final int minutes;

        Choice(String label, int episodes, int minutes)
        {
            this.label = label;
            this.episodes = episodes;
            this.minutes = minutes;
        }
    }

    static final int FILM_FALLBACK_MINUTES = 180;

    private StillWatchingChoices()
    {
    }

    public static List<Choice> options(boolean episode)
    {
        List<Choice> options = new ArrayList<>();
        options.add(new Choice("Off", 0, 0));
        if (episode)
        {
            for (int count : new int[] {2, 3})
            {
                options.add(new Choice(count + " episodes", count, 0));
            }
        }
        // Chips Off · 2 episodes · 3 episodes · 2 hours · 3 hours (owner, 18 Nov 2026; REVIEW_0157_2 PS11).
        for (int hours : new int[] {2, 3})
        {
            options.add(new Choice(hours + " hours", 0, hours * 60));
        }
        return options;
    }

    /** Which option the saved setting matches. */
    public static int index(List<Choice> options, int episodes, int minutes, boolean episode)
    {
        if (!episode && episodes > 0 && minutes <= 0)
        {
            minutes = FILM_FALLBACK_MINUTES;
            episodes = 0;
        }
        for (int i = 0; i < options.size(); i++)
        {
            Choice option = options.get(i);
            if (option.episodes == episodes && option.minutes == minutes)
            {
                return i;
            }
        }
        // An older choice no longer offered (5 episodes, 4 hours) shows as the nearest one of its kind.
        int nearest = 0;
        for (int i = 1; i < options.size(); i++)
        {
            Choice option = options.get(i);
            if (episodes > 0 && option.episodes > 0 || episodes <= 0 && minutes > 0 && option.minutes > 0)
            {
                nearest = i;
            }
        }
        return nearest;
    }
}
