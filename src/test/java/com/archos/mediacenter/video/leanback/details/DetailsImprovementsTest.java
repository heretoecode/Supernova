package com.archos.mediacenter.video.leanback.details;

import android.app.Application;
import android.content.Context;
import android.view.*;
import android.widget.*;
import androidx.leanback.widget.ArrayObjectAdapter;
import com.archos.mediacenter.video.browser.adapters.object.Movie;
import com.archos.mediacenter.video.leanback.*;
import com.archos.mediacenter.video.metadata.OmdbSupplement;
import java.io.IOException;
import java.util.*;
import org.json.*;
import org.junit.*;
import org.junit.runner.RunWith;
import org.robolectric.*;
import org.robolectric.annotation.Config;
import org.robolectric.util.ReflectionHelpers;
import static org.junit.Assert.*;

@RunWith(RobolectricTestRunner.class) @Config(application=Application.class,sdk=28,qualifiers="w960dp-h540dp-land-xhdpi")
public class DetailsImprovementsTest {
    Context context;
    @Before public void setup(){context=RuntimeEnvironment.getApplication();PreviewAsyncFixtures.offlineTransport();try{com.squareup.picasso.Picasso.get();}catch(IllegalStateException absent){com.squareup.picasso.Picasso.setSingletonInstance(new com.squareup.picasso.Picasso.Builder(context).build());}}
    @After public void drain()throws Exception{PreviewAsyncFixtures.drain();}
    @Test public void languageHasNoDrawableAndTextUpdatesKeepPanelAndPeople()throws Exception{
        LinearLayout cast=new LinearLayout(context),crew=new LinearLayout(context);
        Map<String,String> facts=new HashMap<>();facts.put("Language","English");
        DetailsBlueprintPanel panel=new DetailsBlueprintPanel(context,"Synopsis",facts,Collections.emptyMap(),Map.of("TMDB Rating","7.8 / 10"),cast,crew,OmdbSupplement.Ratings.EMPTY,()->{});
        TextView language=find(panel,"English");assertNotNull(language);for(var drawable:language.getCompoundDrawables())assertNull(drawable);
        facts.put("Language","English, French");panel.updateText("New synopsis",facts,Collections.emptyMap());
        assertSame(language,find(panel,"English, French"));assertEquals("Language: English, French",((View)language.getParent()).getContentDescription());assertNotNull(panel.findViewWithTag("details.reviews"));assertTrue(panel.findViewWithTag("details.reviews").isFocusable());
        assertNotNull(cast.getParent());assertNotNull(crew.getParent());
    }
    @Test public void peopleGridHasSharedGapFourColumnsAndNoFocusStops(){
        for(int count:new int[]{0,1,4,5,8}){
            DetailsPeopleGrid grid=new DetailsPeopleGrid(context);for(int i=0;i<count;i++)grid.addView(new LinearLayout(context));
            grid.measure(View.MeasureSpec.makeMeasureSpec(801,View.MeasureSpec.EXACTLY),View.MeasureSpec.makeMeasureSpec(281,View.MeasureSpec.EXACTLY));grid.layout(0,0,801,281);
            if(count>4){assertEquals(grid.rowGap(),grid.getChildAt(4).getTop()-grid.getChildAt(0).getBottom());assertEquals(grid.getChildAt(0).getLeft(),grid.getChildAt(4).getLeft());}
            for(int i=0;i<count;i++){assertTrue(grid.getChildAt(i).getBottom()<=grid.getHeight());assertTrue(grid.getChildAt(i).getRight()<=grid.getWidth());}
            assertEquals(ViewGroup.FOCUS_BLOCK_DESCENDANTS,grid.getDescendantFocusability());
        }
    }
    @Test public void partialMergeDoesNotEraseKnownDataAndSnapshotsOwnCollections()throws Exception{
        PreviewDetailsData.Result known=new PreviewDetailsData.Result();known.details=new JSONObject("{runtime:120,original_title:'Known',genres:[{name:'Drama'}]}");known.classification=new JSONObject("{id:1}");known.episodes.put(1,new JSONArray());
        PreviewDetailsData.Result partial=new PreviewDetailsData.Result();partial.details=new JSONObject("{runtime:0,original_title:'',genres:[],overview:'New synopsis'}");
        PreviewDetailsData.Result merged=PreviewDetailsData.merge(known,partial);
        assertEquals(120,merged.details.getInt("runtime"));assertEquals("Known",merged.details.getString("original_title"));assertEquals(1,merged.details.getJSONArray("genres").length());assertSame(known.classification,merged.classification);assertTrue(merged.episodes.containsKey(1));
        known.extras.add(new PreviewDetailsData.Extra("Old","Trailer","abcdefghijk"));PreviewDetailsData.Result empty=new PreviewDetailsData.Result();empty.extrasLoaded=true;
        assertTrue("Successful empty replaces old extras",PreviewDetailsData.merge(known,empty).extras.isEmpty());assertEquals("Failed/unrequested extras retained",1,PreviewDetailsData.merge(known,new PreviewDetailsData.Result()).extras.size());
        PreviewDetailsData.Result snapshot=PreviewDetailsData.copy(known);known.episodes.put(2,new JSONArray());assertFalse(snapshot.episodes.containsKey(2));assertFalse(known.details.has("overview"));
    }
    @Test public void corePublishesBeforeOptionalSeasonOrProviderLookups()throws Exception{
        List<String> requests=new ArrayList<>();List<PreviewDetailsData.Result> frames=new ArrayList<>();
        PreviewDetailsData.load(context,"tv",42,Collections.emptySet(),frames::add,section->{
            requests.add(section);
            if(section.isEmpty())return new JSONObject("{id:42,name:'Series',seasons:[{season_number:1}]}");
            assertFalse("Core visible before any optional request",frames.isEmpty());assertEquals("Series",frames.get(0).details.getString("name"));
            if(section.equals("credits"))return new JSONObject("{cast:[],crew:[]}");
            if(section.equals("season/1")){assertNotNull(frames.get(1).credits);throw new IOException("Season unavailable");}
            if(section.equals("recommendations")||section.equals("videos"))return new JSONObject("{results:[]}");
            throw new IOException("Optional unavailable");
        });
        assertEquals("",requests.get(0));assertEquals("credits",requests.get(1));assertTrue(requests.indexOf("videos")<requests.indexOf("season/1"));assertNull(frames.get(0).credits);assertFalse(frames.get(0).episodes.containsKey(1));
    }
    @Test public void sameInformationRetainsViewAndOldGenerationCannotReplaceNewTitle()throws Exception{
        PreviewMoviePage page=new PreviewMoviePage(context,ArrayObjectAdapter::new,a->{},()->{},u->{});page.bind(movie(1,"First"));
        int generation=ReflectionHelpers.getField(page,"enrichmentGeneration");PreviewDetailsData.Result data=new PreviewDetailsData.Result();data.details=new JSONObject("{original_title:'Original',spoken_languages:[{english_name:'English'}]}");apply(page,data,generation);
        View panel=page.findViewWithTag("details.blueprint.v3");apply(page,PreviewDetailsData.copy(data),generation);assertSame(panel,page.findViewWithTag("details.blueprint.v3"));
        PreviewDetailsData.Result update=new PreviewDetailsData.Result();update.details=new JSONObject("{budget:1234}");apply(page,update,generation);assertSame("Key facts updated in place",panel,page.findViewWithTag("details.blueprint.v3"));assertNotNull(find(page,"English"));
        page.bind(movie(2,"Second"));apply(page,data,generation);assertNull(find(page,"English"));assertNull(find(page,"Original"));assertEquals("Second",((TextView)ReflectionHelpers.getField(page,"title")).getText().toString());
    }
    @Test public void partialRemoteCacheKeepsAlreadySuppliedMetadata()throws Exception{
        PreviewMoviePage page=new PreviewMoviePage(context,ArrayObjectAdapter::new,a->{},()->{},u->{});
        page.bindRemote(new JSONObject("{id:42,title:'Remote',release_date:'2024-02-03',runtime:120,spoken_languages:[{english_name:'English'}]}"),"movie",42);
        int generation=ReflectionHelpers.getField(page,"enrichmentGeneration");PreviewDetailsData.Result partial=new PreviewDetailsData.Result();partial.details=new JSONObject();partial.credits=new JSONObject("{cast:[],crew:[]}");apply(page,partial,generation);
        assertNotNull(find(page,"English"));assertNotNull(find(page,"2h"));
        ReflectionHelpers.callInstanceMethod(page,"freshTitle",ReflectionHelpers.ClassParameter.from(String.class,"other"));
    }
    @Test public void expandedDetailsPreservesTabFocusWithoutWrittenReviews()throws Exception{
        var host=Robolectric.buildActivity(TopNavigationTest.Host.class).setup().visible();try{
            PreviewMoviePage page=new PreviewMoviePage(host.get(),ArrayObjectAdapter::new,a->{},()->{},u->{});TopNavigation root=new TopNavigation(host.get(),page,i2->{},page::atTop);host.get().setContentView(root);
            page.bindRemote(new JSONObject("{title:'Movie',id:42}"),"movie",42);
            for(int i2=0;i2<3;i2++){root.measure(View.MeasureSpec.makeMeasureSpec(1920,View.MeasureSpec.EXACTLY),View.MeasureSpec.makeMeasureSpec(1080,View.MeasureSpec.EXACTLY));root.layout(0,0,1920,1080);Shadows.shadowOf(android.os.Looper.getMainLooper()).idle();}
            View tab=page.findViewWithTag("section:Details");assertTrue(tab.requestFocus());root.dispatchKeyEvent(new KeyEvent(KeyEvent.ACTION_DOWN,KeyEvent.KEYCODE_DPAD_DOWN));assertSame(tab,root.findFocus());root.dispatchKeyEvent(new KeyEvent(KeyEvent.ACTION_DOWN,KeyEvent.KEYCODE_DPAD_DOWN));assertNull(page.findViewWithTag("details.reviews"));assertSame(tab,root.findFocus());
        }finally{host.pause().stop().destroy();}
    }
    @Test public void sameTitleArtRequestRetainsDrawableAndNewIdentityClearsIt(){
        PreviewMoviePage page=new PreviewMoviePage(context,ArrayObjectAdapter::new,a->{},()->{},u->{});page.bind(movie(1,"First"));
        ImageView art=ReflectionHelpers.getField(page,"cardArtwork");android.graphics.drawable.Drawable known=new android.graphics.drawable.ColorDrawable(android.graphics.Color.BLUE);art.setImageDrawable(known);
        android.net.Uri pending=android.net.Uri.parse("file:///missing-details-fixture.png");ReflectionHelpers.callInstanceMethod(page,"cardArtwork",ReflectionHelpers.ClassParameter.from(android.net.Uri.class,pending));
        assertSame("Loading replacement must not clear known image",known,art.getDrawable());
        ReflectionHelpers.callInstanceMethod(page,"cardArtwork",ReflectionHelpers.ClassParameter.from(android.net.Uri.class,pending));assertSame(known,art.getDrawable());
        page.bind(movie(2,"Second"));assertNull("No artwork from previous title",art.getDrawable());
    }
    @Test public void reviewParsingBoundsContentAndRejectsUntrustedProvenance()throws Exception{
        JSONObject json=new JSONObject("{page:1,total_pages:100,results:[{id:'abcdef012345abcdef012345',author:'Author',content:'**Plain text** <script>inert</script>',created_at:'2026-10-27T00:00:00Z',author_details:{rating:8}},{id:'bad',author:'',content:'Body',url:'https://evil.invalid',author_details:{rating:999}}]}");
        DetailsReviews.Page page=DetailsReviews.parse(json,1);assertEquals(5,page.total);assertEquals(2,page.reviews.size());assertEquals("2026-10-27",page.reviews.get(0).date);assertEquals(8,page.reviews.get(0).rating,0);assertTrue(page.reviews.get(0).url.startsWith("https://www.themoviedb.org/review/"));assertEquals("",page.reviews.get(1).url);assertTrue(Double.isNaN(page.reviews.get(1).rating));assertTrue(page.reviews.get(0).body.contains("<script>"));
        JSONArray rows=new JSONArray();for(int i=0;i<25;i++)rows.put(new JSONObject().put("content","x".repeat(20000)).put("id",String.valueOf(i)));
        page=DetailsReviews.parse(new JSONObject().put("page",1).put("total_pages",2).put("results",rows),1);assertEquals(20,page.reviews.size());assertTrue(page.reviews.get(0).body.length()<12100);
    }
    @Test public void successfulEmptyReviewsDifferFromInvalidPage()throws Exception{
        assertTrue(DetailsReviews.parse(new JSONObject("{page:1,total_pages:0,results:[]}"),1).reviews.isEmpty());
        for(String bad:new String[]{"{page:2,total_pages:2,results:[]}","{page:1,total_pages:1}","{page:1,total_pages:-1,results:[]}"})try{DetailsReviews.parse(new JSONObject(bad),1);fail("Expected invalid page");}catch(IOException expected){}
    }
    @Test public void reviewReaderRetainsContentOnFailedNextPageAndRestoresOpener()throws Exception{
        var host=Robolectric.buildActivity(TopNavigationTest.Host.class).setup().visible();try{
            TextView opener=TvButton.create(host.get(),"Read reviews",()->{},false);host.get().setContentView(opener);opener.requestFocus();
            DetailsReviews reader=new DetailsReviews(host.get(),"movie",42,page->{if(page==2)throw new IOException("Offline");return new JSONObject("{page:1,total_pages:2,results:[{author:'Author',content:'Retained review'}]}");});reader.show();finish(reader);
            android.app.Dialog dialog=ReflectionHelpers.getField(reader,"dialog");assertNotNull(find(dialog.getWindow().getDecorView(),"Retained review"));
            find(dialog.getWindow().getDecorView(),"Next").performClick();finish(reader);
            assertNotNull(find(dialog.getWindow().getDecorView(),"Retained review"));assertNotNull(find(dialog.getWindow().getDecorView(),"Could not load this page. Previously loaded reviews are retained."));
            reader.close();assertFalse(dialog.isShowing());assertTrue(opener.hasFocus());
        }finally{host.pause().stop().destroy();}
    }
    @Test public void reviewReaderShowsSuccessfulEmptyState()throws Exception{
        var host=Robolectric.buildActivity(TopNavigationTest.Host.class).setup().visible();try{
            DetailsReviews reader=new DetailsReviews(host.get(),"tv",42,page->new JSONObject("{page:1,total_pages:0,results:[]}"));reader.show();finish(reader);
            TextView status=ReflectionHelpers.getField(reader,"status");assertTrue(status.getText().toString().startsWith("No written reviews available"));assertFalse(status.getText().toString().contains("Reviews unavailable"));reader.close();
        }finally{host.pause().stop().destroy();}
    }
    private void finish(DetailsReviews reader)throws Exception{java.util.concurrent.Future<?> task=ReflectionHelpers.getField(reader,"task");task.get(10,java.util.concurrent.TimeUnit.SECONDS);Shadows.shadowOf(android.os.Looper.getMainLooper()).idle();}
    private void apply(PreviewMoviePage page,PreviewDetailsData.Result result,int generation){ReflectionHelpers.callInstanceMethod(page,"applyEnrichment",ReflectionHelpers.ClassParameter.from(PreviewDetailsData.Result.class,result),ReflectionHelpers.ClassParameter.from(int.class,generation));}
    private Movie movie(long id,String name){return new Movie(id,"file:///fixture.mkv",name,1,"Synopsis",2026,7,"",null,7200000,0,0,0,false,false,false,false,0,0,1920,1080,"AAC","H.264",null,null,0,1,1234,0);}
    private TextView find(View root,String text){if(root instanceof TextView&&text.contentEquals(((TextView)root).getText()))return (TextView)root;if(root instanceof ViewGroup)for(int i=0;i<((ViewGroup)root).getChildCount();i++){TextView found=find(((ViewGroup)root).getChildAt(i),text);if(found!=null)return found;}return null;}
}
