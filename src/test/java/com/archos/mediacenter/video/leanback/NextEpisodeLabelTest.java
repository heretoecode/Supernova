package com.archos.mediacenter.video.leanback;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collections;
import org.json.JSONObject;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

/** The next-episode label and Status words (BRIEF_0158_B › Next episode on the hero; DETAILS.md). */
@RunWith(RobolectricTestRunner.class)
@Config(sdk = 30)
public class NextEpisodeLabelTest
{
    private static final LocalDate WED = LocalDate.of(2026, 11, 25);

    private static JSONObject show(String status, String nextDate) throws Exception
    {
        JSONObject details = new JSONObject().put("status", status).put("number_of_seasons", 2);
        if (nextDate != null)
            details.put("next_episode_to_air",
                        new JSONObject().put("air_date", nextDate).put("season_number", 2).put("episode_number", 4));
        return details;
    }

    @Test public void todayTomorrowAndTheWeekday() throws Exception
    {
        assertEquals("New episode today",
                     NextEpisodeLabel.words(show("Returning Series", "2026-11-25"), null, WED));
        assertEquals("New episode tomorrow",
                     NextEpisodeLabel.words(show("Returning Series", "2026-11-26"), null, WED));
        assertEquals("New episode Sunday",
                     NextEpisodeLabel.words(show("Returning Series", "2026-11-29"), null, WED));
    }

    @Test public void everySundayOnceTheLastThreeAiredOnSundays() throws Exception
    {
        assertEquals("New episode every Sunday", NextEpisodeLabel.words(show("Returning Series", "2026-11-29"),
            Arrays.asList(LocalDate.of(2026, 11, 8), LocalDate.of(2026, 11, 15), LocalDate.of(2026, 11, 22)), WED));
        assertEquals("New episode Sunday", NextEpisodeLabel.words(show("Returning Series", "2026-11-29"),
            Arrays.asList(LocalDate.of(2026, 11, 8), LocalDate.of(2026, 11, 14), LocalDate.of(2026, 11, 22)), WED));
    }

    @Test public void nothingForEndedShowsUnknownOrLaterDates() throws Exception
    {
        assertNull(NextEpisodeLabel.words(show("Ended", "2026-11-26"), null, WED));
        assertNull(NextEpisodeLabel.words(show("Returning Series", null), null, WED));
        assertNull(NextEpisodeLabel.words(show("Returning Series", "2026-12-03"), Collections.emptyList(), WED));
        assertNull(NextEpisodeLabel.words(null, null, WED));
    }

    @Test public void statusNamesTheNextEpisodeOrTheSeasons() throws Exception
    {
        assertEquals("Returning · S2 E4 on Sun 29 Nov",
                     NextEpisodeLabel.status(show("Returning Series", "2026-11-29"), WED));
        assertEquals("Returning · 2 seasons", NextEpisodeLabel.status(show("Returning Series", null), WED));
        assertEquals("Ended · 2 seasons", NextEpisodeLabel.status(show("Ended", null), WED));
    }
}
