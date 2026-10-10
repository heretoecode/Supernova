package com.archos.mediacenter.video.leanback;

import android.content.Context;
import com.archos.mediacenter.video.browser.adapters.object.Video;
import com.archos.mediacenter.video.utils.VideoMetadata;
import java.util.*;
import java.util.concurrent.*;

/** Background hydration through Nova's existing retriever, never a second scanner. */
final class PreviewMetadata {
    private static final Set<String> pending=new HashSet<>();
    private static final ThreadPoolExecutor worker=new ThreadPoolExecutor(1,1,0,TimeUnit.SECONDS,new ArrayBlockingQueue<>(8),r->new Thread(r,"SupernovaMetadata"));
    private static final ScheduledExecutorService queue=Executors.newSingleThreadScheduledExecutor(r->new Thread(r,"SupernovaTechnicalQueue"));
    private static final LinkedHashMap<String,PreviewLibraryLoader.Entry> jobs=new LinkedHashMap<>();
    private static boolean draining;
    private static ScheduledFuture<?> retry;
    private static final long RETRY=30*60*1000L;
    static String key(PreviewLibraryLoader.Entry entry){return "135:"+((Video)entry.media).getId()+":"+entry.bytes+":"+entry.modified;}
    /** Re-offered from the indexed snapshot after restart; completion/backoff survive the process. */
    static void library(Context context,PreviewLibraryLoader.Snapshot snapshot,int tab){
        Context app=context.getApplicationContext();List<PreviewLibraryLoader.Entry> candidates=candidates(snapshot,tab);
        queue.execute(()->{LinkedHashMap<String,PreviewLibraryLoader.Entry> prioritised=new LinkedHashMap<>();for(PreviewLibraryLoader.Entry entry:candidates)if(entry.media instanceof Video&&((Video)entry.media).isIndexed())prioritised.putIfAbsent(key(entry),entry);for(Map.Entry<String,PreviewLibraryLoader.Entry> old:jobs.entrySet())prioritised.putIfAbsent(old.getKey(),old.getValue());jobs.clear();jobs.putAll(prioritised);if(!draining){if(retry!=null)retry.cancel(false);draining=true;drain(app);}});
    }
    static List<PreviewLibraryLoader.Entry> candidates(PreviewLibraryLoader.Snapshot snapshot,int tab){
        List<PreviewLibraryLoader.Entry> candidates=new ArrayList<>(snapshot.continuingMovies);candidates.addAll(snapshot.continuingShows);candidates.addAll(snapshot.recent.subList(0,Math.min(30,snapshot.recent.size())));
        if(tab==2)candidates.addAll(snapshot.episodes);else candidates.addAll(snapshot.movies);
        candidates.addAll(snapshot.movies);candidates.addAll(snapshot.episodes);candidates.addAll(snapshot.unmatched);
        if(snapshot.technical!=null)candidates.addAll(snapshot.technical);
        return candidates;
    }
    private static void drain(Context app){
        android.content.SharedPreferences state=app.getSharedPreferences("preview-technical-v1",0);long now=System.currentTimeMillis(),earliest=Long.MAX_VALUE;
        Iterator<Map.Entry<String,PreviewLibraryLoader.Entry>> iterator=jobs.entrySet().iterator();
        while(iterator.hasNext()){Map.Entry<String,PreviewLibraryLoader.Entry> job=iterator.next();String identity=job.getKey();PreviewLibraryLoader.Entry entry=job.getValue();
            if(state.getBoolean("complete:"+identity,false)){iterator.remove();continue;}
            long next=state.getLong("retry:"+identity,0);if(next>now){earliest=Math.min(earliest,next);continue;}
            iterator.remove();
            // Only one extraction is submitted at a time; no focus/scroll event drives this queue.
            boolean accepted=request(app,entry,()->queue.execute(()->{if(!state.getBoolean("complete:"+identity,false))jobs.put(identity,entry);queue.schedule(()->drain(app),250,TimeUnit.MILLISECONDS);}));
            if(!accepted){jobs.put(identity,entry);queue.schedule(()->drain(app),250,TimeUnit.MILLISECONDS);}
            return;
        }
        draining=false;if(earliest!=Long.MAX_VALUE)retry=queue.schedule(()->{draining=true;drain(app);},Math.max(250,earliest-now),TimeUnit.MILLISECONDS);
    }
    /** Loader-side cached HDR field; core codec/bitrate/dimensions remain in the native index. */
    static void hydrate(Context context,List<PreviewLibraryLoader.Entry> entries){android.content.SharedPreferences state=context.getSharedPreferences("preview-technical-v1",0);for(PreviewLibraryLoader.Entry entry:entries)if(entry.media instanceof Video){entry.hdr=state.getString("hdr:"+key(entry),entry.hdr);((Video)entry.media).setPreviewDynamicRange(entry.hdr);((Video)entry.media).setPreviewDolbyVision(state.getInt("dv-profile:"+key(entry),0),state.getInt("dv-compat:"+key(entry),0));}}
    private static boolean request(Context context,PreviewLibraryLoader.Entry entry,Runnable refreshed){
        if(!(entry.media instanceof Video))return false;
        Video video=(Video)entry.media;String key=key(entry);
        synchronized(pending){if(!pending.add(key))return false;}
        Context app=context.getApplicationContext();
        try{worker.execute(()->{VideoMetadata result=null;boolean complete=false;String operation=com.archos.mediacenter.video.diagnostics.Diagnostics.operation("technical_metadata");long started=android.os.SystemClock.elapsedRealtime();try{result=com.archos.mediacenter.video.info.VideoInfoCommonClass.retrieveMetadata(video,app);if(result!=null&&result.getVideoTrack()!=null&&result.getVideoWidth()>0&&video.isIndexed()){result.save(app,video.getFilePath());complete=true;}}catch(Exception|LinkageError e){com.archos.mediacenter.video.diagnostics.Diagnostics.error("list_metadata_unavailable",e);}finally{synchronized(pending){pending.remove(key);}}
            android.content.SharedPreferences.Editor state=app.getSharedPreferences("preview-technical-v1",0).edit();if(complete){state.putBoolean("complete:"+key,true).remove("retry:"+key).putString("hdr:"+key,result.getVideoTrack().dynamicRange()).putInt("dv-profile:"+key,result.getVideoTrack().dolbyVisionProfile).putInt("dv-compat:"+key,result.getVideoTrack().dolbyVisionCompatibility);}else state.putLong("retry:"+key,System.currentTimeMillis()+RETRY);state.commit();
            com.archos.mediacenter.video.diagnostics.Diagnostics.event("technical_metadata_result","operation_id",operation,"media_id",video.getId(),"complete",complete);com.archos.mediacenter.video.diagnostics.Diagnostics.finishOperation(operation,"technical_metadata",started);
            VideoMetadata ready=result;new android.os.Handler(android.os.Looper.getMainLooper()).post(()->{if(ready!=null)apply(entry,ready);refreshed.run();});
        });return true;}catch(RejectedExecutionException full){synchronized(pending){pending.remove(key);}return false;}
    }
    private static void apply(PreviewLibraryLoader.Entry entry,VideoMetadata metadata){
        ((Video)entry.media).setMetadata(metadata);VideoMetadata.VideoTrack track=metadata.getVideoTrack();
        if(track!=null){entry.codec=com.archos.mediacenter.video.leanback.details.PreviewMediaInfo.format(track.format);entry.bitrate=1000L*Math.max(0,track.bitRate);entry.hdr=track.dynamicRange();((Video)entry.media).setPreviewDolbyVision(track.dolbyVisionProfile,track.dolbyVisionCompatibility);((Video)entry.media).setPreviewDynamicRange(entry.hdr);}
        int w=metadata.getVideoWidth(),h=metadata.getVideoHeight();if(w>0&&h>0)entry.resolution=w>=3840||h>=2160?"4K":w>=1728||h>=1040?"1080p":w>=1200||h>=720?"720p":"SD";
        LinkedHashSet<String> audio=new LinkedHashSet<>();for(int i=0;i<metadata.getAudioTrackNb();i++){VideoMetadata.AudioTrack a=metadata.getAudioTrack(i);if(a!=null&&a.format!=null)audio.add(a.format+(a.channels==null?"":" · "+a.channels));}if(!audio.isEmpty())entry.audio=android.text.TextUtils.join(" · ",audio);
    }
}
