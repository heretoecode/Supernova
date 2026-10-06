package com.archos.mediacenter.video.diagnostics;

import android.graphics.Bitmap;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;
import java.util.*;

/** Deliberately cannot accept a URL, diagnostic payload or arbitrary category. */
final class DiagnosticReference {
    static String payload(String reference,String category,long time){
        if(reference==null||!reference.matches("[a-fA-F0-9]{8}-[0-9]{1,20}")||time<=0
                ||!Arrays.asList("Playback","UI","Network","Other").contains(category))
            throw new IllegalArgumentException("Invalid diagnostic reference");
        return "SUPERNOVA REPORT\nReference: "+reference+"\nCategory: "+category+"\nUTC milliseconds: "+time;
    }
    static BitMatrix matrix(String reference,String category,long time,int size)throws com.google.zxing.WriterException{
        if(size<128||size>1024)throw new IllegalArgumentException("Invalid QR size");
        Map<EncodeHintType,Object> hints=new EnumMap<>(EncodeHintType.class);
        hints.put(EncodeHintType.ERROR_CORRECTION,ErrorCorrectionLevel.M);hints.put(EncodeHintType.MARGIN,4);
        return new QRCodeWriter().encode(payload(reference,category,time),BarcodeFormat.QR_CODE,size,size,hints);
    }
    static Bitmap bitmap(String reference,String category,long time,int size)throws com.google.zxing.WriterException{
        BitMatrix matrix=matrix(reference,category,time,size);int width=matrix.getWidth(),height=matrix.getHeight();
        int[] pixels=new int[width*height];
        for(int y=0;y<height;y++)for(int x=0;x<width;x++)pixels[y*width+x]=matrix.get(x,y)?0xff000000:0xffffffff;
        return Bitmap.createBitmap(pixels,width,height,Bitmap.Config.ARGB_8888);
    }
    private DiagnosticReference(){}
}
