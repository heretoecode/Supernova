package com.archos.mediacenter.video.leanback;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;
import org.robolectric.annotation.GraphicsMode;
import static org.junit.Assert.*;

@RunWith(RobolectricTestRunner.class) @Config(application=android.app.Application.class,sdk=28)
public class PreviewNavigationShadeTest {
    @Test @GraphicsMode(GraphicsMode.Mode.NATIVE)
    @Config(qualifiers="w960dp-h540dp-land-mdpi")
    public void scrollingViewportExtendsBehindNavigationAndShadeSamplesItsPixels()throws Exception{
        android.app.Activity host=org.robolectric.Robolectric.buildActivity(android.app.Activity.class).setup().get();
        android.widget.ScrollView scroll=new android.widget.ScrollView(host);android.view.View red=new android.view.View(host);red.setBackgroundColor(android.graphics.Color.RED);
        scroll.addView(red,new android.widget.ScrollView.LayoutParams(-1,1200));
        TopNavigation nav=new TopNavigation(host,scroll,index->{},()->scroll.getScrollY()==0);host.setContentView(nav);
        nav.measure(android.view.View.MeasureSpec.makeMeasureSpec(960,1073741824),android.view.View.MeasureSpec.makeMeasureSpec(540,1073741824));nav.layout(0,0,960,540);
        assertEquals(540,scroll.getHeight());assertEquals(52,scroll.getPaddingTop());assertEquals(0,((android.view.View)scroll.getParent()).getTop());
        nav.setScrolled(true);org.robolectric.Shadows.shadowOf(android.os.Looper.getMainLooper()).idleFor(java.time.Duration.ofMillis(220));
        // Finish the Activity's posted initial layout before positioning this scroll fixture.
        nav.measure(android.view.View.MeasureSpec.makeMeasureSpec(960,1073741824),android.view.View.MeasureSpec.makeMeasureSpec(540,1073741824));nav.layout(0,0,960,540);
        scroll.scrollTo(0,160);assertEquals(160,scroll.getScrollY());assertEquals(0,((android.view.View)scroll.getParent()).getTop());
        android.graphics.Bitmap image=android.graphics.Bitmap.createBitmap(960,540,android.graphics.Bitmap.Config.ARGB_8888);nav.draw(new android.graphics.Canvas(image));
        org.robolectric.Shadows.shadowOf(android.os.Looper.getMainLooper()).idleFor(java.time.Duration.ofMillis(120));
        scroll.scrollTo(0,160);nav.draw(new android.graphics.Canvas(image));
        java.io.File png=new java.io.File("build/reports/preview-ui/navigation-scroll.png");png.getParentFile().mkdirs();try(java.io.FileOutputStream stream=new java.io.FileOutputStream(png)){image.compress(android.graphics.Bitmap.CompressFormat.PNG,100,stream);}
        // At the blank left margin: scrolled red content survives, darkened behind the nav.
        int covered=image.getPixel(4,20),clear=image.getPixel(4,140);
        assertTrue("Covered pixel="+Integer.toHexString(covered)+", clear="+Integer.toHexString(clear),android.graphics.Color.red(covered)>android.graphics.Color.blue(covered));
        assertTrue(android.graphics.Color.red(covered)<android.graphics.Color.red(clear));
        assertEquals(android.graphics.Color.RED,clear);image.recycle();host.finish();
    }
    @Test @GraphicsMode(GraphicsMode.Mode.NATIVE) public void navigationShadeFadesToTransparentWithoutASeparator(){
        android.graphics.Bitmap image=android.graphics.Bitmap.createBitmap(240,160,android.graphics.Bitmap.Config.ARGB_8888);
        android.graphics.drawable.ColorDrawable source=new android.graphics.drawable.ColorDrawable(0xff355878);source.setBounds(0,0,240,160);
        PreviewNavigationShade shade=new PreviewNavigationShade();shade.draw(new android.graphics.Canvas(image),source,240,116,255);
        assertTrue(android.graphics.Color.alpha(image.getPixel(120,5))>android.graphics.Color.alpha(image.getPixel(120,100)));
        assertEquals(0,android.graphics.Color.alpha(image.getPixel(120,120)));
        shade.release();image.recycle();
    }
    @Test public void uniformBackgroundStaysUniform(){
        int[] pixels=new int[35];java.util.Arrays.fill(pixels,0xff173956);
        PreviewNavigationShade.blur(pixels,7,5);for(int colour:pixels)assertEquals(0xff173956,colour);
    }
    @Test public void isolatedDetailSoftensAcrossBothAxesWithoutChangingAlpha(){
        int[] pixels=new int[49];java.util.Arrays.fill(pixels,0xff000000);pixels[24]=0xffffffff;
        PreviewNavigationShade.blur(pixels,7,7);
        assertTrue((pixels[24]&255)>0);assertTrue((pixels[24]&255)<255);
        assertTrue((pixels[23]&255)>0);assertTrue((pixels[17]&255)>0);
        for(int colour:pixels)assertEquals(255,colour>>>24);
    }
}
