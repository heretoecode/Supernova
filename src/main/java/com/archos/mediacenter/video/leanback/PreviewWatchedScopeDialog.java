package com.archos.mediacenter.video.leanback;

import android.app.Dialog;
import android.content.Context;
import com.archos.mediacenter.video.browser.adapters.object.Season;
import java.util.*;
import java.util.function.BiConsumer;

/** The retained DbUtils handlers own all watched/Trakt writes; this chooses their scope. */
public final class PreviewWatchedScopeDialog {
    public static Dialog show(Context context,List<Season> input,BiConsumer<List<Season>,Boolean> apply){
        List<Season> seasons=Collections.unmodifiableList(new ArrayList<>(input));
        int total=0,watched=0;for(Season season:seasons){total+=season.getEpisodeTotalCount();watched+=Math.min(season.getEpisodeTotalCount(),season.getEpisodeWatchedCount());}
        boolean all=total>0&&watched>=total;
        String[] labels=new String[seasons.size()+1];labels[0]=label("Entire Series",watched,total);
        for(int i=0;i<seasons.size();i++){Season season=seasons.get(i);labels[i+1]=label(season.getSeasonNumber()==0?"Specials":"Season "+season.getSeasonNumber(),season.getEpisodeWatchedCount(),season.getEpisodeTotalCount());}
        return PreviewDialog.choose(context,"Watched state",labels,0,n->{
            if(n==0)apply.accept(seasons,!all);
            else{Season season=seasons.get(n-1);apply.accept(Collections.singletonList(season),!season.allEpisodesWatched());}
        });
    }
    private static String label(String scope,int watched,int total){return scope+" · "+Math.min(watched,total)+"/"+total+" watched · "+(total>0&&watched>=total?"Mark unwatched":"Mark watched");}
    private PreviewWatchedScopeDialog(){}
}
