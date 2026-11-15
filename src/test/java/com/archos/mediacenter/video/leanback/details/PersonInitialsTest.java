package com.archos.mediacenter.video.leanback.details;

import static org.junit.Assert.assertEquals;

import android.app.Application;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

@RunWith(RobolectricTestRunner.class)
@Config(application = Application.class, sdk = 30)
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
