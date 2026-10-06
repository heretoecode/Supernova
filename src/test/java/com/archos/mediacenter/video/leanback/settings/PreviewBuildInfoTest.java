package com.archos.mediacenter.video.leanback.settings;

import android.app.Application;
import com.archos.mediacenter.video.BuildConfig;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;
import static org.junit.Assert.*;

@RunWith(RobolectricTestRunner.class)
@Config(application=Application.class,sdk=28)
public class PreviewBuildInfoTest {
    @Test public void aboutRetainsCurrentIdentityAndAuthoritativeImplementationBaseline()throws Exception{
        var describe=PreviewBuildInfo.class.getDeclaredMethod("describe");describe.setAccessible(true);
        String text=(String)describe.invoke(null);
        assertTrue(text.contains("Custom build: SUPERNOVA Preview"));
        assertTrue(text.contains("Version: "+BuildConfig.VERSION_NAME));
        assertTrue(text.contains("Package: "+BuildConfig.APPLICATION_ID));
        assertTrue(text.contains("Custom Git SHA: "+BuildConfig.PREVIEW_GIT_SHA));
        assertTrue(text.contains("Implementation baseline: Preview 4.1.6 094d8e80938501222b4b16ed715c03937abde68f"));
        assertFalse(text.contains("(Preview 4.1.5)"));
    }
}
