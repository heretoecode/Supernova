package com.archos.mediacenter.video.leanback;

import android.app.*;
import android.view.*;
import com.archos.mediascraper.ScraperImage;
import com.squareup.picasso.Picasso;
import org.junit.*;
import org.junit.runner.RunWith;
import org.robolectric.*;
import org.robolectric.annotation.Config;
import java.util.*;
import java.util.concurrent.atomic.*;
import java.util.function.Consumer;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith(RobolectricTestRunner.class) @Config(application=Application.class,sdk=28,qualifiers="w960dp-h540dp-land-mdpi")
public class PreviewArtworkPickerTest {
    @Before public void images(){try{Picasso.get();}catch(IllegalStateException e){Picasso.setSingletonInstance(new Picasso.Builder(RuntimeEnvironment.getApplication()).build());}}
    @Test public void failedSaveKeepsOldCheckAndSuccessfulSaveKeepsGridOpen(){
        org.robolectric.android.controller.ActivityController<Activity> host=Robolectric.buildActivity(Activity.class).setup().visible();
        try{
            ScraperImage first=mock(ScraperImage.class),second=mock(ScraperImage.class);
            AtomicReference<Consumer<Boolean>> completion=new AtomicReference<>();AtomicInteger writes=new AtomicInteger();
            Dialog dialog=PreviewArtworkPicker.show(host.get(),"Posters",Arrays.asList(first,second),first,true,(image,done)->{writes.incrementAndGet();completion.set(done);});
            View root=dialog.getWindow().getDecorView(),a=root.findViewWithTag("artwork-check:0"),b=root.findViewWithTag("artwork-check:1");
            View target=root.findViewWithTag("artwork:1");target.requestFocus();target.performClick();target.performClick();assertEquals(1,writes.get());
            completion.get().accept(false);Shadows.shadowOf(android.os.Looper.getMainLooper()).idle();
            assertEquals(View.VISIBLE,a.getVisibility());assertEquals(View.INVISIBLE,b.getVisibility());assertTrue(dialog.isShowing());
            target.performClick();completion.get().accept(true);Shadows.shadowOf(android.os.Looper.getMainLooper()).idle();
            assertEquals(View.INVISIBLE,a.getVisibility());assertEquals(View.VISIBLE,b.getVisibility());assertTrue(dialog.isShowing());assertSame(target,dialog.getCurrentFocus());
            dialog.dismiss();
        }finally{host.pause().stop().destroy();}
    }
    @Test public void rowEdgesAndIncompleteLastRowDoNotEscapeGrid(){
        org.robolectric.android.controller.ActivityController<Activity> host=Robolectric.buildActivity(Activity.class).setup().visible();
        try{
            List<ScraperImage> images=new ArrayList<>();for(int i=0;i<4;i++)images.add(mock(ScraperImage.class));
            Dialog dialog=PreviewArtworkPicker.show(host.get(),"Backdrops",images,images.get(0),false,(image,done)->{});
            View root=dialog.getWindow().getDecorView(),first=root.findViewWithTag("artwork:0"),last=root.findViewWithTag("artwork:3");
            first.requestFocus();first.dispatchKeyEvent(new KeyEvent(KeyEvent.ACTION_DOWN,KeyEvent.KEYCODE_DPAD_LEFT));assertSame(first,dialog.getCurrentFocus());
            first.dispatchKeyEvent(new KeyEvent(KeyEvent.ACTION_DOWN,KeyEvent.KEYCODE_DPAD_DOWN));assertSame(last,dialog.getCurrentFocus());
            last.dispatchKeyEvent(new KeyEvent(KeyEvent.ACTION_DOWN,KeyEvent.KEYCODE_DPAD_RIGHT));assertSame(last,dialog.getCurrentFocus());dialog.dismiss();
        }finally{host.pause().stop().destroy();}
    }
}
