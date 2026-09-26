package com.archos.mediacenter.video.utils;

import java.io.*;
import org.junit.Test;
import static org.junit.Assert.*;

public class SubtitleTransferTest {
    @Test public void completeResponseIsStagedUnchanged() throws Exception {
        byte[] payload="1\n00:00:00,000 --> 00:00:01,000\nExample\n".getBytes("UTF-8");
        ByteArrayOutputStream staged=new ByteArrayOutputStream();
        assertEquals(payload.length,SubtitleTransfer.stage(new ByteArrayInputStream(payload),staged,()->false));
        assertArrayEquals(payload,staged.toByteArray());
    }
    @Test public void emptyAndCancelledResponsesFail() throws Exception {
        assertFails(new ByteArrayInputStream(new byte[0]),false);
        assertFails(new ByteArrayInputStream(new byte[]{1}),true);
    }
    @Test public void interruptedResponseFails() throws Exception {
        assertFails(new InputStream(){public int read() throws IOException {throw new IOException("Disconnected");}},false);
    }
    @Test public void oversizedResponseFailsWithoutRetainingPayloadInMemory() throws Exception {
        InputStream infinite=new InputStream(){public int read(){return 1;}public int read(byte[] b){return b.length;}};
        assertFails(infinite,false);
    }
    private static void assertFails(InputStream input,boolean cancelled) throws Exception {
        try {SubtitleTransfer.stage(input,new OutputStream(){public void write(int b){}public void write(byte[] b,int o,int n){}},()->cancelled);fail("Expected rejected response");}
        catch(IOException expected) {assertNotNull(expected.getMessage());}
    }
}
