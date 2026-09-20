package com.archos.mediacenter.video.leanback;

import android.content.Context;
import android.graphics.*;
import android.graphics.drawable.Drawable;

/** Small bounded bloom: no full-view blur or surrounding focus container. */
final class PreviewFocusUnderline extends Drawable {
    private final Paint paint=new Paint(Paint.ANTI_ALIAS_FLAG);
    private final float density;
    private final int colour;
    private final boolean focused;
    PreviewFocusUnderline(Context context,boolean focused){density=context.getResources().getDisplayMetrics().density;colour=PreviewAccent.color(context);this.focused=focused;}
    @Override public void draw(Canvas canvas){Rect b=getBounds();float left=b.left+10*density,right=b.right-10*density,y=b.bottom-3*density;
        if(focused){paint.setShader(new LinearGradient(0,y-14*density,0,y,new int[]{colour&0xffffff,(colour&0xffffff)|0x40000000},null,Shader.TileMode.CLAMP));canvas.drawRect(left,y-14*density,right,y,paint);paint.setShader(null);}
        paint.setColor(focused?colour:(colour&0xffffff)|0x99000000);canvas.drawRoundRect(left,y,right,y+2*density,density,density,paint);
    }
    @Override public void setAlpha(int alpha){paint.setAlpha(alpha);}
    @Override public void setColorFilter(ColorFilter filter){paint.setColorFilter(filter);}
    @Override public int getOpacity(){return PixelFormat.TRANSLUCENT;}
}
