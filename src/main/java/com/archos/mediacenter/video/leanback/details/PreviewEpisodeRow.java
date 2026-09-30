package com.archos.mediacenter.video.leanback.details;

import android.app.Activity;
import android.view.*;
import androidx.recyclerview.widget.*;
import androidx.leanback.widget.Presenter;
import com.archos.mediacenter.video.browser.adapters.object.*;
import com.archos.mediacenter.video.leanback.*;
import java.util.*;
import com.archos.mediacenter.video.streaming.*;
import android.net.Uri;

/** Recycled season rail; long seasons do not inflate or request all artwork at once. */
final class PreviewEpisodeRow extends PreviewFocusRecycler {
    private final List<PreviewEpisodeChoice> episodes;
    private int remembered;
    PreviewEpisodeRow(Activity activity,List<PreviewEpisodeChoice> values,long showId,StreamingRepository.Availability availability){super(activity);episodes=new ArrayList<>(values);setLayoutManager(new LinearLayoutManager(activity,HORIZONTAL,false));setItemAnimator(null);setClipChildren(false);setClipToPadding(false);setPadding(PreviewDialog.dp(activity,5),PreviewDialog.dp(activity,6),PreviewDialog.dp(activity,5),PreviewDialog.dp(activity,6));setAdapter(new RecyclerView.Adapter<RecyclerView.ViewHolder>(){
        public int getItemCount(){return episodes.size();}
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent,int type){PreviewLandscapeCard card=new PreviewLandscapeCard(activity);RecyclerView.LayoutParams size=new RecyclerView.LayoutParams(cardWidth(),-1);size.rightMargin=PreviewDialog.dp(activity,12);card.setLayoutParams(size);return new RecyclerView.ViewHolder(card){};}
        public void onBindViewHolder(RecyclerView.ViewHolder holder,int position){
            PreviewEpisodeChoice choice=episodes.get(position);Episode ep=choice.local;PreviewLandscapeCard card=(PreviewLandscapeCard)holder.itemView;
            card.progress.setVisibility(View.GONE);card.setOnClickListener(null);card.setClickable(false);
            if(ep!=null){
                card.bind(ep.getEpisodeNumber()+" · "+ep.getEpisodeName(),(ep.getDurationMs()>0?ep.getDurationMs()/60000+" min":"")+(PreviewSeriesJourney.completed(ep)?" · Watched":""),ep.getPictureUri()!=null?ep.getPictureUri():ep.getPreviewBackdrop(),true,ep.getId(),"details.episodes","episode_still");
                card.setTag("episode:"+ep.getId());card.progress.setProgress(com.archos.mediacenter.video.leanback.presenter.PreviewCardPresenter.progress(ep.getResumeMs(),ep.getDurationMs()));card.progress.setVisibility(ep.getResumeMs()>0?View.VISIBLE:View.GONE);
                card.setOnClickListener(v->new VideoViewClickedListener(activity).onItemClicked(new Presenter.ViewHolder(v),ep,null,null));return;
            }
            org.json.JSONObject remote=choice.remote;String name=choice.number+" · "+remote.optString("name","Episode "+choice.number),still=remote.optString("still_path");int runtime=remote.optInt("runtime");
            List<StreamingRepository.Offer> offers=availability!=null&&StreamingRepository.prefs(activity).getBoolean(StreamingRepository.ENABLED,false)&&aired(remote.optString("air_date"))?StreamingRepository.filter(availability,StreamingRepository.selected(activity),StreamingRepository.preferred(activity)):Collections.emptyList();
            boolean exact=StreamingRepository.exactEpisodeAvailability(availability,showId,choice.season,choice.number);
            card.bind(name,(runtime>0?runtime+" min · ":"")+(availability==null||!offers.isEmpty()&&!exact?"Availability unknown":offers.isEmpty()?"Unavailable":offers.get(0).provider.name),still.matches("/[A-Za-z0-9._-]+")?Uri.parse("https://image.tmdb.org/t/p/w780"+still):null,exact&&!offers.isEmpty(),remote.optLong("id"),"details.episodes","episode_still");card.setTag("remote-episode:"+choice.season+":"+choice.number);
            if(exact&&!offers.isEmpty()){
                StreamingRepository.Provider provider=offers.get(0).provider;card.availability.setImageDrawable(new PreviewIcon("streaming"));card.availability.setColorFilter(android.graphics.Color.WHITE,android.graphics.PorterDuff.Mode.SRC_IN);card.availability.setImageAlpha(205);
                if(provider.logo!=null&&provider.logo.matches("/[A-Za-z0-9._-]+")){Uri logo=Uri.parse("https://image.tmdb.org/t/p/w154"+provider.logo);com.archos.mediacenter.video.diagnostics.ArtworkRequest.load(card.availability,logo,remote.optLong("id"),"details.episodes","provider_mark",com.squareup.picasso.Picasso.get().load(logo));}
                card.setOnClickListener(v->{String[] labels=new String[offers.size()];for(int i=0;i<labels.length;i++)labels[i]="Watch on "+offers.get(i).provider.name;
                    PreviewDialog.choose(activity,name,labels,0,index->StreamingRepository.IO.submit(()->{
                        String resolved=StreamingRepository.episodeLink(activity.getApplicationContext(),showId,choice.season,choice.number,availability,offers.get(index).provider.id);
                        com.archos.mediacenter.video.diagnostics.Diagnostics.event("episode_provider_link","season",choice.season,"episode",choice.number,"exact_scope",true);
                        activity.runOnUiThread(()->{if(!activity.isFinishing()&&!activity.isDestroyed())StreamingActions.openWeb(activity,StreamingRepository.safeWebUrl(resolved)?resolved:availability.watchUrl);});
                    }));
                });
            }
        }
        public void onViewRecycled(RecyclerView.ViewHolder holder){((PreviewLandscapeCard)holder.itemView).release();}
    });}
    static boolean aired(String date){return date!=null&&date.matches("[0-9]{4}-[0-9]{2}-[0-9]{2}")&&date.compareTo(new java.text.SimpleDateFormat("yyyy-MM-dd",java.util.Locale.ROOT).format(new Date()))<=0;}
    boolean focusRemote(String key){for(int i=0;i<episodes.size();i++){PreviewEpisodeChoice choice=episodes.get(i);if(key.equals("remote-episode:"+choice.season+":"+choice.number)){remembered=i;focusRemembered();return true;}}return false;}
    private int cardWidth(){int width=getWidth()>0?getWidth():getResources().getDisplayMetrics().widthPixels-PreviewDialog.dp(getContext(),84);return Math.max(1,(width-getPaddingLeft()-getPaddingRight()-PreviewDialog.dp(getContext(),36))/4);}
    @Override public void requestChildFocus(View child,View focused){super.requestChildFocus(child,focused);View item=findContainingItemView(focused);if(item!=null){int p=getChildAdapterPosition(item);if(p>=0)remembered=p;}}
    boolean focusEpisode(long id){for(int i=0;i<episodes.size();i++)if(episodes.get(i).local!=null&&episodes.get(i).local.getId()==id){remembered=i;focusRemembered();return true;}return false;}
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
