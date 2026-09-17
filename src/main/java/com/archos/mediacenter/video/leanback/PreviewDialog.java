package com.archos.mediacenter.video.leanback;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.StateListDrawable;
import android.view.*;
import android.widget.*;
import java.util.function.IntConsumer;
/** Content-sized Nova menus. The caller still owns every real action and selection. */
public final class PreviewDialog {
 public static Dialog read(Context c,String title,String body){
  Dialog dialog=new Dialog(c);dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);LinearLayout panel=new LinearLayout(c);panel.setOrientation(1);panel.setPadding(dp(c,20),dp(c,16),dp(c,20),dp(c,16));panel.setBackground(surface(c,false));
  TextView heading=new TextView(c);heading.setText(title);heading.setTextSize(20);heading.setTextColor(Color.WHITE);panel.addView(heading);
  ScrollView scroll=new ScrollView(c);TextView text=new TextView(c);text.setText(body);text.setTextColor(0xffd2e1ed);text.setTextSize(13);text.setPadding(0,dp(c,14),0,dp(c,14));scroll.addView(text);scroll.setFocusable(true);panel.addView(scroll,new LinearLayout.LayoutParams(-1,0,1));
  LinearLayout actions=new LinearLayout(c);for(String label:new String[]{"Close","Copy"}){TextView button=new TextView(c);button.setText(label);button.setTextSize(13);button.setTextColor(Color.WHITE);button.setGravity(Gravity.CENTER);button.setFocusable(true);button.setBackground(focus(c));button.setOnClickListener(v->{if(label.equals("Close"))dialog.dismiss();else{android.content.ClipboardManager clipboard=(android.content.ClipboardManager)c.getSystemService(Context.CLIPBOARD_SERVICE);if(clipboard!=null)clipboard.setPrimaryClip(android.content.ClipData.newPlainText(title,body));Toast.makeText(c,"Copied",Toast.LENGTH_SHORT).show();}});actions.addView(button,new LinearLayout.LayoutParams(dp(c,90),dp(c,38)));}panel.addView(actions);
  dialog.setContentView(panel);dialog.show();Window w=dialog.getWindow();w.setBackgroundDrawableResource(android.R.color.transparent);w.setDimAmount(.35f);w.setLayout(Math.min(dp(c,720),c.getResources().getDisplayMetrics().widthPixels-dp(c,56)),Math.min(dp(c,420),c.getResources().getDisplayMetrics().heightPixels-dp(c,56)));scroll.requestFocus();return dialog;
 }
 public static Dialog choose(Context c,String title,String[] labels,int selected,IntConsumer action){
  return choose(c,title,labels,selected,selected<0?java.util.Collections.emptySet():java.util.Collections.singleton(selected),action);
 }
 public static Dialog choose(Context c,String title,String[] labels,int selected,java.util.Set<Integer> checked,IntConsumer action){
  return choose(c,title,labels,selected,checked,true,action);
 }
 public static Dialog choose(Context c,String title,String[] labels,int selected,java.util.Set<Integer> checked,boolean dismissOnSelect,IntConsumer action){
  Dialog d=new Dialog(c);d.requestWindowFeature(Window.FEATURE_NO_TITLE);
  LinearLayout panel=new LinearLayout(c);panel.setOrientation(1);int pad=dp(c,12);panel.setPadding(pad,pad,pad,pad);panel.setBackground(surface(c,false));
  TextView heading=new TextView(c);heading.setText(title);heading.setTextSize(17);heading.setTextColor(Color.WHITE);heading.setPadding(dp(c,6),dp(c,2),0,dp(c,12));panel.addView(heading);
  ScrollView scroll=new ScrollView(c);scroll.setVerticalScrollBarEnabled(false);LinearLayout rows=new LinearLayout(c);rows.setOrientation(1);scroll.addView(rows);panel.addView(scroll,new LinearLayout.LayoutParams(-1,-2));
  View initial=null;int height=60;
  for(int i=0;i<labels.length;i++){final int index=i;boolean group=labels[i].startsWith("— ");boolean enabled=!group&&!labels[i].contains("unavailable")&&!labels[i].contains("Coming soon");
   LinearLayout row=new LinearLayout(c);row.setGravity(Gravity.CENTER_VERTICAL);row.setPadding(dp(c,10),0,dp(c,10),0);row.setBackground(focus(c));row.setFocusable(enabled);row.setFocusableInTouchMode(enabled);row.setEnabled(enabled);row.setDescendantFocusability(ViewGroup.FOCUS_BLOCK_DESCENDANTS);row.setAlpha(enabled?1f:group?1f:.4f);
   if(!group){ImageView icon=new ImageView(c);String iconLabel=title.equals(c.getString(com.archos.mediacenter.video.R.string.menu_audio))?"Audio":title.equals(c.getString(com.archos.mediacenter.video.R.string.menu_subtitles))?"Subtitles":labels[i];icon.setImageDrawable(new PreviewIcon(iconLabel));row.addView(icon,new LinearLayout.LayoutParams(dp(c,18),dp(c,18)));}
   TextView label=new TextView(c);label.setText(group?labels[i].substring(2):labels[i]);label.setTextSize(group?11:14);label.setTextColor(group?0xff8aaec5:Color.WHITE);label.setSingleLine(true);label.setEllipsize(android.text.TextUtils.TruncateAt.END);LinearLayout.LayoutParams lp=new LinearLayout.LayoutParams(0,-2,1);lp.leftMargin=group?0:dp(c,10);row.addView(label,lp);
   if(checked.contains(i)){ImageView check=new ImageView(c);check.setImageDrawable(new PreviewIcon("check"));row.addView(check,new LinearLayout.LayoutParams(dp(c,18),dp(c,18)));}
   row.setTag(i);row.setContentDescription(labels[i]+(checked.contains(i)?", selected":""));row.setOnClickListener(v->{if(dismissOnSelect)d.dismiss();action.accept(index);});int rh=group?25:37;height+=rh;rows.addView(row,new LinearLayout.LayoutParams(-1,dp(c,rh)));if(enabled&&(initial==null||i==selected))initial=row;
  }
  d.setContentView(panel);d.show();Window w=d.getWindow();w.setBackgroundDrawableResource(android.R.color.transparent);w.addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);w.setDimAmount(.32f);w.setLayout(Math.min(dp(c,title.equals("More")?330:280),c.getResources().getDisplayMetrics().widthPixels-dp(c,64)),Math.min(dp(c,height),c.getResources().getDisplayMetrics().heightPixels-dp(c,64)));if(initial!=null)initial.requestFocus();return d;
 }
 public static int dp(Context c,int v){return Math.round(v*c.getResources().getDisplayMetrics().density);}
 public static StateListDrawable focus(Context c){StateListDrawable s=new StateListDrawable();s.addState(new int[]{android.R.attr.state_focused},surface(c,true));s.addState(new int[]{},new android.graphics.drawable.ColorDrawable(Color.TRANSPARENT));return s;}
 public static GradientDrawable surface(Context c,boolean f){GradientDrawable g=new GradientDrawable();g.setColor(f?0x60416b84:0xef0b1b29);g.setCornerRadius(dp(c,6));g.setStroke(dp(c,1),f?0xff59d8ff:0x50426a80);return g;}
}
