package com.archos.mediacenter.video.leanback;

import android.content.Context;
import android.graphics.*;
import android.graphics.drawable.Drawable;

/** Exact supplied 4.1.2 asset, aspect-preserving viewport cover; decoded once. */
public final class PreviewUtilityBackground extends Drawable {
    private static Bitmap image;
    private final Paint paint=new Paint(Paint.ANTI_ALIAS_FLAG|Paint.FILTER_BITMAP_FLAG);
    public PreviewUtilityBackground(Context context){synchronized(PreviewUtilityBackground.class){if(image==null)image=BitmapFactory.decodeResource(context.getResources(),com.archos.mediacenter.video.R.drawable.preview_utility_background);}}
    @Override public void draw(Canvas canvas){Rect b=getBounds();canvas.drawColor(0xff09111a);if(image==null)return;float scale=Math.max(b.width()/(float)image.getWidth(),b.height()/(float)image.getHeight());float w=image.getWidth()*scale,h=image.getHeight()*scale;canvas.drawBitmap(image,null,new RectF(b.exactCenterX()-w/2,b.exactCenterY()-h/2,b.exactCenterX()+w/2,b.exactCenterY()+h/2),paint);canvas.drawColor(0x44050b12);}
    @Override public void setAlpha(int alpha){paint.setAlpha(alpha);}
    @Override public void setColorFilter(ColorFilter filter){paint.setColorFilter(filter);}
    @Override public int getOpacity(){return PixelFormat.OPAQUE;}
}
