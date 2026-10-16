package com.archos.mediacenter.video.leanback;

import android.app.Application;
import android.graphics.Rect;
import android.view.*;
import com.archos.mediacenter.video.browser.adapters.object.Movie;
import com.archos.mediacenter.video.leanback.PreviewLibraryLoader.*;
import org.junit.*;
import org.junit.runner.RunWith;
import org.robolectric.*;
import org.robolectric.annotation.*;
import static org.junit.Assert.*;

@RunWith(RobolectricTestRunner.class) @Config(application=Application.class,sdk=28,qualifiers="w960dp-h540dp-land-xhdpi") @GraphicsMode(GraphicsMode.Mode.NATIVE)
public class LibraryStatisticsLayoutTest {
    @Before public void setup(){PreviewAsyncFixtures.offlineTransport();try{com.squareup.picasso.Picasso.get();}catch(IllegalStateException absent){com.squareup.picasso.Picasso.setSingletonInstance(new com.squareup.picasso.Picasso.Builder(RuntimeEnvironment.getApplication()).build());}}
    @After public void drain()throws Exception{PreviewAsyncFixtures.drain();}
    @Test public void fullHdPanelAlignsToLibraryAndHasEqualReferenceGaps()throws Exception{verify();}
    @Test @Config(qualifiers="w640dp-h360dp-land-xhdpi") public void hdPanelFitsAndKeepsFocusOnControls()throws Exception{verify();}
    @Test @Config(qualifiers="w960dp-h540dp-land-xxxhdpi") public void ultraHdPanelFitsAndKeepsFocusOnControls()throws Exception{verify();}
    private void verify()throws Exception{
        var host=Robolectric.buildActivity(TopNavigationTest.Host.class).setup().visible();try{PreviewPages pages=new PreviewPages(host.get(),(holder,item)->{});TopNavigation nav=new TopNavigation(host.get(),pages,pages::setTab,pages::atTop);host.get().setContentView(nav);Snapshot snapshot=new Snapshot();for(int i=1;i<=12;i++){Movie movie=new Movie(i,"/fixture/film"+i,"Film "+i,i,"Fixture synopsis",2026,7,"",null,100000,i==1?10000:0,0,0,false,false,false,false,i,i,1920,1080,"AAC",null,null,null,0,1,1000,0);Entry entry=new Entry(movie,System.currentTimeMillis()/1000,0,"Drama");snapshot.movies.add(entry);snapshot.technical.add(entry);}pages.setSnapshot(snapshot);pages.setTab(1);nav.selectTab(1);
            var config=nav.getResources().getConfiguration();float density=nav.getResources().getDisplayMetrics().density;int width=Math.round(config.screenWidthDp*density),height=Math.round(config.screenHeightDp*density);for(int n=0;n<3;n++){nav.measure(View.MeasureSpec.makeMeasureSpec(width,View.MeasureSpec.EXACTLY),View.MeasureSpec.makeMeasureSpec(height,View.MeasureSpec.EXACTLY));nav.layout(0,0,width,height);Shadows.shadowOf(android.os.Looper.getMainLooper()).idle();}
            View panel=nav.findViewWithTag("library.statistics"),filters=nav.findViewWithTag("control:0");assertNotNull(panel);assertNotNull(filters);Rect panelBounds=new Rect(),controlBounds=new Rect();assertTrue(panel.getGlobalVisibleRect(panelBounds));assertTrue(filters.getGlobalVisibleRect(controlBounds));int gap=Math.round(width*16f/1920);assertEquals("Top reference gap",gap,panelBounds.top-nav.getChildAt(0).getBottom());assertEquals("Bottom reference gap",gap,controlBounds.top-panelBounds.bottom);assertFalse(panel.isFocusable());assertTrue(filters.requestFocus());assertSame(filters,nav.findFocus());PreviewPagesTest.capture(nav,"0140-library-statistics-"+width);
        }finally{host.pause().stop().destroy();}
    }
}
