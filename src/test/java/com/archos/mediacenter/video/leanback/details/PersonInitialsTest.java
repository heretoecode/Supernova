package com.archos.mediacenter.video.leanback.details;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class PersonInitialsTest
{
    @Test
    public void firstAndLastNameInitials()
    {
        assertEquals("AL", PersonInitials.initials("Ada Lovelace"));
        assertEquals("GH", PersonInitials.initials("Grace Brewster Hopper"));
        assertEquals("C", PersonInitials.initials("Cher"));
        assertEquals("", PersonInitials.initials("  "));
        assertEquals("", PersonInitials.initials(null));
    }
}
