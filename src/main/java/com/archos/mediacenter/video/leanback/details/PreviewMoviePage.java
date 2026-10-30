package com.archos.mediacenter.video.leanback.details;

import android.app.*;
import android.content.*;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.net.Uri;
import android.view.*;
import android.widget.*;
import androidx.leanback.widget.*;
import com.archos.mediacenter.video.browser.adapters.object.*;
import com.archos.mediacenter.video.leanback.*;
import com.archos.mediacenter.video.leanback.PreviewLibraryLoader.*;
import com.archos.mediacenter.video.leanback.presenter.PreviewCardPresenter;
import com.archos.mediacenter.video.streaming.StreamingRepository;
import com.archos.mediacenter.video.streaming.PreviewProviderIcons;
import com.archos.mediascraper.*;
import java.util.*;
import java.util.function.*;

/** Presentation over the existing details fragment: playback, edits and deletion keep native handlers. */
public final class PreviewMoviePage extends ScrollView {
    private final LinearLayout body,cast,crew,details,related,trailers;
    private final Map<View,View> extraHeadings=new IdentityHashMap<>();
    private final ConnectedDetailsPanel sectionFrame;private final ScrollView reading;
    private final LinearLayout heroPage,tabBar,information;private final CinematicCard detailsCard;private final ImageView cardArtwork;private final FrameLayout lower;private final List<TextView> sectionTabs=new ArrayList<>();private String activeSection="Details";private String boundTitle="";private int versionCount;private final List<PreviewEpisodeRow> episodeRows=new ArrayList<>();private final List<PreviewLandscapeCard> landscapeCards=new ArrayList<>(),extraCards=new ArrayList<>();
    private final TextView title,meta,plot,play,trailer,context,moreButton,matchMetadata;
    private final LinearLayout pills;
    private final LinearLayout providerActions;
    private TextView versions;
    private Runnable chooseVersions=()->{};
    private Dialog moreDialog;
    private final Map<Integer,Action> moreRows=new HashMap<>();
    private ObjectAdapter observedActions;
    private final com.archos.mediacenter.video.streaming.StreamingActionPresenter providerPresenter=new com.archos.mediacenter.video.streaming.StreamingActionPresenter();
    private final List<Presenter.ViewHolder> providerHolders=new ArrayList<>();
    private final ObjectAdapter.DataObserver actionObserver=new ObjectAdapter.DataObserver(){public void onChanged(){renderProviders();}public void onItemRangeChanged(int start,int count){renderProviders();}public void onItemRangeInserted(int start,int count){renderProviders();}public void onItemRangeRemoved(int start,int count){renderProviders();}};
    private final Supplier<ObjectAdapter> actions;
    private final Consumer<Action> action;
    private final Runnable nativeDetails;
    private final Consumer<Uri> artwork;
    private Video movie;
    private Tvshow show;private int seasonNumber=-1;private boolean compactCard;private SharedMetadata.Facts latestSummary;
    private Runnable showPlay;
    private final java.util.SortedMap<Integer,java.util.List<Episode>> seasons=new java.util.TreeMap<>();
    private LinearLayout episodes;
    private BaseTags tags;
    private final Map<String,ImageView> portraits=new HashMap<>();
    private List<ScraperTrailer> trailerList=Collections.emptyList();
    private Snapshot snapshot;
    private PreviewDetailsData.Result enrichment;private java.util.concurrent.Future<?> enrichmentTask;private String enrichmentKey="";private int enrichmentGeneration;private org.json.JSONObject remoteDetails;private String remoteKind;private long remoteId;
    private final List<Presenter.ViewHolder> cards=new ArrayList<>();
    private PreviewDetailsData.Result pendingEnrichment;private int pendingEnrichmentGeneration;private boolean enrichmentDeliveryScheduled;
    private final Runnable deliverEnrichment=()->{enrichmentDeliveryScheduled=false;PreviewDetailsData.Result result=pendingEnrichment;pendingEnrichment=null;if(result!=null)applyEnrichment(result,pendingEnrichmentGeneration);};
    /** Cached sections completed within one frame enter as one coherent update. Slow I/O never blocks Back. */
    private void enqueueEnrichment(PreviewDetailsData.Result result,int generation){if(generation!=enrichmentGeneration)return;if(pendingEnrichmentGeneration!=generation)pendingEnrichment=null;pendingEnrichmentGeneration=generation;pendingEnrichment=PreviewDetailsData.merge(pendingEnrichment,result);if(!enrichmentDeliveryScheduled){enrichmentDeliveryScheduled=true;postOnAnimation(deliverEnrichment);}}
    private String informationSignature="",peopleSignature="",receptionSignature="";
    private DetailsBlueprintPanel informationPanel;
    private com.squareup.picasso.Picasso artworkLoader;
    private String artworkUri,failedArtworkUri;private boolean artworkReady;private long openedAt,artworkRetryAfter;
    private DetailsReviews reviews;
    private final PreviewCardPresenter presenter=new PreviewCardPresenter(PreviewCardPresenter.Style.CONTINUE);
    public PreviewMoviePage(Context c,Supplier<ObjectAdapter> actions,Consumer<Action> action,Runnable nativeDetails,Consumer<Uri> artwork){
        super(c);setBackground(new PreviewUtilityBackground(c));this.actions=actions;this.action=action;this.nativeDetails=nativeDetails;this.artwork=artwork;
        setFillViewport(true);setVerticalScrollBarEnabled(false);setFocusable(false);setFocusableInTouchMode(false);setSmoothScrollingEnabled(false);setClipToPadding(false);setClipChildren(false);body=new LinearLayout(c);body.setOrientation(LinearLayout.VERTICAL);body.setPadding(dp(42),0,dp(42),dp(24));body.setClipChildren(false);addView(body);
        heroPage=new LinearLayout(c);heroPage.setOrientation(LinearLayout.VERTICAL);heroPage.setPadding(0,dp(24),0,0);heroPage.setClipChildren(false);body.addView(heroPage,new LinearLayout.LayoutParams(-1,dp(488)));
        detailsCard=new CinematicCard(c);detailsCard.setTag("semantic:details.card");heroPage.addView(detailsCard,new LinearLayout.LayoutParams(-1,0,1));
        cardArtwork=new ImageView(c);cardArtwork.setScaleType(ImageView.ScaleType.CENTER_CROP);detailsCard.addView(cardArtwork,new FrameLayout.LayoutParams(-1,-1));detailsCard.addTextScrim();
        LinearLayout cardCopy=new LinearLayout(c);cardCopy.setTag("details.copy");cardCopy.setOrientation(LinearLayout.VERTICAL);cardCopy.setPadding(dp(24),dp(18),dp(24),dp(16));detailsCard.addView(cardCopy,new FrameLayout.LayoutParams(-1,-1));
        title=text("",34);title.setTypeface(android.graphics.Typeface.create("sans-serif-light",android.graphics.Typeface.NORMAL));title.setMaxLines(2);title.setGravity(Gravity.CENTER_VERTICAL);title.setEllipsize(android.text.TextUtils.TruncateAt.END);cardCopy.addView(title,new LinearLayout.LayoutParams(dp(430),dp(96)));
        meta=text("",18);meta.setSingleLine(true);meta.setEllipsize(android.text.TextUtils.TruncateAt.END);meta.setPadding(0,dp(10),0,dp(4));cardCopy.addView(meta);
        context=text("",13);context.setTextColor(0xffe1e9ef);context.setMaxLines(1);context.setEllipsize(android.text.TextUtils.TruncateAt.END);cardCopy.addView(context);
        pills=new LinearLayout(c);pills.setVisibility(GONE);cardCopy.addView(pills);
        plot=text("",14);plot.setMaxLines(4);plot.setLineSpacing(dp(2),1);plot.setEllipsize(android.text.TextUtils.TruncateAt.END);LinearLayout.LayoutParams plotSize=new LinearLayout.LayoutParams(Math.min(dp(440),getResources().getDisplayMetrics().widthPixels-dp(84)),dp(76));plotSize.topMargin=dp(8);View synopsisSpace=new View(c);cardCopy.addView(synopsisSpace,new LinearLayout.LayoutParams(1,0,1));cardCopy.addView(plot,plotSize);
        matchMetadata=button("Match Metadata",()->{Action match=matchingAction();if(match!=null)action.accept(match);});matchMetadata.setVisibility(GONE);cardCopy.addView(matchMetadata,new LinearLayout.LayoutParams(-2,dp(38)));
        LinearLayout buttons=new LinearLayout(c);buttons.setTag("details.actions");buttons.setClipChildren(false);buttons.setPadding(0,dp(12),0,dp(10));cardCopy.addView(new View(c),new LinearLayout.LayoutParams(1,0,1));cardCopy.addView(buttons);
        trailer=button("Play Trailer",this::chooseTrailer);trailer.setVisibility(GONE);play=button("Play",this::play);buttons.addView(play);providerActions=new LinearLayout(c);providerActions.setClipChildren(false);buttons.addView(providerActions);moreButton=button("More",this::more);margin(buttons,moreButton);
        versions=button("Versions",()->chooseVersions.run());versions.setVisibility(GONE);
        tabBar=new PreviewToolbar(c);tabBar.setClipChildren(false);LinearLayout.LayoutParams tabsSize=new LinearLayout.LayoutParams(-1,dp(44));tabsSize.topMargin=dp(14);sectionFrame=new ConnectedDetailsPanel(c);LinearLayout.LayoutParams sectionSize=new LinearLayout.LayoutParams(-1,dp(220));sectionSize.topMargin=dp(22);body.addView(sectionFrame,sectionSize);((PreviewToolbar)tabBar).setConnected(true);tabBar.setPadding(0,0,0,0);tabsSize.topMargin=0;sectionFrame.addView(tabBar,tabsSize);
        episodes=section("Episodes");cast=section("Cast");crew=section("Crew");details=section("Details");related=section("More Like This");trailers=section("Extras");
        lower=new FrameLayout(c);lower.setClipChildren(false);reading=new ScrollView(c){@Override protected void onMeasure(int w,int h){super.onMeasure(w,h);if("Details".equals(activeSection)&&getChildCount()>0)getChildAt(0).measure(MeasureSpec.makeMeasureSpec(Math.max(1,getMeasuredWidth()-getPaddingLeft()-getPaddingRight()),MeasureSpec.EXACTLY),MeasureSpec.makeMeasureSpec(Math.max(1,getMeasuredHeight()-getPaddingTop()-getPaddingBottom()),MeasureSpec.EXACTLY));}@Override protected void onLayout(boolean changed,int l,int t,int r,int b){super.onLayout(changed,l,t,r,b);revealContentFocus();}@Override protected void dispatchDraw(android.graphics.Canvas canvas){int saved=canvas.save();canvas.clipRect(0,getScrollY(),getWidth(),getScrollY()+getHeight());super.dispatchDraw(canvas);canvas.restoreToCount(saved);}};reading.setTag("details.reading.overflow");reading.setFillViewport(true);reading.setClipToPadding(false);reading.setSmoothScrollingEnabled(false);reading.setFocusable(false);reading.setPadding(dp(8),dp(6),dp(8),dp(8));reading.addView(lower,new ScrollView.LayoutParams(-1,-2));sectionFrame.addView(reading,new LinearLayout.LayoutParams(-1,0,1));information=new LinearLayout(c);information.setOrientation(LinearLayout.HORIZONTAL);lower.addView(information,new FrameLayout.LayoutParams(-1,-1));
        for(LinearLayout section:new LinearLayout[]{cast,crew,details}){View wrapper=(View)section.getParent();body.removeView(wrapper);if(section==details){wrapper.setPadding(0,0,0,0);details.setPadding(0,0,0,0);information.addView(wrapper,new LinearLayout.LayoutParams(-1,-1));details.setLayoutParams(new LinearLayout.LayoutParams(-1,-1));}}
        for(LinearLayout section:new LinearLayout[]{episodes,related,trailers}){View wrapper=(View)section.getParent();body.removeView(wrapper);lower.addView(wrapper,new FrameLayout.LayoutParams(-1,-2));}
        ((View)cast.getParent()).setVisibility(GONE);((View)crew.getParent()).setVisibility(GONE);
        rebuildTabs();
    }
    @Override protected void onMeasure(int widthSpec,int heightSpec){
        int available=View.MeasureSpec.getSize(heightSpec);
        if(available>0&&heroPage!=null){
            int viewport=Math.max(0,available-getPaddingTop()-getPaddingBottom());
            int heroHeight=Math.max(dp(180),viewport-dp(90));
            boolean compact=viewport<dp(380);compactCard=compact;heroPage.setPadding(0,dp(compact?12:24),0,0);LinearLayout.LayoutParams tabs=(LinearLayout.LayoutParams)tabBar.getLayoutParams();tabs.topMargin=0;int cardHeight=heroHeight-heroPage.getPaddingTop()-heroPage.getPaddingBottom()-dp(12);
            ViewGroup copy=detailsCard.findViewWithTag("details.copy");copy.setPadding(dp(24),dp(compact?8:18),dp(24),dp(compact?8:16));title.getLayoutParams().width=Math.round((View.MeasureSpec.getSize(widthSpec)-body.getPaddingLeft()-body.getPaddingRight())*.49f);title.getLayoutParams().height=Math.min(TvLayout.dimension(getContext(),96),Math.max(dp(40),Math.round(cardHeight*.24f)));title.setTextSize((compact?24:34)*TvLayout.scale(getContext()));title.setMaxLines(compact?1:2);androidx.core.widget.TextViewCompat.setAutoSizeTextTypeUniformWithConfiguration(title,Math.round(18*TvLayout.scale(getContext())),Math.round((compact?24:34)*TvLayout.scale(getContext())),1,android.util.TypedValue.COMPLEX_UNIT_SP);meta.setTextSize(16*TvLayout.scale(getContext()));meta.setMaxLines(1);meta.setPadding(0,dp(10),0,dp(5));plot.setMaxLines(compact?1:cardHeight<dp(330)?2:4);plot.getLayoutParams().height=-2;LinearLayout.LayoutParams synopsis=(LinearLayout.LayoutParams)plot.getLayoutParams();synopsis.topMargin=dp(compact?4:8);View actions=detailsCard.findViewWithTag("details.actions");actions.setPadding(0,dp(compact?3:12),0,dp(compact?3:10));if(latestSummary!=null)bindSummary();
            // Navigation measures the stage twice. Resolve before child measurement,
            // not from an intermediate layout size, so the lower-content teaser stays visible.
            if(heroPage.getLayoutParams().height!=heroHeight)heroPage.getLayoutParams().height=heroHeight;
            tabBar.getLayoutParams().height=Math.max(dp(26),Math.round(available*5f/72));
            ((LinearLayout.LayoutParams)sectionFrame.getLayoutParams()).topMargin=dp(22);
            sectionFrame.getLayoutParams().height=Math.max(dp(90),available-Math.round(available/6f)-Math.round(available*.04f));
            if(lower!=null)lower.getLayoutParams().height=activeSection.equals("Details")?Math.max(1,sectionFrame.getLayoutParams().height-tabBar.getLayoutParams().height-reading.getPaddingTop()-reading.getPaddingBottom()):LayoutParams.WRAP_CONTENT;
        }
        int safeMargin=Math.round(MeasureSpec.getSize(widthSpec)*.04f);body.setPadding(safeMargin,0,safeMargin,Math.round(available*.04f));
        super.onMeasure(widthSpec,heightSpec);
        // Resolve provider geometry during measurement so the first rendered frame matches Play.
        int actionHeight=Math.max(dp(44),play.getMeasuredHeight());boolean changed=false;
        for(int n=0;n<providerActions.getChildCount();n++){View provider=providerActions.getChildAt(n);ViewGroup.LayoutParams size=provider.getLayoutParams();if(size.height!=actionHeight){size.height=actionHeight;provider.setLayoutParams(size);changed=true;}}
        if(changed)super.onMeasure(widthSpec,heightSpec);
    }
    @Override protected void onScrollChanged(int left,int top,int oldLeft,int oldTop){
        super.onScrollChanged(left,top,oldLeft,oldTop);
        android.view.ViewParent parent=getParent();
        while(parent!=null){if(parent instanceof TopNavigation){((TopNavigation)parent).setScrolled(top>0);((TopNavigation)parent).setDetailsExpanded(title!=null&&top>title.getBottom());break;}parent=parent.getParent();}
        invalidate();
    }
    /** Reuse the real title/logo; no second asynchronous request or focus target. */
    @Override protected void dispatchDraw(android.graphics.Canvas canvas){
        float progress=title==null?0:compactTitleProgress(getScrollY(),title.getBottom(),dp(40));
        int content=canvas.save();
        if(progress>0)canvas.clipRect(0,getScrollY()+Math.round(getHeight()/6f)*progress,getWidth(),getScrollY()+getHeight());
        super.dispatchDraw(canvas);canvas.restoreToCount(content);
        if(title==null||title.getWidth()==0||title.getHeight()==0)return;
        if(progress<=0)return;
        float scale=Math.min(dp(220)/(float)title.getWidth(),dp(40)/(float)title.getHeight());
        int saved=canvas.save();
        canvas.translate(body.getPaddingLeft(),getScrollY()+Math.round(getHeight()*.04f));
        canvas.scale(scale,scale);
        int layer=canvas.saveLayerAlpha(0,0,title.getWidth(),title.getHeight(),Math.round(255*progress));
        title.draw(canvas);canvas.restoreToCount(layer);canvas.restoreToCount(saved);
    }
    static float compactTitleProgress(int scroll,int titleBottom,int transition){
        return Math.max(0f,Math.min(1f,(scroll-titleBottom)/(float)Math.max(1,transition)));
    }
    @Override protected int computeScrollDeltaToGetChildRectOnScreen(android.graphics.Rect rect){
        android.graphics.Rect safe=new android.graphics.Rect(rect);
        if(heroPage!=null&&rect.top>=heroPage.getBottom()-dp(44))safe.top-=dp(48);
        return super.computeScrollDeltaToGetChildRectOnScreen(safe);
    }
    private void rebuildTabs(){
        View oldFocus=tabBar.findFocus();Object focusedTag=oldFocus==null?null:oldFocus.getTag();String selected=activeSection;
        List<String> labels=new ArrayList<>();
        labels.add("Details");if(hasPlayableExtras())labels.add("Extras");
        if(show!=null||"tv".equals(remoteKind))labels.add("Seasons & Episodes");
        if(!landscapeCards.isEmpty())labels.add("More Like This");
        boolean unchanged=labels.size()==sectionTabs.size();
        for(int i=0;unchanged&&i<labels.size();i++)unchanged=("section:"+labels.get(i)).equals(sectionTabs.get(i).getTag());
        if(unchanged)return;
        tabBar.removeAllViews();sectionTabs.clear();
        for(String label:labels){
            TextView tab=button(label,()->showSection(label,true));tab.setTextSize(12*TvLayout.scale(getContext()));tab.setSingleLine(true);PreviewIcon.apply(tab,label.equals("Details")?"info":label.equals("Extras")?"play":label.equals("Seasons & Episodes")?"tv":"recommendation",Math.round(16*TvLayout.scale(getContext())));tab.setTag("section:"+label);tab.setContentDescription(label);
            android.graphics.drawable.Drawable tabIcon=tab.getCompoundDrawables()[0];tab.setCompoundDrawables(null,null,null,null);android.text.SpannableString caption=new android.text.SpannableString("\ufffc  "+label);caption.setSpan(new android.text.style.ImageSpan(tabIcon,android.text.style.ImageSpan.ALIGN_BOTTOM),0,1,android.text.Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);tab.setText(caption);if(label.equals("Details"))tab.setMinWidth(Math.round(getResources().getDisplayMetrics().widthPixels*.143f));
            LinearLayout.LayoutParams lp=new LinearLayout.LayoutParams(-2,-1);lp.rightMargin=dp(16);tabBar.addView(tab,lp);android.graphics.drawable.StateListDrawable focus=new android.graphics.drawable.StateListDrawable();focus.addState(new int[]{android.R.attr.state_focused},MenuFoundation.controlBackground(getContext()));focus.addState(new int[]{},new android.graphics.drawable.ColorDrawable(Color.TRANSPARENT));tab.setBackgroundColor(Color.TRANSPARENT);tab.setForeground(null);if(android.os.Build.VERSION.SDK_INT>=26)tab.setDefaultFocusHighlightEnabled(false);if(TvLayout.width(getContext())<780){tab.setTextSize(10*TvLayout.scale(getContext()));tab.setPadding(dp(8),dp(6),dp(8),dp(6));}
            tab.setOnFocusChangeListener((v,focused)->{if(focused)showSection(label,false);sectionFrame.invalidate();});sectionTabs.add(tab);
        }
        showSection(labels.contains(selected)?selected:labels.get(0),false);
        if(focusedTag!=null){View restore=tabBar.findViewWithTag(focusedTag);if(restore!=null)restore.requestFocus();}
    }
    private int expandedScrollTarget(){return Math.max(0,sectionFrame.getTop()+getPaddingTop()-Math.round(getHeight()/6f));}
    private void showSection(String label,boolean enter){
        com.archos.mediacenter.video.diagnostics.Diagnostics.uiState("details",label,"details","none","none",movie!=null?movie.getId():show!=null?show.getTvshowId():remoteId);
        if(!activeSection.equals(label))reading.scrollTo(0,0);activeSection=label;reading.setVerticalScrollBarEnabled(!label.equals("Details"));lower.getLayoutParams().height=label.equals("Details")?LayoutParams.MATCH_PARENT:LayoutParams.WRAP_CONTENT;lower.requestLayout();
        for(LinearLayout section:new LinearLayout[]{episodes,related,trailers})((View)section.getParent()).setVisibility(label.equals(section==episodes?"Seasons & Episodes":section==related?"More Like This":"Extras")?VISIBLE:GONE);
        information.setVisibility(label.equals("Details")?VISIBLE:GONE);
        for(TextView tab:sectionTabs){boolean selected=("section:"+label).equals(tab.getTag());tab.setSelected(selected);if(selected){((PreviewToolbar)tabBar).setSelectedSegment(tab);sectionFrame.selected(tab);}}
        if(enter){scrollTo(0,expandedScrollTarget());if(label.equals("Seasons & Episodes"))focusNextEpisode();else if(label.equals("Extras")&&!extraCards.isEmpty())extraCards.get(0).requestFocus();else if(!label.equals("Details"))lower.requestFocus();}
    }
    /** Keep the existing growing media focus outline inside the new reading viewport. */
    private void revealContentFocus(){
        View focused=findFocus();if(focused==null||!inside(focused,lower))return;
        android.graphics.Rect rect=new android.graphics.Rect(0,0,focused.getWidth(),focused.getHeight());lower.offsetDescendantRectToMyCoords(focused,rect);rect.inset(-dp(10),-dp(10));View heading=extraHeadings.get(focused);if(heading!=null){android.graphics.Rect header=new android.graphics.Rect(0,0,heading.getWidth(),heading.getHeight());lower.offsetDescendantRectToMyCoords(heading,header);if(rect.bottom-header.top<=reading.getHeight())rect.top=header.top;}reading.requestChildRectangleOnScreen(lower,rect,true);
    }
    private void focusNextEpisode(){Snapshot cache=snapshot!=null?snapshot:PreviewLibraryLoader.memoryCache();if(cache!=null&&show!=null){List<Entry> values=new ArrayList<>();for(Entry e:cache.episodes)if(e.show==show.getTvshowId())values.add(e);PreviewSeriesJourney.Selection selected=PreviewSeriesJourney.select(getContext(),values);if(selected.episode!=null)for(PreviewEpisodeRow row:episodeRows)if(row.focusEpisode(((Video)selected.episode.media).getId()))return;}if(!episodeRows.isEmpty())episodeRows.get(0).focusRemembered();}

