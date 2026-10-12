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
    @Override public void requestChildFocus(View child,View focused){super.requestChildFocus(child,focused);setForeground(new PreviewFocusGlow(getContext()));}
    @Override public void clearChildFocus(View child){super.clearChildFocus(child);post(()->{if(!hasFocus())setForeground(null);});}
    public void addScrim(){View shade=new View(getContext());shade.setBackground(new GradientDrawable(GradientDrawable.Orientation.LEFT_RIGHT,new int[]{0xe6071522,0xb3071522,0x00071522}));addView(shade,new LayoutParams(-1,-1));}
}
