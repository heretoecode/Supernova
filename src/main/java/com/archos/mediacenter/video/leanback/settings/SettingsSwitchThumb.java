package com.archos.mediacenter.video.leanback.settings;

import android.content.Context;
import android.graphics.*;
import android.graphics.drawable.Drawable;
import com.archos.mediacenter.video.leanback.PreviewDialog;

/** SwitchCompat gives thumbs the track's full height; keep the visible thumb circular. */
final class SettingsSwitchThumb extends Drawable {
    private final int size;private final Paint paint=new Paint(Paint.ANTI_ALIAS_FLAG);
    SettingsSwitchThumb(Context context){size=PreviewDialog.dp(context,22);paint.setColor(Color.WHITE);}
    @Override public int getIntrinsicWidth(){return size;}
    @Override public int getIntrinsicHeight(){return size;}
    @Override public void draw(Canvas canvas){Rect b=getBounds();canvas.drawCircle(b.exactCenterX(),b.exactCenterY(),Math.min(b.width(),b.height())/2f,paint);}
    @Override public void setAlpha(int alpha){paint.setAlpha(alpha);invalidateSelf();}
    @Override public void setColorFilter(ColorFilter filter){paint.setColorFilter(filter);invalidateSelf();}
    @Override public int getOpacity(){return PixelFormat.TRANSLUCENT;}
}
