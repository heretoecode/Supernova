package com.archos.mediacenter.video.foundation;

import android.app.Application;
import org.junit.*;
import org.junit.runner.RunWith;
import org.robolectric.*;
import org.robolectric.annotation.Config;
import java.util.*;
import static org.junit.Assert.*;

@RunWith(RobolectricTestRunner.class) @Config(application=Application.class,sdk=30,qualifiers="w960dp-h540dp-land-mdpi")
public class BundledNoticePagerTest {
    @Test public void completeActualNoticesAreLosslessAndEveryTextLayoutIsBounded()throws Exception{
        for(String asset:new String[]{"foundation/legal/androidx.txt","foundation/legal/android-ndk-libc.txt"}){
            String notice;try(java.io.InputStream in=RuntimeEnvironment.getApplication().getAssets().open(asset)){notice=new String(in.readAllBytes(),java.nio.charset.StandardCharsets.UTF_8);}
            List<String> pages=BundledNoticeReader.blocks(notice);assertEquals(notice,String.join("",pages));assertTrue(pages.size()>1);for(String page:pages)assertTrue(page.length()<=BundledNoticeReader.BLOCK_CHARS);
        }
    }
    @Test public void pageBoundariesCannotSplitUnicodeSurrogatePairs(){String body="a".repeat(3999)+"😀"+"z".repeat(4001);List<String> pages=BundledNoticeReader.blocks(body);assertEquals(body,String.join("",pages));for(String page:pages)assertFalse(Character.isHighSurrogate(page.charAt(page.length()-1)));}
    @Test public void continuousReaderHasNoPaginationAndReturnsToCatalogue(){
        var host=Robolectric.buildActivity(android.app.Activity.class).setup().visible();
        try{int[] returned={0};BundledNoticeReader reader=new BundledNoticeReader(host.get(),"Notice\n".repeat(3000),()->returned[0]++);host.get().setContentView(reader);
            reader.measure(android.view.View.MeasureSpec.makeMeasureSpec(400,android.view.View.MeasureSpec.EXACTLY),android.view.View.MeasureSpec.makeMeasureSpec(440,android.view.View.MeasureSpec.EXACTLY));reader.layout(0,0,400,440);
            assertEquals(2,reader.getChildCount());assertTrue(reader.getChildAt(1) instanceof androidx.recyclerview.widget.RecyclerView);
            var list=(androidx.recyclerview.widget.RecyclerView)reader.getChildAt(1);assertTrue(list.getAdapter().getItemCount()>1);reader.focusReader();assertTrue(list.hasFocus());
            list.dispatchKeyEvent(new android.view.KeyEvent(android.view.KeyEvent.ACTION_DOWN,android.view.KeyEvent.KEYCODE_DPAD_LEFT));assertEquals(1,returned[0]);
        }finally{host.pause().stop().destroy();}
    }
}
