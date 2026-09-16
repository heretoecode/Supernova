package com.archos.mediacenter.video.player;
import android.app.Dialog;
import android.view.*;
import java.util.*;
import com.archos.mediacenter.video.leanback.PreviewDialog;
import com.archos.mediacenter.video.player.tvmenu.*;
/** Thin presentation of the native player menu; all handlers and availability come from that model. */
final class PreviewPlaybackMenus {
 private static Dialog current;
 static void close(){if(current!=null){current.dismiss();current=null;}}
 static void show(PlayerActivity activity,TVMenuAdapter adapter,String target){
  close();List<TVCardView> cards=adapter.previewCards();if(target!=null)for(TVCardView card:cards)if(target.equals(card.previewTitle())){select(activity,card);return;}
  String audio=activity.getString(com.archos.mediacenter.video.R.string.menu_audio),subtitles=activity.getString(com.archos.mediacenter.video.R.string.menu_subtitles),speed=activity.getString(com.archos.mediacenter.video.R.string.player_pref_audio_speed_title);
  cards.sort(java.util.Comparator.comparingInt(c->audio.equals(c.previewTitle())?0:subtitles.equals(c.previewTitle())?1:2));
  List<String> labels=new ArrayList<>();List<Runnable> actions=new ArrayList<>();TVMenuItem speedItem=null;
  for(TVCardView card:cards){labels.add(card.previewTitle());actions.add(()->select(activity,card));TVMenu menu=card.previewMenu();if(menu!=null)for(int i=0;i<menu.getChildCount();i++){View v=menu.getChildAt(i);if(v instanceof TVMenuItem&&speed.equals(((TVMenuItem)v).getText())&&v.isEnabled())speedItem=(TVMenuItem)v;}}
  if(speedItem!=null){final TVMenuItem item=speedItem;int at=Math.min(2,labels.size());labels.add(at,speed);actions.add(at,item::previewClick);}
  current=PreviewDialog.choose(activity,"More",labels.toArray(new String[0]),-1,n->actions.get(n).run());position(activity,current,true);
 }
 private static void select(PlayerActivity activity,TVCardView card){TVMenu menu=card.previewMenu();if(menu==null){card.previewClick();return;}List<TVMenuItem> actions=new ArrayList<>();List<String> labels=new ArrayList<>();Set<Integer> checked=new HashSet<>();int selected=-1;
  for(int i=0;i<menu.getChildCount();i++){View view=menu.getChildAt(i);if(!(view instanceof TVMenuItem)||view.getVisibility()!=View.VISIBLE)continue;TVMenuItem item=(TVMenuItem)view;actions.add(item);labels.add(android.text.Html.fromHtml(item.getText(),0).toString()+(item.isEnabled()&&item.isFocusable()?"":" — unavailable"));if(item.isChecked()){checked.add(actions.size()-1);if(selected<0)selected=actions.size()-1;}}
  if(actions.isEmpty())return;current=PreviewDialog.choose(activity,card.previewTitle(),labels.toArray(new String[0]),selected,checked,n->{actions.get(n).previewClick();});position(activity,current,false);
 }
 private static void position(PlayerActivity a,Dialog d,boolean right){Window w=d.getWindow();w.setDimAmount(.12f);w.setGravity(Gravity.TOP|(right?Gravity.END:Gravity.START));WindowManager.LayoutParams p=w.getAttributes();p.x=PreviewDialog.dp(a,32);p.y=PreviewDialog.dp(a,74);w.setAttributes(p);}
}
