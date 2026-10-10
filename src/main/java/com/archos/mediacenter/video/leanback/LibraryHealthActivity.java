package com.archos.mediacenter.video.leanback;

import android.os.Bundle;
import android.content.Intent;
import android.view.*;
import android.widget.*;
import com.archos.mediacenter.video.leanback.scrapping.ManualVideoScrappingActivity;
import com.archos.mediacenter.video.browser.adapters.object.*;
import com.archos.mediacenter.video.leanback.PreviewLibraryLoader.*;
import com.archos.mediacenter.video.leanback.details.*;
import java.util.*;
import java.util.concurrent.*;

/** Indexed issue review. Storage availability never changes records or viewing state. */
public final class LibraryHealthActivity extends LeanbackActivity {
 private Workspace workspace;private volatile boolean closed;
 private final ExecutorService worker=Executors.newSingleThreadExecutor();
 @Override public void onCreate(Bundle state){super.onCreate(state);workspace=new Workspace();TopNavigation shell=new TopNavigation(this,workspace,index->{if(index==4)startActivity(new Intent(this,com.archos.mediacenter.video.leanback.settings.VideoSettingsActivity.class));else if(index==5)startActivity(new Intent(this,com.archos.mediacenter.video.leanback.search.VideoSearchActivity.class));else{startActivity(new Intent(this,MainActivityLeanback.class).putExtra("preview_tab",index).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));finish();}},()->workspace.categories.hasFocus());shell.selectTab(3);setContentView(shell);}
 @Override public void onResume(){super.onResume();reload();}
 private void reload(){PreviewLibraryLoader loader=new PreviewLibraryLoader(this);worker.execute(()->{try(android.database.Cursor cursor=loader.loadInBackground()){Snapshot snapshot=loader.snapshot;runOnUiThread(()->{if(!closed&&!isFinishing())workspace.update(snapshot==null?new Snapshot():snapshot);});}catch(RuntimeException error){runOnUiThread(()->{if(!closed)workspace.description.setText("Library review is temporarily unavailable. Your library is unchanged.");});}});}
 @Override protected void onDestroy(){closed=true;worker.shutdownNow();super.onDestroy();}
 final class Workspace extends SharedThreePanel {
  final LinearLayout categories=column(),issues=column(),context=column();final TextView description=SharedThreePanel.text(LibraryHealthActivity.this,"Loading indexed library…",16);
  final String[] names={"Unmatched Media","Incorrect Matches","Missing Metadata or Artwork","Unavailable Files","Source Problems"};
  final List<List<Entry>> groups=new ArrayList<>();final List<TextView> categoryRows=new ArrayList<>();int category;boolean rendering,reviewing;View leftAnchor,issueAnchor;Snapshot snapshot=new Snapshot();final Set<String> sourceIssues=new TreeSet<>();
  Workspace(){super(LibraryHealthActivity.this);for(String name:names)groups.add(new ArrayList<>());ScrollView left=new ScrollView(getContext()),middle=new ScrollView(getContext()),right=new ScrollView(getContext());left.addView(categories);middle.addView(issues);right.addView(context);panels(left,middle,right);categories.addView(SharedThreePanel.text(getContext(),"Library Health",21));context.addView(description);for(int i=0;i<names.length;i++){final int selected=i;TextView row=SharedThreePanel.action(getContext(),names[i],()->choose(selected));row.setOnFocusChangeListener((v,focused)->{if(focused){leftAnchor=v;choose(selected);}});categoryRows.add(row);categories.addView(row);if(i==0)leftAnchor=row;}post(()->leftAnchor.requestFocus());}
  void update(Snapshot next){snapshot=next;sourceIssues.clear();for(List<Entry> group:groups)group.clear();boolean importing=com.archos.mediaprovider.ImportState.VIDEO.isInitialImport()||com.archos.mediaprovider.ImportState.VIDEO.isRegularImport()||com.archos.mediaprovider.video.LoaderUtils.getScrapeInProgress();android.content.SharedPreferences health=getContext().getSharedPreferences(com.archos.mediaprovider.video.SupernovaLibraryPolicy.HEALTH,0);
   for(Entry entry:next.technical){if(!(entry.media instanceof Video))continue;Video video=(Video)entry.media;boolean matched=video instanceof Movie||video instanceof Episode;
    if(!importing&&!matched)groups.get(0).add(entry);if(health.getBoolean("incorrect:"+video.getId(),false))groups.get(1).add(entry);
    if(!importing&&matched&&(video.getPosterUri()==null||plot(video).trim().isEmpty()))groups.get(2).add(entry);
    if(LibraryHealth.state(getContext(),video)!=LibraryHealth.State.AVAILABLE)groups.get(3).add(entry);

   }
   for(String key:health.getAll().keySet())if(key.startsWith("offline:")||key.startsWith("problem:"))sourceIssues.add(key.substring(8));
   for(int i=0;i<names.length;i++)categoryRows.get(i).setText(names[i]+"  ("+(i==4?sourceIssues.size():groups.get(i).size())+")");choose(category);
  }
  void choose(int selected){if(rendering)return;rendering=true;try{leftAnchor=categoryRows.get(selected);Object anchor=issueAnchor==null?null:issueAnchor.getTag();boolean fromRight=context.hasFocus(),restore=issues.hasFocus()||fromRight;String action=fromRight&&findFocus() instanceof TextView?((TextView)findFocus()).getText().toString():null;category=selected;boolean previousReview=reviewing;reviewing=true;try{issues.removeAllViews();context.removeAllViews();context.addView(description);}finally{reviewing=previousReview;}issues.addView(SharedThreePanel.text(getContext(),names[selected],21));List<Entry> entries=groups.get(selected);
   if(selected==4&&!sourceIssues.isEmpty()){for(String source:sourceIssues){TextView row=SharedThreePanel.action(getContext(),source,()->reviewSource(source));row.setTag("health.source:"+source);row.setOnFocusChangeListener((v,yes)->{if(yes){issueAnchor=v;reviewSource(source);}});issues.addView(row);}reviewSource(sourceIssues.iterator().next());if(restore)restoreIssue(anchor,fromRight,action);return;}
   if(entries.isEmpty()){boolean healthy=sourceIssues.isEmpty();for(List<Entry> group:groups)if(!group.isEmpty())healthy=false;issues.addView(SharedThreePanel.text(getContext(),healthy?"Your library is healthy\n\nNo issues need your attention.":"No issues in this category.",16));description.setText(selected==1?"Incorrect matches appear here when you flag a title. Open a title’s details and select Find a Match to correct its identification.":"Importing and identification still in progress are excluded from unmatched warnings.");if(selected==4)description.setText(LibraryHealth.report(getContext()));if(restore){issueAnchor=null;categoryRows.get(selected).requestFocus();}return;}
   for(Entry entry:entries){Video video=(Video)entry.media;TextView row=SharedThreePanel.action(getContext(),video.getFilenameNonCryptic(),()->review(video));row.setTag("health:"+video.getId());row.setOnFocusChangeListener((v,focused)->{if(focused){issueAnchor=v;review(video);}});issues.addView(row);}if(restore)restoreIssue(anchor,fromRight,action);else review((Video)entries.get(0).media);
  }finally{rendering=false;}}
  void restoreIssue(Object identity,boolean right,String action){View target=null;for(int n=1;n<issues.getChildCount();n++){View row=issues.getChildAt(n);if(target==null)target=row;if(Objects.equals(identity,row.getTag())){target=row;break;}}
   if(target==null){categoryRows.get(category).requestFocus();return;}target.requestFocus();if(right){View first=null;for(int n=1;n<context.getChildCount();n++){View button=context.getChildAt(n);if(!button.isFocusable())continue;if(first==null)first=button;if(button instanceof TextView&&Objects.equals(action,((TextView)button).getText().toString())){button.requestFocus();return;}}if(first!=null)first.requestFocus();}
  }
  void reviewSource(String source){if(reviewing)return;reviewing=true;try{context.removeAllViews();context.addView(description);description.setText("Source Problem\n\n"+source+"\n\nReconnect storage or check access to this location. Library records and viewing progress are kept. Partial directory failures do not mark the whole source offline.");context.addView(SharedThreePanel.action(getContext(),"Scan Library",()->PreviewLibraryScan.request(getContext())));}finally{reviewing=false;}}
  void review(Video video){if(reviewing)return;reviewing=true;try{context.removeAllViews();context.addView(description);String status=video instanceof Movie||video instanceof Episode?"Matched: "+video.getName():"Unmatched";
   description.setText("Filename\n"+video.getFilenameNonCryptic()+"\n\nSource / Location\n"+com.archos.mediaprovider.video.SupernovaLibraryPolicy.canonical(video.getFileUri())+"\n\nMatch status\n"+status+"\n\nAvailability\n"+LibraryHealth.message(LibraryHealth.state(getContext(),video)));
   context.addView(SharedThreePanel.action(getContext(),"View Details",()->startActivity(new Intent(LibraryHealthActivity.this,VideoDetailsActivity.class).putExtra(VideoDetailsFragment.EXTRA_VIDEO,video))));
   context.addView(SharedThreePanel.action(getContext(),"Find a Match",()->startActivity(new Intent(LibraryHealthActivity.this,ManualVideoScrappingActivity.class).putExtra(ManualVideoScrappingActivity.EXTRA_VIDEO,video))));
   if(category==1)context.addView(SharedThreePanel.action(getContext(),"Match is correct",()->{getContext().getSharedPreferences(com.archos.mediaprovider.video.SupernovaLibraryPolicy.HEALTH,0).edit().remove("incorrect:"+video.getId()).apply();update(snapshot);}));
   if(category>=3)context.addView(SharedThreePanel.action(getContext(),"Check Sources / Scan Library",()->PreviewLibraryScan.request(getContext())));
  }finally{reviewing=false;}}
  @Override public boolean dispatchKeyEvent(KeyEvent event){if(event.getAction()==KeyEvent.ACTION_DOWN){int key=event.getKeyCode();if(categories.hasFocus()){if(key==KeyEvent.KEYCODE_DPAD_LEFT)return true;if(key==KeyEvent.KEYCODE_DPAD_RIGHT){if(issues.getChildCount()>1&&issues.getChildAt(1).isFocusable())issues.getChildAt(1).requestFocus();return true;}}else if(issues.hasFocus()){if(key==KeyEvent.KEYCODE_DPAD_LEFT){leftAnchor.requestFocus();return true;}if(key==KeyEvent.KEYCODE_DPAD_RIGHT){if(context.getChildCount()>1)context.getChildAt(1).requestFocus();return true;}}else if(context.hasFocus()){if(key==KeyEvent.KEYCODE_DPAD_LEFT){if(issueAnchor!=null)issueAnchor.requestFocus();return true;}if(key==KeyEvent.KEYCODE_DPAD_RIGHT)return true;}}return super.dispatchKeyEvent(event);}
  LinearLayout column(){LinearLayout column=new LinearLayout(LibraryHealthActivity.this);column.setOrientation(LinearLayout.VERTICAL);return column;}
 }
 static String plot(Video video){return value(video.getDescriptionBody());}
 static String value(String value){return value==null?"":value;}
}
