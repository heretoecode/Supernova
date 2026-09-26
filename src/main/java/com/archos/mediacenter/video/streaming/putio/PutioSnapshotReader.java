package com.archos.mediacenter.video.streaming.putio;

import java.util.*;

/** Traversal is complete only after every folder and cursor succeeds; never writes library state. */
public final class PutioSnapshotReader {
    public interface Pages {PutioReadClient.Page read(long parent,String cursor)throws PutioReadClient.Unavailable;}
    private static final class Folder {final long id;final String path;Folder(long id,String path){this.id=id;this.path=path;}}
    public static PutioReconciliation.Snapshot read(String scope,long root,Pages pages){
        PutioReconciliation.Snapshot snapshot=new PutioReconciliation.Snapshot(scope,"folder:"+root);
        Deque<Folder> folders=new ArrayDeque<>();folders.add(new Folder(root,""));Set<Long> visited=new HashSet<>();
        try{
            while(!folders.isEmpty()){
                Folder folder=folders.removeFirst();
                if(!visited.add(folder.id)){snapshot.fail(PutioReconciliation.Failure.INVALID_PAGE);return snapshot;}
                String cursor=null;Set<String> cursors=new HashSet<>();long count=0,total=-1;int page=0;
                do{
                    if(Thread.currentThread().isInterrupted()){snapshot.fail(PutioReconciliation.Failure.CANCELLED);return snapshot;}
                    PutioReadClient.Page data=pages.read(folder.id,cursor);
                    if(data.total>=0){if(total>=0&&total!=data.total){snapshot.fail(PutioReconciliation.Failure.INVALID_PAGE);return snapshot;}total=data.total;}
                    count+=data.items.size();List<PutioReconciliation.File> media=new ArrayList<>();List<String> more=new ArrayList<>();
                    for(PutioReadClient.Item item:data.items){String path=folder.path+item.name;
                        if(item.folder()){folders.addLast(new Folder(item.id,path+"/"));more.add("folder:"+item.id);}
                        else if(item.video())media.add(new PutioReconciliation.File(item.id,item.parentId,path,item.size));
                    }
                    if(data.cursor!=null){if(!cursors.add(data.cursor)){snapshot.fail(PutioReconciliation.Failure.INVALID_PAGE);return snapshot;}more.add("folder:"+folder.id+":page:"+(page+1));}
                    snapshot.page(page==0?"folder:"+folder.id:"folder:"+folder.id+":page:"+page,media,more);
                    if(snapshot.failure()!=PutioReconciliation.Failure.NONE)return snapshot;
                    cursor=data.cursor;page++;
                }while(cursor!=null);
                if(total>=0&&count!=total){snapshot.fail(PutioReconciliation.Failure.INVALID_PAGE);return snapshot;}
            }
            snapshot.finish();
        }catch(PutioReadClient.Unavailable error){snapshot.fail(error.reason);}
        catch(IllegalArgumentException invalid){snapshot.fail(PutioReconciliation.Failure.INVALID_PAGE);}
        return snapshot;
    }
    private PutioSnapshotReader(){}
}
