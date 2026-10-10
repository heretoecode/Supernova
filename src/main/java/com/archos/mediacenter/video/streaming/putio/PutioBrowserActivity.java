package com.archos.mediacenter.video.streaming.putio;

import androidx.activity.ComponentActivity;
import androidx.activity.OnBackPressedCallback;
import android.os.Bundle;
import android.view.*;
import android.widget.*;
import com.archos.mediacenter.video.leanback.PreviewDialog;
import com.archos.mediacenter.video.leanback.filebrowsing.PreviewBrowserSurface;
import com.archos.mediacenter.video.streaming.StreamingRepository;
import java.util.*;
import java.util.concurrent.Future;

/** Read-only provider adapter for the shared three-panel browser. No transport credentials in intents. */
public final class PutioBrowserActivity extends ComponentActivity {
    private PreviewBrowserSurface surface;private LinearLayout rows;private Future<?> task;private int generation;
    private boolean ancestryReady;private PutioReadClient client;private long folder;private String title="put.io",cursor;
    private final Deque<Folder> parents=new ArrayDeque<>();private final Set<String> cursors=new HashSet<>();
    private static final class Folder {final long id,selected;final String title;Folder(long id,String title,long selected){this.id=id;this.title=title;this.selected=selected;}}
    @Override public void onCreate(Bundle state){super.onCreate(state);
        getOnBackPressedDispatcher().addCallback(this,new OnBackPressedCallback(true){@Override public void handleOnBackPressed(){back();}});
        rows=new LinearLayout(this);rows.setOrientation(LinearLayout.VERTICAL);rows.setClipChildren(false);rows.setClipToPadding(false);
        ScrollView listing=new ScrollView(this);listing.setClipChildren(false);listing.setClipToPadding(false);listing.addView(rows);
        surface=new PreviewBrowserSurface(this,listing,android.net.Uri.parse("putio://put.io/"),this::options);setContentView(surface);
        folder=getIntent().getLongExtra("folder",0);title=getIntent().getStringExtra("folder_name");if(title==null)title="put.io";
        if(state!=null){folder=state.getLong("folder",0);title=state.getString("title","put.io");}
        load(null,-1);
    }
    private void options(){PreviewDialog.choose(this,"put.io browser",new String[]{"Refresh folder","Account / Connection"},0,index->{if(index==0){cursors.clear();load(null,-1);}else{account();}});}
    private void account(){generation++;if(task!=null)task.cancel(true);client=null;PutioAccountController.open(this);}
    private interface Work {Runnable run()throws Exception;}
    private void work(Work work){final int request=++generation;if(task!=null)task.cancel(true);
        task=StreamingRepository.IO.submit(()->{try{Runnable update=work.run();runOnUiThread(()->{if(alive(request))update.run();});}catch(Exception error){boolean auth=error instanceof PutioReadClient.Unavailable&&((PutioReadClient.Unavailable)error).reason==PutioReconciliation.Failure.UNAUTHORISED;
            runOnUiThread(()->{if(alive(request)){if(auth)client=null;PreviewDialog.choose(this,auth?"Reconnect put.io · library retained":"Folder unavailable · library retained",new String[]{"Close","Retry","Account / Connection"},0,index->{if(index==1)load(cursor,-1);else if(index==2){account();}});}});}
        });
    }
    private boolean alive(int request){return request==generation&&!isFinishing()&&!isDestroyed();}
    private void load(String page,long restore){cursor=page;final long parent=folder;final Set<String> visited=new HashSet<>(cursors);rows.removeAllViews();rows.addView(label("Loading "+title+"…"));
        PutioReadClient existing=client;
        work(()->{PutioReadClient candidate=existing;if(candidate==null){String token=new PutioTokenStore(getApplicationContext()).read();if(token==null)throw new PutioReadClient.Unavailable(PutioReconciliation.Failure.UNAUTHORISED,0);candidate=new PutioReadClient(token);long expected=getIntent().getLongExtra("account",0);if(expected>0&&candidate.account().id!=expected)throw new PutioReadClient.Unavailable(PutioReconciliation.Failure.UNAUTHORISED,0);}
            List<Folder> recovered=null;
            if(!ancestryReady){recovered=new ArrayList<>();Set<Long> seen=new HashSet<>();long ancestor=parent;long selected=parent;
                while(ancestor!=0){if(recovered.size()>=128||!seen.add(ancestor))throw new PutioReadClient.Unavailable(PutioReconciliation.Failure.INVALID_PAGE,0);PutioReadClient.Item item=candidate.file(ancestor).item;if(item.id!=ancestor||!item.folder())throw new PutioReadClient.Unavailable(PutioReconciliation.Failure.INVALID_PAGE,0);if(ancestor!=parent)recovered.add(new Folder(item.id,item.name,selected));selected=ancestor;ancestor=item.parentId;}
                if(parent!=0)recovered.add(new Folder(0,"put.io",selected));Collections.reverse(recovered);
            }
            PutioReadClient.Page result=candidate.list(parent,page);if(result.cursor!=null&&(result.cursor.equals(page)||visited.contains(result.cursor)))throw new PutioReadClient.Unavailable(PutioReconciliation.Failure.INVALID_PAGE,0);
            PutioReadClient verified=candidate;List<Folder> recoveredPath=recovered;return ()->{client=verified;if(recoveredPath!=null){parents.clear();for(Folder ancestor:recoveredPath)parents.push(ancestor);ancestryReady=true;}render(result,restore);};
        });
    }
    private void render(PutioReadClient.Page page,long restore){breadcrumbs();rows.removeAllViews();rows.addView(label(title));
        if(folder!=0)add("Parent folder",-1,this::back);
        if(cursor!=null)add("First page",-2,()->{cursors.clear();load(null,-1);});
        List<PutioReadClient.Item> items=new ArrayList<>(page.items);items.sort(Comparator.comparing((PutioReadClient.Item item)->!item.folder()).thenComparing(item->item.name,String.CASE_INSENSITIVE_ORDER));
        TextView first=null,restored=null;
        for(PutioReadClient.Item item:items){Runnable open=()->{if(item.folder()){parents.push(new Folder(folder,title,item.id));folder=item.id;title=item.name;cursors.clear();load(null,-1);}else info(item);};
            TextView row=add((item.folder()?"Folder · ":"")+item.name,item.id,open);
            row.setOnFocusChangeListener((view,focused)->{if(focused)surface.providerItem(item.name,item.folder()?"Folder":android.text.format.Formatter.formatFileSize(this,item.size)+"\n\nOriginal-quality playback remains in the indexed WebDAV library.",item.folder(),open);});
            if(first==null)first=row;if(item.id==restore)restored=row;
        }
        if(page.cursor!=null)add("Next page",-3,()->{if(cursor!=null)cursors.add(cursor);load(page.cursor,-1);});
        if(items.isEmpty())rows.addView(label("No files in this page"));
        View target=restored!=null?restored:first;if(target==null)for(int i=0;i<rows.getChildCount();i++)if(rows.getChildAt(i).isFocusable()){target=rows.getChildAt(i);break;}
        if(target!=null)target.requestFocus();else surface.providerItem(title,"Empty folder",true,()->{});
    }
    private void breadcrumbs(){List<Folder> trail=new ArrayList<>();parents.descendingIterator().forEachRemaining(trail::add);trail.add(new Folder(folder,title,-1));List<String> labels=new ArrayList<>();List<Runnable> navigate=new ArrayList<>();
        for(int n=0;n<trail.size();n++){Folder ancestor=trail.get(n);List<Folder> before=new ArrayList<>(trail.subList(0,n));labels.add(ancestor.title);navigate.add(()->{parents.clear();for(Folder previous:before)parents.push(previous);folder=ancestor.id;title=ancestor.title;cursors.clear();load(null,-1);});}
        surface.providerPath(labels,navigate);
    }
    private void info(PutioReadClient.Item item){work(()->{PutioReadClient.FileInfo info=client.file(item.id);return ()->PreviewDialog.read(this,info.item.name,android.text.format.Formatter.formatFileSize(this,info.item.size)+"\n"+info.description);});}
    private TextView label(String text){TextView v=com.archos.mediacenter.video.leanback.SharedThreePanel.text(this,text,18);v.setPadding(dp(10),dp(10),dp(10),dp(10));return v;}
    private TextView add(String text,long id,Runnable action){TextView v=label(text);v.setFocusable(true);v.setFocusableInTouchMode(true);v.setSingleLine(true);v.setEllipsize(android.text.TextUtils.TruncateAt.MIDDLE);v.setTag("semantic:putio.file."+id);v.setBackground(PreviewDialog.focus(this));v.setOnClickListener(view->action.run());rows.addView(v,new LinearLayout.LayoutParams(-1,dp(50)));return v;}
    private int dp(int value){return PreviewDialog.dp(this,value);}
    private void back(){if(!parents.isEmpty()){Folder previous=parents.pop();folder=previous.id;title=previous.title;cursors.clear();load(null,previous.selected);}else if(folder!=0){folder=0;title="put.io";cursors.clear();load(null,-1);}else finish();}
    @Override protected void onSaveInstanceState(Bundle state){state.putLong("folder",folder);state.putString("title",title);super.onSaveInstanceState(state);}
    @Override protected void onDestroy(){generation++;if(task!=null)task.cancel(true);client=null;super.onDestroy();}
}
