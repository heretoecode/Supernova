package com.archos.mediacenter.video.leanback;

import android.app.Application;
import android.view.*;
import android.widget.TextView;
import androidx.recyclerview.widget.*;
import com.archos.mediacenter.video.browser.adapters.object.*;
import com.archos.mediacenter.video.leanback.PreviewLibraryLoader.*;
import org.junit.*;
import org.junit.runner.RunWith;
import org.robolectric.*;
import org.robolectric.annotation.*;
import static org.junit.Assert.*;

@RunWith(RobolectricTestRunner.class)
@Config(application=Application.class,sdk=28,qualifiers="w960dp-h540dp-land-mdpi")
@GraphicsMode(GraphicsMode.Mode.NATIVE)
public class Supernova139LibraryNavigationTest {
    @Before public void offline(){androidx.preference.PreferenceManager.getDefaultSharedPreferences(RuntimeEnvironment.getApplication()).edit().putBoolean("supernova_onboarding_complete",true).commit();PreviewAsyncFixtures.offlineTransport();try{com.squareup.picasso.Picasso.get();}catch(IllegalStateException absent){com.squareup.picasso.Picasso.setSingletonInstance(new com.squareup.picasso.Picasso.Builder(RuntimeEnvironment.getApplication()).build());}}
    @After public void drain() throws Exception {PreviewAsyncFixtures.drain();}
    private Snapshot library(){Snapshot s=new Snapshot();for(int id=1;id<=40;id++){Movie m=new Movie(id,"/fixture/"+id,"Film "+id,id,"Synopsis",2024,7,"12",null,100000,0,0,0,false,false,false,false,id,id,1920,1080,null,null,null,null,0,1,1000,0);s.movies.add(new Entry(m,id,0,"Drama"));}s.shows.add(new Entry(new Tvshow(100,"Series",null,1,4,0,"/fixture/tv"),100,100,"Drama"));return s;}
    private void key(View root,int key){assertTrue(root.dispatchKeyEvent(new KeyEvent(KeyEvent.ACTION_DOWN,key)));root.dispatchKeyEvent(new KeyEvent(KeyEvent.ACTION_UP,key));for(int i=0;i<6;i++)PreviewPagesTest.layout(root);}
    @Test public void rootRoutesMoviesTvAndCustomThroughLeftmostControlAndFirstDisplayedPoster() throws Exception {
        var host=Robolectric.buildActivity(TopNavigationTest.Host.class).setup().visible();
        try{for(int mode=0;mode<3;mode++){
            PreviewPages pages=new PreviewPages(host.get(),(h,item)->{});pages.setDiscovery(new PreviewDiscovery());pages.setSnapshot(library());
            int tab=mode==1?2:1;
            if(mode==2){CustomLibraryPage custom=new CustomLibraryPage();custom.name="My films";custom.type="movies";pages.configureCustomLibrary(custom,()->{});}
            TopNavigation nav=new TopNavigation(host.get(),pages,pages::setTab,pages::atTop);host.get().setContentView(nav);pages.setTab(tab);nav.selectTab(mode==2?6:tab);for(int i=0;i<6;i++)PreviewPagesTest.layout(nav);
            String first=mode==2?"custom.control.filters":"control:0";
            View selected=nav.findViewWithTag("semantic:topnav."+(mode==2?"custom":mode==1?"tv":"movies"));assertTrue(selected.requestFocus());
            key(nav,KeyEvent.KEYCODE_DPAD_DOWN);assertEquals(first,nav.findFocus().getTag());
            key(nav,KeyEvent.KEYCODE_DPAD_LEFT);assertEquals(first,nav.findFocus().getTag());
            key(nav,KeyEvent.KEYCODE_DPAD_RIGHT);assertNotEquals(first,nav.findFocus().getTag());
            key(nav,KeyEvent.KEYCODE_DPAD_DOWN);assertEquals(mode==1?"s100":"v40",nav.findFocus().getTag());
            key(nav,KeyEvent.KEYCODE_DPAD_UP);assertEquals(first,nav.findFocus().getTag());
            key(nav,KeyEvent.KEYCODE_DPAD_UP);assertSame(selected,nav.findFocus());
            key(nav,KeyEvent.KEYCODE_DPAD_DOWN);assertEquals(first,nav.findFocus().getTag());
            TextView control=nav.findViewWithTag(mode==2?"custom.control.order":"control:2");assertEquals(1,control.getLineCount());assertEquals(0,control.getLayout().getEllipsisCount(0));assertTrue(control.getHeight()>=44&&control.getHeight()<49);assertNotNull(control.getCompoundDrawables()[0]);
            PreviewPagesTest.capture(nav,"0139-library-"+mode);
        }}finally{host.pause().stop().destroy();}
    }
    @Test public void explicitNavigationEntryDoesNotReuseDeepPosterAnchor(){
        var host=Robolectric.buildActivity(TopNavigationTest.Host.class).setup().visible();
        try{PreviewPages pages=new PreviewPages(host.get(),(h,item)->{});TopNavigation nav=new TopNavigation(host.get(),pages,pages::setTab,pages::atTop);host.get().setContentView(nav);pages.setDiscovery(new PreviewDiscovery());pages.setSnapshot(library());pages.setTab(1);nav.selectTab(1);PreviewPagesTest.layout(nav);
            RecyclerView list=(RecyclerView)pages.getChildAt(0);GridLayoutManager layout=(GridLayoutManager)list.getLayoutManager();layout.scrollToPositionWithOffset(25,100);PreviewPagesTest.layout(nav);View item=layout.findViewByPosition(25);assertTrue(item.requestFocus());
            View selected=nav.findViewWithTag("semantic:topnav.movies");assertTrue(selected.requestFocus());key(nav,KeyEvent.KEYCODE_DPAD_DOWN);assertEquals("control:0",nav.findFocus().getTag());assertFalse(list.canScrollVertically(-1));
        }finally{host.pause().stop().destroy();}
    }
}
