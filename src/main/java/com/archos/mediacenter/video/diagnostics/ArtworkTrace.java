package com.archos.mediacenter.video.diagnostics;

/** Safe artwork lifecycle shared by bitmap routes; never accepts an artwork URL or title. */
public final class ArtworkTrace {
    private final String operation,surface,type,source;
    private final long media,started=android.os.SystemClock.elapsedRealtime();
    private boolean ended;
    public ArtworkTrace(long media,String surface,String type,String source){
        this.media=Math.max(0,media);this.surface=Diagnostics.uiLabel(surface);this.type=Diagnostics.uiLabel(type);this.source=Diagnostics.uiLabel(source);
        operation=Diagnostics.operation("artwork");emit("artwork_request","unknown","none",false,false);
    }
    public synchronized void ready(String cache){if(ended)return;ended=true;emit("artwork_ready",cache,"none",false,false);}
    public synchronized void failed(String category,boolean retained){if(ended)return;ended=true;emit("artwork_failed","unknown",category,retained,retained);}
    public synchronized void fallback(String category){if(ended)return;ended=true;emit("artwork_fallback","unknown",category,true,true);}
    public synchronized void cancelled(){if(ended)return;ended=true;emit("artwork_cancelled","unknown","superseded",false,false);}
    private void emit(String event,String cache,String failure,boolean fallbackAttempted,boolean fallbackSucceeded){
        Diagnostics.event(event,"operation_id",operation,"media_id",media,"surface",surface,"artwork_type",type,"source",source,
                "cache_layer",Diagnostics.uiLabel(cache),"failure_category",Diagnostics.uiLabel(failure),"elapsed_ms",android.os.SystemClock.elapsedRealtime()-started,
                "fallback_attempted",fallbackAttempted,"fallback_succeeded",fallbackSucceeded);
        if(ended)Diagnostics.finishOperation(operation,"artwork",started);
    }
}
