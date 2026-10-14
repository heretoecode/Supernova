package com.archos.mediacenter.video.utils.backup;

import android.content.*;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import com.archos.mediaprovider.DbHolder;
import com.archos.mediaprovider.video.*;
import com.archos.mediascraper.OwnedCanonicalBatch;
import java.io.*;
import java.util.function.BooleanSupplier;

/** Executes prepared scraper operations on the caller's native transaction, without a second writer or early publication. */
public final class CanonicalMatchExecutor {
    public static long apply(Context context,SQLiteDatabase database,OwnedCanonicalBatch batch,BooleanSupplier cancelled)throws IOException {
        if(database==null||!database.inTransaction()||database.getVersion()!=VideoOpenHelper.getDatabaseVersion())throw new IOException("Canonical application requires the coordinated native transaction");
        if(batch==null||batch.operations.isEmpty()||batch.operations.size()>10000||batch.primaryOperation<0||batch.primaryOperation>=batch.operations.size()||cancelled==null)throw new IOException("Invalid canonical operation batch");
        DbHolder sameConnection=new DbHolder(null){@Override public SQLiteDatabase get(){return database;}@Override public void close(){throw new IllegalStateException("Caller owns native transaction");}};
        ScraperProvider scraper=new ScraperProvider(context,sameConnection);
        ContentProvider local=new ContentProvider(){
            public boolean onCreate(){return true;}public String getType(Uri uri){return null;}
            public Cursor query(Uri uri,String[] columns,String where,String[] args,String order){
                if(ScraperStore.AUTHORITY.equals(uri.getAuthority()))return scraper.query(uri,columns,where,args,order);
                String last=uri.getLastPathSegment();
                if(!VideoStore.AUTHORITY.equals(uri.getAuthority())||last==null||!last.matches("[1-9][0-9]{0,18}"))throw new IllegalArgumentException("Unsupported canonical file assertion");
                return database.query(VideoOpenHelper.FILES_TABLE_NAME,columns,"_id="+Long.parseLong(last)+(where==null?"":" AND ("+where+")"),args,null,null,order);
            }
            public Uri insert(Uri uri,ContentValues values){return scraper.insert(uri,values);}
            public int update(Uri uri,ContentValues values,String where,String[] args){return scraper.update(uri,values,where,args);}
            public int delete(Uri uri,String where,String[] args){throw new IllegalArgumentException("Canonical batch does not delete receiving files or metadata");}
        };
        local.attachInfo(context,null);
        ContentProviderResult[] results=new ContentProviderResult[batch.operations.size()];
        try {
            for(int i=0;i<results.length;i++){
                if(cancelled.getAsBoolean()||Thread.currentThread().isInterrupted())throw new InterruptedIOException("Canonical application cancelled");
                ContentProviderOperation operation=batch.operations.get(i);
                if(operation.isDelete()||!operation.isAssertQuery()&&!ScraperStore.AUTHORITY.equals(operation.getUri().getAuthority()))throw new IOException("Unsupported canonical mutation");
                results[i]=operation.apply(local,results,i);
                if(operation.isInsert()&&(results[i].uri==null||ContentUris.parseId(results[i].uri)<=0))throw new IOException("Canonical insertion failed");
            }
            long id=ContentUris.parseId(results[batch.primaryOperation].uri);if(id<=0)throw new IOException("Canonical owner is unavailable");return id;
        }catch(OperationApplicationException|RuntimeException invalid){throw new IOException("Canonical native application failed",invalid);}
    }
    private CanonicalMatchExecutor(){}
}
