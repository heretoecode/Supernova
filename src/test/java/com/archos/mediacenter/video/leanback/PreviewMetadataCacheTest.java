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
import java.util.concurrent.*;
import static org.junit.Assert.*;

@RunWith(RobolectricTestRunner.class)
@Config(application=Application.class,sdk=28)
public class PreviewMetadataCacheTest {
    @Test public void cacheReadDoesNotWaitForNetworkFetch() throws Exception {
        Context context=RuntimeEnvironment.getApplication();long id=System.nanoTime();
        ExecutorService workers=Executors.newFixedThreadPool(2);
        CountDownLatch fetching=new CountDownLatch(1),release=new CountDownLatch(1);
        try{
            Future<JSONObject> request=workers.submit(()->PreviewMetadataCache.load(context,"tv",id,"credits",()->{
                fetching.countDown();if(!release.await(5,TimeUnit.SECONDS))throw new IOException("Test release timed out");
                return new JSONObject().put("cast_count",3);
            }));
            assertTrue(fetching.await(2,TimeUnit.SECONDS));
            Future<JSONObject> read=workers.submit(()->PreviewMetadataCache.read(context,"tv",id,"credits"));
            assertNull(read.get(1,TimeUnit.SECONDS));
            release.countDown();assertEquals(3,request.get(2,TimeUnit.SECONDS).getInt("cast_count"));
            assertTrue(PreviewMetadataCache.fresh(context,"tv",id,"credits"));
        }finally{release.countDown();workers.shutdownNow();}
    }
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
