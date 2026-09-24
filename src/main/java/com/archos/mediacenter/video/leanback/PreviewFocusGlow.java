package com.archos.mediacenter.video.leanback;

import android.content.Context;
import android.graphics.*;
import android.graphics.drawable.Drawable;

/** Bounded, hardware-safe soft perimeter. No underline and no opaque focus fill. */
public final class PreviewFocusGlow extends Drawable {
    private final Paint paint=new Paint(Paint.ANTI_ALIAS_FLAG);
    private final float density;
    private final int colour;
    private int alpha=255;
    public PreviewFocusGlow(Context context){
        density=context.getResources().getDisplayMetrics().density;
        colour=PreviewAccent.color(context);
        paint.setStyle(Paint.Style.STROKE);
    }
    @Override public void draw(Canvas canvas){
        Rect b=getBounds();float inset=.5f*density;
        RectF edge=new RectF(b.left+inset,b.top+inset,b.right-inset,b.bottom-inset);
        for(int i=4;i>=1;i--){
            paint.setStrokeWidth(density);
            RectF halo=new RectF(edge);halo.inset(-i*density,-i*density);
            paint.setColor(colour);paint.setAlpha((i==1?42:12)*alpha/255);
            canvas.drawRoundRect(halo,(6+i)*density,(6+i)*density,paint);
        }
        paint.setStrokeWidth(density);paint.setAlpha(190*alpha/255);
        canvas.drawRoundRect(edge,6*density,6*density,paint);
    }
    @Override public void setAlpha(int value){alpha=value;invalidateSelf();}
    @Override public void setColorFilter(ColorFilter filter){paint.setColorFilter(filter);invalidateSelf();}
    @Override public int getOpacity(){return PixelFormat.TRANSLUCENT;}
}
