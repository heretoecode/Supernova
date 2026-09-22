package com.archos.mediacenter.video.leanback;

import android.content.Context;
import com.archos.mediacenter.video.browser.adapters.object.Video;
import com.archos.mediacenter.video.utils.VideoMetadata;
import java.util.*;
import java.util.concurrent.*;

/** On-demand list hydration through Nova's existing retriever, never a second scanner. */
final class PreviewMetadata {
    private static final Set<String> pending=new HashSet<>();
    private static final android.util.LruCache<String,VideoMetadata> cache=new android.util.LruCache<>(64);
    private static final ThreadPoolExecutor worker=new ThreadPoolExecutor(1,1,0,TimeUnit.SECONDS,new ArrayBlockingQueue<>(8),r->new Thread(r,"SupernovaMetadata"));
    static void request(Context context,PreviewLibraryLoader.Entry entry,Runnable refreshed){
        if(!(entry.media instanceof Video)||!entry.codec.isEmpty()&&!entry.audio.isEmpty()&&!entry.resolution.isEmpty())return;
        Video video=(Video)entry.media;String key=video.getId()+":"+entry.bytes+":"+entry.modified;
        synchronized(pending){VideoMetadata found=cache.get(key);if(found!=null){apply(entry,found);refreshed.run();return;}if(!pending.add(key))return;}
        Context app=context.getApplicationContext();
        try{worker.execute(()->{VideoMetadata result=null;try{result=com.archos.mediacenter.video.info.VideoInfoCommonClass.retrieveMetadata(video,app);if(result!=null&&result.getVideoTrack()!=null&&result.getVideoWidth()>0&&video.isIndexed())result.save(app,video.getFilePath());}catch(Exception|LinkageError e){com.archos.mediacenter.video.diagnostics.Diagnostics.error("list_metadata_unavailable",e);}finally{synchronized(pending){pending.remove(key);cache.put(key,result==null?new VideoMetadata():result);}}
            VideoMetadata ready=result;if(ready!=null)new android.os.Handler(android.os.Looper.getMainLooper()).post(()->{apply(entry,ready);refreshed.run();});
        });}catch(RejectedExecutionException full){synchronized(pending){pending.remove(key);}}
    }
    private static void apply(PreviewLibraryLoader.Entry entry,VideoMetadata metadata){
        ((Video)entry.media).setMetadata(metadata);VideoMetadata.VideoTrack track=metadata.getVideoTrack();
        if(track!=null){entry.codec=com.archos.mediacenter.video.leanback.details.PreviewMediaInfo.format(track.format);if(track.colorTrc==16)entry.hdr="HDR (PQ)";else if(track.colorTrc==18)entry.hdr="HLG";}
        int w=metadata.getVideoWidth(),h=metadata.getVideoHeight();if(w>0&&h>0)entry.resolution=w>=3840||h>=2160?"4K":w>=1728||h>=1040?"1080p":w>=1200||h>=720?"720p":"SD";
        LinkedHashSet<String> audio=new LinkedHashSet<>();for(int i=0;i<metadata.getAudioTrackNb();i++){VideoMetadata.AudioTrack a=metadata.getAudioTrack(i);if(a!=null&&a.format!=null)audio.add(a.format+(a.channels==null?"":" · "+a.channels));}if(!audio.isEmpty())entry.audio=android.text.TextUtils.join(" · ",audio);
    }
}
