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
import com.archos.mediascraper.*;
import java.util.*;
import java.util.function.*;

/** Presentation over the existing details fragment: playback, edits and deletion keep native handlers. */
public final class PreviewMoviePage extends ScrollView {
    private final LinearLayout body,cast,crew,details,related,trailers;
    private final LinearLayout heroPage,tabBar,information;private final FrameLayout lower;private final View tabLine;private final List<TextView> sectionTabs=new ArrayList<>();private String activeSection="";private int versionCount;private final List<PreviewEpisodeRow> episodeRows=new ArrayList<>();private final List<PreviewLandscapeCard> landscapeCards=new ArrayList<>(),extraCards=new ArrayList<>();
    private final TextView title,meta,plot,play,trailer,context;
    private final LinearLayout pills;
    private final LinearLayout providerActions;
    private TextView versions;
    private Runnable chooseVersions=()->{};
    private ObjectAdapter observedActions;
    private final com.archos.mediacenter.video.streaming.StreamingActionPresenter providerPresenter=new com.archos.mediacenter.video.streaming.StreamingActionPresenter();
    private final List<Presenter.ViewHolder> providerHolders=new ArrayList<>();
    private final ObjectAdapter.DataObserver actionObserver=new ObjectAdapter.DataObserver(){public void onChanged(){renderProviders();}public void onItemRangeChanged(int start,int count){renderProviders();}public void onItemRangeInserted(int start,int count){renderProviders();}public void onItemRangeRemoved(int start,int count){renderProviders();}};
    private final Supplier<ObjectAdapter> actions;
    private final Consumer<Action> action;
    private final Runnable nativeDetails;
    private final Consumer<Uri> artwork;
    private Video movie;
    private Tvshow show;
    private Runnable showPlay;
    private final java.util.SortedMap<Integer,java.util.List<Episode>> seasons=new java.util.TreeMap<>();
    private LinearLayout episodes;
    private BaseTags tags;
    private final Map<String,ImageView> portraits=new HashMap<>();
    private List<ScraperTrailer> trailerList=Collections.emptyList();
    private Snapshot snapshot;
    private PreviewDetailsData.Result enrichment;private java.util.concurrent.Future<?> enrichmentTask;private String enrichmentKey="";private int enrichmentGeneration;private org.json.JSONObject remoteDetails;private String remoteKind;private long remoteId;
    private final List<Presenter.ViewHolder> cards=new ArrayList<>();
    private final PreviewCardPresenter presenter=new PreviewCardPresenter(PreviewCardPresenter.Style.CONTINUE);
    public PreviewMoviePage(Context c,Supplier<ObjectAdapter> actions,Consumer<Action> action,Runnable nativeDetails,Consumer<Uri> artwork){
        super(c);this.actions=actions;this.action=action;this.nativeDetails=nativeDetails;this.artwork=artwork;
        setFillViewport(true);setSmoothScrollingEnabled(false);setClipToPadding(false);setClipChildren(false);body=new LinearLayout(c);body.setOrientation(LinearLayout.VERTICAL);body.setPadding(dp(42),0,dp(42),dp(24));body.setClipChildren(false);addView(body);
        heroPage=new LinearLayout(c);heroPage.setOrientation(LinearLayout.VERTICAL);heroPage.setPadding(0,dp(34),0,0);heroPage.setClipChildren(false);body.addView(heroPage,new LinearLayout.LayoutParams(-1,dp(488)));
        title=text("",34);title.setTypeface(android.graphics.Typeface.create("sans-serif-light",0));title.setMaxLines(2);title.setGravity(Gravity.CENTER_VERTICAL);title.setEllipsize(android.text.TextUtils.TruncateAt.END);heroPage.addView(title,new LinearLayout.LayoutParams(dp(420),dp(86)));
        meta=text("",13);meta.setPadding(0,dp(10),0,dp(4));heroPage.addView(meta);
        context=text("",13);context.setTextColor(0xffe1e9ef);context.setMaxLines(1);context.setEllipsize(android.text.TextUtils.TruncateAt.END);heroPage.addView(context);
        pills=new LinearLayout(c);pills.setVisibility(GONE);heroPage.addView(pills);
        plot=text("",14);plot.setMaxLines(4);plot.setLineSpacing(dp(2),1);plot.setEllipsize(android.text.TextUtils.TruncateAt.END);LinearLayout.LayoutParams plotSize=new LinearLayout.LayoutParams(Math.min(dp(440),getResources().getDisplayMetrics().widthPixels-dp(84)),dp(88));plotSize.topMargin=dp(12);heroPage.addView(plot,plotSize);
        LinearLayout buttons=new LinearLayout(c);buttons.setClipChildren(false);buttons.setPadding(0,dp(12),0,dp(10));heroPage.addView(buttons);
        trailer=button("Play Trailer",this::chooseTrailer);trailer.setVisibility(GONE);play=button("Play",this::play);buttons.addView(play);providerActions=new LinearLayout(c);providerActions.setClipChildren(false);buttons.addView(providerActions);margin(buttons,button("More",this::more));
        versions=button("Versions",()->chooseVersions.run());versions.setVisibility(GONE);
        heroPage.addView(new View(c),new LinearLayout.LayoutParams(1,0,1));tabBar=new LinearLayout(c);tabBar.setClipChildren(false);heroPage.addView(tabBar,new LinearLayout.LayoutParams(-1,dp(44)));tabLine=new View(c);tabLine.setBackgroundColor(0xbbffffff);heroPage.addView(tabLine,new LinearLayout.LayoutParams(-1,dp(1)));
        episodes=section("Episodes");cast=section("Cast");crew=section("Crew");details=section("Details");related=section("More Like This");trailers=section("Extras");
        lower=new FrameLayout(c);lower.setClipChildren(false);body.addView(lower,new LinearLayout.LayoutParams(-1,-2));information=new LinearLayout(c);information.setOrientation(LinearLayout.VERTICAL);lower.addView(information,new FrameLayout.LayoutParams(-1,-2));
        for(LinearLayout section:new LinearLayout[]{cast,crew,details}){View wrapper=(View)section.getParent();body.removeView(wrapper);information.addView(wrapper);}
        for(LinearLayout section:new LinearLayout[]{episodes,related,trailers}){View wrapper=(View)section.getParent();body.removeView(wrapper);lower.addView(wrapper,new FrameLayout.LayoutParams(-1,-2));}
        rebuildTabs();
    }
    @Override protected void onSizeChanged(int w,int h,int oldw,int oldh){super.onSizeChanged(w,h,oldw,oldh);heroPage.setLayoutParams(new LinearLayout.LayoutParams(-1,Math.max(dp(410),h)));lower.setMinimumHeight(h);}
    private void rebuildTabs(){View oldFocus=tabBar.findFocus();Object focusedTag=oldFocus==null?null:oldFocus.getTag();String selected=activeSection;tabBar.removeAllViews();sectionTabs.clear();List<String> labels=new ArrayList<>();if(show!=null)labels.add("Episodes");labels.add("More Like This");if(hasPlayableExtras())labels.add("Extras");labels.add("Details");for(String label:labels){TextView tab=button(label,()->showSection(label,true));tab.setCompoundDrawables(null,null,null,null);tab.setTag("section:"+label);tab.setOnFocusChangeListener((v,focused)->{if(focused)showSection(label,false);});LinearLayout.LayoutParams lp=new LinearLayout.LayoutParams(-2,-1);lp.rightMargin=dp(16);tabBar.addView(tab,lp);sectionTabs.add(tab);}showSection(labels.contains(selected)?selected:show!=null?"Episodes":"More Like This",false);if(focusedTag!=null){View restore=tabBar.findViewWithTag(focusedTag);if(restore!=null)restore.requestFocus();}}
    private void showSection(String label,boolean enter){activeSection=label;for(LinearLayout section:new LinearLayout[]{episodes,related,trailers})((View)section.getParent()).setVisibility(label.equals(section==episodes?"Episodes":section==related?"More Like This":"Extras")?VISIBLE:GONE);information.setVisibility(label.equals("Details")?VISIBLE:GONE);for(TextView tab:sectionTabs){boolean selected=("section:"+label).equals(tab.getTag());tab.setSelected(selected);android.graphics.drawable.LayerDrawable line=new android.graphics.drawable.LayerDrawable(new android.graphics.drawable.Drawable[]{PreviewDialog.focus(getContext()),new android.graphics.drawable.ColorDrawable(selected?PreviewAccent.color(getContext()):Color.TRANSPARENT)});line.setLayerHeight(1,dp(2));line.setLayerGravity(1,Gravity.BOTTOM);tab.setBackground(line);}if(enter){scrollTo(0,heroPage.getHeight());if(label.equals("Episodes")){focusNextEpisode();}else lower.requestFocus();}}
    private void focusNextEpisode(){Snapshot cache=snapshot!=null?snapshot:PreviewLibraryLoader.memoryCache();if(cache!=null&&show!=null){List<Entry> values=new ArrayList<>();for(Entry e:cache.episodes)if(e.show==show.getTvshowId())values.add(e);PreviewSeriesJourney.Selection selected=PreviewSeriesJourney.select(getContext(),values);if(selected.episode!=null)for(PreviewEpisodeRow row:episodeRows)if(row.focusEpisode(((Video)selected.episode.media).getId()))return;}if(!episodeRows.isEmpty())episodeRows.get(0).focusRemembered();}

