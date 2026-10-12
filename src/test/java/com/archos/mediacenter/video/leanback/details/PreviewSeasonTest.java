package com.archos.mediacenter.video.leanback.details;

import android.app.Application;
import com.archos.mediacenter.video.leanback.*;
import com.archos.mediacenter.video.leanback.PreviewLibraryLoader.*;
import com.archos.mediacenter.video.browser.adapters.object.*;
import org.junit.*;
import org.junit.runner.RunWith;
import org.robolectric.*;
import org.robolectric.annotation.Config;
import org.robolectric.util.ReflectionHelpers;
import org.json.*;
import java.util.*;
import static org.junit.Assert.*;

@RunWith(RobolectricTestRunner.class) @Config(application=Application.class,sdk=28,qualifiers="w960dp-h540dp-land-mdpi")
public class PreviewSeasonTest {
    @Before public void before(){PreviewAsyncFixtures.offlineTransport();try{com.squareup.picasso.Picasso.get();}catch(IllegalStateException absent){com.squareup.picasso.Picasso.setSingletonInstance(new com.squareup.picasso.Picasso.Builder(RuntimeEnvironment.getApplication()).build());}}
    @After public void after()throws Exception{PreviewAsyncFixtures.drain();}
    @Test public void seasonUsesSharedCardExactSeasonMetadataAndAvailableEpisodeCount()throws Exception{
        var host=Robolectric.buildActivity(TopNavigationTest.Host.class).setup().visible();try{PreviewMoviePage page=new PreviewMoviePage(host.get(),androidx.leanback.widget.ArrayObjectAdapter::new,a->{},()->{},uri->{});host.get().setContentView(page);Tvshow show=new Tvshow(7,"Series",null,4,60,0,"/series");Snapshot library=new Snapshot();Entry series=new Entry(show,0,7,"Drama");series.onlineId=7;library.shows.add(series);List<Episode> season=new ArrayList<>();for(int n=1;n<=3;n++){Episode e=new Episode(n,n,1,n,"Episode "+n,0,0,"","","Series","/season/"+n,null,null,1800000,0,0,0,false,false,false,false,1,0,1920,1080,"AAC","h264",null,null,0,1,1000);season.add(e);library.episodes.add(new Entry(e,0,7,"Drama"));}
            JSONObject data=new JSONObject().put("air_date","2025-01-03").put("overview","Exact season synopsis").put("episodes",new JSONArray());PreviewDetailsData.Result result=new PreviewDetailsData.Result();result.details=data;result.episodes.put(1,new JSONArray());page.setSnapshot(library);page.bindSeason(show,1,season,()->{});ReflectionHelpers.setField(page,"enrichment",result);ReflectionHelpers.callInstanceMethod(page,"renderDetails");
            android.widget.TextView metadata=ReflectionHelpers.getField(page,"meta");assertTrue(metadata.getText().toString(),metadata.getText().toString().contains("3 Episodes"));assertFalse(metadata.getText().toString().contains("4 Seasons"));assertFalse(metadata.getText().toString().contains("30m"));assertFalse(metadata.getText().toString().contains("TMDB"));assertNotNull(page.findViewWithTag("semantic:details.card"));android.widget.LinearLayout badges=ReflectionHelpers.getField(page,"pills");assertEquals(android.view.View.GONE,badges.getVisibility());
        }finally{host.pause().stop().destroy();}
    }
    @Test public void seasonDetailCreditsRemainScopedToSeasonCache()throws Exception{Application app=RuntimeEnvironment.getApplication();long id=987654;JSONObject details=new JSONObject().put("season_number",2).put("overview","Season two").put("episodes",new JSONArray());JSONObject credits=new JSONObject().put("cast",new JSONArray().put(new JSONObject().put("id",3).put("name","Season Actor")));PreviewMetadataCache.load(app,"tv",id,"season/2",()->details);PreviewMetadataCache.load(app,"tv",id,"season/2/credits",()->credits);assertNull(PreviewDetailsData.cachedSeason(app,id,1));assertEquals("Season two",PreviewDetailsData.cachedSeason(app,id,2).details.optString("overview"));assertEquals("Season Actor",PreviewDetailsData.cachedSeason(app,id,2).credits.getJSONArray("cast").getJSONObject(0).getString("name"));}
}
