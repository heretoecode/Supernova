package com.archos.mediacenter.video.streaming.putio;

import android.content.*;
import android.database.Cursor;
import android.net.Uri;
import com.archos.filecorelibrary.FileUtils;
import com.archos.mediaprovider.ArchosMediaFile;
import com.archos.mediaprovider.video.*;
import java.util.*;

/** Native scanned-file ingestion; never synthesises scraper matches or edits playback history. */
final class PutioLibraryBridge implements PutioSync.Library {
    private static final Uri FILES=Uri.parse("content://"+VideoStore.AUTHORITY+"/raw/files");
    private final Context context;
    PutioLibraryBridge(Context context){this.context=context.getApplicationContext();}
    public List<PutioReconciliation.Existing> existing(Uri source,List<PutioAssociationStore.Link> links){
        Map<Long,Long> identities=new HashMap<>();for(PutioAssociationStore.Link link:links)identities.put(link.mediaId,link.fileId);
        List<PutioReconciliation.Existing> rows=new ArrayList<>();
        try(Cursor cursor=context.getContentResolver().query(FILES,new String[]{"_id","_data","_size"},"media_type=?",new String[]{""+VideoStore.Files.FileColumns.MEDIA_TYPE_VIDEO},null)){
            if(cursor==null)throw new IllegalStateException("Library unavailable");
            while(cursor.moveToNext()){
                Uri path=Uri.parse(cursor.getString(1));if(!ProviderDiscoveryGate.contains(source,path)){
                    if(identities.containsKey(cursor.getLong(0)))throw new IllegalStateException("Linked source has moved; reassignment needs review");
                    continue;
                }
                List<String> parts=path.getPathSegments();int start=source.getPathSegments().size();if(parts.size()<=start)continue;
                String relative=String.join("/",parts.subList(start,parts.size()));long id=cursor.getLong(0);
                rows.add(new PutioReconciliation.Existing(id,relative,Math.max(0,cursor.getLong(2)),identities.getOrDefault(id,0L)));
            }
        }
        return rows;
    }
    static Uri playback(Uri source,String relative){
        if(relative==null||relative.isEmpty())throw new IllegalArgumentException("Missing path");
        Uri.Builder builder=source.buildUpon();for(String part:relative.split("/",-1)){
            if(part.isEmpty()||part.equals(".")||part.equals("..")||part.indexOf('\\')>=0||part.indexOf('\0')>=0)throw new IllegalArgumentException("Invalid path");
            builder.appendPath(part);
        }
        return builder.build();
    }
    public long insert(Uri source,PutioReconciliation.File file){
        Uri uri=playback(source,file.relativePath);ContentResolver resolver=context.getContentResolver();
        try(Cursor existing=resolver.query(FILES,new String[]{"_id"},"_data=?",new String[]{uri.toString()},null)){
            if(existing==null)throw new IllegalStateException("Library unavailable");
            if(existing.moveToFirst())throw new IllegalStateException("Existing path needs review");
        }
        String name=uri.getLastPathSegment();int dot=name.lastIndexOf('.');String extension=dot<0?"":name.substring(dot+1);
        ArchosMediaFile.MediaFileType type=ArchosMediaFile.getFileType(extension);
        if(type==null||!ArchosMediaFile.isVideoFileType(type.fileType))throw new IllegalArgumentException("Unsupported media type");
        ContentValues values=new ContentValues();values.put("_data",uri.toString());values.put("_display_name",name);values.put("_size",file.size);
        values.put("mime_type",type.mimeType);values.put("title",dot>0?name.substring(0,dot):name);values.put("media_type",VideoStore.Files.FileColumns.MEDIA_TYPE_VIDEO);
        Uri parent=FileUtils.getParentUrl(uri);values.put("bucket_id",FileUtils.getBucketId(parent));values.put("bucket_display_name",parent.getLastPathSegment());
        values.put("format",0x3000);values.put("parent",-1);values.put("date_added",System.currentTimeMillis()/1000);values.put("date_modified",System.currentTimeMillis()/1000);
        // Native files_scanned triggers create the canonical Video row and its stable media ID.
        if(resolver.insert(VideoStoreInternal.FILES_SCANNED,values)==null)throw new IllegalStateException("Library import failed");
        try(Cursor created=resolver.query(FILES,new String[]{"_id"},"_data=?",new String[]{uri.toString()},null)){
            if(created==null||!created.moveToFirst()||created.getCount()!=1)throw new IllegalStateException("Library import requires review");return created.getLong(0);
        }
    }
    public void relocate(Uri source,PutioReconciliation.Existing old,PutioReconciliation.File file){
        if(old.relativePath.equals(file.relativePath))return;
        Uri before=playback(source,old.relativePath),after=playback(source,file.relativePath);ContentResolver resolver=context.getContentResolver();
        try(Cursor collision=resolver.query(FILES,new String[]{"_id"},"_data=? AND _id<>?",new String[]{after.toString(),""+old.mediaId},null)){
            if(collision==null||collision.moveToFirst())throw new IllegalStateException("Moved file needs review");
        }
        // The native URI-update trigger preserves the same canonical ID and metadata/history.
        ContentValues values=new ContentValues();values.put("_data",after.toString());
        if(resolver.update(VideoStoreInternal.FILES_SCANNED,values,"_data=?",new String[]{before.toString()})!=1)throw new IllegalStateException("Original indexed file is unavailable");
    }
    public void enrich(){com.archos.mediascraper.AutoScrapeService.startService(context);}
}
