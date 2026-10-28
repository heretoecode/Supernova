package com.archos.mediacenter.filecoreextension.upnp2;

import org.junit.Test;
import javax.servlet.http.HttpServlet;
import javax.servlet.AsyncContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.Socket;
import java.net.URI;
import java.util.concurrent.Executors;
import java.util.concurrent.ExecutorService;
import org.jupnp.transport.impl.jetty.StreamClientConfigurationImpl;
import org.jupnp.model.message.StreamRequestMessage;
import org.jupnp.model.message.UpnpRequest;
import java.nio.charset.StandardCharsets;
import java.lang.reflect.Constructor;
import java.util.concurrent.atomic.AtomicInteger;
import static org.junit.Assert.*;

/** Real TCP/Jetty/servlet coverage; no mocked HTTP parser. */
public class Jetty145SecurityTest {
    private static final String SECRET = "/private/media/secret-token";
    private static final class Fixture implements AutoCloseable {
        final NovaJettyServletContainer container;
        final AtomicInteger calls = new AtomicInteger();
        final int port;
        Fixture(boolean error, boolean soapFault) throws Exception { this(error, soapFault, false); }
        Fixture(boolean error, boolean soapFault, boolean async) throws Exception {
            Constructor<NovaJettyServletContainer> ctor = NovaJettyServletContainer.class.getDeclaredConstructor();
            ctor.setAccessible(true); container = ctor.newInstance();
            container.registerServlet("/upnp", new HttpServlet() {
                @Override protected void service(HttpServletRequest req, HttpServletResponse resp) throws IOException {
                    calls.incrementAndGet();
                    byte[] input = req.getInputStream().readAllBytes();
                    if (async) {
                        AsyncContext pending = req.startAsync();
                        new Thread(() -> {
                            try { pending.getResponse().getWriter().write(req.getMethod() + ":" + input.length); }
                            catch (IOException failure) { throw new RuntimeException(failure); }
                            finally { pending.complete(); }
                        }).start();
                        return;
                    }
                    if (error) { resp.sendError(500, SECRET); return; }
                    if (soapFault) {
                        resp.setStatus(500); resp.setContentType("text/xml");
                        resp.getWriter().write("<Fault>UPnPError</Fault>"); return;
                    }
                    resp.setStatus(200); resp.getWriter().write(req.getMethod() + ":" + input.length);
                }
            });
            port = container.addConnector("127.0.0.1", 0); container.startIfNotRunning();
        }
        String send(String message) throws Exception {
            try (Socket socket = new Socket("127.0.0.1", port)) {
                socket.setSoTimeout(5000);
                socket.getOutputStream().write(message.getBytes(StandardCharsets.US_ASCII));
                socket.getOutputStream().flush();
                return new String(socket.getInputStream().readAllBytes(), StandardCharsets.US_ASCII);
            }
        }
        public void close() { container.stopIfRunning(); }
    }
    private String request(String method, String headers, String body) {
        return method + " /upnp/callback HTTP/1.1\r\nHost: localhost\r\nConnection: close\r\n" + headers + "\r\n" + body;
    }
    @Test public void validUpnpMethodsAndBodiesStillWork() throws Exception {
        try (Fixture f = new Fixture(false, false)) {
            for (String method : new String[]{"GET","POST","NOTIFY","SUBSCRIBE","UNSUBSCRIBE"}) {
                String response = f.send(request(method,"Content-Length: 4\r\n","test"));
                assertTrue(response, response.startsWith("HTTP/1.1 200"));
                assertTrue(response, response.contains(method + ":4"));
                assertFalse(response, response.contains("Server: Jetty"));
            }
            assertEquals(5, f.calls.get());
        }
    }
    @Test public void signedAndDuplicateContentLengthsNeverReachServlet() throws Exception {
        try (Fixture f = new Fixture(false, false)) {
            for (String headers : new String[]{"Content-Length: +4\r\n", "Content-Length: -4\r\n", "Content-Length: 4\r\nContent-Length: 4\r\n", "Content-Length: 4,4\r\n", "Content-Length: 9223372036854775808\r\n"}) {
                String response = f.send(request("NOTIFY", headers, "test"));
                assertTrue(response, response.startsWith("HTTP/1.1 400"));
            }
            assertEquals(0, f.calls.get());
        }
    }
    @Test public void transferEncodingWithContentLengthIsRejected() throws Exception {
        try (Fixture f = new Fixture(false, false)) {
            String response = f.send(request("POST", "Transfer-Encoding: chunked\r\nContent-Length: 4\r\n", "0\r\n\r\n"));
            assertTrue(response, response.startsWith("HTTP/1.1 400")); assertEquals(0, f.calls.get());
        }
    }
    @Test public void chunkedUpnpBodyStillWorks() throws Exception {
        try (Fixture f = new Fixture(false, false)) {
            String response = f.send(request("NOTIFY", "Transfer-Encoding: chunked\r\n", "4\r\ntest\r\n0\r\n\r\n"));
            assertTrue(response, response.contains("NOTIFY:4")); assertEquals(1, f.calls.get());
        }
    }
    @Test public void connectionIsClosedBeforePipelinedRequestCanRun() throws Exception {
        try (Fixture f = new Fixture(false, false)) {
            String response = f.send(request("NOTIFY","Content-Length: 0\r\n", "").replace("Connection: close", "Connection: keep-alive") + request("POST","Content-Length: 0\r\n", ""));
            assertTrue(response, response.toLowerCase().contains("connection: close")); assertEquals(1, f.calls.get());
        }
    }
    @Test public void errorResponsesDoNotExposePrivateDetails() throws Exception {
        try (Fixture f = new Fixture(true, false)) {
            String response = f.send(request("GET","", ""));
            assertTrue(response, response.startsWith("HTTP/1.1 500"));
            assertTrue(response, response.contains("Request failed (500)"));
            assertFalse(response, response.contains(SECRET)); assertFalse(response, response.contains("Jetty"));
        }
    }
    @Test public void soapFaultBodiesArePreserved() throws Exception {
        try (Fixture f = new Fixture(false, true)) {
            String response = f.send(request("POST","Content-Length: 0\r\n", ""));
            assertTrue(response, response.startsWith("HTTP/1.1 500")); assertTrue(response, response.contains("<Fault>UPnPError</Fault>"));
        }
    }
    @Test public void asyncNotifyCompletesBeforeConnectionCloses() throws Exception {
        try (Fixture f = new Fixture(false, false, true)) {
            String response = f.send(request("NOTIFY", "Content-Length: 4\r\n", "test"));
            assertTrue(response, response.startsWith("HTTP/1.1 200"));
            assertTrue(response, response.contains("NOTIFY:4")); assertEquals(1, f.calls.get());
        }
    }
    @Test public void parserErrorUsesOpaqueBody() throws Exception {
        try (Fixture f = new Fixture(false, false)) {
            String response = f.send(request("NOTIFY", "Content-Length: " + SECRET + "\r\n", ""));
            assertTrue(response, response.startsWith("HTTP/1.1 400"));
            assertTrue(response, response.contains("Request failed (400)"));
            assertFalse(response, response.contains(SECRET)); assertFalse(response, response.contains("<h1>"));
            assertEquals(0, f.calls.get());
        }
    }

