package com.archos.mediacenter.video.diagnostics;

import static com.archos.mediacenter.video.diagnostics.Diagnostics.*;

/** Bounded correlation state only; the existing Diagnostics recorder owns every write and retention. */
public final class PlaybackDiagnosticState {
    private PlaybackAttempt open,seek;
    private Object backend;
    private boolean overlapping,seekCallback;
    public synchronized PlaybackAttempt opening(){
        close(PlaybackOutcome.SUPERSEDED);
        open=playbackAttempt(PlaybackOperation.OPEN,null,-1);
        return open;
    }
    public synchronized void bind(Object backend,PlaybackAttempt owner,int type){
        if(owner!=open||owner==null||backend==null)return;this.backend=backend;
        playbackBoundary(open,PlaybackBoundary.BACKEND_CREATED,PlaybackOutcome.PENDING,MeasurementQuality.PROXY,-1,type);
    }
    public synchronized void submitted(PlaybackAttempt owner,int type){
        if(owner!=open||owner==null)return;
        playbackBoundary(open,PlaybackBoundary.PREPARE_SUBMITTED,PlaybackOutcome.PENDING,MeasurementQuality.PROXY,-1,type);
    }
    public synchronized void prepared(Object backend,int type){
        if(backend==null||backend!=this.backend)return;
        playbackBoundary(open,PlaybackBoundary.PREPARED,PlaybackOutcome.PENDING,MeasurementQuality.CALLBACK,-1,type);
    }
    public synchronized void output(Object backend,int type){
        if(backend==null||backend!=this.backend)return;
        playbackBoundary(open,PlaybackBoundary.FIRST_OUTPUT,PlaybackOutcome.PENDING,MeasurementQuality.CALLBACK,-1,type);
        playbackBoundary(open,PlaybackBoundary.TERMINAL,PlaybackOutcome.COMPLETED,MeasurementQuality.CALLBACK,-1,type);
    }
    public synchronized void failed(PlaybackAttempt owner){if(owner!=null&&owner==open)close(PlaybackOutcome.FAILED);}
    public synchronized void failed(Object backend){if(backend!=null&&backend==this.backend)close(PlaybackOutcome.FAILED);}
    public synchronized void seekCallback(Object backend,int type){
        if(backend==null||backend!=this.backend)return;
        if(seek==null||overlapping){unpairedSeekCallback(open,type);return;}
        seekCallback=true;playbackBoundary(seek,PlaybackBoundary.SEEK_CALLBACK,PlaybackOutcome.PENDING,MeasurementQuality.CALLBACK,-1,type);
    }
    public synchronized void seeking(long target,int backend){
        if(seek!=null){overlapping=true;playbackBoundary(seek,PlaybackBoundary.TERMINAL,PlaybackOutcome.SUPERSEDED,MeasurementQuality.UNKNOWN,-1,backend);}
        seek=playbackAttempt(PlaybackOperation.SEEK,open,target);seekCallback=false;
    }
    public synchronized void allSeeksComplete(Object backend,int type){
        if(backend==null||backend!=this.backend)return;
        if(seek!=null)playbackBoundary(seek,PlaybackBoundary.TERMINAL,
            overlapping||!seekCallback?PlaybackOutcome.UNATTRIBUTED:PlaybackOutcome.COMPLETED,MeasurementQuality.CALLBACK,-1,type);
        seek=null;overlapping=false;seekCallback=false;
    }
    public synchronized void close(PlaybackOutcome outcome){
        playbackBoundary(seek,PlaybackBoundary.TERMINAL,outcome,MeasurementQuality.UNKNOWN,-1,-1);
        playbackBoundary(open,PlaybackBoundary.TERMINAL,outcome,MeasurementQuality.UNKNOWN,-1,-1);
        open=null;seek=null;backend=null;overlapping=false;seekCallback=false;
    }
}
