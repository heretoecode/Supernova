package com.archos.mediacenter.video.player;

import static org.junit.Assert.assertEquals;

import java.util.List;

import org.junit.Test;

public class StillWatchingChoicesTest
{
    @Test
    public void showsOfferEpisodesAndTimeFilmsOnlyTime()
    {
        List<StillWatchingChoices.Choice> show = StillWatchingChoices.options(true);
        // Chips Off · 2 episodes · 3 episodes · 2 hours · 3 hours (owner, 18 Nov 2026; REVIEW_0157_2 PS11).
        assertEquals(5, show.size());
        assertEquals("3 episodes", show.get(2).label);
        assertEquals("2 hours", show.get(3).label);
        List<StillWatchingChoices.Choice> film = StillWatchingChoices.options(false);
        assertEquals(3, film.size());
        assertEquals("2 hours", film.get(1).label);
    }

    @Test
    public void anEpisodeChoiceCountsAsThreeHoursForAFilm()
    {
        List<StillWatchingChoices.Choice> film = StillWatchingChoices.options(false);
        assertEquals("3 hours", film.get(StillWatchingChoices.index(film, 3, 0, false)).label);
        List<StillWatchingChoices.Choice> show = StillWatchingChoices.options(true);
        // A choice no longer offered (5 episodes) shows as the nearest of its kind.
        assertEquals("3 episodes", show.get(StillWatchingChoices.index(show, 5, 0, true)).label);
        assertEquals("Off", show.get(StillWatchingChoices.index(show, 0, 0, true)).label);
    }
}
