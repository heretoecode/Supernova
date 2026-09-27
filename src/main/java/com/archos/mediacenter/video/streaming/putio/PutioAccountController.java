package com.archos.mediacenter.video.streaming.putio;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.ContextWrapper;
import android.text.format.Formatter;
import com.archos.mediacenter.video.leanback.PreviewDialog;
import com.archos.mediacenter.video.streaming.StreamingRepository;
import java.util.*;
import java.util.concurrent.Future;

/** TV account and read-only API browsing. Credentials never enter view state or diagnostics. */
public final class PutioAccountController {
    private final Activity activity;
    private final PutioTokenStore tokens;
    private Future<?> task;
    private Dialog dialog;
    private int generation;
    private PutioReadClient client;
    private PutioReadClient.Account account;
    private PutioAssociationStore.Scope changing;
    private PutioAccountController(Activity activity){this.activity=activity;tokens=new PutioTokenStore(activity);}
    public static void open(Context context){while(context instanceof ContextWrapper&&!(context instanceof Activity))context=((ContextWrapper)context).getBaseContext();if(context instanceof Activity)new PutioAccountController((Activity)context).load();}
    private interface Work {Runnable run()throws Exception;}
    private void work(String title,Work work){
        int current=++generation;if(task!=null)task.cancel(true);replace(PreviewDialog.read(activity,title,"Please wait…"));
        dialog.setOnDismissListener(d->{if(current==generation){generation++;if(task!=null)task.cancel(true);}});
        task=StreamingRepository.IO.submit(()->{try{Runnable update=work.run();activity.runOnUiThread(()->{if(alive(current)){generation++;update.run();}});}catch(Exception failure){
            boolean reconnect=failure instanceof java.io.IOException&&!(failure instanceof PutioReadClient.Unavailable)||failure instanceof PutioReadClient.Unavailable&&((PutioReadClient.Unavailable)failure).reason==PutioReconciliation.Failure.UNAUTHORISED;
            activity.runOnUiThread(()->{if(alive(current)){generation++;if(reconnect)replace(PreviewDialog.choose(activity,"Reconnect put.io · your library has been kept",new String[]{"Cancel","Reconnect put.io"},0,index->{if(index==1)PutioAuthFlow.open(activity,this::load);}));else replace(PreviewDialog.read(activity,"put.io operation did not finish","Please retry or review the selected source. Completed imports may remain indexed safely. No library history has been deleted, and WebDAV playback remains independent."));}});
        }});
    }
    private boolean alive(int current){return generation==current&&!activity.isFinishing()&&!activity.isDestroyed();}
    private void replace(Dialog next){Dialog previous=dialog;dialog=next;if(previous!=null){previous.setOnDismissListener(null);previous.dismiss();}}
    private android.content.SharedPreferences selections(){return activity.getSharedPreferences("putio-library-selection-v1",0);}
    private void load(){work("put.io",()->{String token=tokens.read();if(token==null)return this::disconnected;client=new PutioReadClient(token);account=client.account();long previous=selections().getLong("account",0);if(previous>0&&previous!=account.id)return ()->accountChanged(previous);selections().edit().putLong("account",account.id).apply();return this::overview;});}
    private void accountChanged(long previous){replace(PreviewDialog.choose(activity,"Different put.io account · keep the previous library and choose discovery ownership",new String[]{"Cancel","Keep previous sources inactive","Return previous sources to generic discovery"},0,index->{if(index==0)return;work("Change account",()->{try(PutioAssociationStore store=new PutioAssociationStore(activity)){store.disconnectAccount(previous,index==1?PutioAssociationStore.DisconnectChoice.KEEP_INACTIVE:PutioAssociationStore.DisconnectChoice.REVERT_TO_GENERIC);}selections().edit().putLong("account",account.id).apply();return this::overview;});}));}
    private void disconnected(){replace(PreviewDialog.choose(activity,"put.io",new String[]{"Connect put.io"},0,index->PutioAuthFlow.open(activity,this::load)));}
    private void overview(){work("put.io account",()->{
        StringBuilder summary=new StringBuilder("put.io · ").append(account.username).append(" · ").append(account.status).append("\nStorage used: ").append(Formatter.formatFileSize(activity,account.usedBytes)).append(" of ").append(Formatter.formatFileSize(activity,account.totalBytes));
        try(PutioAssociationStore store=new PutioAssociationStore(activity)){List<PutioAssociationStore.Scope> scopes=store.scopes(account.id);
            for(String kind:new String[]{"movie","tv"}){PutioAssociationStore.Scope chosen=null;for(PutioAssociationStore.Scope scope:scopes)if(kind.equals(selections().getString("kind:"+account.id+":"+scope.folderId,""))&&(chosen==null||scope.ownership==PutioAssociationStore.Ownership.API))chosen=scope;
                summary.append("\n").append(kind.equals("movie")?"Movies: ":"TV Shows: ");
                if(chosen==null){summary.append("Not associated");continue;}String key=account.id+":"+chosen.folderId;String name=selections().getString("name:"+key,chosen.source.getPath());
                summary.append(name!=null&&name.length()>60?"…"+name.substring(name.length()-59):name);
                summary.append(" · ").append(chosen.ownership==PutioAssociationStore.Ownership.API?selections().getString("status:"+key,"Connected"):chosen.ownership==PutioAssociationStore.Ownership.INACTIVE?"Inactive":"Needs review");
                long synced=selections().getLong("sync:"+key,0);summary.append(" · Last sync: ").append(synced>0?android.text.format.DateUtils.getRelativeTimeSpanString(synced):"Not yet synced");
            }
        }
        return ()->showOverview(summary.toString());
    });}
    private void showOverview(String title){
        replace(PreviewDialog.choose(activity,title,new String[]{"Library folders / Sync Now","Associate Movies folder","Associate TV Shows folder","Change Library Folders","Browse Files","Search Files","Account / Connection","Disconnect"},0,index->{
            changing=null;if(index==0)scopes(false);else if(index==1||index==2)chooseFolder(0,"put.io",index==1?"movie":"tv",null,Collections.emptySet());
            else if(index==3)scopes(true);else if(index==4)activity.startActivity(new android.content.Intent(activity,PutioBrowserActivity.class).putExtra("account",account.id));else if(index==5)searchInput("");else if(index==6)replace(PreviewDialog.read(activity,"Account / Connection",account.username+"\n"+account.status+"\n\nAPI discovery and WebDAV playback use independent connections."));else disconnect();}));
    }
    private void scopes(boolean changeFolder){work("Library folders",()->{
        List<PutioAssociationStore.Scope> scopes;try(PutioAssociationStore store=new PutioAssociationStore(activity)){scopes=store.scopes(account.id);}
        return ()->{if(scopes.isEmpty()){replace(PreviewDialog.read(activity,"Library folders","Choose Associate Movies folder or Associate TV Shows folder to link an existing WebDAV source without importing it twice."));return;}
            String[] labels=new String[scopes.size()];for(int i=0;i<labels.length;i++){PutioAssociationStore.Scope scope=scopes.get(i);String key=account.id+":"+scope.folderId;long synced=selections().getLong("sync:"+key,0);String status=scope.ownership==PutioAssociationStore.Ownership.API?selections().getString("status:"+key,"Connected"):scope.ownership==PutioAssociationStore.Ownership.REASSIGNING?"Folder change pending":scope.ownership==PutioAssociationStore.Ownership.PREPARING?"Association pending":scope.ownership==PutioAssociationStore.Ownership.INACTIVE?"Inactive":"Generic discovery";labels[i]=selections().getString("name:"+key,scope.source.getPath())+" · "+status+(synced>0?" · "+android.text.format.DateUtils.getRelativeTimeSpanString(synced):"");}
            replace(PreviewDialog.choose(activity,changeFolder?"Choose the folder association to change":"Sync a library folder",labels,0,index->{PutioAssociationStore.Scope scope=scopes.get(index);
                if(scope.ownership==PutioAssociationStore.Ownership.REASSIGNING){resumeReassignment(scope.folderId);return;}
                if(changeFolder){changing=scope;chooseFolder(0,"put.io",selections().getString("kind:"+account.id+":"+scope.folderId,"movie"),null,Collections.emptySet());return;}
                if(scope.ownership==PutioAssociationStore.Ownership.API||scope.ownership==PutioAssociationStore.Ownership.PREPARING)sync(scope.folderId,scope.source);else replace(PreviewDialog.choose(activity,"Resume native discovery for this retained association?",new String[]{"Cancel","Review & Resume"},0,choice->{if(choice==1)work("Resume association",()->{try(PutioAssociationStore store=new PutioAssociationStore(activity)){store.resume(account.id,scope.folderId);}return ()->sync(scope.folderId,scope.source);});}));}));};
    });}
    private void chooseFolder(long folder,String name,String kind,String cursor,Set<String> visited){work("Choose "+(kind.equals("movie")?"Movies":"TV Shows")+" folder",()->{
        PutioReadClient.Page page=client.list(folder,cursor);Set<String> seen=new HashSet<>(visited);if(cursor!=null)seen.add(cursor);
        if(page.cursor!=null&&seen.contains(page.cursor))throw new PutioReadClient.Unavailable(PutioReconciliation.Failure.INVALID_PAGE,0);
        List<PutioReadClient.Item> folders=new ArrayList<>();for(PutioReadClient.Item item:page.items)if(item.folder())folders.add(item);
        return ()->{String[] labels=new String[folders.size()+2+(page.cursor==null?0:1)];labels[0]="Use this folder · "+name;labels[1]="Back to root";for(int i=0;i<folders.size();i++)labels[i+2]=folders.get(i).name;if(page.cursor!=null)labels[labels.length-1]="Next page";
            replace(PreviewDialog.choose(activity,name,labels,0,index->{if(index==0)chooseSource(folder,name,kind);else if(index==1)chooseFolder(0,"put.io",kind,null,Collections.emptySet());else if(index>=folders.size()+2)chooseFolder(folder,name,kind,page.cursor,seen);else{PutioReadClient.Item child=folders.get(index-2);chooseFolder(child.id,name+"/"+child.name,kind,null,Collections.emptySet());}}));};
    });}
    private void chooseSource(long folder,String name,String kind){work("Choose existing WebDAV playback source",()->{
        List<android.net.Uri> sources=new ArrayList<>();
        try(android.database.Cursor c=com.archos.mediacenter.utils.ShortcutDbAdapter.VIDEO.getAllShortcuts(activity,null,null)){
            if(c==null)throw new IllegalStateException("Sources unavailable");
            int path=c.getColumnIndexOrThrow(com.archos.mediacenter.utils.ShortcutDbAdapter.KEY_PATH);
            while(c.moveToNext()){android.net.Uri uri=android.net.Uri.parse(c.getString(path));if(Arrays.asList("https","http","webdav","webdavs","dav","davs").contains(uri.getScheme())&&uri.getUserInfo()==null&&uri.getQuery()==null&&uri.getFragment()==null)sources.add(uri);}
        }
        return ()->{if(sources.isEmpty()){replace(PreviewDialog.read(activity,"WebDAV source required","Add the corresponding put.io WebDAV folder in Network Shares first. Native discovery will retain that source for original-quality playback."));return;}
            String[] labels=new String[sources.size()];for(int i=0;i<labels.length;i++)labels[i]=sources.get(i).getHost()+sources.get(i).getPath();
            replace(PreviewDialog.choose(activity,"Which WebDAV source is the same folder as "+name+"?",labels,0,index->{android.net.Uri source=sources.get(index);replace(PreviewDialog.choose(activity,"Preserve your existing library and link matching files rather than importing them again?",new String[]{"Cancel","Save & Continue"},0,confirm->{if(confirm!=1)return;androidx.preference.PreferenceManager.getDefaultSharedPreferences(activity).edit().putString("preview_source_kind:"+source,kind).apply();selections().edit().putString("name:"+account.id+":"+folder,name).putString("kind:"+account.id+":"+folder,kind).apply();if(changing==null)sync(folder,source);else confirmReassignment(folder,source);}));}));};
    });}
    private void confirmReassignment(long folder,android.net.Uri source){PutioAssociationStore.Scope previous=changing;
        if(previous.folderId==folder&&previous.source.equals(source)){changing=null;sync(folder,source);return;}
        replace(PreviewDialog.choose(activity,"After the new folder is read completely, preserve history and retire the previous source:",new String[]{"Cancel","Keep previous source inactive","Return previous source to generic discovery"},0,index->{if(index==0)return;work("Change library folder",()->{PutioSync.Review result=PutioReassignment.run(activity,client,account.id,previous.folderId,folder,source,index==1?PutioAssociationStore.DisconnectChoice.KEEP_INACTIVE:PutioAssociationStore.DisconnectChoice.REVERT_TO_GENERIC);return ()->{changing=null;applyReview(result,Collections.emptyMap());};});}));
    }
    private void resumeReassignment(long folder){work("Resume library folder change",()->{PutioReassignment.Pending pending=PutioReassignment.pending(activity,account.id,folder);if(pending==null)throw new IllegalStateException("Source change unavailable");PutioSync.Review result=PutioReassignment.run(activity,client,pending.account,pending.oldFolder,pending.newFolder,pending.newSource,pending.choice);return ()->applyReview(result,Collections.emptyMap());});}
    private void sync(long folder,android.net.Uri source){work("Read complete put.io library",()->{PutioSync.Review review=PutioSync.fetch(activity,client,account.id,folder,source);return ()->applyReview(review,Collections.emptyMap());});}
    private void applyReview(PutioSync.Review review,Map<Long,Long> choices){work("Link and reconcile library",()->{PutioSync.Review result=PutioSync.apply(activity,review,choices);return ()->review(result);});}
    private void review(PutioSync.Review review){
        List<PutioReconciliation.Change> pending=new ArrayList<>();for(PutioReconciliation.Change change:review.plan.changes)if(change.decision==PutioReconciliation.Decision.NEEDS_REVIEW)pending.add(change);
        if(pending.isEmpty()){replace(PreviewDialog.read(activity,"Sync complete",review.matchedExisting()+" existing library items matched.\n"+review.newlyImported()+" new items imported.\nDuplicate playback paths created: 0.\n"+review.plan.missingMediaIds.size()+" missing files flagged for review; no library history deleted."));return;}
        PutioReconciliation.Change change=pending.get(0);List<String> labels=new ArrayList<>();labels.add("Finish Later");labels.add("Keep Separate");
        for(long id:change.candidates){String path="Library item "+id;for(PutioReconciliation.Existing row:review.existing)if(row.mediaId==id)path=row.relativePath+" · "+Formatter.formatFileSize(activity,row.size);labels.add("Same File · "+path);}
        replace(PreviewDialog.choose(activity,"Needs Review · put.io: "+change.file.relativePath+" · "+Formatter.formatFileSize(activity,change.file.size),labels.toArray(new String[0]),0,index->{if(index==0)return;applyReview(review,Collections.singletonMap(change.file.id,index==1?0L:change.candidates.get(index-2)));}));
    }
    private static final class Folder {final long id;final String name;Folder(long id,String name){this.id=id;this.name=name;}}
    private void searchInput(String previous){if(dialog!=null)dialog.dismiss();com.archos.mediacenter.video.leanback.PreviewTextInput.show(activity,"Search put.io files",previous,256,query->searchPage(query,null,Collections.emptySet()));}
    private void searchPage(String query,String cursor,Set<String> visited){work("Search put.io files",()->{
        PutioReadClient.Page page=client.search(query,cursor);Set<String> seen=new HashSet<>(visited);if(cursor!=null)seen.add(cursor);
        if(page.cursor!=null&&seen.contains(page.cursor))throw new PutioReadClient.Unavailable(PutioReconciliation.Failure.INVALID_PAGE,0);
        return ()->{String[] labels=new String[page.items.size()+2+(page.cursor==null?0:1)];labels[0]="Back to account";labels[1]="Change search";
            for(int i=0;i<page.items.size();i++){PutioReadClient.Item item=page.items.get(i);labels[i+2]=(item.folder()?"Folder · ":"")+item.name;}
            if(page.cursor!=null)labels[labels.length-1]="Next page";
            replace(PreviewDialog.choose(activity,"Search · "+query+" · "+page.total+" results",labels,0,index->{if(index==0)overview();else if(index==1)searchInput(query);else if(index>=page.items.size()+2)searchPage(query,page.cursor,seen);else{PutioReadClient.Item item=page.items.get(index-2);if(item.folder())browse(item.id,item.name,new ArrayList<>());else fileInfo(item,()->searchPage(query,cursor,visited));}}));};
    });}
    private void fileInfo(PutioReadClient.Item item,Runnable back){work("File information",()->{PutioReadClient.FileInfo info=client.file(item.id);return ()->{
        replace(PreviewDialog.read(activity,info.item.name,Formatter.formatFileSize(activity,info.item.size)+"\n"+info.description+"\n\nPlay indexed media through its existing WebDAV library source."));
        dialog.setOnDismissListener(d->{if(!activity.isFinishing()&&!activity.isDestroyed())back.run();});
    };});}
    private void browse(long folder,String name,List<Folder> parents){browsePage(folder,name,parents,null,Collections.emptySet());}
    private void browsePage(long folder,String name,List<Folder> parents,String cursor,Set<String> visited){work("Browse "+name,()->{
        PutioReadClient.Page page=client.list(folder,cursor);Set<String> seen=new HashSet<>(visited);if(cursor!=null)seen.add(cursor);
        if(page.cursor!=null&&seen.contains(page.cursor))throw new PutioReadClient.Unavailable(PutioReconciliation.Failure.INVALID_PAGE,0);
        List<PutioReadClient.Item> items=new ArrayList<>(page.items);
        items.sort(Comparator.comparing((PutioReadClient.Item item)->!item.folder()).thenComparing(item->item.name,String.CASE_INSENSITIVE_ORDER));
        return ()->{String[] labels=new String[items.size()+1+(page.cursor==null?0:1)];labels[0]=parents.isEmpty()?"Back to account":"Parent folder";for(int i=0;i<items.size();i++)labels[i+1]=(items.get(i).folder()?"Folder · ":"")+items.get(i).name;if(page.cursor!=null)labels[labels.length-1]="Next page";
            replace(PreviewDialog.choose(activity,name,labels,0,index->{if(index==0){if(parents.isEmpty())overview();else{List<Folder> previous=new ArrayList<>(parents);Folder parent=previous.remove(previous.size()-1);browse(parent.id,parent.name,previous);}}
                else if(index>items.size())browsePage(folder,name,parents,page.cursor,seen);
                else{PutioReadClient.Item item=items.get(index-1);if(item.folder()){List<Folder> next=new ArrayList<>(parents);next.add(new Folder(folder,name));browse(item.id,item.name,next);}else fileInfo(item,()->browsePage(folder,name,parents,cursor,visited));}}));};
    });}
    private void disconnect(){replace(PreviewDialog.choose(activity,"Disconnect put.io? Keep media and library history.",new String[]{"Cancel","Keep associated sources inactive","Return associated sources to generic discovery"},0,index->{if(index==0){overview();return;}work("Disconnect put.io",()->{try(PutioAssociationStore store=new PutioAssociationStore(activity)){store.disconnectAccount(account.id,index==1?PutioAssociationStore.DisconnectChoice.KEEP_INACTIVE:PutioAssociationStore.DisconnectChoice.REVERT_TO_GENERIC);}tokens.clear();client=null;return this::disconnected;});}));}
}
