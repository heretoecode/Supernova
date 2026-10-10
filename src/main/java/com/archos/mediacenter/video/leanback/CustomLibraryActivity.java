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
 private final class Workspace extends FrameLayout implements PageExitGuard {
  final String[] steps={"Page Details","Content Type","Filters","Display Options","Review"};CustomLibraryPage draft;String original;boolean editing;int step;Snapshot snapshot=new Snapshot();LinearLayout options,libraryBody;LinearLayout footer;boolean choice;TextView preview;RecyclerView grid;PreviewLibraryColumns columns;String restoreFocus="";android.os.Parcelable restoreScroll;String pendingFocus;LinearLayout wizardRail;ScrollView wizardPreview,wizardOptions;View wizardCentreAnchor;
  Workspace(){super(CustomLibraryActivity.this);setPadding(dp(24),dp(14),dp(24),dp(16));CustomLibraryPage saved=CustomLibraryPage.load(getContext());editing=saved==null;draft=saved==null?new CustomLibraryPage():CustomLibraryPage.decode(saved.encode());original=draft.encode();getViewTreeObserver().addOnGlobalFocusChangeListener((oldFocus,newFocus)->{
   if(editing||navigation==null||newFocus==null)return;View target=newFocus;
   while(target!=this&&!(target.getTag() instanceof String)&&target.getParent() instanceof View)target=(View)target.getParent();
   if(!(target.getTag() instanceof String))return;
   for(Entry entry:draft.entries(snapshot))if(entry.key().equals(target.getTag())){navigation.setArtwork(entry.backdrop);navigation.setScrolled(grid!=null&&grid.canScrollVertically(-1));break;}
  });}
  boolean atTop(){return editing?false:grid==null||!grid.canScrollVertically(-1);}
  TextView action(String label,Runnable run){return SharedThreePanel.action(getContext(),label,run);}
  void footerAction(String label,Runnable run){TextView button=action(label,run);button.setMinHeight(dp(40));button.setBackground(SharedThreePanel.control(getContext(),10));LayoutParams size=new LayoutParams(-2,dp(40));size.topMargin=dp(6);footer.addView(button,size);}
  void row(String label,Runnable run){TextView row=action(label,run);row.setTag("wizard.option."+step+"."+options.getChildCount());options.addView(row,new LayoutParams(-1,dp(42)));}
  LinearLayout column(){LinearLayout view=new LinearLayout(getContext());view.setOrientation(LinearLayout.VERTICAL);return view;}
  void rememberFocus(){View focused=findFocus();if(focused!=null&&focused.getTag() instanceof String)restoreFocus=(String)focused.getTag();if(grid!=null&&grid.getLayoutManager()!=null)restoreScroll=grid.getLayoutManager().onSaveInstanceState();}
  void render(){rememberFocus();if(pendingFocus!=null){restoreFocus=pendingFocus;pendingFocus=null;}removeAllViews();if(!editing){library();return;}
   boolean saved=CustomLibraryPage.load(getContext())!=null;if(saved){CustomLibraryPage edited=draft;draft=CustomLibraryPage.load(getContext());library();draft=edited;libraryBody.setDescendantFocusability(ViewGroup.FOCUS_BLOCK_DESCENDANTS);libraryBody.setAlpha(.35f);if(android.os.Build.VERSION.SDK_INT>=31)libraryBody.setRenderEffect(android.graphics.RenderEffect.createBlurEffect(dp(6),dp(6),android.graphics.Shader.TileMode.CLAMP));TextView label=SharedThreePanel.text(getContext(),"Edit Library",22);label.setGravity(Gravity.CENTER);FrameLayout.LayoutParams titleSize=new FrameLayout.LayoutParams(-1,dp(36),Gravity.TOP);addView(label,titleSize);}

   SharedThreePanel panels=new SharedThreePanel(getContext());LinearLayout left=column();options=column();LinearLayout right=column();ScrollView centreScroll=new ScrollView(getContext()),rightScroll=new ScrollView(getContext());wizardOptions=centreScroll;centreScroll.addView(options);rightScroll.addView(right);LinearLayout centre=column();footer=column();SharedThreePanel.heading(centre,steps[step]);centre.addView(centreScroll,new LinearLayout.LayoutParams(-1,0,1));centre.addView(footer,new LinearLayout.LayoutParams(-1,-2));wizardRail=left;wizardPreview=rightScroll;rightScroll.setFocusable(true);rightScroll.setFocusableInTouchMode(true);rightScroll.setForeground(SharedThreePanel.focus(getContext()));panels.panels(left,centre,rightScroll);FrameLayout.LayoutParams panelSize=new FrameLayout.LayoutParams(-1,-1);if(saved){panelSize.topMargin=dp(48);panelSize.bottomMargin=dp(24);panelSize.leftMargin=dp(24);panelSize.rightMargin=dp(24);}else{TextView introduction=SharedThreePanel.text(getContext(),"Create one personalised library page using your Movies and TV Shows. Choose filters and a layout, then save your page.",15);FrameLayout.LayoutParams introSize=new FrameLayout.LayoutParams(-1,dp(48),Gravity.TOP);addView(introduction,introSize);panelSize.topMargin=dp(56);}addView(panels,panelSize);
   SharedThreePanel.heading(left,"Library Page");for(int n=0;n<steps.length;n++){final int position=n;TextView entry=action((n+1)+". "+steps[n],()->{step=position;pendingFocus="wizard.step."+position;render();});entry.setTag("wizard.step."+n);SharedThreePanel.rowSeparator(entry);entry.setOnFocusChangeListener((v,focused)->{if(focused&&step!=position){step=position;pendingFocus="wizard.step."+position;post(this::render);}});left.addView(entry,new LayoutParams(-1,dp(42)));}

   switch(step){case 0:row("Name: "+draft.name,()->PreviewTextInput.show(getContext(),"Page name",draft.name,40,value->{draft.name=value;render();}));if(saved)row("Delete Page",()->PreviewDialog.confirmPageDelete(getContext(),"Delete this page?","Your library media, watched state and playback progress are kept.",()->{if(CustomLibraryPage.delete(getContext()))finish();else PreviewDialog.read(getContext(),"Page not deleted","Retry after returning to the page.");}));break;
    case 1:for(int type=0;type<3;type++){final int selected=type;String value=new String[]{"movies","tv","both"}[type];row((draft.type.equals(value)?"✓  ":"○  ")+new String[]{"Movies","TV Shows","Both"}[type],()->{draft.type=new String[]{"movies","tv","both"}[selected];render();});}break;
    case 2:row("Genres: "+(draft.genres.isEmpty()?"All":android.text.TextUtils.join(", ",draft.genres)),()->{Set<String> available=new TreeSet<>();for(Entry entry:snapshot.movies)available.addAll(PreviewGenres.parse(entry.genres));for(Entry entry:snapshot.shows)available.addAll(PreviewGenres.parse(entry.genres));PreviewGenres.choose(getContext(),available,draft.genres,chosen->{draft.genres.clear();draft.genres.addAll(chosen);render();});});row("Genre matching: "+(draft.allGenres?"All":"Any"),()->{draft.allGenres=!draft.allGenres;render();});
     row("Year: "+(draft.year==0?"All":draft.year),()->{TreeSet<Integer> years=new TreeSet<>(Comparator.reverseOrder());for(Entry entry:snapshot.movies)if(entry.year()>0)years.add(entry.year());for(Entry entry:snapshot.shows)if(entry.year()>0)years.add(entry.year());List<Integer> values=new ArrayList<>();values.add(0);values.addAll(years);String[] labels=values.stream().map(value->value==0?"All":String.valueOf(value)).toArray(String[]::new);choose("Year",labels,n->draft.year=values.get(n));});
     row("Watched state: "+draft.watched,()->choose("Watched state",new String[]{"All","Watched","Unwatched","In Progress"},n->draft.watched=new String[]{"all","watched","unwatched","progress"}[n]));row("Minimum rating: "+(draft.rating==0?"Any":draft.rating),()->choose("Minimum rating",new String[]{"Any","5","6","7","8","9"},n->draft.rating=n==0?0:n+4));
     facet("Original Language",draft.language,0,value->draft.language=value);facet("Studio / Network",draft.studio,1,value->draft.studio=value);break;
    case 3:row("View: "+draft.view,()->choose("Display",new String[]{"Grid","List"},n->draft.view=n==0?"grid":"list"));row("Sort: "+draft.sort,()->choose("Sort",new String[]{"Title","Year","Date Added"},n->draft.sort=new String[]{"title","year","added"}[n]));row("Direction: "+(draft.descending?"Descending":"Ascending"),()->{draft.descending=!draft.descending;render();});break;
    case 4:review(options);break;
   }
   if(step>0)footerAction("Previous",()->{step--;pendingFocus="wizard.step."+step;render();});
   if(step<4)footerAction("Next",()->{step++;pendingFocus="wizard.step."+step;render();});else footerAction("Save Page",this::save);
   footerAction(saved?"Close Editor":"Cancel",()->requestExit(saved?this::closeEditor:CustomLibraryActivity.this::finish));
   SharedThreePanel.heading(right,"Live Preview");preview=SharedThreePanel.text(getContext(),"",16);right.addView(preview);preview();
   options.post(()->{View target=findViewWithTag(restoreFocus);if(target==null)target=findViewWithTag("wizard.step."+step);if(target!=null)target.requestFocus();});
  }
  void choose(String title,String[] labels,java.util.function.IntConsumer chosen){choice=true;options.removeAllViews();SharedThreePanel.heading(options,title);for(int n=0;n<labels.length;n++){final int selected=n;row(labels[n],()->{chosen.accept(selected);choice=false;render();});}options.post(()->{if(options.getChildCount()>2)options.getChildAt(2).requestFocus();});}
  void facet(String title,String selected,int kind,java.util.function.Consumer<String> changed){
   Set<String> available=new TreeSet<>();List<Entry> all=new ArrayList<>(snapshot.movies);all.addAll(snapshot.shows);
   for(Entry entry:all){String value=kind==0?entry.language:kind==1?entry.studio:entry.collection;if(value==null||value.isEmpty()||kind==0&&"und".equals(value))continue;if(kind==1)available.addAll(PreviewGenres.parse(value));else available.add(value);}
   if(available.isEmpty()&&selected.isEmpty())return;available.add(selected);available.remove("");List<String> choices=new ArrayList<>();choices.add("");choices.addAll(available);String[] labels=choices.stream().map(value->value.isEmpty()?"All":kind==0?new java.util.Locale(value).getDisplayLanguage():value).toArray(String[]::new);
   row(title+": "+(selected.isEmpty()?"All":selected),()->{choose(title,labels,n->changed.accept(choices.get(n)));if(kind==0)for(int n=0;n<choices.size();n++){TextView row=(TextView)options.getChildAt(n+2);PreviewLanguageIcon icon=new PreviewLanguageIcon(choices.get(n));icon.setBounds(0,0,dp(22),dp(22));row.setCompoundDrawables(icon,null,null,null);row.setCompoundDrawablePadding(dp(8));}});
  }
  void review(LinearLayout target){String[] values={"Name: "+draft.name,"Content: "+(draft.type.equals("tv")?"TV Shows":draft.type.equals("movies")?"Movies":"Both"),"Genres: "+(draft.genres.isEmpty()?"All Genres":android.text.TextUtils.join(", ",draft.genres)),"Genre Match: "+(draft.allGenres?"All":"Any"),"Year: "+(draft.year==0?"All":draft.year),"Watched State: "+draft.watched,"Minimum Rating: "+(draft.rating==0?"Any":draft.rating),"Original Language: "+(draft.language.isEmpty()?"All":new Locale(draft.language).getDisplayLanguage()),"Studio / Network: "+(draft.studio.isEmpty()?"All":draft.studio),"Display: "+draft.view,"Sort: "+draft.sort+" · "+(draft.descending?"Descending":"Ascending")};for(String value:values){target.addView(SharedThreePanel.text(getContext(),value,15));SharedThreePanel.divider(target);}}
  void preview(){if(preview==null)return;List<Entry> entries=draft.entries(snapshot);StringBuilder text=new StringBuilder(draft.name).append("\n\n").append(PreviewLibrarySummary.describe(getContext(),snapshot,entries)).append("\n");for(int i=0;i<Math.min(20,entries.size());i++)text.append("\n").append(PreviewPages.displayName(entries.get(i)));preview.setText(text);}
  void save(){String validation=draft.validation();if(validation!=null){PreviewDialog.read(getContext(),"Review page settings",validation);return;}if(!draft.save(getContext())){PreviewDialog.read(getContext(),"Page not saved","Keep editing and retry.");return;}original=draft.encode();editing=false;pendingFocus="custom.control.edit";navigation.updateCustomPage();render();}
  @Override public void requestExit(Runnable leave){if(!editing||draft.encode().equals(original)){leave.run();return;}PreviewDialog.choose(getContext(),"Unsaved page changes",new String[]{"Save Page","Discard Changes","Continue Editing"},2,n->{if(n==0){String validation=draft.validation();if(validation!=null){PreviewDialog.read(getContext(),"Review page settings",validation);return;}if(draft.save(getContext())){original=draft.encode();editing=false;navigation.updateCustomPage();leave.run();}else PreviewDialog.read(getContext(),"Page not saved","Keep editing and retry.");}else if(n==1){CustomLibraryPage saved=CustomLibraryPage.load(getContext());if(saved!=null)draft=saved;leave.run();}});}
  void closeEditor(){CustomLibraryPage saved=CustomLibraryPage.load(getContext());if(saved!=null)draft=saved;original=draft.encode();editing=false;pendingFocus="custom.control.edit";render();}
  void back(){if(editing){if(choice){choice=false;render();return;}if(wizardPreview!=null&&wizardPreview.hasFocus()){if(wizardCentreAnchor!=null)wizardCentreAnchor.requestFocus();return;}if((options!=null&&options.hasFocus()||footer!=null&&footer.hasFocus())||footer!=null&&footer.hasFocus()){View rail=findViewWithTag("wizard.step."+step);if(rail!=null)rail.requestFocus();return;}requestExit(CustomLibraryPage.load(getContext())==null?CustomLibraryActivity.this::finish:this::closeEditor);return;}requestExit(CustomLibraryActivity.this::finish);}
  void persistDisplay(){if(!draft.save(getContext())){PreviewDialog.read(getContext(),"Page not saved","Retry after returning to the page.");return;}render();}
  void control(LinearLayout controls,String key,String label,Runnable action){TextView button=action(label,action);button.setTag("custom.control."+key);button.setMinHeight(dp(40));button.setBackground(SharedThreePanel.control(getContext(),10));LinearLayout.LayoutParams size=new LinearLayout.LayoutParams(-2,dp(40));size.leftMargin=dp(8);controls.addView(button,size);}
  void edit(int selected){editing=true;step=selected;original=draft.encode();pendingFocus="wizard.step."+step;render();}
  void library(){libraryBody=column();addView(libraryBody,new FrameLayout.LayoutParams(-1,-1));options=null;preview=null;boolean list=draft.view.equals("list");if(columns==null)columns=new PreviewLibraryColumns(getContext(),true,"custom_");
   LinearLayout controls=new PreviewToolbar(getContext(),true);controls.setGravity(Gravity.CENTER_VERTICAL);LinearLayout heading=column();LibraryPageHeader.add(heading,draft.name,snapshot,draft.entries(snapshot));libraryBody.addView(heading);libraryBody.addView(controls,new LinearLayout.LayoutParams(-1,-2));
   control(controls,"filters","Filters",()->edit(2));
   control(controls,"sort","Sort",()->PreviewDialog.choose(getContext(),"Sort",new String[]{"Title","Year","Date Added"},Arrays.asList("title","year","added").indexOf(draft.sort),n->{draft.sort=new String[]{"title","year","added"}[n];columns.clearSort();persistDisplay();}));
   control(controls,"order",draft.descending?"Descending":"Ascending",()->{draft.descending=!draft.descending;columns.setAscending(!draft.descending);persistDisplay();});
   control(controls,"view",list?"Grid View":"List View",()->{draft.view=list?"grid":"list";restoreScroll=null;persistDisplay();});
   if(list)control(controls,"columns","Columns",()->columns.choose(this::render));
   controls.addView(new View(getContext()),new LayoutParams(0,1,1));control(controls,"edit","Edit Library",()->edit(0));
   View divider=new View(getContext());divider.setBackgroundColor(0x557f8996);LinearLayout.LayoutParams dividerSize=new LinearLayout.LayoutParams(-1,dp(1));dividerSize.topMargin=dp(12);libraryBody.addView(divider,dividerSize);
   if(list)libraryBody.addView(columns.header(this::render));
   List<Entry> entries=columns.sort(draft.entries(snapshot));grid=new PreviewFocusRecycler(getContext());grid.setLayoutManager(list?new LinearLayoutManager(getContext()):new GridLayoutManager(getContext(),6));grid.setClipToPadding(false);grid.setPadding(0,dp(12),0,dp(12));PreviewCardPresenter presenter=new PreviewCardPresenter(PreviewCardPresenter.Style.POSTER);
   grid.setAdapter(new RecyclerView.Adapter<Card>(){
    public Card onCreateViewHolder(ViewGroup parent,int type){if(list){LinearLayout row=columns.newRow();row.setLayoutParams(new RecyclerView.LayoutParams(-1,dp(44)));return new Card(new Presenter.ViewHolder(row),true);}Card card=new Card(presenter.onCreateViewHolder(parent),false);RecyclerView.LayoutParams size=new RecyclerView.LayoutParams(-1,-2);size.setMargins(0,0,dp(10),dp(15));card.itemView.setLayoutParams(size);return card;}
    public void onBindViewHolder(Card holder,int position){Entry entry=entries.get(position);if(holder.table)columns.bind((LinearLayout)holder.itemView,entry);else presenter.bindEntry(holder.nativeHolder,entry);holder.itemView.setTag(entry.key());holder.itemView.setOnClickListener(v->new VideoViewClickedListener(CustomLibraryActivity.this).onItemClicked(holder.nativeHolder,entry.media,null,null));}
    public int getItemCount(){return entries.size();}
    public void onViewRecycled(Card card){if(card.table)columns.clear(card.itemView);else presenter.onUnbindViewHolder(card.nativeHolder);}
   });libraryBody.addView(grid,new LinearLayout.LayoutParams(-1,0,1));if(entries.isEmpty())libraryBody.addView(SharedThreePanel.text(getContext(),"No matching library titles",18));
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
    }else if((options!=null&&options.hasFocus()||footer!=null&&footer.hasFocus())){
     wizardCentreAnchor=focused;
     if(key==KeyEvent.KEYCODE_DPAD_LEFT){View rail=findViewWithTag("wizard.step."+step);if(rail!=null)rail.requestFocus();return true;}
     if(key==KeyEvent.KEYCODE_DPAD_RIGHT){wizardPreview.requestFocus();return true;}
     if(key==KeyEvent.KEYCODE_DPAD_UP||key==KeyEvent.KEYCODE_DPAD_DOWN){ViewGroup centre=(ViewGroup)wizardOptions.getParent();View next=FocusFinder.getInstance().findNextFocus(centre,focused,key==KeyEvent.KEYCODE_DPAD_UP?FOCUS_UP:FOCUS_DOWN);if(next!=null&&next!=focused)next.requestFocus();return true;}
    }else if(wizardPreview!=null&&wizardPreview.hasFocus()){
     if(key==KeyEvent.KEYCODE_DPAD_UP||key==KeyEvent.KEYCODE_DPAD_DOWN){wizardPreview.smoothScrollBy(0,key==KeyEvent.KEYCODE_DPAD_UP?-dp(72):dp(72));return true;}
     if(key==KeyEvent.KEYCODE_DPAD_RIGHT)return true;
     if(key==KeyEvent.KEYCODE_DPAD_LEFT){if(wizardCentreAnchor!=null)wizardCentreAnchor.requestFocus();return true;}
    }
   }return super.dispatchKeyEvent(event);
  }
  class Card extends RecyclerView.ViewHolder{final Presenter.ViewHolder nativeHolder;final boolean table;Card(Presenter.ViewHolder holder,boolean table){super(holder.view);nativeHolder=holder;this.table=table;}}
  int dp(int n){return SharedThreePanel.dp(getContext(),n);}
 }
}
