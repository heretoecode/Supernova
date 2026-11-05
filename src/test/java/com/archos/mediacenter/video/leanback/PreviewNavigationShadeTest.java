package com.archos.mediacenter.video.leanback;

import android.app.Activity;
import android.app.Application;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.os.Looper;
import android.view.View;
import android.widget.ScrollView;
import java.io.File;
import java.io.FileOutputStream;
import java.time.Duration;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.Shadows;
import org.robolectric.annotation.Config;
import org.robolectric.annotation.GraphicsMode;
import static org.junit.Assert.*;

/** Checks navigation shading samples scrolled content and fades without changing background transparency. */
@RunWith(RobolectricTestRunner.class) @Config(application=Application.class,sdk=28)
public class PreviewNavigationShadeTest {
    @Test @GraphicsMode(GraphicsMode.Mode.NATIVE)
    @Config(qualifiers="w960dp-h540dp-land-mdpi")
    public void scrollingViewportExtendsBehindNavigationAndShadeSamplesItsPixels()throws Exception{
        Activity host=Robolectric.buildActivity(Activity.class).setup().get();
        ScrollView scroll=new ScrollView(host);View red=new View(host);red.setBackgroundColor(Color.RED);red.setMinimumHeight(1200);
        scroll.addView(red,new ScrollView.LayoutParams(-1,1200));
        TopNavigation nav=new TopNavigation(host,scroll,index->{},()->scroll.getScrollY()==0);host.setContentView(nav);
        nav.measure(View.MeasureSpec.makeMeasureSpec(960,1073741824),View.MeasureSpec.makeMeasureSpec(540,1073741824));nav.layout(0,0,960,540);
        assertEquals(540,scroll.getHeight());assertEquals(52,scroll.getPaddingTop());assertEquals(0,((View)scroll.getParent()).getTop());
        nav.setScrolled(true);Shadows.shadowOf(Looper.getMainLooper()).idleFor(Duration.ofMillis(220));
        // Finish the Activity's posted initial layout before positioning this scroll fixture.
        nav.measure(View.MeasureSpec.makeMeasureSpec(960,1073741824),View.MeasureSpec.makeMeasureSpec(540,1073741824));nav.layout(0,0,960,540);
        scroll.scrollTo(0,160);assertEquals(160,scroll.getScrollY());assertEquals(0,((View)scroll.getParent()).getTop());
        Bitmap image=Bitmap.createBitmap(960,540,Bitmap.Config.ARGB_8888);nav.draw(new Canvas(image));
        Shadows.shadowOf(Looper.getMainLooper()).idleFor(Duration.ofMillis(120));
        scroll.scrollTo(0,160);nav.draw(new Canvas(image));
        File png=new File("build/reports/preview-ui/navigation-scroll.png");png.getParentFile().mkdirs();try(FileOutputStream stream=new FileOutputStream(png)){image.compress(Bitmap.CompressFormat.PNG,100,stream);}
        // At the blank left margin: scrolled red content survives, darkened behind the nav.
        int covered=image.getPixel(4,20),clear=image.getPixel(4,140);
        assertTrue("Covered pixel="+Integer.toHexString(covered)+", clear="+Integer.toHexString(clear),Color.red(covered)>Color.blue(covered));
        assertTrue(Color.red(covered)<Color.red(clear));
        assertEquals(Color.RED,clear);image.recycle();host.finish();
    }
    /** Owner-approved 4 Nov 2026 (top-bar/01–04): nothing at the top, charcoal (never blue) once scrolled. */
    @Test @GraphicsMode(GraphicsMode.Mode.NATIVE)
    @Config(qualifiers="w960dp-h540dp-land-mdpi")
    public void fadeFollowsTheRealScrollAndIsCharcoalNotBlue() {
        Activity host = Robolectric.buildActivity(Activity.class).setup().get();
        ScrollView scroll = new ScrollView(host);
        TopNavigation nav = new TopNavigation(host, scroll, index -> {}, () -> true);
        host.setContentView(nav);
        nav.selectTab(1);
        nav.setScrollOffset(0);
        assertEquals("nothing at all at the top", 0, nav.fadeAlpha());
        nav.setScrollOffset(10);
        int partial = nav.fadeAlpha();
        assertTrue(partial > 0 && partial < 255);
        nav.setScrollOffset(10_000);
        assertEquals(255, nav.fadeAlpha());
        nav.setScrollOffset(0);
        assertEquals("returning to the top clears it", 0, nav.fadeAlpha());
        nav.selectTab(4);
        nav.setScrollOffset(10_000);
        assertEquals("utility pages have no fade", 0, nav.fadeAlpha());
        assertEquals(0x16, TopNavigation.FADE_COLOUR >> 16 & 255);
        assertTrue("no blue cast", (TopNavigation.FADE_COLOUR & 255) - (TopNavigation.FADE_COLOUR >> 16 & 255) < 8);
        host.finish();
    }
}