    public boolean atTop(){View focused=findFocus();return getScrollY()==0&&focused!=null&&inside(focused,heroPage)&&!inside(focused,tabBar);}
    public void focusPrimary(){if(play.getVisibility()==VISIBLE)play.requestFocus();else if(trailer.getVisibility()==VISIBLE)trailer.requestFocus();else if(!providerActions.requestFocus())heroPage.requestFocus();}
    public void setVersions(int count,Runnable choose){chooseVersions=choose;versions.setText("Versions ("+count+")");versionCount=count;versions.setVisibility(GONE);}
    @Override public boolean dispatchKeyEvent(KeyEvent event){
        if(event.getAction()!=KeyEvent.ACTION_DOWN)return super.dispatchKeyEvent(event);
        View focused=findFocus();int key=event.getKeyCode();String tag=focused==null?"":String.valueOf(focused.getTag());
        if(tag.startsWith("section:")){
            if(key==KeyEvent.KEYCODE_DPAD_UP){focusPrimary();scrollTo(0,0);return true;}
            if(key==KeyEvent.KEYCODE_DPAD_DOWN){showSection(tag.substring(8),true);return true;}
        }else if(focused!=null&&inside(focused,heroPage)&&key==KeyEvent.KEYCODE_DPAD_DOWN){
            for(TextView tab:sectionTabs)if(tab.isSelected()){tab.requestFocus();return true;}
        }else if(focused!=null&&(key==KeyEvent.KEYCODE_DPAD_UP||key==KeyEvent.KEYCODE_DPAD_DOWN)){
            for(int i=0;i<episodeRows.size();i++)if(inside(focused,episodeRows.get(i))){
                int next=i+(key==KeyEvent.KEYCODE_DPAD_UP?-1:1);
                if(next>=0&&next<episodeRows.size())episodeRows.get(next).focusRemembered();
                else if(next<0)focusSectionTab();
                return true;
            }
            if(key==KeyEvent.KEYCODE_DPAD_UP&&getScrollY()>0){View next=focused.focusSearch(FOCUS_UP);if(next==null||!inside(next,lower)){focusSectionTab();return true;}}
        }
        return super.dispatchKeyEvent(event);
    }
    private void focusSectionTab(){for(TextView tab:sectionTabs)if(tab.isSelected()){tab.requestFocus();scrollTo(0,0);return;}}
    private boolean inside(View child,View parent){while(child!=null){if(child==parent)return true;child=child.getParent() instanceof View?(View)child.getParent():null;}return false;}
    private int dp(int n){return Math.round(n*getResources().getDisplayMetrics().density);}
    private TextView text(String s,int size){TextView t=new TextView(getContext());t.setText(s);t.setTextColor(Color.WHITE);t.setTextSize(size);return t;}
    private GradientDrawable bg(boolean focus){GradientDrawable d=new GradientDrawable();d.setColor(focus?0xee254964:0xcc10283b);d.setCornerRadius(dp(5));d.setStroke(dp(focus?2:1),focus?PreviewAccent.color(getContext()):0xff284b62);return d;}
    private TextView button(String s,Runnable run){TextView t=text(s,14);t.setTag("action:"+s);PreviewIcon.apply(t,s,16);t.setGravity(Gravity.CENTER);t.setPadding(dp(14),dp(9),dp(14),dp(9));t.setFocusable(true);t.setFocusableInTouchMode(true);t.setBackground(PreviewDialog.buttonFocus(getContext()));t.setOnClickListener(v->run.run());return t;}
    private void margin(LinearLayout row,View view){LinearLayout.LayoutParams lp=new LinearLayout.LayoutParams(-2,-2);lp.leftMargin=dp(18);row.addView(view,lp);}
    private LinearLayout section(String name){LinearLayout section=new LinearLayout(getContext());section.setOrientation(LinearLayout.VERTICAL);section.setPadding(0,dp(14),0,dp(8));TextView label=text(name,19);label.setTextColor(0xff9ed4f7);section.addView(label);LinearLayout content=new LinearLayout(getContext());content.setOrientation(LinearLayout.VERTICAL);content.setPadding(0,dp(10),0,0);section.addView(content);body.addView(section);return content;}
    public void bind(Video value){movie=value;title.setText(movie.getName());OfficialTitleArtwork.bind(title,value,false);plot.setText(movie.getDescriptionBody());
        play.setText("Play");
        if(movie.getPreviewBackdrop()!=null)artwork.accept(movie.getPreviewBackdrop());renderDetails();requestEnrichment();
    }
    private void renderHumanMetadata(){if(movie==null)return;
        List<String> metadata=new ArrayList<>();
        if(movie instanceof Episode){Episode episode=(Episode)movie;title.setText(safe(episode.getShowName())+" — "+safe(episode.getEpisodeName()));
            if(episode.getSeasonNumber()>=0&&episode.getEpisodeNumber()>0)metadata.add("S"+episode.getSeasonNumber()+" E"+episode.getEpisodeNumber());
            if(episode.getEpisodeDate()>0)metadata.add(episode.getEpisodeDateFormatted());
            else if(tags instanceof EpisodeTags){Date aired=((EpisodeTags)tags).getAired();if(aired!=null&&aired.getTime()>0)metadata.add(java.text.DateFormat.getDateInstance().format(aired));}
        }else if(movie instanceof Movie&&((Movie)movie).getYear()>0)metadata.add(String.valueOf(((Movie)movie).getYear()));
        long minutes=movie.getDurationMs()/60000;
        if(minutes<=0&&movie instanceof Episode&&tags instanceof EpisodeTags)minutes=tags.getRuntime(java.util.concurrent.TimeUnit.MINUTES);
        if(minutes>0)metadata.add(minutes+" min");
        String certificate=movie instanceof Movie?((Movie)movie).getContentRating():movie instanceof Episode?((Episode)movie).getContentRating():null;
        if(certificate!=null&&!certificate.isEmpty())metadata.add(certificate);
        meta.setText((movie instanceof Episode?safe(((Episode)movie).getEpisodeName())+"\n":"")+android.text.TextUtils.join("  ·  ",metadata));meta.setVisibility(metadata.isEmpty()?GONE:VISIBLE);
    }
    public void setTags(BaseTags value,List<ScraperTrailer> videos,List<ScraperImage> backdrops){View oldCast=cast.findFocus();Object castKey=oldCast==null?null:oldCast.getTag();int oldY=getScrollY();tags=value;trailerList=videos==null?Collections.emptyList():videos;
        if(backdrops!=null&&!backdrops.isEmpty()){ScraperImage image=backdrops.get(0);java.io.File file=image.getLargeFileF();if(file!=null&&file.exists()){artwork.accept(Uri.fromFile(file));if(movie!=null)movie.setPreviewBackdrop(Uri.fromFile(file).toString());}else if(image.getLargeUrl()!=null)artwork.accept(Uri.parse(image.getLargeUrl()));}
        portraits.clear();cast.removeAllViews();HorizontalScrollView scroll=new HorizontalScrollView(getContext());scroll.setSmoothScrollingEnabled(false);scroll.setHorizontalScrollBarEnabled(false);LinearLayout people=new LinearLayout(getContext());scroll.addView(people);cast.addView(scroll);
        if(tags!=null){for(Map.Entry<String,String> person:tags.getActors().entrySet()){person(people,person.getKey(),person.getValue());}
            crew.removeAllViews();HorizontalScrollView crewScroll=new HorizontalScrollView(getContext());LinearLayout crewPeople=new LinearLayout(getContext());crewScroll.addView(crewPeople);crew.addView(crewScroll);if(!safe(tags.getDirectorsFormatted()).isEmpty())person(crewPeople,tags.getDirectorsFormatted(),"Director");if(!safe(tags.getWritersFormatted()).isEmpty())person(crewPeople,tags.getWritersFormatted(),"Writer");}
        ((View)cast.getParent()).setVisibility(people.getChildCount()==0?GONE:VISIBLE);
        trailer.setVisibility(GONE);renderExtras();rebuildTabs();
        PreviewPeople.load(getContext().getApplicationContext(),tags,new HashMap<>(portraits));renderDetails();renderRelated();requestEnrichment();restoreRowFocus(cast,castKey,oldY);
    }
    public void setSnapshot(Snapshot value){snapshot=value;renderRelated();requestEnrichment();}
    private void renderDetails(){observeActions();if(movie==null&&show==null&&remoteDetails==null)return;renderHumanMetadata();details.removeAllViews();
        LinearLayout panels=new LinearLayout(getContext());panels.setClipChildren(false);details.addView(panels);LinearLayout key=factPanel(panels,"Key Information"),reception=factPanel(panels,"Reception"),technical=factPanel(panels,"Technical Information");
        String genre=tags instanceof VideoTags?((VideoTags)tags).getGenresFormatted():"";if(safe(genre).isEmpty()&&tags instanceof EpisodeTags){ShowTags parent=((EpisodeTags)tags).getShowTags();if(parent!=null)genre=parent.getGenresFormatted();}context.setText(safe(genre));context.setVisibility(safe(genre).isEmpty()?GONE:VISIBLE);
        int year=movie instanceof Movie?((Movie)movie).getYear():show!=null?show.getYear():0;if(year>0)fact(key,"Year",String.valueOf(year));long runtime=movie==null?0:movie.getDurationMs();if(runtime>0)fact(key,"Runtime",runtime/60000+" min");String certificate=movie instanceof Movie?((Movie)movie).getContentRating():movie instanceof Episode?((Episode)movie).getContentRating():null;fact(key,"Age rating",certificate);fact(key,"Genres",genre);
        if(tags instanceof MovieTags)fact(key,"Studio / Network",((MovieTags)tags).getStudiosFormatted());else if(tags instanceof ShowTags)fact(key,"Studio / Network",((ShowTags)tags).getStudiosFormatted());else if(tags instanceof EpisodeTags&&((EpisodeTags)tags).getShowTags()!=null)fact(key,"Studio / Network",((EpisodeTags)tags).getShowTags().getStudiosFormatted());
        float rating=movie instanceof Episode?(tags instanceof EpisodeTags?((EpisodeTags)tags).getRating():((Episode)movie).getEpisodeRating()):movie instanceof Movie?((Movie)movie).getRating():show==null?0:show.getRating();if(rating>0)fact(reception,"TMDb rating",String.format(Locale.UK,"%.1f / 10",rating));
        if(movie!=null){if(movie.getMeasuredWidth()>0&&movie.getMeasuredHeight()>0)fact(technical,"Resolution",movie.getMeasuredWidth()+" × "+movie.getMeasuredHeight());fact(technical,"Video codec",PreviewMediaInfo.format(movie.getCalculatedVideoFormat()));fact(technical,"Audio",PreviewMediaInfo.format(movie.getCalculatedBestAudioFormat()));fact(technical,"Source",source(movie.getFileUri()));if(movie.getSize()>0)fact(technical,"File size",android.text.format.Formatter.formatShortFileSize(getContext(),movie.getSize()));fact(technical,"Selected file",movie.getFilenameNonCryptic());}else if(show!=null)fact(technical,"Episode file","Select an episode to inspect its physical file.");
        org.json.JSONObject enriched=enrichment!=null?enrichment.details:remoteDetails;
        if(movie!=null&&movie.getMetadata()!=null&&movie.getMetadata().getVideoTrack()!=null){int transfer=movie.getMetadata().getVideoTrack().colorTrc;if(transfer==16)fact(technical,"Dynamic range","HDR (PQ)");else if(transfer==18)fact(technical,"Dynamic range","HLG");}
        if(enriched!=null){long budget=enriched.optLong("budget");if(budget>0)fact(key,"Budget (TMDb)",java.text.NumberFormat.getCurrencyInstance(Locale.US).format(budget));if(remoteDetails!=null){fact(key,"Release / first air date",enriched.optString("release_date",enriched.optString("first_air_date")));if(enriched.optInt("runtime")>0)fact(key,"Runtime",enriched.optInt("runtime")+" min");if(enriched.optDouble("vote_average")>0)fact(reception,"TMDb rating",String.format(Locale.UK,"%.1f / 10",enriched.optDouble("vote_average")));}}
        ((View)details.getParent()).setVisibility(VISIBLE);pills.setVisibility(GONE);
    }
    private LinearLayout factPanel(LinearLayout panels,String name){LinearLayout panel=new LinearLayout(getContext());panel.setOrientation(LinearLayout.VERTICAL);panel.setPadding(dp(16),dp(16),dp(16),dp(20));panel.setBackground(PreviewDialog.surface(getContext(),false));panel.setFocusable(true);panel.setForeground(PreviewDialog.focus(getContext()));LinearLayout.LayoutParams size=new LinearLayout.LayoutParams(0,-2,1);size.rightMargin=dp(12);panels.addView(panel,size);panel.addView(text(name,18));return panel;}
    private void fact(LinearLayout panel,String label,String value){if(value==null||value.trim().isEmpty())return;TextView name=text(label,12);name.setTextColor(0xffb9c7d2);name.setPadding(0,dp(16),0,dp(4));panel.addView(name);TextView content=text(value,14);content.setLineSpacing(dp(3),1);panel.addView(content);}
    private void observeActions(){ObjectAdapter next=actions.get();if(next!=observedActions){if(observedActions!=null)observedActions.unregisterObserver(actionObserver);observedActions=next;if(next!=null)next.registerObserver(actionObserver);}renderProviders();}
    private void renderProviders(){
        View focused=providerActions.findFocus();Object focusKey=focused==null?null:focused.getTag();
        for(Presenter.ViewHolder holder:providerHolders)providerPresenter.onUnbindViewHolder(holder);providerHolders.clear();providerActions.removeAllViews();if(observedActions==null){if(focused!=null)play.requestFocus();return;}
        List<Action> offers=new ArrayList<>(),extraOptions=new ArrayList<>();for(int i=0;i<observedActions.size();i++){Object item=observedActions.get(i);if(item instanceof Action&&com.archos.mediacenter.video.streaming.StreamingActions.isAvailableOffer((Action)item))offers.add((Action)item);else if(item instanceof Action&&"•••".contentEquals(((Action)item).getLabel1()))extraOptions.add((Action)item);}
        play.setVisibility(remoteDetails==null?VISIBLE:GONE);
        if(!offers.isEmpty()){
            Action offer=offers.get(0);Presenter.ViewHolder holder=providerPresenter.onCreateViewHolder(providerActions);providerPresenter.onBindViewHolder(holder,offer);holder.view.setTag(offer.getId());holder.view.setOnClickListener(v->action.accept(offer));LinearLayout.LayoutParams lp=new LinearLayout.LayoutParams(dp(160),dp(38));lp.leftMargin=dp(18);providerActions.addView(holder.view,lp);providerHolders.add(holder);

        }
        if(focused!=null){View replacement=focusKey==null?null:providerActions.findViewWithTag(focusKey);if(replacement!=null)replacement.requestFocus();else if(providerActions.getChildCount()>0)providerActions.getChildAt(0).requestFocus();else play.requestFocus();}
    }
    private void pill(String value){if(value==null||value.isEmpty())return;TextView label=text(value,10);label.setSingleLine(true);label.setPadding(dp(6),dp(3),dp(6),dp(3));GradientDrawable badge=new GradientDrawable();badge.setColor(0x50142634);badge.setCornerRadius(dp(3));badge.setStroke(dp(1),0xff648196);label.setBackground(badge);LinearLayout.LayoutParams lp=new LinearLayout.LayoutParams(-2,-2);lp.rightMargin=dp(6);pills.addView(label,lp);}

