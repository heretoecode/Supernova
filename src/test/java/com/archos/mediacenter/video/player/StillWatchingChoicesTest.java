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
        assertEquals(7, show.size());
        assertEquals("After 3 episodes", show.get(2).label);
        assertEquals("After 2 hours", show.get(4).label);
        List<StillWatchingChoices.Choice> film = StillWatchingChoices.options(false);
        assertEquals(4, film.size());
        assertEquals("After 2 hours", film.get(1).label);
    }

    @Test
    public void anEpisodeChoiceCountsAsThreeHoursForAFilm()
    {
        List<StillWatchingChoices.Choice> film = StillWatchingChoices.options(false);
        assertEquals("After 3 hours", film.get(StillWatchingChoices.index(film, 3, 0, false)).label);
        List<StillWatchingChoices.Choice> show = StillWatchingChoices.options(true);
        assertEquals("After 5 episodes", show.get(StillWatchingChoices.index(show, 5, 0, true)).label);
        assertEquals("Off", show.get(StillWatchingChoices.index(show, 0, 0, true)).label);
    }
}
