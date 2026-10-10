package com.archos.mediacenter.video.leanback.search;

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.os.*;
import android.text.*;
import android.view.*;
import android.view.inputmethod.InputMethodManager;
import android.widget.*;
import androidx.recyclerview.widget.*;
import androidx.leanback.widget.Presenter;
import com.archos.mediacenter.video.browser.adapters.object.*;
import com.archos.mediacenter.video.browser.adapters.mappers.VideoCursorMapper;
import com.archos.mediacenter.video.browser.loader.*;
import com.archos.mediacenter.video.leanback.*;
import com.archos.mediaprovider.video.VideoStore;
import com.squareup.picasso.Picasso;
import com.archos.mediacenter.video.diagnostics.Diagnostics;
import com.archos.mediacenter.video.diagnostics.ArtworkRequest;
import java.util.*;
import java.util.concurrent.*;

/** Local Nova search, with recycled compact rows and no remote discovery result universe. */
public final class PreviewSearch extends LinearLayout {
 private final Activity activity;
 private final EditText query;
 private PreviewKeyboard keyboard;
 private final TextView status;
 private final PreviewFocusRecycler results;
 private final LinearLayoutManager layout;
 private final ResultAdapter adapter=new ResultAdapter();
 private final List<Result> items=new ArrayList<>();
 private final Handler main=new Handler(Looper.getMainLooper());
 private final ExecutorService worker=Executors.newSingleThreadExecutor();
 private CancellationSignal cancellation;
 private int generation;
 private long selectedId=-1;
 private boolean restoring;
 private Parcelable scrollState;
 private Runnable pending;
 private String detailsFocusToken="";
 private java.lang.ref.WeakReference<View> detailsOpener=new java.lang.ref.WeakReference<>(null);
 private boolean detailsWindowAway;
 private final int mode;
 private static class Result {Video video;Tvshow show;String genres;int rank;long showId;Result(Video video,String genres){this.video=video;this.genres=genres;}Result(Tvshow show,String genres){this.show=show;this.genres=genres;}long id(){return show==null?video.getId():-show.getTvshowId()-2;}}
 private ScrollView guidancePanel;
 public PreviewSearch(Activity activity,int mode,Bundle state){
  super(activity);Diagnostics.uiState("search","results","list","none","none",0);this.activity=activity;this.mode=mode;setOrientation(VERTICAL);setPadding(dp(28),dp(12),dp(28),dp(12));setBackgroundColor(android.graphics.Color.TRANSPARENT);
  status=text("",12);status.setGravity(Gravity.END);addView(status,new LayoutParams(-1,dp(22)));
  LinearLayout columns=new LinearLayout(activity);columns.setOrientation(HORIZONTAL);addView(columns,new LayoutParams(-1,0,1));LinearLayout keyboardColumn=new LinearLayout(activity);keyboardColumn.setOrientation(VERTICAL);TextView heading=text("Search",27);heading.setTextColor(-1);keyboardColumn.addView(heading);heading.setPadding(0,0,0,dp(10));LayoutParams keyboardSize=new LayoutParams(0,-1,.40f);keyboardSize.rightMargin=dp(24);columns.addView(keyboardColumn,keyboardSize);
  LinearLayout search=new LinearLayout(activity);search.setGravity(Gravity.CENTER_VERTICAL);search.setPadding(dp(12),0,dp(8),0);search.setBackground(PreviewDialog.surface(activity,false));ImageView icon=new ImageView(activity);icon.setImageResource(com.archos.mediacenter.video.R.drawable.preview_search);search.addView(icon,new LayoutParams(dp(20),dp(20)));
  query=new EditText(activity);query.setId(View.generateViewId());query.setHint("Search Movies and TV Shows");query.setTextColor(-1);query.setHintTextColor(0xff8da7b9);query.setTextSize(16);query.setSingleLine(true);query.setBackgroundColor(android.graphics.Color.TRANSPARENT);query.setFocusable(false);query.setCursorVisible(false);query.setLongClickable(false);query.setImeOptions(android.view.inputmethod.EditorInfo.IME_ACTION_SEARCH | android.view.inputmethod.EditorInfo.IME_FLAG_NO_EXTRACT_UI);query.setPadding(dp(12),0,dp(12),0);search.addView(query,new LayoutParams(0,dp(46),1));keyboardColumn.addView(search);query.setShowSoftInputOnFocus(false);
  results=new PreviewFocusRecycler(activity);results.setId(View.generateViewId());query.setNextFocusRightId(results.getId());layout=new LinearLayoutManager(activity);results.setLayoutManager(layout);results.setAdapter(adapter);results.setPadding(0,dp(12),0,0);results.setClipToPadding(false);FrameLayout resultArea=new FrameLayout(activity);columns.addView(resultArea,new LayoutParams(0,-1,.60f));resultArea.addView(results,new FrameLayout.LayoutParams(-1,-1));
  ScrollView help=new ScrollView(activity);guidancePanel=help;
  LinearLayout guidance=new LinearLayout(activity);guidance.setOrientation(VERTICAL);help.addView(guidance);
  String[][] features={{"movie","Your Library","Search movies, TV shows, episodes and filenames."},
      {"title","Flexible Formatting","Punctuation and opening articles are optional."},
      {"language","Original Titles","Find a title using its indexed original name."},
      {"search","Minor Typos","Small spelling differences can still match."},
      {"info","People & Studios","Search cast, crew and studios where indexed. Select a result to open its details."}};
  for(String[] feature:features){TextView title=text(feature[1],18);PreviewIcon.apply(title,feature[0],20);title.setPadding(0,dp(16),0,dp(5));guidance.addView(title);TextView explanation=text(feature[2],15);explanation.setPadding(dp(29),0,0,dp(5));guidance.addView(explanation);}
  resultArea.addView(help,new FrameLayout.LayoutParams(-1,-1));results.setVisibility(GONE);
  Diagnostics.uiRebuild(results,"search.results","adapter_created",0,adapter.getItemCount(),true);
  buildKeyboard(keyboardColumn);
  query.addTextChangedListener(new TextWatcher(){public void beforeTextChanged(CharSequence s,int start,int count,int after){}public void onTextChanged(CharSequence s,int start,int before,int count){schedule(s.toString());}public void afterTextChanged(Editable e){}});
  query.setOnEditorActionListener((v,id,event)->{hideKeyboard();if(!items.isEmpty())results.requestFocus();return true;});
  if(state!=null){selectedId=state.getLong("preview_search_selected",-1);scrollState=state.getParcelable("preview_search_scroll");restoring=state.getBoolean("preview_search_results_focused",selectedId!=-1);keyboard.restoreKey(state.getString("preview_search_key","T"));query.setText(state.getString("preview_search_query",""));} if(!restoring)keyboard.focusLastKey();
 }
 private void buildKeyboard(LinearLayout column){
  keyboard=new PreviewKeyboard(activity,query,()->{if(!items.isEmpty()){results.scrollToPosition(0);results.post(()->{RecyclerView.ViewHolder first=results.findViewHolderForAdapterPosition(0);if(first!=null)first.itemView.requestFocus();});}});column.addView(keyboard,new LayoutParams(-1,-2));keyboard.setActions(()->{if(!items.isEmpty())results.requestFocus();},()->activity.onBackPressed());
 }
 @Override public void onWindowFocusChanged(boolean focused){
  super.onWindowFocusChanged(focused);
  if(!focused&&!detailsFocusToken.isEmpty())detailsWindowAway=true;
  if(focused){hideKeyboard();if(detailsWindowAway&&!detailsFocusToken.isEmpty())main.post(()->{
   if(!isAttachedToWindow()||detailsFocusToken.isEmpty())return;
   View requested=detailsOpener.get(),restored=findFocus();
   Diagnostics.focusRestored(detailsFocusToken,requested,restored,requested!=restored,restored!=null&&results.hasFocus());
   detailsFocusToken="";detailsOpener.clear();detailsWindowAway=false;
  });}
 }
 private void recordDetailsEntry(View opener){detailsOpener=new java.lang.ref.WeakReference<>(opener);detailsFocusToken=Diagnostics.focusEntry(opener,"search.details");detailsWindowAway=false;}
 public boolean atTop(){return keyboard.atTop();}
 public void focusQuery(){keyboard.focusLastKey();}
 public void acceptVoice(String text){query.setText(text);query.setSelection(query.length());}
 public void save(Bundle state){state.putBoolean("preview_search_results_focused",results.hasFocus());state.putString("preview_search_key",keyboard.focusedKey());state.putString("preview_search_query",query.getText().toString());state.putLong("preview_search_selected",selectedId);state.putParcelable("preview_search_scroll",layout.onSaveInstanceState());}
 private void hideKeyboard(){InputMethodManager ime=(InputMethodManager)activity.getSystemService(Context.INPUT_METHOD_SERVICE);if(ime!=null)ime.hideSoftInputFromWindow(query.getWindowToken(),0);}
 private void schedule(String value){if(pending!=null)main.removeCallbacks(pending);if(cancellation!=null)cancellation.cancel();int request=++generation;String search=value.trim();if(search.isEmpty()){int previous=items.size();items.clear();results.setVisibility(GONE);guidancePanel.setVisibility(VISIBLE);adapter.notifyDataSetChanged();Diagnostics.uiRebuild(results,"search.results","query_cleared",previous,0,false);status.setText("");return;}pending=()->load(search,request);main.postDelayed(pending,250);}
 private void load(String value,int request){
  final long started=android.os.SystemClock.elapsedRealtime();final String operation=com.archos.mediacenter.video.diagnostics.Diagnostics.operation("search");
  VideoLoader loader;switch(mode){case VideoSearchActivity.SEARCH_MODE_MOVIE:SearchMovieLoader movie=new SearchMovieLoader(activity);movie.setQuery("");loader=movie;break;case VideoSearchActivity.SEARCH_MODE_EPISODE:SearchEpisodeLoader ep=new SearchEpisodeLoader(activity);ep.setQuery("");loader=ep;break;case VideoSearchActivity.SEARCH_MODE_NON_SCRAPED:SearchNonScrapedVideoLoader file=new SearchNonScrapedVideoLoader(activity);file.setQuery("");loader=file;break;default:SearchVideoLoader all=new SearchVideoLoader(activity);all.setQuery("");loader=all;}
  android.net.Uri uri=loader.getUri();String[] extra={VideoStore.Video.VideoColumns.SCRAPER_M_GENRES,VideoStore.Video.VideoColumns.SCRAPER_S_GENRES,VideoStore.Video.VideoColumns.SCRAPER_ACTORS,VideoStore.Video.VideoColumns.SCRAPER_DIRECTORS,VideoStore.Video.VideoColumns.SCRAPER_WRITERS,VideoStore.Video.VideoColumns.SCRAPER_STUDIOS,VideoStore.Video.VideoColumns.SCRAPER_E_ACTORS,VideoStore.Video.VideoColumns.SCRAPER_E_DIRECTORS,VideoStore.Video.VideoColumns.SCRAPER_E_WRITERS,VideoStore.Video.VideoColumns.SCRAPER_SHOW_ID};String[] projection=Arrays.copyOf(loader.getProjection(),loader.getProjection().length+extra.length);System.arraycopy(extra,0,projection,loader.getProjection().length,extra.length);String selection=loader.getSelection(),sort=loader.getSortOrder();String[] args=loader.getSelectionArgs();CancellationSignal signal=new CancellationSignal();cancellation=signal;status.setText("Searching library…");Context app=activity.getApplicationContext();AllTvshowsLoader showLoader=new AllTvshowsLoader(activity);android.net.Uri showUri=showLoader.getUri();String[] showProjection=showLoader.getProjection(),showArgs=showLoader.getSelectionArgs();String showSelection=showLoader.getSelection(),showSort=showLoader.getSortOrder();
  worker.execute(()->{Map<Long,String[]> movieNames=metadata(app,false,signal),showNames=metadata(app,true,signal);List<Result> found=new ArrayList<>();String error=null;try(Cursor cursor=app.getContentResolver().query(uri,projection,selection,args,sort,signal)){if(cursor!=null){VideoCursorMapper mapper=new VideoCursorMapper();mapper.bindColumns(cursor);while(cursor.moveToNext()){signal.throwIfCanceled();String name=cursor.getString(cursor.getColumnIndexOrThrow(VideoLoader.COLUMN_NAME));String episode=cursor.getString(cursor.getColumnIndexOrThrow(VideoStore.Video.VideoColumns.SCRAPER_E_NAME));String path=cursor.getString(cursor.getColumnIndexOrThrow(VideoStore.Video.VideoColumns.DATA));Video video=(Video)mapper.bind(cursor);List<String> searchable=new ArrayList<>(Arrays.asList(name,episode,path));for(String column:extra)if(!column.endsWith("genres")&&!column.equals(VideoStore.Video.VideoColumns.SCRAPER_SHOW_ID))searchable.add(cell(cursor,column));String[] local=video instanceof Movie?movieNames.get(((Movie)video).getMovieId()):video instanceof Episode?showNames.get(longCell(cursor,VideoStore.Video.VideoColumns.SCRAPER_SHOW_ID)):null;if(local!=null)Collections.addAll(searchable,local);int rank=PreviewSearchText.rank(value,searchable.toArray(new String[0]));if(rank==0)continue;String genres=cursor.getString(cursor.getColumnIndexOrThrow(video instanceof Episode?VideoStore.Video.VideoColumns.SCRAPER_S_GENRES:VideoStore.Video.VideoColumns.SCRAPER_M_GENRES));Result result=new Result(video,genres);result.rank=rank;result.showId=longCell(cursor,VideoStore.Video.VideoColumns.SCRAPER_SHOW_ID);found.add(result);}}}catch(OperationCanceledException cancelled){Diagnostics.event("search_cancelled","operation_id",operation);Diagnostics.finishOperation(operation,"search",started);return;}catch(RuntimeException failure){Diagnostics.error("search_failed",failure);error="Library search is temporarily unavailable";}if(mode!=VideoSearchActivity.SEARCH_MODE_MOVIE&&mode!=VideoSearchActivity.SEARCH_MODE_NON_SCRAPED){List<Result> parents=new ArrayList<>();try(Cursor shows=app.getContentResolver().query(showUri,showProjection,showSelection,showArgs,showSort,signal)){if(shows!=null){com.archos.mediacenter.video.browser.adapters.mappers.TvshowCursorMapper mapper=new com.archos.mediacenter.video.browser.adapters.mappers.TvshowCursorMapper();mapper.bindColumns(shows);while(shows.moveToNext()){signal.throwIfCanceled();Tvshow show=(Tvshow)mapper.bind(shows);List<String> names=new ArrayList<>();names.add(show.getName());names.add(show.getActors());names.add(show.getStudio());String[] local=showNames.get(show.getTvshowId());if(local!=null)Collections.addAll(names,local);int rank=PreviewSearchText.rank(value,names.toArray(new String[0]));for(Result result:found)if(result.showId==show.getTvshowId())rank=Math.max(rank,result.rank);if(rank>0){Result result=new Result(show,"");result.rank=rank;parents.add(result);}}}}catch(OperationCanceledException cancelled){Diagnostics.event("search_cancelled","operation_id",operation);Diagnostics.finishOperation(operation,"search",started);return;}catch(RuntimeException failure){Diagnostics.error("search_series_failed",failure);}found.addAll(0,parents);}found.sort(Comparator.comparingInt((Result item)->item.rank).reversed().thenComparing(item->item.show==null?item.video.getName():item.show.getName(),String.CASE_INSENSITIVE_ORDER));String message=error;com.archos.mediacenter.video.diagnostics.Diagnostics.finishOperation(operation,"search",started);com.archos.mediacenter.video.diagnostics.Diagnostics.event("search_results","operation_id",operation,"count",found.size());main.post(()->{if(request!=generation||!isAttachedToWindow())return;boolean focusResults=results.hasFocus();int position=0;if(selectedId!=-1)for(int i=0;i<found.size();i++)if(found.get(i).id()==selectedId){position=i;break;}int previous=items.size();items.clear();items.addAll(found);results.setVisibility(items.isEmpty()?GONE:VISIBLE);guidancePanel.setVisibility(items.isEmpty()?VISIBLE:GONE);adapter.notifyDataSetChanged();Diagnostics.uiRebuild(results,"search.results","query_result",previous,items.size(),false);status.setText(message!=null?message:found.isEmpty()?"No matching library titles":found.size()+" results · In your library");if(scrollState!=null){layout.onRestoreInstanceState(scrollState);scrollState=null;}if((restoring||focusResults)&&!items.isEmpty()){final int target=position;results.scrollToPosition(target);results.post(()->{RecyclerView.ViewHolder h=results.findViewHolderForAdapterPosition(target);if(h!=null)h.itemView.requestFocus();});}restoring=false;});});
 }
 @Override public boolean dispatchKeyEvent(KeyEvent e){if(e.getAction()==KeyEvent.ACTION_DOWN&&e.getKeyCode()==KeyEvent.KEYCODE_DPAD_LEFT&&results.hasFocus()){keyboard.focusLastKey();return true;}if(e.getAction()==KeyEvent.ACTION_DOWN&&e.getKeyCode()==KeyEvent.KEYCODE_DPAD_UP&&results.hasFocus()){View focus=results.findFocus(),row=focus==null?null:results.findContainingItemView(focus);if(row!=null&&results.getChildAdapterPosition(row)==0){keyboard.focusLastKey();return true;}}return super.dispatchKeyEvent(e);}
 @Override protected void onDetachedFromWindow(){main.removeCallbacksAndMessages(null);if(cancellation!=null)cancellation.cancel();worker.shutdownNow();super.onDetachedFromWindow();}
 private static long longCell(Cursor cursor,String column){int index=cursor.getColumnIndex(column);return index<0?-1:cursor.getLong(index);}
 private static String cell(Cursor cursor,String column){int index=cursor.getColumnIndex(column);return index<0||cursor.isNull(index)?"":cursor.getString(index);}
 private static Map<Long,String[]> metadata(Context context,boolean shows,CancellationSignal cancellation){
  Map<Long,String[]> result=new HashMap<>();String[] columns=shows?new String[]{"_id","original_title_show"}:new String[]{"_id","original_title_movie"};
  android.net.Uri uri=shows?com.archos.mediaprovider.video.ScraperStore.Show.URI.ALL:com.archos.mediaprovider.video.ScraperStore.Movie.URI.ALL;
  try(Cursor cursor=context.getContentResolver().query(uri,columns,null,null,null,cancellation)){if(cursor!=null)while(cursor.moveToNext()){String[] values=new String[columns.length-1];for(int i=1;i<columns.length;i++)values[i-1]=cell(cursor,columns[i]);result.put(cursor.getLong(cursor.getColumnIndexOrThrow("_id")),values);}}
  catch(OperationCanceledException cancelled){return result;}catch(RuntimeException unavailable){Diagnostics.error("search_indexed_metadata_unavailable",unavailable);}return result;
 }
 private int dp(int n){return PreviewDialog.dp(activity,n);}
 private TextView text(String s,int size){TextView t=new TextView(activity);t.setText(s);t.setTextSize(size);t.setTextColor(0xffc0d3df);t.setSingleLine(true);t.setEllipsize(TextUtils.TruncateAt.END);return t;}
 private class Holder extends RecyclerView.ViewHolder {ImageView image;TextView title,metadata,genres,badge,action;Holder(LinearLayout row){super(row);image=new ImageView(activity);image.setScaleType(ImageView.ScaleType.FIT_CENTER);row.addView(image,new LayoutParams(dp(92),dp(65)));LinearLayout labels=new LinearLayout(activity);labels.setOrientation(VERTICAL);labels.setPadding(dp(12),0,dp(12),0);badge=text("Top Result",11);badge.setTextColor(0xff9eb2c0);labels.addView(badge);title=text("",18);title.setTextColor(-1);metadata=text("",12);genres=text("",12);labels.addView(title);labels.addView(metadata);labels.addView(genres);row.addView(labels,new LayoutParams(0,-2,1));action=text("View Show",13);action.setGravity(Gravity.CENTER);action.setDuplicateParentStateEnabled(true);action.setBackgroundColor(android.graphics.Color.TRANSPARENT);action.setPadding(dp(8),dp(7),dp(8),dp(7));row.addView(action,new LayoutParams(dp(104),dp(38)));}}
 private class ResultAdapter extends RecyclerView.Adapter<Holder>{
  public int getItemCount(){return items.size();}
  public int getItemViewType(int position){return items.get(position).show==null?0:1;}
  public Holder onCreateViewHolder(ViewGroup parent,int type){LinearLayout row=new LinearLayout(activity);row.setGravity(Gravity.CENTER_VERTICAL);row.setPadding(dp(6),dp(5),dp(6),dp(5));row.setFocusable(true);row.setFocusableInTouchMode(true);row.setDescendantFocusability(FOCUS_BLOCK_DESCENDANTS);row.setBackgroundColor(0x5506121d);row.setForeground(PreviewDialog.focus(activity));RecyclerView.LayoutParams lp=new RecyclerView.LayoutParams(-1,dp(type==1?150:72));lp.bottomMargin=dp(8);row.setLayoutParams(lp);Holder holder=new Holder(row);holder.badge.setVisibility(type==1?VISIBLE:GONE);holder.action.setVisibility(type==1?VISIBLE:GONE);if(type==1){holder.image.setLayoutParams(new LayoutParams(dp(98),dp(140)));holder.genres.setSingleLine(false);holder.genres.setMaxLines(2);}return holder;}
  public void onBindViewHolder(Holder h,int position){Result r=items.get(position);Diagnostics.semantic(h.itemView,"search.result."+(r.show==null?"video.":"series.")+(r.show==null?r.video.getId():r.show.getTvshowId()));if(r.show!=null){Tvshow show=r.show;h.title.setText(show.getName());h.metadata.setText((show.getYear()>0?show.getYear()+" · ":"")+"TV Series");h.genres.setText(show.getPlot());h.genres.setVisibility(TextUtils.isEmpty(show.getPlot())?GONE:VISIBLE);ArtworkRequest.cancel(h.image);h.image.setImageDrawable(null);if(show.getPosterUri()!=null)ArtworkRequest.load(h.image,show.getPosterUri(),show.getTvshowId(),"search.results","poster",Picasso.get().load(show.getPosterUri()).resize(dp(98),dp(140)).centerInside().noFade());h.itemView.setOnFocusChangeListener((v,focused)->{if(focused){selectedId=r.id();Diagnostics.focusedMedia(show.getTvshowId());}});h.itemView.setOnClickListener(v->{selectedId=r.id();hideKeyboard();recordDetailsEntry(v);VideoViewClickedListener.showTvshowDetails(activity,show,new Presenter.ViewHolder(v));});return;}Video video=r.video;h.title.setText(video instanceof Episode?((Episode)video).getShowName()+" · "+((Episode)video).getEpisodeName():video.getName());List<String> meta=new ArrayList<>();if(video instanceof Movie){Movie m=(Movie)video;if(m.getYear()>0)meta.add(String.valueOf(m.getYear()));if(m.getContentRating()!=null&&!m.getContentRating().isEmpty())meta.add(m.getContentRating());}else if(video instanceof Episode){Episode e=(Episode)video;meta.add("S"+e.getSeasonNumber()+" E"+e.getEpisodeNumber());}meta.add(com.archos.mediacenter.video.leanback.PreviewVariants.label(video));if(video.getDurationMs()>0)meta.add(video.getDurationMs()/60000+" min");h.metadata.setText(TextUtils.join("  ·  ",meta));h.genres.setText(r.genres==null?"":r.genres);h.genres.setVisibility(TextUtils.isEmpty(r.genres)?GONE:VISIBLE);ArtworkRequest.cancel(h.image);h.image.setImageDrawable(null);android.net.Uri art=video instanceof Episode?((Episode)video).getPictureUri():video.getPosterUri();if(art!=null)ArtworkRequest.load(h.image,art,video.getId(),"search.results",video instanceof Episode?"episode_still":"poster",Picasso.get().load(art).resize(dp(92),dp(65)).centerInside().noFade());h.itemView.setOnFocusChangeListener((v,focused)->{if(focused){selectedId=video.getId();Diagnostics.focusedMedia(video.getId());}});h.itemView.setOnClickListener(v->{selectedId=video.getId();hideKeyboard();recordDetailsEntry(v);VideoViewClickedListener.showVideoDetails(activity,video,new Presenter.ViewHolder(v),true,-1);});}
  public void onViewRecycled(Holder h){ArtworkRequest.cancel(h.image);h.image.setImageDrawable(null);}
 }
}
