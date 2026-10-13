package com.archos.mediacenter.video.streaming;

import android.app.Application;
import android.graphics.*;
import android.widget.ImageView;
import java.io.*;
import org.junit.*;
import org.junit.runner.RunWith;
import org.robolectric.*;
import org.robolectric.annotation.*;
import static org.junit.Assert.*;

@RunWith(RobolectricTestRunner.class) @Config(application=Application.class,sdk=28)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
public class Supernova139ProviderMarksTest {
    @Before public void images(){try{com.squareup.picasso.Picasso.get();}catch(IllegalStateException absent){com.squareup.picasso.Picasso.setSingletonInstance(new com.squareup.picasso.Picasso.Builder(RuntimeEnvironment.getApplication()).build());}}
    @Test public void authenticBundledMarksStayWhiteTransparentAndIdenticalAcrossConsumers()throws Exception{
        Application context=RuntimeEnvironment.getApplication();String[] names={"Netflix","Apple TV+","Paramount+","HBO","HBO Max","MUBI","Crunchyroll"};Bitmap sheet=Bitmap.createBitmap(7*96,96,Bitmap.Config.ARGB_8888);Canvas canvas=new Canvas(sheet);canvas.drawColor(0xff14212c);
        for(int i=0;i<names.length;i++){assertNotEquals(0,PreviewProviderIcons.resource(names[i]));ImageView first=new ImageView(context),second=new ImageView(context);StreamingRepository.Provider provider=new StreamingRepository.Provider(i+1,names[i]);PreviewProviderIcons.bind(first,provider,123,"provider.choice");PreviewProviderIcons.bind(second,provider,123,"details.actions");assertNull(first.getColorFilter());assertNull(first.getBackground());assertEquals(ImageView.ScaleType.FIT_CENTER,first.getScaleType());
            Bitmap mark=Bitmap.createBitmap(72,72,Bitmap.Config.ARGB_8888),other=Bitmap.createBitmap(72,72,Bitmap.Config.ARGB_8888);first.getDrawable().setBounds(0,0,72,72);first.getDrawable().draw(new Canvas(mark));second.getDrawable().setBounds(0,0,72,72);second.getDrawable().draw(new Canvas(other));assertTrue(mark.sameAs(other));int opaque=0,transparent=0;
            for(int y=0;y<72;y++)for(int x=0;x<72;x++){int color=mark.getPixel(x,y);if(Color.alpha(color)==0)transparent++;else if(Color.alpha(color)>=128){opaque++;assertEquals(255,Color.red(color));assertEquals(255,Color.green(color));assertEquals(255,Color.blue(color));}}
            assertTrue(names[i]+" must contain brand strokes",opaque>50);assertTrue(names[i]+" must not be an opaque logo tile",transparent>1000);canvas.drawBitmap(mark,i*96+12,12,null);
        }
        File image=new File("build/reports/preview-ui/0139-white-provider-marks.png");image.getParentFile().mkdirs();try(FileOutputStream output=new FileOutputStream(image)){sheet.compress(Bitmap.CompressFormat.PNG,100,output);}
        assertEquals(PreviewProviderIcons.resource("Netflix"),PreviewProviderIcons.resource("Netflix Standard with Ads"));
    }
    @Test public void unverifiedBrandOrArbitraryPathUsesSemanticFallbackWithoutLoadingOpaqueCatalogueArt(){
        Application context=RuntimeEnvironment.getApplication();ImageView icon=new ImageView(context);assertEquals(0,PreviewProviderIcons.resource("Disney+"));assertEquals(0,PreviewProviderIcons.resource("Unverified service"));
        PreviewProviderIcons.bind(icon,"/opaque-square.png",1,"provider.choice");assertTrue(icon.getDrawable() instanceof com.archos.mediacenter.video.leanback.PreviewIcon);assertNull(icon.getBackground());assertNull(icon.getColorFilter());
    }
}
