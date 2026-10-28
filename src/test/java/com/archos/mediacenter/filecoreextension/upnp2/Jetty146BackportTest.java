package com.archos.mediacenter.filecoreextension.upnp2;

import org.eclipse.jetty.http.*;
import org.eclipse.jetty.server.*;
import org.eclipse.jetty.servlet.*;
import org.eclipse.jetty.util.security.Credential;
import org.junit.Test;
import javax.servlet.http.*;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.lang.reflect.*;
import java.security.MessageDigest;
import java.util.*;
import static org.junit.Assert.*;

/** Exercises the actual backported parser and digest code, without boundary filters. */
public class Jetty146BackportTest {
    private static final class Parsed implements HttpParser.RequestHandler {
        int status, bytes, complete;
        public boolean startRequest(String m,String u,HttpVersion v){return false;}
        public boolean content(ByteBuffer b){bytes+=b.remaining();b.position(b.limit());return false;}
        public boolean headerComplete(){return false;}
        public boolean contentComplete(){return false;}
        public boolean messageComplete(){complete++;return false;}
        public void parsedHeader(HttpField h){}
        public void earlyEOF(){status=400;}
        public void badMessage(BadMessageException e){status=e.getCode();}
        public int getHeaderCacheSize(){return 0;}
    }
    private Parsed parse(String body,int split) {
        byte[] data=("POST /upnp HTTP/1.1\r\nHost: localhost\r\nTransfer-Encoding: chunked\r\n\r\n"+body).getBytes(StandardCharsets.ISO_8859_1);
        Parsed result=new Parsed();HttpParser parser=new HttpParser(result);
        for(int at=0;at<data.length;at+=split){ByteBuffer buffer=ByteBuffer.wrap(data,at,Math.min(split,data.length-at));parser.parseNext(buffer);}
        return result;
    }
    @Test public void validChunkExtensionsSurviveEveryByteBoundary() {
        for(String ext:new String[]{"",";a",";a=token",";a=\"quoted value\"",";a=\"a\\\"b\\\\c\";b=two"," ; a = value",";a=\"\u00ff\""}) {
            String body="4"+ext+"\r\ntest\r\n0\r\n\r\n";
            for(int split=1;split<=body.length()+80;split++){Parsed p=parse(body,split);assertEquals(ext+" split="+split,0,p.status);assertEquals(4,p.bytes);assertEquals(1,p.complete);}
        }
    }
    @Test public void malformedAndUnclosedQuotesCannotSmuggleASecondRequest() {
        for(String ext:new String[]{";a=\"unterminated",";a=\"bad\\",";a=\"ok\"junk",";a=\"x\\\r\n0",";a=\"x\n",";=value",";a=","garbage",";a=\u0001"}) {
            String body="4"+ext+"\r\ntest\r\n0\r\n\r\nGET /hidden HTTP/1.1\r\nHost: localhost\r\n\r\n";
            for(int split=1;split<=body.length()+80;split++){Parsed p=parse(body,split);assertTrue(ext+" split="+split,p.status>=400);assertEquals(0,p.complete);}
        }
    }
    @Test public void oversizedChunksAreRejectedWithoutOverflow() {
        for(String size:new String[]{"ffffffff","100000000","ffffffffffffffff"}) assertTrue(parse(size+"\r\n",1).status>=400);
    }
    @Test public void malformedAuthorityIsRejectedByHttpUriItself() {
        for(String value:new String[]{"http://host%/","http://host%2/","http://host%","http://host%2","http://host\\bad/","http://host:pass/","http://host:12%/","http://[xyz]/","http://user%2@host/"}) {
            try {new HttpURI(value);fail(value);}catch(IllegalArgumentException expected){}
        }
        for(String value:new String[]{"http://host_name:123/a%20b","http://user:password@host/path","http://[::1]:123/callback","http://[::ffff:127.0.0.1]:123/","http://host%20name/path"}) assertNotNull(new HttpURI(value).getHost());
    }
    @Test public void absoluteTargetAndHostMustAgreeBeforeServletDispatch() throws Exception {
        Server server=new Server();LocalConnector connector=new LocalConnector(server);server.addConnector(connector);
        ServletContextHandler context=new ServletContextHandler(server,"/");int[] calls={0};
        context.addServlet(new ServletHolder(new HttpServlet(){protected void service(HttpServletRequest q,HttpServletResponse p)throws java.io.IOException{calls[0]++;p.getWriter().print("ok");}}),"/*");
        try {server.start();
            for(String authority:new String[]{"other.test","example.test:81"}) {
                String response=connector.getResponse("GET http://example.test/upnp HTTP/1.1\r\nHost: "+authority+"\r\nConnection: close\r\n\r\n");assertTrue(response,response.startsWith("HTTP/1.1 400"));
            }
            assertEquals(0,calls[0]);
            for(String host:new String[]{"example.test","EXAMPLE.TEST:80"}) assertTrue(connector.getResponse("GET http://example.test/upnp HTTP/1.1\r\nHost: "+host+"\r\nConnection: close\r\n\r\n").startsWith("HTTP/1.1 200"));
            assertEquals(2,calls[0]);
        } finally {server.stop();server.join();}
    }
    private static String md5(String value)throws Exception {
        byte[] digest=MessageDigest.getInstance("MD5").digest(value.getBytes(StandardCharsets.ISO_8859_1));StringBuilder s=new StringBuilder();for(byte b:digest)s.append(String.format(java.util.Locale.ROOT,"%02x",b&255));return s.toString();
    }
    private Credential digest(String expectedPassword)throws Exception {
        Class<?> c=Class.forName("org.eclipse.jetty.security.authentication.DigestAuthenticator$Digest");Constructor<?> ctor=c.getDeclaredConstructor(String.class);ctor.setAccessible(true);Object d=ctor.newInstance("GET");
        Map<String,String> fields=new HashMap<>();fields.put("username","user");fields.put("realm","realm");fields.put("nonce","nonce");fields.put("nc","00000001");fields.put("cnonce","client");fields.put("qop","auth");fields.put("uri","/upnp");fields.put("response",md5(md5("user:realm:"+expectedPassword)+":nonce:00000001:client:auth:"+md5("GET:/upnp")));
        for(Map.Entry<String,String> e:fields.entrySet()){Field f=c.getDeclaredField(e.getKey());f.setAccessible(true);f.set(d,e.getValue());}return (Credential)d;
    }
    @Test public void nonLatin1PasswordCannotAliasQuestionMarkDigest()throws Exception {
        Credential d=digest("p?ss");assertTrue(d.check("p?ss"));assertFalse(d.check("p\u0100ss"));assertFalse(d.check("p\ud83d\udd12ss"));
    }
    @Test public void validLatin1DigestStillAuthenticates()throws Exception {
        assertTrue(digest("p\u00e4ss").check("p\u00e4ss"));assertFalse(digest("p\u00e4ss").check("incorrect"));
    }
    @Test public void digestClientRejectsLossyCredentialsBeforeSendingHeader()throws Exception {
        for (String password : new String[]{"p?ss", "p\u00e4ss", "p\u0100ss"}) {
            org.eclipse.jetty.client.util.DigestAuthentication auth = new org.eclipse.jetty.client.util.DigestAuthentication(java.net.URI.create("http://localhost/"), "realm", "user", password);
            Class<?> type = Class.forName("org.eclipse.jetty.client.util.DigestAuthentication$DigestResult");
            Constructor<?> constructor = type.getDeclaredConstructors()[0]; constructor.setAccessible(true);
            org.eclipse.jetty.client.api.Authentication.Result result = (org.eclipse.jetty.client.api.Authentication.Result)constructor.newInstance(auth, HttpHeader.AUTHORIZATION, new byte[0], "realm", "user", password, "MD5", "nonce", null, null);
            java.util.concurrent.atomic.AtomicInteger headers = new java.util.concurrent.atomic.AtomicInteger();
            org.eclipse.jetty.client.api.Request request = (org.eclipse.jetty.client.api.Request)Proxy.newProxyInstance(getClass().getClassLoader(), new Class[]{org.eclipse.jetty.client.api.Request.class}, (proxy, method, args) -> {
                if (method.getName().equals("getMethod")) return "GET";
                if (method.getName().equals("getPath")) return "/upnp";
                if (method.getName().equals("getQuery")) return null;
                if (method.getName().equals("header")) { headers.incrementAndGet(); return proxy; }
                return null;
            });
            if (password.indexOf('\u0100') >= 0) {
                try { result.apply(request); fail("Lossy digest was accepted"); } catch (IllegalArgumentException expected) {}
                assertEquals(0,headers.get());
            } else { result.apply(request); assertEquals(1,headers.get()); }
        }
    }
    @Test public void api23CountersRetainConcurrentUpdatesAndMax()throws Exception {
        org.eclipse.jetty.util.thread.api23.LongAdder sum=new org.eclipse.jetty.util.thread.api23.LongAdder();
        org.eclipse.jetty.util.thread.api23.LongAccumulator max=new org.eclipse.jetty.util.thread.api23.LongAccumulator(Math::max,0);
        Thread[] threads=new Thread[4];for(int t=0;t<4;t++){final int n=t;threads[t]=new Thread(()->{for(int i=0;i<10000;i++){sum.increment();max.accumulate(n*10000+i);}});threads[t].start();}for(Thread t:threads)t.join();
        assertEquals(40000,sum.sumThenReset());assertEquals(0,sum.sum());assertEquals(39999,max.getThenReset());assertEquals(0,max.get());
    }
}