    public boolean atTop(){View focused=findFocus();return getScrollY()==0&&focused!=null&&inside(focused,heroPage)&&!inside(focused,tabBar);}
    public void focusPrimary(){if(play.getVisibility()==VISIBLE)play.requestFocus();else if(trailer.getVisibility()==VISIBLE)trailer.requestFocus();else if(!providerActions.requestFocus()&&!sectionTabs.isEmpty())sectionTabs.get(0).requestFocus();}
    public void setVersions(int count,Runnable choose){chooseVersions=choose;versions.setText("Versions ("+count+")");versionCount=count;versions.setVisibility(count>1&&movie!=null?VISIBLE:GONE);}
    @Override public boolean dispatchKeyEvent(KeyEvent event){
        if(event.getAction()!=KeyEvent.ACTION_DOWN)return super.dispatchKeyEvent(event);
        View focused=findFocus();int key=event.getKeyCode();String tag=focused==null?"":String.valueOf(focused.getTag());
        if(focused==play&&key==KeyEvent.KEYCODE_DPAD_UP&&matchMetadata.getVisibility()==VISIBLE){matchMetadata.requestFocus();return true;}
        if(focused==matchMetadata){if(key==KeyEvent.KEYCODE_DPAD_DOWN){play.requestFocus();return true;}if(key==KeyEvent.KEYCODE_DPAD_LEFT||key==KeyEvent.KEYCODE_DPAD_RIGHT)return true;}
        if(focused==play&&key==KeyEvent.KEYCODE_DPAD_LEFT)return true;
        if(focused==play&&key==KeyEvent.KEYCODE_DPAD_RIGHT){if(providerActions.getChildCount()>0)providerActions.getChildAt(0).requestFocus();else moreButton.requestFocus();return true;}
        if(focused!=null&&inside(focused,providerActions)){if(key==KeyEvent.KEYCODE_DPAD_LEFT){if(play.getVisibility()==VISIBLE)play.requestFocus();return true;}if(key==KeyEvent.KEYCODE_DPAD_RIGHT){if(moreButton.getVisibility()==VISIBLE)moreButton.requestFocus();return true;}}
        if(focused==versions&&key==KeyEvent.KEYCODE_DPAD_LEFT){play.requestFocus();return true;}
        if(focused==versions&&key==KeyEvent.KEYCODE_DPAD_RIGHT){moreButton.requestFocus();return true;}
        if(focused==moreButton&&key==KeyEvent.KEYCODE_DPAD_LEFT){if(providerActions.getChildCount()>0)providerActions.getChildAt(0).requestFocus();else if(play.getVisibility()==VISIBLE)play.requestFocus();return true;}
        if(focused==moreButton&&key==KeyEvent.KEYCODE_DPAD_RIGHT)return true;
        if(tag.equals("details.reviews")){
            if(key==KeyEvent.KEYCODE_DPAD_UP){focusSectionTab();return true;}
            if(key==KeyEvent.KEYCODE_DPAD_DOWN||key==KeyEvent.KEYCODE_DPAD_LEFT||key==KeyEvent.KEYCODE_DPAD_RIGHT)return true;
        }
        if(tag.startsWith("section:")){
            if(key==KeyEvent.KEYCODE_DPAD_UP){focusPrimary();scrollTo(0,0);return true;}
            if(key==KeyEvent.KEYCODE_DPAD_DOWN){if(activeSection.equals("Details")){int target=expandedScrollTarget();int before=getScrollY();scrollTo(0,target);if(before==getScrollY()){View reviews=findViewWithTag("details.reviews");if(reviews!=null)reviews.requestFocus();}}else showSection(tag.substring(8),true);return true;}
            if((key==KeyEvent.KEYCODE_DPAD_CENTER||key==KeyEvent.KEYCODE_ENTER)&&activeSection.equals("Details"))return true;
        }else if(focused!=null&&inside(focused,heroPage)&&key==KeyEvent.KEYCODE_DPAD_DOWN){
            if(!sectionTabs.isEmpty()){sectionTabs.get(0).requestFocus();return true;}
        }else if(focused!=null&&(key==KeyEvent.KEYCODE_DPAD_UP||key==KeyEvent.KEYCODE_DPAD_DOWN)){
            if(inside(focused,trailers)){
                View rail=focused;while(rail.getParent()!=trailers&&rail.getParent() instanceof View)rail=(View)rail.getParent();int index=trailers.indexOfChild(rail),next=index+(key==KeyEvent.KEYCODE_DPAD_UP?-2:2);
                if(next<0)focusSectionTab();else if(next<trailers.getChildCount()&&trailers.getChildAt(next) instanceof HorizontalScrollView){ViewGroup row=(ViewGroup)((HorizontalScrollView)trailers.getChildAt(next)).getChildAt(0);View remembered=row.getTag() instanceof View?(View)row.getTag():row.getChildAt(0);if(remembered!=null)remembered.requestFocus();}return true;
            }

            for(int i=0;i<episodeRows.size();i++)if(inside(focused,episodeRows.get(i))){
                int rowPosition=episodes.indexOfChild(episodeRows.get(i));View heading=rowPosition>0?episodes.getChildAt(rowPosition-1):null;if(key==KeyEvent.KEYCODE_DPAD_UP&&heading!=null&&heading.isFocusable()){heading.requestFocus();return true;}
                int next=i+(key==KeyEvent.KEYCODE_DPAD_UP?-1:1);
                if(next>=0&&next<episodeRows.size())episodeRows.get(next).focusRemembered();
                else if(next<0)focusSectionTab();
                return true;
            }
            if(key==KeyEvent.KEYCODE_DPAD_UP&&getScrollY()>0){View next=focused.focusSearch(FOCUS_UP);if(next==null||!inside(next,lower)){focusSectionTab();return true;}}
        }
        return super.dispatchKeyEvent(event);
    }
    private void focusSectionTab(){for(TextView tab:sectionTabs)if(tab.isSelected()){tab.requestFocus();reading.scrollTo(0,0);scrollTo(0,expandedScrollTarget());return;}}
    private boolean inside(View child,View parent){while(child!=null){if(child==parent)return true;child=child.getParent() instanceof View?(View)child.getParent():null;}return false;}
    private int dp(int n){return Math.round(n*getResources().getDisplayMetrics().density);}
    private TextView text(String s,int size){TextView t=new TextView(getContext());t.setText(s);t.setTextColor(Color.WHITE);t.setTextSize(size);t.setIncludeFontPadding(false);t.setTypeface(android.graphics.Typeface.create("sans-serif-light",android.graphics.Typeface.NORMAL));return t;}
    private GradientDrawable bg(boolean focus){GradientDrawable d=new GradientDrawable();d.setColor(focus?0xee254964:0xcc10283b);d.setCornerRadius(dp(5));d.setStroke(dp(focus?2:1),focus?PreviewAccent.color(getContext()):0xff284b62);return d;}
    private TextView button(String s,Runnable run){TextView t=TvButton.create(getContext(),s,run,false);t.setTag("action:"+s);com.archos.mediacenter.video.diagnostics.Diagnostics.semantic(t,"details.action."+s.toLowerCase(Locale.ROOT).replaceAll("[^a-z0-9]+","."));PreviewIcon.apply(t,s,16);t.setGravity(Gravity.CENTER);t.setPadding(dp(14),dp(9),dp(14),dp(9));t.setFocusable(true);t.setFocusableInTouchMode(true);t.setBackground(SharedThreePanel.control(getContext(),10));t.setOnClickListener(v->run.run());return t;}
    private void margin(LinearLayout row,View view){LinearLayout.LayoutParams lp=new LinearLayout.LayoutParams(-2,-2);lp.leftMargin=dp(18);row.addView(view,lp);}
    private LinearLayout section(String name){LinearLayout section=new LinearLayout(getContext());section.setOrientation(LinearLayout.VERTICAL);section.setPadding(0,dp(10),0,dp(8));TextView label=text(name,16);label.setTextColor(0xff9ed4f7);section.addView(label);if(name.equals("More Like This")||name.equals("Details")||name.equals("Episodes")||name.equals("Extras"))label.setVisibility(GONE);LinearLayout content=new LinearLayout(getContext());content.setOrientation(LinearLayout.VERTICAL);content.setPadding(0,dp(6),0,0);section.addView(content);body.addView(section);return content;}
    private void freshTitle(String identity){
        if(identity.equals(boundTitle))return;
        boundTitle=identity;openedAt=android.os.SystemClock.elapsedRealtime();activeSection="Details";scrollTo(0,0);
        enrichmentGeneration++;if(enrichmentTask!=null)enrichmentTask.cancel(true);enrichmentTask=null;enrichmentKey="";enrichment=null;
        if(reviews!=null){reviews.close();reviews=null;}
        plot.setText("");for(PreviewLandscapeCard card:landscapeCards)card.release();landscapeCards.clear();for(PreviewLandscapeCard card:extraCards)card.release();extraCards.clear();extraHeadings.clear();related.removeAllViews();trailers.removeAllViews();episodes.removeAllViews();episodeRows.clear();movie=null;show=null;tags=null;remoteDetails=null;remoteKind=null;remoteId=0;latestSummary=null;
        informationSignature=peopleSignature=receptionSignature=summarySignature="";informationPanel=null;
        artworkUri=null;failedArtworkUri=null;artworkRetryAfter=0;artworkReady=false;if(artworkLoader!=null)artworkLoader.cancelRequest(cardArtwork);cardArtwork.setImageDrawable(null);
        cast.removeAllViews();crew.removeAllViews();portraits.clear();trailerList=Collections.emptyList();seasons.clear();
    }
    public void bind(Video value){seasonNumber=-1;freshTitle("video:"+value.getId()+":"+(value instanceof Movie?((Movie)value).getOnlineId():0));movie=value;com.archos.mediacenter.video.utils.SubtitleDiscovery.details(getContext(),value,snapshot!=null?snapshot:PreviewLibraryLoader.memoryCache());title.setText(movie instanceof Episode?safe(((Episode)movie).getShowName())+" — "+safe(((Episode)movie).getEpisodeName()):movie.getName());OfficialTitleArtwork.bind(title,value,false);if(plot.length()==0||!safe(movie.getDescriptionBody()).isEmpty())plot.setText(movie.getDescriptionBody());
        if(!movie.hasScraperData()){if(safe(movie.getName()).isEmpty())title.setText(movie.getFilenameNonCryptic());plot.setText("Not matched · Synopsis unavailable");}
        play.setText(movie.getResumeMs()>0&&!PreviewSeriesJourney.completed(movie)?"Resume":"Play");
        if(movie.getPreviewBackdrop()!=null)cardArtwork(movie.getPreviewBackdrop());renderDetails();rebuildTabs();requestEnrichment();
    }
    public void setTags(BaseTags value,List<ScraperTrailer> videos,List<ScraperImage> backdrops){View oldCast=cast.findFocus(),oldCrew=crew.findFocus();Object castKey=oldCast==null?null:oldCast.getTag(),crewKey=oldCrew==null?null:oldCrew.getTag();int oldY=getScrollY();if(value!=null)tags=value;if(videos!=null&&!videos.isEmpty())trailerList=videos;
        ScraperImage selectedBackdrop=tags==null?null:tags.getDefaultBackdrop();
        if(selectedBackdrop==null&&backdrops!=null&&!backdrops.isEmpty())selectedBackdrop=backdrops.get(0);
        if(selectedBackdrop!=null){java.io.File file=selectedBackdrop.getLargeFileF();Uri uri=file!=null&&file.exists()?Uri.fromFile(file):selectedBackdrop.getLargeUrl()==null?null:Uri.parse(selectedBackdrop.getLargeUrl());if(uri!=null){cardArtwork(uri);if(movie!=null)movie.setPreviewBackdrop(uri.toString());}}
        if(enrichment==null||enrichment.credits==null){portraits.clear();cast.removeAllViews();LinearLayout people=new DetailsPeopleGrid(getContext());cast.addView(people,new LinearLayout.LayoutParams(-1,-1));
        crew.removeAllViews();LinearLayout crewPeople=new DetailsPeopleGrid(getContext());crew.addView(crewPeople,new LinearLayout.LayoutParams(-1,-1));
        if(tags!=null){for(Map.Entry<String,String> person:tags.getActors().entrySet()){if(people.getChildCount()>=8)break;person(people,person.getKey(),person.getValue());}
            if(tags.getDirectors()!=null)for(String name:tags.getDirectors())if(!safe(name).isEmpty()&&crewPeople.getChildCount()<8)person(crewPeople,name,"Director");
            if(tags.getWriters()!=null)for(String name:tags.getWriters())if(!safe(name).isEmpty()&&crewPeople.getChildCount()<8)person(crewPeople,name,"Writer");}
        }
        trailer.setVisibility(GONE);renderExtras();rebuildTabs();
        PreviewPeople.load(getContext().getApplicationContext(),tags,new HashMap<>(portraits));renderEnrichedPeople();renderDetails();renderRelated();requestEnrichment();restoreRowFocus(cast,castKey,oldY);restoreRowFocus(crew,crewKey,oldY);
    }
    public void setSnapshot(Snapshot value){snapshot=value;renderDetails("indexed_snapshot");renderRelated();requestEnrichment();}
    public void setSeriesPlaybackTarget(PreviewSeriesJourney.Selection selection){play.setText(PreviewSeriesJourney.playLabel(selection));}
    private void renderDetails(){
        renderDetails("information_refresh");
    }
    private void renderDetails(String reason){
        int previous=details.getChildCount();
        View focused=details.findFocus();Object focusKey=focused==null?null:focused.getTag();int oldY=getScrollY();
        observeActions();if(movie==null&&show==null&&remoteDetails==null)return;
        String synopsis=String.valueOf(plot.getText());
        LinearLayout panels=new LinearLayout(getContext());
        LinearLayout key=factPanel(panels,"Key Information"),reception=factPanel(panels,"Reception"),
                technical=factPanel(panels,remoteDetails!=null?"Streaming Availability":"Technical Information");
        org.json.JSONObject enriched=enrichment!=null?enrichment.details:remoteDetails;
        Episode selectedEpisode=movie instanceof Episode?(Episode)movie:null;
        org.json.JSONObject episodeFacts=selectedEpisode!=null&&enrichment!=null?PreviewDetailsFacts.episode(enrichment.episodes,selectedEpisode.getSeasonNumber(),selectedEpisode.getEpisodeNumber()):null;
        long episodeDate=selectedEpisode==null?0:selectedEpisode.getEpisodeDate();
        if(episodeDate<=0&&tags instanceof EpisodeTags&&((EpisodeTags)tags).getAired()!=null)episodeDate=((EpisodeTags)tags).getAired().getTime();
        if(movie!=null&&!movie.hasScraperData())fact(key,"Metadata","Not matched");
        String genre=tags instanceof VideoTags?((VideoTags)tags).getGenresFormatted():"";
        if(safe(genre).isEmpty()&&tags instanceof EpisodeTags){ShowTags parent=((EpisodeTags)tags).getShowTags();if(parent!=null)genre=parent.getGenresFormatted();}
        if(safe(genre).isEmpty()&&enriched!=null)genre=jsonNames(enriched.optJSONArray("genres"),"name");
        if(safe(genre).isEmpty()){Snapshot local=snapshot!=null?snapshot:PreviewLibraryLoader.memoryCache();if(local!=null)for(Entry entry:movie!=null?local.technical:local.shows)if(movie!=null&&entry.media instanceof Video&&((Video)entry.media).getId()==movie.getId()||show!=null&&entry.show==show.getTvshowId()){genre=entry.genres.replace('|',',');break;}}
        context.setText(seasonNumber>=0?"Season "+seasonNumber:selectedEpisode==null?"":"S"+selectedEpisode.getSeasonNumber()+" E"+selectedEpisode.getEpisodeNumber());context.setVisibility(selectedEpisode==null&&seasonNumber<0?GONE:VISIBLE);
        int year=movie instanceof Movie?((Movie)movie).getYear():show!=null?show.getYear():0;
        if(selectedEpisode!=null)year=episodeDate>0?PreviewDetailsFacts.dateYear(episodeDate):PreviewDetailsFacts.year(episodeFacts);
        else if(year<=0||seasonNumber>=0)year=PreviewDetailsFacts.year(enriched);
        if(year>0)fact(key,"Year",String.valueOf(year));
        long runtime=movie==null?0:movie.getDurationMs()/60000;
        if(runtime<=0&&selectedEpisode!=null&&tags instanceof EpisodeTags)runtime=tags.getRuntime(java.util.concurrent.TimeUnit.MINUTES);
        if(runtime<=0){org.json.JSONObject runtimeFacts=selectedEpisode!=null?episodeFacts:enriched;if(runtimeFacts!=null)runtime=runtimeFacts.optLong("runtime");}
        if(runtime>0&&(movie!=null||"movie".equals(remoteKind)))fact(key,"Runtime",SharedMetadata.runtime(runtime));else if(movie==null){String episodeRuntime=PreviewDetailsFacts.episodeRuntimes(enriched);if(!episodeRuntime.isEmpty())fact(key,"Runtime","Episodes · "+episodeRuntime);}

        String certificate=selectedEpisode==null&&seasonNumber<0&&enrichment!=null?SharedMetadata.certificate(enrichment.classification,StreamingRepository.country(getContext())):"";

        fact(key,"Age rating",certificate);fact(key,"Genres",genre);
        String studios=tags instanceof MovieTags?((MovieTags)tags).getStudiosFormatted():tags instanceof ShowTags?((ShowTags)tags).getStudiosFormatted():tags instanceof EpisodeTags&&((EpisodeTags)tags).getShowTags()!=null?((EpisodeTags)tags).getShowTags().getStudiosFormatted():"";
        if(safe(studios).isEmpty()&&enriched!=null)studios=jsonNames(enriched.optJSONArray(show!=null||"tv".equals(remoteKind)?"networks":"production_companies"),"name");
        fact(key,"Studio / Network",studios);
        float rating=selectedEpisode!=null?(tags instanceof EpisodeTags?((EpisodeTags)tags).getRating():selectedEpisode.getEpisodeRating()):movie instanceof Movie?((Movie)movie).getRating():show==null?0:show.getRating();
        if(seasonNumber>=0)rating=0;
        if(rating<=0&&selectedEpisode!=null)rating=selectedEpisode.getEpisodeRating();
        boolean tmdbRating=episodeFacts!=null&&episodeFacts.optDouble("vote_average",0)>0||selectedEpisode==null&&enriched!=null&&enriched.optDouble("vote_average",0)>0;
        if(episodeFacts!=null&&episodeFacts.optDouble("vote_average",0)>0)rating=(float)episodeFacts.optDouble("vote_average",0);
        if(enriched!=null&&!(movie instanceof Episode)&&enriched.optDouble("vote_average",0)>0)rating=(float)enriched.optDouble("vote_average");
        if(rating>0&&rating<=10)fact(reception,tmdbRating?"TMDB Rating":"Indexed Rating",String.format(Locale.UK,tmdbRating?"%.1f / 10":"%.1f",rating));
        if(selectedEpisode!=null)fact(key,"Air date",episodeDate>0?PreviewDetailsFacts.date(episodeDate):episodeFacts==null?"":episodeFacts.optString("air_date"));
        if(enriched!=null){
            if(selectedEpisode==null)fact(key,"Release / first air date",enriched.optString("release_date",enriched.optString("first_air_date")));
            fact(key,selectedEpisode!=null?"Series original title":"Original title",enriched.optString("original_title",enriched.optString("original_name")));
            if(show!=null||"tv".equals(remoteKind)){fact(key,"Last air date",enriched.optString("last_air_date"));fact(key,"Series status",enriched.optString("status"));}
            fact(key,"Countries",jsonNames(enriched.optJSONArray("production_countries"),"name"));
            fact(key,selectedEpisode!=null?"Series tagline":"Tagline",enriched.optString("tagline"));
            org.json.JSONObject collection=enriched.optJSONObject("belongs_to_collection");
            if(collection!=null)fact(key,"Collection",collection.optString("name"));
            for(String field:new String[]{"budget","revenue"})if(enriched.optLong(field)>0)fact(key,field.equals("budget")?"Budget (TMDb)":"Box office (TMDb)",java.text.NumberFormat.getCurrencyInstance(Locale.US).format(enriched.optLong(field)));
            if(enriched.optLong("vote_count")>0&&!(movie instanceof Episode))fact(reception,"TMDb votes",java.text.NumberFormat.getIntegerInstance().format(enriched.optLong("vote_count")));
            if(episodeFacts!=null&&episodeFacts.optLong("vote_count")>0)fact(reception,"TMDb votes",java.text.NumberFormat.getIntegerInstance().format(episodeFacts.optLong("vote_count")));
        }
        if(movie!=null){
            if(movie.getMeasuredWidth()>0&&movie.getMeasuredHeight()>0)fact(technical,"Resolution",movie.getMeasuredWidth()+" × "+movie.getMeasuredHeight());
            fact(technical,"Video codec",PreviewMediaInfo.format(movie.getCalculatedVideoFormat()));
            fact(technical,"Audio",PreviewMediaInfo.format(movie.getCalculatedBestAudioFormat()));
            fact(technical,"Dynamic range",PreviewVariants.dynamicRange(getContext(),movie));
            fact(technical,"Source",source(movie.getFileUri()));
            if(movie.getSize()>0)fact(technical,"File size",android.text.format.Formatter.formatShortFileSize(getContext(),movie.getSize()));
            fact(technical,"Selected file",movie.getFilenameNonCryptic());
            fact(technical,"Location",PreviewVariants.safeLocation(movie.getFileUri()));
            String filename=movie.getFilenameNonCryptic();int extension=filename==null?-1:filename.lastIndexOf('.');
            if(extension>=0&&filename.length()-extension<=6)fact(technical,"Container",filename.substring(extension+1).toUpperCase(Locale.ROOT));
            if(movie.getMetadata()!=null){
                com.archos.mediacenter.video.utils.VideoMetadata data=movie.getMetadata();
                com.archos.mediacenter.video.utils.VideoMetadata.VideoTrack video=data.getVideoTrack();
                if(video!=null){
                    if(video.bitRate>0)fact(technical,"Video bitrate",video.bitRate+" kb/s");
                    if(video.fpsScale>0)fact(technical,"Frame rate",String.format(Locale.UK,"%.3f fps",video.fpsRate/(double)video.fpsScale));
                }
                LinkedHashSet<String> subtitles=new LinkedHashSet<>();
                LinkedHashSet<String> audioTracks=new LinkedHashSet<>();
                for(int n=0;n<data.getAudioTrackNb();n++){com.archos.mediacenter.video.utils.VideoMetadata.AudioTrack track=data.getAudioTrack(n);if(track!=null){String format=PreviewMediaInfo.audioTrack(track.format,track.channels,track.sampleRate);if(!format.isEmpty())audioTracks.add(format);}}
                fact(technical,"Audio tracks",android.text.TextUtils.join("\n",audioTracks));
                for(int n=0;n<data.getSubtitleTrackNb();n++){com.archos.mediacenter.video.utils.VideoMetadata.SubtitleTrack track=data.getSubtitleTrack(n);if(track!=null&&!safe(track.language).isEmpty())subtitles.add(track.language);}
                fact(technical,"Subtitles",android.text.TextUtils.join(", ",subtitles));
            }
        }else if(show!=null){
            Snapshot library=snapshot!=null?snapshot:PreviewLibraryLoader.memoryCache();
            if(library!=null){
                Set<Long> physical=new HashSet<>();Set<String> logical=new HashSet<>();Set<Integer> availableSeasons=new HashSet<>();long bytes=0;int specials=0,knownSizes=0;
                Set<String> locations=new TreeSet<>(),audioFormats=new TreeSet<>(),containers=new TreeSet<>();long firstAdded=Long.MAX_VALUE;Map<String,Integer> formats=new TreeMap<>();
                for(Entry entry:library.episodes)if(entry.show==show.getTvshowId()&&entry.media instanceof Episode&&(seasonNumber<0||((Episode)entry.media).getSeasonNumber()==seasonNumber)){
                    if(!entry.audio.isEmpty())audioFormats.add(entry.audio);if(!entry.container.isEmpty())containers.add(entry.container);if(entry.added>0)firstAdded=Math.min(firstAdded,entry.added);Episode episode=(Episode)entry.media;logical.add(episode.getSeasonNumber()+":"+episode.getEpisodeNumber());availableSeasons.add(episode.getSeasonNumber());
                    if(physical.add(episode.getId())){bytes+=Math.max(0,episode.getSize());if(episode.getSize()>0)knownSizes++;if(episode.getSeasonNumber()==0)specials++;
                        android.net.Uri uri=episode.getFileUri();if(uri!=null){String path=uri.getPath();int slash=path==null?-1:path.lastIndexOf('/');locations.add(PreviewVariants.safeLocation(uri.buildUpon().path(slash>=0?path.substring(0,slash):"").clearQuery().fragment(null).build()));}
                        String format=(entry.resolution+" "+entry.codec+" "+entry.hdr).trim();if(!format.isEmpty())formats.put(format,formats.getOrDefault(format,0)+1);
                    }
                }
                fact(technical,"Local episodes",String.valueOf(logical.size()));fact(technical,"Local seasons",String.valueOf(availableSeasons.size()));
                if(bytes>0)fact(technical,"Library size",(knownSizes<physical.size()?"≥ ":"")+android.text.format.Formatter.formatShortFileSize(getContext(),bytes));
                if(!physical.isEmpty()&&knownSizes==physical.size())fact(technical,"Average file size",android.text.format.Formatter.formatShortFileSize(getContext(),bytes/physical.size()));
                if(specials>0)fact(technical,"Specials (files)",String.valueOf(specials));
                if(enriched!=null&&enriched.optInt("number_of_episodes")>0)fact(technical,"Series episodes (metadata)",String.valueOf(enriched.optInt("number_of_episodes")));
                List<String> formatCounts=new ArrayList<>();for(Map.Entry<String,Integer> format:formats.entrySet())formatCounts.add(format.getKey()+": "+format.getValue()+" files");fact(technical,"File formats",android.text.TextUtils.join(", ",formatCounts));
                fact(technical,"Storage locations",android.text.TextUtils.join("\n",locations));fact(technical,"Location",android.text.TextUtils.join(", ",locations));fact(technical,"Audio",android.text.TextUtils.join(", ",audioFormats));fact(technical,"Container",android.text.TextUtils.join(", ",containers));if(firstAdded!=Long.MAX_VALUE)fact(technical,"Added",android.text.format.DateFormat.getDateFormat(getContext()).format(new Date(firstAdded*1000L)));
            }
        }else{
            fact(technical,"Region",com.archos.mediacenter.video.streaming.StreamingRepository.country(getContext()));
            if(observedActions!=null)for(int n=0;n<observedActions.size();n++){
                Object item=observedActions.get(n);
                if(item instanceof Action&&com.archos.mediacenter.video.streaming.StreamingActions.isAvailableOffer((Action)item))fact(technical,"Available on",String.valueOf(((Action)item).getLabel1()));
            }
        }
        if(enriched!=null){fact(key,"Language",jsonNames(enriched.optJSONArray("spoken_languages"),"english_name"));fact(key,"Alternative names",PreviewDetailsFacts.alternativeNames(enrichment==null?null:enrichment.alternatives,StreamingRepository.country(getContext())));}
        if(movie!=null){Snapshot local=snapshot!=null?snapshot:PreviewLibraryLoader.memoryCache();if(local!=null)for(Entry entry:local.technical)if(entry.media instanceof Video&&((Video)entry.media).getId()==movie.getId()&&entry.added>0){fact(technical,"Added",android.text.format.DateFormat.getDateFormat(getContext()).format(new Date(entry.added*1000L)));break;}}
        Map<String,String> technicalFacts=DetailsBlueprintPanel.facts(technical);if(movie!=null)technicalFacts.put("Video",SharedMetadata.join(technicalFacts.get("Resolution"),technicalFacts.get("Video codec"),technicalFacts.get("Dynamic range"),technicalFacts.get("Frame rate"),technicalFacts.get("Video bitrate")));
        if(technicalFacts.containsKey("Subtitles"))technicalFacts.put("Audio tracks",SharedMetadata.join(technicalFacts.get("Audio tracks"),"Subtitles: "+technicalFacts.get("Subtitles")));
        if(technicalFacts.containsKey("Average file size"))technicalFacts.put("Library size",technicalFacts.get("Library size")+" · avg "+technicalFacts.get("Average file size"));
        Map<String,String> keyFacts=DetailsBlueprintPanel.facts(key),receptionFacts=DetailsBlueprintPanel.facts(reception);
        com.archos.mediacenter.video.metadata.OmdbSupplement.Ratings supplied=enrichment!=null&&selectedEpisode==null&&seasonNumber<0?enrichment.supplementary:com.archos.mediacenter.video.metadata.OmdbSupplement.Ratings.EMPTY;
        String receptionKey=new TreeMap<>(receptionFacts).toString()+supplied.imdbVotes;
        for(int slot=0;slot<4;slot++)receptionKey+="|"+supplied.value(slot);
        String signature=synopsis+new TreeMap<>(keyFacts)+new TreeMap<>(technicalFacts)+new TreeMap<>(receptionFacts)+supplied.imdbVotes;
        for(int slot=0;slot<4;slot++)signature+="|"+supplied.value(slot);
        boolean reviewable=selectedEpisode==null&&seasonNumber<0&&metadataIdentity()>0;
        signature+="|"+reviewable;receptionKey+="|"+reviewable;
        if(!signature.equals(informationSignature)){
            informationSignature=signature;
            com.archos.mediacenter.video.diagnostics.Diagnostics.uiRebuild(details,"details.information",reason,previous,1,false);
            if(informationPanel!=null&&receptionKey.equals(receptionSignature)){informationPanel.updateText(synopsis,keyFacts,technicalFacts);}else{
            DetailsBlueprintPanel blueprint=new DetailsBlueprintPanel(getContext(),synopsis,keyFacts,technicalFacts,receptionFacts,cast,crew,supplied,null);
            details.removeAllViews();details.addView(blueprint,new LinearLayout.LayoutParams(-1,-1));informationPanel=blueprint;receptionSignature=receptionKey;}
            com.archos.mediacenter.video.diagnostics.Diagnostics.event("details_information_ready","media_id",diagnosticMedia(),"elapsed_ms",android.os.SystemClock.elapsedRealtime()-openedAt);
        }
        reception.setVisibility(reception.getChildCount()>1?VISIBLE:GONE);
        technical.setVisibility(technical.getChildCount()>1?VISIBLE:GONE);
        ((View)details.getParent()).setVisibility(VISIBLE);
        SharedMetadata.Facts summary=new SharedMetadata.Facts();summary.certificate=selectedEpisode==null?safe(certificate):"";summary.year=year>0?String.valueOf(year):"";summary.genres=safe(genre);summary.rating=tmdbRating?rating:0;
        org.json.JSONObject votesSource=selectedEpisode==null?enriched:episodeFacts;summary.votes=votesSource==null?0:votesSource.optLong("vote_count");
        if(show!=null||"tv".equals(remoteKind)){Snapshot library=snapshot!=null?snapshot:PreviewLibraryLoader.memoryCache();Entry local=null;if(library!=null&&show!=null)for(Entry e:library.shows)if(e.show==show.getTvshowId())local=e;int seasonCount=local!=null?local.seasons:enriched==null?(show==null?0:show.getSeasonCount()):Math.max(enriched.optInt("number_of_seasons"),show==null?0:show.getSeasonCount()),episodeCount=local!=null?local.episodes:enriched==null?(show==null?0:show.getEpisodeCount()):Math.max(enriched.optInt("number_of_episodes"),show==null?0:show.getEpisodeCount());if(seasonNumber>=0){Set<Integer> localEpisodes=new HashSet<>();if(library!=null)for(Entry e:library.episodes)if(e.show==show.getTvshowId()&&((Episode)e.media).getSeasonNumber()==seasonNumber)localEpisodes.add(((Episode)e.media).getEpisodeNumber());summary.context=SharedMetadata.count(localEpisodes.size(),"Episode");}else summary.context=SharedMetadata.join(SharedMetadata.count(seasonCount,"Season"),SharedMetadata.count(episodeCount,"Episode"));}
        else summary.context=SharedMetadata.runtime(runtime);
        if(movie!=null)summary.technical.addAll(SharedMetadata.technical(getContext(),movie));latestSummary=summary;bindSummary();
        if(focused!=null&&!focused.hasFocus())restoreRowFocus(details,focusKey,oldY);
    }
    private String summarySignature="";
    private void bindSummary(){
        SharedMetadata.Facts summary=latestSummary.copy();if(compactCard&&movie instanceof Episode){Episode episode=(Episode)movie;summary.context=SharedMetadata.join("S"+episode.getSeasonNumber()+" E"+episode.getEpisodeNumber(),summary.context);context.setVisibility(GONE);}else context.setVisibility(movie instanceof Episode||seasonNumber>=0?VISIBLE:GONE);
        SharedMetadata.Facts cardSummary=SharedMetadata.cardFacts(summary,false);String signature=compactCard+"|"+SharedMetadata.primary(cardSummary)+cardSummary.technical;if(!signature.equals(summarySignature)){summarySignature=signature;SharedMetadata.bindCard(meta,pills,summary,false);}plot.setVisibility(plot.length()==0?GONE:VISIBLE);
    }
    private static String jsonNames(org.json.JSONArray values,String key){
        if(values==null)return "";List<String> names=new ArrayList<>();
        for(int n=0;n<values.length();n++){org.json.JSONObject item=values.optJSONObject(n);if(item!=null&&!item.optString(key).isEmpty())names.add(item.optString(key));}
        return android.text.TextUtils.join(", ",names);
    }
    private LinearLayout factPanel(LinearLayout panels,String name){LinearLayout panel=new LinearLayout(getContext());panel.setTag("semantic:details.panel."+name.toLowerCase(Locale.ROOT).replace(' ','.'));panel.setOrientation(LinearLayout.VERTICAL);panel.setPadding(dp(6),dp(6),dp(6),dp(6));panel.setBackground(PreviewDialog.surface(getContext(),false));panel.setFocusable(false);panel.setDescendantFocusability(FOCUS_BLOCK_DESCENDANTS);LinearLayout.LayoutParams size=new LinearLayout.LayoutParams(0,-2,1);size.rightMargin=dp(8);panels.addView(panel,size);panel.addView(text(name,15));return panel;}
    private void fact(LinearLayout panel,String label,String value){
        if(value==null||value.trim().isEmpty())return;
        LinearLayout row=new LinearLayout(getContext());row.setGravity(Gravity.CENTER_VERTICAL);row.setContentDescription(label+": "+value);
        TextView name=text(label,12),content=text(value.replace("\n",", "),13);name.setTextColor(0xffb9c7d2);name.setSingleLine(false);content.setSingleLine(false);
        row.addView(name,new LinearLayout.LayoutParams(0,-2,.44f));LinearLayout.LayoutParams valueSize=new LinearLayout.LayoutParams(0,-2,.56f);valueSize.leftMargin=dp(8);row.addView(content,valueSize);panel.addView(row);
    }
    private void showReviews(){
        if(reviews!=null)reviews.close();
        String kind=remoteDetails!=null?remoteKind:show!=null?"tv":"movie";
        reviews=new DetailsReviews(getContext(),kind,metadataIdentity());reviews.show();
    }
    private void cardArtwork(Uri uri){
        if(uri==null||uri.toString().equals(artworkUri)||uri.toString().equals(failedArtworkUri)&&android.os.SystemClock.elapsedRealtime()<artworkRetryAfter)return;
        artworkUri=uri.toString();String identity=boundTitle,requested=artworkUri;
        artworkLoader=com.squareup.picasso.Picasso.get();artworkLoader.load(uri).fit().centerCrop().noPlaceholder().noFade().into(cardArtwork,new com.squareup.picasso.Callback(){
            public void onSuccess(){if(!identity.equals(boundTitle)||!requested.equals(artworkUri))return;artworkReady=true;failedArtworkUri=null;com.archos.mediacenter.video.diagnostics.Diagnostics.event("details_artwork_ready","media_id",diagnosticMedia(),"elapsed_ms",android.os.SystemClock.elapsedRealtime()-openedAt);}
            public void onError(Exception error){if(identity.equals(boundTitle)&&requested.equals(artworkUri)){artworkUri=null;failedArtworkUri=requested;artworkRetryAfter=android.os.SystemClock.elapsedRealtime()+30000;com.archos.mediacenter.video.diagnostics.Diagnostics.event("details_artwork_unavailable","media_id",diagnosticMedia());}}
        });
    }
    private void observeActions(){ObjectAdapter next=actions.get();if(next!=observedActions){if(observedActions!=null)observedActions.unregisterObserver(actionObserver);observedActions=next;if(next!=null)next.registerObserver(actionObserver);renderProviders();}}
    private void renderProviders(){
        refreshMoreRows();
        matchMetadata.setVisibility(movie!=null&&!movie.hasScraperData()&&matchingAction()!=null?VISIBLE:GONE);
        moreButton.setVisibility(remoteDetails==null?VISIBLE:GONE);
        View focused=providerActions.findFocus();Object focusKey=focused==null?null:focused.getTag();
        for(Presenter.ViewHolder holder:providerHolders)providerPresenter.onUnbindViewHolder(holder);providerHolders.clear();providerActions.removeAllViews();if(observedActions==null){if(focused!=null)play.requestFocus();return;}
        List<Action> offers=new ArrayList<>(),extraOptions=new ArrayList<>();for(int i=0;i<observedActions.size();i++){Object item=observedActions.get(i);if(item instanceof Action&&com.archos.mediacenter.video.streaming.StreamingActions.isAvailableOffer((Action)item))offers.add((Action)item);else if(item instanceof Action&&"•••".contentEquals(((Action)item).getLabel1()))extraOptions.add((Action)item);}
        play.setVisibility(remoteDetails==null?VISIBLE:GONE);
        if(!offers.isEmpty()){
            Action offer=offers.get(0);Presenter.ViewHolder holder=providerPresenter.onCreateViewHolder(providerActions);providerPresenter.onBindViewHolder(holder,offer);holder.view.setTag(offer.getId());holder.view.setOnClickListener(v->action.accept(offer));LinearLayout.LayoutParams lp=new LinearLayout.LayoutParams(dp(160),Math.max(dp(44),play.getMeasuredHeight()));lp.leftMargin=dp(18);providerActions.addView(holder.view,lp);providerHolders.add(holder);

        }
        if(focused!=null){View replacement=focusKey==null?null:providerActions.findViewWithTag(focusKey);if(replacement!=null)replacement.requestFocus();else if(providerActions.getChildCount()>0)providerActions.getChildAt(0).requestFocus();else play.requestFocus();}
    }
    private Action matchingAction(){ObjectAdapter adapter=actions.get();if(adapter!=null)for(int i=0;i<adapter.size();i++){Object item=adapter.get(i);if(item instanceof Action&&((Action)item).getId()==VideoActionAdapter.ACTION_SCRAP)return (Action)item;}return null;}
    private void pill(String value){if(value==null||value.isEmpty())return;TextView label=text(value,10);label.setSingleLine(true);label.setPadding(dp(6),dp(3),dp(6),dp(3));GradientDrawable badge=new GradientDrawable();badge.setColor(0x50142634);badge.setCornerRadius(dp(3));badge.setStroke(dp(1),0xff648196);label.setBackground(badge);LinearLayout.LayoutParams lp=new LinearLayout.LayoutParams(-2,-2);lp.rightMargin=dp(6);pills.addView(label,lp);}

