package com.archos.mediacenter.video.leanback.details;

import android.app.Application;
import android.content.Context;
import com.archos.mediacenter.video.leanback.PreviewMetadataCache;
import java.util.Collections;
import org.json.JSONObject;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;
import static org.junit.Assert.*;

@RunWith(RobolectricTestRunner.class) @Config(application=Application.class,sdk=28)
public class PreviewDetailsCacheTest {
    @Test public void diskPackageSuppliesDetailsWithoutInventingProviderAvailability()throws Exception{
        Context context=RuntimeEnvironment.getApplication();
        PreviewMetadataCache.load(context,"tv",42,"",()->new JSONObject("{name:'Series',seasons:[{season_number:1}]}"));
        PreviewMetadataCache.load(context,"tv",42,"credits",()->new JSONObject("{crew:[{id:8,name:'Director',job:'Director'}]}"));
        PreviewMetadataCache.load(context,"tv",42,"season/1",()->new JSONObject("{episodes:[{episode_number:3,name:'Episode'}]}"));
        PreviewMetadataCache.load(context,"tv",42,"videos",()->new JSONObject("{results:[{site:'YouTube',key:'abcdefghijk',name:'Trailer',type:'Trailer'}]}"));
        PreviewMetadataCache.load(context,"tv",42,"recommendations",()->new JSONObject("{results:[{id:43,name:'Local'},{id:44,name:'Unknown availability'}]}"));
        PreviewDetailsData.Result cached=PreviewDetailsData.cached(context,"tv",42,Collections.singleton(43L));
        assertNotNull(cached);assertEquals("Series",cached.details.getString("name"));
        assertEquals(1,cached.credits.getJSONArray("crew").length());assertEquals(3,cached.episodes.get(1).getJSONObject(0).getInt("episode_number"));
        assertEquals(1,cached.extras.size());assertEquals("abcdefghijk",cached.extras.get(0).key);
        assertEquals(1,cached.related.size());assertEquals(43,cached.related.get(0).title.getLong("id"));
        assertTrue(cached.seasonAvailability.isEmpty());
        assertNull(PreviewDetailsData.cached(context,"tv",99,Collections.emptySet()));
    }
}
