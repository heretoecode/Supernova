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
    private final LinearLayout body,cast,details,related,trailers;
    private final TextView title,meta,plot,play,trailer,context;
    private final LinearLayout pills;
    private final ImageView poster;
    private final LinearLayout providerActions;
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
    private final List<Presenter.ViewHolder> cards=new ArrayList<>();
    private final PreviewCardPresenter presenter=new PreviewCardPresenter(PreviewCardPresenter.Style.CONTINUE);
    public PreviewMoviePage(Context c,Supplier<ObjectAdapter> actions,Consumer<Action> action,Runnable nativeDetails,Consumer<Uri> artwork){
        super(c);this.actions=actions;this.action=action;this.nativeDetails=nativeDetails;this.artwork=artwork;
        setFillViewport(true);setSmoothScrollingEnabled(false);setClipToPadding(false);body=new LinearLayout(c);body.setOrientation(LinearLayout.VERTICAL);body.setPadding(dp(28),dp(20),dp(28),dp(24));addView(body);
        LinearLayout composition=new LinearLayout(c);composition.setGravity(Gravity.TOP);body.addView(composition,new LinearLayout.LayoutParams(-1,-2));
        poster=new ImageView(c);poster.setScaleType(ImageView.ScaleType.FIT_CENTER);poster.setBackgroundColor(0x40071622);
        LinearLayout.LayoutParams posterParams=new LinearLayout.LayoutParams(dp(142),dp(213));posterParams.rightMargin=dp(20);composition.addView(poster,posterParams);
        LinearLayout hero=new LinearLayout(c);hero.setOrientation(LinearLayout.VERTICAL);hero.setGravity(Gravity.TOP);composition.addView(hero,new LinearLayout.LayoutParams(0,-2,1));
        title=text("",27);title.setTypeface(null,android.graphics.Typeface.BOLD);title.setMaxLines(2);title.setEllipsize(android.text.TextUtils.TruncateAt.END);hero.addView(title,new LinearLayout.LayoutParams(-1,-2));
        meta=text("",12);meta.setPadding(0,dp(5),0,dp(4));hero.addView(meta);
        context=text("",12);context.setTextColor(0xffb3c8d7);context.setMaxLines(2);context.setEllipsize(android.text.TextUtils.TruncateAt.END);hero.addView(context,new LinearLayout.LayoutParams(-1,-2));
        pills=new LinearLayout(c);pills.setPadding(0,dp(5),0,dp(5));hero.addView(pills);
        LinearLayout buttons=new LinearLayout(c);buttons.setPadding(0,dp(9),0,dp(10));hero.addView(buttons);
        play=button("Play",this::play);buttons.addView(play);providerActions=new LinearLayout(c);buttons.addView(providerActions);
        trailer=button("Trailer",this::chooseTrailer);trailer.setVisibility(View.GONE);margin(buttons,trailer);
        margin(buttons,button("More",this::more));TextView add=button("Add to List · Coming soon",()->{});add.setEnabled(false);add.setFocusable(false);add.setAlpha(.45f);margin(buttons,add);
        plot=text("",13);plot.setMaxLines(3);plot.setEllipsize(android.text.TextUtils.TruncateAt.END);hero.addView(plot,new LinearLayout.LayoutParams(-1,-2));
        episodes=section("Seasons & Episodes");episodes.getParent();((View)episodes.getParent()).setVisibility(GONE);cast=section("Cast & Crew");details=section("Details");related=section("More Like This — In Your Library");trailers=section("Trailers & Extras");((View)trailers.getParent()).setVisibility(GONE);
        ((View)cast.getParent()).setVisibility(GONE);((View)related.getParent()).setVisibility(GONE);
    }
    public boolean atTop(){return getScrollY()==0 && (play.hasFocus()||trailer.hasFocus()||findFocus()!=null&&"more".equals(findFocus().getTag()));}
    public void focusPrimary(){play.requestFocus();}
    @Override public boolean dispatchKeyEvent(KeyEvent e){if(e.getAction()==KeyEvent.ACTION_DOWN&&e.getKeyCode()==KeyEvent.KEYCODE_DPAD_UP&&(play.hasFocus()||trailer.hasFocus())&&getScrollY()>0){smoothScrollTo(0,0);return true;}return super.dispatchKeyEvent(e);}
    private int dp(int n){return Math.round(n*getResources().getDisplayMetrics().density);}
    private TextView text(String s,int size){TextView t=new TextView(getContext());t.setText(s);t.setTextColor(Color.WHITE);t.setTextSize(size);return t;}
    private GradientDrawable bg(boolean focus){GradientDrawable d=new GradientDrawable();d.setColor(focus?0xee254964:0xcc10283b);d.setCornerRadius(dp(5));d.setStroke(dp(focus?2:1),focus?0xff62bbf3:0xff284b62);return d;}
    private TextView button(String s,Runnable run){TextView t=text(s,14);if(s.startsWith("More"))t.setTag("more");PreviewIcon.apply(t,s,16);t.setGravity(Gravity.CENTER);t.setPadding(dp(14),dp(9),dp(14),dp(9));t.setFocusable(true);t.setFocusableInTouchMode(true);t.setBackground(bg(false));t.setOnFocusChangeListener((v,f)->v.setBackground(bg(f)));t.setOnClickListener(v->run.run());return t;}
    private void margin(LinearLayout row,View view){LinearLayout.LayoutParams lp=new LinearLayout.LayoutParams(-2,-2);lp.leftMargin=dp(12);row.addView(view,lp);}
    private LinearLayout section(String name){LinearLayout section=new LinearLayout(getContext());section.setOrientation(LinearLayout.VERTICAL);section.setPadding(0,dp(14),0,dp(8));TextView label=text(name,19);label.setTextColor(0xff9ed4f7);section.addView(label);LinearLayout content=new LinearLayout(getContext());content.setOrientation(LinearLayout.VERTICAL);content.setPadding(0,dp(10),0,0);section.addView(content);body.addView(section);return content;}
    public void bind(Video value){movie=value;title.setText(movie.getName());plot.setText(movie.getDescriptionBody());
        bindPoster(movie);
        play.setText(movie.getResumeMs()>0?"Resume":"Play");
        if(movie.getPreviewBackdrop()!=null)artwork.accept(movie.getPreviewBackdrop());renderDetails();
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
        meta.setText(android.text.TextUtils.join("  ·  ",metadata));meta.setVisibility(metadata.isEmpty()?GONE:VISIBLE);
    }
    public void setTags(BaseTags value,List<ScraperTrailer> videos,List<ScraperImage> backdrops){tags=value;trailerList=videos==null?Collections.emptyList():videos;
        if(backdrops!=null&&!backdrops.isEmpty()){ScraperImage image=backdrops.get(0);java.io.File file=image.getLargeFileF();if(file!=null&&file.exists()){artwork.accept(Uri.fromFile(file));if(movie!=null)movie.setPreviewBackdrop(Uri.fromFile(file).toString());}else if(image.getLargeUrl()!=null)artwork.accept(Uri.parse(image.getLargeUrl()));}
        portraits.clear();cast.removeAllViews();HorizontalScrollView scroll=new HorizontalScrollView(getContext());scroll.setSmoothScrollingEnabled(false);scroll.setHorizontalScrollBarEnabled(false);LinearLayout people=new LinearLayout(getContext());scroll.addView(people);cast.addView(scroll);
        if(tags!=null){for(Map.Entry<String,String> person:tags.getActors().entrySet())person(people,person.getKey(),person.getValue());
            if(tags.getDirectorsFormatted()!=null&&!tags.getDirectorsFormatted().isEmpty())person(people,tags.getDirectorsFormatted(),"Director");}
        ((View)cast.getParent()).setVisibility(people.getChildCount()==0?GONE:VISIBLE);
        trailer.setVisibility(primaryTrailer()==null?View.GONE:View.VISIBLE);
        PreviewPeople.load(getContext().getApplicationContext(),tags,new HashMap<>(portraits));renderDetails();renderRelated();
    }
    public void setSnapshot(Snapshot value){snapshot=value;renderRelated();}
    private void renderDetails(){observeActions();if(movie==null&&show==null)return;renderHumanMetadata();details.removeAllViews();
        LinearLayout row=new LinearLayout(getContext());row.setOrientation(LinearLayout.VERTICAL);details.addView(row);
        String genre=tags instanceof VideoTags?((VideoTags)tags).getGenresFormatted():"";
        if(safe(genre).isEmpty()&&tags instanceof EpisodeTags){ShowTags parent=((EpisodeTags)tags).getShowTags();if(parent!=null)genre=parent.getGenresFormatted();}
        String studio=tags instanceof VideoTags?((VideoTags)tags).getStudiosFormatted():"";
        float rating=movie instanceof Episode?((Episode)movie).getEpisodeRating():movie instanceof Movie?((Movie)movie).getRating():show==null?0:show.getRating();
        if(movie instanceof Episode&&rating<=0&&tags instanceof EpisodeTags)rating=tags.getRating();
        List<String> contextParts=new ArrayList<>();if(!safe(genre).isEmpty())contextParts.add(genre);if(rating>0)contextParts.add(String.format(Locale.UK,"TMDb %.1f / 10",rating));context.setText(android.text.TextUtils.join("  ·  ",contextParts));context.setVisibility(contextParts.isEmpty()?GONE:VISIBLE);
        pills.removeAllViews();if(movie!=null){if(movie.hasMeasured4K())pill("4K");pill(PreviewMediaInfo.format(movie.getCalculatedVideoFormat()));pill(PreviewMediaInfo.format(movie.getCalculatedBestAudioFormat()));
        }
        ((View)details.getParent()).setVisibility(row.getChildCount()==0?GONE:VISIBLE);
    }
    private void observeActions(){ObjectAdapter next=actions.get();if(next!=observedActions){if(observedActions!=null)observedActions.unregisterObserver(actionObserver);observedActions=next;if(next!=null)next.registerObserver(actionObserver);}renderProviders();}
    private void renderProviders(){
        View focused=providerActions.findFocus();Object focusKey=focused==null?null:focused.getTag();
        for(Presenter.ViewHolder holder:providerHolders)providerPresenter.onUnbindViewHolder(holder);providerHolders.clear();providerActions.removeAllViews();if(observedActions==null){if(focused!=null)play.requestFocus();return;}
        for(int i=0;i<observedActions.size();i++){Object item=observedActions.get(i);if(!(item instanceof Action)||!com.archos.mediacenter.video.streaming.StreamingActions.isAvailableOffer((Action)item))continue;Action offer=(Action)item;Presenter.ViewHolder holder=providerPresenter.onCreateViewHolder(providerActions);providerPresenter.onBindViewHolder(holder,offer);holder.view.setTag(offer.getId());holder.view.setOnClickListener(v->action.accept(offer));LinearLayout.LayoutParams lp=new LinearLayout.LayoutParams(dp(64),dp(38));lp.leftMargin=dp(10);providerActions.addView(holder.view,lp);providerHolders.add(holder);}
        if(focused!=null){View replacement=focusKey==null?null:providerActions.findViewWithTag(focusKey);if(replacement!=null)replacement.requestFocus();else if(providerActions.getChildCount()>0)providerActions.getChildAt(0).requestFocus();else play.requestFocus();}
    }
    private void pill(String value){if(value==null||value.isEmpty())return;TextView label=text(value,10);label.setSingleLine(true);label.setPadding(dp(6),dp(3),dp(6),dp(3));GradientDrawable badge=new GradientDrawable();badge.setColor(0x50142634);badge.setCornerRadius(dp(3));badge.setStroke(dp(1),0xff648196);label.setBackground(badge);LinearLayout.LayoutParams lp=new LinearLayout.LayoutParams(-2,-2);lp.rightMargin=dp(6);pills.addView(label,lp);}

    private void person(LinearLayout people,String name,String role){
        LinearLayout card=new LinearLayout(getContext());card.setOrientation(android.widget.LinearLayout.HORIZONTAL);card.setGravity(Gravity.CENTER_VERTICAL);card.setBackgroundResource(com.archos.mediacenter.video.R.drawable.preview_surface_focus);card.setFocusable(true);card.setFocusableInTouchMode(true);card.setPadding(dp(6),dp(6),dp(6),dp(8));
        // The existing scraper stores names/roles but no portrait URLs. Use the real Nova asset.
        ImageView portrait=new ImageView(getContext());portrait.setImageResource(com.archos.mediacenter.video.R.drawable.preview_person);portraits.put(name,portrait);portrait.setScaleType(ImageView.ScaleType.CENTER_CROP);portrait.setClipToOutline(true);portrait.setOutlineProvider(new ViewOutlineProvider(){@Override public void getOutline(View view,android.graphics.Outline outline){outline.setOval(0,0,view.getWidth(),view.getHeight());}});card.addView(portrait,new LinearLayout.LayoutParams(dp(42),dp(42)));
        LinearLayout words=new LinearLayout(getContext());words.setOrientation(android.widget.LinearLayout.VERTICAL);words.setPadding(dp(8),0,0,0);card.addView(words,new LinearLayout.LayoutParams(0,-2,1));
        TextView label=text(name,12);label.setMaxLines(2);label.setEllipsize(android.text.TextUtils.TruncateAt.END);words.addView(label);TextView detail=text(role==null?"":role,10);detail.setMaxLines(1);detail.setTextColor(0xffa4b6c7);words.addView(detail);card.setContentDescription(name+" "+safe(role));LinearLayout.LayoutParams lp=new LinearLayout.LayoutParams(dp(165),dp(62));lp.rightMargin=dp(10);people.addView(card,lp);
    }
    private void bindPoster(Base value){
        com.squareup.picasso.Picasso.get().cancelRequest(poster);poster.setImageDrawable(null);
        if(value.getPosterUri()!=null)com.squareup.picasso.Picasso.get().load(value.getPosterUri()).resize(dp(142),dp(213)).centerInside().noFade().into(poster);
        poster.setContentDescription(value.getName());
    }
    public void bindShow(Tvshow value,Runnable playAction){show=value;showPlay=playAction;bindPoster(value);title.setText(value.getName());plot.setText(value.getPlot());meta.setText((value.getYear()>0?value.getYear()+" · ":"")+value.getSeasonCount()+(value.getSeasonCount()==1?" season":" seasons")+" · "+value.getEpisodeCount()+(value.getEpisodeCount()==1?" episode":" episodes"));renderDetails();}
    public void setSeason(int number,List<Episode> values){
        View focused=findFocus();Object old=focused==null?null:focused.getTag();int y=getScrollY();seasons.put(number,values);episodes.removeAllViews();((View)episodes.getParent()).setVisibility(VISIBLE);
        for(Map.Entry<Integer,List<Episode>> season:seasons.entrySet()){
            episodes.addView(text(season.getKey()==0?"Specials":"Season "+season.getKey(),16));HorizontalScrollView scroll=new HorizontalScrollView(getContext());scroll.setSmoothScrollingEnabled(false);LinearLayout row=new LinearLayout(getContext());scroll.addView(row);episodes.addView(scroll);
            for(Episode e:season.getValue()){Presenter.ViewHolder h=presenter.onCreateViewHolder(row);presenter.onBindViewHolder(h,e);h.view.setTag("episode:"+e.getId());h.view.setContentDescription("S"+e.getSeasonNumber()+" E"+e.getEpisodeNumber()+" · "+e.getEpisodeName());LinearLayout.LayoutParams lp=new LinearLayout.LayoutParams(dp(172),dp(105));lp.setMargins(0,dp(6),dp(12),dp(12));row.addView(h.view,lp);h.view.setOnKeyListener((v,key,event)->{if(event.getAction()!=KeyEvent.ACTION_DOWN||key!=KeyEvent.KEYCODE_DPAD_LEFT&&key!=KeyEvent.KEYCODE_DPAD_RIGHT)return false;int index=row.indexOfChild(v),next=index+(key==KeyEvent.KEYCODE_DPAD_LEFT?-1:1);if(next>=0&&next<row.getChildCount()){View target=row.getChildAt(next);target.requestFocus();android.graphics.Rect rect=new android.graphics.Rect();target.getDrawingRect(rect);target.requestRectangleOnScreen(rect,true);}return true;});h.view.setOnClickListener(v->new VideoViewClickedListener((Activity)getContext()).onItemClicked(h,e,null,null));if(old!=null&&old.equals(h.view.getTag()))h.view.requestFocus();}
        }
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
    private void renderRelated(){for(Presenter.ViewHolder h:cards)presenter.onUnbindViewHolder(h);cards.clear();related.removeAllViews();if(movie==null||snapshot==null||tags==null)return;
        Entry seed=new Entry(movie,0,0,tags instanceof VideoTags?((VideoTags)tags).getGenresFormatted():"");
        HorizontalScrollView scroll=new HorizontalScrollView(getContext());scroll.setSmoothScrollingEnabled(false);LinearLayout row=new LinearLayout(getContext());scroll.addView(row);related.addView(scroll);
        int count=0;for(Entry e:PreviewDiscovery.similar(seed,snapshot)){if(!(e.media instanceof Movie))continue;if(count++==12)break;
            Presenter.ViewHolder h=presenter.onCreateViewHolder(row);presenter.onBindViewHolder(h,e.media);cards.add(h);LinearLayout.LayoutParams lp=new LinearLayout.LayoutParams(dp(172),-2);lp.rightMargin=dp(12);row.addView(h.view,lp);h.view.setOnClickListener(v->new VideoViewClickedListener((Activity)getContext()).onItemClicked(h,e.media,null,null));}
        ((View)related.getParent()).setVisibility(count==0?GONE:VISIBLE);
    }
    public void play(){if(showPlay!=null){showPlay.run();return;}ObjectAdapter adapter=actions.get();if(adapter==null)return;
        for(int id:new int[]{VideoActionAdapter.ACTION_RESUME,VideoActionAdapter.ACTION_LOCAL_RESUME,VideoActionAdapter.ACTION_PLAY,VideoActionAdapter.ACTION_PLAY_FROM_BEGIN,VideoActionAdapter.ACTION_REMOTE_RESUME})
            for(int i=0;i<adapter.size();i++){Object item=adapter.get(i);if(item instanceof Action&&((Action)item).getId()==id){action.accept((Action)item);return;}}
    }
    private void more(){ObjectAdapter adapter=actions.get();if(adapter==null)return;List<Action> items=new ArrayList<>();List<String> labels=new ArrayList<>();
        List<Action> source=new ArrayList<>();for(int i=0;i<adapter.size();i++){Object value=adapter.get(i);if(value instanceof Action){Action a=(Action)value;if(show!=null&&a.getId()==com.archos.mediacenter.video.leanback.tvshow.TvshowActionAdapter.ACTION_MORE_DETAILS)continue;source.add(a);}}
        for(int group=0;group<3;group++){boolean heading=false;for(Action a:source){String label=String.valueOf(a.getLabel1())+(a.getLabel2()==null?"":" — "+a.getLabel2());String lower=label.toLowerCase(Locale.ROOT);int category=lower.contains("delete")||lower.contains("remove")?2:lower.contains("play")||lower.contains("resume")||lower.contains("episode")?0:1;if(category!=group)continue;if(!heading){items.add(null);labels.add(group==0?"— Playback & navigation":group==1?"— Library":"— Remove / delete");heading=true;}items.add(a);labels.add(label);}}
        final int synopsisIndex=items.size();items.add(null);labels.add("Full synopsis");
        if(show==null){items.add(null);labels.add("— File & media");labels.add("File, subtitles and artwork");}
        PreviewDialog.choose(getContext(),"More",labels.toArray(new String[0]),-1,n->{if(n==synopsisIndex)PreviewDialog.read(getContext(),title.getText().toString(),show!=null?safe(show.getPlot()):movie==null?"":safe(movie.getDescriptionBody()));else if(n==items.size())nativeDetails.run();else if(items.get(n)!=null)action.accept(items.get(n));});
    }
    private ScraperTrailer primaryTrailer(){
        ScraperTrailer best=null;int score=0;for(ScraperTrailer t:trailerList){if(!"YouTube".equals(t.mSite)||t.mVideoKey==null||!t.mVideoKey.matches("[A-Za-z0-9_-]{11}"))continue;String name=t.mName==null?"":t.mName.toLowerCase(Locale.ROOT);if(!name.contains("trailer")||name.contains("fan")||name.contains("reaction"))continue;int rank=(name.contains("official")?4:1)+(name.contains("main")?2:0)+(name.contains("teaser")?-1:0);if(rank>score){score=rank;best=t;}}return best;
    }
    private void chooseTrailer(){ScraperTrailer t=primaryTrailer();if(t!=null)PreviewTrailer.show((Activity)getContext(),t);}
    @Override protected void onDetachedFromWindow(){if(observedActions!=null){observedActions.unregisterObserver(actionObserver);observedActions=null;}for(Presenter.ViewHolder h:providerHolders)providerPresenter.onUnbindViewHolder(h);providerHolders.clear();for(Presenter.ViewHolder h:cards)presenter.onUnbindViewHolder(h);cards.clear();super.onDetachedFromWindow();}
}
