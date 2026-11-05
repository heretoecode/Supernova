package com.archos.mediacenter.video.leanback;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

/** Edit Page year range (owner, 4 Nov 2026; mock-ups edit-page/02–03). */
@RunWith(RobolectricTestRunner.class)
public class CustomLibraryPageYearTest {
    @Test
    public void rangeIncludesBothEndsAndAnyYearMatchesEverything() {
        CustomLibraryPage page = new CustomLibraryPage();
        assertTrue(page.inYears(1999));
        assertTrue("no range: titles without a year still show", page.inYears(0));
        page.year = 2003;
        page.yearTo = 2006;
        assertTrue(page.inYears(2003));
        assertTrue(page.inYears(2006));
        assertFalse(page.inYears(2002));
        assertFalse(page.inYears(2007));
        assertFalse("a range needs a year", page.inYears(0));
        assertEquals("2003 – 2006", page.yearSummary());
    }

    @Test
    public void openEndsAndSingleYearsReadPlainly() {
        CustomLibraryPage page = new CustomLibraryPage();
        assertEquals("Any", page.yearSummary());
        page.year = 2010;
        assertEquals("From 2010", page.yearSummary());
        assertTrue(page.inYears(2024));
        page.year = 0;
        page.yearTo = 1989;
        assertEquals("Up to 1989", page.yearSummary());
        assertTrue(page.inYears(1950));
        assertFalse(page.inYears(1990));
        page.year = 2005;
        page.yearTo = 2005;
        assertEquals("2005", page.yearSummary());
    }

    @Test
    public void pagesSavedWithOneYearBecomeThatYearAndBackwardsRangesAreRejected() {
        CustomLibraryPage old = CustomLibraryPage.decode("{\"name\":\"Old\",\"year\":2004}");
        assertNotNull(old);
        assertEquals(2004, old.year);
        assertEquals(2004, old.yearTo);
        assertFalse(old.inYears(2005));
        CustomLibraryPage page = CustomLibraryPage.decode(old.encode());
        assertEquals(2004, page.yearTo);
        page.year = 2010;
        page.yearTo = 2000;
        assertNotNull("To before From is not saved", page.validation());
        page.yearTo = 2012;
        assertNull(page.validation());
    }
}
