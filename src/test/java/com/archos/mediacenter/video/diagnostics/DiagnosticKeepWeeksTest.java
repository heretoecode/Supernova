package com.archos.mediacenter.video.diagnostics;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class DiagnosticKeepWeeksTest
{
    @Test
    public void onlyOneTwoOrFourWeeksWithTwoAsTheDefault()
    {
        assertEquals(1, Diagnostics.keepWeeks("1"));
        assertEquals(4, Diagnostics.keepWeeks("4"));
        assertEquals(2, Diagnostics.keepWeeks("2"));
        assertEquals(2, Diagnostics.keepWeeks(null));
        assertEquals(2, Diagnostics.keepWeeks("3"));
    }
}
