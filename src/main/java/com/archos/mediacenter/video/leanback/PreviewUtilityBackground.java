package com.archos.mediacenter.video.leanback;

import android.content.Context;
import android.graphics.*;
import android.graphics.drawable.Drawable;

/** Exact supplied 4.1.2 asset, aspect-preserving viewport cover; decoded once. */
public final class PreviewUtilityBackground extends Drawable {
    private static Bitmap image, foundationImage;
    private final Paint paint=new Paint(Paint.ANTI_ALIAS_FLAG|Paint.FILTER_BITMAP_FLAG);
    public PreviewUtilityBackground(Context context){synchronized(PreviewUtilityBackground.class){if(com.archos.mediacenter.video.BuildConfig.FOUNDATION){if(foundationImage==null){BitmapFactory.Options options=new BitmapFactory.Options();options.inSampleSize=2;foundationImage=BitmapFactory.decodeResource(context.getResources(),com.archos.mediacenter.video.R.drawable.foundation_space_black,options);}}else if(image==null)image=BitmapFactory.decodeResource(context.getResources(),com.archos.mediacenter.video.R.drawable.preview_utility_background);}}
    @Override public void draw(Canvas canvas){Bitmap image=com.archos.mediacenter.video.BuildConfig.FOUNDATION?foundationImage:PreviewUtilityBackground.image;Rect b=getBounds();canvas.drawColor(0xff09111a);if(image==null)return;float scale=Math.max(b.width()/(float)image.getWidth(),b.height()/(float)image.getHeight());float w=image.getWidth()*scale,h=image.getHeight()*scale;canvas.drawBitmap(image,null,new RectF(b.exactCenterX()-w/2,b.exactCenterY()-h/2,b.exactCenterX()+w/2,b.exactCenterY()+h/2),paint);if(!com.archos.mediacenter.video.BuildConfig.FOUNDATION)canvas.drawColor(0x44050b12);}
    @Override public void setAlpha(int alpha){paint.setAlpha(alpha);}
    @Override public void setColorFilter(ColorFilter filter){paint.setColorFilter(filter);}
    @Override public int getOpacity(){return PixelFormat.OPAQUE;}
}
