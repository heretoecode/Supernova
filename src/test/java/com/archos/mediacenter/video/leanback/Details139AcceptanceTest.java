package com.archos.mediacenter.video.leanback;

import android.app.Application;
import android.graphics.Rect;
import android.view.*;
import android.widget.*;
import androidx.leanback.widget.ArrayObjectAdapter;
import com.archos.mediacenter.video.leanback.details.PreviewMoviePage;
import com.archos.mediascraper.ScraperTrailer;
import java.util.*;
import org.json.JSONObject;
import org.junit.*;
import org.junit.runner.RunWith;
import org.robolectric.*;
import org.robolectric.annotation.*;
import static org.junit.Assert.*;

@RunWith(RobolectricTestRunner.class)
@Config(application=Application.class,sdk=28,qualifiers="w960dp-h540dp-land-mdpi")
@GraphicsMode(GraphicsMode.Mode.NATIVE)
public class Details139AcceptanceTest {
    @Before public void isolate(){PreviewAsyncFixtures.offlineTransport();try{com.squareup.picasso.Picasso.get();}catch(IllegalStateException absent){com.squareup.picasso.Picasso.setSingletonInstance(new com.squareup.picasso.Picasso.Builder(RuntimeEnvironment.getApplication()).build());}}
    @After public void drain()throws Exception{PreviewAsyncFixtures.drain();}
    private void key(View root,int key){root.dispatchKeyEvent(new KeyEvent(KeyEvent.ACTION_DOWN,key));root.dispatchKeyEvent(new KeyEvent(KeyEvent.ACTION_UP,key));}
    private void layout(View view){var c=view.getResources().getConfiguration();float d=view.getResources().getDisplayMetrics().density;int w=Math.round(c.screenWidthDp*d),h=Math.round(c.screenHeightDp*d);for(int i=0;i<3;i++){view.measure(View.MeasureSpec.makeMeasureSpec(w,View.MeasureSpec.EXACTLY),View.MeasureSpec.makeMeasureSpec(h,View.MeasureSpec.EXACTLY));view.layout(0,0,w,h);Shadows.shadowOf(android.os.Looper.getMainLooper()).idle();}}
    private Rect bounds(View v){Rect r=new Rect();v.getGlobalVisibleRect(r);return r;}
    @Test @Config(qualifiers="w640dp-h360dp-land-xhdpi") public void hdPhysicalViewportFitsSixRegions()throws Exception{verify(true);}
    @Test @Config(qualifiers="w960dp-h540dp-land-xhdpi") public void fullHdPhysicalViewportFitsSixRegions()throws Exception{verify(false);}
    @Test public void connectedSelectionSurvivesInteractiveFocusAndTabRebuild()throws Exception{verify(false);}
    @Test @Config(qualifiers="w640dp-h360dp-land-mdpi") public void compactLongContentRemainsReadableAndRemoteOperable()throws Exception{verify(true);}
    @Test @Config(qualifiers="w1920dp-h1080dp-land-mdpi") public void largeTextOverflowKeepsEveryFact()throws Exception{RuntimeEnvironment.setFontScale(1.5f);try{verify(false);}finally{RuntimeEnvironment.setFontScale(1f);}}
    @Test @Config(qualifiers="w960dp-h540dp-land-xxxhdpi") public void ultraHdDensityRetainsProportionalFrame()throws Exception{verify(false);}
    @Test @Config(qualifiers="w640dp-h360dp-land-mdpi") public void fourCompactTabsRemainCompleteAndBounded()throws Exception{fourTabs();}
    @Test public void fourTabsKeepAStableFrameAtLargeText()throws Exception{RuntimeEnvironment.setFontScale(1.5f);try{fourTabs();}finally{RuntimeEnvironment.setFontScale(1f);}}
    private void fourTabs()throws Exception{
        var host=Robolectric.buildActivity(TopNavigationTest.Host.class).setup().visible();try{
            PreviewMoviePage page=new PreviewMoviePage(host.get(),ArrayObjectAdapter::new,a->{},()->{},uri->{});TopNavigation root=new TopNavigation(host.get(),page,i->{},page::atTop);host.get().setContentView(root);
            var show=new com.archos.mediacenter.video.browser.adapters.object.Tvshow(8,"Series",null,1,1,0,"/fixture");page.bindShow(show,()->{});
            var tags=new com.archos.mediascraper.ShowTags();tags.addGenreIfAbsent("Drama");page.setTags(tags,List.of(new ScraperTrailer(ScraperTrailer.Type.SHOW_TRAILER,"Trailer","abcdefghijk","YouTube","")),List.of());
            var snapshot=new PreviewLibraryLoader.Snapshot();snapshot.shows.add(new PreviewLibraryLoader.Entry(new com.archos.mediacenter.video.browser.adapters.object.Tvshow(9,"Related",null,1,1,0,"/other"),1,9,"Drama"));page.setSnapshot(snapshot);layout(root);
            View details=page.findViewWithTag("section:Details");details.requestFocus();key(root,KeyEvent.KEYCODE_DPAD_DOWN);layout(root);View frame=page.findViewWithTag("details.connected.frame");Rect stable=bounds(frame);
            for(String name:List.of("Details","Extras","Seasons & Episodes","More Like This")){
                TextView tab=page.findViewWithTag("section:"+name);assertNotNull(name,tab);tab.requestFocus();layout(root);Rect visible=bounds(tab);assertEquals(name,tab.getWidth(),visible.width());assertEquals(name,1,tab.getLineCount());assertEquals(name,0,tab.getLayout().getEllipsisCount(0));assertTrue(name,tab.getLayout().getLineWidth(0)<=tab.getWidth()-tab.getCompoundPaddingLeft()-tab.getCompoundPaddingRight());assertEquals(stable,bounds(frame));
            }
            key(root,KeyEvent.KEYCODE_DPAD_RIGHT);assertEquals("section:More Like This",page.findFocus().getTag());
            PreviewPagesTest.capture(root,"0139-details-four-tabs-"+root.getWidth()+"-"+root.getResources().getConfiguration().fontScale);
            page.focusPrimary();key(root,KeyEvent.KEYCODE_DPAD_DOWN);key(root,KeyEvent.KEYCODE_DPAD_UP);assertEquals("action:Play",page.findFocus().getTag());
        }finally{host.pause().stop().destroy();}
    }
    private int[] headerPixels(View root,View frame,View reading){
        Rect a=bounds(frame),b=bounds(reading);android.graphics.Bitmap bitmap=android.graphics.Bitmap.createBitmap(a.width(),b.top-a.top,android.graphics.Bitmap.Config.ARGB_8888);android.graphics.Canvas canvas=new android.graphics.Canvas(bitmap);canvas.translate(-a.left,-a.top);root.draw(canvas);int[] pixels=new int[bitmap.getWidth()*bitmap.getHeight()];bitmap.getPixels(pixels,0,bitmap.getWidth(),0,0,bitmap.getWidth(),bitmap.getHeight());bitmap.recycle();return pixels;
    }
    // 0.140 supersedes the old scrolling Details contract with the approved fixed six regions.
    private void verify(boolean compact)throws Exception{
        var host=Robolectric.buildActivity(TopNavigationTest.Host.class).setup().visible();try{
            PreviewMoviePage page=new PreviewMoviePage(host.get(),ArrayObjectAdapter::new,a->{},()->{},uri->{});TopNavigation root=new TopNavigation(host.get(),page,i->{},page::atTop);host.get().setContentView(root);
            String synopsis="Complete synopsis sentence with meaningful information. ".repeat(90);
            page.bindRemote(new JSONObject().put("name","Long series fixture").put("overview",synopsis).put("original_name","Original title"),"tv",0);
            page.setTags(null,List.of(new ScraperTrailer(ScraperTrailer.Type.SHOW_TRAILER,"Fixture trailer","abcdefghijk","YouTube","")),List.of());layout(root);
            View tab=page.findViewWithTag("section:Details");tab.requestFocus();key(root,KeyEvent.KEYCODE_DPAD_DOWN);layout(root);
            View frame=page.findViewWithTag("details.connected.frame");ScrollView reading=page.findViewWithTag("details.reading.overflow");
            assertFalse("Application navigation disappears in the approved lower-tab view",root.findViewWithTag("semantic:topnav.home").isShown());assertEquals("Reference tab-top anchor",Math.round(root.getHeight()/6f),bounds(frame).top);assertEquals(Math.round(page.getWidth()*.04f),frame.getLeft());assertEquals(page.getWidth()-Math.round(page.getWidth()*.04f),frame.getRight());assertEquals("Four percent viewport bottom inset",page.getHeight()-Math.round(page.getHeight()*.04f),bounds(frame).bottom-page.getTop());
            for(String section:List.of("Description","Key Information","Cast","Crew","Technical Information")){View region=page.findViewWithTag("details.region:"+section);if(region==null)region=page.findViewWithTag("details.region."+section);if(region==null)region=page.findViewWithTag("semantic:details.panel."+section.toLowerCase(java.util.Locale.ROOT).replace(' ','.'));assertNotNull(section,region);assertFalse(region.isFocusable());assertTrue(section+" has height",region.getHeight()>0);Rect regionBounds=bounds(region),viewport=bounds(reading);assertEquals(section+" fully visible",region.getHeight(),regionBounds.height());assertTrue(section+" stays above bottom",regionBounds.bottom<=viewport.bottom);}
            assertNull("No Reception panel without reliable ratings",page.findViewWithTag("semantic:details.panel.reception"));
            TextView full=page.findViewWithTag("details.full.synopsis");assertEquals(synopsis,full.getText().toString());assertFalse(full.isFocusable());assertNotNull("Explicit bounded long-synopsis policy",full.getEllipsize());
            for(int n=0;n<5;n++)key(root,KeyEvent.KEYCODE_DPAD_DOWN);assertSame(tab,page.findFocus());assertEquals("Details is not independently scrollable",0,reading.getScrollY());
            assertFalse(root.dispatchKeyEvent(new KeyEvent(KeyEvent.ACTION_DOWN,KeyEvent.KEYCODE_BACK)));
            PreviewPagesTest.capture(root,"0140-details-fixed-"+root.getWidth()+"-"+root.getResources().getConfiguration().fontScale);
            key(root,KeyEvent.KEYCODE_DPAD_RIGHT);View extras=page.findViewWithTag("section:Extras");assertSame(extras,page.findFocus());key(root,KeyEvent.KEYCODE_DPAD_DOWN);layout(root);assertEquals("extra:abcdefghijk",page.findFocus().getTag());
            PreviewPagesTest.capture(root,"0140-extras-card-"+root.getWidth()+"-"+root.getResources().getConfiguration().fontScale);
            key(root,KeyEvent.KEYCODE_DPAD_UP);layout(root);assertSame(extras,page.findFocus());assertEquals("Heading is recoverable without switching tabs",0,reading.getScrollY());
            assertTrue("Extras heading remains visible",bounds(PreviewPagesTest.findText(page,"Trailers")).height()>0);
            PreviewPagesTest.capture(root,"0140-extras-tab-"+root.getWidth()+"-"+root.getResources().getConfiguration().fontScale);
            key(root,KeyEvent.KEYCODE_DPAD_UP);layout(root);assertTrue("Up restores the existing application navigation",root.findViewWithTag("semantic:topnav.home").isShown());assertEquals(0,page.getScrollY());
        }finally{host.pause().stop().destroy();}
    }
}
