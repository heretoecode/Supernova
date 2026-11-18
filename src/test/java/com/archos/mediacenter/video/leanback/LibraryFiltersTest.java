package com.archos.mediacenter.video.leanback;

import java.util.Arrays;
import java.util.List;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

/** Year filter steps (page sheet LIBRARY.md: From / To, Left / Right steps through years that hold titles). */
public class LibraryFiltersTest
{
    private static final List<Integer> PRESENT = Arrays.asList(2024, 2019, 1994, 2021);

    @Test
    public void fromStepsUpFromAnyAndBackToAny()
    {
        assertEquals(1994, LibraryFilters.stepYear(PRESENT, 0, 1, true));
        assertEquals(2019, LibraryFilters.stepYear(PRESENT, 1994, 1, true));
        assertEquals(0, LibraryFilters.stepYear(PRESENT, 1994, -1, true));
        assertEquals(0, LibraryFilters.stepYear(PRESENT, 0, -1, true));
    }

    @Test
    public void toStepsDownFromAnyAndPastTheNewestToAny()
    {
        assertEquals(2024, LibraryFilters.stepYear(PRESENT, 0, -1, false));
        assertEquals(2021, LibraryFilters.stepYear(PRESENT, 2024, -1, false));
        assertEquals(0, LibraryFilters.stepYear(PRESENT, 2024, 1, false));
    }

    @Test
    public void aYearWithNoTitlesStepsToTheNearestThatHasSome()
    {
        assertEquals(2019, LibraryFilters.stepYear(PRESENT, 2000, 1, true));
        assertEquals(1994, LibraryFilters.stepYear(PRESENT, 2000, -1, true));
    }
}
