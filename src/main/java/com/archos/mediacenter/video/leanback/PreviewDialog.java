package com.archos.mediacenter.video.leanback;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.view.*;
import android.widget.*;
import java.util.function.IntConsumer;
/** Compact, remote-friendly menus shared by Preview controls and Details. */
public final class PreviewDialog {
 public static void choose(Context c,String title,String[] labels,int selected,IntConsumer action){
  Dialog d=new Dialog(c);d.requestWindowFeature(Window.FEATURE_NO_TITLE);
  LinearLayout panel=new LinearLayout(c);panel.setOrientation(1);int pad=dp(c,18);panel.setPadding(pad,pad,pad,pad);panel.setBackground(bg(c,false));
  TextView heading=new TextView(c);heading.setText(title);heading.setTextSize(21);heading.setTextColor(Color.WHITE);heading.setPadding(0,0,0,pad);panel.addView(heading);
  ScrollView scroll=new ScrollView(c);LinearLayout rows=new LinearLayout(c);rows.setOrientation(1);scroll.addView(rows);panel.addView(scroll,new LinearLayout.LayoutParams(-1,-2));
  View initial=null;for(int i=0;i<labels.length;i++){final int index=i;TextView row=new TextView(c);row.setText((i==selected?"✓  ":"    ")+labels[i]);row.setTextSize(15);row.setTextColor(Color.WHITE);row.setPadding(pad,dp(c,11),pad,dp(c,11));row.setFocusable(true);row.setFocusableInTouchMode(true);row.setBackground(bg(c,false));row.setOnFocusChangeListener((v,f)->v.setBackground(bg(c,f)));boolean enabled=!labels[i].contains("unavailable")&&!labels[i].contains("Coming soon");row.setEnabled(enabled);row.setFocusable(enabled);row.setAlpha(enabled?1f:.45f);row.setOnClickListener(v->{d.dismiss();action.accept(index);});rows.addView(row,new LinearLayout.LayoutParams(-1,dp(c,44)));if(initial==null&&enabled||i==selected&&enabled)initial=row;}
  d.setContentView(panel);d.show();d.getWindow().setBackgroundDrawableResource(android.R.color.transparent);d.getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);d.getWindow().setDimAmount(.35f);d.getWindow().setLayout(Math.min(dp(c,440),c.getResources().getDisplayMetrics().widthPixels-dp(c,64)),Math.min(dp(c,76+labels.length*44),c.getResources().getDisplayMetrics().heightPixels-dp(c,70)));if(initial!=null)initial.requestFocus();
 }
 private static int dp(Context c,int v){return Math.round(v*c.getResources().getDisplayMetrics().density);}
 private static GradientDrawable bg(Context c,boolean f){GradientDrawable g=new GradientDrawable();g.setColor(f?0xe02b4a60:0xf0182c3e);g.setCornerRadius(dp(c,6));g.setStroke(dp(c,1),f?0xff59d8ff:0x303d5870);return g;}
}
