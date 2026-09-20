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
        setFillViewport(true);setSmoothScrollingEnabled(false);setClipToPadding(false);body=new LinearLayout(c);body.setOrientation(LinearLayout.VERTICAL);body.setPadding(dp(42),dp(20),dp(42),dp(24));addView(body);
        LinearLayout composition=new LinearLayout(c);composition.setGravity(Gravity.TOP);body.addView(composition,new LinearLayout.LayoutParams(-1,-2));
        LinearLayout hero=new LinearLayout(c);hero.setOrientation(LinearLayout.VERTICAL);hero.setGravity(Gravity.TOP);composition.addView(hero,new LinearLayout.LayoutParams(0,-2,1));
        title=text("",34);title.setTypeface(null,android.graphics.Typeface.BOLD);title.setMaxLines(2);title.setGravity(Gravity.CENTER_VERTICAL);title.setEllipsize(android.text.TextUtils.TruncateAt.END);hero.addView(title,new LinearLayout.LayoutParams(dp(420),dp(86)));
        meta=text("",12);meta.setPadding(0,dp(5),0,dp(4));hero.addView(meta);
        context=text("",12);context.setTextColor(0xffb3c8d7);context.setMaxLines(2);context.setEllipsize(android.text.TextUtils.TruncateAt.END);hero.addView(context,new LinearLayout.LayoutParams(-1,-2));
        pills=new LinearLayout(c);pills.setPadding(0,dp(5),0,dp(5));hero.addView(pills);
        plot=text("",13);plot.setMaxLines(3);plot.setLineSpacing(dp(2),1);plot.setEllipsize(android.text.TextUtils.TruncateAt.END);hero.addView(plot,new LinearLayout.LayoutParams(Math.min(dp(390),getResources().getDisplayMetrics().widthPixels-dp(84)),dp(56)));
        LinearLayout buttons=new LinearLayout(c);buttons.setPadding(0,dp(12),0,dp(10));hero.addView(buttons);
        trailer=button("Play Trailer",this::chooseTrailer);trailer.setVisibility(View.GONE);buttons.addView(trailer);
        play=button("Play",this::play);margin(buttons,play);providerActions=new LinearLayout(c);buttons.addView(providerActions);
        margin(buttons,button("More Info",this::moreInfo));margin(buttons,button("Actions",this::more));
        episodes=section("Seasons & Episodes");episodes.getParent();((View)episodes.getParent()).setVisibility(GONE);cast=section("Cast & Crew");details=section("Details");related=section("More Like This — In Your Library");trailers=section("Trailers & Extras");((View)trailers.getParent()).setVisibility(GONE);
        ((View)cast.getParent()).setVisibility(GONE);((View)related.getParent()).setVisibility(GONE);
    }
    public boolean atTop(){return getScrollY()==0 && (play.hasFocus()||trailer.hasFocus()||findFocus()!=null&&String.valueOf(findFocus().getTag()).startsWith("action:"));}
    public void focusPrimary(){if(trailer.getVisibility()==VISIBLE)trailer.requestFocus();else if(play.getVisibility()==VISIBLE)play.requestFocus();else providerActions.requestFocus();}
    @Override public boolean dispatchKeyEvent(KeyEvent e){if(e.getAction()==KeyEvent.ACTION_DOWN&&e.getKeyCode()==KeyEvent.KEYCODE_DPAD_UP&&(play.hasFocus()||trailer.hasFocus())&&getScrollY()>0){smoothScrollTo(0,0);return true;}return super.dispatchKeyEvent(e);}
    private int dp(int n){return Math.round(n*getResources().getDisplayMetrics().density);}
    private TextView text(String s,int size){TextView t=new TextView(getContext());t.setText(s);t.setTextColor(Color.WHITE);t.setTextSize(size);return t;}
    private GradientDrawable bg(boolean focus){GradientDrawable d=new GradientDrawable();d.setColor(focus?0xee254964:0xcc10283b);d.setCornerRadius(dp(5));d.setStroke(dp(focus?2:1),focus?PreviewAccent.color(getContext()):0xff284b62);return d;}
    private TextView button(String s,Runnable run){TextView t=text(s,14);t.setTag("action:"+s);PreviewIcon.apply(t,s,16);t.setGravity(Gravity.CENTER);t.setPadding(dp(14),dp(9),dp(14),dp(9));t.setFocusable(true);t.setFocusableInTouchMode(true);t.setBackground(PreviewDialog.buttonFocus(getContext()));t.setOnFocusChangeListener((v,f)->{t.setTextColor(f?PreviewAccent.color(getContext()):0xffe1e9ef);t.setShadowLayer(f?dp(5):0,0,0,PreviewAccent.color(getContext()));});t.setOnClickListener(v->run.run());return t;}
    private void margin(LinearLayout row,View view){LinearLayout.LayoutParams lp=new LinearLayout.LayoutParams(-2,-2);lp.leftMargin=dp(12);row.addView(view,lp);}
    private LinearLayout section(String name){LinearLayout section=new LinearLayout(getContext());section.setOrientation(LinearLayout.VERTICAL);section.setPadding(0,dp(14),0,dp(8));TextView label=text(name,19);label.setTextColor(0xff9ed4f7);section.addView(label);LinearLayout content=new LinearLayout(getContext());content.setOrientation(LinearLayout.VERTICAL);content.setPadding(0,dp(10),0,0);section.addView(content);body.addView(section);return content;}
    public void bind(Video value){movie=value;title.setText(movie.getName());OfficialTitleArtwork.bind(title,value,false);plot.setText(movie.getDescriptionBody());
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
        meta.setText((movie instanceof Episode?safe(((Episode)movie).getEpisodeName())+"\n":"")+android.text.TextUtils.join("  ·  ",metadata));meta.setVisibility(metadata.isEmpty()?GONE:VISIBLE);
    }
    public void setTags(BaseTags value,List<ScraperTrailer> videos,List<ScraperImage> backdrops){View oldCast=cast.findFocus();Object castKey=oldCast==null?null:oldCast.getTag();int oldY=getScrollY();tags=value;trailerList=videos==null?Collections.emptyList():videos;
        if(backdrops!=null&&!backdrops.isEmpty()){ScraperImage image=backdrops.get(0);java.io.File file=image.getLargeFileF();if(file!=null&&file.exists()){artwork.accept(Uri.fromFile(file));if(movie!=null)movie.setPreviewBackdrop(Uri.fromFile(file).toString());}else if(image.getLargeUrl()!=null)artwork.accept(Uri.parse(image.getLargeUrl()));}
        portraits.clear();cast.removeAllViews();HorizontalScrollView scroll=new HorizontalScrollView(getContext());scroll.setSmoothScrollingEnabled(false);scroll.setHorizontalScrollBarEnabled(false);LinearLayout people=new LinearLayout(getContext());scroll.addView(people);cast.addView(scroll);
        if(tags!=null){int count=0;for(Map.Entry<String,String> person:tags.getActors().entrySet()){if(count++==6)break;person(people,person.getKey(),person.getValue());}
            if(tags.getDirectorsFormatted()!=null&&!tags.getDirectorsFormatted().isEmpty())person(people,tags.getDirectorsFormatted(),"Director");
            if(!tags.getActors().isEmpty()){TextView all=button("See All",this::allPeople);all.setTag("person:all");people.addView(all,new LinearLayout.LayoutParams(dp(96),dp(92)));rowKeys(all,people);}}
        ((View)cast.getParent()).setVisibility(people.getChildCount()==0?GONE:VISIBLE);
        trailer.setVisibility(primaryTrailer()==null?View.GONE:View.VISIBLE);
        PreviewPeople.load(getContext().getApplicationContext(),tags,new HashMap<>(portraits));renderDetails();renderRelated();restoreRowFocus(cast,castKey,oldY);
    }
    public void setSnapshot(Snapshot value){snapshot=value;renderRelated();}
    private void renderDetails(){observeActions();if(movie==null&&show==null)return;renderHumanMetadata();details.removeAllViews();
        LinearLayout row=new LinearLayout(getContext());row.setOrientation(LinearLayout.VERTICAL);details.addView(row);
        String genre=tags instanceof VideoTags?((VideoTags)tags).getGenresFormatted():"";
        if(safe(genre).isEmpty()&&tags instanceof EpisodeTags){ShowTags parent=((EpisodeTags)tags).getShowTags();if(parent!=null)genre=parent.getGenresFormatted();}
        String studio=tags instanceof VideoTags?((VideoTags)tags).getStudiosFormatted():"";
        float rating=movie instanceof Episode?((Episode)movie).getEpisodeRating():movie instanceof Movie?((Movie)movie).getRating():show==null?0:show.getRating();
        if(movie instanceof Episode&&rating<=0&&tags instanceof EpisodeTags)rating=tags.getRating();
        context.setText(safe(genre));context.setVisibility(safe(genre).isEmpty()?GONE:VISIBLE);
        pills.removeAllViews();if(rating>0)pill(String.format(Locale.UK,"TMDb %.1f / 10",rating));if(movie!=null){if(movie.hasMeasured4K())pill("4K");else if(movie.getMeasuredHeight()>=1040)pill("1080p");else if(movie.getMeasuredHeight()>=720)pill("720p");
        }
        ((View)details.getParent()).setVisibility(row.getChildCount()==0?GONE:VISIBLE);
    }
    private void observeActions(){ObjectAdapter next=actions.get();if(next!=observedActions){if(observedActions!=null)observedActions.unregisterObserver(actionObserver);observedActions=next;if(next!=null)next.registerObserver(actionObserver);}renderProviders();}
    private void renderProviders(){
        View focused=providerActions.findFocus();Object focusKey=focused==null?null:focused.getTag();
        for(Presenter.ViewHolder holder:providerHolders)providerPresenter.onUnbindViewHolder(holder);providerHolders.clear();providerActions.removeAllViews();if(observedActions==null){if(focused!=null)play.requestFocus();return;}
        List<Action> offers=new ArrayList<>(),extraOptions=new ArrayList<>();for(int i=0;i<observedActions.size();i++){Object item=observedActions.get(i);if(item instanceof Action&&com.archos.mediacenter.video.streaming.StreamingActions.isAvailableOffer((Action)item))offers.add((Action)item);else if(item instanceof Action&&"•••".contentEquals(((Action)item).getLabel1()))extraOptions.add((Action)item);}
        play.setVisibility(offers.isEmpty()?VISIBLE:GONE);
        if(!offers.isEmpty()){
            Action offer=offers.get(0);Presenter.ViewHolder holder=providerPresenter.onCreateViewHolder(providerActions);providerPresenter.onBindViewHolder(holder,offer);holder.view.setTag(offer.getId());holder.view.setOnClickListener(v->action.accept(offer));LinearLayout.LayoutParams lp=new LinearLayout.LayoutParams(dp(160),dp(38));lp.leftMargin=dp(10);providerActions.addView(holder.view,lp);providerHolders.add(holder);
            margin(providerActions,button("More Options",()->{List<String> labels=new ArrayList<>();labels.add("Play library file");for(int i=1;i<offers.size();i++){Action extra=offers.get(i);labels.add(extra instanceof com.archos.mediacenter.video.streaming.StreamingActionPresenter.LogoAction?((com.archos.mediacenter.video.streaming.StreamingActionPresenter.LogoAction)extra).provider.name:String.valueOf(extra.getLabel1()));}int providerCount=labels.size();for(Action extra:extraOptions)labels.add("More Streaming Services");PreviewDialog.choose(getContext(),"Playback Options",labels.toArray(new String[0]),-1,n->{if(n==0)play();else if(n<providerCount)action.accept(offers.get(n));else action.accept(extraOptions.get(n-providerCount));});}));
        }
        if(focused!=null){View replacement=focusKey==null?null:providerActions.findViewWithTag(focusKey);if(replacement!=null)replacement.requestFocus();else if(providerActions.getChildCount()>0)providerActions.getChildAt(0).requestFocus();else play.requestFocus();}
    }
    private void pill(String value){if(value==null||value.isEmpty())return;TextView label=text(value,10);label.setSingleLine(true);label.setPadding(dp(6),dp(3),dp(6),dp(3));GradientDrawable badge=new GradientDrawable();badge.setColor(0x50142634);badge.setCornerRadius(dp(3));badge.setStroke(dp(1),0xff648196);label.setBackground(badge);LinearLayout.LayoutParams lp=new LinearLayout.LayoutParams(-2,-2);lp.rightMargin=dp(6);pills.addView(label,lp);}

    private void person(LinearLayout people,String name,String role){
        LinearLayout card=new LinearLayout(getContext());card.setOrientation(android.widget.LinearLayout.VERTICAL);card.setGravity(Gravity.TOP|Gravity.CENTER_HORIZONTAL);card.setBackgroundColor(Color.TRANSPARENT);card.setFocusable(true);card.setFocusableInTouchMode(true);card.setPadding(dp(4),dp(4),dp(4),dp(4));
        // The existing scraper stores names/roles but no portrait URLs. Use the real Nova asset.
        ImageView portrait=new ImageView(getContext());portrait.setImageResource(com.archos.mediacenter.video.R.drawable.preview_person);portraits.put(name,portrait);portrait.setScaleType(ImageView.ScaleType.CENTER_CROP);portrait.setClipToOutline(true);portrait.setOutlineProvider(new ViewOutlineProvider(){@Override public void getOutline(View view,android.graphics.Outline outline){outline.setOval(0,0,view.getWidth(),view.getHeight());}});card.addView(portrait,new LinearLayout.LayoutParams(dp(42),dp(42)));
        LinearLayout words=new LinearLayout(getContext());words.setOrientation(android.widget.LinearLayout.VERTICAL);words.setGravity(Gravity.CENTER_HORIZONTAL);words.setPadding(0,dp(6),0,0);card.addView(words,new LinearLayout.LayoutParams(-1,-2));
        TextView label=text(name,11);label.setGravity(Gravity.CENTER);label.setMaxLines(2);label.setEllipsize(android.text.TextUtils.TruncateAt.END);words.addView(label);TextView detail=text(role==null?"":role,9);detail.setMaxLines(1);detail.setTextColor(0xffa4b6c7);words.addView(detail);card.setContentDescription(name+" "+safe(role));LinearLayout.LayoutParams lp=new LinearLayout.LayoutParams(dp(96),dp(100));lp.rightMargin=dp(10);people.addView(card,lp);card.setTag("person:"+name+":"+safe(role));rowKeys(card,people);
        card.setOnFocusChangeListener((v,focused)->{label.setTextColor(focused?PreviewAccent.color(getContext()):Color.WHITE);label.setShadowLayer(focused?dp(4):0,0,0,PreviewAccent.color(getContext()));portrait.animate().alpha(focused?1f:.85f).scaleX(focused?1.06f:1).scaleY(focused?1.06f:1).setDuration(140).start();});
    }
    private void allPeople(){if(tags==null)return;StringBuilder people=new StringBuilder();if(!safe(tags.getDirectorsFormatted()).isEmpty())people.append("Director · ").append(tags.getDirectorsFormatted()).append("\n\n");if(!safe(tags.getWritersFormatted()).isEmpty())people.append("Writers · ").append(tags.getWritersFormatted()).append("\n\n");for(Map.Entry<String,String> person:tags.getActors().entrySet())people.append(person.getKey()).append(safe(person.getValue()).isEmpty()?"":" · "+person.getValue()).append('\n');PreviewDialog.read(getContext(),"Cast & Crew",people.toString());}
    public void bindShow(Tvshow value,Runnable playAction){show=value;showPlay=playAction;OfficialTitleArtwork.bind(title,value,false);title.setText(value.getName());plot.setText(value.getPlot());meta.setText((value.getYear()>0?value.getYear()+" · ":"")+value.getSeasonCount()+(value.getSeasonCount()==1?" season":" seasons")+" · "+value.getEpisodeCount()+(value.getEpisodeCount()==1?" episode":" episodes"));renderDetails();}
    public void setSeason(int number,List<Episode> values){
        View focused=findFocus();Object old=focused==null?null:focused.getTag();int y=getScrollY();seasons.put(number,values);episodes.removeAllViews();((View)episodes.getParent()).setVisibility(VISIBLE);
        for(Map.Entry<Integer,List<Episode>> season:seasons.entrySet()){
            episodes.addView(text(season.getKey()==0?"Specials":"Season "+season.getKey(),16));HorizontalScrollView scroll=new HorizontalScrollView(getContext());scroll.setSmoothScrollingEnabled(false);LinearLayout row=new LinearLayout(getContext());scroll.addView(row);episodes.addView(scroll);
            for(Episode e:season.getValue()){Presenter.ViewHolder h=presenter.onCreateViewHolder(row);presenter.onBindViewHolder(h,e);h.view.setTag("episode:"+e.getId());h.view.setContentDescription("S"+e.getSeasonNumber()+" E"+e.getEpisodeNumber()+" · "+e.getEpisodeName());LinearLayout.LayoutParams lp=new LinearLayout.LayoutParams(dp(172),dp(105));lp.setMargins(0,dp(6),dp(12),dp(12));row.addView(h.view,lp);h.view.setOnKeyListener((v,key,event)->{if(event.getAction()!=KeyEvent.ACTION_DOWN||key!=KeyEvent.KEYCODE_DPAD_LEFT&&key!=KeyEvent.KEYCODE_DPAD_RIGHT)return false;int index=row.indexOfChild(v),next=index+(key==KeyEvent.KEYCODE_DPAD_LEFT?-1:1);if(next>=0&&next<row.getChildCount()){View target=row.getChildAt(next);target.requestFocus();android.graphics.Rect rect=new android.graphics.Rect();target.getDrawingRect(rect);target.requestRectangleOnScreen(rect,true);}return true;});h.view.setOnClickListener(v->new VideoViewClickedListener((Activity)getContext()).onItemClicked(h,e,null,null));if(old!=null&&old.equals(h.view.getTag()))h.view.requestFocus();}
        }
        if(show!=null){List<Entry> all=new ArrayList<>();Snapshot cached=PreviewLibraryLoader.memoryCache();if(cached!=null)for(Entry ep:cached.episodes)if(ep.show==show.getTvshowId())all.add(ep);if(all.isEmpty())for(List<Episode> group:seasons.values())for(Episode ep:group){Entry entry=new Entry(ep,0,show.getTvshowId(),"");if(show.getShowTags()!=null)entry.onlineId=show.getShowTags().getOnlineId();all.add(entry);}PreviewSeriesJourney.Selection selected=PreviewSeriesJourney.select(getContext(),all);play.setText(selected.episode!=null&&PreviewSeriesJourney.resumable((Video)selected.episode.media)?"Resume":"Play");}
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
    private void renderRelated(){View oldFocus=related.findFocus();Object focusKey=oldFocus==null?null:oldFocus.getTag();int oldY=getScrollY();for(Presenter.ViewHolder h:cards)presenter.onUnbindViewHolder(h);cards.clear();related.removeAllViews();if(movie==null||snapshot==null||tags==null){restoreRowFocus(related,focusKey,oldY);return;}
        Entry seed=new Entry(movie,0,0,tags instanceof VideoTags?((VideoTags)tags).getGenresFormatted():"");
        HorizontalScrollView scroll=new HorizontalScrollView(getContext());scroll.setSmoothScrollingEnabled(false);LinearLayout row=new LinearLayout(getContext());scroll.addView(row);related.addView(scroll);
        int count=0;for(Entry e:PreviewDiscovery.similar(seed,snapshot)){if(!(e.media instanceof Movie))continue;if(count++==12)break;
            Presenter.ViewHolder h=presenter.onCreateViewHolder(row);presenter.onBindViewHolder(h,e.media);cards.add(h);LinearLayout.LayoutParams lp=new LinearLayout.LayoutParams(dp(172),-2);lp.rightMargin=dp(12);row.addView(h.view,lp);h.view.setTag("related:"+e.key());rowKeys(h.view,row);h.view.setOnClickListener(v->new VideoViewClickedListener((Activity)getContext()).onItemClicked(h,e.media,null,null));}
        ((View)related.getParent()).setVisibility(count==0?GONE:VISIBLE);restoreRowFocus(related,focusKey,oldY);
    }
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
            if(group==1){labels.add("Add to Row");commands.add(()->PreviewHomeRows.add(getContext(),rowEntry(),()->{}));labels.add("Full Synopsis");commands.add(()->PreviewDialog.read(getContext(),title.getText().toString(),show!=null?safe(show.getPlot()):movie==null?"":safe(movie.getDescriptionBody())));}
            if(group==3&&show==null){labels.add("File, Subtitles and Artwork");commands.add(nativeDetails);}
        }
        Dialog dialog=PreviewDialog.choose(getContext(),"Actions",labels.toArray(new String[0]),-1,n->{if(commands.get(n)!=null)commands.get(n).run();});for(Map.Entry<Integer,String> logo:logos.entrySet())com.archos.mediacenter.video.streaming.PreviewProviderIcons.bind(dialog,logo.getKey(),logo.getValue());
    }
    private ScraperTrailer primaryTrailer(){
        ScraperTrailer best=null;int score=0;for(ScraperTrailer t:trailerList){if(!"YouTube".equals(t.mSite)||t.mVideoKey==null||!t.mVideoKey.matches("[A-Za-z0-9_-]{11}"))continue;String name=t.mName==null?"":t.mName.toLowerCase(Locale.ROOT);if(!name.contains("trailer")||name.contains("fan")||name.contains("reaction"))continue;int rank=(name.contains("official")?4:1)+(name.contains("main")?2:0)+(name.contains("teaser")?-1:0);if(rank>score){score=rank;best=t;}}return best;
    }
    private void chooseTrailer(){ScraperTrailer t=primaryTrailer();if(t!=null)PreviewTrailer.show((Activity)getContext(),t);}
    @Override protected void onDetachedFromWindow(){if(observedActions!=null){observedActions.unregisterObserver(actionObserver);observedActions=null;}for(Presenter.ViewHolder h:providerHolders)providerPresenter.onUnbindViewHolder(h);providerHolders.clear();for(Presenter.ViewHolder h:cards)presenter.onUnbindViewHolder(h);cards.clear();super.onDetachedFromWindow();}
}
