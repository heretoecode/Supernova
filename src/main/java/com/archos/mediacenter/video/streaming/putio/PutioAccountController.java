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
    private PutioAccountController(Activity activity){this.activity=activity;tokens=new PutioTokenStore(activity);}
    public static void open(Context context){while(context instanceof ContextWrapper&&!(context instanceof Activity))context=((ContextWrapper)context).getBaseContext();if(context instanceof Activity)new PutioAccountController((Activity)context).load();}
    private interface Work {Runnable run()throws Exception;}
    private void work(String title,Work work){
        int current=++generation;if(task!=null)task.cancel(true);replace(PreviewDialog.read(activity,title,"Please wait…"));
        dialog.setOnDismissListener(d->{if(current==generation){generation++;if(task!=null)task.cancel(true);}});
        task=StreamingRepository.IO.submit(()->{try{Runnable update=work.run();activity.runOnUiThread(()->{if(alive(current)){generation++;update.run();}});}catch(Exception failure){
            boolean reconnect=failure instanceof java.io.IOException&&!(failure instanceof PutioReadClient.Unavailable)||failure instanceof PutioReadClient.Unavailable&&((PutioReadClient.Unavailable)failure).reason==PutioReconciliation.Failure.UNAUTHORISED;
            activity.runOnUiThread(()->{if(alive(current)){generation++;replace(PreviewDialog.read(activity,reconnect?"Reconnect put.io":"put.io unavailable",reconnect?"Please reconnect your put.io account. Your library and WebDAV playback sources have been kept.":"The account service is unavailable. Please try again later. Your indexed library and WebDAV playback sources have been kept."));}});
        }});
    }
    private boolean alive(int current){return generation==current&&!activity.isFinishing()&&!activity.isDestroyed();}
    private void replace(Dialog next){Dialog previous=dialog;dialog=next;if(previous!=null){previous.setOnDismissListener(null);previous.dismiss();}}
    private void load(){work("put.io",()->{String token=tokens.read();if(token==null)return this::disconnected;client=new PutioReadClient(token);account=client.account();return this::overview;});}
    private void disconnected(){replace(PreviewDialog.choose(activity,"put.io",new String[]{"Connect put.io"},0,index->replace(PreviewDialog.read(activity,"Connect put.io","Account linking is awaiting the registered Supernova OAuth configuration. Existing WebDAV library access and playback remain available."))));}
    private void overview(){String title="put.io · "+account.username+"\nStorage used: "+Formatter.formatFileSize(activity,account.usedBytes)+" of "+Formatter.formatFileSize(activity,account.totalBytes);
        replace(PreviewDialog.choose(activity,title,new String[]{"Browse Files","Account / Connection","Disconnect"},0,index->{if(index==0)browse(0,"put.io",new ArrayList<>());else if(index==1)replace(PreviewDialog.read(activity,"Account / Connection",account.username+"\n"+account.status+"\n\nAPI discovery and WebDAV playback use independent connections."));else disconnect();}));
    }
    private static final class Folder {final long id;final String name;Folder(long id,String name){this.id=id;this.name=name;}}
    private void browse(long folder,String name,List<Folder> parents){browsePage(folder,name,parents,null,Collections.emptySet());}
    private void browsePage(long folder,String name,List<Folder> parents,String cursor,Set<String> visited){work("Browse "+name,()->{
        PutioReadClient.Page page=client.list(folder,cursor);Set<String> seen=new HashSet<>(visited);if(cursor!=null)seen.add(cursor);
        if(page.cursor!=null&&seen.contains(page.cursor))throw new PutioReadClient.Unavailable(PutioReconciliation.Failure.INVALID_PAGE,0);
        List<PutioReadClient.Item> items=new ArrayList<>(page.items);
        items.sort(Comparator.comparing((PutioReadClient.Item item)->!item.folder()).thenComparing(item->item.name,String.CASE_INSENSITIVE_ORDER));
        return ()->{String[] labels=new String[items.size()+1+(page.cursor==null?0:1)];labels[0]=parents.isEmpty()?"Back to account":"Parent folder";for(int i=0;i<items.size();i++)labels[i+1]=(items.get(i).folder()?"Folder · ":"")+items.get(i).name;if(page.cursor!=null)labels[labels.length-1]="Next page";
            replace(PreviewDialog.choose(activity,name,labels,0,index->{if(index==0){if(parents.isEmpty())overview();else{List<Folder> previous=new ArrayList<>(parents);Folder parent=previous.remove(previous.size()-1);browse(parent.id,parent.name,previous);}}
                else if(index>items.size())browsePage(folder,name,parents,page.cursor,seen);
                else{PutioReadClient.Item item=items.get(index-1);if(item.folder()){List<Folder> next=new ArrayList<>(parents);next.add(new Folder(folder,name));browse(item.id,item.name,next);}else replace(PreviewDialog.read(activity,item.name,Formatter.formatFileSize(activity,item.size)+"\n\nPlay indexed media through its existing WebDAV library source."));}}));};
    });}
    private void disconnect(){replace(PreviewDialog.choose(activity,"Disconnect put.io? Keep media and library history.",new String[]{"Cancel","Keep associated sources inactive","Return associated sources to generic discovery"},0,index->{if(index==0){overview();return;}work("Disconnect put.io",()->{try(PutioAssociationStore store=new PutioAssociationStore(activity)){store.disconnectAccount(account.id,index==1?PutioAssociationStore.DisconnectChoice.KEEP_INACTIVE:PutioAssociationStore.DisconnectChoice.REVERT_TO_GENERIC);}tokens.clear();client=null;return this::disconnected;});}));}
}
