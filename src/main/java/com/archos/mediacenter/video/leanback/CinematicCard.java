package com.archos.mediacenter.video.leanback;

import android.content.Context;
import android.graphics.*;
import android.graphics.drawable.GradientDrawable;
import android.view.*;
import android.widget.FrameLayout;

/** Shared artwork region, visually active while its real action buttons hold focus. */
public final class CinematicCard extends FrameLayout {
    public CinematicCard(Context c){super(c);setFocusable(false);setClickable(false);setClipChildren(false);setClipToPadding(false);
        GradientDrawable background=new GradientDrawable();background.setColor(0xff07131f);background.setCornerRadius(PreviewDialog.dp(c,12));setBackground(background);setClipToOutline(true);}
    @Override public void requestChildFocus(View child,View focused){super.requestChildFocus(child,focused);setForeground(new PreviewFocusGlow(getContext(),12));}
    @Override public void clearChildFocus(View child){super.clearChildFocus(child);post(()->{if(!hasFocus())setForeground(null);});}
    /** Details keeps artwork colour outside the text column. Home retains its own scrim. */
    public void addTextScrim(){View shade=new View(getContext());shade.setTag("details.text.scrim");shade.setBackground(new android.graphics.drawable.Drawable(){
        private final Paint paint=new Paint(Paint.ANTI_ALIAS_FLAG);
        public void draw(Canvas canvas){Rect b=getBounds();paint.setShader(new LinearGradient(b.left,0,b.right,0,new int[]{0xd9000000,0x80000000,0x00000000},new float[]{0,.35f,.68f},Shader.TileMode.CLAMP));canvas.drawRect(b,paint);}
        public void setAlpha(int alpha){paint.setAlpha(alpha);}public void setColorFilter(ColorFilter filter){paint.setColorFilter(filter);}public int getOpacity(){return PixelFormat.TRANSLUCENT;}
    });addView(shade,new LayoutParams(-1,-1));}
    public void addScrim(){View shade=new View(getContext());shade.setBackground(new GradientDrawable(GradientDrawable.Orientation.LEFT_RIGHT,new int[]{0xe6071522,0xb3071522,0x00071522}));addView(shade,new LayoutParams(-1,-1));}
}
