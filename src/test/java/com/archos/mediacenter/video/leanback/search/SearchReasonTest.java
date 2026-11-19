package com.archos.mediacenter.video.leanback.search;

import java.util.Arrays;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

/** SEARCH.md: a person match lists the title with the reason, e.g. "Taylor Sheridan · director". */
public class SearchReasonTest
{
    @Test
    public void namesWhoMatchedAndAsWhat()
    {
        assertEquals("Avery Lindqvist · director", PreviewSearch.reason("lindqvist",
            Arrays.asList("m_actors", "m_directors"), Arrays.asList("Sam Okoro (Ray), Jo Hart", "Avery Lindqvist")));
        assertEquals("Sam Okoro · cast", PreviewSearch.reason("sam okoro",
            Arrays.asList("m_actors", "m_directors"), Arrays.asList("Sam Okoro (Ray), Jo Hart", "Avery Lindqvist")));
        assertEquals("", PreviewSearch.reason("nobody", Arrays.asList("m_actors"), Arrays.asList("Jo Hart")));
    }
}
