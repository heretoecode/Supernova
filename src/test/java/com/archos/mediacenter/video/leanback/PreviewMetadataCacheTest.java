package com.archos.mediacenter.video.leanback;

import android.app.Application;
import android.content.Context;
import org.json.JSONObject;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;
import java.io.IOException;
import java.util.concurrent.atomic.AtomicInteger;
import static org.junit.Assert.*;

@RunWith(RobolectricTestRunner.class)
@Config(application=Application.class,sdk=28)
public class PreviewMetadataCacheTest {
    @Test public void freshPackageAvoidsRepeatedFetch() throws Exception {
        Context context=RuntimeEnvironment.getApplication();AtomicInteger calls=new AtomicInteger();
        long id=System.nanoTime();
        JSONObject first=PreviewMetadataCache.load(context,"movie",id,"credits",()->{calls.incrementAndGet();return new JSONObject().put("cast_count",5);});
        JSONObject second=PreviewMetadataCache.load(context,"movie",id,"credits",()->{calls.incrementAndGet();throw new IOException("Must use fresh cache");});
        assertEquals(5,first.getInt("cast_count"));assertEquals(5,second.getInt("cast_count"));
        assertEquals(1,calls.get());assertTrue(PreviewMetadataCache.fresh(context,"movie",id,"credits"));
    }
    @Test public void failedFirstFetchDoesNotCreateCompletePackage() throws Exception {
        Context context=RuntimeEnvironment.getApplication();long id=System.nanoTime();
        try{PreviewMetadataCache.load(context,"tv",id,"images",()->{throw new IOException("Offline");});fail("Failure must reach retry queue");}catch(IOException expected){}
        assertFalse(PreviewMetadataCache.fresh(context,"tv",id,"images"));assertNull(PreviewMetadataCache.read(context,"tv",id,"images"));
    }
}
