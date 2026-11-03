package com.archos.mediacenter.video.leanback;

import android.app.Activity;
import android.app.Application;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Looper;
import android.view.View;
import android.widget.ScrollView;
import java.io.File;
import java.io.FileOutputStream;
import java.time.Duration;
import java.util.Arrays;
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
    @Test @GraphicsMode(GraphicsMode.Mode.NATIVE) public void navigationShadeFadesToTransparentWithoutASeparator(){
        Bitmap image=Bitmap.createBitmap(240,160,Bitmap.Config.ARGB_8888);
        ColorDrawable source=new ColorDrawable(0xff355878);source.setBounds(0,0,240,160);
        PreviewNavigationShade shade=new PreviewNavigationShade();shade.draw(new Canvas(image),source,240,116,255);
        assertTrue(Color.alpha(image.getPixel(120,5))>Color.alpha(image.getPixel(120,100)));
        assertEquals(0,Color.alpha(image.getPixel(120,120)));
        shade.release();image.recycle();
    }
    @Test public void uniformBackgroundStaysUniform(){
        int[] pixels=new int[35];Arrays.fill(pixels,0xff173956);
        PreviewNavigationShade.blur(pixels,7,5);for(int colour:pixels)assertEquals(0xff173956,colour);
    }
    @Test public void isolatedDetailSoftensAcrossBothAxesWithoutChangingAlpha(){
        int[] pixels=new int[49];Arrays.fill(pixels,0xff000000);pixels[24]=0xffffffff;
        PreviewNavigationShade.blur(pixels,7,7);
        assertTrue((pixels[24]&255)>0);assertTrue((pixels[24]&255)<255);
        assertTrue((pixels[23]&255)>0);assertTrue((pixels[17]&255)>0);
        for(int colour:pixels)assertEquals(255,colour>>>24);
    }
}
