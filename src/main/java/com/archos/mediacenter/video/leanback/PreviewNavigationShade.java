package com.archos.mediacenter.video.leanback;

import android.graphics.*;
import android.graphics.drawable.Drawable;

/** Small cached artwork sample, blurred without requiring Android 12 RenderEffect. */
final class PreviewNavigationShade {
    private final Paint paint=new Paint(Paint.ANTI_ALIAS_FLAG|Paint.FILTER_BITMAP_FLAG);
    private Bitmap sample;
    private Drawable source;
    private boolean dirty=true;
    private long sampledAt;
    void invalidate(){dirty=true;}
    void release(){if(sample!=null)sample.recycle();sample=null;source=null;dirty=true;}
    void draw(Canvas canvas,Drawable background,int width,float height,int alpha){
        if(alpha<=0||width<=0||height<=0)return;
        long now=android.os.SystemClock.uptimeMillis();
        int smallWidth=Math.max(1,Math.min(240,width/8)),smallHeight=Math.max(1,Math.round(height*smallWidth/width));
        if(sample==null||sample.getWidth()!=smallWidth||sample.getHeight()!=smallHeight||source!=background){release();source=background;dirty=true;}
        if(background!=null&&dirty&&(sample==null||now-sampledAt>=100)){
            if(sample==null)sample=Bitmap.createBitmap(smallWidth,smallHeight,Bitmap.Config.ARGB_8888);
            sample.eraseColor(Color.TRANSPARENT);Canvas target=new Canvas(sample);target.scale((float)smallWidth/width,smallHeight/height);
            background.draw(target);
            int[] pixels=new int[smallWidth*smallHeight];sample.getPixels(pixels,0,smallWidth,0,0,smallWidth,smallHeight);
            blur(pixels,smallWidth,smallHeight);sample.setPixels(pixels,0,smallWidth,0,0,smallWidth,smallHeight);
            sampledAt=now;dirty=false;
        }
        if(sample!=null){
            int layer=canvas.saveLayer(0,0,width,height,null);
            paint.setAlpha(alpha);paint.setShader(null);canvas.drawBitmap(sample,null,new RectF(0,0,width,height),paint);
            paint.setAlpha(255);paint.setShader(new LinearGradient(0,0,0,height,new int[]{0xd0ffffff,0xa0ffffff,0x00ffffff},new float[]{0,.45f,1},Shader.TileMode.CLAMP));
            paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_IN));canvas.drawRect(0,0,width,height,paint);
            paint.setXfermode(null);canvas.restoreToCount(layer);
        }
        paint.setAlpha(alpha);paint.setShader(new LinearGradient(0,0,0,height,new int[]{0xc00b1624,0x990b1624,0x000b1624},new float[]{0,.45f,1},Shader.TileMode.CLAMP));
        canvas.drawRect(0,0,width,height,paint);paint.setShader(null);paint.setAlpha(255);
    }
    static void blur(int[] pixels,int width,int height){
        int[] intermediate=new int[pixels.length];
        for(int pass=0;pass<2;pass++){
            int[] input=pass==0?pixels:intermediate,output=pass==0?intermediate:pixels;
            for(int y=0;y<height;y++)for(int x=0;x<width;x++){
                int a=0,r=0,g=0,b=0;
                for(int offset=-2;offset<=2;offset++){
                    int sx=pass==0?Math.max(0,Math.min(width-1,x+offset)):x;
                    int sy=pass==1?Math.max(0,Math.min(height-1,y+offset)):y;
                    int colour=input[sy*width+sx];a+=colour>>>24;r+=(colour>>16)&255;g+=(colour>>8)&255;b+=colour&255;
                }
                output[y*width+x]=(a/5)<<24|(r/5)<<16|(g/5)<<8|(b/5);
            }
        }
    }
}
