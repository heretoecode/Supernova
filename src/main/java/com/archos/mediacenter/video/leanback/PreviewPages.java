package com.archos.mediacenter.video.leanback;

import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.view.*;
import android.widget.*;
import androidx.recyclerview.widget.*;
import androidx.leanback.widget.Presenter;
import com.archos.mediacenter.video.leanback.PreviewLibraryLoader.*;
import com.archos.mediacenter.video.leanback.adapter.object.Box;
import com.archos.mediacenter.video.leanback.presenter.PreviewCardPresenter;
import java.util.*;
import com.archos.mediacenter.video.browser.adapters.object.*;

/** Recycled native TV grids. Only visible artwork is decoded. Classic Browse remains untouched. */
public final class PreviewPages extends FrameLayout {
    public interface Click { void open(Presenter.ViewHolder holder,Object item); }
    private final RecyclerView list;
    private final GridLayoutManager layout;
    private final PageAdapter adapter=new PageAdapter();
    private final Click click;
    private Snapshot snapshot=new Snapshot();
    private List<Box> files=new ArrayList<>();
    private final List<com.archos.mediacenter.video.leanback.adapter.object.Shortcut> librarySources=new ArrayList<>(),savedLocations=new ArrayList<>();
    private boolean sourcesLoading;
    private boolean networkEntryPending;
    private void loadSources(){if(sourcesLoading)return;sourcesLoading=true;Context app=getContext().getApplicationContext();new Thread(()->{
        List<com.archos.mediacenter.video.leanback.adapter.object.Shortcut> indexed=new ArrayList<>(),saved=new ArrayList<>();
        try(android.database.Cursor c=com.archos.mediacenter.utils.ShortcutDbAdapter.VIDEO.getAllShortcuts(app,null,null)){if(c!=null){com.archos.mediacenter.video.leanback.adapter.NetworkShortcutMapper m=new com.archos.mediacenter.video.leanback.adapter.NetworkShortcutMapper();m.bindColumns(c);while(c.moveToNext())indexed.add((com.archos.mediacenter.video.leanback.adapter.object.Shortcut)m.bind(c));}}
        catch(Exception e){com.archos.mediacenter.video.diagnostics.Diagnostics.error("source_summary_unavailable",e);}
        try(android.database.Cursor c=com.archos.mediacenter.video.browser.ShortcutDb.STATIC.getCursorAllShortcuts(app)){if(c!=null){com.archos.mediacenter.video.leanback.adapter.GenericNetworkShortcutMapper m=new com.archos.mediacenter.video.leanback.adapter.GenericNetworkShortcutMapper();m.bindColumns(c);while(c.moveToNext())saved.add((com.archos.mediacenter.video.leanback.adapter.object.Shortcut)m.bind(c));}}
        catch(Exception e){com.archos.mediacenter.video.diagnostics.Diagnostics.error("saved_locations_unavailable",e);}
        post(()->{sourcesLoading=false;librarySources.clear();librarySources.addAll(indexed);savedLocations.clear();savedLocations.addAll(saved);if(tab==3&&isAttachedToWindow())render("sources_loaded");});
    },"SupernovaSourceSummary").start();}
    private void openSource(com.archos.mediacenter.video.leanback.adapter.object.Shortcut source){getContext().startActivity(new android.content.Intent(getContext(),com.archos.mediacenter.video.leanback.network.NetworkShortcutDetailsActivity.class).putExtra(com.archos.mediacenter.video.leanback.network.NetworkShortcutDetailsFragment.EXTRA_SHORTCUT,source));}

    private final List<Cell> cells=new ArrayList<>();
    private int tab;
    private final FocusAnchor[] anchors=new FocusAnchor[4];
    private final android.os.Parcelable[] scrollStates=new android.os.Parcelable[4];
    private boolean restoringFocus, switchingTab;
    private boolean returnPending, suspended;
    private int returnTab;
    private android.os.Parcelable returnScroll;
    private final Map<String,android.os.Parcelable> railScrollStates=new HashMap<>();
    private android.content.SharedPreferences preferences;
    private int focusGeneration;
    private static final class FocusAnchor {
        String cell, child,token=""; int position, inner;
    }
    private String cellKey(Cell c){return c.type+":"+(c.type==POSTER?((Entry)c.value).key():c.type==STORAGE?((Box)c.value).getBoxId()+":"+((Box)c.value).getPath():c.type==RAIL&&!c.homeRowId.isEmpty()?c.homeRowId:c.title);}
    private boolean inside(View view,View ancestor){while(view!=null){if(view==ancestor)return true;android.view.ViewParent p=view.getParent();view=p instanceof View?(View)p:null;}return false;}
    private final class FocusRecycler extends PreviewFocusRecycler {
        final boolean horizontal;
        FocusRecycler(Context c,boolean horizontal){super(c);this.horizontal=horizontal;}
        @Override protected boolean focusablePosition(int p){if(horizontal)return true;if(p<0||p>=cells.size())return false;Cell c=cells.get(p);return c.type!=NOTICE&&(c.type!=HEADER||Boolean.TRUE.equals(c.value));}
        @Override public boolean requestChildRectangleOnScreen(View child,android.graphics.Rect rect,boolean immediate){
            int position=getChildAdapterPosition(child);
            if(!horizontal&&(tab==1||tab==2)&&position>=0&&position<cells.size()&&cells.get(position).type==POSTER)return false;
            return super.requestChildRectangleOnScreen(child,rect,immediate);
        }
        @Override public View focusSearch(View focused,int direction){
            View next=super.focusSearch(focused,direction);
            // At loaded-content boundaries retain the last valid card; do not let
            // RecyclerView's ancestor search fall through to global navigation.
            if(horizontal&&(direction==FOCUS_LEFT||direction==FOCUS_RIGHT)&&!inside(next,this))return focused;
            if(!horizontal&&!inside(next,PreviewPages.this)&&!(direction==FOCUS_UP&&atTop()))return focused;
            return next;
        }
    }
    private void rememberFocus(){
        if(restoringFocus||switchingTab||suspended)return;
        View focused=list.findFocus();if(focused==null)return;
        View item=list.findContainingItemView(focused);if(item==null)return;
        int pos=list.getChildAdapterPosition(item);if(pos<0||pos>=cells.size())return;
        FocusAnchor a=new FocusAnchor();a.cell=cellKey(cells.get(pos));a.position=pos;
        a.child=focused.getTag() instanceof String?(String)focused.getTag():null;
        if(anchors[tab]!=null&&a.cell.equals(anchors[tab].cell)&&java.util.Objects.equals(a.child,anchors[tab].child))a.token=anchors[tab].token;
        if(cells.get(pos).type==RAIL){List<Entry> entries=(List<Entry>)cells.get(pos).value;for(int i=0;i<entries.size();i++)if(entries.get(i).key().equals(a.child)){a.inner=i;break;}}
        anchors[tab]=a;
        if(cells.get(pos).type==RAIL&&item instanceof ViewGroup){
            RecyclerView rail=(RecyclerView)((ViewGroup)item).getChildAt(0);
            railScrollStates.put(a.cell,rail.getLayoutManager().onSaveInstanceState());
        }
    }
    @Override public void requestChildFocus(View child,View focused){super.requestChildFocus(child,focused);if(list!=null){rememberFocus();if(tab==1||tab==2){View item=list.findContainingItemView(focused);int p=item==null?-1:list.getChildAdapterPosition(item);if(p>=0&&p<cells.size()&&cells.get(p).value instanceof Entry){lastArtwork[tab]=((Entry)cells.get(p).value).backdrop;artwork.accept(lastArtwork[tab]);if(!restoringFocus&&!suspended)post(()->{if(focused.hasFocus()&&!restoringFocus&&!suspended){int centre=list.getPaddingTop()+(list.getHeight()-list.getPaddingTop()-list.getPaddingBottom())/2;int delta=(item.getTop()+item.getBottom())/2-centre;int row=Math.max(1,item.getHeight());if(Math.abs(delta)>row/3){list.stopScroll();list.smoothScrollBy(0,Math.max(-row,Math.min(row,delta)));}}});}}}}

