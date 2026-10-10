package com.archos.mediacenter.video.leanback;
import android.os.Bundle;
import android.content.Intent;
import android.database.Cursor;
import android.view.*;
import android.widget.*;
import androidx.recyclerview.widget.*;
import androidx.leanback.widget.Presenter;
import com.archos.mediacenter.video.leanback.PreviewLibraryLoader.*;
import com.archos.mediacenter.video.leanback.presenter.PreviewCardPresenter;
import java.util.*;
import java.util.concurrent.*;
/** Native five-step editor and library view, backed by the ordinary library snapshot. */
public final class CustomLibraryActivity extends LeanbackActivity {
 private Workspace workspace;private TopNavigation navigation;private final ExecutorService worker=Executors.newSingleThreadExecutor();private volatile boolean closed;
 @Override public void onCreate(Bundle state){super.onCreate(state);workspace=new Workspace();navigation=new TopNavigation(this,workspace,this::navigate,()->workspace.atTop());navigation.selectTab(6);setContentView(navigation);
  getOnBackPressedDispatcher().addCallback(this,new androidx.activity.OnBackPressedCallback(true){public void handleOnBackPressed(){workspace.back();}});
  Snapshot cache=PreviewLibraryLoader.memoryCache();if(cache!=null)workspace.snapshot=cache;
  if(state!=null){CustomLibraryPage draft=CustomLibraryPage.decode(state.getString("draft","{}"));if(draft!=null)workspace.draft=draft;workspace.editing=state.getBoolean("editing");workspace.step=state.getInt("step");workspace.original=state.getString("original",workspace.original);workspace.restoreFocus=state.getString("focus","");workspace.restoreScroll=state.getParcelable("scroll");}
  workspace.render();PreviewLibraryLoader loader=new PreviewLibraryLoader(this);worker.execute(()->{try(Cursor cursor=loader.loadInBackground()){Snapshot library=loader.snapshot;runOnUiThread(()->{if(!closed){workspace.snapshot=library;workspace.preview();if(!workspace.editing)workspace.render();}});}catch(RuntimeException failure){com.archos.mediacenter.video.diagnostics.Diagnostics.error("custom_library_load",failure);}});
 }
 private void navigate(int index){if(index==6)return;if(index==4)startActivity(new Intent(this,com.archos.mediacenter.video.leanback.settings.VideoSettingsActivity.class));else if(index==5)startActivity(new Intent(this,com.archos.mediacenter.video.leanback.search.VideoSearchActivity.class));else{startActivity(new Intent(this,MainActivityLeanback.class).putExtra("preview_tab",index).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));finish();}}
 @Override protected void onSaveInstanceState(Bundle state){workspace.rememberFocus();state.putString("focus",workspace.restoreFocus);state.putParcelable("scroll",workspace.restoreScroll);state.putString("draft",workspace.draft.encode());state.putString("original",workspace.original);state.putBoolean("editing",workspace.editing);state.putInt("step",workspace.step);super.onSaveInstanceState(state);}
 @Override protected void onDestroy(){closed=true;worker.shutdownNow();super.onDestroy();}
 private final class Workspace extends LinearLayout implements PageExitGuard {
  final String[] steps={"Page Details","Content Type","Filters","Display Options","Review"};CustomLibraryPage draft;String original;boolean editing;int step;Snapshot snapshot=new Snapshot();LinearLayout options;TextView preview;RecyclerView grid;PreviewLibraryColumns columns;String restoreFocus="";android.os.Parcelable restoreScroll;String pendingFocus;LinearLayout wizardRail;ScrollView wizardPreview;View wizardCentreAnchor;
  Workspace(){super(CustomLibraryActivity.this);setOrientation(VERTICAL);setPadding(dp(24),dp(14),dp(24),dp(16));CustomLibraryPage saved=CustomLibraryPage.load(getContext());editing=saved==null;draft=saved==null?new CustomLibraryPage():CustomLibraryPage.decode(saved.encode());original=draft.encode();getViewTreeObserver().addOnGlobalFocusChangeListener((oldFocus,newFocus)->{
   if(editing||navigation==null||newFocus==null)return;View target=newFocus;
   while(target!=this&&!(target.getTag() instanceof String)&&target.getParent() instanceof View)target=(View)target.getParent();
   if(!(target.getTag() instanceof String))return;
   for(Entry entry:draft.entries(snapshot))if(entry.key().equals(target.getTag())){navigation.setArtwork(entry.backdrop);navigation.setScrolled(grid!=null&&grid.canScrollVertically(-1));break;}
  });}
  boolean atTop(){return editing?step==0&&findFocus()!=null&&"wizard.step.0".equals(findFocus().getTag()):grid==null||!grid.canScrollVertically(-1);}
  TextView action(String label,Runnable run){return SharedThreePanel.action(getContext(),label,run);}
  void row(String label,Runnable run){TextView row=action(label,run);row.setTag("wizard.option."+step+"."+options.getChildCount());options.addView(row,new LayoutParams(-1,dp(50)));}
  LinearLayout column(){LinearLayout view=new LinearLayout(getContext());view.setOrientation(VERTICAL);return view;}
  void rememberFocus(){View focused=findFocus();if(focused!=null&&focused.getTag() instanceof String)restoreFocus=(String)focused.getTag();if(grid!=null&&grid.getLayoutManager()!=null)restoreScroll=grid.getLayoutManager().onSaveInstanceState();}
  void render(){rememberFocus();if(pendingFocus!=null){restoreFocus=pendingFocus;pendingFocus=null;}removeAllViews();if(!editing){library();return;}
   SharedThreePanel panels=new SharedThreePanel(getContext());LinearLayout left=column();options=column();LinearLayout right=column();ScrollView centreScroll=new ScrollView(getContext()),rightScroll=new ScrollView(getContext());centreScroll.addView(options);rightScroll.addView(right);wizardRail=left;wizardPreview=rightScroll;rightScroll.setFocusable(true);panels.panels(left,centreScroll,rightScroll);addView(panels,new LayoutParams(-1,-1));
   left.addView(SharedThreePanel.text(getContext(),"Library Page",22));for(int n=0;n<steps.length;n++){final int position=n;TextView entry=action((n+1)+". "+steps[n],()->{step=position;render();});entry.setTag("wizard.step."+n);left.addView(entry,new LayoutParams(-1,dp(50)));}
   options.addView(SharedThreePanel.text(getContext(),steps[step],22));
   switch(step){case 0:row("Name: "+draft.name,()->PreviewTextInput.show(getContext(),"Page name",draft.name,40,value->{draft.name=value;render();}));row("Icon: "+draft.icon,()->choose("Page icon",new String[]{"Library","Movies","TV Shows","Documentaries"},n->draft.icon=new String[]{"Library","Movies","TV Shows","Documentaries"}[n]));break;
    case 1:row("Content: "+draft.type,()->choose("Content Type",new String[]{"Movies","TV Shows","Both"},n->draft.type=new String[]{"movies","tv","both"}[n]));break;
    case 2:row("Genres: "+(draft.genres.isEmpty()?"All":android.text.TextUtils.join(", ",draft.genres)),()->{Set<String> available=new TreeSet<>();for(Entry entry:snapshot.movies)available.addAll(PreviewGenres.parse(entry.genres));for(Entry entry:snapshot.shows)available.addAll(PreviewGenres.parse(entry.genres));PreviewGenres.choose(getContext(),available,draft.genres,chosen->{draft.genres.clear();draft.genres.addAll(chosen);render();});});row("Genre matching: "+(draft.allGenres?"All":"Any"),()->{draft.allGenres=!draft.allGenres;render();});
     row("Year: "+(draft.year==0?"All":draft.year),()->{TreeSet<Integer> years=new TreeSet<>(Comparator.reverseOrder());for(Entry entry:snapshot.movies)if(entry.year()>0)years.add(entry.year());for(Entry entry:snapshot.shows)if(entry.year()>0)years.add(entry.year());List<Integer> values=new ArrayList<>();values.add(0);values.addAll(years);String[] labels=values.stream().map(value->value==0?"All":String.valueOf(value)).toArray(String[]::new);choose("Year",labels,n->draft.year=values.get(n));});
     row("Watched state: "+draft.watched,()->choose("Watched state",new String[]{"All","Watched","Unwatched","In Progress"},n->draft.watched=new String[]{"all","watched","unwatched","progress"}[n]));row("Minimum rating: "+(draft.rating==0?"Any":draft.rating),()->choose("Minimum rating",new String[]{"Any","5","6","7","8","9"},n->draft.rating=n==0?0:n+4));
     facet("Original Language",draft.language,0,value->draft.language=value);facet("Studio / Network",draft.studio,1,value->draft.studio=value);facet("Collection",draft.collection,2,value->draft.collection=value);break;
    case 3:row("View: "+draft.view,()->choose("Display",new String[]{"Grid","List"},n->draft.view=n==0?"grid":"list"));row("Sort: "+draft.sort,()->choose("Sort",new String[]{"Title","Year","Date Added"},n->draft.sort=new String[]{"title","year","added"}[n]));row("Direction: "+(draft.descending?"Descending":"Ascending"),()->{draft.descending=!draft.descending;render();});break;
    case 4:options.addView(SharedThreePanel.text(getContext(),draft.name+"\n"+draft.type+" · "+draft.view+"\n"+draft.sort+" · "+(draft.descending?"Descending":"Ascending"),18));row("Save Page",this::save);break;
   }
   if(step>0)row("Previous",()->{step--;render();});if(step<4)row("Next",()->{step++;render();});
   if(CustomLibraryPage.load(getContext())!=null)row("Delete Page",()->PreviewDialog.confirmDelete(getContext(),"Delete this page?","Your library media, watched state and playback progress are kept.",()->{if(CustomLibraryPage.delete(getContext()))finish();else PreviewDialog.read(getContext(),"Page not deleted","Retry after returning to the page.");}));
   right.addView(SharedThreePanel.text(getContext(),"Live Preview",22));preview=SharedThreePanel.text(getContext(),"",16);right.addView(preview);preview();
   options.post(()->{View target=findViewWithTag(restoreFocus);if(target==null)target=findViewWithTag("wizard.step."+step);if(target!=null)target.requestFocus();});
  }
  void choose(String title,String[] labels,java.util.function.IntConsumer chosen){PreviewDialog.choose(getContext(),title,labels,-1,n->{chosen.accept(n);render();});}
  void facet(String title,String selected,int kind,java.util.function.Consumer<String> changed){
   Set<String> available=new TreeSet<>();List<Entry> all=new ArrayList<>(snapshot.movies);all.addAll(snapshot.shows);
   for(Entry entry:all){String value=kind==0?entry.language:kind==1?entry.studio:entry.collection;if(value==null||value.isEmpty()||kind==0&&"und".equals(value))continue;if(kind==1)available.addAll(PreviewGenres.parse(value));else available.add(value);}
   if(available.isEmpty()&&selected.isEmpty())return;available.add(selected);available.remove("");List<String> choices=new ArrayList<>();choices.add("");choices.addAll(available);String[] labels=choices.stream().map(value->value.isEmpty()?"All":kind==0?new java.util.Locale(value).getDisplayLanguage():value).toArray(String[]::new);
   row(title+": "+(selected.isEmpty()?"All":selected),()->choose(title,labels,n->changed.accept(choices.get(n))));
  }
  void preview(){if(preview==null)return;List<Entry> entries=draft.entries(snapshot);StringBuilder text=new StringBuilder(draft.name).append("\n\n").append(entries.size()).append(" matching titles\n");for(int i=0;i<Math.min(20,entries.size());i++)text.append("\n").append(PreviewPages.displayName(entries.get(i)));preview.setText(text);}
  void save(){String validation=draft.validation();if(validation!=null){PreviewDialog.read(getContext(),"Review page settings",validation);return;}if(!draft.save(getContext())){PreviewDialog.read(getContext(),"Page not saved","Keep editing and retry.");return;}original=draft.encode();editing=false;pendingFocus="custom.control.filters";navigation.updateCustomPage();render();}
  @Override public void requestExit(Runnable leave){if(!editing||draft.encode().equals(original)){leave.run();return;}PreviewDialog.choose(getContext(),"Unsaved page changes",new String[]{"Save Page","Discard Changes","Continue Editing"},2,n->{if(n==0){String validation=draft.validation();if(validation!=null){PreviewDialog.read(getContext(),"Review page settings",validation);return;}if(draft.save(getContext())){original=draft.encode();editing=false;navigation.updateCustomPage();leave.run();}else PreviewDialog.read(getContext(),"Page not saved","Keep editing and retry.");}else if(n==1){CustomLibraryPage saved=CustomLibraryPage.load(getContext());if(saved!=null)draft=saved;leave.run();}});}
  void back(){if(editing&&step>0){step--;render();return;}requestExit(CustomLibraryActivity.this::finish);}
  void persistDisplay(){if(!draft.save(getContext())){PreviewDialog.read(getContext(),"Page not saved","Retry after returning to the page.");return;}render();}
  void control(LinearLayout controls,String key,String label,Runnable action){TextView button=action(label,action);button.setTag("custom.control."+key);LinearLayout.LayoutParams size=new LinearLayout.LayoutParams(-2,dp(46));size.leftMargin=dp(8);controls.addView(button,size);}
  void edit(int selected){editing=true;step=selected;original=draft.encode();pendingFocus="wizard.step."+step;render();}
  void library(){options=null;preview=null;boolean list=draft.view.equals("list");if(columns==null)columns=new PreviewLibraryColumns(getContext(),true,"custom_");
   LinearLayout controls=new PreviewToolbar(getContext(),true);controls.setGravity(Gravity.CENTER_VERTICAL);addView(controls,new LayoutParams(-1,-2));
   control(controls,"filters","Filters",()->edit(2));
   control(controls,"sort","Sort",()->PreviewDialog.choose(getContext(),"Sort",new String[]{"Title","Year","Date Added"},Arrays.asList("title","year","added").indexOf(draft.sort),n->{draft.sort=new String[]{"title","year","added"}[n];columns.clearSort();persistDisplay();}));
   control(controls,"order",draft.descending?"Descending":"Ascending",()->{draft.descending=!draft.descending;columns.setAscending(!draft.descending);persistDisplay();});
   control(controls,"view",list?"Grid View":"List View",()->{draft.view=list?"grid":"list";restoreScroll=null;persistDisplay();});
   if(list)control(controls,"columns","Columns",()->columns.choose(this::render));
   controls.addView(new View(getContext()),new LayoutParams(0,1,1));control(controls,"edit","Edit",()->edit(0));
   View divider=new View(getContext());divider.setBackgroundColor(0x557f8996);LayoutParams dividerSize=new LayoutParams(-1,dp(1));dividerSize.topMargin=dp(12);addView(divider,dividerSize);
   if(list)addView(columns.header(this::render));
   List<Entry> entries=columns.sort(draft.entries(snapshot));grid=new PreviewFocusRecycler(getContext());grid.setLayoutManager(list?new LinearLayoutManager(getContext()):new GridLayoutManager(getContext(),6));grid.setClipToPadding(false);grid.setPadding(dp(12),dp(12),dp(12),dp(12));PreviewCardPresenter presenter=new PreviewCardPresenter(PreviewCardPresenter.Style.POSTER);
   grid.setAdapter(new RecyclerView.Adapter<Card>(){
    public Card onCreateViewHolder(ViewGroup parent,int type){if(list){LinearLayout row=columns.newRow();row.setLayoutParams(new RecyclerView.LayoutParams(-1,dp(44)));return new Card(new Presenter.ViewHolder(row),true);}return new Card(presenter.onCreateViewHolder(parent),false);}
    public void onBindViewHolder(Card holder,int position){Entry entry=entries.get(position);if(holder.table)columns.bind((LinearLayout)holder.itemView,entry);else presenter.bindEntry(holder.nativeHolder,entry);holder.itemView.setTag(entry.key());holder.itemView.setOnClickListener(v->new VideoViewClickedListener(CustomLibraryActivity.this).onItemClicked(holder.nativeHolder,entry.media,null,null));}
    public int getItemCount(){return entries.size();}
    public void onViewRecycled(Card card){if(card.table)columns.clear(card.itemView);else presenter.onUnbindViewHolder(card.nativeHolder);}
   });addView(grid,new LayoutParams(-1,0,1));if(entries.isEmpty())addView(SharedThreePanel.text(getContext(),"No matching library titles",18));
   if(restoreScroll!=null)grid.getLayoutManager().onRestoreInstanceState(restoreScroll);
   int position=-1;for(int i=0;i<entries.size();i++)if(entries.get(i).key().equals(restoreFocus))position=i;
   if(position>=0){final int target=position;grid.scrollToPosition(target);grid.post(()->{RecyclerView.ViewHolder row=grid.findViewHolderForAdapterPosition(target);if(row!=null)row.itemView.requestFocus();});}
   else controls.post(()->{View button=findViewWithTag(restoreFocus);if(button==null)button=findViewWithTag("custom.control.filters");if(button!=null)button.requestFocus();});
  }
  @Override public boolean dispatchKeyEvent(KeyEvent event){
   if(editing&&event.getAction()==KeyEvent.ACTION_DOWN){int key=event.getKeyCode();View focused=findFocus();
    if(wizardRail!=null&&wizardRail.hasFocus()){
     if(key==KeyEvent.KEYCODE_DPAD_LEFT)return true;
     if(key==KeyEvent.KEYCODE_DPAD_RIGHT){for(int n=0;n<options.getChildCount();n++)if(options.getChildAt(n).isFocusable()){options.getChildAt(n).requestFocus();break;}return true;}
     if(key==KeyEvent.KEYCODE_DPAD_UP||key==KeyEvent.KEYCODE_DPAD_DOWN){int position=wizardRail.indexOfChild(focused),next=position+(key==KeyEvent.KEYCODE_DPAD_UP?-1:1);if(next>=1&&next<wizardRail.getChildCount())wizardRail.getChildAt(next).requestFocus();return true;}
    }else if(options!=null&&options.hasFocus()){
     wizardCentreAnchor=focused;
     if(key==KeyEvent.KEYCODE_DPAD_LEFT){View rail=findViewWithTag("wizard.step."+step);if(rail!=null)rail.requestFocus();return true;}
     if(key==KeyEvent.KEYCODE_DPAD_RIGHT){if(wizardPreview.canScrollVertically(1)||wizardPreview.canScrollVertically(-1))wizardPreview.requestFocus();return true;}
     if(key==KeyEvent.KEYCODE_DPAD_UP||key==KeyEvent.KEYCODE_DPAD_DOWN){View next=FocusFinder.getInstance().findNextFocus(options,focused,key==KeyEvent.KEYCODE_DPAD_UP?FOCUS_UP:FOCUS_DOWN);if(next!=null&&next!=focused)next.requestFocus();return true;}
    }else if(wizardPreview!=null&&wizardPreview.hasFocus()){
     if(key==KeyEvent.KEYCODE_DPAD_RIGHT)return true;
     if(key==KeyEvent.KEYCODE_DPAD_LEFT){if(wizardCentreAnchor!=null)wizardCentreAnchor.requestFocus();return true;}
    }
   }return super.dispatchKeyEvent(event);
  }
  class Card extends RecyclerView.ViewHolder{final Presenter.ViewHolder nativeHolder;final boolean table;Card(Presenter.ViewHolder holder,boolean table){super(holder.view);nativeHolder=holder;this.table=table;}}
  int dp(int n){return SharedThreePanel.dp(getContext(),n);}
 }
}
