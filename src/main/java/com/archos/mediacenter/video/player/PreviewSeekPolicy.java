package com.archos.mediacenter.video.player;

/** Remote-key increments only. No decoder, playback-state or network responsibilities. */
final class PreviewSeekPolicy {
    private int direction,presses;
    private long last=-1;
    int next(int way,long now){
        if(way!=1&&way!=-1)throw new IllegalArgumentException("Invalid seek direction");
        if(direction!=way||last<0||now<last||now-last>=1250)presses=0;
        direction=way;last=now;presses=Math.min(10,presses+1);
        return (presses<=3?10000:presses<=6?30000:presses<=9?60000:120000)*way;
    }
    void reset(){direction=presses=0;last=-1;}
    static int position(int current,int delta,int duration){
        return (int)Math.max(0,Math.min(Math.max(0,(long)duration-2000),(long)current+delta));
    }
}
