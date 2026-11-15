package com.archos.mediacenter.video.leanback;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class ChoiceListSearchTest
{
    @Test
    public void searchAppearsOnlyAboveTwelveChoicesNotCountingHeadings()
    {
        String[] twelve = new String[13];
        twelve[0] = "— Selected";
        for (int i = 1; i < twelve.length; i++)
        {
            twelve[i] = "Item " + i;
        }
        assertEquals(12, ChoiceListSearch.choices(twelve));
        assertFalse(ChoiceListSearch.shown(twelve));
        String[] thirteen = new String[13];
        for (int i = 0; i < thirteen.length; i++)
        {
            thirteen[i] = "Item " + i;
        }
        assertTrue(ChoiceListSearch.shown(thirteen));
    }

    @Test
    public void promptIsWordedForTheList()
    {
        assertEquals("Search countries", ChoiceListSearch.prompt("Country"));
        assertEquals("Search languages", ChoiceListSearch.prompt("Language"));
        assertEquals("Search services", ChoiceListSearch.prompt("Your services"));
        assertEquals("Search subtitle tracks", ChoiceListSearch.prompt("Subtitle Track"));
        assertEquals("Search", ChoiceListSearch.prompt(""));
    }

    @Test
    public void matchesAnywhereIgnoringCase()
    {
        assertTrue(ChoiceListSearch.matches("United Kingdom", "king"));
        assertTrue(ChoiceListSearch.matches("United Kingdom", " "));
        assertFalse(ChoiceListSearch.matches("France", "kin"));
    }
}
