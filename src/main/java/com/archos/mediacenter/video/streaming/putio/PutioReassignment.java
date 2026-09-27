package com.archos.mediacenter.video.streaming.putio;

import android.content.*;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import com.archos.mediaprovider.video.ProviderDiscoveryGate;
import java.util.*;

/** Explicit, resumable source change. The journal precedes native URI writes; no media is deleted. */
final class PutioReassignment {
    static final class Pending {
        final long account,oldFolder,newFolder;final Uri oldSource,newSource;final PutioAssociationStore.DisconnectChoice choice;
        Pending(long account,long oldFolder,long newFolder,Uri oldSource,Uri newSource,PutioAssociationStore.DisconnectChoice choice){this.account=account;this.oldFolder=oldFolder;this.newFolder=newFolder;this.oldSource=oldSource;this.newSource=newSource;this.choice=choice;}
    }
    static Pending pending(Context context,long account,long folder){try(PutioAssociationStore store=new PutioAssociationStore(context)){return pending(store.getReadableDatabase(),account,folder);}}
    private static Pending pending(SQLiteDatabase db,long account,long folder){
        try(Cursor c=db.rawQuery("SELECT folder_id,old_folder,old_source,new_source,choice FROM reassignments WHERE account_id=? AND (folder_id=? OR old_folder=?)",new String[]{""+account,""+folder,""+folder})){
            return c.moveToFirst()?new Pending(account,c.getLong(1),c.getLong(0),Uri.parse(c.getString(2)),Uri.parse(c.getString(3)),PutioAssociationStore.DisconnectChoice.valueOf(c.getString(4))):null;
        }
    }
    static PutioSync.Review run(Context context,PutioReadClient client,long account,long oldFolder,long newFolder,Uri newSource,PutioAssociationStore.DisconnectChoice choice)throws Exception{
        List<String> rootPath=PutioRootLocation.readPath(context,client,newFolder);
        PutioReconciliation.Snapshot snapshot=PutioSnapshotReader.read(account+":"+newFolder,newFolder,client::list);
        if(!snapshot.complete())throw new PutioReadClient.Unavailable(snapshot.failure(),0);
        if(!rootPath.equals(PutioRootLocation.readPath(context,client,newFolder)))throw new PutioReadClient.Unavailable(PutioReconciliation.Failure.INVALID_PAGE,0);
        PutioSync.Review result=apply(context,account,oldFolder,newFolder,newSource,choice,snapshot);
        PutioRootLocation.remember(context,account,newFolder,newSource,rootPath);return result;
    }
    static PutioSync.Review apply(Context context,long account,long oldFolder,long newFolder,Uri newSource,PutioAssociationStore.DisconnectChoice choice,PutioReconciliation.Snapshot snapshot)throws Exception{
        if(account<=0||oldFolder<0||newFolder<0||choice==null||!snapshot.complete()||!snapshot.scope.equals(account+":"+newFolder)||newSource==null||newSource.getHost()==null||newSource.getUserInfo()!=null||newSource.getQuery()!=null||newSource.getFragment()!=null||!Arrays.asList("http","https","webdav","webdavs","dav","davs").contains(newSource.getScheme()))throw new IllegalArgumentException("Complete reassignment and discovery choice required");
        try(PutioAssociationStore store=new PutioAssociationStore(context)){synchronized(store){synchronized(ProviderDiscoveryGate.LOCK){
            SQLiteDatabase db=store.getWritableDatabase();Pending change=pending(db,account,newFolder);
            if(change!=null){if(change.oldFolder!=oldFolder||change.newFolder!=newFolder||!change.newSource.equals(newSource)||change.choice!=choice)throw new IllegalStateException("Finish the pending source change first");}
            else{
                Uri oldSource=null;for(PutioAssociationStore.Scope scope:store.scopes(account)){
                    if(scope.folderId==oldFolder)oldSource=scope.source;
                    else if(scope.folderId==newFolder||ProviderDiscoveryGate.contains(scope.source,newSource)||ProviderDiscoveryGate.contains(newSource,scope.source))throw new IllegalStateException("Selected source overlaps another association");
                }
                if(oldSource==null)throw new IllegalStateException("Original source unavailable");
                change=new Pending(account,oldFolder,newFolder,oldSource,newSource,choice);
            }
            Set<Long> incoming=new HashSet<>();for(PutioReconciliation.File file:snapshot.files())incoming.add(file.id);
            try(Cursor c=db.rawQuery("SELECT file_id,folder_id FROM links WHERE account_id=?",new String[]{""+account})){
                while(c.moveToNext())if(incoming.contains(c.getLong(0))&&c.getLong(1)!=oldFolder&&c.getLong(1)!=newFolder)throw new IllegalStateException("A file belongs to another source; review its association first");
            }
            String retired=retiredKey(account,change.oldSource);
            // Protect both sources before changing paths. The retired-source row survives a crash.
            ProviderDiscoveryGate.exclude(context,retired,change.oldSource);ProviderDiscoveryGate.exclude(context,"putio:"+account+":"+newFolder,newSource);
            db.beginTransaction();try{
                ContentValues journal=new ContentValues();journal.put("account_id",account);journal.put("folder_id",newFolder);journal.put("old_folder",oldFolder);journal.put("old_source",change.oldSource.toString());journal.put("new_source",newSource.toString());journal.put("choice",choice.name());db.insertWithOnConflict("reassignments",null,journal,SQLiteDatabase.CONFLICT_REPLACE);
                ContentValues retained=new ContentValues();retained.put("account_id",account);retained.put("source",change.oldSource.toString());retained.put("gate_key",retired);db.insertWithOnConflict("retired_sources",null,retained,SQLiteDatabase.CONFLICT_REPLACE);
                ContentValues target=new ContentValues();target.put("account_id",account);target.put("folder_id",newFolder);target.put("source",newSource.toString());target.put("ownership",PutioAssociationStore.Ownership.REASSIGNING.name());
                db.insertWithOnConflict("scopes",null,target,SQLiteDatabase.CONFLICT_IGNORE);
                db.execSQL("UPDATE scopes SET generation=generation+1,ownership=?,complete_generation=-1 WHERE account_id=? AND (folder_id=? OR folder_id=?)",new Object[]{PutioAssociationStore.Ownership.REASSIGNING.name(),account,oldFolder,newFolder});
                db.update("scopes",target,"account_id=? AND folder_id=?",new String[]{""+account,""+newFolder});db.setTransactionSuccessful();
            }finally{db.endTransaction();}
            Map<Long,PutioReconciliation.File> files=new HashMap<>();for(PutioReconciliation.File file:snapshot.files())files.put(file.id,file);
            Map<Long,PutioAssociationStore.Link> links=new HashMap<>();for(PutioAssociationStore.Link link:store.links(account,oldFolder))links.put(link.fileId,link);for(PutioAssociationStore.Link link:store.links(account,newFolder))links.put(link.fileId,link);
            PutioLibraryBridge library=new PutioLibraryBridge(context);
            for(PutioAssociationStore.Link link:links.values()){
                if(Thread.currentThread().isInterrupted())throw new java.io.InterruptedIOException();
                PutioReconciliation.File file=files.get(link.fileId);if(file==null)continue;
                // Repeated after an interruption: move() reads the current canonical URI by media ID.
                library.move(link.mediaId,PutioLibraryBridge.playback(newSource,file.relativePath));
                ContentValues moved=new ContentValues();moved.put("folder_id",newFolder);moved.put("parent_id",file.parentId);moved.put("relative_path",file.relativePath);moved.put("size",file.size);moved.put("missing",0);
                db.update("links",moved,"account_id=? AND file_id=?",new String[]{""+account,""+file.id});
            }
            db.beginTransaction();try{
                if(oldFolder==newFolder)for(PutioAssociationStore.Link link:links.values())if(!files.containsKey(link.fileId)){ContentValues missing=new ContentValues();missing.put("missing",1);db.update("links",missing,"account_id=? AND file_id=?",new String[]{""+account,""+link.fileId});}
                if(oldFolder!=newFolder){ContentValues old=new ContentValues();old.put("ownership",choice==PutioAssociationStore.DisconnectChoice.KEEP_INACTIVE?PutioAssociationStore.Ownership.INACTIVE.name():PutioAssociationStore.Ownership.GENERIC.name());db.update("scopes",old,"account_id=? AND folder_id=?",new String[]{""+account,""+oldFolder});if(choice==PutioAssociationStore.DisconnectChoice.REVERT_TO_GENERIC)ProviderDiscoveryGate.release(context,"putio:"+account+":"+oldFolder);}
                ContentValues ready=new ContentValues();ready.put("ownership",PutioAssociationStore.Ownership.PREPARING.name());db.update("scopes",ready,"account_id=? AND folder_id=?",new String[]{""+account,""+newFolder});
                if(choice==PutioAssociationStore.DisconnectChoice.REVERT_TO_GENERIC){ProviderDiscoveryGate.release(context,retired);db.delete("retired_sources","account_id=? AND source=?",new String[]{""+account,change.oldSource.toString()});}
                db.delete("reassignments","account_id=? AND folder_id=?",new String[]{""+account,""+newFolder});db.setTransactionSuccessful();
            }finally{db.endTransaction();}
            PutioAssociationStore.Session session=store.begin(account,newFolder);List<PutioReconciliation.Existing> existing=library.existing(newSource,store.links(account,newFolder));
            return new PutioSync.Review(session,newSource,snapshot,PutioReconciliation.plan(snapshot,existing),existing);
        }}}
    }
    private static String retiredKey(long account,Uri source)throws Exception{
        byte[] hash=java.security.MessageDigest.getInstance("SHA-256").digest(source.toString().getBytes(java.nio.charset.StandardCharsets.UTF_8));StringBuilder key=new StringBuilder("putio-retired:"+account+":");for(byte b:hash)key.append(String.format(java.util.Locale.ROOT,"%02x",b&255));return key.toString();
    }
    private PutioReassignment(){}
}
