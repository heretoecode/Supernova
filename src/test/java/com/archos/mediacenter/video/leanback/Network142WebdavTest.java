package com.archos.mediacenter.video.leanback;

import android.app.Application;
import android.net.Uri;
import com.archos.filecorelibrary.samba.NetworkCredentialsDatabase;
import com.archos.filecorelibrary.webdav.*;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;
import java.net.*;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.*;
import static org.junit.Assert.*;
import org.robolectric.util.ReflectionHelpers;

/** Real loopback PROPFIND through the pinned DAV client; no external account or Shield claim. */
@RunWith(org.robolectric.RobolectricTestRunner.class) @Config(application=Application.class,sdk=28)
public class Network142WebdavTest {
 @Test public void actualDavListingSendsIntactEmailUsernameAndReadsEncodedNames()throws Exception{
  Application app=RuntimeEnvironment.getApplication();WebdavUtils utils=WebdavUtils.getInstance(app);ExecutorService worker=Executors.newSingleThreadExecutor();
  try(ServerSocket server=new ServerSocket(0,1,InetAddress.getByName("127.0.0.1"))){
   server.setSoTimeout(5000);Future<String> request=worker.submit(()->{for(int attempt=0;attempt<4;attempt++)try(Socket socket=server.accept()){socket.setSoTimeout(5000);BufferedReader in=new BufferedReader(new InputStreamReader(socket.getInputStream(),StandardCharsets.UTF_8));String first=in.readLine(),auth=null,line;int length=0;while((line=in.readLine())!=null&&!line.isEmpty()){if(line.toLowerCase(java.util.Locale.ROOT).startsWith("authorization:"))auth=line.substring(line.indexOf(':')+1).trim();if(line.toLowerCase(java.util.Locale.ROOT).startsWith("content-length:"))length=Integer.parseInt(line.substring(line.indexOf(':')+1).trim());}for(int left=length;left>0;){int read=in.read(new char[Math.min(left,4096)]);if(read<0)throw new EOFException();left-=read;}
    if(first.startsWith("OPTIONS ")){socket.getOutputStream().write("HTTP/1.1 200 OK\r\nAllow: OPTIONS, HEAD, PROPFIND\r\nDAV: 1, 2\r\nContent-Length: 0\r\nConnection: close\r\n\r\n".getBytes(StandardCharsets.US_ASCII));continue;}if(first.startsWith("HEAD ")){socket.getOutputStream().write("HTTP/1.1 200 OK\r\nContent-Length: 0\r\nConnection: close\r\n\r\n".getBytes(StandardCharsets.US_ASCII));continue;}assertTrue(first.startsWith("PROPFIND "));
    String xml="<?xml version=\"1.0\" encoding=\"utf-8\"?><d:multistatus xmlns:d=\"DAV:\"><d:response><d:href>/Media/</d:href><d:propstat><d:prop><d:resourcetype><d:collection/></d:resourcetype><d:getcontentlength>0</d:getcontentlength></d:prop><d:status>HTTP/1.1 200 OK</d:status></d:propstat></d:response><d:response><d:href>/Media/Film%20One.mkv</d:href><d:propstat><d:prop><d:resourcetype/><d:getcontentlength>42</d:getcontentlength><d:getcontenttype>video/x-matroska</d:getcontenttype></d:prop><d:status>HTTP/1.1 200 OK</d:status></d:propstat></d:response></d:multistatus>";
    byte[] body=xml.getBytes(StandardCharsets.UTF_8);socket.getOutputStream().write(("HTTP/1.1 207 Multi-Status\r\nContent-Type: application/xml; charset=utf-8\r\nContent-Length: "+body.length+"\r\nConnection: close\r\n\r\n").getBytes(StandardCharsets.US_ASCII));socket.getOutputStream().write(body);return auth;}throw new AssertionError("PROPFIND was not received");});
   Uri uri=Uri.parse("webdav://127.0.0.1:"+server.getLocalPort()+"/Media/");NetworkCredentialsDatabase.getInstance().addCredential(new NetworkCredentialsDatabase.Credential("person@example.test","fixture-only",uri.toString(),"",true));
   Object client=ReflectionHelpers.callInstanceMethod(utils,"getSardine",ReflectionHelpers.ClassParameter.from(Uri.class,uri));java.util.List<?> listed=ReflectionHelpers.callInstanceMethod(client,"list",ReflectionHelpers.ClassParameter.from(String.class,WebdavFile2.uriToHttp(uri).toString()));assertEquals(2,listed.size());assertEquals("Film One.mkv",ReflectionHelpers.callInstanceMethod(listed.get(1),"getName"));assertEquals(Long.valueOf(42),ReflectionHelpers.callInstanceMethod(listed.get(1),"getContentLength"));String auth=request.get(5,TimeUnit.SECONDS);assertNotNull(auth);assertEquals("person@example.test:fixture-only",new String(java.util.Base64.getDecoder().decode(auth.substring(6)),StandardCharsets.UTF_8));
  }finally{worker.shutdownNow();}
 }
}