    @Test public void malformedOutboundAuthoritiesNeverProduceARequest() throws Exception {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        ValidatingJettyStreamClient client = new ValidatingJettyStreamClient(new StreamClientConfigurationImpl(executor));
        try {
            for (String endpoint : new String[]{"http://localhost;/path", "http://browser.check%23%40vulndetector.com/", "http://browser.check%5c/", "file:///storage/movie.mkv", "//localhost/path"}) {
                assertNull(endpoint, client.createRequest(new StreamRequestMessage(UpnpRequest.Method.GET, URI.create(endpoint))));
            }
        } finally { client.stop(); executor.shutdownNow(); }
    }
    @Test public void normalEncodedAndIpv6EndpointsRemainSupported() throws Exception {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        ValidatingJettyStreamClient client = new ValidatingJettyStreamClient(new StreamClientConfigurationImpl(executor));
        try {
            for (String endpoint : new String[]{"http://127.0.0.1:1234/path%20with%20spaces?a=b%2Fc", "http://[::1]:1234/callback", "http://example.test/desc.xml"}) {
                assertNotNull(endpoint, client.createRequest(new StreamRequestMessage(UpnpRequest.Method.GET, URI.create(endpoint))));
            }
        } finally { client.stop(); executor.shutdownNow(); }
    }

}
