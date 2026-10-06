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
    @Test public void progressiveDeliveryRetainsCachedSectionsUntilTheirRefreshCompletes()throws Exception{
        PreviewDetailsData.Result cached=new PreviewDetailsData.Result();cached.credits=new JSONObject("{cast:[{name:'Actor'}]}");
        cached.extras.add(new PreviewDetailsData.Extra("Trailer","Trailer","abcdefghijk"));cached.extrasReady=true;
        cached.episodes.put(1,new org.json.JSONArray("[{episode_number:1}]"));
        PreviewDetailsData.Result partial=new PreviewDetailsData.Result();partial.details=new JSONObject("{name:'Refreshed'}");
        PreviewDetailsData.Result merged=PreviewDetailsData.merge(cached,partial);
        assertEquals("Refreshed",merged.details.getString("name"));assertEquals(1,merged.extras.size());assertNotNull(merged.credits);assertNotNull(merged.episodes.get(1));
        PreviewDetailsData.Result emptyVideos=new PreviewDetailsData.Result();emptyVideos.extrasReady=true;
        PreviewDetailsData.merge(merged,emptyVideos);assertTrue(merged.extras.isEmpty());assertNotNull(merged.episodes.get(1));
    }
    @Test public void publishedCollectionsCannotBeChangedByLaterWorkerSections(){
        PreviewDetailsData.Result worker=new PreviewDetailsData.Result();worker.extras.add(new PreviewDetailsData.Extra("Trailer","Trailer","abcdefghijk"));
        java.util.concurrent.atomic.AtomicReference<PreviewDetailsData.Result> posted=new java.util.concurrent.atomic.AtomicReference<>();
        PreviewDetailsData.publish(worker,posted::set);worker.extras.clear();worker.episodes.put(2,new org.json.JSONArray());
        assertEquals(1,posted.get().extras.size());assertTrue(posted.get().episodes.isEmpty());
    }
    @Test public void cachedSeasonAndRecommendationsConsumeKnownProviderPackages()throws Exception{
        Context context=RuntimeEnvironment.getApplication();
        PreviewMetadataCache.load(context,"tv",432,"",()->new JSONObject("{name:'Series',seasons:[{season_number:1}]}"));
        PreviewMetadataCache.load(context,"tv",432,"recommendations",()->new JSONObject("{results:[{id:433,name:'Remote'},{id:434,name:'Unknown'}]}"));
        android.content.SharedPreferences prefs=com.archos.mediacenter.video.streaming.StreamingRepository.prefs(context);
        prefs.edit().putBoolean("streaming_enabled",true).putString("streaming_country","IE").putStringSet("streaming_providers_IE",Collections.singleton("8")).apply();
        for(String key:new String[]{"tv:432:IE:1","tv:433:IE:-1"})prefs.edit().putLong("streaming_known_at:"+key,System.currentTimeMillis()).putString("streaming_snapshot:"+key,"{results:{IE:{flatrate:[{provider_id:8,provider_name:'Provider'}]}}}").apply();
        PreviewDetailsData.Result result=PreviewDetailsData.cached(context,"tv",432,Collections.emptySet());
        assertNotNull(result.seasonAvailability.get(1));assertEquals(1,result.related.size());assertEquals(433,result.related.get(0).title.getLong("id"));assertEquals(8,result.related.get(0).provider.id);
    }
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
