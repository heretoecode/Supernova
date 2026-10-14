package com.archos.mediacenter.video.diagnostics;

import android.app.Application;
import android.net.Uri;
import com.archos.filecorelibrary.TransportReadObserver;
import com.archos.filecorelibrary.TransportReadObserver.*;
import com.archos.filecorelibrary.webdav.*;
import java.io.*;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.*;
import java.util.zip.*;
import org.json.*;
import org.junit.*;
import org.junit.runner.RunWith;
import org.robolectric.*;
import org.robolectric.annotation.Config;
import org.robolectric.util.ReflectionHelpers;
import static org.junit.Assert.*;

@RunWith(RobolectricTestRunner.class) @Config(application=Application.class,sdk=28)
public class TransportReadDiagnosticsTest {
    private Application app;
    @Before public void setup(){app=RuntimeEnvironment.getApplication();Diagnostics.setEnabled(app,false);Diagnostics.setEnabled(app,true);Diagnostics.beginPlayback(Uri.parse("smbj://user:AUTH_CANARY@host/FILE_CANARY?token=QUERY_CANARY"));}
    @After public void close(){Diagnostics.endPlayback("test");Diagnostics.setEnabled(app,false);}
    private List<JSONObject> records()throws Exception {
        DiagnosticFlightRecorder recorder=ReflectionHelpers.getStaticField(Diagnostics.class,"FLIGHT");List<JSONObject> result=new ArrayList<>();
        for(String line:recorder.snapshot(android.os.SystemClock.elapsedRealtime()).split("\n"))if(!line.isEmpty()){JSONObject row=new JSONObject(line);if(row.optString("event").equals("transport_read"))result.add(row);}return result;
    }
    @Test public void disabledObservationReturnsExactStreamAndTransportAliasesAreClassified(){
        Diagnostics.setEnabled(app,false);InputStream original=new ByteArrayInputStream(new byte[4]);assertSame(original,TransportReadObserver.observe(Protocol.SMBJ,original));
        for(String source:List.of("smbj","sshj","sftp"))assertEquals(source,Diagnostics.sourceType(Uri.parse(source+"://user:AUTH_CANARY@host/FILE_CANARY")));
        assertEquals("other",Diagnostics.sourceType(Uri.parse("PRIVATE_CANARY://host/path")));
    }
    @Test public void bulkReadsSkipResetAndClosePreserveStreamContractAndCountOnlyBytesActuallyRead()throws Exception {
        int[] bulk={0},single={0};ByteArrayInputStream original=new ByteArrayInputStream(new byte[]{1,2,3,4,5,6}){
            @Override public synchronized int read(byte[] b,int off,int len){bulk[0]++;return super.read(b,off,len);}
            @Override public synchronized int read(){single[0]++;return super.read();}
        };
        InputStream observed=TransportReadObserver.observe(Protocol.SFTP,original);assertTrue(observed.markSupported());observed.mark(20);assertEquals(3,observed.read(new byte[3]));assertEquals(1,bulk[0]);assertEquals(0,single[0]);assertEquals(2,observed.skip(2));assertEquals(6,observed.read());assertEquals(-1,observed.read());observed.reset();assertEquals(1,observed.read());observed.close();observed.close();
        List<JSONObject> rows=records();assertEquals(4,rows.size());assertEquals("OPENED",rows.get(0).getString("boundary"));assertEquals("FIRST_BYTES",rows.get(1).getString("boundary"));assertEquals("END_OF_INPUT",rows.get(2).getString("boundary"));JSONObject terminal=rows.get(3);assertEquals("CLOSED",terminal.getString("boundary"));assertEquals(5,terminal.getLong("bytes"));assertEquals(4,terminal.getLong("read_calls"));assertTrue(terminal.getLong("read_duration_ns")>=0);assertEquals("PROXY",terminal.getString("measurement_quality"));
    }
    @Test public void originalTimeoutAndCloseExceptionsPropagateWithoutTheirMessagesEnteringRecorder()throws Exception {
        IOException timeout=new SocketTimeoutException("TIMEOUT_CANARY password=SECRET_CANARY");InputStream failing=new InputStream(){public int read()throws IOException{throw timeout;}};
        InputStream observed=TransportReadObserver.observe(Protocol.SMBJ,failing);try{observed.read();fail();}catch(IOException actual){assertSame(timeout,actual);}observed.close();assertEquals("TIMEOUT",records().get(1).getString("boundary"));
        IOException close=new IOException("CLOSE_CANARY");InputStream closing=TransportReadObserver.observe(Protocol.SSHJ,new ByteArrayInputStream(new byte[0]){@Override public void close()throws IOException{throw close;}});try{closing.close();fail();}catch(IOException actual){assertSame(close,actual);}assertEquals("FAILED",records().get(3).getString("boundary"));
        assertFalse(records().toString().contains("CANARY"));
    }
    @Test public void brokenOptionalObserverCannotChangeBytesOrThrowIntoTransport()throws Exception {
        InputStream input=new ByteArrayInputStream(new byte[]{4});TransportReadObserver.install(protocol->{throw new IllegalStateException("observer unavailable");});assertSame(input,TransportReadObserver.observe(Protocol.FTP,input));
        TransportReadObserver.install(protocol->(a,b,c,d,e)->{throw new IllegalStateException("observer unavailable");});try(InputStream observed=TransportReadObserver.observe(Protocol.FTPS,input)){assertEquals(4,observed.read());assertEquals(-1,observed.read());}
    }
    @Test public void actualWebdavReadUsesAdapterHookAndCapturesProducerSessionWithoutMediaAddressesInZip()throws Exception {
        WebdavUtils.getInstance(app);ExecutorService worker=Executors.newSingleThreadExecutor();
        try(ServerSocket server=new ServerSocket(0,1,InetAddress.getByName("127.0.0.1"))){
            server.setSoTimeout(15000);Future<?> served=worker.submit(()->{try(Socket socket=server.accept()){socket.setSoTimeout(5000);BufferedReader request=new BufferedReader(new InputStreamReader(socket.getInputStream(),StandardCharsets.US_ASCII));String line;while((line=request.readLine())!=null&&!line.isEmpty()){}socket.getOutputStream().write("HTTP/1.1 200 OK\r\nContent-Length: 6\r\nConnection: close\r\n\r\nABCDEF".getBytes(StandardCharsets.US_ASCII));}catch(IOException failure){throw new UncheckedIOException(failure);}});
            Uri uri=Uri.parse("webdav://127.0.0.1:"+server.getLocalPort()+"/FILE_CANARY");
            InputStream opened;try{opened=new WebdavFileEditor(uri).getInputStream();}catch(Exception failure){try{served.get(1,TimeUnit.SECONDS);}catch(ExecutionException serverFailure){throw new AssertionError("Loopback server failed",serverFailure.getCause());}throw failure;}
            try(InputStream input=opened){
                assertEquals("WEBDAV",records().get(0).getString("source"));String session=records().get(0).getString("playback_session");Diagnostics.beginPlayback(Uri.parse("file:///OTHER_CANARY"));assertEquals("ABCDEF",new String(input.readAllBytes(),StandardCharsets.US_ASCII));
                assertEquals(session,records().get(records().size()-1).getString("playback_session"));
            }
            served.get(5,TimeUnit.SECONDS);assertEquals(6,records().get(records().size()-1).getLong("bytes"));
        }finally{worker.shutdownNow();}
        for(String name:List.of("WORK","IMPORTANT")){ThreadPoolExecutor writer=ReflectionHelpers.getStaticField(Diagnostics.class,name);writer.submit(()->{}).get(10,TimeUnit.SECONDS);}
        ByteArrayOutputStream output=new ByteArrayOutputStream();Diagnostics.export(app,output);StringBuilder exported=new StringBuilder();try(ZipInputStream zip=new ZipInputStream(new ByteArrayInputStream(output.toByteArray()))){while(zip.getNextEntry()!=null)exported.append(new String(zip.readAllBytes(),StandardCharsets.UTF_8));}
        for(String canary:List.of("AUTH_CANARY","FILE_CANARY","QUERY_CANARY","OTHER_CANARY","SECRET_CANARY"))assertFalse(canary,exported.toString().contains(canary));assertTrue(exported.toString().contains("JAVA_STREAM_READS_ONLY"));
    }
}
