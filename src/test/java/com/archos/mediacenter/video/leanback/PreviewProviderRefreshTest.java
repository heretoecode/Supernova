package com.archos.mediacenter.video.leanback;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;
import static org.junit.Assert.*;

@RunWith(RobolectricTestRunner.class) @Config(application=android.app.Application.class,sdk=28)
public class PreviewProviderRefreshTest {
    @Test public void refreshOnlyForActiveKindCountryAndTitleScope(){
        assertTrue(PreviewPages.providerCacheAffectsPage("streaming_known_at:movie:42:IE:-1",1,"IE",true));
        assertTrue(PreviewPages.providerCacheAffectsPage("streaming_known_at:tv:42:IE:-1",2,"IE",true));
        assertFalse(PreviewPages.providerCacheAffectsPage("streaming_known_at:tv:42:IE:-1",1,"IE",true));
        assertFalse(PreviewPages.providerCacheAffectsPage("streaming_known_at:movie:42:GB:-1",1,"IE",true));
        assertFalse(PreviewPages.providerCacheAffectsPage("streaming_known_at:tv:42:IE:3",2,"IE",true));
    }
    @Test public void backgroundEnrichmentDoesNotRebuildUnfilteredPages(){
        assertFalse(PreviewPages.providerCacheAffectsPage("streaming_known_at:movie:42:IE:-1",1,"IE",false));
        assertFalse(PreviewPages.providerCacheAffectsPage("streaming_known_at:movie:42:IE:-1",0,"IE",true));
        assertFalse(PreviewPages.providerCacheAffectsPage(null,1,"IE",true));
    }
}
