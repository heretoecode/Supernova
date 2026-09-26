package com.archos.mediacenter.video.diagnostics;

import com.google.zxing.*;
import com.google.zxing.common.*;
import com.google.zxing.qrcode.QRCodeReader;
import org.junit.Test;
import static org.junit.Assert.*;

public class DiagnosticReferenceTest {
    @Test public void qrRoundTripContainsOnlyAllowListedReferenceFields()throws Exception{
        String id="d78c2515-1009",category="UI";long time=1790410000123L;
        BitMatrix matrix=DiagnosticReference.matrix(id,category,time,512);
        int width=matrix.getWidth(),height=matrix.getHeight();int[] pixels=new int[width*height];
        for(int y=0;y<height;y++)for(int x=0;x<width;x++)pixels[y*width+x]=matrix.get(x,y)?0xff000000:0xffffffff;
        String decoded=new QRCodeReader().decode(new BinaryBitmap(new HybridBinarizer(new RGBLuminanceSource(width,height,pixels)))).getText();
        assertEquals(DiagnosticReference.payload(id,category,time),decoded);
        assertEquals(4,decoded.split("\n").length);assertFalse(decoded.contains("://"));
        assertTrue(decoded.contains(id));assertTrue(decoded.contains(String.valueOf(time)));
    }
    @Test public void arbitraryCategoryOrReferenceCannotReachQr(){
        for(String value:new String[]{"https://private/path?token=secret","/storage/private","password=secret","UI\ntoken=secret"}){
            try{DiagnosticReference.payload(value,"UI",1);fail("Unsafe reference accepted");}catch(IllegalArgumentException expected){assertFalse(expected.getMessage().contains("secret"));}
            try{DiagnosticReference.payload("d78c2515-1009",value,1);fail("Unsafe category accepted");}catch(IllegalArgumentException expected){assertFalse(expected.getMessage().contains("secret"));}
        }
    }
    @Test public void invalidTimeRejected(){
        try{DiagnosticReference.payload("d78c2515-1009","UI",0);fail("Invalid time accepted");}catch(IllegalArgumentException expected){}
    }
}
