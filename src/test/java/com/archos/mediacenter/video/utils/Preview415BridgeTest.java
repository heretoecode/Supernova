package com.archos.mediacenter.video.utils;
import android.app.Application;
import android.net.Uri;
import com.archos.filecorelibrary.StreamOverHttp;
import java.net.*;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.*;
import org.robolectric.annotation.Config;
import static org.junit.Assert.*;
@RunWith(RobolectricTestRunner.class) @Config(application=Application.class,sdk=28)
public class Preview415BridgeTest {
    @Test public void bridgeBindsOnlyLoopbackAndAcceptsLocalConnections()throws Exception{
        StreamOverHttp bridge=new StreamOverHttp(Uri.parse("file:///storage/probe.mkv"),"video/mp4");
        try{
            ServerSocket socket=org.robolectric.util.ReflectionHelpers.getField(bridge,"serverSocket");
            assertTrue(socket.getInetAddress().isLoopbackAddress());assertFalse(socket.getInetAddress().isAnyLocalAddress());
            Uri uri=bridge.getUri("probe.mkv");assertEquals("127.0.0.1",uri.getHost());
            try(Socket local=new Socket()){local.connect(new InetSocketAddress(uri.getHost(),uri.getPort()),2000);assertTrue(local.isConnected());}
        }finally{bridge.close();}
    }
}
