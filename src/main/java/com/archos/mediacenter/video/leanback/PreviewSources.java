package com.archos.mediacenter.video.leanback;
import android.content.Context;
import android.graphics.drawable.GradientDrawable;
import android.view.*;
import android.widget.*;
import androidx.leanback.widget.*;
import androidx.recyclerview.widget.*;
import androidx.recyclerview.widget.GridLayoutManager;
import java.util.*;
/** Source grid over Nova's existing discovery adapters and click handlers. */
public final class PreviewSources extends FrameLayout {
 private final ObjectAdapter rows;private final OnItemViewClickedListener click;private final RecyclerView grid;private final List<Item> items=new ArrayList<>();private final List<ObjectAdapter> observed=new ArrayList<>();private final Adapter adapter=new Adapter();
 private final ObjectAdapter.DataObserver observer=new ObjectAdapter.DataObserver(){public void onChanged(){refresh();}public void onItemRangeChanged(int s,int n){refresh();}public void onItemRangeInserted(int s,int n){refresh();}public void onItemRangeRemoved(int s,int n){refresh();}};
 private static class Item{Object value;Presenter presenter;ListRow row;String heading;}
 public PreviewSources(Context c,ObjectAdapter rows,OnItemViewClickedListener click){super(c);this.rows=rows;this.click=click;setFocusable(true);setDescendantFocusability(FOCUS_AFTER_DESCENDANTS);setBackgroundColor(0xff132638);grid=new RecyclerView(c);GridLayoutManager layout=new GridLayoutManager(c,4);layout.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup(){public int getSpanSize(int p){return items.get(p).heading==null?1:4;}});grid.setLayoutManager(layout);grid.setItemAnimator(null);grid.setPadding(dp(28),dp(10),dp(28),dp(20));grid.setClipToPadding(false);grid.setAdapter(adapter);addView(grid);}
 protected void onAttachedToWindow(){super.onAttachedToWindow();rows.registerObserver(observer);refresh();}
 protected void onDetachedFromWindow(){rows.unregisterObserver(observer);for(ObjectAdapter a:observed)a.unregisterObserver(observer);observed.clear();super.onDetachedFromWindow();}
 public boolean atTop(){View focus=grid.findFocus();View child=focus==null?null:grid.findContainingItemView(focus);return child!=null&&grid.getChildAdapterPosition(child)<5;}
 private void refresh(){post(()->{if(!isAttachedToWindow())return;Object focused=null;View f=grid.findFocus();View item=f==null?null:grid.findContainingItemView(f);if(item!=null){int p=grid.getChildAdapterPosition(item);if(p>=0&&p<items.size())focused=items.get(p).value;}for(ObjectAdapter a:observed)a.unregisterObserver(observer);observed.clear();items.clear();
  for(int i=0;i<rows.size();i++){Object value=rows.get(i);if(!(value instanceof ListRow))continue;ListRow row=(ListRow)value;ObjectAdapter children=row.getAdapter();observed.add(children);children.registerObserver(observer);if(children.size()==0)continue;Item heading=new Item();heading.heading=String.valueOf(row.getHeaderItem().getName());items.add(heading);for(int j=0;j<children.size();j++){Item e=new Item();e.value=children.get(j);e.presenter=children.getPresenter(e.value);e.row=row;items.add(e);}}
  final Object restore=focused;adapter.notifyDataSetChanged();if(restore!=null)for(int i=0;i<items.size();i++)if(items.get(i).value==restore){final int p=i;grid.scrollToPosition(p);grid.post(()->{RecyclerView.ViewHolder h=grid.findViewHolderForAdapterPosition(p);if(h!=null)h.itemView.requestFocus();});break;}
 });}
 private int dp(int n){return Math.round(n*getResources().getDisplayMetrics().density);}
 private GradientDrawable bg(boolean f){GradientDrawable d=new GradientDrawable();d.setColor(f?0xdd25445c:0xbb192f45);d.setCornerRadius(dp(6));d.setStroke(dp(f?2:1),f?0xff59d8ff:0xff304b60);return d;}
 class Holder extends RecyclerView.ViewHolder{Presenter presenter;Presenter.ViewHolder nativeHolder;Holder(View v){super(v);}}
 class Adapter extends RecyclerView.Adapter<Holder>{public int getItemCount(){return items.size();}public Holder onCreateViewHolder(ViewGroup p,int type){FrameLayout card=new FrameLayout(getContext());RecyclerView.LayoutParams lp=new RecyclerView.LayoutParams(-1,-2);lp.setMargins(0,dp(5),dp(12),dp(10));card.setLayoutParams(lp);return new Holder(card);}public void onBindViewHolder(Holder h,int position){Item item=items.get(position);FrameLayout card=(FrameLayout)h.itemView;if(h.presenter!=null&&h.nativeHolder!=null)h.presenter.onUnbindViewHolder(h.nativeHolder);card.removeAllViews();h.presenter=null;h.nativeHolder=null;
   if(item.heading!=null){TextView label=new TextView(getContext());label.setText(item.heading);label.setTextSize(19);label.setTextColor(0xffa8cce7);label.setPadding(0,dp(10),0,dp(4));card.addView(label);card.setFocusable(false);card.setBackground(null);return;}
   h.presenter=item.presenter;h.nativeHolder=item.presenter.onCreateViewHolder(card);item.presenter.onBindViewHolder(h.nativeHolder,item.value);View nativeView=h.nativeHolder.view;
   if(nativeView instanceof ViewGroup){ViewGroup g=(ViewGroup)nativeView;for(int k=0;k<g.getChildCount();k++){View child=g.getChildAt(k);child.getLayoutParams().width=-1;child.getLayoutParams().height=dp(108);child.setBackgroundColor(android.graphics.Color.TRANSPARENT);}}
   android.widget.ImageView icon=nativeView.findViewById(com.archos.mediacenter.video.R.id.image);if(icon!=null){icon.setScaleType(ImageView.ScaleType.FIT_CENTER);icon.setPadding(dp(14),dp(10),dp(14),dp(10));icon.setBackgroundColor(android.graphics.Color.TRANSPARENT);}
   for(int id:new int[]{com.archos.mediacenter.video.R.id.primary,com.archos.mediacenter.video.R.id.secondary}){TextView label=nativeView.findViewById(id);if(label!=null){label.setTextSize(id==com.archos.mediacenter.video.R.id.primary?13:10);label.setSingleLine(true);}}
   nativeView.setFocusable(false);if(nativeView instanceof ViewGroup)((ViewGroup)nativeView).setDescendantFocusability(FOCUS_BLOCK_DESCENDANTS);card.addView(nativeView,new FrameLayout.LayoutParams(-1,dp(108)));card.setFocusable(true);card.setBackground(bg(false));card.setOnFocusChangeListener((v,f)->v.setBackground(bg(f)));card.setOnClickListener(v->click.onItemClicked(h.nativeHolder,item.value,null,item.row));}
 public void onViewRecycled(Holder h){if(h.presenter!=null)h.presenter.onUnbindViewHolder(h.nativeHolder);}}
}
