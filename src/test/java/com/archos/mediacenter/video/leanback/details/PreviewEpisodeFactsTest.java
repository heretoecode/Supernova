package com.archos.mediacenter.video.leanback.details;

import android.app.Application;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.leanback.widget.ArrayObjectAdapter;
import com.archos.mediacenter.video.browser.adapters.object.Episode;
import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.*;
import org.robolectric.annotation.Config;
import org.robolectric.util.ReflectionHelpers;
import static org.junit.Assert.*;

@RunWith(RobolectricTestRunner.class) @Config(application=Application.class,sdk=28)
public class PreviewEpisodeFactsTest {
    @Test public void episodePanelConsumesItsSeasonPackageWithoutBorrowingSeriesFacts()throws Exception{
        try{com.squareup.picasso.Picasso.get();}catch(IllegalStateException missing){com.squareup.picasso.Picasso.setSingletonInstance(new com.squareup.picasso.Picasso.Builder(RuntimeEnvironment.getApplication()).build());}
        var host=Robolectric.buildActivity(android.app.Activity.class).setup().visible();
        try{
            PreviewMoviePage page=new PreviewMoviePage(host.get(),ArrayObjectAdapter::new,a->{},()->{},uri->{});host.get().setContentView(page);
            Episode episode=new Episode(42,42,2,3,"Episode",0,0,"","Synopsis","Series","/fixture",null,null,0,0,0,0,false,false,false,false,0,0,1920,1080,null,null,null,null,0,1,1000);
            page.bind(episode);
            PreviewDetailsData.Result packageData=new PreviewDetailsData.Result();
            packageData.details=new JSONObject("{first_air_date:'1999-01-01',original_name:'Series original',vote_average:9.8,vote_count:999,runtime:88}");
            packageData.episodes.put(2,new JSONArray("[{episode_number:2,air_date:'2010-02-03',runtime:77,vote_average:9.1},{episode_number:3,air_date:'2022-02-03',runtime:47,vote_average:7.2,vote_count:123}]") );
            ReflectionHelpers.callInstanceMethod(page,"applyEnrichment",ReflectionHelpers.ClassParameter.from(PreviewDetailsData.Result.class,packageData),ReflectionHelpers.ClassParameter.from(int.class,0));
            View key=page.findViewWithTag("semantic:details.panel.key.information"),reception=page.findViewWithTag("semantic:details.panel.reception");
            assertNotNull(key);assertNotNull(reception);String facts=text(key),ratings=text(reception);
            assertTrue(facts.contains("2022"));assertTrue(facts.contains("2022-02-03"));assertTrue(facts.contains("47 min"));assertTrue(facts.contains("Series original title"));
            assertFalse(facts.contains("1999"));assertFalse(facts.contains("2010"));assertFalse(facts.contains("88 min"));assertFalse(facts.contains("77 min"));
            assertTrue(ratings.contains("7.2"));assertTrue(ratings.contains("123"));assertFalse(ratings.contains("9.8"));assertFalse(ratings.contains("999"));
            packageData.episodes.clear();ReflectionHelpers.callInstanceMethod(page,"applyEnrichment",ReflectionHelpers.ClassParameter.from(PreviewDetailsData.Result.class,packageData),ReflectionHelpers.ClassParameter.from(int.class,0));
            facts=text(page.findViewWithTag("semantic:details.panel.key.information"));assertFalse(facts.contains("1999"));assertFalse(facts.contains("88 min"));
        }finally{host.pause().stop().destroy();}
    }
    private String text(View view){StringBuilder result=new StringBuilder();if(view instanceof TextView)result.append(((TextView)view).getText()).append('\n');if(view instanceof ViewGroup)for(int i=0;i<((ViewGroup)view).getChildCount();i++)result.append(text(((ViewGroup)view).getChildAt(i)));return result.toString();}
}
