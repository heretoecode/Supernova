package com.archos.mediacenter.video.leanback;

import android.app.Application;
import android.net.Uri;
import com.archos.filecorelibrary.webdav.*;
import java.net.*;
import java.io.*;
import java.nio.charset.StandardCharsets;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.*;
import org.robolectric.annotation.Config;
import static org.junit.Assert.*;

/** Exercise the real patched FileCore editor without credentials or a private WebDAV account. */
@RunWith(RobolectricTestRunner.class) @Config(application=Application.class,sdk=28)
public class WebdavDeleteResponseTest {
 @Test public void realDeleteAcceptsOnlySuccessOr404()throws Exception{
  WebdavUtils.getInstance(RuntimeEnvironment.getApplication());com.archos.environment.ArchosUtils.setGlobalContext(RuntimeEnvironment.getApplication());
  for(int status:new int[]{204,404,401,403,405,423,500,503}){
   try(ServerSocket server=new ServerSocket(0,8,InetAddress.getByName("127.0.0.1"))){
    java.util.concurrent.atomic.AtomicReference<String> method=new java.util.concurrent.atomic.AtomicReference<>();
    Thread responder=new Thread(()->{while(!server.isClosed())try(Socket socket=server.accept()){
     BufferedReader reader=new BufferedReader(new InputStreamReader(socket.getInputStream(),StandardCharsets.US_ASCII));String line=reader.readLine();if(line!=null)method.set(line.split(" ")[0]);while((line=reader.readLine())!=null&&!line.isEmpty()){}
     socket.getOutputStream().write(("HTTP/1.1 "+status+" Fixture\r\nContent-Length: 0\r\nConnection: close\r\n\r\n").getBytes(StandardCharsets.US_ASCII));socket.getOutputStream().flush();
    }catch(IOException stopped){if(!server.isClosed())throw new RuntimeException(stopped);}},"webdav-response-fixture");responder.setDaemon(true);responder.start();
    try{
     WebdavFileEditor editor=new WebdavFileEditor(Uri.parse("webdav://127.0.0.1:"+server.getLocalPort()+"/file.mkv"));
     try{editor.delete();assertTrue("Unexpected success "+status,status==204||status==404);assertEquals(status==404,editor.wasAlreadyAbsent());
      com.archos.mediacenter.video.browser.Delete deletion=new com.archos.mediacenter.video.browser.Delete(null,RuntimeEnvironment.getApplication());
      var physical=deletion.getClass().getDeclaredMethod("deletePhysicalFile",android.content.Context.class,Uri.class);physical.setAccessible(true);assertEquals(Boolean.TRUE,physical.invoke(deletion,RuntimeEnvironment.getApplication(),Uri.parse("webdav://127.0.0.1:"+server.getLocalPort()+"/file.mkv")));
      var absent=deletion.getClass().getDeclaredMethod("sourceWasAbsent");absent.setAccessible(true);assertEquals("Only 404 cleanup must skip surviving sidecars",status==404,absent.invoke(deletion));}
     catch(Exception failure){assertTrue(status!=204&&status!=404);assertEquals(status,((Number)failure.getClass().getMethod("getStatusCode").invoke(failure)).intValue());}
     assertEquals("DELETE",method.get());
    }finally{server.close();responder.join(2000);assertFalse(responder.isAlive());}
   }
  }
 }
}
