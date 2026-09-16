package com.archos.mediacenter.video.player;
import android.app.*;
import android.view.*;
import android.widget.*;
import com.archos.mediacenter.video.leanback.*;
import com.archos.mediacenter.video.browser.adapters.object.*;
final class PreviewPlaybackInfo {
 static void show(Activity a,String title,String episode,Object media,Runnable restart,Runnable full){Dialog d=new Dialog(a);d.requestWindowFeature(Window.FEATURE_NO_TITLE);LinearLayout row=new LinearLayout(a);row.setPadding(dp(a,24),dp(a,24),dp(a,24),dp(a,24));row.setBackground(PreviewDialog.surface(a,false));LinearLayout text=new LinearLayout(a);text.setOrientation(1);row.addView(text,new LinearLayout.LayoutParams(0,-2,1));label(text,title,24);if(!episode.isEmpty())label(text,episode,13);
  if(media instanceof Video){Video v=(Video)media;boolean same=v instanceof Episode?title.equals(((Episode)v).getShowName()):title.equals(v.getName());if(same){String meta=(v instanceof Movie&&((Movie)v).getYear()>0?((Movie)v).getYear()+"  ·  ":"")+(v.getDurationMs()>0?v.getDurationMs()/60000+" min":"");label(text,meta,12);label(text,v.getDescriptionBody(),14);}}
  LinearLayout actions=new LinearLayout(a);actions.setOrientation(1);LinearLayout.LayoutParams lp=new LinearLayout.LayoutParams(dp(a,210),-2);lp.leftMargin=dp(a,28);row.addView(actions,lp);
  String[] labels={"Resume","Play from beginning","File and technical details","Add to List · Coming soon"};for(int i=0;i<labels.length;i++){final int index=i;TextView b=new TextView(a);b.setText(labels[i]);b.setTextColor(-1);b.setTextSize(13);b.setPadding(dp(a,10),0,dp(a,10),0);b.setGravity(Gravity.CENTER_VERTICAL);PreviewIcon.apply(b,labels[i],17);b.setBackground(PreviewDialog.focus(a));b.setFocusable(i<3);b.setEnabled(i<3);if(i==3)b.setAlpha(.4f);b.setOnClickListener(v->{d.dismiss();if(index==1)restart.run();if(index==2)full.run();});actions.addView(b,new LinearLayout.LayoutParams(-1,dp(a,38)));}
  d.setContentView(row);d.show();d.getWindow().setBackgroundDrawableResource(android.R.color.transparent);d.getWindow().setDimAmount(.18f);d.getWindow().setLayout(Math.min(dp(a,860),a.getResources().getDisplayMetrics().widthPixels-dp(a,64)),-2);actions.getChildAt(0).requestFocus();
 }
 private static int dp(Activity a,int v){return PreviewDialog.dp(a,v);}private static void label(LinearLayout p,String s,int size){if(s==null||s.isEmpty())return;TextView t=new TextView(p.getContext());t.setText(s);t.setTextSize(size);t.setTextColor(size>=19?-1:0xffbfd1de);t.setMaxLines(size>=19?2:5);t.setEllipsize(android.text.TextUtils.TruncateAt.END);t.setPadding(0,0,0,PreviewDialog.dp(p.getContext(),10));p.addView(t);}
}
