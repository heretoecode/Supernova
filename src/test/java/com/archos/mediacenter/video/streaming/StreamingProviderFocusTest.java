package com.archos.mediacenter.video.streaming;

import android.app.Application;
import android.view.*;
import androidx.leanback.widget.ArrayObjectAdapter;
import com.archos.mediacenter.video.browser.adapters.object.Movie;
import com.archos.mediacenter.video.leanback.PreviewAsyncFixtures;
import com.archos.mediacenter.video.leanback.details.PreviewMoviePage;
import java.util.concurrent.atomic.AtomicInteger;
import org.junit.*;
import org.junit.runner.RunWith;
import org.robolectric.*;
import org.robolectric.annotation.Config;
import static org.junit.Assert.*;

@RunWith(RobolectricTestRunner.class) @Config(application=Application.class,sdk=28,qualifiers="w960dp-h540dp-land-mdpi")
public class StreamingProviderFocusTest {
    @Before public void setup(){PreviewAsyncFixtures.offlineTransport();try{com.squareup.picasso.Picasso.get();}catch(IllegalStateException absent){com.squareup.picasso.Picasso.setSingletonInstance(new com.squareup.picasso.Picasso.Builder(RuntimeEnvironment.getApplication()).build());}}
    @After public void drain()throws Exception{PreviewAsyncFixtures.drain();}
    @Test public void dpadVisitsProviderBetweenPlayAndMoreAndDispatchesExistingAction(){var host=Robolectric.buildActivity(android.app.Activity.class).setup().visible();try{AtomicInteger launched=new AtomicInteger();ArrayObjectAdapter actions=new ArrayObjectAdapter();actions.add(new StreamingActionPresenter.LogoAction(new StreamingRepository.Provider(9,"Amazon Prime Video"),launched::incrementAndGet));PreviewMoviePage page=new PreviewMoviePage(host.get(),()->actions,StreamingActions::onClick,()->{},uri->{});host.get().setContentView(page);Movie movie=new Movie(1,"/fixture/film","Film",0,"Fixture synopsis",2026,7,"",null,100000,0,0,0,false,false,false,false,1,1,1920,1080,"AAC",null,null,null,0,1,1000,0);page.bind(movie);for(int i=0;i<2;i++){page.measure(View.MeasureSpec.makeMeasureSpec(960,View.MeasureSpec.EXACTLY),View.MeasureSpec.makeMeasureSpec(540,View.MeasureSpec.EXACTLY));page.layout(0,0,960,540);}View play=page.findViewWithTag("action:Play"),more=page.findViewWithTag("action:More");assertTrue(play.requestFocus());page.dispatchKeyEvent(new KeyEvent(KeyEvent.ACTION_DOWN,KeyEvent.KEYCODE_DPAD_RIGHT));View provider=page.findFocus();assertEquals("Open Amazon Prime Video",provider.getContentDescription());assertEquals(play.getHeight(),provider.getHeight());provider.performClick();assertEquals(1,launched.get());page.dispatchKeyEvent(new KeyEvent(KeyEvent.ACTION_DOWN,KeyEvent.KEYCODE_DPAD_RIGHT));assertSame(more,page.findFocus());page.dispatchKeyEvent(new KeyEvent(KeyEvent.ACTION_DOWN,KeyEvent.KEYCODE_DPAD_LEFT));assertSame(provider,page.findFocus());page.dispatchKeyEvent(new KeyEvent(KeyEvent.ACTION_DOWN,KeyEvent.KEYCODE_DPAD_LEFT));assertSame(play,page.findFocus());}finally{host.pause().stop().destroy();}}
}
