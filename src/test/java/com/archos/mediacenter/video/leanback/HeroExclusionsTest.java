package com.archos.mediacenter.video.leanback;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import android.app.Application;
import android.content.Context;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;

/** Exclude from hero (owner, 21 Nov 2026): kept by the title's identity until included again. */
@RunWith(RobolectricTestRunner.class)
@Config(application = Application.class, sdk = 30)
public class HeroExclusionsTest
{
    @Test public void anExcludedTitleStaysOutUntilIncludedAgain()
    {
        Context context = RuntimeEnvironment.getApplication();
        assertFalse(HeroExclusions.excluded(context, "movie:603"));
        HeroExclusions.change(context, "movie:603", true);
        assertTrue(HeroExclusions.excluded(context, "movie:603"));
        assertFalse("another title is unaffected", HeroExclusions.excluded(context, "tv:603"));
        assertEquals(1, HeroExclusions.count(context));
        HeroExclusions.change(context, "movie:603", false);
        assertFalse(HeroExclusions.excluded(context, "movie:603"));
    }
}
