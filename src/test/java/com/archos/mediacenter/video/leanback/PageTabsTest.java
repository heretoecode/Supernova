package com.archos.mediacenter.video.leanback;

import static org.junit.Assert.assertEquals;

import android.app.Application;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

@RunWith(RobolectricTestRunner.class)
@Config(application = Application.class, sdk = 30)
public class PageTabsTest
{
    @Test
    public void onlyTheOpenTabHasAnUnderlineBrightWhileTheRemoteIsBelow()
    {
        assertEquals(PageTabs.Underline.BRIGHT, PageTabs.underline(true, false, true));
        assertEquals(PageTabs.Underline.DULL, PageTabs.underline(true, false, false));
        assertEquals(PageTabs.Underline.NONE, PageTabs.underline(true, true, false));
        assertEquals(PageTabs.Underline.NONE, PageTabs.underline(false, false, true));
    }
}
