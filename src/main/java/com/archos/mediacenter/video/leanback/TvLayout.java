package com.archos.mediacenter.video.leanback;

import android.content.Context;
import android.view.View;
import android.view.WindowInsets;

/** Logical Android TV viewport tokens. Physical resolution is deliberately irrelevant. */
public final class TvLayout {
 private TvLayout(){}
 public static float width(Context c){return c.getResources().getConfiguration().screenWidthDp;}
 public static float height(Context c){return c.getResources().getConfiguration().screenHeightDp;}
 public static float scale(Context c){return Math.max(.85f,Math.min(1.25f,Math.min(width(c)/960f,height(c)/540f)));}
 public static int px(Context c,float dp){return Math.round(dp*c.getResources().getDisplayMetrics().density);}
 public static int dimension(Context c,float baseline){return px(c,baseline*scale(c));}
 public static int safeWidth(Context c){return Math.max(1,px(c,width(c))-2*dimension(c,28));}
 public static int safeHeight(Context c){return Math.max(1,px(c,height(c))-2*dimension(c,24));}
 public static int dialogWidth(Context c){return Math.min(Math.round(safeWidth(c)*.9f),Math.max(px(c,320),Math.min(px(c,560),Math.round(safeWidth(c)*.40f))));}
 public static int gridColumns(Context c){return width(c)<780?4:width(c)<920?5:width(c)>1180?8:6;}
 /** Keep system bars/cutouts outside the shared navigation and safe content region. */
 public static void applyInsets(View root){root.setOnApplyWindowInsetsListener((v,insets)->{if(android.os.Build.VERSION.SDK_INT>=30){android.graphics.Insets edge=insets.getInsets(WindowInsets.Type.systemBars()|WindowInsets.Type.displayCutout());v.setPadding(edge.left,edge.top,edge.right,edge.bottom);}else v.setPadding(insets.getSystemWindowInsetLeft(),insets.getSystemWindowInsetTop(),insets.getSystemWindowInsetRight(),insets.getSystemWindowInsetBottom());return insets;});root.requestApplyInsets();}
}
