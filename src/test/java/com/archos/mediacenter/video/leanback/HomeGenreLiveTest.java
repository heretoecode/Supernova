package com.archos.mediacenter.video.leanback;

import android.app.*;
import android.content.Intent;
import android.net.Uri;
import android.view.*;
import android.widget.*;
import com.archos.mediaprovider.ArchosMediaIntent;
import com.archos.mediaprovider.ImportState;
import com.archos.mediaprovider.video.NetworkScannerReceiver;
import com.archos.mediacenter.video.leanback.PreviewLibraryLoader.*;
import com.archos.mediacenter.video.browser.adapters.object.Movie;
import java.time.Duration;
import java.util.*;
import org.junit.*;
import org.junit.runner.RunWith;
import org.robolectric.*;
import org.robolectric.annotation.*;
import org.robolectric.shadows.ShadowDialog;
import org.robolectric.util.ReflectionHelpers;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith(RobolectricTestRunner.class) @Config(application=Application.class,sdk=28,qualifiers="w960dp-h540dp-land-mdpi") @GraphicsMode(GraphicsMode.Mode.NATIVE)
public class HomeGenreLiveTest {
    private Entry movie(long id,String genres){Movie video=mock(Movie.class);when(video.getId()).thenReturn(id);return new Entry(video,id,0,genres);}
    private void layout(Dialog dialog){View root=dialog.getWindow().getDecorView();var size=dialog.getWindow().getAttributes();root.measure(View.MeasureSpec.makeMeasureSpec(size.width,View.MeasureSpec.EXACTLY),View.MeasureSpec.makeMeasureSpec(size.height,View.MeasureSpec.EXACTLY));root.layout(0,0,size.width,size.height);root.dispatchWindowFocusChanged(true);}
    private void advance(){Shadows.shadowOf(android.os.Looper.getMainLooper()).idleFor(Duration.ofMillis(1100));}
    private ScrollView viewport(View view){while(!(view instanceof ScrollView))view=(View)view.getParent();return (ScrollView)view;}
    private void scanner(android.content.Context app,String action){new NetworkScannerReceiver().onReceive(app,new Intent(action,Uri.parse("file:///synthetic-indexed-root")));}
    @Test public void liveSnapshotChangesCountsWithoutChangingSelectedTargetOrderOrScrollAndAllowsZeroCounts()throws Exception {
        var host=Robolectric.buildActivity(TopNavigationTest.Host.class).setup().visible();Snapshot previous=PreviewLibraryLoader.cached;Dialog dialog=null;
        try{
            ImportState.VIDEO.setState(ImportState.State.IDLE);Snapshot initial=new Snapshot();initial.movies.add(movie(1,"Drama|Unknown fixture genre"));PreviewLibraryLoader.cached=initial;
            var row=new PreviewHomeRows.Row("custom:live","Live counts");List<Set<String>> selected=new ArrayList<>();HomeGenrePicker.show(host.get(),row,value->selected.add(value));dialog=ShadowDialog.getLatestDialog();layout(dialog);View root=dialog.getWindow().getDecorView();
            View drama=root.findViewWithTag("home.genre:Drama"),unknown=root.findViewWithTag("home.genre:Unknown fixture genre"),zero=root.findViewWithTag("home.genre:Western");assertNotNull(unknown);assertTrue(unknown.getContentDescription().toString().contains("1 indexed titles"));assertTrue(zero.getContentDescription().toString().contains("0 indexed titles"));zero.performClick();assertEquals(Set.of("Western"),selected.get(0));assertTrue(zero.isSelected());
            unknown.requestFocus();advance();layout(dialog);ScrollView viewport=viewport(unknown);int scroll=viewport.getScrollY();ViewGroup options=(ViewGroup)unknown.getParent();List<View> order=new ArrayList<>();for(int i=0;i<options.getChildCount();i++)order.add(options.getChildAt(i));
            Snapshot changed=new Snapshot();changed.movies.add(movie(1,"Drama|Unknown fixture genre"));changed.movies.add(movie(2,"Drama"));PreviewLibraryLoader.cached=changed;advance();layout(dialog);
            assertSame(unknown,dialog.getCurrentFocus());assertEquals(scroll,viewport.getScrollY());for(int i=0;i<order.size();i++)assertSame(order.get(i),options.getChildAt(i));assertTrue(drama.getContentDescription().toString().contains("2 indexed titles"));assertTrue(zero.isSelected());
            PreviewLibraryLoader.cached=null;advance();assertTrue(drama.getContentDescription().toString().contains("not yet indexed"));assertTrue(zero.isSelected());assertNull(PreviewPagesTest.findText(root,"Done"));PreviewPagesTest.capture(root,"0139-genres-live-unknown-focus");
        }finally{if(dialog!=null)dialog.dismiss();PreviewLibraryLoader.cached=previous;host.pause().stop().destroy();}
    }
    @Test public void realScannerBroadcastAndReducedMotionControlOnlyActualIndicatorAndDismissStopsIt()throws Exception {
        var host=Robolectric.buildActivity(TopNavigationTest.Host.class).setup().visible();Dialog dialog=null;
        try{
            ImportState.VIDEO.setState(ImportState.State.IDLE);scanner(host.get(),ArchosMediaIntent.ACTION_VIDEO_SCANNER_SCAN_FINISHED);HomeGenrePicker.show(host.get(),new PreviewHomeRows.Row("custom:scan","Scan"),value->{});dialog=ShadowDialog.getLatestDialog();layout(dialog);
            TextView label=(TextView)PreviewPagesTest.findText(dialog.getWindow().getDecorView(),"Scanning library…");LinearLayout indicator=(LinearLayout)label.getParent();android.graphics.drawable.Animatable spinner=(android.graphics.drawable.Animatable)((ImageView)indicator.getChildAt(0)).getDrawable();assertEquals(View.GONE,indicator.getVisibility());assertFalse(spinner.isRunning());
            scanner(host.get(),ArchosMediaIntent.ACTION_VIDEO_SCANNER_SCAN_STARTED);advance();assertEquals(View.VISIBLE,indicator.getVisibility());assertTrue(spinner.isRunning());
            ReflectionHelpers.callStaticMethod(android.animation.ValueAnimator.class,"setDurationScale",ReflectionHelpers.ClassParameter.from(float.class,0f));advance();assertEquals(View.VISIBLE,indicator.getVisibility());assertFalse(spinner.isRunning());
            scanner(host.get(),ArchosMediaIntent.ACTION_VIDEO_SCANNER_SCAN_FINISHED);advance();assertEquals(View.GONE,indicator.getVisibility());assertFalse(spinner.isRunning());
            dialog.dismiss();advance();assertFalse(spinner.isRunning());
        }finally{ReflectionHelpers.callStaticMethod(android.animation.ValueAnimator.class,"setDurationScale",ReflectionHelpers.ClassParameter.from(float.class,1f));scanner(host.get(),ArchosMediaIntent.ACTION_VIDEO_SCANNER_SCAN_FINISHED);if(dialog!=null)dialog.dismiss();host.pause().stop().destroy();}
    }
}
