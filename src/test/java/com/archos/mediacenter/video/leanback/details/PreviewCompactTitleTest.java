package com.archos.mediacenter.video.leanback.details;

import android.app.Application;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;
import static org.junit.Assert.*;

/** Checks the compact Details title appears only after the hero title scrolls out of view. */
@RunWith(RobolectricTestRunner.class)
@Config(application=Application.class,sdk=28)
public class PreviewCompactTitleTest {
    @Test public void compactTitleAppearsOnlyAfterHeroTitleLeavesAndReversesWithScroll() {
        assertEquals(0f,PreviewMoviePage.compactTitleProgress(0,120,40),0f);
        assertEquals(0f,PreviewMoviePage.compactTitleProgress(120,120,40),0f);
        assertEquals(.5f,PreviewMoviePage.compactTitleProgress(140,120,40),0f);
        assertEquals(1f,PreviewMoviePage.compactTitleProgress(200,120,40),0f);
        assertEquals(.25f,PreviewMoviePage.compactTitleProgress(130,120,40),0f);
    }
}
