package com.archos.mediacenter.video.player;

/** Paused prediction means 'ends if resumed now'; seeks use the preview position. */
public final class PreviewHudTiming {
    public static long end(long now,long duration,long position,float speed){
        if(duration<=0)return -1;if(!Float.isFinite(speed)||speed<=0)speed=1;
        return now+Math.round(Math.max(0,duration-Math.max(0,Math.min(duration,position)))/(double)speed);
    }
    public static float pillLeft(float desired,int trackWidth,int pillWidth){return Math.max(0,Math.min(desired,Math.max(0,trackWidth-pillWidth)));}
    private PreviewHudTiming(){}
}