    private void person(LinearLayout people,String name,String role){
        LinearLayout card=new LinearLayout(getContext());card.setOrientation(android.widget.LinearLayout.VERTICAL);card.setGravity(Gravity.TOP|Gravity.CENTER_HORIZONTAL);card.setBackgroundColor(Color.TRANSPARENT);card.setFocusable(true);card.setFocusableInTouchMode(true);card.setPadding(dp(4),dp(4),dp(4),dp(4));
        // The existing scraper stores names/roles but no portrait URLs. Use the real Nova asset.
        ImageView portrait=new ImageView(getContext());portrait.setImageResource(com.archos.mediacenter.video.R.drawable.preview_person);portraits.put(name,portrait);portrait.setScaleType(ImageView.ScaleType.CENTER_CROP);portrait.setClipToOutline(true);portrait.setOutlineProvider(new ViewOutlineProvider(){@Override public void getOutline(View view,android.graphics.Outline outline){outline.setOval(0,0,view.getWidth(),view.getHeight());}});card.addView(portrait,new LinearLayout.LayoutParams(dp(52),dp(52)));
        LinearLayout words=new LinearLayout(getContext());words.setOrientation(android.widget.LinearLayout.VERTICAL);words.setGravity(Gravity.CENTER_HORIZONTAL);words.setPadding(0,dp(6),0,0);card.addView(words,new LinearLayout.LayoutParams(-1,-2));
        TextView label=text(name,11);label.setGravity(Gravity.CENTER);label.setMaxLines(2);label.setEllipsize(android.text.TextUtils.TruncateAt.END);words.addView(label,new LinearLayout.LayoutParams(-1,dp(28)));TextView detail=text(role==null?"":role,9);detail.setGravity(Gravity.CENTER);detail.setMaxLines(1);detail.setTextColor(0xffa4b6c7);words.addView(detail);card.setContentDescription(name+" "+safe(role));LinearLayout.LayoutParams lp=new LinearLayout.LayoutParams(dp(96),dp(108));lp.rightMargin=dp(10);people.addView(card,lp);card.setTag("person:"+name+":"+safe(role));rowKeys(card,people);
        card.setForeground(PreviewDialog.focus(getContext()));
    }
    private void allPeople(){if(tags==null)return;StringBuilder people=new StringBuilder();if(!safe(tags.getDirectorsFormatted()).isEmpty())people.append("Director · ").append(tags.getDirectorsFormatted()).append("\n\n");if(!safe(tags.getWritersFormatted()).isEmpty())people.append("Writers · ").append(tags.getWritersFormatted()).append("\n\n");for(Map.Entry<String,String> person:tags.getActors().entrySet())people.append(person.getKey()).append(safe(person.getValue()).isEmpty()?"":" · "+person.getValue()).append('\n');PreviewDialog.read(getContext(),"Cast & Crew",people.toString());}
    public void bindShow(Tvshow value,Runnable playAction){show=value;showPlay=playAction;OfficialTitleArtwork.bind(title,value,false);title.setText(value.getName());plot.setText(value.getPlot());rebuildTabs();meta.setText((value.getYear()>0?value.getYear()+" · ":"")+value.getSeasonCount()+(value.getSeasonCount()==1?" season":" seasons")+" · "+value.getEpisodeCount()+(value.getEpisodeCount()==1?" episode":" episodes"));renderDetails();}
    public void setSeason(int number,List<Episode> values){View focused=findFocus();Object key=focused==null?null:focused.getTag();int y=getScrollY();seasons.put(number,new ArrayList<>(values));episodes.removeAllViews();episodeRows.clear();for(Map.Entry<Integer,List<Episode>> season:seasons.entrySet()){episodes.addView(text(season.getKey()==0?"Specials":"Season "+season.getKey(),18));List<Entry> physical=new ArrayList<>();for(Episode ep:season.getValue())physical.add(new Entry(ep,0,show==null?0:show.getTvshowId(),""));List<Episode> logical=new ArrayList<>();for(Entry e:PreviewVariants.logicalChoices(physical))logical.add((Episode)e.media);logical.sort(Comparator.comparingInt(Episode::getEpisodeNumber));PreviewEpisodeRow row=new PreviewEpisodeRow((Activity)getContext(),logical);episodeRows.add(row);episodes.addView(row,new LinearLayout.LayoutParams(-1,dp(156)));}if(key instanceof String&&((String)key).startsWith("episode:")){try{long id=Long.parseLong(((String)key).substring(8));for(PreviewEpisodeRow row:episodeRows)if(row.focusEpisode(id))break;}catch(NumberFormatException ignored){}}scrollTo(0,y);}

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
    private void renderRelated(){View focused=related.findFocus();Object focusKey=focused==null?null:focused.getTag();int oldY=getScrollY();for(PreviewLandscapeCard card:landscapeCards)card.release();landscapeCards.clear();related.removeAllViews();if(movie==null&&show==null&&remoteDetails==null)return;
        Entry selected=remoteDetails!=null?null:new Entry(show!=null?show:movie,0,show==null?0:show.getTvshowId(),tags instanceof VideoTags?((VideoTags)tags).getGenresFormatted():context.getText().toString());
        LinearLayout row=null;int count=0;for(Entry entry:snapshot==null?Collections.<Entry>emptyList():PreviewDiscovery.similar(selected,snapshot)){if(count>=12)break;if(show!=null&&!(entry.media instanceof Tvshow)||show==null&&!(entry.media instanceof Movie))continue;if(count%4==0){row=new LinearLayout(getContext());row.setClipChildren(false);related.addView(row);}PreviewLandscapeCard card=new PreviewLandscapeCard(getContext());card.bind(PreviewPages.displayName(entry),"",entry.backdrop!=null?entry.backdrop:entry.media.getPosterUri(),true);card.setTag("related:"+entry.key());card.setOnClickListener(v->new VideoViewClickedListener((Activity)getContext()).onItemClicked(new Presenter.ViewHolder(v),entry.media,null,null));LinearLayout.LayoutParams lp=new LinearLayout.LayoutParams(0,dp(142),1);lp.setMargins(dp(5),dp(8),dp(5),dp(8));row.addView(card,lp);landscapeCards.add(card);count++;}if(enrichment!=null)for(PreviewDetailsData.Remote remote:enrichment.related){if(count>=12)break;boolean local=false;if(snapshot!=null)for(Entry entry:(show!=null||"tv".equals(remoteKind)?snapshot.shows:snapshot.movies))if(entry.onlineId==remote.title.optLong("id")){local=true;break;}if(local)continue;if(count%4==0){row=new LinearLayout(getContext());row.setClipChildren(false);related.addView(row);}PreviewLandscapeCard card=new PreviewLandscapeCard(getContext());org.json.JSONObject value=remote.title;String name=value.optString("title",value.optString("name"));String backdrop=value.optString("backdrop_path");card.bind(name,"",backdrop.matches("/[A-Za-z0-9._-]+")?Uri.parse("https://image.tmdb.org/t/p/w780"+backdrop):null,true);card.availability.setImageDrawable(new PreviewIcon("streaming"));String logo=remote.provider.logo;if(logo!=null&&logo.matches("/[A-Za-z0-9._-]+"))com.squareup.picasso.Picasso.get().load("https://image.tmdb.org/t/p/w154"+logo).into(card.availability);card.setTag("remote:"+value.optLong("id"));card.setContentDescription(name+" · "+remote.provider.name);card.setOnClickListener(v->getContext().startActivity(new Intent(getContext(),com.archos.mediacenter.video.streaming.PreviewRemoteDetailsActivity.class).putExtra("kind",remoteDetails!=null?remoteKind:show!=null?"tv":"movie").putExtra("tmdb_id",value.optLong("id"))));LinearLayout.LayoutParams lp=new LinearLayout.LayoutParams(0,dp(142),1);lp.setMargins(dp(5),dp(8),dp(5),dp(8));row.addView(card,lp);landscapeCards.add(card);count++;}if(row!=null)for(int i=count%4;i>0&&i<4;i++)row.addView(new View(getContext()),new LinearLayout.LayoutParams(0,1,1));if(count==0)related.addView(text("No similar playable titles in your library.",14));restoreRowFocus(related,focusKey,oldY);
    }
    private boolean hasPlayableExtras(){for(ScraperTrailer e:trailerList)if("YouTube".equals(e.mSite)&&e.mVideoKey!=null&&e.mVideoKey.matches("[A-Za-z0-9_-]{11}"))return true;return false;}
    private void renderExtras(){View focused=trailers.findFocus();Object focusKey=focused==null?null:focused.getTag();int y=getScrollY();for(PreviewLandscapeCard old:extraCards)old.release();extraCards.clear();trailers.removeAllViews();LinearLayout row=null;int count=0;for(ScraperTrailer extra:trailerList){if(!"YouTube".equals(extra.mSite)||extra.mVideoKey==null||!extra.mVideoKey.matches("[A-Za-z0-9_-]{11}"))continue;if(count>=12)break;if(count%4==0){row=new LinearLayout(getContext());row.setClipChildren(false);trailers.addView(row);}PreviewLandscapeCard card=new PreviewLandscapeCard(getContext());String name=safe(extra.mName);String lowerName=name.toLowerCase(Locale.ROOT);String type=lowerName.contains("trailer")?"Trailer":lowerName.contains("teaser")?"Teaser":lowerName.contains("featurette")?"Featurette":lowerName.contains("interview")?"Interview":"Video";if(enrichment!=null)for(PreviewDetailsData.Extra e:enrichment.extras)if(e.key.equals(extra.mVideoKey)){type=e.type;break;}card.bind(name,type,Uri.parse("https://i.ytimg.com/vi/"+extra.mVideoKey+"/hqdefault.jpg"),false);card.setTag("extra:"+extra.mVideoKey);card.setOnClickListener(v->PreviewTrailer.show((Activity)getContext(),extra));LinearLayout.LayoutParams lp=new LinearLayout.LayoutParams(0,dp(155),1);lp.setMargins(dp(5),dp(8),dp(5),dp(8));row.addView(card,lp);extraCards.add(card);count++;}if(row!=null)for(int i=count%4;i>0&&i<4;i++)row.addView(new View(getContext()),new LinearLayout.LayoutParams(0,1,1));restoreRowFocus(trailers,focusKey,y);}
    public void bindRemote(org.json.JSONObject details,String kind,long id){remoteDetails=details;remoteKind=kind;remoteId=id;title.setText(details.optString("title",details.optString("name")));plot.setText(details.optString("overview"));meta.setText(details.optString("release_date",details.optString("first_air_date")));String backdrop=details.optString("backdrop_path");if(backdrop.matches("/[A-Za-z0-9._-]+"))artwork.accept(Uri.parse("https://image.tmdb.org/t/p/w1280"+backdrop));play.setVisibility(GONE);renderDetails();requestEnrichment();}
    private void requestEnrichment(){long id=remoteDetails!=null?remoteId:movie instanceof Movie?((Movie)movie).getOnlineId():show!=null&&show.getShowTags()!=null?show.getShowTags().getOnlineId():0;String kind=remoteDetails!=null?remoteKind:show==null?"movie":"tv";if(id<=0)return;String key=kind+":"+id;if(key.equals(enrichmentKey))return;enrichmentKey=key;int generation=++enrichmentGeneration;if(enrichmentTask!=null)enrichmentTask.cancel(true);Set<Long> local=new HashSet<>();if(snapshot!=null)for(Entry e:kind.equals("movie")?snapshot.movies:snapshot.shows)local.add(e.onlineId);Context app=getContext().getApplicationContext();enrichmentTask=com.archos.mediacenter.video.streaming.StreamingRepository.IO.submit(()->{long start=android.os.SystemClock.elapsedRealtime();try{PreviewDetailsData.Result result=PreviewDetailsData.load(app,kind,id,local);post(()->{if(generation!=enrichmentGeneration)return;enrichment=result;List<ScraperTrailer> extras=new ArrayList<>();for(PreviewDetailsData.Extra e:result.extras)extras.add(new ScraperTrailer(ScraperTrailer.Type.SHOW_TRAILER,e.name,e.key,"YouTube",""));if(!extras.isEmpty())trailerList=extras;renderDetails();renderRelated();renderExtras();rebuildTabs();});}catch(Exception error){com.archos.mediacenter.video.diagnostics.Diagnostics.error("details_enrichment_unavailable",error);}finally{com.archos.mediacenter.video.diagnostics.Diagnostics.event("details_enrichment","elapsed_ms",android.os.SystemClock.elapsedRealtime()-start);}});}
    private void rowKeys(View card,LinearLayout row){card.setOnKeyListener((v,key,event)->{if(event.getAction()!=KeyEvent.ACTION_DOWN||key!=KeyEvent.KEYCODE_DPAD_LEFT&&key!=KeyEvent.KEYCODE_DPAD_RIGHT)return false;int next=row.indexOfChild(v)+(key==KeyEvent.KEYCODE_DPAD_LEFT?-1:1);if(next>=0&&next<row.getChildCount())row.getChildAt(next).requestFocus();return true;});}
    private void restoreRowFocus(ViewGroup area,Object key,int y){if(key==null)return;View target=area.findViewWithTag(key);if(target==null)for(View candidate:area.getFocusables(View.FOCUS_FORWARD))if(candidate.getTag()!=null){target=candidate;break;}if(target==null)target=play;target.requestFocus();scrollTo(0,y);final View restored=target;post(()->{if(restored.hasFocus())scrollTo(0,y);});}
    public void play(){if(showPlay!=null){showPlay.run();return;}ObjectAdapter adapter=actions.get();if(adapter==null)return;
        for(int id:new int[]{VideoActionAdapter.ACTION_RESUME,VideoActionAdapter.ACTION_LOCAL_RESUME,VideoActionAdapter.ACTION_PLAY,VideoActionAdapter.ACTION_PLAY_FROM_BEGIN,VideoActionAdapter.ACTION_REMOTE_RESUME})
            for(int i=0;i<adapter.size();i++){Object item=adapter.get(i);if(item instanceof Action&&((Action)item).getId()==id){action.accept((Action)item);return;}}
    }
    private Entry rowEntry(){if(show!=null)return new Entry(show,0,show.getTvshowId(),"");if(movie==null)return null;long showId=0;Snapshot cached=PreviewLibraryLoader.memoryCache();if(movie instanceof Episode&&cached!=null)for(Entry e:cached.episodes)if(((Video)e.media).getId()==movie.getId()){showId=e.show;break;}return new Entry(movie,0,showId,"");}
    private void moreInfo(){
        View previous=findFocus();Dialog dialog=new Dialog(getContext());dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        ScrollView scroll=new ScrollView(getContext());scroll.setFillViewport(false);scroll.setFocusable(true);scroll.setBackground(PreviewDialog.surface(getContext(),false));LinearLayout page=new LinearLayout(getContext());page.setOrientation(LinearLayout.VERTICAL);page.setPadding(dp(24),dp(20),dp(24),dp(22));scroll.addView(page);
        TextView heading=text(title.getText().toString(),28);page.addView(heading);TextView metadata=text(meta.getText()+"  "+context.getText(),13);metadata.setTextColor(0xffadc3d3);page.addView(metadata);
        LinearLayout columns=new LinearLayout(getContext());columns.setPadding(0,dp(10),0,0);page.addView(columns,new LinearLayout.LayoutParams(-1,-2));LinearLayout overview=new LinearLayout(getContext());overview.setOrientation(LinearLayout.VERTICAL);columns.addView(overview,new LinearLayout.LayoutParams(0,-2,1.65f));LinearLayout facts=new LinearLayout(getContext());facts.setOrientation(LinearLayout.VERTICAL);facts.setPadding(dp(30),0,0,0);columns.addView(facts,new LinearLayout.LayoutParams(0,-2,1));
        infoSection(overview,"Overview",show!=null?safe(show.getPlot()):movie==null?"":safe(movie.getDescriptionBody()));
        if(tags!=null){StringBuilder people=new StringBuilder();int count=0;for(Map.Entry<String,String> p:tags.getActors().entrySet()){if(count++==3)break;people.append(p.getKey()).append(safe(p.getValue()).isEmpty()?"":" · "+p.getValue()).append("\n");}if(!safe(tags.getDirectorsFormatted()).isEmpty())people.append("Director · ").append(tags.getDirectorsFormatted());infoSection(overview,"Cast & Crew",people.toString());overview.addView(button("See All",this::allPeople));}
        if(movie!=null){List<String> technical=new ArrayList<>();if(movie.getMeasuredWidth()>0&&movie.getMeasuredHeight()>0)technical.add(movie.getMeasuredWidth()+" × "+movie.getMeasuredHeight());String video=PreviewMediaInfo.format(movie.getCalculatedVideoFormat()),audio=PreviewMediaInfo.format(movie.getCalculatedBestAudioFormat());if(!safe(video).isEmpty())technical.add(video);if(!safe(audio).isEmpty())technical.add(audio);technicalBadges(facts,technical);String file=safe(movie.getFilenameNonCryptic())+"\n"+source(movie.getFileUri());if(movie.getSize()>0)file+=" · "+android.text.format.Formatter.formatFileSize(getContext(),movie.getSize());infoSection(facts,"Indexed library file",file);}
        if(show!=null)infoSection(facts,"Series",meta.getText()+"\n"+context.getText());TextView back=text("Press Back to return to Details",11);back.setPadding(0,dp(26),0,0);back.setTextColor(0xff8da9bb);page.addView(back);
        Runnable removeFrost=PreviewFrost.show((Activity)getContext());dialog.setContentView(scroll);dialog.setOnDismissListener(d->{removeFrost.run();if(previous!=null)previous.requestFocus();});dialog.show();android.view.Window window=dialog.getWindow();window.setBackgroundDrawableResource(android.R.color.transparent);window.addFlags(android.view.WindowManager.LayoutParams.FLAG_DIM_BEHIND);window.setDimAmount(.48f);int width=Math.min(dp(840),getResources().getDisplayMetrics().widthPixels-dp(64));int maximum=Math.min(dp(360),getResources().getDisplayMetrics().heightPixels-dp(96));scroll.measure(View.MeasureSpec.makeMeasureSpec(width,View.MeasureSpec.EXACTLY),View.MeasureSpec.makeMeasureSpec(maximum,View.MeasureSpec.AT_MOST));window.setLayout(width,Math.min(maximum,scroll.getMeasuredHeight()));window.setGravity(Gravity.CENTER);scroll.requestFocus();
    }
    private void infoSection(LinearLayout page,String label,String value){if(value==null||value.trim().isEmpty())return;TextView heading=text(label,18);heading.setTextColor(PreviewAccent.color(getContext()));heading.setPadding(0,dp(22),0,dp(8));page.addView(heading);TextView content=text(value.trim(),14);content.setLineSpacing(dp(3),1f);page.addView(content);}
    private void technicalBadges(LinearLayout parent,List<String> values){
        if(values.isEmpty())return;infoSection(parent,"Technical Information","");
        LinearLayout line=null;for(int i=0;i<values.size();i++){if(i%2==0){line=new LinearLayout(getContext());parent.addView(line);}TextView badge=text(values.get(i),11);badge.setPadding(dp(7),dp(5),dp(7),dp(5));badge.setBackground(PreviewDialog.surface(getContext(),false));LinearLayout.LayoutParams lp=new LinearLayout.LayoutParams(0,-2,1);lp.setMargins(0,dp(4),dp(5),0);line.addView(badge,lp);}
    }
    private static String titleStyle(String label){StringBuilder out=new StringBuilder();for(String word:label.split(" ")){if(out.length()>0)out.append(' ');if(!word.isEmpty())out.append(Character.toUpperCase(word.charAt(0))).append(word.substring(1));}return out.toString();}
    private void more(){ObjectAdapter adapter=actions.get();if(adapter==null)return;
        List<String> labels=new ArrayList<>();List<Runnable> commands=new ArrayList<>();Map<Integer,String> logos=new HashMap<>();
        if(versionCount>1){labels.add("Versions ("+versionCount+")");commands.add(chooseVersions);}
        String[] groups={"Playback & Navigation","Library","Streaming Services","Files & Media"};
        for(int group=0;group<groups.length;group++){
            List<Action> section=new ArrayList<>();for(int i=0;i<adapter.size();i++){Object value=adapter.get(i);if(!(value instanceof Action))continue;Action a=(Action)value;
                if(show!=null&&a.getId()==com.archos.mediacenter.video.leanback.tvshow.TvshowActionAdapter.ACTION_MORE_DETAILS)continue;
                String label=String.valueOf(a.getLabel1()).toLowerCase(Locale.ROOT);int category=a instanceof com.archos.mediacenter.video.streaming.StreamingActionPresenter.LogoAction||label.contains("streaming")||label.equals("•••")?2:label.contains("delete")||label.contains("file")||label.contains("remove info")?3:label.contains("play")||label.contains("resume")||label.contains("episode")?0:1;
                if(category==group)section.add(a);
            }
            if(section.isEmpty()&&group==2)continue;
            labels.add("— "+groups[group]);commands.add(null);
            for(Action a:section){String label=a instanceof com.archos.mediacenter.video.streaming.StreamingActionPresenter.LogoAction?((com.archos.mediacenter.video.streaming.StreamingActionPresenter.LogoAction)a).provider.name:titleStyle(String.valueOf(a.getLabel1()));if(a instanceof com.archos.mediacenter.video.streaming.StreamingActionPresenter.LogoAction)logos.put(labels.size(),((com.archos.mediacenter.video.streaming.StreamingActionPresenter.LogoAction)a).provider.logo);labels.add(label.equals("•••")?"More Streaming Services":label);commands.add(()->action.accept(a));}
            if(group==1){if(remoteDetails==null){labels.add("Add to Row");commands.add(()->PreviewHomeRows.add(getContext(),rowEntry(),()->{}));}labels.add("Full Synopsis");commands.add(()->PreviewDialog.read(getContext(),title.getText().toString(),plot.getText().toString()));}
            if(group==3&&movie!=null){labels.add("File, Subtitles and Artwork");commands.add(nativeDetails);}
        }
        Dialog dialog=PreviewDialog.choose(getContext(),"Actions",labels.toArray(new String[0]),-1,n->{if(commands.get(n)!=null)commands.get(n).run();});for(Map.Entry<Integer,String> logo:logos.entrySet())com.archos.mediacenter.video.streaming.PreviewProviderIcons.bind(dialog,logo.getKey(),logo.getValue());
    }
    private ScraperTrailer primaryTrailer(){
        ScraperTrailer best=null;int score=0;for(ScraperTrailer t:trailerList){if(!"YouTube".equals(t.mSite)||t.mVideoKey==null||!t.mVideoKey.matches("[A-Za-z0-9_-]{11}"))continue;String name=t.mName==null?"":t.mName.toLowerCase(Locale.ROOT);if(!name.contains("trailer")||name.contains("fan")||name.contains("reaction"))continue;int rank=(name.contains("official")?4:1)+(name.contains("main")?2:0)+(name.contains("teaser")?-1:0);if(rank>score){score=rank;best=t;}}return best;
    }
    private void chooseTrailer(){ScraperTrailer t=primaryTrailer();if(t!=null)PreviewTrailer.show((Activity)getContext(),t);}
    @Override protected void onDetachedFromWindow(){enrichmentGeneration++;if(enrichmentTask!=null)enrichmentTask.cancel(true);if(observedActions!=null){observedActions.unregisterObserver(actionObserver);observedActions=null;}for(Presenter.ViewHolder h:providerHolders)providerPresenter.onUnbindViewHolder(h);providerHolders.clear();for(Presenter.ViewHolder h:cards)presenter.onUnbindViewHolder(h);cards.clear();for(PreviewLandscapeCard card:landscapeCards)card.release();landscapeCards.clear();for(PreviewLandscapeCard card:extraCards)card.release();extraCards.clear();super.onDetachedFromWindow();}
}
