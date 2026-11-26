package com.archos.mediacenter.video.leanback.search;

import static org.junit.Assert.assertEquals;

import android.app.Application;
import org.json.JSONObject;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

/** Search › "Stream more from <person>" (SEARCH.md): the person is found in a library film's credits by name. */
@RunWith(RobolectricTestRunner.class)
@Config(application = Application.class, sdk = 28)
public class SearchStreamingPersonTest
{
    @Test
    public void findsThePersonInCastOrCrew() throws Exception
    {
        JSONObject credits = new JSONObject("{\"cast\":[{\"id\":11,\"name\":\"Jonah Reyes\"}],"
            + "\"crew\":[{\"id\":42,\"name\":\"Mara Ellison\",\"job\":\"Director\"}]}");
        assertEquals(42, SearchStreaming.personId(credits, "mara ellison "));
        assertEquals(11, SearchStreaming.personId(credits, "Jonah Reyes"));
        assertEquals(0, SearchStreaming.personId(credits, "Nobody Here"));
        assertEquals("Stream more from Mara Ellison", SearchStreaming.personHeading("Mara Ellison"));
    }

    @Test
    public void secondLineSaysKindAndJob()
    {
        assertEquals("2022 · TV show · director", PreviewSearch.secondLine(2022, "TV show", "director"));
    }
}
