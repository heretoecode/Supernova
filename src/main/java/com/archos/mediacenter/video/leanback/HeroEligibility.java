package com.archos.mediacenter.video.leanback;

import java.util.Set;

/** Conservative policy shared by indexed hero selection and executable JVM tests. */
public final class HeroEligibility {
    public static boolean sequential(int season,int episode,Set<String> completed,long added,long lastCompletion){
        if(season<1||episode<2||added<=0||lastCompletion<=0||added<lastCompletion)return false;
        // Crossing a season requires a verified episode-count boundary; absent that, do not guess.
        for(int number=1;number<episode;number++)if(!completed.contains(season+":"+number))return false;
        return true;
    }
    public static boolean unstarted(boolean completed,boolean started,int resume){return !completed&&!started&&resume<=0;}
    private HeroEligibility(){}
}