    private void person(LinearLayout row,String name,String role){
        if(row.getChildCount()>=8)return;
        LinearLayout cell=new LinearLayout(getContext());cell.setOrientation(LinearLayout.VERTICAL);cell.setGravity(Gravity.CENTER);cell.setFocusable(false);cell.setTag("person:"+name+":"+role);cell.setContentDescription(name+", "+role);
        ImageView portrait=new androidx.appcompat.widget.AppCompatImageView(getContext()){private final android.graphics.Path circle=new android.graphics.Path();@Override public void draw(android.graphics.Canvas canvas){int saved=canvas.save();circle.reset();circle.addOval(0,0,getWidth(),getHeight(),android.graphics.Path.Direction.CW);canvas.clipPath(circle);super.draw(canvas);canvas.restoreToCount(saved);}};portrait.setImageResource(com.archos.mediacenter.video.R.drawable.preview_person);portrait.setScaleType(ImageView.ScaleType.CENTER_CROP);portrait.setBackground(new android.graphics.drawable.ColorDrawable(0xff1d2936));portrait.setOutlineProvider(new ViewOutlineProvider(){public void getOutline(View view,android.graphics.Outline outline){outline.setOval(0,0,view.getWidth(),view.getHeight());}});portrait.setClipToOutline(true);portraits.put(name,portrait);cell.addView(portrait,new LinearLayout.LayoutParams(dp(34),dp(34)));
        for(String value:new String[]{name,safe(role)}){TextView text=text(value,9);text.setGravity(Gravity.CENTER);text.setSingleLine(true);text.setEllipsize(android.text.TextUtils.TruncateAt.END);cell.addView(text,new LinearLayout.LayoutParams(-1,dp(12)));}
        row.addView(cell,new LinearLayout.LayoutParams(-1,-1));
    }

