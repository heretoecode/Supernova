package com.archos.mediacenter.video.leanback;
import android.os.Bundle;
import android.content.Intent;
import android.database.Cursor;
import android.view.*;
import android.widget.*;
import com.archos.mediacenter.video.leanback.PreviewLibraryLoader.*;
import java.util.concurrent.*;

/**
 * The user's custom library page, backed by the ordinary library snapshot.
 *
 * <p>Editing (owner, 2 Nov 2026): the approved {@link EditPagePanel} opens over the real page, and every
 * change re-renders the page behind it — "the live preview is the page itself". The page name in the top
 * navigation follows the draft. Nothing is stored until Save; Discard restores the previous page.
 */
public final class CustomLibraryActivity extends LeanbackActivity {
 static final String NEW_PAGE_NAME = "New Page";
 static final String NEW_PAGE_PLACEHOLDER = "Choose what to show. Your page fills in here.";
 private Workspace workspace;private TopNavigation navigation;private final ExecutorService worker=Executors.newSingleThreadExecutor();private volatile boolean closed;
 @Override public void onCreate(Bundle state){super.onCreate(state);workspace=new Workspace();navigation=new TopNavigation(this,workspace,this::navigate,()->workspace.atTop());navigation.selectTab(6);setContentView(navigation);
  getOnBackPressedDispatcher().addCallback(this,new androidx.activity.OnBackPressedCallback(true){public void handleOnBackPressed(){workspace.back();}});
  Snapshot cache=PreviewLibraryLoader.memoryCache();if(cache!=null)workspace.snapshot=cache;
  workspace.freshEntry = state == null;
  if(state!=null){CustomLibraryPage draft=CustomLibraryPage.decode(state.getString("draft","{}"));if(draft!=null)workspace.draft=draft;workspace.editing=state.getBoolean("editing");workspace.original=state.getString("original",workspace.original);workspace.restoreFocus=state.getString("focus","");}
  workspace.render();PreviewLibraryLoader loader=new PreviewLibraryLoader(this);worker.execute(()->{try(Cursor cursor=loader.loadInBackground()){Snapshot library=loader.snapshot;runOnUiThread(()->{if(!closed)applySnapshot(library);});}catch(RuntimeException failure){com.archos.mediacenter.video.diagnostics.Diagnostics.error("custom_library_load",failure);}});
 }
 /** A snapshot update is a data refresh, not a new library view lifecycle. */
 void applySnapshot(Snapshot library){workspace.snapshot=library;if(workspace.sharedLibrary!=null)workspace.sharedLibrary.setSnapshot(library);if(workspace.editor!=null){workspace.editor.snapshotChanged();workspace.updatePlaceholder();}}
 private void navigate(int index){if(index==6)return;if(index==4)startActivity(new Intent(this,com.archos.mediacenter.video.leanback.settings.VideoSettingsActivity.class));else if(index==5)startActivity(new Intent(this,com.archos.mediacenter.video.leanback.search.VideoSearchActivity.class));else{startActivity(new Intent(this,MainActivityLeanback.class).putExtra("preview_tab",index).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));finish();}}
 @Override protected void onSaveInstanceState(Bundle state){workspace.rememberFocus();state.putString("focus",workspace.restoreFocus);state.putString("draft",workspace.draft.encode());state.putString("original",workspace.original);state.putBoolean("editing",workspace.editing);super.onSaveInstanceState(state);}
 @Override protected void onDestroy(){closed=true;worker.shutdownNow();workspace.dismissEditor();super.onDestroy();}
 /** The open Edit Page panel, or null. */
 EditPagePanel editor(){return workspace.editor;}

 private final class Workspace extends FrameLayout implements PageExitGuard,TopNavigation.ContentFocus,EditPagePanel.Host {
  CustomLibraryPage draft;String original;boolean editing;Snapshot snapshot=new Snapshot();LinearLayout libraryBody;String restoreFocus="";String pendingFocus;PreviewPages sharedLibrary;EditPagePanel editor;
  /** The blank draft a new page starts from; until it changes, the page shows {@link #NEW_PAGE_PLACEHOLDER}. */
  private final String blank;
  Workspace(){super(CustomLibraryActivity.this);CustomLibraryPage saved=CustomLibraryPage.load(getContext());editing=saved==null;
   CustomLibraryPage fresh=new CustomLibraryPage();fresh.name=NEW_PAGE_NAME;blank=fresh.encode();
   draft=saved==null?fresh:CustomLibraryPage.decode(saved.encode());original=draft.encode();
   getViewTreeObserver().addOnGlobalFocusChangeListener((oldFocus,newFocus)->{
    if(editing||navigation==null||newFocus==null)return;View target=newFocus;
    while(target!=this&&!(target.getTag() instanceof String)&&target.getParent() instanceof View)target=(View)target.getParent();
    if(!(target.getTag() instanceof String))return;
    for(Entry entry:draft.entries(snapshot))if(entry.key().equals(target.getTag())){navigation.setArtwork(entry.backdrop);break;}
   });}
  /** True only for the first library mount of a newly opened page (not a restore or editor return). */
  boolean freshEntry;
  @Override public boolean focusFromNavigation(){if(editing)return false;return sharedLibrary!=null&&sharedLibrary.focusFromNavigation();}
  boolean atTop(){return sharedLibrary==null||sharedLibrary.atTop();}
  void rememberFocus(){View focused=findFocus();if(focused!=null&&focused.getTag() instanceof String)restoreFocus=(String)focused.getTag();}

  void render(){rememberFocus();if(pendingFocus!=null){restoreFocus=pendingFocus;pendingFocus=null;}removeAllViews();library();
   if(editing)openEditor();else{dismissEditor();navigation.updateCustomPage();}}

  // ---- Edit Page panel (EditPagePanel.Host) ----

  void openEditor(){
   updatePlaceholder();showDraftName();
   if(editor==null||!editor.isShowing())editor=EditPagePanel.show(CustomLibraryActivity.this,this);
  }
  void dismissEditor(){if(editor!=null){editor.dismiss();editor=null;}}
  @Override public CustomLibraryPage draft(){return draft;}
  @Override public boolean isNew(){return CustomLibraryPage.load(getContext())==null;}
  @Override public Snapshot snapshot(){return snapshot;}
  @Override public void changed(){if(sharedLibrary!=null)sharedLibrary.configureCustomLibrary(draft,()->edit(0));updatePlaceholder();showDraftName();}
  @Override public boolean untouched(){return editing&&isNew()&&draft.encode().equals(blank);}
  void updatePlaceholder(){if(sharedLibrary!=null)sharedLibrary.setCustomPlaceholder(editing&&isNew()&&draft.encode().equals(blank)?NEW_PAGE_PLACEHOLDER:null);}
  void showDraftName(){navigation.previewCustomPage(draft.name.trim().isEmpty()?NEW_PAGE_NAME:draft.name.trim(),isNew());}
  @Override public void save(){save(null);}
  @Override public void back(){if(editing){requestExit(isNew()?CustomLibraryActivity.this::finish:this::closeEditor);return;}requestExit(CustomLibraryActivity.this::finish);}
  @Override public void delete(){PreviewDialog.confirmPageDelete(getContext(),"Delete this page?","Removes this page only. Your media, watched state and playback progress are never deleted.",()->{if(CustomLibraryPage.delete(getContext())){dismissEditor();finish();}else PreviewDialog.read(getContext(),"Page not deleted","Retry after returning to the page.");});}

  boolean save(Runnable leave){String validation=draft.validation();if(validation!=null){PreviewDialog.read(getContext(),"Check the page settings",validation);return false;}if(!draft.save(getContext())){PreviewDialog.read(getContext(),"Page not saved","Keep editing and retry.");return false;}original=draft.encode();editing=false;freshEntry=false;pendingFocus="custom.control.edit";sharedLibrary=null;render();if(leave!=null)leave.run();return true;}
  @Override public void requestExit(Runnable leave){if(!editing||draft.encode().equals(original)){leave.run();return;}
   PreviewDialog.message(getContext(),"Unsaved Changes","You've changed this page. Save the changes, discard them, or keep editing?",false,new String[]{"Keep Editing","Save","Discard"},0,n->{
    if(n==1)save(leave);
    else if(n==2){CustomLibraryPage clean=CustomLibraryPage.decode(original);if(clean!=null)draft=clean;boolean wasNew=isNew();editing=wasNew;if(!wasNew){pendingFocus="custom.control.edit";sharedLibrary=null;render();}leave.run();}
   });}
  void closeEditor(){CustomLibraryPage saved=CustomLibraryPage.load(getContext());if(saved!=null)draft=saved;original=draft.encode();editing=false;pendingFocus="custom.control.edit";sharedLibrary=null;render();}
  void edit(int ignored){editing=true;original=draft.encode();if(sharedLibrary!=null)sharedLibrary.setCustomEditing(true);render();}

  void library(){
   setBackground(null);setPadding(0,0,0,0);
   libraryBody=new LinearLayout(getContext());libraryBody.setOrientation(LinearLayout.VERTICAL);addView(libraryBody,new FrameLayout.LayoutParams(-1,-1));
   if(sharedLibrary==null){sharedLibrary=new PreviewPages(getContext(),(holder,item)->new VideoViewClickedListener(CustomLibraryActivity.this).onItemClicked(holder,item,null,null));sharedLibrary.setCustomEditing(editing);sharedLibrary.configureCustomLibrary(draft,()->edit(0));sharedLibrary.setArtworkListener(uri->{if(navigation!=null)navigation.setArtwork(uri);});
    // Owner 2 Nov 2026: like Movies/TV, scrolling content blurs and fades under the top bar.
    sharedLibrary.setScrollListener(scrolled->{if(navigation!=null)navigation.setScrolled(scrolled);});}
   if(sharedLibrary.getParent() instanceof ViewGroup)((ViewGroup)sharedLibrary.getParent()).removeView(sharedLibrary);
   sharedLibrary.setSnapshot(snapshot);libraryBody.addView(sharedLibrary,new LinearLayout.LayoutParams(-1,-1));
   if(editing)return;
   if (freshEntry) {
       // Owner decision 2 Nov 2026: opening the custom page from the top bar behaves like
       // Movies/TV — focus stays visibly on this page's own tab; DOWN then enters the page
       // through TopNavigation's normal content-focus route.
       freshEntry = false;
       navigation.post(navigation::focusSelectedTab);
   } else {
       sharedLibrary.focusFromNavigation();
   }
   if(restoreFocus!=null&&restoreFocus.startsWith("custom.control.")){String control=restoreFocus.substring("custom.control.".length());sharedLibrary.post(()->sharedLibrary.focusCustomControl(control));}
  }
 }
}
