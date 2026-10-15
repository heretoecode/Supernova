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
    private void verify(boolean compact)throws Exception{
        var host=Robolectric.buildActivity(TopNavigationTest.Host.class).setup().visible();try{
            PreviewMoviePage page=new PreviewMoviePage(host.get(),ArrayObjectAdapter::new,a->{},()->{},uri->{});
            TopNavigation root=new TopNavigation(host.get(),page,i->{},page::atTop);host.get().setContentView(root);
            String synopsis="Complete synopsis sentence with meaningful information. ".repeat(90);
            String original="Original title containing substantial additional information ".repeat(5);
            page.bindRemote(new JSONObject().put("name","Long series fixture").put("overview",synopsis).put("original_name",original).put("tagline","Long supplied tagline ".repeat(20)),"tv",0);
            var extra=new ScraperTrailer(ScraperTrailer.Type.SHOW_TRAILER,"Fixture trailer","abcdefghijk","YouTube","");
            page.setTags(null,List.of(extra),List.of());layout(root);
            View tab=page.findViewWithTag("section:Details");tab.requestFocus();layout(root);
            View frame=page.findViewWithTag("details.connected.frame");ScrollView reading=page.findViewWithTag("details.reading.overflow");
            key(root,KeyEvent.KEYCODE_DPAD_DOWN);layout(root);
            Rect stable=bounds(frame);assertEquals(Math.round(page.getWidth()*.04f),frame.getLeft());assertEquals(page.getWidth()-Math.round(page.getWidth()*.04f),frame.getRight());
            assertEquals("Bottom proportional inset",page.getHeight()-Math.round((page.getHeight()-page.getPaddingTop()-page.getPaddingBottom())*.04f),stable.bottom-page.getTop());
            TextView full=page.findViewWithTag("details.full.synopsis");assertEquals(synopsis,full.getText().toString());assertNull(full.getEllipsize());assertFalse(full.isFocusable());
            TextView originalValue=(TextView)PreviewPagesTest.findText(page,original);assertNotNull(originalValue);assertNull(originalValue.getEllipsize());assertTrue(originalValue.getLineCount()>1);
            int last=-1;for(int n=0;n<150&&last!=reading.getScrollY();n++){last=reading.getScrollY();key(root,KeyEvent.KEYCODE_DPAD_DOWN);assertSame(tab,page.findFocus());assertTrue(tab.isSelected());}
            assertTrue("Long information actually scrolls",reading.getScrollY()>0);assertEquals(reading.getChildAt(0).getHeight()+reading.getPaddingTop()+reading.getPaddingBottom()-reading.getHeight(),reading.getScrollY());
            PreviewPagesTest.capture(root,"0139-details-overflow-bottom-"+root.getWidth()+"-"+root.getResources().getConfiguration().fontScale);
            key(root,KeyEvent.KEYCODE_BACK);assertEquals(0,reading.getScrollY());assertSame(tab,page.findFocus());
            key(root,KeyEvent.KEYCODE_DPAD_RIGHT);View extras=page.findViewWithTag("section:Extras");assertSame(extras,page.findFocus());assertFalse(tab.isSelected());assertTrue(extras.isSelected());
            key(root,KeyEvent.KEYCODE_DPAD_DOWN);layout(root);assertEquals("Stable frame dimensions",stable.width(),frame.getWidth());assertEquals(stable.height(),frame.getHeight());assertEquals("extra:abcdefghijk",page.findFocus().getTag());assertTrue(extras.isSelected());assertFalse(extras.hasFocus());
            Shadows.shadowOf(android.os.Looper.getMainLooper()).idleFor(java.time.Duration.ofMillis(200));layout(root);
            View contentFocus=page.findFocus();android.graphics.Rect shown=bounds(contentFocus),viewport=bounds(reading);int outline=TvLayout.px(root.getContext(),3);assertTrue("Focused content top clearance",shown.top-outline>=viewport.top);assertTrue("Focused content bottom clearance",shown.bottom+outline<=viewport.bottom);assertTrue("Entire growing focused content remains visible: shown="+shown+" viewport="+viewport+" height="+contentFocus.getHeight()+" scale="+contentFocus.getScaleY()+" scroll="+reading.getScrollY(),shown.height()>=Math.round(contentFocus.getHeight()*contentFocus.getScaleY()));
            PreviewPagesTest.capture(root,"0139-details-selected-content-"+root.getWidth()+"-"+root.getResources().getConfiguration().fontScale);
            page.setSnapshot(new PreviewLibraryLoader.Snapshot());layout(root);assertTrue(page.findViewWithTag("section:Extras").isSelected());
            key(root,KeyEvent.KEYCODE_DPAD_UP);assertEquals("section:Extras",page.findFocus().getTag());
            key(root,KeyEvent.KEYCODE_DPAD_LEFT);assertTrue("After Left: focus="+page.findFocus().getTag()+" details="+page.findViewWithTag("section:Details").isSelected()+" extras="+page.findViewWithTag("section:Extras").isSelected(),page.findViewWithTag("section:Details").isSelected());
            key(root,KeyEvent.KEYCODE_DPAD_UP);assertEquals("Remote item with no playable action retains its heading", "section:Details",page.findFocus().getTag());assertEquals(0,page.getScrollY());
        }finally{host.pause().stop().destroy();}
    }
}
