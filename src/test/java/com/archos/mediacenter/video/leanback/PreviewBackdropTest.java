package com.archos.mediacenter.video.leanback;

import android.app.Application;
import android.graphics.Bitmap;
import com.squareup.picasso.Picasso;
import java.io.IOException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;
import org.robolectric.util.ReflectionHelpers;
import static org.junit.Assert.*;

@RunWith(RobolectricTestRunner.class) @Config(application=Application.class,sdk=28)
public class PreviewBackdropTest {
    @Test public void failedReplacementKeepsDisplayedArtwork(){
        PreviewBackdrop backdrop=new PreviewBackdrop(RuntimeEnvironment.getApplication());
        Bitmap image=Bitmap.createBitmap(160,90,Bitmap.Config.ARGB_8888);
        backdrop.onBitmapLoaded(image,Picasso.LoadedFrom.MEMORY);
        backdrop.onBitmapFailed(new IOException("Offline"),null);
        assertSame(image,ReflectionHelpers.getField(backdrop,"bitmap"));assertTrue(backdrop.readyForFirstFrame());
    }
    @Test public void rejectedPortraitKeepsDisplayedLandscape(){
        PreviewBackdrop backdrop=new PreviewBackdrop(RuntimeEnvironment.getApplication());
        Bitmap image=Bitmap.createBitmap(160,90,Bitmap.Config.ARGB_8888);
        backdrop.onBitmapLoaded(image,Picasso.LoadedFrom.MEMORY);
        backdrop.onBitmapLoaded(Bitmap.createBitmap(90,160,Bitmap.Config.ARGB_8888),Picasso.LoadedFrom.NETWORK);
        assertSame(image,ReflectionHelpers.getField(backdrop,"bitmap"));
    }
    @Test public void sameDecodedImageDoesNotStartAnotherCrossFade(){
        PreviewBackdrop backdrop=new PreviewBackdrop(RuntimeEnvironment.getApplication());
        Bitmap image=Bitmap.createBitmap(160,90,Bitmap.Config.ARGB_8888);
        backdrop.onBitmapLoaded(image,Picasso.LoadedFrom.MEMORY);backdrop.onBitmapLoaded(image,Picasso.LoadedFrom.MEMORY);
        assertNull(ReflectionHelpers.getField(backdrop,"previous"));
    }
}
