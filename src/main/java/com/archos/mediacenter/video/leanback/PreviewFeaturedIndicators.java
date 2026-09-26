package com.archos.mediacenter.video.leanback;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.view.View;
import android.view.animation.DecelerateInterpolator;

/** Persistent carousel state: retarget from the visible pill widths, never flash replacement dots. */
final class PreviewFeaturedIndicators extends View {
    private final Paint paint=new Paint(Paint.ANTI_ALIAS_FLAG);
    private float[] weights=new float[0];
    private ValueAnimator morph;
    PreviewFeaturedIndicators(Context context){
        super(context);setFocusable(false);setTag("hero:indicators");
        setContentDescription("Featured carousel, Left or Right on More Info to change title");
    }
    void setPosition(int count,int selected){
        if(count<0||count>8)throw new IllegalArgumentException("Invalid Featured count");
        if(morph!=null)morph.cancel();
        if(count==0){weights=new float[0];invalidate();return;}
        int target=Math.floorMod(selected,count);
        if(weights.length!=count){weights=new float[count];weights[target]=1;invalidate();return;}
        float[] origin=weights.clone();
        morph=ValueAnimator.ofFloat(0,1);morph.setDuration(200);morph.setInterpolator(new DecelerateInterpolator());
        morph.addUpdateListener(value->{float progress=(float)value.getAnimatedValue();
            for(int i=0;i<weights.length;i++)weights[i]=origin[i]+((i==target?1:0)-origin[i])*progress;
            invalidate();
        });morph.start();
    }
    @Override protected void onDraw(Canvas canvas){
        super.onDraw(canvas);if(weights.length==0)return;
        float density=getResources().getDisplayMetrics().density;
        float total=weights.length*11*density+19*density,left=(getWidth()-total)/2+3*density;
        float top=(getHeight()-5*density)/2;
        for(float weight:weights){float width=(5+19*weight)*density;
            paint.setColor(androidx.core.graphics.ColorUtils.blendARGB(0x7792aabd,PreviewAccent.color(getContext()),weight));
            canvas.drawRoundRect(left,top,left+width,top+5*density,2.5f*density,2.5f*density,paint);
            left+=width+6*density;
        }
    }
    @Override protected void onDetachedFromWindow(){if(morph!=null)morph.cancel();super.onDetachedFromWindow();}
}
