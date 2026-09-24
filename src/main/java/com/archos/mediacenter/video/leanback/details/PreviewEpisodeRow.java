package com.archos.mediacenter.video.leanback.details;

import android.app.Activity;
import android.view.*;
import androidx.recyclerview.widget.*;
import androidx.leanback.widget.Presenter;
import com.archos.mediacenter.video.browser.adapters.object.*;
import com.archos.mediacenter.video.leanback.*;
import java.util.*;

/** Recycled season rail; long seasons do not inflate or request all artwork at once. */
final class PreviewEpisodeRow extends PreviewFocusRecycler {
    private final List<Episode> episodes;
    private int remembered;
    PreviewEpisodeRow(Activity activity,List<Episode> values){super(activity);episodes=new ArrayList<>(values);setLayoutManager(new LinearLayoutManager(activity,HORIZONTAL,false));setItemAnimator(null);setClipChildren(false);setClipToPadding(false);setPadding(PreviewDialog.dp(activity,5),PreviewDialog.dp(activity,6),PreviewDialog.dp(activity,5),PreviewDialog.dp(activity,6));setAdapter(new RecyclerView.Adapter<RecyclerView.ViewHolder>(){
        public int getItemCount(){return episodes.size();}
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent,int type){PreviewLandscapeCard card=new PreviewLandscapeCard(activity);RecyclerView.LayoutParams size=new RecyclerView.LayoutParams(PreviewDialog.dp(activity,184),-1);size.rightMargin=PreviewDialog.dp(activity,12);card.setLayoutParams(size);return new RecyclerView.ViewHolder(card){};}
        public void onBindViewHolder(RecyclerView.ViewHolder holder,int position){Episode ep=episodes.get(position);PreviewLandscapeCard card=(PreviewLandscapeCard)holder.itemView;card.bind(ep.getEpisodeNumber()+" · "+ep.getEpisodeName(),(ep.getDurationMs()>0?ep.getDurationMs()/60000+" min":"")+(PreviewSeriesJourney.completed(ep)?" · Watched":""),ep.getPictureUri()!=null?ep.getPictureUri():ep.getPreviewBackdrop(),false);card.setTag("episode:"+ep.getId());card.progress.setProgress(com.archos.mediacenter.video.leanback.presenter.PreviewCardPresenter.progress(ep.getResumeMs(),ep.getDurationMs()));card.progress.setVisibility(ep.getResumeMs()>0?View.VISIBLE:View.GONE);card.setOnClickListener(v->new VideoViewClickedListener(activity).onItemClicked(new Presenter.ViewHolder(v),ep,null,null));}
        public void onViewRecycled(RecyclerView.ViewHolder holder){((PreviewLandscapeCard)holder.itemView).release();}
    });}
    @Override public void requestChildFocus(View child,View focused){super.requestChildFocus(child,focused);View item=findContainingItemView(focused);if(item!=null){int p=getChildAdapterPosition(item);if(p>=0)remembered=p;}}
    boolean focusEpisode(long id){for(int i=0;i<episodes.size();i++)if(episodes.get(i).getId()==id){remembered=i;focusRemembered();return true;}return false;}
    void focusRemembered(){
        if(episodes.isEmpty())return;
        scrollToPosition(remembered);
        // Wait for layout, not an arbitrary time delay: off-screen cards do not exist yet.
        final android.view.ViewTreeObserver.OnGlobalLayoutListener listener=new android.view.ViewTreeObserver.OnGlobalLayoutListener(){
            public void onGlobalLayout(){RecyclerView.ViewHolder holder=findViewHolderForAdapterPosition(remembered);if(holder!=null){getViewTreeObserver().removeOnGlobalLayoutListener(this);holder.itemView.requestFocus();}}
        };
        RecyclerView.ViewHolder holder=findViewHolderForAdapterPosition(remembered);
        if(holder!=null)holder.itemView.requestFocus();else{getViewTreeObserver().addOnGlobalLayoutListener(listener);requestLayout();}
    }
    @Override public View focusSearch(View focused,int direction){View next=super.focusSearch(focused,direction);if(direction==FOCUS_LEFT||direction==FOCUS_RIGHT){View cursor=next;while(cursor!=null&&cursor!=this)cursor=cursor.getParent() instanceof View?(View)cursor.getParent():null;if(cursor==null)return focused;}return next;}
}
