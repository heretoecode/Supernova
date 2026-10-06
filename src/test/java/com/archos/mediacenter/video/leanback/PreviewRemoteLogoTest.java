package com.archos.mediacenter.video.leanback;

import android.app.Application;
import android.graphics.Bitmap;
import android.widget.TextView;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.*;
import org.robolectric.annotation.Config;
import org.robolectric.util.ReflectionHelpers;
import static org.junit.Assert.*;

@RunWith(RobolectricTestRunner.class) @Config(application=Application.class,sdk=28)
public class PreviewRemoteLogoTest {
    @Test public void remoteTitleConsumesTheSameLogoCacheAndKeepsFallbackText(){
        android.util.LruCache<String,Bitmap> aliases=ReflectionHelpers.getStaticField(OfficialTitleArtwork.class,"ALIASES");
        String key="movie/42:"+java.util.Locale.getDefault().getLanguage();aliases.put(key,Bitmap.createBitmap(80,30,Bitmap.Config.ARGB_8888));
        TextView view=new TextView(RuntimeEnvironment.getApplication());view.setText("Title");
        try{OfficialTitleArtwork.bindRemote(view,"movie",42);assertNotNull(view.getForeground());assertEquals("Title",view.getText().toString());assertTrue(OfficialTitleArtwork.readyForFirstFrame(view));
            OfficialTitleArtwork.bindRemote(view,"invalid",42);assertNull(view.getForeground());assertEquals(0xffffffff,view.getCurrentTextColor());
        }finally{aliases.remove(key);}
    }
}
