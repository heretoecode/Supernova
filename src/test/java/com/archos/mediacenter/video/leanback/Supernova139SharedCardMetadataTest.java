package com.archos.mediacenter.video.leanback;

import android.app.Application;
import android.view.View;
import android.widget.*;
import androidx.leanback.widget.ArrayObjectAdapter;
import com.archos.mediacenter.video.leanback.details.PreviewMoviePage;
import org.json.JSONObject;
import org.junit.*;
import org.junit.runner.RunWith;
import org.robolectric.*;
import org.robolectric.annotation.*;
import org.robolectric.util.ReflectionHelpers;
import static org.junit.Assert.*;

@RunWith(RobolectricTestRunner.class) @Config(application=Application.class,sdk=28,qualifiers="w960dp-h540dp-land-mdpi")
@GraphicsMode(GraphicsMode.Mode.NATIVE)
public class Supernova139SharedCardMetadataTest {
    @Before public void offline(){PreviewAsyncFixtures.offlineTransport();}
    @After public void workers()throws Exception{PreviewAsyncFixtures.drain();}
    @Test public void cardPolicyBoundsGenresAndVotesWithoutMutatingFullFacts(){
        SharedMetadata.Facts full=new SharedMetadata.Facts();full.certificate="15";full.year="2024";full.genres="Action|Adventure|Drama|Fantasy";full.context="2h";full.rating=8.2;full.votes=123456;full.technical.add("4K");
        SharedMetadata.Facts home=SharedMetadata.cardFacts(full,true),details=SharedMetadata.cardFacts(full,false);
        assertEquals(3,PreviewGenres.parse(home.genres).size());assertEquals(home.genres,details.genres);assertEquals(0,home.votes);assertEquals(0,details.votes);assertTrue(home.technical.isEmpty());assertEquals("",home.context);assertEquals(full.technical,details.technical);assertEquals("2h",details.context);
        assertEquals(4,PreviewGenres.parse(full.genres).size());assertEquals(123456,full.votes);assertEquals(1,full.technical.size());
        assertEquals("15 · 2024 · Action, Adventure, Drama · TMDB 8.2/10",SharedMetadata.primary(home).toString());
    }
    @Test public void actualDetailsHeaderUsesSharedPolicyAfterRemeasureAndLowerFactsRetainFullGenresAndVotes()throws Exception{
        var host=Robolectric.buildActivity(TopNavigationTest.Host.class).setup().visible();
        try{
            PreviewMoviePage page=new PreviewMoviePage(host.get(),ArrayObjectAdapter::new,a->{},()->{},uri->{});host.get().setContentView(page);
            page.bindRemote(new JSONObject("{title:'Metadata fixture',release_date:'2024-01-01',runtime:120,vote_average:8.2,vote_count:123456,overview:'Full synopsis remains unchanged.',genres:[{name:'Action'},{name:'Adventure'},{name:'Drama'},{name:'Fantasy'}]}"),"movie",123);
            PreviewPagesTest.layout(page);TextView meta=ReflectionHelpers.getField(page,"meta");String header=meta.getText().toString();assertTrue(header.contains("TMDB 8.2/10"));assertFalse(header.contains("votes"));assertFalse(header.contains("Fantasy"));assertEquals(1,meta.getMaxLines());assertEquals(16,meta.getTextSize(),.01);
            LinearLayout lower=ReflectionHelpers.getField(page,"details");assertTrue(contains(lower,"Fantasy"));assertTrue(contains(lower,"TMDb 8.2 / 10"));assertTrue(contains(lower,"123,456 votes"));SharedMetadata.Facts full=ReflectionHelpers.getField(page,"latestSummary");assertEquals(123456,full.votes);assertNull(PreviewPagesTest.findText(lower,"No rating available"));TextView plot=ReflectionHelpers.getField(page,"plot");assertEquals("Full synopsis remains unchanged.",plot.getText().toString());
            PreviewPagesTest.layout(page);assertEquals(1,meta.getMaxLines());assertEquals(16,meta.getTextSize(),.01);assertEquals(header,meta.getText().toString());
        }finally{host.pause().stop().destroy();}
    }
    private boolean contains(View view,String text){if(view instanceof TextView&&((TextView)view).getText().toString().contains(text))return true;if(view instanceof android.view.ViewGroup)for(int i=0;i<((android.view.ViewGroup)view).getChildCount();i++)if(contains(((android.view.ViewGroup)view).getChildAt(i),text))return true;return false;}
}
