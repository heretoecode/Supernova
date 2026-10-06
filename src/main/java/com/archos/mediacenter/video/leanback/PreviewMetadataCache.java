package com.archos.mediacenter.video.leanback;

import android.content.Context;
import android.util.AtomicFile;
import org.json.JSONObject;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.Locale;
import java.util.concurrent.Callable;

/** Persisted read-only enrichment packages. A failed refresh never erases usable cached metadata. */
public final class PreviewMetadataCache {
    private static final long STALE_AFTER = 7L * 24 * 60 * 60 * 1000;
    private static final Object[] LOCKS=new Object[64];
    private static final Object[] FETCH_LOCKS=new Object[64];
    static {for(int i=0;i<LOCKS.length;i++){LOCKS[i]=new Object();FETCH_LOCKS[i]=new Object();}}
    private static File file(Context c,String kind,long id,String section) {
        File directory=new File(c.getFilesDir(),"preview-metadata");
        return new File(directory,kind+"_"+id+"_"+section.replace('/','_')+"_"+Locale.getDefault().toLanguageTag()+".json");
    }
    public static JSONObject read(Context c,String kind,long id,String section) {
        synchronized(LOCKS[(file(c,kind,id,section).getName().hashCode()&Integer.MAX_VALUE)%LOCKS.length]){
            return readLocked(c,kind,id,section);
        }
    }
    private static JSONObject readLocked(Context c,String kind,long id,String section) {
        File file=file(c,kind,id,section);if(!file.isFile()||file.length()>3*1024*1024)return null;
        try(InputStream input=new AtomicFile(file).openRead();ByteArrayOutputStream output=new ByteArrayOutputStream()){
            byte[] buffer=new byte[8192];int count;while((count=input.read(buffer))!=-1){if(output.size()+count>3*1024*1024)return null;output.write(buffer,0,count);}
            return new JSONObject(output.toString("UTF-8"));
        }catch(Exception unavailable){return null;}
    }
    public static JSONObject load(Context c,String kind,long id,String section,Callable<JSONObject> fetch)throws Exception {
        // Network coalescing must never hold the disk-read lock used by the UI.
        synchronized(FETCH_LOCKS[(file(c,kind,id,section).getName().hashCode()&Integer.MAX_VALUE)%FETCH_LOCKS.length]){
            return loadLocked(c,kind,id,section,fetch);
        }
    }
    private static JSONObject loadLocked(Context c,String kind,long id,String section,Callable<JSONObject> fetch)throws Exception {
        JSONObject cached=read(c,kind,id,section);
        if(cached!=null&&System.currentTimeMillis()-cached.optLong("fetched_at")<STALE_AFTER)return cached.getJSONObject("data");
        try {
            JSONObject data=fetch.call();
            JSONObject envelope=new JSONObject().put("fetched_at",System.currentTimeMillis()).put("complete",true).put("data",data);
            File target=file(c,kind,id,section);
            synchronized(LOCKS[(target.getName().hashCode()&Integer.MAX_VALUE)%LOCKS.length]){
                target.getParentFile().mkdirs();AtomicFile atomic=new AtomicFile(target);FileOutputStream output=null;
                try{output=atomic.startWrite();output.write(envelope.toString().getBytes(StandardCharsets.UTF_8));atomic.finishWrite(output);}catch(IOException failure){if(output!=null)atomic.failWrite(output);}
            }
            return data;
        } catch(Exception unavailable) { if(cached!=null&&cached.has("data"))return cached.getJSONObject("data");throw unavailable; }
    }
    /** Enrichment completion must not mistake a stale fallback for a successful refresh. */
    public static boolean fresh(Context c,String kind,long id,String section) {
        JSONObject cached=read(c,kind,id,section);
        return cached!=null&&cached.optBoolean("complete")&&cached.optJSONObject("data")!=null
                &&System.currentTimeMillis()-cached.optLong("fetched_at")<STALE_AFTER;
    }
    private PreviewMetadataCache(){}
}
