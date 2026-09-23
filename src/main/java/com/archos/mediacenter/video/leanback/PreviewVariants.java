package com.archos.mediacenter.video.leanback;

import com.archos.mediacenter.video.browser.adapters.object.Video;
import java.util.*;
import com.archos.mediacenter.video.browser.adapters.object.*;
import com.archos.mediacenter.video.leanback.PreviewLibraryLoader.Entry;

/** Real indexed measurements only; unknown measurements sort after known values. */
public final class PreviewVariants {
    public static final Comparator<Video> BEST_FIRST=Comparator
        .comparingLong((Video v)->(long)Math.max(0,v.getMeasuredWidth())*Math.max(0,v.getMeasuredHeight())).reversed()
        .thenComparing(Comparator.comparingLong((Video v)->v.getDurationMs()>0?Math.max(0,v.getSize())/v.getDurationMs():0).reversed())
        .thenComparingLong(Video::getId);
    public static String label(Video video){
        int w=video.getMeasuredWidth(),h=video.getMeasuredHeight();
        String resolution=w>0&&h>0?w+" × "+h:"Resolution unavailable";
        return resolution+" · "+video.getFilenameNonCryptic();
    }
    public static String logicalKey(Entry e){
        if(e.media instanceof Episode){Episode ep=(Episode)e.media;return "episode:"+e.show+":"+ep.getSeasonNumber()+":"+ep.getEpisodeNumber();}
        if(e.media instanceof Movie&&((Movie)e.media).getOnlineId()>0)return "movie:"+((Movie)e.media).getOnlineId();
        return e.key();
    }
    public static List<Entry> logicalChoices(List<Entry> source){
        Map<String,List<Entry>> groups=new LinkedHashMap<>();for(Entry e:source)groups.computeIfAbsent(logicalKey(e),key->new ArrayList<>()).add(e);
        List<Entry> result=new ArrayList<>();for(List<Entry> group:groups.values()){
            Entry best=Collections.min(group,(a,b)->BEST_FIRST.compare((Video)a.media,(Video)b.media));
            Entry history=group.stream().filter(e->((Video)e.media).getResumeMs()!=0||((Video)e.media).isWatched()).max(Comparator.comparingLong(e->((Video)e.media).getLastPlayed())).orElse(null);
            if(history!=null&&group.size()>1){Video origin=(Video)history.media,target=(Video)best.media;target.setAutomaticResumeMs(PreviewSeriesJourney.completed(origin)?com.archos.mediacenter.video.player.PlayerActivity.LAST_POSITION_END:origin.getResumeMs());target.setRemoteResumeMs(origin.getRemoteResumeMs());best.playedAt=Math.max(best.playedAt,history.playedAt);}
            result.add(best);
        }return result;
    }
    /** Navigate distinct episodes, then select the deterministic best physical file. */
    public static Episode adjacentEpisode(List<Entry> source, long currentId, int direction) {
        Entry current = null;
        for (Entry entry : source) if (((Video)entry.media).getId() == currentId) { current = entry; break; }
        if (current == null || !(current.media instanceof Episode) || direction == 0) return null;
        Episode now = (Episode)current.media;
        Comparator<Episode> order = Comparator.comparingInt(Episode::getSeasonNumber).thenComparingInt(Episode::getEpisodeNumber);
        Episode selected = null;
        for (Entry entry : source) {
            if (entry.show != current.show || !(entry.media instanceof Episode)) continue;
            Episode candidate = (Episode)entry.media;
            int relative = order.compare(candidate, now);
            if (direction > 0 ? relative <= 0 : relative >= 0) continue;
            int comparison = selected == null ? 0 : order.compare(candidate, selected);
            if (selected == null || (direction > 0 ? comparison < 0 : comparison > 0)
                    || comparison == 0 && BEST_FIRST.compare(candidate, selected) < 0) selected = candidate;
        }
        return selected;
    }
    private PreviewVariants(){}
}
