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
            for (int count : new int[] {2, 3, 5})
            {
                options.add(new Choice("After " + count + " episodes", count, 0));
            }
        }
        for (int hours : new int[] {2, 3, 4})
        {
            options.add(new Choice("After " + hours + " hours", 0, hours * 60));
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
        return 0;
    }
}