    private void renderEnrichedPeople(){
        if(enrichment==null||enrichment.credits==null)return;
        String signature=enrichment.credits.toString();if(signature.equals(peopleSignature))return;peopleSignature=signature;
        for(String section:new String[]{"cast","crew"}){
            org.json.JSONArray values=enrichment.credits.optJSONArray(section);if(values==null)continue;
            LinearLayout target=section.equals("cast")?cast:crew;
            View focused=target.findFocus();Object focusKey=focused==null?null:focused.getTag();int y=getScrollY();
            LinearLayout people=new DetailsPeopleGrid(getContext());Set<String> seen=new HashSet<>();
            for(int n=0;n<values.length()&&people.getChildCount()<8;n++){
                org.json.JSONObject value=values.optJSONObject(n);if(value==null)continue;
                String name=value.optString("name"),role=value.optString(section.equals("cast")?"character":"job");
                if(name.isEmpty()||section.equals("crew")&&!Arrays.asList("Director","Writer","Screenplay","Story","Creator","Producer","Executive Producer","Original Music Composer","Director of Photography","Editor").contains(role))continue;
                if(!seen.add(value.optLong("id")+":"+name))continue;
                person(people,name,role);
                long personId=value.optLong("id");
                com.archos.mediacenter.video.diagnostics.Diagnostics.semantic(people.getChildAt(people.getChildCount()-1),"details."+section+(personId>0?".person."+personId:".slot."+n));
                ImageView portrait=portraits.get(name);String path=value.optString("profile_path");
                if(portrait!=null&&path.matches("/[A-Za-z0-9._-]+")){Uri uri=Uri.parse("https://image.tmdb.org/t/p/w185"+path);com.archos.mediacenter.video.diagnostics.ArtworkRequest.load(portrait,uri,diagnosticMedia(),"details."+section,"portrait",com.squareup.picasso.Picasso.get().load(uri).fit().centerCrop().placeholder(com.archos.mediacenter.video.R.drawable.preview_person).error(com.archos.mediacenter.video.R.drawable.preview_person),true);}
            }
            target.removeAllViews();target.addView(people,new LinearLayout.LayoutParams(-1,-1));
            restoreRowFocus(target,focusKey,y);
        }
    }
    public void bindShow(Tvshow value,Runnable playAction){freshTitle("show:"+value.getTvshowId()+":"+seasonNumber);show=value;com.archos.mediacenter.video.utils.SubtitleDiscovery.details(getContext(),value,snapshot!=null?snapshot:PreviewLibraryLoader.memoryCache());showPlay=playAction;OfficialTitleArtwork.bind(title,value,false);title.setText(value.getName());if(seasonNumber<0&&(plot.length()==0||!safe(value.getPlot()).isEmpty()))plot.setText(value.getPlot());rebuildTabs();renderDetails();requestEnrichment();}
    public void bindSeason(Tvshow value,int number,List<Episode> local,Runnable playAction){seasonNumber=number;bindShow(value,playAction);seasons.clear();setSeason(number,local);requestEnrichment();}
    public void setSeason(int number,List<Episode> values){seasons.put(number,new ArrayList<>(values));renderSeasons();}
    private void renderSeasons(){
        View focused=findFocus();Object key=focused==null?null:focused.getTag();int y=getScrollY();
        Map<Integer,List<Episode>> locals=new TreeMap<>();
        for(Map.Entry<Integer,List<Episode>> season:seasons.entrySet()){
            List<Entry> physical=new ArrayList<>();for(Episode ep:season.getValue())if(LibraryHealth.state(getContext(),ep)==LibraryHealth.State.AVAILABLE)physical.add(new Entry(ep,0,show==null?0:show.getTvshowId(),""));
            List<Episode> logical=new ArrayList<>();for(Entry e:PreviewVariants.logicalChoices(physical))logical.add((Episode)e.media);locals.put(season.getKey(),logical);
        }
        episodes.removeAllViews();episodeRows.clear();
        long id=remoteDetails!=null?remoteId:show!=null&&show.getShowTags()!=null?show.getShowTags().getOnlineId():0;
        for(Map.Entry<Integer,List<PreviewEpisodeChoice>> season:PreviewEpisodeChoice.indexed(locals,enrichment==null?Collections.emptyMap():enrichment.episodes).entrySet()){
            if(seasonNumber>=0&&season.getKey()!=seasonNumber)continue;
            if(seasonNumber<0&&show!=null){TextView seasonInfo=button(season.getKey()==0?"Specials":"Season "+season.getKey(),()->getContext().startActivity(new android.content.Intent(getContext(),PreviewSeasonActivity.class).putExtra("show",show.getTvshowId()).putExtra("season",season.getKey())));seasonInfo.setTag("season:"+season.getKey());seasonInfo.setTextSize(16*TvLayout.scale(getContext()));seasonInfo.setGravity(Gravity.LEFT|Gravity.CENTER_VERTICAL);seasonInfo.setPadding(dp(4),dp(5),dp(4),dp(5));seasonInfo.setBackground(PreviewDialog.focus(getContext()));LinearLayout.LayoutParams headingSize=new LinearLayout.LayoutParams(-2,dp(44));headingSize.topMargin=dp(8);headingSize.bottomMargin=dp(6);episodes.addView(seasonInfo,headingSize);}else episodes.addView(text(season.getKey()==0?"Specials":"Season "+season.getKey(),18));
            PreviewEpisodeRow row=new PreviewEpisodeRow((Activity)getContext(),season.getValue(),id,enrichment==null?null:enrichment.seasonAvailability.get(season.getKey()));episodeRows.add(row);episodes.addView(row,new LinearLayout.LayoutParams(-1,dp(156)));
        }
        if(key instanceof String){String tag=(String)key;for(PreviewEpisodeRow row:episodeRows){if(tag.startsWith("episode:")){try{if(row.focusEpisode(Long.parseLong(tag.substring(8))))break;}catch(NumberFormatException ignored){}}else if(row.focusRemote(tag))break;}}
        scrollTo(0,y);
    }

