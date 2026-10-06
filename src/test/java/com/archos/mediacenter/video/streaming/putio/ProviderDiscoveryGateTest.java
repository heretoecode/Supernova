package com.archos.mediacenter.video.streaming.putio;
import android.net.Uri;
import android.content.Context;
import com.archos.mediaprovider.video.ProviderDiscoveryGate;
import org.junit.*;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;
import static org.junit.Assert.*;
@RunWith(RobolectricTestRunner.class) @Config(application=android.app.Application.class,sdk=28)
public class ProviderDiscoveryGateTest {
    private Context context;
    @Before public void reset(){context=RuntimeEnvironment.getApplication();context.getSharedPreferences("provider-discovery-ownership-v1",0).edit().clear().commit();}
    @Test public void ownershipCoversDescendantsNotSiblingPrefixesOrOtherEndpoints(){
        ProviderDiscoveryGate.exclude(context,"putio:10:20",Uri.parse("https://webdav.put.io/Films"));
        assertTrue(ProviderDiscoveryGate.excludes(context,Uri.parse("webdavs://webdav.put.io:443/Films/a.mkv")));
        assertFalse(ProviderDiscoveryGate.excludes(context,Uri.parse("https://webdav.put.io/Films2/a.mkv")));
        assertFalse(ProviderDiscoveryGate.excludes(context,Uri.parse("http://webdav.put.io/Films/a.mkv")));
        assertFalse(ProviderDiscoveryGate.excludes(context,Uri.parse("https://other.example/Films/a.mkv")));
        assertFalse(ProviderDiscoveryGate.excludes(context,Uri.parse("https://webdav.put.io/")));
        assertTrue(ProviderDiscoveryGate.protects(context,Uri.parse("https://webdav.put.io/")));
    }
    @Test public void releasingOneScopeDoesNotReleaseAnotherOwner(){
        ProviderDiscoveryGate.exclude(context,"putio:10:20",Uri.parse("https://webdav.put.io/Films"));ProviderDiscoveryGate.exclude(context,"putio:10:21",Uri.parse("https://webdav.put.io/Films/Archive"));
        ProviderDiscoveryGate.release(context,"putio:10:20");assertFalse(ProviderDiscoveryGate.excludes(context,Uri.parse("https://webdav.put.io/Films/new.mkv")));assertTrue(ProviderDiscoveryGate.excludes(context,Uri.parse("https://webdav.put.io/Films/Archive/old.mkv")));
    }
    @Test public void credentialBearingSourcesAreNeverPersisted(){try{ProviderDiscoveryGate.exclude(context,"putio:10:20",Uri.parse("https://user:secret@webdav.put.io/Films"));fail();}catch(IllegalArgumentException expected){}assertTrue(context.getSharedPreferences("provider-discovery-ownership-v1",0).getAll().isEmpty());}
    @Test public void nativeDeleteCannotReachWebdavForManagedFile()throws Exception{
        ProviderDiscoveryGate.exclude(context,"putio:10:20",Uri.parse("https://webdav.put.io/Films"));
        com.archos.mediacenter.video.browser.Delete operation=new com.archos.mediacenter.video.browser.Delete(null,context);
        java.lang.reflect.Method method=operation.getClass().getDeclaredMethod("deletePhysicalFile",Context.class,Uri.class);method.setAccessible(true);
        assertEquals(Boolean.FALSE,method.invoke(operation,context,Uri.parse("https://webdav.put.io/Films/title.mkv")));
    }
}
