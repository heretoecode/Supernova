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
    private final TextView title,meta,plot,play,trailer;
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
        LinearLayout hero=new LinearLayout(c);hero.setOrientation(LinearLayout.VERTICAL);hero.setGravity(Gravity.CENTER_VERTICAL);
        body.addView(hero,new LinearLayout.LayoutParams(-1,dp(255)));
        title=text("",34);title.setTypeface(null,android.graphics.Typeface.BOLD);title.setMaxLines(2);hero.addView(title,new LinearLayout.LayoutParams(dp(530),-2));
        meta=text("",13);meta.setPadding(0,dp(10),0,dp(10));hero.addView(meta);
        plot=text("",14);plot.setMaxLines(4);plot.setEllipsize(android.text.TextUtils.TruncateAt.END);hero.addView(plot,new LinearLayout.LayoutParams(dp(480),-2));
        LinearLayout buttons=new LinearLayout(c);buttons.setPadding(0,dp(16),0,0);hero.addView(buttons);
        play=button("▶  Play",this::play);buttons.addView(play);
        trailer=button("Trailer",this::chooseTrailer);trailer.setVisibility(View.GONE);margin(buttons,trailer);
        margin(buttons,button("More  ▾",this::more));TextView add=button("Add to List · Coming soon",()->{});add.setEnabled(false);add.setFocusable(false);add.setAlpha(.45f);margin(buttons,add);
        episodes=section("Seasons & Episodes");episodes.getParent();((View)episodes.getParent()).setVisibility(GONE);cast=section("Cast & Crew");details=section("Details");related=section("More Like This — In Your Library");trailers=section("Trailers & Extras");((View)trailers.getParent()).setVisibility(GONE);
        ((View)cast.getParent()).setVisibility(GONE);((View)related.getParent()).setVisibility(GONE);
    }
    public boolean atTop(){return getScrollY()==0 && (play.hasFocus()||trailer.hasFocus()||findFocus()!=null&&"more".equals(findFocus().getTag()));}
    public void focusPrimary(){play.requestFocus();}
    @Override public boolean dispatchKeyEvent(KeyEvent e){if(e.getAction()==KeyEvent.ACTION_DOWN&&e.getKeyCode()==KeyEvent.KEYCODE_DPAD_UP&&(play.hasFocus()||trailer.hasFocus())&&getScrollY()>0){smoothScrollTo(0,0);return true;}return super.dispatchKeyEvent(e);}
    private int dp(int n){return Math.round(n*getResources().getDisplayMetrics().density);}
    private TextView text(String s,int size){TextView t=new TextView(getContext());t.setText(s);t.setTextColor(Color.WHITE);t.setTextSize(size);return t;}
    private GradientDrawable bg(boolean focus){GradientDrawable d=new GradientDrawable();d.setColor(focus?0xee254964:0xcc10283b);d.setCornerRadius(dp(5));d.setStroke(dp(focus?2:1),focus?0xff62bbf3:0xff284b62);return d;}
    private TextView button(String s,Runnable run){TextView t=text(s,14);if(s.startsWith("More"))t.setTag("more");t.setGravity(Gravity.CENTER);t.setPadding(dp(18),dp(10),dp(18),dp(10));t.setFocusable(true);t.setFocusableInTouchMode(true);t.setBackground(bg(false));t.setOnFocusChangeListener((v,f)->v.setBackground(bg(f)));t.setOnClickListener(v->run.run());return t;}
    private void margin(LinearLayout row,View view){LinearLayout.LayoutParams lp=new LinearLayout.LayoutParams(-2,-2);lp.leftMargin=dp(12);row.addView(view,lp);}
    private LinearLayout section(String name){LinearLayout section=new LinearLayout(getContext());section.setOrientation(LinearLayout.VERTICAL);section.setPadding(0,dp(14),0,dp(8));TextView label=text(name,19);label.setTextColor(0xff9ed4f7);section.addView(label);LinearLayout content=new LinearLayout(getContext());content.setOrientation(LinearLayout.VERTICAL);content.setPadding(0,dp(10),0,0);section.addView(content);body.addView(section);return content;}
    public void bind(Video value){movie=value;title.setText(movie.getName());plot.setText(movie.getDescriptionBody());
        String m=(movie instanceof Movie&&((Movie)movie).getYear()>0?((Movie)movie).getYear()+"   ":"")+(movie.getDurationMs()>0?movie.getDurationMs()/60000+" min   ":"")+(movie instanceof Movie?safe(((Movie)movie).getContentRating()):"");
        if(movie.hasMeasured4K())m+="   4K";
        if(PreviewMediaInfo.format(movie.getCalculatedVideoFormat())!=null)m+="   "+PreviewMediaInfo.format(movie.getCalculatedVideoFormat());
        if(PreviewMediaInfo.format(movie.getCalculatedBestAudioFormat())!=null)m+="   "+PreviewMediaInfo.format(movie.getCalculatedBestAudioFormat());meta.setText(m);
        play.setText(movie.getResumeMs()>0?"▶  Resume":"▶  Play");
        if(movie.getPreviewBackdrop()!=null)artwork.accept(movie.getPreviewBackdrop());renderDetails();
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
    private void renderDetails(){if(movie==null&&show==null)return;details.removeAllViews();
        LinearLayout row=new LinearLayout(getContext());row.setOrientation(LinearLayout.VERTICAL);details.addView(row);
        String genre=tags instanceof VideoTags?((VideoTags)tags).getGenresFormatted():"";
        String release=tags instanceof MovieTags?((MovieTags)tags).getReleaseDate():"";
        String studio=tags instanceof VideoTags?((VideoTags)tags).getStudiosFormatted():"";
        panel(row,"Genre",genre,"Release date",release);
        float rating=movie instanceof Movie?((Movie)movie).getRating():show==null?0:show.getRating();
        panel(row,"Studio",studio,"Rating",rating>0?String.format(Locale.UK,"TMDb %.1f / 10",rating):"");
        if(movie!=null){panel(row,"Source",source(movie.getFileUri()),"File",shortFile(movie.getFilenameNonCryptic()));panel(row,"Video",PreviewMediaInfo.format(movie.getCalculatedVideoFormat()),"Audio",PreviewMediaInfo.format(movie.getCalculatedBestAudioFormat()));}
    }
    private void panel(LinearLayout row,String a,String av,String b,String bv){String value="";if(av!=null&&!av.isEmpty())value=a+"\n"+av;if(bv!=null&&!bv.isEmpty())value+=(value.isEmpty()?"":"\n\n")+b+"\n"+bv;if(!value.isEmpty())info(row,"",value);}
    private void person(LinearLayout people,String name,String role){
        LinearLayout card=new LinearLayout(getContext());card.setOrientation(1);card.setBackgroundResource(com.archos.mediacenter.video.R.drawable.preview_surface_focus);card.setFocusable(true);card.setFocusableInTouchMode(true);card.setPadding(dp(6),dp(6),dp(6),dp(8));
        // The existing scraper stores names/roles but no portrait URLs. Use the real Nova asset.
        ImageView portrait=new ImageView(getContext());portrait.setImageResource(com.archos.mediacenter.video.R.drawable.preview_person);portraits.put(name,portrait);portrait.setScaleType(ImageView.ScaleType.FIT_CENTER);card.addView(portrait,new LinearLayout.LayoutParams(-1,dp(90)));
        TextView label=text(name,12);label.setMaxLines(2);label.setEllipsize(android.text.TextUtils.TruncateAt.END);card.addView(label);TextView detail=text(role==null?"":role,10);detail.setMaxLines(1);detail.setTextColor(0xffa4b6c7);card.addView(detail);card.setContentDescription(name+" "+safe(role));LinearLayout.LayoutParams lp=new LinearLayout.LayoutParams(dp(105),dp(143));lp.rightMargin=dp(10);people.addView(card,lp);
    }
    public void bindShow(Tvshow value,Runnable playAction){show=value;showPlay=playAction;title.setText(value.getName());plot.setText(value.getPlot());meta.setText((value.getYear()>0?value.getYear()+" · ":"")+value.getSeasonCount()+(value.getSeasonCount()==1?" season":" seasons")+" · "+value.getEpisodeCount()+(value.getEpisodeCount()==1?" episode":" episodes"));renderDetails();}
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
    private void info(LinearLayout row,String name,String value){TextView t=text(value.replace("\n\n","    ·    ").replace("\n",": "),13);t.setMaxLines(3);t.setEllipsize(android.text.TextUtils.TruncateAt.END);t.setPadding(dp(14),dp(10),dp(14),dp(10));t.setBackground(bg(false));LinearLayout.LayoutParams lp=new LinearLayout.LayoutParams(-1,-2);lp.bottomMargin=dp(7);row.addView(t,lp);}
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
        if(show==null){items.add(null);labels.add("— File & media");labels.add("File, subtitles and artwork");}
        PreviewDialog.choose(getContext(),"More",labels.toArray(new String[0]),-1,n->{if(n==items.size())nativeDetails.run();else if(items.get(n)!=null)action.accept(items.get(n));});
    }
    private ScraperTrailer primaryTrailer(){
        ScraperTrailer best=null;int score=0;for(ScraperTrailer t:trailerList){if(!"YouTube".equals(t.mSite)||t.mVideoKey==null||!t.mVideoKey.matches("[A-Za-z0-9_-]{11}"))continue;String name=t.mName==null?"":t.mName.toLowerCase(Locale.ROOT);if(!name.contains("trailer")||name.contains("fan")||name.contains("reaction"))continue;int rank=(name.contains("official")?4:1)+(name.contains("main")?2:0)+(name.contains("teaser")?-1:0);if(rank>score){score=rank;best=t;}}return best;
    }
    private void chooseTrailer(){ScraperTrailer t=primaryTrailer();if(t!=null)PreviewTrailer.show((Activity)getContext(),t);}
    @Override protected void onDetachedFromWindow(){for(Presenter.ViewHolder h:cards)presenter.onUnbindViewHolder(h);cards.clear();super.onDetachedFromWindow();}
}
