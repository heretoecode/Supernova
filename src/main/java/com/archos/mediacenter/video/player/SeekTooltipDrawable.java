package com.archos.mediacenter.video.player;

import android.graphics.*;
import android.graphics.drawable.Drawable;

/** Seek tooltip whose pointer remains at the seek position after the body is clamped. */
final class SeekTooltipDrawable extends Drawable {
    private final Paint paint=new Paint(Paint.ANTI_ALIAS_FLAG);
    private final float density;private float pointer;
    SeekTooltipDrawable(float density,float pointer){this.density=density;this.pointer=pointer;}
    void setPointer(float pointer){this.pointer=pointer;invalidateSelf();}
    @Override public void draw(Canvas canvas){
        Rect b=getBounds();float notch=4*density,tip=Math.max(b.left+notch,Math.min(b.right-notch,b.left+pointer));
        paint.setColor(0xee152738);paint.setStyle(Paint.Style.FILL);
        canvas.drawRoundRect(b.left,b.top,b.right,b.bottom-notch,5*density,5*density,paint);
        Path arrow=new Path();arrow.moveTo(tip-notch,b.bottom-notch);arrow.lineTo(tip,b.bottom);arrow.lineTo(tip+notch,b.bottom-notch);arrow.close();canvas.drawPath(arrow,paint);
    }
    @Override public void setAlpha(int alpha){paint.setAlpha(alpha);invalidateSelf();}
    @Override public void setColorFilter(ColorFilter filter){paint.setColorFilter(filter);invalidateSelf();}
    @Override public int getOpacity(){return PixelFormat.TRANSLUCENT;}
}
