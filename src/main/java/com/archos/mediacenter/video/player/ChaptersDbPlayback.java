package com.archos.mediacenter.video.player;

import android.content.Context;
import org.json.JSONObject;
import java.util.*;
import java.util.concurrent.*;
import com.archos.mediacenter.video.streaming.StreamingRepository;
import com.archos.mediacenter.video.diagnostics.Diagnostics;

/** Title-scoped fallback acquisition; strict alignment is enforced before any external marker is exposed. */
final class ChaptersDbPlayback implements AutoCloseable {
    private final Context context;
    private final ChaptersDbClient client;
    private final ExecutorService io=Executors.newSingleThreadExecutor(r->new Thread(r,"SupernovaChapters"));
    private String identity="";private int generation;private Future<?> work;private boolean closed;
    private volatile ChaptersDbClient.Result result;
    ChaptersDbPlayback(Context c){context=c.getApplicationContext();client=new ChaptersDbClient(context);}
    synchronized List<PlaybackChapters.Chapter> chapters(Object localIdentity,List<PlaybackChapters.Chapter> embedded,String kind,String tmdb,String knownImdb,int season,int episode,long duration){
        if(embedded!=null&&!embedded.isEmpty())return embedded;
        if(closed||duration<=0||tmdb==null||!tmdb.matches("[1-9][0-9]{0,17}"))return Collections.emptyList();
        String key=localIdentity+"|"+kind+"|"+tmdb+"|"+season+"|"+episode+"|"+duration;
        if(!key.equals(identity)){
            identity=key;result=null;int expected=++generation;if(work!=null)work.cancel(true);
            work=io.submit(()->{try{
                String imdb=knownImdb;if(!ChaptersDbClient.imdb(imdb))imdb=StreamingRepository.metadata(context,kind,Long.parseLong(tmdb),"external_ids").optString("imdb_id");
                ChaptersDbClient.Result loaded;
                if("tv".equals(kind)){JSONObject ids=StreamingRepository.metadata(context,"tv",Long.parseLong(tmdb),"season/"+season+"/episode/"+episode+"/external_ids");long tvdb=ids.optLong("tvdb_id");loaded=client.episode(imdb,tvdb>0?String.valueOf(tvdb):"",season,episode,duration);}else loaded=client.movie(imdb,duration);
                synchronized(this){if(closed||expected!=generation)return;result=loaded;}
                Diagnostics.event("chaptersdb_result","status",loaded.status.name(),"sets",loaded.candidates.size());
            }catch(Exception unavailable){if(!Thread.currentThread().isInterrupted())Diagnostics.event("chaptersdb_unavailable","failure_category",unavailable.getClass().getSimpleName());}});
        }
        ChaptersDbClient.Result known=result;return known==null?Collections.emptyList():known.verifiedChapters();
    }
    @Override public synchronized void close(){closed=true;generation++;if(work!=null)work.cancel(true);io.shutdownNow();result=null;identity="";}
}
