package com.archos.mediacenter.video.leanback.details;
import android.content.Context;
/** Scale the FD-033 960×540 logical reference, including compact TV viewports. */
final class DetailsDimensions {
 private DetailsDimensions(){}
 static float scale(Context c){android.content.res.Configuration cfg=c.getResources().getConfiguration();return Math.max(.45f,Math.min(cfg.screenWidthDp/960f,cfg.screenHeightDp/540f));}
 static int px(Context c,float value){return Math.max(1,Math.round(value*scale(c)*c.getResources().getDisplayMetrics().density));}
 static int sp(Context c,float value){return Math.max(1,Math.round(value*scale(c)));}
}
