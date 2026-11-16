package com.archos.mediacenter.video.leanback.files;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import android.app.Application;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

@RunWith(RobolectricTestRunner.class)
@Config(application = Application.class, sdk = 30)
public class MatchCheckTest
{
    @Test
    public void onlyADifferentTitleIsAChange()
    {
        assertTrue(MatchCheck.suggests(550, 680));
        assertFalse(MatchCheck.suggests(550, 550));
        assertFalse("Unmatched films are not changes", MatchCheck.suggests(0, 680));
        assertFalse("Nothing found is not a change", MatchCheck.suggests(550, 0));
    }
}
