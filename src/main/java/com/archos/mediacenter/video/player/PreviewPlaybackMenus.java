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
  String[] labels=new String[cards.size()];for(int i=0;i<cards.size();i++)labels[i]=cards.get(i).previewTitle();current=PreviewDialog.choose(activity,"More",labels,-1,n->select(activity,cards.get(n)));position(activity,current,true);
 }
 private static void select(PlayerActivity activity,TVCardView card){TVMenu menu=card.previewMenu();if(menu==null){card.previewClick();return;}List<TVMenuItem> actions=new ArrayList<>();List<String> labels=new ArrayList<>();int selected=-1;
  for(int i=0;i<menu.getChildCount();i++){View view=menu.getChildAt(i);if(!(view instanceof TVMenuItem)||view.getVisibility()!=View.VISIBLE)continue;TVMenuItem item=(TVMenuItem)view;actions.add(item);labels.add(android.text.Html.fromHtml(item.getText(),0).toString()+(item.isEnabled()&&item.isFocusable()?"":" — unavailable"));if(item.isChecked())selected=actions.size()-1;}
  if(actions.isEmpty())return;current=PreviewDialog.choose(activity,card.previewTitle(),labels.toArray(new String[0]),selected,n->{actions.get(n).previewClick();});position(activity,current,false);
 }
 private static void position(PlayerActivity a,Dialog d,boolean right){Window w=d.getWindow();w.setDimAmount(.12f);w.setGravity(Gravity.TOP|(right?Gravity.END:Gravity.START));WindowManager.LayoutParams p=w.getAttributes();p.x=PreviewDialog.dp(a,32);p.y=PreviewDialog.dp(a,74);w.setAttributes(p);}
}
