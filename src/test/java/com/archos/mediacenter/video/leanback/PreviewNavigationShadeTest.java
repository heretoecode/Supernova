package com.archos.mediacenter.video.leanback;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;
import org.robolectric.annotation.GraphicsMode;
import static org.junit.Assert.*;

@RunWith(RobolectricTestRunner.class) @Config(application=android.app.Application.class,sdk=28)
public class PreviewNavigationShadeTest {
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
