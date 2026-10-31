package com.archos.mediacenter.video.player;

/** Duration arithmetic independent of presentation. Paused HUD hides the finish prediction. */
public final class PreviewHudTiming {
    public static long end(long now,long duration,long position,float speed){
        if(duration<=0)return -1;if(!Float.isFinite(speed)||speed<=0)speed=1;
        return now+Math.round(Math.max(0,duration-Math.max(0,Math.min(duration,position)))/(double)speed);
    }
    public static float pillLeft(float desired,int trackWidth,int pillWidth){return Math.max(0,Math.min(desired,Math.max(0,trackWidth-pillWidth)));}
    public static String timestamp(long ms){long seconds=Math.max(0,ms)/1000;return String.format(java.util.Locale.ROOT,"%02d:%02d:%02d",seconds/3600,seconds/60%60,seconds%60);}
    private PreviewHudTiming(){}
}
