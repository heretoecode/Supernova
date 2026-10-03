package com.archos.mediacenter.video.leanback;
import android.app.Application;
import android.view.*;
import androidx.leanback.widget.*;
import com.archos.mediacenter.video.browser.adapters.object.Movie;
import com.archos.mediacenter.video.leanback.details.*;
import org.junit.*;
import org.junit.runner.RunWith;
import org.robolectric.*;
import org.robolectric.annotation.*;
import static org.junit.Assert.*;
@RunWith(RobolectricTestRunner.class) @Config(application=Application.class,sdk=28,qualifiers="w960dp-h540dp-land-mdpi")
public class PreviewMoviePageTest {
    @Test public void seriesInformationDoesNotCountUnknownFileSizesAsZero(){
        var host=Robolectric.buildActivity(TopNavigationTest.Host.class).setup().visible();
        try{
            PreviewMoviePage page=new PreviewMoviePage(host.get(),ArrayObjectAdapter::new,a->{},()->{},uri->{});host.get().setContentView(page);
            page.bindShow(new com.archos.mediacenter.video.browser.adapters.object.Tvshow(7,"Series",null,1,2,0,"/series"),()->{});
            for(boolean complete:new boolean[]{false,true}){
                PreviewLibraryLoader.Snapshot snapshot=new PreviewLibraryLoader.Snapshot();
                for(int id=1;id<=2;id++){
                    var episode=new com.archos.mediacenter.video.browser.adapters.object.Episode(id,id,1,id,"Episode",0,0,"","Synopsis","Series","/fixture/"+id,null,null,0,0,0,0,false,false,false,false,0,0,1920,1080,null,null,null,null,0,1,id==1||complete?1000:0);
                    snapshot.episodes.add(new PreviewLibraryLoader.Entry(episode,0,7,""));
                }
                page.setSnapshot(snapshot);
                View panel=page.findViewWithTag("semantic:details.panel.library.information");assertNotNull(panel);
                assertNotNull(PreviewPagesTest.findText(panel,"Library size"));
                if(complete){assertNull(PreviewPagesTest.findText(panel,"≥ "));assertNotNull(PreviewPagesTest.findText(panel,"Average file size"));}
                else{assertNotNull(PreviewPagesTest.findText(panel,"≥ "));assertNull(PreviewPagesTest.findText(panel,"Average file size"));}
            }
        }finally{host.pause().stop().destroy();}
    }
    @Test public void unmatchedHeroExposesTheExistingMatchHandlerAndHonestPlaceholders()throws Exception{
        var host=Robolectric.buildActivity(TopNavigationTest.Host.class).setup().visible();
        try{
            ArrayObjectAdapter actions=new ArrayObjectAdapter();actions.add(new Action(VideoActionAdapter.ACTION_SCRAP,"Find a Match"));long[] chosen={-1};
            PreviewMoviePage page=new PreviewMoviePage(host.get(),()->actions,a->chosen[0]=a.getId(),()->{},uri->{});host.get().setContentView(page);
            android.net.Uri file=android.net.Uri.parse("file:///storage/unmatched.mkv");page.bind(new com.archos.mediacenter.video.browser.adapters.object.NonIndexedVideo(file,file,"Unmatched fixture",null));PreviewPagesTest.layout(page);
            View match=page.findViewWithTag("action:Match Metadata"),play=page.findViewWithTag("action:Play");assertNotNull(match);assertEquals(View.VISIBLE,match.getVisibility());
            assertNotNull(PreviewPagesTest.findText(page,"Not matched"));assertNotNull(PreviewPagesTest.findText(page,"Not matched · Synopsis unavailable"));
            play.requestFocus();page.dispatchKeyEvent(new KeyEvent(KeyEvent.ACTION_DOWN,KeyEvent.KEYCODE_DPAD_UP));assertSame(match,page.findFocus());
            match.performClick();assertEquals(VideoActionAdapter.ACTION_SCRAP,chosen[0]);
            page.dispatchKeyEvent(new KeyEvent(KeyEvent.ACTION_DOWN,KeyEvent.KEYCODE_DPAD_DOWN));assertSame(play,page.findFocus());
            actions.clear();assertEquals(View.GONE,match.getVisibility());
        }finally{host.pause().stop().destroy();}
    }
    @Test @GraphicsMode(GraphicsMode.Mode.NATIVE) public void scrolledTitleDoesNotIntroduceAFocusTarget()throws Exception{
        try{com.squareup.picasso.Picasso.get();}catch(IllegalStateException e){com.squareup.picasso.Picasso.setSingletonInstance(new com.squareup.picasso.Picasso.Builder(RuntimeEnvironment.getApplication()).build());}
        org.robolectric.android.controller.ActivityController<TopNavigationTest.Host> host=Robolectric.buildActivity(TopNavigationTest.Host.class).setup().visible();
        try{
            PreviewMoviePage page=new PreviewMoviePage(host.get(),ArrayObjectAdapter::new,a->{},()->{},uri->{});
            TopNavigation nav=new TopNavigation(host.get(),page,i->{},page::atTop);host.get().setContentView(nav);
            page.bindRemote(new org.json.JSONObject().put("title","Compact title fixture"),"movie",0);
            PreviewPagesTest.layout(nav);
            View panel=page.findViewWithTag("semantic:details.panel.key.information");assertTrue(panel.requestFocus());
            page.scrollTo(0,250);
            android.graphics.Bitmap bitmap=android.graphics.Bitmap.createBitmap(960,540,android.graphics.Bitmap.Config.ARGB_8888);
            nav.draw(new android.graphics.Canvas(bitmap));
            assertSame(panel,page.findFocus());
            java.io.File out=new java.io.File("build/reports/preview-ui/details-compact-title.png");out.getParentFile().mkdirs();
            try(java.io.FileOutputStream stream=new java.io.FileOutputStream(out)){bitmap.compress(android.graphics.Bitmap.CompressFormat.PNG,100,stream);}bitmap.recycle();
            page.scrollTo(0,0);assertSame(panel,page.findFocus());
        }finally{host.pause().stop().destroy();}
    }
    @Test public void backgroundSnapshotRetainsInformationPanelFocus()throws Exception{
        org.robolectric.android.controller.ActivityController<TopNavigationTest.Host> host=Robolectric.buildActivity(TopNavigationTest.Host.class).setup().visible();
        try{
            PreviewMoviePage page=new PreviewMoviePage(host.get(),ArrayObjectAdapter::new,a->{},()->{},uri->{});host.get().setContentView(page);
            page.bindRemote(new org.json.JSONObject().put("title","Fixture"),"movie",0);
            PreviewPagesTest.layout(page);
            View panel=page.findViewWithTag("semantic:details.panel.key.information");assertNotNull(panel);assertTrue(panel.requestFocus());
            page.setSnapshot(new PreviewLibraryLoader.Snapshot());
            assertNotNull(page.findFocus());assertEquals("semantic:details.panel.key.information",page.findFocus().getTag());
        }finally{host.pause().stop().destroy();}
    }
    @Test public void remotePanelsOmitEmptyReceptionAndLocalFileInformation() throws Exception {
        org.robolectric.android.controller.ActivityController<TopNavigationTest.Host> host=Robolectric.buildActivity(TopNavigationTest.Host.class).setup();
        try{
            PreviewMoviePage page=new PreviewMoviePage(host.get(),ArrayObjectAdapter::new,a->{},()->{},uri->{});host.get().setContentView(page);
            page.bindRemote(new org.json.JSONObject().put("title","Remote fixture").put("tagline","A real supplied tagline").put("revenue",1234),"movie",0);
            assertNotNull(PreviewPagesTest.findText(page,"Streaming Availability"));
            assertNull(PreviewPagesTest.findText(page,"Technical Information"));
            View reception=PreviewPagesTest.findText(page,"Reception");assertNotNull(reception);
            assertEquals(View.GONE,((View)reception.getParent()).getVisibility());
            assertNotNull(PreviewPagesTest.findText(page,"A real supplied tagline"));
        }finally{host.pause().stop().destroy();}
    }
    @Test public void extrasHavePopulatedCategoriesOnly() throws Exception {
        try{com.squareup.picasso.Picasso.get();}catch(IllegalStateException e){com.squareup.picasso.Picasso.setSingletonInstance(new com.squareup.picasso.Picasso.Builder(RuntimeEnvironment.getApplication()).build());}
        org.robolectric.android.controller.ActivityController<TopNavigationTest.Host> host=Robolectric.buildActivity(TopNavigationTest.Host.class).setup();
        try{
            PreviewMoviePage page=new PreviewMoviePage(host.get(),ArrayObjectAdapter::new,a->{},()->{},uri->{});host.get().setContentView(page);
            page.setTags(null,java.util.Arrays.asList(
                new com.archos.mediascraper.ScraperTrailer(com.archos.mediascraper.ScraperTrailer.Type.SHOW_TRAILER,"Official Trailer","abcdefghijk","YouTube",""),
                new com.archos.mediascraper.ScraperTrailer(com.archos.mediascraper.ScraperTrailer.Type.SHOW_TRAILER,"Official Teaser","lmnopqrstuv","YouTube","")),java.util.Collections.emptyList());
            assertNotNull(PreviewPagesTest.findText(page,"Trailers"));assertNotNull(PreviewPagesTest.findText(page,"Teasers"));
            assertNull(PreviewPagesTest.findText(page,"Interviews"));assertNotNull(page.findViewWithTag("section:Extras"));
            View card=page.findViewWithTag("extra:abcdefghijk");assertNotNull(card);assertTrue(card.isFocusable());
        }finally{host.pause().stop().destroy();}
    }
    @Test @GraphicsMode(GraphicsMode.Mode.NATIVE) public void episodeMetadataDoesNotBorrowSeriesRating() throws Exception {
        try{com.squareup.picasso.Picasso.get();}catch(IllegalStateException e){com.squareup.picasso.Picasso.setSingletonInstance(new com.squareup.picasso.Picasso.Builder(RuntimeEnvironment.getApplication()).build());}
        org.robolectric.android.controller.ActivityController<TopNavigationTest.Host> host=Robolectric.buildActivity(TopNavigationTest.Host.class).setup();
        try {
            ArrayObjectAdapter actions=new ArrayObjectAdapter();PreviewMoviePage page=new PreviewMoviePage(host.get(),()->actions,a->{},()->{},uri->{});host.get().setContentView(page);
            com.archos.mediacenter.video.browser.adapters.object.Episode episode=new com.archos.mediacenter.video.browser.adapters.object.Episode(1,1,1,1,"Pilot",1642377600000L,0,"","An episode synopsis","Example show","/episode",null,null,0,0,0,0,false,false,false,false,1,0,1920,1080,null,null,null,null,0,1,2000);
            com.archos.mediascraper.ShowTags series=new com.archos.mediascraper.ShowTags();series.setRating(9.8f);series.addGenreIfAbsent("Drama");
            com.archos.mediascraper.EpisodeTags tags=new com.archos.mediascraper.EpisodeTags(series,1,1);tags.setRuntime(45,java.util.concurrent.TimeUnit.MINUTES);page.bind(episode);page.setTags(tags,java.util.Collections.emptyList(),java.util.Collections.emptyList());
            assertNotNull(PreviewPagesTest.findText(page,"S1 E1"));assertNotNull(PreviewPagesTest.findText(page,"45 min"));assertNotNull(PreviewPagesTest.findText(page,"Drama"));assertNull(PreviewPagesTest.findText(page,"9.8"));
            tags.setRating(7.4f);page.setTags(tags,java.util.Collections.emptyList(),java.util.Collections.emptyList());assertNotNull(PreviewPagesTest.findText(page,"7.4"));assertNull(PreviewPagesTest.findText(page,"9.8"));PreviewPagesTest.layout(page);PreviewPagesTest.capture(page,"episode-details");
        }finally{host.pause().stop().destroy();}
    }
    @Test @GraphicsMode(GraphicsMode.Mode.NATIVE) public void moviePageDelegatesPlaybackAndRenders() throws Exception {
        try{com.squareup.picasso.Picasso.get();}catch(IllegalStateException e){com.squareup.picasso.Picasso.setSingletonInstance(new com.squareup.picasso.Picasso.Builder(RuntimeEnvironment.getApplication()).build());}
        org.robolectric.android.controller.ActivityController<TopNavigationTest.Host> host=Robolectric.buildActivity(TopNavigationTest.Host.class).setup();
        try{
            ArrayObjectAdapter actions=new ArrayObjectAdapter();actions.add(new Action(VideoActionAdapter.ACTION_RESUME,"Resume"));final long[] selected={-1};
            PreviewMoviePage page=new PreviewMoviePage(host.get(),()->actions,a->selected[0]=a.getId(),()->{},uri->{});
            TopNavigation nav=new TopNavigation(host.get(),page,i->{},page::atTop);nav.selectTab(1);host.get().setContentView(nav);
            Movie m=new Movie(1,"smb://server/movies/film.mkv","The Last Horizon",1,"A journey through the mountains brings a family together.",2024,7.5f,"12",null,7200000,1000,0,0,false,false,false,false,1,0,3840,2160,"Atmos","HEVC",null,null,0,1,1000,0);
            page.bind(m);page.play();assertEquals(VideoActionAdapter.ACTION_RESUME,selected[0]);
            View primary=page.findViewWithTag("action:Play"),more=page.findViewWithTag("action:More");
            primary.requestFocus();page.dispatchKeyEvent(new KeyEvent(KeyEvent.ACTION_DOWN,KeyEvent.KEYCODE_DPAD_RIGHT));assertSame(more,page.findFocus());
            page.dispatchKeyEvent(new KeyEvent(KeyEvent.ACTION_DOWN,KeyEvent.KEYCODE_DPAD_LEFT));assertSame(primary,page.findFocus());
            assertNotNull(PreviewPagesTest.findText(page,"SMB · server/movies/film.mkv"));assertNotNull(PreviewPagesTest.findText(page,"MKV"));
            com.archos.mediascraper.ShowTags cast=new com.archos.mediascraper.ShowTags();for(int person=1;person<=10;person++)cast.addActorIfAbsent("Fixture Person "+person,"Role "+person);page.setTags(cast,java.util.Collections.emptyList(),java.util.Collections.emptyList());
            android.view.ViewGroup body=(android.view.ViewGroup)page.getChildAt(0);android.view.ViewGroup hero=(android.view.ViewGroup)body.getChildAt(0);assertTrue("Hero retains its content and lower tabs",hero.getChildCount()>1);
            assertNull(PreviewPagesTest.findText(page,"See All"));assertNotNull(PreviewPagesTest.findText(page,"Fixture Person 10"));
            assertNull("Empty recommendations have no navigation entry",page.findViewWithTag("section:More Like This"));assertNotNull(page.findViewWithTag("section:Details"));assertNull(page.findViewWithTag("section:Extras"));
            for(int frame=0;frame<4;frame++){nav.measure(View.MeasureSpec.makeMeasureSpec(960,View.MeasureSpec.EXACTLY),View.MeasureSpec.makeMeasureSpec(540,View.MeasureSpec.EXACTLY));nav.layout(0,0,960,540);Shadows.shadowOf(android.os.Looper.getMainLooper()).idleFor(java.time.Duration.ofMillis(50));}
            assertTrue("Initial hero leaves at least 44dp of lower content visible",page.getPaddingTop()+hero.getBottom()<=page.getHeight()-44);
            PreviewPagesTest.addTestArtwork(nav);Shadows.shadowOf(android.os.Looper.getMainLooper()).idleFor(java.time.Duration.ofMillis(210));
            android.graphics.Bitmap bitmap=android.graphics.Bitmap.createBitmap(960,540,android.graphics.Bitmap.Config.ARGB_8888);nav.draw(new android.graphics.Canvas(bitmap));java.io.File out=new java.io.File("build/reports/preview-ui/movie-details.png");out.getParentFile().mkdirs();try(java.io.FileOutputStream stream=new java.io.FileOutputStream(out)){bitmap.compress(android.graphics.Bitmap.CompressFormat.PNG,100,stream);}
            View tab=page.findViewWithTag("section:Details");assertNotNull(tab);tab.requestFocus();
            page.setTags(cast,java.util.Collections.emptyList(),java.util.Collections.emptyList());
            View restored=page.findViewWithTag("section:Details");assertTrue(restored.isSelected());assertTrue(restored.hasFocus());
            restored.performClick();PreviewPagesTest.layout(nav);
            assertNotNull(PreviewPagesTest.findText(page,"Key Information"));assertNotNull(PreviewPagesTest.findText(page,"Reception"));assertNotNull(PreviewPagesTest.findText(page,"Technical Information"));
            View keyPanel=page.findViewWithTag("semantic:details.panel.key.information");assertNotNull(keyPanel);keyPanel.requestFocus();
            for(int frame=0;frame<8;frame++){page.computeScroll();PreviewPagesTest.layout(nav);}
            android.graphics.Rect visible=new android.graphics.Rect();assertTrue("Information fixture must actually show its panel",keyPanel.getGlobalVisibleRect(visible));assertTrue(visible.height()>80);
            PreviewPagesTest.capture(nav,"details-information-next");
        }finally{host.pause().stop().destroy();}
    }
}