    /** A launch transaction owns return focus, independently of Android's retained View focus. */
    private void beginReturn(View opener,String destination){
        rememberFocus();
        if(anchors[tab]==null)return;
        anchors[tab].token=com.archos.mediacenter.video.diagnostics.Diagnostics.focusEntry(opener,destination);
        returnTab=tab;returnScroll=layout.onSaveInstanceState();returnPending=true;
    }
    public void suspendForChild(){
        rememberFocus();
        if(returnPending)returnScroll=layout.onSaveInstanceState();
        suspended=true;++focusGeneration;restoringFocus=false;
    }
    public void resumeFromChild(){suspended=false;post(this::restoreReturn);}
    @Override public void onWindowFocusChanged(boolean focused){
        super.onWindowFocusChanged(focused);
        if(focused)post(this::restoreReturn);
    }
    private void restoreReturn(){
        if(!returnPending||suspended||tab!=returnTab||!isAttachedToWindow()||!hasWindowFocus()||!isShown())return;
        if(returnScroll!=null)layout.onRestoreInstanceState(returnScroll);
        holdFocus();restoreFocus();
    }
    @Override protected boolean onRequestFocusInDescendants(int direction,android.graphics.Rect rect){
        if(restoringFocus)return false;
        if(anchors[tab]!=null){holdFocus();restoreFocus();return true;}
        return super.onRequestFocusInDescendants(direction,rect);
    }
    private void holdFocus(){restoringFocus=true;setDescendantFocusability(FOCUS_BLOCK_DESCENDANTS);requestFocus();setDescendantFocusability(FOCUS_AFTER_DESCENDANTS);}
    private View tagged(View root,String tag){if(tag!=null&&tag.equals(root.getTag())&&root.isFocusable())return root;if(root instanceof ViewGroup){ViewGroup g=(ViewGroup)root;for(int i=0;i<g.getChildCount();i++){View found=tagged(g.getChildAt(i),tag);if(found!=null)return found;}}return null;}
    private void restoreFocus(){
        final int generation=++focusGeneration;final FocusAnchor anchor=anchors[tab];
        int position=anchor==null?0:Math.min(anchor.position,Math.max(0,cells.size()-1));
        if(anchor!=null)for(int i=0;i<cells.size();i++)if(cellKey(cells.get(i)).equals(anchor.cell)){position=i;break;}
        final int target=position;
        if(layout.findViewByPosition(target)==null)list.scrollToPosition(target);
        list.postOnAnimation(new Runnable(){int attempts;
            public void run(){
                if(generation!=focusGeneration)return;
                if(!hasFocus()||suspended){restoringFocus=false;return;}
                View item=layout.findViewByPosition(target);
                if(item==null||list.hasPendingAdapterUpdates()){if(attempts++<8){list.postOnAnimation(this);return;}}
                if(item!=null&&anchor!=null&&target<cells.size()&&cells.get(target).type==RAIL){
                    RecyclerView rail=(RecyclerView)((ViewGroup)item).getChildAt(0);
                    List<Entry> entries=(List<Entry>)cells.get(target).value;
                    int inner=Math.min(anchor.inner,Math.max(0,entries.size()-1));
                    for(int i=0;i<entries.size();i++)if(entries.get(i).key().equals(anchor.child)){inner=i;break;}
                    if(rail.findViewHolderForAdapterPosition(inner)==null&&attempts++<8){rail.scrollToPosition(inner);list.postOnAnimation(this);return;}
                    RecyclerView.ViewHolder holder=rail.findViewHolderForAdapterPosition(inner);if(holder!=null)item=holder.itemView;
                }
                View exact=item==null||anchor==null?null:tagged(item,anchor.child);
                if(exact!=null)exact.requestFocus();else if(item!=null&&item.requestFocus()){}else list.requestFocus();
                restoringFocus=false;
                if(anchor!=null&&!anchor.token.isEmpty()){com.archos.mediacenter.video.diagnostics.Diagnostics.focusRestored(anchor.token,null,list.findFocus(),exact==null,list.findFocus()!=null);anchor.token="";if(anchors[tab]!=null)anchors[tab].token="";}
                rememberFocus();
                if(returnPending&&tab==returnTab&&list.findFocus()!=null){returnPending=false;returnScroll=null;}
            }
        });
    }
    private final int[] sorts={0,0,0};
    private final String[] genres={"","",""};
    private final int[] years={0,0,0};
    private final String[] providers={"","",""};
    private final String[] selectedYears={"","",""};
    private final boolean[] unmatched={false,false,false};
    private boolean loaded;
    private final Map<String,Integer> quietOrder=new HashMap<>();
    private PreviewDiscovery discovery=new PreviewDiscovery();
    private java.util.concurrent.ExecutorService worker;
    private java.util.function.Consumer<android.net.Uri> artwork=uri->{};
    private final boolean[] ascending={false,false,false};
    private final boolean[] listMode={false,false,false};
    private int featuredIndex;
    private PreviewFeaturedIndicators featuredIndicators;
    private int featuredDirection;
    private boolean featuredMoving;
    private void changeFeatured(int direction){
        lastInteraction=android.os.SystemClock.elapsedRealtime();
        if(featuredMoving||featuredCandidates().size()<2)return;
        View focused=findFocus();View card=focused==null?null:list.findContainingItemView(focused);
        featuredMoving=true;
        Runnable next=()->{for(android.view.ViewParent parent=getParent();parent!=null;parent=parent.getParent())if(parent instanceof TopNavigation){((TopNavigation)parent).setFeaturedDirection(direction);break;}featuredIndex+=direction;featuredDirection=direction;featuredMoving=false;renderFeatured();};
        if(card!=null)card.animate().translationX(-dp(18)*direction).alpha(.3f).setDuration(150).withEndAction(next).start();else next.run();
    }
    private final android.net.Uri[] lastArtwork=new android.net.Uri[4];
    private final Map<String,String> featuredReasons=new HashMap<>();
    private List<Entry> featuredCandidates(){List<List<Entry>> sources=new ArrayList<>();featuredReasons.clear();if(preferences==null||preferences.getBoolean("preview_featured_recent",true))sources.add(snapshot.recent);else sources.add(Collections.emptyList());sources.add(preferences!=null&&preferences.getBoolean("preview_featured_trending",true)?discovery.matches(snapshot,true):Collections.emptyList());sources.add(preferences!=null&&preferences.getBoolean("preview_featured_popular",true)?discovery.matches(snapshot,false):Collections.emptyList());List<Entry> result=new ArrayList<>();Set<String> seen=new HashSet<>();String[] reasons={"RECENTLY ADDED","TRENDING ON TRAKT · IN YOUR LIBRARY","POPULAR ON TRAKT · IN YOUR LIBRARY"};for(int i=0;i<12;i++)for(int n=0;n<sources.size();n++){List<Entry> source=sources.get(n);if(i<source.size()){Entry e=source.get(i);if(seen.add(e.key())){result.add(e);featuredReasons.put(e.key(),reasons[n]);}}}if(result.isEmpty()){List<Entry> local=new ArrayList<>(snapshot.movies);local.addAll(snapshot.shows);for(Entry e:local)if(seen.add(e.key())){result.add(e);featuredReasons.put(e.key(),"IN YOUR LIBRARY");if(result.size()==8)break;}}return result.subList(0,Math.min(8,result.size()));}
    private final PreviewLibraryColumns[] columns=new PreviewLibraryColumns[3];
    private long lastInteraction;
    private final Runnable rotateFeatured=new Runnable(){public void run(){
        if(isAttachedToWindow()){
            boolean heroFocus=findFocus()!=null&&findFocus().getTag() instanceof String&&((String)findFocus().getTag()).startsWith("hero:");
            if(tab==0&&hasWindowFocus()&&isShown()&&!list.canScrollVertically(-1)&&!heroFocus&&android.os.SystemClock.elapsedRealtime()-lastInteraction>=30000&&featuredCandidates().size()>1){featuredIndex++;renderFeatured();}
            postDelayed(this,30000);
        }
    }};
    private Runnable ready=()->{};
    private final Runnable prioritiseVisible=this::prioritiseVisibleEntries;
    private void prioritiseVisibleEntries(){
        if(!loaded||!isAttachedToWindow()||tab==3)return;
        List<Entry> visible=new ArrayList<>(),next=new ArrayList<>();int last=-1;
        for(int n=0;n<list.getChildCount();n++){
            int position=list.getChildAdapterPosition(list.getChildAt(n));
            if(position<0||position>=cells.size())continue;last=Math.max(last,position);
            Cell cell=cells.get(position);
            if(cell.value instanceof Entry)visible.add((Entry)cell.value);
            else if(cell.type==RAIL){
                View item=list.getChildAt(n);
                if(item instanceof ViewGroup&&((ViewGroup)item).getChildCount()>0&&((ViewGroup)item).getChildAt(0) instanceof RecyclerView)
                    collectVisibleRail((RecyclerView)((ViewGroup)item).getChildAt(0),(List<Entry>)cell.value,visible,next);
            }
        }
        for(int n=last+1;n<Math.min(cells.size(),last+13);n++)if(cells.get(n).value instanceof Entry)next.add((Entry)cells.get(n).value);
        PreviewEnrichmentQueue.visible(getContext(),visible,next);
    }
    static void collectVisibleRail(RecyclerView rail,List<Entry> entries,List<Entry> visible,List<Entry> next){
        int last=-1;
        for(int i=0;i<rail.getChildCount();i++){
            View child=rail.getChildAt(i);int position=rail.getChildAdapterPosition(child);
            if(position>=0&&position<entries.size()&&child.getRight()>rail.getPaddingLeft()&&child.getLeft()<rail.getWidth()-rail.getPaddingRight()){
                visible.add(entries.get(position));last=Math.max(last,position);
            }
        }
        if(last>=0)next.addAll(entries.subList(last+1,Math.min(entries.size(),last+7)));
    }
    private void scheduleVisibleEnrichment(){removeCallbacks(prioritiseVisible);postDelayed(prioritiseVisible,300);}
    public boolean hasComposedContent(){return loaded&&!list.isComputingLayout()&&list.getChildCount()>0&&list.getWidth()>0;}
    private boolean visibleArtworkReady(View view){if(view instanceof TextView&&!OfficialTitleArtwork.readyForFirstFrame((TextView)view))return false;if(view instanceof PreviewCardPresenter.Card)return ((PreviewCardPresenter.Card)view).artworkReady;if(view instanceof ViewGroup){ViewGroup group=(ViewGroup)view;for(int i=0;i<group.getChildCount();i++){View child=group.getChildAt(i);if(child.getVisibility()==VISIBLE&&!visibleArtworkReady(child))return false;}}return true;}
    public void setReadyListener(Runnable listener){ready=listener;if(loaded)post(ready);}
    public boolean hasLoadedSnapshot(){return loaded;}
    public void setArtworkListener(java.util.function.Consumer<android.net.Uri> listener){artwork=listener;updateArtwork();}
    public void setDiscovery(PreviewDiscovery value){requestedDiscovery=true;discovery=value;render("discovery_update");}
    private boolean requestedDiscovery;
    private final Runnable providerRefresh=()->{if(isAttachedToWindow()&&(tab==1||tab==2)&&!providers[tab].isEmpty()){
        com.archos.mediacenter.video.diagnostics.Diagnostics.event("library_filter_refresh","reason","provider_cache_changed","tab",tab);
        render("provider_cache_changed"); // Diff the existing snapshot; never re-query the media library here.
    }};
    static boolean providerCacheAffectsPage(String key,int tab,String country,boolean filtered){
        return filtered&&key!=null&&(tab==1||tab==2)
                &&key.startsWith("streaming_known_at:"+(tab==1?"movie:":"tv:"))&&key.endsWith(":"+country+":-1");
    }
    private final android.content.SharedPreferences.OnSharedPreferenceChangeListener providerSettings=(prefs,key)->{
        if(key==null)return;
        boolean scope=key.equals("streaming_country")||key.equals("streaming_enabled");
        if(scope)PreviewEnrichmentQueue.library(getContext(),snapshot,tab);
        if(scope||key.startsWith("streaming_providers_")||providerCacheAffectsPage(key,tab,
                com.archos.mediacenter.video.streaming.StreamingRepository.country(getContext()),tab>0&&tab<3&&!providers[tab].isEmpty())){
            removeCallbacks(providerRefresh);postDelayed(providerRefresh,500);
        }
    };
    private final android.content.SharedPreferences.OnSharedPreferenceChangeListener homeSettings=(prefs,key)->{if(key!=null&&(key.startsWith("preview_featured_")||key.equals("preview_home_rows41")||key.equals("preview_accent41")||key.equals("hide_watched")||key.equals("sort_ignore_articles")))post(()->{if(isAttachedToWindow()){if(key.equals("hide_watched")||key.equals("sort_ignore_articles"))quietOrder.clear();render("home_settings_changed");}});};
    @Override protected void onAttachedToWindow(){super.onAttachedToWindow();preferences.registerOnSharedPreferenceChangeListener(homeSettings);preferences.registerOnSharedPreferenceChangeListener(providerSettings);requestDiscovery();lastInteraction=android.os.SystemClock.elapsedRealtime();postDelayed(rotateFeatured,30000);}
    private void requestDiscovery(){if(requestedDiscovery||!isAttachedToWindow()||snapshot.movies.isEmpty()&&snapshot.shows.isEmpty())return;requestedDiscovery=true;
        worker=java.util.concurrent.Executors.newSingleThreadExecutor();worker.execute(()->{try{PreviewDiscovery result=PreviewDiscovery.load(getContext().getApplicationContext());post(()->{if(isAttachedToWindow())setDiscovery(result);});}finally{worker.shutdown();}});}
    @Override protected void onDetachedFromWindow(){removeCallbacks(rotateFeatured);removeCallbacks(providerRefresh);if(worker!=null)worker.shutdownNow();preferences.unregisterOnSharedPreferenceChangeListener(homeSettings);preferences.unregisterOnSharedPreferenceChangeListener(providerSettings);super.onDetachedFromWindow();}
    private Entry featured(){List<Entry> entries=tab==1?snapshot.movies:tab==2?snapshot.shows:featuredCandidates();return entries.isEmpty()?null:entries.get(Math.floorMod(featuredIndex,entries.size()));}
    private void updateArtwork(){if((tab==1||tab==2)&&loaded){if(lastArtwork[tab]!=null){artwork.accept(lastArtwork[tab]);return;}Entry first=featured();artwork.accept(first==null?null:first.backdrop);return;}Entry entry=featured();artwork.accept(tab==3||entry==null?null:entry.backdrop);}
    public static String displayName(Entry e){return e.media instanceof Episode?((Episode)e.media).getShowName():e.media.getName();}
    private void open(Entry e,View v){beginReturn(v,"details");click.open(new Presenter.ViewHolder(v),e.media);}
    private void play(Entry e,View v){
        if(e.media instanceof Tvshow){List<Entry> episodes=new ArrayList<>();for(Entry ep:snapshot.episodes)if(ep.show==e.show)episodes.add(ep);Entry next=PreviewSeriesJourney.select(getContext(),episodes).episode;if(next!=null){play(next,v);return;}}
        if(e.media instanceof Video && getContext() instanceof android.app.Activity){
            beginReturn(v,"playback");
            com.archos.mediacenter.video.utils.PlayUtils.startVideo((android.app.Activity)getContext(),(Video)e.media,com.archos.mediacenter.video.player.PlayerActivity.RESUME_FROM_LAST_POS,false,-1,null,-1);
        }else open(e,v);
    }
    private static final int HEADER=0, POSTER=1, RAIL=2, STORAGE=3, HERO=4, NOTICE=5, LIST=6, SCAN=7, CUSTOMISE=8, NETWORK_PANEL=9, NETWORK=10;
    static class Cell {
        int type; String title; Object value; String signature="",homeRowId=""; int viewType;
        Cell(int type,String title,Object value) {this.type=type;this.title=title;this.value=value;}
    }
    public PreviewPages(Context c,Click click) {
        super(c); this.click=click; setBackgroundColor(Color.TRANSPARENT);setFocusable(true);setFocusableInTouchMode(true);setDescendantFocusability(FOCUS_AFTER_DESCENDANTS);
        list=new FocusRecycler(c,false); list.setClipChildren(false);list.setClipToPadding(false);setClipChildren(false); list.setPadding(dp(28),dp(10),dp(28),dp(12));
        layout=new GridLayoutManager(c,24); layout.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup(){@Override public int getSpanSize(int p){return cells.get(p).type==POSTER?(tab<3&&listMode[tab]?24:4):cells.get(p).type==NETWORK_PANEL?8:cells.get(p).type==STORAGE?6:24;}});
        adapter.setHasStableIds(true);
        list.addOnScrollListener(new RecyclerView.OnScrollListener(){@Override public void onScrolled(RecyclerView rv,int dx,int dy){notifyScroll();scheduleVisibleEnrichment();}});list.setLayoutManager(layout); list.setAdapter(adapter); list.setItemAnimator(null);
        com.archos.mediacenter.video.diagnostics.Diagnostics.uiRebuild(list,"library.adapter","adapter_created",0,adapter.getItemCount(),true);
        addView(list,new FrameLayout.LayoutParams(-1,-1));
        preferences=androidx.preference.PreferenceManager.getDefaultSharedPreferences(c);
        if(preferences.getBoolean("remember_library_views",true))for(int i=1;i<=2;i++){
            String k="preview_library_"+i+"_";sorts[i]=Math.max(0,Math.min(4,preferences.getInt(k+"sort",0)));genres[i]=preferences.getString(k+"genre","");years[i]=preferences.getInt(k+"year",0);selectedYears[i]=preferences.getString(k+"years",years[i]==0?"":String.valueOf(years[i]));providers[i]=preferences.getString(k+"providers","");ascending[i]=preferences.getBoolean(k+"ascending",false);listMode[i]=preferences.getBoolean(k+"list",false);
        }
        columns[1]=new PreviewLibraryColumns(c,false);columns[2]=new PreviewLibraryColumns(c,true);
        render("initial");
        if(PreviewLibraryLoader.memoryCache()==null){java.util.concurrent.ExecutorService cacheWorker=java.util.concurrent.Executors.newSingleThreadExecutor();cacheWorker.execute(()->{try{Snapshot previous=PreviewLibraryLoader.readCache(c.getApplicationContext());post(()->{if(!loaded&&previous!=null)setSnapshot(previous);});}finally{cacheWorker.shutdown();}});}
    }
    private java.util.function.Consumer<Boolean> scrollListener=value->{};
    private int navigationInset,utilityNavigationInset;
    public void setNavigationInset(int inset){navigationInset=inset;applyNavigationInset();}
    public void setUtilityNavigationInset(int inset){if(utilityNavigationInset==inset)return;utilityNavigationInset=inset;applyNavigationInset();}
    private void applyNavigationInset(){list.setPadding(dp(28),tab==3?utilityNavigationInset:dp(10)+navigationInset,dp(28),tab==3?0:dp(12));}
    public void setScrollListener(java.util.function.Consumer<Boolean> listener){scrollListener=listener;notifyScroll();}
    private void notifyScroll(){scrollListener.accept(list.canScrollVertically(-1));}
    public boolean atTop() {
        View focused=list.findFocus(); if(focused==null)return isFocused()&&!restoringFocus;
        View item=list.findContainingItemView(focused);if(item==null)return false;
        int p=list.getChildAdapterPosition(item);if(p<0||p>=cells.size())return false;
        Cell cell=cells.get(p);
        if(tab==0)return cell.type==HERO && item.getTop()>=list.getPaddingTop() || cell.type==CUSTOMISE&&cells.stream().noneMatch(c->c.type==HERO)&&!list.canScrollVertically(-1);
        if(tab==1||tab==2)return cell.type==HEADER&&Boolean.TRUE.equals(cell.value)&&focused.getTag() instanceof String&&((String)focused.getTag()).startsWith("control:")&&!list.canScrollVertically(-1)
                ||cell.type==HERO&&item.getTop()>=list.getPaddingTop();
        if(cell.type==NETWORK){
            View cursor=focused;
            while(cursor!=null&&cursor!=item){
                if(cursor instanceof PreviewNetworkWorkspace)return ((PreviewNetworkWorkspace)cursor).atTop();
                cursor=cursor.getParent() instanceof View?(View)cursor.getParent():null;
            }
            return false;
        }
        if(cell.type==NETWORK_PANEL)return p<=3&&!list.canScrollVertically(-1);
        if(cell.type==SCAN)return !list.canScrollVertically(-1);
        if(cell.type!=STORAGE)return false;
        int first=0;while(first<cells.size()&&cells.get(first).type!=STORAGE)first++;
        return cells.stream().noneMatch(c->c.type==SCAN)&&first<cells.size()&&layout.getSpanSizeLookup().getSpanGroupIndex(p,24)==layout.getSpanSizeLookup().getSpanGroupIndex(first,24)&&!list.canScrollVertically(-1);
    }
    public void setTab(int tab) {
        if(rowControlOverlay!=null){removeView(rowControlOverlay);rowControlOverlay=null;}
        networkEntryPending=tab==3;
        if(tab==3)loadSources();
        if(this.tab==tab){if(tab==3)requestNetworkEntryFocus();return;}
        returnPending=false;returnScroll=null;++focusGeneration;restoringFocus=false;
        rememberFocus();scrollStates[this.tab]=layout.onSaveInstanceState();
        // TopNavigation owns the single full-viewport background, including the header.
        quietOrder.clear();switchingTab=true;this.tab=tab;applyNavigationInset();setBackground(null);featuredIndex=0;render("tab_change");switchingTab=false;
        if(scrollStates[tab]!=null)layout.onRestoreInstanceState(scrollStates[tab]);else list.scrollToPosition(0);PreviewMetadata.library(getContext(),snapshot,tab);
    }
    private void requestNetworkEntryFocus(){
        if(!networkEntryPending||tab!=3||!isAttachedToWindow())return;
        View overview=list.findViewWithTag("semantic:network.category.overview");
        if(overview!=null&&overview.requestFocus())networkEntryPending=false;
    }
    public void setSnapshot(Snapshot s) { if(s==null)return;
        quietOrder.clear();if(loaded&&(tab==1||tab==2))for(Cell cell:cells)if(cell.type==POSTER)quietOrder.put(((Entry)cell.value).key(),quietOrder.size());
        if(loaded&&tab==0){String featuredKey=featured()==null?null:featured().key();if(featuredKey!=null)for(int i=0;i<Math.min(5,s.recent.size());i++)if(s.recent.get(i).key().equals(featuredKey)){featuredIndex=i;break;}}
        snapshot=s;loaded=true;render("indexed_snapshot");PreviewEnrichmentQueue.library(getContext(),s,tab);PreviewMetadata.library(getContext(),s,tab);post(ready);postDelayed(this::requestDiscovery,750);
    }
    private static void keepOrder(List<Entry> old,List<Entry> current){Map<String,Integer> rank=new HashMap<>();for(Entry e:old)rank.put(e.key(),rank.size());current.sort(Comparator.comparingInt(e->rank.getOrDefault(e.key(),Integer.MAX_VALUE)));}
    public void setFiles(List<Box> f) {
        boolean same=f.size()==files.size();
        for(int i=0;same && i<f.size();i++)same=f.get(i).getBoxId()==files.get(i).getBoxId() && Objects.equals(f.get(i).getName(),files.get(i).getName()) && Objects.equals(f.get(i).getPath(),files.get(i).getPath());
        if(same)return;files=f;if(tab==3)render("storage_changed");
    }
    private void header(String title,boolean controls) {cells.add(new Cell(HEADER,title,controls));}
    private void rail(String title,List<Entry> items) {if(items.isEmpty())return; header(title,false);cells.add(new Cell(RAIL,title,new ArrayList<>(items.subList(0,items.size()))));}
    private void persistViews(){
        if(preferences==null||!preferences.getBoolean("remember_library_views",true)||tab<1||tab>2)return;
        String k="preview_library_"+tab+"_";preferences.edit().putInt(k+"sort",sorts[tab]).putString(k+"genre",genres[tab]).putInt(k+"year",years[tab]).putString(k+"years",selectedYears[tab]).putString(k+"providers",providers[tab]).putBoolean(k+"ascending",ascending[tab]).putBoolean(k+"list",listMode[tab]).apply();
    }
    @Override public boolean dispatchKeyEvent(KeyEvent event){
        if(event.getAction()==KeyEvent.ACTION_UP&&event.getKeyCode()==rowControlConsumedKey){rowControlConsumedKey=-1;return true;}
        if(tab==0&&event.getAction()==KeyEvent.ACTION_DOWN&&event.getKeyCode()==KeyEvent.KEYCODE_DPAD_LEFT){
            View focus=findFocus(),item=focus==null?null:list.findContainingItemView(focus);
            int position=item==null?-1:list.getChildAdapterPosition(item);
            if(position>=0&&cells.get(position).type==RAIL&&!cells.get(position).homeRowId.isEmpty()&&item instanceof ViewGroup){
                View child=((ViewGroup)item).getChildAt(0);
                if(child instanceof RecyclerView){RecyclerView rail=(RecyclerView)child;View card=rail.findContainingItemView(focus);
                    if(card!=null&&rail.getChildAdapterPosition(card)==0){showRowControls(cells.get(position).homeRowId,card);return true;}
                }
            }
        }
        if(tab==0&&event.getAction()==KeyEvent.ACTION_DOWN&&(event.getKeyCode()==KeyEvent.KEYCODE_DPAD_LEFT||event.getKeyCode()==KeyEvent.KEYCODE_DPAD_RIGHT)){
            View focused=findFocus();String tag=focused==null?"":String.valueOf(focused.getTag());if(tag.equals("hero:info")||tag.equals("hero:play")){changeFeatured(event.getKeyCode()==KeyEvent.KEYCODE_DPAD_LEFT?-1:1);return true;}}
        if(tab==0&&event.getAction()==KeyEvent.ACTION_DOWN&&event.getKeyCode()==KeyEvent.KEYCODE_DPAD_DOWN&&findFocus()!=null&&String.valueOf(findFocus().getTag()).startsWith("hero:")){for(int i=0;i<cells.size();i++)if(cells.get(i).type==RAIL){FocusAnchor anchor=new FocusAnchor();anchor.cell=cellKey(cells.get(i));anchor.position=i;anchors[tab]=anchor;holdFocus();restoreFocus();return true;}}
        if(event.getAction()==KeyEvent.ACTION_DOWN)lastInteraction=android.os.SystemClock.elapsedRealtime();
        if((tab==1||tab==2)&&event.getAction()==KeyEvent.ACTION_DOWN&&event.getKeyCode()==KeyEvent.KEYCODE_DPAD_UP){
            View focused=list.findFocus(),item=focused==null?null:list.findContainingItemView(focused);
            int p=item==null?-1:list.getChildAdapterPosition(item);
            if(p>=0&&p<cells.size()){
                Cell c=cells.get(p);int header=-1,first=-1;
                for(int i=0;i<cells.size();i++){if(cells.get(i).type==HEADER&&Boolean.TRUE.equals(cells.get(i).value))header=i;if(cells.get(i).type==POSTER){first=i;break;}}
                boolean firstRow=first>=0&&c.type==POSTER&&layout.getSpanSizeLookup().getSpanGroupIndex(p,24)==layout.getSpanSizeLookup().getSpanGroupIndex(first,24);
                if(firstRow&&header>=0){FocusAnchor a=new FocusAnchor();a.cell=cellKey(cells.get(header));a.position=header;a.child="control:0";anchors[tab]=a;holdFocus();layout.scrollToPositionWithOffset(0,0);restoreFocus();return true;}
                if((c.type==HEADER||c.type==HERO)&&list.canScrollVertically(-1)){layout.scrollToPositionWithOffset(0,0);return true;}
            }
        }
        if(tab==0&&event.getAction()==KeyEvent.ACTION_DOWN&&event.getKeyCode()==KeyEvent.KEYCODE_DPAD_UP){
            View focused=list.findFocus(),item=focused==null?null:list.findContainingItemView(focused);
            if(item!=null){int p=list.getChildAdapterPosition(item);if(p>=0&&p<cells.size()){
                if(cells.get(p).type==HERO&&item.getTop()<list.getPaddingTop()){layout.scrollToPositionWithOffset(0,0);return true;}
                if(cells.get(p).type==RAIL){boolean first=true;for(int i=0;i<p;i++)if(cells.get(i).type==RAIL)first=false;
                    if(first){FocusAnchor a=new FocusAnchor();a.cell=HERO+":Featured";a.child="hero:info";anchors[tab]=a;holdFocus();layout.scrollToPositionWithOffset(0,0);restoreFocus();return true;}}
            }}
        }
        return super.dispatchKeyEvent(event);
    }
    private View rowControlOverlay;private int rowControlConsumedKey=-1;
    private void showRowControls(String id,View opener){
        if(rowControlOverlay!=null)return;
        LinearLayout controls=new LinearLayout(getContext());controls.setTag("semantic:home.row.controls");controls.setOrientation(LinearLayout.VERTICAL);controls.setPadding(dp(8),dp(8),dp(8),dp(8));controls.setBackground(PreviewDialog.menuSurface(getContext()));controls.setElevation(dp(12));
        final boolean[] moving={false};TextView move=button("Move",()->{}),hide=button("Hide",()->{});move.setTag("semantic:home.row.move");hide.setTag("semantic:home.row.hide");controls.addView(move,new LinearLayout.LayoutParams(dp(92),dp(40)));controls.addView(hide,new LinearLayout.LayoutParams(dp(92),dp(40)));
        int[] cardAt=new int[2],pageAt=new int[2];opener.getLocationOnScreen(cardAt);getLocationOnScreen(pageAt);FrameLayout.LayoutParams size=new FrameLayout.LayoutParams(dp(108),dp(96));size.leftMargin=dp(8);size.topMargin=Math.max(list.getPaddingTop(),Math.min(getHeight()-dp(108),cardAt[1]-pageAt[1]));addView(controls,size);rowControlOverlay=controls;
        Runnable close=()->{removeView(controls);rowControlOverlay=null;if(opener.isAttachedToWindow())opener.requestFocus();else{holdFocus();restoreFocus();}};
        move.setOnClickListener(v->{moving[0]=!moving[0];move.setText(moving[0]?"↕ Move":"Move");});hide.setOnClickListener(v->{PreviewHomeRows.hideRow(getContext(),id);removeView(controls);rowControlOverlay=null;render("row_hidden");holdFocus();restoreFocus();});
        for(TextView control:new TextView[]{move,hide})control.setOnKeyListener((v,key,event)->{
            if(key!=KeyEvent.KEYCODE_BACK&&key!=KeyEvent.KEYCODE_DPAD_LEFT&&key!=KeyEvent.KEYCODE_DPAD_RIGHT&&key!=KeyEvent.KEYCODE_DPAD_UP&&key!=KeyEvent.KEYCODE_DPAD_DOWN)return false;
            if(event.getAction()!=KeyEvent.ACTION_DOWN)return true;
            if(key==KeyEvent.KEYCODE_BACK||key==KeyEvent.KEYCODE_DPAD_RIGHT){rowControlConsumedKey=key;close.run();return true;}
            if(key==KeyEvent.KEYCODE_DPAD_LEFT)return true;
            if(moving[0]){PreviewHomeRows.moveRow(getContext(),id,key==KeyEvent.KEYCODE_DPAD_UP?-1:1);render("row_moved");move.requestFocus();}
            else (key==KeyEvent.KEYCODE_DPAD_UP?move:hide).requestFocus();return true;
        });move.requestFocus();
    }
    /** A carousel change must not rebind the library rows below it. */
    private void renderFeatured() {
        if(tab!=0 || cells.isEmpty() || cells.get(0).type!=HERO){render("featured_structure_changed");return;}
        rememberFocus();boolean preserve=list.hasFocus()&&!suspended;if(preserve)holdFocus();
        cells.set(0,new Cell(HERO,"Featured",featured()));
        updateArtwork();
        cells.get(0).signature=signature(cells.get(0));adapter.notifyItemChanged(0);
        if(preserve)restoreFocus();
    }
    private void render() {
        render("control_change");
    }
    private void render(String reason) {
        if(tab==1||tab==2)com.archos.mediacenter.video.diagnostics.Diagnostics.libraryState(tab==1?"movies":"tv",listMode[tab]?"list":"grid",(columns[tab].sortColumn==null?"preset_"+sorts[tab]:columns[tab].sortColumn.name())+":"+((columns[tab].sortColumn==null?ascending[tab]:columns[tab].ascending)?"ascending":"descending"),genres[tab],selectedYears[tab],providers[tab],unmatched[tab]);
        else com.archos.mediacenter.video.diagnostics.Diagnostics.uiState(tab==0?"home":"network","none","grid","none","none",0);
        long started=android.os.SystemClock.elapsedRealtime();
        List<Cell> previous=new ArrayList<>(cells);
        persistViews();
        rememberFocus();boolean preserve=list.hasFocus()&&!suspended;
        if(preserve)holdFocus();
        Object state=layout.onSaveInstanceState(); cells.clear();
        if(tab==0) {
            if(featured()!=null)cells.add(new Cell(HERO,"Featured",featured()));
            List<Entry> continuing=new ArrayList<>(snapshot.continuingMovies);continuing.addAll(snapshot.continuingShows);continuing.sort(Comparator.comparingLong((Entry e)->e.playedAt).reversed());
            PreviewHomeRows home=new PreviewHomeRows(getContext());Set<String> continuingKeys=new HashSet<>();for(Entry e:continuing)continuingKeys.add(e.key());continuing.removeIf(home::dismissed);home.supersedeWatchNext(continuingKeys);
            for(PreviewHomeRows.Row row:home.rows){if(!row.visible)continue;List<Entry> entries=new ArrayList<>();String name=row.name;
                switch(row.id){case "continue":entries=new ArrayList<>(continuing.subList(0,Math.min(30,continuing.size())));break;case "recent":for(Entry e:snapshot.recent)if(!continuingKeys.contains(e.key())&&entries.size()<50)entries.add(e);break;case "trending":entries=discovery.matches(snapshot,true);break;case "popular":entries=discovery.matches(snapshot,false);break;case "watched":entries=snapshot.watched;break;case "similar":Entry seed=snapshot.played.isEmpty()?null:snapshot.played.get(0);if(seed!=null){entries=PreviewDiscovery.similar(seed,snapshot);name="Because You Watched "+displayName(seed);}break;default:entries=home.members(row,snapshot);}
                int before=cells.size();rail(name,entries);if(cells.size()>before)cells.get(cells.size()-1).homeRowId=row.id;
            }
            cells.add(new Cell(CUSTOMISE,"Customise Home",null));
            if(loaded&&snapshot.movies.isEmpty()&&snapshot.shows.isEmpty()&&snapshot.recent.isEmpty()) header("Your library is empty — add media through Network & files",false);
        } else if(tab==1||tab==2) {
            cells.add(new Cell(HERO,tab==1?"Movies":"TV Shows",featured()));

            header(tab==1?"Movie library":"TV show library",true);
            List<Entry> entries=filtered(); for(Entry e:entries)cells.add(new Cell(POSTER,"",e));
            if(loaded&&entries.isEmpty())header(providers[tab].isEmpty()?"No matching titles":"No known matches in cached provider availability",false);
        } else {
            cells.add(new Cell(NETWORK,"Network & Files",null));


        }
        if(!loaded&&tab!=3)header(tab==0?"Home":tab==1?"Movies":"TV Shows",false);
        updateArtwork();for(Cell cell:cells){cell.signature=signature(cell);cell.viewType=cell.type==POSTER&&tab<3&&listMode[tab]?LIST:cell.type;}
        int[] changes=new int[4];androidx.recyclerview.widget.AdapterListUpdateCallback updates=new androidx.recyclerview.widget.AdapterListUpdateCallback(adapter);
        DiffUtil.calculateDiff(new DiffUtil.Callback(){public int getOldListSize(){return previous.size();}public int getNewListSize(){return cells.size();}public boolean areItemsTheSame(int a,int b){return previous.get(a).viewType==cells.get(b).viewType&&cellKey(previous.get(a)).equals(cellKey(cells.get(b)));}public boolean areContentsTheSame(int a,int b){return previous.get(a).signature.equals(cells.get(b).signature);}}).dispatchUpdatesTo(new androidx.recyclerview.widget.ListUpdateCallback(){
            public void onInserted(int position,int count){changes[0]+=count;updates.onInserted(position,count);}
            public void onRemoved(int position,int count){changes[1]+=count;updates.onRemoved(position,count);}
            public void onMoved(int from,int to){changes[2]++;updates.onMoved(from,to);}
            public void onChanged(int position,int count,Object payload){changes[3]+=count;updates.onChanged(position,count,payload);}
        });
        com.archos.mediacenter.video.diagnostics.Diagnostics.event("home_page_render","reason",reason,"tab",tab,"previous_cells",previous.size(),"cells",cells.size(),"inserted",changes[0],"removed",changes[1],"moved",changes[2],"rebound",changes[3],"adapter_recreated",false,"elapsed_ms",android.os.SystemClock.elapsedRealtime()-started);
        com.archos.mediacenter.video.diagnostics.Diagnostics.uiRebuild(list,tab==0?"home.rows":tab==1?"movies.library":tab==2?"tv.library":"network.page",reason,previous.size(),cells.size(),false);
        if(state!=null)layout.onRestoreInstanceState((android.os.Parcelable)state);if(preserve)restoreFocus();if(returnPending&&!suspended)post(this::restoreReturn);list.post(this::notifyScroll);scheduleVisibleEnrichment();
    }
    private static String entrySignature(Entry e){return e.key()+"|"+displayName(e)+"|"+e.backdrop+"|"+e.media.getPosterUri()+"|"+e.secondary+"|"+e.active+"|"+e.bytes+"|"+e.runtime+"|"+e.year()+"|"+e.resolution+"|"+e.audio+"|"+e.codec+"|"+e.hdr+"|"+(e.media instanceof Video?((Video)e.media).getResumeMs():0);}
    private String signature(Cell c){String base=tab+":"+c.title+":"+c.type+":"+(tab>0&&tab<3?listMode[tab]+":"+sorts[tab]+":"+ascending[tab]+":"+genres[tab]+":"+selectedYears[tab]+":"+unmatched[tab]+":"+providers[tab]+":"+columns[tab].sortColumn+":"+columns[tab].ascending+":"+columns[tab].visible():"");if(c.value instanceof Entry)base+=entrySignature((Entry)c.value);else if(c.type==RAIL){StringBuilder rail=new StringBuilder(base);for(Entry e:(List<Entry>)c.value)rail.append(entrySignature(e));base=rail.toString();}else base+=String.valueOf(c.value);if(c.type==HERO&&tab>0)base+=PreviewLibrarySummary.describe(getContext(),snapshot,tab==2);if(c.type==NETWORK_PANEL||c.type==NETWORK){StringBuilder sources=new StringBuilder(base);for(com.archos.mediacenter.video.leanback.adapter.object.Shortcut source:librarySources)sources.append(source.getName()).append(source.getUri());for(com.archos.mediacenter.video.leanback.adapter.object.Shortcut source:savedLocations)sources.append(source.getName()).append(source.getUri());base=sources.append(files).toString();}return base;}
    private List<Entry> source(){
        if(!unmatched[tab])return tab==1?snapshot.movies:snapshot.shows;
        List<Entry> result=new ArrayList<>();
        Map<android.net.Uri,PreviewMediaClassification.Kind> hints=PreviewMediaClassification.hints(preferences);
        if(snapshot.unmatched!=null)for(Entry entry:snapshot.unmatched){Video video=(Video)entry.media;PreviewMediaClassification.Kind kind=PreviewMediaClassification.classify(video.getFilenameNonCryptic(),video.getFileUri(),hints);if(kind==PreviewMediaClassification.Kind.UNKNOWN||kind==(tab==1?PreviewMediaClassification.Kind.MOVIE:PreviewMediaClassification.Kind.TV))result.add(entry);}
        return result;
    }
    private void networkSection(String section){getContext().startActivity(new android.content.Intent(getContext(),com.archos.mediacenter.video.leanback.network.NetworkRootActivity.class).putExtra("preview_source_section",section));}
    private void chart(String name,boolean trend){
        List<Entry> matches=discovery.matches(snapshot,trend);
        if(!matches.isEmpty())rail(name,matches);
        else {cells.add(new Cell(NOTICE,name,discovery.available?"No matching titles in the current Trakt chart":"Unavailable — Trakt charts will appear here when connected"));}
    }
    public static List<Entry> sortEntries(List<Entry> input,int sort,boolean ascending,PreviewDiscovery discovery){
        List<Entry> entries=new ArrayList<>(input);
        Comparator<Entry> cmp;
        if(sort>=3){Map<String,Integer> ranks=sort==3?discovery.trending:discovery.popular;
            cmp=(a,b)->{Integer x=ranks.get(PreviewDiscovery.key(a)),y=ranks.get(PreviewDiscovery.key(b));
                if(x==null||y==null)return x==null?(y==null?0:1):-1;
                return ascending?Integer.compare(y,x):Integer.compare(x,y);};
        }else{
            cmp=sort==1?Comparator.comparing(e->displayName(e),String.CASE_INSENSITIVE_ORDER):sort==2?Comparator.comparing(e->e.releaseDate==null||e.releaseDate.isEmpty()?String.valueOf(e.year()):e.releaseDate):Comparator.comparingLong(e->e.added);
            if(!ascending)cmp=cmp.reversed();
        }
        entries.sort(cmp.thenComparing(e->displayName(e),String.CASE_INSENSITIVE_ORDER));return entries;
    }
    public static String titleForSort(Context context,Entry entry){
        return com.archos.mediacenter.video.utils.SortUtils.isIgnoreArticlesEnabled(context)&&entry.sortTitle!=null&&!entry.sortTitle.isEmpty()?entry.sortTitle:displayName(entry);
    }
    public static boolean hiddenByWatchedPreference(Context context,Entry entry){
        if(!androidx.preference.PreferenceManager.getDefaultSharedPreferences(context).getBoolean("hide_watched",false))return false;
        return entry.media instanceof Video?PreviewSeriesJourney.completed((Video)entry.media):entry.media instanceof Tvshow&&((Tvshow)entry.media).getEpisodeCount()>0&&((Tvshow)entry.media).isWatched();
    }
    private List<Entry> filtered(){
        List<Entry> entries=new ArrayList<>();for(Entry e:source())if(!hiddenByWatchedPreference(getContext(),e)&&PreviewGenres.matches(e.genres,PreviewGenres.parse(genres[tab]))&&(selectedYears[tab].isEmpty()||PreviewGenres.parse(selectedYears[tab]).contains(String.valueOf(e.year())))&&(providers[tab].isEmpty()||PreviewGenres.parse(providers[tab]).stream().anyMatch(provider->com.archos.mediacenter.video.streaming.StreamingRepository.selected(getContext()).contains(provider)&&com.archos.mediacenter.video.streaming.StreamingRepository.knownOn(getContext(),tab==1?"movie":"tv",e.onlineId,provider))))entries.add(e);
        entries=sortEntries(entries,sorts[tab],ascending[tab],discovery);
        if(sorts[tab]==1){Comparator<Entry> titles=Comparator.comparing(e->titleForSort(getContext(),e),String.CASE_INSENSITIVE_ORDER);if(!ascending[tab])titles=titles.reversed();entries.sort(titles);}
        if(columns[tab]!=null)entries=columns[tab].sort(entries);if(!quietOrder.isEmpty())entries.sort(Comparator.comparingInt(e->quietOrder.getOrDefault(e.key(),Integer.MAX_VALUE)));return entries;
    }
    private String[] sortLabels(){return new String[]{"Date Added","Title",tab==2?"Air Date":"Release Date","Trakt Trending","Trakt Popular"};}
    private void sort(){
        List<String> labels=new ArrayList<>(Arrays.asList(sortLabels()));
        if(!discovery.available){labels.set(3,labels.get(3)+" — unavailable");labels.set(4,labels.get(4)+" — unavailable");}
        List<PreviewLibraryColumns.Column> metadata=columns[tab].available();
        metadata.remove(PreviewLibraryColumns.Column.TITLE);metadata.remove(PreviewLibraryColumns.Column.ADDED);
        for(PreviewLibraryColumns.Column column:metadata)labels.add(column.label);
        PreviewLibraryColumns.Column active=columns[tab].sortColumn;
        int selected=active==null?sorts[tab]:active==PreviewLibraryColumns.Column.TITLE?1:active==PreviewLibraryColumns.Column.ADDED?0:5+metadata.indexOf(active);
        PreviewDialog.choose(getContext(),"Sort",labels.toArray(new String[0]),selected,n->{
            if((n==3||n==4)&&!discovery.available)return;
            quietOrder.clear();
            if(n<5){columns[tab].clearSort();sorts[tab]=n;ascending[tab]=n==1;}
            else{PreviewLibraryColumns.Column column=metadata.get(n-5);columns[tab].setSort(column,column==PreviewLibraryColumns.Column.YEAR);ascending[tab]=columns[tab].ascending;}
            render();
        });
    }
    static String filterSummary(String genre,String year,String provider){
        List<String> values=new ArrayList<>();values.addAll(PreviewGenres.parse(genre));values.addAll(PreviewGenres.parse(year));values.addAll(PreviewGenres.parse(provider));
        return values.isEmpty()?"Filters":values.size()==1?"Filters: "+(PreviewGenres.parse(provider).isEmpty()?shortFilterValue(values.get(0)):"1 service"):"Filters ("+values.size()+")";
    }
    private static String shortFilterValue(String value){return value.length()>18?value.substring(0,17)+"…":value;}
    private boolean hasUnmatched(){
        Map<android.net.Uri,PreviewMediaClassification.Kind> hints=PreviewMediaClassification.hints(preferences);
        for(Entry entry:snapshot.unmatched){Video video=(Video)entry.media;PreviewMediaClassification.Kind kind=PreviewMediaClassification.classify(video.getFilenameNonCryptic(),video.getFileUri(),hints);if(kind==PreviewMediaClassification.Kind.UNKNOWN||kind==(tab==1?PreviewMediaClassification.Kind.MOVIE:PreviewMediaClassification.Kind.TV))return true;}return false;
    }
    private void filter(){PreviewDialog.choose(getContext(),"Filters",new String[]{"Genre"+(genres[tab].isEmpty()?"":": "+genres[tab]),"Year"+(years[tab]==0?"":": "+years[tab]),"Streaming Service"+(providers[tab].isEmpty()?"":": selected"),"Clear Filters"},-1,n->{
        if(n==3){genres[tab]="";years[tab]=0;selectedYears[tab]="";providers[tab]="";render();return;}
        if(n==2){java.util.List<com.archos.mediacenter.video.streaming.StreamingRepository.Provider> selected=com.archos.mediacenter.video.streaming.StreamingRepository.selectedCatalogue(getContext());List<String> names=new ArrayList<>(),ids=new ArrayList<>();for(com.archos.mediacenter.video.streaming.StreamingRepository.Provider provider:selected){names.add(provider.name);ids.add(String.valueOf(provider.id));}android.app.Dialog menu=chooseMany("Streaming Services",names,ids,providers[tab],value->{providers[tab]=value;quietOrder.clear();render();});for(int i=0;i<selected.size();i++)com.archos.mediacenter.video.streaming.PreviewProviderIcons.bind(menu,i,selected.get(i).logo,"library.filters");return;}
        if(n==0){Set<String> values=new TreeSet<>();for(Entry e:source())values.addAll(PreviewGenres.parse(e.genres));PreviewGenres.chooseLive(getContext(),values,PreviewGenres.parse(genres[tab]),selected->{genres[tab]=android.text.TextUtils.join("|",selected);quietOrder.clear();render();});return;}
        TreeSet<String> values=new TreeSet<>(Collections.reverseOrder());for(Entry e:source())if(e.year()>0)values.add(String.valueOf(e.year()));
        List<String> options=new ArrayList<>(values);chooseMany("Year",options,options,selectedYears[tab],value->{selectedYears[tab]=value;years[tab]=0;quietOrder.clear();render();});
    });}
    private android.app.Dialog chooseMany(String title,List<String> names,List<String> values,String saved,java.util.function.Consumer<String> accept){
        Set<String> selected=PreviewGenres.parse(saved);List<String> labels=new ArrayList<>(names);labels.add("Clear Selection");Set<Integer> checks=new HashSet<>();for(int i=0;i<values.size();i++)if(selected.contains(values.get(i)))checks.add(i);
        android.app.Dialog[] dialog={null};dialog[0]=PreviewDialog.choose(getContext(),title,labels.toArray(new String[0]),-1,checks,false,n->{if(n==values.size())selected.clear();else if(!selected.add(values.get(n)))selected.remove(values.get(n));checks.clear();for(int i=0;i<values.size();i++)if(selected.contains(values.get(i)))checks.add(i);PreviewDialog.updateChecks(dialog[0],checks);accept.accept(android.text.TextUtils.join("|",selected));});
        return dialog[0];
    }
    private TextView text(String value,int size){TextView t=new TextView(getContext());t.setText(value);t.setTextColor(Color.WHITE);t.setTextSize(size);return t;}
    private GradientDrawable background(boolean focus){GradientDrawable d=new GradientDrawable();d.setColor(focus?0x60416b84:0xc00b1b29);d.setCornerRadius(dp(5));d.setStroke(dp(focus?2:1),focus?PreviewAccent.color(getContext()):0xff304b60);return d;}
    private TextView button(String name,Runnable action){TextView b=text(name,13);com.archos.mediacenter.video.leanback.PreviewIcon.apply(b,name,16);b.setGravity(Gravity.CENTER);b.setPadding(dp(14),dp(9),dp(14),dp(9));b.setFocusable(true);b.setFocusableInTouchMode(true);b.setClickable(true);b.setBackground(PreviewDialog.focus(getContext()));b.setOnFocusChangeListener((v,f)->{b.setTextColor(Color.WHITE);b.setShadowLayer(0,0,0,0);});b.setOnClickListener(v->{quietOrder.clear();action.run();});return b;}
    private int dp(int n){return Math.round(n*getResources().getDisplayMetrics().density);}
    class Holder extends RecyclerView.ViewHolder {
        PreviewCardPresenter presenter; Presenter.ViewHolder card;
        Holder(View v){super(v);}
    }
    class PageAdapter extends RecyclerView.Adapter<Holder> {
        private void refreshColumns(){quietOrder.clear();render("columns_change");}
        @Override public long getItemId(int p){Cell c=cells.get(p);String key=c.type==POSTER?((Entry)c.value).key():c.type==STORAGE?((Box)c.value).getBoxId()+":"+((Box)c.value).getPath():c.title;return ((long)c.type<<32) | (key.hashCode() & 0xffffffffL);}
        @Override public int getItemCount(){return cells.size();}
        @Override public int getItemViewType(int p){return cells.get(p).type==POSTER&&tab<3&&listMode[tab]?LIST:cells.get(p).type;}
        @Override public Holder onCreateViewHolder(ViewGroup parent,int type){
            if(type==LIST){LinearLayout row=columns[tab].newRow();RecyclerView.LayoutParams lp=new RecyclerView.LayoutParams(-1,dp(44));lp.bottomMargin=dp(1);row.setLayoutParams(lp);return new Holder(row);}
            if(type==POSTER){PreviewCardPresenter pr=new PreviewCardPresenter(type==LIST?PreviewCardPresenter.Style.LIST:PreviewCardPresenter.Style.POSTER); Presenter.ViewHolder card=pr.onCreateViewHolder(parent);Holder h=new Holder(card.view);h.presenter=pr;h.card=card;RecyclerView.LayoutParams lp=new RecyclerView.LayoutParams(-1,-2);lp.setMargins(0,0,dp(10),dp(15));h.itemView.setLayoutParams(lp);return h;}
            LinearLayout v=new LinearLayout(getContext());v.setGravity(Gravity.CENTER_VERTICAL);v.setPadding(0,dp(6),0,dp(6));v.setLayoutParams(new RecyclerView.LayoutParams(-1,-2));return new Holder(v);
        }
        @Override public void onBindViewHolder(Holder h,int p){Cell c=cells.get(p);
            if(c.type==POSTER&&h.itemView instanceof LinearLayout){Entry e=(Entry)c.value;columns[tab].bind((LinearLayout)h.itemView,e);h.itemView.setTag(e.key());h.itemView.setOnClickListener(v->open(e,v));h.itemView.setOnLongClickListener(v->{contextMenu(e,v,false);return true;});return;}
            if(c.type==POSTER){Entry e=(Entry)c.value;h.presenter.bindEntry(h.card,e);h.itemView.setTag(e.key());h.itemView.setOnClickListener(v->open(e,v));h.itemView.setOnLongClickListener(v->{contextMenu(e,v,false);return true;});return;}
            LinearLayout v=(LinearLayout)h.itemView;
            if(c.type==RAIL&&v.getChildCount()==1&&v.getChildAt(0) instanceof RecyclerView){RecyclerView rail=(RecyclerView)v.getChildAt(0);if(rail.getAdapter() instanceof RailAdapter){((RailAdapter)rail.getAdapter()).update((List<Entry>)c.value);return;}}
            v.removeAllViews();v.setFocusable(false);v.setOnClickListener(null);v.setBackground(null);v.setOrientation(LinearLayout.HORIZONTAL);v.setPadding(0,dp(6),0,dp(6));v.setLayoutParams(new RecyclerView.LayoutParams(-1,-2));
            if(c.type==HERO){Entry e=(Entry)c.value;v.setOrientation(LinearLayout.VERTICAL);v.setGravity(Gravity.TOP);v.setPadding(0,dp(10),0,dp(8));
                v.setMinimumHeight(dp(tab==0?244:76));
                v.setLayoutParams(new RecyclerView.LayoutParams(-1,tab==0?dp(244):android.view.ViewGroup.LayoutParams.WRAP_CONTENT));
                if(tab==0){
                    int heroHeight=Math.max(dp(280),getHeight()-list.getPaddingTop()-list.getPaddingBottom()-dp(170));
                    v.setMinimumHeight(heroHeight);v.setLayoutParams(new RecyclerView.LayoutParams(-1,heroHeight));v.setPadding(0,dp(12),0,dp(18));
                    PreviewFeaturedCard featured=new PreviewFeaturedCard(getContext(),featuredCandidates(),featuredIndex,()->play(e,v),()->open(e,v));v.addView(featured,new LinearLayout.LayoutParams(-1,-1));
                    v.animate().cancel();v.setAlpha(1f);v.setTranslationX(0);if(featuredDirection!=0){v.setTranslationX(dp(36)*featuredDirection);v.setAlpha(.5f);v.animate().translationX(0).alpha(1f).setDuration(250).start();featuredDirection=0;}
                }else{TextView title=text(c.title,30);title.setTypeface(null,android.graphics.Typeface.BOLD);v.addView(title);String[] lines=PreviewLibrarySummary.describe(getContext(),snapshot,tab==2).split("\n");String[] icons={tab==2?"TV Shows":"Movies","Total Size","Local Storage","Network"};for(int line=0;line<lines.length;line++){TextView summary=text(lines[line],13);summary.setTextColor(0xffe1e9ef);summary.setPadding(0,dp(2),0,dp(2));if(line<icons.length)PreviewIcon.apply(summary,icons[line],17);v.addView(summary);}}
            }
            else if(c.type==CUSTOMISE){v.setGravity(Gravity.CENTER);TextView custom=button("Customise Home",()->PreviewHomeRows.customise(getContext(),()->{render();}));custom.setTag("home:customise");v.addView(custom);}
            else if(c.type==NETWORK){networkLayout(v);}
            else if(c.type==NETWORK_PANEL){bindNetworkPanel(v,c);}
            else if(c.type==SCAN){v.setOrientation(LinearLayout.VERTICAL);TextView scan=button("Scan Library",()->PreviewLibraryScan.request(getContext()));scan.setTag("scan:library");v.addView(scan,new LinearLayout.LayoutParams(dp(158),dp(40)));TextView description=text("Scan local storage and indexed network folders. Full Library Scan in Settings also retries unmatched descriptions.",12);description.setPadding(0,dp(8),0,dp(8));description.setTextColor(0xffaac1d1);v.addView(description);TextView progress=text("",13);progress.setTextColor(PreviewAccent.color(getContext()));progress.setMinHeight(dp(36));v.addView(progress,new LinearLayout.LayoutParams(-1,-2));progress.post(new Runnable(){public void run(){if(!progress.isAttachedToWindow())return;String status=PreviewLibraryScan.libraryStatus(getContext());progress.setText(status);progress.setVisibility(VISIBLE);progress.postDelayed(this,1000);}});}
            else if(c.type==NOTICE){v.setOrientation(LinearLayout.VERTICAL);TextView title=text(c.title,18);title.setTextColor(0xff8298aa);v.addView(title);TextView status=text((String)c.value,12);status.setTextColor(0xff8298aa);v.addView(status);v.setPadding(0,dp(12),0,dp(12));}
            else if(c.type==HEADER){
                if(Boolean.TRUE.equals(c.value)){
                    v.setOrientation(LinearLayout.VERTICAL);v.setPadding(0,dp(13),0,dp(18));LinearLayout controls=new PreviewToolbar(getContext());controls.setGravity(Gravity.BOTTOM);
                    List<TextView> toolbar=new ArrayList<>();List<String> controlIds=new ArrayList<>();
                    toolbar.add(button(listMode[tab]?"Grid view":"List view",()->{listMode[tab]=!listMode[tab];render();}));controlIds.add("view");
                    toolbar.add(button(filterSummary(genres[tab],selectedYears[tab],providers[tab])+"  ▾",()->filter()));controlIds.add("filters");
                    toolbar.add(button("Sort: "+(columns[tab].sortColumn!=null?columns[tab].sortColumn.label:sortLabels()[sorts[tab]])+"  ▾",()->sort()));controlIds.add("sort");
                    String order=columns[tab].sortColumn!=null?(columns[tab].ascending?"Ascending":"Descending"):sorts[tab]==1?(ascending[tab]?"A → Z":"Z → A"):sorts[tab]>=3?(ascending[tab]?"Lowest ranked first":"Highest ranked first"):(ascending[tab]?"Oldest first":"Newest first");
                    toolbar.add(button("Order: "+order+"  ▾",()->PreviewDialog.choose(getContext(),"Order",new String[]{"Ascending","Descending"},(columns[tab].sortColumn==null?ascending[tab]:columns[tab].ascending)?0:1,n->{quietOrder.clear();ascending[tab]=n==0;columns[tab].setAscending(n==0);render();})));controlIds.add("order");
                    if(listMode[tab]){toolbar.add(button("Columns",()->columns[tab].choose(this::refreshColumns)));controlIds.add("columns");}
                    if(hasUnmatched()){toolbar.add(button(unmatched[tab]?"Matched":"Unmatched",()->{unmatched[tab]=!unmatched[tab];quietOrder.clear();render();}));controlIds.add("unmatched");}
                    for(int i=0;i<toolbar.size();i++){TextView control=toolbar.get(i);LinearLayout.LayoutParams lp=new LinearLayout.LayoutParams(-2,-2);if(i>0)lp.leftMargin=dp(8);controls.addView(control,lp);control.setPadding(dp(10),dp(18),dp(10),0);control.setIncludeFontPadding(false);control.setTag("control:"+i);com.archos.mediacenter.video.diagnostics.Diagnostics.semantic(control,"library.toolbar."+controlIds.get(i));}v.addView(controls);
                    if(listMode[tab])v.addView(columns[tab].header(this::refreshColumns));
                }else{TextView title=text(c.title,tab==3&&c.title.equals("Network & files")?30:19);title.setTextColor(0xff9ed4f7);v.addView(title);}
            }
            else if(c.type==RAIL){v.setPadding(0,0,0,dp(10));RecyclerView rail=new FocusRecycler(getContext(),true);rail.setLayoutManager(new LinearLayoutManager(getContext(),RecyclerView.HORIZONTAL,false));rail.setClipChildren(false);rail.setClipToPadding(false);rail.setPadding(dp(12),dp(10),dp(12),dp(10));rail.setItemAnimator(null);rail.setAdapter(new RailAdapter((List<Entry>)c.value,"Continue Watching".equals(c.title)));com.archos.mediacenter.video.diagnostics.Diagnostics.uiRebuild(rail,"home.rail","row_bound",0,rail.getAdapter().getItemCount(),true);rail.addOnScrollListener(new RecyclerView.OnScrollListener(){@Override public void onScrolled(RecyclerView row,int dx,int dy){scheduleVisibleEnrichment();}});LinearLayout.LayoutParams strip=new LinearLayout.LayoutParams(-1,dp(125));strip.rightMargin=-list.getPaddingRight();v.addView(rail,strip);android.os.Parcelable railState=railScrollStates.get(cellKey(c));if(railState!=null)rail.getLayoutManager().onRestoreInstanceState(railState);}
            else if(c.type==STORAGE){Box b=(Box)c.value;v.setPadding(dp(12),dp(12),dp(12),dp(12));RecyclerView.LayoutParams lp=new RecyclerView.LayoutParams(-1,dp(76));lp.setMargins(0,0,dp(12),dp(12));v.setLayoutParams(lp);v.setBackground(PreviewDialog.surface(getContext(),false));v.setForeground(PreviewDialog.focus(getContext()));v.setDescendantFocusability(FOCUS_BLOCK_DESCENDANTS);v.setFocusable(true);v.setClickable(true);
                ImageView icon=new ImageView(getContext());icon.setImageDrawable(new StorageIcon(b.getBoxId()));v.addView(icon,new LinearLayout.LayoutParams(dp(35),dp(35)));
                LinearLayout labels=new LinearLayout(getContext());labels.setOrientation(LinearLayout.VERTICAL);labels.setPadding(dp(12),0,0,0);
                String name=b.getName(),sub="";if(b.getBoxId()==Box.ID.NETWORK){name="Browse network";sub="Find shared folders";}else if(b.getBoxId()==Box.ID.FOLDERS){name="Internal storage";}else if(b.getBoxId()==Box.ID.VIDEOS_BY_LISTS){name="Playlists";sub="Browse saved playlists";}else{int at=name.indexOf('(');if(at>0){sub=name.substring(at+1).replace(")","").trim();name=name.substring(0,at).trim();}name=name.replaceFirst("^[^:]+:\\s*","");}
                TextView title=text(name,13);title.setMaxLines(1);title.setEllipsize(android.text.TextUtils.TruncateAt.END);labels.addView(title);if(!sub.isEmpty()){TextView detail=text(sub,10);detail.setTextColor(0xffb4cbe0);detail.setMaxLines(2);labels.addView(detail);}v.addView(labels,new LinearLayout.LayoutParams(0,-2,1));v.setContentDescription(name+" "+sub);v.setOnClickListener(view->click.open(new Presenter.ViewHolder(view),b));
            }
        }
        private void bindNetworkPanel(LinearLayout v,Cell c){
                v.setOrientation(LinearLayout.VERTICAL);v.setGravity(Gravity.TOP);v.setPadding(dp(14),dp(12),dp(14),dp(12));v.setBackground(PreviewDialog.surface(getContext(),false));
                RecyclerView.LayoutParams size=new RecyclerView.LayoutParams(-1,dp(c.title.equals("Local Storage")||c.title.equals("Discover Devices")||c.title.equals("Saved Locations")?145:210));size.setMargins(0,0,dp(10),dp(10));v.setLayoutParams(size);
                TextView heading=text(c.title,16);PreviewIcon.apply(heading,c.title,24);heading.setPadding(0,0,0,dp(8));v.addView(heading);
                if(c.title.equals("Scan Library")){
                    v.addView(text("Check local storage and library sources for new or changed media.",12));
                    v.addView(button("Scan Library",()->PreviewLibraryScan.request(getContext())));
                    TextView status=text(PreviewNetworkScanning.lastResult(getContext()),12);v.addView(status,new LinearLayout.LayoutParams(-1,dp(70)));
                    status.post(new Runnable(){public void run(){if(!status.isAttachedToWindow())return;status.setText(com.archos.mediaprovider.video.NetworkScannerReceiver.isScannerWorking()?"Scanning network sources · "+com.archos.mediaprovider.video.NetworkScannerServiceVideo.getFilesFoundCount()+" files found":PreviewNetworkScanning.lastResult(getContext()));status.postDelayed(this,1000);}});
                }else if(c.title.equals("Network Scanning")){
                    v.addView(text("Automatic scanning for your network sources.",12));
                    int period=com.archos.mediaprovider.video.NetworkAutoRefresh.getRescanPeriod(getContext());TextView schedule=text("Automatic: "+(period>0?"On":"Off")+"\nFrequency: "+(period>0?period/60000+" minutes":"Not scheduled")+"\nSources: "+librarySources.size()+"\n"+PreviewNetworkScanning.lastResult(getContext()),12);schedule.setLineSpacing(dp(4),1);schedule.setPadding(0,dp(12),0,dp(8));v.addView(schedule);v.addView(button("Configure Network Scanning",()->PreviewNetworkScanning.show(getContext())));
                }else if(c.title.equals("Local Storage")){
                    for(Box box:files)if(box.getBoxId()==Box.ID.FOLDERS||box.getBoxId()==Box.ID.USB||box.getBoxId()==Box.ID.SDCARD||box.getBoxId()==Box.ID.OTHER)v.addView(button(box.getBoxId()==Box.ID.FOLDERS?"Internal Storage":box.getName(),()->click.open(new Presenter.ViewHolder(v),box)));
                }else if(c.title.equals("Discover Devices")){
                    v.addView(button("Network Computers & NAS",()->networkSection("smb")));v.addView(button("Media Servers · DLNA/UPnP",()->networkSection("upnp")));
                }else{
                    v.addView(text(c.title.equals("Library Sources")?"Indexed network sources appearing in Movies and TV Shows.":"Saved browsing locations.",12));
                    List<com.archos.mediacenter.video.leanback.adapter.object.Shortcut> sourceList=c.title.equals("Library Sources")?librarySources:savedLocations;
                    for(int i=0;i<Math.min(2,sourceList.size());i++){com.archos.mediacenter.video.leanback.adapter.object.Shortcut source=sourceList.get(i);TextView item=button(source.getName()+" · "+source.getUri().getScheme(),()->openSource(source));item.setSingleLine(true);item.setEllipsize(android.text.TextUtils.TruncateAt.END);v.addView(item,new LinearLayout.LayoutParams(-1,dp(34)));}
                    v.addView(button("Open "+c.title,()->networkSection(c.title.equals("Library Sources")?"library":"saved")));
                    if(c.title.equals("Library Sources"))v.addView(button("Add Network Source",()->networkSection("add")));
                }

        }
        private void networkLayout(LinearLayout container){
            container.setOrientation(LinearLayout.HORIZONTAL);container.setGravity(Gravity.TOP);container.setClipChildren(false);container.setLayoutParams(new RecyclerView.LayoutParams(-1,Math.max(dp(240),getHeight()-list.getPaddingTop()-list.getPaddingBottom())));
            container.addView(new PreviewNetworkWorkspace(getContext(),files,librarySources,savedLocations,box->click.open(new Presenter.ViewHolder(container),box),PreviewPages.this::networkSection),new LinearLayout.LayoutParams(-1,-1));
            if(networkEntryPending)container.post(PreviewPages.this::requestNetworkEntryFocus);
        }
        private void setNetworkBack(View view,Runnable back){if(view.isFocusable())view.setOnKeyListener((v,key,event)->{if(key==KeyEvent.KEYCODE_DPAD_LEFT&&event.getAction()==KeyEvent.ACTION_DOWN){back.run();return true;}return false;});if(view instanceof ViewGroup)for(int i=0;i<((ViewGroup)view).getChildCount();i++)setNetworkBack(((ViewGroup)view).getChildAt(i),back);}
        @Override public void onViewRecycled(Holder h){if(h.presenter!=null)h.presenter.onUnbindViewHolder(h.card);else if(h.itemView instanceof LinearLayout&&h.itemView.isFocusable()&&tab>0&&tab<3)columns[tab].clear(h.itemView);else if(h.itemView instanceof ViewGroup){ViewGroup group=(ViewGroup)h.itemView;for(int i=0;i<group.getChildCount();i++)if(group.getChildAt(i) instanceof RecyclerView)((RecyclerView)group.getChildAt(i)).setAdapter(null);}}
    }
    class RailAdapter extends RecyclerView.Adapter<Holder>{
        final List<Entry> entries;final PreviewCardPresenter pr=new PreviewCardPresenter(PreviewCardPresenter.Style.CONTINUE);
        final boolean resume;RailAdapter(List<Entry> e,boolean resume){entries=new ArrayList<>(e);this.resume=resume;setHasStableIds(true);}
        void update(List<Entry> next){List<Entry> old=new ArrayList<>(entries);entries.clear();entries.addAll(next);DiffUtil.calculateDiff(new DiffUtil.Callback(){public int getOldListSize(){return old.size();}public int getNewListSize(){return entries.size();}public boolean areItemsTheSame(int a,int b){return old.get(a).key().equals(entries.get(b).key());}public boolean areContentsTheSame(int a,int b){return entrySignature(old.get(a)).equals(entrySignature(entries.get(b)));}}).dispatchUpdatesTo(this);}
        public long getItemId(int p){return entries.get(p).key().hashCode();}public int getItemCount(){return entries.size();}
        public Holder onCreateViewHolder(ViewGroup p,int type){Presenter.ViewHolder card=pr.onCreateViewHolder(p);Holder h=new Holder(card.view);h.card=card;h.presenter=pr;RecyclerView.LayoutParams lp=new RecyclerView.LayoutParams(Math.max(dp(140),Math.round(((p.getWidth()>0?p.getWidth():getResources().getDisplayMetrics().widthPixels)-dp(24))/4.25f)-dp(12)),dp(105));lp.rightMargin=dp(12);h.itemView.setLayoutParams(lp);return h;}
        public void onBindViewHolder(Holder h,int p){Entry e=entries.get(p);pr.bindEntry(h.card,e);h.itemView.setTag(e.key());h.itemView.setOnClickListener(v->{if(resume)play(e,v);else open(e,v);});h.itemView.setOnLongClickListener(v->{contextMenu(e,v,resume);return true;});}
        public void onViewRecycled(Holder h){pr.onUnbindViewHolder(h.card);}
    }
    private void contextMenu(Entry e,View view,boolean continuing){List<String> labels=new ArrayList<>(Arrays.asList("More Info","Add to Row"));if(continuing)labels.add("Dismiss from Continue Watching");PreviewDialog.choose(getContext(),displayName(e),labels.toArray(new String[0]),-1,n->{if(n==0)open(e,view);else if(n==1)PreviewHomeRows.add(getContext(),e,this::render);else{new PreviewHomeRows(getContext()).dismiss(e);render();}});}
    static final class StorageIcon extends android.graphics.drawable.Drawable {
        final Box.ID id;final android.graphics.Paint paint=new android.graphics.Paint(3);
        StorageIcon(Box.ID id){this.id=id;paint.setColor(0xffb7d7f5);paint.setStyle(android.graphics.Paint.Style.STROKE);paint.setStrokeWidth(1.7f);paint.setStrokeCap(android.graphics.Paint.Cap.ROUND);}
        public void draw(android.graphics.Canvas c){c.save();c.translate(getBounds().left,getBounds().top);c.scale(getBounds().width()/32f,getBounds().height()/32f);
            if(id==Box.ID.VIDEOS_BY_LISTS){for(int y=7;y<29;y+=8){c.drawCircle(4,y,1,paint);c.drawLine(10,y,29,y,paint);}}
            else if(id==Box.ID.NETWORK){for(int y=3;y<25;y+=14){c.drawRoundRect(2,y,30,y+10,2,2,paint);c.drawCircle(7,y+5,1,paint);}}
            else {c.drawRoundRect(4,5,28,27,3,3,paint);c.drawLine(4,20,28,20,paint);c.drawCircle(23,24,1,paint);}
            c.restore();}
        public void setAlpha(int a){paint.setAlpha(a);}public void setColorFilter(android.graphics.ColorFilter f){paint.setColorFilter(f);}public int getOpacity(){return android.graphics.PixelFormat.TRANSLUCENT;}
    }
}
