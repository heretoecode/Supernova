package com.archos.mediacenter.video.leanback;

import android.content.Context;
import android.graphics.*;
import android.graphics.drawable.Drawable;

/** Focus outline for artwork cards: a single crisp line. No underline, no rings and no opaque focus fill. */
public final class PreviewFocusGlow extends Drawable {
    private final Paint paint=new Paint(Paint.ANTI_ALIAS_FLAG);
    private final float density;
    private final int colour;private final float radius;
    private int alpha=255;
    public PreviewFocusGlow(Context context){this(context,6);}
    public PreviewFocusGlow(Context context,float cornerRadius){
        radius=cornerRadius;
        density=context.getResources().getDisplayMetrics().density;
        colour=PreviewAccent.color(context);
        paint.setStyle(Paint.Style.STROKE);
    }
    @Override public void draw(Canvas canvas){
        Rect b=getBounds();float inset=.5f*density;
        RectF edge=new RectF(b.left+inset,b.top+inset,b.right-inset,b.bottom-inset);
        // One crisp outline only: the faint outer rings read as a ghost second line on a TV.
        paint.setColor(colour);paint.setStrokeWidth(density);paint.setAlpha(190*alpha/255);
        canvas.drawRoundRect(edge,radius*density,radius*density,paint);
    }
    @Override public void setAlpha(int value){alpha=value;invalidateSelf();}
    @Override public void setColorFilter(ColorFilter filter){paint.setColorFilter(filter);invalidateSelf();}
    @Override public int getOpacity(){return PixelFormat.TRANSLUCENT;}
}
