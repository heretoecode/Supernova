package com.archos.mediacenter.video.leanback;

import android.content.Context;
import android.graphics.*;
import android.graphics.drawable.Drawable;

/** One launch surface, shared by Entry and the first composed Home frame. No timer. */
public final class PreviewStartupSurface extends Drawable {
 private final Paint paint=new Paint(Paint.ANTI_ALIAS_FLAG);
 private final float density;
 private final Drawable foundation;
 public PreviewStartupSurface(Context c){density=c.getResources().getDisplayMetrics().density;foundation=com.archos.mediacenter.video.BuildConfig.FOUNDATION?c.getDrawable(com.archos.mediacenter.video.R.drawable.foundation_splash):null;}
 public void draw(Canvas canvas){Rect r=getBounds();if(foundation!=null){foundation.setBounds(r);foundation.draw(canvas);return;}canvas.drawColor(0xff09111a);paint.setColor(0xffdce8f1);paint.setTypeface(Typeface.create("sans-serif-light",Typeface.NORMAL));paint.setTextSize(30*density);paint.setTextAlign(Paint.Align.CENTER);canvas.drawText("SUPERNOVA",r.exactCenterX(),r.exactCenterY()-(paint.ascent()+paint.descent())/2,paint);}
 public void setAlpha(int alpha){paint.setAlpha(alpha);}
 public void setColorFilter(ColorFilter filter){paint.setColorFilter(filter);}
 public int getOpacity(){return PixelFormat.OPAQUE;}
}
