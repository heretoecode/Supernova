package com.archos.mediacenter.video.streaming.putio;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.Uri;
import java.util.*;

/** Sidecar identities only. Never updates/deletes a Video row, file or playback history. */
public final class PutioAssociationStore extends SQLiteOpenHelper {
    public enum Ownership { PREPARING, API, INACTIVE, GENERIC }
    public enum DisconnectChoice { KEEP_INACTIVE, REVERT_TO_GENERIC }
    public static final class Session {
        public final long accountId, folderId, generation;
        public final String scope;
        private Session(long account,long folder,long generation) {
            accountId=account;folderId=folder;this.generation=generation;scope=account+":"+folder;
        }
    }
    public static final class Link {
        public final long fileId,parentId,mediaId,size;
        public final String relativePath;
        public final boolean missing;
        private Link(Cursor cursor) {
            fileId=cursor.getLong(0);parentId=cursor.getLong(1);mediaId=cursor.getLong(2);
            size=cursor.getLong(3);relativePath=cursor.getString(4);missing=cursor.getInt(5)!=0;
        }
    }
    public PutioAssociationStore(Context context) { super(context,"putio-associations.db",null,1); }
    @Override public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE scopes (account_id INTEGER NOT NULL,folder_id INTEGER NOT NULL,source TEXT NOT NULL,generation INTEGER NOT NULL DEFAULT 0,ownership TEXT NOT NULL,complete_generation INTEGER NOT NULL DEFAULT -1,unresolved INTEGER NOT NULL DEFAULT 0,PRIMARY KEY(account_id,folder_id))");
        db.execSQL("CREATE TABLE links (account_id INTEGER NOT NULL,file_id INTEGER NOT NULL,folder_id INTEGER NOT NULL,parent_id INTEGER NOT NULL,media_id INTEGER NOT NULL UNIQUE,size INTEGER NOT NULL,relative_path TEXT NOT NULL,missing INTEGER NOT NULL DEFAULT 0,PRIMARY KEY(account_id,file_id))");
    }
    @Override public void onUpgrade(SQLiteDatabase db,int oldVersion,int newVersion) {
        throw new IllegalStateException("Association migration is required");
    }
    /** Explicit folder selection; changing a source requires a separate reassignment review. */
    public synchronized void prepare(long account,long folder,Uri source) {
        if(account<=0||folder<0)throw new IllegalArgumentException("Invalid provider scope");
        String safe=source(source);
        SQLiteDatabase db=getWritableDatabase();
        try(Cursor c=db.rawQuery("SELECT source FROM scopes WHERE account_id=? AND folder_id=?",args(account,folder))) {
            if(c.moveToFirst()) {
                if(!safe.equals(c.getString(0)))throw new IllegalStateException("Source reassignment needs review");
                return;
            }
        }
        ContentValues row=new ContentValues();row.put("account_id",account);row.put("folder_id",folder);
        row.put("source",safe);row.put("ownership",Ownership.PREPARING.name());db.insertOrThrow("scopes",null,row);
    }
    /** A new generation invalidates every earlier in-flight result for this folder. */
    public synchronized Session begin(long account,long folder) {
        SQLiteDatabase db=getWritableDatabase();db.beginTransaction();
        try {
            long generation;
            try(Cursor c=db.rawQuery("SELECT generation FROM scopes WHERE account_id=? AND folder_id=?",args(account,folder))) {
                if(!c.moveToFirst())throw new IllegalStateException("Scope has not been prepared");
                generation=Math.addExact(c.getLong(0),1);
            }
            ContentValues values=new ContentValues();values.put("generation",generation);
            db.update("scopes",values,"account_id=? AND folder_id=?",args(account,folder));
            db.setTransactionSuccessful();return new Session(account,folder,generation);
        } finally { db.endTransaction(); }
    }
    /** Only complete, current snapshots may attach IDs. Conflicts roll back all attachments. */
    public synchronized boolean commit(Session session,PutioReconciliation.Snapshot snapshot,Collection<PutioReconciliation.Existing> existing) {
        if(!session.scope.equals(snapshot.scope))throw new IllegalArgumentException("Snapshot belongs to another scope");
        synchronized(snapshot) {
            PutioReconciliation.Plan plan=PutioReconciliation.plan(snapshot,existing);
            if(!plan.complete)return false;
            SQLiteDatabase db=getWritableDatabase();db.beginTransaction();
            try {
                if(!current(db,session))return false;
                int unresolved=0;
                for(PutioReconciliation.Change change:plan.changes) {
                    if(change.decision==PutioReconciliation.Decision.NEW_FILE||change.decision==PutioReconciliation.Decision.NEEDS_REVIEW){unresolved++;continue;}
                    PutioReconciliation.File file=change.file;
                    boolean present=false;
                    try(Cursor c=db.rawQuery("SELECT media_id,folder_id FROM links WHERE account_id=? AND file_id=?",args(session.accountId,file.id))) {
                        if(c.moveToFirst()) {
                            if(c.getLong(0)!=change.mediaId||c.getLong(1)!=session.folderId)throw new IllegalStateException("Provider identity needs review");
                            present=true;
                        }
                    }
                    ContentValues row=new ContentValues();row.put("account_id",session.accountId);row.put("file_id",file.id);
                    row.put("folder_id",session.folderId);row.put("parent_id",file.parentId);row.put("media_id",change.mediaId);
                    row.put("size",file.size);row.put("relative_path",file.relativePath);row.put("missing",0);
                    if(present)db.update("links",row,"account_id=? AND file_id=?",args(session.accountId,file.id));
                    else db.insertOrThrow("links",null,row);
                }
                // Missing is a review state. There is deliberately no library deletion operation.
                ContentValues missing=new ContentValues();missing.put("missing",1);
                for(long mediaId:plan.missingMediaIds)db.update("links",missing,"account_id=? AND folder_id=? AND media_id=?",new String[]{""+session.accountId,""+session.folderId,""+mediaId});
                ContentValues complete=new ContentValues();complete.put("complete_generation",session.generation);complete.put("unresolved",unresolved);
                db.update("scopes",complete,"account_id=? AND folder_id=?",args(session.accountId,session.folderId));
                db.setTransactionSuccessful();return true;
            } finally { db.endTransaction(); }
        }
    }
    /** The scanner hand-off must call this only after its own source exclusion is ready. */
    public synchronized boolean activate(Session session) {
        ContentValues values=new ContentValues();values.put("ownership",Ownership.API.name());
        return getWritableDatabase().update("scopes",values,"account_id=? AND folder_id=? AND generation=? AND complete_generation=? AND unresolved=0 AND ownership=?",
                new String[]{""+session.accountId,""+session.folderId,""+session.generation,""+session.generation,Ownership.PREPARING.name()})==1;
    }
    /** Explicit user choice; neither path erases the retained stable identities. */
    public synchronized void disconnect(long account,long folder,DisconnectChoice choice) {
        if(choice==null)throw new IllegalArgumentException("Discovery choice is required");
        Session invalidate=begin(account,folder);
        ContentValues values=new ContentValues();values.put("ownership",choice==DisconnectChoice.KEEP_INACTIVE?Ownership.INACTIVE.name():Ownership.GENERIC.name());
        getWritableDatabase().update("scopes",values,"account_id=? AND folder_id=? AND generation=?",new String[]{""+account,""+folder,""+invalidate.generation});
    }
    public synchronized List<Link> links(long account,long folder) {
        List<Link> result=new ArrayList<>();
        try(Cursor c=getReadableDatabase().rawQuery("SELECT file_id,parent_id,media_id,size,relative_path,missing FROM links WHERE account_id=? AND folder_id=? ORDER BY file_id",args(account,folder))) {
            while(c.moveToNext())result.add(new Link(c));
        }
        return Collections.unmodifiableList(result);
    }
    public synchronized Ownership ownership(long account,long folder) {
        try(Cursor c=getReadableDatabase().rawQuery("SELECT ownership FROM scopes WHERE account_id=? AND folder_id=?",args(account,folder))) {
            if(!c.moveToFirst())throw new IllegalStateException("Unknown scope");return Ownership.valueOf(c.getString(0));
        }
    }
    private static boolean current(SQLiteDatabase db,Session session) {
        try(Cursor c=db.rawQuery("SELECT generation,ownership FROM scopes WHERE account_id=? AND folder_id=?",args(session.accountId,session.folderId))) {
            return c.moveToFirst()&&c.getLong(0)==session.generation&&(Ownership.PREPARING.name().equals(c.getString(1))||Ownership.API.name().equals(c.getString(1)));
        }
    }
    private static String[] args(long a,long b){return new String[]{Long.toString(a),Long.toString(b)};}
    private static String source(Uri uri) {
        if(uri==null||uri.getHost()==null||uri.getUserInfo()!=null||uri.getQuery()!=null||uri.getFragment()!=null)
            throw new IllegalArgumentException("Expected credential-free WebDAV source");
        String scheme=uri.getScheme();
        if(!Arrays.asList("http","https","webdav","webdavs","dav","davs").contains(scheme))throw new IllegalArgumentException("Expected WebDAV source");
        return uri.toString();
    }
}