    public static String source(Uri uri){if(uri==null)return "Unknown";String scheme=uri.getScheme();
        if(scheme==null||scheme.equals("file")||scheme.equals("content"))return "Local storage";
        if(scheme.equals("smb"))return "NAS / SMB";if(scheme.equals("webdav")||scheme.equals("webdavs")||scheme.equals("dav")||scheme.equals("davs"))return "WebDAV";
        if(scheme.equals("http")||scheme.equals("https"))return "Network / HTTP(S)";
        return "Network / "+scheme.toUpperCase(Locale.ROOT);
    }
    private static String safe(String s){return s==null?"":s;}
    private static String shortFile(String s){if(s==null)return "";return s.length()>90?s.substring(0, sixty(s))+"…"+s.substring(s.length()-20):s;}
    private static int sixty(String s){return Math.min(60,s.length());}
    private void info(LinearLayout row,String name,String value){TextView t=text(value.replace("\n\n","    ·    ").replace("\n",": "),13);t.setMaxLines(3);t.setEllipsize(android.text.TextUtils.TruncateAt.END);t.setTextColor(0xffa8becf);t.setPadding(0,dp(3),0,dp(3));LinearLayout.LayoutParams lp=new LinearLayout.LayoutParams(-1,-2);lp.bottomMargin=dp(7);row.addView(t,lp);}
    private void renderRelated(){
        View focused=related.findFocus();Object focusKey=focused==null?null:focused.getTag();int oldY=getScrollY();
        for(PreviewLandscapeCard card:landscapeCards)card.release();landscapeCards.clear();related.removeAllViews();
        if(movie==null&&show==null&&remoteDetails==null)return;
        boolean television=show!=null||"tv".equals(remoteKind);
        Entry selected=remoteDetails!=null?null:new Entry(show!=null?show:movie,0,show==null?0:show.getTvshowId(),tags instanceof VideoTags?((VideoTags)tags).getGenresFormatted():context.getText().toString());
        Map<Long,Entry> local=new HashMap<>();
        if(snapshot!=null)for(Entry entry:television?snapshot.shows:snapshot.movies)if(entry.onlineId>0)local.putIfAbsent(entry.onlineId,entry);
        Set<String> shown=new HashSet<>();
        if(enrichment!=null)for(PreviewDetailsData.Remote recommendation:enrichment.related){
            if(landscapeCards.size()>=12)break;
            long id=recommendation.title.optLong("id");Entry entry=local.get(id);
            if(entry!=null){addLocalRecommendation(entry);shown.add("id:"+id);shown.add(entry.key());}
            else if(recommendation.provider!=null){
                org.json.JSONObject value=recommendation.title;String name=value.optString("title",value.optString("name"));String backdrop=value.optString("backdrop_path");
                PreviewLandscapeCard card=new PreviewLandscapeCard(getContext());
                card.bind(name,value.optString("release_date",value.optString("first_air_date")).replaceFirst("-.*$",""),backdrop.matches("/[A-Za-z0-9._-]+")?Uri.parse("https://image.tmdb.org/t/p/w780"+backdrop):null,true,id,"details.related","landscape");
                PreviewProviderIcons.bindProvider(card.availability,recommendation.provider,id,"details.related");
                card.availability.setImageAlpha(205);
                card.setTag("remote:"+id);com.archos.mediacenter.video.diagnostics.Diagnostics.semantic(card,"details.related.remote."+id);card.setContentDescription(name+" · "+recommendation.provider.name);
                card.setOnClickListener(v->getContext().startActivity(new Intent(getContext(),com.archos.mediacenter.video.streaming.PreviewRemoteDetailsActivity.class).putExtra("kind",television?"tv":"movie").putExtra("tmdb_id",id)));
                addRecommendation(card);shown.add("id:"+id);
            }
        }
        if(snapshot!=null)for(Entry entry:PreviewDiscovery.similar(selected,snapshot)){
            if(landscapeCards.size()>=12)break;
            if(television?!(entry.media instanceof Tvshow):!(entry.media instanceof Movie))continue;
            if(shown.contains(entry.key())||entry.onlineId>0&&shown.contains("id:"+entry.onlineId))continue;
            addLocalRecommendation(entry);shown.add(entry.key());if(entry.onlineId>0)shown.add("id:"+entry.onlineId);
        }
        if(related.getChildCount()>0){LinearLayout row=(LinearLayout)related.getChildAt(related.getChildCount()-1);while(row.getChildCount()<4)row.addView(new View(getContext()),new LinearLayout.LayoutParams(0,1,1));}
        restoreRowFocus(related,focusKey,oldY);rebuildTabs();
    }
    private void addLocalRecommendation(Entry entry){
        PreviewLandscapeCard card=new PreviewLandscapeCard(getContext());
        card.bind(PreviewPages.displayName(entry),entry.year()>0?String.valueOf(entry.year()):"",entry.backdrop!=null?entry.backdrop:entry.media.getPosterUri(),true,entry.media instanceof Video?((Video)entry.media).getId():entry.media instanceof Tvshow?((Tvshow)entry.media).getTvshowId():0,"details.related","landscape");
        card.setTag("related:"+entry.key());com.archos.mediacenter.video.diagnostics.Diagnostics.semantic(card,"details.related."+entry.key());
        card.setOnClickListener(v->new VideoViewClickedListener((Activity)getContext()).onItemClicked(new Presenter.ViewHolder(v),entry.media,null,null));
        addRecommendation(card);
    }
    private void addRecommendation(PreviewLandscapeCard card){
        LinearLayout row;
        if(landscapeCards.size()%4==0){row=new LinearLayout(getContext());row.setClipChildren(false);related.addView(row);}
        else row=(LinearLayout)related.getChildAt(related.getChildCount()-1);
        LinearLayout.LayoutParams lp=new LinearLayout.LayoutParams(0,dp(142),1);lp.setMargins(dp(5),dp(8),dp(5),dp(8));row.addView(card,lp);landscapeCards.add(card);rowKeys(card,row);
    }
    private boolean hasPlayableExtras(){for(ScraperTrailer e:trailerList)if("YouTube".equals(e.mSite)&&e.mVideoKey!=null&&e.mVideoKey.matches("[A-Za-z0-9_-]{11}"))return true;return false;}
    private void renderExtras(){
        View focused=trailers.findFocus();Object focusKey=focused==null?null:focused.getTag();int y=getScrollY();
        for(PreviewLandscapeCard old:extraCards)old.release();extraCards.clear();extraHeadings.clear();trailers.removeAllViews();
        Map<String,List<ScraperTrailer>> categories=new LinkedHashMap<>();
        for(String category:new String[]{"Trailers","Teasers","Behind the Scenes","Featurettes","Interviews","Clips","Other Videos"})categories.put(category,new ArrayList<>());
        Set<String> seen=new HashSet<>();
        for(ScraperTrailer extra:trailerList){
            if(!"YouTube".equals(extra.mSite)||extra.mVideoKey==null||!extra.mVideoKey.matches("[A-Za-z0-9_-]{11}")||!seen.add(extra.mVideoKey))continue;
            String type="";
            if(enrichment!=null)for(PreviewDetailsData.Extra value:enrichment.extras)if(value.key.equals(extra.mVideoKey)){type=value.type;break;}
            if(type.isEmpty()){String name=safe(extra.mName).toLowerCase(Locale.ROOT);type=name.contains("trailer")?"Trailer":name.contains("teaser")?"Teaser":name.contains("featurette")?"Featurette":name.contains("interview")?"Interview":"Video";}
            String category=type.equals("Trailer")?"Trailers":type.equals("Teaser")?"Teasers":type.equals("Behind the Scenes")?"Behind the Scenes":type.equals("Featurette")?"Featurettes":type.equals("Interview")?"Interviews":type.equals("Clip")?"Clips":"Other Videos";
            categories.get(category).add(extra);
        }
        for(Map.Entry<String,List<ScraperTrailer>> category:categories.entrySet()){
            if(category.getValue().isEmpty())continue;
            TextView heading=text(category.getKey(),18);heading.setPadding(dp(5),dp(12),0,dp(4));trailers.addView(heading);
            HorizontalScrollView rail=new HorizontalScrollView(getContext());rail.setFocusable(false);rail.setFocusableInTouchMode(false);rail.setDescendantFocusability(FOCUS_AFTER_DESCENDANTS);rail.setHorizontalScrollBarEnabled(false);rail.setClipChildren(false);rail.setClipToPadding(false);rail.setPadding(dp(10),dp(10),dp(10),dp(10));LinearLayout row=new LinearLayout(getContext());row.setClipChildren(false);row.setClipToPadding(false);rail.addView(row);trailers.addView(rail,new LinearLayout.LayoutParams(-1,dp(125)));
            for(ScraperTrailer extra:category.getValue()){
                PreviewLandscapeCard card=new PreviewLandscapeCard(getContext());
                card.bind(safe(extra.mName),PreviewExtraDuration.label(PreviewExtraDuration.cached(getContext(),extra.mVideoKey)),Uri.parse("https://i.ytimg.com/vi/"+extra.mVideoKey+"/hqdefault.jpg"),true,diagnosticMedia(),"details.extras","video_thumbnail");
                card.setTag("extra:"+extra.mVideoKey);com.archos.mediacenter.video.diagnostics.Diagnostics.semantic(card,"details.extra."+extra.mVideoKey);card.setOnClickListener(v->PreviewTrailer.show((Activity)getContext(),extra));
                View.OnFocusChangeListener focus=card.getOnFocusChangeListener();
                card.setOnFocusChangeListener((v,active)->{if(focus!=null)focus.onFocusChange(v,active);if(active){row.setTag(card);PreviewExtraDuration.request(card,extra.mVideoKey);post(this::revealContentFocus);}});
                LinearLayout.LayoutParams lp=new LinearLayout.LayoutParams(dp(172),dp(97));lp.setMargins(0,0,dp(12),0);row.addView(card,lp);extraCards.add(card);extraHeadings.put(card,heading);rowKeys(card,row);
            }
        }
        restoreRowFocus(trailers,focusKey,y);
    }
    public void bindRemote(org.json.JSONObject details,String kind,long id){freshTitle(kind+":"+id);remoteDetails=details;remoteKind=kind;remoteId=id;title.setText(details.optString("title",details.optString("name")));OfficialTitleArtwork.bindRemote(title,kind,id);plot.setText(details.optString("overview"));String backdrop=details.optString("backdrop_path");if(backdrop.matches("/[A-Za-z0-9._-]+"))cardArtwork(Uri.parse("https://image.tmdb.org/t/p/w1280"+backdrop));play.setVisibility(GONE);renderDetails();requestEnrichment();}
    private long diagnosticMedia(){return movie!=null?movie.getId():show!=null?show.getTvshowId():remoteId;}
    private long metadataIdentity(){
        if(remoteDetails!=null)return remoteId;if(movie instanceof Movie)return ((Movie)movie).getOnlineId();if(tags instanceof EpisodeTags&&((EpisodeTags)tags).getShowTags()!=null)return ((EpisodeTags)tags).getShowTags().getOnlineId();if(show!=null&&show.getShowTags()!=null)return show.getShowTags().getOnlineId();
        Snapshot library=snapshot!=null?snapshot:PreviewLibraryLoader.memoryCache();if(library!=null)for(Entry entry:show!=null?library.shows:library.episodes)if(show!=null&&entry.show==show.getTvshowId()||movie!=null&&entry.media instanceof Video&&((Video)entry.media).getId()==movie.getId())return entry.onlineId;return 0;
    }
    private void requestEnrichment(){
        long id=metadataIdentity();
        String kind=remoteDetails!=null?remoteKind:show!=null||movie instanceof Episode?"tv":"movie";if(id<=0)return;
        String key=kind+":"+id+":"+seasonNumber+":"+Locale.getDefault().toLanguageTag();if(key.equals(enrichmentKey))return;if(!enrichmentKey.isEmpty()){enrichment=null;peopleSignature="";}enrichmentKey=key;
        if(seasonNumber<0)PreviewEnrichmentQueue.enqueue(getContext(),kind,id,PreviewEnrichmentQueue.FOREGROUND);
        int generation=++enrichmentGeneration;if(enrichmentTask!=null)enrichmentTask.cancel(true);
        Set<Long> local=new HashSet<>();if(snapshot!=null)for(Entry e:kind.equals("movie")?snapshot.movies:snapshot.shows)local.add(e.onlineId);
        Context app=getContext().getApplicationContext();final int requestedSeason=seasonNumber;
        enrichmentTask=PreviewDetailsData.IO.submit(()->{
            long start=android.os.SystemClock.elapsedRealtime();
            String operation=com.archos.mediacenter.video.diagnostics.Diagnostics.operation("details_enrichment");
            try(com.archos.mediacenter.video.diagnostics.Diagnostics.OperationScope ignored=com.archos.mediacenter.video.diagnostics.Diagnostics.operationScope(operation)){
                PreviewDetailsData.Result cached=requestedSeason>=0?PreviewDetailsData.cachedSeason(app,id,requestedSeason):PreviewDetailsData.cached(app,kind,id,local);
                if(cached!=null)post(()->enqueueEnrichment(cached,generation));
                Consumer<PreviewDetailsData.Result> progress=partial->post(()->enqueueEnrichment(partial,generation));
                PreviewDetailsData.Result result=requestedSeason>=0?PreviewDetailsData.loadSeason(app,id,requestedSeason,progress):PreviewDetailsData.load(app,kind,id,local,progress);
                post(()->enqueueEnrichment(result,generation));
            }catch(Exception error){com.archos.mediacenter.video.diagnostics.Diagnostics.event("details_enrichment_unavailable","operation_id",operation,"media_id",id,"failure_category",error.getClass().getSimpleName());post(()->{if(generation==enrichmentGeneration)enrichmentKey="";});}
            finally{com.archos.mediacenter.video.diagnostics.Diagnostics.event("details_enrichment","operation_id",operation,"elapsed_ms",android.os.SystemClock.elapsedRealtime()-start);com.archos.mediacenter.video.diagnostics.Diagnostics.finishOperation(operation,"details_enrichment",start);}
        });
    }
    private void applyEnrichment(PreviewDetailsData.Result result,int generation){
        if(generation!=enrichmentGeneration)return;
        String oldInformation=informationFingerprint(enrichment);
        String oldCredits=enrichment==null?"":String.valueOf(enrichment.credits);
        String oldEpisodes=enrichment==null?"":enrichment.episodes.toString()+enrichment.seasonAvailability.toString();
        String oldRelated=enrichment==null?"":relatedFingerprint(enrichment);
        String oldExtras=enrichment==null?"":extrasFingerprint(enrichment);
        if(enrichment==null&&remoteDetails!=null){enrichment=new PreviewDetailsData.Result();enrichment.details=remoteDetails;}
        enrichment=PreviewDetailsData.merge(enrichment,result);
        if(enrichment.details!=null&&!enrichment.details.optString("overview").isEmpty()&&(seasonNumber>=0||plot.length()==0||movie!=null&&!movie.hasScraperData()))plot.setText(enrichment.details.optString("overview"));
        if(artworkUri==null&&!artworkReady&&enrichment.details!=null){String path=enrichment.details.optString("backdrop_path");if(path.matches("/[A-Za-z0-9._-]+"))cardArtwork(Uri.parse("https://image.tmdb.org/t/p/w1280"+path));}
        if(!oldCredits.equals(String.valueOf(enrichment.credits)))renderEnrichedPeople();
        if(!oldInformation.equals(informationFingerprint(enrichment)))renderDetails();
        if(!oldRelated.equals(relatedFingerprint(enrichment)))renderRelated();
        if(!oldExtras.equals(extrasFingerprint(enrichment))){
            List<ScraperTrailer> extras=new ArrayList<>();for(PreviewDetailsData.Extra e:enrichment.extras)extras.add(new ScraperTrailer(ScraperTrailer.Type.SHOW_TRAILER,e.name,e.key,"YouTube",""));
            if(enrichment.extrasLoaded||!extras.isEmpty())trailerList=extras;renderExtras();rebuildTabs();
        }
        if(!oldEpisodes.equals(enrichment.episodes.toString()+enrichment.seasonAvailability.toString()))renderSeasons();
    }
    private static String extrasFingerprint(PreviewDetailsData.Result result){StringBuilder text=new StringBuilder().append(result.extrasLoaded);for(PreviewDetailsData.Extra e:result.extras)text.append(e.key).append(e.name).append(e.type);return text.toString();}
    private static String relatedFingerprint(PreviewDetailsData.Result result){StringBuilder text=new StringBuilder().append(result.relatedLoaded);for(PreviewDetailsData.Remote e:result.related)text.append(e.title).append(e.provider==null?0:e.provider.id);return text.toString();}
    private String informationFingerprint(PreviewDetailsData.Result result){
        if(result==null)return "";String text=String.valueOf(result.details)+result.classification+result.alternatives+result.supplementary.imdbVotes;
        for(int slot=0;slot<4;slot++)text+="|"+result.supplementary.value(slot);
        return text+(movie instanceof Episode?result.episodes.toString():"");
    }
    private void rowKeys(View card,LinearLayout row){card.setOnKeyListener((v,key,event)->{if(event.getAction()!=KeyEvent.ACTION_DOWN||key!=KeyEvent.KEYCODE_DPAD_LEFT&&key!=KeyEvent.KEYCODE_DPAD_RIGHT)return false;int next=row.indexOfChild(v)+(key==KeyEvent.KEYCODE_DPAD_LEFT?-1:1);if(next>=0&&next<row.getChildCount())row.getChildAt(next).requestFocus();return true;});}
    private void restoreRowFocus(ViewGroup area,Object key,int y){
        if(key==null)return;View requested=area.findViewWithTag(key),target=requested;boolean fallback=false;
        if(target==null||!target.isShown()||!target.isFocusable()){
            fallback=true;target=null;
            for(View candidate:area.getFocusables(View.FOCUS_FORWARD))if(candidate.isShown()&&candidate.getTag()!=null){target=candidate;break;}
        }
        if(target==null){fallback=true;target=play.getVisibility()==VISIBLE?play:sectionTabs.isEmpty()?null:sectionTabs.get(0);}
        boolean success=target!=null&&target.requestFocus();
        com.archos.mediacenter.video.diagnostics.Diagnostics.focusRestored(requested,findFocus(),fallback,success);
        scrollTo(0,y);final View restored=target;post(()->{if(restored!=null&&restored.hasFocus())scrollTo(0,y);});
    }
    public void play(){if(showPlay!=null){showPlay.run();return;}ObjectAdapter adapter=actions.get();if(adapter==null)return;
        for(int id:new int[]{VideoActionAdapter.ACTION_RESUME,VideoActionAdapter.ACTION_LOCAL_RESUME,VideoActionAdapter.ACTION_PLAY,VideoActionAdapter.ACTION_PLAY_FROM_BEGIN,VideoActionAdapter.ACTION_REMOTE_RESUME})
            for(int i=0;i<adapter.size();i++){Object item=adapter.get(i);if(item instanceof Action&&((Action)item).getId()==id){action.accept((Action)item);return;}}
    }
    private Entry rowEntry(){if(show!=null)return new Entry(show,0,show.getTvshowId(),"");if(movie==null)return null;long showId=0;Snapshot cached=PreviewLibraryLoader.memoryCache();if(movie instanceof Episode&&cached!=null)for(Entry e:cached.episodes)if(((Video)e.media).getId()==movie.getId()){showId=e.show;break;}return new Entry(movie,0,showId,"");}
    private void moreInfo(){
        Dialog dialog=PreviewDialog.create(getContext(),"reader");dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        ScrollView scroll=new ScrollView(getContext());scroll.setFillViewport(false);scroll.setFocusable(true);scroll.setBackground(PreviewDialog.surface(getContext(),false));LinearLayout page=new LinearLayout(getContext());page.setOrientation(LinearLayout.VERTICAL);page.setPadding(dp(24),dp(20),dp(24),dp(22));scroll.addView(page);
        TextView heading=text(title.getText().toString(),28);page.addView(heading);TextView metadata=text(meta.getText()+"  "+context.getText(),13);metadata.setTextColor(0xffadc3d3);page.addView(metadata);
        LinearLayout columns=new LinearLayout(getContext());columns.setPadding(0,dp(10),0,0);page.addView(columns,new LinearLayout.LayoutParams(-1,-2));LinearLayout overview=new LinearLayout(getContext());overview.setOrientation(LinearLayout.VERTICAL);columns.addView(overview,new LinearLayout.LayoutParams(0,-2,1.65f));LinearLayout facts=new LinearLayout(getContext());facts.setOrientation(LinearLayout.VERTICAL);facts.setPadding(dp(30),0,0,0);columns.addView(facts,new LinearLayout.LayoutParams(0,-2,1));
        infoSection(overview,"Overview",show!=null?safe(show.getPlot()):movie==null?"":safe(movie.getDescriptionBody()));
        if(tags!=null){StringBuilder people=new StringBuilder();int count=0;for(Map.Entry<String,String> p:tags.getActors().entrySet()){if(count++==3)break;people.append(p.getKey()).append(safe(p.getValue()).isEmpty()?"":" · "+p.getValue()).append("\n");}if(!safe(tags.getDirectorsFormatted()).isEmpty())people.append("Director · ").append(tags.getDirectorsFormatted());infoSection(overview,"Cast & Crew",people.toString());}
        if(movie!=null){List<String> technical=new ArrayList<>();if(movie.getMeasuredWidth()>0&&movie.getMeasuredHeight()>0)technical.add(movie.getMeasuredWidth()+" × "+movie.getMeasuredHeight());String video=PreviewMediaInfo.format(movie.getCalculatedVideoFormat()),audio=PreviewMediaInfo.format(movie.getCalculatedBestAudioFormat());if(!safe(video).isEmpty())technical.add(video);if(!safe(audio).isEmpty())technical.add(audio);technicalBadges(facts,technical);String file=safe(movie.getFilenameNonCryptic())+"\n"+source(movie.getFileUri());if(movie.getSize()>0)file+=" · "+android.text.format.Formatter.formatFileSize(getContext(),movie.getSize());infoSection(facts,"Indexed library file",file);}
        if(show!=null)infoSection(facts,"Series",meta.getText()+"\n"+context.getText());TextView back=text("Press Back to return to Details",11);back.setPadding(0,dp(26),0,0);back.setTextColor(0xff8da9bb);page.addView(back);
        com.archos.mediacenter.video.diagnostics.Diagnostics.semantic(scroll,"details.reader");
        Runnable removeFrost=PreviewFrost.show((Activity)getContext());dialog.setContentView(scroll);dialog.setOnDismissListener(d->removeFrost.run());dialog.show();android.view.Window window=dialog.getWindow();window.setBackgroundDrawableResource(android.R.color.transparent);window.addFlags(android.view.WindowManager.LayoutParams.FLAG_DIM_BEHIND);window.setDimAmount(.48f);int width=Math.min(dp(840),getResources().getDisplayMetrics().widthPixels-dp(64));int maximum=Math.min(dp(360),getResources().getDisplayMetrics().heightPixels-dp(96));scroll.measure(View.MeasureSpec.makeMeasureSpec(width,View.MeasureSpec.EXACTLY),View.MeasureSpec.makeMeasureSpec(maximum,View.MeasureSpec.AT_MOST));window.setLayout(width,Math.min(maximum,scroll.getMeasuredHeight()));window.setGravity(Gravity.CENTER);scroll.requestFocus();
    }
    private void infoSection(LinearLayout page,String label,String value){if(value==null||value.trim().isEmpty())return;TextView heading=text(label,18);heading.setTextColor(PreviewAccent.color(getContext()));heading.setPadding(0,dp(22),0,dp(8));page.addView(heading);TextView content=text(value.trim(),14);content.setLineSpacing(dp(3),1f);page.addView(content);}
    private void technicalBadges(LinearLayout parent,List<String> values){
        if(values.isEmpty())return;infoSection(parent,"Technical Information","");
        LinearLayout line=null;for(int i=0;i<values.size();i++){if(i%2==0){line=new LinearLayout(getContext());parent.addView(line);}TextView badge=text(values.get(i),11);badge.setPadding(dp(7),dp(5),dp(7),dp(5));badge.setBackground(PreviewDialog.surface(getContext(),false));LinearLayout.LayoutParams lp=new LinearLayout.LayoutParams(0,-2,1);lp.setMargins(0,dp(4),dp(5),0);line.addView(badge,lp);}
    }
    private static String titleStyle(String label){StringBuilder out=new StringBuilder();for(String word:label.split(" ")){if(out.length()>0)out.append(' ');if(!word.isEmpty())out.append(Character.toUpperCase(word.charAt(0))).append(word.substring(1));}return out.toString();}
    private void refreshMoreRows(){
        if(moreDialog==null||!moreDialog.isShowing())return;
        for(Map.Entry<Integer,Action> row:moreRows.entrySet()){
            Action current=PreviewMoreActions.current(actions.get(),row.getValue(),show!=null);
            if(current!=null)PreviewDialog.updateLabel(moreDialog,row.getKey(),titleStyle(String.valueOf(current.getLabel1())));
        }
    }
    private void more(){if(seasonNumber>=0){PreviewDialog.choose(getContext(),"Season "+seasonNumber,new String[]{"Series Details","Episodes"},-1,n->{if(n==0)nativeDetails.run();else{showSection("Seasons & Episodes",true);}});return;}ObjectAdapter adapter=actions.get();if(adapter==null)return;
        moreRows.clear();
        List<String> labels=new ArrayList<>();List<Runnable> commands=new ArrayList<>();Map<Integer,String> logos=new HashMap<>();
        if(movie!=null&&getContext() instanceof Activity){labels.add(movie instanceof Episode?"Restart Episode":"Restart Movie");commands.add(()->com.archos.mediacenter.video.utils.PlayUtils.startVideo((Activity)getContext(),movie,com.archos.mediacenter.video.player.PlayerActivity.RESUME_NO,false,-1,null,-1));}

        if(movie!=null&&movie.hasScraperData()){labels.add("Report Incorrect Match");commands.add(()->{getContext().getSharedPreferences(com.archos.mediaprovider.video.SupernovaLibraryPolicy.HEALTH,0).edit().putBoolean("incorrect:"+movie.getId(),true).apply();getContext().startActivity(new android.content.Intent(getContext(),com.archos.mediacenter.video.leanback.LibraryHealthActivity.class));});}
        String[] groups={"Playback & Navigation","Library","Streaming Services","Files & Media"};
        for(int group=0;group<groups.length;group++){
            List<Action> section=new ArrayList<>();for(int i=0;i<adapter.size();i++){Object value=adapter.get(i);if(!(value instanceof Action))continue;Action a=(Action)value;
                int category=remoteDetails!=null?-1:PreviewMoreActions.group(a,show!=null);
                if(category==group)section.add(a);
            }
            if(section.isEmpty()&&!(group==1&&remoteDetails==null)&&!(group==3&&(movie!=null||show!=null)))continue;
            labels.add("— "+groups[group]);commands.add(null);
            for(Action a:section){moreRows.put(labels.size(),a);labels.add(titleStyle(String.valueOf(a.getLabel1())));commands.add(()->{Action current=PreviewMoreActions.current(actions.get(),a,show!=null);if(current!=null)action.accept(current);refreshMoreRows();});}
            if(group==1){if(remoteDetails==null){labels.add("Add to Row");commands.add(()->PreviewHomeRows.add(getContext(),rowEntry(),()->{}));}}
            if(group==3&&movie!=null){labels.add("Subtitles and Artwork");commands.add(nativeDetails);}
            else if(group==3&&show!=null){labels.add("Artwork");commands.add(nativeDetails);}
        }
        Dialog dialog=PreviewDialog.choose(getContext(),"Actions",labels.toArray(new String[0]),-1,Collections.emptySet(),false,n->{if(commands.get(n)!=null)commands.get(n).run();});moreDialog=dialog;dialog.setOnDismissListener(d->{if(moreDialog==dialog){moreDialog=null;moreRows.clear();}});
    }
    private ScraperTrailer primaryTrailer(){
        ScraperTrailer best=null;int score=0;for(ScraperTrailer t:trailerList){if(!"YouTube".equals(t.mSite)||t.mVideoKey==null||!t.mVideoKey.matches("[A-Za-z0-9_-]{11}"))continue;String name=t.mName==null?"":t.mName.toLowerCase(Locale.ROOT);if(!name.contains("trailer")||name.contains("fan")||name.contains("reaction"))continue;int rank=(name.contains("official")?4:1)+(name.contains("main")?2:0)+(name.contains("teaser")?-1:0);if(rank>score){score=rank;best=t;}}return best;
    }
    private void chooseTrailer(){ScraperTrailer t=primaryTrailer();if(t!=null)PreviewTrailer.show((Activity)getContext(),t);}
    @Override protected void onDetachedFromWindow(){removeCallbacks(deliverEnrichment);pendingEnrichment=null;enrichmentDeliveryScheduled=false;android.view.ViewParent shell=getParent();while(shell!=null){if(shell instanceof TopNavigation){((TopNavigation)shell).setDetailsExpanded(false);break;}shell=shell.getParent();}enrichmentGeneration++;if(enrichmentTask!=null)enrichmentTask.cancel(true);if(artworkLoader!=null)artworkLoader.cancelRequest(cardArtwork);if(reviews!=null)reviews.close();if(observedActions!=null){observedActions.unregisterObserver(actionObserver);observedActions=null;}for(Presenter.ViewHolder h:providerHolders)providerPresenter.onUnbindViewHolder(h);providerHolders.clear();for(Presenter.ViewHolder h:cards)presenter.onUnbindViewHolder(h);cards.clear();for(PreviewLandscapeCard card:landscapeCards)card.release();landscapeCards.clear();for(PreviewLandscapeCard card:extraCards)card.release();extraCards.clear();super.onDetachedFromWindow();}
}
