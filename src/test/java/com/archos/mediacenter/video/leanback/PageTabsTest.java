package com.archos.mediacenter.video.leanback;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

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
