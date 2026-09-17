package com.archos.mediacenter.video.player;

import android.app.Dialog;
import android.view.*;
import android.widget.*;
import java.util.*;
import com.archos.mediacenter.video.R;
import com.archos.mediacenter.video.leanback.PreviewDialog;
import com.archos.mediacenter.video.leanback.PreviewIcon;
import com.archos.mediacenter.video.player.tvmenu.*;

/** Presentation over the existing menu actions, with an explicit parent/focus return path. */
final class PreviewPlaybackMenus {
 private static Dialog current;
 private static Runnable restoreParent;
 private static View origin;
 private static void dismissCurrent(){if(current!=null){current.setOnCancelListener(null);current.dismiss();current=null;}}
 static void close(){dismissCurrent();restoreParent=null;if(origin!=null&&origin.isAttachedToWindow())origin.requestFocus();origin=null;}
 static void show(PlayerActivity activity,TVMenuAdapter adapter,String target){
  close();origin=activity.getCurrentFocus();
  if(target!=null)for(TVCardView card:adapter.previewCards())if(target.equals(card.previewTitle())){select(activity,card,()->root(activity,adapter),-1,false);return;}
  root(activity,adapter);
 }
 private static void root(PlayerActivity activity,TVMenuAdapter adapter){
  dismissCurrent();restoreParent=null;List<TVCardView> cards=new ArrayList<>(adapter.previewCards());
  String audio=activity.getString(R.string.menu_audio),subs=activity.getString(R.string.menu_subtitles),speed=activity.getString(R.string.player_pref_audio_speed_title);
  cards.sort(Comparator.comparingInt(c->audio.equals(c.previewTitle())?0:subs.equals(c.previewTitle())?1:2));
  List<String> labels=new ArrayList<>();List<Runnable> actions=new ArrayList<>();TVMenuItem speedItem=null;
  for(TVCardView card:cards){labels.add(card.previewTitle());actions.add(()->select(activity,card,()->root(activity,adapter),-1,false));TVMenu menu=card.previewMenu();if(menu!=null)for(int i=0;i<menu.getChildCount();i++){View v=menu.getChildAt(i);if(v instanceof TVMenuItem&&speed.equals(((TVMenuItem)v).getText())&&v.isEnabled())speedItem=(TVMenuItem)v;}}
  if(speedItem!=null){final TVMenuItem item=speedItem;int at=Math.min(2,labels.size());labels.add(at,speed);actions.add(at,()->{restoreParent=()->root(activity,adapter);item.previewClick();});}
  current=PreviewDialog.choose(activity,"More",labels.toArray(new String[0]),-1,Collections.emptySet(),false,n->actions.get(n).run());
  current.setOnCancelListener(d->close());position(activity,current,true);
 }
 private static void select(PlayerActivity activity,TVCardView card,Runnable parent,int focus,boolean otherLanguages){
  TVMenu menu=card.previewMenu();if(menu==null||menu.getChildCount()==0){dismissCurrent();card.previewClick();return;}
  List<TVMenuItem> actions=new ArrayList<>();List<String> labels=new ArrayList<>();Set<Integer> checked=new HashSet<>();int selected=focus;boolean hasOther=false;
  boolean subtitles=activity.getString(R.string.menu_subtitles).equals(card.previewTitle());
  for(int i=0;i<menu.getChildCount();i++){
   View view=menu.getChildAt(i);if(!(view instanceof TVMenuItem)||view.getVisibility()!=View.VISIBLE)continue;TVMenuItem item=(TVMenuItem)view;
   boolean other=subtitles&&Boolean.FALSE.equals(item.getTag());if(other)hasOther=true;if(otherLanguages?!other:other)continue;
   actions.add(item);labels.add(android.text.Html.fromHtml(item.getText(),0).toString()+(item.isEnabled()&&item.isFocusable()?"":" — unavailable"));if(item.isChecked()){checked.add(actions.size()-1);if(selected<0)selected=actions.size()-1;}
  }
  if(hasOther&&!otherLanguages){actions.add(null);labels.add("Other languages");}
  if(actions.isEmpty()){dismissCurrent();card.previewClick();return;}
  dismissCurrent();restoreParent=()->select(activity,card,parent,focus,otherLanguages);
  current=PreviewDialog.choose(activity,otherLanguages?"Other subtitle languages":card.previewTitle(),labels.toArray(new String[0]),selected,checked,false,n->{
   TVMenuItem item=actions.get(n);
   if(item==null){select(activity,card,()->select(activity,card,parent,n,false),-1,true);return;}
   Dialog before=current;restoreParent=()->select(activity,card,parent,n,otherLanguages);item.previewClick();
   // Track and switch actions update in place. A nested native picker replaces current.
   if(current==before&&before.isShowing())select(activity,card,parent,n,otherLanguages);
  });current.setOnCancelListener(d->{dismissCurrent();parent.run();});position(activity,current,false);
 }
 static void showNested(PlayerActivity activity,TVCardDialog card){
  Runnable parent=restoreParent;dismissCurrent();
  if(card.getParent() instanceof ViewGroup)((ViewGroup)card.getParent()).removeView(card);
  Dialog dialog=new Dialog(activity);dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
  card.setAlpha(1f);card.setPadding(dp(activity,12),dp(activity,10),dp(activity,12),dp(activity,10));card.setBackground(PreviewDialog.surface(activity,false));compact(card,activity);
  card.setPreviewDismiss(()->{dismissCurrent();if(parent!=null)parent.run();else close();});
  dialog.setContentView(card);current=dialog;dialog.setOnCancelListener(d->card.handleBackPressed());dialog.show();
  Window window=dialog.getWindow();window.setBackgroundDrawableResource(android.R.color.transparent);window.setLayout(dp(activity,330),-2);position(activity,dialog,false);
 }
 private static int dp(PlayerActivity a,int n){return PreviewDialog.dp(a,n);}
 private static void compact(View view,PlayerActivity a){
  if(view instanceof TextView){TextView text=(TextView)view;text.setTextSize(view.getId()==R.id.info_text&&view.getParent() instanceof LinearLayout&&!(view.getParent() instanceof TVMenuItem)?15:13);text.setMaxLines(2);text.setEllipsize(android.text.TextUtils.TruncateAt.END);text.setTextColor(0xffe7eff5);text.setMinHeight(0);}
  if(view instanceof TVMenuItem){ViewGroup.LayoutParams lp=view.getLayoutParams();if(lp!=null){lp.height=dp(a,38);view.setLayoutParams(lp);}view.setBackground(PreviewDialog.focus(a));}
  if(view instanceof ViewGroup)for(int i=0;i<((ViewGroup)view).getChildCount();i++)compact(((ViewGroup)view).getChildAt(i),a);
 }
 private static void position(PlayerActivity a,Dialog d,boolean right){Window w=d.getWindow();w.setDimAmount(.12f);w.setGravity(Gravity.TOP|(right?Gravity.END:Gravity.START));WindowManager.LayoutParams p=w.getAttributes();p.x=dp(a,32);p.y=dp(a,74);w.setAttributes(p);}
}
